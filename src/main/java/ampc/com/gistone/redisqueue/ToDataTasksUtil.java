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
import ampc.com.gistone.util.LogUtil;

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
	    
	  /*  //只要跑完一天的任务 该天的数据就可以使用
	    if (("0".equals(code)&&stepindex==8)||("0".equals(code)&&stepindex==4)) {
	    	tasksStatus.setBeizhu("1");
		}*/
	    int i = tasksStatusMapper.updateStatus(tasksStatus);
	    System.out.println(tasksStatus+"tasksstatus");
	    if(i>0){
	    	System.out.println("跟新tasksstatus成功");
	    	//当tasksstatus更新成功 并且执行成功  发送下一条消息
	    	//通过情景的ID查找该情景的开始时间结束时间和情景类型
	    //	String scentype = tScenarinoDetailMapper.selectscentype(tasksScenarinoId);
	    	TScenarinoDetail selectByPrimaryKey = tScenarinoDetailMapper.selecttypetime(tasksScenarinoId);
	    	//获取当前情景pathdate 用于确定该条记录是不是补发的
	    	Date pathDate = selectByPrimaryKey.getPathDate();
	    	Date today = DateUtil.DateToDate(new Date(), "yyyyMMdd");
	    	if (pathDate!=null) {
	    		int pathcompare = pathDate.compareTo(today);
		    	
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
		    	//修改该情景的状态  为1 表示该条情景模式运行过 
		    	if ("0".equals(code)&&"4".equals(scentype)&&stepindex==8&&StartCompare==0) {
					//实时预报第一天的跑完或者补跑的fnl跑完状态变为1可用
		    		TTasksStatus tasksStatus2 = new TTasksStatus();
		    		tasksStatus2.setBeizhu("1");
		    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
		    		tasksStatusMapper.updateRunstatus(tasksStatus2);
		    		System.out.println("跟新"+tasksScenarinoId+"的状态了");
				}else {
					if("0".equals(code)&&compareTo>0){
						//其他情况需要跑完整个情景模式状态才变为2 没跑完为1 出错为0
			    		TTasksStatus tasksStatus2 = new TTasksStatus();
			    		tasksStatus2.setBeizhu("1");
			    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
			    		tasksStatusMapper.updateRunstatus(tasksStatus2);
					}else if (("0".equals(code)&&compareTo==0&&index==stepindex)) {
						//模式运行完毕就会变成2
						TTasksStatus tasksStatus2 = new TTasksStatus();
			    		tasksStatus2.setBeizhu("2"); 
			    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
			    		tasksStatusMapper.updateRunstatus(tasksStatus2);
			    		System.out.println("不是事实预报"+scentype+index+":"+stepindex);
					}
				}
		    	//code为0的时候是成功的  同时是实时预报类型的情况下 stepindex==8才会发下一条 同时时间小于该任务的结束时间  同时该条情景对应的pathdate是当天才能走这个方法
		    	if (code.equals("0")&&"4".equals(scentype)&&stepindex==index&&compareTo>0) {
		    		if (pathcompare<0) {
						//pathdate 比系统当天的时间小 表示是补发之前遗漏的 继续触发今天的实时预报
		    			TScenarinoDetail idandcore = tScenarinoDetailMapper.getidAndcores(today);
		    			Long scenarinoId = idandcore.getScenarinoId();
		    			Long cores =Long.parseLong(idandcore.getExpand3());
		    			readyData.readyRealMessageDataFirst(scenarinoId, cores);
					}
		    		if (pathcompare==0) {
		    			//当时间到当天的时候发当天的实时预报
		    			readyData.sendqueueRealData(tasksEndDate,tasksScenarinoId);
					}
		    		System.out.println(tasksEndDate+"tasks的结束时间");
				}if (stepindex==-1) {
					//发生错误的时候重新组织上一条的参数发送
					//Date changeDay = DateUtil.ChangeDay(tasksEndDate, -1);
					ErrorStatus.Errortips(tasksScenarinoId);
					//readyData.sendqueueRealData(changeDay,tasksScenarinoId);
				}
				//预评估任务的预评估情景 准备下一条数据
		    	if("0".equals(code)&&"1".equals(scentype)&&index==stepindex&&compareTo>0){
		    		readyData.sendDataEvaluationSituationThen(tasksEndDate,tasksScenarinoId);
		    	}
			}
	    	
	    }else {
			System.out.println("更新tasksstatus失败");
			LogUtil.getLogger().info(tasksScenarinoId+"该情景状态未更新成功");
		}
	    
	}

	/**
	 * @Description: 确定index的最大值 根据情景类型
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
