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

    private String stopStatus;

    private Date sendTime;

    private Date endTime;

    private String startModelResult;

    private String stopModelResult;

    private String contunueStatus;

    private String pauseStatus;

    private Long expand1;

    private Date expand2;

    private String expand3;

    private String expand4;

    private String expand5;

    private Date updateTime;

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

    public String getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(String stopStatus) {
        this.stopStatus = stopStatus == null ? null : stopStatus.trim();
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStartModelResult() {
        return startModelResult;
    }

    public void setStartModelResult(String startModelResult) {
        this.startModelResult = startModelResult == null ? null : startModelResult.trim();
    }

    public String getStopModelResult() {
        return stopModelResult;
    }

    public void setStopModelResult(String stopModelResult) {
        this.stopModelResult = stopModelResult == null ? null : stopModelResult.trim();
    }

    public String getContunueStatus() {
        return contunueStatus;
    }

    public void setContunueStatus(String contunueStatus) {
        this.contunueStatus = contunueStatus == null ? null : contunueStatus.trim();
    }

    public String getPauseStatus() {
        return pauseStatus;
    }

    public void setPauseStatus(String pauseStatus) {
        this.pauseStatus = pauseStatus == null ? null : pauseStatus.trim();
    }

    public Long getExpand1() {
        return expand1;
    }

    public void setExpand1(Long expand1) {
        this.expand1 = expand1;
    }

    public Date getExpand2() {
        return expand2;
    }

    public void setExpand2(Date expand2) {
        this.expand2 = expand2;
    }

    public String getExpand3() {
        return expand3;
    }

    public void setExpand3(String expand3) {
        this.expand3 = expand3 == null ? null : expand3.trim();
    }

    public String getExpand4() {
        return expand4;
    }

    public void setExpand4(String expand4) {
        this.expand4 = expand4 == null ? null : expand4.trim();
    }

    public String getExpand5() {
        return expand5;
    }

    public void setExpand5(String expand5) {
        this.expand5 = expand5 == null ? null : expand5.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	@Override
	public String toString() {
		return "TTasksStatus [tasksId=" + tasksId + ", tasksScenarinoId="
				+ tasksScenarinoId + ", errorStatus=" + errorStatus
				+ ", scenarinoStartDate=" + scenarinoStartDate
				+ ", scenarinoEndDate=" + scenarinoEndDate + ", rangeDay="
				+ rangeDay + ", stepindex=" + stepindex + ", tasksEndDate="
				+ tasksEndDate + ", sourceid=" + sourceid + ", calctype="
				+ calctype + ", psal=" + psal + ", ssal=" + ssal
				+ ", meiccityconfig=" + meiccityconfig + ", beizhu=" + beizhu
				+ ", beizhu2=" + beizhu2 + ", beizhu3=" + beizhu3
				+ ", stopStatus=" + stopStatus + ", sendTime=" + sendTime
				+ ", endTime=" + endTime + ", startModelResult="
				+ startModelResult + ", stopModelResult=" + stopModelResult
				+ ", contunueStatus=" + contunueStatus + ", pauseStatus="
				+ pauseStatus + ", expand1=" + expand1 + ", expand2=" + expand2
				+ ", expand3=" + expand3 + ", expand4=" + expand4
				+ ", expand5=" + expand5 + ", updateTime=" + updateTime + "]";
	}
    
}