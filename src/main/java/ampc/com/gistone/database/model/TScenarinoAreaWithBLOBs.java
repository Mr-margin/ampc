package ampc.com.gistone.database.model;
/**
 * 区域实体类WithClob
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public class TScenarinoAreaWithBLOBs extends TScenarinoArea {
	//省级区域代码数
    private String provinceCodes;
    //市级区域代码数
    private String cityCodes;
    //区县区域代码数
    private String countyCodes;

    public String getProvinceCodes() {
        return provinceCodes;
    }

    public void setProvinceCodes(String provinceCodes) {
        this.provinceCodes = provinceCodes == null ? null : provinceCodes.trim();
    }

    public String getCityCodes() {
        return cityCodes;
    }

    public void setCityCodes(String cityCodes) {
        this.cityCodes = cityCodes == null ? null : cityCodes.trim();
    }

    public String getCountyCodes() {
        return countyCodes;
    }

    public void setCountyCodes(String countyCodes) {
        this.countyCodes = countyCodes == null ? null : countyCodes.trim();
    }
}