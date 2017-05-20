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
	@Autowired
	private ExtractPngService extractPngService;

	@RequestMapping(value = "/data")
	public AmpcResult getExtractData(@RequestBody Map<String, Object> requestData) {
		Map<String, Map> data = (Map) requestData.get("data");
		List res;
		try {
			String calcType = String.valueOf(data.get("calcType"));
			String showType = String.valueOf(data.get("showType"));
			int borderType = Integer.valueOf(String.valueOf(data.get("borderType")));
			Long userId = Long.valueOf(String.valueOf(data.get("userId")));
			Long domainId = Long.valueOf(String.valueOf(data.get("domainId")));
			Long missionId = Long.valueOf(String.valueOf(data.get("missionId")));
			int domain = Integer.valueOf(String.valueOf(data.get("domain")));
			List<String> species = (List) (data.get("species"));
			String timePoint = String.valueOf(data.get("timePoint"));

			ExtractRequestParams params = new ExtractRequestParams(calcType, showType, userId, domainId, missionId,
					domain, species, timePoint);
			params.setBorderType(borderType);
			Long scenarioId1 = Long.valueOf(String.valueOf(data.get("scenarioId1")));
			params.setScenarioId1(scenarioId1);
			if (!Constants.CALCTYPE_SHOW.equals(calcType)) {
				Long scenarioId2 = Long.valueOf(String.valueOf(data.get("scenarioId2")));
				params.setScenarioId2(scenarioId2);
			}
			if (Constants.TIMEPOINT_D.equals(timePoint)) {
				String day = String.valueOf(data.get("day"));
				params.setDay(day);
				params.setHour(0);
			}
			if (Constants.TIMEPOINT_H.equals(timePoint)) {
				String day = String.valueOf(data.get("day"));
				params.setDay(day);
				int hour = Integer.valueOf(String.valueOf(data.get("hour")));
				if (hour < 0 || hour > 24) {
					logger.error("the hour params is wrong, the value should be between in 0 and 23");
					return AmpcResult.build(1000, "参数hour的值错误， 范围应该在[0,23]");
				}
				params.setHour(hour);
			}
			if (Constants.TIMEPOINT_A.equals(timePoint)) {
				List dates = (List) data.get("dates");
				params.setDates(dates);
			}
			if (Constants.SHOW_TYPE_WIND.equals(showType)) {
				int windSymbol = Integer.valueOf(String.valueOf(data.get("windSymbol")));
				params.setWindSymbol(windSymbol);
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
			ManageParams manageParams = new ManageParams();
			manageParams.setParams(params);
			res = extractDataService.buildData(manageParams);
			if (res != null)
				return AmpcResult.ok(res);

		} catch (Exception e) {
			return AmpcResult.build(1003, "参数异常");
		}
		return AmpcResult.build(1001, "系统异常");
	}
}
