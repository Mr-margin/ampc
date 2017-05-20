package ampc.com.gistone.database.model;

import java.util.Date;

public class TEsNation {
    private Long esNationId;

    private Object esNationName;

    private Short esNationYear;

    private Date publishTime;

    private Long esNationRelationId;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;
    
    private String reamrk;

    public Long getEsNationId() {
        return esNationId;
    }

    public String getReamrk() {
		return reamrk;
	}

	public void setReamrk(String reamrk) {
		this.reamrk = reamrk;
	}

	public void setEsNationId(Long esNationId) {
        this.esNationId = esNationId;
    }

    public Object getEsNationName() {
        return esNationName;
    }

    public void setEsNationName(Object esNationName) {
        this.esNationName = esNationName;
    }

    public Short getEsNationYear() {
        return esNationYear;
    }

    public void setEsNationYear(Short esNationYear) {
        this.esNationYear = esNationYear;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Long getEsNationRelationId() {
        return esNationRelationId;
    }

    public void setEsNationRelationId(Long esNationRelationId) {
        this.esNationRelationId = esNationRelationId;
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