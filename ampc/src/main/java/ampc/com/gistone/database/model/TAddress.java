package ampc.com.gistone.database.model;

/**
 * 地址实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月28日
 */
public class TAddress {
	//地址ID
    private Integer addressId;
    //地址名称
    private Object addressName;
    //地址编码
    private Integer addressCode;
    //地区级别 1省 2市 3区
    private String addressLevel;
    //省级编码
    private String provinceCode;
    //市级编码
    private String cityCode;
    //区县级编码
    private String countyCode;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Object getAddressName() {
        return addressName;
    }

    public void setAddressName(Object addressName) {
        this.addressName = addressName;
    }

    public Integer getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(Integer addressCode) {
        this.addressCode = addressCode;
    }

    public String getAddressLevel() {
        return addressLevel;
    }

    public void setAddressLevel(String addressLevel) {
        this.addressLevel = addressLevel == null ? null : addressLevel.trim();
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
}