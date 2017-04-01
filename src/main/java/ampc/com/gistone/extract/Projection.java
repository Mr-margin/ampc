package ampc.com.gistone.extract;

import com.jhlabs.map.proj.ProjectionFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.Point2D;

public class Projection {

  com.jhlabs.map.proj.Projection projection;

  public Projection(String[] var) throws TransformException, FactoryException {
    projection = ProjectionFactory.fromPROJ4Specification(var);
  }

  public Point2D.Double transform(double lon, double lat) {
    Point2D.Double src = new Point2D.Double(lon, lat);
    Point2D.Double dst = new Point2D.Double();
    projection.transform(src, dst);
    return dst;
  }

  public Point2D.Double inverseTransform(double lon, double lat) {
    Point2D.Double src = new Point2D.Double(lon, lat);
    Point2D.Double dst = new Point2D.Double();
    projection.inverseTransform(src,dst);
    return dst;
  }

  public static double transToLat(double merc_y) {
    double lat = merc_y / 20037508.34 * 180;
    lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
    return lat;
  }

  public static double transToLon(double merc_x) {
    return merc_x / 20037508.34 * 180;
  }
  
}
