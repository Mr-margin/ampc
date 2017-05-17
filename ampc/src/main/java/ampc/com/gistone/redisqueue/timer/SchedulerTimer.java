/**  
 * @Title: RealForecastTimer.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月31日 下午3:12:38
 * @version 
 */
package ampc.com.gistone.redisqueue.timer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






















import net.sf.json.JSONObject;

import org.apache.ibatis.annotations.Select;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.logging.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TGlobalSettingMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TGlobalSetting;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.redisqueue.ReadyData;
import ampc.com.gistone.redisqueue.ToDataUngribUtil;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**  
 * @Title: RealForecastTimer.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: 定时器  实时预报
 * @author yanglei
 * @date 2017年3月31日 下午3:12:38
 * @version 1.0
 * @param <V>
 */
@EnableScheduling
@Component
public class SchedulerTimer<V> {
	private ObjectMapper mapper=new ObjectMapper();
	//加载准备数据工具类
	@Autowired
	private ReadyData readyData;
	//加载任务详情映射
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	//情景详情映射
	@Autowired
	private  TScenarinoDetailMapper tScenarinoDetailMapper;
	//tasksstatus映射
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	//加载globalsetting映射
	@Autowired
	private TGlobalSettingMapper tGlobalSettingMapper;
	
	@Autowired
	private TUngribMapper tUngribMapper;
	
	//读取路径的帮助类
	@Autowired
	private ConfigUtil configUtil;
	//加载dataungributil
	@Autowired
	private ToDataUngribUtil toDataUngribUtil;
	
	
	// 情景区域映射(测试，后面删除）
		@Autowired
		private TScenarinoAreaMapper tScenarinoAreaMapper;
	
