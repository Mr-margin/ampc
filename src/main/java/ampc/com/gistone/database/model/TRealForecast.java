package ampc.com.gistone.database.model;

import java.util.Date;

public class TRealForecast {
    private Long realForecastId;

    private Long userId;

    private Date createTime;

    private Long forecastScenarinoType;

    private Long rangeDay;

    private Date updateTime;

    private Long forecastStatus;

    private Long spinup;

    private Long baseScenarinoId;

    private Date startTime;

    public Long getRealForecastId() {
        return realForecastId;
    }

    public void setRealForecastId(Long realForecastId) {
        this.realForecastId = realForecastId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getForecastScenarinoType() {
        return forecastScenarinoType;
    }

    public void setForecastScenarinoType(Long forecastScenarinoType) {
        this.forecastScenarinoType = forecastScenarinoType;
    }

    public Long getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Long rangeDay) {
        this.rangeDay = rangeDay;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getForecastStatus() {
        return forecastStatus;
    }

    public void setForecastStatus(Long forecastStatus) {
        this.forecastStatus = forecastStatus;
    }

    public Long getSpinup() {
        return spinup;
    }

    public void setSpinup(Long spinup) {
        this.spinup = spinup;
    }

    public Long getBaseScenarinoId() {
        return baseScenarinoId;
    }

    public void setBaseScenarinoId(Long baseScenarinoId) {
        this.baseScenarinoId = baseScenarinoId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}