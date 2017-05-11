/**  
 * @Title: Timertest.java
 * @Package ampc.com.gistone.redisqueue.test
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午7:47:58
 * @version 
 */
package ampc.com.gistone.redisqueue.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RedisConfig;

/**  
 * @Title: Timertest.java
 * @Package ampc.com.gistone.redisqueue.test
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午7:47:58
 * @version 1.0
 */
@EnableScheduling
@Component
public class Timertest {

	@Autowired
	private RedisTestService redisqueue;
	/*@Autowired
	private RedisConfig redisConfig;*/
	@Autowired
	private ConfigUtil configUtil;
	
//	@Scheduled(fixedRate = 3000)
//	@Scheduled(cron="0 37 10 * * ?")
	public void continueRealModel() {
		//--------------启动模式的实例----------
		Result_Start_model result_Start_model = new Result_Start_model();
				/*String format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				result_Start_model.setId(UUID.randomUUID().toString());
				result_Start_model.setId(format3);*/
				result_Start_model.setId("992fbb95-0127-4c75-8a88-f96e111ad947");
				result_Start_model.setTime("2017-05-04 15:26:41");
				result_Start_model.setType("model.start.result");
				Map<String,Object> map = new HashMap<String, Object>();
				Long scenarioid2 = (long)2;
				map.put("scenarioid",scenarioid2 );
				map.put("index", 8);
				map.put("date", "20170507");
				map.put("code", "1");
				map.put("desc", "");
				result_Start_model.setBody(map);
				JSONObject jsonObject = JSONObject.fromObject(result_Start_model);
				String json = jsonObject.toString();
//				redisqueue.in("send_queue_name", json);//result_Start_model
//				redisqueue.in("r0_mb", json);//result_Start_model
				Long in = redisqueue.in("r0_test_mb", json);//result_Start_model
				System.out.println(in+":"+json+"返回结果");
	}
	
	
//	@Scheduled(fixedRate = 5000)
	public void test() {
		System.out.println(configUtil.getRedisQueueAcceptName());//mb
		System.out.println(configUtil.getRedisQueuesSendName());//bm
	}
	
//	@Scheduled(fixedRate = 5000)
//	@Scheduled(cron="0 32 10 * * ?")
	private void ungribshili() {
		//----------------ungrib的实例-------------
				//RedisQueue redisqueue = new RedisQueue();
				System.out.println("------1-----");
				UngribTest ungribTest = new UngribTest();
				//redisService.leftPush("task-queue", "12345678911");
				ungribTest.setId(UUID.randomUUID().toString());
				String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				ungribTest.setTime("2017-05-10 09:53:39");
				ungribTest.setType("ungrib.result");
				Map<String,Object> map = new HashMap<String, Object>();
				//Map<String,Object> body = new HashMap<String,Object>();
				String format2 = new SimpleDateFormat("yyyyMMdd").format(new Date());
				map.put("pathdate",  "20170510");
				 int[] fnl ={1};
		 		map.put("fnl", fnl);
		 		
		 		int[] gfs = {1, 1, 1, 1, 1, 1, 1, 1, 1};
		 		map.put("gfs", gfs);
		 		String[] fnlerror = {""}; 
		 		map.put("fnlDesc", fnlerror);
		 		String[] gfserror = {"","","","","","","","",""};
		 		map.put("gfsDesc", gfserror);
		 		ungribTest.setBody(map);
				JSONObject jsonObject1 = JSONObject.fromObject(ungribTest);
				String json1 = jsonObject1.toString();
				//redisqueue.in("test", json1);
				redisqueue.in("r0_mb", json1);
				System.out.println(json1+"fangrushuju  ungrib");
				//{"id":"bafe5458-fb66-4fe3-b268-917d0a96b28f","time":"2017-04-27 11:41:17","type":"ungrib.result","body":{"gfsDesc":["","","","","","","","",""],"fnl":["1"],"gfs":["1","1","1","1","1","1","1","1","1"],"fnlDesc":[""],"pathdate":"20170427"}}
				/*
				 * {
    "id": "bafe5458-fb66-4fe3-b268-917d0a96b28f",
    "time": "2017-04-27 11:41:17",
    "type": "ungrib.result",
    "body": {
        "gfsDesc": [
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        ],
        "fnl": [
            "1"
        ],
        "gfs": [
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1"
        ],
        "fnlDesc": [
            ""
        ],
        "pathdate": "20170427"
    }
}
				 * 
				 * 
				 * */
	}
}
