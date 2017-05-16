package ampc.com.gistone.preprocess.concn;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private PreproUtil preproUtil;
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TSiteMapper tSiteMapper;
	@Autowired
	private CityWorkerV2 cityWorkerV2;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Transactional
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
			Date date = tScenarinoDetail.getScenarinoAddTime();
			int year = DateUtil.getYear(date);
			params.setYear(year); // 根据情景的创建时间来决定将数据存储至哪张表中
			int type = Integer.valueOf(tScenarinoDetail.getScenType());
			Map<String, String[]> dataTypeMap = preproUtil.buildFnlAndGfsDate(tScenarinoDetail);

			params.setDateTypeMap(dataTypeMap);
			// 根据userID、预案的创建时间来检测当前的表是否存在，不存在要创建
			CheckTableParams checkTableParams = new CheckTableParams();
			checkTableParams.setUser_id(params.getUserId());
			checkTableParams.setYears(String.valueOf(year));
			checkTableParams.setTime_point(params.getTimePoint());
			checkTableParams.setResType(Constants.SHOW_TYPE_CONCN);
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
					cityWorkerV2.exe(params, Constants.AREA_CITY, cites, Constants.SHOW_TYPE_CONCN);
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
			preproUtil.updateRecord(tableName, params, dataMap, Constants.SHOW_TYPE_CONCN, type);

			Map map = new HashMap();
			map.put("siteCodeList", cites);
			List<String> stationList = tSiteMapper.selectBySiteCodeList(map);

			params.setMode(Constants.AREA_POINT2);
			params.setFilter(stationList);
			preproUtil.updateRecord(tableName, params, dataMap, Constants.SHOW_TYPE_CONCN, type);

			return true;
		} catch (JsonParseException e) {
			logger.error("ConcnService | requestConcnData JsonParseException");
			return false;
		} catch (JsonMappingException e) {
			logger.error("ConcnService | requestConcnData JsonMappingException");
			return false;
		} catch (JsonProcessingException e) {
			logger.error("ConcnService | requestConcnData JsonProcessingException");
			return false;
		} catch (IOException e) {
			logger.error("ConcnService | requestConcnData IOException");
			return false;
		}
	}

}
