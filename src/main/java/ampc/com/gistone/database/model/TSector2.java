package ampc.com.gistone.database.model;

import java.util.Date;

public class TSector2 {
    private Long id;

    private Long versionId;

    private Object name;

    private Object l4s;

    private Date addTime;

    private Date updateTime;

    private Object measureIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
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