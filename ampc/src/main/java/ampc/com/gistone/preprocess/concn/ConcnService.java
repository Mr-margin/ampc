package ampc.com.gistone.preprocess.concn;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.preprocess.core.CityWorkerV2;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;
import net.sf.json.JSONObject;

@Service
public class ConcnService {

	private final static Logger logger = LoggerFactory.getLogger(ConcnService.class);

	@Autowired
	private ConfigUtil configUtil;
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TSiteMapper tSiteMapper;
	@Autowired
	private CityWorkerV2 cityWorkerV2;

	private ObjectMapper objectMapper = new ObjectMapper();

	public boolean requestConcnData(RequestParams params) {

		String[] filter = { "1101", "1201", "1301", "1302", "1303", "1304", "1305", "1306", "1307", "1308", "1309",
				"1310", "1311", "1402", "1403", "1404", "1407", "1409", "1504", "1509", "3701", "3703", "3705", "3707",
				"3709", "3712", "3714", "3715", "3716", "4105", "4106", "4109" };

		List<String> cites = Arrays.asList(filter);
		params.setFilter(cites);
		params.setMode(Constants.AREA_CITY);

		try {
			Long sId = Long.valueOf(params.getScenarioId());
			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(sId);
			int year = getScenarinoYear(tScenarinoDetail);
			params.setYear(year);
			int type = Integer.valueOf(tScenarinoDetail.getScenType());
			if (type == 4)
				params.setType(true);
			// 根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
			CheckTableParams checkTableParams = new CheckTableParams();
			checkTableParams.setUser_id(params.getUserId());
			checkTableParams.setYears(String.valueOf(year));
			checkTableParams.setTime_point(params.getTimePoint());
			tPreProcessMapper.checkScenarinoResTable(checkTableParams);
			String tableName = checkTableParams.gettName();
			logger.info(tableName);

			Map dataMap = null;
			logger.info("start request concn data, the params : ");
			logger.info(params.toString());
			if (!configUtil.isPreproEnable()) { // 代表从其他服务获取数据
				String paramStr = JsonUtil.objToJson(params);
				String resStr = ClientUtil.doPost(configUtil.getPreproURL(), paramStr);
				if (StringUtils.isEmpty(resStr))
					return false;
				Map<String, Object> resMap = objectMapper.readValue(resStr, Map.class);
				JSONObject jsonObject = JSONObject.fromObject(resMap.get("data"));
				ObjectMapper mapper = new ObjectMapper();
				dataMap = mapper.readValue(jsonObject.toString(), Map.class);
			} else { // 从当前服务中的接口获取数据
				try {
					cityWorkerV2.exe(params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDate(), params.getTimePoint(),
							Constants.AREA_CITY, cites);
					dataMap = cityWorkerV2.getResult();

				} catch (TransformException e) {
					logger.error("ConcnService | requestConcnData TransformException", e);
				} catch (FactoryException e) {
					logger.error("ConcnService | requestConcnData FactoryException", e);
				} catch (ParseException e) {
					logger.error("ConcnService | requestConcnData ParseException", e);
				}
			}
			if (dataMap == null || dataMap.size() == 0)
				return false;
			updateRecord(tableName, params, dataMap);

			Map map = new HashMap();
			map.put("siteCodeList", cites);
			List<String> stationList = tSiteMapper.selectBySiteCodeList(map);

			params.setMode(Constants.AREA_POINT2);
			params.setFilter(stationList);
			updateRecord(tableName, params, dataMap);

			return true;
		} catch (JsonParseException e) {
			logger.error("ConcnService | requestConcnData JsonParseException", e);
			return false;
		} catch (JsonMappingException e) {
			logger.error("ConcnService | requestConcnData JsonMappingException", e);
			return false;
		} catch (JsonProcessingException e) {
			logger.error("ConcnService | requestConcnData JsonProcessingException", e);
			return false;
		} catch (IOException e) {
			logger.error("ConcnService | requestConcnData IOException", e);
			return false;
		}
	}

