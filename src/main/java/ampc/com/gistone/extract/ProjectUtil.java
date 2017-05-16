package ampc.com.gistone.extract;

import java.util.Map;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectUtil {

	private final static Logger logger = LoggerFactory.getLogger(ProjectUtil.class);

	public static String[] getProjParam(Map attributes) {
		String lat_1 = attributes.get("P_ALP").toString();
		String lat_2 = attributes.get("P_BET").toString();
		String lat_0 = attributes.get("YCENT").toString();
		String lon_0 = attributes.get("XCENT").toString();
		String[] proj = new String[] { "+proj=lcc", "+lat_1= " + lat_1, "+lat_2=" + lat_2, "+lat_0= " + lat_0,
				"+lon_0= " + lon_0, "+x_0=0", "+y_0=0", "+ellps=WGS84", "+units=m +no_defs" };
		return proj;
	}

	public static Projection getProj(Map attributes) {
		String[] proj = getProjParam(attributes);

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

	public static String getWKT(Map attribute) {

		Double lat_1 = Double.valueOf(attribute.get("P_ALP").toString());
		Double lat_2 = Double.valueOf(attribute.get("P_BET").toString());
		Double lat_0 = Double.valueOf(attribute.get("YCENT").toString());
		Double lon_0 = Double.valueOf(attribute.get("XCENT").toString());

		String wkt = "PROJCS[\"source\"," + "GEOGCS[\"WGS 84\"," + "DATUM[\"World Geodetic System 1984\", "
				+ "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], \n"
				+ "AUTHORITY[\"EPSG\",\"6326\"]]," + "PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],"
				+ "PROJECTION[\"Lambert_Conformal_Conic_2SP\"]," + "PARAMETER[\"standard_parallel_1\"," + lat_1 + "],"
				+ "PARAMETER[\"standard_parallel_2\"," + lat_2 + "]," + "PARAMETER[\"latitude_of_origin\"," + lat_0
				+ "]," + "PARAMETER[\"central_meridian\"," + lon_0 + "]," + "PARAMETER[\"false_easting\",0],"
				+ "PARAMETER[\"false_northing\",0]," + "UNIT[\"Meter\",1]]";
		return wkt;
	}
}
