package ampc.com.gistone.preprocess.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.preprocess.concn.RequestParams;
import ampc.com.gistone.util.AmpcResult;

/**
 * Created by xll on 2017/3/16.
 */
@RestController
@RequestMapping(value = "/cmaq")
public class CMAQV2 {

	static final Logger logger = LoggerFactory.getLogger(CMAQV2.class);
	@Autowired
	private CityWorkerV2 cityWorkerV2;

	@RequestMapping(value = "/result/v2", method = RequestMethod.POST)
	public AmpcResult getResult(@RequestBody Map<String, Object> requestData) {
		try {
			Map data = (Map) requestData.get("data");
			Long userId = Long.valueOf(String.valueOf(data.get("userId")));
			Long domainId = Long.valueOf(String.valueOf(data.get("domainId")));
			Long missionId = Long.valueOf(String.valueOf(data.get("missionId")));
			Long scenarioId = Long.valueOf(String.valueOf(data.get("scenarioId")));
			int domain = Integer.valueOf(String.valueOf(data.get("domain")));
			List<String> dates = (List<String>) data.get("date");
			String timePoint = String.valueOf(data.get("timePoint"));
			String mode = String.valueOf(data.get("mode"));
			List filter = (List) data.get("filter");
			long startTimes = System.currentTimeMillis();
			RequestParams params = new RequestParams(userId, domainId, missionId, scenarioId, domain, dates, timePoint);
			if ("city".equals(mode)) {
				cityWorkerV2.exe(params, mode, filter, Constants.SHOW_TYPE_CONCN);
				logger.info((System.currentTimeMillis() - startTimes) + "ms");
				return AmpcResult.ok(cityWorkerV2.getResult());
			} else {
				return AmpcResult.build(1000, "mode参数错误");
			}

		} catch (Exception e) {
			logger.error("");
			return AmpcResult.build(1000, "参数错误");
		}
	}
}
