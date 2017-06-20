/**  
 * @Title: AcceptMessage.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:06:49
 * @version 
 */
package ampc.com.gistone.redisqueue;



import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: AcceptMessage.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:06:49
 * @version 1.0
 */
@Component
public class AcceptMessageQueue implements Runnable{
	
	
	//加载redis工具类
	@Autowired
	private RedisUtilServer redisUtilServer;
	//加载ungrib映射 
	@Autowired
	private TUngribMapper tUngribMapper;
	//加载持久ungrib数据的工具类
	@Autowired
	private ToDataUngribUtil toDataUngribUtil;
	//加载持久tasksStatus数据的工具类
	@Autowired
	private ToDataTasksUtil toDataTasksUtil;
	@Autowired
	private CreateDomainJsonData  createDomainJsonData;
	@Autowired
	private MessageLog messageLog;
	
	/*@Autowired
	private RedisConfig redisConfig;*/
	@Autowired
	private ConfigUtil configUtil;

	/* (非 Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/ 
	
	public void run() {
		LogUtil.getLogger().info("线程开始运行：redis-ip:"+configUtil.getRedisHost()+",redis-port:"+configUtil.getRedisPort());
		boolean runningSetting = configUtil.isRunningSetting();
		LogUtil.getLogger().info("AcceptMessageQueue :runningSetting:"+runningSetting);
		if (runningSetting) {
			while (true) {
				try {
					LogUtil.getLogger().info("AcceptMessageQueue:队列接受数据");
					String acceptName = configUtil.getRedisQueueAcceptName();
					String rpop = redisUtilServer.brpop(acceptName);//send_queue_name
					LogUtil.getLogger().info(rpop+"刚取出来的");
					if (null==rpop) {
						LogUtil.getLogger().info("队列里面没有数据了！");
					}else {
						Message message = JsonUtil.jsonToObj(rpop, Message.class);
						String key = message.getType();
						switch (key) {
						case "model.start.result":
							LogUtil.getLogger().info("start tasks-时间："+new Date()+"，内容:"+rpop);
							messageLog.savesatrtModelMessagelog(rpop);
//							toDataTasksUtil.cheakModelResult(rpop);
							toDataTasksUtil.updateDB(rpop);
							LogUtil.getLogger().info("end tasks"+new Date());
							break;
						case "model.continue.result":
							LogUtil.getLogger().info("model.continue.result tasks-时间："+new Date()+"，内容:"+rpop);
							messageLog.savesatrtModelMessagelog(rpop);
//							toDataTasksUtil.cheakModelResult(rpop);
							toDataTasksUtil.updateDB(rpop);
							LogUtil.getLogger().info("model.continue.result end tasks，"+new Date());
							break;
						case "ungrib.result":
							LogUtil.getLogger().info("接受ungrib数据："+new Date()+"，内容:"+rpop);
							messageLog.saveUngribMessagelog(rpop);
							toDataUngribUtil.updateUngrib(rpop);
							LogUtil.getLogger().info("ungrib处理完毕："+new Date());
							break;
						case "model.stop.result":
							LogUtil.getLogger().info("停止模式处理开始："+new Date()+"，内容:"+rpop);
							messageLog.savestopMessagelog(rpop);
							toDataTasksUtil.stopModelresult(rpop);
							LogUtil.getLogger().info("停止模式处理完毕："+new Date());
							break;
						case "model.pause.result":
							LogUtil.getLogger().info("暂停模式处理开始："+new Date()+"，内容:"+rpop);
							messageLog.savepauseMessagelog(rpop);
							toDataTasksUtil.pauseModelresult(rpop);
							LogUtil.getLogger().info("暂停模式处理完毕："+new Date());
							break;
						case "domain.create.result":
							LogUtil.getLogger().info("domain-result处理开始："+new Date()+"，内容:"+rpop);
							messageLog.saveDomainlog(rpop);
							createDomainJsonData.updateDomainResult(rpop);
							LogUtil.getLogger().info("创建domain处理完毕："+new Date());
							break;
						default:
							break;
						}
					}
					
				} catch (Exception e) {                                                                             
					LogUtil.getLogger().error("线程出现异常了,redis-ip:"+configUtil.getRedisHost()+",redis-port:"+configUtil.getRedisPort(),e.getMessage(),e);
				}
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			LogUtil.getLogger().info("线程空执行！");
		}
	}
}
