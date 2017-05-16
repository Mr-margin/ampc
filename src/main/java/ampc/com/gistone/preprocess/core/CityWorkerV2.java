package ampc.com.gistone.preprocess.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import ampc.com.gistone.database.model.TSite;
import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.extract.ExtractConfig;
import ampc.com.gistone.extract.ProjectUtil;
import ampc.com.gistone.extract.ResultPathUtil;
import ampc.com.gistone.extract.netcdf.Netcdf;
import ampc.com.gistone.preprocess.concn.RequestParams;
import ampc.com.gistone.util.DateUtil;
import ucar.ma2.Array;

/**
 * Created by xll on 2017/3/17.
 */
@Service
public class CityWorkerV2 {

	private static Logger logger = LoggerFactory.getLogger(CityWorkerV2.class);
	@Autowired
	private ResultPathUtil resultPathUtil;
	@Autowired
	private CalculateCityService calculateCityService;
	private ExtractConfig extractConfig;
	Map<String, List<Point>> pointGroup;
	Map result;
	Map<String, ArrayList<TSite>> stationMap;
	Map attributes;

	public void Cities(List<String> cities) throws TransformException, FactoryException {

		pointGroup = new HashMap<>();
		String[] proj = ProjectUtil.getProjParam(attributes);
		for (String city : cities) {
			// List<HashMap> points = PointList.build(city);
			List<TSite> stations = stationMap.get(city);
			List ps = new ArrayList<>();
			for (TSite hm : stations) {
				Point p = new Point();
				p.setLat(Double.valueOf(hm.getLat()));
				p.setLon(Double.valueOf(hm.getLon()));
				p.setName(hm.getStationName());
				p.setProj(proj);
				p.setCode(hm.getStationId());
				p.buildIndex(attributes);
				if (p.isCross()) {
					// logger.info("Point " + p.getCode() + " is at cell X:" +
					// p.xi + " Y:" + p.yi + " beyond the domain");
				} else {
					ps.add(p);
					// logger.info("Point " + p.getCode() + " is at cell X:" +
					// p.xi + " Y:" + p.yi);
				}
			}
			if (ps.size() > 0)
				pointGroup.put(city, ps);
		}
	}

