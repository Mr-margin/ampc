package ampc.com.gistone.database.model;

import java.util.Date;

public class TEsNativeTp {
    private Long esNativeTpId;

    private String esNativeTpName;

    private Short esNativeTpYear;

    private Date esUploadTpTime;

    private String esComment;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;

    private String esVersion;
    
    private String isUpload;

    public String getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(String isUpload) {
		this.isUpload = isUpload;
	}

	public Long getEsNativeTpId() {
        return esNativeTpId;
    }

    public void setEsNativeTpId(Long esNativeTpId) {
        this.esNativeTpId = esNativeTpId;
    }

    public String getEsNativeTpName() {
        return esNativeTpName;
    }

    public void setEsNativeTpName(String esNativeTpName) {
        this.esNativeTpName = esNativeTpName == null ? null : esNativeTpName.trim();
    }

    public Short getEsNativeTpYear() {
        return esNativeTpYear;
    }

    public void setEsNativeTpYear(Short esNativeTpYear) {
        this.esNativeTpYear = esNativeTpYear;
    }

    public Date getEsUploadTpTime() {
        return esUploadTpTime;
    }

    public void setEsUploadTpTime(Date esUploadTpTime) {
        this.esUploadTpTime = esUploadTpTime;
    }

    public String getEsComment() {
        return esComment;
    }

    public void setEsComment(String esComment) {
        this.esComment = esComment == null ? null : esComment.trim();
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

    public String getEsVersion() {
        return esVersion;
    }

    public void setEsVersion(String esVersion) {
        this.esVersion = esVersion == null ? null : esVersion.trim();
    }
}