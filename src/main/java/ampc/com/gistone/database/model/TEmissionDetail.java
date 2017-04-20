package ampc.com.gistone.database.model;

import java.util.Date;

public class TEmissionDetail {
    private Long emissionId;

    private Date emissionDate;

    private String code;

    private String codelevel;

    private String emissionType;

    private Long scenarinoId;

    private String pm25;

    private String pm10;

    private String so2;

    private String nox;

    private String voc;

    private String co;

    private String nh3;

    private String bc;

    private String oc;

    private String pmfine;

    private String pmc;

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

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25 == null ? null : pm25.trim();
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10 == null ? null : pm10.trim();
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2 == null ? null : so2.trim();
    }

    public String getNox() {
        return nox;
    }

    public void setNox(String nox) {
        this.nox = nox == null ? null : nox.trim();
    }

    public String getVoc() {
        return voc;
    }

    public void setVoc(String voc) {
        this.voc = voc == null ? null : voc.trim();
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co == null ? null : co.trim();
    }

    public String getNh3() {
        return nh3;
    }

    public void setNh3(String nh3) {
        this.nh3 = nh3 == null ? null : nh3.trim();
    }

    public String getBc() {
        return bc;
    }

    public void setBc(String bc) {
        this.bc = bc == null ? null : bc.trim();
    }

    public String getOc() {
        return oc;
    }

    public void setOc(String oc) {
        this.oc = oc == null ? null : oc.trim();
    }

    public String getPmfine() {
        return pmfine;
    }

    public void setPmfine(String pmfine) {
        this.pmfine = pmfine == null ? null : pmfine.trim();
    }

    public String getPmc() {
        return pmc;
    }

    public void setPmc(String pmc) {
        this.pmc = pmc == null ? null : pmc.trim();
    }
}