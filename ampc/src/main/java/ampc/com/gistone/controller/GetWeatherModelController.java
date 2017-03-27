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
import ampc.com.gistone.database.model.TRealForecast;
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
	//实时预报映射
	@Autowired
	private TRealForecastMapper tRealForecastMapper;
	
	

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
			System.out.println(1);
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			System.out.println(2);
			
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
			//准备实时预报的数据
		//	readyData.readyRealMessageData(body);
			readyData.getLastUngrib(body);
			return AmpcResult.build(0, "ok");
			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	/**
	 * 
	 * @Description: （不用）
	 * @param request
	 * @param response
	 * @return   
	 * String  创建实时预报类型 跟新实时预报表格 
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 下午2:19:05
	 */
	@RequestMapping("/CreateRealForecast")
	public AmpcResult getData(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		System.out.println(token);
		try {
			System.out.println(1);
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			System.out.println(2);
			Map<String,Object> data=(Map)requestDate.get("data");
			System.out.println(3);
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			System.out.println(userId);
			//创建时间
			Date createTime = new Date();
			System.out.println(createTime);
			//实时预报类型 约定为4
			Long forecastScenarinoType = (long) 4;
			//预报范围
			Long rangeDay = Long.parseLong(data.get("rangeDay").toString());
			System.out.println(rangeDay);
			//预报状态 (1 表示执行 0 表示未执行） 更新开始就直接插入为1
			Long forecastStatus = (long) 1;
			//spinup
			Long spinup =Long.parseLong(data.get("spinup").toString());
			System.out.println(spinup);
			//预报开始时间（年月日形式）
			String starttimeString = (String) (data.get("startTime").toString());
			Date startTime = DateUtil.StrtoDateYMD(starttimeString);
			System.out.println("2"+new Date());
			TRealForecast tRealForecast = new TRealForecast();
			tRealForecast.setUserId(userId);
			tRealForecast.setCreateTime(createTime);
			tRealForecast.setForecastScenarinoType(forecastScenarinoType);
			tRealForecast.setRangeDay(rangeDay);
			tRealForecast.setForecastStatus(forecastStatus);
			tRealForecast.setSpinup(spinup);
			tRealForecast.setStartTime(startTime);
			System.out.println(tRealForecast+"0000000");
			//添加到数据库
			int i = tRealForecastMapper.insertSelective(tRealForecast);
			if (i>0) {
		    	return	AmpcResult.ok(i);
			}
			return AmpcResult.build(1000, "添加失败",null);
		} catch (Exception e) {
			// TODO: handle exception
			return AmpcResult.build(1000, "参数错误",null);
		}
		//return AmpcResult.build(0, "ok");
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
			
			readyData.readyBaseData(body);
			return AmpcResult.build(0, "ok");
			
			
			
			
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "添加失败");
		}
	}
	
	

}
