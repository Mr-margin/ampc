package ampc.com.gistone.entity;

import java.util.ArrayList;
import java.util.HashMap;
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
	private List<Map> filters=new ArrayList<Map>();
	private Map summary=new HashMap();
	private List<Map> table=new ArrayList<Map>();
	private List<Map> table1=new ArrayList<Map>();
	public List<Map> getTable1() {
		return table1;
	}
	public void setTable1(List<Map> table1) {
		this.table1 = table1;
	}
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
	public Map getSummary() {
		return summary;
	}
	public void setSummary(Map summary) {
		this.summary = summary;
	}
	public List<Map> getTable() {
		return table;
	}
	public void setTable(List<Map> table) {
		this.table = table;
	}
	
}
