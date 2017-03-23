/**  
 * @Title: TestRedisRunnbleConsumer.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 上午11:28:57
 * @version 
 */
package ampc.com.gistone.redis;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

/**  
 * @Title: TestRedisRunnbleConsumer.java
 * @Package ampc.com.gistone.redis
 * @Description: 消费者
 * @author yanglei
 * @date 2017年3月15日 上午11:28:57
 * @version 1.0
 */
@RestController
public class TestRedisRunnbleConsumer implements Runnable {
	/*@Autowired
	private Jedis jedis*/
	/*@Autowired
	private RedisInterface redisqueue;*/
	/*@Autowired
	 private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisQueue redisqueue;*/
	//Jedis jedis = new Jedis("166.111.42.46",16379,123456);
	
	@Autowired
	private RedisTestService redisTestService;
	

	/* (非 Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/ 
	
	@Override
	public void run() {
		Random random = new Random();
		while(true) {
			
			/*String  keyString = redisTestService.out("receive_channel_name");
			
			System.out.println(keyString);*/
			//从任务队列 task-queue中获取一个任务 并且将该任务放入暂存的队列中
			String taskid = redisTestService.rpoplpush("task-queue", "tmp-queue");
			//String taskid = redisqueue.rpoplpush("task-queue", "tmp-queue");
			//处理任务 ---模拟睡觉
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//模拟成功和失败的情况
			if (random.nextInt(13)%7==0) {//模拟失败的情况，概率为2/13
				 //将本次处理失败的任务从暂存队列"tmp-queue"中，弹回任务队列"task-queue"  
				redisTestService.rpoplpush("tmp-queue", "task-queue");
				System.out.println(taskid + "处理失败，被弹回任务队列");
			}else {//模拟成功的情况
				//将本次任务从暂存队列 tmp-queue 中清除
				//redisTestService.rpop("tmp-queue");
				System.out.println(taskid + "处理成功，被清除");
			}
		}
		
		
	}

}
