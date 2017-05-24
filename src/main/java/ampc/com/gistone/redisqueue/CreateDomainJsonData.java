/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月22日 上午11:09:52
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.redisqueue.entity.DomainBodyData;
import ampc.com.gistone.redisqueue.entity.DomainDataCmaq;
import ampc.com.gistone.redisqueue.entity.DomainDataCommon;
import ampc.com.gistone.redisqueue.entity.DomainDataMcip;
import ampc.com.gistone.redisqueue.entity.DomainDataMeic;
import ampc.com.gistone.redisqueue.entity.DomainDataWrf;
import ampc.com.gistone.redisqueue.entity.DomainParams;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月22日 上午11:09:52
 * @version 1.0
 */
@Component
public class CreateDomainJsonData {
	@Autowired
	private ReadyData readyData;
	@Autowired
	private SendQueueData sendQueueData;
	
	
	/**
	 * 
	 * @Description: 准备domain参数
	 * @param userId
	 * @param domainId   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:33:46
	 */
	public void readyDomainData(Long userId,Long domainId){
		//消息头部的参数
		QueueData queueData = getHeadParameter("domain.create");
		//消息体参数
		DomainBodyData domainbodydata = new DomainBodyData();
		domainbodydata.setUserid(userId.toString());
		domainbodydata.setDomainid(domainId.toString());
		//domain数据参数
		DomainParams domainParams = getdomainParams(userId,domainId);
		domainbodydata.setDomain(domainParams);
		queueData.setBody(domainbodydata);
		boolean senddoamindata = sendQueueData.sendDomainDatajson(queueData);
		if (senddoamindata) {
			LogUtil.getLogger().info("");
		}else {
			LogUtil.getLogger().info("");
		}
	}
	
	
	
	
	/**
	 * @Description: domain参数的具体内容（common，cmaq，wrf，mcip，meic）
	 * @param userId
	 * @param domainId
	 * @return   
	 * DomainParams  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:32:28
	 */
	private DomainParams getdomainParams(Long userId, Long domainId) {
		DomainParams domainParams = new DomainParams();
		//domain的common参数
		DomainDataCommon domainDatacommon = getDomainDataCommon();
		//domain的cmaq参数
		DomainDataCmaq domainDataCmaq = getDoaminDataCmaq();
		//domain的wrf参数
		DomainDataWrf domainDataWrf = getDomainDataWrf();
		//domain的meip参数
		DomainDataMcip domainDataMcip = getDomianDataMcip();
		//domain的meic参数
		DomainDataMeic domainDataMeic = getDomainDataMeic();
		
		domainParams.setCommon(domainDatacommon);
		domainParams.setCmaq(domainDataCmaq);
		domainParams.setWrf(domainDataWrf);
		domainParams.setMcip(domainDataMcip);
		domainParams.setMeic(domainDataMeic);
		
		return domainParams;
	}




	/**
	 * @Description: 获取DomainDataMeic 参数
	 * @return   
	 * DomainDataMeic  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:15:26
	 */
	private DomainDataMeic getDomainDataMeic() {
		return null;
	}




	/**
	 * @Description: 获取DomianDataMcip参数
	 * @return   
	 * DomainDataMcip  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:13:55
	 */
	private DomainDataMcip getDomianDataMcip() {
		return null;
	}




	/**
	 * @Description: 获取DomainDataWrf参数
	 * @return   
	 * DomainDataWrf  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:12:39
	 */
	private DomainDataWrf getDomainDataWrf() {
		return null;
	}




	/**
	 * @Description: 获取DoaminDataCmaq参数
	 * @return   
	 * DomainDataCmaq  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:56:52
	 */
	private DomainDataCmaq getDoaminDataCmaq() {
		return null;
	}




	/**
	 * @Description: 获取domain参数的common的参数
	 * @return   
	 * DomainDataCommon  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:53:52
	 */
	private DomainDataCommon getDomainDataCommon() {
		// TODO Auto-generated method stub
		return null;
	}




	/**
	 * 
	 * @Description: 设置队列头部的消息内容
	 * @param type
	 * @return   
	 * QueueData  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 上午11:46:19
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

}
