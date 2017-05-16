package ampc.com.gistone.extract.image;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 * Created by dell on 2016-2-3.
 */
public class Images {

	public static boolean buildImage(final MapContent map, final int imageWidth, String imagePath) throws IOException {

		GTRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);

		Rectangle imageBounds = null;
		ReferencedEnvelope mapBounds = null;
		try {
			mapBounds = map.getViewport().getBounds();
			double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
			imageBounds = new Rectangle(0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D gr = image.createGraphics();
		renderer.paint(gr, imageBounds, mapBounds);

		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		File file = new File(imagePath);
		ImageIO.write(image, "png", file);
		map.dispose();
		return true;
		// return out.toByteArray();

	}
}
