package ampc.com.gistone.extract.vertical;

import java.util.List;


/**
 * 
 * @author qiao
 *
 */
public class VerticalParams {

  private String calcType; // show diff ratio
  private String showType; // concn

  private Double xmin;
  private Double ymin;
  private Double xmax;
  private Double ymax;
  private Double space;
  private Integer pointNums;

  private Long userId;
  private Long domainId;
  private Long missionId;
  private Integer domain;

  private String specie;
  private String timePoint;
  private String day;
  private Integer hour;
  private List<String> dates;
  private Long scenarioId1;
  private Long scenarioId2;

  private Double xorig;
  private Double yorig;
  private Double xcell;
  private Double ycell;
  private String lat_1;
  private String lat_2;
  private String lat_0;
  private String lon_0;
  private Integer row;
  private Integer col;

  public VerticalParams() {}

  public VerticalParams(Double xmin, Double ymin, Double xmax, Double ymax, Integer pointNums, Long userId, Long domainId
  ,Long missionId, Integer domain, String specie, String timePoint, String day) {
    this.xmin = xmin;
    this.ymin = ymin;
    this.xmax = xmax;
    this.ymax = ymax;
    this.pointNums = pointNums;
    this.userId = userId;
    this.domainId = domainId;
    this.missionId = missionId;
    this.domain = domain;
    this.specie = specie;
    this.timePoint = timePoint;
    this.day = day;
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

public Double getXmin() {
	return xmin;
}

public void setXmin(Double xmin) {
	this.xmin = xmin;
}

public Double getYmin() {
	return ymin;
}

public void setYmin(Double ymin) {
	this.ymin = ymin;
}

public Double getXmax() {
	return xmax;
}

public void setXmax(Double xmax) {
	this.xmax = xmax;
}

public Double getYmax() {
	return ymax;
}

public void setYmax(Double ymax) {
	this.ymax = ymax;
}

public Double getSpace() {
	return space;
}

public void setSpace(Double space) {
	this.space = space;
}

public Integer getPointNums() {
	return pointNums;
}

public void setPointNums(Integer pointNums) {
	this.pointNums = pointNums;
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

public Integer getDomain() {
	return domain;
}

public void setDomain(Integer domain) {
	this.domain = domain;
}

public String getSpecie() {
	return specie;
}

public void setSpecie(String specie) {
	this.specie = specie;
}

public String getTimePoint() {
	return timePoint;
}

public void setTimePoint(String timePoint) {
	this.timePoint = timePoint;
}

public String getDay() {
	return day;
}

public void setDay(String day) {
	this.day = day;
}

public Integer getHour() {
	return hour;
}

public void setHour(Integer hour) {
	this.hour = hour;
}

public List<String> getDates() {
	return dates;
}

public void setDates(List<String> dates) {
	this.dates = dates;
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

public Double getXorig() {
	return xorig;
}

public void setXorig(Double xorig) {
	this.xorig = xorig;
}

public Double getYorig() {
	return yorig;
}

public void setYorig(Double yorig) {
	this.yorig = yorig;
}

public Double getXcell() {
	return xcell;
}

public void setXcell(Double xcell) {
	this.xcell = xcell;
}

public Double getYcell() {
	return ycell;
}

public void setYcell(Double ycell) {
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

public Integer getRow() {
	return row;
}

public void setRow(Integer row) {
	this.row = row;
}

public Integer getCol() {
	return col;
}

public void setCol(Integer col) {
	this.col = col;
}
  
}
