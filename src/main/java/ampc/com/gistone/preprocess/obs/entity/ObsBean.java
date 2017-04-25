package ampc.com.gistone.preprocess.obs.entity;

import java.util.Calendar;
import java.util.Date;

public class ObsBean {

	// 主键
	private String id;
	// 添加时间
	private Date addTime;
	// 城市平均还是站点
	private String mode;
	// 城市code或者站点code
	private String city_station;
	// 请求内容
	private String content;
	// 具体的时间
	private Date date;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	// 开始的具体的时间
	private Date startDate;
	// 结束的具体的时间
	private Date endDate;
	// 更新时间
	private Date updateTime;
	// 所属年份
	private int years;

	private String tableName;

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		this.years = c.get(Calendar.YEAR);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getCity_station() {
		return city_station;
	}

	public void setCity_station(String city_station) {
		this.city_station = city_station;
	}

	public int getYears() {
		return years;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
