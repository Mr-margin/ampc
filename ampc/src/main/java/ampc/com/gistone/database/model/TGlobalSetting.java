package ampc.com.gistone.database.model;

import java.util.Date;

public class TGlobalSetting {
    private Long globalSettingId;

	private Long userid;

	private Integer rangeday;

	private Integer spinup;

	private Integer cores;

	private Long domainId;

	private Long esCouplingId;

	private Date addDate;

	private Date updateDate;

	private String beizhu1;

	private Long beizhu2;

	private Date beizhu3;

	private String beizhu4;

	public Long getGlobalSettingId() {
		return globalSettingId;
	}

	public void setGlobalSettingId(Long globalSettingId) {
		this.globalSettingId = globalSettingId;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Integer getRangeday() {
		return rangeday;
	}

	public void setRangeday(Integer rangeday) {
		this.rangeday = rangeday;
	}

	public Integer getSpinup() {
		return spinup;
	}

	public void setSpinup(Integer spinup) {
		this.spinup = spinup;
	}

	public Integer getCores() {
		return cores;
	}

	public void setCores(Integer cores) {
		this.cores = cores;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getEsCouplingId() {
		return esCouplingId;
	}

	public void setEsCouplingId(Long esCouplingId) {
		this.esCouplingId = esCouplingId;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getBeizhu1() {
		return beizhu1;
	}

	public void setBeizhu1(String beizhu1) {
		this.beizhu1 = beizhu1 == null ? null : beizhu1.trim();
	}

	public Long getBeizhu2() {
		return beizhu2;
	}

	public void setBeizhu2(Long beizhu2) {
		this.beizhu2 = beizhu2;
	}

	public Date getBeizhu3() {
		return beizhu3;
	}

	public void setBeizhu3(Date beizhu3) {
		this.beizhu3 = beizhu3;
	}

	public String getBeizhu4() {
		return beizhu4;
	}

	public void setBeizhu4(String beizhu4) {
		this.beizhu4 = beizhu4 == null ? null : beizhu4.trim();
	}

}