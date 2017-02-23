package ampc.com.gistone.database.model;

import java.util.Date;

public class TScenarinoArea {
    private Long scenarinoAreaId;

    private Long scenarinoDetailId;

    private Object areaName;

    private Date addTime;

    private Long userId;

    private Date updateTime;

    private String isEffective;

    private Date deleteTime;

    public Long getScenarinoAreaId() {
        return scenarinoAreaId;
    }

    public void setScenarinoAreaId(Long scenarinoAreaId) {
        this.scenarinoAreaId = scenarinoAreaId;
    }

    public Long getScenarinoDetailId() {
        return scenarinoDetailId;
    }

    public void setScenarinoDetailId(Long scenarinoDetailId) {
        this.scenarinoDetailId = scenarinoDetailId;
    }

    public Object getAreaName() {
        return areaName;
    }

    public void setAreaName(Object areaName) {
        this.areaName = areaName;
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