/**  
 * @Title: ModelExecuteStutas.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月28日 上午9:46:23
 * @version 
 */
package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.net.aso.i;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.tools.classfile.StackMapTable_attribute.verification_type_info;

import ampc.com.gistone.database.inter.TMessageLogMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TModelScheduleMessageMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TMessageLog;
import ampc.com.gistone.database.model.TModelScheduleMessage;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.redisqueue.entity.ModelExecuJson;
import ampc.com.gistone.redisqueue.tasksenum.TasksEnum1;
import ampc.com.gistone.redisqueue.tasksenum.TasksEnum2;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**  
 * @Title: ModelExecuteStutas.java
 * @Package ampc.com.gistone.controller
 * @Description: 模式执行的状态的controller  返回给前端的tasks任务运行状态的接口
 * @author yanglei
 * @date 2017年3月28日 上午9:46:23
 * @version 1.0
 * @param <E>
 */

@RestController
@RequestMapping
public class ModelExecuteStatusController<E> {
	
	@Autowired
	private TTasksStatusMapper tasksStatusMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	@Autowired
	private TMessageLogMapper tMessageLogMapper;
	@Autowired
	private TModelScheduleMessageMapper tModelScheduleMessageMapper;
	@Autowired
	private ConfigUtil configUtil;
	/**
	 * 
	 * @Description: TODO
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午9:49:03
	 */
	@RequestMapping("/ModelExecuteStatus")
	public AmpcResult getModelexecuteStatus(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		
		String token = (String) requestDate.get("token");
		//获取数据包
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Object param = data.get("userId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus userId为空或出现非法字符!");
				return AmpcResult.build(1003, "userId为空或出现非法字符!");
			}
			Long userId = Long.parseLong(data.get("userId").toString());
			
