package ampc.com.gistone.extract;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;

import ampc.com.gistone.extract.image.ImageColorConfig;
import ampc.com.gistone.util.DateUtil;

@Component
public class ResultPathUtil {

	private final static Logger logger = LoggerFactory.getLogger(ResultPathUtil.class);
	private ExtractConfig extractConfig;
	private ImageColorConfig imageColorConfig;

	@PostConstruct
	public void init() {
		getExtractConfig("/extract.properties");
		buildImageColorConfig();
	}

	private void buildImageColorConfig() {
		imageColorConfig = new ImageColorConfig();
		buildColorSetting("/drawPicture/color/concn_color_setting.xlsx");
		buildColorSetting("/drawPicture/color/emis_color_setting.xlsx");
	}

	private void buildColorSetting(String configPath) {
		InputStream ins;
		ins = getClass().getResourceAsStream(configPath);
		if (ins != null) {
			try {
				Workbook wb = WorkbookFactory.create(ins);
				int sheets = wb.getNumberOfSheets();
				for (int i = 0; i < sheets; i++) {
					buildColorMap(wb.getSheetAt(i));
				}
			} catch (EncryptedDocumentException e) {
				logger.error("ResultPathUtil | buildColorSetting EncryptedDocumentException", e);
			} catch (InvalidFormatException e) {
				logger.error("ResultPathUtil | buildColorSetting InvalidFormatException", e);
			} catch (IOException e) {
				logger.error("ResultPathUtil | buildColorSetting IOException", e);
			}

		} else {
			logger.error(configPath + " file does not exist!");
		}
	}

	private void buildColorMap(Sheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		String sheetName = sheet.getSheetName().trim().toLowerCase();
		Map<String, Map<String, List>> map = imageColorConfig.getSheetMap(sheetName);
		Row titleTow = sheet.getRow(0);
		int cellCount = titleTow.getLastCellNum();
		String[] titleName = new String[cellCount];
		for (int i = 0; i < cellCount; i++) {
			Cell cell = titleTow.getCell(i);
			String value = getCellValue(0, i, cell);
			if (StringUtils.isEmpty(value))
				return;
			titleName[i] = value.toUpperCase();
		}
		int count = 0;
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			if (count == 0) {
				rowIterator.next();
				count++;
				continue;
			}
			Row row = rowIterator.next();
			int colorR = 0;
			int colorG = 0;
			int colorB = 0;
			Color color = null;
			long argbs = 0;
			for (int i = 0; i < 3; i++) {
				Cell cell = row.getCell(i);
				String value = getCellValue(count, i, cell);
				if (count != lastRowNum) { // 不用校验R、G、B三列最后一行单元格是否为空
					if (StringUtils.isEmpty(value)) {
						logger.error("[行 :" + (count + 1) + ",列: " + (i + 1) + "] 数据是空值！");
						return;
					}
					if (i == 0) {
						colorR = Integer.valueOf(value);
					} else if (i == 1) {
						colorG = Integer.valueOf(value);
					} else if (i == 2) {
						colorB = Integer.valueOf(value);
					}
				}
			}
			color = new Color(colorR, colorG, colorB, 255);
			argbs = (255 << 24) + (colorR << 16) + (colorG << 8) + colorB; // a默认为255
			for (int i = 3; i < cellCount; i++) {
				Cell cell = row.getCell(i);
				String value = getCellValue(count, i, cell);
				// if (count != lastRowNum && (i != 0 || i != 1 || i != 1)) { //
				// 每个sheet中R、G、B列对应的单元格最后一行可以为空
				if (StringUtils.isEmpty(value)) {
					logger.error("[行 :" + (count + 1) + ",列: " + (i + 1) + "] 数据是空值！");
					return;
				}

				// }
				float v = Float.valueOf(value);

				String specie = titleName[i];
				Map<String, List> cvMap;
				if (map.containsKey(specie)) {
					cvMap = map.get(specie);
				} else {
					cvMap = new HashMap<String, List>();
					map.put(specie, cvMap);
				}
				List<Float> valueList;
				List<Color> colorList;
				List<Integer> colorLongList;
				if (cvMap.containsKey(Constants.VALUE_LIST)) {
					valueList = (List<Float>) cvMap.get(Constants.VALUE_LIST);
				} else {
					valueList = new ArrayList<Float>();
					cvMap.put(Constants.VALUE_LIST, valueList);
				}
				valueList.add(v);
				if (count != lastRowNum) {
					if (cvMap.containsKey(Constants.COLOR_LIST)) {
						colorList = (List<Color>) cvMap.get(Constants.COLOR_LIST);
					} else {
						colorList = new ArrayList<Color>();
						cvMap.put(Constants.COLOR_LIST, colorList);
					}
					if (cvMap.containsKey(Constants.COLOR_LONG_LIST)) {
						colorLongList = cvMap.get(Constants.COLOR_LONG_LIST);
					} else {
						colorLongList = new ArrayList<Integer>();
						cvMap.put(Constants.COLOR_LONG_LIST, colorLongList);
					}
					colorList.add(color);
					colorLongList.add((int) (argbs & 0xffffffffl));
				}
			}
			count++;
		}
	}

	private static String getCellValue(int row, int column, Cell cell) {
		String value = "";
		if (cell == null)
			return value;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			DataFormatter dataFormatter = new DataFormatter();
			Format format = dataFormatter.getDefaultFormat(cell);
			value = format.format(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			try {
				throw new Exception("请不要在描述文件中使用公式: [行 :" + row + ",列: " + (column + 1) + "]");
			} catch (Exception e) {
				logger.error("请不要在描述文件中使用公式: [行 :" + row + ",列: " + (column + 1) + "]");
			}
		}
		return value;
	}

	/**
	 * 获取后处理文件的配置路径
	 * 
	 * @param config
	 */
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

	public ImageColorConfig getImageColorConfig() {
		return imageColorConfig;
	}

	public void setImageColorConfig(ImageColorConfig imageColorConfig) {
		this.imageColorConfig = imageColorConfig;
	}

}
