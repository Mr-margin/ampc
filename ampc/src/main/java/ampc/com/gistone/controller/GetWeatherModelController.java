package ampc.com.gistone.controller;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TRealForecastMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TRealForecast;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.redisqueue.ReadyData;
import ampc.com.gistone.redisqueue.RedisUtilServer;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

/**  
 * @Title: GetQueue.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月16日 上午9:27:13
 * @version 1.0
 */
@RestController
@RequestMapping
public class GetWeatherModelController {
	@Autowired
	private RedisUtilServer redisUtilServer;
	//加载准备数据工具类
	
	@Autowired
	private ReadyData readyData;
	
	//情景详情映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
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
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			//持久化cores
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
			if (updateCores>0) {
				 readyData.branchPredict(scenarinoId, cores, scenarinoType, missionType);
				 return AmpcResult.build(0, "ok");
			}else {
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
	 * @Description: TODO
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return 实时预报启动接口   接受关于消息的数据包括用户ID 情景ID  情景模式固定 其中固定情景类型为4
	 * Map<String,String>  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月16日 下午2:49:04
	 */
	@RequestMapping("/ModelType/RealForecast")
	public AmpcResult getStartPAM(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		String token = (String) requestDate.get("token");
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			
			HashMap<String, Object> body = new HashMap<String,Object>();
			
			body.put("userId", userId);
			body.put("missionId", missionId);
			body.put("scenarinoId", scenarinoId);
			body.put("scenarinoType", scenarinoType);
			body.put("cores", cores);
			//持久化cores
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
			//准备实时预报的数据
			//readyData.readyRealMessageDataFirst(body);
		//	readyData.getLastUngrib(body);
			
			return AmpcResult.build(0, "ok");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	
	
	
	/**
	 * 
	 * @Description: 后评估的任务中的基准情景
	 * @param requestDate
	 * @param request
	 * @param respons   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 上午10:00:14
	 */
	@RequestMapping("/postevaluation/BaseSituation")
	public AmpcResult BaseSituation(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			
			HashMap<String, Object> body = new HashMap<String,Object>();
			
			body.put("userId", userId);
			body.put("missionId", missionId);
			body.put("scenarinoId", scenarinoId);
			body.put("scenarinoType", scenarinoType);
			body.put("cores", cores);
			//准备基准情景的参数
			//readyData.readyBaseData(body);
			return AmpcResult.build(0, "ok");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	
	/**
	 * 
	 * @Description: 预评估任务的预评估情景
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午5:35:42
	 */
	
	@RequestMapping("/preevaluation/pre_evaluation_Situation")
	public AmpcResult preEvaluation(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			System.out.println(userId);
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			System.out.println(missionId);
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			System.out.println(scenarinoId);
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			System.out.println(scenarinoType);
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			System.out.println(cores);
			
			HashMap<String, Object> body = new HashMap<String,Object>();
			
			body.put("userId", userId);
			body.put("missionId", missionId);
			body.put("scenarinoId", scenarinoId);
			body.put("scenarinoType", scenarinoType);
			body.put("cores", cores);
			//持久化cores
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
		//	readyData.readyPreEvaluationSituationDataFirst(body);
			return AmpcResult.build(0, "ok");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	
	/**
	 * 
	 * @Description:  预评估任务的后评估情景
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return    
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 上午11:33:13
	 */
	@RequestMapping("/preevaluation/post_evaluation_Situation")
	public AmpcResult prePostEvaluation(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			
			HashMap<String, Object> body = new HashMap<String,Object>();
			
			body.put("userId", userId);
			body.put("missionId", missionId);
			body.put("scenarinoId", scenarinoId);
			body.put("scenarinoType", scenarinoType);
			body.put("cores", cores);
			//持久化cores
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
	//		readyData.readyPrePostEvaluationSituationData(body);
			return AmpcResult.build(0, "ok");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	
	/**
	 * 
	 * @Description: 后评估任务的后评估情景
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午3:31:05
	 */
	@RequestMapping("/postevaluation/post_evaluation_Situation")
	public AmpcResult postPostEvaluation(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			
			HashMap<String, Object> body = new HashMap<String,Object>();
			
			body.put("userId", userId);
			body.put("missionId", missionId);
			body.put("scenarinoId", scenarinoId);
			body.put("scenarinoType", scenarinoType);
			body.put("cores", cores);
			//持久化cores
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
		//	readyData.readypost_PostEvaluationSituationData(body);
			if (updateCores>0) {
				return AmpcResult.build(0, "ok");
			}else {
				return AmpcResult.build(1000, "启动失败");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "启动失败");
		}
	}
	
	

}
