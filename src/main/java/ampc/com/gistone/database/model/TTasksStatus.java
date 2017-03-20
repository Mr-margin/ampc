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

}