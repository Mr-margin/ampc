/**  
 * @Title: Rukun.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月2日 下午8:20:06
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.net.aso.s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ConcnService;
import ampc.com.gistone.preprocess.concn.RequestParams;
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
	private ConcnService concnService;
	@Autowired
	private ReadyData readyData;
	/**
	 * @Description: TODO
	 * @param tasksScenarinoId   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午8:23:58
	 */
	public void readyRukuparamsBasis(Integer stepindex,Long tasksScenarinoId,Date tasksEndDate,int flag) {
		Long scenarioId = tasksScenarinoId;
		TScenarinoDetail scenarinoDetail = tScenarinoDetailMapper.selectByPrimaryKey(scenarioId);
		Long userId = scenarinoDetail.getUserId();
		Long missionId = scenarinoDetail.getMissionId();
		Long domainId = tMissionDetailMapper.selectDomainid(missionId);
		//domain的层数 
		int domain = 3;
		//tasksenddate 情景tasks的结束时间
		Date endDate = DateUtil.DateToDate(tasksEndDate, "yyyy-MM-dd");
		//情景开始时间
		Date scenStartDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoStartDate(),"yyyy-MM-dd");
		//情景结束时间
		Date scenEndDate =  DateUtil.DateToDate( scenarinoDetail.getScenarinoEndDate(),"yyyy-MM-dd");
		long day = (endDate.getTime()-scenStartDate.getTime())/(24*60*60*1000);
		int len = Integer.parseInt(String.valueOf(day));
		//时间差
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			String dateString = DateUtil.changeDate(scenStartDate, "yyyy-MM-dd", len);
			list.add(dateString);
		}
		//String timePoint = "h";
		String[] timepointarray = {"h","d"};
		for (int i = 1; i < domain; i++) {
			for (String string : timepointarray) {
				RequestParams requestParams = new RequestParams();
				requestParams.setUserId(userId);
				requestParams.setMissionId(missionId);
				requestParams.setDomainId(domainId);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				//requestParams.setShowType("concn");
				boolean res = concnService.requestConcnData(requestParams);
				if (res) {
					LogUtil.getLogger().info("基准情景气象数据入库成功！");
				}else {
					LogUtil.getLogger().info("基准情景气象数据入库失败！");
				}
			}
		}
		
		int compareTo = endDate.compareTo(scenEndDate);
		if (compareTo==0) {
			//基准情景的dp_met运行完毕
			LogUtil.getLogger().info("基准情景的气象数据入库完毕!");
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
		int domains = 3;
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
				requestParams.setUserId(userId);
				requestParams.setMissionId(missionId);
				requestParams.setDomainId(domainId);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				//requestParams.setShowType("concn");
				if (flag==1) {
					//气象入库
					
				}
				if (flag==0) {
					//化学入库
					boolean res = concnService.requestConcnData(requestParams);
					if (res) {
						LogUtil.getLogger().info("实时预报数据入库成功！");
						int compareTo = endDate.compareTo(scenEndDate);//模式结束时间和情景的结束时间
						if (compareTo==0&&8==stepindex) {
							//模式结束时间和情景的结束时间一致表示数据入库完毕
							LogUtil.getLogger().info("数据入库完毕！");
							//修改情景状态
							readyData.updateScenStatusUtil(8l, scenarioId);
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
		// TODO Auto-generated method stub
		
	}
	/**
	 * @Description: 后评估情景
	 * @param tasksScenarinoId
	 * @param tasksEndDate   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午9:47:32
	 */
	public void readyRukuparamspostPevtion(Long tasksScenarinoId,
			Date tasksEndDate) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add(DateUtil.DATEtoString(new Date(), "yyyy-MM-dd"));
		String[] timepointarray = {"h","d"};
		for(int i = 0; i<3; i++){
			for (String string : timepointarray) {
				RequestParams requestParams = new RequestParams();
				requestParams.setUserId(1l);
				requestParams.setMissionId(2l);
				requestParams.setDomainId(1l);
				requestParams.setDomain(i);
				requestParams.setTimePoint(string);
				requestParams.setDate(list);
				//requestParams.setShowType("concn");
			//	boolean res = concnService.requestConcnData(requestParams);
			//	if (res) {
				//	LogUtil.getLogger().info("实时预报气象数据入库成功！");
			//	}else {
				//	LogUtil.getLogger().info("实时预报气象数据入库失败！");
				//}
			}
		}
		
	}
}
