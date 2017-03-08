package ampc.com.gistone.database.model;

public class TSectordocExcel {
    private Long sectordocId;

    private Object sectordocName;

    private String sectordocEtitle;

    private Object sectordocCtitle;

    private String sectordocType;

    private Object sectordocStype;

    private Object sectordocDoc;

    private Object sectordocDisname;

    public Long getSectordocId() {
        return sectordocId;
    }

    public void setSectordocId(Long sectordocId) {
        this.sectordocId = sectordocId;
    }

    public Object getSectordocName() {
        return sectordocName;
    }

    public void setSectordocName(Object sectordocName) {
        this.sectordocName = sectordocName;
    }

    public String getSectordocEtitle() {
        return sectordocEtitle;
    }

    public void setSectordocEtitle(String sectordocEtitle) {
        this.sectordocEtitle = sectordocEtitle == null ? null : sectordocEtitle.trim();
    }

    public Object getSectordocCtitle() {
        return sectordocCtitle;
    }

    public void setSectordocCtitle(Object sectordocCtitle) {
        this.sectordocCtitle = sectordocCtitle;
    }

    public String getSectordocType() {
        return sectordocType;
    }

    public void setSectordocType(String sectordocType) {
        this.sectordocType = sectordocType == null ? null : sectordocType.trim();
    }

    public Object getSectordocStype() {
        return sectordocStype;
    }

    public void setSectordocStype(Object sectordocStype) {
        this.sectordocStype = sectordocStype;
    }

    public Object getSectordocDoc() {
        return sectordocDoc;
    }

    public void setSectordocDoc(Object sectordocDoc) {
        this.sectordocDoc = sectordocDoc;
    }

    public Object getSectordocDisname() {
        return sectordocDisname;
    }

    public void setSectordocDisname(Object sectordocDisname) {
        this.sectordocDisname = sectordocDisname;
    }
}