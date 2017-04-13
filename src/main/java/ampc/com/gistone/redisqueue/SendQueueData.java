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
	
	
	public void sendData(String json,TTasksStatus tasksStatus) {
		//获取tasksstatus的状态
		//TTasksStatus tasksStatus = tTasksStatusMapper.selectStatus(tasksScenarinoId);
	/*	Long stepindex = tasksStatus.getStepindex();//index为负数的时候表示参数有问题
		Date TasksendDate = tasksStatus.getTasksEndDate();
		Date startDate = tasksStatus.getScenarinoStartDate();
		//TasksendDate = (Boolean) null?startDate:TasksendDate;
		if (TasksendDate==null) {
			TasksendDate=startDate;
		}
		Date scenarinoEndDate = tasksStatus.getScenarinoEndDate();
		//比较两个时间的大小
		int i = scenarinoEndDate.compareTo(TasksendDate);
		String errorStatus = tasksStatus.getErrorStatus();*/
		System.out.println("开始发送");
	//	if (null==TasksendDate&&null == stepindex) {
			redisqueue.leftPush("test",json);//receive_queue_name
			//测试用的
			
	//	}else if (null==errorStatus&&i>0) {
	//		redisqueue.leftPush("test",json);//receive_queue_name
	//	}
		/*System.out.println(list.size());
		for (int j = 0; j < list.size(); j++) {
			//开始发送消息
			if (j==0) {
				redisqueue.leftPush("test",list.get(0));//receive_queue_name
			}else {
				if (errorStatus.isEmpty()&&i>0&&stepindex>0) {
					redisqueue.leftPush("test",list.get(j));//receive_queue_name
				}else if(!errorStatus.isEmpty()&&i>0){
					
				}
			}
			
		}*/
		System.out.println("发送完毕");
		
	}
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
	 * 
	 * @Description: TODO
	 * @param list
	 * @param scenarinoId   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 上午11:42:56
	 */
	public void getQueuePool(List list, Long scenarinoId) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).toString()+"消息");
		}
		
		for (int i = 0; i < list.size(); i++) {
			//获取tasks执行的状态
			TTasksStatus tasksStatus = tTasksStatusMapper.selectStatus(scenarinoId);
			//index为负数的时候表示参数有问题
			Long stepindex = tasksStatus.getStepindex();
			//情景开始任务结束的时间
			Date TasksendDate = tasksStatus.getTasksEndDate();
			//整个情景开始时间
			Date taskstartDate = tasksStatus.getScenarinoStartDate();
			//整个情景结束时间
			Date scenarinoEndDate = tasksStatus.getScenarinoEndDate();
			//比较两个时间的大小
			if (null==TasksendDate) {
				//当为空的时候 把开始时间赋值给tasks结束时间进行比较
				TasksendDate =taskstartDate;
			}
			//情景的结束时间和当条消息的完成时间比较
			int j = scenarinoEndDate.compareTo(TasksendDate);
			//情景的开始时间和当条记录的时间比较
			int k = taskstartDate.compareTo(TasksendDate);
			//获取出现错误的情况
			String errorStatus = tasksStatus.getErrorStatus();
			//发送第一条数据
			if (i==0) {
				Object object = list.get(0);
				JSONObject jsonObject = JSONObject.fromObject(object);
				String json = jsonObject.toString();
				sendQueueData.sendData(json);
				System.out.println(json);
			}else if(i!=0&&null==stepindex){
				//查询第一条数据是否运行成功  当数据都是空的时候正在执行 尚未返回结果  等待状态
			}else if(i!=0&&stepindex>0&&stepindex<7){
				//当条条尚未执行完毕
				System.out.println("正在执行第一天的任务");
			}else if(i!=0&&stepindex==7&&j>0){
				//当条执行完毕  可以发送后面一条消息
				Object object = list.get(i);
				JSONObject jsonObject = JSONObject.fromObject(object);
				String json = jsonObject.toString();
				sendQueueData.sendData(json);
			}else if (i!=0&&stepindex<0) {
				//参数错误 重新发
				Object object = list.get(i-1);
				JSONObject jsonObject = JSONObject.fromObject(object);
				String json = jsonObject.toString();
				sendQueueData.sendData(json);
			}else if (i!=0 && stepindex<7&&errorStatus!=null) {
				//当条消息中间出现错误  不发送下一条消息
			}
			
		}
		
		
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
