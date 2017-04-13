package ampc.com.gistone.database.model;

/**
 * 用户实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年4月13日
 */
public class TUser {
	//用户ID	
    private Long userId;
    //用户昵称
    private Object userName;
    //用户密码
    private Object password;
    //用户所在的省级code
    private Integer provinceCode;
    //用户所在的市级code
    private Integer cityCode;
    //用户所在的县级code    
    private Integer countyCode;
    //极限预报天数    
    private Integer predictionTime;


	public Integer getPredictionTime() {
		return predictionTime;
	}

	public void setPredictionTime(Integer predictionTime) {
		this.predictionTime = predictionTime;
	}

	public Integer getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(Integer countyCode) {
		this.countyCode = countyCode;
	}

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

	public Integer getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(Integer provinceCode) {
		this.provinceCode = provinceCode;
	}

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}


}