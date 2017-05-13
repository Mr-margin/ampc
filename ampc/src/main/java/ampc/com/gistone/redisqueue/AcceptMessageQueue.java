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
		
	
	/*
		while (!Thread.interrupted()) {
			
			try {
		//	String send_queue_name = redisUtilServer.rpop("send_queue_name");//result_Start_model
			System.out.println("队列接受数据");
		//	String rpop = redisUtilServer.brpop("send_queue_name");//result_Start_model
			String rpop2 = redisUtilServer.rpop("mb");//result_Start_model
			if (null==rpop2) {
				System.out.println(rpop2+"刚取出来的");
				LogUtil.getLogger().info("队列里面没有数据了！");
			}else {
			
				Message message = JsonUtil.jsonToObj(rpop2, Message.class);
				
				String key = message.getType();
				switch (key) {
				case "model.start.result":
					LogUtil.getLogger().info("start tasks"+new Date());
					LogUtil.getLogger().info(rpop2);
					toDataTasksUtil.updateDB(message);
					LogUtil.getLogger().info("end tasks"+new Date());
					break;
				case "ungrib.result":
					LogUtil.getLogger().info("接受ungrib数据："+new Date()+":"+rpop2);
					toDataUngribUtil.updateDB(rpop2);
					LogUtil.getLogger().info("ungrib处理完毕："+new Date());
					break;

				default:
					break;
				}
			}
				
			} catch (IOException e) {                                                                             
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtil.getLogger().error("线程出现异常了");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		LogUtil.getLogger().info("线程开始运行：redis-ip:"+configUtil.getRedisHost()+",redis-port:"+configUtil.getRedisPort());
	while (true) {
			try {
				System.out.println("队列接受数据");
				String acceptName = configUtil.getRedisQueueAcceptName();
//				String rpop = redisUtilServer.brpop("r0_mb");//send_queue_name
				String rpop = redisUtilServer.brpop(acceptName);//send_queue_name
//				String rpop = redisUtilServer.brpop("r0_test_mb");//send_queue_name
//				String rpop = redisUtilServer.brpop("receive_queue_name");//send_queue_name
//				String rpop = redisUtilServer.brpop("r0_mb");//r0_mb
				//	String rpop2 = redisUtilServer.rpop("test");//result_Start_model
				if (null==rpop) {
					System.out.println(rpop+"刚取出来的");
					LogUtil.getLogger().info("队列里面没有数据了！");
				}else {
					Message message = JsonUtil.jsonToObj(rpop, Message.class);
					String key = message.getType();
					switch (key) {
					case "model.start.result":
						LogUtil.getLogger().info("start tasks"+new Date()+":"+rpop);
						messageLog.savesatrtModelMessagelog(rpop);
						toDataTasksUtil.updateDB(rpop);
						LogUtil.getLogger().info("end tasks"+new Date());
						break;
					case "ungrib.result":
						LogUtil.getLogger().info("接受ungrib数据："+new Date()+":"+rpop);
						messageLog.saveUngribMessagelog(rpop);
//						toDataUngribUtil.updateDB(rpop);
						toDataUngribUtil.updateUngrib(rpop);
						LogUtil.getLogger().info("ungrib处理完毕："+new Date());
						break;
					case "model.stop.result":
						LogUtil.getLogger().info("停止模式处理开始："+new Date()+":"+rpop);
						messageLog.savestopMessagelog(rpop);
						toDataTasksUtil.stopModelresult(rpop);
						LogUtil.getLogger().info("停止模式处理完毕："+new Date());
						break;
					case "model.stop.pause":
						LogUtil.getLogger().info("暂停模式处理开始："+new Date()+":"+rpop);
						messageLog.savepauseMessagelog(rpop);
						toDataTasksUtil.pauseModelresult(rpop);
						LogUtil.getLogger().info("暂停模式处理完毕："+new Date());
						break;
				/*	case "domain.create.result":
						LogUtil.getLogger().info("domain模式处理开始："+new Date()+":"+rpop);
						messageLog.saveDomainlog(rpop);
						toDataTasksUtil.pauseModelresult(rpop);
						LogUtil.getLogger().info("domain模式处理完毕："+new Date());
						break;*/
						
					default:
						break;
					}
				}
				
			} catch (Exception e) {                                                                             
				LogUtil.getLogger().error("线程出现异常了,redis-ip:"+configUtil.getRedisHost()+",redis-port:"+configUtil.getRedisPort(),e.getMessage(),e);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
