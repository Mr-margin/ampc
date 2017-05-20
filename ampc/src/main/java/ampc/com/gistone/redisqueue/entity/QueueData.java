/**  
 * @Title: QueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:46:42
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

import java.io.Serializable;

/**  
 * @Title: QueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:46:42
 * @version 1.0
 */
public class QueueData implements Serializable{
	//id 每一条消息的id 保证唯一
	private String id;
	// 消息发送的系统时间
	private String time;
	//情景启动的模式
	private String type;
	//消息体的具体内容
	private Object body;
	
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
	
	
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	public QueueData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
