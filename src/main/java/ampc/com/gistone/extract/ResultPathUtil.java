package ampc.com.gistone.extract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ampc.com.gistone.util.DateUtil;

@Component
public class ResultPathUtil {

	private final static Logger logger = LoggerFactory.getLogger(ResultPathUtil.class);
	private ExtractConfig extractConfig;

	@PostConstruct
	public void init() {
		getExtractConfig("/extract.properties");
	}

	private void getExtractConfig(String config) {

		InputStream ins = getClass().getResourceAsStream(config);
		Properties pro = new Properties();
		try {
			pro.load(ins);
			extractConfig = new ExtractConfig();
			extractConfig.setConcnFilePath((String) pro.get("concnFilePath"));
			extractConfig.setEmisFilePath((String) pro.get("emisFilePath"));
			extractConfig.setMeteorFnlFilePath((String) pro.get("meteorFnlFilePath"));
			extractConfig.setMeteorGfsFilePath((String) pro.get("meteorGfsFilePath"));
			extractConfig.setMeteorMoment((String) pro.get("meteorMoment"));
			extractConfig.setConcnDailyPrefix((String) pro.get("concnDailyPrefix"));
			extractConfig.setConcnHourlyPrefix((String) pro.get("concnHourlyPrefix"));
			extractConfig.setMeteorDailyPrefix((String) pro.get("meteorDailyPrefix"));
			extractConfig.setMeteorHourlyPrefix((String) pro.get("meteorHourlyPrefix"));
			extractConfig.setMeteorDailyWindPrefix((String) pro.get("meteorDailyWindPrefix"));
			extractConfig.setMeteorHourlyWindPrefix((String) pro.get("meteorHourlyWindPrefix"));
			extractConfig.setEmisDailyPrefix((String) pro.get("emisDailyPrefix"));
			extractConfig.setEmisHourlyPrefix((String) pro.get("emisHourlyPrefix"));
			extractConfig.setTiffFilePath((String) pro.get("tiffFilePath"));
			extractConfig.setTiffFileName((String) pro.get("tiffFileName"));
			extractConfig.setImageFilePath((String) pro.get("imageFilePath"));
			extractConfig.setImageFileName((String) pro.get("imageFileName"));
		} catch (FileNotFoundException e) {
			logger.error(config + " file does not exits!", e);
		} catch (IOException e) {
			logger.error("load " + config + " file error!", e);
		}

		try {
			if (ins != null)
				ins.close();
		} catch (IOException e) {
			logger.error("close " + config + " file error!", e);
		}
	}

	public String getRealPath(String filePath, Long userId, Long domainId, Long missionId, Long scenarioId,
			int domain) {
		String base = filePath.replace("$userid", String.valueOf(userId)).replace("$domainid", String.valueOf(domainId))
				.replace("$missionid", String.valueOf(missionId)).replace("$scenarioid", String.valueOf(scenarioId))
				.replace("$domain", String.valueOf(domain));
		return base;
	}

	// 气象数据文件路径有fnl和gfs之分
	public String getResultFilePath(String date, String showType, String timePoint, Long userId, Long domainId,
			Long missionId, Long scenarioId, int domain, Map<String, String[]> dateTypeMap) {
		if (Constants.SHOW_TYPE_CONCN.equals(showType)) {
			String filePath1 = extractConfig.getConcnFilePath() + "/" + (Constants.TIMEPOINT_H.equals(timePoint)
					? extractConfig.getConcnHourlyPrefix() : extractConfig.getConcnDailyPrefix());
			String base = getRealPath(filePath1, userId, domainId, missionId, scenarioId, domain);
			return base + "$Day";
		} else if (Constants.SHOW_TYPE_EMIS.equals(showType)) {
			String filePath1 = extractConfig.getEmisFilePath() + "/" + (Constants.TIMEPOINT_H.equals(timePoint)
					? extractConfig.getEmisHourlyPrefix() : extractConfig.getEmisDailyPrefix());
			String base = getRealPath(filePath1, userId, domainId, missionId, scenarioId, domain);
			return base + "$Day";
		} else {

			String[] fnlDateArray = (String[]) dateTypeMap.get(Constants.MODEL_DATA_TYPE_FNL);
			String[] gfsDateArray = (String[]) dateTypeMap.get(Constants.MODEL_DATA_TYPE_GFS);
			String[] firstGfsDateArray = (String[]) dateTypeMap.get(Constants.MODEL_DATA_TYPE_GFS_FIRST);
			if (fnlDateArray != null) {
				boolean bool = Arrays.asList(fnlDateArray).contains(date);
				if (bool) {
					String year = date.substring(0, 4);
					if (Constants.SHOW_TYPE_METEOR.equals(showType)) {
						String filePath1 = extractConfig.getMeteorFnlFilePath() + "/"
								+ (Constants.TIMEPOINT_H.equals(timePoint) ? extractConfig.getMeteorHourlyPrefix()
										: extractConfig.getMeteorDailyPrefix());
						String base1 = filePath1.replace("$userid", String.valueOf(userId))
								.replace("$domainid", String.valueOf(domainId)).replace("$year", String.valueOf(year))
								.replace("$domain", String.valueOf(domain));
						return base1 + "$Day";
					} else if (Constants.SHOW_TYPE_WIND.equals(showType)) {
						String filePath2 = extractConfig.getMeteorFnlFilePath() + "/"
								+ (Constants.TIMEPOINT_H.equals(timePoint) ? extractConfig.getMeteorHourlyWindPrefix()
										: extractConfig.getMeteorDailyWindPrefix());
						String base2 = filePath2.replace("$userid", String.valueOf(userId))
								.replace("$domainid", String.valueOf(domainId)).replace("$year", String.valueOf(year))
								.replace("$domain", String.valueOf(domain));
						return base2 + "$Day";
					}
				}
			}
			if (gfsDateArray != null) {
				boolean bool = Arrays.asList(gfsDateArray).contains(date);
				if (bool) {
					String utcStr = DateUtil.zoneFormat(firstGfsDateArray[0]); // 组织gfs文件的目录名称，第一天gfs的世界时+18
					if (utcStr != null) {
						if (Constants.SHOW_TYPE_METEOR.equals(showType)) {
							String filePath1 = extractConfig.getMeteorGfsFilePath() + "/"
									+ (Constants.TIMEPOINT_H.equals(timePoint) ? extractConfig.getMeteorHourlyPrefix()
											: extractConfig.getMeteorDailyPrefix());
							String base1 = filePath1.replace("$userid", String.valueOf(userId))
									.replace("$domainid", String.valueOf(domainId))
									.replace("$date", utcStr + extractConfig.getMeteorMoment())
									.replace("$domain", String.valueOf(domain));
							return base1 + "$Day";
						} else if (Constants.SHOW_TYPE_WIND.equals(showType)) {
							String filePath2 = extractConfig.getMeteorGfsFilePath() + "/"
									+ (Constants.TIMEPOINT_H.equals(timePoint)
											? extractConfig.getMeteorHourlyWindPrefix()
											: extractConfig.getMeteorDailyWindPrefix());
							String base2 = filePath2.replace("$userid", String.valueOf(userId))
									.replace("$domainid", String.valueOf(domainId))
									.replace("$date", utcStr + extractConfig.getMeteorMoment())
									.replace("$domain", String.valueOf(domain));
							return base2 + "$Day";
						}
					}
				}
			}
		}
		return null;
	}

	public ExtractConfig getExtractConfig() {
		return extractConfig;
	}

	public void setExtractConfig(ExtractConfig extractConfig) {
		this.extractConfig = extractConfig;
	}

}
