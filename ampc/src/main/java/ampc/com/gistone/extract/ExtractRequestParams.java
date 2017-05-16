package ampc.com.gistone.extract;

import java.util.List;
import java.util.Map;

public class ExtractRequestParams {

	private String calcType; // show diff ratio
	private String showType; // emis concn wind
	private int windSymbol; // 0 as arrows wind and 1 as F wind
	private int borderType; // 0 don't show over point , 1 show all point
	private Long userId;
	private Long domainId;
	private Long missionId;
	private int domain;
	private List<String> species;
	private String timePoint; // d h a
	private Long scenarioId1;
	private Long scenarioId2;
	private int layer;
	private String day;
	private int hour;
	private List dates;
	private String specie;
	private boolean type; // true代表基准情景 false代表非基准
	private int year;
	private Map<String, String[]> dateTypeMap1; // 将当前情景的日期区分fnl和gfs,日期格式yyyy-MM-dd
	private Map<String, String[]> dateTypeMap2;
	// 如果是实时预报情景的气象数据，目前firstDay代表预报气象数据的中gfs的第一天数据，用于确定gfs文件路径（firstDay转换为世界时，然后+18）

	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;

	private int rows;
	private int cols;
	private int width; // 用来指定图片的宽
	private int height;// 用来指定图片的高

	private double xorig;
	private double yorig;
	private double xcell;
	private double ycell;
	private String lat_1;
	private String lat_2;
	private String lat_0;
	private String lon_0;
	private int row; // domain的行
	private int col; // domain的列

	public ExtractRequestParams(String calcType, String showType, Long userId, Long domainId, Long missionId,
			int domain, List<String> species, String timePoint) {
		this.calcType = calcType;
		this.showType = showType;
		this.userId = userId;
		this.domainId = domainId;
		this.missionId = missionId;
		this.domain = domain;
		this.species = species;
		this.timePoint = timePoint;
	}

	public ExtractRequestParams(String calcType, String showType, Long userId, Long domainId, Long missionId,
			int domain, String specie, String timePoint) {
		this.calcType = calcType;
		this.showType = showType;
		this.userId = userId;
		this.domainId = domainId;
		this.missionId = missionId;
		this.domain = domain;
		this.specie = specie;
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

	public int getWindSymbol() {
		return windSymbol;
	}

	public void setWindSymbol(int windSymbol) {
		this.windSymbol = windSymbol;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getMissionId() {
		return missionId;
	}

	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public List<String> getSpecies() {
		return species;
	}

	public void setSpecies(List<String> species) {
		this.species = species;
	}

	public String getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(String timePoint) {
		this.timePoint = timePoint;
	}

	public Long getScenarioId1() {
		return scenarioId1;
	}

	public void setScenarioId1(Long scenarioId1) {
		this.scenarioId1 = scenarioId1;
	}

	public Long getScenarioId2() {
		return scenarioId2;
	}

	public void setScenarioId2(Long scenarioId2) {
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

	public double getXorig() {
		return xorig;
	}

	public void setXorig(double xorig) {
		this.xorig = xorig;
	}

	public double getYorig() {
		return yorig;
	}

	public void setYorig(double yorig) {
		this.yorig = yorig;
	}

	public double getXcell() {
		return xcell;
	}

	public void setXcell(double xcell) {
		this.xcell = xcell;
	}

	public double getYcell() {
		return ycell;
	}

	public void setYcell(double ycell) {
		this.ycell = ycell;
	}

	public String getLat_1() {
		return lat_1;
	}

	public void setLat_1(String lat_1) {
		this.lat_1 = lat_1;
	}

	public String getLat_2() {
		return lat_2;
	}

	public void setLat_2(String lat_2) {
		this.lat_2 = lat_2;
	}

	public String getLat_0() {
		return lat_0;
	}

	public void setLat_0(String lat_0) {
		this.lat_0 = lat_0;
	}

	public String getLon_0() {
		return lon_0;
	}

	public void setLon_0(String lon_0) {
		this.lon_0 = lon_0;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getBorderType() {
		return borderType;
	}

	public void setBorderType(int borderType) {
		this.borderType = borderType;
	}

	public String getSpecie() {
		return specie;
	}

	public void setSpecie(String specie) {
		this.specie = specie;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Map<String, String[]> getDateTypeMap1() {
		return dateTypeMap1;
	}

	public void setDateTypeMap1(Map<String, String[]> dateTypeMap1) {
		this.dateTypeMap1 = dateTypeMap1;
	}

	public Map<String, String[]> getDateTypeMap2() {
		return dateTypeMap2;
	}

	public void setDateTypeMap2(Map<String, String[]> dateTypeMap2) {
		this.dateTypeMap2 = dateTypeMap2;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
