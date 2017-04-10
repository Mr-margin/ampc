/**  
 * @Title: RealForecastTimerApp.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月7日 下午4:55:57
 * @version 
 */
package ampc.com.gistone.redisqueue.timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**  
 * @Title: RealForecastTimerApp.java
 * @Package ampc.com.gistone.redisqueue.timer
 * @Description: 定时器入口
 * @author yanglei
 * @date 2017年4月7日 下午4:55:57
 * @version 1.0
 */
@SpringBootApplication  
@EnableScheduling
public class RealForecastTimerApp {
	/* (非 Javadoc) 
	* <p>Title: configure</p> 
	* <p>Description: </p> 
	* @param builder
	* @return 
	* @see org.springframework.boot.web.support.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder) 
	 
	
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(RealForecastTimerApp.class);
	}*/
	/*public static void main(String[] args) {
		SpringApplication.run(RealForecastTimerApp.class);
	}*/

}
