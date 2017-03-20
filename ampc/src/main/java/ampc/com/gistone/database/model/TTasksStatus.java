package ampc.com.gistone.database.model;

import java.util.Date;

public class TTasksStatus {
    private Long tasksId;

	private Long tasksScenarinoId;

	private String errorStatus;

	private Date scenarinoStartDate;

	private Date scenarinoEndDate;

	private Long rangeDay;

	private Long stepindex;

	private Date tasksEndDate;

	public Long getTasksId() {
		return tasksId;
	}

	public void setTasksId(Long tasksId) {
		this.tasksId = tasksId;
	}

	public Long getTasksScenarinoId() {
		return tasksScenarinoId;
	}

	public void setTasksScenarinoId(Long tasksScenarinoId) {
		this.tasksScenarinoId = tasksScenarinoId;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus == null ? null : errorStatus.trim();
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

	public Long getRangeDay() {
		return rangeDay;
	}

	public void setRangeDay(Long rangeDay) {
		this.rangeDay = rangeDay;
	}

	public Long getStepindex() {
		return stepindex;
	}

	public void setStepindex(Long stepindex) {
		this.stepindex = stepindex;
	}

	public Date getTasksEndDate() {
		return tasksEndDate;
	}

	public void setTasksEndDate(Date tasksEndDate) {
		this.tasksEndDate = tasksEndDate;
	}

	private Long tasksId;

    private Long tasksScenarionId;

    private Long wrf;

    private Long mcip;

    private Long megan;

    private Long meic;

    private Long cmap;

    private Long dp;

    private String errorStatus;

    private Date scenarinoStartDate;

    private Date scenarinoEndDate;

    private Long rangeDay;

    private Long status;

    public Long getTasksId() {
        return tasksId;
    }

    public void setTasksId(Long tasksId) {
        this.tasksId = tasksId;
    }

    public Long getTasksScenarionId() {
        return tasksScenarionId;
    }

    public void setTasksScenarionId(Long tasksScenarionId) {
        this.tasksScenarionId = tasksScenarionId;
    }

    public Long getWrf() {
        return wrf;
    }

    public void setWrf(Long wrf) {
        this.wrf = wrf;
    }

    public Long getMcip() {
        return mcip;
    }

    public void setMcip(Long mcip) {
        this.mcip = mcip;
    }

    public Long getMegan() {
        return megan;
    }

    public void setMegan(Long megan) {
        this.megan = megan;
    }

    public Long getMeic() {
        return meic;
    }

    public void setMeic(Long meic) {
        this.meic = meic;
    }

    public Long getCmap() {
        return cmap;
    }

    public void setCmap(Long cmap) {
        this.cmap = cmap;
    }

    public Long getDp() {
        return dp;
    }

    public void setDp(Long dp) {
        this.dp = dp;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus == null ? null : errorStatus.trim();
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

    public Long getRangeDay() {
        return rangeDay;
    }

    public void setRangeDay(Long rangeDay) {
        this.rangeDay = rangeDay;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}