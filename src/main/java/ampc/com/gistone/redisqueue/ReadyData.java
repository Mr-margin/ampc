/**  
 * @Title: ReadyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 上午11:20:18
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
































































































import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.controller.PlanAndMeasureController;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TPlanMeasureReuseMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.entity.JPResult;
import ampc.com.gistone.redisqueue.entity.QueueBodyData;
import ampc.com.gistone.redisqueue.entity.QueueBodyData2;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.redisqueue.entity.QueueDataCmaq;
import ampc.com.gistone.redisqueue.entity.QueueDataCommon;
import ampc.com.gistone.redisqueue.entity.QueueDataEmis;
import ampc.com.gistone.redisqueue.entity.QueueDataWrf;
import ampc.com.gistone.redisqueue.timer.SchedulerTimer;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: ReadyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 上午11:20:18
 * @version 1.0
 * @param <V>
 */
@Component
public class ReadyData {
	//公用的Jackson解析对象
	private ObjectMapper mapper=new ObjectMapper();
	//读取路径的帮助类
	@Autowired
	private ConfigUtil configUtil;
	//任务详情映射
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	//ungrib映射
	@Autowired
	private  TUngribMapper tUngribMapper;
	//情景详情映射
	@Autowired
	private  TScenarinoDetailMapper tScenarinoDetailMapper;
	//tasks映射
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	@Autowired
	private TScenarinoAreaMapper tScenarinoAreaMapper;
	
	// 预案映射
	@Autowired
	private TPlanMapper tPlanMapper;
	// 预案措施库映射
	@Autowired
	private TPlanMeasureReuseMapper tPlanMeasureReuseMapper;
	
	@Autowired
	private PlanAndMeasureController planAndMeasureController = new PlanAndMeasureController();
	
	// 预案措施映射
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	//引入发送消息的工具类
	@Autowired
	private SendQueueData sendQueueData;
	
	/**
	 * @Description: 根据减排计算的结果触发发送数据是
	 * @param scenarioid   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月17日 上午9:19:32
	 */
	public void needJPsituation(Long scenarinoId) {
		//获取任务ID
		Long missionId = tScenarinoDetailMapper.selectMissionidByID(scenarinoId);
		//获取情景类型和任务类型
		Long scenarinoType = Long.parseLong(tScenarinoDetailMapper.selectscentype(scenarinoId));
		Long missionType = tMissionDetailMapper.selectMissionType(missionId);
		if (scenarinoType==2&&missionType==3) {
			//后评估任务后评估情景
			readypost_PostEvaluationSituationData(scenarinoId,false);
		}
		if (scenarinoType==2&&missionType==2) {
			//预评估的后评估任务
			//readyPrePostEvaluationSituationData(scenarinoId);
			LogUtil.getLogger().info("预评估任务的后评估情景模式开始！");
			readyPrePostEvaluationSituationData(scenarinoId,false);
		} 
		if (scenarinoType==1&&missionType==2) {
			//预评估的预评估任务 交由定时器处理
			
		}
		/*if (scenarinoType==3&&missionType==3) {
			//新基准情景
			readyBaseData(scenarinoId,false);
		}
		if (scenarinoType==4&&missionType==1) {
			//实时预报情景
			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			String lastungrib = readyLastUngrib(tScenarinoDetail.getUserId());
			if (null!=lastungrib) {
				readyRealMessageDataFirst(tScenarinoDetail, lastungrib);
			}else {
				LogUtil.getLogger().info("当天的实时预报已经发送过了！");
			}
		}*/
	}
	/**
	 * @Description: 续跑的方法
	 * @param scenarinoId
	 * @param scenarinoType
	 * @param missionType
	 * @param missionId
	 * @param userId
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月28日 下午4:02:09
	 */
	public boolean continuePredict(Long scenarinoId, Integer scenarinoType,
			Integer missionType, Long missionId, Long userId) {
		boolean flag = false;
		boolean continuemodel = true;
		if (scenarinoType==4&&missionType==1) {
			//准备实时预报的数据(自己测试用的）
			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			String lastungrib = readyLastUngrib(userId);
			boolean comtinueRealpredict = continueRealpredict(tScenarinoDetail,lastungrib);
			flag=comtinueRealpredict;
		}
		if (scenarinoType==3&&missionType==3) {
			//基准情景
			flag =readyBaseData(scenarinoId,continuemodel);
		}
		if (scenarinoType==1&&missionType==2) {
			//预评估任务的预评估情景
			LogUtil.getLogger().info("预评估任务的预评估情景模式开始！");
			flag = readycontinuePreEvaluationSituationData(scenarinoId,continuemodel);
		}
		if (scenarinoType==2&&missionType==2) {
			//预评估任务的后评估情景
			LogUtil.getLogger().info("预评估任务的后评估情景模式开始！");
			flag= readyPrePostEvaluationSituationData(scenarinoId,continuemodel);
		}
		if (scenarinoType==2&&missionType==3) {
			//后评估任务的后评估情景
			flag = readypost_PostEvaluationSituationData(scenarinoId,continuemodel);
		}
		return flag;
	}

	

	/**
	 * 
	 * @Description: 根据情景和任务类型分类准备数据 当这几种情况都不存在的时候返回参数1
	 * @param scenarinoId
	 * @param cores
	 * @param scenarinoType
	 * @param missionType
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 下午2:37:25
	 */
	public String branchPredict(Long scenarinoId,Integer scenarinoType,Integer missionType,Long missionId,Long userId) {
		String str = null;
		if (scenarinoType==4&&missionType==1) {
			//准备实时预报的数据(自己测试用的）
/*			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			String lastungrib = readyLastUngrib(userId);
			if (null!=lastungrib) {
				readyRealMessageDataFirst(tScenarinoDetail, lastungrib);
			}else {
				LogUtil.getLogger().info("当天的实时预报已经发送过了！");
			}*/
			str="ok";
		}
		if (scenarinoType==3&&missionType==3) {
			//基准情景
//			str="ok";
			LogUtil.getLogger().info("基准情景模式开始！");
			boolean emisParams = getEmisParams(scenarinoId);
			if (emisParams) {
				boolean readyBaseData = readyBaseData(scenarinoId,false);
				if (readyBaseData) {
					str="ok";
				}else {
					str="queue_error";
				}
			}else {
				str="error";
			}
		}
		if (scenarinoType==1&&missionType==2) {
			//预评估任务的预评估情景
			//readyPreEvaluationSituationDataFirst(scenarinoId,cores);
			LogUtil.getLogger().info("预评估任务的预评估情景模式开始！");
//			str="ok";
			str = JPParams(scenarinoId,userId);
		}
		if (scenarinoType==2&&missionType==2) {
			//预评估任务的后评估情景  
			LogUtil.getLogger().info("预评估任务的后评估情景模式开始！");
//			readyPrePostEvaluationSituationData(scenarinoId,false);
			str = JPParams(scenarinoId,userId);
		}
		if (scenarinoType==2&&missionType==3) {
			try {
				//后评估任务后评估情景
				//获取能够执行的条件
				//获取该情景的基础的情景ID和时间
				TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
				//对应的基准的ID
				Long basisScenarinoId = scenarinoDetail.getBasisScenarinoId();
				//对应的基础时间
				Date basisTime = scenarinoDetail.getBasisTime();
				TTasksStatus tasksStatus = tTasksStatusMapper.selectendByscenarinoId(basisScenarinoId);
				Long stepindex = tasksStatus.getStepindex();
				Date tasksEndDate = tasksStatus.getTasksEndDate();
				Date scenarinoEndDate = tasksStatus.getTasksScenarinoEndDate();
				Date scenarinoStartDate = tasksStatus.getTasksScenarinoStartDate();
				//int compareTo = scenarinoEndDate.compareTo(tasksEndDate);
				tasksEndDate = tasksEndDate == null ? scenarinoStartDate:tasksEndDate;
				stepindex = stepindex == null? 0:stepindex;
				int compareTo = basisTime.compareTo(tasksEndDate);
				if (stepindex>=4&&compareTo<=0) {
					LogUtil.getLogger().info("后评估任务的后评估情景开始！");
					//请求actionlist
					LogUtil.getLogger().info("请求actionlist，获取减排系数");
					//获取情景的开始时间和结束时间
					TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectStartAndEndDate(scenarinoId);
					Date start = tScenarinoDetail.getScenarinoStartDate();
					String startDate = DateUtil.DATEtoString(start, "yyyy-MM-dd HH:mm:ss");
					Date end = tScenarinoDetail.getScenarinoEndDate();
					String endDate = DateUtil.DATEtoString(end, "yyyy-MM-dd HH:mm:ss");
					//获取减排的json串
					String jpjson = planAndMeasureController.JPUtil(scenarinoId, userId, startDate, endDate);
					if (null!=jpjson) {
						//发送actionlist请求
						String actionlistURL = configUtil.getActionlistURL()+scenarinoId;
						String getResult=ClientUtil.doPost(actionlistURL,jpjson);
						LogUtil.getLogger().info(getResult+"jp action list,情景ID："+scenarinoId);
							Map mapResult=mapper.readValue(getResult, Map.class);
							LogUtil.getLogger().info(mapResult+"返回值");
							if(mapResult.get("status").toString().equals("success")){
								str="ok";
							}else {
								str="error";
							}
					}
				}else {
					str = "false";
					LogUtil.getLogger().info("基准情景尚未运行完毕");
				}
			
			} catch (IOException e) {
				LogUtil.getLogger().error("发送actionlist出异常了  branchPredict",e);
				str = "exception";
			}
		}
		return str;
			
	}
	
