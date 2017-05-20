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
public class ExtractTiffController {

	private final static Logger logger = LoggerFactory.getLogger(ExtractTiffController.class);
	@Autowired
	private ExtractTiffService extractTiffService;

	@RequestMapping(value = "/tiff")
	public AmpcResult getExtractData(@RequestBody Map<String, Object> requestData) {
		long startTimes = System.currentTimeMillis();
		Map<String, Map> data = (Map) requestData.get("data");
		try {
			String calcType = String.valueOf(data.get("calcType"));
			String showType = String.valueOf(data.get("showType"));
			Long userId = Long.valueOf(String.valueOf(data.get("userId")));
			Long domainId = Long.valueOf(String.valueOf(data.get("domainId")));
			Long missionId = Long.valueOf(String.valueOf(data.get("missionId")));
			int domain = Integer.valueOf(String.valueOf(data.get("domain")));
			List<String> species = (List) (data.get("species"));
			String timePoint = String.valueOf(data.get("timePoint"));

			ExtractRequestParams params = new ExtractRequestParams(calcType, showType, userId, domainId, missionId,
					domain, species.get(0), timePoint);
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
				int windSymbol = Integer.valueOf(String.valueOf(data.get("windSymbol"))); // 代表箭头风还是F风
				params.setWindSymbol(windSymbol);
			}
			int layer = Integer.valueOf(String.valueOf(data.get("layer")));
			if (layer <= 0)
				return AmpcResult.build(1000, "参数layer的值错误， 不应该小于等于0");
			params.setLayer(layer);
			ManageParams manageParams = new ManageParams();
			manageParams.setParams(params);
			String outFile = extractTiffService.buildTiff(manageParams);
			logger.info("get tiff file times: " + (System.currentTimeMillis() - startTimes) + "ms");
			if (outFile != null)
				return AmpcResult.ok(outFile);
		} catch (Exception e) {
			return AmpcResult.build(1003, "参数异常");
		}
		return AmpcResult.build(1001, "系统异常");
	}

}
