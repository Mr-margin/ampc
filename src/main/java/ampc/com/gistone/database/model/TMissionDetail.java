package ampc.com.gistone.database.model;

import java.util.Date;

public class TMissionDetail {
    private Long missionId;

    private Object missionName;

    private Long missionDomainId;

    private Long esCouplingId;

    private Date missionStartDate;

    private Date missionEndDate;

    private Date missionAddTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;

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
}