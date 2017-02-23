package ampc.com.gistone.database.model;

import java.util.Date;

public class TPlanMeasureReuse {
    private Long id;

    private Long planId;

    private Long sectorId;

    private Long measureId;

    private Date addTime;

    private String isEffective;

    private Date deleteTime;

    private String measureContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
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

    public String getMeasureContent() {
        return measureContent;
    }

    public void setMeasureContent(String measureContent) {
        this.measureContent = measureContent == null ? null : measureContent.trim();
    }
}