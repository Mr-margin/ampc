package ampc.com.gistone.database.model;

import java.util.Date;

public class TTime {
    private Long timeId;

    private Date timeStartDate;

    private Date timeEndDate;

    private Long areaId;

    private Long missionId;

    private Long scenarinoId;

    private Long planId;

    private Long userId;

    private String isEffective;

    private Date deleteTime;
    
    private String sort;//排序字段

    public Long getTimeId() {
        return timeId;
    }

    public void setTimeId(Long timeId) {
        this.timeId = timeId;
    }

    public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Date getTimeStartDate() {
        return timeStartDate;
    }

    public void setTimeStartDate(Date timeStartDate) {
        this.timeStartDate = timeStartDate;
    }

    public Date getTimeEndDate() {
        return timeEndDate;
    }

    public void setTimeEndDate(Date timeEndDate) {
        this.timeEndDate = timeEndDate;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
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