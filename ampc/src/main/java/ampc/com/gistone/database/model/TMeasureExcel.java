package ampc.com.gistone.database.model;

import java.math.BigDecimal;

public class TMeasureExcel {
    private Long measureExcelId;

    private Long measureExcelVersion;

    private String measureExcelName;

    private Long measureExcelDebug;

    private Object measureExcelDisplay;

    private Object measureExcelType;

    private Long measureExcelLevel;

    private String measureExcelL4s;

    private String measureExcelOp;

    private BigDecimal measureExcelA;

    private BigDecimal measureExcelA1;

    private BigDecimal measureExcelIntensity;

    private BigDecimal measureExcelIntensity1;

    private BigDecimal measureExcelAsh;

    private BigDecimal measureExcelSulfer;

    private String measureExcelSv;

    private Long userId;

    public Long getMeasureExcelId() {
        return measureExcelId;
    }

    public void setMeasureExcelId(Long measureExcelId) {
        this.measureExcelId = measureExcelId;
    }

    public Long getMeasureExcelVersion() {
        return measureExcelVersion;
    }

    public void setMeasureExcelVersion(Long measureExcelVersion) {
        this.measureExcelVersion = measureExcelVersion;
    }

    public String getMeasureExcelName() {
        return measureExcelName;
    }

    public void setMeasureExcelName(String measureExcelName) {
        this.measureExcelName = measureExcelName == null ? null : measureExcelName.trim();
    }

    public Long getMeasureExcelDebug() {
        return measureExcelDebug;
    }

    public void setMeasureExcelDebug(Long measureExcelDebug) {
        this.measureExcelDebug = measureExcelDebug;
    }

    public Object getMeasureExcelDisplay() {
        return measureExcelDisplay;
    }

    public void setMeasureExcelDisplay(Object measureExcelDisplay) {
        this.measureExcelDisplay = measureExcelDisplay;
    }

    public Object getMeasureExcelType() {
        return measureExcelType;
    }

    public void setMeasureExcelType(Object measureExcelType) {
        this.measureExcelType = measureExcelType;
    }

    public Long getMeasureExcelLevel() {
        return measureExcelLevel;
    }

    public void setMeasureExcelLevel(Long measureExcelLevel) {
        this.measureExcelLevel = measureExcelLevel;
    }

    public String getMeasureExcelL4s() {
        return measureExcelL4s;
    }

    public void setMeasureExcelL4s(String measureExcelL4s) {
        this.measureExcelL4s = measureExcelL4s == null ? null : measureExcelL4s.trim();
    }

    public String getMeasureExcelOp() {
        return measureExcelOp;
    }

    public void setMeasureExcelOp(String measureExcelOp) {
        this.measureExcelOp = measureExcelOp == null ? null : measureExcelOp.trim();
    }

    public BigDecimal getMeasureExcelA() {
        return measureExcelA;
    }

    public void setMeasureExcelA(BigDecimal measureExcelA) {
        this.measureExcelA = measureExcelA;
    }

    public BigDecimal getMeasureExcelA1() {
        return measureExcelA1;
    }

    public void setMeasureExcelA1(BigDecimal measureExcelA1) {
        this.measureExcelA1 = measureExcelA1;
    }

    public BigDecimal getMeasureExcelIntensity() {
        return measureExcelIntensity;
    }

    public void setMeasureExcelIntensity(BigDecimal measureExcelIntensity) {
        this.measureExcelIntensity = measureExcelIntensity;
    }

    public BigDecimal getMeasureExcelIntensity1() {
        return measureExcelIntensity1;
    }

    public void setMeasureExcelIntensity1(BigDecimal measureExcelIntensity1) {
        this.measureExcelIntensity1 = measureExcelIntensity1;
    }

    public BigDecimal getMeasureExcelAsh() {
        return measureExcelAsh;
    }

    public void setMeasureExcelAsh(BigDecimal measureExcelAsh) {
        this.measureExcelAsh = measureExcelAsh;
    }

    public BigDecimal getMeasureExcelSulfer() {
        return measureExcelSulfer;
    }

    public void setMeasureExcelSulfer(BigDecimal measureExcelSulfer) {
        this.measureExcelSulfer = measureExcelSulfer;
    }

    public String getMeasureExcelSv() {
        return measureExcelSv;
    }

    public void setMeasureExcelSv(String measureExcelSv) {
        this.measureExcelSv = measureExcelSv == null ? null : measureExcelSv.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}