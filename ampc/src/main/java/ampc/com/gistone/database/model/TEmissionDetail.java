package ampc.com.gistone.database.model;

import java.math.BigDecimal;
import java.util.Date;

public class TEmissionDetail {
    private Long emissionId;

    private Date emissionDate;

    private String code;

    private String codelevel;

    private String emissionType;

    private Long scenarinoId;

    private BigDecimal pm25;

    private BigDecimal pm10;

    private BigDecimal so2;

    private BigDecimal nox;

    private BigDecimal voc;

    private BigDecimal co;

    private BigDecimal nh3;

    private BigDecimal bc;

    private BigDecimal oc;

    private BigDecimal pmfine;

    private BigDecimal pmc;

    public Long getEmissionId() {
        return emissionId;
    }

    public void setEmissionId(Long emissionId) {
        this.emissionId = emissionId;
    }

    public Date getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(Date emissionDate) {
        this.emissionDate = emissionDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCodelevel() {
        return codelevel;
    }

    public void setCodelevel(String codelevel) {
        this.codelevel = codelevel == null ? null : codelevel.trim();
    }

    public String getEmissionType() {
        return emissionType;
    }

    public void setEmissionType(String emissionType) {
        this.emissionType = emissionType == null ? null : emissionType.trim();
    }

    public Long getScenarinoId() {
        return scenarinoId;
    }

    public void setScenarinoId(Long scenarinoId) {
        this.scenarinoId = scenarinoId;
    }

    public BigDecimal getPm25() {
        return pm25;
    }

    public void setPm25(BigDecimal pm25) {
        this.pm25 = pm25;
    }

    public BigDecimal getPm10() {
        return pm10;
    }

    public void setPm10(BigDecimal pm10) {
        this.pm10 = pm10;
    }

    public BigDecimal getSo2() {
        return so2;
    }

    public void setSo2(BigDecimal so2) {
        this.so2 = so2;
    }

    public BigDecimal getNox() {
        return nox;
    }

    public void setNox(BigDecimal nox) {
        this.nox = nox;
    }

    public BigDecimal getVoc() {
        return voc;
    }

    public void setVoc(BigDecimal voc) {
        this.voc = voc;
    }

    public BigDecimal getCo() {
        return co;
    }

    public void setCo(BigDecimal co) {
        this.co = co;
    }

    public BigDecimal getNh3() {
        return nh3;
    }

    public void setNh3(BigDecimal nh3) {
        this.nh3 = nh3;
    }

    public BigDecimal getBc() {
        return bc;
    }

    public void setBc(BigDecimal bc) {
        this.bc = bc;
    }

    public BigDecimal getOc() {
        return oc;
    }

    public void setOc(BigDecimal oc) {
        this.oc = oc;
    }

    public BigDecimal getPmfine() {
        return pmfine;
    }

    public void setPmfine(BigDecimal pmfine) {
        this.pmfine = pmfine;
    }

    public BigDecimal getPmc() {
        return pmc;
    }

    public void setPmc(BigDecimal pmc) {
        this.pmc = pmc;
    }
}