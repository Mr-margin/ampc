/**  
 * @Title: ResultStartModel.java
 * @Package ampc.com.gistone.redisqueue.result
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:51:57
 * @version 
 */
package ampc.com.gistone.redisqueue.result;

import java.util.Date;

/**  
 * @Title: ResultStartModel.java
 * @Package ampc.com.gistone.redisqueue.result
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:51:57
 * @version 1.0
 */
public class Message {
	
    private String id;
	
	private Date time;
	
	private String type;
	
	private Object body;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
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

	@Override
	public String toString() {
		return "Message [id=" + id + ", time=" + time + ", type=" + type
				+ ", body=" + body + "]";
	}

	
}
