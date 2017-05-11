/**  
 * @Title: StopModel.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月20日 下午4:26:45
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.redisqueue.entity.StopModelBean;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: StopModel.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月20日 下午4:26:45
 * @version 1.0
 */
@Component
public class StopModelData {
	//加载情景详情映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	//引入发送消息的工具类
	@Autowired
	private SendQueueData sendQueueData;
	
	
	
	

	/**
	 * @Description: 
	 * @param scenarinoId   
	 * void  
	 * @param userId 
	 * @param missionId 
	 * @param domainId 
	 * @throws
	 * @author yanglei
	 * @date 2017年4月20日 下午4:30:33
	 */
	public boolean StopModel(Long scenarinoId, Long domainId, Long missionId, Long userId) {
		QueueData queueData = getHeadParameter("model.stop");
		StopModelBean stopModelBean = new StopModelBean();
		stopModelBean.setDomainid(domainId.toString());
		stopModelBean.setMissionid(missionId.toString());
		stopModelBean.setUserid(userId.toString());
		stopModelBean.setScenarioid(scenarinoId.toString());
		queueData.setBody(stopModelBean);
		LogUtil.getLogger().info("开始发送终止模式的消息，该情景id是："+scenarinoId);
		boolean stoptoJson = sendQueueData.stoptoJson(queueData,scenarinoId);
		return stoptoJson;
	}
	
	/**
	 * 
	 * @Description: 设置消息队列的头部参数
	 * @param type
	 * @return   
	 * QueueData  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月20日 下午5:12:38
	 */
	private QueueData getHeadParameter(String type) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//消息时间为当前的系统时间（北京时间 ）
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		queueData.setId(UUID.randomUUID().toString());//设置消息id
		queueData.setTime(time);//设置消息时间
		queueData.setType(type);
		return queueData;
	}

	/**
	 * @Description: 暂停
	 * @param scenarinoId
	 * @param domainId
	 * @param missionId
	 * @param userId
	 * @return   
	 * boolean  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月28日 上午10:05:10
	 */
	public boolean pauseModel(Long scenarinoId, Long domainId, Long missionId,
			Long userId) {
		QueueData queueData = getHeadParameter("model.pause");
		StopModelBean stopModelBean = new StopModelBean();
		stopModelBean.setDomainid(domainId.toString());
		stopModelBean.setMissionid(missionId.toString());
		stopModelBean.setUserid(userId.toString());
		stopModelBean.setScenarioid(scenarinoId.toString());
		queueData.setBody(stopModelBean);
		LogUtil.getLogger().info("开始发送暂停模式的消息，该情景id是："+scenarinoId);
		boolean pausetoJson = sendQueueData.pausetoJson(queueData,scenarinoId);
		return pausetoJson;
	}

}
