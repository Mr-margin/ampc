package ampc.com.gistone.database.model;

import java.math.BigDecimal;
import java.util.Date;

public class TRealForecast {
    private Long realForecastId;

    private Long userId;

    private Date createTime;

    private Short forecastScenarinoType;

    private Long rangeDay;

    private Date updateTime;

    private Short forecastStatus;

    private Long spinup;

    private BigDecimal baseScenarinoId;

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

    public Short getForecastScenarinoType() {
        return forecastScenarinoType;
    }

    public void setForecastScenarinoType(Short forecastScenarinoType) {
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

    public Short getForecastStatus() {
        return forecastStatus;
    }

    public void setForecastStatus(Short forecastStatus) {
        this.forecastStatus = forecastStatus;
    }

    public Long getSpinup() {
        return spinup;
    }

    public void setSpinup(Long spinup) {
        this.spinup = spinup;
    }

    public BigDecimal getBaseScenarinoId() {
        return baseScenarinoId;
    }

    public void setBaseScenarinoId(BigDecimal baseScenarinoId) {
        this.baseScenarinoId = baseScenarinoId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}