/**  
 * @Title: ToDataTasksUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:03:17
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;





















import org.springframework.util.StringUtils;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**  
 * @Title: ToDataTasksUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月27日 下午2:03:17
 * @version 1.0
 * @param <V>
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
	public void updateDB(String rpop){
		LogUtil.getLogger().info("模式启动的返回结果");
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			//创建tasksstatus对象
			TTasksStatus tasksStatus = new TTasksStatus();
			Date time = message.getTime();
		    Object object = message.getBody();
		    Map map = (Map) object;
		    Object code = map.get("code");
		    Object param = map.get("scenarioid");
		    if (RegUtil.CheckParameter(param, "Integer", null, false)) {
			    Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
			    String endtime = map.get("date").toString();
			   //结束时间的年与日形式 用于比较
			   if (null==endtime||"".equals(endtime)) {
				   LogUtil.getLogger().info("该条消息出错误！！");
			   }else{
				   Date endtimeDate = null;
				   try {
					   endtimeDate = DateUtil.StrtoDateYMD(endtime, "yyyyMMdd");
					   String endtimeString = endtime+" "+"23:59:59";
					   Date tasksEndDate = DateUtil.StrtoDateYMD(endtimeString, "yyyyMMdd HH:mm:ss");
					   Object step = map.get("index");
					   Integer stepindex = Integer.parseInt(step.toString());
					   String errorStatus = (String) map.get("desc");
					   LogUtil.getLogger().info("time:"+time+"index:"+step+"taskendDate:"+tasksEndDate);
					    tasksStatus.setTasksScenarinoId(tasksScenarinoId);
					    tasksStatus.setStepindex((long)stepindex);
					    tasksStatus.setTasksEndDate(tasksEndDate);
					    LogUtil.getLogger().info("开始更新tasksstatus数据库了");
					    try {//找出上一条消息的结果
					    //	String modelresult = tasksStatusMapper.selectStartModelresult(tasksScenarinoId);
						    int i = tasksStatusMapper.updateStatus(tasksStatus);
						    LogUtil.getLogger().info("tasksstatus："+tasksStatus);
						    if(i>0){
						    	if (!errorStatus.equals("")) {
						    		//出现错误，模式变为暂停
						    		//更新情景状态
						    		//更新状态
									Map statusmap = new HashMap();
									statusmap.put("scenarinoId", tasksScenarinoId);
									Long status = (long)7;
									statusmap.put("scenarinoStatus", status);
									int a = tScenarinoDetailMapper.updateStatus(statusmap);
									if (a>0) {
										LogUtil.getLogger().info("修改情景id为"+tasksScenarinoId+"的状态成功");
									}else {
										LogUtil.getLogger().info("修改情景id为"+tasksScenarinoId+"的状态失败");
									}
								}else {
									LogUtil.getLogger().info("跟新tasksstatus成功");
							    	//当tasksstatus更新成功 并且执行成功  发送下一条消息
							    	//通过情景的ID查找该情景的开始时间结束时间和情景类型
							    	TScenarinoDetail selectByPrimaryKey = tScenarinoDetailMapper.selecttypetime(tasksScenarinoId);
							    	//获取当前情景pathdate 用于确定该条记录是不是补发的
							    	Date pathDate = selectByPrimaryKey.getPathDate();
							    	Date today = DateUtil.DateToDate(new Date(), "yyyyMMdd");
							    	String scentype = selectByPrimaryKey.getScenType();
							    	//根据情景类型确定stepindex的数量
							    	Integer index = surestepindex(scentype);
										if (null!=pathDate) {
								    		//比较今天的和当前发送的情景的pathdate
								    		int pathcompare = pathDate.compareTo(today);
									    	//获取情景任务的开始时间和结束时间
									    	Date startDate = selectByPrimaryKey.getScenarinoStartDate();
									    	Date endDate = selectByPrimaryKey.getScenarinoEndDate();
									    	//当条情景的结束时间和当条情景的任务完成状态结束时间 比较
									    	int compareTo = endDate.compareTo(tasksEndDate);
									    	//比较开始时间和任务完成结束的时间
									    	int StartCompare = startDate.compareTo(endtimeDate);
									    	//修改该情景的状态  为1 表示该条情景模式运行过 
									    	if ("0".equals(code)&&"4".equals(scentype)&&stepindex==8&&StartCompare==0) {
												//实时预报第一天的跑完或者补跑的fnl跑完状态变为1可用
									    		TTasksStatus tasksStatus2 = new TTasksStatus();
									    		tasksStatus2.setBeizhu("1");
									    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
									    		tasksStatusMapper.updateRunstatus(tasksStatus2);
									    		LogUtil.getLogger().info("跟新"+tasksScenarinoId+"的状态为1了"+":"+scentype+",stepindex:"+stepindex);
											}else {
												if("0".equals(code)&&compareTo>0&&!scentype.equals("4")){
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
										    		LogUtil.getLogger().info("不是事实预报"+scentype+index+":"+stepindex);
												}
											}
									    	//code为0的时候是成功的  同时是实时预报类型的情况下 stepindex==8才会发下一条 同时时间小于该任务的结束时间  同时该条情景对应的pathdate是当天才能走这个方法
									    	if (code.equals("0")&&"4".equals(scentype)&&stepindex==index&&compareTo>0) {
									    		if (pathcompare<0) {
									    			//查找用户ID
									    			TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(tasksScenarinoId);
													//pathdate 比系统当天的时间小 表示是补发之前遗漏的 继续触发今天的实时预报
									    			Long userId = scenarinoDetail.getUserId();
									    			Map map2 = new HashMap();
									    			map2.put("userId", userId);
									    			map2.put("pathDate", today);
									    			TScenarinoDetail idandcore = tScenarinoDetailMapper.getidByuserIdAndpathdate(map2);
									    			if (null!=idandcore) {
									    				Long scenarinoId = idandcore.getScenarinoId();
										    			Long cores =Long.parseLong(idandcore.getExpand3());
										    			//检查当天的实时预报是否正在执行（测试出的bug）
										    			TTasksStatus tasksStatus2 = tasksStatusMapper.selectendByscenarinoId(scenarinoId);
										    			//获取当条情景是否在执行
										    			String nowexMo = tasksStatus2.getBeizhu2();
										    			if (nowexMo.trim().equals("0")) {
										    			//	readyData.readyRealMessageDataFirst(scenarinoId, cores,userId);
										    				String lastungrib = readyData.readyLastUngrib(userId);
										    				if (null!=lastungrib) {
										    					readyData.readyRealMessageDataFirst(idandcore, lastungrib,false);
															}
														}else {
															//表示已经发送过当条情景 不用触发
										    				LogUtil.getLogger().info("情景ID为："+scenarinoId+"的情景已经发送过消息了！");
														}
													}else {
														LogUtil.getLogger().info("当天的实时预报情景还没有创建！");
														//当天的实时预报 还未创建  上一天的gfs可以继续发送消息直到当天的上午7点整
														//上一条的情景ID和完成tasks的时间
														//当前系统时间
														Date nowDate = new Date();
														//当天时间的早上七点
														Date initDate = DateUtil.DateToDate(nowDate, "yyyyMMdd");
														Date upperDate = DateUtil.changedateByHour(initDate,7);
														int goon = nowDate.compareTo(upperDate);
														if (goon>0) {
															LogUtil.getLogger().info("时间超过七点，不能再发送了");
														}else{
														//	readyData.sendqueueRealData(tasksEndDate,tasksScenarinoId);
															readyData.sendqueueRealDataThen(tasksEndDate,tasksScenarinoId);
														}
													}
												}
									    		if (pathcompare==0) {
									    			//当时间到当天的时候发当天的实时预报
									    		//	readyData.sendqueueRealData(tasksEndDate,tasksScenarinoId); 
									    			readyData.sendqueueRealDataThen(tasksEndDate,tasksScenarinoId); 
												}
									    		LogUtil.getLogger().info(tasksEndDate+"tasks的结束时间");
											}if (stepindex==-1) {
												//发生错误的时候重新组织上一条的参数发送
												ErrorStatus.Errortips(tasksScenarinoId);
											}
										}else {
											//针对其他三种情景（ 预评估的后评估情景 后评估任务的两种情景）不存在pathdate 当接受到消息时候 给每个tasksstatus一个状态
											if ("0".equals(code)&&"2".equals(scentype)&&index.equals(stepindex)) {
												//后评估情景更新状态
												TTasksStatus tasksStatus2 = new TTasksStatus();
									    		tasksStatus2.setBeizhu("2"); 
									    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
									    		tasksStatusMapper.updateRunstatus(tasksStatus2);
											}
											if ("0".equals(code)&&"3".equals(scentype)&&index.equals(stepindex)) {
												//基准情景更新状态
												TTasksStatus tasksStatus2 = new TTasksStatus();
									    		tasksStatus2.setBeizhu("2"); 
									    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
									    		tasksStatusMapper.updateRunstatus(tasksStatus2);
											}
											
										}
								}
						    }else {
								LogUtil.getLogger().info("情景ID为："+tasksScenarinoId+"的状态更新失败");
							}
						} catch (Exception e) {
							// TODO: handle exception
							LogUtil.getLogger().error("查询模式返回结果状态出错！",e);
						}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.getLogger().error("更新tasksstauts出异常！"+e.getMessage());
				}
				}
			}else {
				LogUtil.getLogger().error("start.model.result ScenarinoId参数错误！");
			}

		} catch (Exception e1) {
			LogUtil.getLogger().error("tasksresult转换异常"+e1.getMessage());
		}
		
	}

	

	/**
	 * @Description: 停止模式的处理
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月26日 下午4:44:43
	 */
	public void stopModelresult(String rpop) {
		Message message;
		try {
			message = JsonUtil.jsonToObj(rpop, Message.class);
			LogUtil.getLogger().info("停止模式返回的结果！");
		    Object object = message.getBody();
		    Map map = (Map) object;
		    Object code = map.get("code");
		    Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
		    TTasksStatus tTasksStatus = new TTasksStatus();
		    tTasksStatus.setTasksScenarinoId(tasksScenarinoId);
		    tTasksStatus.setStopStatus(code.toString());
		    tTasksStatus.setStopModelResult(rpop);
		    tTasksStatus.setBeizhu2("0");
//		    tTasksStatus.setStepindex(null);
//		    tTasksStatus.setTasksEndDate(null);
		    try {
		    	//更新tasks状态
		    	int i = tasksStatusMapper.updatestopModelresult(tTasksStatus);
		    	if (i>0) {
		    		LogUtil.getLogger().info("停止模式的消息更新数据库成功！");
				}else {
					LogUtil.getLogger().error("停止模式的消息更新数据库失败！");
				}
			} catch (Exception e) {
				LogUtil.getLogger().error("停止模式的消息更新数据出现异常！"+e.getMessage());
			}
		    try {
		    	//更新情景状态
		    	Map hashMap = new HashMap();
		    	hashMap.put("scenarinoId", tasksScenarinoId);
		    	hashMap.put("scenarinoStatus", 5);
		    	int updateStatus = tScenarinoDetailMapper.updateStatus(hashMap);
		    	if (updateStatus>0) {
		    		LogUtil.getLogger().info("id为"+tasksScenarinoId+"的情景终止后更新状态成功！");
				}else {
					LogUtil.getLogger().info("id为"+tasksScenarinoId+"的情景终止后更新状态失败！");
				}
			} catch (Exception e) {
				LogUtil.getLogger().error("停止模式的消息更新情景状态出现异常！"+e.getMessage());
			}
		    if (code.equals(0)) {
				LogUtil.getLogger().info("情景："+tasksScenarinoId+"停止成功！");
			}else {
				LogUtil.getLogger().info("情景："+tasksScenarinoId+"停止失败！");
			}
		} catch (IOException e1) {
			LogUtil.getLogger().error("停止的消息转换异常！"+e1.getMessage(),e1);
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
