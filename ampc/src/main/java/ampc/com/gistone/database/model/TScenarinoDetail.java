package ampc.com.gistone.database.model;

import java.util.Date;

public class TScenarinoDetail {
    private Long scenarinoId;

    private Long scenarinoType;

    private Object scenarinoName;

    private Date scenarinoStartDate;

    private Date scenarinoEndDate;

    private Long scenarinoStatus;

    private Long missionId;

    private Date scenarinoAddTime;

    private Long userId;

    private Date updateTime;

    private String isEffective;

    private Date deleteTime;

    private Long basisScenarinoId;

    private Date basisTime;

    private Long contrastScenarinoId;

    private String scenType;

    private Date pathDate;

    private Long spinup;

    private Date ratioStartDate;

    private Date ratioEndDate;

    private Long rangeDay;

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
        this.isEffective = isEffective == null ? null : isEffective.trim();
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

    public Long getContrastScenarinoId() {
        return contrastScenarinoId;
    }

    public void setContrastScenarinoId(Long contrastScenarinoId) {
        this.contrastScenarinoId = contrastScenarinoId;
    }

    public String getScenType() {
        return scenType;
    }

    public void setScenType(String scenType) {
        this.scenType = scenType == null ? null : scenType.trim();
    }

    public Date getPathDate() {
        return pathDate;
    }

    public void setPathDate(Date pathDate) {
        this.pathDate = pathDate;
    }

    public Long getSpinup() {
        return spinup;
    }

    public void setSpinup(Long spinup) {
        this.spinup = spinup;
    }

    public Date getRatioStartDate() {
        return ratioStartDate;
    }

    public void setRatioStartDate(Date ratioStartDate) {
        this.ratioStartDate = ratioStartDate;
    }

    public Date getRatioEndDate() {
        return ratioEndDate;
    }

    public void setRatioEndDate(Date ratioEndDate) {
        this.ratioEndDate = ratioEndDate;
    }

    public Long getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Long rangeDay) {
        this.rangeDay = rangeDay;
    }
}