/**  
 * @Title: UngribTest.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月23日 上午9:51:50
 * @version 
 */
package ampc.com.gistone.redis;

import java.util.Map;

import oracle.net.aso.a;

import org.springframework.stereotype.Component;

/**  
 * @Title: UngribTest.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei untrib 消息的测试类
 * @date 2017年3月23日 上午9:51:50
 * @version 1.0
 */
@Component
public class UngribTest {
	private String id;
	private String time;
	private String type;
	private Map<String, Map<String, Object>> body;
	@Override
	public String toString() {
		return "UngribTest [id=" + id + ", time=" + time + ", type=" + type
				+ ", body=" + body + "]";
	}
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
	
	public Map<String, Map<String, Object>> getBody() {
		return body;
	}
	public void setBody(Map<String, Map<String, Object>> body) {
		this.body = body;
	}
	public UngribTest() {
		super();
		// TODO Auto-generated constructor stub
	}
		
	
	

}
