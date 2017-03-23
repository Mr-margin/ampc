/**  
 * @Title: QueueDataWrf.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:00:34
 * @version 
 */
package ampc.com.gistone.redisqueue;

import org.springframework.stereotype.Component;

/**  
 * @Title: QueueDataWrf.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午2:00:34
 * @version 1.0
 */
@Component
public class QueueDataWrf {
	//来源于情景详情表  
	private String spinup;
	//来自于ungrib
	private String lastfnl;

	public String getSpinup() {
		return spinup;
	}
	public void setSpinup(String spinup) {
		this.spinup = spinup;
	}
	public String getLastfnl() {
		return lastfnl;
	}
	public void setLastfnl(String lastfnl) {
		this.lastfnl = lastfnl;
	}
	public QueueDataWrf() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QueueDataWrf(String spinup, String lastfnl) {
		super();
		this.spinup = spinup;
		this.lastfnl = lastfnl;
	}
	
}
