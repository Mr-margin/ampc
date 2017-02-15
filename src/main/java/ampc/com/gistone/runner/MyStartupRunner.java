package ampc.com.gistone.runner;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
	
	@Override
    public void run(String... args) throws Exception {
    	
		Date start1 = new Date();
		
//		OverallSituation.Calculation_Template_p = this.questTemplate.getCalculation_Template();
//		OverallSituation.Update_Template_p = this.questTemplate.getUpdate_Template();
		
		System.out.println("获取文件........，共用时 " + (new Date().getTime() - start1.getTime())/1000 + " m");
		System.out.println("-------------Load Complete-----------");
		
//		System.out.println(OverallSituation.Calculation_Template_p);
		
    }
	
}
