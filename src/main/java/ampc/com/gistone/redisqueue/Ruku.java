/**  
 * @Title: Rukun.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月2日 下午8:20:06
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import oracle.net.aso.s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TDomainMissionMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.preprocess.concn.ConcnService;
import ampc.com.gistone.preprocess.concn.RequestParams;
import ampc.com.gistone.preprocess.meteor.MeteorService;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: Rukun.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月2日 下午8:20:06
 * @version 1.0
 */
@Component
public class Ruku {
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	@Autowired
	private TDomainMissionMapper tDomainMissionMapper;
	
	private ObjectMapper mapper=new ObjectMapper();
	@Autowired
	private ConcnService concnService;
	@Autowired
	private MeteorService meteorService;
	@Autowired
	private ReadyData readyData;
	/**
	 * @Description: 基准情景入库
	 * @param tasksScenarinoId   
	 * void  
	 * @param oldStatus 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午8:23:58
	 */
	public void readyRukuparamsBasis(Integer stepindex,Long tasksScenarinoId,Date tasksEndDate,TTasksStatus oldStatus,int flag) {
		Long scenarioId = tasksScenarinoId;
		TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId);
		Long userId = scenarinoDetail.getUserId();
		Long missionId = scenarinoDetail.getMissionId();
		Long domainId = tMissionDetailMapper.selectDomainid(missionId);
		//domain的层数 
		String common = tDomainMissionMapper.selectCommonData(domainId);
		Map readValue;
		Object max_dom = null;
		try {
			readValue = mapper.readValue(common, Map.class);
			Map map = (Map) readValue.get("common");
			max_dom = map.get("max_dom");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int domain = Integer.parseInt(max_dom.toString());
		//tasksenddate 情景tasks的结束时间
		Date endDate = DateUtil.DateToDate(tasksEndDate, "yyyy-MM-dd");
		//情景开始时间
		Date scenStartDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoStartDate(),"yyyy-MM-dd");
		//情景结束时间
		Date scenEndDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoEndDate(),"yyyy-MM-dd");
		//上一次tasks的时间
		Date preDate = DateUtil.DateToDate(oldStatus.getTasksEndDate(), "yyyy-MM-dd");
		Long oldstepindex = oldStatus.getStepindex();
		List<String> list = new ArrayList<String>();
		if (flag==1) {
			//气象数据入库
			if (oldstepindex==3) {
				//时间差
				long day = (endDate.getTime()-preDate.getTime())/(24*60*60*1000);
				int len = Integer.parseInt(String.valueOf(day));
				for (int i = 1; i <= len; i++) {
					String dateString = DateUtil.changeDate(preDate, "yyyy-MM-dd", len);
					list.add(dateString);
				}
			}else if(oldstepindex<3){
				//时间差
				long day = (endDate.getTime()-scenStartDate.getTime())/(24*60*60*1000);
				int len = Integer.parseInt(String.valueOf(day));
				for (int i = 1; i <= len; i++) {
					String dateString = DateUtil.changeDate(scenStartDate, "yyyy-MM-dd", len);
					list.add(dateString);
				}
			}
			//String timePoint = "h";
			String[] timepointarray = {"h","d"};
			for (int i = 1; i <= domain; i++) {
				for (String string : timepointarray) {
					RequestParams requestParams = new RequestParams();
					requestParams.setScenarioId(scenarioId);
					requestParams.setUserId(userId);
					requestParams.setMissionId(missionId);
					requestParams.setDomainId(domainId);
					requestParams.setDomain(i);
					requestParams.setTimePoint(string);
					requestParams.setDate(list);
					//requestParams.setShowType("concn");
					boolean res = meteorService.requestMeteorData(requestParams);
					if (res) {
						LogUtil.getLogger().info("基准情景气象数据入库成功！");
					}else {
						LogUtil.getLogger().info("基准情景气象数据入库失败！");
					}
				}
			}
		}
		if (flag==0) {
			//-----------化学数据入库----------
			if (oldstepindex==8) {
				//时间差
				long day = (endDate.getTime()-preDate.getTime())/(24*60*60*1000);
				int len = Integer.parseInt(String.valueOf(day));
				for (int i = 1; i <= len; i++) {
					String dateString = DateUtil.changeDate(preDate, "yyyy-MM-dd", len);
					list.add(dateString);
				}
			}else if(oldstepindex<8){
				//时间差
				long day = (endDate.getTime()-scenStartDate.getTime())/(24*60*60*1000);
				int len = Integer.parseInt(String.valueOf(day));
				for (int i = 1; i <= len; i++) {
					String dateString = DateUtil.changeDate(scenStartDate, "yyyy-MM-dd", len);
					list.add(dateString);
				}
			}
			
			//String timePoint = "h";
			String[] timepointarray = {"h","d"};
			for (int i = 1; i <= domain; i++) {
				for (String string : timepointarray) {
					RequestParams requestParams = new RequestParams();
					requestParams.setScenarioId(scenarioId);
					requestParams.setUserId(userId);
					requestParams.setMissionId(missionId);
					requestParams.setDomainId(domainId);
					requestParams.setDomain(i);
					requestParams.setTimePoint(string);
					requestParams.setDate(list);
					//requestParams.setShowType("concn");
					boolean res = concnService.requestConcnData(requestParams);
					if (res) {
						LogUtil.getLogger().info("基准情景化学数据入库成功！");
					}else {
						LogUtil.getLogger().info("基准情景化学数据入库失败！");
					}
				}
			}
			int compareTo = endDate.compareTo(scenEndDate);
			if (compareTo==0) {
				//基准情景的dp_chem行完毕
				LogUtil.getLogger().info("基准情景化学数据入库完毕!");
				readyData.updateScenStatusUtil(8l, scenarioId);
			}
		}
		
		
		
		
		
	}
	/**
	 * @Description: 实时预报   浓度入库
	 * 一天一天的完成气象数据   日均 小时均  第一层 第二层 第三层 共调用6次
	 * @param tasksScenarinoId
	 * @param tasksEndDate   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午9:18:31
	 */
	public void readyRukuparamsRealPredict(Integer stepindex,Long tasksScenarinoId,
			Date tasksEndDate,int flag) {
		
		Long scenarioId = tasksScenarinoId;
		TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId);
		Long userId = scenarinoDetail.getUserId();
		Long missionId = scenarinoDetail.getMissionId();
		Long domainId = tMissionDetailMapper.selectDomainid(missionId);
		//domain的层数 
		String common = tDomainMissionMapper.selectCommonData(domainId);
		Map readValue;
		Object max_dom = null;
		try {
			readValue = mapper.readValue(common, Map.class);
			Map map = (Map) readValue.get("common");
			max_dom = map.get("max_dom");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int domains = Integer.parseInt(max_dom.toString());
//		int domains = 3;
		Date endDate = DateUtil.DateToDate(tasksEndDate, "yyyy-MM-dd");
		//情景开始时间
		Date scenStartDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoStartDate(),"yyyy-MM-dd");
		//情景结束时间
		Date scenEndDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoEndDate(),"yyyy-MM-dd");
		List<String> list = new ArrayList<String>();
		list.add(DateUtil.DATEtoString(endDate, "yyyy-MM-dd"));
		String[] timepointarray = {"h","d"};
		for(int i = 1; i<=domains; i++){
			for (String string : timepointarray) {
				RequestParams requestParams = new RequestParams();
				requestParams.setScenarioId(scenarioId);
				requestParams.setUserId(userId);
				requestParams.setMissionId(missionId);
				requestParams.setDomainId(domainId);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				if (flag==1) {
					//气象入库
					boolean res = meteorService.requestMeteorData(requestParams);
					if (res) {
						LogUtil.getLogger().info("实时预报气象数据入库成功！");
					}else {
						LogUtil.getLogger().info("实时预报气象数据入库失败！");
					}
					
				}
				if (flag==0) {
					//化学入库
					boolean res = concnService.requestConcnData(requestParams);
					if (res) {
						LogUtil.getLogger().info("实时预报数据入库成功！");
						int compareTo = endDate.compareTo(scenEndDate);//模式结束时间和情景的结束时间
						if (compareTo==0) {
							//模式结束时间和情景的结束时间一致表示数据入库完毕
							LogUtil.getLogger().info("数据入库完毕！");
							//修改情景状态
							readyData.updateScenStatusUtil(8l, scenarioId);
						}else {
							Date date = new Date();
							Date pathDate = scenarinoDetail.getPathDate();
							Date tempDate = DateUtil.DateToDate(DateUtil.ChangeDay(pathDate, 1), "yyyyMMdd");
							Date timeCritical = DateUtil.changedateByHour(tempDate, 7);
							/*
							 * 临界时间和当前系统时间比较 
							 * 如果当前系统时间大于临界时间 表示当前执行的消息是该条实时预报的能执行的上线次数
							 * 本次对应的消息执行完毕，以后的实时预报不再执行
							 * */
							int criticalCompare = date.compareTo(timeCritical);	
							if (criticalCompare>0) {
								//修改情景状态
								readyData.updateScenStatusUtil(8l, scenarioId);
								LogUtil.getLogger().info("Ruku-readyRukuparamsRealPredict :实时预报模式执行完毕！");
							}
						}
					}else {
						LogUtil.getLogger().info("实时预报数据入库失败！");
					}
				}
				
			}
		}
	}
	/**
	 * @Description: 预评估任务预评估情景
	 * @param tasksScenarinoId
	 * @param tasksEndDate   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午9:46:44
	 */
	public void readyRukuparamsRrePredict(Long tasksScenarinoId,
			Date tasksEndDate) {
		Long scenarioId = tasksScenarinoId;
		TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId);
		Long userId = scenarinoDetail.getUserId();
		Long missionId = scenarinoDetail.getMissionId();
		Long domainId = tMissionDetailMapper.selectDomainid(missionId);
		//domain的层数 
		String common = tDomainMissionMapper.selectCommonData(domainId);
		Map readValue;
		Object max_dom = null;
		try {
			readValue = mapper.readValue(common, Map.class);
			Map map = (Map) readValue.get("common");
			max_dom = map.get("max_dom");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int domain = Integer.parseInt(max_dom.toString());
//		int domain = 3;
		Date endDate = DateUtil.DateToDate(tasksEndDate, "yyyy-MM-dd");
		//情景结束时间
		Date scenEndDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoEndDate(),"yyyy-MM-dd");
		List<String> list = new ArrayList<String>();
		list.add(DateUtil.DATEtoString(endDate, "yyyy-MM-dd"));
		String[] timepointarray = {"h","d"};
		for (int i = 1; i <= domain; i++) {
			for (String string : timepointarray) {
				RequestParams requestParams = new RequestParams();
				requestParams.setScenarioId(scenarioId);
				requestParams.setUserId(userId);
				requestParams.setMissionId(missionId);
				requestParams.setDomainId(domainId);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				boolean res = concnService.requestConcnData(requestParams);
				
				if (res) {
					LogUtil.getLogger().info("预评估数据入库成功！情景ID："+scenarioId);
					int compareTo = endDate.compareTo(scenEndDate);//模式结束时间和情景的结束时间
					/*
					 * 1.正常情况下，模式执行的结束时间和情景结束的结束时间一直，入库完成 ---模式执行完毕
					 * 2.当当条预评估情景对应的实时预报情景没有执行完毕，只执行到其中的某一天，预评估情景也只能执行到其对应的时间，然后模式执行完毕
					 */
					if (compareTo==0) {
						//模式结束时间和情景的结束时间一致表示数据入库完毕
						LogUtil.getLogger().info("预评估数据入库完毕！情景ID："+scenarioId);
						//修改情景状态
						readyData.updateScenStatusUtil(8l, scenarioId);
					}else {
						Date date = new Date();
						Date pathDate = scenarinoDetail.getPathDate();
						Date tempDate = DateUtil.DateToDate(DateUtil.ChangeDay(pathDate, 1), "yyyyMMdd");
						Date timeCritical = DateUtil.changedateByHour(tempDate, 7);
						/* 临界时间和当前系统时间比较 
						 * 如果当前系统时间大于临界时间 表示当前执行的消息是该条实时预报的能执行的上线次数
						 * 本次对应的消息执行完毕，以后的实时预报不再执行
						 * 比较当次预评估任务的time和最大的气象数据，当时间相等的时候，预评估情景结束，之后的预评估的次数不再执行
						 * 
						 * */
						int criticalCompare = date.compareTo(timeCritical);	
						if (criticalCompare>0) {
							Date maxTimeForMegan = readyData.getMaxTimeForMegan(pathDate,userId);//最大的气象数据时间
							tasksEndDate = DateUtil.DateToDate(tasksEndDate, "yyyyMMdd");
							maxTimeForMegan = DateUtil.DateToDate(maxTimeForMegan, "yyyyMMdd");
							int compareifSend = maxTimeForMegan.compareTo(tasksEndDate);
							if (compareifSend==0) {
								//修改情景状态
								readyData.updateScenStatusUtil(8l, scenarioId);
							}
						}
					}
				}
			}
		}
	}
	/**
	 * @Description: 后评估情景入库
	 * @param tasksScenarinoId
	 * @param tasksEndDate   
	 * void  
	 * @param oldStatus 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午9:47:32
	 */
	public void readyRukuparamspostPevtion(Long tasksScenarinoId,
			Date tasksEndDate, TTasksStatus oldStatus) {
		Long scenarioId = tasksScenarinoId;
		TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId);
		Long userId = scenarinoDetail.getUserId();
		Long missionId = scenarinoDetail.getMissionId();
		Long domainId = tMissionDetailMapper.selectDomainid(missionId);
		//domain的层数 
		String common = tDomainMissionMapper.selectCommonData(domainId);
		Map readValue;
		Object max_dom = null;
		try {
			readValue = mapper.readValue(common, Map.class);
			Map map = (Map) readValue.get("common");
			max_dom = map.get("max_dom");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int domain = Integer.parseInt(max_dom.toString());
//		int domain = 3;
		Date endDate = DateUtil.DateToDate(tasksEndDate, "yyyy-MM-dd");
		//情景开始时间
		Date scenStartDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoStartDate(),"yyyy-MM-dd");
		//情景结束时间
		Date scenEndDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoEndDate(),"yyyy-MM-dd");
		//上一次tasks的时间
		Date preDate = DateUtil.DateToDate(oldStatus.getTasksEndDate(), "yyyy-MM-dd");
		Long oldstepindex = oldStatus.getStepindex();
		String[] timepointarray = {"h","d"};
		List<String> list = new ArrayList<String>();
		if (oldstepindex<4) {
			//时间差
			long day = (endDate.getTime()-scenStartDate.getTime())/(24*60*60*1000);
			int len = Integer.parseInt(String.valueOf(day));
			for (int i = 1; i <= len; i++) {
				String dateString = DateUtil.changeDate(scenStartDate, "yyyy-MM-dd", len);
				list.add(dateString);
			}
		}else if (oldstepindex==4) {
			//时间差
			long day = (endDate.getTime()-preDate.getTime())/(24*60*60*1000);
			int len = Integer.parseInt(String.valueOf(day));
			for (int i = 1; i <= len; i++) {
				String dateString = DateUtil.changeDate(preDate, "yyyy-MM-dd", len);
				list.add(dateString);
			}
		}
		for (int i = 1; i < domain; i++) {
			for (String string : timepointarray) {
				RequestParams requestParams = new RequestParams();
				requestParams.setScenarioId(scenarioId);
				requestParams.setUserId(userId);
				requestParams.setMissionId(missionId);
				requestParams.setDomainId(domainId);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				boolean res = concnService.requestConcnData(requestParams);
				if (res) {
					LogUtil.getLogger().info("后评估情景化学数据入库成功！domain："+domain+",timepoint:"+string+",scenarioId："+scenarioId);
				}else {
					LogUtil.getLogger().info("后评估情景化学数据入库失败！domain："+domain+",timepoint:"+string+",scenarioId："+scenarioId);
				}
			}
		}
		int compareTo = endDate.compareTo(scenEndDate);
		if (compareTo==0) {
			//基准情景的dp_chem行完毕
			LogUtil.getLogger().info("后评估情景化学数据入库完毕!scenarioId："+scenarioId);
			readyData.updateScenStatusUtil(8l, scenarioId);
		}
	}

}
