package ampc.com.gistone.preprocess.concn;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;

@Component
public class PreproUtil {

	private final static Logger logger = LoggerFactory.getLogger(PreproUtil.class);

	@Autowired
	private TPreProcessMapper tPreProcessMapper;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void updateRecord(String tableName, RequestParams params, Map dataMap, String resType)
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
						checkAndAddFnlData(entity, params, resType);
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
					checkAndAddFnlData(scenarinoEntity, params, resType);
			}
		} catch (Exception e) {
			logger.error("PreproUtil | updateRecord error");
		}
	}

	public int getScenarinoYear(TScenarinoDetail tScenarinoDetail) {
		Date date = tScenarinoDetail.getScenarinoAddTime();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	private void checkAndAddFnlData(ScenarinoEntity scenarinoEntity, RequestParams params, String resType)
			throws IOException {

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
			checkTableParams.setResType(resType);
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
