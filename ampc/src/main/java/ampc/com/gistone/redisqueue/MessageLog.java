/**  
 * @Title: MessageLog.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月27日 下午8:32:45
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.support.logging.Log;

import ampc.com.gistone.database.inter.TMessageLogMapper;
import ampc.com.gistone.database.model.TMessageLog;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: MessageLog.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月27日 下午8:32:45
 * @version 1.0
 */
@Component
public class MessageLog {
	//日志映射
	@Autowired
	private TMessageLogMapper tMessageLogMapper;
	@Autowired
	private ConfigUtil configUtil;

	/**
	 * @Description: 保存返回消息的日志
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 下午8:35:11
	 */
	public void saveDomainlog(String rpop) {
		LogUtil.getLogger().info("开始添加消息的log到数据库！");
		try {
			//获取rpop数据
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();//messageId
			Date time = message.getTime();//messageTIME
			String type = message.getType();//messagetype
			Map body = (Map) message.getBody();//messagebody
			Long userid = Long.parseLong(body.get("userid").toString());
			Long domainid = Long.valueOf(body.get("domainid").toString());
			String taskenddate = body.get("date").toString();
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			
			
			tMessageLog.setUserId(userid);
			tMessageLog.setDomainId(domainid);
			tMessageLog.setTasksEndDate(taskenddate);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新domain消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新domain消息日志失败！！");
				throw new SQLException("更新domain消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.getLogger().error("更新domain消息日志失败！！"+e);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 * @Description: ungrib的日志
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 下午9:13:27
	 */
	public void saveUngribMessagelog(String rpop) {
		LogUtil.getLogger().info("开始添加ungrib消息的log到数据库！");
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(message.getId());
			tMessageLog.setMessageTime(message.getTime());
			tMessageLog.setMessageType(message.getType());
			Map body = (Map)message.getBody();
			
			tMessageLog.setUngribPathDate(body.get("pathdate").toString());
			if(body.get("fnl")!=null){
			tMessageLog.setUngribFnl(body.get("fnl").toString());
			}
			if(body.get("gfs")!=null){
			tMessageLog.setUngribGfs(body.get("gfs").toString());
			}
			if(body.get("fnlDesc")!=null){
			tMessageLog.setFnlDesc(body.get("fnlDesc").toString());
			}
			if(body.get("gfsDesc")!=null){
			tMessageLog.setGfsDesc(body.get("gfsDesc").toString());
			}
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新ungrib消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新ungrib消息日志失败！！");
				throw new SQLException("更新消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.getLogger().error("更新消息日志失败！！"+e);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * @Description: 停止模式的日志
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 下午9:13:48
	 */
	public void savestopMessagelog(String rpop) {
		LogUtil.getLogger().info("开始添加model.stop消息的log到数据库！");
		boolean modelResultFlagLog = configUtil.isModelResultFlagLog();
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
//			Long domainId = Long.parseLong(body.get("domainId").toString()); ///kongzhongzheng
//			Long userId =  Long.parseLong(body.get("userId").toString());
			
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			tMessageLog.setScenarinoId(scenarioid);
//			tMessageLog.setUserId(userId);
//			tMessageLog.setDomainId(domainId);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			if (!modelResultFlagLog) {
				tMessageLog.setExpand1("1");
			}
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新model.stop消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新model.stop消息日志失败！！");
				throw new SQLException("更新model.stop消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			LogUtil.getLogger().error(" 更新model.stop消息日志失败！！",e);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	/**
	 * 
	 * @Description: 暂停返回的结果 日志持久化
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月11日 下午9:03:47
	 */
	public void savepauseMessagelog(String rpop) {
		LogUtil.getLogger().info("开始添加model.stop.pause消息的log到数据库！");
		boolean modelResultFlagLog = configUtil.isModelResultFlagLog();
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
//			Long domainId = Long.parseLong(body.get("domainId").toString()); ///kongzhongzheng
//			Long userId =  Long.parseLong(body.get("userId").toString());
			
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			tMessageLog.setScenarinoId(scenarioid);
//			tMessageLog.setUserId(userId);
//			tMessageLog.setDomainId(domainId);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			if (!modelResultFlagLog) {
				tMessageLog.setExpand1("1");
			}
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新model.stop.pause消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新model.stop.pause消息日志失败！！");
				throw new SQLException("更新model.stop.pause消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			LogUtil.getLogger().error(" 更新model.stop.pause消息日志失败！！",e);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 * @Description: 执行模式返回的结果日志
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月11日 下午5:11:20
	 */
	public void savesatrtModelMessagelog(String rpop) {
		LogUtil.getLogger().info("savesatrtModelMessagelog：开始处理模式执行返回消息的log到数据库！");
		boolean modelResultFlagLog = configUtil.isModelResultFlagLog();
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
		/*	if (type.equals("model.start.result")) {
				LogUtil.getLogger().info("savesatrtModelMessagelog：model.start.result 消息");
			}else if (type.equals("model.continue.result")) {
				LogUtil.getLogger().info("savesatrtModelMessagelog：model.continue.result 消息");
			}*/
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
			Integer index = Integer.parseInt(body.get("index").toString());
			String dates =  body.get("date").toString();
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			
			tMessageLog.setMessageIndex(index);
			tMessageLog.setScenarinoId(scenarioid);
			tMessageLog.setTasksEndDate(dates);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			if (!modelResultFlagLog) {
				tMessageLog.setExpand1("1");
			}
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新model.start.result消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新model.start.result消息日志失败！！");
				throw new SQLException("更新model.start.result消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			LogUtil.getLogger().error(" 更新model.start.result消息日志失败！！",e);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	

}
