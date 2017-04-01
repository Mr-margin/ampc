package ampc.com.gistone.extract;

import java.util.List;

public class ExtractRequestParams {

	private String calcType;  // show diff ratio
	private String showType;  // emis concn wind
	private int userId;
	private int domainId;
	private int missionId;
	private int domain;
	private String species;
	private String timePoint;  // d h a
	private int scenarioId1;
	private int scenarioId2;
	private int layer;
	private String day;
	private int hour;
	private List dates;
	
	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;
	
	private int rows;
	private int cols;
	
	public ExtractRequestParams(String calcType, String showType, int userId, int domainId, int missionId, int domain, String species, String timePoint) {
		this.calcType = calcType;
		this.showType = showType;
		this.userId = userId;
		this.domainId = domainId;
		this.missionId = missionId;
		this.domain = domain;
		this.species = species;
		this.timePoint = timePoint;
	}
	
	public String getCalcType() {
		return calcType;
	}
	public void setCalcType(String calcType) {
		this.calcType = calcType;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public int getMissionId() {
		return missionId;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getTimePoint() {
		return timePoint;
	}
	public void setTimePoint(String timePoint) {
		this.timePoint = timePoint;
	}
	public int getScenarioId1() {
		return scenarioId1;
	}
	public void setScenarioId1(int scenarioId1) {
		this.scenarioId1 = scenarioId1;
	}
	public int getScenarioId2() {
		return scenarioId2;
	}
	public void setScenarioId2(int scenarioId2) {
		this.scenarioId2 = scenarioId2;
	}
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public List getDates() {
		return dates;
	}
	public void setDates(List dates) {
		this.dates = dates;
	}

	public double getXmin() {
		return xmin;
	}

	public void setXmin(double xmin) {
		this.xmin = xmin;
	}

	public double getYmin() {
		return ymin;
	}

	public void setYmin(double ymin) {
		this.ymin = ymin;
	}

	public double getXmax() {
		return xmax;
	}

	public void setXmax(double xmax) {
		this.xmax = xmax;
	}

	public double getYmax() {
		return ymax;
	}

	public void setYmax(double ymax) {
		this.ymax = ymax;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}
	
	
}
