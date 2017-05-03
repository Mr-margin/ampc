package ampc.com.gistone.preprocess.meteor;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.preprocess.concn.CheckTableParams;
import ampc.com.gistone.preprocess.concn.PreproUtil;
import ampc.com.gistone.preprocess.concn.RequestParams;
import ampc.com.gistone.preprocess.core.CityWorkerV2;

@Service
public class MeteorService {

	private final static Logger logger = LoggerFactory.getLogger(MeteorService.class);
	@Autowired
	private PreproUtil preproUtil;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	@Autowired
	private TSiteMapper tSiteMapper;
	@Autowired
	private CityWorkerV2 cityWorkerV2;

	public boolean requestMeteorData(RequestParams params) {

		String[] filter = { "1101", "1201", "1301", "1302", "1303", "1304", "1305", "1306", "1307", "1308", "1309",
				"1310", "1311", "1402", "1403", "1404", "1407", "1409", "1504", "1509", "3701", "3703", "3705", "3707",
				"3709", "3712", "3714", "3715", "3716", "4105", "4106", "4109" };

		List<String> cites = Arrays.asList(filter);
		params.setFilter(cites);
		params.setMode(Constants.AREA_CITY);

		try {
			Long sId = Long.valueOf(params.getScenarioId());
			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(sId);
			int year = preproUtil.getScenarinoYear(tScenarinoDetail);
			params.setYear(year);
			int type = Integer.valueOf(tScenarinoDetail.getScenType());
			if (type == 4)
				params.setType(true);
			CheckTableParams checkTableParams = new CheckTableParams();
			checkTableParams.setUser_id(params.getUserId());
			checkTableParams.setYears(String.valueOf(year));
			checkTableParams.setTime_point(params.getTimePoint());
			checkTableParams.setResType(Constants.SHOW_TYPE_METEOR);
			tPreProcessMapper.checkScenarinoResTable(checkTableParams);
			String tableName = checkTableParams.gettName();
			logger.info(tableName);

			Map dataMap = null;
			logger.info("start request meteor data, the params : ");
			logger.info(params.toString());
			try {
				cityWorkerV2.exe(params.getUserId(), params.getDomainId(), params.getMissionId(),
						params.getScenarioId(), params.getDomain(), params.getDate(), params.getTimePoint(),
						Constants.AREA_CITY, cites, Constants.SHOW_TYPE_METEOR);
				dataMap = cityWorkerV2.getResult();

			} catch (TransformException e) {
				logger.error("MeteorService | requestMeteorData TransformException");
			} catch (FactoryException e) {
				logger.error("MeteorService | requestMeteorData FactoryException");
			} catch (ParseException e) {
				logger.error("MeteorService | requestMeteorData ParseException");
			}
			if (dataMap == null || dataMap.size() == 0)
				return false;

			preproUtil.updateRecord(tableName, params, dataMap, Constants.SHOW_TYPE_METEOR);

			Map map = new HashMap();
			map.put("siteCodeList", cites);
			List<String> stationList = tSiteMapper.selectBySiteCodeList(map);

			params.setMode(Constants.AREA_POINT2);
			params.setFilter(stationList);
			preproUtil.updateRecord(tableName, params, dataMap, Constants.SHOW_TYPE_METEOR);

			return true;
		} catch (JsonParseException e) {
			logger.error("MeteorService | requestMeteorData JsonParseException");
			return false;
		} catch (JsonMappingException e) {
			logger.error("MeteorService | requestMeteorData JsonMappingException");
			return false;
		} catch (IOException e) {
			logger.error("MeteorService | requestMeteorData IOException");
			return false;
		}

	}

}
