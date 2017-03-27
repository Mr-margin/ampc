package ampc.com.gistone.database.model;

import java.util.Date;

public class TTasksStatus {
    private Long tasksId;

    private Long tasksScenarinoId;

    private String errorStatus;

    private Date scenarinoStartDate;

    private Date scenarinoEndDate;

    private Long rangeDay;

    private Long stepindex;

    private Date tasksEndDate;

    private String sourceid;

    private String calctype;

    private String psal;

    private String ssal;

    private String meiccityconfig;

    private String beizhu;

    private String beizhu2;

    private String beizhu3;

    public Long getTasksId() {
        return tasksId;
    }

    public void setTasksId(Long tasksId) {
        this.tasksId = tasksId;
    }

    public Long getTasksScenarinoId() {
        return tasksScenarinoId;
    }

    public void setTasksScenarinoId(Long tasksScenarinoId) {
        this.tasksScenarinoId = tasksScenarinoId;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus == null ? null : errorStatus.trim();
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

    public Long getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Long rangeDay) {
        this.rangeDay = rangeDay;
    }

    public Long getStepindex() {
        return stepindex;
    }

    public void setStepindex(Long stepindex) {
        this.stepindex = stepindex;
    }

    public Date getTasksEndDate() {
        return tasksEndDate;
    }

    public void setTasksEndDate(Date tasksEndDate) {
        this.tasksEndDate = tasksEndDate;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid == null ? null : sourceid.trim();
    }

    public String getCalctype() {
        return calctype;
    }

    public void setCalctype(String calctype) {
        this.calctype = calctype == null ? null : calctype.trim();
    }

    public String getPsal() {
        return psal;
    }

    public void setPsal(String psal) {
        this.psal = psal == null ? null : psal.trim();
    }

    public String getSsal() {
        return ssal;
    }

    public void setSsal(String ssal) {
        this.ssal = ssal == null ? null : ssal.trim();
    }

    public String getMeiccityconfig() {
        return meiccityconfig;
    }

    public void setMeiccityconfig(String meiccityconfig) {
        this.meiccityconfig = meiccityconfig == null ? null : meiccityconfig.trim();
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu == null ? null : beizhu.trim();
    }

    public String getBeizhu2() {
        return beizhu2;
    }

    public void setBeizhu2(String beizhu2) {
        this.beizhu2 = beizhu2 == null ? null : beizhu2.trim();
    }

    public String getBeizhu3() {
        return beizhu3;
    }

    public void setBeizhu3(String beizhu3) {
        this.beizhu3 = beizhu3 == null ? null : beizhu3.trim();
    }
}