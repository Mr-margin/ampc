package ampc.com.gistone.extract;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

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
	
	private final static Logger logger = LoggerFactory.getLogger(ExtractDataService.class);
	private final static String SHOW_TYPE_CONCN = "concn";
	private final static String SHOW_TYPE_EMIS = "emis";
	private final static String TIMEPOINT_A = "a";
	private final static String TIMEPOINT_D = "d";
	private final static String TIMEPOINT_H = "h";
	private final static String CALCTYPE_SHOW = "show";
	private final static double MINIMUM = 0.0000000001;
	private ExtractConfig extractConfig;
	private PointBean[][] pointBeanArray;
	private NetcdfFile ncFile1;
	private Variable variable1;
	private NetcdfFile ncFile2;
	private Variable variable2;
	private List<Variable> variableList1;
	private List<Variable> variableList2;
	private Map attributes;
	private Projection projection;
	private NumberFormat nf;
	private List<PointBean> pointBeanList;
	private Interpolation interpolation;
	private List<Map<String, Object>> res;
	
	private StringBuffer sbcsv_latlon = new StringBuffer();
	private StringBuffer sbcsv_lcc = new StringBuffer();
	
	private String pathOut;
	
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
			extractConfig.setWindFilePath((String) pro.get("windFilePath"));
			extractConfig.setConcnDailyPrefix((String) pro.get("concnDailyPrefix"));
			extractConfig.setConcnHourlyPrefix((String) pro.get("concnHourlyPrefix"));
			extractConfig.setMeteorDailyPrefix((String) pro.get("meteorDailyPrefix"));
			extractConfig.setMeteorHourlyPrefix((String) pro.get("meteorHourlyPrefix"));
			extractConfig.setMeteorDailyWindPrefix((String) pro.get("meteorDailyWindPrefix"));
			extractConfig.setMeteorHourlyWindPrefix((String) pro.get("meteorHourlyWindPrefix"));
			extractConfig.setEmisDailyPrefix((String) pro.get("emisDailyPrefix"));
			extractConfig.setEmisHourlyPrefix((String) pro.get("emisHourlyPrefix"));
		} catch (FileNotFoundException e) {
			logger.error(config + " file does not exits!", e);
		} catch (IOException e) {
			logger.error("load " + config + " file error!", e);
		}
		
		try {
			if(ins != null) ins.close();
		} catch (IOException e) {
			logger.error("close " + config + " file error!", e);
		}
	}

	public List<Map<String, Object>> buildData(ExtractRequestParams params) throws IOException, TransformException, FactoryException, InvalidRangeException {
		res = new ArrayList<Map<String,Object>>();
		getNcFile(params);
		
		projection = buildProject(params);
	    if(projection == null) return null;
	    
	    nf = NumberFormat.getInstance();
	    nf.setGroupingUsed(false);
	    nf.setMaximumFractionDigits(10);
		
		pointBeanList = buildPointList(params, res);
		
		ObjectMapper mapper = new ObjectMapper();
		if(SHOW_TYPE_CONCN.equals(params.getShowType())) {
			pathOut = extractConfig.getConcnFilePath();
	    } else if(SHOW_TYPE_EMIS.equals(params.getShowType())) {
	    	pathOut = extractConfig.getEmisFilePath();
	    } else {
	    	pathOut = extractConfig.getWindFilePath();
	    }
		pathOut = pathOut.replace("$userid", String.valueOf(params.getUserId())).replace("$domainid", String.valueOf(params.getDomainId()))
			    .replace("$missionid", String.valueOf(params.getMissionId())).replace("$scenarioid", String.valueOf(params.getScenarioId1()))
			      .replace("$domain", String.valueOf(params.getDomain()));
		pathOut = pathOut + "/" + params.getCalcType() + "-" + params.getTimePoint();
		saveFile(pathOut, "/data.json", mapper.writeValueAsString(res));
	    saveFile(pathOut, "/extract-data-lonlat.csv", sbcsv_latlon.toString());
	    saveFile(pathOut, "/extract-data-lcc.csv", sbcsv_lcc.toString());
		
		exportExcel(pointBeanArray, pathOut);
		
		return res;
		
	}

	private void getNcFile(ExtractRequestParams params) {
		String filePath1 = "";
	    if(SHOW_TYPE_CONCN.equals(params.getShowType())) {
	      filePath1 = extractConfig.getConcnFilePath() + "/" + (TIMEPOINT_H.equals(params.getTimePoint()) ? extractConfig.getConcnHourlyPrefix() : extractConfig.getConcnDailyPrefix());
	    } else if(SHOW_TYPE_EMIS.equals(params.getShowType())) {
	      filePath1 = extractConfig.getEmisFilePath() + "/" + (TIMEPOINT_D.equals(params.getTimePoint()) ? extractConfig.getEmisDailyPrefix() : extractConfig.getEmisHourlyPrefix());
	    } else {
	      filePath1 = extractConfig.getWindFilePath() + "/" + (TIMEPOINT_D.equals(params.getTimePoint()) ? extractConfig.getMeteorDailyWindPrefix() : extractConfig.getMeteorHourlyWindPrefix());
	    }
	    String filePath2 = filePath1;

	    filePath1 = filePath1.replace("$userid", String.valueOf(params.getUserId())).replace("$domainid", String.valueOf(params.getDomainId()))
	    .replace("$missionid", String.valueOf(params.getMissionId())).replace("$scenarioid", String.valueOf(params.getScenarioId1()))
	      .replace("$domain", String.valueOf(params.getDomain()));

	    if(!CALCTYPE_SHOW.equals(params.getCalcType())) {
	      filePath2 = filePath2.replace("$userid", String.valueOf(params.getUserId())).replace("$domainid", String.valueOf(params.getDomainId()))
	        .replace("$missionid", String.valueOf(params.getMissionId())).replace("$scenarioid", String.valueOf(params.getScenarioId2()))
	          .replace("$domain", String.valueOf(params.getDomain()));
	    }
	    
	    if(TIMEPOINT_A.equals(params.getTimePoint()) && SHOW_TYPE_CONCN.equals(params.getShowType())) {
	      List<String> list = params.getDates();
	      variableList1 = new ArrayList<>();
	      variableList2 = new ArrayList<>();
	      for(int i = 0; i < list.size(); i++) {
	        String filePath = filePath1 + list.get(i);
	        try {
	          long openNcTimes = System.currentTimeMillis();
	          NetcdfFile nc1 = NetcdfFile.open(filePath);
	          ncFile1 = nc1;
	          logger.info("open nc file" + filePath + ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");
	          Variable variable1 = nc1.findVariable(null, params.getSpecies());
	          variableList1.add(variable1);
	          if(!CALCTYPE_SHOW.equals(params.getCalcType())) {
	            NetcdfFile nc2 = NetcdfFile.open(filePath);
	            Variable variable2 = nc2.findVariable(null, params.getSpecies());
	            variableList2.add(variable2);
	          }
	        } catch (IOException e) {
	          logger.error("open file " + filePath + " error");
	          return ;
	        }
	      }
	      try {
	        attributes = Netcdf.getAttributes(filePath1 + list.get(0));
	      } catch (IOException e) {
	        logger.error("getAttributes IOException");
	        return;
	      }
	    } else {
	      filePath1 = filePath1 + params.getDay();
	      try {
	        long openNcTimes = System.currentTimeMillis();
	        ncFile1 = NetcdfFile.open(filePath1);
	        logger.info("open nc file" + filePath1 + ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");
	      } catch (IOException e) {
	        logger.error("open file " + filePath1 + " error");
	        return ;
	      }
	      if(ncFile1 == null) return;
	      variable1 = ncFile1.findVariable(null, params.getSpecies());
	      if(variable1 == null) return;

	      try {
	        attributes = Netcdf.getAttributes(filePath1);
	      } catch (IOException e) {
	        logger.error("getAttributes IOException");
	        return;
	      }

	      if(!CALCTYPE_SHOW.equals(params.getCalcType())) {
	        filePath2 = filePath2 + params.getDay();
	        try {
	          long openNcTimes = System.currentTimeMillis();
	          ncFile2 = NetcdfFile.open(filePath2);
	          logger.info("open nc file " + filePath2 + ", times = " + (System.currentTimeMillis() - openNcTimes) + "ms");
	        } catch (IOException e) {
	          logger.error("open file " + filePath2 + " error");
	          return ;
	        }
	        if(ncFile2 == null) return;
	        variable2 = ncFile2.findVariable(null, params.getSpecies());
	        if(variable2 == null) return;
	      }
	    }
		
	}

	private Projection buildProject(ExtractRequestParams params) throws TransformException, FactoryException, IOException {

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
	    params.setLat_0(lat_0);
	    logger.info("read attr, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");
	    int layer = params.getLayer();
	    if(layer < 1 || layer > totalLayer) {
	      logger.error("the layer params should be between in 1 and " + totalLayer);
	    }
	    logger.info("(xorig,yorig) = (" + xorig + "," + yorig + "), (xcell,ycell) = (" + xcell + "," + ycell + "), (nrows, ncols) = (" + row + "," + col + ")");

	    interpolation = new Interpolation(params.getHour(), layer, xorig, yorig, xcell, ycell, row, col);

	    String[] proj = new String[]{"+proj=lcc", "+lat_1= " + lat_1,
	      "+lat_2=" + lat_2,
	      "+lat_0= " + lat_0,
	      "+lon_0= " + lon_0,
	      "+x_0=0", "+y_0=0", "+ellps=WGS84", "+units=m +no_defs"};

	    try {
	      Projection projection = new Projection(proj);
	      return projection;
	    } catch (TransformException e) {
	      logger.error("TransformException");
	      return null;
	    } catch (FactoryException e) {
	      logger.error("FactoryException");
	      return null;
	    }
	  }
	
	public List<PointBean> buildPointList(ExtractRequestParams params, List<Map<String, Object>> res) throws IOException, InvalidRangeException {
	    List<PointBean> pointBeanList = new ArrayList<>();
	    int cols = params.getCols();
	    int rows = params.getRows();
	    double xcellsize = (params.getXmax() - params.getXmin()) / cols;
	    double ycellsize = (params.getYmax() - params.getYmin()) / rows;
	    double x = params.getXmin();
	    double y = params.getYmin();

	    pointBeanArray = new PointBean[rows + 1][cols + 1];
	    long startTimes = System.currentTimeMillis();
	    for (int i = 0; i <=  rows; i++) {
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
	        PointBean pb = new PointBean(Projection.transToLat(merc_y), Projection.transToLon(merc_x), merc_x, merc_y);
	        pointBeanList.add(pb);
	        pointBeanArray[i][j] = pb;

	        double v = getValue(pb, params);
	        pb.setValue(v);

	        Map map = new HashMap<>();
	        map.put("x", nf.format(pb.getX()));
	        map.put("y", nf.format(pb.getY()));
//	      map.put("lat", pb.getLat());
//	      map.put("lon", pb.getLon());
//	      map.put("xlcc", p.getX());
//	      map.put("ylcc", p.getY());
	        map.put("v", pb.getValue());
	        res.add(map);

	        //保存经纬度、兰伯特数据
	        buildDataFile(pb);
	      }
	      x = params.getXmin();
	      y += ycellsize;
	    }
	    logger.info("get need show point list and calc value, times = " + (System.currentTimeMillis() - startTimes) + "ms");
	    return pointBeanList;
	  }

	  private void buildDataFile(PointBean pb) {
	    sbcsv_latlon.append(pb.getLon()).append(",").append(pb.getLat()).append(",").append(pb.getValue()).append("\r\n");
	    sbcsv_lcc.append(pb.getXlcc()).append(",").append(pb.getYlcc()).append(",").append(pb.getValue()).append("\r\n");
	  }

	  private double getValue(PointBean pb, ExtractRequestParams params) throws IOException, InvalidRangeException {
	    Point2D p = projection.transform(pb.getLon(), pb.getLat());
	    pb.setXlcc(p.getX());
	    pb.setYlcc(p.getY());
	    double value = 0;

	    if(CALCTYPE_SHOW.equals(params.getCalcType())) {
	      if(TIMEPOINT_A.equals(params.getTimePoint())) {
	        int size = variableList1.size();
	        for(int i = 0; i < size; i++) {
	          value += interpolation.interpolation(variableList1.get(i), p) / size;
	        }
	      } else {
	        value = interpolation.interpolation(variable1, p);
	      }
	    } else if("diff".equals(params.getCalcType())) {
	      if(TIMEPOINT_A.equals(params.getTimePoint())) {
	        double value1 = 0;
	        double value2 = 0;
	        int size1 = variableList1.size();
	        int size2 = variableList2.size();
	        for(int i = 0; i < size1; i++) {
	          value1 += interpolation.interpolation(variableList1.get(i), p) / size1;
	        }
	        for(int i = 0; i < size2; i++) {
	          value2 += interpolation.interpolation(variableList2.get(i), p) / size2;
	        }
	        value = value2 - value1;
	      } else {
	        double value1 = interpolation.interpolation(variable1, p);
	        double value2 = interpolation.interpolation(variable2, p);
	        value = value2 - value1;
	      }
	    } else if("ratio".equals(params.getCalcType())) {
	      if(TIMEPOINT_A.equals(params.getTimePoint())) {
	        double value1 = 0;
	        double value2 = 0;
	        int size1 = variableList1.size();
	        int size2 = variableList2.size();
	        for(int i = 0; i < size1; i++) {
	          value1 += interpolation.interpolation(variableList1.get(i), p) / size1;
	        }
	        for(int i = 0; i < size2; i++) {
	          value2 += interpolation.interpolation(variableList2.get(i), p) / size2;
	        }
	        if(value1 == 0.0) value1 = MINIMUM;
	        value = (value2 - value1) / value1;
	      } else {
	        double value1 = interpolation.interpolation(variable1, p);
	        double value2 = interpolation.interpolation(variable2, p);
	        if(value1 == 0.0) value1 = MINIMUM;
	        value = (value2 - value1) / value1;
	      }
	    }
	    return value;
	  }

	  public void exportExcel(PointBean[][] pointBeanArray, String pathOut) throws IOException, InvalidRangeException, TransformException, FactoryException {

	    HSSFWorkbook workbook = new HSSFWorkbook();
	    HSSFSheet oneOfSheet = workbook.createSheet("data");

	    for(int i = 0; i < pointBeanArray.length; i++) {
	      HSSFRow hssfRow = oneOfSheet.createRow(i);
	      for(int j = 0; j < pointBeanArray[i].length; j++) {
	        HSSFCell cell = hssfRow.createCell(j);
	        PointBean pb = pointBeanArray[i][j];
	        cell.setCellValue(pb.getValue());
	      }
	    }
	    String excel = pathOut + "/extract-data.xls";
	    File file = new File(pathOut);
	    if(!file.exists()) {
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
