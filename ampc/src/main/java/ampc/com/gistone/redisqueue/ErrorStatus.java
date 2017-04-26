/**  
 * @Title: ErrorStatus.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月6日 下午2:59:01
 * @version 
 */
package ampc.com.gistone.redisqueue;

import org.springframework.stereotype.Component;

import ampc.com.gistone.util.LogUtil;

/**  
 * @Title: ErrorStatus.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月6日 下午2:59:01
 * @version 1.0
 */
@Component
public class ErrorStatus {

	/**
	 * @Description: TODO
	 * @param tasksScenarinoId   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 下午3:00:39
	 */
	public static void Errortips(Long tasksScenarinoId) {
		LogUtil.getLogger().info("该条情景参数组织错误");
	}

}
