package ampc.com.gistone.database.model;

import java.util.Date;

public class TPlanReuse {
    private Long planReuseId;

    private Long userId;

    private Object planReuseName;

    private Date addTime;

    private Long usedBy;

    private Date deleteTime;

    private String isEffective;

    private Long scenarioId;

    private Long missionId;

    private Date planReuseStartTime;

    private Date planReuseEndTime;

    private Long areaId;

    public Long getPlanReuseId() {
        return planReuseId;
    }

    public void setPlanReuseId(Long planReuseId) {
        this.planReuseId = planReuseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Object getPlanReuseName() {
        return planReuseName;
    }

    public void setPlanReuseName(Object planReuseName) {
        this.planReuseName = planReuseName;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Long getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(Long usedBy) {
        this.usedBy = usedBy;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Date getPlanReuseStartTime() {
        return planReuseStartTime;
    }

    public void setPlanReuseStartTime(Date planReuseStartTime) {
        this.planReuseStartTime = planReuseStartTime;
    }

    public Date getPlanReuseEndTime() {
        return planReuseEndTime;
    }

    public void setPlanReuseEndTime(Date planReuseEndTime) {
        this.planReuseEndTime = planReuseEndTime;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
}