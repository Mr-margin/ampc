package ampc.com.gistone.database.model;

import java.util.Date;

public class TEmissionDetail {
    private Long emissionId;

    private Date emissionDate;

    private String code;

    private String emissionDetails;

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

    public String getEmissionDetails() {
        return emissionDetails;
    }

    public void setEmissionDetails(String emissionDetails) {
        this.emissionDetails = emissionDetails == null ? null : emissionDetails.trim();
    }
}