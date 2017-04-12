/**  
 * @Title: RealForecastTimer.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月31日 下午3:12:38
 * @version 
 */
package ampc.com.gistone.redisqueue.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.ReadyData;
import ampc.com.gistone.util.DateUtil;

/**  
 * @Title: RealForecastTimer.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: 定时器  实时预报
 * @author yanglei
 * @date 2017年3月31日 下午3:12:38
 * @version 1.0
 */
@EnableScheduling
@Component
public class SchedulerTimer {
	
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
	/*@Autowired
	private TGlobalSettingMapper tGlobalSettingMapper;*/
	
	private Logger logger = Logger.getLogger(this.getClass());

	
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
//	@Scheduled(cron="0 0 20 * * ?")
	@Scheduled(fixedRate = 5000)
	public void realForTimer() {
		//Date date = new Date();
		/*System.out.println("我每天中午12点开始执行");
		logger.info("我每天中午12点开始执行");
		Long scenarinoId = null;
		Long cores = null;
		int  i = 0;
		int insertSelective = 0;
		//查找实时时预报任务并修改任务为最新的时间状态 
		List<TGlobalSetting>  list = tGlobalSettingMapper.selectAll();
		for (TGlobalSetting tGlobalSetting : list) {
			System.out.println(tGlobalSetting);
			Long userId = tGlobalSetting.getUserId();
			Integer spinup = tGlobalSetting.getSpinup();
			cores = Long.parseLong(tGlobalSetting.getCores().toString());
			Integer rangeday = tGlobalSetting.getRangeday();
			Long domainId = tGlobalSetting.getDomainId();
			//创建任务对象
			TMissionDetail MissionDetail = new TMissionDetail();
			MissionDetail.setMissionName("实时预报任务");
			MissionDetail.setMissionDomainId(domainId);
			MissionDetail.setEsCouplingId(tGlobalSetting.getEsCouplingId());
			MissionDetail.setUserId(userId);
			MissionDetail.setMissionAddTime(DateUtil.DateToDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		//	MissionDetail.setIsEffective("1");
			MissionDetail.setMissionStatus("1");
			System.out.println(MissionDetail);
			Map<String,Long> map = new HashMap<String, Long>();
			map.put("userId", userId);
			map.put("missionDomainId", domainId);
			//第一次需要创建任务 后面的则是修改
				List<TMissionDetail> missionlist = tMissionDetailMapper.selectMissionDetail(userId);
			Long missionId = null ;
			
			if (missionlist.isEmpty()) {
				//没有则创建实时预报任务 没有时间段
				//设置任务开始时间
				Date missionStartDate = DateUtil.DateToDate(new Date(), "yyyy-MM-dd HH:mm:ss");
				//设置开始时间
				MissionDetail.setMissionStartDate(missionStartDate);
				//添加一个新的任务
				int insert = tMissionDetailMapper.insertSelective(MissionDetail);
				//得到相应的任务ID
				missionId = tMissionDetailMapper.getmissionid(userId);
				logger.info("创建了一个新的实时预报任务");
				System.out.println("创建了一个新的实时预报任务");
			}*/
			/*else {
				//如果不为空 表示至少有一个实时预报任务
				for (TMissionDetail tMissionDetail : missionlist) {
					Long missionDomainId = tMissionDetail.getMissionDomainId();
					if (domainId!=missionDomainId) {
						//用户更改了domainid 需要新建一个新的实时预报任务
						Date missionDate = DateUtil.DateToDate(new Date(), "yyyyMMdd");
						//添加旧任务的结束时间
						TMissionDetail updateOlDetail = new TMissionDetail();
						updateOlDetail.setMissionEndDate(missionDate);
						updateOlDetail.setMissionId(tMissionDetail.getMissionId());
						//修改旧任务
						int update = tMissionDetailMapper.updateOldMissionDetail(updateOlDetail);
						//设置开始时间
						MissionDetail.setMissionStartDate(missionDate);
						//添加新任务
						int insertSelec = tMissionDetailMapper.insertSelective(MissionDetail);
						//获取新任务的任务ID
						missionId = tMissionDetailMapper.getmissionidbyMission(MissionDetail);
					}else {
						//没有跟换domainid的情况下 修改任务或者覆盖一次
						//设置任务的修改时间
						Date updateDate = new Date();
						MissionDetail.setUpdateTime(updateDate);
						int update = tMissionDetailMapper.updateByPrimaryKeySelective(MissionDetail);
						//得到相应的任务ID
						missionId = tMissionDetail.getMissionId();
					}
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
			//基础日期
			Date basisTime = DateUtil.ChangeDay(pathDate, -1);
			tScenarinoDetail.setBasisTime(basisTime);
			//查询上一天的实时预报情景ID 作为当天的实时预报基础情景
			Long basisScenarinoId = tScenarinoDetailMapper.selectBasisId(basisTime);
			tScenarinoDetail.setBasisScenarinoId(basisScenarinoId);
			tScenarinoDetail.setScenType("4");
			tScenarinoDetail.setSpinup((long)spinup);
			tScenarinoDetail.setRangeDay((long)rangeday);
			tScenarinoDetail.setExpand3(cores.toString());
			//创建新的情景
			insertSelective = tScenarinoDetailMapper.insertSelective(tScenarinoDetail);
			if (insertSelective>0) {
				System.out.println("创建了新的实时预报");
				logger.info("创建了新的实时预报");
			}
			//得到刚新建的情景ID
			scenarinoId = tScenarinoDetailMapper.selectforecastid(tScenarinoDetail);
			//创建情景对应的tasksstatus
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			tTasksStatus.setScenarinoStartDate(scenarinoStartDate);
			tTasksStatus.setScenarinoEndDate(scenarinoEndDate);
			tTasksStatus.setRangeDay((long)rangeday);
			//添加该情景到tasksstatus表
			i = tTasksStatusMapper.insertSelective(tTasksStatus);
			
		}
		if (i>0) {
			//根据情景调动实时预报接口开始实时预报
			readyData.readyRealMessageDataFirst(scenarinoId,cores);
			}*/
//		}
		
		
	}
	
	
	/**
	 * 
	 * @Description: TODO
	 * @return   
	 * Date  获取预评估是否能发送的条件
	 * @throws
	 * @author yanglei
	 * @date 2017年4月7日 下午8:46:00
	 */
/*	@Scheduled(cron="0 0/10 * * * ?")
	public void getMaxTime() {
		System.out.println("我没隔10分钟执行一次");
		//根据情景的状态和情景的类型确定准备参数
		List<TScenarinoDetail> list = tScenarinoDetailMapper.getscenidAndcores();
		//查找可执行的时间
		Date maxtime = tScenarinoDetailMapper.getmaxtime();
		
		
		for (TScenarinoDetail tScenarinoDetail : list) {
			Date startDate = tScenarinoDetail.getScenarinoStartDate();
			Long scenarinoId = tScenarinoDetail.getScenarinoId();
			Long cores = Long.parseLong(tScenarinoDetail.getExpand3());
			//查询该预评估情景执行到哪一步了
			TTasksStatus tasksStatus = tTasksStatusMapper.gettaskEnddate(scenarinoId);
			//当前情景执行到的日期
			Date nowDate = tasksStatus.getTasksEndDate();
			//当前情景已经发送过得日期
			String completetime = tasksStatus.getBeizhu2();
			Date ctime = DateUtil.StrtoDateYMD(completetime, "yyyyMMdd");
			//当前情景在当前执行日期下执行到哪一步
			Long stepindex = tasksStatus.getStepindex();
			//当前情景的结束时间
			Date EndDate = tScenarinoDetail.getScenarinoEndDate();
			//nowDate和任务的坐标以及已经发送过得状态都为空表示第一次发送 该情景模式没跑过
			if (null==nowDate&&null==stepindex&&null==completetime) {
				//第一次发送
				nowDate = startDate;
				//准备数据发送消息
				readyData.readyPreEvaluationSituationDataFirst(scenarinoId, cores);
				//跟新该情景下的startdate时间已经发送完毕
				String time = DateUtil.DATEtoString(nowDate, "yyyyMMdd");
				TTasksStatus tTasksStatus = new TTasksStatus();
				tTasksStatus.setTasksScenarinoId(scenarinoId);
				tTasksStatus.setBeizhu2(time);
				tTasksStatusMapper.updatemessageStatus(tTasksStatus);
			}
			//比较当前情景的时间和情景的结束时间大小
			int compareTo = EndDate.compareTo(nowDate);
			//比较当前情景的时间和情景是否可发送的最大时间
			int compareTo2 = nowDate.compareTo(maxtime);
			//比较当前时间和已经发送的时间大小 理论上应该是一样大
			int compareTo3 = nowDate.compareTo(ctime);
			//compareTo大于0表示时间在开始时间到结束时间之间 还要继续发送消息  
			//compareTo2小于0表示最新的时间大于该情景正在执行的时间 
			//compareTo3小于等于0表示已经发送过了当条消息
			 if(compareTo2<0&&compareTo>0&&compareTo3<=0){
				//如果不为空表示当前情景已经发送过了，该时间已经完成了任务  准备下一条数据的时间是当时的时间加一天
			//	timedate= DateUtil.ChangeDay(nowDate, 1);
				//准备数据发送消息
				readyData.sendDataEvaluationSituationThen(nowDate, scenarinoId);
				//跟新该情景下的startdate时间已经发送完毕
				String time = DateUtil.DATEtoString(nowDate, "yyyyMMdd");
				TTasksStatus tTasksStatus = new TTasksStatus();
				tTasksStatus.setTasksScenarinoId(scenarinoId);
				tTasksStatus.setBeizhu2(time);
				tTasksStatusMapper.updatemessageStatus(tTasksStatus);
			}
		}
		
	}*/
	/**
	 * 
	 * @Description: 测试定时器 每隔5秒开始一次   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 上午10:19:16
	 *//*
	@Scheduled(fixedRate = 5000)
	public String  test1() {
		System.out.println("开始定时任务");
		String name = "zhangsan";
		//System.out.println(name+"定时的name");
		System.out.println("这个时间是"+DateUtil.DATEtoString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		return name;
	}*/
	

}
