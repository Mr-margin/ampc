package ampc.com.gistone.extract;

/**
 * Created by xll on 2017/3/2.
 */
public class PointBean {
	private double lat;
	private double lon;
	private double x;
	private double y;
	private double xlcc;
	private double ylcc;
	private double value;

	public PointBean() {
	}

	public PointBean(double lat, double lon, double merc_x, double merc_y) {
		this.lat = lat;
		this.lon = lon;
		this.x = merc_x;
		this.y = merc_y;
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

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getXlcc() {
		return xlcc;
	}

	public void setXlcc(double xlcc) {
		this.xlcc = xlcc;
	}

	public double getYlcc() {
		return ylcc;
	}

	public void setYlcc(double ylcc) {
		this.ylcc = ylcc;
	}
}
