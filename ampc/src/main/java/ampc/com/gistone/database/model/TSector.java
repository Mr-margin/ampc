package ampc.com.gistone.database.model;

import java.util.Date;

public class TSector {
    private Long sectorId;

    private Long versionId;

    private Object sectorName;

    private Object l4s;

    private Date addTime;

    private Date updateTime;

    private Object measureIds;

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Object getSectorName() {
        return sectorName;
    }

    public void setSectorName(Object sectorName) {
        this.sectorName = sectorName;
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