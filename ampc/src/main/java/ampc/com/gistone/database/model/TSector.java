package ampc.com.gistone.database.model;

import java.util.Date;

/**
 * 行业的实体类
 * 
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
public class TSector {
	// 行业Id
	private Long sectorId;
	// 版本号
	private Long versionId;
	// 行业名称
	private Object sectorName;
	// 行业对应L4S
	private Object l4s;
	// 添加时间
	private Date addTime;
	// 最后一次更新时间
	private Date updateTime;
	// 关联措施集合 暂时不用 使用中间表关联
	private Object measureIds;
	// 用户ID
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getSectorId() {
		return sectorId;
	}

	public void setSectorId(Long sectorId) {
		this.sectorId = sectorId;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public Object getSectorName() {
		return sectorName;
	}

	public void setSectorName(Object sectorName) {
		this.sectorName = sectorName;
	}

	public Object getL4s() {
		return l4s;
	}

	public void setL4s(Object l4s) {
		this.l4s = l4s;
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

	public Object getMeasureIds() {
		return measureIds;
	}

	public void setMeasureIds(Object measureIds) {
		this.measureIds = measureIds;
	}
}