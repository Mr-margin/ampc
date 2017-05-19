package ampc.com.gistone.preprocess.concn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;

/**
 * 预处理工具类
 * 
 * @author xll
 *
 */
@Component
public class PreproUtil {

	private final static Logger logger = LoggerFactory.getLogger(PreproUtil.class);

	@Autowired
	private TPreProcessMapper tPreProcessMapper;

	private ObjectMapper objectMapper = new ObjectMapper();

	// 组织fnl和gfs日期; dataTypeMap为气象数据使用; dataTypeMap在预处理和抽取浓度、排放数据时不用
	public Map<String, String[]> buildFnlAndGfsDate(TScenarinoDetail tScenarinoDetail) {
		int type = Integer.valueOf(tScenarinoDetail.getScenType());
		Map<String, String[]> dataTypeMap = new HashMap<String, String[]>();
		if (type == 4 || type == 1) { // 实时预报情景
			Date fnlDay = tScenarinoDetail.getScenarinoStartDate();
			dataTypeMap.put(Constants.MODEL_DATA_TYPE_FNL,
					new String[] { DateUtil.DATEtoString(fnlDay, DateUtil.DATE_FORMAT) });
			List<String> list = new ArrayList<String>();
			Date endDay = tScenarinoDetail.getScenarinoEndDate();
			Calendar c = Calendar.getInstance();
			c.setTime(fnlDay);
			c.add(Calendar.DAY_OF_YEAR, 1);
			// 记录实时预报中第一天gfs
			dataTypeMap.put(Constants.MODEL_DATA_TYPE_GFS_FIRST,
					new String[] { DateUtil.DATEtoString(c.getTime(), DateUtil.DATE_FORMAT) });
			while (c.getTime().before(endDay) || c.getTime().equals(endDay)) {
				list.add(DateUtil.DATEtoString(c.getTime(), DateUtil.DATE_FORMAT));
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			dataTypeMap.put(Constants.MODEL_DATA_TYPE_GFS, list.toArray(new String[list.size()]));
		} else if (type == 3 || type == 2) { // 后评估任务中的基准情景，目前后评估任务中的基准情景关于浓度、气象的fnl数据不用写入到fnl表中
			Date startDay = tScenarinoDetail.getScenarinoStartDate();
			Date endDay = tScenarinoDetail.getScenarinoEndDate();
			Calendar c = Calendar.getInstance();
			c.setTime(startDay);
			List<String> list = new ArrayList<String>();
			while (c.getTime().before(endDay) || c.getTime().equals(endDay)) {
				list.add(DateUtil.DATEtoString(c.getTime(), DateUtil.DATE_FORMAT));
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			dataTypeMap.put(Constants.MODEL_DATA_TYPE_FNL, list.toArray(new String[list.size()]));
		}
		return dataTypeMap;
	}

	public void updateRecord(String tableName, RequestParams params, Map dataMap, String resType, int type)
			throws JsonParseException, JsonMappingException, IOException {

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("tableName", tableName);
		requestParams.put("sId", params.getScenarioId());
		requestParams.put("mode", params.getMode());
		requestParams.put("city_station", params.getFilter());
		requestParams.put("domainId", params.getDomainId());
		requestParams.put("domain", params.getDomain());
		List<ScenarinoEntity> entites = tPreProcessMapper.selectByUniqueKey(requestParams);

		String[] fnlDateArray = params.getDateTypeMap().get(Constants.MODEL_DATA_TYPE_FNL);
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

					if (fnlDateArray != null && fnlDateArray.length >= 0 && type == 4) {
						checkAndAddFnlData(entity, params, resType);
					}
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
				if (fnlDateArray != null && fnlDateArray.length >= 0 && type == 4) {
					checkAndAddFnlData(scenarinoEntity, params, resType);
				}
			}
		} catch (Exception e) {
			logger.error("PreproUtil | updateRecord error", e);
		}
	}

	private void checkAndAddFnlData(ScenarinoEntity scenarinoEntity, RequestParams params, String resType)
			throws IOException {

		String[] fnlDateArray = params.getDateTypeMap().get(Constants.MODEL_DATA_TYPE_FNL);
		TreeMap dbMap = JsonUtil.jsonToObj(scenarinoEntity.getContent(), TreeMap.class);
		for (String day : fnlDateArray) {
			if (dbMap.containsKey(day)) {
				// 根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
				CheckTableParams checkTableParams = new CheckTableParams();
				checkTableParams.setUser_id(params.getUserId());
				checkTableParams.setYears(String.valueOf(params.getYear()));
				checkTableParams.setTime_point(params.getTimePoint());
				checkTableParams.setResType(resType);
				tPreProcessMapper.checkForcastFnlTable(checkTableParams);
				String tableName = checkTableParams.gettName();
				// logger.info(tableName);
				Date date = DateUtil.StrToDate1(day);
				ScenarinoEntity fnlEntity = new ScenarinoEntity();
				fnlEntity.setId(UUID.randomUUID().toString());
				fnlEntity.setsId(scenarinoEntity.getsId());
				fnlEntity.setDay(date);
				fnlEntity.setMode(scenarinoEntity.getMode());
				fnlEntity.setCity_station(scenarinoEntity.getCity_station());
				fnlEntity.setDomainId(scenarinoEntity.getDomainId());
				fnlEntity.setDomain(scenarinoEntity.getDomain());
				ObjectMapper mapper = new ObjectMapper();
				fnlEntity.setContent(mapper.writeValueAsString(dbMap.get(day)));
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
			}
		}
	}
}
