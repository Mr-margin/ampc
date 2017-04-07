package ampc.com.gistone.database.model;

import java.util.Date;

public class TPlanReuse {
    private Long planReuseId;

    private Long userId;

    private Object planReuseName;

    private Date addTime;

    private Date timeStartTime;

    private Date deleteTime;

    private String isEffective;

    private Object scenarioName;

    private Object missionName;

    private Object areaName;

    private Long oldPlanId;

    private Date timeEndTime;

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

    public Date getTimeStartTime() {
        return timeStartTime;
    }

    public void setTimeStartTime(Date timeStartTime) {
        this.timeStartTime = timeStartTime;
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

    public Object getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(Object scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Object getMissionName() {
        return missionName;
    }

    public void setMissionName(Object missionName) {
        this.missionName = missionName;
    }

    public Object getAreaName() {
        return areaName;
    }

    public void setAreaName(Object areaName) {
        this.areaName = areaName;
    }

    public Long getOldPlanId() {
        return oldPlanId;
    }

    public void setOldPlanId(Long oldPlanId) {
        this.oldPlanId = oldPlanId;
    }

    public Date getTimeEndTime() {
        return timeEndTime;
    }

    public void setTimeEndTime(Date timeEndTime) {
        this.timeEndTime = timeEndTime;
    }
}