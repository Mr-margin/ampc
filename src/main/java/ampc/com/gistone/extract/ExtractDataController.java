package ampc.com.gistone.extract;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.util.AmpcResult;

@RestController
@RequestMapping(value = "/extract")
public class ExtractDataController {
	
	private final static Logger logger = LoggerFactory.getLogger(ExtractDataController.class);
			
	@Autowired
	private ExtractDataService extractDataService;

	@RequestMapping(value="/data")
	public AmpcResult getExtractData(@RequestBody Map<String,Object> requestData) {
		Map<String, Map> data = (Map) requestData.get("data");
		List res;
		try {
			String calcType = String.valueOf(data.get("calcType"));
			String showType = String.valueOf(data.get("showType"));
			int borderType = Integer.valueOf(String.valueOf(data.get("borderType")));
			int userId = Integer.valueOf(String.valueOf(data.get("userId")));
			int domainId = Integer.valueOf(String.valueOf(data.get("domainId")));
			int missionId = Integer.valueOf(String.valueOf(data.get("missionId")));
			int domain = Integer.valueOf(String.valueOf(data.get("domain")));
			String species = String.valueOf(data.get("species"));
			String timePoint = String.valueOf(data.get("timePoint"));
			
			ExtractRequestParams params = new ExtractRequestParams(calcType, showType, userId, domainId, missionId, domain, species, timePoint);
			params.setBorderType(borderType);
			int scenarioId1 = Integer.valueOf(String.valueOf(data.get("scenarioId1")));
			params.setScenarioId1(scenarioId1);
			if(!"show".equals(calcType)) {
				int scenarioId2 = Integer.valueOf(String.valueOf(data.get("scenarioId2")));
				params.setScenarioId2(scenarioId2);
			}
			if("d".equals(timePoint)) {
				String day = String.valueOf(data.get("day"));
				params.setDay(day);
				params.setHour(0);
			}
			if("h".equals(timePoint)) {
				String day = String.valueOf(data.get("day"));
				params.setDay(day);
				int hour = Integer.valueOf(String.valueOf(data.get("hour")));
				if(hour < 0 || hour > 24) {
			        logger.error("the hour params is wrong, the value should be between in 0 and 23");
			        return AmpcResult.build(1000, "参数hour的值错误， 范围应该在[0,23]");
			    }
				params.setHour(hour);
			}
			if("a".equals(timePoint)) {
				List dates = (List) data.get("dates");
				params.setDates(dates);
			}
			int layer = Integer.valueOf(String.valueOf(data.get("layer")));
			params.setLayer(layer);
			double xmin = Double.valueOf(String.valueOf(data.get("xmin")));
			double ymin = Double.valueOf(String.valueOf(data.get("ymin")));
			double xmax = Double.valueOf(String.valueOf(data.get("xmax")));
			double ymax = Double.valueOf(String.valueOf(data.get("ymax")));
			params.setXmin(xmin);
			params.setYmin(ymin);
			params.setXmax(xmax);
			params.setYmax(ymax);
			
			int rows = Integer.valueOf(String.valueOf(data.get("rows")));
			int cols = Integer.valueOf(String.valueOf(data.get("cols")));
			params.setRows(rows);
			params.setCols(cols);
			res  = extractDataService.buildData(params);
			if(res == null) AmpcResult.build(1000, "buildData error");
		} catch (Exception e) {
			return AmpcResult.build(1000, "参数错误");
		}
		return AmpcResult.ok(res);
	}
}
