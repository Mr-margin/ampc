package ampc.com.gistone.extract.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.ColorMapEntryImpl;
import org.geotools.styling.ColorMapImpl;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;

/**
 * Created by dell on 2016-2-20.
 */

public class Colors {

	public static ColorMap buildColor(String path, float v, String name) {
		try {
			ColorMap w = new ColorMapImpl();
			File f = new File(path);
			BufferedReader reader = new BufferedReader(new FileReader(f));

			reader.readLine();
			reader.readLine();
			String tempString = null;

			FilterFactory ff = CommonFactoryFinder.getFilterFactory();

			int num = 0;
			while ((tempString = reader.readLine()) != null) {
				while (tempString.contains("  ")) {
					tempString = tempString.replaceAll("  ", " ");
				}
				String[] rgb = tempString.split(" ");
				if (rgb.length < 4)
					continue;
				num++;
			}

			reader = new BufferedReader(new FileReader(f));

			reader.readLine();
			reader.readLine();

			int count = 0;
			while ((tempString = reader.readLine()) != null) {

				while (tempString.contains("  ")) {
					tempString = tempString.replaceAll("  ", " ");
				}
				String[] rgb = tempString.split(" ");
				if (rgb.length < 4)
					continue;
				String r = Integer.toHexString(Integer.valueOf(rgb[1]));
				if (r.length() % 2 == 1)
					r = "0" + r;

				String g = Integer.toHexString(Integer.valueOf(rgb[2]));
				if (g.length() % 2 == 1)
					g = "0" + g;

				String b = Integer.toHexString(Integer.valueOf(rgb[3]));
				if (b.length() % 2 == 1)
					b = "0" + b;

				String val = "#" + r + g + b;

				ColorMapEntryImpl colorMapEntry = new ColorMapEntryImpl();

				Literal ep = ff.literal(val);
				colorMapEntry.setColor(ep);
				// 颜色渲染方法 1
				ep = ff.literal(count * v / num);

				count++;

				colorMapEntry.setQuantity(ep);

				// add-by-Wgy 当quantity值为0时将颜色设置为透明
				if (ep.getValue().equals(0.0)) {

					org.opengis.filter.expression.Expression ex = ff.literal(0);

					colorMapEntry.setOpacity(ex);

				}

				w.addColorMapEntry(colorMapEntry);
			}
			// bulidLegnd(w, name);
			return w;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void bulidLegnd(ColorMap w, String name) {
		int last = 200;
		int width = 1000;
		int height = 150;
		int fontHeight = 25;
		BufferedImage buffImg = new BufferedImage(width + last, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D gd = buffImg.createGraphics();
		buffImg = gd.getDeviceConfiguration().createCompatibleImage(width + last, height, Transparency.TRANSLUCENT);
		gd = buffImg.createGraphics();
		gd.setFont(new Font("微软雅黑", Font.PLAIN, fontHeight)); // 设置字体

		int v = w.getColorMapEntries().length;
		int i = 0;
		for (ColorMapEntry c : w.getColorMapEntries()) {
			i++;
			String rgb = c.getColor().toString();

			Color color = new Color(Integer.valueOf(rgb.substring(1, 3), 16), Integer.valueOf(rgb.substring(3, 5), 16),
					Integer.valueOf(rgb.substring(5, 7), 16));

			gd.setColor(color); // 设置颜色
			Rectangle2D rectangle2D = new Rectangle((i - 1) * width / v, height / 3, width / v + 1, height - 1);
			gd.fill(rectangle2D);

			// gd.setColor(Color.black);
			// gd.drawRect((i-1)*width/v, height/3, i*width/v, height-1);
			if (i % 20 == 1) {
				gd.setColor(Color.black);
				double fv = Double.valueOf(c.getQuantity().toString());
				gd.drawString(double2String(fv), (i - 1) * width / v, fontHeight); // 输出文字（中文横向居中）

				rectangle2D = new Rectangle((i - 1) * width / v, height / 3 - 8, 3, 8);
				gd.fill(rectangle2D);

			}
		}

		gd.setColor(Color.black);
		gd.drawString("    ug/m^3", width, fontHeight);

		File outputfile = new File("bar/" + name + ".png");
		try {
			ImageIO.write(buffImg, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String double2String(double d) {
		String s = "";
		if (d == 0)
			return "0";

		if (d > 1) {
			s += ((int) d);
			if (((d - (int) d) * 10) != 0) {
				s += "." + (int) ((d - (int) d) * 10);
			}
		} else if (d > 0.01) {
			return String.valueOf((int) (d * 100) / 100f);
		} else {
			int c = 0;
			while (d < 1) {
				c++;
				d *= 10;
			}
			s += ((int) d);
			// if (((d-(int)d)*10)!=0){
			// s+="."+(int)((d-(int)d)*10);
			// }
			s += "e-" + c;
		}
		return s;
	}

}