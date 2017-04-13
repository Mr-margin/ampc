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
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

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
	
	
	@Autowired
	private RedisUtilServer redisUtilServer;
	//加载准备数据工具类
	
	@Autowired
	private ReadyData readyData;
	
	//情景详情映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	
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
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//计算核数
			Long cores = Long.parseLong(data.get("cores").toString());
			//持久化cores 并更新状态变为模式执行中
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			tScenarinoDetail.setScenarinoStatus((long)6);
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
	public AmpcResult saveEmisData(HttpServletRequest request,HttpServletResponse response) {
		try {
		String sourceid = request.getParameter("sourceid");
		String calctype = request.getParameter("calctype");
		String psal = request.getParameter("psal");
		String ssal = request.getParameter("ssal");
		String meiccityconfig = request.getParameter("meiccityconfig");
		Long scenarioid =Long.parseLong(request.getParameter("scenarioid"));
		TTasksStatus tTasksStatus = new TTasksStatus();
		tTasksStatus.setSourceid(sourceid);
		tTasksStatus.setCalctype(calctype);
		tTasksStatus.setPsal(psal);
		tTasksStatus.setSsal(ssal);
		tTasksStatus.setMeiccityconfig(meiccityconfig);
		tTasksStatus.setTasksScenarinoId(scenarioid);
		//添加到对应的情景下面去
			int i = tTasksStatusMapper.updateEmisData(tTasksStatus);
			if (i>0) {
				return AmpcResult.build(0, "ok");
			}else {
				return AmpcResult.build(1000, "失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
			return AmpcResult.build(1000, "参数错误");
		}
		
		
	}
	
}
