package ampc.com.gistone.database.model;

import java.util.Date;

public class TSystemAdmin {
    private Long systemAdminId;

    private String password;

    private String systemAdminEmail;

    private Object systemAdminName;

    private Object companyName;

    private String provinceCode;

    private String cityCode;

    private String countyCode;

    private Long systemAdminPhone;

    private Date addTime;

    public Long getSystemAdminId() {
        return systemAdminId;
    }

    public void setSystemAdminId(Long systemAdminId) {
        this.systemAdminId = systemAdminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSystemAdminEmail() {
        return systemAdminEmail;
    }

    public void setSystemAdminEmail(String systemAdminEmail) {
        this.systemAdminEmail = systemAdminEmail == null ? null : systemAdminEmail.trim();
    }

    public Object getSystemAdminName() {
        return systemAdminName;
    }

    public void setSystemAdminName(Object systemAdminName) {
        this.systemAdminName = systemAdminName;
    }

    public Object getCompanyName() {
        return companyName;
    }

    public void setCompanyName(Object companyName) {
        this.companyName = companyName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode == null ? null : provinceCode.trim();
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode == null ? null : countyCode.trim();
    }

    public Long getSystemAdminPhone() {
        return systemAdminPhone;
    }

    public void setSystemAdminPhone(Long systemAdminPhone) {
        this.systemAdminPhone = systemAdminPhone;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}