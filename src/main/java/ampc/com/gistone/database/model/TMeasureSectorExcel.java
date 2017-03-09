package ampc.com.gistone.database.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class TMeasureSectorExcel {
    private Long msExcelId;

    private Long userId;

    private Long msExcelVersionId;

    private Long mid;

    private Object msExcelName;

    private Object msExcelDesc;

    private Object msExcelOp;

    private BigDecimal msExcelIntensity;

    private Object sectorsname;

    private Object content;

    private Object msExcelDisplay;

    private Long debugModel;

    private Object msExcelType;

    private Long msExcelLevel;

    private BigDecimal msExcelA;

    private BigDecimal msExcelA1;

    private BigDecimal msExcelIntensity1;

    private BigDecimal msExcelAsh;

    private BigDecimal msExcelSulfer;

    private Object msExcelSv;

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

    public Long getMsExcelVersionId() {
        return msExcelVersionId;
    }

    public void setMsExcelVersionId(Long msExcelVersionId) {
        this.msExcelVersionId = msExcelVersionId;
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

    public BigDecimal getMsExcelIntensity() {
        return msExcelIntensity;
    }

    public void setMsExcelIntensity(BigDecimal msExcelIntensity) {
        this.msExcelIntensity = msExcelIntensity;
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

    public Long getMsExcelLevel() {
        return msExcelLevel;
    }

    public void setMsExcelLevel(Long msExcelLevel) {
        this.msExcelLevel = msExcelLevel;
    }

    public BigDecimal getMsExcelA() {
        return msExcelA;
    }

    public void setMsExcelA(BigDecimal msExcelA) {
        this.msExcelA = msExcelA;
    }

    public BigDecimal getMsExcelA1() {
        return msExcelA1;
    }

    public void setMsExcelA1(BigDecimal msExcelA1) {
        this.msExcelA1 = msExcelA1;
    }

    public BigDecimal getMsExcelIntensity1() {
        return msExcelIntensity1;
    }

    public void setMsExcelIntensity1(BigDecimal msExcelIntensity1) {
        this.msExcelIntensity1 = msExcelIntensity1;
    }

    public BigDecimal getMsExcelAsh() {
        return msExcelAsh;
    }

    public void setMsExcelAsh(BigDecimal msExcelAsh) {
        this.msExcelAsh = msExcelAsh;
    }

    public BigDecimal getMsExcelSulfer() {
        return msExcelSulfer;
    }

    public void setMsExcelSulfer(BigDecimal msExcelSulfer) {
        this.msExcelSulfer = msExcelSulfer;
    }

    public Object getMsExcelSv() {
        return msExcelSv;
    }

    public void setMsExcelSv(Object msExcelSv) {
        this.msExcelSv = msExcelSv;
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
   		return msExcelName.equals(s.msExcelName) && sectorsname.equals(s.sectorsname);
   	}

   	@Override
   	public int hashCode() {
   		UUID uuid = UUID.randomUUID();
   		String in = (uuid.toString()+msExcelName.toString() + sectorsname.toString());
   		return in.hashCode();
   	}

}