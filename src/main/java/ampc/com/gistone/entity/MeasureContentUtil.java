package ampc.com.gistone.entity;

import java.util.List;
import java.util.Map;

/**
 * 预案措施中子措施Json串的转换帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月16日
 */
public class MeasureContentUtil {
	private String bigIndex;
	private String smallIndex;
	private List<Map> filters;
	private List<String> summary;
	private List<Map> table;
	public String getBigIndex() {
		return bigIndex;
	}
	public void setBigIndex(String bigIndex) {
		this.bigIndex = bigIndex;
	}
	public String getSmallIndex() {
		return smallIndex;
	}
	public void setSmallIndex(String smallIndex) {
		this.smallIndex = smallIndex;
	}
	public List<Map> getFilters() {
		return filters;
	}
	public void setFilters(List<Map> filters) {
		this.filters = filters;
	}
	public List<String> getSummary() {
		return summary;
	}
	public void setSummary(List<String> summary) {
		this.summary = summary;
	}
	public List<Map> getTable() {
		return table;
	}
	public void setTable(List<Map> table) {
		this.table = table;
	}
	
}