			//任务类型
			param = data.get("missionType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus missionType为空或出现非法字符!");
				return AmpcResult.build(1003, "missionType为空或出现非法字符!");
			}
			Integer missionType = Integer.parseInt(data.get("missionType").toString());
			
			//情景ID
			param = data.get("scenarinoId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus scenarinoId为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoId为空或出现非法字符!");
			}
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			
			//任务ID
			param = data.get("missionId");
			if (!RegUtil.CheckParameter(param, "Long", null, false)) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus missionId为空或出现非法字符!");
				return AmpcResult.build(1003, "missionId为空或出现非法字符!");
			}
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//情景类型
			param = data.get("scenarinoType");
			if (!RegUtil.CheckParameter(param, "Integer", null, false)) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus scenarinoType为空或出现非法字符!");
				return AmpcResult.build(1003, "scenarinoType为空或出现非法字符!");
			}
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//查找情景的详细信息
			TScenarinoDetail selectByPrimaryKey =null;
			try {
				selectByPrimaryKey = tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			} catch (Exception e) {
				LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus:查询情景信息出现异常,selectByPrimaryKey scenarinoId:"+scenarinoId,e.getMessage());
				return AmpcResult.build(1000, "查询情景信息出现异常！");
			}
			if (selectByPrimaryKey!=null) {
				TTasksStatus selectStatus = null;
				try {
					selectStatus = tasksStatusMapper.selectStatus(scenarinoId);
				} catch (Exception e) {
					LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus:查询该情景的情景状态表异常！selectStatus scenarinoId："+scenarinoId,e.getMessage());
					return AmpcResult.build(1000, "查询情景执行信息出现异常！");
				}
				if (selectStatus!=null) {
					List<TMessageLog> tMessageLoglist = null;
					List<TModelScheduleMessage> tModelScheduleMessageslist = null;
					try {
						tMessageLoglist = tMessageLogMapper.selectListByscenarinoId(scenarinoId);
						tModelScheduleMessageslist = tModelScheduleMessageMapper.selectListByscenarinoId(scenarinoId);
					} catch (Exception e) {
						LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus:查询该情景的情景状态表异常！selectListByscenarinoId scenarinoId："+scenarinoId,e.getMessage());
						return AmpcResult.build(1000, "查询情景执行信息出现异常！");
					}
					ModelExecuJson modelExecuJson = new ModelExecuJson();
					//情景开始时间
					modelExecuJson.setStartTime(selectByPrimaryKey.getScenarinoStartDate());
					//情景结束时间
					modelExecuJson.setEndTime(selectByPrimaryKey.getScenarinoEndDate());
					//模式运行截止位置的时间
					Date tasksEndDate = selectStatus.getTasksEndDate();
//					tasksEndDate = tasksEndDate ==null?selectByPrimaryKey.getScenarinoStartDate():tasksEndDate;
					modelExecuJson.setStopTime(tasksEndDate);
					//模式运行截止到目前的状态对应的tasks的名称
					String stopData = getnowTasksName(scenarinoType,selectStatus);
					modelExecuJson.setStopData(stopData);
					//情景类型对应的tasks 数组
					String [] moduleType = getmoduleType(scenarinoType);
					modelExecuJson.setModuleType(moduleType);
					//模式运行截止到目前的状态
					Long scenarinoStatus = selectByPrimaryKey.getScenarinoStatus();
					int stopType = Integer.parseInt(scenarinoStatus.toString());
					modelExecuJson.setStopType(stopType);
					//任务、情景的类型
					modelExecuJson.setSceneType(scenarinoType.toString());
					modelExecuJson.setMissionType(missionType.toString());
					//执行的方式--逐日还是逐模块
					Integer execModel = getexecModel(scenarinoType);
					modelExecuJson.setExecModel(execModel);
					//模式执行的详细内容数组
//					Object[] excutionMessageDetail = getexcutionMessagedetail(tMessageLoglist,selectStatus,scenarinoType);
					Object[] excutionMessageDetail2 = getexecutionMessagedetailData(tModelScheduleMessageslist,selectStatus,scenarinoType,execModel);
					modelExecuJson.setExcutionMessage(excutionMessageDetail2);
					
					//模式运行出错的消息描述信息
					String modelErrorStatus = selectStatus.getModelErrorStatus();
					modelExecuJson.setStopMessage(modelErrorStatus);
					//模式运行的tips信息
					String tips = getTipsMessage(selectStatus,scenarinoStatus);
					modelExecuJson.setModelExecTips(tips);
//					JSONArray json = JSONArray.fromObject(jpList);
//					JSONArray fromObject = JSONArray.fromObject(modelExecuJson);
//					String json = fromObject.toString();
//					System.out.println(json);
//					return AmpcResult.ok(json);
					return AmpcResult.build(0, "successs",modelExecuJson);
				}else {
					return AmpcResult.build(1005, "未查到运行状态信息！请联系管理员！");
				}
			}else {
				return AmpcResult.build(1005, "未查到情景信息！请联系管理员！");
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.getLogger().error("ModelExecuteStatusController getModelexecuteStatus",e);
			return AmpcResult.build(1001, "系统异常！");
		}
	}

	/**
	 * @Description: 获取模式执行的详细信息
	 * @param tModelScheduleMessageslist
	 * @param selectStatus
	 * @param scenarinoType
	 * @param execModel 
	 * @return   
	 * Object []  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月15日 下午2:36:23
	 */
	private Object[] getexecutionMessagedetailData(
			List<TModelScheduleMessage> tModelScheduleMessageslist,
			TTasksStatus selectStatus, Integer scenarinoType, Integer execModel) {
		Map map = new LinkedHashMap();
		String pattern="yyyyMMdd";
		Date tasksScenarinoStartDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoStartDate(), pattern);
		Date tasksScenarinoEndDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoEndDate(), pattern);
		Object[] outerArray = null;
		String[] execDetailMsg = null;
		Long tasksRangeDay = selectStatus.getTasksRangeDay();
		int daylength = Integer.parseInt(tasksRangeDay.toString());
		int tasksLength = getmodeltasksLength(scenarinoType);
		//临时时间  用于组织时间对
		String  tempdate = null;
		//临时变量index 
		Integer tempIndex = null;
		//临时变量 用于计算该条消息是否属于该次数组
		int i = 0,j = 0,k = 0;
		if (1==execModel) {
			//逐日执行的数据
			//外部数组-长度是情景的运行时间段
			outerArray = new Object[daylength];
			//内部数组--长度是tasks的长度
			execDetailMsg = new String[tasksLength];
			for (TModelScheduleMessage tModelScheduleMessage : tModelScheduleMessageslist) {
				String tasksEndDate = tModelScheduleMessage.getExeMessageTasksdate();
				String messageType = tModelScheduleMessage.getExeMessageType();
				String resultDesc = tModelScheduleMessage.getExeMessageDesc();
				int resultCode = tModelScheduleMessage.getExeMessageCode();
				Integer messageIndex = tModelScheduleMessage.getExeMessageIndex();
				if (map.get(tasksEndDate)!=null) {
					map.put(tasksEndDate, i);
					if (j==i) {
						Date messageTime = tModelScheduleMessage.getExeMessageTime();
						String indextime = DateUtil.DATEtoString(messageTime, "yyyy-MM-dd HH:mm:ss");
						if (0==k) {
							String str = (tempdate+"<br/>"+ indextime).toString();
							execDetailMsg[k] = str;
							tempdate = indextime;
						}else {
							String str = (tempdate+"<br/>"+ indextime).toString();
							execDetailMsg[k] = str;
							tempdate = indextime;
						}
						k++;
					}
				}else {
					map.put(tasksEndDate, i);
					i++;
					Date messageTime = tModelScheduleMessage.getExeMessageTime();
					String indextime = DateUtil.DATEtoString(messageTime, "yyyy-MM-dd HH:mm:ss");
					if (0==messageIndex) {
						tempdate = indextime;
						tempIndex = messageIndex;
					}else {
						//index =0 的消息丢失
						tempdate = "未获取到时间！";
						tempIndex = messageIndex;
					}
					k=0;
					if (j>=1) {
						outerArray[i-2] = execDetailMsg;
						//清空数组
						execDetailMsg = new String[tasksLength];
					}
					j=i;
				}
				if (i>=1) {
					outerArray[i-1] = execDetailMsg;
				}
			}	
		}else if(2==execModel){
			//逐模块执行的数据
			//外部数组
			outerArray = new Object[tasksLength];
			//内部数组
			execDetailMsg = new String[daylength];
			for (TModelScheduleMessage tModelScheduleMessage : tModelScheduleMessageslist) {
				String tasksEndDate = tModelScheduleMessage.getExeMessageTasksdate();
				String messageType = tModelScheduleMessage.getExeMessageType();
				String resultDesc = tModelScheduleMessage.getExeMessageDesc();
				int resultCode = tModelScheduleMessage.getExeMessageCode();
				Integer messageIndex = tModelScheduleMessage.getExeMessageIndex();
				Date messageTime = tModelScheduleMessage.getExeMessageTime();
				String indextime = DateUtil.DATEtoString(messageTime, "yyyy-MM-dd HH:mm:ss");
				if (0==messageIndex) {
					tempdate = indextime;
					tempIndex = messageIndex;
				}else {
					if (map.get(messageIndex)!=null) {
						k++;
						String str = (tempdate+"<br/>"+ indextime).toString();
						execDetailMsg[k] = str;
						tempdate = indextime;
					}else {
						map.put(messageIndex, tasksEndDate);
						i++;
						k=0;
						String str = (tempdate+"<br/>"+ indextime).toString();
						execDetailMsg[k] = str;
						tempdate = indextime;
						if (j>=1) {
							outerArray[i-2] = execDetailMsg;
							//清空数组
							execDetailMsg = new String[daylength];
						}
						j=i;
					}
				}
				if (j>1) {
					//给最后一个赋值
					outerArray[i-1] = execDetailMsg;
				}
			}
		}
		
	
		return outerArray;
	}

	/**
	 * @Description: 获取模式运行提示消息
	 * @param selectStatus
	 * @param scenarinoStatus 
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月6日 上午10:19:27
	 */
	private String getTipsMessage(TTasksStatus selectStatus, Long scenarinoStatus) {
		Date tasksEndTime = selectStatus.getTasksEndTime();
		Date tasksScenarinoEndDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoEndDate(), "yyyyMMdd"); 
		String tips = null;
		Integer statusInteger = Integer.parseInt(scenarinoStatus.toString());
		switch (statusInteger) {
		case 8:
			//模式执行完毕的情况  
			if (null!=tasksEndTime) {
				Date EndTime = DateUtil.DateToDate(tasksEndTime, "yyyyMMdd");
				int compareTo = EndTime.compareTo(tasksScenarinoEndDate);
				if (compareTo<0) {
					//模式未执行到最后一天
					String taskstime = DateUtil.DATEtoString(tasksEndTime, "yyyyMMdd");
					tips = "模式执行超时，模式执行到："+taskstime+"为止！";
				}
				if (compareTo==0) {
					//模式执行完毕
					tips = "模式执行完毕！";
				}
			}
			break;
		case 9:
			tips = "模式执行出错！";
			break;
		case 7:
			tips = "模式暂停！";
			break;
		case 6:
			tips = "模式执行中！";
			break;
		default:
			break;
		}
		return tips;
	}
