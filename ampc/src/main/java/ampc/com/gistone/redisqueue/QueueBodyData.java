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
@Component
public class QueueBodyData {
	//用户id
	private Long userId;
	//domainid 根据任务从任务详情表（TMissionDetail）中获取对应的domainid
	private Long domainId;
	//任务ID
	private Long missionId;
	//情景ID
	private Long scenarinoId;
	//计算核数  
	private Long cores;
	//模式类型  共五种模式类型
	private String modelType;
	//common数据
	private Object common;
	//emis 数据
	private Object emis ;
	//wrf 数据
	private Object wrf;
	//cmap 数据
	private Object cmap;
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public Long getMissionId() {
		return missionId;
	}
	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}
	public Long getScenarinoId() {
		return scenarinoId;
	}
	public void setScenarinoId(Long scenarinoId) {
		this.scenarinoId = scenarinoId;
	}
	public Long getCores() {
		return cores;
	}
	public void setCores(Long cores) {
		this.cores = cores;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
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
	public Object getCmap() {
		return cmap;
	}
	public void setCmap(Object cmap) {
		this.cmap = cmap;
	}
	public QueueBodyData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QueueBodyData(Long userId, Long domainId, Long missionId,
			Long scenarinoId, Long cores, String modelType, Object common,
			Object emis, Object wrf, Object cmap) {
		super();
		this.userId = userId;
		this.domainId = domainId;
		this.missionId = missionId;
		this.scenarinoId = scenarinoId;
		this.cores = cores;
		this.modelType = modelType;
		this.common = common;
		this.emis = emis;
		this.wrf = wrf;
		this.cmap = cmap;
	}
	
	
	
	

}
