/**  
 * @Title: QueueDataCommon.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:52:17
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.util.Map;

import org.springframework.stereotype.Component;

/**  
 * @Title: QueueDataCommon.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:52:17
 * @version 1.0
 */
@Component
public class QueueDataCommon {
	//初始化 第一次的值 为true 其他均为false
	private boolean firsttime;
	//气象数据类型，fnl, gfs
	private String datatype;
	//模拟开始和结束时间
	private Map<String, Map<String, String>> time;
	//起报时间
	private String pathdate;
	
	
	public boolean isFirsttime() {
		return firsttime;
	}
	public void setFirsttime(boolean firsttime) {
		this.firsttime = firsttime;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public Map<String, Map<String, String>> getTime() {
		return time;
	}
	public void setTime(Map<String, Map<String, String>> time) {
		this.time = time;
	}
	public String getPathdate() {
		return pathdate;
	}
	public void setPathdate(String pathdate) {
		this.pathdate = pathdate;
	}
	@Override
	public String toString() {
		return "QueueDataCommon [firsttime=" + firsttime + ", datatype="
				+ datatype + ", time=" + time + ", pathdate=" + pathdate + "]";
	}
	public QueueDataCommon(boolean firsttime, String datatype,
			Map<String, Map<String, String>> time, String pathdate) {
		super();
		this.firsttime = firsttime;
		this.datatype = datatype;
		this.time = time;
		this.pathdate = pathdate;
	}
	public QueueDataCommon() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	

}
