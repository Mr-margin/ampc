package ampc.com.gistone.preprocess.concn;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.util.JsonUtil;

public class RequestParams {

	private final static Logger logger = LoggerFactory.getLogger(RequestParams.class);
	private String showType;
	private Long userId;
	private Long domainId;
	private Long missionId;
	private Long scenarioId;
	private int domain;
	private List<String> date;
	private String timePoint;
	private String mode;
	private List filter;
	private int year;
	private int scenarioType; // 情景类型
	private Map<String, String[]> dateTypeMap; // 将当前情景的日期区分fnl和gfs,日期格式yyyy-MM-dd

	public RequestParams() {
	}

	public RequestParams(Long userId, Long domainId, Long missionId, Long scenarioId, int domain, List<String> date,
			String timePoint) {
		super();
		this.userId = userId;
		this.domainId = domainId;
		this.missionId = missionId;
		this.scenarioId = scenarioId;
		this.domain = domain;
		this.date = date;
		this.timePoint = timePoint;
	}

	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("showType: ").append(showType).append("\r\n");
			sb.append("userId: ").append(userId).append("\r\n");
			sb.append("domainId: ").append(domainId).append("\r\n");
			sb.append("missionId: ").append(missionId).append("\r\n");
			sb.append("scenarioId: ").append(scenarioId).append("\r\n");
			sb.append("domain: ").append(domain).append("\r\n");
			sb.append("date: ").append(JsonUtil.objToJson(date)).append("\r\n");
			sb.append("timePoint: ").append(timePoint).append("\r\n");
			sb.append("mode: ").append(mode).append("\r\n");
			sb.append("filter: ").append(JsonUtil.objToJson(filter)).append("\r\n");
			sb.append("year: ").append(year).append("\r\n");
			sb.append("scenarioType: ").append(scenarioType).append("\r\n");
			sb.append("dateType: ").append(mapper.writeValueAsString(dateTypeMap)).append("\r\n");
		} catch (JsonProcessingException e) {
			logger.error("[RequestParams | toString() ]  JsonProcessingException ", e);
		}
		return sb.toString();
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

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public List<String> getDate() {
		return date;
	}

	public void setDate(List<String> date) {
		this.date = date;
	}

	public String getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(String timePoint) {
		this.timePoint = timePoint;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List getFilter() {
		return filter;
	}

	public void setFilter(List filter) {
		this.filter = filter;
	}

	public static Logger getLogger() {
		return logger;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public int getScenarioType() {
		return scenarioType;
	}

	public void setScenarioType(int scenarioType) {
		this.scenarioType = scenarioType;
	}

	public Map<String, String[]> getDateTypeMap() {
		return dateTypeMap;
	}

	public void setDateTypeMap(Map<String, String[]> dateTypeMap) {
		this.dateTypeMap = dateTypeMap;
	}

}