/*
	*//**
	 * @Description: 获取详细信息
	 * @param tMessageLoglist
	 * @param selectStatus
	 * @param scenarinoStatus 
	 * @param scenarinoType 
	 * @return   
	 * String []  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 下午2:36:09
	 *//*
	private Object[] getexcutionMessagedetail(
			List<TMessageLog> tMessageLoglist, TTasksStatus selectStatus, Integer scenarinoType) {
		Map map = new LinkedHashMap();
		String pattern="yyyyMMdd";
		Date tasksScenarinoStartDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoStartDate(), pattern);
		Date tasksScenarinoEndDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoEndDate(), pattern);
		//初始化本次tMessageLoglist的方法
		List< TMessageLog> newtMessageLoglist = getnewMessageLogList(tMessageLoglist,selectStatus); 
		
		//外部数组-长度是情景的运行时间段
		Long tasksRangeDay = selectStatus.getTasksRangeDay();
		int outlength = Integer.parseInt(tasksRangeDay.toString());
		Object[] outerArray = new Object[outlength];
		//内部数组--长度是tasks的长度
		int tasksLength = getmodeltasksLength(scenarinoType);
		String[] execDetailMsg = new String[tasksLength];
		//临时时间  用于组织时间对
		String  tempdate = null;
		//临时变量 用于计算该条消息是否属于该次数组
		int i = 0,j = 0,k = 0;
		for (TMessageLog tMessageLog : newtMessageLoglist) {
			String tasksEndDate = tMessageLog.getTasksEndDate();
			String messageType = tMessageLog.getMessageType();
			String resultDesc = tMessageLog.getResultDesc();
			String resultCode = tMessageLog.getResultCode();
			Integer messageIndex = tMessageLog.getMessageIndex();
			if (map.get(tasksEndDate)!=null) {
				map.put(tasksEndDate, i);
				if (j==i) {
					Date messageTime = tMessageLog.getMessageTime();
					String indextime = DateUtil.DATEtoString(messageTime, "yyyy-MM-dd HH:mm:ss");
					if (0==k) {
						String str = (tempdate+"<br/>"+ indextime).toString();
						execDetailMsg[k] = str;
						tempdate = indextime;
					}else {
						String str = (tempdate+"<br/>"+ indextime).toString();
						execDetailMsg[k] = str;
						tempdate = indextime;
					}
					k++;
				}
			}else {
				map.put(tasksEndDate, i);
				i++;
				Date messageTime = tMessageLog.getMessageTime();
				String indextime = DateUtil.DATEtoString(messageTime, "yyyy-MM-dd HH:mm:ss");
				if (0==messageIndex) {
					tempdate = indextime;
				}
				k=0;
				if (j>=1) {
					outerArray[i-2] = execDetailMsg;
					//清空数组
					execDetailMsg = new String[tasksLength];
				}
				j=i;
			}
			if (i>=1) {
				outerArray[i-1] = execDetailMsg;
			}
		}
	return outerArray;
	}

	*//**
	 * @Description:初始化日志消息
	 * @param tMessageLoglist
	 * @param selectStatus 
	 * @return   
	 * List<TMessageLog>  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月7日 下午3:00:31
	 *//*
	private List<TMessageLog> getnewMessageLogList(
			List<TMessageLog> tMessageLoglist, TTasksStatus selectStatus) {
		List<TMessageLog> arrayList = new ArrayList<TMessageLog>();
		Map<String,TMessageLog> hashMap = new LinkedHashMap<String, TMessageLog>();
		String pattern="yyyyMMdd";
		Date tasksScenarinoStartDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoStartDate(), pattern);
		Date tasksScenarinoEndDate = DateUtil.DateToDate(selectStatus.getTasksScenarinoEndDate(), pattern);
		for (TMessageLog tMessageLog : tMessageLoglist) {
			String tasksEndDate = tMessageLog.getTasksEndDate();
			String messageType = tMessageLog.getMessageType();
			String resultDesc = tMessageLog.getResultDesc();
			String resultCode = tMessageLog.getResultCode();
			Integer messageIndex = tMessageLog.getMessageIndex();
			if ("model.start.result".equals(messageType.trim())) {
				if (resultDesc==null&&"0".equals(resultCode.trim())) {
					if (tasksEndDate!=null) {
						Date taskDate = null ;
						Integer compareTo = null ;
						Integer compareTo2 = null;
						try {
							taskDate = DateUtil.StrtoDateYMD(tasksEndDate, pattern);
							//与情景开始时间比较
							compareTo = taskDate.compareTo(tasksScenarinoStartDate);
							//与情景结束时间比较
							compareTo2 = taskDate.compareTo(tasksScenarinoEndDate);
						} catch (Exception e) {
							LogUtil.getLogger().error("ModelExecuteStatusController getnewMessageLogList tasksendDate转为时间出错！",e.getMessage());
						}
						String key = tasksEndDate+"-"+messageIndex;
						if (compareTo>=0&&compareTo2<=0) {
							if (hashMap.get(key)!=null) {
								hashMap.put(key, tMessageLog);
							}else {
								hashMap.put(key, tMessageLog);
							}
						}else {
							LogUtil.getLogger().info("ModelExecuteStatusController getnewMessageLogList 消息时间不在情景开始结束时间范围之内！");
						}
					}
				}
			}
		}
		for (TMessageLog tMessageLog : hashMap.values()) {
			arrayList.add(tMessageLog);
		}
		return arrayList;
	}
*/
	/**
	 * @Description:获取task的长度
	 * @param scenarinoType
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 下午9:15:58
	 */
	private int getmodeltasksLength(Integer scenarinoType) {
		Integer index = null ;
		switch (scenarinoType) {
		case 1:
		case 2:
			index = 4;
			break;
		case 3:
		case 4:
			index = 8;
			break;
		default:
			break;
		}
		return index;
	}

	/**
	 * @Description: 设置是逐日执行还是逐模块执行
	 * @param scenarinoType
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 下午2:17:43
	 */
	private int getexecModel(Integer scenarinoType) {
		Integer execModel = null;
		switch (scenarinoType) {
		case 1:
		case 4:
			//逐日执行
			execModel = 1;
			break;
		case 2:
		case 3:
			//逐模块执行
			execModel = 2;
			break;
		default:
			break;
		}
		return execModel;
	}

	/**
	 * @Description: 获取模块的数组
	 * @param scenarinoType
	 * @return   
	 * String []  
	 * @throws UnsupportedEncodingException 
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 下午1:55:04
	 */
	private String[] getmoduleType(Integer scenarinoType)  {
		String[] split = null;
		switch (scenarinoType) {
		case 1:
		case 2:	
			String tasksArray2="人为源排放,CMAQ,排放后处理,化学后处理";
//			String tasksArray2 = configUtil.getTasksArray2();
			split = tasksArray2.split(",");
			break;
		case 3:
		case 4:	
			String tasksArray1="气象模拟,气象格式转换,气象后处理,天然源排放,人为源排放,CMAQ,排放后处理,化学后处理";
//			String tasksArray1 = configUtil.getTasksArray1();
			split = tasksArray1.split(",");
			break;
		default:
			break;
		}
		return split;
	}

	/**
	 * @param scenarinoType 
	 * @Description: 获取index的name
	 * @param selectStatus
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 下午1:00:04
	 */
	private String getnowTasksName(Integer scenarinoType, TTasksStatus selectStatus) {
		Long stepindex = selectStatus.getStepindex();
		int index = Integer.parseInt(stepindex.toString());
		String taskasname = null;
		switch (scenarinoType) {
		case 1:
		case 2:
			taskasname = TasksEnum2.getname2(index);
			break;
		case 3:
		case 4:
			taskasname = TasksEnum1.getname1(index);
			break;
		default:
			break;
		}
		return taskasname;
	}

}
