package ampc.com.gistone.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 减排计算最后结果类   用来调用减排计算接口 传值的帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月7日
 */
public class JPResult {
	//预案措施Id+预案名称 用来作为唯一表示
	private String groupName;
	//行业名称
	private String smallIndex;
	//根据子措施拆分出来 （清单的标识）
	private String bigIndex;
	//预案的开始时间  分秒要0分0秒
	private String start;
	//预案的结束时间  分秒要59分59秒
	private String end;
	//装的是行政区划代码  暂时先不传递
	private List<Map> regionIds;
	//每一个Object对象中都是一个Map集合 放着子措施拆分出来和过滤信息
	private List<Object> ops;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getSmallIndex() {
		return smallIndex;
	}
	public void setSmallIndex(String smallIndex) {
		this.smallIndex = smallIndex;
	}
	public String getBigIndex() {
		return bigIndex;
	}
	public void setBigIndex(String bigIndex) {
		this.bigIndex = bigIndex;
	}
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public List<Map> getRegionIds() {
		return regionIds;
	}
	public void setRegionIds(List<Map> regionIds) {
		this.regionIds = regionIds;
	}
	public List<Object> getOps() {
		return ops;
	}
	public void setOps(List<Object> ops) {
		this.ops = ops;
	}
	
}
