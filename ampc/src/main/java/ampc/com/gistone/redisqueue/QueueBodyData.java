/**  
 * @Title: QueueBodyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:44:34
 * @version 
 */
package ampc.com.gistone.redisqueue;


import org.springframework.stereotype.Component;

/**  
 * @Title: QueueBodyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO 消息队列的body内的消息
 * @author yanglei
 * @date 2017年3月21日 上午9:44:34
 * @version 1.0
 */
public class QueueBodyData {
	//用户id
	private Long userid;
	//domainid 根据任务从任务详情表（TMissionDetail）中获取对应的domainid
	private Long domainid;
	//任务ID
	private Long missionid;
	//情景ID
	private Long scenarioid;
	//计算核数  
	private Long cores;
	//模式类型  共五种模式类型
	private String modeltype;
	//common数据
	private Object common;
	//emis 数据
	private Object emis ;
	//wrf 数据
	private Object wrf;
	//cmap 数据
	private Object cmaq;
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public Long getDomainid() {
		return domainid;
	}
	public void setDomainid(Long domainid) {
		this.domainid = domainid;
	}
	public Long getMissionid() {
		return missionid;
	}
	public void setMissionid(Long missionid) {
		this.missionid = missionid;
	}
	public Long getCores() {
		return cores;
	}
	public void setCores(Long cores) {
		this.cores = cores;
	}
	public String getModeltype() {
		return modeltype;
	}
	public void setModeltype(String modeltype) {
		this.modeltype = modeltype;
	}
	public Object getCommon() {
		return common;
	}
	public void setCommon(Object common) {
		this.common = common;
	}
	public Object getEmis() {
		return emis;
	}
	public void setEmis(Object emis) {
		this.emis = emis;
	}
	public Object getWrf() {
		return wrf;
	}
	public void setWrf(Object wrf) {
		this.wrf = wrf;
	}
	public Object getCmaq() {
		return cmaq;
	}
	public void setCmaq(Object cmaq) {
		this.cmaq = cmaq;
	}
	public QueueBodyData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getScenarioid() {
		return scenarioid;
	}
	public void setScenarioid(Long scenarioid) {
		this.scenarioid = scenarioid;
	}
}
