package ampc.com.gistone.database.model;

import java.util.Date;

public class TMessageLog {
    private Long messageLogId;

    private String messageUuid;

    private Date messageTime;

    private String messageType;

    private Long scenarinoId;

    private Integer messageIndex;

    private String tasksEndDate;

    private String resultDesc;

    private String resultCode;

    private Date addTime;

    private Long userId;

    private Long domainId;

    private String ungribPathDate;

    private String ungribFnl;

    private String ungribGfs;

    private String fnlDesc;

    private String gfsDesc;

    private String expand1;

    private String expand2;

    private String expand3;

    private String expand4;

    private String expand5;

    public Long getMessageLogId() {
        return messageLogId;
    }

    public void setMessageLogId(Long messageLogId) {
        this.messageLogId = messageLogId;
    }

    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid == null ? null : messageUuid.trim();
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType == null ? null : messageType.trim();
    }

    public Long getScenarinoId() {
        return scenarinoId;
    }

    public void setScenarinoId(Long scenarinoId) {
        this.scenarinoId = scenarinoId;
    }

    public Integer getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(Integer messageIndex) {
        this.messageIndex = messageIndex;
    }

    public String getTasksEndDate() {
        return tasksEndDate;
    }

    public void setTasksEndDate(String tasksEndDate) {
        this.tasksEndDate = tasksEndDate == null ? null : tasksEndDate.trim();
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc == null ? null : resultDesc.trim();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode == null ? null : resultCode.trim();
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

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getUngribPathDate() {
        return ungribPathDate;
    }

    public void setUngribPathDate(String ungribPathDate) {
        this.ungribPathDate = ungribPathDate == null ? null : ungribPathDate.trim();
    }

    public String getUngribFnl() {
        return ungribFnl;
    }

    public void setUngribFnl(String ungribFnl) {
        this.ungribFnl = ungribFnl == null ? null : ungribFnl.trim();
    }

    public String getUngribGfs() {
        return ungribGfs;
    }

    public void setUngribGfs(String ungribGfs) {
        this.ungribGfs = ungribGfs == null ? null : ungribGfs.trim();
    }

    public String getFnlDesc() {
        return fnlDesc;
    }

    public void setFnlDesc(String fnlDesc) {
        this.fnlDesc = fnlDesc == null ? null : fnlDesc.trim();
    }

    public String getGfsDesc() {
        return gfsDesc;
    }

    public void setGfsDesc(String gfsDesc) {
        this.gfsDesc = gfsDesc == null ? null : gfsDesc.trim();
    }

    public String getExpand1() {
        return expand1;
    }

    public void setExpand1(String expand1) {
        this.expand1 = expand1 == null ? null : expand1.trim();
    }

    public String getExpand2() {
        return expand2;
    }

    public void setExpand2(String expand2) {
        this.expand2 = expand2 == null ? null : expand2.trim();
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
}