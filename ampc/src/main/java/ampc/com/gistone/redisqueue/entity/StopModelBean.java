/**  
 * @Title: StopModelBean.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月13日 下午5:38:31
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

/**  
 * @Title: StopModelBean.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: 停止模式的实体类
 * @author yanglei
 * @date 2017年4月13日 下午5:38:31
 * @version 1.0
 */
public class StopModelBean {
	//用户ID
	private String userid ;
	//domainID
	private String domainid;
	//任务ID
	private String missionid;
	//情景ID
	private String secnarioid;
	
	

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDomainid() {
		return domainid;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	public String getMissionid() {
		return missionid;
	}

	public void setMissionid(String missionid) {
		this.missionid = missionid;
	}

	public String getSecnarioid() {
		return secnarioid;
	}

	public void setSecnarioid(String secnarioid) {
		this.secnarioid = secnarioid;
	}

	
	public StopModelBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
