/**  
 * @Title: QueueDataCmap.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:03:14
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;


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
public class QueueDataCmaq {
	
	//来源于情景详情表  
	private Long spinup;
	//来自于基础情景
	private Map<String, Object> ic;
	
	
	public Long getSpinup() {
		return spinup;
	}
	public void setSpinup(Long spinup) {
		this.spinup = spinup;
	}
	public Map<String, Object> getIc() {
		return ic;
	}
	public void setIc(Map<String, Object> ic) {
		this.ic = ic;
	}
	public QueueDataCmaq() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
