package ampc.com.gistone.database.model;

import java.util.Date;

public class TMissionDetail {
    private Long id;

    private Object missionName;

    private Long missionDomainId;

    private Long esCouplingId;

    private Date missionStartDate;

    private Date missionEndDate;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
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
}