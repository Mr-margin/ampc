package ampc.com.gistone.database.model;

import java.util.Date;

public class TCoresTimes {
    private Long coresTimeId;

    private Long coresDomainId;

    private Long userId;

    private Integer coresMissionType;

    private Integer coresScenarinoType;

    private Integer cores;

    private Date sendTime;

    private Date returnTime;

    private Double avgTime;

    private String beuzhu1;

    private Long beuzhu2;

    private Date beizhu3;

    private Integer beizhu4;

    private String beizhu5;

    public Long getCoresTimeId() {
        return coresTimeId;
    }

    public void setCoresTimeId(Long coresTimeId) {
        this.coresTimeId = coresTimeId;
    }

    public Long getCoresDomainId() {
        return coresDomainId;
    }

    public void setCoresDomainId(Long coresDomainId) {
        this.coresDomainId = coresDomainId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCoresMissionType() {
        return coresMissionType;
    }

    public void setCoresMissionType(Integer coresMissionType) {
        this.coresMissionType = coresMissionType;
    }

    public Integer getCoresScenarinoType() {
        return coresScenarinoType;
    }

    public void setCoresScenarinoType(Integer coresScenarinoType) {
        this.coresScenarinoType = coresScenarinoType;
    }

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(Double avgTime) {
        this.avgTime = avgTime;
    }

    public String getBeuzhu1() {
        return beuzhu1;
    }

    public void setBeuzhu1(String beuzhu1) {
        this.beuzhu1 = beuzhu1 == null ? null : beuzhu1.trim();
    }

    public Long getBeuzhu2() {
        return beuzhu2;
    }

    public void setBeuzhu2(Long beuzhu2) {
        this.beuzhu2 = beuzhu2;
    }

    public Date getBeizhu3() {
        return beizhu3;
    }

    public void setBeizhu3(Date beizhu3) {
        this.beizhu3 = beizhu3;
    }

    public Integer getBeizhu4() {
        return beizhu4;
    }

    public void setBeizhu4(Integer beizhu4) {
        this.beizhu4 = beizhu4;
    }

    public String getBeizhu5() {
        return beizhu5;
    }

    public void setBeizhu5(String beizhu5) {
        this.beizhu5 = beizhu5 == null ? null : beizhu5.trim();
    }
}