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
}
