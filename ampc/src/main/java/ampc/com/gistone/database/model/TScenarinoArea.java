package ampc.com.gistone.database.model;

import java.util.Date;
/**
 * 区域实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public class TScenarinoArea {
	//区域ID
    private Long scenarinoAreaId;
    //所属情景ID
    private Long scenarinoDetailId;
    //区域名称
    private Object areaName;
    //创建时间
    private Date addTime;
    //用户ID
    private Long userId;
    //修改时间
    private Date updateTime;
    //是否有效 0无效 1有效
    private String isEffective;
    //删除时间
    private Date deleteTime;
    
    public Long getScenarinoAreaId() {
        return scenarinoAreaId;
    }

    public void setScenarinoAreaId(Long scenarinoAreaId) {
        this.scenarinoAreaId = scenarinoAreaId;
    }

    public Long getScenarinoDetailId() {
        return scenarinoDetailId;
    }

    public void setScenarinoDetailId(Long scenarinoDetailId) {
        this.scenarinoDetailId = scenarinoDetailId;
    }

    public Object getAreaName() {
        return areaName;
    }

    public void setAreaName(Object areaName) {
        this.areaName = areaName;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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