package ampc.com.gistone.database.model;

import java.util.Date;

public class TEsNative {
    private Long esNativeId;

    private Object esNativeName;

    private Short esNativeYear;

    private Date uploadTime;

    private Object esComment;

    private Long esNativeRelationId;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;

    public Long getEsNativeId() {
        return esNativeId;
    }

    public void setEsNativeId(Long esNativeId) {
        this.esNativeId = esNativeId;
    }

    public Object getEsNativeName() {
        return esNativeName;
    }

    public void setEsNativeName(Object esNativeName) {
        this.esNativeName = esNativeName;
    }

    public Short getEsNativeYear() {
        return esNativeYear;
    }

    public void setEsNativeYear(Short esNativeYear) {
        this.esNativeYear = esNativeYear;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Object getEsComment() {
        return esComment;
    }

    public void setEsComment(Object esComment) {
        this.esComment = esComment;
    }

    public Long getEsNativeRelationId() {
        return esNativeRelationId;
    }

    public void setEsNativeRelationId(Long esNativeRelationId) {
        this.esNativeRelationId = esNativeRelationId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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