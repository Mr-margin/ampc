/**  
 * @Title: ToDataTasksUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:03:17
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


















import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.DateUtil;

/**  
 * @Title: ToDataTasksUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:03:17
 * @version 1.0
 */
@Component
public class ToDataTasksUtil {
	//加载ttasksstatus表映射
	@Autowired
	private TTasksStatusMapper tasksStatusMapper;

	/**
	 * @Description: TODO
	 * @param result_Start_model   
	 * void  持久化tasks result_start_model 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 下午2:05:42
	 */
	public void updateDB(Message message) {
		System.out.println("这个是启动模式的返回结果");
		//创建tasksstatus对象
		TTasksStatus tasksStatus = new TTasksStatus();
	    Object object = message.getBody();
	    System.out.println("这个是启动模式的返回结果2");
	    Map map = (Map) object;
	    System.out.println("这个是启动模式的返回结果3");
	    
	  //  Long tasksScenarinoId = Long.parseLong(map.get("scenarioid"));
	   Integer scenarinoId = (Integer) map.get("scenarioid");
	 //   Long tasksScenarinoId = Long.parseLong(string);
	   String endtime = map.get("date").toString();
	   String endtimeString = endtime+" "+"23:59:59";
	   
	    Date tasksEndDate = DateUtil.StrtoDateYMD(endtimeString, "yyyyMMdd HH:mm:ss");
	   Integer stepindex = (Integer) map.get("index");
	    String errorStatus = (String) map.get("desc");
	    System.out.println("---------");
	    
	    System.out.println(tasksEndDate);
	    System.out.println(stepindex);
	    System.out.println(errorStatus);
	    
	    System.out.println("---------");
	    tasksStatus.setErrorStatus(errorStatus);
	    tasksStatus.setTasksScenarinoId((long)scenarinoId);
	    tasksStatus.setStepindex((long)stepindex);
	    tasksStatus.setTasksEndDate(tasksEndDate);
	    System.out.println("开始更新数据库了");
	    int i = tasksStatusMapper.updateStatus(tasksStatus);
	    if(i>0){
	    	System.out.println("跟新成功");
	    }else {
			System.out.println("更新失败");
		}
	    
	    
	  /*  try {
	    	//TTasksStatus iTasksStatus = JsonUtil.jsonToObj(JsonUtil.objToJson(object), TTasksStatus.class);
			
	    	
	    	//System.out.println(iTasksStatus);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 */
	
		/*int i = tasksStatusMapper.updateByPrimaryKey(tasksStatus);
		if(i>0){
			System.out.println("跟新成功了");
		}*/
		
		
		
		
		
		/*tasksStatus.setErrorStatus(errorStatus);
		tasksStatus.setStepindex(stepindex);
		tasksStatus.setErrorStatus(errorStatus);*/
		
	}

	/**
	 * @Description: TODO
	 * @param message   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 下午4:07:47
	 *//*
	public void updateDB(Message message) {
		// TODO Auto-generated method stub
		
	}*/

}
