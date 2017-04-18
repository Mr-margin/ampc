package ampc.com.gistone.database.model;

public class TDomainMissionWithBLOBs extends TDomainMission {
    private String domainCode;

    private String domainInfo;

    private String common;

    private String cmap;

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode == null ? null : domainCode.trim();
    }

    public String getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(String domainInfo) {
        this.domainInfo = domainInfo == null ? null : domainInfo.trim();
    }

    public String getCommon() {
        return common;
    }

    public void setCommon(String common) {
        this.common = common == null ? null : common.trim();
    }

    public String getCmap() {
        return cmap;
    }

    public void setCmap(String cmap) {
        this.cmap = cmap == null ? null : cmap.trim();
    }
}