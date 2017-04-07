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

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.JsonUtil;

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
	
	@Override
	public void run() {
		
	
		
		while (true) {
			try {
		//	String send_queue_name = redisUtilServer.rpop("send_queue_name");//result_Start_model
			System.out.println("队列接受数据");
			String rpop = redisUtilServer.brpop("send_queue_name");//result_Start_model
			System.out.println(rpop+"刚取出来的");
		//	String rpop2 = redisUtilServer.rpop("send_queue_name");//result_Start_model
				Message message = JsonUtil.jsonToObj(rpop, Message.class);
				
				String key = message.getType();
				switch (key) {
				case "model.start.result":
					System.out.println("start tasks----------");
					toDataTasksUtil.updateDB(message);
					System.out.println("end tasks----------");
					break;
				case "ungrib.result":
					toDataUngribUtil.updateDB(rpop);
					System.out.println("ungrib---------------");
					break;

				default:
					break;
				}
				
				
			} catch (IOException e) {                                                                             
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
