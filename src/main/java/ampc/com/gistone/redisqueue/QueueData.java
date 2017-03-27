/**  
 * @Title: QueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:46:42
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**  
 * @Title: QueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:46:42
 * @version 1.0
 */
@Component
public class QueueData {
	//id 每一条消息的id 保证唯一
	private String id;
	// 消息发送的系统时间
	private String time;
	//情景启动的模式
	private String type;
	//消息体的具体内容
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
	public QueueData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QueueData(String id, String time, String type,
			Map<String, Object> body) {
		super();
		this.id = id;
		this.time = time;
		this.type = type;
		this.body = body;
	}
	
	

}
