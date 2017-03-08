package ampc.com.gistone.database.model;

import java.util.Date;

public class TPlanMeasure {
    private Long planMeasureId;

    private Long planId;

    private Long sectorId;

    private Long measureId;

    private Date addTime;

    private String isEffective;

    private Date deleteTime;

    private String implementationScope;

    private String reductionRatio;

    private String ratio;

    private String measureContent;

    public Long getPlanMeasureId() {
        return planMeasureId;
    }

    public void setPlanMeasureId(Long planMeasureId) {
        this.planMeasureId = planMeasureId;
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

    public String getImplementationScope() {
        return implementationScope;
    }

    public void setImplementationScope(String implementationScope) {
        this.implementationScope = implementationScope == null ? null : implementationScope.trim();
    }

    public String getReductionRatio() {
        return reductionRatio;
    }

    public void setReductionRatio(String reductionRatio) {
        this.reductionRatio = reductionRatio == null ? null : reductionRatio.trim();
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio == null ? null : ratio.trim();
    }

    public String getMeasureContent() {
        return measureContent;
    }

    public void setMeasureContent(String measureContent) {
        this.measureContent = measureContent == null ? null : measureContent.trim();
    }
}