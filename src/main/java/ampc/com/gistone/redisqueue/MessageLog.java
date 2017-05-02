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
		/*try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			String id = message.getId();
			Date time = message.getTime();
			String type = message.getType();
			Map body = (Map) message.getBody();
			Long scenarioid = Long.parseLong(body.get("scenarioid").toString());
			Integer index = Integer.parseInt(body.get("index").toString());
			String taskenddate = body.get("date").toString();
			String desc = body.get("desc").toString();
			String code = body.get("code").toString();
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setUuid(id);
			tMessageLog.setTime(time);
			tMessageLog.setType(type);
			tMessageLog.setScenarinoId(scenarioid);
			tMessageLog.setIndex(index);
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
			e.printStackTrace();
			LogUtil.getLogger().error("message类型转换异常！"+e);
		}
		*/
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
		/*LogUtil.getLogger().info("开始添加ungrib消息的log到数据库！");
		try {
			Message message  = JsonUtil.jsonToObj(rpop, Message.class);
			TMessageLog tMessageLog = new TMessageLog();
			tMessageLog.setUuid(message.getId());
			tMessageLog.setTime(message.getTime());
			tMessageLog.setType(message.getType());
			Map body = (Map)message.getBody();
			tMessageLog.setPathDate(body.get("pathdate").toString());
			tMessageLog.setFnl(body.get("fnl").toString());
			tMessageLog.setGfs(body.get("gfs").toString());
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
			e.printStackTrace();
		}
		*/
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
		// TODO Auto-generated method stub
		
	}
	

}
