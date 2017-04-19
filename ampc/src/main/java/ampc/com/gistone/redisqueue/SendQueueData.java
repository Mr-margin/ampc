/**  
 * @Title: SendQueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午4:31:03
 * @version 
 */
package ampc.com.gistone.redisqueue;


import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.util.LogUtil;


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
	private SendQueueData sendQueueData;
	//加载redi工具类
	@Autowired
	private RedisUtilServer redisqueue;
	//加载tasksstatus映射
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	
	/*public void sendData(String json,TTasksStatus tasksStatus) {
		System.out.println("开始发送");
	//	if (null==TasksendDate&&null == stepindex) {
			redisqueue.leftPush("test",json);//receive_queue_name
		System.out.println("发送完毕");
		
	}*/
	/**
	 * @Description: TODO
	 * @param queueData   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 下午4:05:24
	 */
	public void toJson(QueueData queueData,Long tasksScenarinoId) {
		JSONObject jsonObject = JSONObject.fromObject(queueData);
		String json = jsonObject.toString();
		System.out.println(json+"这是发送的数据包 ");
		sendQueueData.sendData(json);
		System.out.println("发送成功");
	}
	
	
	/**
	 * @Description: TODO
	 * @param json   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 上午11:51:50
	 */
	private void sendData(String json) {
		LogUtil.getLogger().info(json+new Date());
		System.out.println("开始发送");
		redisqueue.leftPush("receive_queue_name",json);//receive_queue_name
	//	redisqueue.leftPush("queue_test",json);//receive_queue_name
		System.out.println("发送结束");
		
	}
	/**
	 * @param tasksEndDate 
	 * @Description: TODO   
	 * void  实时预报发送消息到队列的方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午3:42:48
	 */
	public void sendqueueData(Date tasksEndDate) {
		// TODO Auto-generated method stub
		
	}
	



	
	
	
	
	
	
	
}
