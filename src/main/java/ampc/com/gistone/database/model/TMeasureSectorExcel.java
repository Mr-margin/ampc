package ampc.com.gistone.database.model;

import java.util.Date;

public class TMeasureSectorExcel {
    private Long msExcelId;

    private Long userId;

    private String msExcelVersionId;

    private Long mid;

    private Object msExcelName;

    private Object msExcelDesc;

    private Object msExcelOp;

    private String msExcelIntensity;

    private Object sectorsname;

    private Object content;

    private Object msExcelDisplay;

    private Long debugModel;

    private Object msExcelType;

    private String msExcelLevel;

    private String msExcelA;

    private String msExcelA1;

    private String msExcelIntensity1;

    private String msExcelAsh;

    private String msExcelSulfer;

    private String msExcelSv;

    private Long sid;

    private String colorcode;

    private Object colorname;

    private Date addTime;

    private Date deleteTime;

    private String isEffective;

    public Long getMsExcelId() {
        return msExcelId;
    }

    public void setMsExcelId(Long msExcelId) {
        this.msExcelId = msExcelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMsExcelVersionId() {
        return msExcelVersionId;
    }

    public void setMsExcelVersionId(String msExcelVersionId) {
        this.msExcelVersionId = msExcelVersionId == null ? null : msExcelVersionId.trim();
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Object getMsExcelName() {
        return msExcelName;
    }

    public void setMsExcelName(Object msExcelName) {
        this.msExcelName = msExcelName;
    }

    public Object getMsExcelDesc() {
        return msExcelDesc;
    }

    public void setMsExcelDesc(Object msExcelDesc) {
        this.msExcelDesc = msExcelDesc;
    }

    public Object getMsExcelOp() {
        return msExcelOp;
    }

    public void setMsExcelOp(Object msExcelOp) {
        this.msExcelOp = msExcelOp;
    }

    public String getMsExcelIntensity() {
        return msExcelIntensity;
    }

    public void setMsExcelIntensity(String msExcelIntensity) {
        this.msExcelIntensity = msExcelIntensity == null ? null : msExcelIntensity.trim();
    }

    public Object getSectorsname() {
        return sectorsname;
    }

    public void setSectorsname(Object sectorsname) {
        this.sectorsname = sectorsname;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getMsExcelDisplay() {
        return msExcelDisplay;
    }

    public void setMsExcelDisplay(Object msExcelDisplay) {
        this.msExcelDisplay = msExcelDisplay;
    }

    public Long getDebugModel() {
        return debugModel;
    }

    public void setDebugModel(Long debugModel) {
        this.debugModel = debugModel;
    }

    public Object getMsExcelType() {
        return msExcelType;
    }

    public void setMsExcelType(Object msExcelType) {
        this.msExcelType = msExcelType;
    }

    public String getMsExcelLevel() {
        return msExcelLevel;
    }

    public void setMsExcelLevel(String msExcelLevel) {
        this.msExcelLevel = msExcelLevel == null ? null : msExcelLevel.trim();
    }

    public String getMsExcelA() {
        return msExcelA;
    }

    public void setMsExcelA(String msExcelA) {
        this.msExcelA = msExcelA == null ? null : msExcelA.trim();
    }

    public String getMsExcelA1() {
        return msExcelA1;
    }

    public void setMsExcelA1(String msExcelA1) {
        this.msExcelA1 = msExcelA1 == null ? null : msExcelA1.trim();
    }

    public String getMsExcelIntensity1() {
        return msExcelIntensity1;
    }

    public void setMsExcelIntensity1(String msExcelIntensity1) {
        this.msExcelIntensity1 = msExcelIntensity1 == null ? null : msExcelIntensity1.trim();
    }

    public String getMsExcelAsh() {
        return msExcelAsh;
    }

    public void setMsExcelAsh(String msExcelAsh) {
        this.msExcelAsh = msExcelAsh == null ? null : msExcelAsh.trim();
    }

    public String getMsExcelSulfer() {
        return msExcelSulfer;
    }

    public void setMsExcelSulfer(String msExcelSulfer) {
        this.msExcelSulfer = msExcelSulfer == null ? null : msExcelSulfer.trim();
    }

    public String getMsExcelSv() {
        return msExcelSv;
    }

    public void setMsExcelSv(String msExcelSv) {
        this.msExcelSv = msExcelSv == null ? null : msExcelSv.trim();
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode == null ? null : colorcode.trim();
    }

    public Object getColorname() {
        return colorname;
    }

    public void setColorname(Object colorname) {
        this.colorname = colorname;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }
    @Override
   	public boolean equals(Object obj) {
       	TMeasureSectorExcel s = (TMeasureSectorExcel) obj;
   		return (msExcelName.equals(s.msExcelName) && sectorsname.equals(s.sectorsname));
   	}

   	@Override
   	public int hashCode() {
   		//UUID uuid = UUID.randomUUID();
   		String in = (msExcelName.toString() + sectorsname.toString());
   		return in.hashCode();
   	}
}