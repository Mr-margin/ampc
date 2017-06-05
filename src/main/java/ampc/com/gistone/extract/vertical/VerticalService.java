package ampc.com.gistone.extract.vertical;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.extract.Interpolation;
import ampc.com.gistone.extract.PointBean;
import ampc.com.gistone.extract.ProjectUtil;
import ampc.com.gistone.extract.Projection;
import ampc.com.gistone.extract.ResultPathUtil;
import ampc.com.gistone.extract.netcdf.Netcdf;
import ampc.com.gistone.util.CSVUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.StringUtil;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

@Service
public class VerticalService {
	private static Logger logger = LoggerFactory.getLogger(VerticalService.class);
	@Autowired
	private ResultPathUtil resultPathUtil;
	@Autowired
	private CLIHelper cliHelper;

	private String tempPath;
	private List<PointBean> pointBeanList;
	private Map<String, Object> attributes;
	private List<Variable> variableList1; // diff or ratio scenario1/scenario2
	private List<Variable> variableList2;
	private Projection projection;
	private double[][] resTemp;
	private double[][] res;

	public String vertical(VerticalParams verticalParams) {
		// 获取可变参数封装进variableList1，variableList2中，设置临时文件路径
		if (!getVariables(verticalParams)) {
			return null;
		}
		// 将projecttion参数从verticalParams中获取封装
		if (!setProjection(verticalParams)) {
			return null;
		}
		// 2. build earth surf point取座标划线
		buildSurfPoints(verticalParams);
		// 3. every layer level interpolation每层插值
		try {
			buildEveryLayerValue(verticalParams);
		} catch (IOException e) {
			logger.error("VerticalService | variables() : buildEveryLayerValue IOException");
			return null;
		} catch (InvalidRangeException e) {
			logger.error("VerticalService | variables() : buildEveryLayerValue InvalidRangeException");
			return null;
		}
		// 4. vertical interpolation在线上取点
		buildHeightInterpolation();
		// 返回verticalpng路径
		return createPng(verticalParams);

	}

