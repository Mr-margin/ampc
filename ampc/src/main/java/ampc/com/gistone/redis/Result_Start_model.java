/**  
 * @Title: Result_Start_model.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午1:52:30
 * @version 
 */
package ampc.com.gistone.redis;

import java.util.Map;

import org.springframework.stereotype.Component;

/**  
 * @Title: Result_Start_model.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午1:52:30
 * @version 1.0
 */
@Component
public class Result_Start_model {
	private String id;
	private String time;
	private String type;
	private Map<String, Object> body;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getBody() {
		return body;
	}
	public void setBody(Map<String, Object> body) {
		this.body = body;
	}
	
	

}
