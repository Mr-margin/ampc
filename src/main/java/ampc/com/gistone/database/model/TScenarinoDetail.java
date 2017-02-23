package ampc.com.gistone.database.model;

import java.util.Date;

public class TScenarinoDetail {
    private Long id;

    private Long scenarinoType;

    private Object scenarinoName;

    private Date startDate;

    private Date endDate;

    private Long status;

    private Long missionId;

    private Date addTime;

    private Long userId;

    private Date updateTime;

    private String isEffective;

    private Date deleteTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
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
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}