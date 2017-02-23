package ampc.com.gistone.database.model;

import java.util.Date;
/**
 * 任务详情实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
public class TMissionDetail {
	//任务Id
    private Long id; 
    //任务名称
    private Object missionName;
    //模拟范围ID
    private Long missionDomainId;
    //清单ID
    private Long esCouplingId;
    //任务开始时间
    private Date missionStartDate;
    //任务结束时间
    private Date missionEndDate;
    //任务添加时间
    private Date addTime;
    //任务修改时间
    private Date updateTime;
    //用户ID
    private Long userId;
    //是否有效  1有效 0无效
    private String isEffective;
    //删除时间
    private Date deleteTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getMissionName() {
        return missionName;
    }

    public void setMissionName(Object missionName) {
        this.missionName = missionName;
    }

    public Long getMissionDomainId() {
        return missionDomainId;
    }

    public void setMissionDomainId(Long missionDomainId) {
        this.missionDomainId = missionDomainId;
    }

    public Long getEsCouplingId() {
        return esCouplingId;
    }

    public void setEsCouplingId(Long esCouplingId) {
        this.esCouplingId = esCouplingId;
    }

    public Date getMissionStartDate() {
        return missionStartDate;
    }

    public void setMissionStartDate(Date missionStartDate) {
        this.missionStartDate = missionStartDate;
    }

    public Date getMissionEndDate() {
        return missionEndDate;
    }

    public void setMissionEndDate(Date missionEndDate) {
        this.missionEndDate = missionEndDate;
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
}