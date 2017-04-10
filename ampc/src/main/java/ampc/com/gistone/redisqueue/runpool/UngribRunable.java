/**  
 * @Title: UngribRunable.java
 * @Package ampc.com.gistone.redisqueue.runpool
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月30日 下午4:20:53
 * @version 
 */
package ampc.com.gistone.redisqueue.runpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.redisqueue.RedisUtilServer;
import ampc.com.gistone.redisqueue.ToDataTasksUtil;
import ampc.com.gistone.redisqueue.ToDataUngribUtil;

/**  
 * @Title: UngribRunable.java
 * @Package ampc.com.gistone.redisqueue.runpool
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月30日 下午4:20:53
 * @version 1.0
 */
@Component
public class UngribRunable implements Runnable{
	
	
	
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
		
		
		
	}

}
