package ampc.com.gistone.database.model;

import java.util.Date;
/**
 * 耦合后清单实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
public class TEsCoupling {
	//耦合后清单ID
    private Long esCouplingId;
    //耦合后清单名称
    private Object esCouplingName;
    //耦合后清单详细
    private Object esCouplingDesc;
    //耦合后清单年份
    private Short esCouplingYear;
    //全国清单ID
    private Long esCouplingNationId;
    //本地清单ID
    private Long esCouplingNativeId;
    //与云平台关联ID
    private Long esCoupingRelationId;
    //创建时间
    private Date addTime;
    //更新时间
    private Date updateTime;
    //用户ID
    private Long userId;
    //是否有效 0无效 1有效
    private String isEffective;
    //删除时间
    private Date deleteTime;
    //使用历史
    private String historyCoupling;

    public Long getEsCouplingId() {
        return esCouplingId;
    }

    public void setEsCouplingId(Long esCouplingId) {
        this.esCouplingId = esCouplingId;
    }

    public Object getEsCouplingName() {
        return esCouplingName;
    }

    public void setEsCouplingName(Object esCouplingName) {
        this.esCouplingName = esCouplingName;
    }

    public Object getEsCouplingDesc() {
        return esCouplingDesc;
    }

    public void setEsCouplingDesc(Object esCouplingDesc) {
        this.esCouplingDesc = esCouplingDesc;
    }

    public Short getEsCouplingYear() {
        return esCouplingYear;
    }

    public void setEsCouplingYear(Short esCouplingYear) {
        this.esCouplingYear = esCouplingYear;
    }

    public Long getEsCouplingNationId() {
        return esCouplingNationId;
    }

    public void setEsCouplingNationId(Long esCouplingNationId) {
        this.esCouplingNationId = esCouplingNationId;
    }

    public Long getEsCouplingNativeId() {
        return esCouplingNativeId;
    }

    public void setEsCouplingNativeId(Long esCouplingNativeId) {
        this.esCouplingNativeId = esCouplingNativeId;
    }

    public Long getEsCoupingRelationId() {
        return esCoupingRelationId;
    }

    public void setEsCoupingRelationId(Long esCoupingRelationId) {
        this.esCoupingRelationId = esCoupingRelationId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getHistoryCoupling() {
        return historyCoupling;
    }

    public void setHistoryCoupling(String historyCoupling) {
        this.historyCoupling = historyCoupling == null ? null : historyCoupling.trim();
    }
}