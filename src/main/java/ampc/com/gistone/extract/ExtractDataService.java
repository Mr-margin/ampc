package ampc.com.gistone.extract;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ampc.com.gistone.extract.netcdf.Netcdf;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

@Service
public class ExtractDataService {
	
	private final static Logger logger = LoggerFactory.getLogger(ExtractDataService.class);

	public List<Map<String, Object>> buildData(ExtractRequestParams params) throws IOException, TransformException, FactoryException, InvalidRangeException {
		
		if(!checkParams(params)) return null;
		
		List<PointBean> pointBeanList = buildPointList(params);
		
		List<Map<String, Object>> res = calc(params, pointBeanList);
		
		return res;
		
	}

	private boolean checkParams(ExtractRequestParams params) {
		if(params.getCalcType() == null || params.getShowType() == null || params.getTimePoint() == null || params.getSpecies() == null) {
			return false;
		}
		return true;
	}

	private List<Map<String, Object>> calc(ExtractRequestParams params, List<PointBean> pointBeanList) throws TransformException, FactoryException, IOException, InvalidRangeException {
		long calcTimes = System.currentTimeMillis();
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
	    nf.setGroupingUsed(false);
	    nf.setMaximumFractionDigits(10);
		
		String showType = params.getShowType();
		String calcType = params.getCalcType();
		String filePath1 = "";
		if(params.getScenarioId1() == 251) {
			filePath1 = "E:\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
		} 
		if(params.getScenarioId1() == 252) {
			filePath1 = "demo-data\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
		} 
		if(params.getScenarioId1() == 253) {
			filePath1 = "demo-data\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
		} 
		
		long openNcTimes = System.currentTimeMillis();
		NetcdfFile ncFile1 = null;
		Variable variable1;
		NetcdfFile ncFile2 = null;
	    Variable variable2 = null;
		try {
			ncFile1 = NetcdfFile.open(filePath1);
			variable1 = ncFile1.findVariable(null, params.getSpecies());
		    
		    String filePath2 = "";
		    if(calcType.equals("diff") || calcType.equals("ratio")) {
		    	if(params.getScenarioId2() == 251) {
					filePath2 = "demo-data\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
				} 
				if(params.getScenarioId2() == 252) {
					filePath2 = "demo-data\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
				} 
				if(params.getScenarioId2() == 253) {
					filePath2 = "demo-data\\hebei\\concn\\423-1189\\ConcnDaily3D.interpolation.2016322";
				} 
		      ncFile2 = NetcdfFile.open(filePath2);
		      variable2 = ncFile2.findVariable(null, params.getSpecies());
		    }
		} catch (IOException e) {
			logger.error("open nc file error!", e);
			return null;
		}
	    
	    logger.info("open nc file, times = " + (System.currentTimeMillis() - openNcTimes) + "ms");

	    long readAttrTimes = System.currentTimeMillis();
	    Map attributes = null;
		try {
			attributes = Netcdf.getAttributes(filePath1);
		} catch (IOException e) {
			logger.error("load nc file attribute error!", e);
			return null;
		}

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
	    int hour = params.getHour();
	    int layer = params.getLayer();
	    if(hour < 0 || hour > 24) {
	      logger.error("the hour params is wrong, the value should be between in 0 and 23");
	      return null;
	    }
	    if(layer < 1 || layer > totalLayer) {
	      logger.error("the layer params should be between in 1 and " + totalLayer);
	      return null;
	    }
	    logger.info("read attribute, times = " + (System.currentTimeMillis() - readAttrTimes) + "ms");

	    long tranTimes = System.currentTimeMillis();
	    String[] proj = new String[]{"+proj=lcc", "+lat_1= " + lat_1,
	      "+lat_2=" + lat_2,
	      "+lat_0= " + lat_0,
	      "+lon_0= " + lon_0,
	      "+x_0=0", "+y_0=0", "+ellps=WGS84", "+units=m +no_defs"};
	    List<Map<String, Object>> res = new ArrayList<>();
	    Projection projection;
		try {
			projection = new Projection(proj);
			for (PointBean pb : pointBeanList) {
		      Point2D p = projection.transform(pb.getLon(), pb.getLat());
		      logger.info("[" + pb.getLon() + "," + pb.getLat() + "], merc: [" + pb.getX() + "," + pb.getY() + "], lcc: [" + p.getX() + "," + p.getY() + "]");
		      double value = 0;
		      if(calcType.equals("show")) {
				value = Interpolation.interpolation(hour, layer, xorig, yorig, xcell, ycell, row, col, variable1, p);
		      } else if(calcType.equals("diff")) {
		        double value1 = Interpolation.interpolation(hour, layer, xorig, yorig, xcell, ycell, row, col, variable1, p);
		        double value2 = Interpolation.interpolation(hour, layer, xorig, yorig, xcell, ycell, row, col, variable2, p);
		        value = value2 - value1;
		      } else if(calcType.equals("ratio")) {
		        double value1 = Interpolation.interpolation(hour, layer, xorig, yorig, xcell, ycell, row, col, variable1, p);
		        double value2 = Interpolation.interpolation(hour, layer, xorig, yorig, xcell, ycell, row, col, variable2, p);
		        value = (value2 - value1) / value1;
		      }
		      if(value == -9999) value = 0;
		      BigDecimal b = new BigDecimal(value);
		      double v = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		      pb.setValue(v);
		      Map<String, Object> map = new HashMap<>();
		      map.put("x", nf.format(pb.getX()));
		      map.put("y", nf.format(pb.getY()));
		      map.put("v", pb.getValue());
		      res.add(map);
		    }
			logger.info("transform project and get value, times = " + (System.currentTimeMillis() - tranTimes) + "ms");
		} catch (TransformException e) {
			logger.error("transform projection error!", e);
			return null;
		} catch (FactoryException e) {
			logger.error("transform projection error!", e);
			return null;
		} catch (IOException e) {
			logger.error("interpolation error!", e);
			return null;
		} catch (InvalidRangeException e) {
			logger.error("interpolation error!", e);
			return null;
		}
	    logger.info("calc total, times = " + (System.currentTimeMillis() - calcTimes) + "ms");
		return res;
	}

	private List<PointBean> buildPointList(ExtractRequestParams params) {
		long startTimes = System.currentTimeMillis();
		double xmax = params.getXmax();
		double xmin = params.getXmin();
		double ymax = params.getYmax();
		double ymin = params.getYmin();
		int rows = params.getRows();
		int cols = params.getCols();
		List<PointBean> pointBeanList = new ArrayList<>();
	    double xcellsize = (xmax - xmin) / cols;
	    double ycellsize = (ymax - ymin) / rows;
	    double x = xmin;
	    double y = ymin;
	    
	    for (int i = 0; i <= rows; i++) {
	      for (int j = 0; j <= cols; j++) {
	        double merc_y = y;
	        double merc_x;
	        if (i == 0 && j == 0) {
	          merc_x = xmin;
	        } else if (j == 0) {
	          merc_x = x;
	        } else {
	          x += xcellsize;
	          merc_x = x;
	        }
	        PointBean pb = new PointBean(Projection.transToLat(merc_y), Projection.transToLon(merc_x), merc_x, merc_y);

	        pointBeanList.add(pb);
	      }
	      x = xmin;
	      y += ycellsize;
	    }
	    logger.info("get need show point list, times = " + (System.currentTimeMillis() - startTimes) + "ms");
	    return pointBeanList;
		
	}
	
	
}
