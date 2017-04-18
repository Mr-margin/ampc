package ampc.com.gistone.database.model;

import java.util.Date;

public class TUserMsg {
    private Long userMsgId;

    private Long userId;

    private Object msgSender;

    private Object msgContent;

    private Object msgTitle;

    private Date msgTime;

    private Long msgType;

    private Long isRead;

    private Long senderId;

    public Long getUserMsgId() {
        return userMsgId;
    }

    public void setUserMsgId(Long userMsgId) {
        this.userMsgId = userMsgId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Object getMsgSender() {
        return msgSender;
    }

    public void setMsgSender(Object msgSender) {
        this.msgSender = msgSender;
    }

    public Object getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(Object msgContent) {
        this.msgContent = msgContent;
    }

    public Object getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(Object msgTitle) {
        this.msgTitle = msgTitle;
    }

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }

    public Long getMsgType() {
        return msgType;
    }

    public void setMsgType(Long msgType) {
        this.msgType = msgType;
    }

    public Long getIsRead() {
        return isRead;
    }

    public void setIsRead(Long isRead) {
        this.isRead = isRead;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}