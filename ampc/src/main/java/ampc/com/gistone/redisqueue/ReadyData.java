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
 */
@Component
public class ReadyData {
	@Autowired
	private ReadyData ReadyData;
	//任务详情映射
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	//引发送数据包队列类
	@Autowired
	private SendQueueData sendQueueData;
	//ungrib映射
	@Autowired
	private  TUngribMapper tUngribMapper;
	//情景详情映射
	@Autowired
	private  TScenarinoDetailMapper tScenarinoDetailMapper;
	//tasks映射
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	//消息头类
	@Autowired
	private QueueData queueData;
	//消息体类
	@Autowired
	private QueueBodyData bodyData;
	//消息体 common 消息
	@Autowired
	private QueueDataCommon commonData;
	//消息体 emis 数据
	@Autowired
	private QueueDataEmis emisData;
	//消息体 wrf数据
	@Autowired
	private QueueDataWrf wrfData;
	//消息体cmap数据
	@Autowired 
	private QueueDataCmap cmapData;
	
	/**
	 * @Description: TODO
	 * @param body   
	 * void  准备实时预报的数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午10:40:32
	 */
	public void readyRealMessageData(HashMap<String, Object> body) {
		String type = "model.start";//执行模式
		queueData.setType(type);
		//设置消息里面的的time和type的值
		queueData = ReadyData.getHeadParameter();
		//设置消息里面body节点的主体消息
		bodyData = ReadyData.getbodyDataHead(body);
		Integer scenarinoType =  (Integer) body.get("scenarinoType");//情景类型
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		//获取tasksid
		Long tasksId = tTasksStatusMapper.selectTasksId(scenarinoId);
		
		//准备最新的lastungrib 
		Map<String, Object> map = ReadyData.getLastUngrib();
		String lastfnl = (String) map.get("lastfnl");
		//initial 初始化的时候是true 其他情况都是false
		String initial =   (String) map.get("initial");
		commonData.setFirsttime(initial);
		//准备common的数据
		commonData = getRealcommonMSG(scenarinoId,lastfnl,scenarinoType);
		
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
		Integer scenarinoType =  (Integer) body.get("scenarinoType");//情景类型
		Long userId = (Long) body.get("userId");
		Long missionId = (Long) body.get("missionId");//任务id
		Long scenarinoId = (Long) body.get("scenarinoId");//情景id
		Long cores = (Long) body.get("cores");//计算核数
		bodyData.setUserId(userId);
		bodyData.setScenarinoId(scenarinoId);
		bodyData.setMissionId(missionId);
		bodyData.setCores(cores);
		//从任务表里面获取当条任务的详细内容
		TMissionDetail mission = tMissionDetailMapper.selectByPrimaryKey(missionId);
		String missiontype = mission.getMissionStatus().toString();
		//准备modeltype数据
		String modeltype = ReadyData.getmodelType(missiontype,scenarinoType);
		bodyData.setModelType(modeltype);
		//获取domainId
		Long domainId = mission.getMissionDomainId();
		bodyData.setDomainId(domainId);//domainid
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
	 * void  获取实时预报的common数据
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 上午11:31:35
	 */
	private  QueueDataCommon getRealcommonMSG(Long scenarinoId, String lastfnl,
			Integer scenarinoType) {
		ArrayList<String> list = new ArrayList<String>();
		Map<String,String> map = new HashMap<String, String>();
		System.out.println(scenarinoId);
		//获取该情景下的所有信息
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		System.out.println(scenarinoDetailMSG);
		//获取情景开始时间
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		//获取情景结束时间
		Date endDate = scenarinoDetailMSG.getScenarinoEndDate();
		//获取情景起报时间
		Date pathDate = scenarinoDetailMSG.getPathDate();
		
		String pathdate = DateUtil.DATEtoString(pathDate);
		String starttime = DateUtil.DATEtoString(startDate);
		String endtime = DateUtil.DATEtoString(endDate);
		commonData.setPathdate(pathdate);
		map.put("start", starttime);
		map.put("end", endtime);
		commonData.setTime(time);
		return commonData;
		
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
			Map<String, String> wrfMap = ReadyData.getWrfData(scenarinoType);
			//准备cmap数据
			Map<String, Map<String, String>> cmapMap = ReadyData.getCmapData(scenarinoType);
			
		}
		//判断tasks准备的时候 lastfnl 是否是小于today-1 如果小于 则lastfnl 则不发送数据包
		//获取lastfnl ，initial
		
		Map<String, Object> map = ReadyData.getLastUngrib();
		String lastfnl = (String) map.get("lastfnl");
		
		System.out.println(scenarinoId);
		//准备common数据
		Map<String, Map<String, String>> common = new HashMap<String, Map<String, String>>();
		ReadyData.getcommonMSG(scenarinoId,lastfnl,scenarinoType);
		sendQueueData.sendData(body,scenarinoId);
		
		
		
		
		
	}
	/**
	 * @param scenarinoId 
	 * @param lastfnl 
	 * @param scenarinoType 
	 * @Description: TODO   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 下午6:33:40
	 */
	private  Map<String, String> getcommonMSG(Long scenarinoId, String lastfnl, Integer scenarinoType) {
		TScenarinoDetail scenarinoDetailMSG = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
		if (scenarinoType==4) {
			
			
		}
		Date startDate = scenarinoDetailMSG.getScenarinoStartDate();
		Date endDate = scenarinoDetailMSG.getScenarinoEndDate();
		Date pathDate = scenarinoDetailMSG.getPathDate();
		String pathdate = DateUtil.DATEtoString(pathDate);
		String starttime = DateUtil.DATEtoString(startDate);
		String endtime = DateUtil.DATEtoString(endDate);
		/*Long spinUpOriginal= scenarinoDetailMSG.getSpinUp();
		Long spinup0 = spinUpOriginal+5;
		String wrfspinup =spinup0.toString();
		String cmapspinup = spinUpOriginal.toString();*/
		return null;
	}
	/**
	 * @Description: TODO
	 * @return   
	 * boolean  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 上午11:24:54
	 */
	private  Map<String, Object> getLastUngrib() {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		String initial = "false";
		String oldlastungrib;
		TUngrib tUngrib = tUngribMapper.getlastungrib();
		System.out.println(tUngrib);
		
		if (tUngrib==null) {
			initial = "true";
			hashMap.put("initial", initial);
			hashMap.put("lastfnl", null);
		}else{
			initial = "false";
			hashMap.put("initial", initial);
			Date pathDate = tUngrib.getPathDate();
			if(tUngrib.getFnlStatus()==1){
					// 出现中断的情况；fnl_status ：0表示成功，1   表示不成功。
				//不成功的时候表示出现中断的状态 重新查新数据库，找最新的成功的lastfnl lastfnl的状态显示成功为0为条件
				Date getinterruptlastFnl = tUngribMapper.getinterruptlastFnl(0);
				 Date reduceOneDay = DateUtil.reduceOneDay(getinterruptlastFnl);
				 String datEtoString = DateUtil.DATEtoString(reduceOneDay);
				 System.out.println(datEtoString+"最新的fnl");
				hashMap.put("lastfnl", datEtoString);	
			}else if (tUngrib.getFnlStatus()==0) {
				Date reduceOneDay = DateUtil.reduceOneDay(pathDate);
				String lastfnl = DateUtil.DATEtoString(reduceOneDay);
				hashMap.put("lastfnl", lastfnl);
			}
		}
		
		return hashMap;
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
	 * @Description: TODO
	 * @param scenarinoType
	 * @return   
	 * Map<String,String>  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月18日 上午10:40:19
	 */
	private static Map<String, String> getWrfData(Integer scenarinoType) {
		
		return null;
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
