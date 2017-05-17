package ampc.com.gistone.runner;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ampc.com.gistone.redisqueue.AcceptMessageQueue;

/**
 * 服务启动执行
 *
 * @author   
 * @myblog  
 * @create   
 */
@Component
@Order(value=1)
public class MyStartupRunner implements CommandLineRunner{
	
//	@Autowired
//	private GetBySqlMapper getBySqlMapper;
	@Autowired
	private AcceptMessageQueue acceptMessageQueue;
	
	
	
	/*private AcceptMessageQueue acceptMessageQueue;*/
	@Override
    public void run(String... args) throws Exception {
    	
		Date start1 = new Date();
		
//		OverallSituation.Calculation_Template_p = this.questTemplate.getCalculation_Template();
//		OverallSituation.Update_Template_p = this.questTemplate.getUpdate_Template();
		
			/*//启动一个生产者线程 模拟任务的产生
				new Thread(testRedisRunnble).start();
				Thread.sleep(10000);*/
//				new Thread(acceptMessageQueue).start();
				new Thread(acceptMessageQueue).start();
//				new Thread(acceptMessageQueue).start();
			//	Thread.sleep(5000);
				//启动一个线程者线程 模拟任务的处理
				/*new Thread(testRedisRunnbleConsumer).start();
				//主线程休眠
				Thread.sleep(Long.MAX_VALUE);*/
		
		System.out.println("获取文件........，共用时 " + (new Date().getTime() - start1.getTime())/1000 + " m");
		System.out.println("-------------Load Complete-----------");
		
//		System.out.println(OverallSituation.Calculation_Template_p);
		
    }
	
}