	public void exe(RequestParams params, String mode, List<String> filter, String type)
			throws IOException, TransformException, FactoryException, ParseException {
		String timePoint = params.getTimePoint();
		Long userId = params.getUserId();
		Long domainId = params.getDomainId();
		Long missionId = params.getMissionId();
		Long scenarioId = params.getScenarioId();
		int domain = params.getDomain();
		List<String> dates = params.getDate();
		stationMap = calculateCityService.getStations();
		// this.Cities(filter);
		result = new LinkedHashMap<>();
		extractConfig = resultPathUtil.getExtractConfig();

		if (Constants.SHOW_TYPE_CONCN.equals(type)) {

			switch (timePoint.toLowerCase()) {
			case "d":
				for (String date : dates) {
					String base = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_CONCN,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String day = DateUtil.strConvertToStr(date);
					String ncPath = base.replace("$Day", day);
					if (!Constants.checkHourlyFile(ncPath))
						continue;
					logger.info("Loading File " + ncPath);
					Array[] ncFile = Netcdf.loadArray(ncPath, Constants.DAILYSPECIES);
					attributes = Netcdf.getAttributes(ncPath);
					this.Cities(filter);
					buildDailyData(ncFile, filter, date, Constants.DAILYSPECIES);

					for (Map.Entry entry : (Set<Map.Entry>) result.entrySet()) {
						Map cityOrStationMap = (Map) entry.getValue();
						for (Map.Entry e : (Set<Map.Entry>) cityOrStationMap.entrySet()) {
							String cityOrStation = (String) e.getKey();
							Map dateMap = (Map) cityOrStationMap.get(cityOrStation);
							Map speciesMap = (Map) dateMap.get(date);
							HashMap hm = new HashMap();
							for (String spe : Constants.DAILYSPECIES) {
								if (spe.equals("O3_8_MAX")) {
									hm.put("O3_8", ((Map) speciesMap.get(spe)).get(0));
								} else if (spe.equals("NO2") || spe.equals("SO2") || spe.equals("CO")) {
									hm.put(spe + "_24", ((Map) speciesMap.get(spe)).get(0));
								} else {
									hm.put(spe, ((Map) speciesMap.get(spe)).get(0));
								}
							}
							if (!speciesMap.containsKey("AQI")) {
								speciesMap.put("AQI", new HashMap<>());
							}
							((Map) speciesMap.get("AQI")).put(0, AqiUtil.getAqi(hm));
						}
					}
				}
				break;
			case "h":
				for (String date : dates) {
					String base = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_CONCN,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String day = DateUtil.strConvertToStr(date);
					String ncPath = base.replace("$Day", day);
					if (!Constants.checkHourlyFile(ncPath))
						continue;
					logger.info("Loading File " + ncPath);
					Array[] ncFile = Netcdf.loadArray(ncPath, Constants.HOURLYSPECIES);
					attributes = Netcdf.getAttributes(ncPath);
					this.Cities(filter);
					buildHourlyData(ncFile, filter, date, Constants.HOURLYSPECIES);

					for (Map.Entry entry : (Set<Map.Entry>) result.entrySet()) {
						Map cityOrStationMap = (Map) entry.getValue();
						for (Map.Entry e : (Set<Map.Entry>) cityOrStationMap.entrySet()) {
							String cityOrStation = (String) e.getKey();
							Map dateMap = (Map) cityOrStationMap.get(cityOrStation);
							Map speciesMap = (Map) dateMap.get(date);
							HashMap hm = new HashMap();
							for (String spe : Constants.HOURLYSPECIES) {

								if (spe.equals("O3")) {
									hm.put("O3_8", ((Map) speciesMap.get(spe)).get(0));
								} else {
									hm.put(spe, ((Map) speciesMap.get(spe)).get(0));
								}
							}
							if (!speciesMap.containsKey("AQI")) {
								speciesMap.put("AQI", new HashMap<>());
							}
							((Map) speciesMap.get("AQI")).put(0, AqiUtil.getAqis(hm));
						}
					}

				}

				break;
			}

		} else if (Constants.SHOW_TYPE_METEOR.equals(type)) {

			switch (timePoint.toLowerCase()) {
			case "d":
				for (String date : dates) {
					String base1 = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_METEOR,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String base2 = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_WIND,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String day = DateUtil.strConvertToStr(date);
					String ncPath1 = base1.replace("$Day", day); // 气象文件路径，不包含风
					String ncPath2 = base2.replace("$Day", day); // 风场文件路径
					if (!Constants.checkHourlyFile(ncPath1))
						continue;
					if (!Constants.checkHourlyFile(ncPath2))
						continue;
					logger.info("Loading File " + ncPath1);
					Array[] ncFile1 = Netcdf.loadArray(ncPath1, Constants.METEORSPECIES);
					attributes = Netcdf.getAttributes(ncPath1);
					this.Cities(filter);
					buildDailyData(ncFile1, filter, date, Constants.METEORSPECIES);

					Array[] ncFile2 = Netcdf.loadArray(ncPath2, Constants.WINDSPECIES);
					buildDailyData(ncFile2, filter, date, Constants.WINDSPECIES);

				}
				break;
			case "h":
				for (String date : dates) {
					String base1 = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_METEOR,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String base2 = resultPathUtil.getResultFilePath(date, Constants.SHOW_TYPE_WIND,
							params.getTimePoint(), params.getUserId(), params.getDomainId(), params.getMissionId(),
							params.getScenarioId(), params.getDomain(), params.getDateTypeMap());
					String day = DateUtil.strConvertToStr(date);
					String ncPath1 = base1.replace("$Day", day); // 气象文件路径，不包含风
					String ncPath2 = base2.replace("$Day", day); // 风场文件路径
					if (!Constants.checkHourlyFile(ncPath1))
						continue;
					logger.info("Loading File " + ncPath1);
					Array[] ncFile1 = Netcdf.loadArray(ncPath1, Constants.METEORSPECIES);
					attributes = Netcdf.getAttributes(ncPath1);
					this.Cities(filter);
					buildHourlyData(ncFile1, filter, date, Constants.METEORSPECIES);

					Array[] ncFile2 = Netcdf.loadArray(ncPath2, Constants.WINDSPECIES);
					buildHourlyData(ncFile2, filter, date, Constants.WINDSPECIES);

				}
				break;
			}

		}

	}

