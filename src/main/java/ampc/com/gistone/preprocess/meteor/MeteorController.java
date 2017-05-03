package ampc.com.gistone.preprocess.meteor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.preprocess.concn.RequestParams;
import ampc.com.gistone.util.AmpcResult;

@RestController
@RequestMapping(value = "/meteor")
public class MeteorController {

	private static final Logger logger = LoggerFactory.getLogger(MeteorController.class);

	@Autowired
	private MeteorService meteorService;

	@RequestMapping(value = "/processData", method = RequestMethod.POST)
	public AmpcResult processData(@RequestBody Map<String, Object> requestData, HttpServletRequest request,
			HttpServletResponse response) {
		Map data = (Map) requestData.get("data");
		try {
			Long userId = Long.valueOf(String.valueOf(data.get("userId")));
			Long domainId = Long.valueOf(String.valueOf(data.get("domainId")));
			Long missionId = Long.valueOf(String.valueOf(data.get("missionId")));
			Long scenarioId = Long.valueOf(String.valueOf(data.get("scenarioId")));
			int domain = Integer.valueOf(String.valueOf(data.get("domain")));
			List date = (List) data.get("date");
			String timePoint = String.valueOf(data.get("timePoint"));
			// String mode = String.valueOf(data.get("mode"));
			// List filter = (List) data.get("filter");
			RequestParams requestParams = new RequestParams(userId, domainId, missionId, scenarioId, domain, date,
					timePoint);

			boolean res = meteorService.requestMeteorData(requestParams);
			if (res)
				return AmpcResult.ok("success");
			return AmpcResult.build(1004, "未查询出数据");
		} catch (Exception e) {
			logger.error("requestMeteorData error", e);
			return AmpcResult.build(1003, "参数异常");
		}
	}
}
