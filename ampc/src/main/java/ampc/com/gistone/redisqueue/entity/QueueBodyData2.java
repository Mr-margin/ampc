/**  
 * @Title: QueueBodyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:44:34
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;


/**  
 * @Title: QueueBodyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO 消息队列的body内的消息
 * @author yanglei
 * @date 2017年3月21日 上午9:44:34
 * @version 1.0
 */
public class QueueBodyData2 {
	//用户id
	private String userid;
	//domainid 根据任务从任务详情表（TMissionDetail）中获取对应的domainid
	private String domainid;
	//任务ID
	private String missionid;
	//情景ID
	private String scenarioid;
	//计算核数  
	private Long cores;
	//模式类型  共五种模式类型
	private String modeltype;
	//tasksindex 
	private Integer cIndex;
	//tasksenddate
	private String cDate;
	//common数据
	private Object common;
	//emis 数据
	private Object emis ;
	//wrf 数据
	private Object wrf;
	//cmap 数据
	private Object cmaq;
	//flag 调试模式
	private Integer flag;
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
	public String getScenarioid() {
		return scenarioid;
	}
	public void setScenarioid(String scenarioid) {
		this.scenarioid = scenarioid;
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
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Integer getcIndex() {
		return cIndex;
	}
	public void setcIndex(Integer cIndex) {
		this.cIndex = cIndex;
	}
	public String getcDate() {
		return cDate;
	}
	public void setcDate(String cDate) {
		this.cDate = cDate;
	}
	
	
	
}
