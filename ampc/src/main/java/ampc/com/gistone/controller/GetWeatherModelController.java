/**  
 * @Title: GetWeatherModelController.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月12日 上午11:57:45
 * @version 
 */
package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.ReadyData;
import ampc.com.gistone.redisqueue.RedisUtilServer;
import ampc.com.gistone.redisqueue.StopModelData;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: GetWeatherModelController.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月12日 上午11:57:45
 * @version 1.0
 */
@RestController
@RequestMapping
public class GetWeatherModelController {
	
	//加载准备数据工具类
	
	@Autowired
	private ReadyData readyData;
	
	//情景详情映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	@Autowired
	private StopModelData stopModelData;
	/**
	 * 
	 * @Description: 模式启动接口
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月13日 下午4:23:53
	 */
	
	@RequestMapping("/ModelType/startModel")
	public AmpcResult getRunModel(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		String token = (String) requestDate.get("token");
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//任务类型
			Integer missionType = Integer.parseInt(data.get("missionType").toString());
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			//持久化cores 并更新状态变为模式执行中
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
			//获取减排参数
		//	Map<String, String> emis = readyData.getEmis(missionId,scenarinoId,userId,scenarinoType);
			
			//清空taskstatus中的index和tasksenddate 以及情景运行完成状态beizhu为0
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setStepindex(null);
			tTasksStatus.setTasksEndDate(null);
			tTasksStatus.setBeizhu("0");
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			//清空模式运行状态
			int updateByPrimaryKey = tTasksStatusMapper.updateByPrimaryKeySelective(tTasksStatus);
			System.out.println(updateByPrimaryKey+"清空模式运行状态");
			if (updateByPrimaryKey>0) {
				LogUtil.getLogger().info("情况模式的运行状态");
			}else {
				LogUtil.getLogger().info("清空运行状态失败！");
			}
			String conditionString =  readyData.branchPredict(scenarinoId, cores, scenarinoType, missionType,missionId,userId);
			if (updateCores>0&&conditionString.equals("ok")) {
				//更新状态
				Map map = new HashMap();
				map.put("scenarinoId", scenarinoId);
				Long status = (long)6;
				map.put("scenarinoStatus", status);
				int a = tScenarinoDetailMapper.updateStatus(map);
				if (a>0) {
					 return AmpcResult.build(0, "ok");
				}else {
					return AmpcResult.build(1000, "启动失败");
				}
			}
			if(updateCores>0&&conditionString.equals("false")){
				
				return AmpcResult.build(1000, "基准情景模式未完成！");
				
			}
			if (updateCores>0&&conditionString.equals("error")) {
				return AmpcResult.build(1000, "减排计算失败");
			}
			
			else {
				return AmpcResult.build(1000, "启动失败");
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "启动失败");
		}
		
	}
	/**
	 * 
	 * @Description: 取减排系数的接口
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月13日 下午4:22:55
	 */
	@RequestMapping("/saveEmis")
	public AmpcResult saveEmisData(@RequestBody Map<String, Object> requestDate, Long jobId,HttpServletRequest request,HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			String sourceid = requestDate.get("sourceid").toString();
			String psal = requestDate.get("psal").toString();
			String ssal = requestDate.get("ssal").toString();
			String meiccityconfig = requestDate.get("meiccityconfig").toString();
			Long scenarinoId = Long.parseLong( requestDate.get("scenarioid").toString());
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setSourceid("1");
			tTasksStatus.setCalctype("server");
			tTasksStatus.setPsal(psal);
			tTasksStatus.setSsal(ssal);
			tTasksStatus.setMeiccityconfig(meiccityconfig);
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			System.out.println(tTasksStatus.toString());
		//添加到对应的情景下面去
			int i = tTasksStatusMapper.updateEmisData(tTasksStatus);
		//	int i = tTasksStatusMapper.updateByPrimaryKeySelective(tTasksStatus);
			if (i>0) {
				LogUtil.getLogger().info("获取减排系数成功！并更新了数据库！");
				//后评估任务后评估情景
				//readyData.readypost_PostEvaluationSituationData(scenarinoid);
				//准备对应的队列参数数据
				readyData.needJPsituation(scenarinoId);
				//预评估任务的预评估情景
	//			readyData.readyPreEvaluationSituationDataFirst(scenarioid);
				return AmpcResult.build(0, "ok");
			}else {
				return AmpcResult.build(1000, "失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
		
	}
	/**
	 * 
	 * @Description: 停止模式
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月14日 下午5:16:58
	 */
	@RequestMapping("/ModelType/sendstopModel")
	public AmpcResult stopModel(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		Long userId;
		Long domainId;
		Long scenarinoId;
		Long missionId;
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			try {
				//用户id
				userId = Long.parseLong(data.get("userId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "userId参数错误");
			}
			try {
				//doaminid
				domainId = Long.parseLong(data.get("domainId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "domainId参数错误");
			}
			try {
				//情景ID
				scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "scenarinoId参数错误");
			}
			try {
				//任务ID
				missionId = Long.parseLong(data.get("missionId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "missionId参数错误");
			}
			
			boolean branchModel = stopModelData.branchModel(scenarinoId,domainId,missionId,userId);
				if (branchModel) {
					return AmpcResult.build(0, "停止请求发送成功！");
				}else {
					return AmpcResult.build(1004, "停止请求发送失败！");
				}
				
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "系统错误");
		}
		
		
	}
	@RequestMapping("/ModelType/successStopModel")
	public AmpcResult stopModelsuccess(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		Long userId;
		Long scenarinoId;
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			try {
				//用户id
				userId = Long.parseLong(data.get("userId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "userId参数错误");
			}
			try {
				//情景ID
				scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			} catch (Exception e) {
				// TODO: handle exception
				return AmpcResult.build(1003, "scenarinoId参数错误");
			}
			TTasksStatus status = tTasksStatusMapper.selectStatus(scenarinoId);
			String stopStatus = status.getStopStatus();
			if ("0".equals(stopStatus.trim())) {
				return AmpcResult.build(0, "模式终止成功！");
			}
			else if ("1".equals(stopStatus.trim())){
				return AmpcResult.build(0, "模式终止失败！");
			}else if("2".equals(stopStatus.trim())){
				 return AmpcResult.build(0, "模式终止过程中！");
			}else {
				return AmpcResult.build(1000, "系统错误");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "系统错误");
		}
		
	}
	/**
	 * 
	 * @Description: 请求减排计算
	 * @param sourceid
	 * @return   
	 * Map<String,Object>  获取emis的数据
	 * @throws
	 * @author yanglei
	 * @date 2017年3月17日 下午2:19:53
	 */
	public Map<String, String> getEmis(Long sourceid) {
		/*Map<String,String> map = new HashMap<String,String>();
		String url="http://192.168.1.128:8082/ampc/app";
		String getResult=ClientUtil.doPost(url,sourceid.toString());*/
		return null;
	}
}
