package ampc.com.gistone.preprocess.obs;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import ampc.com.gistone.database.inter.TObsMapper;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;
import ampc.com.gistone.preprocess.obs.entity.ObsDailyBean;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

@Service
@Transactional
public class ObsDateService extends ObsService {

	private Logger logger = LoggerFactory.getLogger(ObsDateService.class);

	@Autowired
	private TObsMapper obsMapper;

	public void preCollectDayDataInCitys(LocalDate startDateTime, LocalDate endDateTime, List<String> cityList) {

		if (cityList != null && cityList.size() != 0) {
			for (String city : cityList) {
				ObsParams params = new ObsParams();
				params.setStartDate(dayTimeFormatter.format(startDateTime));
				params.setEndDate(dayTimeFormatter.format(endDateTime));
				params.setArea(AREA_CITY);
				params.setRes(RESOLUTION_DAY.toLowerCase());
				params.setAreaId(city);

				preCollectDayData(params, startDateTime, endDateTime);
			}
		}
	}

	private void preCollectDayData(ObsParams params, LocalDate startDateTime, LocalDate endTime) {

		// 请求obs接口
		ClientUtil clientUtil = new ClientUtil();
		AmpcResult ampcResult = clientUtil.doGet(params.toParams(), TaskSchedulerObsConfig.OBS_URL,
				TaskSchedulerObsConfig.timeout);
		Map interfaceMap = (Map) ampcResult.getData();
		// 转换格式
		Collection<ObsDailyBean> list = transformType(interfaceMap, params, startDateTime, endTime);
		// 将数据放到数据库中
		commonPutInDataBase(list, RESOLUTION_DAY);

	}

	public Collection<ObsDailyBean> transformType(Map interfaceMap, ObsParams params, LocalDate startDateTime,
			LocalDate endDateTime) {

		long days = ChronoUnit.DAYS.between(startDateTime, endDateTime);
		Map<String, ObsDailyBean> dateMap = new HashMap<String, ObsDailyBean>();

		for (int currDay = 0; currDay <= days; currDay++) {

			String date = dayTimeFormatter.format(startDateTime.plus(currDay, ChronoUnit.DAYS));
			ObsDailyBean obsDailyBean = new ObsDailyBean();
			obsDailyBean.setMode(params.getArea());
			obsDailyBean.setCity_station(params.getAreaId());
			Date da = DateUtil.StrToDate1(date);
			obsDailyBean.setDate(DateUtil.StrToDate1(date));
			dateMap.put(date, obsDailyBean);
		}

		Iterator<Map.Entry<Object, Object>> iterator = interfaceMap.entrySet().iterator();

		while (iterator.hasNext()) {

			Map.Entry entry = iterator.next();
			Object speciesKey = entry.getKey();
			Map dateEntryValue = (Map) entry.getValue();
			Set<Map.Entry> dateEntrySet = dateEntryValue.entrySet();

			for (Map.Entry dateEntry : dateEntrySet) {

				Object date = dateEntry.getKey();
				ObsDailyBean obsDailyBean = null;
				if (dateMap.containsKey(date)) {

					obsDailyBean = dateMap.get(date);
					Map speciesMap = obsDailyBean.getContentMap();
					if (!speciesMap.containsKey(speciesKey)) {
						speciesMap.put(speciesKey, dateEntry.getValue());
					}
					dateMap.put(date.toString(), obsDailyBean);
				}
			}
		}

		Collection<ObsDailyBean> list = dateMap.values();

		list = list.stream().filter(ObsDailyBean -> {

			return Objects.nonNull(ObsDailyBean) && ObsDailyBean.getDate() != null;

		}).collect(Collectors.toList());

		return list;
	}

	public void commonPutInDataBase(Collection<ObsDailyBean> list, String timePoint) {

		Iterator<ObsDailyBean> iteratorList = list.iterator();
		while (iteratorList.hasNext()) {

			ObsDailyBean newObsDailyBean = iteratorList.next();

			String tName = checkAndAddTable(newObsDailyBean, timePoint);
			newObsDailyBean.setTableName(tName);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tableName", tName);
			params.put("date", newObsDailyBean.getDate());
			params.put("mode", newObsDailyBean.getMode());
			params.put("city_station", newObsDailyBean.getCity_station());

			ObsBean oldObsDailyBean = obsMapper.queryUnionResult(params);

			try {
				if (null != oldObsDailyBean) {
					// 将oldObsDailyBean的Id赋值给新的newObsDailyBean
					newObsDailyBean.setId(oldObsDailyBean.getId());
					newObsDailyBean.setContent(mapper.writeValueAsString(newObsDailyBean.getContentMap()));
					obsMapper.update(newObsDailyBean);

				} else {
					String id = UUID.randomUUID().toString();
					newObsDailyBean.setId(id);
					newObsDailyBean.setContent(mapper.writeValueAsString(newObsDailyBean.getContentMap()));
					obsMapper.insert(newObsDailyBean);
				}
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException | ObsDailyService: convert Map to String error!", e);
				;
			}
		}
	}

	public void preCollectDayDataInStations(LocalDate startDateTime, LocalDate endTime, List<String> regionList) {

		if (regionList != null && regionList.size() != 0) {

			for (String stationId : regionList) {

				if (!StringUtils.isEmpty(stationId)) {

					preCollectDayDataInStation(startDateTime, endTime, stationId);

				}
			}
		}
	}

	private void preCollectDayDataInStation(LocalDate startDateTime, LocalDate endTime, String stationId) {

		ObsParams params = new ObsParams();
		params.setStartDate(dayTimeFormatter.format(startDateTime));
		params.setEndDate(dayTimeFormatter.format(endTime));
		params.setArea(AREA_POINT);
		params.setRes(RESOLUTION_DAY.toLowerCase());
		params.setAreaId(stationId);

		preCollectDayData(params, startDateTime, endTime);

	}

}
