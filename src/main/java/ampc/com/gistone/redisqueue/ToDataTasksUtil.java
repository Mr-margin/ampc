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
import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.ConfigUtil;
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
	@Autowired
	private Ruku ruku;
	@Autowired
	private ConfigUtil configUtil;
	@Autowired
	private TUngribMapper tUngribMapper;
	//加载dataungributil
	@Autowired
	private ToDataUngribUtil toDataUngribUtil;

	/**
	 * @Description: TODO
	 * @param result_Start_model   
	 * void  持久化tasks result_start_model 
	 * @throws IOException 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 下午2:05:42
	 */
	public void updateDB(String rpop) {
		LogUtil.getLogger().info("updateDB-model.start.result-处理模式启动的返回结果");
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			Date time = message.getTime();
			//创建tasksstatus对象 
			TTasksStatus tasksStatus = new TTasksStatus();
			//1.验证消息参数的合法性
			Map map =(Map) message.getBody();
			//1.1验证code
			Object codeobject = map.get("code");
			if (RegUtil.CheckParameter(codeobject, "Integer", null, false)) {
				int code = Integer.parseInt(codeobject.toString());
				//1.2验证情景ID
				Object scenarioidparam = map.get("scenarioid");
				if (RegUtil.CheckParameter(scenarioidparam, "Long", null, false)) {
					Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
					//1.3 验证index的合法性
					Object step = map.get("index");
					if (RegUtil.CheckParameter(step, "Integer", null, false)) {
						Integer stepindex = Integer.parseInt(step.toString().trim());
						//1.4验证时间date的正确性
						String endtime = map.get("date").toString();
						if (null!=endtime&&!"".equals(endtime)) {
							Date endtimeDate = null;
							try {
								endtimeDate = DateUtil.StrtoDateYMD(endtime, "yyyyMMdd");
								String endtimeString = endtime+" "+"23:59:59";
								Date tasksEndDate = DateUtil.StrtoDateYMD(endtimeString, "yyyyMMdd HH:mm:ss");
								String errorStatus = (String) map.get("desc");
								//2.验证结束-根据code区别系统错误
								if (code!=0&&code!=1) {
									//模式出错处理
									ModelexecuteErrorHandel(code,tasksScenarinoId,stepindex,endtime,errorStatus);
								}else {
									//3.验证结束 -为保证健壮性code非0时，和错误信息合并
									if (stepindex<=8&&stepindex>0) {
										if (code!=0) {
											errorStatus ="code:"+code+","+errorStatus;
										}
										LogUtil.getLogger().info("time:"+time+"index:"+step+"taskendDate:"+tasksEndDate);
									    tasksStatus.setTasksScenarinoId(tasksScenarinoId);
									    tasksStatus.setStepindex((long)stepindex);
									    tasksStatus.setTasksEndDate(tasksEndDate);
									    tasksStatus.setModelErrorStatus(errorStatus); 
									    LogUtil.getLogger().info("updateDB-model.start.result：开始更新tasksstatus数据库");
									    TTasksStatus oldStatus=null;
									    try {
									    	//查找上一次消息的结束时间
									    	oldStatus = tasksStatusMapper.selectendByscenarinoId(tasksScenarinoId);   //经常出问题的地方
										} catch (Exception e) {
											LogUtil.getLogger().error("");
										}
									    int i = tasksStatusMapper.updateStatus(tasksStatus);
									    LogUtil.getLogger().info("tasksstatus："+tasksStatus);
									    if (i>0) {
									    	LogUtil.getLogger().info("更新tasksstatus成功，情景ID："+tasksScenarinoId);
									    	if (code!=0||!"".equals(errorStatus)) {
									    		//出现错误，模式变为出错
									    		//更新情景状态
									    		//更新状态
									    		readyData.updateScenStatusUtil(9l, tasksScenarinoId);
									    		LogUtil.getLogger().info("updateDB-model.start.result：模式运行出错！情景ID："+tasksScenarinoId);
											}else {
												TScenarinoDetail selectByPrimaryKey=null;
												try {
													//通过情景的ID查找该情景的开始时间结束时间和情景类型
													selectByPrimaryKey = tScenarinoDetailMapper.selecttypetime(tasksScenarinoId);
												} catch (Exception e) {
													LogUtil.getLogger().error("updateDB-model.start.result:查询情景selecttypetime出错！scenarinoId："+tasksScenarinoId,e);
												}
										    	//获取当前情景pathdate 用于确定该条记录是不是补发的
										    	Date pathDate = selectByPrimaryKey.getPathDate();
										    	Date today = DateUtil.DateToDate(new Date(), "yyyyMMdd");
										    	String scentype = selectByPrimaryKey.getScenType();
										    	//根据情景类型确定stepindex的数量
										    	Integer index = surestepindex(scentype);
										    	//4.分情景处理各种情景执行的结果
										    	if (null!=pathDate) {
										    		//预评估情景或者实时预报情景
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
											    	if (code==0&&"4".equals(scentype)&&stepindex==8&&StartCompare==0) {
											    		//实时预报第一天的跑完或者补跑的fnl跑完状态变为1可用
											    		TTasksStatus tasksStatus2 = new TTasksStatus();
											    		tasksStatus2.setBeizhu("1");
											    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
											    		tasksStatusMapper.updateRunstatus(tasksStatus2);
											    		LogUtil.getLogger().info("跟新"+tasksScenarinoId+"的状态为1了"+":"+scentype+",stepindex:"+stepindex);
													}else {
														if(code==0&&compareTo>0&&!scentype.equals("4")){
															//其他情况需要跑完整个情景模式状态才变为2 没跑完为1 
												    		TTasksStatus tasksStatus2 = new TTasksStatus();
												    		tasksStatus2.setBeizhu("1");
												    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
												    		tasksStatusMapper.updateRunstatus(tasksStatus2);
														}else if (code==0&&compareTo==0&&index==stepindex) {
															//模式运行完毕就会变成2
															TTasksStatus tasksStatus2 = new TTasksStatus();
												    		tasksStatus2.setBeizhu("2"); 
												    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
												    		tasksStatusMapper.updateRunstatus(tasksStatus2);
												    		LogUtil.getLogger().info("不是事实预报,情景类型："+scentype+",情景ID："+tasksScenarinoId+"执行完毕！"+index+":"+stepindex);
														}
													}
											    	//code为0的时候是成功的  同时是实时预报类型的情况下 stepindex==8才会发下一条 同时时间小于该任务的结束时间  同时该条情景对应的pathdate是当天才能走这个方法
											    	if (code==0&&"4".equals(scentype)&&stepindex==index&&compareTo>0) {
											    		//获取最新的ungrib 
									    				TUngrib tUngrib = tUngribMapper.getlastungrib();
									    				int length = toDataUngribUtil.UpdateORNo(tUngrib);
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
												    			//检查当天的实时预报是否正在执行（测试出的bug）
												    			TTasksStatus tasksStatus2 = tasksStatusMapper.selectendByscenarinoId(scenarinoId);
												    			//获取当条情景是否在执行
												    			String nowexMo = tasksStatus2.getBeizhu2();
												    			if (nowexMo.trim().equals("0")) {
												    				Long rangeDay = idandcore.getRangeDay();
												    				if (length>=rangeDay) {
												    					//最新的pathdate（年月日）
																		Date pathdate = tUngrib.getPathDate();
													    				//查找中断的预报时间
																		Date lastpathdate = tScenarinoDetailMapper.getlastrunstatus(userId);
																		String lastungrib = readyData.pivot(userId, lastpathdate, pathdate);
													    				if (null!=lastungrib) {
													    					readyData.readyRealMessageDataFirst(idandcore, lastungrib);
																		}
												    				}
																}else {
																	//表示已经发送过当条情景 不用触发
												    				LogUtil.getLogger().info("updateDB-model.start.result:情景ID为："+scenarinoId+"的情景已经发送过消息了！");
																}
															}else {
																LogUtil.getLogger().info("updateDB-model.start.result:当天的实时预报情景还没有创建！");
																//当天的实时预报 还未创建  上一天的gfs可以继续发送消息直到当天的上午7点整
																//上一条的情景ID和完成tasks的时间
																//当前系统时间
																Date nowDate = new Date();
																//当天时间的早上七点
																Date initDate = DateUtil.DateToDate(nowDate, "yyyyMMdd");
																Date upperDate = DateUtil.changedateByHour(initDate,7);
																int goon = nowDate.compareTo(upperDate);
																if (goon>0) {
																	LogUtil.getLogger().info("updateDB-model.start.result:时间超过七点，不能再发送了");
																}else{
																	readyData.sendqueueRealDataThen(tasksEndDate,tasksScenarinoId);
																}
															}
														}
											    		if (pathcompare==0) {
											    			//当时间到当天的时候发当天的实时预报
											    			readyData.sendqueueRealDataThen(tasksEndDate,tasksScenarinoId); 
														}
											    		LogUtil.getLogger().info("updateDB-model.start.result:"+tasksEndDate+"tasks的结束时间");
													}
										    	}
										    	
										    	else {
													//针对其他三种情景（ 预评估的后评估情景 后评估任务的两种情景）不存在pathdate 当接受到消息时候 给每个tasksstatus一个状态
													if (code==0&&"2".equals(scentype)&&index==stepindex) {
														//后评估情景更新状态
														TTasksStatus tasksStatus2 = new TTasksStatus();
											    		tasksStatus2.setBeizhu("2"); 
											    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
											    		tasksStatusMapper.updateRunstatus(tasksStatus2);
													}
													if (code==0&&"3".equals(scentype)&&index==stepindex) {
														//基准情景更新状态
														TTasksStatus tasksStatus2 = new TTasksStatus();
											    		tasksStatus2.setBeizhu("2"); 
											    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
											    		tasksStatusMapper.updateRunstatus(tasksStatus2);
													}
												}
	//-------------------------------------	入库处理---------------------------------------------								    	
										    	
												if (code==0) {
													//基准情景
													if ("3".equals(scentype)) {
														//基准入库
														if (stepindex==3) {
															//气象入库
															ruku.readyRukuparamsBasis(stepindex,tasksScenarinoId,tasksEndDate,oldStatus,1);
														}
														if (stepindex==8) {
															//浓度入库
															ruku.readyRukuparamsBasis(stepindex,tasksScenarinoId,tasksEndDate,oldStatus,0);
														}
													}
													//实时预报
													if ("4".equals(scentype)) {
														if (stepindex==3) {
															//气象入库
															ruku.readyRukuparamsRealPredict(stepindex,tasksScenarinoId,tasksEndDate,1);
														}
														if (stepindex==8) {
															//浓度入库
															ruku.readyRukuparamsRealPredict(stepindex,tasksScenarinoId,tasksEndDate,0);
														}
													}
													//预评估任务的预评估情景
													if ("1".equals(scentype)&&stepindex==4) {
														//浓度入库
														ruku.readyRukuparamsRrePredict(tasksScenarinoId,tasksEndDate);
													}
													//后评估评估情景
													if ("2".equals(scentype)&&stepindex==4) {
														//浓度入库
														ruku.readyRukuparamspostPevtion(tasksScenarinoId,tasksEndDate,oldStatus);
													}
												}
											}
										}else {
											LogUtil.getLogger().error("updateDB-model.start.result:更新taskstatus状态失败!scenarinoId："+tasksScenarinoId);
											throw new SQLException("updateDB-model.start.result 更新taskstatus状态失败!scenarinoId："+tasksScenarinoId);
										}
									}else {
										LogUtil.getLogger().error("更新stepindex参数不合法，不在规定范围内！stepindex:"+stepindex);
										if (stepindex==-1) {
											//表示没有运行成功
											ErrorStatus.Errortips(tasksScenarinoId);
											readyData.updateScenStatusUtil(9l, tasksScenarinoId);
								    		LogUtil.getLogger().info("updateDB-model.start.result：模式运行开始前出错！");
										}
									}
								}
							} catch (Exception e) {
								LogUtil.getLogger().error("updateDB-start.model.result date时间转换异常！endtimeDate："+endtime,e.getMessage(),e);
							}
						}else {
							LogUtil.getLogger().info("updateDB-start.model.result 该条消息date不合法！！date:"+endtime);
						}
					}else {
						LogUtil.getLogger().info("updateDB-start.model.result stepindex参数不合法，不是整形！stepindex:"+step);
					}
				}else {
					LogUtil.getLogger().info("updateDB-start.model.result ScenarinoId参数不合法！ScenarinoId："+scenarioidparam);
				}
			}else {
				LogUtil.getLogger().info("updateDB-start.model.result code参数不合法！code:"+codeobject);
			}
		} catch (IOException e) {
			LogUtil.getLogger().error("updateDB-model.start.result：model.start.result消息转换异常"+e.getMessage());
		}
