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
	@Autowired
	private AcceptMessageQueue acceptMessageQueue;
	
	@Override
    public void run(String... args) throws Exception {
		Date start1 = new Date();
		new Thread(acceptMessageQueue).start();
//		System.out.println("获取文件........，共用时 " + (new Date().getTime() - start1.getTime())/1000 + " m");
		System.out.println("-------------Load Complete-----------");
    }
	
}
