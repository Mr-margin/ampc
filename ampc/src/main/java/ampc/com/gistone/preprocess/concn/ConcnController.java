package ampc.com.gistone.preprocess.concn;

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

import ampc.com.gistone.util.AmpcResult;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/concn")
public class ConcnController {
	
	private static final Logger logger  = LoggerFactory.getLogger(ConcnController.class);
	
	@Autowired
	private ConcnService concnService;

	@RequestMapping(value = "/processData", method = RequestMethod.POST)
	public AmpcResult processData(@RequestBody Map<String, Object> requestData,
			HttpServletRequest request, HttpServletResponse response) {
		Map data = (Map) requestData.get("data");
		int userId = Integer.valueOf(String.valueOf(data.get("userId")));
		int domainId = Integer.valueOf(String.valueOf(data.get("domainId")));
		int missionId = Integer.valueOf(String.valueOf(data.get("missionId")));
		int scenarioId = Integer.valueOf(String.valueOf(data.get("scenarioId")));
		int domain = Integer.valueOf(String.valueOf(data.get("domain")));
		List date = (List) data.get("date");
		String timePoint = String.valueOf(data.get("timePoint"));
		String mode = String.valueOf(data.get("mode"));
		List filter = (List) data.get("filter");
		RequestParams requestParams = new RequestParams(userId, domainId, missionId, scenarioId, domain, date, timePoint, mode, filter);
		try {
			JSONObject res = (JSONObject) concnService.requestConcnData(requestParams);
			return AmpcResult.ok("success");
		} catch (Exception e) {
			logger.error("requestConcnData error", e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
}
