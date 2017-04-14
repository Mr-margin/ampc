package ampc.com.gistone.preprocess.obs;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class ObsParams {

	private String startDate;
	  private String endDate;
	  private String area;
	  private String station;
	  private String areaId;
	  private String res;
	  private String actionId;

	  public String toKey() {
	    return format("-", startDate, endDate, area, station, areaId, res);
	  }
	  
	  public static String format(String separator, Object... params) {
	     StringBuilder sb = new StringBuilder();
	     int i = 0;
	     for (Object obj : params) {
	        if (i++ > 0) sb.append(separator);
	        sb.append(obj);
	     }
	     return sb.toString();
	  }

	  public String toParams() {
	    List<NameValuePair> params = new ArrayList<>();
	    params.add(new BasicNameValuePair("startDate", startDate));
	    params.add(new BasicNameValuePair("endDate", endDate));
	    params.add(new BasicNameValuePair("area", area));
	    params.add(new BasicNameValuePair("station", station));
	    params.add(new BasicNameValuePair("areaId", areaId));
	    params.add(new BasicNameValuePair("res", res));
	    params.add(new BasicNameValuePair("actionId", getActionId()));
	    return buildParams(params);
	  }
	  
	  protected String buildParams(List<NameValuePair> params) {
	    return URLEncodedUtils.format(params, "utf-8");
	  }

	  public String getStartDate() {
	    return startDate;
	  }

	  public void setStartDate(String startDate) {
	    this.startDate = startDate;
	  }

	  public String getEndDate() {
	    return endDate;
	  }

	  public void setEndDate(String endDate) {
	    this.endDate = endDate;
	  }

	  public String getArea() {
	    return area;
	  }

	  public void setArea(String area) {
	    this.area = area;
	  }

	  public String getStation() {
	    return station;
	  }

	  public void setStation(String station) {
	    this.station = station;
	  }

	  public String getAreaId() {
	    return areaId;
	  }

	  public void setAreaId(String areaId) {
	    this.areaId = areaId;
	  }

	  public String getRes() {
	    return res;
	  }

	  public void setRes(String res) {
	    this.res = res;
	  }

	  public String getActionId() {
		 return actionId;
	  }

	  public void setActionId(String actionId) {
		 this.actionId = actionId;
	  }
	  
}
