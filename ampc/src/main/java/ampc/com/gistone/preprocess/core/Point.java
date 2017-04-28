package ampc.com.gistone.preprocess.core;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import ampc.com.gistone.extract.ProjectUtil;
import ampc.com.gistone.extract.Projection;

public class Point {
	double lat, lon;
	String name, code;
	List species;
	List timePoint;
	Map value;
	String[] proj;
	public int xi, yi, index;
	public boolean cross;

	public void buildIndex(Map attribute) throws TransformException, FactoryException {
		double xorig = Double.parseDouble(attribute.get("XORIG").toString());
		double yorig = Double.parseDouble(attribute.get("YORIG").toString());
		double xcell = Double.parseDouble(attribute.get("XCELL").toString());
		double ycell = Double.parseDouble(attribute.get("YCELL").toString());
		int dy = Integer.parseInt(attribute.get("NROWS").toString());
		int dx = Integer.parseInt(attribute.get("NCOLS").toString());
		Projection projection = ProjectUtil.getProj(attribute);
		Point2D p = projection.transform(lon, lat);

		xi = (int) ((p.getX() - xorig) / xcell);
		yi = (int) ((p.getY() - yorig) / ycell);

		if (yi > dy - 1 || xi > dx - 1 || yi < 0 || xi < 0)
			cross = true; // 代表越界

		index = yi * dx + xi;
	}

	public int getIndex() {
		return index;
	}

	public String[] getProj() {
		return proj;
	}

	public void setProj(String[] proj) {
		this.proj = proj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isCross() {
		return cross;
	}

	public void setCross(boolean cross) {
		this.cross = cross;
	}
}
