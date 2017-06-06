/**  
 * @Title: ModelExecuJson.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年6月5日 上午10:46:44
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

import java.util.Arrays;
import java.util.Date;

/**  
 * @Title: ModelExecuJson.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: 模式执行的json实体类
 * @author yanglei
 * @date 2017年6月5日 上午10:46:44
 * @version 1.0
 */
public class ModelExecuJson {
	//开始的日期 情景开始时间
	private Date startTime;
	//情景结束时间
	private Date endTime;
	//情景终止时间
	private Date stopTime;
	//情景终止的task名称
	private String stopData;
	//情景tasks数组
	private String[] moduleType;
	//在停止进度上hover时，做的信息提示
	private String stopMessage;
	//模式执行状态
	private int stopType;
	//情景类型
	private String sceneType;
	//任务类型
	private String missionType;
	//模式执行标识符1-逐日执行，2-逐模块执行
	private Integer execModel;
	//执行详细信息数据
	private Object[] excutionMessage;
	//执行提示消息
	private String modelExecTips;
	
	
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the stopTime
	 */
	public Date getStopTime() {
		return stopTime;
	}
	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	/**
	 * @return the stopData
	 */
	public String getStopData() {
		return stopData;
	}
	/**
	 * @param stopData the stopData to set
	 */
	public void setStopData(String stopData) {
		this.stopData = stopData;
	}
	/**
	 * @return the moduleType
	 */
	public String[] getModuleType() {
		return moduleType;
	}
	/**
	 * @param moduleType the moduleType to set
	 */
	public void setModuleType(String[] moduleType) {
		this.moduleType = moduleType;
	}
	/**
	 * @return the stopMessage
	 */
	public String getStopMessage() {
		return stopMessage;
	}
	/**
	 * @param stopMessage the stopMessage to set
	 */
	public void setStopMessage(String stopMessage) {
		this.stopMessage = stopMessage;
	}
	/**
	 * @return the stopType
	 */
	public int getStopType() {
		return stopType;
	}
	/**
	 * @param stopType the stopType to set
	 */
	public void setStopType(int stopType) {
		this.stopType = stopType;
	}
	/**
	 * @return the sceneType
	 */
	public String getSceneType() {
		return sceneType;
	}
	/**
	 * @param sceneType the sceneType to set
	 */
	public void setSceneType(String sceneType) {
		this.sceneType = sceneType;
	}
	/**
	 * @return the missionType
	 */
	public String getMissionType() {
		return missionType;
	}
	/**
	 * @param missionType the missionType to set
	 */
	public void setMissionType(String missionType) {
		this.missionType = missionType;
	}
	
	/**
	 * @return the execModel
	 */
	public Integer getExecModel() {
		return execModel;
	}
	/**
	 * @param execModel the execModel to set
	 */
	public void setExecModel(Integer execModel) {
		this.execModel = execModel;
	}
	
	/**
	 * @return the excutionMessage
	 */
	public Object[] getExcutionMessage() {
		return excutionMessage;
	}
	/**
	 * @param excutionMessage the excutionMessage to set
	 */
	public void setExcutionMessage(Object[] excutionMessage) {
		this.excutionMessage = excutionMessage;
	}
	/**
	 * @return the modelExecTips
	 */
	public String getModelExecTips() {
		return modelExecTips;
	}
	/**
	 * @param modelExecTips the modelExecTips to set
	 */
	public void setModelExecTips(String modelExecTips) {
		this.modelExecTips = modelExecTips;
	}
	
	/**
	
	 * <p>Description: </p>
	
	
	 */
	public ModelExecuJson() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (非 Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/ 
	
	@Override
	public String toString() {
		return "ModelExecuJson [startTime=" + startTime + ", endTime="
				+ endTime + ", stopTime=" + stopTime + ", stopData=" + stopData
				+ ", moduleType=" + Arrays.toString(moduleType)
				+ ", stopMessage=" + stopMessage + ", stopType=" + stopType
				+ ", sceneType=" + sceneType + ", missionType=" + missionType
				+ ", execModel=" + execModel + ", excutionMessage="
				+ Arrays.toString(excutionMessage) + ", modelExecTips="
				+ modelExecTips + "]";
	}
	
	
	
	
	
	
	
}
