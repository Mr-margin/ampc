/**  
 * @Title: QueueDataWrf.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:00:34
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;




/**  
 * @Title: QueueDataWrf.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:00:34
 * @version 1.0
 */
public class QueueDataWrf {
	//来源于情景详情表  
	private Long spinup;
	//来自于ungrib
	private String lastungrib;

	
	
	public Long getSpinup() {
		return spinup;
	}
	public void setSpinup(Long spinup) {
		this.spinup = spinup;
	}
	public String getLastungrib() {
		return lastungrib;
	}
	public void setLastungrib(String lastungrib) {
		this.lastungrib = lastungrib;
	}
	public QueueDataWrf() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
