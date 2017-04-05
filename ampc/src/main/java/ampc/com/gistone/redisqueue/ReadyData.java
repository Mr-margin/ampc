/**  
 * @Title: ReadyData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 上午11:20:18
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import oracle.net.aso.q;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

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
	//引入发送消息的工具类
	@Autowired
	private SendQueueData sendQueueData;
	
	
	
	/**
	 * @Description: TODO
	 * @param body   
	 * void   准备后评估任务的后评估情景的数据
	 * 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午3:34:19
	 */
	public void readypost_PostEvaluationSituationData(
			Long scenarinoId,Long cores) {
		//创建消息体对象
		QueueData queueData = new QueueData();
	//	Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//获取lastungrib
		String lastungrib = "";
		//确定firsttime
		boolean firsttime = false;
		//设置datatype
		String datatype = "fnl";
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//创建消息bady对象   并获取 bodydata的数据
		QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG,cores);
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置消息里面的的time和type的值
		queueData = getHeadParameter();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId, datatype, null, scenarinoDetailMSG.getScenType(), firsttime, scenarinoDetailMSG);
		commonData = (QueueDataCommon) map.get("commonData");
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
		//准备ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId ,firsttime,icdate);
		cmaqData.setIc(icMap);
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId);
		//设置body的数据
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		bodyData.setEmis(DataEmis);
		queueData.setBody(bodyData);
		//发送消息到队列
		sendQueueData.toJson(queueData, scenarinoId);
	}
	
	
	/**
	 * @Description: TODO
	 * @param body   预评估任务的后评估情景的数据
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 上午11:34:48
	 */
	public void readyPrePostEvaluationSituationData(Long scenarinoId,Long cores) {
		//Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//获取lastungrib
		String lastungrib = "";
		//确定firsttime
		boolean firsttime = false;
		//设置datatype
		String datatype = "fnl";
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建消息bady对象   并获取 bodydata的数据
		QueueBodyData bodyData = getbodyDataHead(scenarinoDetailMSG,cores);
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置消息里面的的time和type的值
		queueData = getHeadParameter();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,datatype,null,scenarinoDetailMSG.getScenType() ,firsttime,scenarinoDetailMSG);
		commonData = (QueueDataCommon) map.get("commonData");
		//准备wrf数据
		//设置wrf的spinup
		wrfData.setSpinup((long)0);
		wrfData.setLastungrib(lastungrib);
		//设置cmaq的spinup
		cmaqData.setSpinup((long)0);
		Long missionId = scenarinoDetailMSG.getMissionId();
		//基础情景的时间  基础时间
		Date icDate = scenarinoDetailMSG.getBasisTime();
		//
		String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
		//准备ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId ,firsttime,icdate);
		cmaqData.setIc(icMap);
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId);
		//设置body的数据
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		bodyData.setEmis(DataEmis);
		queueData.setBody(bodyData);
		sendQueueData.toJson(queueData, scenarinoId);
	}
	
	
	/**
	 * @Description: TODO
	 * @param body   
	 * void  实时预报的参数准备
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午4:32:34
	 */
	public void readyRealMessageDataFirst(Long scenarinoId,Long cores) {
		//Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//获取lastungrib
		String lastungrib = readyLastUngrib();
		//确定firsttime
		boolean firsttime = getfirsttime(scenarinoId);
		
		String datatype = "fnl";
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		//第一条预报数据的时间
		String time =DateUtil.DATEtoString(startDate , "yyyyMMdd");
		String icdate = DateUtil.changeDate(startDate, "yyyyMMdd", -1);
		QueueData queueData = readyRealMessageData(datatype, time, scenarinoId, lastungrib, firsttime, icdate,cores);
		sendQueueData.toJson(queueData, scenarinoId);
	}
	
	
	
	/**
	 * @Description: TODO
	 * @param scenarinoId
	 * @return   
	 * String  获取firsttime
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午5:44:21
	 */
	private Boolean getfirsttime(Long scenarinoId) {
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//准备firsttime 通过用户ID 情景类型 和pathdate来决定
		Date zoreDate = DateUtil.ChangeDay(scenarinoDetailMSG.getPathDate(), -1);
		Map<String,Object> map2 = new HashMap<String, Object>();
		map2.put("pathDate", zoreDate);
		map2.put("userId", scenarinoDetailMSG.getUserId());
		map2.put("scenType", scenarinoDetailMSG.getScenType());
		TScenarinoDetail scenarinoDetailMSG2 = tScenarinoDetailMapper.selectFirstTime(map2);
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
	public void sendqueueRealData(Date tasksEndDate, Long tasksScenarinoId) {
		System.out.println("发第二条数据");
		//firsttime为faslse
		boolean firsttime = false;
		//消息的time的内容
		String time = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
		System.out.println(time+"该条消息的时间");
		//lastungrib
		String lastungrib = readyLastUngrib();
		
		//设置datatype
		
		String datatype = "gfs";
		Date iCDate = tasksEndDate;
		String icdate = DateUtil.DATEtoString(iCDate, "yyyyMMdd");
	    Long scenarinoId = tasksScenarinoId;
		//	QueueData queueData = readyRealMessageData(datatype,time,body,lastungrib,firsttime,iCDate);
		//准备实时预报的参数
		QueueData queueData = readyRealMessageData(datatype, time, scenarinoId, lastungrib, firsttime, icdate,null);
		sendQueueData.toJson(queueData, scenarinoId);
	}
	/**
	 * @Description: TODO
	 * @return   
	 * String   准备lastungrib的方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午4:23:17
	 */
	private String readyLastUngrib() {
		String lastungrib = null;
		//获取最新的ungrib 
		TUngrib tUngrib = tUngribMapper.getlastungrib();
		//fnl的状态
		Integer fnlStatus = tUngrib.getFnlStatus();
		//gfs的状态   如果出现断层的情况 gfs 状态为空
		Integer gfs1Status = tUngrib.getGfs1Status();
		//获取最新pathdate
		Date pathdate = tUngrib.getPathDate();
		//最新的pathdate（年月日）
		Date pathDate = pathdate;
		//将当前的的系统时间转化为于pathdate一样的时间格式（ 年月日形式）
		Date todayDate = DateUtil.DateToDate(new Date(),"yyyyMMdd");
		//比较最新的pathdate和当前系统时间
		int i = pathDate.compareTo(todayDate);
		//i=0表示时间一致 小于0表示 时间pathdate小于系统时间
		
		if(i==0){
			lastungrib = DateUtil.DATEtoString(pathDate, "yyyyMMdd");
		}else if (i==-1) {
			//最新的ungrib小于今天  只是今天未更新的情况    不发送 等待状态  
			// 当 当天的ungrib跟新之后 开始实施实时预报
			
			
			
		}else if (i<-1) {
			//出现断层的情况
			//出现断层跑了几个fnl的情况下（n个fnl+0个gfs情况）
			//出现断层  n个fnl+n个gfs情况
			
			
		}
		return lastungrib;
	}

	/**
	 * @Description: TODO
	 * @param body   
	 * QueueData  准备实时预报的数据 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午10:40:32
	 */
	public QueueData readyRealMessageData(String datatype,String time,Long scenarinoId,String lastungrib,Boolean firsttime,String icdate,Long cores) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//设置消息里面的的time和type的值
		queueData = getHeadParameter();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//设置消息里面body节点的主体消息
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		bodyData = getbodyDataHead(scenarinoDetailMSG,cores);
		
		String scenarinoType = scenarinoDetailMSG.getScenType();
		System.out.println(scenarinoType);
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,datatype,time,scenarinoType ,firsttime,scenarinoDetailMSG);
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		commonData = (QueueDataCommon) map.get("commonData");
		//准备wrf数据
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		if (firsttime.equals(true)) {
			//spinup
			Long DBspinup = (Long) map.get("spinup");
			//设置wrf的spinup
			Long spinup = DBspinup+5;
			wrfData.setSpinup(spinup);
			cmaqData.setSpinup(DBspinup);
		}else {
			wrfData.setSpinup((long)0);
			cmaqData.setSpinup((long)0);
		}
		
		
		wrfData.setLastungrib(lastungrib);
		//设置cmaq的spinup
		
		Long missionId = scenarinoDetailMSG.getMissionId();
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId ,firsttime,icdate); 
		cmaqData.setIc(icMap);
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId);
		//设置body的数据
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		bodyData.setEmis(DataEmis);
		queueData.setBody(bodyData);
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
	public void readyBaseData(Long scenarinoId,Long cores) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置消息里面的的time和type的值
		System.out.println("基准情景");
		queueData = getHeadParameter();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//设置消息里面body节点的主体消息
		bodyData = getbodyDataHead(scenarinoDetailMSG,cores);
	//	Integer scenarinoType =  (Integer) body.get("scenarinoType");
		//情景类型
		Integer scenarinoType = Integer.parseInt(scenarinoDetailMSG.getScenType());
		Long missionId = scenarinoDetailMSG.getMissionId();//任务id
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,"fnl",null,scenarinoType.toString(),true,scenarinoDetailMSG);
		commonData = (QueueDataCommon) map.get("commonData");
		//spinup
		Long DBspinup = (Long) map.get("spinup");
		System.out.println(DBspinup+"这个是Dbspinup");
		//准备wrf数据
		//设置wrf的spinup
		Long spinup = DBspinup+5;
		System.out.println(spinup+"这个是SPINUP");
		wrfData.setSpinup(spinup);
		
		//设置cmaq的spinup
		cmaqData.setSpinup(DBspinup);
		
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId);
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		bodyData.setEmis(DataEmis);
		queueData.setBody(bodyData);
		sendQueueData.toJson(queueData,null);
		
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
	public void readyPreEvaluationSituationDataFirst(Long scenarinoId,Long cores) {
		//本方法准备第一条数据
		//Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		Date today = DateUtil.DateToDate(new Date(), "yyyyMMdd");
		//比较时间 确定datetype
		int i = startDate.compareTo(today);
		String datatype = null;
		if (i<0) {
			 datatype = "fnl";
		}
		String time = DateUtil.DATEtoString(startDate, "yyyyMMdd");
		QueueData queueData = readypreEvaSituMessageData(scenarinoId,datatype,time);
		//判断是否能发该条消息
		Map<String, String> map = cantopreEvaluation(null, null, 1);
		String maxusetime = map.get("useable");
		Date maxdate = DateUtil.StrtoDateYMD(maxusetime, "yyyyMMdd");
		int compareTo = maxdate.compareTo(startDate);
		if (compareTo>=0) {
			sendQueueData.toJson(queueData, null);
		}
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
	public void sendDataEvaluationSituationThen(Date tasksEndDate,
			Long tasksScenarinoId) {
		System.out.println("发第二条数据");
		//消息的time的内容
		String time = DateUtil.changeDate(tasksEndDate, "yyyyMMdd", 1);
		Long scenarinoId = tasksScenarinoId;
		Date today = DateUtil.DateToDate(new Date(), "yyyyMMdd");
		//比较时间 确定datetype
		int i = tasksEndDate.compareTo(today);
		String datatype = null;
		if (i<0) {
			datatype = "fnl";
		}else if(i>=0){
			datatype = "gfs";
		}
		QueueData queueData = readypreEvaSituMessageData(scenarinoId , datatype, time);
		//判断是否能发该条消息 获取实时预报的最新完成的时间
		Map<String, String> map = cantopreEvaluation(null, null, 1);
		String maxusetime = map.get("useable");
		Date maxdate = DateUtil.StrtoDateYMD(maxusetime, "yyyyMMdd");
		Date mesdate = DateUtil.StrtoDateYMD(time, "yyyyMMdd");
		//时间做比较 当当条消息的时间小于等于实时预报的的最大完成时间的时候 可以做预评估
		int compareTo = maxdate.compareTo(mesdate);
		if (compareTo>=0) {
			sendQueueData.toJson(queueData, null);
		}
	}
	/**
	 * @Description: TODO
	 * @param tasksEndDate
	 * @param tasksScenarinoId   
	 * void  可以进行预评估的条件 当天的当天的fnl或者gfs是否可用
	 * @throws
	 * @author yanglei
	 * @date 2017年4月1日 上午9:55:11
	 */
	public static Map<String, String> cantopreEvaluation(Date tasksEndDate, Long tasksScenarinoId,Integer i) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String useabledate = DateUtil.DATEtoString(tasksEndDate, "yyyyMMdd");
		if (i==null) {
			hashMap=null;
		}if (i==1) {
			hashMap.put("useable", useabledate);
		}
		return hashMap;
	}
	
	/**
	 * @Description: TODO
	 * @return   
	 * QueueData   准备预评估任务 预评估情景的具体数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午4:45:39
	 */
	private QueueData readypreEvaSituMessageData(Long scenarinoId,String datatype,String time) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置消息里面的的time和type的值
		queueData = getHeadParameter();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//查找数据库的情景详情的消息通过情景ID
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//创建消息bady对象 设置消息里面body节点的主体消息
		QueueBodyData bodyDate = getbodyDataHead(scenarinoDetailMSG,null);
		String scenarinoType = scenarinoDetailMSG.getScenType();
		//设置firsttime
		boolean firsttime = false;
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,datatype,time,scenarinoType,firsttime,scenarinoDetailMSG);
		//创建common消息对象
		QueueDataCommon commonData = (QueueDataCommon) map.get("commonData");
		//设置spinup
		wrfData.setSpinup((long)0);
		cmaqData.setSpinup((long)0);
		//设置lastungrib
		wrfData.setLastungrib("");
		Long missionId = scenarinoDetailMSG.getMissionId();
		//基础情景的时间  基础时间
		Date icDate = scenarinoDetailMSG.getBasisTime();
		//
		String icdate = DateUtil.DATEtoString(icDate, "yyyyMMdd");
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId ,false,icdate); 
		cmaqData.setIc(icMap);
		//创建emis对象   调用方法获取emis数据
		QueueDataEmis DataEmis = getDataEmis(missionId);
		//设置body的数据
		bodyDate.setCommon(commonData);
		bodyDate.setWrf(wrfData);
		bodyDate.setCmaq(cmaqData);
		bodyDate.setEmis(DataEmis);
		queueData.setBody(bodyDate);
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
	 * @Description: 设置消息里面body节点的主体消息
	 * @param body
	 * @return   
	 * QueueBodyData  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月24日 下午6:21:55
	 */
	private QueueBodyData getbodyDataHead(TScenarinoDetail scenarinoDetailMSG,Long cores) {
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		//调试模式的内容
		bodyData.setFlag(1);
		Integer scenarinoType = Integer.parseInt(scenarinoDetailMSG.getScenType());//情景类型
		Long userId = scenarinoDetailMSG.getUserId();
		Long missionId = scenarinoDetailMSG.getMissionId();//任务id
		Long scenarinoId = scenarinoDetailMSG.getScenarinoId();//情景id
		if (cores==null) {
			cores =Long.parseLong(scenarinoDetailMSG.getExpand3());//计算核数
		}else {
			bodyData.setCores(cores);
		}
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
	private QueueData getHeadParameter() {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//消息时间为当前的系统时间（北京时间 ）
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		queueData.setId(UUID.randomUUID().toString());//设置消息id
		queueData.setTime(time);//设置消息时间
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
	 * @Description: TODO
	 * @param scenarinoId
	 * @param lastfnl
	 * @param scenarinoType   
	 * QueueDataCommon  获取实时预报的common数据
	 * @param scenarinoDetailMSG2 
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午11:31:35
	 */
	private Map<String, Object> getcommonMSG(Long scenarinoId, String datatype,String time,
			String scenarinoType,Boolean firsttime, TScenarinoDetail scenarinoDetailMSG) {
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		Map<String,String> map = new HashMap<String, String>();
		Map<String, Object> hashmap = new HashMap<String, Object>();
		Integer scenType = Integer.parseInt(scenarinoType);
		/*//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);*/
		//如果是实时预报的类型，开始的时间和结束的时间以及datatype都是来自上面传来的参数，其他类型则来自查询得到的开始和结束时间
		if (scenType==4||scenType==1) {
			//设置类型
			commonData.setDatatype(datatype);
			//设置开始结束时间
			map.put("start", time);
			map.put("end", time);
			commonData.setTime(map);
			System.out.println(time);
			//设置firsttime
			commonData.setFirsttime(firsttime);
			//设置起报日期
			String pathdate = DateUtil.DATEtoString(new Date(), "yyyyMMdd");
			commonData.setPathdate(pathdate);
			hashmap.put("commonData", commonData);
			hashmap.put("spinup", scenarinoDetailMSG.getSpinup());
		}else {
			//获取情景开始时间
			Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
			//获取情景结束时间
			Date endDate = scenarinoDetailMSG.getScenarinoEndDate();
			//获取情景起报时间
			Date pathDate = scenarinoDetailMSG.getPathDate();
			//设置气象数据类型
			commonData.setDatatype(datatype);
			//设置firsttime
			commonData.setFirsttime(firsttime);
			String pathdate = DateUtil.DATEtoString(pathDate,"yyyyMMdd");
			String starttime = DateUtil.DATEtoString(startDate,"yyyyMMdd");
			String endtime = DateUtil.DATEtoString(endDate,"yyyyMMdd");
			//设置起报时间
			commonData.setPathdate(pathdate);
			map.put("start", starttime);
			map.put("end", endtime);
			//设置时间
			commonData.setTime(map);
			hashmap.put("commonData", commonData);
			hashmap.put("spinup", scenarinoDetailMSG.getSpinup());
		}
		
		return hashmap;
		
	}
	/**
	 * @Description:  通过情景ID获取wrf参数
	 * @param scenarinoId
	 * @return   
	 * wrfData 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月18日 上午10:40:19
	 */
	private  QueueDataWrf  getWrfData(Long scenarinoId,String lastungrib) {
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//获取数据库的spinup值
		Long DBspinup = scenarinoDetailMSG.getSpinup();
		Long spinup = DBspinup+5;
		//设置spinup
		wrfData.setSpinup(spinup);
		wrfData.setLastungrib(lastungrib);
		return wrfData;
	}
	
	
	
	/**
	 * 
	 * @Description: TODO
	 * @param sourceid
	 * @return   
	 * Map<String,Object>  获取emis的数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月17日 下午2:19:53
	 */
	public Map<String, Object> getEmis(String sourceid) {
		/*Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		String url="http://192.168.1.10:8082/ampc/app";
		String getResult=ClientUtil.doPost(url,sourceid);*/
		
		
		
		
		return null;
	}

	/**
	 * @Description: TODO
	 * @param missionId
	 * @return   
	 * QueueDataEmis  设置emis数据的方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午6:50:19
	 */
	private QueueDataEmis getDataEmis(Long missionId) {
		//通过情景ID获取清单ID
		Long sourceid = tMissionDetailMapper.getsourceid(missionId);
		//创建对象
		QueueDataEmis  DataEmis = new QueueDataEmis();
		//设置sourceid
		DataEmis.setSourceid(sourceid.toString());
		//设置Calctype 计算方式
		DataEmis.setCalctype("server");//cache
		//
		DataEmis.setPsal("");
		//
		DataEmis.setSsal("");
		//
		DataEmis.setMeiccityconfig("/work/modelcloud/meic_tool/meic-city.conf");
		return DataEmis;
	}


	
	

	

	

	

	

	

	
	

}
