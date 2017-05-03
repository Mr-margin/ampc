package ampc.com.gistone.preprocess.concn;

public class CheckTableParams {

	private Long user_id;
	private String years;
	private String time_point;
	private String tName;
	private String resType; // concn or meteor

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

	public String getTime_point() {
		return time_point;
	}

	public void setTime_point(String time_point) {
		this.time_point = time_point;
	}

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}

}
