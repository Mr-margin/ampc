package ampc.com.gistone.extract;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	private final static Logger logger = LoggerFactory.getLogger(Constants.class);

	public final static double OVERBORDER = -9999;
	public final static String SHOW_TYPE_CONCN = "concn";
	public final static String SHOW_TYPE_EMIS = "emis";
	public final static String SHOW_TYPE_WIND = "wind";
	public final static String SHOW_TYPE_METEOR = "meteor";
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

	public static final String[] METEORSPECIES = { "TEMP", "RH", "PRSFC", "PT", "PBL" };

	public static final String[] WINDSPECIES = { "WSPD", "WSPDCONVERT", "WDIR", "WDIRCONVERT" };

	public static final Map<Float, String> WINDDIRMAP;

	static {
		WINDDIRMAP = new HashMap<Float, String>();
		WINDDIRMAP.put(0f, "N");
		WINDDIRMAP.put(22.5f, "NNE");
		WINDDIRMAP.put(45f, "NE");
		WINDDIRMAP.put(67.5f, "ENE");
		WINDDIRMAP.put(90f, "E");
		WINDDIRMAP.put(112.5f, "ESE");
		WINDDIRMAP.put(135f, "SE");
		WINDDIRMAP.put(157.5f, "SSE");
		WINDDIRMAP.put(180f, "S");
		WINDDIRMAP.put(202.5f, "SSW");
		WINDDIRMAP.put(225f, "SW");
		WINDDIRMAP.put(247.5f, "WSW");
		WINDDIRMAP.put(270f, "W");
		WINDDIRMAP.put(292.5f, "WNW");
		WINDDIRMAP.put(315f, "NW");
		WINDDIRMAP.put(337.5f, "NNW");
	}

	public static float[] buildWindDir() {
		float unit = 360f / 16;
		float[] windDirArray = new float[17];
		for (int i = 0; i < 16; i++) {
			windDirArray[i] = unit * i;
		}
		windDirArray[16] = 360f;
		return windDirArray;
	}

	public static float binarySearchKeyDir(double targetNum) {

		if (targetNum > 360f) {
			targetNum -= 360f;
		}
		if (targetNum == 360f) {
			return 0;
		}
		float[] array = buildWindDir();
		int left = 0, right = 0;
		for (right = array.length - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			float midValue = array[midIndex];
			if (targetNum == midValue) {
				return midValue;
			}
			if (targetNum > midValue) {
				left = midIndex;
			} else {
				right = midIndex;
			}
			if (mid <= 1) {
				break;
			}
		}
		float rightnum = array[right];
		float leftnum = array[left];
		float ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum : leftnum;
		if (ret == 360)
			ret = 0;
		return ret;
	}

	public static int[] buildWindSpd(int windSymbol) {
		int total = 0;
		if (windSymbol == 0) {
			total = 7;
		} else {
			total = 11;
		}
		int[] windSpdArray = new int[total];
		int count = 0;
		for (int i = 0; i < windSpdArray.length; i++) {
			if (count % 2 == 0)
				windSpdArray[i] = count;
			count += 2;
		}
		return windSpdArray;
	}

	public static int binarySearchKeySpd(double targetNum, int windSymbol) {
		int[] array = buildWindSpd(windSymbol);
		int first = array[0];
		int last = array[array.length - 1];
		if (targetNum > last) {
			return last;
		}
		if (targetNum <= first) {
			return first;
		}

		int left = 0, right = 0;
		for (right = array.length - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			int midValue = array[midIndex];
			if (targetNum == midValue) {
				return midValue;
			}
			if (targetNum > midValue) {
				left = midIndex;
			} else {
				right = midIndex;
			}
			if (mid <= 1) {
				break;
			}
		}
		int rightnum = array[right];
		int leftnum = array[left];
		int ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum : leftnum;
		return ret;

	}

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
