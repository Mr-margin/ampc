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
	private String userid ;
	
	private String domainid;
	
	private String missionid;
	
	private String secnarioid;
	
	private String start;
	
	private String end;

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

	public StopModelBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
