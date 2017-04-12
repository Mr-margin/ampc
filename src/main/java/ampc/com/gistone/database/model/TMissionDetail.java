package ampc.com.gistone.database.model;

import java.util.Date;

/**
 * 任务详情实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
public class TMissionDetail {
	//任务ID
    private Long missionId;
    //任务名称
    private Object missionName;
    //范围ID
    private Long missionDomainId;
    //清单ID
    private Long esCouplingId;
    //任务开始时间
    private Date missionStartDate;
    //任务结束时间
    private Date missionEndDate;
    //任务创建时间
    private Date missionAddTime;
    //任务修改时间
    private Date updateTime;
    //用户ID 
    private Long userId;
    //任务是否有效 0无效 1有效
    private String isEffective;
    //任务删除时间
    private Date deleteTime;
    //任务状态 
    private Object missionStatus;

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Object getMissionName() {
        return missionName;
    }

    public void setMissionName(Object missionName) {
        this.missionName = missionName;
    }

    public Long getMissionDomainId() {
        return missionDomainId;
    }

    public void setMissionDomainId(Long missionDomainId) {
        this.missionDomainId = missionDomainId;
    }

    public Long getEsCouplingId() {
        return esCouplingId;
    }

    public void setEsCouplingId(Long esCouplingId) {
        this.esCouplingId = esCouplingId;
    }

    public Date getMissionStartDate() {
        return missionStartDate;
    }

    public void setMissionStartDate(Date missionStartDate) {
        this.missionStartDate = missionStartDate;
    }

    public Date getMissionEndDate() {
        return missionEndDate;
    }

    public void setMissionEndDate(Date missionEndDate) {
        this.missionEndDate = missionEndDate;
    }

    public Date getMissionAddTime() {
        return missionAddTime;
    }

    public void setMissionAddTime(Date missionAddTime) {
        this.missionAddTime = missionAddTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Object getMissionStatus() {
        return missionStatus;
    }

    public void setMissionStatus(Object missionStatus) {
        this.missionStatus = missionStatus;
    }

	@Override
	public String toString() {
		return "TMissionDetail [missionId=" + missionId + ", missionName="
				+ missionName + ", missionDomainId=" + missionDomainId
				+ ", esCouplingId=" + esCouplingId + ", missionStartDate="
				+ missionStartDate + ", missionEndDate=" + missionEndDate
				+ ", missionAddTime=" + missionAddTime + ", updateTime="
				+ updateTime + ", userId=" + userId + ", isEffective="
				+ isEffective + ", deleteTime=" + deleteTime
				+ ", missionStatus=" + missionStatus + "]";
	}

    
}