	// 将projecttion参数从verticalParams中获取封装
	private boolean setProjection(VerticalParams verticalParams) {
		long readAttrTimes = System.currentTimeMillis();
		try {
			double xorig = Double.valueOf((String.valueOf(attributes.get("XORIG"))));
			double yorig = Double.valueOf((String.valueOf(attributes.get("YORIG"))));
			double xcell = Double.valueOf((String.valueOf(attributes.get("XCELL"))));
			double ycell = Double.valueOf((String.valueOf(attributes.get("YCELL"))));
			String lat_1 = attributes.get("P_ALP").toString();
			String lat_2 = attributes.get("P_BET").toString();
			String lat_0 = attributes.get("YCENT").toString();
			String lon_0 = attributes.get("XCENT").toString();
			int row = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
			int col = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
			verticalParams.setXorig(xorig);
			verticalParams.setYorig(yorig);
			verticalParams.setXcell(xcell);
			verticalParams.setYcell(ycell);
			verticalParams.setLat_1(lat_1);
			verticalParams.setLat_2(lat_2);
			verticalParams.setLat_0(lat_0);
			verticalParams.setLon_0(lon_0);
			verticalParams.setRow(row);
			verticalParams.setCol(col);
			logger.info("read attr, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
			logger.info("(xorig,yorig) = (" + xorig + "," + yorig + "), (xcell,ycell) = (" + xcell + "," + ycell
					+ "), (nrows, ncols) = (" + row + "," + col + ")");
		} catch (Exception e) {
			logger.error("read parameter error, at attributes", e);
			return false;
		}
		projection = ProjectUtil.getProj(attributes);
		if (projection == null) {
			logger.error("VerticalService.java projection is null");
			return false;
		}
		return true;
	}

	// 生成画图所需两个csv文件,将路径及图片生成路径传递入csh文件中画图
	private String createPng(VerticalParams verticalParams) {
		long readAttrTimes = System.currentTimeMillis();
		File pathFile = new File(resultPathUtil
				.getRealPath(resultPathUtil.getExtractConfig().getVerticalPngPath(), verticalParams.getUserId(),
						verticalParams.getDomainId(), verticalParams.getMissionId(), verticalParams.getScenarioId1(),
						verticalParams.getDomain())
				.replace("$currDate", DateUtil.DATEtoString(new Date(), DateUtil.DATE_FORMAT)));
		if (!pathFile.exists() && !pathFile.isDirectory()) {
			pathFile.mkdirs();
		}
		File juzhen = CSVUtil.createCSVFile(res, tempPath, Constants.CSV_JZ_PREFIX);
		// 获取对应条件污染物图像下标参数，用条件和污染物拼接成要向csh中传递的文件名，形成“名=下标参数”的字符串返回
		String specieIndex = getSpecieIndex(verticalParams);
		File csvIndexFile = new File(resultPathUtil.getExtractConfig().getCsvIndexPath());
		File specieIndexFile = CSVUtil.createIndexCSVFile(specieIndex, csvIndexFile.getAbsolutePath());
		String tempFilePath = tempPath + "/" + System.currentTimeMillis();
		String pngPath = resultPathUtil
				.getRealPath(resultPathUtil.getExtractConfig().getVerticalPngPath() + System.currentTimeMillis(),
						verticalParams.getUserId(), verticalParams.getDomainId(), verticalParams.getMissionId(),
						verticalParams.getScenarioId1(), verticalParams.getDomain())
				.replace("$currDate", DateUtil.DATEtoString(new Date(), DateUtil.DATE_FORMAT));
		if (juzhen == null || specieIndexFile == null) {
			return null;
		}
		ShellVertival shellVertival = new ShellVertival();
		shellVertival.setJuzhenPath(juzhen.getAbsolutePath());
		shellVertival.setSpecieIndexFilePath(specieIndexFile.getAbsolutePath());
		shellVertival.setNclPath(getClass().getClassLoader().getResource(Constants.NCL_PATH).getPath());
		shellVertival.setPngPath(new File(pngPath).getAbsolutePath());
		String filePath = tempFilePath + ".csh";
		logger.info("create two csv file is juzheng and specieIndex,Transfer parameter:" + shellVertival.toString());
		logger.info("build csh file ,Incoming parameter");
		// 根据摸版生成要调用的csh文件
		if (!buildCshWithVertical(filePath, shellVertival)) {
			return null;
		}
		logger.info("start Execute script");
		String pngFilePath = pngPath + ".png";
		// 运行filePath文件
		logger.info("run createPng csv, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
		if (cliHelper.process(new String[] { "csh", filePath })) {
			File pngFile = new File(pngFilePath);
			if (pngFile.exists() && pngFile.isFile()) {
				logger.info("run createPng ncl, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms,pngPath="
						+ pngFilePath);
				return pngFilePath;
			}
		}
		return null;
	}

	// 通过摸版动态加载csh文件，并执行
	private boolean buildCshWithVertical(String filePath, ShellVertival shellVertival) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(
					new FileReader(getClass().getClassLoader().getResource(Constants.CSH_TEMPLATE_PATH).getPath()));
			bw = new BufferedWriter(new FileWriter(filePath));
			String line = null;
			while ((line = br.readLine()) != null) {
				String writeLine = line.replace("${juzhenPath}", shellVertival.getJuzhenPath().replace("\\", "/"))
						.replace("${specieIndexFilePath}", shellVertival.getSpecieIndexFilePath().replace("\\", "/"))
						.replace("${pngPath}", shellVertival.getPngPath().replace("\\", "/"))
						.replace("${nclPath}", shellVertival.getNclPath().replace("\\", "/"));
				bw.write(writeLine);
				bw.write("\n");
				bw.flush();
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
			return false;
		} catch (IOException e) {
			logger.error("", e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
					return false;
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("", e);
					return false;
				}
			}
		}
		return true;
	}

	// 获取对应条件污染物图像下标参数，用条件和污染物拼接成要向csh中传递的文件名，形成“名=下标参数”的字符串返回
	private String getSpecieIndex(VerticalParams verticalParams) {
		String specie = verticalParams.getSpecie();
		String showType = verticalParams.getShowType();
		String calcType = verticalParams.getCalcType();
		String timePoint = verticalParams.getTimePoint();
		Map<String, Map<String, List>> specieMap = resultPathUtil.getSheetName(showType, calcType, timePoint, specie);
		Map<String, List> valueMap = specieMap.get(specie);
		List<Float> valueList = valueMap.get(Constants.VALUE_LIST);
		String speciespecieIndex = "";
		for (Float float1 : valueList) {
			speciespecieIndex += float1 + ",";
		}
		if (!StringUtil.isEmpty(speciespecieIndex)) {
			speciespecieIndex.substring(0, speciespecieIndex.length() - 1);
		}
		logger.info("Determine what pollutant data is loaded according to the conditions:" + showType + "_" + calcType
				+ "_" + timePoint + "_" + specie + "=" + speciespecieIndex);
		return showType + "_" + calcType + "_" + timePoint + "_" + specie + "=" + speciespecieIndex;
	}

