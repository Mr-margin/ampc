package ampc.com.gistone.database.model;

import java.util.Date;

public class TModelScheduleMessage {
    private Long executeScheduleId;

    private Integer exeMessageIndex;

    private Date exeMessageTime;

    private String exeMessageTasksdate;

    private String exeMessageDesc;

    private Long exeScenarinoId;

    private String exeMessageType;

    private Integer exeMessageCode;

    private Date messageAddTime;

    private Date messsageUpdateTime;

    private String messageExpend1;

    private Long messageExpend2;

    private Date messageExpend3Time;

    private String messageExpend4;

    private String messageExpend5;

    private String messageExpend6;

    private String messageExpend7;

    private String messageExpend8;

    public Long getExecuteScheduleId() {
        return executeScheduleId;
    }

    public void setExecuteScheduleId(Long executeScheduleId) {
        this.executeScheduleId = executeScheduleId;
    }

    public Integer getExeMessageIndex() {
        return exeMessageIndex;
    }

    public void setExeMessageIndex(Integer exeMessageIndex) {
        this.exeMessageIndex = exeMessageIndex;
    }

    public Date getExeMessageTime() {
        return exeMessageTime;
    }

    public void setExeMessageTime(Date exeMessageTime) {
        this.exeMessageTime = exeMessageTime;
    }

    public String getExeMessageTasksdate() {
        return exeMessageTasksdate;
    }

    public void setExeMessageTasksdate(String exeMessageTasksdate) {
        this.exeMessageTasksdate = exeMessageTasksdate == null ? null : exeMessageTasksdate.trim();
    }

    public String getExeMessageDesc() {
        return exeMessageDesc;
    }

    public void setExeMessageDesc(String exeMessageDesc) {
        this.exeMessageDesc = exeMessageDesc == null ? null : exeMessageDesc.trim();
    }

    public Long getExeScenarinoId() {
        return exeScenarinoId;
    }

    public void setExeScenarinoId(Long exeScenarinoId) {
        this.exeScenarinoId = exeScenarinoId;
    }

    public String getExeMessageType() {
        return exeMessageType;
    }

    public void setExeMessageType(String exeMessageType) {
        this.exeMessageType = exeMessageType == null ? null : exeMessageType.trim();
    }

    public Integer getExeMessageCode() {
        return exeMessageCode;
    }

    public void setExeMessageCode(Integer exeMessageCode) {
        this.exeMessageCode = exeMessageCode;
    }

    public Date getMessageAddTime() {
        return messageAddTime;
    }

    public void setMessageAddTime(Date messageAddTime) {
        this.messageAddTime = messageAddTime;
    }

    public Date getMesssageUpdateTime() {
        return messsageUpdateTime;
    }

    public void setMesssageUpdateTime(Date messsageUpdateTime) {
        this.messsageUpdateTime = messsageUpdateTime;
    }

    public String getMessageExpend1() {
        return messageExpend1;
    }

    public void setMessageExpend1(String messageExpend1) {
        this.messageExpend1 = messageExpend1 == null ? null : messageExpend1.trim();
    }

    public Long getMessageExpend2() {
        return messageExpend2;
    }

    public void setMessageExpend2(Long messageExpend2) {
        this.messageExpend2 = messageExpend2;
    }

    public Date getMessageExpend3Time() {
        return messageExpend3Time;
    }

    public void setMessageExpend3Time(Date messageExpend3Time) {
        this.messageExpend3Time = messageExpend3Time;
    }

    public String getMessageExpend4() {
        return messageExpend4;
    }

    public void setMessageExpend4(String messageExpend4) {
        this.messageExpend4 = messageExpend4 == null ? null : messageExpend4.trim();
    }

    public String getMessageExpend5() {
        return messageExpend5;
    }

    public void setMessageExpend5(String messageExpend5) {
        this.messageExpend5 = messageExpend5 == null ? null : messageExpend5.trim();
    }

    public String getMessageExpend6() {
        return messageExpend6;
    }

    public void setMessageExpend6(String messageExpend6) {
        this.messageExpend6 = messageExpend6 == null ? null : messageExpend6.trim();
    }

    public String getMessageExpend7() {
        return messageExpend7;
    }

    public void setMessageExpend7(String messageExpend7) {
        this.messageExpend7 = messageExpend7 == null ? null : messageExpend7.trim();
    }

    public String getMessageExpend8() {
        return messageExpend8;
    }

    public void setMessageExpend8(String messageExpend8) {
        this.messageExpend8 = messageExpend8 == null ? null : messageExpend8.trim();
    }
}