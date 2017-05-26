package ampc.com.gistone.extract.vertical;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.StringUtil;
import oracle.net.aso.e;

@RestController
@RequestMapping(value = "/extract")
public class VerticalController {
	private final static Logger logger = LoggerFactory.getLogger(VerticalController.class);

	@Autowired
	private VerticalService verticalService;
//获取封装参数
	@RequestMapping(value = "/vertical")
	public AmpcResult vertical(@RequestBody Map<String, Object> requestParams) {
		Map<String, Map> Params = (Map) requestParams.get("vertical");
		String calcType = String.valueOf(Params.get("calcType"));
		String showType = String.valueOf(Params.get("showType"));
		Double xmin = Double.valueOf(String.valueOf(Params.get("xmin")));
		Double ymin = Double.valueOf(String.valueOf(Params.get("ymin")));
		Double xmax = Double.valueOf(String.valueOf(Params.get("xmax")));
		Double ymax = Double.valueOf(String.valueOf(Params.get("ymax")));
		Double space = Double.valueOf(String.valueOf(Params.get("space")));
		Integer pointNums = Integer.valueOf(String.valueOf(Params.get("pointNums")));
		Long userId = Long.valueOf(String.valueOf(Params.get("userId")));
		Long domainId = Long.valueOf(String.valueOf(Params.get("domainId")));
		Long missionId = Long.valueOf(String.valueOf(Params.get("missionId")));
		Integer domain = Integer.valueOf(String.valueOf(Params.get("domain")));
		String specie = String.valueOf(Params.get("specie"));
		if (StringUtil.isEmpty(specie)) {
			logger.error("the specie params is wrong, the value notis null");
			return AmpcResult.build(1000, "参数specie的值错误，不应该为空");
		}
		String timePoint = String.valueOf(Params.get("timePoint"));
		String day = String.valueOf(Params.get("day"));
		Integer hour = Integer.valueOf(String.valueOf(Params.get("hour")));
		Long scenarioId1 = Long.valueOf(String.valueOf(Params.get("scenarioId1")));
		Long scenarioId2 = Long.valueOf(String.valueOf(Params.get("scenarioId2")));
		List<String> dates = (List<String>) Params.get("dates");
		VerticalParams verticalParams = new VerticalParams(xmin, ymin, xmax, ymax, pointNums, userId, domainId,
				missionId, domain, specie, timePoint, day);
		verticalParams.setCalcType(calcType);
		verticalParams.setShowType(showType);
		verticalParams.setScenarioId1(scenarioId1);

		if (!Constants.CALCTYPE_SHOW.equals(verticalParams.getCalcType())) {
			if (scenarioId2 == null) {
				logger.error("the ScenarioId2 params is wrong, the value notis null");
				return AmpcResult.build(1000, "参数ScenarioId2的值错误，不应该为空");
			}
			verticalParams.setScenarioId2(scenarioId2);
		}
		if (!Constants.TIMEPOINT_H.equals(verticalParams.getTimePoint())) {
			verticalParams.setHour(0);
			if (Constants.TIMEPOINT_A.equals(verticalParams.getTimePoint())) {
				if (verticalParams.getDates().isEmpty()) {
					logger.error("the dates params is wrong, the value notis null");
					return AmpcResult.build(1000, "参数dates的值错误，不能为空");
				}
				verticalParams.setDates(dates);
			}
		} else {
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

//返回的是图片路径
		String res = verticalService.vertical(verticalParams);
		if (!StringUtil.isEmpty(res)) {
			//还没有和前端交互，交互时要将图片路径返回到前端页面
			return AmpcResult.ok(res);
		}
		return AmpcResult.build(1001, "系统异常");
	}
}
