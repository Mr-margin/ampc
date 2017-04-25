package ampc.com.gistone.preprocess.obs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;
import ampc.com.gistone.preprocess.obs.entity.ObsHourlyBean;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

@Service
@Transactional
public class ObsHourService extends ObsService {

	private Logger logger = LoggerFactory.getLogger(ObsHourService.class);

	private final DateTimeFormatter hourTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
	private final DateTimeFormatter dayTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Autowired
	private TObsMapper obsMapper;

	public void preCollectHourDataInCitys(LocalDateTime startDateTime, LocalDateTime endDateTime,
			List<String> cityList) {

		if (cityList != null && cityList.size() != 0) {

			for (String city : cityList) {

				ObsParams params = new ObsParams();
				params.setStartDate(dayTimeFormatter.format(startDateTime));
				params.setEndDate(dayTimeFormatter.format(endDateTime));
				params.setArea(Constants.AREA_CITY);
				params.setRes(Constants.RESOLUTION_HOUR.toLowerCase());
				params.setAreaId(city);
				preCollectHourData(params, startDateTime, endDateTime);

			}
		}
	}

	private void preCollectHourData(ObsParams params, LocalDateTime startTime, LocalDateTime endTime) {

		// 请求obs接口
		ClientUtil clientUtil = new ClientUtil();
		AmpcResult ampcResult = clientUtil.doGet(params.toParams(), TaskSchedulerObsConfig.OBS_URL,
				TaskSchedulerObsConfig.timeout);
		Map interfaceMap = (Map) ampcResult.getData();
		// 转换格式
		Collection<ObsHourlyBean> obsHourlyBeanList = transformType(interfaceMap, params, startTime, endTime);
		// 将数据放到数据库中
		commonPutInDataBase(obsHourlyBeanList, Constants.RESOLUTION_HOUR);

	}

	public Collection<ObsHourlyBean> transformType(Map interfaceMap, ObsParams params, LocalDateTime startDateTime,
			LocalDateTime endDateTime) {

		Map<String, ObsHourlyBean> dateMap = new HashMap();

		long days = ChronoUnit.DAYS.between(startDateTime, endDateTime);

		for (int currday = 0; currday <= days; currday++) {

			String date = dayTimeFormatter.format(startDateTime.plus(currday, ChronoUnit.DAYS));

			ObsHourlyBean obsHourlyBean = new ObsHourlyBean();

			obsHourlyBean.setCity_station(params.getAreaId());

			obsHourlyBean.setDate(DateUtil.StrToDate1(date));

			obsHourlyBean.setMode(params.getArea());

			dateMap.put(date, obsHourlyBean);

		}

		Iterator<Map.Entry> iterator = interfaceMap.entrySet().iterator();

		while (iterator.hasNext()) {

			// species->{}
			Map.Entry entry = iterator.next();

			// specie
			String specie = entry.getKey().toString();

			Map dateHourMaps = (Map) entry.getValue();

			List<Map.Entry> list = new ArrayList<Map.Entry>();

			list.addAll(dateHourMaps.entrySet());
			// etc. 2016-12-12 : {}
			Map<String, List<Map.Entry>> dayEntrys = list.stream().collect(Collectors.groupingBy(evertEntry -> {

				// 按日分组
				String pattern = evertEntry.getKey().toString();

				return pattern.substring(0, pattern.indexOf(" "));

			}));

			Iterator<Map.Entry<String, List<Map.Entry>>> dayEntryIterator = dayEntrys.entrySet().iterator();

			while (dayEntryIterator.hasNext()) {

				Map.Entry<String, List<Map.Entry>> dayEntry = dayEntryIterator.next();

				String timePoint = dayEntry.getKey();

				LocalDate localDate = LocalDate.parse(timePoint, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (dateMap.containsKey(timePoint)) {

					ObsHourlyBean obshourlyBean = dateMap.get(timePoint);

					Map<String, List<String>> contentMap = obshourlyBean.getContentMap();

					List<String> speciesTo24Hours = null;

					if (contentMap.containsKey(specie)) {

						speciesTo24Hours = contentMap.get(specie);
					}

					else {

						speciesTo24Hours = new ArrayList<String>();

						for (int currHour = 0; currHour < 24; currHour++) {

							speciesTo24Hours.add("-");
						}
					}

					for (int currHour = 0; currHour < 24; currHour++) {

						LocalTime localTime = LocalTime.of(currHour, 0);

						LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

						// 获取小时字段
						String hourTimePoint = hourTimeFormatter.format(localDateTime);

						if (dateHourMaps.containsKey(hourTimePoint)) {

							Object v = dateHourMaps.get(hourTimePoint);
							if (v != null) {

								String speToValue = v.toString();
								// if("-".equals(speciesTo24Hours.get(currHour)))
								// {
								speciesTo24Hours.set(currHour, speToValue);
								// }
							}

						}
					}

					contentMap.put(specie, speciesTo24Hours);
				}
			}
		}

		Collection<ObsHourlyBean> obsHourlyBeanList = dateMap.values();

		obsHourlyBeanList = obsHourlyBeanList.stream().filter(ObsHourlyBean -> {

			return Objects.nonNull(ObsHourlyBean) && ObsHourlyBean.getDate() != null;

		}).collect(Collectors.toList());

		return obsHourlyBeanList;
	}

	public void commonPutInDataBase(Collection<ObsHourlyBean> obsHourlyBeanList, String timePoint) {

		Iterator<ObsHourlyBean> iteratorList = obsHourlyBeanList.iterator();

		while (iteratorList.hasNext()) {

			ObsHourlyBean newObsHourlyBean = iteratorList.next();

			String tName = checkAndAddTable(newObsHourlyBean, timePoint);
			newObsHourlyBean.setTableName(tName);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tableName", tName);
			params.put("date", newObsHourlyBean.getDate());
			params.put("mode", newObsHourlyBean.getMode());
			params.put("city_station", newObsHourlyBean.getCity_station());

			ObsBean oldObsHourlyBean = obsMapper.queryUnionResult(params);

			try {
				if (null != oldObsHourlyBean) {
					// 将oldObsHourlyBean的Id赋值给新的newObsHourlyBean
					newObsHourlyBean.setId(oldObsHourlyBean.getId());
					newObsHourlyBean.setContent(mapper.writeValueAsString(newObsHourlyBean.getContentMap()));
					obsMapper.update(newObsHourlyBean);

				} else {
					String id = UUID.randomUUID().toString();
					newObsHourlyBean.setId(id);
					newObsHourlyBean.setContent(mapper.writeValueAsString(newObsHourlyBean.getContentMap()));
					obsMapper.insert(newObsHourlyBean);
				}
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException | ObsHourlyService: convert Map to String error!", e);
				;
			}
		}

	}

	public void preCollectHourDataInStations(LocalDateTime startDateTime, LocalDateTime endTime,
			List<String> regionList) {

		if (regionList != null && regionList.size() != 0) {
			for (String stationId : regionList) {
				if (!StringUtils.isEmpty(stationId)) {
					preCollectHourDataInStation(startDateTime, endTime, stationId);

				}
			}
		}
	}

	private void preCollectHourDataInStation(LocalDateTime startDateTime, LocalDateTime endTime, String stationId) {

		ObsParams params = new ObsParams();
		params.setStartDate(dayTimeFormatter.format(startDateTime));
		params.setEndDate(dayTimeFormatter.format(endTime));
		params.setArea(Constants.AREA_POINT);
		params.setRes(Constants.RESOLUTION_HOUR.toLowerCase());
		params.setAreaId(stationId);
		preCollectHourData(params, startDateTime, endTime);

	}
}
