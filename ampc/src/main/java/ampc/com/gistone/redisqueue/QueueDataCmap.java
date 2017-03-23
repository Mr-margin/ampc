/**  
 * @Title: QueueDataCmap.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:03:14
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.util.Map;

import org.springframework.stereotype.Component;

/**  
 * @Title: QueueDataCmap.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:03:14
 * @version 1.0
 */
@Component
public class QueueDataCmap {
	
	//来源于情景详情表  
	private String spinup;
	//来自于基础情景
	private Map<String, Map<String, String>> ic;
	
	public String getSpinup() {
		return spinup;
	}
	public void setSpinup(String spinup) {
		this.spinup = spinup;
	}
	public Map<String, Map<String, String>> getIc() {
		return ic;
	}
	public void setIc(Map<String, Map<String, String>> ic) {
		this.ic = ic;
	}
	public QueueDataCmap() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
