package ampc.com.gistone.extract;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	private final static Logger logger = LoggerFactory.getLogger(Constants.class);

	public final static double OVERBORDER = -9999;
	public final static String SHOW_TYPE_CONCN = "concn";
	public final static String SHOW_TYPE_EMIS = "emis";
	public final static String TIMEPOINT_A = "a";
	public final static String TIMEPOINT_D = "d";
	public final static String TIMEPOINT_H = "h";
	public final static String CALCTYPE_SHOW = "show";
	public final static double MINIMUM = 0.0000000001;

	public final static String RESOLUTION_MONTH = "M";
	public final static String RESOLUTION_DAY = TIMEPOINT_D;
	public final static String RESOLUTION_HOUR = TIMEPOINT_H;

	public final static String AREA_CITY = "city";
	public final static String AREA_PROVINCE = "province";
	public final static String AREA_POINT = "station";
	public final static String AREA_POINT2 = "point";

	public static final String[] HOURLYSPECIES = { "PM25", "PM10", "O3", "SO2", "NO2", "CO", "SO4", "NO3", "NH4", "BC",
			"OM", "PMFINE" };
	public static final String[] DAILYSPECIES = { "PM25", "PM10", "O3_8_MAX", "O3_1_MAX", "O3_AVG", "SO2", "NO2", "CO",
			"SO4", "NO3", "NH4", "BC", "OM", "PMFINE" };

	public static boolean checkHourlyFile(String filePath) {
		logger.info("check " + filePath + " there exists!");
		File currFile = new File(filePath);
		if (currFile.exists()) {
			return true;
		} else {
			logger.error(filePath + " file does not exist!");
			return false;
		}
	}
}
