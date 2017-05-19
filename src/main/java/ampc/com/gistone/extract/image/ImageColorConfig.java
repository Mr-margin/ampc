package ampc.com.gistone.extract.image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ampc.com.gistone.extract.Constants;

public class ImageColorConfig {

	private Map<String, Map<String, List>> concnShowHourlyColorMap;
	private Map<String, Map<String, List>> concnShowDailyColorMap;
	private Map<String, Map<String, List>> concnDiffHourlyColorMap;
	private Map<String, Map<String, List>> concnDiffDailyColorMap;
	private Map<String, Map<String, List>> concnRatioHourlyColorMap;
	private Map<String, Map<String, List>> concnRatioDailyColorMap;

	private Map<String, Map<String, List>> emisShowHourlyColorMap;
	private Map<String, Map<String, List>> emisShowDailyColorMap;
	private Map<String, Map<String, List>> emisDiffHourlyColorMap;
	private Map<String, Map<String, List>> emisDiffDailyColorMap;
	private Map<String, Map<String, List>> emisRatioHourlyColorMap;
	private Map<String, Map<String, List>> emisRatioDailyColorMap;

	public ImageColorConfig() {
		this.concnShowHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.concnShowDailyColorMap = new HashMap<String, Map<String, List>>();
		this.concnDiffHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.concnDiffDailyColorMap = new HashMap<String, Map<String, List>>();
		this.concnRatioHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.concnRatioDailyColorMap = new HashMap<String, Map<String, List>>();

		this.emisShowHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.emisShowDailyColorMap = new HashMap<String, Map<String, List>>();
		this.emisDiffHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.emisDiffDailyColorMap = new HashMap<String, Map<String, List>>();
		this.emisRatioHourlyColorMap = new HashMap<String, Map<String, List>>();
		this.emisRatioDailyColorMap = new HashMap<String, Map<String, List>>();
	}

	public Map<String, Map<String, List>> getSheetMap(String sheetName) {
		if (sheetName.equals(Constants.CONCN_SHOW_HOURLY)) {
			return this.getConcnShowHourlyColorMap();
		}
		if (sheetName.equals(Constants.CONCN_SHOW_DAILY)) {
			return this.getConcnShowDailyColorMap();
		}
		if (sheetName.equals(Constants.CONCN_DIFF_HOURLY)) {
			return this.getConcnDiffHourlyColorMap();
		}
		if (sheetName.equals(Constants.CONCN_DIFF_DAILY)) {
			return this.getConcnDiffDailyColorMap();
		}
		if (sheetName.equals(Constants.CONCN_RATIO_HOURLY)) {
			return this.getConcnRatioHourlyColorMap();
		}
		if (sheetName.equals(Constants.CONCN_RATIO_DAILY)) {
			return this.getConcnRatioDailyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_SHOW_HOURLY)) {
			return this.getEmisShowHourlyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_SHOW_DAILY)) {
			return this.getEmisShowDailyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_DIFF_HOURLY)) {
			return this.getEmisDiffHourlyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_DIFF_DAILY)) {
			return this.getEmisDiffDailyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_RATIO_HOURLY)) {
			return this.getEmisRatioHourlyColorMap();
		}
		if (sheetName.equals(Constants.EMIS_RATIO_DAILY)) {
			return this.getEmisRatioDailyColorMap();
		}
		return null;
	}

	public Map<String, Map<String, List>> getConcnShowHourlyColorMap() {
		return concnShowHourlyColorMap;
	}

	public void setConcnShowHourlyColorMap(Map<String, Map<String, List>> concnShowHourlyColorMap) {
		this.concnShowHourlyColorMap = concnShowHourlyColorMap;
	}

	public Map<String, Map<String, List>> getConcnShowDailyColorMap() {
		return concnShowDailyColorMap;
	}

	public void setConcnShowDailyColorMap(Map<String, Map<String, List>> concnShowDailyColorMap) {
		this.concnShowDailyColorMap = concnShowDailyColorMap;
	}

	public Map<String, Map<String, List>> getConcnDiffHourlyColorMap() {
		return concnDiffHourlyColorMap;
	}

	public void setConcnDiffHourlyColorMap(Map<String, Map<String, List>> concnDiffHourlyColorMap) {
		this.concnDiffHourlyColorMap = concnDiffHourlyColorMap;
	}

	public Map<String, Map<String, List>> getConcnDiffDailyColorMap() {
		return concnDiffDailyColorMap;
	}

	public void setConcnDiffDailyColorMap(Map<String, Map<String, List>> concnDiffDailyColorMap) {
		this.concnDiffDailyColorMap = concnDiffDailyColorMap;
	}

	public Map<String, Map<String, List>> getConcnRatioHourlyColorMap() {
		return concnRatioHourlyColorMap;
	}

	public void setConcnRatioHourlyColorMap(Map<String, Map<String, List>> concnRatioHourlyColorMap) {
		this.concnRatioHourlyColorMap = concnRatioHourlyColorMap;
	}

	public Map<String, Map<String, List>> getConcnRatioDailyColorMap() {
		return concnRatioDailyColorMap;
	}

	public void setConcnRatioDailyColorMap(Map<String, Map<String, List>> concnRatioDailyColorMap) {
		this.concnRatioDailyColorMap = concnRatioDailyColorMap;
	}

	public Map<String, Map<String, List>> getEmisShowHourlyColorMap() {
		return emisShowHourlyColorMap;
	}

	public void setEmisShowHourlyColorMap(Map<String, Map<String, List>> emisShowHourlyColorMap) {
		this.emisShowHourlyColorMap = emisShowHourlyColorMap;
	}

	public Map<String, Map<String, List>> getEmisShowDailyColorMap() {
		return emisShowDailyColorMap;
	}

	public void setEmisShowDailyColorMap(Map<String, Map<String, List>> emisShowDailyColorMap) {
		this.emisShowDailyColorMap = emisShowDailyColorMap;
	}

	public Map<String, Map<String, List>> getEmisDiffHourlyColorMap() {
		return emisDiffHourlyColorMap;
	}

	public void setEmisDiffHourlyColorMap(Map<String, Map<String, List>> emisDiffHourlyColorMap) {
		this.emisDiffHourlyColorMap = emisDiffHourlyColorMap;
	}

	public Map<String, Map<String, List>> getEmisDiffDailyColorMap() {
		return emisDiffDailyColorMap;
	}

	public void setEmisDiffDailyColorMap(Map<String, Map<String, List>> emisDiffDailyColorMap) {
		this.emisDiffDailyColorMap = emisDiffDailyColorMap;
	}

	public Map<String, Map<String, List>> getEmisRatioHourlyColorMap() {
		return emisRatioHourlyColorMap;
	}

	public void setEmisRatioHourlyColorMap(Map<String, Map<String, List>> emisRatioHourlyColorMap) {
		this.emisRatioHourlyColorMap = emisRatioHourlyColorMap;
	}

	public Map<String, Map<String, List>> getEmisRatioDailyColorMap() {
		return emisRatioDailyColorMap;
	}

	public void setEmisRatioDailyColorMap(Map<String, Map<String, List>> emisRatioDailyColorMap) {
		this.emisRatioDailyColorMap = emisRatioDailyColorMap;
	}

}