	/**
	 * @Description:准备后评估任务的后评估情景的数据
	 * @param body   
	 * void   
	 * 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午3:34:19
	 */
	public boolean readypost_PostEvaluationSituationData(
			Long scenarinoId,boolean continuemodel) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//获取lastungrib
		String lastungrib = "";
		//确定firsttime
		boolean firsttime = false;
		//设置datatype
		String datatype = "fnl";
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//情景类型
		String scenarinoType = scenarinoDetailMSG.getScenType();
		//准备commom数据
		QueueDataCommon commonData = getcommonMSG(scenarinoId, datatype, null, scenarinoType, firsttime, scenarinoDetailMSG);
		//准备wrf数据
		//设置wrf的spinup
		wrfData.setSpinup((long)0);
		//于ungrib无关 用空值代替
		wrfData.setLastungrib(lastungrib);
		//设置cmaq的spinup
		cmaqData.setSpinup((long)0);
		Long missionId = scenarinoDetailMSG.getMissionId();
		//基础情景的时间  基础时间
		Date icDate = scenarinoDetailMSG.getBasisTime();
		//
		String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
		//ic的情景ID
		Long basisScenarinoId = scenarinoDetailMSG.getBasisScenarinoId();
		//ic的任务ID
		Long basismissionid = tScenarinoDetailMapper.selectMissionidByID(basisScenarinoId);
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(basisScenarinoId,basismissionid ,firsttime,icdate); 
		cmaqData.setIc(icMap);
		
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		if (continuemodel) {
			//续跑模式
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.continue");
			//创建消息bady对象   并获取 bodydata的数据
			QueueBodyData2 bodyData2 = getcontinueParams(scenarinoDetailMSG,null);
			//设置body的数据
			bodyData2.setCommon(commonData);
			bodyData2.setWrf(wrfData);
			bodyData2.setCmaq(cmaqData);
			bodyData2.setEmis(DataEmis);
			queueData.setBody(bodyData2);
		}else {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.start");
			//创建消息bady对象   并获取 bodydata的数据
			QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG);
			//设置body的数据
			bodyData.setCommon(commonData);
			bodyData.setWrf(wrfData);
			bodyData.setCmaq(cmaqData);
			bodyData.setEmis(DataEmis);
			queueData.setBody(bodyData);
		}
		
		//预评估任务的后评估情景 时间是一个时间段  存结束时间
		String string = DateUtil.DATEtoString(scenarinoDetailMSG.getScenarinoEndDate(), "yyyyMMdd HH:mm:ss");
		//发送消息到队列
		boolean flag = sendQueueData.toJson(queueData, scenarinoId,string);
		return flag;
	}
	
	
	/**
	 * @Description: TODO
	 * @param body   预评估任务的后评估情景的数据
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 上午11:34:48
	 */
	public boolean readyPrePostEvaluationSituationData(Long scenarinoId,boolean continuemodel) {
		//Long scenarinoId = (Long) body.get("scenarinoId");//情景id
//		String lastungrib = "";
		//确定firsttime
		boolean firsttime = false;
		//设置datatype
		String datatype = "fnl";
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//准备commom数据
		//情景类型
		String scenarinoType = scenarinoDetailMSG.getScenType();
		 QueueDataCommon commonData = getcommonMSG(scenarinoId,datatype,null,scenarinoType,firsttime,scenarinoDetailMSG);
		//准备wrf数据
		//设置wrf的spinup
		wrfData.setSpinup((long)0);
		//获取lastungrib
		wrfData.setLastungrib("");
		//设置cmaq的spinup
		cmaqData.setSpinup((long)0);
		Long missionId = scenarinoDetailMSG.getMissionId();
		//基础情景的时间  基础时间
		Date icDate = scenarinoDetailMSG.getBasisTime();
		//
		String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
		//ic的情景ID
		Long basisScenarinoId = scenarinoDetailMSG.getBasisScenarinoId();
		//ic的任务ID
		Long basismissionid = tScenarinoDetailMapper.selectMissionidByID(basisScenarinoId);
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(basisScenarinoId,basismissionid ,firsttime,icdate); 
		cmaqData.setIc(icMap);
		
		//创建emis对象   调用方法获取emis数据
		//QueueDataEmis dataEmisData = getEmis(missionId);
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		if (continuemodel) {
			//续跑模式
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.continue");
			QueueBodyData2 bodyData2 = getcontinueParams(scenarinoDetailMSG,null);
			bodyData2.setCommon(commonData);
			bodyData2.setWrf(wrfData);
			bodyData2.setCmaq(cmaqData);
			bodyData2.setEmis(DataEmis);
			queueData.setBody(bodyData2);
		}else {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.start");
			//创建消息bady对象   并获取 bodydata的数据
			QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG);
			//设置body的数据
			bodyData.setCommon(commonData);
			bodyData.setWrf(wrfData);
			bodyData.setCmaq(cmaqData);
			bodyData.setEmis(DataEmis);
			queueData.setBody(bodyData);
		}
		//预评估任务的后评估情景 时间是一个时间段  存结束时间
		String string = DateUtil.DATEtoString(scenarinoDetailMSG.getScenarinoEndDate(), "yyyyMMdd HH:mm:ss");
		boolean flag = sendQueueData.toJson(queueData, scenarinoId,string);
		return flag;
	}
	/**
	 * @Description: 续跑实时预报的方法
	 * @param scenarinoId
	 * @param lastungrib   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月6日 上午11:13:28
	 */
	public boolean continueRealpredict(TScenarinoDetail tScenarinoDetail, String lastungrib) {
		boolean continuemodel = true ;
		//查询实时预报的当前的运行状态
		Long scenarinoId=tScenarinoDetail.getScenarinoId();
		Date pathDate = tScenarinoDetail.getPathDate();
		Date scenarinoStartDate = tScenarinoDetail.getScenarinoStartDate();
		Date startdate = DateUtil.DateToDate(scenarinoStartDate, "yyyyMMdd");
		//tasks状态
		TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(scenarinoId);
		Long stepindex = selectStatus.getStepindex();//运行的index
		Date tasksEndDate = selectStatus.getTasksEndDate();//运行的taskenddate
		String compstatus = selectStatus.getBeizhu();//运行状态
		String sendtime = selectStatus.getBeizhu2();//当前运行的消息的time
		
		Date nowrundate = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
		int compareTo = nowrundate.compareTo(pathDate);
		//1.fnl在运行的时候
		if (null==stepindex&&null==tasksEndDate&&compstatus.equals("0")&&sendtime.equals(DateUtil.DATEtoString(startdate, "yyyyMMdd"))) {
			//确定firsttime
			boolean firsttime = getfirsttime(tScenarinoDetail);
			QueueData queueData = readyRealMessageData(tScenarinoDetail, sendtime, firsttime, "fnl", scenarinoId, lastungrib,continuemodel,selectStatus);
			boolean flag = sendQueueData.toJson(queueData, scenarinoId,sendtime);
			return flag;
		}
			//2.gfs在运行的时候
		else if (compareTo>=0) {
			//确定firsttime
			boolean firsttime = getfirsttime(tScenarinoDetail);
			QueueData queueData = readyRealMessageData(tScenarinoDetail, sendtime, firsttime, "gfs", scenarinoId, lastungrib,continuemodel,selectStatus);
			boolean flag = sendQueueData.toJson(queueData, scenarinoId,sendtime);
			return flag;
		}else {
			LogUtil.getLogger().info("续跑失败！");
			return false;
		}
	}
	
	/**
	 * @Description: TODO
	 * @param body   
	 * void  实时预报的参数准备
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午4:32:34
	 */
	public void readyRealMessageDataFirst(TScenarinoDetail scenarinoDetailMSG,String lastungrib) {
		LogUtil.getLogger().info("开始准备第一次的预报数据:第一天的预报数据，类型是fnl");
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		//第一条预报数据的时间
		String time =DateUtil.DATEtoString(startDate , "yyyyMMdd");
		//确定firsttime
		boolean firsttime = getfirsttime(scenarinoDetailMSG);
		String datatype = "fnl";
		QueueData queueData = readyRealMessageData(scenarinoDetailMSG,time,firsttime,datatype,null,lastungrib,false,null);
		sendQueueData.toJson(queueData, scenarinoId,time);
	}
	
	/**
	 * @Description:获取第一次的值
	 * @param scenarinoId
	 * @return   
	 * String  获取firsttime
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午5:44:21
	 */
	private Boolean getfirsttime(TScenarinoDetail scenarinoDetailMSG) {
		//准备firsttime 通过用户ID 情景类型 和pathdate来决定
		Date zoreDate = DateUtil.ChangeDay(scenarinoDetailMSG.getPathDate(), -1);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("pathDate", zoreDate);
		map.put("userId", scenarinoDetailMSG.getUserId());
		map.put("scenType", scenarinoDetailMSG.getScenType());
		TScenarinoDetail scenarinoDetailMSG2 = tScenarinoDetailMapper.selectFirstTime(map);
		boolean firsttime;
		if (scenarinoDetailMSG2!=null) {
			 firsttime = false;
		}else {
			firsttime = true;
		}
		return firsttime;
	}
	/**
	 * @Description: TODO
	 * @param tasksEndDate   
	 * void  实时预报总的准备数据接口 由tasksStatus状态跟新成功之后触发执行
	 * @param scenarinoId 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午3:49:58
	 */
	public void sendqueueRealDataThen(Date tasksEndDate, Long tasksScenarinoId) {
		LogUtil.getLogger().info("发实时预报gfs的数据");
		//消息的time的内容
		String time = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
		LogUtil.getLogger().info("该条消息的时间:"+time);
		String datatype;
	    Long scenarinoId = tasksScenarinoId;
	    //查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		Date pathDate = DateUtil.DateToDate(scenarinoDetailMSG.getPathDate(), "yyyyMMdd");//起报时间
		Date timeDate = DateUtil.StrtoDateYMD(time, "yyyyMMdd");//当天数据的time
		int compareTo = pathDate.compareTo(timeDate);
		if (compareTo<=0) {
			//设置datatype
			datatype = "gfs";
		}else {
			//设置datatype
			datatype = "fnl";
		}
		//firsttime为faslse
		boolean firsttime = getfirsttime(scenarinoDetailMSG);
		Long userId = scenarinoDetailMSG.getUserId();
		//lastungrib
		String lastungrib = readyLastUngrib(userId);
		LogUtil.getLogger().info("sendqueueRealDataThen   实时预报对应的lastungrib："+lastungrib);
		//准备实时预报的参数
		QueueData queueData = readyRealMessageData(scenarinoDetailMSG, time, firsttime, datatype, scenarinoId, lastungrib,false,null);
		sendQueueData.toJson(queueData, scenarinoId,time);
	}
	
	/**
	 * @Description: TODO
	 * @return   
	 * String   准备lastungrib的方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午4:23:17
	 */
	public String  readyLastUngrib(Long userId) {
		LogUtil.getLogger().info("查找最新的ungrib的方法！");
		String lastungrib = null;
		//获取最新的ungrib 
		TUngrib tUngrib = tUngribMapper.getlastungrib();
		//fnl的状态
		if (null==tUngrib) {
			LogUtil.getLogger().info("没有ungrib数据");
		}else {
			//获取最新ungrib 的pathdate 最新的pathdate（年月日）
			Date pathdate = tUngrib.getPathDate();
			//lastungrib = DateUtil.DATEtoString(pathdate, "yyyyMMdd");
			LogUtil.getLogger().info("最新的ungrib"+pathdate);
			//查找是否是连续的   最早的实时预报pathdate
			Date lastpathdate = tScenarinoDetailMapper.getlastrunstatus(userId);
			lastungrib = pivot(userId, lastpathdate, pathdate);
			if (null==lastungrib) {
				//已经发送过了数据
				lastungrib = DateUtil.DATEtoString(pathdate, "yyyyMMdd");
			}
		}
		return lastungrib;
	}

	/**
	 * 
	 * @Description: 确定是开始进入正常的预报还是补发之前的fnl方法
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月25日 下午5:07:43
	 */
	public String  pivot(Long userId,Date lastpathdate,Date pathdate) {
		String lastungrib = null;
		if (null!=lastpathdate) {
			LogUtil.getLogger().info("pivot方法日志:从"+DateUtil.DATEtoString(lastpathdate, "yyyy-MM-dd")+"开始断了");
			LogUtil.getLogger().info("pivot方法日志:pathdate:"+pathdate);
			//将当前的的系统时间转化为于pathdate一样的时间格式（ 年月日形式）
			Date todayDate = DateUtil.DateToDate(new Date(),"yyyyMMdd");
			//比较最新的pathdate和当前系统时间 i=0表示时间一致 小于0表示 时间pathdate小于系统时间
			int i = pathdate.compareTo(todayDate);
			//比较大小
			int compareTo = lastpathdate.compareTo(todayDate);
			//比较最新的ungrib的pathdate和最早没运行的情景的pathdate
			int compareungrib = lastpathdate.compareTo(pathdate);
			Map Parmap = new HashMap();
			Parmap.put("userId", userId);
			Parmap.put("pathDate", lastpathdate);
			//中断了的实时预报的数据
			TScenarinoDetail scenarinoDetailMSG =tScenarinoDetailMapper.getForecastScenID(Parmap);
			if (i==0&&compareTo==0) {
				//今天等于最新的ungrib 没有出现断层的情况--正常情况下
				lastungrib = DateUtil.DATEtoString(pathdate, "yyyyMMdd");
			}
			if (i==0&&compareTo<0) {
				//出现断层的情况 中间存在几天没跑的情况 但是ungrib是最新的 ungrib=today
				lastungrib = DateUtil.DATEtoString(pathdate, "yyyyMMdd");
				bufamessage(scenarinoDetailMSG,lastungrib);
				LogUtil.getLogger().info("pivot方法日志:补发了ID为"+scenarinoDetailMSG.getScenarinoId()+"的情景！");
				//修改情景状态
				updateScenStatusUtil(6l, scenarinoDetailMSG.getScenarinoId());
				lastungrib=null;
			}
			if (i<0&&compareungrib<=0) {
				//最新的ungrib不是最新， 同时最早未运行的情景小于最新的等于ungrib 表示跟新了中间的几个断层
				lastungrib = DateUtil.DATEtoString(pathdate, "yyyyMMdd");
				bufamessage(scenarinoDetailMSG,lastungrib);
				LogUtil.getLogger().info("pivot方法日志:补发了ID为"+scenarinoDetailMSG.getScenarinoId()+"的情景！");
				//修改情景状态
				updateScenStatusUtil(6l, scenarinoDetailMSG.getScenarinoId());
				lastungrib=null;
			}
			if (i<0&&compareTo==0&&compareungrib>0) {
				//表示ungrib不是当天 但是没有出现断层的情况下 等待ungrib跟新  最新未运行的情景是当天 compareTo
				LogUtil.getLogger().info("pivot方法日志:时间："+todayDate+"的ungrib还未跟新！不发送任何数据！");
				lastungrib=null;
			}
			if (i==0&&compareungrib==0&&compareTo<0) {
				//表示ungrib不是当天 但是没有出现断层的情况下 等待ungrib跟新 那天的数据则不发了等下次补发  表示当天的没有跟新
				LogUtil.getLogger().info("pivot方法日志:时间："+todayDate+"的ungrib还未跟新！不发送任何数据！");
				lastungrib=null;
			}
		}else {
			LogUtil.getLogger().info("pivot方法日志:没有未运行的预报数据了！");
		}
		return lastungrib;
	}
	/**
	 * @Description: 补发这个情景的fnl
	 * @param scenarinoDetailMSG   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 上午11:23:09
	 */
	public void bufamessage(TScenarinoDetail scenarinoDetailMSG,String lastungrib) {
		LogUtil.getLogger().info("bufamessage：进入补发方法");
		//设置第一次
		boolean firsttime = false;
		//确定datatype
		String datatype = "fnl";
		//创建消息体对象
		QueueData queueData = new QueueData();
		//设置消息里面的的time和type的值
		queueData = getHeadParameter("model.start");
		//创建消息bady对象   并获取 bodydata的数据
		QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG);
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		String time = DateUtil.DATEtoString(startDate, "yyyyMMdd");
		//准备commom数据
		//情景类型
		String scenarinoType = scenarinoDetailMSG.getScenType();
		QueueDataCommon commonData = getcommonMSG(scenarinoDetailMSG.getScenarinoId(),datatype,time,scenarinoType ,firsttime,scenarinoDetailMSG);
		//准备wrf数据
		wrfData.setSpinup((long)0);
		wrfData.setLastungrib(lastungrib);
		//cmaq的spinup
		cmaqData.setSpinup((long)0);
		//准备cmaq的ic
		Long missionId = scenarinoDetailMSG.getMissionId();
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();
		//设置ic
		//基础情景的时间  基础时间
		Date icDate = scenarinoDetailMSG.getBasisTime();
		String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
	//	String icdate = DateUtil.changeDate(startDate, "yyyyMMdd", -1);
		//ic的情景ID
		Long basisScenarinoId = scenarinoDetailMSG.getBasisScenarinoId();
		//ic的任务ID
		Long basismissionid = tScenarinoDetailMapper.selectMissionidByID(basisScenarinoId);
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(basisScenarinoId,basismissionid ,firsttime,icdate); 
		cmaqData.setIc(icMap);
		//设置emis 调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		
		//设置body的数据
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		bodyData.setEmis(DataEmis);
		queueData.setBody(bodyData);
		//发送消息
		sendQueueData.toJson(queueData, scenarinoId,time);
	}


	/**
	 * @Description: TODO
	 * @param body   
	 * QueueData  准备实时预报的数据 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午10:40:32
	 */
	private QueueData readyRealMessageData(
			TScenarinoDetail scenarinoDetailMSG,String time, boolean firsttime,
			String datatype, Long scenarinoId,String lastungrib,boolean continuemodel,TTasksStatus selectStatus) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		if (null!=scenarinoId) {
			//查找数据库的情景详情的消息通过情景ID
			scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		}
		
		String scenarinoType = scenarinoDetailMSG.getScenType();
		//准备commom数据
		 QueueDataCommon commonData = getcommonMSG(scenarinoId,datatype,time,scenarinoType ,firsttime,scenarinoDetailMSG);
		//准备wrf数据   创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
			if (firsttime==true) {
			//spinup
			Long DBspinup = scenarinoDetailMSG.getSpinup();
			//设置wrf的spinup
			Long spinup = DBspinup+5;
			wrfData.setSpinup(spinup);
			cmaqData.setSpinup(DBspinup);
		}else {
			wrfData.setSpinup(0l);
			cmaqData.setSpinup(0l);
		}
		wrfData.setLastungrib(lastungrib);
		Long missionId = scenarinoDetailMSG.getMissionId();//任务ID
		if (datatype.equals("fnl")) {
			//ic的情景ID
			Long basisScenarinoId = scenarinoDetailMSG.getBasisScenarinoId();
			//ic的任务ID
			Long basismissionid = tScenarinoDetailMapper.selectMissionidByID(basisScenarinoId);
			//准备cmaq的ic
			Date basisTime = scenarinoDetailMSG.getBasisTime();
			String icdate = DateUtil.DATEtoString(basisTime, "yyyyMMdd");
			Map<String, Object> icMap = getIC(basisScenarinoId,basismissionid ,firsttime,icdate); 
			cmaqData.setIc(icMap);
		}
		if (datatype.equals("gfs")) {
			//当条消息的时间
			Date messageDate = DateUtil.StrtoDateYMD(time, "yyyyMMdd");
			String icdate = DateUtil.changeDate(messageDate, "yyyyMMdd", -1);
			Map<String, Object> icMap = getIC(scenarinoId,missionId ,firsttime,icdate); 
			//准备cmaq的ic
			cmaqData.setIc(icMap);
		}
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		if (continuemodel) {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.continue");
			//设置消息里面body节点的主体消息
			QueueBodyData2 bodyData2 = getcontinueParams(scenarinoDetailMSG,selectStatus);
			//设置body的数据
			bodyData2.setCommon(commonData);
			bodyData2.setWrf(wrfData);
			bodyData2.setCmaq(cmaqData);
			bodyData2.setEmis(DataEmis);
			queueData.setBody(bodyData2);
		}else {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.start");
			//设置消息里面body节点的主体消息
			QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG);
			//设置body的数据
			bodyData.setCommon(commonData);
			bodyData.setWrf(wrfData);
			bodyData.setCmaq(cmaqData);
			bodyData.setEmis(DataEmis);
			queueData.setBody(bodyData);
		}
		return queueData;
	}
	
	

	/**
	 * @Description: 接口调用该方法   准备基准情景的参数数据  
	 * @param body   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 上午10:23:18
	 */
	public boolean readyBaseData(Long scenarinoId,boolean continuemodel) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置消息里面的的time和type的值
		LogUtil.getLogger().info("基准情景");
		//查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//情景类型
		String scenarinoType = scenarinoDetailMSG.getScenType();
		Long missionId = scenarinoDetailMSG.getMissionId();//任务id
		//准备commom数据
		QueueDataCommon commonData = getcommonMSG(scenarinoId,"fnl",null,scenarinoType,true,scenarinoDetailMSG);
		//spinup
		Long DBspinup = scenarinoDetailMSG.getSpinup();
		LogUtil.getLogger().info("readyBaseData方法：Dbspinup:"+DBspinup);
		//准备wrf数据
		//设置wrf的spinup
		Long spinup = DBspinup+5;
		LogUtil.getLogger().info("readyBaseData方法：SPINUP:"+spinup);
		wrfData.setSpinup(spinup);
		//准备lastungrib 无关就设置为空
		//String lastungrib = readyLastUngrib();
		wrfData.setLastungrib("");
		//设置cmaq的spinup
		cmaqData.setSpinup(DBspinup);
		//准备ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId ,true,null);
		cmaqData.setIc(icMap);
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		if (continuemodel) {
			queueData = getHeadParameter("model.continue");
			//续跑模式
			QueueBodyData2 bodyData2 = getcontinueParams(scenarinoDetailMSG,null);
			bodyData2.setCommon(commonData);
			bodyData2.setWrf(wrfData);
			bodyData2.setCmaq(cmaqData);
			bodyData2.setEmis(DataEmis);
			queueData.setBody(bodyData2);
		}else {
			queueData = getHeadParameter("model.start");
			//设置消息里面body节点的主体消息
			QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG);
			bodyData.setCommon(commonData);
			bodyData.setWrf(wrfData);
			bodyData.setCmaq(cmaqData);
			bodyData.setEmis(DataEmis);
			queueData.setBody(bodyData);
		}
		
		//预评估任务的后评估情景 时间是一个时间段  存结束时间
		String string = DateUtil.DATEtoString(scenarinoDetailMSG.getScenarinoEndDate(), "yyyyMMdd HH:mm:ss");
		boolean successORfail = sendQueueData.toJson(queueData,scenarinoId,string);
		return successORfail;
	}
	
	
	
	
	/**
	 * @Description: 预评估任务的预评估情景
	 * @param body   
	 * void   这个是总的准备数据 当启动的时候，该方法发送第一条数据进行，而后通过result的结果准备后面的数据
	 * 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午5:37:48
	 */
	public boolean readyPreEvaluationSituationDataFirst(TScenarinoDetail tScenarinoDetail) {
		//本方法准备第一条数据
		Long scenarinoId = tScenarinoDetail.getScenarinoId();//情景id
		//查找数据库的情景详情的消息通过情景ID
//		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		Date startDate = tScenarinoDetail.getScenarinoStartDate();
		Date patDate = DateUtil.DateToDate(tScenarinoDetail.getPathDate(), "yyyyMMdd");
		//比较时间 确定datetype
		int i = startDate.compareTo(patDate);
		String datatype = null;
		if (i<0) {
			 datatype = "fnl";
		}
		//第一次发送数据 ic是基础情景 后面的户数ic是本条情景ID和任务ID
		boolean first= true;
		String time = DateUtil.DATEtoString(startDate, "yyyyMMdd");
		QueueData queueData = readypreEvaSituMessageData(tScenarinoDetail,datatype,time,first,false,null);
		boolean json = sendQueueData.toJson(queueData,scenarinoId, time);
		return json;
	}
	
	/**
	 * @Description: TODO
	 * @param tasksEndDate
	 * @param tasksScenarinoId   
	 * void   预评估任务的预评估情景的第二条数据及后面的参数数据准备
	 * @throws
	 * @author yanglei
	 * @date 2017年3月31日 下午6:38:15
	 */
	public boolean sendDataEvaluationSituationThen(Date tasksEndDate,
			TScenarinoDetail tScenarinoDetail) {
		LogUtil.getLogger().info("发下一条数据");
		//消息的time的内容
		String time = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
//		String time = DateUtil.DATEtoString(tasksEndDate, "yyyyMMdd");
		Long scenarinoId = tScenarinoDetail.getScenarinoId();
		Date today = DateUtil.DateToDate(tScenarinoDetail.getPathDate(), "yyyyMMdd");
		//比较时间 确定datetype
		int i = tasksEndDate.compareTo(today);
		String datatype = null;
		if (i<0) {
			datatype = "fnl";
		}else if(i>=0){
			datatype = "gfs";
		}
		boolean first=false;
		QueueData queueData = readypreEvaSituMessageData(tScenarinoDetail , datatype, time,first,false,null);
		//预评估数据已经跟新了状态了 这里传空
		boolean json = sendQueueData.toJson(queueData,scenarinoId, time);
		return json;
	}
	/**
	 * @Description: 预评估任务的续跑
	 * 	//模式运行未出错的情况下
	 * //模式运行出错的情况下的续跑
	 * @param scenarinoId
	 * @param continuemodel   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月12日 下午7:15:23
	 */
	private boolean readycontinuePreEvaluationSituationData(Long scenarinoId,
			boolean continuemodel) {
		boolean successflag = false ;
		String datatype ;
		String time ;
		boolean first = false;
		try {
			TScenarinoDetail selectByPrimaryKey = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			if (null!=selectByPrimaryKey) {
				TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(scenarinoId);
				if (null!=selectStatus) {
					Long stepindex = selectStatus.getStepindex();
					Date tasksEndDate = selectStatus.getTasksEndDate();
					String sendtime = selectStatus.getBeizhu2();
					String modelErrorStatus = selectStatus.getModelErrorStatus();
					Date pathDate = DateUtil.DateToDate(selectByPrimaryKey.getPathDate(), "yyyyMMdd");
					Date nowDate = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
					//比较已经发送了的时间和pathdate
					int compareTo = pathDate.compareTo(nowDate);
					Date scenarinoStartDate =DateUtil.DateToDate(selectByPrimaryKey.getScenarinoStartDate(), "yyyyMMdd");
					//比较已经发送了的时间和情景的开始时间 等于0表示发的是第一条消息 小于0表示至少是第二条消息
					int compareTo2 = scenarinoStartDate.compareTo(nowDate);
					
					if (compareTo>0) {
						 datatype="fnl";
					}else {
						datatype = "gfs";
					}
					time = sendtime;
					if (null!=modelErrorStatus) {
						if (compareTo2==0) {
							first=true;
						}else {
							first = false;
						}
					}else {
						if (stepindex<4) {
							if (compareTo2==0) {
								first=true;
							}else {
								first=false;
							}
						}
						if (stepindex==4) {
							//至少第一次的消息发送完毕
							first = false;
							time = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
						}
					}
					QueueData queueData = readypreEvaSituMessageData(selectByPrimaryKey, datatype, time, first,continuemodel,selectStatus);
					successflag = sendQueueData.toJson(queueData,scenarinoId,time);
					if (successflag) {
						LogUtil.getLogger().info("readycontinuePreEvaluationSituationData：发送预评估情景续跑到消息队列成功！");
					}else {
						LogUtil.getLogger().info("readycontinuePreEvaluationSituationData：发送预评估情景续跑消息到消息队列失败！");
					}
				}else {
					LogUtil.getLogger().info("readycontinuePreEvaluationSituationData 查找预评估情景的tasks状态为空");
					successflag = false;
				}
			}else {
				LogUtil.getLogger().info("readycontinuePreEvaluationSituationData  查找预评估情景为空!");
				successflag = false;
			}
		} catch (Exception e) {
			 successflag = false;
			 LogUtil.getLogger().error("readycontinuePreEvaluationSituationData :",e.getMessage(),e);
		}
		return successflag;
	}
	/**
	 * @Description: TODO
	 * @return   
	 * QueueData   准备预评估任务 预评估情景的具体数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午4:45:39
	 */
	private QueueData readypreEvaSituMessageData(TScenarinoDetail scenarinoDetailMSG,String datatype,String time,boolean first,boolean continuemodel,TTasksStatus selectStatus) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//查找数据库的情景详情的消息通过情景ID
