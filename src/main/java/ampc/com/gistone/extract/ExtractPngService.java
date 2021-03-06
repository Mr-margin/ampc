package ampc.com.gistone.extract;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.processing.Operations;
import org.geotools.coverage.processing.operation.Resample;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.extract.image.Colors;
import ampc.com.gistone.extract.image.Images;
import ampc.com.gistone.extract.image.Styles;
import ampc.com.gistone.preprocess.concn.PreproUtil;
import ampc.com.gistone.util.DateUtil;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

@Service
public class ExtractPngService extends ExtractService {

	private final static Logger logger = LoggerFactory.getLogger(ExtractPngService.class);
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private PreproUtil preproUtil;

	// public List<Variable> variableList1; // diff or ratio
	// // scenario1/scenario2
	// public List<Variable> variableList2;
	//
	// private NumberFormat nf;
	// private List<PointBean> pointBeanList;

	public Map buildPng(ManageParams manageParams)
			throws IOException, TransformException, FactoryException, InvalidRangeException {
		long startTimes = System.currentTimeMillis();
		extractConfig = resultPathUtil.getExtractConfig();
		ExtractRequestParams params = manageParams.getParams();

		Long scenarioId1 = params.getScenarioId1();
		TScenarinoDetail tScenarinoDetail1 = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId1);
		Map<String, String[]> dataTypeMap1 = preproUtil.buildFnlAndGfsDate(tScenarinoDetail1);
		params.setDateTypeMap1(dataTypeMap1);
		if (!Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
			Long scenarioId2 = params.getScenarioId2();
			TScenarinoDetail tScenarinoDetail2 = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId2);
			Map<String, String[]> dataTypeMap2 = preproUtil.buildFnlAndGfsDate(tScenarinoDetail2);
			params.setDateTypeMap2(dataTypeMap2);
		}
		getVariables(manageParams);

		Projection projection = buildProject(manageParams);
		if (projection == null) {
			return null;
		} else {
			manageParams.setProjection(projection);
		}

		float[][] res = buildPngData(manageParams);
		String imagePath = buildPngImage(res, params);
		// try {
		// exportExcel(res, imagePath);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		Map data = new HashMap();
		// data.put("imagePath", drawPngPicture(res, params));
		data.put("imagePath", imagePath);
		ObjectMapper mapper = new ObjectMapper();
		// data.put("data", pointBeanList);
		logger.info("buildPng total times : " + (System.currentTimeMillis() - startTimes) + "ms");
		return data;
	}

	private String buildPngImage(float[][] res, ExtractRequestParams params) {
		logger.info("start buildPngImage...");
		long startTimes = System.currentTimeMillis();
		String[] imagePath = buildIamgeFilePath(params, Constants.FILE_TYPE_IMAGE);
		logger.info("pngImage path:" + imagePath[0]);
		try {
			String showType = params.getShowType();
			String calcType = params.getCalcType();
			String timePoint = params.getTimePoint();
			Map<String, Map<String, List>> specieMap = resultPathUtil.getSheetName(showType, calcType, timePoint, params.getSpecie());
			Map<String, List> valueMap = specieMap.get(params.getSpecie());
			List<Float> valueList = valueMap.get(Constants.VALUE_LIST);
			List<Color> colorList = valueMap.get(Constants.COLOR_LIST);
			List<Integer> colorLongList = valueMap.get(Constants.COLOR_LONG_LIST);
			int row = res.length;
			int col = res[0].length;
			int[] imgArr = new int[row * col];
			for (int r = 0; r < row; r++) {
				for (int c = 0; c < col; c++) {
					int cc = Colors.getColor(res[r][c], valueList, colorList, colorLongList);
					imgArr[r * col + c] = cc;
				}
			}
			BufferedImage bi = Images.buildGraphics2D(imgArr, res[0].length, res.length);
			ImageIO.write(bi, "png", new File(imagePath[0]));
		} catch (Exception e) {
			logger.error("ExtractPngService | buildPngImage error", e);
		}
		logger.info("buildPngImage times : " + (System.currentTimeMillis() - startTimes) + "ms");
		return imagePath[1];
	}

	public void exportExcel(float[][] pointBeanArray, String pathOut)
			throws IOException, InvalidRangeException, TransformException, FactoryException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet oneOfSheet = workbook.createSheet("data");

		for (int i = 0; i < pointBeanArray.length; i++) {
			XSSFRow hssfRow = oneOfSheet.createRow(i);
			for (int j = 0; j < pointBeanArray[i].length; j++) {
				XSSFCell cell = hssfRow.createCell(j);
				float pb = pointBeanArray[i][j];
				cell.setCellValue(pb);
			}
		}
		File file = new File(pathOut);
		if (!file.exists()) {
			file.mkdirs();
		}
		String excel = file.getParent() + "/extract-data.xls";
		FileOutputStream out = new FileOutputStream(excel);
		workbook.write(out);
		out.close();

	}

	public void buildVariables(ManageParams manageParams, String date) {
		ExtractRequestParams params = manageParams.getParams();
		String specie = params.getSpecie();
		try {
			String base1 = resultPathUtil.getResultFilePath(date, params.getShowType(), params.getTimePoint(),
					params.getUserId(), params.getDomainId(), params.getMissionId(), params.getScenarioId1(),
					params.getDomain(), params.getDateTypeMap1());
			String day1 = DateUtil.strConvertToStr(date);
			String p1 = base1.replace("$Day", day1);
			long openNcTimes = System.currentTimeMillis();
			NetcdfFile nc1;
			try {
				nc1 = NetcdfFile.open(p1);
				logger.info("open nc file " + p1 + ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");
			} catch (IOException e) {
				logger.error("open file " + p1 + " error");
				return;
			}
			if (nc1 == null)
				return;
			manageParams.setNcFile1(nc1);
			manageParams.setFilePath1(p1);
			Variable variable1 = nc1.findVariable(null, specie);
			manageParams.getVariableList1().add(variable1);

			if (!Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
				String base2 = resultPathUtil.getResultFilePath(date, params.getShowType(), params.getTimePoint(),
						params.getUserId(), params.getDomainId(), params.getMissionId(), params.getScenarioId2(),
						params.getDomain(), params.getDateTypeMap2());
				String day2 = DateUtil.strConvertToStr(date);
				String p2 = base2.replace("$Day", day2);
				NetcdfFile nc2;
				try {
					nc2 = NetcdfFile.open(p2);
				} catch (IOException e) {
					logger.error("open file " + p2 + " error");
					return;
				}
				manageParams.setFilePath2(p2);
				Variable variable2 = nc2.findVariable(null, specie);
				manageParams.getVariableList2().add(variable2);
			}
		} catch (Exception e) {
			logger.error("ExtractPngService | buildVariables() error");
		}
	}

	private float[][] buildPngData(ManageParams manageParams) throws IOException, InvalidRangeException {
		long startTimes = System.currentTimeMillis();
		List<PointBean> pointBeanList = new ArrayList<>();
		manageParams.setPointBeanList(pointBeanList);
		ExtractRequestParams params = manageParams.getParams();
		Projection projection = manageParams.getProjection();
		List<Variable> variableList1 = manageParams.getVariableList1();
		List<Variable> variableList2 = manageParams.getVariableList2();
		Interpolation interpolation = manageParams.getInterpolation();
		int cols = params.getCols();
		int rows = params.getRows();
		int width = params.getWidth();
		int height = params.getHeight();
		rows = (cols * height) / width;
		double xcellsize = (params.getXmax() - params.getXmin()) / cols;
		double ycellsize = (params.getYmax() - params.getYmin()) / rows;
		double x = params.getXmin();
		double y = params.getYmin();

		float[][] res = new float[rows + 1][cols + 1];

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

				pb = getValue(pb, params, projection, variableList1, variableList2, interpolation);
				BigDecimal b = pb.getV();
				float value = b.floatValue();
				res[i][j] = value;
				if (value != Constants.OVERBORDER) {
					PointBean point = new PointBean();
					point.setX(merc_x);
					point.setY(merc_y);
					point.setValue(String.valueOf(value));
					point.setV(b);
					pointBeanList.add(point);
				}
			}
			x = params.getXmin();
			y += ycellsize;
		}
		res = reversalArray(res);
		logger.info("buildPngData times: " + (System.currentTimeMillis() - startTimes) + "ms");
		return res;
	}

	public PointBean getValue(PointBean pb, ExtractRequestParams params, Projection projection,
			List<Variable> variableList1, List<Variable> variableList2, Interpolation interpolation)
					throws IOException, InvalidRangeException {
		try {
			Point2D p = projection.transform(pb.getLon(), pb.getLat());
			pb.setXlcc(p.getX());
			pb.setYlcc(p.getY());
			Map<String, Double> resMap = new HashMap<String, Double>();
			double value = 0;

			if (Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {

				for (Variable variable : variableList1) {
					double vv = interpolation.interpolation(variable, p);
					if (vv == Constants.OVERBORDER) {
						value = vv;
						break;
					}
					value += vv / variableList1.size();
				}

			} else if (Constants.CALCTYPE_DIFF.equals(params.getCalcType())
					|| Constants.CALCTYPE_RATIO.equals(params.getCalcType())) {
				double value1 = 0;
				double value2 = 0;

				for (Variable variable : variableList1) {
					double vv = interpolation.interpolation(variable, p);
					if (vv == Constants.OVERBORDER) {
						value = vv;
						pb.setValue(String.valueOf(value));
						pb.setV(new BigDecimal(value));
						return pb;
					}
					value1 += vv / variableList1.size();
				}

				for (Variable variable : variableList2) {
					double vv = interpolation.interpolation(variable, p);
					if (vv == Constants.OVERBORDER) {
						value = vv;
						pb.setValue(String.valueOf(value));
						pb.setV(new BigDecimal(value));
						return pb;
					}
					value2 += vv / variableList2.size();
				}

				if (Constants.CALCTYPE_DIFF.equals(params.getCalcType())) {
					value = value2 - value1;
				} else {
					if (value1 == 0) {
						value1 = Constants.MINIMUM;
					}
					value = (value2 - value1) / value1;
				}
			}
			pb.setValue(String.valueOf(value));
			pb.setV(new BigDecimal(value));
			return pb;
		} catch (Exception e) {
			logger.error("ExtractPngService | getValue error", e);
			return null;
		}
	}

	// 暂时没用到
	private String drawPngPicture(float[][] temp, ExtractRequestParams params) {
		MapContent map = new MapContent();

		double ymin = params.getYmin();
		double ymax = params.getYmax();
		double xmin = params.getXmin();
		double xmax = params.getXmax();

		try {

			// double x1 =
			// Double.valueOf(String.valueOf(attributes.get("XORIG")));
			// double y1 =
			// Double.valueOf(String.valueOf(attributes.get("YORIG")));
			// int rows =
			// Integer.valueOf(String.valueOf(attributes.get("NROWS")));
			// int cols =
			// Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
			// double xcell =
			// Double.valueOf(String.valueOf(attributes.get("XCELL")));
			// double ycell =
			// Double.valueOf(String.valueOf(attributes.get("YCELL")));
			// double x2 = x1 + xcell * cols;
			// double y2 = y1 + ycell * rows;
			// CoordinateReferenceSystem sourceCRS =
			// CRS.parseWKT(ProjectUtil.getWKT(attributes));
			// ReferencedEnvelope refEnvelope = new ReferencedEnvelope(x1, x2,
			// y1, y2, sourceCRS);

			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3857");
			ReferencedEnvelope sourceee = new ReferencedEnvelope(ymin, ymax, xmin, xmax, sourceCRS);

			GridCoverageFactory factory = new GridCoverageFactory();
			GridCoverage gcs = factory.create("source", temp, sourceee);

			final RenderedOp scaledImage = ScaleDescriptor.create(gcs.getRenderedImage(), 2f, 2f, 0f, 0f,
					javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_BICUBIC_2), null);

			BufferedImage bi = scaledImage.getAsBufferedImage();

			GridCoverage2D newcov = (new GridCoverageFactory()).create("new", bi, sourceee);

			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
			ReferencedEnvelope targetee = sourceee.transform(targetCRS, true);

			MapViewport mapViewport = new MapViewport(targetee);
			map.setViewport(mapViewport);

			double defaultValue[] = { 0 };

			GridCoverage2D gct = (GridCoverage2D) Operations.DEFAULT.resample(newcov, targetCRS,
					Resample.computeGridGeometry(newcov, targetee),
					javax.media.jai.Interpolation.getInstance(javax.media.jai.Interpolation.INTERP_BICUBIC_2),
					defaultValue);

			Style rasterStyle = Styles.createXMLStyle("WhiteBlueGreenYellowRed", 200, "PNG");
			// Style rasterStyle = Styles.createXMLStyle(params.getSpecie());
			Layer rasterLayer = new GridCoverageLayer(gct, rasterStyle);

			map.addLayer(rasterLayer);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);

			String[] imagePath = buildIamgeFilePath(params, Constants.FILE_TYPE_IMAGE);
			boolean b = Images.buildImage(map, params.getWidth(), params.getHeight(), imagePath[0]);
			if (b)
				return imagePath[1];
			return null;
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return Images.buildImage(map, width);
		return null;
	}

	// public String[] buildIamgeFilePath(ExtractRequestParams params) {
	// String[] path = new String[2];
	// Date date = new Date();
	// String rootPath = System.getProperty("user.dir");
	// String imageFilePath = extractConfig.getImageFilePath(); //
	// /$userid/$domainid/$missionid/$domain/$showType/$calcType/$currDate/
	// imageFilePath = imageFilePath.replace("$userid",
	// String.valueOf(params.getUserId()))
	// .replace("$domainid", String.valueOf(params.getDomainId()))
	// .replace("$missionid", String.valueOf(params.getMissionId()))
	// .replace("$domain",
	// String.valueOf(params.getDomain())).replace("$showType",
	// params.getShowType())
	// .replace("$calcType", params.getCalcType())
	// .replace("$currDate", DateUtil.DATEtoString(date, DateUtil.DATE_FORMAT));
	// String imageFileName = extractConfig.getImageFileName(); //
	// $timePoint-$day-$hour-$layer-$specie-$scenario-$random.png
	// String day = "";
	// String scenario = "";
	// if (Constants.TIMEPOINT_A.equals(params.getTimePoint())) {
	// List<String> list = params.getDates();
	// day = list.get(0) + list.get(list.size() - 1);
	// } else {
	// day = params.getDay();
	// }
	// if (Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
	// scenario = String.valueOf(params.getScenarioId1());
	// } else {
	// scenario = String.valueOf(params.getScenarioId1().toString()) + "-"
	// + String.valueOf(params.getScenarioId2());
	// }
	//
	// imageFileName = imageFileName.replace("$timePoint",
	// params.getTimePoint()).replace("$day", day)
	// .replace("$hour", String.valueOf(params.getHour()))
	// .replace("$layer", String.valueOf(params.getLayer() -
	// 1)).replace("$specie", params.getSpecie())
	// .replace("$scenario", scenario).replace("$random", String.valueOf(new
	// Date().getTime()));
	// String contentPath = rootPath + "/" + imageFilePath + "/";
	// File file = new File(contentPath);
	// if (!file.exists()) {
	// file.mkdirs();
	// }
	// path[0] = contentPath + imageFileName; // 绝对路径，全路径
	// path[1] = imageFilePath + imageFileName; // 需要返给前端的路径
	// logger.info("image path : " + path[0]);
	// return path;
	//
	// }

}