	public SchedulerTimer() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * 
	 * @Description: 创建定时任务 每天定时的创建实时预报的情景  时间定在每天的中午12点 整   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月7日 上午9:53:09
	 */
//	@Scheduled(cron="0 0 11 * * ?")
	@Scheduled(cron="0 30 09 * * ?")   //每天9:30定时触发创建实时预报任务---服务器配置
//	@Scheduled(fixedRate = 50000)
	public void realForTimer() {
		boolean runningSetting = configUtil.isRunningSetting();
		LogUtil.getLogger().info("realForTimer:runningSetting:"+runningSetting);
		if (runningSetting) {
			//Date date = new Date();
			LogUtil.getLogger().info("实时预报任务开始");
		//	logger.info("我每天中午12点开始执行");
			Long scenarinoId = null;
			Long cores = null;
			int  i = 0;
			int insertSelective = 0;
			//查找实时时预报任务并修改任务为最新的时间状态 
			List<TGlobalSetting>  list = tGlobalSettingMapper.selectAll();
			for (TGlobalSetting tGlobalSetting : list) {
				System.out.println(tGlobalSetting);
				Long userId = tGlobalSetting.getUserid();
				Integer spinup = tGlobalSetting.getSpinup();
				cores = Long.parseLong(tGlobalSetting.getCores().toString());
				Integer rangeday = tGlobalSetting.getRangeday();
				Long domainId = tGlobalSetting.getDomainId();
				Long esCouplingId = tGlobalSetting.getEsCouplingId();
				//创建任务对象
				TMissionDetail MissionDetail = new TMissionDetail();
				MissionDetail.setMissionName("实时预报任务"+userId);
				LogUtil.getLogger().info("创建了用户"+userId+"实时预报任务");
				MissionDetail.setMissionDomainId(domainId);
				MissionDetail.setEsCouplingId(esCouplingId);
				MissionDetail.setUserId(userId);
				//MissionDetail.setMissionAddTime(DateUtil.DateToDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			//	MissionDetail.setIsEffective("1");
				MissionDetail.setMissionStatus("1");
				//第一次需要创建任务 后面的则是修改 
				List<TMissionDetail> missionlist = tMissionDetailMapper.selectMissionDetail(userId);
				Long missionId = null ;
				
				if (missionlist.isEmpty()) {
					//没有则创建实时预报任务 没有时间段
					//设置任务开始时间
					Date missionStartDate = DateUtil.DateToDate(new Date(), "yyyy-MM-dd");
					//设置开始时间
					MissionDetail.setMissionStartDate(missionStartDate);
					//添加一个新的任务
					int insert = tMissionDetailMapper.insertSelective(MissionDetail);
				    //得到相应的任务ID
					 List<TMissionDetail> selectByEntity = tMissionDetailMapper.selectByEntity(MissionDetail);
					 missionId = selectByEntity.get(0).getMissionId();
					 LogUtil.getLogger().info("创建了一个新的实时预报任务"+missionId);
				}
				
				else {
					//如果不为空 表示至少有一个实时预报任务
					boolean flag = false;
					Long oldMissionid = null;
					boolean escouplingflag = false; 
					Date oldmissionDate = null;
					//清单不一样的时候的旧的任务的ID
					Long oldMissionid_ecgouplingId = null ;
					for (TMissionDetail tMissionDetail : missionlist) {
						//得到所有的该用户下的domain创建的实时预报的任务
						Long missionDomainId = tMissionDetail.getMissionDomainId();
						Long oldesCouplingId = tMissionDetail.getEsCouplingId();
						if (domainId==missionDomainId) {
							if (esCouplingId==oldesCouplingId) {
								//没有跟换domainid同时也没有换清单的情况下 修改任务或者覆盖一次
								//设置任务的修改时间 数据库自己添加
								//得到相应的任务ID
								missionId = tMissionDetail.getMissionId();
								MissionDetail.setMissionId(missionId);
								LogUtil.getLogger().info(missionId+"任务开启了");
								int update = tMissionDetailMapper.updateByPrimaryKeySelective(MissionDetail);
								flag = true;
								escouplingflag= true;
								break;
							}
							if (esCouplingId!=oldesCouplingId) {
								//没有跟换domainid同时更换清单的情况下 新建一个任务
								//旧的任务ID
								oldMissionid_ecgouplingId = tMissionDetail.getMissionId();
								flag = true;
							}
							flag = true;
						}
						else {
							//domainID不一致的时候 记录当条任务ID  domainid 不一样是时候 会让判断清单ID的flag为true 
							escouplingflag = true;
							oldMissionid = tMissionDetail.getMissionId();
						}
					}
					//清单不一致的时候穿件新的任务
					if (escouplingflag==false) {
						String missionend = DateUtil.DATEtoString(new Date(), "yyyy-MM-dd");
						missionend = missionend+" "+"23:59:59";
						Date oldmissionenddate = DateUtil.StrtoDateYMD(missionend, "yyyy-MM-dd HH:mm:ss");
						//设置开始时间
						TMissionDetail updateOlDetail = new TMissionDetail();
						updateOlDetail.setMissionEndDate(oldmissionenddate);
						updateOlDetail.setMissionId(oldMissionid_ecgouplingId);
						updateOlDetail.setIsEffective("0");
						//修改旧任务 相当于删除
						int update = tMissionDetailMapper.updateByPrimaryKeySelective(updateOlDetail);
						//添加新任务
						Date missionDate = DateUtil.DateToDate(new Date(), "yyyyMMdd");
						MissionDetail.setMissionStartDate(missionDate);
						int insertSelec = tMissionDetailMapper.insertSelective(MissionDetail);
						//获取新任务的任务ID
						missionId = tMissionDetailMapper.getmissionidbyMission(MissionDetail);
						LogUtil.getLogger().info("创建了一个新的实时预报任务"+missionId);
					}
					
					if (flag==false) {
						//用户更改了domainid 需要新建一个新的实时预报任务
						String missionend = DateUtil.DATEtoString(new Date(), "yyyy-MM-dd");
						missionend = missionend+" "+"23:59:59";
						Date oldmissionenddate = DateUtil.StrtoDateYMD(missionend, "yyyy-MM-dd HH:mm:ss");
						//设置开始时间
						//查找该用户 该domain 该清单的情况下只能有一个实时预报任务
						TMissionDetail updateOlDetail = new TMissionDetail();
						//添加旧任务的结束时间
						updateOlDetail.setMissionEndDate(oldmissionenddate);
						updateOlDetail.setMissionId(oldMissionid);
						updateOlDetail.setIsEffective("0");
						//修改旧任务 相当于删除
						try {
							int update = tMissionDetailMapper.updateByPrimaryKeySelective(updateOlDetail);
						} catch (Exception e) {
							// TODO: handle exception
							LogUtil.getLogger().error("修改实时预报任务出现异常！该任务的ID是"+oldMissionid);
						}
						Date missionDate = DateUtil.DateToDate(new Date(), "yyyyMMdd");
						MissionDetail.setMissionStartDate(missionDate);
						//添加新任务
						try {
							int insertSelec = tMissionDetailMapper.insertSelective(MissionDetail);
							missionId = tMissionDetailMapper.getmissionidbyMission(MissionDetail);
							if (insertSelec>0) {
								LogUtil.getLogger().info("创建了一个新的实时预报任务"+missionId);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LogUtil.getLogger().error("创建新的实时预报任务出现异常！",e);
						}
						//获取新任务的任务ID
					}
				}
				
				//创建实时预报情景
				TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
				tScenarinoDetail.setScenarinoAddTime(new Date());
				tScenarinoDetail.setScenarinoName("实时预报情景"+DateUtil.DATEtoString(new Date(), "yyyyMMdd"));
				//系统的头一天为情景开始时间
				Date scenarinoStartDate = DateUtil.DateToDate(DateUtil.ChangeDay(new Date(), -1), "yyyyMMdd"); 
				tScenarinoDetail.setScenarinoStartDate(scenarinoStartDate);
				//系统加上rangeday-2为情景结束时间
				int rang = rangeday-2;
				Date scenarinoEndDate = DateUtil.DateToDate( DateUtil.ChangeDay(new Date(), rang),"yyyyMMdd");
				String end = DateUtil.DATEtoString(scenarinoEndDate, "yyyy-MM-dd");
				end = end+" "+"23:59:59";
				scenarinoEndDate = DateUtil.StrtoDateYMD(end, "yyyy-MM-dd HH:mm:ss");
				tScenarinoDetail.setScenarinoEndDate(scenarinoEndDate);
				//设置pathdate
				Date pathDate = DateUtil.DateToDate(new Date(), "yyyyMMdd");
				tScenarinoDetail.setPathDate(pathDate);
				tScenarinoDetail.setScenarinoType((long)4);
				//设置状态 5 表示可执行
				tScenarinoDetail.setScenarinoStatus((long)5);
				tScenarinoDetail.setMissionId(missionId);
				tScenarinoDetail.setUserId(userId);
				tScenarinoDetail.setIsEffective("1");
				//基础日期9
				Date basisTime = DateUtil.ChangeDay(pathDate, -2);
				tScenarinoDetail.setBasisTime(basisTime);
				Map Parmap = new HashMap();
				Parmap.put("userId", userId);
				Parmap.put("pathDate", pathDate);
				//查询当天的情景是否被创建（测试时候定时器时间间隔比较小测试用）
				TScenarinoDetail scen_detail = tScenarinoDetailMapper.getForecastScenID(Parmap);
				if (null!=scen_detail) {
					LogUtil.getLogger().info("实时预报情景已经建立了");
					//检查该情景的运行状态，如果运行fnl出错，则重新运行，如果gfs运行出错，和运行正常，则跳出循环
					//chackRunningStatus(scen_detail);
					break;
				}
				//查询上一天的实时预报情景ID 作为当天的实时预报基础情景
				Date yesDate = DateUtil.ChangeDay(pathDate, -1);
				Long basisScenarinoId = tScenarinoDetailMapper.selectBasisId(yesDate);
				if (null==basisScenarinoId) {
					LogUtil.getLogger().info("系统第一次创建实时预报情景");
				}
				tScenarinoDetail.setBasisScenarinoId(basisScenarinoId);
				tScenarinoDetail.setScenType("4");
				tScenarinoDetail.setSpinup((long)spinup);
				tScenarinoDetail.setRangeDay((long)rangeday);
				tScenarinoDetail.setExpand3(cores.toString());
				try {
					//创建新的情景
					insertSelective = tScenarinoDetailMapper.insertSelective(tScenarinoDetail);
					if (insertSelective>0) {
						LogUtil.getLogger().info("创建了新的实时预报");
					}else {
						throw new SQLException("SchedulerTimer  创建了新的实时预报出错!");
					}
				} catch (Exception e) {
					LogUtil.getLogger().error("SchedulerTimer realForTimer  创建了新的实时预报出错!",e);
				}
				
				//创建情景对应的tasksstatus
				TTasksStatus tTasksStatus = new TTasksStatus();
				try {
					//得到刚新建的情景ID
					scenarinoId = tScenarinoDetailMapper.selectforecastid(tScenarinoDetail);
					tTasksStatus.setTasksScenarinoId(scenarinoId);
					tTasksStatus.setTasksScenarinoStartDate(scenarinoStartDate);
					tTasksStatus.setTasksScenarinoEndDate(scenarinoEndDate);
					tTasksStatus.setTasksRangeDay((long)rangeday);
					//添加该情景到tasksstatus表
					i = tTasksStatusMapper.insertSelective(tTasksStatus);
					if (i>0) {
						LogUtil.getLogger().info("添加预报到tasks表成功！");
					}else {
						throw new SQLException("SchedulerTimer  添加预报到tasks表失败!");
					}
				} catch (Exception e) {
					LogUtil.getLogger().error("系统错误！同一天的实时预报存在多条数据！");
				}
				//调用方法获取meiccityconfig
				String emisParamesURL = configUtil.getEmisParamesURL();
				String getResult=ClientUtil.doPost(emisParamesURL,scenarinoId.toString());
				LogUtil.getLogger().info(getResult+"emisdata params，情景ID："+scenarinoId);
				Map mapResult;
				try {
					mapResult = mapper.readValue(getResult, Map.class);
					LogUtil.getLogger().info(mapResult+"返回值");
					if(mapResult.get("status").toString().equals("success")){
						Map map = (Map) mapResult.get("data");
						String controlfile = map.get("controlfile").toString();
						String meiccityconfig = map.get("meiccityconfig").toString();
//						int flag = 1;//表示不需要减排系数
//						Map<String, String> emis = getEmisData(esCouplingId,scenarinoId,flag);
						TTasksStatus tTasksStatus2 = new TTasksStatus();
						tTasksStatus2.setTasksScenarinoId(scenarinoId);
						tTasksStatus2.setSourceid(esCouplingId.toString());
						//设置calctype是系统设置 目前定义为server
						tTasksStatus2.setCalctype("server");
						tTasksStatus2.setPsal("");
						tTasksStatus2.setSsal("");
						//目前的死数据
						tTasksStatus2.setTasksExpand1(0l);
//						tTasksStatus.setExpand3("/work/modelcloud/lixin_meic/hebei/cf/cf_zero.csv");
//						tTasksStatus.setMeiccityconfig("/work/modelcloud/meic_tool/meic-city.conf");
						tTasksStatus2.setTasksExpand3(controlfile);
						tTasksStatus2.setMeiccityconfig(meiccityconfig);
						//添加该情景到tasksstatus表
						i = tTasksStatusMapper.updateEmisData(tTasksStatus2);
						if (i>0) {
							LogUtil.getLogger().info("添加预报emisData到tasks表成功！");
						}else {
							throw new SQLException("SchedulerTimer  添加预报emisData到tasks表失败!");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					LogUtil.getLogger().error("获取emis参数出错！预报情景ID为："+scenarinoId,e);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				/*boolean emisParams = readyData.getEmisParams(scenarinoId);
				if (emisParams) {
					LogUtil.getLogger().info("实时预报情景请求meiccityconfig参数成功！");
				}else {
					LogUtil.getLogger().info("实时预报情景请求meiccityconfig参数失败！");
				}*/
		/*		if (i>0) {
					//根据情景调动实时预报接口开始实时预报
					readyData.readyRealMessageDataFirst(scenarinoId,cores,userId);
					LogUtil.getLogger().info("实时预报模式启动！");
				}
				
				//修改情景状态为执行模式
				Map map = new HashMap();
				map.put("scenarinoStatus", (long)6);
				map.put("scenarinoId", scenarinoId);
				int updateScenType = tScenarinoDetailMapper.updateScenType(map);
				if(updateScenType>0){
					LogUtil.getLogger().info("实时预报修改执行状态");
				}*/
			}
			
		}else {
			LogUtil.getLogger().info("定时器空执行！");
		}
	}
	/**
	 * 
	 * @Description: 发送实时预报的定时器 由ungrib的成与否来触发当天的数据 每天十点之后 没隔十分钟触发一次 
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月21日 下午7:39:01
	 */
//	@Scheduled(fixedRate = 5000)
	@Scheduled(cron="0 0/10 * * * ?")   //隔10分钟定时检查一次实时预报的发送情况---服务器配置
//	@Scheduled(cron="0 30 10 * * ?")
	public void  sendMessageOnRealprediction() {
		boolean runningSetting = configUtil.isRunningSetting();
		LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:runningSetting:"+runningSetting);
		if (runningSetting) {
			LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:开始检测ungrib的数据");
			 try {
				//获取最新的ungrib 
				 TUngrib tUngrib = tUngribMapper.getlastungrib();
					if (null==tUngrib) {
						LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:没有ungrib数据,系统第一次启动预报！");
					}else {
						//最新的pathdate（年月日）
						Date pathdate = tUngrib.getPathDate();
						 //当天时间
						Date pathdatetoday = DateUtil.DateToDate(new Date(), "yyyyMMdd");
						int length = toDataUngribUtil.UpdateORNo(tUngrib);
						//查找当天所有的用户的实时预报的状态
						Map map = new HashMap();
						String scenType = "4";
						map.put("pathDate", pathdatetoday);
						map.put("scenType", scenType);
						List<TScenarinoDetail>  list = tScenarinoDetailMapper.selectAllByPathdateAndtype(map);
						for (TScenarinoDetail tScenarinoDetail : list) {
							//检查排放数据参数是否存在
							TTasksStatus selectEmisDataByScenId = tTasksStatusMapper.selectEmisDataByScenId(tScenarinoDetail.getScenarinoId());
							if (0==(selectEmisDataByScenId.getTasksExpand1())) {
								Long userId = tScenarinoDetail.getUserId();
								Long rangeDay = tScenarinoDetail.getRangeDay();
								LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:rangeDay的长度是："+rangeDay);
								if (length>=rangeDay) {
									//查找中断的预报时间
									Date lastpathdate = tScenarinoDetailMapper.getlastrunstatus(userId);
									String lastungrib = readyData.pivot(userId, lastpathdate, pathdate);
									if (null!=lastungrib) {
										readyData.readyRealMessageDataFirst(tScenarinoDetail, lastungrib);
										//修改状态为执行中
										readyData.updateScenStatusUtil(6l, tScenarinoDetail.getScenarinoId());
									}else {
										LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:lastungrib对应的实时预报已经发送过了或者当天的ungrib还未更新！");
									}
								}
							}else {
								//重新请求一次emis排放数据
								boolean emisParams = readyData.getEmisParams(tScenarinoDetail.getScenarinoId());
								if (emisParams) {
									LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:重新请求实时预报的emisdata参数成功！");
								}else {
									LogUtil.getLogger().info("定时器 sendMessageOnRealprediction:重新请求实时预报的emisdata参数失败！");
								}
							}	
						}
					}	
			} catch (Exception e) {
				LogUtil.getLogger().error("定时器 sendMessageOnRealprediction 出错！",e);
			}
		}else {
			LogUtil.getLogger().info("sendMessageOnRealprediction:定时器空运行！");
		}
	}
	
	/**
	 * @Description: 检查该情景的运行状态
	 
	 * @param scen_detail   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月20日 上午11:02:42
	 */
	public void chackRunningStatus(TScenarinoDetail scen_detail) {
		Long scenarinoId = scen_detail.getScenarinoId();
		TTasksStatus tTasksStatus = tTasksStatusMapper.selectendByscenarinoId(scenarinoId);
		String errorStatus = tTasksStatus.getModelErrorStatus();
		if (null!=errorStatus) {
			//出错了的情况
			Date tasksEndDate = DateUtil.DateToDate(tTasksStatus.getTasksEndDate(), "yyyyMMdd");
			Date scenarinoStartDate = DateUtil.DateToDate(tTasksStatus.getTasksScenarinoStartDate(), "yyyyMMdd");
			String goingtime = tTasksStatus.getBeizhu2();
			Long userId = scen_detail.getUserId();
			Calendar cal = Calendar.getInstance();
			long day = (tasksEndDate.getTime()-scenarinoStartDate.getTime())/(24*60*60*1000);
			if (day==0) {
				//表示fnl出错  需要重新启动模式
				//readyData.readyRealMessageDataFirst(scenarinoId,null,userId);
				
			}
			
		}
	}


	
	/**
	 * 
	 * @Description: 启动预评估任务--预评估情景的定时器
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月7日 下午8:46:00
	 */
	@Scheduled(cron="0 0/10 * * * ?")  //每隔10分钟定时触发一次预评估任务的检查---服务器配置
//	@Scheduled(fixedRate = 5000)
	public void ForpreEvalution() {
		boolean runningSetting = configUtil.isRunningSetting();
		LogUtil.getLogger().info("定时器 ForpreEvalution:runningSetting:"+runningSetting);
		if (runningSetting) {
			LogUtil.getLogger().info("每隔10分钟执行一次");
			//根据情景的状态和情景的类型确定准备参数
			try {
				//找到每一条启动中的预评估情景
				List<TScenarinoDetail> list = tScenarinoDetailMapper.selectpreEvaluationSituation();
				for (TScenarinoDetail tScenarinoDetail : list) {
					Date pathDate = DateUtil.DateToDate(tScenarinoDetail.getPathDate(), "yyyyMMdd");
					Long userId = tScenarinoDetail.getUserId();
					Date maxtime = readyData.getMaxTimeForMegan(pathDate,userId);
					LogUtil.getLogger().info("定时器 ForpreEvalution-预评估任务最大的气象数据maxtime："+maxtime);
					Date startDate = DateUtil.DateToDate(tScenarinoDetail.getScenarinoStartDate(), "yyyyMMdd");//预评估情景的开始时间
					int compareTo = maxtime.compareTo(startDate);//比较最大的时间和预评估情景的开始时间
					Long scenarinoId = tScenarinoDetail.getScenarinoId();
					//查询该预评估情景执行到哪一步了 
//					TTasksStatus tasksStatus = tTasksStatusMapper.gettaskEnddate(scenarinoId);
					TTasksStatus tasksStatus = tTasksStatusMapper.selectStatus(scenarinoId);
						if (null!=tasksStatus) {
							//当前情景已经发送过得日期
							String sendtime = tasksStatus.getBeizhu2();
							if (sendtime.equals("0")) {
								LogUtil.getLogger().info("该情景尚未发送过一次！");
								if (compareTo>=0) {
									//发送第一次的数据
									boolean flag = readyData.readyPreEvaluationSituationDataFirst(tScenarinoDetail);
									if (flag) {
										LogUtil.getLogger().info("ID为"+scenarinoId+"预评估情景发送成功！本次消息的时间是："+startDate);
									}else {
										LogUtil.getLogger().info("预评估情景发送失败！");
									}
								}else {
									LogUtil.getLogger().info("气象数据不满足");
								}
							}else {
								//当前情景执行到的日期
								//已经发送的消息，正在执行或者执行完毕
								Date ctime = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
								Date nowDate = tasksStatus.getTasksEndDate();
								nowDate = nowDate ==null?ctime:nowDate;
//								Date toSendDate = DateUtil.StrtoDateYMD(DateUtil.changeDate(nowDate, "yyyyMMdd", 1), "yyyyMMdd") ;
								//当前情景在当前执行日期下执行到哪一步
								Long preEvastepindex = tasksStatus.getStepindex();
								preEvastepindex = preEvastepindex == null?0:preEvastepindex;
								//当前情景的结束时间
								Date EndDate = tScenarinoDetail.getScenarinoEndDate();
								//当前任务的错误状态
								String errorStatus = tasksStatus.getModelErrorStatus();
								if(null==errorStatus&&preEvastepindex==4){
									//比较当前情景的模式运行的时间和情景的结束时间大小
									nowDate = DateUtil.DateToDate(nowDate, "yyyyMMdd");
									int compareTo1 = EndDate.compareTo(nowDate);
									//比较当前情景的模式运行的时间时间和情景是否可发送的最大时间
									int compareTo2 = nowDate.compareTo(maxtime);
									//比较当前时间模式运行的时间和已经发送的时间大小 理论上应该是一样大（index=4的时候，并在运行下一条的数据的时候会小一天）
									int compareTo3 = nowDate.compareTo(ctime);
									//compareTo1大于0表示时间在开始时间到结束时间之间 还要继续发送消息  
									//compareTo2小于0表示最新的时间大于该情景正在执行的时间---气象数据满足
									//compareTo3小于等于0表示已经发送过了当条消息 index等于4且compareto3等于0 表示可以发送下一条消息
									//将要发送的消息的时间
//									Date willsendDate = DateUtil.changestrToDate(sendtime, "yyyyMMdd", 1);
									if(compareTo2<0&&compareTo1>0&&compareTo3==0){
										//如果不为空表示当前情景已经发送过了，该时间已经完成了任务  准备下一条数据的时间是当时的时间加一天
//										timedate= DateUtil.ChangeDay(nowDate, 1);
										//准备数据发送消息
										boolean sendDataEvaluationSituationThen = readyData.sendDataEvaluationSituationThen(nowDate, tScenarinoDetail);
										if (sendDataEvaluationSituationThen) {
											LogUtil.getLogger().info("定时器 ForpreEvalution：ID为"+scenarinoId+"预评估情景发送成功！");
										}else {
											LogUtil.getLogger().info("定时器 ForpreEvalution：ID为"+scenarinoId+"预评估情景发送失败！");
										}
									}
									
								}else {
									LogUtil.getLogger().info("定时器 ForpreEvalution：ID为："+scenarinoId+"的预评估情景的本次消息尚未执行完毕！");
								}
							}
						}else {
							LogUtil.getLogger().info("定时器 ForpreEvalution：该情景尚未创建对应的状态表，程序出错了！");
						}
				}
			} catch (Exception e) {
				LogUtil.getLogger().error("定时器 ForpreEvalution：预评估情景定时器查询预评估异常",e);
			}
		}else {
			LogUtil.getLogger().info("定时器 ForpreEvalution:空运行！");
		}
		
		
	}
	


	/**
	 * 
	 * @Description: 定时查错，续跑实时预报
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 上午10:19:16
	 */
//	@Scheduled(fixedRate = 5000)
	public void  test1() {
		/*String url="http://192.168.1.10:8082/ampc/saveEmis";
		HashMap<String,String> hashMap = new HashMap<String, String>();
		hashMap.put("sourceid", "sourceid");
		hashMap.put("psal", "psal-----");
		hashMap.put("meiccityconfig", "meiccityconfig-----");
		hashMap.put("scenarioid", "10000");
		hashMap.put("calctype", "10000");
		hashMap.put("ssal", "10000---");
		JSONObject jsonObject = JSONObject.fromObject(hashMap);
		String getResult=ClientUtil.doPost(url,jsonObject.toString());
		System.out.println(getResult);*/
		/*System.out.println("aaaaaaaa--------");
//		TTasksStatus tasksStatus = tTasksStatusMapper.selectEmisDataByScenId(642l);
		TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(730l);
		System.out.println(selectStatus);*/
//		if (null==tasksStatus) {
//			LogUtil.getLogger().info("没有收到减排系数");
//		}else {
//			System.out.println("123------");
//		}
//		Date lastpathdate = tScenarinoDetailMapper.getlastrunstatus(1l);
//		System.out.println(lastpathdate.toString());
//		TTasksStatus tasksStatus = tTasksStatusMapper.selectEmisDataByScenId(755l);
//		System.out.println(tasksStatus.getTasksExpand3()+"-----------------");
//		 String jpParams = readyData.JPParams(753l,1l);
		
		
		
		
	}


	public Map<String, String> getEmisData(Long sourceid,Long scenarinoId,int flag) {
		
		/*Map<String,String> map = new HashMap<String,String>();
		String url="http://192.168.1.128:8082/ampc/app";
		String getResult=ClientUtil.doPost(url,sourceid.toString());*/
		return null;
	}
	
	/**
	 * 
	 * @Description: 定时查错，续跑实时预报
	 * 1.当前系统时间大于当天的上午七点整 检查当天的实时预报情景，如果出错则自动续跑
	 * 2.当前系统时间小于当天的上午七点整，检查上一天的实时预报情景，如果出错，则自动续跑
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月17日 上午10:23:32
	 */
	
//	@Scheduled(fixedRate = 5000)
//	@Scheduled(cron="0 */3 * * * ") //每隔三个小时检查一次实时预报的情况---服务器配置
	public void continueRealModelprediction() {
		boolean runningSetting = configUtil.isRunningSetting();
		LogUtil.getLogger().info("定时器 ForpreEvalution:runningSetting:"+runningSetting);
		if (runningSetting) {
			LogUtil.getLogger().info("continueRealModelprediction:每隔三个小时检查一次实时预报的情况！");
			//查找当天的实时预报的运行状态
			Date originalDate = DateUtil.DateToDate(new Date(), "yyyyMMdd");
			Date starandDate = DateUtil.changedateByHour(originalDate,7);
			Date nowDate = new Date();
			int compareTo = nowDate.compareTo(starandDate);
			Date pathDate;
			if (compareTo>0) {
				pathDate = originalDate;
				Map map = new HashMap();
				map.put("pathdate", pathDate);
				map.put("type", "4");
				try {
					List<TTasksStatus> selectTasksstatuslist = tTasksStatusMapper.selectTasksstatusByPathdateandtype(map);
					if (null!=selectTasksstatuslist) {
						for (TTasksStatus tTasksStatus : selectTasksstatuslist) {
							String modelErrorStatus = tTasksStatus.getModelErrorStatus();
							Long tasksScenarinoId = tTasksStatus.getTasksScenarinoId();
							TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(tasksScenarinoId);
							if (null!=modelErrorStatus) {
								TUngrib tUngrib = tUngribMapper.getlastungrib();
								String lastungrib =  DateUtil.DATEtoString(tUngrib.getPathDate(), "yyyyMMdd");
								boolean comtinueRealpredict =readyData.continueRealpredict(tScenarinoDetail,lastungrib);
								if (comtinueRealpredict) {
									LogUtil.getLogger().info("continueRealModelprediction:自动续跑实时预报成功！");
									//修改情景状态和清空模式错误信息
									boolean updateScenStatusUtil = readyData.updateScenStatusUtil(6l, tasksScenarinoId);
									if (updateScenStatusUtil) {
										LogUtil.getLogger().info("continueRealModelprediction:续跑后修改状态成功！");
									}else {
										LogUtil.getLogger().info("continueRealModelprediction:续跑后修改状态失败！");
									}
								}else {
									LogUtil.getLogger().info("continueRealModelprediction:自动续跑实时预报失败！");
								}
							}else {
								LogUtil.getLogger().info("continueRealModelprediction:预报运行正常！");
							}
						}
					}else {
						LogUtil.getLogger().info("continueRealModelprediction:当天的预报情景未创建！");
					}
				} catch (Exception e) {
					LogUtil.getLogger().error("continueRealModelprediction: 查询预报情景执行状态出错或者对应的情景的详情出错！（当天）pathdate:"+pathDate,e.getMessage(),e);
				}
			}
			if (compareTo<0) {
				pathDate = DateUtil.ChangeDay(originalDate, -1);
				Map map = new HashMap();
				map.put("pathdate", pathDate);
				map.put("type", "4");
				try {
					List<TTasksStatus> selectTasksstatuslist = tTasksStatusMapper.selectTasksstatusByPathdateandtype(map);
					if (null!=selectTasksstatuslist) {
						for (TTasksStatus tTasksStatus : selectTasksstatuslist) {
							String modelErrorStatus = tTasksStatus.getModelErrorStatus();
							Long tasksScenarinoId = tTasksStatus.getTasksScenarinoId();
							TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(tasksScenarinoId);
							if (null!=modelErrorStatus) {
								TUngrib tUngrib = tUngribMapper.getlastungrib();
								String lastungrib =  DateUtil.DATEtoString(tUngrib.getPathDate(), "yyyyMMdd");
								boolean comtinueRealpredict =readyData.continueRealpredict(tScenarinoDetail,lastungrib);
								if (comtinueRealpredict) {
									LogUtil.getLogger().info("continueRealModelprediction:自动续跑实时预报成功！");
									//修改情景状态和清空模式错误信息
									boolean updateScenStatusUtil = readyData.updateScenStatusUtil(6l, tasksScenarinoId);
									if (updateScenStatusUtil) {
										LogUtil.getLogger().info("continueRealModelprediction:续跑后修改状态成功！");
									}else {
										LogUtil.getLogger().info("continueRealModelprediction:续跑后修改状态失败！");
									}
								}else {
									LogUtil.getLogger().info("continueRealModelprediction:自动续跑实时预报失败！");
								}
							}else {
								LogUtil.getLogger().info("continueRealModelprediction:预报运行正常！");
							}
						}
					}else {
						LogUtil.getLogger().info("continueRealModelprediction:当天的预报情景未创建！");
					}
				} catch (Exception e) {
					LogUtil.getLogger().error("continueRealModelprediction:查询预报情景执行状态出错或者对应的情景的详情出错！（上一天）pathdate:"+pathDate,e.getMessage(),e);
				}
			}
		}else {
			LogUtil.getLogger().info("continueRealModelprediction:空执行！");
		}
	}
}
