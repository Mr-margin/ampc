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
import java.util.Map;
import java.util.UUID;

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
	 * @Description: 获取最新的ungrib 通过ungrib来控制消息的条数和内容
	 * @return   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 上午11:24:54
	 */
	public void getLastUngrib(HashMap<String, Object> body) {
		System.out.println("进入ungrib的方法");
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		System.out.println("进入ungrib的方法2");
		
		String lastungrib;
		//获取最新的ungrib 
		//Date pathdate = tUngribMapper.getlastungrib();
		Long ungribId = (long) 1;
		tUngribMapper.deleteByPrimaryKey(2l);
		
		/*TUngrib tUngrib = tUngribMapper.getlastungrib();
		System.out.println("aaaaaaa");
		Date pathdate = tUngrib.getPathDate();
		//最新的pathdate（年月日）
		Date pathDate = pathdate;
		//将当前的的系统时间转化为于pathdate一样的时间格式（ 年月日形式）
		Date todayDate = DateUtil.DateToDate(new Date(),"yyyyMMdd");
		//比较最新的pathdate和当前系统时间
		int i = pathDate.compareTo(todayDate);
		//i=0表示时间一致 小于0表示 时间pathdate小于系统时间
		System.out.println("bbbbbbb");
		if (i==0) {
			//时间一致的时候可以准备数据(1个fnl+n个gfs形式）
			lastungrib = DateUtil.DATEtoString(pathDate, "yyyyMMdd");
			//从tasksStatus表里获取情景的天数 开始时间和结束时间
			TTasksStatus tTasksStatus = tTasksStatusMapper.getRangeTime(scenarinoId);
			//情景开始时间
			Date startDate = tTasksStatus.getScenarinoStartDate();
			//情景结束时间
			Date endDate = tTasksStatus.getScenarinoEndDate();
			Long rangeday = tTasksStatus.getRangeDay();
			for (int j = 0; j <rangeday; j++) {
				//声明firsttime
				String firsttime;
				//从数据库里获取该情景下的所有信息
				TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
				//准备firsttime 通过用户ID 情景类型 和pathdate来决定
				Date zoreDate = DateUtil.ChangeDay(scenarinoDetailMSG.getPathDate(), -1);
				Map<String,Object> map2 = new HashMap<String, Object>();
				map2.put("pathDate", zoreDate);
				map2.put("userId", scenarinoDetailMSG.getUserId());
				map2.put("scenType", scenarinoDetailMSG.getScenType());
				TScenarinoDetail scenarinoDetailMSG2 = tScenarinoDetailMapper.selectFirstTime(map2);
				if (scenarinoDetailMSG2!=null) {
					 firsttime = "false";
				}else {
					firsttime = "true";
				}
				//开始时间小于今天 datatype 为fnl 大于等于今天为gfs
				if (j==0) {
					//设置datatype
					String datatype = "fnl";
					//消息的time的内容
					String time = DateUtil.DATEtoString(startDate, "yyyyMMdd");
					String icdate = DateUtil.changeDate(startDate, "yyyyMMdd", -1);
					queueData = readyRealMessageData(datatype,time,body,lastungrib,firsttime,icdate);
					//队列消息准备成功,调用方法 将数据转为json数据,准备就绪等待发送
					sendQueueData.toJson(queueData);
				}else {
					//消息的time的内容
					String time = DateUtil.changeDate(startDate, "yyyyMMdd", i);
					//设置datatype
					String datatype = "gfs";
					Date iCDate = DateUtil.StrtoDateYMD(time, "yyyyMMdd");
					String icdate = DateUtil.changeDate(iCDate, "yyyyMMdd", -1);
					queueData = readyRealMessageData(datatype,time,body,lastungrib,firsttime,icdate);
					sendQueueData.toJson(queueData);
				}
			}
		}*/
		
		
	}
	
	/**
	 * @Description: TODO
	 * @param body   
	 * QueueData  准备实时预报的数据 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午10:40:32
	 */
	public QueueData readyRealMessageData(String datatype,String time,HashMap<String, Object> body,String lastungrib,String firsttime,String icdate) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		String type = "model.start";//执行模式
		queueData.setType(type);
		//设置消息里面的的time和type的值
		queueData = getHeadParameter();
		//设置消息里面body节点的主体消息
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		bodyData = getbodyDataHead(body);
		Integer scenarinoType =  (Integer) body.get("scenarinoType");//情景类型
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		Long missionId = (Long) body.get("missionId");//任务id
		Long userId = (Long) body.get("userId");
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,datatype,time,scenarinoType,firsttime);
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		commonData = (QueueDataCommon) map.get("commonData");
		//spinup
		Long DBspinup = (Long) map.get("spinup");
		//准备wrf数据
		//设置wrf的spinup
		Long spinup = DBspinup+5;
		//创建wrf对象
		QueueDataWrf wrfData = new QueueDataWrf();
		wrfData.setSpinup(spinup.toString());
		wrfData.setLastungrib(lastungrib);
	//	wrfData = ReadyData.getWrfData(scenarinoId,lastungrib);
		//准备cmap数据
	//	cmapData = ReadyData.getCmapData(scenarinoId,missionId);
		//创建cmaq对象
		QueueDataCmaq cmaqData = new QueueDataCmaq();
		//设置cmaq的spinup
		cmaqData.setSpinup(DBspinup.toString());
		//准备cmaq的ic
		Map<String, Object> icMap = getIC(scenarinoId,missionId,firsttime,icdate); 
		cmaqData.setIc(icMap);
		//设置body的数据
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
		Map<String,Object> hashMap = new HashMap<String, Object>();
		hashMap.put("body", bodyData);
		queueData.setBody(hashMap);
		//queueData.setBody(bodyData);
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
	public void readyBaseData(HashMap<String, Object> body) {
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
		//设置消息里面body节点的主体消息
		bodyData = getbodyDataHead(body);
		Integer scenarinoType =  (Integer) body.get("scenarinoType");//情景类型
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		Long missionId = (Long) body.get("missionId");//任务id
		Long userId = (Long) body.get("userId");
		//准备commom数据
		Map<String, Object> map = getcommonMSG(scenarinoId,"fnl",null,scenarinoType,"true");
		commonData = (QueueDataCommon) map.get("commonData");
		//spinup
		Long DBspinup = (Long) map.get("spinup");
		System.out.println(DBspinup+"这个是Dbspinup");
		//准备wrf数据
		//设置wrf的spinup
		Long spinup = DBspinup+5;
		System.out.println(spinup+"这个是SPINUP");
		wrfData.setSpinup(spinup.toString());
		//String lastungrib = tUngribMapper.getlastungrib();
		
		//wrfData.setLastungrib(lastungrib);
		//设置cmaq的spinup
		cmaqData.setSpinup(DBspinup.toString());
		
		bodyData.setCommon(commonData);
		bodyData.setWrf(wrfData);
		bodyData.setCmaq(cmaqData);
	/*	
		Map<String,Object> hashMap = new HashMap<String, Object>();
		hashMap.put("body", bodyData);*/
		queueData.setBody(bodyData);
	//	SendQueueData sendQueueData = new SendQueueData();
		sendQueueData.toJson(queueData);
		
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
			String firsttime,String icdate) {
		Map<String,Object> map = new HashMap<String, Object>();
		//当天的上一天为基础情景
		if(firsttime.contains("true")){
			map=null;
		}else {
			map.put("missionid", missionId);
			map.put("scenarinoid", scenarinoId);
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
	private QueueBodyData getbodyDataHead(HashMap<String, Object> body) {
		//创建消息bady对象
		QueueBodyData bodyData = new QueueBodyData();
		System.out.println("消息体头部内容");
		Integer scenarinoType =  (Integer) body.get("scenarinoType");//情景类型
		Long userId = (Long) body.get("userId");
		Long missionId = (Long) body.get("missionId");//任务id
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		Long cores = (Long) body.get("cores");//计算核数
		bodyData.setUserid(userId);
		bodyData.setScenarioid(scenarinoId);
		bodyData.setMissionid(missionId);
		bodyData.setCores(cores);
		System.out.println("开始操作数据库");
		//从任务表里面获取当条任务的详细内容
		TMissionDetail mission = tMissionDetailMapper.selectByPrimaryKey(missionId);
		String missiontype = mission.getMissionStatus().toString();
		//准备modeltype数据
		String modeltype = getmodelType(missiontype,scenarinoType);
		bodyData.setModeltype(modeltype);
		//获取domainId
		Long domainId = mission.getMissionDomainId();
		bodyData.setDomainid(domainId);//domainid
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
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午11:31:35
	 */
	private Map<String, Object> getcommonMSG(Long scenarinoId, String datatype,String time,
			Integer scenarinoType,String firsttime) {
		//创建common消息对象
		QueueDataCommon commonData = new QueueDataCommon();
		Map<String,String> map = new HashMap<String, String>();
		Map<String, Object> hashmap = new HashMap<String, Object>();
		//从数据库里获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		//如果是实时预报的类型，开始的时间和结束的时间以及datatype都是来自上面传来的参数，其他类型则来自查询得到的开始和结束时间
		if (scenarinoType==4) {
			//设置类型
			commonData.setDatatype(datatype);
			//设置开始结束时间
			map.put("start", time);
			map.put("end", time);
			commonData.setTime(map);
			
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
	 * @Description: TODO
	 * @param lastfnl
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 上午10:33:47
	 */
	private String getDateType(String lastfnl) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/**
	 * @Description: TODO
	 * @param body   
	 * void  准备body的数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月17日 上午11:26:23
	 */
	public void sendMessageData(HashMap<String, Object> body) {
		StringBuffer buffer = new StringBuffer();
		Integer scenarinoType =  (Integer) body.get("scenarinoType");
		Long missionId =  (Long) body.get("missionId");
		Long scenarinoId = (Long) body.get("scenarinoId");
		//准备modeltype
		//准备domainid
		//Long domainId = qgetDataMapper.selectDomainid(missionId);
		TMissionDetail mission = tMissionDetailMapper.selectByPrimaryKey(missionId);
		Integer missionStatus = (Integer) mission.getMissionStatus();
		buffer.append(mission.getMissionStatus());
		buffer.append("_");
		buffer.append(scenarinoType);
		String modeltype = buffer.toString();
		System.out.println(mission.getMissionDomainId()+"数据库查出来的domainid");
		//定时开启，查询数据fnl等
		
		//实时预报情景
		if (scenarinoType==1) {
			//准备wrf数据
			//Map<String, String> wrfMap = ReadyData.getWrfData(scenarinoType);
			//准备cmap数据
		//	Map<String, Map<String, String>> cmapMap = ReadyData.getCmapData(scenarinoType);
			
		}
		//判断tasks准备的时候 lastfnl 是否是小于today-1 如果小于 则lastfnl 则不发送数据包
		//获取lastfnl ，initial
		
	//	Map<String, Object> map = ReadyData.getLastUngrib(scenarinoId);
	//	String lastfnl = (String) map.get("lastfnl");
		
	//	System.out.println(scenarinoId);
		//准备common数据
		Map<String, Map<String, String>> common = new HashMap<String, Map<String, String>>();
		/*ReadyData.getcommonMSG(scenarinoId,lastfnl,scenarinoType);
		sendQueueData.sendData(body,scenarinoId);*/
		
		
		
		
		
	}
	
	
	/**
	 * @Description: TODO
	 * @param scenarinoType
	 * @return   
	 * Map<String,String>  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月18日 上午11:05:13
	 */
	private static Map<String,Map<String, String>> getCmapData(Integer scenarinoType) {
		// TODO Auto-generated method stub
		return null;
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
		wrfData.setSpinup(spinup.toString());
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
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		String url="http://192.168.1.10:8082/ampc/app";
		String getResult=ClientUtil.doPost(url,sourceid);
		return null;
	}

	
	

}
