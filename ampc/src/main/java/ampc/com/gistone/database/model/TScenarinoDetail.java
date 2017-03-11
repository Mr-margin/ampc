package ampc.com.gistone.database.model;

import java.util.Date;

/**
 * 情景详情实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月25日
 */
public class TScenarinoDetail {
	//情景ID
    private Long scenarinoId;
    //情景的类型 1为基准 2为自定义
    private Long scenarinoType;
    //情景名称
    private Object scenarinoName;
    //情景开始时间
    private Date scenarinoStartDate;
    //情景结束时间
    private Date scenarinoEndDate;
    //情景执行状态 0为初始化
    private Long scenarinoStatus;
    //任务的Id
    private Long missionId;
    //创建时间
    private Date scenarinoAddTime;
    //用户ID
    private Long userId;
    //更新时间
    private Date updateTime;
    //是否有效 0无效 1有效
    private String isEffective;
    //删除时间
    private Date deleteTime;
    //基础情景id
    private Long basisScenarinoId;
    //基础日期
    private Date basisTime;
    //对比情景id
    private Long controstScenarinoId;
    //情景类型
    private String scenType;
    //起报日期
    private Date pathDate;
    
    private Long spinUp;

	public Long getScenarinoId() {
		return scenarinoId;
	}

	public void setScenarinoId(Long scenarinoId) {
		this.scenarinoId = scenarinoId;
	}

	public Long getScenarinoType() {
		return scenarinoType;
	}

	public void setScenarinoType(Long scenarinoType) {
		this.scenarinoType = scenarinoType;
	}

	public Object getScenarinoName() {
		return scenarinoName;
	}

	public void setScenarinoName(Object scenarinoName) {
		this.scenarinoName = scenarinoName;
	}

	public Date getScenarinoStartDate() {
		return scenarinoStartDate;
	}

	public void setScenarinoStartDate(Date scenarinoStartDate) {
		this.scenarinoStartDate = scenarinoStartDate;
	}

	public Date getScenarinoEndDate() {
		return scenarinoEndDate;
	}

	public void setScenarinoEndDate(Date scenarinoEndDate) {
		this.scenarinoEndDate = scenarinoEndDate;
	}

	public Long getScenarinoStatus() {
		return scenarinoStatus;
	}

	public void setScenarinoStatus(Long scenarinoStatus) {
		this.scenarinoStatus = scenarinoStatus;
	}

	public Long getMissionId() {
		return missionId;
	}

	public void setMissionId(Long missionId) {
		this.missionId = missionId;
	}

	public Date getScenarinoAddTime() {
		return scenarinoAddTime;
	}

	public void setScenarinoAddTime(Date scenarinoAddTime) {
		this.scenarinoAddTime = scenarinoAddTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getIsEffective() {
		return isEffective;
	}

	public void setIsEffective(String isEffective) {
		this.isEffective = isEffective;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public Long getBasisScenarinoId() {
		return basisScenarinoId;
	}

	public void setBasisScenarinoId(Long basisScenarinoId) {
		this.basisScenarinoId = basisScenarinoId;
	}

	public Date getBasisTime() {
		return basisTime;
	}

	public void setBasisTime(Date basisTime) {
		this.basisTime = basisTime;
	}

	public Long getControstScenarinoId() {
		return controstScenarinoId;
	}

	public void setControstScenarinoId(Long controstScenarinoId) {
		this.controstScenarinoId = controstScenarinoId;
	}

	public String getScenType() {
		return scenType;
	}

	public void setScenType(String scenType) {
		this.scenType = scenType;
	}

	public Date getPathDate() {
		return pathDate;
	}

	public void setPathDate(Date pathDate) {
		this.pathDate = pathDate;
	}

	public Long getSpinUp() {
		return spinUp;
	}

	public void setSpinUp(Long spinUp) {
		this.spinUp = spinUp;
	}

	
    
  
}