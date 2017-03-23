/**  
 * @Title: AcceptMessage.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月21日 上午9:06:49
 * @version 
 */
package ampc.com.gistone.redisqueue;



import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;

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

	/* (非 Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/ 
	
	@Override
	public void run() {
		String rpop = redisUtilServer.rpop("test_task-queue_ungrib");
		System.out.println(rpop);
		toDataUngribUtil.updateDB(rpop);
		//JSONArray array = JSONArray.fromObject(rpop);
		/*try {
			JSONObject jsonObject = new JSONObject(rpop);
			String id = (String) jsonObject.get("id");
			String time = (String) jsonObject.get("time");
			String type = (String) jsonObject.get("type");
			String id = (String) jsonObject.get("id");
			String id = (String) jsonObject.get("id");
			System.out.println(id);
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*while (true) {
		//	String rpop = redisUtilServer.rpop("send_queue_name");
			//System.out.println(rpop);
			
			
			
		}
*/		
		
	}

}
