package ampc.com.gistone.database.model;

import java.util.Date;

public class TEsCoupling {
    private Long esCouplingId;

    private Object esCouplingName;

    private Object esCouplingDesc;

    private Short esCouplingYear;

    private Long esCouplingNationId;

    private Long esCouplingNativeId;

    private Long esCoupingRelationId;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String isEffective;

    private Date deleteTime;

    private String historyCoupling;

    public Long getEsCouplingId() {
        return esCouplingId;
    }

    public void setEsCouplingId(Long esCouplingId) {
        this.esCouplingId = esCouplingId;
    }

    public Object getEsCouplingName() {
        return esCouplingName;
    }

    public void setEsCouplingName(Object esCouplingName) {
        this.esCouplingName = esCouplingName;
    }

    public Object getEsCouplingDesc() {
        return esCouplingDesc;
    }

    public void setEsCouplingDesc(Object esCouplingDesc) {
        this.esCouplingDesc = esCouplingDesc;
    }

    public Short getEsCouplingYear() {
        return esCouplingYear;
    }

    public void setEsCouplingYear(Short esCouplingYear) {
        this.esCouplingYear = esCouplingYear;
    }

    public Long getEsCouplingNationId() {
        return esCouplingNationId;
    }

    public void setEsCouplingNationId(Long esCouplingNationId) {
        this.esCouplingNationId = esCouplingNationId;
    }

    public Long getEsCouplingNativeId() {
        return esCouplingNativeId;
    }

    public void setEsCouplingNativeId(Long esCouplingNativeId) {
        this.esCouplingNativeId = esCouplingNativeId;
    }

    public Long getEsCoupingRelationId() {
        return esCoupingRelationId;
    }

    public void setEsCoupingRelationId(Long esCoupingRelationId) {
        this.esCoupingRelationId = esCoupingRelationId;
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

    public String getHistoryCoupling() {
        return historyCoupling;
    }

    public void setHistoryCoupling(String historyCoupling) {
        this.historyCoupling = historyCoupling == null ? null : historyCoupling.trim();
    }
}