/**  
 * @Title: SendQueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午4:31:03
 * @version 
 */
package ampc.com.gistone.redisqueue;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.redis.RedisTestService;


/**  
 * @Title: SendQueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午4:31:03
 * @version 1.0
 */
@Component
public class SendQueueData {
	@Autowired
	private QueueData queueDatabase;
	@Autowired
	private RedisUtilServer redisqueue;
	
	
	
	public void sendData(HashMap<String, Object> body,Long scenarinoId) {
		System.out.println("1230000");
		String id =queueDatabase.getId();
		System.out.println(id);
		redisqueue.leftPush("id",id);
		
	}
	public void testpushqueue() {
		
	}




	
	
	
	
	
	
	
}
