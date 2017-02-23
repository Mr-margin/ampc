package ampc.com.gistone.database.model;

import java.util.Date;

public class TSectorTemplate {
    private Long sectorTemplateId;

    private Long versionId;

    private Object sectorTemplateName;

    private Object l4s;

    private Date addTime;

    private Date updateTime;

    private Object measureIds;

    public Long getSectorTemplateId() {
        return sectorTemplateId;
    }

    public void setSectorTemplateId(Long sectorTemplateId) {
        this.sectorTemplateId = sectorTemplateId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Object getSectorTemplateName() {
        return sectorTemplateName;
    }

    public void setSectorTemplateName(Object sectorTemplateName) {
        this.sectorTemplateName = sectorTemplateName;
    }

    public Object getL4s() {
        return l4s;
    }

    public void setL4s(Object l4s) {
        this.l4s = l4s;
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

    public Object getMeasureIds() {
        return measureIds;
    }

    public void setMeasureIds(Object measureIds) {
        this.measureIds = measureIds;
    }
}