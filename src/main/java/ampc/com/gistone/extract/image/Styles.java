package ampc.com.gistone.extract.image;

import java.io.File;
import java.io.FileNotFoundException;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.RasterSymbolizerImpl;
import org.geotools.styling.SLD;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

/**
 * Created by dell on 2016-2-3.
 */
public class Styles {

	public static Style createXMLStyle() throws FileNotFoundException {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		SLDParser stylereader = new SLDParser(styleFactory, new File("demo-data/sld/raster_manycolorgradient.sld.xml"));
		final Style[] styles = stylereader.readXML();
		return styles[0];
	}

	public static Style createXMLStyle(String rgb, float max, String name) throws FileNotFoundException {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		String rootPath = System.getProperty("user.dir");
		SLDParser stylereader = new SLDParser(styleFactory,
				new File(rootPath + "/src/main/resources/drawPicture/raster_manycolorgradient.sld.xml"));
		final Style[] styles = stylereader.readXML();

		Symbolizer[] symbolizers = styles[0].featureTypeStyles().get(0).rules().get(0).getSymbolizers();
		for (Symbolizer s : symbolizers) {
			if (s instanceof RasterSymbolizerImpl) {
				((RasterSymbolizerImpl) s).setColorMap(Colors
						.buildColor(rootPath + "/src/main/resources/drawPicture/color/" + rgb + ".rgb", max, name));
			}
		}

		return styles[0];
	}

	public static Style createGreyscaleStyle(int band) {
		StyleFactory sf = CommonFactoryFinder.getStyleFactory();
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
		SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(band), ce);

		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ChannelSelection sel = sf.channelSelection(sct);
		sym.setChannelSelection(sel);
		return SLD.wrapSymbolizers(sym);
	}

}
