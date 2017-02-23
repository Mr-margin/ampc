package ampc.com.gistone.database.model;

public class TScenarinoAreaWithBLOBs extends TScenarinoArea {
    private String provinceCodes;

    private String cityCodes;

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