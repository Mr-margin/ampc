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
import ampc.com.gistone.util.DateUtil;
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
	
	
	/**
	 * @Description: TODO
	 * @param queueData   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 下午4:05:24
	 */
	public void toJson(QueueData queueData,Long tasksScenarinoId,String time) {
		JSONObject jsonObject = JSONObject.fromObject(queueData);
		String json = jsonObject.toString();
		LogUtil.getLogger().info("这是发送的数据包:"+json);
		sendQueueData.sendData(json);
		TTasksStatus tTasksStatus = new TTasksStatus();
		tTasksStatus.setTasksScenarinoId(tasksScenarinoId);
		tTasksStatus.setBeizhu2(time);
		tTasksStatusMapper.updatemessageStatus(tTasksStatus);
		//String pathdate = DateUtil.changeDate(DateUtil.StrtoDateYMD(time, "yyyyMMdd"), "yyyyMMdd", 1);
		LogUtil.getLogger().info("情景ID为："+tasksScenarinoId+",time:"+time+"当天的数据发送了");
		LogUtil.getLogger().info("发送成功");
	}
	
	
	

	/**
	 * @Description: 停止模式的消息
	 * @param queueData
	 * @param object
	 * @param object2   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月26日 下午3:56:36
	 */
	public boolean stoptoJson(QueueData queueData,Long scenarinoId) {
		JSONObject jsonObject = JSONObject.fromObject(queueData);
		String json = jsonObject.toString();
		LogUtil.getLogger().info("发送了停止的指令:"+json);
		boolean flag = sendQueueData.sendData(json);
		//修改状态表示发送了停止的消息
		TTasksStatus tTasksStatus = new TTasksStatus();
		tTasksStatus.setTasksScenarinoId(scenarinoId);
		tTasksStatus.setStopStatus("2");
		tTasksStatus.setStopModelResult(null);
		int i = tTasksStatusMapper.updatestopstatus(tTasksStatus);
		if (i>0) {
			LogUtil.getLogger().info("更新发送停止模式的状态成功！");
		}else {
			LogUtil.getLogger().info("更新发送停止模式的状态失败！");
		}
		
		return flag;
	}
	
	/**
	 * @Description: TODO
	 * @param json   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 上午11:51:50
	 */
	private boolean sendData(String json) {
		LogUtil.getLogger().info("开始发送");
		boolean flag;
		long leftPush = redisqueue.leftPush("receive_queue_name",json);//receive_queue_name
		if (leftPush>0) {
			flag = true;
		}else {
			flag = false;
		}
//		redisqueue.leftPush("bm",json);//receive_queue_name
		LogUtil.getLogger().info("发送结束");
		return flag;
	}



	
	
	
	
	
	
	
}
