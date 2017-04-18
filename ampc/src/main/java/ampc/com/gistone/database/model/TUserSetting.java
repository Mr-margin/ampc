package ampc.com.gistone.database.model;

import java.math.BigDecimal;
import java.util.Date;

public class TUserSetting {
    private Long userSettingId;

    private Long userId;

    private Long basicSpinup;

    private Object basicSpinname;

    private Long predictionTime;

    private BigDecimal storageCap;

    private Long calculationCap;

    private Long missionCap;

    private Long operationTime;

    private Long basicAutoOn;

    private Date addTime;

    public Long getUserSettingId() {
        return userSettingId;
    }

    public void setUserSettingId(Long userSettingId) {
        this.userSettingId = userSettingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBasicSpinup() {
        return basicSpinup;
    }

    public void setBasicSpinup(Long basicSpinup) {
        this.basicSpinup = basicSpinup;
    }

    public Object getBasicSpinname() {
        return basicSpinname;
    }

    public void setBasicSpinname(Object basicSpinname) {
        this.basicSpinname = basicSpinname;
    }

    public Long getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(Long predictionTime) {
        this.predictionTime = predictionTime;
    }

    public BigDecimal getStorageCap() {
        return storageCap;
    }

    public void setStorageCap(BigDecimal storageCap) {
        this.storageCap = storageCap;
    }

    public Long getCalculationCap() {
        return calculationCap;
    }

    public void setCalculationCap(Long calculationCap) {
        this.calculationCap = calculationCap;
    }

    public Long getMissionCap() {
        return missionCap;
    }

    public void setMissionCap(Long missionCap) {
        this.missionCap = missionCap;
    }

    public Long getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Long operationTime) {
        this.operationTime = operationTime;
    }

    public Long getBasicAutoOn() {
        return basicAutoOn;
    }

    public void setBasicAutoOn(Long basicAutoOn) {
        this.basicAutoOn = basicAutoOn;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}