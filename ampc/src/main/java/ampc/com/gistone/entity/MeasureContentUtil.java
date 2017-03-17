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
	//清单标识
	private String bigIndex;
	//行业标识
	private String smallIndex;
	//过滤条件
	private List<Map> filters=new ArrayList<Map>();
	//一共要控制的项集合
	private Map summary=new HashMap();
	//放着 汇总 剩余 和 点源的 集合
	private List<Map> table=new ArrayList<Map>();
	//放着 子措施 的数据
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
