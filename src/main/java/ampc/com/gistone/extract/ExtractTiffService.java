package ampc.com.gistone.extract;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;
import javax.media.jai.RasterFactory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.geometry.Envelope;
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
import ampc.com.gistone.extract.image.Colors;
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

	// public List<float[][]> variableList1; // diff or ratio
	// // scenario1/scenario2
	// public List<float[][]> variableList2;

	// public float[][] imagePixelData;

	public String buildTiff(ManageParams manageParams) throws IOException, FactoryException {
		ExtractRequestParams params = manageParams.getParams();
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
		// 2. get variables and build attributes
		getVariables(manageParams);
		// 3. calc data
		float[][] imagePixelData = buildImagePixelData(manageParams);
		// 4. save tiff file
		String[] tiffPath = buildIamgeFilePath(params, Constants.FILE_TYPE_TIFF);
		logger.info("tiff file path:" + tiffPath[0]);
		String outFile = writeGeotiff(tiffPath[0], imagePixelData, manageParams);
		return tiffPath[1];

	}
	
	public String writeGeotiff1(String fileName, float[][] imagePixelData, ManageParams manageParams)
			throws FactoryException, IOException {
		logger.info("ExtractTiffService | writing geotiff.");
		ExtractRequestParams params = manageParams.getParams();
		Map attributes = manageParams.getAttributes();
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
		
		String showType = params.getShowType();
		String calcType = params.getCalcType();
		String timePoint = params.getTimePoint();
		timePoint = timePoint.equals(Constants.TIMEPOINT_D) ? Constants.TIMEPOINT_DAILY
				: Constants.TIMEPOINT_HOURLY;
		String name = showType + "-" + calcType + "-" + timePoint;
		name = name.toLowerCase();
		Map<String, Map<String, List>> specieMap = resultPathUtil.getImageColorConfig().getSheetMap(name);
		Map<String, List> valueMap = specieMap.get(params.getSpecie());
		List<Float> valueList = valueMap.get(Constants.VALUE_LIST);
		List<Color> colorList = valueMap.get(Constants.COLOR_LIST);
		List<Integer> colorLongList = valueMap.get(Constants.COLOR_LONG_LIST);
		
		int row = imagePixelData.length;
		int col = imagePixelData[0].length;
		int res[][] = new int[row][col];
		int[] imgArr = new int[row * col];
		Color[][] colors = new Color[4][row * col];
		for(int i = 0; i < colors.length; i++) {
			for(int j = 0; j < colors[i].length; j++) {
				colors[i][j] = new Color(20, 20, 240, 255);
			}
		}
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				Color cc = Colors.getColor1(imagePixelData[r][c], valueList, colorList, colorLongList);
				res[r][c] = (int) imagePixelData[r][c];
//				colors[r][c] = cc;
				imgArr[r * col + c] = (int) imagePixelData[r][c];
			}
		}

		long startTimes = System.currentTimeMillis();
		try {
			CoordinateReferenceSystem sourceCRS = CRS.parseWKT(ProjectUtil.getWKT(attributes));
			ReferencedEnvelope refEnvelope = new ReferencedEnvelope(x1, x2, y1, y2, sourceCRS);
			
//			WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, col, row, 1, null);
//			raster.setDataElements(0, 0, col, row, imagePixelData);
			
//			BufferedImage image = new BufferedImage(col, row, BufferedImage.TYPE_INT_ARGB);
//			WritableRaster raster = image.getWritableTile(0, 0);
//			raster.setDataElements(0, 0, col, row, imagePixelData);
//			raster.setDataElements(0, 0, res);
			
//			WritableRaster raster = Raster.createCompatibleWritableRaster();
			ColorModel model = new DirectColorModel(32, 0x00ff0000,0x0000ff00,0x000000ff, 0xff000000);
			DataBufferInt buffer = new DataBufferInt(imgArr, col * row); 
			WritableRaster raster = WritableRaster.createPackedRaster(buffer, col, row, col, 
                    new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 }, null);
