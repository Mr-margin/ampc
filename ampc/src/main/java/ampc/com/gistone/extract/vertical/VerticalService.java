package ampc.com.gistone.extract.vertical;

import java.awt.geom.Point2D;
import java.io.File;
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
import ampc.com.gistone.extract.netcdf.Netcdf;
import ampc.com.gistone.util.CSVUtil;
import ampc.com.gistone.util.excelUtil.DateUtil;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

@Service
public class VerticalService {
	private static Logger logger = LoggerFactory.getLogger(VerticalService.class);
	@Autowired
	private SpecieIndexList specieIndexList;

	private String tempPath = Constants.CSV_TEMP_PATH + DateUtil.getYYYYMMDDLineStr(new Date())+"/";
	private List<PointBean> pointBeanList;
	private String filePath1;
	private String filePath2;
	private Map<String, Object> attributes;
	private NetcdfFile ncFile;
	private List<Variable> variableList1; // diff or ratio scenario1/scenario2
	private List<Variable> variableList2;
	private Projection projection;
	private double[][] resTemp;
	private double[][] res;

	// private final static int[] height =
	// {0,50,100,200,300,400,500,700,1000,1500,2000,3000,4000}; //现有的垂直层
	private final static int[] height = { 0, 50, 100, 200, 300, 400, 500, 700, 1000, 1500, 2000, 3000 }; // 现有的垂直层
	private final static double heightSpace = 50;

	public String vertical(VerticalParams verticalParams, String concnFilePath) {
		//获取可变参数封装进variableList1，variableList2中
		getVariables(concnFilePath, verticalParams);
		//将projecttion参数从verticalParams中获取封装
		setProjection(verticalParams);
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
		//返回verticalpng路径
		return createPng(verticalParams);

	}

