package ampc.com.gistone.extract.image;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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

	public static boolean buildImage(final MapContent map, int imageWidth, int imageHeight, String imagePath)
			throws IOException {

		GTRenderer renderer = new StreamingRenderer();
		renderer.setMapContent(map);

		Rectangle imageBounds = null;
		ReferencedEnvelope mapBounds = null;
		try {
			mapBounds = map.getViewport().getBounds();
			double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
			// imageBounds = new Rectangle(0, 0, imageWidth, (int)
			// Math.round(imageWidth * heightToWidth));
			imageBounds = new Rectangle(0, 0, imageWidth, imageHeight);

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

	public static BufferedImage buildGraphics2D(int[] imgArr, int width, int height) {
		try {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			WritableRaster raster = image.getWritableTile(0, 0);
			raster.setDataElements(0, 0, width, height, imgArr);

			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setPaintMode();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setStroke(new BasicStroke(1));
			g2d.dispose();
			return image;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