//			byte b000 = (byte) 0;
//			byte b255 = (byte) 255;
//			byte[] reds = new byte[]{b255, b255, b000, b000, b255, b255, b000, b000};
//			byte[] greens = new byte[]{b255, b255, b255, b255, b000, b000, b000, b000};
//			byte[] blues = new byte[]{b255, b000, b255, b000, b255, b000, b255, b000};
//
//			IndexColorModel colorModel = new IndexColorModel(3, 8, reds, greens, blues);
//			WritableRaster raster = colorModel.createCompatibleWritableRaster(col, row);
			logger.info("band" +  raster.getNumBands()) ;
			for (int r = 0; r < row; r++) {
				for (int c = 0; c < col; c++) {
					int[] colorComponents = new int[4];
					colorComponents[0] = 20;
					colorComponents[1] = 20;
					colorComponents[2] = 240;
					colorComponents[3] = 0;
					// Set the color of the pixel
					raster.setPixel(c, r, colorComponents);
//					raster.setSample(c, r, 0, 20);
//					raster.setSample(c, r, 1, 20);
//					raster.setSample(c, r, 2, 240);
//					raster.setSample(c, r, 3, 30);
				}
			}
			GridCoverage2D coverage1 = new GridCoverageFactory().create("OTPAnalyst", raster, refEnvelope);
			
		
		
//		GridCoverage2D coverage = new GridCoverageFactory().create("OTPAnalyst", imagePixelData, refEnvelope);
		
			GeoTiffWriteParams wp = new GeoTiffWriteParams();
			wp.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
			wp.setCompressionType("LZW");
			long s = System.currentTimeMillis();
			ParameterValueGroup parameter = new GeoTiffFormat().getWriteParameters();
			logger.info((System.currentTimeMillis() - s) + "ms");
			parameter.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
			GeoTiffWriter writer = new GeoTiffWriter(new File(fileName));
			writer.write(coverage1, (GeneralParameterValue[]) parameter.values().toArray(new GeneralParameterValue[1]));
			logger.info("ExtractTiffService | write file " + (System.currentTimeMillis() - startTimes) + "ms");
			logger.info("ExtractTiffService | done writing geotiff.");
			return fileName;
		} catch (Exception e) {
			logger.error("ExtractTiffService | exception while writing geotiff.", e);
			return null;
		}
	}

	public String writeGeotiff(String fileName, float[][] imagePixelData, ManageParams manageParams)
			throws FactoryException, IOException {
		logger.info("ExtractTiffService | writing geotiff.");
		Map attributes = manageParams.getAttributes();
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
			logger.info((System.currentTimeMillis() - s) + "ms");
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
	public float[][] buildImagePixelData(ManageParams manageParams) throws IOException {
		ExtractRequestParams params = manageParams.getParams();
		Map attributes = manageParams.getAttributes();
		List<float[][]> floatList1 = manageParams.getFloatList1();
		List<float[][]> floatList2 = manageParams.getFloatList2();
		String calcType = params.getCalcType();
		int rows = Integer.valueOf(String.valueOf(attributes.get("NROWS")));
		int cols = Integer.valueOf(String.valueOf(attributes.get("NCOLS")));
		float[][] imagePixelData = new float[rows][cols];

		if (Constants.CALCTYPE_SHOW.equals(calcType)) {
			int size = floatList1.size();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					float value = 0;
					for (float[][] variable : floatList1) {
						value += variable[r][c] / size;
					}
					imagePixelData[r][c] = value;
				}
			}
		} else if (Constants.CALCTYPE_DIFF.equals(calcType) || Constants.CALCTYPE_RATIO.equals(calcType)) {
			int size1 = floatList1.size();
			int size2 = floatList2.size();

			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					float value = 0;
					float value1 = 0;
					float value2 = 0;
					for (float[][] variable : floatList1) {
						value1 += variable[r][c] / size1;
					}
					for (float[][] variable : floatList2) {
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
		return imagePixelData;
	}

	public void buildVariables(ManageParams manageParams, String date) {
		ExtractRequestParams params = manageParams.getParams();
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
			manageParams.setNcFile1(nc1);
			manageParams.setFilePath1(p1);

			float[][] variable1 = ((float[][][][]) nc1.findVariable(null, specie).read()
					.copyToNDJavaArray())[hour][layer];
			variable1 = reversalArray(variable1);
			manageParams.getFloatList1().add(variable1);

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
				float[][] variable2 = ((float[][][][]) nc2.findVariable(null, specie).read()
						.copyToNDJavaArray())[hour][layer];
				variable2 = reversalArray(variable2);
				manageParams.getFloatList2().add(variable2);
			}
		} catch (Exception e) {
			logger.error("ExtractTiffService | buildVariables() error", e);
		}
	}

}
