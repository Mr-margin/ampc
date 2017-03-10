package ampc.com.gistone.entity;

import java.util.List;
import java.util.Map;

/**
 * 行业措施帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月7日
 */
public class SMUtil {
	private String sectorsName;
	private List<Map> measureItems;
	private Integer count;
	private List<Map> planMeasure;
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public List<Map> getPlanMeasure() {
		return planMeasure;
	}
	public void setPlanMeasure(List<Map> planMeasure) {
		this.planMeasure = planMeasure;
	}
	public String getSectorsName() {
		return sectorsName;
	}
	public void setSectorsName(String sectorsName) {
		this.sectorsName = sectorsName;
	}
	public List<Map> getMeasureItems() {
		return measureItems;
	}
	public void setMeasureItems(List<Map> measureItems) {
		this.measureItems = measureItems;
	}
	
}
