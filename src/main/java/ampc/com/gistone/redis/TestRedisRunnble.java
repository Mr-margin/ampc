/**  
 * @Title: TestRedisRunnble.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 上午10:54:09
 * @version 
 */
package ampc.com.gistone.redis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**  
 * @Title: TestRedisRunnble.java
 * @Package ampc.com.gistone.redis
 * @Description: 生产者
 * @author yanglei
 * @date 2017年3月15日 上午10:54:09
 * @version 1.0
 * @param <V>
 */
@RestController
public class TestRedisRunnble<V> implements Runnable{
	/*@Autowired
	private Jedis jedis;
	@Autowired
	private RedisService redisService;*/
	@Autowired
	private RedisTestService redisqueue;
	@Autowired
	private UngribTest UngribTest;
	/*@Autowired
	private StringRedisTemplate stringredistemplate;*/

	/* (非 Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/ 
	
	@Override
	public void run() {
		Random random = new Random();
		//RedisQueue redisqueue = new RedisQueue();
		System.out.println("------1-----");
		//redisService.leftPush("task-queue", "12345678911");
		UngribTest.setId(UUID.randomUUID().toString());
		String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		UngribTest.setTime(format);
		UngribTest.setType("ungrib.result");
		Map<String,Object> map = new HashMap<String, Object>();
		//Map<String,Object> body = new HashMap<String,Object>();
		String format2 = new SimpleDateFormat("yyyyMMdd").format(new Date());
		map.put("pathdate",  format2);
		 int[] fnl ={1};
 		map.put("fnl", fnl);
 		
 		int[] gfs = {1,1,1,1,1,1,1,1,1};
 		map.put("gfs", gfs);
 		String[] fnlerror = {"fnlerror"}; 
 		map.put("fnlDesc", fnlerror);
 		String[] gfserror = {"","","","","","gfs6error"};
 		map.put("gfsDesc", gfserror);
 		UngribTest.setBody(map);
		JSONObject jsonObject = JSONObject.fromObject(UngribTest);
		String json = jsonObject.toString();
		redisqueue.in("ungrib_test", json);
		
		//stringredistemplate.opsForList().leftPush("task-queue", "1111111111");
		/*while(true){
			try {
				Thread.sleep(random.nextInt(600)+600);
				System.out.println("-----2------");
				//模拟一个生产任务
				UUID taskid = UUID.randomUUID();
				System.out.println(taskid);
			//	jedis.lpush("task-queue", taskid.toString());
				redisqueue.in("task-queue", taskid.toString());
				System.out.println("----3----");
				System.out.println("插入了一个新任务："+taskid);
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}*/
		
	}
	

}
