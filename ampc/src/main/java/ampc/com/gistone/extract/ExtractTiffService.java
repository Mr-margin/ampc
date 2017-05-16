package ampc.com.gistone.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.PreproUtil;
import ampc.com.gistone.util.DateUtil;
import ucar.nc2.NetcdfFile;

@Service
public class ExtractTiffService extends ExtractService {

	private final static Logger logger = LoggerFactory.getLogger(ExtractTiffService.class);
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private PreproUtil preproUtil;

	public List<float[][]> variableList1; // diff or ratio
											// scenario1/scenario2
	public List<float[][]> variableList2;

	public static float[][] imagePixelData;

	public String buildTiff(ExtractRequestParams extractRequestParams) throws IOException, FactoryException {
		params = extractRequestParams;
		extractConfig = resultPathUtil.getExtractConfig();

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
		variableList1 = new ArrayList<float[][]>();
		variableList2 = new ArrayList<float[][]>();
		// 2. get variables and build attributes
		getVariables();
		// 3. calc data
		buildImagePixelData();
		// 4. save tiff file
		String tiffFilePath = extractConfig.getTiffFilePath(); // /$userid/$domainid/$missionid/$domain/$showType/$calcType/
		tiffFilePath = tiffFilePath.replace("$userid", String.valueOf(params.getUserId()))
				.replace("$domainid", String.valueOf(params.getDomainId()))
				.replace("$missionid", String.valueOf(params.getMissionId()))
				.replace("$domain", String.valueOf(params.getDomain())).replace("$showType", params.getShowType())
				.replace("$calcType", params.getCalcType());
		String tiffFileName = extractConfig.getTiffFileName(); // $timePoint-$day-$hour-$layer-$specie-$scenario.tiff
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
		File file = new File(tiffFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		tiffFileName = tiffFileName.replace("$timePoint", params.getTimePoint()).replace("$day", day)
				.replace("$hour", String.valueOf(params.getHour()))
				.replace("$layer", String.valueOf(params.getLayer() - 1)).replace("$specie", params.getSpecie())
				.replace("$scenario", scenario);
		String outFile = writeGeotiff(tiffFilePath + "/" + tiffFileName);
		return outFile;

	}

	public String writeGeotiff(String fileName) throws FactoryException, IOException {
		logger.info("ExtractTiffService | writing geotiff.");

		long startTime = System.currentTimeMillis();
		double x1 = Double.valueOf(String.valueOf(attributes.get("XORIG")));
		double y1 = Double.valueOf(String.valueOf(attributes.get("YORIG")));
		int rows = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
		int cols = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
		double xcell = Double.valueOf(String.valueOf(attributes.get("XCELL")));
		double ycell = Double.valueOf(String.valueOf(attributes.get("YCELL")));
		double x2 = x1 + xcell * cols;
		double y2 = y1 + ycell * rows;
		logger.info("ExtractTiffService | get params " + (System.currentTimeMillis() - startTime) + "ms");

		long startTimes = System.currentTimeMillis();
		CoordinateReferenceSystem sourceCRS = CRS.parseWKT(ProjectUtil.getWKT(attributes));
		ReferencedEnvelope refEnvelope = new ReferencedEnvelope(x1, x2, y1, y2, sourceCRS);

		GridCoverage2D coverage = new GridCoverageFactory().create("OTPAnalyst", imagePixelData, refEnvelope);
		try {
			GeoTiffWriteParams wp = new GeoTiffWriteParams();
			wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
			wp.setCompressionType("LZW");
			long s = System.currentTimeMillis();
			ParameterValueGroup params = new GeoTiffFormat().getWriteParameters();
			System.out.println((System.currentTimeMillis() - s) + "ms");
			params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
			GeoTiffWriter writer = new GeoTiffWriter(new File(fileName));
			writer.write(coverage, (GeneralParameterValue[]) params.values().toArray(new GeneralParameterValue[1]));
			logger.info("ExtractTiffService | write file " + (System.currentTimeMillis() - startTimes) + "ms");
			logger.info("ExtractTiffService | done writing geotiff.");
			return fileName;
		} catch (Exception e) {
			logger.error("ExtractTiffService | exception while writing geotiff.", e);
			return null;
		}
	}

	// build tiff file need data
	public void buildImagePixelData() throws IOException {
		String calcType = params.getCalcType();
		int rows = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
		int cols = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
		imagePixelData = new float[rows][cols];

		if (Constants.CALCTYPE_SHOW.equals(calcType)) {
			int size = variableList1.size();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					float value = 0;
					for (float[][] variable : variableList1) {
						value += variable[r][c] / size;
					}
					imagePixelData[r][c] = value;
				}
			}
		} else if (Constants.CALCTYPE_DIFF.equals(calcType) || Constants.CALCTYPE_RATIO.equals(calcType)) {
			int size1 = variableList1.size();
			int size2 = variableList2.size();

			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					float value = 0;
					float value1 = 0;
					float value2 = 0;
					for (float[][] variable : variableList1) {
						value1 += variable[r][c] / size1;
					}
					for (float[][] variable : variableList2) {
						value2 += variable[r][c] / size2;
					}
					if (Constants.CALCTYPE_DIFF.equals(params.getCalcType())) {
						value = value2 - value1;
					} else {
						if (value1 == 0) {
							value1 = (float) Constants.MINIMUM;
						}
						value = (value2 - value1) / value1;
					}
					imagePixelData[r][c] = value;
				}
			}
		}

	}

	// public void getVariables() {
	// String timePoint = params.getTimePoint();
	//
	// if (Constants.TIMEPOINT_A.equals(timePoint)) {
	// List<String> list = params.getDates();
	// for (int i = 0; i < list.size(); i++) {
	// String day = list.get(i);
	// buildVariables(day);
	// }
	// try {
	// attributes = Netcdf.getAttributes(filePath1 + list.get(0));
	// } catch (IOException e) {
	// logger.error("ExtractTiffService | getVariables() : getAttributes
	// IOException");
	// return;
	// }
	// } else if (Constants.TIMEPOINT_D.equals(timePoint) ||
	// Constants.TIMEPOINT_H.equals(timePoint)) {
	// buildVariables(params.getDay());
	//
	// try {
	// attributes = Netcdf.getAttributes(filePath1 + params.getDay());
	// } catch (IOException e) {
	// logger.error("ExtractTiffService | getVariables() : getAttributes
	// IOException");
	// return;
	// }
	// }
	// }

	public void buildVariables(String date) {
		String specie = params.getSpecie();
		int hour = params.getHour();
		int layer = params.getLayer() - 1;

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

			float[][] variable1 = ((float[][][][]) nc1.findVariable(null, specie).read()
					.copyToNDJavaArray())[hour][layer];
			variable1 = reversalArray(variable1);
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
				float[][] variable2 = ((float[][][][]) nc2.findVariable(null, specie).read()
						.copyToNDJavaArray())[hour][layer];
				variable2 = reversalArray(variable2);
				variableList2.add(variable2);
			}
		} catch (Exception e) {
			logger.error("ExtractTiffService | buildVariables() error");
		}
	}

}
