/**  
 * @Title: SendQueueData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午4:31:03
 * @version 
 */
package ampc.com.gistone.redisqueue;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;










































import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.util.ConfigUtil;
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
	@Autowired
	private TScenarinoDetailMapper tscenarinoDetailMapper;
	@Autowired
	private ReadyData readyData;
	/*@Autowired
	private RedisConfig redisConfig;*/
	@Autowired
	private ConfigUtil configUtil;
	
	/**
	 * @Description: TODO
	 * @param queueData   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 下午4:05:24
	 */
	public boolean toJson(QueueData queueData,Long tasksScenarinoId,String time) {
//		StringBuffer stringBuffer = new StringBuffer();
		boolean sendData = false;
		JSONObject jsonObject = JSONObject.fromObject(queueData);
		String json = jsonObject.toString();
		LogUtil.getLogger().info("SendQueueData：这是发送的数据包:"+json);
		//检查是否满足发送的条件--情景状态
		/* TScenarinoDetail selectByPrimaryKey = tscenarinoDetailMapper.selectByPrimaryKey(tasksScenarinoId);
		 Long scenarinoStatus = selectByPrimaryKey.getScenarinoStatus();
		if (scenarinoStatus!=6) {
			LogUtil.getLogger().info("SendQueueData：该情景不是运行状态，不可发送！");
		}*/
		TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(tasksScenarinoId);
		String compantstatus = selectStatus.getBeizhu();
//		String tasksExpand4 = selectStatus.getTasksExpand4();//记录已经发送出去的过得消息串
//		if (null!=tasksExpand4) {
//			stringBuffer.append(tasksExpand4);
//		}
		String sendtime = selectStatus.getBeizhu2();
		String stopStatus = selectStatus.getStopStatus();//终止的状态
//		String pauseStatus = selectStatus.getPauseStatus();//暂停的状态
		if (compantstatus.equals("0")&&sendtime.equals("0")&&"2".equals(stopStatus)) {
			//处于终止且不可发送消息的状态
			LogUtil.getLogger().info("SendQueueData：该条消息刚发送终止的指令，不可发送！");
		}/*else if (compantstatus.equals("0")&&sendtime.equals("0")&&"2".equals(pauseStatus)) {
			//处于暂停且不可发送消息的状态
			LogUtil.getLogger().info("该条消息刚发送暂停的指令，不可发送！");
		}*/
		else{
			 sendData = sendQueueData.sendData(json);
			if (sendData) {
				TTasksStatus tTasksStatus = new TTasksStatus();
				tTasksStatus.setTasksScenarinoId(tasksScenarinoId);
				tTasksStatus.setBeizhu2(time);
				tTasksStatus.setTasksSendTime(new Date());
//				stringBuffer.append(",");
//				stringBuffer.append(json);
//				tTasksStatus.setTasksExpand4(stringBuffer.toString());
				tTasksStatusMapper.updatemessageStatus(tTasksStatus);
				LogUtil.getLogger().info("SendQueueData：情景ID为："+tasksScenarinoId+",time:"+time+"当天的数据发送了");
				LogUtil.getLogger().info("SendQueueData：发送成功");
				sendmessagelogfile(json);
			}else {
				//发送消息失败 改为模式执行出错 错误原因-发送消息到redis失败 
				readyData.updateScenStatusUtil(9l, tasksScenarinoId);
				LogUtil.getLogger().info("SendQueueData：发送失败！原因：发送消息到redis队列出错");
			}
		}
		return sendData;
	}
	
	
	

	/**
	 * @param tasksScenarinoId 
	 * @Description: 发送的消息记录成文件存放
	 * @param json   
	 * void  
	 * @throws IOException 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月19日 下午2:19:55
	 */
	private void sendmessagelogfile(String json){
		String sendMessageLogurl = configUtil.getSendMessageLog();
		try {
			File file = new File(sendMessageLogurl);
			if (!file.exists()) {
				file.mkdirs();
			}
			String datEtoString = DateUtil.DATEtoString(new Date(), "yyyyMMdd");
			String ss = DateUtil.DATEtoString(new Date(), "yyyyMMdd HH:mm:ss");
			String messagefilename = datEtoString+"messageJson.json";
			File file2 = new File(file, messagefilename);
			FileOutputStream fileOutputStream = new FileOutputStream(file2,true);
			String messagefile = ss+json+"\r\n";
			byte[] b = messagefile.getBytes();
			fileOutputStream.write(b, 0, b.length);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			LogUtil.getLogger().error("SendQueueData-sendmessagelogfile：发送的消息写入文件失败！",e);
		}
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
		LogUtil.getLogger().info("SendQueueData-stoptoJson：停止的指令:"+json);
		boolean flag = sendQueueData.sendData(json);
		if (flag) {
			//修改状态表示发送了停止的消息
			/*TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			tTasksStatus.setStopStatus("2");
			int i = tTasksStatusMapper.updatestopstatus(tTasksStatus);
			if (i>0) {
				LogUtil.getLogger().info("更新发送停止模式的状态成功！");
			}else {
				LogUtil.getLogger().info("更新发送停止模式的状态失败！");
			}*/
			//修改情景状态为---模式处理中
			readyData.updateScenStatusUtil(10l, scenarinoId);
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
		LogUtil.getLogger().info("SendQueueData-sendData:开始发送");
		boolean flag = false;
		String sendname = configUtil.getRedisQueuesSendName();
		try {
			long leftPush = redisqueue.leftPush(sendname,json);//receive_queue_name   r0_bm
//			long leftPush = redisqueue.leftPush("r0_test_bm",json);//receive_queue_name   r0_bm
//			long leftPush = redisqueue.leftPush("r0_bm",json);//内网
//		redisqueue.leftPush("bm",json);//receive_queue_name
			if (leftPush>0) {
				LogUtil.getLogger().info("leftPush："+leftPush);
				flag = true;
			}else {
				flag = false;
			}
			LogUtil.getLogger().info("SendQueueData-sendData:发送结束");
			return flag;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtil.getLogger().error("SendQueueData-sendData:发送消息到消息队列出现异常！",e);
			flag = false;
			return flag;
		}
		
	}




	/**
	 * @Description: 模式暂停
	 * @param queueData
	 * @param scenarinoId
	 * @return   
	 * boolean  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月28日 上午10:18:55
	 */
	public boolean pausetoJson(QueueData queueData, Long scenarinoId) {
		JSONObject jsonObject = JSONObject.fromObject(queueData);
		String json = jsonObject.toString();
		
		LogUtil.getLogger().info("SendQueueData--pausetoJson方法：发送了暂停的指令:"+json);
		boolean flag = sendQueueData.sendData(json);
		if (flag) {
			//暂停状态
			readyData.updateScenStatusUtil(10l, scenarinoId);
		}
		return flag;
	}



	
	
	
	
	
	
	
}
