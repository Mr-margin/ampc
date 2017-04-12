package ampc.com.gistone.preprocess.concn;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.util.JsonUtil;
import net.sf.json.JSONObject;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.preprocess.concn.CheckTableParams;

@Service
public class ConcnService {
	
	private final static Logger logger = LoggerFactory.getLogger(ConcnService.class);

	private final static String CONCN_URL = "http://localhost:8288/cmaq/result";
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public JSONObject requestConcnData(RequestParams params) throws Exception {
		logger.info("start request concn data, the params : ");
		logger.info(params.toString());
		
		long sId = params.getScenarioId();
		TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(sId);
		int year = getScenarinoYear(tScenarinoDetail);
		
		//根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
		CheckTableParams checkTableParams = new CheckTableParams();
		checkTableParams.setUser_id(params.getUserId());
		checkTableParams.setYears(String.valueOf(year));
		checkTableParams.setTime_point(params.getTimePoint());
		tPreProcessMapper.checkScenarinoResTable(checkTableParams);
		String tableName = checkTableParams.gettName();
		logger.info(tableName);
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("tableName", tableName);
		requestParams.put("sId", params.getScenarioId());
		requestParams.put("mode", params.getMode());
		requestParams.put("city_station", params.getFilter());
		requestParams.put("domainId", params.getDomainId());
		requestParams.put("domain", params.getDomain());
		List<ScenarinoEntity> entites = tPreProcessMapper.selectByUniqueKey(requestParams);
		
		String paramStr = JsonUtil.objToJson(params);
		String resStr = ClientUtil.doPost(CONCN_URL, paramStr);
		
		Map<String, Object> resMap = objectMapper.readValue(resStr, Map.class);
		JSONObject jsonObject = JSONObject.fromObject(resMap.get("data"));
		ObjectMapper mapper = new ObjectMapper();
		Map dataMap = mapper.readValue(jsonObject.toString(), Map.class);

		for(ScenarinoEntity entity : entites) {
			String cityOrStation = entity.getCity_station();
			if(dataMap.containsKey(cityOrStation)) {
				Map map = (Map) dataMap.get(cityOrStation);
				Map dbMap = JsonUtil.jsonToObj(entity.getContent(), Map.class);
				dbMap.putAll(map);
				entity.setContent(mapper.writeValueAsString(dbMap));
				entity.setTableName(tableName);
				tPreProcessMapper.update(entity);
				dataMap.remove(cityOrStation);
				checkAndAddFnlData(entity, params);
			}
		}
		for (Object city_station : dataMap.keySet()) {
			Map map = (Map) dataMap.get(city_station);
			ScenarinoEntity scenarinoEntity = new ScenarinoEntity();
			scenarinoEntity.setId(UUID.randomUUID().toString());
			scenarinoEntity.setsId(params.getScenarioId());
			scenarinoEntity.setMode(params.getMode());
			scenarinoEntity.setCity_station((String)city_station);
			scenarinoEntity.setDomain(params.getDomain());
			scenarinoEntity.setDomainId(params.getDomainId());
			scenarinoEntity.setContent(mapper.writeValueAsString(map));
			scenarinoEntity.setUserId(params.getUserId());
			scenarinoEntity.setTableName(tableName);
			tPreProcessMapper.insert(scenarinoEntity);
			checkAndAddFnlData(scenarinoEntity, params);
		}
		
//		logger.info(jsonObject.toString());
		return jsonObject;
	}

	private int getScenarinoYear(TScenarinoDetail tScenarinoDetail) {
		Date date = tScenarinoDetail.getScenarinoAddTime();
		Calendar c = Calendar.getInstance(); 
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	private void checkAndAddFnlData(ScenarinoEntity scenarinoEntity, RequestParams params) throws IOException {
		long sId = scenarinoEntity.getsId();
		TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(sId);
		int year = getScenarinoYear(tScenarinoDetail);
		Long type = tScenarinoDetail.getScenarinoType();
		if(type == 4) {
			TreeMap dbMap = JsonUtil.jsonToObj(scenarinoEntity.getContent(), TreeMap.class); 
			Set<String> keySet = dbMap.keySet();
			String firstKey = "";
			for(String str : keySet) {
				firstKey = str;
				logger.info(str);
				break;
			}
			
			if(StringUtils.isNotEmpty(firstKey)) {
				//根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
				CheckTableParams checkTableParams = new CheckTableParams();
				checkTableParams.setUser_id(params.getUserId());
				checkTableParams.setYears(String.valueOf(year));
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
				if(entites != null && entites.size() > 0) {
					tPreProcessMapper.updateFnl(fnlEntity);
				} else {
					tPreProcessMapper.insertFnl(fnlEntity);
				}
				
			} else {
				logger.error("this forcast scenarino no result");
			}
			
			
		}
	}
	
	
}