	private void updateRecord(String tableName, RequestParams params, Map dataMap)
			throws JsonParseException, JsonMappingException, IOException {

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("tableName", tableName);
		requestParams.put("sId", params.getScenarioId());
		requestParams.put("mode", params.getMode());
		requestParams.put("city_station", params.getFilter());
		requestParams.put("domainId", params.getDomainId());
		requestParams.put("domain", params.getDomain());
		List<ScenarinoEntity> entites = tPreProcessMapper.selectByUniqueKey(requestParams);

		try {
			Map csMap = (Map) dataMap.get(params.getMode());

			for (ScenarinoEntity entity : entites) {
				String cityOrStation = entity.getCity_station();
				if (csMap.containsKey(cityOrStation)) {
					Map map = (Map) csMap.get(cityOrStation);
					Map dbMap = JsonUtil.jsonToObj(entity.getContent(), Map.class);
					dbMap.putAll(map);
					entity.setContent(objectMapper.writeValueAsString(dbMap));
					entity.setTableName(tableName);
					tPreProcessMapper.update(entity);
					csMap.remove(cityOrStation);
					if (params.isType())
						checkAndAddFnlData(entity, params);
				}
			}
			for (Object city_station : csMap.keySet()) {
				Map map = (Map) csMap.get(city_station);
				ScenarinoEntity scenarinoEntity = new ScenarinoEntity();
				scenarinoEntity.setId(UUID.randomUUID().toString());
				scenarinoEntity.setsId(params.getScenarioId());
				scenarinoEntity.setMode(params.getMode());
				scenarinoEntity.setCity_station((String) city_station);
				scenarinoEntity.setDomain(params.getDomain());
				scenarinoEntity.setDomainId(params.getDomainId());
				scenarinoEntity.setContent(objectMapper.writeValueAsString(map));
				scenarinoEntity.setUserId(params.getUserId());
				scenarinoEntity.setTableName(tableName);
				tPreProcessMapper.insert(scenarinoEntity);
				if (params.isType())
					checkAndAddFnlData(scenarinoEntity, params);
			}
		} catch (Exception e) {
			logger.error("ConcnService | updateRecord error", e);
		}
	}

	private int getScenarinoYear(TScenarinoDetail tScenarinoDetail) {
		Date date = tScenarinoDetail.getScenarinoAddTime();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	private void checkAndAddFnlData(ScenarinoEntity scenarinoEntity, RequestParams params) throws IOException {

		TreeMap dbMap = JsonUtil.jsonToObj(scenarinoEntity.getContent(), TreeMap.class);
		Set<String> keySet = dbMap.keySet();
		String firstKey = "";
		for (String str : keySet) {
			firstKey = str;
			logger.info(str);
			break;
		}

		if (StringUtils.isNotEmpty(firstKey)) {
			// 根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
			CheckTableParams checkTableParams = new CheckTableParams();
			checkTableParams.setUser_id(params.getUserId());
			checkTableParams.setYears(String.valueOf(params.getYear()));
			checkTableParams.setTime_point(params.getTimePoint());
			tPreProcessMapper.checkForcastFnlTable(checkTableParams);
			String tableName = checkTableParams.gettName();
			logger.info(tableName);
			Date date = DateUtil.StrToDate1(firstKey);
			ScenarinoEntity fnlEntity = new ScenarinoEntity();
			fnlEntity.setId(UUID.randomUUID().toString());
			fnlEntity.setsId(scenarinoEntity.getsId());
			fnlEntity.setDay(date);
			fnlEntity.setMode(scenarinoEntity.getMode());
			fnlEntity.setCity_station(scenarinoEntity.getCity_station());
			fnlEntity.setDomainId(scenarinoEntity.getDomainId());
			fnlEntity.setDomain(scenarinoEntity.getDomain());
			ObjectMapper mapper = new ObjectMapper();
			fnlEntity.setContent(mapper.writeValueAsString(dbMap.get(firstKey)));
			fnlEntity.setUserId(params.getUserId());
			fnlEntity.setTableName(tableName);

			Map<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("tableName", tableName);
			requestParams.put("sId", params.getScenarioId());
			requestParams.put("day", date);
			requestParams.put("mode", params.getMode());
			requestParams.put("city_station", scenarinoEntity.getCity_station());
			requestParams.put("domainId", params.getDomainId());
			requestParams.put("domain", params.getDomain());
			List<ScenarinoEntity> entites = tPreProcessMapper.selectByUniqueKeyForfnl(requestParams);
			if (entites != null && entites.size() > 0) {
				tPreProcessMapper.updateFnl(fnlEntity);
			} else {
				tPreProcessMapper.insertFnl(fnlEntity);
			}

		} else {
			logger.error("this forcast scenarino no result");
		}
	}

}
