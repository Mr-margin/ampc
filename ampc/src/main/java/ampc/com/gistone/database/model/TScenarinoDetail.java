package ampc.com.gistone.database.model;

import java.util.Date;

public class TScenarinoDetail {
    private Long scenarinoId;

    private Long scenarinoType;

    private Object scenarinoName;

    private Date scenarinoStartDate;

    private Date scenarinoEndDate;

    private Long scenarinoStatus;

    private Long missionId;

    private Date scenarinoAddTime;

    private Long userId;

    private Date updateTime;

    private String isEffective;

    private Date deleteTime;

    private Long basisScenarinoId;

    private Date basisTime;

    private Long contrastScenarinoId;

    private String scenType;

    private Date pathDate;

    private Long spinup;

    private Date ratioStartDate;

    private Date ratioEndDate;

    private Long rangeDay;
    
    private String expand1;
    
    private String expand2;
    
    private String expand3;
    
    private String expand4;
    
    private String expand5;
    
    private String expand6;
    
    private String expand7;
    
    private String expand8;
    
    private String expand9;
    
    private String expand10;
    

    public String getExpand1() {
		return expand1;
	}

	public void setExpand1(String expand1) {
		this.expand1 = expand1;
	}

	public String getExpand2() {
		return expand2;
	}

	public void setExpand2(String expand2) {
		this.expand2 = expand2;
	}

	public String getExpand3() {
		return expand3;
	}

	public void setExpand3(String expand3) {
		this.expand3 = expand3;
	}

	public String getExpand4() {
		return expand4;
	}

	public void setExpand4(String expand4) {
		this.expand4 = expand4;
	}

	public String getExpand5() {
		return expand5;
	}

	public void setExpand5(String expand5) {
		this.expand5 = expand5;
	}

	public String getExpand6() {
		return expand6;
	}

	public void setExpand6(String expand6) {
		this.expand6 = expand6;
	}

	public String getExpand7() {
		return expand7;
	}

	public void setExpand7(String expand7) {
		this.expand7 = expand7;
	}

	public String getExpand8() {
		return expand8;
	}

	public void setExpand8(String expand8) {
		this.expand8 = expand8;
	}

	public String getExpand9() {
		return expand9;
	}

	public void setExpand9(String expand9) {
		this.expand9 = expand9;
	}

	public String getExpand10() {
		return expand10;
	}

	public void setExpand10(String expand10) {
		this.expand10 = expand10;
	}

	public Long getScenarinoId() {
        return scenarinoId;
    }

    public void setScenarinoId(Long scenarinoId) {
        this.scenarinoId = scenarinoId;
    }

    public Long getScenarinoType() {
        return scenarinoType;
    }

    public void setScenarinoType(Long scenarinoType) {
        this.scenarinoType = scenarinoType;
    }

    public Object getScenarinoName() {
        return scenarinoName;
    }

    public void setScenarinoName(Object scenarinoName) {
        this.scenarinoName = scenarinoName;
    }

    public Date getScenarinoStartDate() {
        return scenarinoStartDate;
    }

    public void setScenarinoStartDate(Date scenarinoStartDate) {
        this.scenarinoStartDate = scenarinoStartDate;
    }

    public Date getScenarinoEndDate() {
        return scenarinoEndDate;
    }

    public void setScenarinoEndDate(Date scenarinoEndDate) {
        this.scenarinoEndDate = scenarinoEndDate;
    }

    public Long getScenarinoStatus() {
        return scenarinoStatus;
    }

    public void setScenarinoStatus(Long scenarinoStatus) {
        this.scenarinoStatus = scenarinoStatus;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Date getScenarinoAddTime() {
        return scenarinoAddTime;
    }

    public void setScenarinoAddTime(Date scenarinoAddTime) {
        this.scenarinoAddTime = scenarinoAddTime;
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

    public Long getBasisScenarinoId() {
        return basisScenarinoId;
    }

    public void setBasisScenarinoId(Long basisScenarinoId) {
        this.basisScenarinoId = basisScenarinoId;
    }

    public Date getBasisTime() {
        return basisTime;
    }

    public void setBasisTime(Date basisTime) {
        this.basisTime = basisTime;
    }

    public Long getContrastScenarinoId() {
        return contrastScenarinoId;
    }

    public void setContrastScenarinoId(Long contrastScenarinoId) {
        this.contrastScenarinoId = contrastScenarinoId;
    }

    public String getScenType() {
        return scenType;
    }

    public void setScenType(String scenType) {
        this.scenType = scenType == null ? null : scenType.trim();
    }

    public Date getPathDate() {
        return pathDate;
    }

    public void setPathDate(Date pathDate) {
        this.pathDate = pathDate;
    }

    public Long getSpinup() {
        return spinup;
    }

    public void setSpinup(Long spinup) {
        this.spinup = spinup;
    }

    public Date getRatioStartDate() {
        return ratioStartDate;
    }

    public void setRatioStartDate(Date ratioStartDate) {
        this.ratioStartDate = ratioStartDate;
    }

    public Date getRatioEndDate() {
        return ratioEndDate;
    }

    public void setRatioEndDate(Date ratioEndDate) {
        this.ratioEndDate = ratioEndDate;
    }

    public Long getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Long rangeDay) {
        this.rangeDay = rangeDay;
    }
}