package ampc.com.gistone.extract;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;

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

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
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

	public List<Variable> variableList1; // diff or ratio
											// scenario1/scenario2
	public List<Variable> variableList2;

	private NumberFormat nf;

	public synchronized String buildPng(ExtractRequestParams extractRequestParams)
			throws IOException, TransformException, FactoryException, InvalidRangeException {

		extractConfig = resultPathUtil.getExtractConfig();
		params = extractRequestParams;

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

		variableList1 = new ArrayList<Variable>();
		variableList2 = new ArrayList<Variable>();

		getVariables();

		projection = buildProject(params);
		if (projection == null)
			return null;

		nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(10);

		float[][] res = buildPngData();

		res = reversalArray(res);

		return drawPngPicture(res);
	}

	public void buildVariables(String date) {
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
			ncFile1 = nc1;
			filePath1 = p1;
			Variable variable1 = nc1.findVariable(null, specie);
			variableList1.add(variable1);

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
				filePath2 = p2;
				Variable variable2 = nc2.findVariable(null, specie);
				variableList2.add(variable2);
			}
		} catch (Exception e) {
			logger.error("ExtractPngService | buildVariables() error");
		}
	}

	private float[][] buildPngData() throws IOException, InvalidRangeException {
		List<PointBean> pointBeanList = new ArrayList<>();
		int cols = params.getCols();
		int rows = params.getRows();
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

				pb = getValue(pb);
				float value = Float.valueOf(pb.getValue());
				res[i][j] = value;
				// if (value == Constants.OVERBORDER) {
				// res[i][j] = value;
				// } else {
				// String specie = params.getSpecie();
				// float value =
				// Float.parseFloat(String.valueOf(resMap.get(specie)));
				// res[i][j] = (float) value;
				// }
			}
			x = params.getXmin();
			y += ycellsize;
		}

		return res;
	}

	public PointBean getValue(PointBean pb) throws IOException, InvalidRangeException {
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

			} else if ("diff".equals(params.getCalcType()) || "ratio".equals(params.getCalcType())) {
				double value1 = 0;
				double value2 = 0;

				for (Variable variable : variableList1) {
					double vv = interpolation.interpolation(variable, p);
					if (vv == Constants.OVERBORDER) {
						value = vv;
						break;
					}
					value1 += vv / variableList1.size();
				}

				for (Variable variable : variableList2) {
					double vv = interpolation.interpolation(variable, p);
					if (vv == Constants.OVERBORDER) {
						value = vv;
						break;
					}
					value2 += vv / variableList2.size();
				}

				if ("diff".equals(params.getCalcType())) {
					value = value2 - value1;
				} else {
					if (value1 == 0) {
						value1 = Constants.MINIMUM;
					}
					value = (value2 - value1) / value1;
				}
			}
			pb.setValue(String.valueOf(value));
			return pb;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ExtractPngService | getValue error");
			return null;
		}
	}

	private String drawPngPicture(float[][] temp) {
		MapContent map = new MapContent();

		double ymin = params.getYmin();
		double ymax = params.getYmax();
		double xmin = params.getXmin();
		double xmax = params.getXmax();

		CoordinateReferenceSystem sourceCRS;
		try {
			sourceCRS = CRS.decode("EPSG:4326");

			ReferencedEnvelope sourceee = new ReferencedEnvelope(Projection.transToLat(params.getXmin()),
					Projection.transToLat(params.getXmax()), Projection.transToLon(params.getYmin()),
					Projection.transToLon(params.getYmax()), sourceCRS); // TODO
																			// 需要更新坐标点，
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
			Layer rasterLayer = new GridCoverageLayer(gct, rasterStyle);

			map.addLayer(rasterLayer);

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);

			String imagePath = buildIamgeFilePath();
			boolean b = Images.buildImage(map, params.getWidth(), params.getHeight(), imagePath);
			if (b)
				return imagePath;
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

	public String buildIamgeFilePath() {
		Date date = new Date();
		String imageFilePath = extractConfig.getImageFilePath(); // /$userid/$domainid/$missionid/$domain/$showType/$calcType/$currDate/
		imageFilePath = imageFilePath.replace("$userid", String.valueOf(params.getUserId()))
				.replace("$domainid", String.valueOf(params.getDomainId()))
				.replace("$missionid", String.valueOf(params.getMissionId()))
				.replace("$domain", String.valueOf(params.getDomain())).replace("$showType", params.getShowType())
				.replace("$calcType", params.getCalcType()).replace("$currDate", DateUtil.DATEtoString(date, DateUtil.DATE_FORMAT));
		String imageFileName = extractConfig.getImageFileName(); // $timePoint-$day-$hour-$layer-$specie-$scenario-$random.png
		String day = "";
		String scenario = "";
		if (Constants.TIMEPOINT_A.equals(params.getTimePoint())) {
			List<String> list = params.getDates();
			day = list.get(0) + list.get(list.size() - 1);
		} else {
			day = params.getDay();
		}
		if (Constants.CALCTYPE_SHOW.equals(params.getCalcType())) {
			scenario = String.valueOf(params.getScenarioId1());
		} else {
			scenario = String.valueOf(params.getScenarioId1().toString()) + "-"
					+ String.valueOf(params.getScenarioId2());
		}
		File file = new File(imageFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		imageFileName = imageFileName.replace("$timePoint", params.getTimePoint()).replace("$day", day)
				.replace("$hour", String.valueOf(params.getHour()))
				.replace("$layer", String.valueOf(params.getLayer() - 1)).replace("$specie", params.getSpecie())
				.replace("$scenario", scenario).replace("$random", String.valueOf(new Date().getTime()));
		return imageFilePath + "/" + imageFileName;

	}

}
