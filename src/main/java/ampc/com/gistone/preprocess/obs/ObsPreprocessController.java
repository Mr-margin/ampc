package ampc.com.gistone.preprocess.obs;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.fasterxml.jackson.core.JsonProcessingException;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.DateUtil;

@RestController
@RequestMapping(value = "/obs")
public class ObsPreprocessController {

	@Autowired
	private ObsQuartz obsQuartz;
	@Autowired
	private ObsService obsService;
	@Autowired
	private ObsDateService obsDateService;
	@Autowired
	private ObsHourService obsHourService;

	private final static Logger logger = LoggerFactory.getLogger(ObsPreprocessController.class);

	@RequestMapping(value = "/tests", method = RequestMethod.GET)
	public AmpcResult tests() throws JsonProcessingException {
		obsQuartz.doDayTask();
		obsQuartz.doHourTask();
		return AmpcResult.ok();
	}

	// 用于补充历史数据
	@RequestMapping(value = "/processData", method = RequestMethod.POST)
	public AmpcResult preprocess(@RequestBody Map<String, Object> requestData, HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException {
		Map data = (Map) requestData.get("data");
		String startDate = String.valueOf(data.get("startDate"));
		String endDate = String.valueOf(data.get("endDate"));
		String area = String.valueOf(data.get("area"));
		String res = String.valueOf(data.get("res"));
		List areaIds = (List) data.get("areaIds");

		if (startDate == null || endDate == null || area == null || res == null || areaIds == null)
			return AmpcResult.build(-1, "参数错误");

		if (Constants.RESOLUTION_DAY.equals(res)) {
			LocalDate startDateTime = DateUtil.convertStringToLocalDate(startDate);
			LocalDate endDateTime = DateUtil.convertStringToLocalDate(endDate);
			if (Constants.AREA_CITY.equals(area)) {
				obsDateService.preCollectDayDataInCitys(startDateTime, endDateTime, areaIds);
			}
			if (Constants.AREA_POINT.equals(area)) {
				obsDateService.preCollectDayDataInStations(startDateTime, endDateTime, areaIds);
			}
		}
		if (Constants.RESOLUTION_HOUR.equals(res)) {
			LocalDateTime startDateTime = DateUtil.convertStringToLocalDatetime(startDate);
			LocalDateTime endDateTime = DateUtil.convertStringToLocalDatetime(endDate);
			if (Constants.AREA_CITY.equals(area)) {
				if (startDateTime != null && endDateTime != null)
					obsHourService.preCollectHourDataInCitys(startDateTime, endDateTime, areaIds);
			}
			if (Constants.AREA_POINT.equals(area)) {
				if (startDateTime != null && endDateTime != null)
					obsHourService.preCollectHourDataInStations(startDateTime, endDateTime, areaIds);
			}
		}

		return AmpcResult.ok();
	}

}
