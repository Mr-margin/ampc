/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午4:56:03
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午4:56:03
 * @version 1.0
 */
public class DomainData {
	//用户ID
	private  String userid;
	//domainID
	private  String domainid;
	//domian内容
	private  DomainParams domain;

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

	public DomainParams getDomain() {
		return domain;
	}

	public void setDomain(DomainParams domain) {
		this.domain = domain;
	}

	
	
	
	
	

}
