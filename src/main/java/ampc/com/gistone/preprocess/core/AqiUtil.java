package ampc.com.gistone.preprocess.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 等谁有空来重构这个类吧
 * 
 * @author HP 并没有重构 反而删减了好多看不懂的
 * @author Dell
 */
public class AqiUtil {

	static final Logger logger = LoggerFactory.getLogger(AqiUtil.class);
	private static Map<String, Integer[]> AQI_CONSTANTS = new HashMap<>();
	public final static String SO2 = "SO2";
	public final static String CO = "CO";
	public final static String NO2 = "NO2";
	public final static String PM25 = "PM25";
	public final static String PM10 = "PM10";
	public final static String O3 = "O3";
	public final static String O3_8 = "O3_8";
	public final static String SO2_24 = "SO2_24";
	public final static String CO_24 = "CO_24";
	public final static String NO2_24 = "NO2_24";
	private final static String[] ELEMENTS = new String[] { SO2, CO, NO2, PM25, PM10, O3, O3_8, SO2_24, CO_24, NO2_24 };

	static {
		AQI_CONSTANTS.put("iaqi", new Integer[] { 0, 50, 100, 150, 200, 300, 400, 500 });
		AQI_CONSTANTS.put(SO2_24, new Integer[] { 0, 50, 150, 475, 800, 1600, 2100, 2620 });
		AQI_CONSTANTS.put(SO2, new Integer[] { 0, 150, 500, 650, 800 });
		AQI_CONSTANTS.put(NO2_24, new Integer[] { 0, 40, 80, 180, 280, 565, 750, 940 });
		AQI_CONSTANTS.put(NO2, new Integer[] { 0, 100, 200, 700, 1200, 2340, 3090, 3840 });
		AQI_CONSTANTS.put(PM10, new Integer[] { 0, 50, 150, 250, 350, 420, 500, 600 });
		AQI_CONSTANTS.put(CO_24, new Integer[] { 0, 2, 4, 14, 24, 36, 48, 60 });
		AQI_CONSTANTS.put(CO, new Integer[] { 0, 5, 10, 35, 60, 90, 120, 150 });
		AQI_CONSTANTS.put(O3, new Integer[] { 0, 160, 200, 300, 400, 800, 1000, 1200 });
		AQI_CONSTANTS.put(O3_8, new Integer[] { 0, 100, 160, 215, 265, 800 });
		AQI_CONSTANTS.put(PM25, new Integer[] { 0, 35, 75, 115, 150, 250, 350, 500 });
	}

	public static int getAqi(Map<String, Float> value) {
		Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
		float _aqi = 0;
		for (String _element : ELEMENTS) {
			String _key = _element.toUpperCase();
			float _cp = 0;
			if (value.containsKey(_key)) {
				_cp = value.get(_key);
			} else {

				continue;
			}
			int _bps[] = getBPLoAndHi(_element, _cp);

			if (_bps == null) {
				Integer[] vals = AQI_CONSTANTS.get(_element);
				if (_cp >= vals[vals.length - 1]) {
					_aqi = 500;
					logger.info(_cp + ">=" + vals[vals.length - 1] + " AQI=500 ");
				}
				continue;
			}

			int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

			float _iaqip = (_iaqis[1] - _iaqis[0]) / (float) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
			if (_aqi < _iaqip) {
				_aqi = _iaqip;
			}
		}
		return (int) Math.ceil(_aqi);
	}

	public static String getPrimElement(Map<String, Float> elementsDatas) {
		String _primElement = null;
		Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
		float _maxAqi = 0;
		for (String _element : ELEMENTS) {
			String _key = _element.toUpperCase();
			float _cp = 0;
			if (elementsDatas.containsKey(_key)) {
				_cp = elementsDatas.get(_key);
			} else {
				// System.out.println(String.format("element %s not found",
				// _element));
				continue;
			}
			int _bps[] = getBPLoAndHi(_element, _cp);
			if (_bps == null) {
				continue;
			}

			int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

			float _iaqip = (_iaqis[1] - _iaqis[0]) / (float) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
			if (_maxAqi < _iaqip) {
				_maxAqi = _iaqip;
				_primElement = _element;
			}
		}
		if (_maxAqi <= 50) {
			return null;
		}
		return _primElement;
	}

	private static int[] getBPLoAndHi(String element, float cp) {
		Integer[] _bps = AQI_CONSTANTS.get(element);
		for (int i = 0; i < _bps.length; i++) {
			if (cp <= _bps[i]) {
				if (i == 0) {
					return null;
				}
				int _lowIndex = i - 1;
				return new int[] { _lowIndex, _bps[_lowIndex], i, _bps[i] };
			}
		}
		return null;
	}

	public static Object getAqis(HashMap value) {
		int[] val = new int[24];
		for (int i = 0; i < 24; i++) {
			Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
			float _aqi = 0;
			for (String _element : ELEMENTS) {
				String _key = _element.toUpperCase();
				float _cp = 0;
				if (value.containsKey(_key)) {
					_cp = ((float[]) value.get(_key))[i];
				} else {
					continue;
				}
				int _bps[] = getBPLoAndHi(_element, _cp);
				if (_bps == null) {
					Integer[] vals = AQI_CONSTANTS.get(_element);
					if (_cp >= vals[vals.length - 1]) {
						_aqi = 500;
						logger.info(_cp + ">=" + vals[vals.length - 1] + " AQI=500 ");
					}
					continue;
				}

				int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

				float _iaqip = (_iaqis[1] - _iaqis[0]) / (float) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
				if (_aqi < _iaqip) {
					_aqi = _iaqip;
				}
			}
			val[i] = (int) Math.ceil(_aqi);
		}
		return val;
	}

	public static int getAqisOneHour(HashMap value) {
		Integer _iaqi[] = AQI_CONSTANTS.get("iaqi");
		float _aqi = 0;
		for (String _element : ELEMENTS) {
			String _key = _element.toUpperCase();
			float _cp = 0;
			if (value.containsKey(_key)) {
				_cp = ((float) value.get(_key));
			} else {
				continue;
			}
			int _bps[] = getBPLoAndHi(_element, _cp);
			if (_bps == null) {
				Integer[] vals = AQI_CONSTANTS.get(_element);
				if (_cp >= vals[vals.length - 1]) {
					_aqi = 500;
					logger.info(_cp + ">=" + vals[vals.length - 1] + " AQI=500 ");
				}
				continue;
			}

			int _iaqis[] = new int[] { _iaqi[_bps[0]], _iaqi[_bps[2]] };

			float _iaqip = (_iaqis[1] - _iaqis[0]) / (float) (_bps[3] - _bps[1]) * (_cp - _bps[1]) + _iaqis[0];
			if (_aqi < _iaqip) {
				_aqi = _iaqip;
			}
		}
		return (int) Math.ceil(_aqi);
	}
}