package ampc.com.gistone.database.model;

public class TUser {
    private Long userId;

	private Object userName;

	private Object password;

	private Long provinceCode;

	private Long cityCode;

	private Long countyCode;

	private String userEmail;

	private Object companyName;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Object getUserName() {
		return userName;
	}

	public void setUserName(Object userName) {
		this.userName = userName;
	}

	public Object getPassword() {
		return password;
	}

	public void setPassword(Object password) {
		this.password = password;
	}

	public Long getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Long provinceCode) {
		this.provinceCode = provinceCode;
	}

	public Long getCityCode() {
		return cityCode;
	}

	public void setCityCode(Long cityCode) {
		this.cityCode = cityCode;
	}

	public Long getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(Long countyCode) {
		this.countyCode = countyCode;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail == null ? null : userEmail.trim();
	}

	public Object getCompanyName() {
		return companyName;
	}

	public void setCompanyName(Object companyName) {
		this.companyName = companyName;
	}
}