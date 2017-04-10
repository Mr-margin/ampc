/**  
 * @Title: QuartzJobRealForecast.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月8日 上午11:34:01
 * @version 
 */
package ampc.com.gistone.redisqueue.timer;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**  
 * @Title: QuartzJobRealForecast.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: 实时预报的定时工作类
 * @author yanglei
 * @date 2017年4月8日 上午11:34:01
 * @version 1.0
 */
public class QuartzJobRealForecast implements Job{

	/* (非 Javadoc) 
	* <p>Title: execute</p> 
	* <p>Description: </p> 
	* @param arg0
	* @throws JobExecutionException 
	* @see org.quartz.Job#execute(org.quartz.JobExecutionContext) 
	*/ 
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("------我是实时预报工作类------");
		
	}

}