//		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		String scenarinoType = scenarinoDetailMSG.getScenType();
		//设置firsttime
		boolean firsttime = false;
		//准备commom数据
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();
		QueueDataCommon commonData = getcommonMSG(scenarinoId,datatype,time,scenarinoType,firsttime,scenarinoDetailMSG);
		//设置spinup
		wrfData.setSpinup((long)0);
		cmaqData.setSpinup((long)0);
		//设置lastungrib
		wrfData.setLastungrib("");
		Long missionId = scenarinoDetailMSG.getMissionId();
		if (first==true) {
			//基础情景的时间  基础时间
			Date icDate = scenarinoDetailMSG.getBasisTime();
			//
			String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
			//ic的情景ID
			Long basisScenarinoId = scenarinoDetailMSG.getBasisScenarinoId();
			//ic的任务ID
			Long basismissionid = tScenarinoDetailMapper.selectMissionidByID(basisScenarinoId);
			//准备cmaq的ic
			Map<String, Object> icMap = getIC(basisScenarinoId,basismissionid ,firsttime,icdate); 
			cmaqData.setIc(icMap);
		}else if(first==false){
			Date timedate = DateUtil.StrtoDateYMD(time, "yyyyMMdd");
			String icdate = DateUtil.changeDate(timedate, "yyyyMMdd",-1);
			//准备cmaq的ic
			Map<String, Object> icMap = getIC(scenarinoId,missionId ,firsttime,icdate); 
			cmaqData.setIc(icMap);
		}
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId,scenarinoType,scenarinoId);
		if (continuemodel) {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.continue");
			//创建消息bady对象 设置消息里面body节点的主体消息
			QueueBodyData2 bodyData2 = getcontinueParams(scenarinoDetailMSG,selectStatus);
			bodyData2.setCommon(commonData);
			bodyData2.setWrf(wrfData);
			bodyData2.setCmaq(cmaqData);
			bodyData2.setEmis(DataEmis);
			queueData.setBody(bodyData2);
		}else {
			//设置消息里面的的time和type的值
			queueData = getHeadParameter("model.start");
			//创建消息bady对象 设置消息里面body节点的主体消息
			QueueBodyData bodyDate = getbodyDataHead(scenarinoDetailMSG);
			//设置body的数据
			bodyDate.setCommon(commonData);
			bodyDate.setWrf(wrfData);
			bodyDate.setCmaq(cmaqData);
			bodyDate.setEmis(DataEmis);
			queueData.setBody(bodyDate);
		}
		return queueData;
	}


	/**
	 * @Description: 获取ic的值
	 * 实时预报的情况下 第一次的时候ic为空 其他情况来自基础情景
	 * @param scenarinoId
	 * @param missionId
	 * @param firsttime
	 * @return   
	 * Map<String,Object>  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 下午6:18:52
	 */
	private Map<String, Object> getIC(Long scenarinoId, Long missionId,
			Boolean firsttime,String icdate) {
		Map<String,Object> map = new HashMap<String, Object>();
		//当天的上一天为基础情景
		if(firsttime==true){
			map.put("missionid", "");
			map.put("scenarioid", "");
			map.put("date", "");
		}else {
			map.put("missionid", missionId);
			map.put("scenarioid", scenarinoId);
			map.put("date", icdate);
		}
		return map;
	}
	
	/**
	 * @Description: 获取cindex和cDate
	 * @param scenarinoId   
	 * void  
	 * @param scenarinoType 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午6:02:56
	 */
	private QueueBodyData2 getCindexAndcDate(Long scenarinoId, Integer scenarinoType,TTasksStatus selectStatus) {
		//创建消息bady对象
		QueueBodyData2 bodyData2 = new QueueBodyData2();
		if (null==selectStatus) {
			selectStatus = tTasksStatusMapper.selectStatus(scenarinoId);
		}
		Date scenarinoEndDate = selectStatus.getTasksScenarinoEndDate();
		Date scenarinoStartDate = selectStatus.getTasksScenarinoStartDate();
		Long stepindex = selectStatus.getStepindex();
		stepindex = stepindex ==null?0:stepindex;
		Integer cIndex = null;
		Date tasksEndDate = selectStatus.getTasksEndDate();
		tasksEndDate= tasksEndDate ==null?scenarinoStartDate:tasksEndDate;
		String cDate = null;
		String errorStatus = selectStatus.getModelErrorStatus();
		if (null!=errorStatus) {
			//1.模式执行出错的状态续跑
			cIndex =Integer.valueOf(stepindex.toString()) ;
			cDate = DateUtil.DATEtoString(tasksEndDate, "yyyyMMdd");
		}else {
			//2.正常情况下的续跑
			if (scenarinoType==1) {
				//预评估情景 逐日执行
				int compareTo = tasksEndDate.compareTo(scenarinoEndDate);
				if (stepindex==4) {
					if (compareTo<0) {
						cIndex = 1;
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
				}
				if(stepindex<4){
					if (compareTo<0) {
						//情景中间的某一个index
						cIndex = Integer.valueOf(stepindex.toString())+1;
						cDate = DateUtil.DATEtoString(tasksEndDate, "yyyyMMdd");
					}
				}
			}
			if (scenarinoType==2) {
				//后评估情景 逐模块执行
				int compareTo = tasksEndDate.compareTo(scenarinoEndDate);
				if (stepindex==4) {
					if (compareTo<0) {
						cIndex = Integer.valueOf(stepindex.toString());
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
					/*if (compareTo==0) {
						cDate = DateUtil.DATEtoString(scenarinoStartDate, "yyyyMMdd");
					}*/
				}
				if(stepindex<4){
					if (compareTo<0) {
						cIndex = Integer.valueOf(stepindex.toString());
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
					if (compareTo==0) {
						cIndex = Integer.valueOf(stepindex.toString())+1;
						cDate =  DateUtil.DATEtoString(scenarinoStartDate, "yyyyMMdd");
					}
				}
			}
			if (scenarinoType==3) {
				//新基准情景
				int compareTo = tasksEndDate.compareTo(scenarinoEndDate);
				if (stepindex==8) {
					if (compareTo<0) {
						cIndex = Integer.valueOf(stepindex.toString());
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
				}
				if (stepindex<8){
					if (compareTo<0) {
						cIndex = Integer.valueOf(stepindex.toString());
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
					if (compareTo==0) {
						cIndex = Integer.valueOf(stepindex.toString())+1;
						cDate = DateUtil.DATEtoString(scenarinoStartDate, "yyyyMMdd");
					}
				}
			}
			if (scenarinoType==4) {
				//实时预报情景 逐日执行
				int compareTo = tasksEndDate.compareTo(scenarinoEndDate);
				if (stepindex==8) {
					if (compareTo<0) {
						cIndex = 1;
						cDate = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
					}
				}
				if (stepindex<8) {
					if (compareTo<0) {
						cIndex = Integer.valueOf(stepindex.toString())+1;
						cDate = DateUtil.DATEtoString(tasksEndDate, "yyyyMMdd");
					}
				}
			}
		}
		bodyData2.setcIndex(cIndex);
		bodyData2.setcDate(cDate);
		return bodyData2;
	}
	/**
	 * @Description:续跑的bodydata
	 * @param scenarinoDetailMSG
	 * @return   
	 * QueueBodyData2  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午5:51:32
	 */
	private QueueBodyData2 getcontinueParams(TScenarinoDetail scenarinoDetailMSG,TTasksStatus selectStatus) {
		Integer scenarinoType = Integer.parseInt(scenarinoDetailMSG.getScenType());//情景类型
		Long userId = scenarinoDetailMSG.getUserId();
		Long missionId = scenarinoDetailMSG.getMissionId();//任务id
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();//情景id
		Long cores =Long.parseLong(scenarinoDetailMSG.getExpand3());//计算核数
		//创建消息bady对象
		QueueBodyData2 bodyData2 = getCindexAndcDate(scenarinoId,scenarinoType,selectStatus);
		//调试模式的内容
		Integer flag = configUtil.getModelbodyflagparams();
//		bodyData2.setFlag(1);
		bodyData2.setFlag(flag);
		bodyData2.setCores(cores);
		bodyData2.setUserid(userId.toString());
		bodyData2.setScenarioid(scenarinoId.toString());
		bodyData2.setMissionid(missionId.toString());
		//从任务表里面获取当条任务的详细内容
		TMissionDetail mission = tMissionDetailMapper.selectByPrimaryKey(missionId);
		String missiontype = mission.getMissionStatus().toString();
		//准备modeltype数据
		String modeltype = getmodelType(missiontype,scenarinoType);
		bodyData2.setModeltype(modeltype);
		//获取domainId
		Long domainId = mission.getMissionDomainId();
		bodyData2.setDomainid(domainId.toString());//domainid
		return bodyData2;
	}
	/**
	 * @Description: 设置消息里面body节点的主体消息
	 * @param body
	 * @return   
	 * QueueBodyData  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月24日 下午6:21:55
	 */
	private QueueBodyData getbodyDataHead(TScenarinoDetail scenarinoDetailMSG) {
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		//调试模式的内容
		Integer flag = configUtil.getModelbodyflagparams();
//		bodyData.setFlag(1);
		bodyData.setFlag(flag); 
		Integer scenarinoType = Integer.parseInt(scenarinoDetailMSG.getScenType());//情景类型
		Long userId = scenarinoDetailMSG.getUserId();
		Long missionId = scenarinoDetailMSG.getMissionId();//任务id
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();//情景id
		Long cores =Long.parseLong(scenarinoDetailMSG.getExpand3());//计算核数
		bodyData.setCores(cores);
		bodyData.setUserid(userId.toString());
		bodyData.setScenarioid(scenarinoId.toString());
		bodyData.setMissionid(missionId.toString());
		//从任务表里面获取当条任务的详细内容
		TMissionDetail mission = tMissionDetailMapper.selectByPrimaryKey(missionId);
		String missiontype = mission.getMissionStatus().toString();
		//准备modeltype数据
		String modeltype = getmodelType(missiontype,scenarinoType);
		bodyData.setModeltype(modeltype);
		//获取domainId
		Long domainId = mission.getMissionDomainId();
		bodyData.setDomainid(domainId.toString());//domainid
		return bodyData;
	}
	/**
	 * @Description: 设置消息体里面的时间和ID
	 * @return   
	 * QueueData  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月24日 下午6:15:49
	 */
	private QueueData getHeadParameter(String type) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//消息时间为当前的系统时间（北京时间 ）
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		queueData.setId(UUID.randomUUID().toString());//设置消息id
		queueData.setTime(time);//设置消息时间
		queueData.setType(type);
		return queueData;
	}
	/**
	 * @Description: 通过任务类型和情景类型确定modeltype的类型
	 * @param missiontype
	 * @param scenarinoType
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月24日 下午5:47:28
	 */
	private String getmodelType(String missiontype, Integer scenarinoType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(missiontype);
		buffer.append("_");
		buffer.append(scenarinoType);
		String modeltype = buffer.toString();// modeltype 1_4       拼接modeltype的类型
		return modeltype;
	}
	/**
	 * @Description: QueueDataCommon  获取实时预报的common数据
	 * @param scenarinoId
	 * @param lastfnl
	 * @param scenarinoType   
	 * @param scenarinoDetailMSG2 
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午11:31:35
	 */
	private QueueDataCommon getcommonMSG(Long scenarinoId, String datatype,String time,
			String scenarinoType,Boolean firsttime, TScenarinoDetail scenarinoDetailMSG) {
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		Map<String,String> map = new HashMap<String, String>();
		Integer scenType = Integer.parseInt(scenarinoType);
		//如果是实时预报的类型，开始的时间和结束的时间以及datatype都是来自上面传来的参数，其他类型则来自查询得到的开始和结束时间
		if (scenType==4||scenType==1) {
			//设置类型
			commonData.setDatatype(datatype);
			//设置开始结束时间
			map.put("start", time);
			map.put("end", time);
			commonData.setTime(map);
			//设置firsttime
			commonData.setFirsttime(firsttime);
			//设置起报日期
			String pathdate = DateUtil.DATEtoString(scenarinoDetailMSG.getPathDate(), "yyyyMMdd");
			commonData.setPathdate(pathdate);
			//hashmap.put("commonData", commonData);
		}else {
			//获取情景开始时间
			Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
			//获取情景结束时间
			Date endDate = scenarinoDetailMSG.getScenarinoEndDate();
			//获取情景起报时间
			Date pathDate = scenarinoDetailMSG.getPathDate();
			//pathdate 只有实时预报和预评估是有值的其他都是没值的
			String pathdate = null;
			if (null==pathDate) {
				pathdate = "99999999";
			}
			//设置气象数据类型
			commonData.setDatatype(datatype);
			//设置firsttime
			commonData.setFirsttime(firsttime);
			
		  //  pathdate = DateUtil.DATEtoString(pathDate,"yyyyMMdd");
			String starttime = DateUtil.DATEtoString(startDate,"yyyyMMdd");
			String endtime = DateUtil.DATEtoString(endDate,"yyyyMMdd");
			//设置起报时间
			commonData.setPathdate(pathdate);
			map.put("start", starttime);
			map.put("end", endtime);
			//设置时间
			commonData.setTime(map);
			//hashmap.put("commonData", commonData);
		}
		
		return commonData;
		
	}
	
	
	/**
	 * @Description: 设置emis数据的方法 从数据库里面取
	 * @param missionId
	 * @return   
	 * QueueDataEmis  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午6:50:19
	 */
	private QueueDataEmis getDataEmis(Long missionId,String scenarinoType,Long scenarinoId) {
		Long sourceid = null;
		TTasksStatus tasksStatus = null;
		try {
			//通过情景ID获取清单ID
			sourceid = tMissionDetailMapper.getsourceid(missionId);
			//通过情景ID获取对应的减排系数
			tasksStatus = tTasksStatusMapper.selectEmisDataByScenId(scenarinoId);
		} catch (Exception e) {
			LogUtil.getLogger().error("ReadyData getDataEmis方法： 查询减排系数异常！ ",e);
		}
		
		if (null==tasksStatus) {
			LogUtil.getLogger().info("没有收到减排系数");
		}
		//获取减排清单 从数据库里面取
		
	//	Map<String, String> emis = getEmis(sourceid);
		//创建对象
		QueueDataEmis  DataEmis = new QueueDataEmis();
		//设置sourceid
		DataEmis.setSourceid(sourceid.toString());
		//设置Calctype 计算方式
		DataEmis.setCalctype("server");//cache
		//实时预报和基准情景不需要取减排系数 设置为空
		if ("3".equals(scenarinoType.trim())) {
			//
			DataEmis.setPsal("");
			//
			DataEmis.setSsal("");
		}else if("4".equals(scenarinoType.trim())){
			//
			DataEmis.setPsal("");
			//
			DataEmis.setSsal("");
		}else {
			//其他情景需要设置减排系数 从数据库里面取
			//DataEmis.setPsal(tasksStatus.getPsal());
			DataEmis.setPsal("");
			//DataEmis.setSsal(tasksStatus.getSsal());
			DataEmis.setSsal("");
		}
	
//		DataEmis.setMeiccityconfig(tasksStatus.getMeiccityconfig());
		DataEmis.setMeiccityconfig("/work/modelcloud/meic_tool/meic-city.conf");
//		DataEmis.setControlfile(tasksStatus.getExpand3());
		DataEmis.setControlfile("/work/modelcloud/lixin_meic/hebei/cf/cf_zero.csv");
		return DataEmis;
	}

	/**
	 * 
	 * @Description: 修改情景状态放  
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 下午2:25:25
	 */
	public boolean updateScenStatusUtil(Long status,Long scenarinoId) {
		boolean flag ;
		//修改情景状态为执行模式
		Map map = new HashMap();
		map.put("scenarinoStatus", status);
		map.put("scenarinoId", scenarinoId);
		try {
			int updateScenType = tScenarinoDetailMapper.updateScenType(map);
			if(updateScenType>0){
				LogUtil.getLogger().info("readyData-updateScenStatusUtil：修改情景id为:"+scenarinoId+"的状态成功");
				flag = true;
			}else {
				LogUtil.getLogger().info("readyData-updateScenStatusUtil：修改情景id为:"+scenarinoId+"的状态失败");
				flag = false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
			LogUtil.getLogger().error("readyData-updateScenStatusUtil：修改情景id为:"+scenarinoId+"的状态出现异常",e);
		}
		return flag;
	}
	/**
	 * 
	 * @Description: 获取emis的参数mecityconfig等
	 * @param scenarinoId
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月10日 下午2:39:22
	 */
	public boolean getEmisParams(Long scenarinoId) {
		boolean flag;
		try {
			String emisParamesURL = configUtil.getEmisParamesURL();
			String getResult=ClientUtil.doPost(emisParamesURL,scenarinoId.toString());
			LogUtil.getLogger().info(getResult+"emisdata params，情景ID："+scenarinoId);
			Map mapResult = mapper.readValue(getResult, Map.class);
			LogUtil.getLogger().info(mapResult+"返回值");
			if(mapResult.get("status").toString().equals("success")){
				Map map = (Map) mapResult.get("data");
				String controlfile = map.get("controlfile").toString();
				String meiccityconfig = map.get("meiccityconfig").toString();
				TTasksStatus tTasksStatus = new TTasksStatus();
				tTasksStatus.setMeiccityconfig(meiccityconfig);
				//setControlfile
				tTasksStatus.setTasksExpand3(controlfile);
				tTasksStatus.setTasksScenarinoId(scenarinoId);
				//获取emis数据成功
				tTasksStatus.setTasksExpand1(0l);
				int	i = tTasksStatusMapper.updateEmisData(tTasksStatus);
				if (i>0) {
					flag=true;
				}else {
					throw new SQLException("readyData-getEmisParams  减排系数存库失败!情景ID："+scenarinoId);
				}
			}else {
				LogUtil.getLogger().error("readyData-getEmisParams   获取emis参数出错！情景ID为："+scenarinoId);
				flag=false;
			}
		} catch (Exception e) {
			flag=false;
			LogUtil.getLogger().error("readyData-getEmisParams  获取emis参数出错！情景ID为："+scenarinoId,e);
		}
		return flag;
	}
	/**
	 * 
	 * @Description: 请求actionlist的公共方法
	 * @param scenarinoId
	 * @param userId
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月10日 下午3:58:22
	 */
	public String JPParams(Long scenarinoId,Long userId) {
		String str = null ;
		try {
			//预评估任务的预评估情景
			//请求actionlist
			LogUtil.getLogger().info("JPParams:请求actionlist，获取减排系数");
			//获取情景的开始时间和结束时间
			TScenarinoDetail tScenarinoDetail = tScenarinoDetailMapper.selectStartAndEndDate(scenarinoId);
			Date start = tScenarinoDetail.getScenarinoStartDate();
			String startDate = DateUtil.DATEtoString(start, "yyyy-MM-dd HH:mm:ss");
			Date end = tScenarinoDetail.getScenarinoEndDate();
			String endDate = DateUtil.DATEtoString(end, "yyyy-MM-dd HH:mm:ss");
			//获取减排的json串
			String jpjson = planAndMeasureController.JPUtil(scenarinoId, userId, startDate, endDate);
			if (null!=jpjson) {
				//发送actionlist请求
				String actionlistURL = configUtil.getActionlistURL()+scenarinoId;
				String getResult=ClientUtil.doPost(actionlistURL,jpjson);
				LogUtil.getLogger().info(getResult+"jp action list，情景ID："+scenarinoId);
					Map mapResult=mapper.readValue(getResult, Map.class);
					LogUtil.getLogger().info(mapResult+"返回值");
					if(mapResult.get("status").toString().equals("success")){
						str="ok";
					}else {
						str="error";
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.getLogger().error("JPParams:发送actionlist出异常了",e);
			str = "exception";
		}
		return str;
	}

	/**
	 * @Description: 查询对应的气象数据
	 * @param pathDate
	 * @param userId   
	 * void  
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月6日 下午8:16:27
	 */
	public Date getMaxTimeForMegan(Date pathDate, Long userId) {
		Date maxtime = null;
		//查找可执行的时间 
		Map map = new HashMap();
		map.put("pathdate", pathDate);
		map.put("userId", userId);
		map.put("type", "4");
		TTasksStatus selectTasksstatusByPathdate=null;
		try {
			//查询对应的实时预报的状态
			selectTasksstatusByPathdate = tTasksStatusMapper.selectTasksstatusByPathdate(map);
		} catch (Exception e) {
			LogUtil.getLogger().error("getMaxTimeForMegan:预评估情景的定时器    查询实时预报的状态出异常了",e);
		}
		String sendtime = selectTasksstatusByPathdate.getBeizhu2();//发送了消息的时间
		Date scenarinoStartDate = DateUtil.DateToDate(selectTasksstatusByPathdate.getTasksScenarinoStartDate(), "yyyyMMdd"); 
		if (!sendtime.equals("0")) {
			Long stepindex = selectTasksstatusByPathdate.getStepindex();//预报情景进行到了index
			stepindex = stepindex == null ?0:stepindex;
			Date sendDate = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
			Date tasksEndDate = selectTasksstatusByPathdate.getTasksEndDate();//预报情景进行到的时间
//			int compareTo = sendDate.compareTo(scenarinoStartDate);//发送的时间和情景开始时间比较
			if (stepindex<4) {
				maxtime =DateUtil.DateToDate(DateUtil.ChangeDay(tasksEndDate, -1),"yyyyMMdd");
			}
			if (stepindex>=4&&stepindex<=8) {
				maxtime =DateUtil.DateToDate(tasksEndDate,"yyyyMMdd");
			}
		}else {
			maxtime = DateUtil.DateToDate(DateUtil.ChangeDay(scenarinoStartDate, -1),"yyyyMMdd");
			LogUtil.getLogger().info("getMaxTimeForMegan:实时预报情景尚未开始运行！");
		}
		return maxtime;
	}
	
	
}
