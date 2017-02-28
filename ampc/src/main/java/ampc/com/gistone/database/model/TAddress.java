package ampc.com.gistone.database.model;

public class TAddress {
    private Integer addressId;

    private Object addressName;

    private Integer addressCode;

    private String addressLevel;

    private String provinceCode;

    private String cityCode;

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