	// 4. vertical interpolation在线上取点
	private void buildHeightInterpolation() {
		long readAttrTimes = System.currentTimeMillis();
		int[] height = Constants.HEIGHT;
		double heightSpace = Constants.HEIGHT_SPACE;
		int targetLayer = (int) (height[height.length - 1] / heightSpace + 1);
		double[] targetArray = new double[targetLayer];
		for (int tl = 0; tl < targetLayer; tl++) {
			targetArray[tl] = tl * heightSpace;
		}
		res = new double[targetLayer][pointBeanList.size()];
		for (int p = 0; p < pointBeanList.size(); p++) {
			for (int tl = 0; tl < targetArray.length; tl++) {
				double th = targetArray[tl];
				double v = 0;
				for (int h = 0; h < height.length - 1; h++) {
					double hdown = height[h];
					double hup = height[h + 1];
					if (th >= hdown && th <= hup) {
						v = resTemp[h][p] + ((th - hdown) / (hup - hdown)) * (resTemp[h + 1][p] - resTemp[h][p]);
					}
				}
				res[tl][p] = v;
			}
		}
		logger.info("run buildHeightInterpolation, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
	}

	// 3. every layer level interpolation每层插值
	private void buildEveryLayerValue(VerticalParams verticalParams) throws IOException, InvalidRangeException {
		long readAttrTimes = System.currentTimeMillis();
		int length = Constants.HEIGHT.length;
		resTemp = new double[length][pointBeanList.size()];
		for (int layer = 1; layer <= length; layer++) {
			Interpolation interpolation = new Interpolation(verticalParams.getHour(), layer, verticalParams.getXorig(),
					verticalParams.getYorig(), verticalParams.getXcell(), verticalParams.getYcell(),
					verticalParams.getRow(), verticalParams.getCol());
			for (int p = 0; p < pointBeanList.size(); p++) {
				PointBean pb = pointBeanList.get(p);
				Point2D point2D = projection.transform(pb.getLon(), pb.getLat());
				pb.setXlcc(point2D.getX());
				pb.setYlcc(point2D.getY());
				double value = 0;
				if (Constants.CALCTYPE_SHOW.equals(verticalParams.getCalcType())) {
					for (Variable variable : variableList1) {
						double vv = interpolation.interpolation(variable, point2D);
						if (vv == Constants.OVERBORDER) {
							value = Constants.OVERBORDER;
							break;
						} else {
							value += vv / variableList1.size();
						}
					}
				} else if (Constants.CALCTYPE_DIFF.equals(verticalParams.getCalcType())
						|| Constants.CALCTYPE_RATIO.equals(verticalParams.getCalcType())) {
					double value1 = 0;
					double value2 = 0;
					for (Variable variable : variableList1) {
						double vv = interpolation.interpolation(variable, point2D);
						if (vv == Constants.OVERBORDER) {
							value = Constants.OVERBORDER;
							break;
						} else {
							value1 += vv / variableList1.size();
						}
					}
					for (Variable variable : variableList2) {
						double vv = interpolation.interpolation(variable, point2D);
						if (vv == Constants.OVERBORDER) {
							value = Constants.OVERBORDER;
							break;
						} else {
							value2 += vv / variableList2.size();
						}
					}
					if (Constants.CALCTYPE_DIFF.equals(verticalParams.getCalcType())) {
						value = value2 - value1;
					} else {
						if (value1 == 0) {
							value1 = Constants.MINIMUM;
						}
						value = (value2 - value1) / value1;
					}
				}
				resTemp[layer - 1][p] = value;
			}
		}
		logger.info("run buildEveryLayerValue, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
	}

	// 获取可变参数封装进attributes中，设置临时文件路径
	private boolean getVariables(VerticalParams verticalParams) {
		long readAttrTimes = System.currentTimeMillis();
		boolean b = false;
		String concnFilePath = resultPathUtil.getResultFilePath(verticalParams.getDay(), verticalParams.getShowType(),
				verticalParams.getTimePoint(), verticalParams.getUserId(), verticalParams.getDomainId(),
				verticalParams.getMissionId(), verticalParams.getScenarioId1(), verticalParams.getDomain(), null);
		String timePoint = verticalParams.getTimePoint();
		tempPath = resultPathUtil
				.getRealPath(resultPathUtil.getExtractConfig().getVerticalTepmPath(), verticalParams.getUserId(),
						verticalParams.getDomainId(), verticalParams.getMissionId(), verticalParams.getScenarioId1(),
						verticalParams.getDomain())
				.replace("$currDate", DateUtil.DATEtoString(new Date(), DateUtil.DATE_FORMAT));
		File tempFile = new File(tempPath);
		tempPath = tempFile.getAbsolutePath();
		variableList1 = new ArrayList<Variable>();
		variableList2 = new ArrayList<Variable>();
		if (Constants.TIMEPOINT_A.equals(timePoint)) {
			List<String> list = verticalParams.getDates();
			for (int i = 0; i < list.size(); i++) {
				String day = list.get(i);
				concnFilePath = concnFilePath.replace("$Day", day);
				b = buildVariables(concnFilePath, verticalParams);// 找到对应netcdf文件，读取数据封装进variableList1，variableList2中
			}
			try {
				attributes = Netcdf.getAttributes(concnFilePath);
				logger.info("read getVariables, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
				logger.info("tempPath=" + tempPath);
			} catch (IOException e) {
				logger.error("VerticalService | variables() : getAttributes IOException");
				return false;
			}
		} else if (Constants.TIMEPOINT_D.equals(timePoint) || Constants.TIMEPOINT_H.equals(timePoint)) {
			concnFilePath = concnFilePath.replace("$Day", verticalParams.getDay());
			b = buildVariables(concnFilePath, verticalParams);
			try {
				attributes = Netcdf.getAttributes(concnFilePath);
				logger.info("read getVariables, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
				logger.info("tempPath=" + tempPath);
			} catch (IOException e) {
				logger.error("VerticalService | variables() : getAttributes IOException");
				return false;
			}
		}
		return b;
	}

	// 找到对应netcdf文件，读取数据封装进variableList1，variableList2中,能正常封装返回true
	private boolean buildVariables(String concnFilePath, VerticalParams verticalParams) {
		try {
			String specie = verticalParams.getSpecie();
			long openNcTimes = System.currentTimeMillis();
			NetcdfFile nc;
			try {
				nc = NetcdfFile.open(concnFilePath);
				logger.info("VerticalService | variables() :buildVariables  open nc file " + concnFilePath
						+ ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");
			} catch (IOException e) {
				logger.error("VerticalService | variables() :buildVariables  open file " + concnFilePath + " error");
				return false;
			}
			if (nc == null) {
				logger.error("open netcdf file :" + concnFilePath + "error");
				return false;
			}
			Variable variable1 = nc.findVariable(null, specie);
			variableList1.add(variable1);
			if (!Constants.CALCTYPE_SHOW.equals(verticalParams.getCalcType())) {
				// 需要ScenarioId2
				String filePath = resultPathUtil.getResultFilePath(verticalParams.getDay(),
						verticalParams.getShowType(), verticalParams.getTimePoint(), verticalParams.getUserId(),
						verticalParams.getDomainId(), verticalParams.getMissionId(), verticalParams.getScenarioId2(),
						verticalParams.getDomain(), null).replace("$Day", verticalParams.getDay());
				NetcdfFile nc2;
				try {
					nc2 = NetcdfFile.open(filePath);
				} catch (IOException e) {
					logger.error("VerticalService | variables() :buildVariables  open file " + filePath + " error");
					return false;
				}
				Variable variable2 = nc2.findVariable(null, specie);
				variableList2.add(variable2);
			}
		} catch (Exception e) {
			logger.error("VerticalService | variables() :buildVariables   error");
			return false;
		}
		return true;
	}

	// 2. build earth surf point取座标划线
	private void buildSurfPoints(VerticalParams verticalParams) {
		long startTimes = System.currentTimeMillis();
		pointBeanList = new ArrayList<>();
		int pointNums = verticalParams.getPointNums();
		double xmin = verticalParams.getXmin();
		double ymin = verticalParams.getYmin();
		double xmax = verticalParams.getXmax();
		double ymax = verticalParams.getYmax();
		double xsub = xmax - xmin;
		double ysub = ymax - ymin;
		if (xsub == 0.0) { // xmin = xmax
			double s = ysub / pointNums;
			for (int i = 0; i < pointNums; i++) {
				double yMiddle = ymin + i * s;
				PointBean pb = new PointBean(Projection.transToLat(yMiddle), Projection.transToLon(xmin), xmin,
						yMiddle);
				pointBeanList.add(pb);
			}
		} else { // ymin = ymax or y = kx + b
			double slope = ysub / xsub; // 斜率
			double b = ymin - (slope * xmin);
			double s = (xmax - xmin) / pointNums;
			for (int i = 0; i < pointNums; i++) {
				double xMiddle = xmin + i * s;
				double yMiddle = slope * xMiddle + b;
				PointBean pb = new PointBean(Projection.transToLat(yMiddle), Projection.transToLon(xMiddle), xMiddle,
						yMiddle);
				pointBeanList.add(pb);
			}
		}
		logger.info("get need line surf point list, times = " + (System.currentTimeMillis() - startTimes) + "ms");
	}
}
