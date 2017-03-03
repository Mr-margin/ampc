package ampc.com.gistone.database.model;

import java.util.Date;

public class TSectorExcel {
    private Long sectorExcelId;

    private Long versionExcelId;

    private Object sectorExcelName;

    private Object sectorExcelL4s;

    private Date sectorExcelAddTime;

    private Date sectorExcelUpdateTime;

    private Object sectorExcelMeasureIds;

    private Long userId;

    public Long getSectorExcelId() {
        return sectorExcelId;
    }

    public void setSectorExcelId(Long sectorExcelId) {
        this.sectorExcelId = sectorExcelId;
    }

    public Long getVersionExcelId() {
        return versionExcelId;
    }

    public void setVersionExcelId(Long versionExcelId) {
        this.versionExcelId = versionExcelId;
    }

    public Object getSectorExcelName() {
        return sectorExcelName;
    }

    public void setSectorExcelName(Object sectorExcelName) {
        this.sectorExcelName = sectorExcelName;
    }

    public Object getSectorExcelL4s() {
        return sectorExcelL4s;
    }

    public void setSectorExcelL4s(Object sectorExcelL4s) {
        this.sectorExcelL4s = sectorExcelL4s;
    }

    public Date getSectorExcelAddTime() {
        return sectorExcelAddTime;
    }

    public void setSectorExcelAddTime(Date sectorExcelAddTime) {
        this.sectorExcelAddTime = sectorExcelAddTime;
    }

    public Date getSectorExcelUpdateTime() {
        return sectorExcelUpdateTime;
    }

    public void setSectorExcelUpdateTime(Date sectorExcelUpdateTime) {
        this.sectorExcelUpdateTime = sectorExcelUpdateTime;
    }

    public Object getSectorExcelMeasureIds() {
        return sectorExcelMeasureIds;
    }

    public void setSectorExcelMeasureIds(Object sectorExcelMeasureIds) {
        this.sectorExcelMeasureIds = sectorExcelMeasureIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}