	private void buildHourlyData(Array[] ncFile, List<String> filter, String date, String[] hourlyspecies)
			throws JsonProcessingException {
		// 处理所有物种的结果
		int c = 0;
		for (String s : hourlyspecies) {

			float v[][][][] = ((float[][][][]) ncFile[c].copyToNDJavaArray());

			for (String city : filter) {
				List<Point> ps = pointGroup.get(city);

				if (!result.containsKey("point")) {
					result.put("point", new HashMap<>());
				}
				if (!result.containsKey("city")) {
					result.put("city", new HashMap<>());
				}
				Map pointMap = (Map) result.get("point");
				Map cityMap = (Map) result.get("city");
				Map dateMap;
				if (cityMap.containsKey(city)) {
					dateMap = (Map) cityMap.get(city);
				} else {
					dateMap = new HashMap<>();
					cityMap.put(city, dateMap);
				}

				Map speciesMap;
				if (dateMap.containsKey(date)) {
					speciesMap = (Map) dateMap.get(date);
				} else {
					speciesMap = new HashMap<>();
					dateMap.put(date, speciesMap);
				}

				if (!speciesMap.containsKey(s)) {
					int point = 0;
					Map layerMap = new HashMap<>();
					speciesMap.put(s, layerMap);
					for (Point p : ps) {
						String stationId = p.getCode();
						Map pointDateMap;
						if (pointMap.containsKey(stationId)) {
							pointDateMap = (Map) pointMap.get(stationId);
						} else {
							pointDateMap = new HashMap<>();
							pointMap.put(stationId, pointDateMap);
						}
						Map pointDateSMap;
						if (pointDateMap.containsKey(date)) {
							pointDateSMap = (Map) pointDateMap.get(date);
						} else {
							pointDateSMap = new HashMap<>();
							pointDateMap.put(date, pointDateSMap);
						}
						Map pointDateSLMap = new HashMap<>();
						pointDateSMap.put(s, pointDateSLMap);
						for (int l = 0; l < v[0].length; l++) {
							float[] hv;
							if (layerMap.containsKey(l)) {
								hv = (float[]) layerMap.get(l);
							} else {
								hv = new float[24];
							}
							float[] curr = new float[24];
							for (int h = 0; h < v.length; h++) {
								curr[h] = v[h][l][p.yi][p.xi];
								hv[h] += v[h][l][p.yi][p.xi] / ps.size();
							}
							pointDateSLMap.put(l, curr);
							layerMap.put(l, hv);
							// logger.info(p.getName() + " date: " + date + " x:
							// " + p.xi + " y: " + p.yi + " z: " + l
							// + " " + s + ":" + Jsons.objToJson(curr));
						}
						point++;
					}
				}
			}
			c++;
		}

	}

	private void buildDailyData(Array[] ncFile, List<String> filter, String date, String[] dailySpecies) {
		int c = 0;
		for (String s : dailySpecies) {

			float v[][][][] = ((float[][][][]) ncFile[c].copyToNDJavaArray());

			for (String city : filter) {
				List<Point> ps = pointGroup.get(city);

				if (!result.containsKey("point")) {
					result.put("point", new HashMap<>());
				}
				if (!result.containsKey("city")) {
					result.put("city", new HashMap<>());
				}
				Map pointMap = (Map) result.get("point");
				Map cityMap = (Map) result.get("city");
				Map dateMap;
				if (cityMap.containsKey(city)) {
					dateMap = (Map) cityMap.get(city);
				} else {
					dateMap = new HashMap<>();
					cityMap.put(city, dateMap);
				}

				Map speciesMap;
				if (dateMap.containsKey(date)) {
					speciesMap = (Map) dateMap.get(date);
				} else {
					speciesMap = new HashMap<>();
					dateMap.put(date, speciesMap);
				}

				if (!speciesMap.containsKey(s)) {
					int point = 0;
					Map layerMap = new HashMap<>();
					speciesMap.put(s, layerMap);
					for (Point p : ps) {
						String stationId = p.getCode();
						Map pointDateMap;
						if (pointMap.containsKey(stationId)) {
							pointDateMap = (Map) pointMap.get(stationId);
						} else {
							pointDateMap = new HashMap<>();
							pointMap.put(stationId, pointDateMap);
						}
						Map pointDateSMap;
						if (pointDateMap.containsKey(date)) {
							pointDateSMap = (Map) pointDateMap.get(date);
						} else {
							pointDateSMap = new HashMap<>();
							pointDateMap.put(date, pointDateSMap);
						}
						Map pointDateSLMap = new HashMap<>();
						pointDateSMap.put(s, pointDateSLMap);
						for (int i = 0; i < v[0].length; i++) {
							float hv = 0;
							pointDateSLMap.put(i, v[0][i][p.yi][p.xi]);
							if (layerMap.containsKey(i)) {
								hv = (float) layerMap.get(i);
							}
							hv += v[0][i][p.yi][p.xi] / ps.size();
							layerMap.put(i, hv);
							// logger.info(p.getName() + " date: " + date + " x:
							// " + p.xi + " y: " + p.yi + " z: " + i
							// + " " + s + ":" + v[0][i][p.yi][p.xi]);
						}
						point++;
					}
				}
			}
			c++;
		}

	}

	public Map getResult() {
		return result;
	}
}