	private void setProjection(VerticalParams verticalParams) {
		long readAttrTimes = System.currentTimeMillis();
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
		projection = ProjectUtil.getProj(attributes);
		if (projection == null) {
			logger.error("projection is null");
			return;
		}

	}
//生成画图所需两个csv文件,将路径及图片生成路径传递入csh文件中画图
	private String createPng(VerticalParams verticalParams) {
		logger.info("create two csv file is juzheng and specieIndex");
		File juzhen = CSVUtil.createCSVFile(res, tempPath, Constants.CSV_JZ_PREFIX);
		String specieIndex = getSpecieIndex(verticalParams);
		File specieIndexFile = CSVUtil.createIndexCSVFile(specieIndex, Constants.CSV_INDEX_PATH);
		String path = tempPath + System.currentTimeMillis();
		if (juzhen == null || specieIndexFile == null) {
			return null;
		}
		ShellVertival shellVertival = new ShellVertival();
		shellVertival.setJuzhenPath(juzhen.getAbsolutePath());
		shellVertival.setSpecieIndexFilePath(specieIndexFile.getAbsolutePath());
		shellVertival.setNclPath(new File(Constants.NCL_PATH).getAbsolutePath());
		shellVertival.setPngPath(new File(path).getAbsolutePath());
		String filePath = path + ".csh";
		try {
			logger.info("build csh file ,Incoming parameter");
			buildCshWithVertical(filePath, shellVertival);
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
		logger.info("start Execute script");
		String script = FilePathUtil.combinePath(filePath);
		String[] cmd = new String[] { script };
		try {
			TaskHelper.execute(cmd);
			String pngPath = path + ".png";
			File pngFile = new File(pngPath);
			if (pngFile.exists() && pngFile.isFile()) {
				return pngPath;
			}
			return null;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}

	//通过摸版动态加载csh文件，并执行
	private void buildCshWithVertical(String filePath, ShellVertival shellVertival) throws IOException {
		String content = VelocityUtil.buildTemplate(Constants.CSV_TEMPLATE_PATH, "shellVertival", shellVertival);
		File modelRunFile = new File(filePath);
		FilePathUtil.writeLocalFile(modelRunFile, content);
		modelRunFile.setExecutable(true);
	}
//需要重写配置获取
	private String getSpecieIndex(VerticalParams verticalParams) {
		logger.info("Determine what pollutant data is loaded according to the conditions");
		String specie = verticalParams.getSpecie();
		String calcType = verticalParams.getCalcType();
		String timePoint = verticalParams.getTimePoint();
		String fileName = null;
		if (Constants.CALCTYPE_SHOW.equals(calcType)) {
			if (Constants.TIMEPOINT_H.equals(timePoint)) {
				switch (specie) {
				case "PM25":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_PM25();
				case "PM10":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_PM10();
				case "O3":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_O3();
				case "SO2":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_SO2();
				case "NO2":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_NO2();
				case "CO":
					return Constants.TIMEPOINT_HOURLY + specie + "=" + specieIndexList.getHourly_CO();
				}
			} else if (Constants.TIMEPOINT_D.equals(timePoint)) {
				switch (specie) {
				case "PM25":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_PM25();
				case "PM10":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_PM10();
				case "O3_8_max":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_O3_8_max();
				case "O3_1_max":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_O3_1_max();
				case "O3_avg":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_O3_avg();
				case "NO2":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_NO2();
				case "SO2":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_SO2();
				case "CO":
					return Constants.TIMEPOINT_DAILY + specie + "=" + specieIndexList.getDaily_CO();
				}
			}
		} else if (Constants.CALCTYPE_DIFF.equals(calcType)) {
			switch (specie) {
			case "PM25":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_PM25();
			case "PM10":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_PM10();
			case "O3":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_O3();
			case "SO2":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_SO2();
			case "NO2":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_NO2();
			case "CO":
				return Constants.CALCTYPE_DIFF + specie + "=" + specieIndexList.getDiff_CO();
			}

		} else if (Constants.CALCTYPE_RATIO.equals(calcType)) {
			return Constants.CALCTYPE_RATIO + "=" + specieIndexList.getRatio();
		}
		return fileName;
	}

	private void buildHeightInterpolation() {
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
	}

	private void buildEveryLayerValue(VerticalParams verticalParams) throws IOException, InvalidRangeException {
		int length = height.length;
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
	}

	//获取可变参数封装进variableList1，variableList2中
	private void getVariables(String concnFilePath, VerticalParams verticalParams) {
		String timePoint = verticalParams.getTimePoint();
		variableList1 = new ArrayList<Variable>();
		variableList2 = new ArrayList<Variable>();
		if (Constants.TIMEPOINT_A.equals(timePoint)) {
			List<String> list = verticalParams.getDates();
			for (int i = 0; i < list.size(); i++) {
				String day = list.get(i);
				buildVariables(day, concnFilePath, verticalParams);
			}
			try {
				attributes = Netcdf.getAttributes(concnFilePath + list.get(0));
			} catch (IOException e) {
				logger.error("VerticalService | variables() : getAttributes IOException");
				return;
			}
		} else if (Constants.TIMEPOINT_D.equals(timePoint) || Constants.TIMEPOINT_H.equals(timePoint)) {
			buildVariables(verticalParams.getDay(), concnFilePath, verticalParams);
			try {
				attributes = Netcdf.getAttributes(concnFilePath + verticalParams.getDay());
			} catch (IOException e) {
				logger.error("VerticalService | variables() : getAttributes IOException");
				return;
			}
		}
	}

	private void buildVariables(String day, String concnFilePath, VerticalParams verticalParams) {
		String specie = verticalParams.getSpecie();
		try {
			String p1 = concnFilePath + day;
			long openNcTimes = System.currentTimeMillis();
			NetcdfFile nc;
			try {
				nc = NetcdfFile.open(p1);
				logger.info("VerticalService | variables() :buildVariables  open nc file " + p1 + ", times = "
						+ (System.currentTimeMillis() - openNcTimes) + "ms");
			} catch (IOException e) {
				logger.error("VerticalService | variables() :buildVariables  open file " + p1 + " error");
				return;
			}
			if (nc == null)
				return;
			ncFile = nc;
			Variable variable1 = nc.findVariable(null, specie);
			variableList1.add(variable1);
			if (!Constants.CALCTYPE_SHOW.equals(verticalParams.getCalcType())) {
				String p2 = filePath2 + day;
				NetcdfFile nc2;
				try {
					nc2 = NetcdfFile.open(p2);
				} catch (IOException e) {
					logger.error("VerticalService | variables() :buildVariables  open file " + p2 + " error");
					return;
				}
				Variable variable2 = nc2.findVariable(null, specie);
				variableList2.add(variable2);
			}
		} catch (Exception e) {
			logger.error("VerticalService | variables() :buildVariables   error");
		}
	}

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
