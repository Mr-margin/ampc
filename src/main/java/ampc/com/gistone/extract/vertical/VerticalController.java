package ampc.com.gistone.extract.vertical;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.StringUtil;

@RestController
@RequestMapping(value = "/extract")
public class VerticalController {
	private final static Logger logger = LoggerFactory.getLogger(VerticalController.class);

	@Autowired
	private VerticalService verticalService;

	// 获取封装参数
	@RequestMapping(value = "/vertical")
	public AmpcResult vertical(@RequestBody Map<String, Object> requestParams) {
		VerticalParams verticalParams = null;
		Map<String, Map> params = (Map) requestParams.get("data");
		try {
			String calcType = String.valueOf(params.get("calcType"));
			String showType = String.valueOf(params.get("showType"));
			Double xmin = Double.valueOf(String.valueOf(params.get("xmin")));
			Double ymin = Double.valueOf(String.valueOf(params.get("ymin")));
			Double xmax = Double.valueOf(String.valueOf(params.get("xmax")));
			Double ymax = Double.valueOf(String.valueOf(params.get("ymax")));
			Double space = Double.valueOf(String.valueOf(params.get("space")));
			Integer pointNums = Integer.valueOf(String.valueOf(params.get("pointNums")));
			Long userId = Long.valueOf(String.valueOf(params.get("userId")));
			Long domainId = Long.valueOf(String.valueOf(params.get("domainId")));
			Long missionId = Long.valueOf(String.valueOf(params.get("missionId")));
			Integer domain = Integer.valueOf(String.valueOf(params.get("domain")));
			String specie = String.valueOf(params.get("specie"));
			if (StringUtil.isEmpty(specie)) {
				logger.error("the specie params is wrong, the value notis null");
				return AmpcResult.build(1000, "参数specie的值错误，不应该为空");
			}
			String timePoint = String.valueOf(params.get("timePoint"));
			String day = String.valueOf(params.get("day"));
			Long scenarioId1 = Long.valueOf(String.valueOf(params.get("scenarioId1")));
			verticalParams = new VerticalParams(xmin, ymin, xmax, ymax, pointNums, userId, domainId, missionId, domain,
					specie, timePoint, day);
			verticalParams.setCalcType(calcType);
			verticalParams.setShowType(showType);
			verticalParams.setScenarioId1(scenarioId1);
		} catch (Exception e) {
			logger.error("参数异常", e);
			return AmpcResult.build(1003, "参数异常");
		}
		if (!Constants.CALCTYPE_SHOW.equals(verticalParams.getCalcType())) {
			Long scenarioId2 = Long.valueOf(String.valueOf(params.get("scenarioId2")));
			if (scenarioId2 == null) {
				logger.error("the ScenarioId2 params is wrong, the value notis null");
				return AmpcResult.build(1000, "参数ScenarioId2的值错误，不应该为空,或valctype错误");
			}
			verticalParams.setScenarioId2(scenarioId2);
		}
		if (!Constants.TIMEPOINT_H.equals(verticalParams.getTimePoint())) {
			verticalParams.setHour(0);
			if (Constants.TIMEPOINT_A.equals(verticalParams.getTimePoint())) {
				List<String> dates = null;
				try {
					dates = (List<String>) params.get("dates");
				} catch (Exception e) {
					logger.error("参数dates的值错误，不能为空", e);
					return AmpcResult.build(1000, "参数dates的值错误，不能为空");
				}
				if (dates.isEmpty()) {
					logger.error("the dates params is wrong, the value notis null");
					return AmpcResult.build(1000, "参数dates的值错误，不能为空");
				}
				verticalParams.setDates(dates);
			}
		} else {
			Integer hour = Integer.valueOf(String.valueOf(params.get("hour")));
			if (hour == null) {
				logger.error("the hour params is wrong, the value not null");
				return AmpcResult.build(1000, "参数hour的值错误，不应该为空");
			}
			if (hour < 0 || hour > 23) {
				logger.error("the hour params is wrong, the value should be between in 0 and 23");
				return AmpcResult.build(1000, "参数hour的值错误， 范围应该在[0,23]");
			}
			verticalParams.setHour(hour);
		}

		// 返回的是图片路径
		String res = verticalService.vertical(verticalParams);
		if (!StringUtil.isEmpty(res)) {
			// 还没有和前端交互，交互时要将图片路径返回到前端页面
			return AmpcResult.ok(res);
		}
		return AmpcResult.build(1001, "系统参数异常");
	}
}
