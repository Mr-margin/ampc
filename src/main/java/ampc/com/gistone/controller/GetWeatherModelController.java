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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;














import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.ReadyData;
import ampc.com.gistone.redisqueue.StopModelData;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
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
@Controller
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
			Object param = data.get("userId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel userId为空或出现非法字符!");
				return AmpcResult.build(1003, "userId为空或出现非法字符!");
			}
			Long userId = Long.parseLong(data.get("userId").toString());
			
			//任务类型
			param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			Integer missionType = Integer.parseInt(data.get("missionType").toString());
			
			//情景ID
			param = data.get("scenarinoId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel scenarinoId为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoId为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			
			//任务ID
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景类型
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			
			//计算核数
			param = data.get("cores");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController getRunModel cores为空或出现非法字符!");
				return AmpcResult.build(1003, "cores为空或出现非法字符!");
			}
			Long cores = Long.parseLong(data.get("cores").toString());
			//持久化cores 并更新状态变为模式执行中
			TScenarinoDetail tScenarinoDetail = new TScenarinoDetail();
			tScenarinoDetail.setExpand3(cores.toString());
			tScenarinoDetail.setScenarinoId(scenarinoId);
			int updateCores = tScenarinoDetailMapper.updateCores(tScenarinoDetail);
			//清空taskstatus中的index和tasksenddate 以及情景运行完成状态beizhu为0
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setStepindex(null);
			tTasksStatus.setTasksEndDate(null);
			tTasksStatus.setBeizhu("0");
			tTasksStatus.setBeizhu2("0");
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			//清空模式运行状态
			int updateByPrimaryKey = tTasksStatusMapper.updatecleanStatus(tTasksStatus);
			if (updateByPrimaryKey>0) {
				LogUtil.getLogger().info("情况模式的运行状态,updateByPrimaryKey："+updateByPrimaryKey);
			}else {
				LogUtil.getLogger().info("清空运行状态失败！updateByPrimaryKey："+updateByPrimaryKey);
			}
			TTasksStatus tTasksStatus2 = new TTasksStatus();
			tTasksStatus2.setTasksScenarinoId(scenarinoId);
			tTasksStatus2.setStopStatus("0");//表示不可发送消息到队列
			int i = tTasksStatusMapper.updatestopstatus(tTasksStatus2);
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
			if (updateCores>0&&conditionString.equals("queue_error")) {
				return AmpcResult.build(1000, "启动失败");
			}
			
			else {
				return AmpcResult.build(1000, "启动失败");
			}
			
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("GetWeatherModelController runmodel",e);
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
	@Transactional
	@RequestMapping("/saveEmis")
	public AmpcResult saveEmisData(@RequestBody Map<String, Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
//			String sourceid = requestDate.get("sourceid").toString();
			//通过情景ID获取清单ID
		//	Long sourceid = tMissionDetailMapper.getsourceid(missionId);
			Object param = requestDate.get("psal");
			if (!RegUtil.CheckParameter(param, "String", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--saveEmis  psal为空或出现非法字符!");
				return AmpcResult.build(1003, "psal为空或出现非法字符!");
			}
			param = requestDate.get("ssal");
			if (!RegUtil.CheckParameter(param, "String", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--saveEmis  ssal为空或出现非法字符!");
				return AmpcResult.build(1003, "ssal为空或出现非法字符!");
			}
			param = requestDate.get("controlfile");
			if (!RegUtil.CheckParameter(param, "String", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--saveEmis  controlfile为空或出现非法字符!");
				return AmpcResult.build(1003, "controlfile为空或出现非法字符!");
			}
			param = requestDate.get("meiccityconfig");
			if (!RegUtil.CheckParameter(param, "String", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--saveEmis  meiccityconfig为空或出现非法字符!");
				return AmpcResult.build(1003, "meiccityconfig为空或出现非法字符!");
			}
			param = requestDate.get("scenarioid");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--saveEmis  scenarioid为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarioid为空或出现非法字符!");
			}
			
			String psal = requestDate.get("psal").toString();
			String ssal = requestDate.get("ssal").toString();
			String controlfile = requestDate.get("controlfile").toString();
			String meiccityconfig = requestDate.get("meiccityconfig").toString();
			Long scenarinoId = Long.parseLong(requestDate.get("scenarioid").toString());
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setSourceid("1");//清单ID
			tTasksStatus.setCalctype("server");//计算方式
			//减排系数路径
			tTasksStatus.setPsal(psal);
			tTasksStatus.setSsal(ssal);
			tTasksStatus.setMeiccityconfig(meiccityconfig);
			//setControlfile
			
			tTasksStatus.setTasksExpand3(controlfile);
//			tTasksStatus.setTasksExpand3("/work/modelcloud/lixin_meic/hebei/cf/cf_zero.csv");
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			tTasksStatus.setTasksExpand1(0l);
		//添加到对应的情景下面去
//			int i = tTasksStatusMapper.updateEmisData(tTasksStatus);
			int	i = tTasksStatusMapper.updateEmisData(tTasksStatus);
		//	int i = tTasksStatusMapper.updateByPrimaryKeySelective(tTasksStatus);
			if (i>0) {
				LogUtil.getLogger().info("获取减排系数成功！并更新了数据库！"+scenarinoId);
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
			LogUtil.getLogger().error("GetWeatherModelController  saveEmisData",e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());	
		} catch (Exception e) {
			LogUtil.getLogger().error("GetWeatherModelController  saveEmisData",e.getMessage(),e);
			return AmpcResult.build(1000, "参数错误");
		}
		
	}
	/**
	 * 
	 * @Description: 停止模式/暂停模式
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
			//情景类型
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			Long scenarinoType = Long.parseLong(param.toString());
			
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			//任务ID
			Long missionId = Long.parseLong(param.toString());
			
			param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			//任务类型
			Long missionType = Long.parseLong(param.toString());
			
			param = data.get("flag");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController flag为空或出现非法字符!");
				return AmpcResult.build(1003, "flag为空或出现非法字符!");
			}
			Integer flag = Integer.parseInt(param.toString());
			TScenarinoDetail selectByPrimaryKey = null;
			try {
				selectByPrimaryKey = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			} catch (Exception e) {
				LogUtil.getLogger().error("GetWeatherModelController stopModel:查找情景状态出错！",e.getMessage(),e);
			}
			Long scenarinoStatus = selectByPrimaryKey.getScenarinoStatus();
			TTasksStatus tasksStatus = tTasksStatusMapper.selectStatus(scenarinoId);
			String sendtime = tasksStatus.getBeizhu2();
			String compentstatus = tasksStatus.getBeizhu();
			Date tasksEndDate = tasksStatus.getTasksEndDate();
			Date tasksScenarinoEndDate = tasksStatus.getTasksScenarinoEndDate();
			Long stepindex = tasksStatus.getStepindex();
			/*
			 * 用于预评估
			 */
			Integer compareTo = null;
			Integer compareTo2 = null;
			if (!"0".equals(sendtime)) {
				Date sendDate = null;
				try {
					sendDate = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
				} catch (Exception e) {
					LogUtil.getLogger().error("GetWeatherModelController-stopModel:时间格式转换错误！",e);
				}
				//比价情景发送了的时间和已经完成了的时间
				compareTo = tasksEndDate.compareTo(sendDate);
				//比较情景的任务结束时间和情景的结束时间
				compareTo2 = tasksEndDate.compareTo(tasksScenarinoEndDate);
			}
			
			/**
			 * 对于预评估情景有两种情况可以直接终止和暂停
			 * 1.模式在减排计算的过程中，点击的暂停和终止-模式不用发送指令到消息队列----这种情景适用于所有的情景
			 * 2.模式在某一次执行完毕中，由定时器等待的发送下一次的消息的情况下，可以直接终止和暂停，因为此时模式没有执行---预评估独有
			 */
/*			if (scenarinoType==1&&missionType==2) {
				//预评估情景
				if (scenarinoStatus!=9&&!"0".equals(sendtime)&&null!=tasksEndDate) {
					Date sendDate = null;
					try {
						sendDate = DateUtil.StrtoDateYMD(sendtime, "yyyyMMdd");
					} catch (Exception e) {
						LogUtil.getLogger().error("GetWeatherModelController-stopModel:时间格式转换错误！",e);
					}
					//比价情景发送了的时间和已经完成了的时间
					int compareTo = tasksEndDate.compareTo(sendDate);
					//比较情景的任务结束时间和情景的结束时间
					int compareTo2 = tasksEndDate.compareTo(tasksScenarinoEndDate);
					if (4==stepindex&&compareTo==0&&compareTo2>0) {
						
						 * 表示某一次的模式执行完毕，正在入库处理或者等待定时器发送下一次消息时间
						 * 这时候可以直接停止
						 * 
						if (flag==0) {
							TTasksStatus tTasksStatus = new TTasksStatus();
							tTasksStatus.setTasksScenarinoId(scenarinoId);
							tTasksStatus.setStopStatus("2");//2表示不可发送消息到队列
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
									throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
								}
							}else {
								throw new SQLException("GetWeatherModelController  更新终止的指令状态失败!scenarinoId："+scenarinoId);
							}
						}else if (flag==1){
							//flag=1--可直接暂停
							TTasksStatus tTasksStatus = new TTasksStatus();
							tTasksStatus.setTasksScenarinoId(scenarinoId);
							tTasksStatus.setPauseStatus("2");;//2表示不可发送消息到队列
							int i = tTasksStatusMapper.updatepausestatus(tTasksStatus);
							if (i>0) {
								//修改情景状态
								Map map = new HashMap();
								map.put("scenarinoStatus", 7l);
								map.put("scenarinoId", scenarinoId);
								int updateScenType = tScenarinoDetailMapper.updateScenType(map);
								if (updateScenType>0) {
									return AmpcResult.build(0, "暂停成功！");
								}else {
									throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
								}
							}else {
								throw new SQLException("GetWeatherModelController--updatepausestatus 修改情景暂停状态失败!scenarinoId："+scenarinoId);
							}
						}else {
							return AmpcResult.build(1003, "flag参数错误！");
						}
					}
				}
			}*/
			if (flag==0) {
				//判断是否可以终止
				if (sendtime.equals("0")&&compentstatus.equals("0")) {
					//还没有发送数据到队列，可以直接终止-标记tasks状态为2
					TTasksStatus tTasksStatus = new TTasksStatus();
					tTasksStatus.setTasksScenarinoId(scenarinoId);
					tTasksStatus.setStopStatus("2");//2表示不可发送消息到队列
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
							throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
						}
					}else {
						throw new SQLException("GetWeatherModelController  更新发送终止的指令状态失败!scenarinoId："+scenarinoId);
					}
				}
				else if (!"0".equals(sendtime)&&compentstatus.equals("2")) {
					//模式已经执行完毕 正在入库等 不能终止
					return AmpcResult.build(1004, "模式正在后处理，不允许终止！");
				}
				else if (scenarinoStatus==9) {
					//模式出错的时候点接终止直接成功 并且清空运行的状态
					//修改情景状态
					Map map = new HashMap();
					map.put("scenarinoStatus", 5l);
					map.put("scenarinoId", scenarinoId);
					int updateScenType = tScenarinoDetailMapper.updateScenType(map);
					if (updateScenType>0) {
						//清空模式运行的记录
						TTasksStatus cleantTasksStatus =new TTasksStatus();
						cleantTasksStatus.setModelErrorStatus("");
						cleantTasksStatus.setBeizhu("0");
						cleantTasksStatus.setBeizhu2("0");
						cleantTasksStatus.setCalctype("");
						cleantTasksStatus.setStepindex(0l);
						cleantTasksStatus.setTasksEndDate(null);
						cleantTasksStatus.setSourceid("");
						cleantTasksStatus.setContunueStatus("");
						cleantTasksStatus.setMeiccityconfig("");
						cleantTasksStatus.setTasksExpand1(null);
						cleantTasksStatus.setTasksExpand3("");
						cleantTasksStatus.setPsal("");
						cleantTasksStatus.setSsal("");
						cleantTasksStatus.setStopStatus(null);
						cleantTasksStatus.setTasksSendTime(null);
						cleantTasksStatus.setTasksScenarinoId(scenarinoId);
						int updatecleanStatus = tTasksStatusMapper.updatecleanStatus(cleantTasksStatus);
						if (updatecleanStatus>0) {
							return AmpcResult.build(0, "停止成功！");
						}else {
							throw new SQLException("GetWeatherModelController 模式出错终止  清空情景状态失败!scenarinoId："+scenarinoId);
						}
					}else {
						throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
					}
				}
				else if (scenarinoType==1&&missionType==2&&scenarinoStatus!=9&&!"0".equals(sendtime)&&null!=tasksEndDate&&4==stepindex&&compareTo==0&&compareTo2>0) {
					/**
					 * 预评估情景的特殊情况
					 * 预评估情景
					 * 表示某一次的模式执行完毕，正在入库处理或者等待定时器发送下一次消息时间
					 * 这时候可以直接停止
					 */
					TTasksStatus tTasksStatus = new TTasksStatus();
					tTasksStatus.setTasksScenarinoId(scenarinoId);
					tTasksStatus.setStopStatus("2");//2表示不可发送消息到队列
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
							throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
						}
					}else {
						throw new SQLException("GetWeatherModelController  更新终止的指令状态失败!scenarinoId："+scenarinoId);
					}		
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
				}else if (scenarinoType==1&&missionType==2&&scenarinoStatus!=9&&!"0".equals(sendtime)&&null!=tasksEndDate&&4==stepindex&&compareTo==0&&compareTo2>0) {
					TTasksStatus tTasksStatus = new TTasksStatus();
					tTasksStatus.setTasksScenarinoId(scenarinoId);
					tTasksStatus.setPauseStatus("2");;//2表示不可发送消息到队列
					int i = tTasksStatusMapper.updatepausestatus(tTasksStatus);
					if (i>0) {
						//修改情景状态
						Map map = new HashMap();
						map.put("scenarinoStatus", 7l);
						map.put("scenarinoId", scenarinoId);
						int updateScenType = tScenarinoDetailMapper.updateScenType(map);
						if (updateScenType>0) {
							return AmpcResult.build(0, "暂停成功！");
						}else {
							throw new SQLException("GetWeatherModelController  修改情景状态失败!scenarinoId："+scenarinoId);
						}
					}else {
						throw new SQLException("GetWeatherModelController--updatepausestatus 修改情景暂停状态失败!scenarinoId："+scenarinoId);
					}
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
			LogUtil.getLogger().error("GetWeatherModelController  stopModel",e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());		
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("GetWeatherModelController  stopModel",e.getMessage(),e);
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
				LogUtil.getLogger().error("GetWeatherModelController continueModel userId为空或出现非法字符!");
				return AmpcResult.build(1003, "userId为空或出现非法字符!");
			}
			Long userId =Long.parseLong(param.toString());
			//情景ID
			param = data.get("scenarinoId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController continueModel scenarinoId为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoId为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(param.toString());
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController continueModel missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			//任务ID
			Long missionId = Long.parseLong(param.toString());
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController continueModel scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			//情景类型
			Integer scenarinoType = Integer.parseInt(param.toString());
			
			param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController continueModel missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			//任务类型
			Integer missionType = Integer.parseInt(param.toString());
			try {
				TTasksStatus selectStatus = tTasksStatusMapper.selectStatus(scenarinoId);
				if (null!=selectStatus) {
					Long scenarinoStatus = tScenarinoDetailMapper.selectScenExecStatus(scenarinoId);
					if (null!=scenarinoStatus) {
//						String compStatus = selectStatus.getBeizhu();
						String sendtime = selectStatus.getBeizhu2();
						Long tasksExpand1 = selectStatus.getTasksExpand1();//减排计算是否成功
//						String pauseStatus = selectStatus.getPauseStatus();
						//1.模式执行中处于暂停的状态
						 if (scenarinoStatus==7&&!sendtime.equals("0")) {
							 boolean continueModelByError = readyData.continuePredict(scenarinoId, scenarinoType, missionType, missionId, userId);
							if (continueModelByError) {
								return AmpcResult.build(0, "续跑成功！");
							}else {
								return AmpcResult.build(1004, "续跑失败！");
							}
						}
						// 2.出错状态下的续跑
						 if (scenarinoStatus==9&&!sendtime.equals("0")) {
							 boolean continueModelByError = readyData.continuePredict(scenarinoId, scenarinoType, missionType, missionId, userId);
							 if (continueModelByError) {
								 return AmpcResult.build(0, "续跑成功！");
							 }else {
								 return AmpcResult.build(1004, "续跑失败！");
							 }
						 }
						 if(scenarinoStatus==9&&sendtime.equals("0")&&tasksExpand1==0){
							//3.消息一次都没发的续跑（发送到消息队列出错的时候）
							 String branchPredict = readyData.branchPredict(scenarinoId, scenarinoType, missionType, missionId, userId);
							if (branchPredict.equals("")) {
								return AmpcResult.build(0, "续跑成功！");
							}else {
								return AmpcResult.build(1004, "续跑失败！");
							}
						}
						 else {
							return AmpcResult.build(1004, "其他错误");
						}
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
			LogUtil.getLogger().error("GetWeatherModelController continueModel",e.getMessage(),e);
			return AmpcResult.build(1000, "系统错误");
		}
	}
	/**
	 * 
	 * @Description: 减排计算出错的情况下
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月19日 下午8:32:14
	 */
	@Transactional
	@RequestMapping("/Model/errorActionlist")
	public AmpcResult CalActionlistError(@RequestBody Map<String, Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Object param = requestDate.get("scenarioid");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("GetWeatherModelController--CalActionlistError  scenarioid为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarioid为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(param.toString());
			param = requestDate.get("actionlistErrorMSG");
			boolean actionlist;
			try {
				actionlist = (boolean) param;
			} catch (Exception e) {
				LogUtil.getLogger().error("GetWeatherModelController--CalActionlistError  actionlistErrorMSG参数错误!");
				return AmpcResult.build(1003, "actionlistErrorMSG为空或出现非法字符!");
			}
			TTasksStatus tTasksStatus = new TTasksStatus();
			tTasksStatus.setTasksScenarinoId(scenarinoId);
			tTasksStatus.setTasksExpand1(1l);
			int updateEmisData = tTasksStatusMapper.updateEmisData(tTasksStatus);
			if (updateEmisData>0) {
				return AmpcResult.build(0, "success");
			}else {
				throw new SQLException("GetWeatherModelController CalActionlistError 更新情景actionlist计算失败状态失败!");
			}
		} catch (UnsupportedEncodingException | SQLException e) {
			LogUtil.getLogger().error("GetWeatherModelController  CalActionlistError",e.getMessage(),e);
			return AmpcResult.build(1000, "系统错误");
		}
	}
	

	/**
	 * 查询暂停是否成功
	 * @param scenarinoId
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/find_tTask")
	public  AmpcResult selectzt(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long scenarinoId=Long.valueOf(data.get("scenarinoId").toString());
			Integer flag=Integer.valueOf(data.get("flag").toString());	
			//根据情景id查询task
				TTasksStatus tTask=tTasksStatusMapper.selectzt(scenarinoId);
				//判断是否查询到task
				if(tTask==null){
					throw new SQLException("未查询到task信息！");
				} 
				//判断为暂停还是停止
				if(flag==1){
					if(tTask.getPauseStatus()!=null&&!tTask.getPauseStatus().equals("")&&tTask.getPauseStatus().equals("0")){//暂停判断
						return AmpcResult.ok("ok");
					}else{
						return AmpcResult.ok("no");
					}
				}else{
					if(tTask.getStopStatus()!=null&&tTask.getStopStatus().equals("")&&tTask.getStopStatus().equals("0")){//停止
						return AmpcResult.ok("ok");
					}else{
						return AmpcResult.ok("no");
					}					
				}
		}catch(SQLException e){
			LogUtil.getLogger().error("未查询到task信息！",e);
			return AmpcResult.build(1000, "未查询到task信息！");
		}catch(Exception e){
			LogUtil.getLogger().error("查询task信息异常！",e);
			return AmpcResult.build(1001, "查询task信息异常！");
		}
	}
}
