package ampc.com.gistone.extract.vertical;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

public class VelocityUtil {

	  private static VelocityEngine velocityEngine;

	  public static void setRelativeOrNot(boolean relativeOrNot) {
	    VelocityUtil.relativeOrNot = relativeOrNot;
	  }

	  private static boolean relativeOrNot = true;

	  private static VelocityEngine newInstance() {
	    if (velocityEngine == null) {
	      synchronized (VelocityEngine.class) {
	        if (velocityEngine == null) {
	          velocityEngine = new VelocityEngine();
	          Properties p = new Properties();
	          p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
	          p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
	          p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
	          if(!relativeOrNot) {
	            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,"");
	          }
	          velocityEngine.init(p);
	        }
	      }
	    }
	    return velocityEngine;
	  }

	  public static String buildTemplate(String templateFile, Map<String, ?> values) {
	    Template template = VelocityUtil.newInstance().getTemplate(templateFile);
	    VelocityContext velocityContext = new VelocityContext();
	    for (String key : values.keySet()) {
	      velocityContext.put(key, values.get(key));
	    }
	    StringWriter writer = new StringWriter();
	    template.merge(velocityContext, writer);
	    return writer.toString().replaceAll("\r\n", "\n");
	  }

	  public static String buildTemplate(String templateFile, String key, Object value) {
	    Template template = VelocityUtil.newInstance().getTemplate(templateFile);
	    VelocityContext velocityContext = new VelocityContext();
	    velocityContext.put(key, value);
	    StringWriter writer = new StringWriter();
	    template.merge(velocityContext, writer);
	    return writer.toString().replaceAll("\r\n", "\n");
	  }

}
