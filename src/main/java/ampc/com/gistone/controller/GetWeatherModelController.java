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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.support.logging.Log;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
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
import ampc.com.gistone.util.RegUtil;

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
	
	//任务详情映射
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
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
			String conditionString =  readyData.branchPredict(scenarinoId, scenarinoType, missionType,missionId,userId);
			if (updateCores>0&&conditionString.equals("ok")) {
				//更新状态
				Map map = new HashMap();
				map.put("scenarinoId", scenarinoId);
				Long status = (long)6;
				map.put("scenarinoStatus", status);
				int a = tScenarinoDetailMapper.updateScenType(map);
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
			return AmpcResult.build(1000, "模式启动异常！");
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
			//通过情景ID获取清单ID
		//	Long sourceid = tMissionDetailMapper.getsourceid(missionId);
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
				throw new SQLException("GetWeatherModelController  减排系数存库失败!");
			}
		}catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());	
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
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Object param = data.get("userId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController userId为空或出现非法字符!");
				return AmpcResult.build(1003, "userId为空或出现非法字符!");
			}
			Long userId =Long.parseLong(param.toString());
			param = data.get("domainId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController domainId为空或出现非法字符!");
				return AmpcResult.build(1003, "domainId为空或出现非法字符!");
			}
			//doaminid
			Long domainId = Long.parseLong(param.toString());
			//情景ID
			param = data.get("scenarinoId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController scenarinoId为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoId为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(param.toString());
			/*//情景类型
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			Long scenarinoType = Long.parseLong(param.toString());*/
			
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			//任务ID
			Long missionId = Long.parseLong(param.toString());
			
			/*param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			//任务类型
			Long missionType = Long.parseLong(param.toString());*/
			
			param = data.get("flag");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController flag为空或出现非法字符!");
				return AmpcResult.build(1003, "flag为空或出现非法字符!");
			}
			Integer flag = Integer.parseInt(param.toString());
			TTasksStatus tasksStatus = tTasksStatusMapper.selectStatus(scenarinoId);
			String sendtime = tasksStatus.getBeizhu2();
			String compentstatus = tasksStatus.getBeizhu();
			if (flag==0) {
				//判断是否可以终止
				if (sendtime.equals("0")&&compentstatus.equals("0")) {
					//还没有发送数据到队列，可以直接终止-标记tasks状态为2
					TTasksStatus tTasksStatus = new TTasksStatus();
					tTasksStatus.setTasksScenarinoId(scenarinoId);
					tTasksStatus.setStopStatus("2");//表示不可发送消息到队列
					int i = tTasksStatusMapper.updatestopstatus(tTasksStatus);
					if (i>0) {
						//修改情景状态
						Map map = new HashMap();
						map.put("scenarinoStatus", 5l);
						map.put("scenarinoId", scenarinoId);
						int updateScenType = tScenarinoDetailMapper.updateScenType(map);
						if (updateScenType>0) {
							return AmpcResult.build(0, "停止成功！");
						}else {
							throw new SQLException("GetWeatherModelController  修改情景状态失败!");
						}
					}else {
						throw new SQLException("GetWeatherModelController  更新发送终止的指令状态失败!");
					}
				}
				else if (!"0".equals(sendtime)&&compentstatus.equals("2")) {
					//模式已经执行完毕 正在入库等 不能终止
					return AmpcResult.build(1004, "模式正在后处理，不允许终止！");
				}
				else {
					//0表示终止 开始发送终止的指令
					boolean branchModel = stopModelData.StopModel(scenarinoId,domainId,missionId,userId);
					if (branchModel) {
						return AmpcResult.build(0, "停止成功！");
					}else {
						return AmpcResult.build(1004, "停止失败！");
					}
				}
				
			}
			else if (flag==1) {
				//判断暂停的条件
				if (sendtime.equals("0")&&compentstatus.equals("0")) {
					//还未发送过数据  -不允许暂停
					return AmpcResult.build(1004, "消息未执行不允许暂停！");
				}
				else if (!"0".equals(sendtime)&&compentstatus.equals("2")) {
					//模式已经执行完毕 正在入库等 不允许暂停
					return AmpcResult.build(1004, "模式正在后处理，不允许暂停！");
				}
				else {
					//1表示暂停
					boolean branchModel = stopModelData.pauseModel(scenarinoId,domainId,missionId,userId);
					if (branchModel) {
						return AmpcResult.build(0, "暂停成功！");
					}else {
						return AmpcResult.build(1004, "暂停失败！");
					}
				}
			}else {
				return AmpcResult.build(1003, "flag参数错误！");
			}
			
		}catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return AmpcResult.build(1000, "系统错误");
		}
		
		
	}
	/**
	 * 
	 * @Description: 续跑的接口
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月2日 下午4:38:35
	 */
	@RequestMapping("/ModelType/continueModel")
	public AmpcResult continueModel(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		String token = (String) requestDate.get("token");
		//获取数据包
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Object param = data.get("userId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController userId为空或出现非法字符!");
				return AmpcResult.build(1003, "userId为空或出现非法字符!");
			}
			Long userId =Long.parseLong(param.toString());
			//情景ID
			param = data.get("scenarinoId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController scenarinoId为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoId为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(param.toString());
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			//任务ID
			Long missionId = Long.parseLong(param.toString());
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			//情景类型
			Integer scenarinoType = Integer.parseInt(param.toString());
			
			param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			//任务类型
			Integer missionType = Integer.parseInt(param.toString());
			try {
				TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(scenarinoId);
				if (null!=selectStatus) {
					Long scenarinoStatus = tScenarinoDetailMapper.selectScenExecStatus(scenarinoId);
					if (null!=scenarinoStatus) {
						String compStatus = selectStatus.getBeizhu();
						String sendtime = selectStatus.getBeizhu2();
//						String pauseStatus = selectStatus.getPauseStatus();
						//1.模式执行中处于暂停的状态 2.出错状态下的续跑
						 if (scenarinoStatus==7&&!sendtime.equals("0")&&!compStatus.equals("0")) {
							 boolean continueModelByError = readyData.continuePredict(scenarinoId, scenarinoType, missionType, missionId, userId);
							if (continueModelByError) {
								return AmpcResult.build(0, "续跑成功！");
							}else {
								return AmpcResult.build(1004, "续跑失败！");
							}
						}else{
							return AmpcResult.build(1004, "其他错误");
						}
						/*//2.出错状态下的续跑
						else if(scenarinoStatus==9&&!sendtime.equals("0")&&!compStatus.equals("0")){
							boolean continueModelByError = readyData.continuePredictByError(scenarinoId, scenarinoType, missionType, missionId, userId);
							if (continueModelByError) {
								return AmpcResult.build(0, "续跑成功！");
							}else {
								return AmpcResult.build(1004, "续跑失败！");
							}*/
						
					}else {
						throw new SQLException("GetWeatherModelController  查找情景的状态失败!");
					}
				}else {
					throw new SQLException("GetWeatherModelController  查找情景的tasks状态失败!");
				}
			}catch (SQLException e) {
				LogUtil.getLogger().error(e.getMessage(),e);
				return AmpcResult.build(1000,e.getMessage());		
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error(e.getMessage(),e);
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
