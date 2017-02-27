package ampc.com.gistone.entity;

import java.util.List;
import java.util.Map;
/**
 * 区域显示帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public class AreaUtil {
	//区域ID
	private Object areaId;
	//区域名称
	private Object areaName;
	//时段的集合
	private List<Map> timeItems;
	public Object getAreaId() {
		return areaId;
	}
	public void setAreaId(Object areaId) {
		this.areaId = areaId;
	}
	public Object getAreaName() {
		return areaName;
	}
	public void setAreaName(Object areaName) {
		this.areaName = areaName;
	}
	public List<Map> getTimeItems() {
		return timeItems;
	}
	public void setTimeItems(List<Map> timeItems) {
		this.timeItems = timeItems;
	}
	
	
}
