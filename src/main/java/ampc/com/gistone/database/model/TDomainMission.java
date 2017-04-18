package ampc.com.gistone.database.model;

import java.util.Date;

public class TDomainMission {
    private Long domainId;

    private Object domainName;

    private Date addTime;

    private Date updateTime;

    private Long userId;

    private String idEffective;

    private Date deleteTime;

    private String domainRange;

    private Object domainDoc;

    private String version;

    private Object createStatus;

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Object getDomainName() {
        return domainName;
    }

    public void setDomainName(Object domainName) {
        this.domainName = domainName;
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

    public String getIdEffective() {
        return idEffective;
    }

    public void setIdEffective(String idEffective) {
        this.idEffective = idEffective == null ? null : idEffective.trim();
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getDomainRange() {
        return domainRange;
    }

    public void setDomainRange(String domainRange) {
        this.domainRange = domainRange == null ? null : domainRange.trim();
    }

    public Object getDomainDoc() {
        return domainDoc;
    }

    public void setDomainDoc(Object domainDoc) {
        this.domainDoc = domainDoc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Object getCreateStatus() {
        return createStatus;
    }

    public void setCreateStatus(Object createStatus) {
        this.createStatus = createStatus;
    }
}