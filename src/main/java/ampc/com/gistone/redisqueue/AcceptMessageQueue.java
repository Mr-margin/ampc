/**  
 * @Title: AcceptMessage.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:06:49
 * @version 
 */
package ampc.com.gistone.redisqueue;



import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.redisqueue.result.Message;
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

	/* (非 Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/ 
	
	public void run() {
		
	
		
		while (!Thread.interrupted()) {
			
			try {
		//	String send_queue_name = redisUtilServer.rpop("send_queue_name");//result_Start_model
			System.out.println("队列接受数据");
			String rpop = redisUtilServer.brpop("send_queue_name");//result_Start_model
		//	String rpop2 = redisUtilServer.rpop("send_queue_name");//result_Start_model
			if (null==rpop) {
				System.out.println(rpop+"刚取出来的");
				LogUtil.getLogger().info("队列里面没有数据了！");
			}else {
			
				Message message = JsonUtil.jsonToObj(rpop, Message.class);
				
				String key = message.getType();
				switch (key) {
				case "model.start.result":
					LogUtil.getLogger().info("start tasks"+new Date());
					LogUtil.getLogger().info(rpop);
					toDataTasksUtil.updateDB(message);
					LogUtil.getLogger().info("end tasks"+new Date());
					break;
				case "ungrib.result":
					LogUtil.getLogger().info("接受ungrib数据："+new Date());
					toDataUngribUtil.updateDB(rpop);
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
		}
		
	}

}