/*	try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			//创建tasksstatus对象 
			TTasksStatus tasksStatus = new TTasksStatus();
			Date time = message.getTime();
			Map map =(Map) message.getBody();
		    Object codeobject = map.get("code");
		    if (RegUtil.CheckParameter(codeobject, "Integer", null, false)) {
		    	int code = Integer.parseInt(codeobject.toString());
		    	 Object param = map.get("scenarioid");
				 if (RegUtil.CheckParameter(param, "Long", null, false)) {
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
							   if (RegUtil.CheckParameter(step, "Integer", null, false)) {
								   Integer stepindex = Integer.parseInt(step.toString().trim());
								   if (stepindex<=8&&stepindex>0) {
									   String errorStatus = (String) map.get("desc");
									   if (code!=0) {
											errorStatus ="code:"+code+errorStatus;
										}
									   LogUtil.getLogger().info("time:"+time+"index:"+step+"taskendDate:"+tasksEndDate);
									    tasksStatus.setTasksScenarinoId(tasksScenarinoId);
									    tasksStatus.setStepindex((long)stepindex);
									    tasksStatus.setTasksEndDate(tasksEndDate);
									    tasksStatus.setModelErrorStatus(errorStatus);
									    LogUtil.getLogger().info("updateDB-model.start.result：开始更新tasksstatus数据库");
									    try {//找出上一条消息的结果
									    //	String modelresult = tasksStatusMapper.selectStartModelresult(tasksScenarinoId);
										   //查找上一次消息的结束时间
									    	TTasksStatus oldStatus = tasksStatusMapper.selectendByscenarinoId(tasksScenarinoId);   //经常出问题的地方
									    	int i = tasksStatusMapper.updateStatus(tasksStatus);
										    LogUtil.getLogger().info("tasksstatus："+tasksStatus);
										    if(i>0){
										    	LogUtil.getLogger().info("更新tasksstatus成功");
										    	if (code!=0||!errorStatus.equals("")) {
										    		//出现错误，模式变为出错
										    		//更新情景状态
										    		//更新状态
										    		readyData.updateScenStatusUtil(9l, tasksScenarinoId);
										    		LogUtil.getLogger().info("updateDB-model.start.result：模式运行出错！");
												}else {
											    	//当tasksstatus数据库更新成功 并且模式执行成功  发送下一条消息
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
													    	if (code==0&&"4".equals(scentype)&&stepindex==8&&StartCompare==0) {
																//实时预报第一天的跑完或者补跑的fnl跑完状态变为1可用
													    		TTasksStatus tasksStatus2 = new TTasksStatus();
													    		tasksStatus2.setBeizhu("1");
													    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
													    		tasksStatusMapper.updateRunstatus(tasksStatus2);
													    		LogUtil.getLogger().info("跟新"+tasksScenarinoId+"的状态为1了"+":"+scentype+",stepindex:"+stepindex);
															}else {
																if(code==0&&compareTo>0&&!scentype.equals("4")){
																	//其他情况需要跑完整个情景模式状态才变为2 没跑完为1 
														    		TTasksStatus tasksStatus2 = new TTasksStatus();
														    		tasksStatus2.setBeizhu("1");
														    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
														    		tasksStatusMapper.updateRunstatus(tasksStatus2);
																}else if (code==0&&compareTo==0&&index==stepindex) {
																	//模式运行完毕就会变成2
																	TTasksStatus tasksStatus2 = new TTasksStatus();
														    		tasksStatus2.setBeizhu("2"); 
														    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
														    		tasksStatusMapper.updateRunstatus(tasksStatus2);
														    		LogUtil.getLogger().info("不是事实预报"+scentype+index+":"+stepindex);
																}
															}
													    	//code为0的时候是成功的  同时是实时预报类型的情况下 stepindex==8才会发下一条 同时时间小于该任务的结束时间  同时该条情景对应的pathdate是当天才能走这个方法
													    	if (code==0&&"4".equals(scentype)&&stepindex==index&&compareTo>0) {
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
														    					readyData.readyRealMessageDataFirst(idandcore, lastungrib);
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
															}
													    	
														}else {
															//针对其他三种情景（ 预评估的后评估情景 后评估任务的两种情景）不存在pathdate 当接受到消息时候 给每个tasksstatus一个状态
															if (code==0&&"2".equals(scentype)&&index==stepindex) {
																//后评估情景更新状态
																TTasksStatus tasksStatus2 = new TTasksStatus();
													    		tasksStatus2.setBeizhu("2"); 
													    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
													    		tasksStatusMapper.updateRunstatus(tasksStatus2);
															}
															if (code==0&&"3".equals(scentype)&&index==stepindex) {
																//基准情景更新状态
																TTasksStatus tasksStatus2 = new TTasksStatus();
													    		tasksStatus2.setBeizhu("2"); 
													    		tasksStatus2.setTasksScenarinoId(tasksScenarinoId);
													    		tasksStatusMapper.updateRunstatus(tasksStatus2);
															}
															
														}
														//入库处理
														if (code==0) {
															//基准情景
															if ("3".equals(scentype)) {
																//基准入库
																if (stepindex==3) {
																	//气象入库
																	ruku.readyRukuparamsBasis(stepindex,tasksScenarinoId,tasksEndDate,oldStatus,1);
																}
																if (stepindex==8) {
																	//浓度入库
																	ruku.readyRukuparamsBasis(stepindex,tasksScenarinoId,tasksEndDate,oldStatus,0);
																}
															}
															//实时预报
															if ("4".equals(scentype)) {
																if (stepindex==3) {
																	//气象入库
																	ruku.readyRukuparamsRealPredict(stepindex,tasksScenarinoId,tasksEndDate,1);
																}
																if (stepindex==8) {
																	//浓度入库
																	ruku.readyRukuparamsRealPredict(stepindex,tasksScenarinoId,tasksEndDate,0);
																}
															}
															//预评估任务的预评估情景
															if ("1".equals(scentype)&&stepindex==4) {
																//浓度入库
																ruku.readyRukuparamsRrePredict(tasksScenarinoId,tasksEndDate);
															}
															//后评估评估情景
															if ("2".equals(scentype)&&stepindex==4) {
																//浓度入库
																ruku.readyRukuparamspostPevtion(tasksScenarinoId,tasksEndDate,oldStatus);
															}
														}
												}
										    }else {
												LogUtil.getLogger().info("情景ID为："+tasksScenarinoId+"的状态更新失败");
											}
										} catch (Exception e) {
											LogUtil.getLogger().error("查找上一次消息的结束时间：查询模式返回结果状态出错！",e);
										}
								}else {
									LogUtil.getLogger().error("更新stepindex参数不合法，不在规定范围内！stepindex:"+stepindex);
									if (stepindex==-1) {
										//表示没有运行成功
										ErrorStatus.Errortips(tasksScenarinoId);
										readyData.updateScenStatusUtil(9l, tasksScenarinoId);
							    		LogUtil.getLogger().info("updateDB-model.start.result：模式运行出错！");
									}
								}
							}else {
								LogUtil.getLogger().error("stepindex参数不是整形！stepindex:"+step);
							}
						} catch (Exception e) {
							LogUtil.getLogger().error("更新tasksstauts出异常！"+e.getMessage(),e);
						}
						}
					}else {
						LogUtil.getLogger().error("start.model.result ScenarinoId参数错误！");
					}
			}else {
				LogUtil.getLogger().error("start.model.result code参数错误！");
			}
		} catch (Exception e1) {
			LogUtil.getLogger().error("updateDB-model.start.result：tasksresult转换异常"+e1.getMessage());
		}
		*/
	} 

	

	/**
	 * @Description:模式出错处理
	 * @param code
	 * @param tasksScenarinoId
	 * @param stepindex
	 * @param endtime
	 * @param errorStatus   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月19日 上午10:59:21
	 */
	private void ModelexecuteErrorHandel(int code, Long tasksScenarinoId,
			Integer stepindex, String endtime, String errorStatus) {
		//模式运行出错，修改情景状态
		readyData.updateScenStatusUtil(9l, tasksScenarinoId);
		String weixinServerURL = configUtil.getWeixinServerURL();
		switch (code) {
		case 9999:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：系统错误！情景ID："+tasksScenarinoId);
			
			break;
		case 1001:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：参数错误！情景ID："+tasksScenarinoId);
			break;
		case 1002:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：commit job failed！情景ID："+tasksScenarinoId);
			break;
		case 1003:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：start date or end date invalid！情景ID："+tasksScenarinoId);
			break;
		case 1004:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：spinup invalid！情景ID："+tasksScenarinoId);
			break;
		case 1005:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：cores invalid！情景ID："+tasksScenarinoId);
			break;
		case 1006:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：invalid path date！情景ID："+tasksScenarinoId);
			break;
		case 1007:
			LogUtil.getLogger().info("ModelexecuteErrorHandel：job not exist！情景ID："+tasksScenarinoId);
			break;
		default:
			break;
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
			LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：处理停止模式返回的结果方法！");
		    Object object = message.getBody();
		    Map map = (Map) object;
		    Object codeobject = map.get("code");
		    if (RegUtil.CheckParameter(codeobject, "Integer", null, false)) {
		    	Integer code = Integer.parseInt(codeobject.toString());
		    	Object scenarioid = map.get("scenarioid");
		    	if (RegUtil.CheckParameter(scenarioid, "Long", null, false)) {
			    	Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
				    TTasksStatus tTasksStatus = new TTasksStatus();
				    tTasksStatus.setTasksScenarinoId(tasksScenarinoId);
				    tTasksStatus.setStopStatus(code.toString());
//				    tTasksStatus.setStopModelResult(rpop);
				    try {
				    	//更新tasks状态
				    	int i = tasksStatusMapper.updatestopstatus(tTasksStatus);
				    	if (i>0) {
				    		LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：停止模式的消息更新数据库成功！");
						}else {
							LogUtil.getLogger().error("ToDataTasksUtil-stopModelresult：停止模式的消息更新数据库失败！");
						}
					} catch (Exception e) {
						LogUtil.getLogger().error("ToDataTasksUtil-stopModelresult：停止模式的消息更新数据库失败！"+e.getMessage());
					}
				    try {
				    	if (code==0) {
				    		LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：情景："+tasksScenarinoId+"停止成功！");
				    		//更新情景状态
					    	boolean updateScenStatus = readyData.updateScenStatusUtil(5l, tasksScenarinoId);
					    	if (updateScenStatus) {
					    		LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：id为"+tasksScenarinoId+"的情景终止后更新状态成功！");
							}else {
								LogUtil.getLogger().error("ToDataTasksUtil-stopModelresult：id为"+tasksScenarinoId+"的情景终止后更新状态失败！");
							}
						}else {
							//停止失败---失败的处理
							LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：情景："+tasksScenarinoId+"停止失败！");
						}
					} catch (Exception e) {
						LogUtil.getLogger().error("ToDataTasksUtil-stopModelresult：停止模式的消息更新情景状态出现异常！"+e.getMessage());
					}
				}else {
					LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：stop-model-result scenariod 参数错误！");
				}
		    }else {
				LogUtil.getLogger().info("ToDataTasksUtil-stopModelresult：stop-model-result code 参数错误！");
			}
		} catch (IOException e1) {
			LogUtil.getLogger().error("ToDataTasksUtil-stopModelresult：停止的消息转换异常！"+e1.getMessage(),e1);
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



	/**
	 * @Description:处理暂停的结果
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月11日 下午5:12:53
	 */
	public void pauseModelresult(String rpop) {
		Message message;
		try {
			message = JsonUtil.jsonToObj(rpop, Message.class);
			LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult：处理暂停模式返回的结果方法！");
		    Object object = message.getBody();
		    Map map = (Map) object;
		    Object codeobject = map.get("code");
		    if (RegUtil.CheckParameter(codeobject, "Integer", null, false)) {
		    	Integer code = Integer.parseInt(codeobject.toString());
		    	Object scenarioid = map.get("scenarioid");
		    	if (RegUtil.CheckParameter(scenarioid, "Long", null, false)) {
		    		Long tasksScenarinoId = Long.parseLong(map.get("scenarioid").toString());
				    TTasksStatus tTasksStatus = new TTasksStatus();
				    tTasksStatus.setTasksScenarinoId(tasksScenarinoId);
					tTasksStatus.setPauseStatus(code.toString());
				    int updatepausestatus = tasksStatusMapper.updatepausestatus(tTasksStatus);
		    		if (updatepausestatus>0) {
						LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult：更新暂停模式返回的结果成功！");
					}else {
						throw new SQLException("ToDataTasksUtil-pauseModelresult： 更新模式暂停返回的结果失败!");
					}
		    		if (code==0) {
		    			LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult：情景ID为："+tasksScenarinoId+"的暂停处理成功！");
		    			boolean updateScenStatusUtil = readyData.updateScenStatusUtil(7l, tasksScenarinoId);
		    			if (updateScenStatusUtil) {
							LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult:情景ID为："+tasksScenarinoId+"更新情景状态为暂停成功！");
						}else {
							LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult:情景ID为："+tasksScenarinoId+"更新情景状态为暂停失败！");
						}
					}else {
						//暂停失败---失败的处理
						LogUtil.getLogger().info("ToDataTasksUtil-spauseModelresult:情景："+tasksScenarinoId+"暂停处理失败！");
					}
		    	}else {
		    		LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult： scenarioid参数错误！scenarioid："+scenarioid);
				}
		    }else {
				LogUtil.getLogger().info("ToDataTasksUtil-pauseModelresult： code 参数错误！code："+codeobject);
			}
		} catch (IOException e) {
			LogUtil.getLogger().error("ToDataTasksUtil-pauseModelresult：暂停模式的返回结果转换异常！",e);
		} catch (SQLException e) {
			LogUtil.getLogger().error("ToDataTasksUtil-pauseModelresult：更新暂停模式返回结果异常！",e);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
