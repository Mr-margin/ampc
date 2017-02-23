package ampc.com.gistone.database.model;

public class TMeasure {
    private Long measureId;

    private Long userId;

    private Long versionId;

    private Object mid;

    private Object measureName;

    private Object mDesc;

    private Object op;

    private Double intensity;

    private Object sectors;

    private Object content;

    private Object display;

    public Long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Object getMid() {
        return mid;
    }

    public void setMid(Object mid) {
        this.mid = mid;
    }

    public Object getMeasureName() {
        return measureName;
    }

    public void setMeasureName(Object measureName) {
        this.measureName = measureName;
    }

    public Object getmDesc() {
        return mDesc;
    }

    public void setmDesc(Object mDesc) {
        this.mDesc = mDesc;
    }

    public Object getOp() {
        return op;
    }

    public void setOp(Object op) {
        this.op = op;
    }

    public Double getIntensity() {
        return intensity;
    }

    public void setIntensity(Double intensity) {
        this.intensity = intensity;
    }

    public Object getSectors() {
        return sectors;
    }

    public void setSectors(Object sectors) {
        this.sectors = sectors;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getDisplay() {
        return display;
    }

    public void setDisplay(Object display) {
        this.display = display;
    }
}