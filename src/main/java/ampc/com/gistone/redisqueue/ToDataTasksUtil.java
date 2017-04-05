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



















import org.springframework.transaction.annotation.Transactional;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
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
	//加载情景详情映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	//
	@Autowired
	private ReadyData readyData;
	

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
	    Map map = (Map) object;
	    Object code = map.get("code");
	    Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
	  // Integer scenarinoId = (Integer) map.get("scenarioid");
	 //   Long tasksScenarinoId = Long.parseLong(string);
	   String endtime = map.get("date").toString();
	   if (endtime==null||"".equals(endtime)) {
		System.out.println("该条消息出错误！！");
	}
	   System.out.println(endtime+"---------");
	   String endtimeString = endtime+" "+"23:59:59";
	   
	    Date tasksEndDate = DateUtil.StrtoDateYMD(endtimeString, "yyyyMMdd HH:mm:ss");
	   Object step = map.get("index");
	   Integer stepindex = Integer.parseInt(step.toString());
	   String errorStatus = (String) map.get("desc");
	    System.out.println("---------");
	    
	    System.out.println(tasksEndDate);
	    System.out.println(stepindex);
	    System.out.println(errorStatus);
	    
	    System.out.println("---------");
	    tasksStatus.setErrorStatus(errorStatus);
	   // tasksStatus.setTasksScenarinoId((long)scenarinoId);
	    tasksStatus.setTasksScenarinoId(tasksScenarinoId);
	    tasksStatus.setStepindex((long)stepindex);
	    tasksStatus.setTasksEndDate(tasksEndDate);
	    System.out.println("开始更新tasksstatus数据库了");
	    
	    int i = tasksStatusMapper.updateStatus(tasksStatus);
	    boolean a = true;
	    System.out.println(tasksStatus+"tasksstatus");
	    if(i>0){
	    	System.out.println("跟新tasksstatus成功");
	    	//当tasksstatus更新成功 并且执行成功  发送下一条消息
	    	//通过情景的ID查找该情景的开始时间结束时间和情景类型
	    //	String scentype = tScenarinoDetailMapper.selectscentype(tasksScenarinoId);
	    	TScenarinoDetail selectByPrimaryKey = tScenarinoDetailMapper.selecttypetime(tasksScenarinoId);
	    	
	    	String scentype = selectByPrimaryKey.getScenType();
	    	//获取情景任务的开始时间和结束时间
	    	Date startDate = selectByPrimaryKey.getScenarinoStartDate();
	    	Date endDate = selectByPrimaryKey.getScenarinoEndDate();
	    	//当条情景的结束时间和当条情景的任务完成状态结束时间 比较
	    	int compareTo = endDate.compareTo(tasksEndDate);
	    	//比较开始时间和任务完成结束的时间
	    	int StartCompare = startDate.compareTo(tasksEndDate);
			//根据情景类型确定stepindex的数量
	    	Integer index = surestepindex(scentype );
	    	//code为0的时候是成功的  同时是实时预报类型的情况下 stepindex==8才会发下一条 同时时间小于该任务的结束时间
	    	if (code.equals("0")&&"4".equals(scentype)&&stepindex==index&&compareTo>0) {
	    		System.out.println(tasksEndDate+"tasks的结束时间");
	    		readyData.sendqueueRealData(tasksEndDate,tasksScenarinoId);
			}if (stepindex==-1) {
				//发生错误的时候重新组织上一条的参数发送
				Date changeDay = DateUtil.ChangeDay(tasksEndDate, -1);
				readyData.sendqueueRealData(changeDay,tasksScenarinoId);
			}
			//如果是实时预报的类型  跟新完一个时间的时候标志着当天的fnl或者gfs可以为预评估所用
			if("0".equals(code)&&"4".equals(scentype)){
				try {
					//readyData.cantopreEvaluation(tasksEndDate,tasksScenarinoId,null);
				Map<String, String> cantopreEvaluation = readyData.cantopreEvaluation(tasksEndDate, tasksScenarinoId, null);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			//预评估任务的预评估情景 准备下一条数据
	    	if("0".equals(code)&&"1".equals(scentype)&&index==stepindex&&compareTo>0){
	    		readyData.sendDataEvaluationSituationThen(tasksEndDate,tasksScenarinoId);
	    	}
	    	//修改该情景的状态  为1 表示该条情景模式运行过 并且可以用当天的数据做fnl
	    	if ("0".equals(code)&&StartCompare<-1) {
	    		Map hashMap = new HashMap();
	    		Integer status = 1;
	    		hashMap.put("tasksScenarinoId", tasksScenarinoId);
	    		hashMap.put("beizhu", status);
				tasksStatusMapper.updateRunstatus(hashMap);
			}
	    	
	    }else {
			System.out.println("更新tasksstatus失败");
		}
	    
	}

	/**
	 * @Description: TODO
	 * @param scentype
	 * @return   
	 * Integer  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午7:05:53
	 */
	private Integer surestepindex(String scentype) {
		Integer index = null ;
		switch (scentype) {
		case "1":
			index = 4;
			break;
		case "2":
			index = 4;
			break;
		case "3":
			index = 8;
			break;
		case "4":
			index = 8;
			break;

		default:
			
			break;
		}
		
		return index;
	}


}
