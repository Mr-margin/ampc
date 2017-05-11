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

	/**
	 * @Description: 保存返回消息的日志
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 下午8:35:11
	 */
	public void saveStartMessagelog(String rpop) {
		LogUtil.getLogger().info("开始添加消息的log到数据库！");
		try {
			//获取rpop数据
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();//messageId
			Date time = message.getTime();//messageTIME
			String type = message.getType();//messagetype
			Map body = (Map) message.getBody();//messagebody
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
			Integer index = Integer.parseInt(body.get("index").toString());
			String taskenddate = body.get("date").toString();
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			tMessageLog.setScenarinoId(scenarioid);
			tMessageLog.setMessageIndex(index);
			tMessageLog.setTasksEndDate(taskenddate);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新消息日志失败！！");
				throw new SQLException("更新消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.getLogger().error("更新消息日志失败！！"+e);
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
			tMessageLog.setUngribFnl(body.get("fnl").toString());
			tMessageLog.setUngribGfs(body.get("gfs").toString());
			tMessageLog.setFnlDesc(body.get("fnlDesc").toString());
			tMessageLog.setGfsDesc(body.get("gfsDesc").toString());
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
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
			Long domainId = Long.parseLong(body.get("domainId").toString());
			Long userId =  Long.parseLong(body.get("userId").toString());
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			
			tMessageLog.setUserId(userId);
			tMessageLog.setDomainId(domainId);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新model.stop消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新model.stop消息日志失败！！");
				throw new SQLException("更新model.stop消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			LogUtil.getLogger().error(" 更新model.stop消息日志失败！！",e);
		}
		
	}

	/**
	 * @Description: TODO
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月11日 下午5:11:20
	 */
	public void savepauseMessagelog(String rpop) {
		LogUtil.getLogger().info("开始添加model.stop.pause消息的log到数据库！");
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
			Long domainId = Long.parseLong(body.get("domainId").toString());
			Long userId =  Long.parseLong(body.get("userId").toString());
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setMessageUuid(id);
			tMessageLog.setMessageTime(time);
			tMessageLog.setMessageType(type);
			
			tMessageLog.setUserId(userId);
			tMessageLog.setDomainId(domainId);
			tMessageLog.setResultDesc(desc);
			tMessageLog.setResultCode(code);
			int insertSelective = tMessageLogMapper.insertSelective(tMessageLog);
			if (insertSelective>0) {
				LogUtil.getLogger().info("更新model.stop.pause消息日志成功！");
			}else {
				LogUtil.getLogger().error("更新model.stop.pause消息日志失败！！");
				throw new SQLException("更新model.stop.pause消息日志失败！！");
			}
		} catch (IOException | SQLException e) {
			LogUtil.getLogger().error(" 更新model.stop.pause消息日志失败！！",e);
		}
		
	}
	

}
