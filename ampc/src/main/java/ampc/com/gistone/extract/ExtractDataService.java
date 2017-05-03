package ampc.com.gistone.extract;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.extract.netcdf.Netcdf;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

@Service
public class ExtractDataService {

	@Autowired
	private ResultPathUtil resultPathUtil;
	private final static Logger logger = LoggerFactory.getLogger(ExtractDataService.class);
	private final static String FLAG = "flag";

	private ExtractConfig extractConfig;
	private ExtractRequestParams params;
	private PointBean[][] pointBeanArray;
	private String filePath1;
	private String filePath2;
	private NetcdfFile ncFile1;
	private Map<String, List<Variable>> variableMap1; // diff or ratio
														// scenario1/scenario2
	private Map<String, List<Variable>> variableMap2;
	private Map attributes;
	private Projection projection;
	private NumberFormat nf;
	private List<PointBean> pointBeanList;
	private Interpolation interpolation;
	private List<Map<String, Object>> res;

	private StringBuffer sbcsv_latlon = new StringBuffer();
	private StringBuffer sbcsv_lcc = new StringBuffer();

	private String pathOut;

	public synchronized List<Map<String, Object>> buildData(ExtractRequestParams extractRequestParams)
			throws IOException, TransformException, FactoryException, InvalidRangeException {
		try {
			extractConfig = resultPathUtil.getExtractConfig();
			params = extractRequestParams;
			res = new ArrayList<Map<String, Object>>();
			getNcFilePath();
			getVariables();

			projection = buildProject(params);
			if (projection == null)
				return null;

			nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			nf.setMaximumFractionDigits(10);

			pointBeanList = buildPointList(res);

			ObjectMapper mapper = new ObjectMapper();
			if (Constants.SHOW_TYPE_CONCN.equals(params.getShowType())) {
				pathOut = extractConfig.getConcnFilePath();
			} else if (Constants.SHOW_TYPE_EMIS.equals(params.getShowType())) {
				pathOut = extractConfig.getEmisFilePath();
			} else if (Constants.SHOW_TYPE_WIND.equals(params.getShowType())) {
				pathOut = extractConfig.getWindFilePath();
			}
			pathOut = pathOut.replace("$userid", String.valueOf(params.getUserId()))
					.replace("$domainid", String.valueOf(params.getDomainId()))
					.replace("$missionid", String.valueOf(params.getMissionId()))
					.replace("$scenarioid", String.valueOf(params.getScenarioId1()))
					.replace("$domain", String.valueOf(params.getDomain()));
			pathOut = pathOut + "/" + params.getCalcType() + "-" + params.getTimePoint();
			saveFile(pathOut, "/data.json", mapper.writeValueAsString(res));
			saveFile(pathOut, "/extract-data-lonlat.csv", sbcsv_latlon.toString());
			saveFile(pathOut, "/extract-data-lcc.csv", sbcsv_lcc.toString());
			exportExcel(pointBeanArray, pathOut);

			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void getVariables() {
		String timePoint = params.getTimePoint();
		variableMap1 = new HashMap<String, List<Variable>>();
		variableMap2 = new HashMap<String, List<Variable>>();
		if (Constants.TIMEPOINT_A.equals(timePoint)) {

			List<String> list = params.getDates();
			for (int i = 0; i < list.size(); i++) {
				String day = list.get(i);
				buildVariables(day);
			}
			try {
				attributes = Netcdf.getAttributes(filePath1 + list.get(0));
			} catch (IOException e) {
				logger.error("getAttributes IOException");
				return;
			}
		} else if (Constants.TIMEPOINT_D.equals(timePoint) || Constants.TIMEPOINT_H.equals(timePoint)) {
			buildVariables(params.getDay());

			try {
				attributes = Netcdf.getAttributes(filePath1 + params.getDay());
			} catch (IOException e) {
				logger.error("getAttributes IOException");
				return;
			}
		}
	}

	private void buildVariables(String day) {
		String p1 = filePath1 + day;
		long openNcTimes = System.currentTimeMillis();
		NetcdfFile nc1;
		try {
			nc1 = NetcdfFile.open(p1);
		} catch (IOException e) {
			logger.error("open file " + p1 + " error");
			return;
		}
		if (nc1 == null)
			return;
		ncFile1 = nc1;
		logger.info("open nc file" + p1 + ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");

		for (String specie : params.getSpecies()) {
			Variable variable1 = nc1.findVariable(null, specie);
			List<Variable> variableList1;
			if (variableMap1.containsKey(specie)) {
				variableList1 = variableMap1.get(specie);
			} else {
				variableList1 = new ArrayList<Variable>();
				variableMap1.put(specie, variableList1);
			}
			variableList1.add(variable1);

			if (!Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
				String p2 = filePath2 + day;
				NetcdfFile nc2;
				try {
					nc2 = NetcdfFile.open(p2);
				} catch (IOException e) {
					logger.error("open file " + p2 + " error");
					return;
				}
				Variable variable2 = nc2.findVariable(null, specie);

				List<Variable> variableList2;
				if (variableMap2.containsKey(specie)) {
					variableList2 = variableMap2.get(specie);
				} else {
					variableList2 = new ArrayList<Variable>();
					variableMap2.put(specie, variableList2);
				}
				variableList2.add(variable2);
			}
		}
	}

	private void getNcFilePath() {
		String showType = params.getShowType();
		if (Constants.SHOW_TYPE_CONCN.equals(showType)) {
			filePath1 = extractConfig.getConcnFilePath() + "/" + (Constants.TIMEPOINT_H.equals(params.getTimePoint())
					? extractConfig.getConcnHourlyPrefix() : extractConfig.getConcnDailyPrefix());
		} else if (Constants.SHOW_TYPE_EMIS.equals(showType)) {
			filePath1 = extractConfig.getEmisFilePath() + "/" + (Constants.TIMEPOINT_D.equals(params.getTimePoint())
					? extractConfig.getEmisDailyPrefix() : extractConfig.getEmisHourlyPrefix());
		} else if (Constants.SHOW_TYPE_WIND.equals(showType)) {
			filePath1 = extractConfig.getWindFilePath() + "/" + (Constants.TIMEPOINT_D.equals(params.getTimePoint())
					? extractConfig.getMeteorDailyWindPrefix() : extractConfig.getMeteorHourlyWindPrefix());
		}
		filePath2 = filePath1;

		filePath1 = filePath1.replace("$userid", String.valueOf(params.getUserId()))
				.replace("$domainid", String.valueOf(params.getDomainId()))
				.replace("$missionid", String.valueOf(params.getMissionId()))
				.replace("$scenarioid", String.valueOf(params.getScenarioId1()))
				.replace("$domain", String.valueOf(params.getDomain()));

		if (!Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
			filePath2 = filePath2.replace("$userid", String.valueOf(params.getUserId()))
					.replace("$domainid", String.valueOf(params.getDomainId()))
					.replace("$missionid", String.valueOf(params.getMissionId()))
					.replace("$scenarioid", String.valueOf(params.getScenarioId2()))
					.replace("$domain", String.valueOf(params.getDomain()));
		}

	}

	private Projection buildProject(ExtractRequestParams params)
			throws TransformException, FactoryException, IOException {

		long readAttrTimes = System.currentTimeMillis();

		double xorig = Double.valueOf((String.valueOf(attributes.get("XORIG"))));
		double yorig = Double.valueOf((String.valueOf(attributes.get("YORIG"))));
		double xcell = Double.valueOf((String.valueOf(attributes.get("XCELL"))));
		double ycell = Double.valueOf((String.valueOf(attributes.get("YCELL"))));
		String lat_1 = attributes.get("P_ALP").toString();
		String lat_2 = attributes.get("P_BET").toString();
		String lat_0 = attributes.get("YCENT").toString();
		String lon_0 = attributes.get("XCENT").toString();
		int totalLayer = ncFile1.findDimension("LAY").getLength();
		int row = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
		int col = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
		params.setXorig(xorig);
		params.setYorig(yorig);
		params.setXcell(xcell);
		params.setYcell(ycell);
		params.setLat_1(lat_1);
		params.setLat_2(lat_2);
		params.setLat_0(lat_0);
		params.setLon_0(lon_0);
		logger.info("read attr, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
		int layer = params.getLayer();
		if (layer < 1 || layer > totalLayer) {
			logger.error("the layer params should be between in 1 and " + totalLayer);
		}
		logger.info("(xorig,yorig) = (" + xorig + "," + yorig + "), (xcell,ycell) = (" + xcell + "," + ycell
				+ "), (nrows, ncols) = (" + row + "," + col + ")");

		interpolation = new Interpolation(params.getHour(), layer, xorig, yorig, xcell, ycell, row, col);

		return ProjectUtil.getProj(attributes);

	}

	public List<PointBean> buildPointList(List<Map<String, Object>> res) throws IOException, InvalidRangeException {
		List<PointBean> pointBeanList = new ArrayList<>();
		int cols = params.getCols();
		int rows = params.getRows();
		double xcellsize = (params.getXmax() - params.getXmin()) / cols;
		double ycellsize = (params.getYmax() - params.getYmin()) / rows;
		double x = params.getXmin();
		double y = params.getYmin();

		pointBeanArray = new PointBean[rows + 1][cols + 1];
		long startTimes = System.currentTimeMillis();
		for (int i = 0; i <= rows; i++) {
			for (int j = 0; j <= cols; j++) {
				double merc_y = y;
				double merc_x;
				if (i == 0 && j == 0) {
					merc_x = params.getXmin();
				} else if (j == 0) {
					merc_x = x;
				} else {
					x += xcellsize;
					merc_x = x;
				}
				PointBean pb = new PointBean(Projection.transToLat(merc_y), Projection.transToLon(merc_x), merc_x,
						merc_y);
				pointBeanList.add(pb);
				pointBeanArray[i][j] = pb;

				Map<String, Double> resMap = getValue(pb);
				if (resMap.containsKey(FLAG) && resMap.get(FLAG) == Constants.OVERBORDER) {
					if (params.getBorderType() == 1) {
						Map map = new HashMap<>();
						map.put("x", nf.format(pb.getX()));
						map.put("y", nf.format(pb.getY()));
						// map.put("lat", pb.getLat());
						// map.put("lon", pb.getLon());
						// map.put("xlcc", p.getX());
						// map.put("ylcc", p.getY());
						String str = "";
						List<String> species = params.getSpecies();
						Double value = 0D;
						for (String specie : species) {
							map.put(specie, value.toString());
							str += value.toString();
							str += "/";
						}

						res.add(map);
						str.substring(0, str.lastIndexOf("/") - 1);
						pb.setValue(str);
					}
				} else {

					Map map = new HashMap<>();
					map.put("x", nf.format(pb.getX()));
					map.put("y", nf.format(pb.getY()));
					// map.put("lat", pb.getLat());
					// map.put("lon", pb.getLon());
					// map.put("xlcc", p.getX());
					// map.put("ylcc", p.getY());
					String str = "";
					List<String> species = params.getSpecies();
					for (String specie : species) {
						String resStr = "";
						Double value = (Double) resMap.get(specie);
						if (specie.equals("WDIR")) {
							float vv = Constants.binarySearchKeyDir(value);
							resStr = Constants.WINDDIRMAP.get(vv);
							map.put(specie, resStr);
						} else if (specie.equals("WSPD")) {
							float vv = Constants.binarySearchKeySpd(value);
							resStr = String.valueOf((int) vv);
							map.put(specie, resStr);
						} else {
							resStr = nf.format(value);
							map.put(specie, resStr);
						}
						str += resStr;
						str += "/";
					}

					res.add(map);
					str.substring(0, str.lastIndexOf("/") - 1);
					pb.setValue(str);
				}

				// 保存经纬度、兰伯特数据
				buildDataFile(pb);
			}
			x = params.getXmin();
			y += ycellsize;
		}
		logger.info(
				"get need show point list and calc value, times = " + (System.currentTimeMillis() - startTimes) + "ms");
		return pointBeanList;
	}

	private void buildDataFile(PointBean pb) {
		sbcsv_latlon.append(pb.getLon()).append(",").append(pb.getLat()).append(",").append(pb.getValue())
				.append("\r\n");
		sbcsv_lcc.append(pb.getXlcc()).append(",").append(pb.getYlcc()).append(",").append(pb.getValue())
				.append("\r\n");
	}

	private Map<String, Double> getValue(PointBean pb) throws IOException, InvalidRangeException {
		try {
			Point2D p = projection.transform(pb.getLon(), pb.getLat());
			pb.setXlcc(p.getX());
			pb.setYlcc(p.getY());
			Map<String, Double> resMap = new HashMap<String, Double>();
			double value = 0;

			if (Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {

				for (Map.Entry entry : variableMap1.entrySet()) {
					String specie = (String) entry.getKey();
					List<Variable> variableList1 = (List<Variable>) entry.getValue();
					for (Variable variable : variableList1) {
						double vv = interpolation.interpolation(variable, p);
						if (vv == Constants.OVERBORDER) {
							resMap.put(FLAG, Constants.OVERBORDER);
							return resMap;
						}
						value += vv / variableList1.size();
						resMap.put(specie, value);
					}
				}

			} else if ("diff".equals(params.getCalcType()) || "ratio".equals(params.getCalcType())) {
				double value1 = 0;
				double value2 = 0;
				for (Map.Entry entry : variableMap1.entrySet()) {
					String specie = (String) entry.getKey();
					List<Variable> variableList1 = (List<Variable>) entry.getValue();
					for (Variable variable : variableList1) {
						double vv = interpolation.interpolation(variable, p);
						if (vv == Constants.OVERBORDER) {
							resMap.put(FLAG, Constants.OVERBORDER);
							return resMap;
						}
						value1 += vv / variableList1.size();
					}
					List<Variable> variableList2 = variableMap2.get(specie);
					for (Variable variable : variableList2) {
						double vv = interpolation.interpolation(variable, p);
						if (vv == Constants.OVERBORDER) {
							resMap.put(FLAG, Constants.OVERBORDER);
							return resMap;
						}
						value2 += vv / variableList2.size();
					}
					if ("diff".equals(params.getCalcType())) {
						value = value2 - value1;
					} else {
						value = (value2 - value1) / value1;
					}
					resMap.put(specie, value);
				}

			}
			return resMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void exportExcel(PointBean[][] pointBeanArray, String pathOut)
			throws IOException, InvalidRangeException, TransformException, FactoryException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet oneOfSheet = workbook.createSheet("data");

		for (int i = 0; i < pointBeanArray.length; i++) {
			HSSFRow hssfRow = oneOfSheet.createRow(i);
			for (int j = 0; j < pointBeanArray[i].length; j++) {
				HSSFCell cell = hssfRow.createCell(j);
				PointBean pb = pointBeanArray[i][j];
				cell.setCellValue(pb.getValue());
			}
		}
		String excel = pathOut + "/extract-data.xls";
		File file = new File(pathOut);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(excel);
		workbook.write(out);
		out.close();

	}

	private static void saveFile(String pathOut, String fileName, String content) throws IOException {
		String dataPath = pathOut + fileName;
		File filePath = new File(pathOut);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		File file = new File(dataPath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file.getPath());
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(content);
		bufferedWriter.flush();
		bufferedWriter.close();
	}

}
