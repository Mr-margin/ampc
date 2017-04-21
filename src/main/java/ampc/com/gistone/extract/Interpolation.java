package ampc.com.gistone.extract;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Variable;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Created by xll on 2017/3/16.
 */
public class Interpolation {
	
	private int hour;
	private int layer;
	private double xorig;
	private double yorig;
	private double xcell;
	private double ycell;
	private int row;
	private int col;

	public Interpolation() {}

	public Interpolation(int hour, int layer, double xorig, double yorig, double xcell, double ycell,
	                       int row, int col) {
	    this.hour = hour;
	    this.layer = layer;
	    this.xorig = xorig;
	    this.yorig = yorig;
	    this.xcell = xcell;
	    this.ycell = ycell;
	    this.row = row;
	    this.col = col;
	}

    public double interpolation(Variable variable, Point2D point2D) throws IOException, InvalidRangeException {
        int[] origin = new int[]{hour, layer - 1, 0, 0};
        int[] size = new int[]{1, 1, 1, 1};
        double x = point2D.getX();
	    double y = point2D.getY();
	
	    double subx = (x - xorig) / xcell;
	    double suby = (y - yorig) / ycell;
	
	    int x1 = (int) (subx);
	    int y1 = (int) (suby);
	
	    int x2 = x1 + 1;
	    int y2 = y1 + 1;
	
	    if(x1 / subx == 1 && y1 / suby != 1) {
	      x2 = x1;
	    }
	    if(x1 / subx != 1 && y1 / suby == 1) {
	      y2 = y1;
	    }
	    if(x1 / subx == 1 && y1 / suby == 1) {
	      x2 = x1;
	      y2 = y1;
	    }
	
	    if(subx < 0 || subx > col - 1 || suby < 0 || suby > row - 1 || x2 < 0 || x2 > col - 1 || y2 < 0 || y2 > row - 1) {
	      return -9999;
	
	    } else {
	
	      origin[2] = y1;
	      origin[3] = x1;
	      Array lbData = variable.read(origin, size);
	      float[] lbv = (float[]) lbData.copyTo1DJavaArray();
	
	      origin[2] = y1;
	      origin[3] = x2;
	      Array rbData = variable.read(origin, size);
	      float[] rbv = (float[]) rbData.copyTo1DJavaArray();
	
	      origin[2] = y2;
	      origin[3] = x1;
	      Array ltData = variable.read(origin, size);
	      float[] ltv = (float[]) ltData.copyTo1DJavaArray();
	
	      origin[2] = y2;
	      origin[3] = x2;
	      Array rtData = variable.read(origin, size);
	      float[] rtv = (float[]) rtData.copyTo1DJavaArray();
	
	      double value1 = (x2 - subx) / (x2 - x1) * lbv[0] + (subx - x1) / (x2 - x1) * rbv[0];
	      double value2 = (x2 - subx) / (x2 - x1) * ltv[0] + (subx - x1) / (x2 - x1) * rtv[0];
	      double value = (y2 - suby) / (y2 - y1) * value1 + (suby - y1) / (y2 - y1) * value2;
	      return value;
	  }	
  }

}
