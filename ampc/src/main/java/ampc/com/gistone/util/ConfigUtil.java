package ampc.com.gistone.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;



@Component
@Configuration
@PropertySource(value = "classpath:/config.properties")
/**
 * 路径帮助类
 * 
 * @author WangShanxi
 */
public class ConfigUtil {
	// 减排Url
	@Value("${jpURL}")
	private String jpURL;
	
	public String getJpURL() {
		return jpURL;
	}

	// 措施的减排分析Url
	@Value("${jp.serverPath}")
	private String serverPath;
		
	// 是否调用外部的预处理接口
	@Value("${jp.preproEnable}")
	private boolean preproEnable;

	// 调用外部的预处理接口时使用的URL
	@Value("${jp.preproURL}")
	private String preproURL;
	//模式运行的bodyflag参数
	@Value("${model.bodyparames.flag}")
	private Integer modelbodyflagparams;
	//actionlist.actionDirPre
	@Value("${actionlist.actionDirPre}")
	private String actionlistDirPre;
	//actionlist.actionDirAfter
	@Value("${actionlist.actionDirAfter}")
	private String actionlistDirAfter;
	//线程和定时器的一键设置
	@Value("${schedulerandrunnable.setting}")
	private boolean runningSetting;
	//正式和测试运行结果的flag-用于记录日志
	@Value("${model.officialexecute.result.log}")
	private boolean modelResultFlagLog;
	//记录发送的消息生成的文件的路径
	@Value("${sendmessage.log}")
	private String sendMessageLog;
	
	@Value("${exqd}")
	private String exqd;

	public String getExqd() {
		return exqd;
	}
	//微信路径url
	@Value("${weixin.serverURL}")
	private String weixinServerURL;
	
	//模式执行task任务
	@Value("${tasksArray1}")
	private String tasksArray1;
	//模式执行task任务2
	@Value("${tasksArray2}")
	private String tasksArray2;
	
			//redisIP地址
			@Value("${spring.redis.host}")
			private String redisHost;
			//redis 端口
			@Value("${spring.redis.port}")
			private int redisPort;
			//redis 密码
			@Value("${spring.redis.password}")
			private String redisPassWord;
			//redis 发送消息队列的名称
			@Value("${redis.receive.queue.name}")
			private String redisQueuesSendName;
			//redis 接受消息队列的名称
			@Value("${redis.send.queue.name}")
			private String redisQueueAcceptName;



			public String getRedisQueuesSendName() {
				return redisQueuesSendName;
			}

			public String getRedisQueueAcceptName() {
				return redisQueueAcceptName;
			}

			public String getRedisHost() {
				return redisHost;
			}

			public int getRedisPort() {
				return redisPort;
			}

			public String getRedisPassWord() {
				return redisPassWord;
			}

	
	

			
			

	/**
	 * @return the tasksArray2
	 */
	public String getTasksArray2() {
		return tasksArray2;
	}

	/**
	 * @return the tasksArray1
	 */
	public String getTasksArray1() {
		return tasksArray1;
	}

	public String getWeixinServerURL() {
				return weixinServerURL;
			}

	public String getSendMessageLog() {
				return sendMessageLog;
			}

	public boolean isModelResultFlagLog() {
				return modelResultFlagLog;
			}

	public boolean isRunningSetting() {
				return runningSetting;
			}

	public String getActionlistDirPre() {
				return actionlistDirPre;
			}

	public String getActionlistDirAfter() {
			return actionlistDirAfter;
		}

	public Integer getModelbodyflagparams() {
				return modelbodyflagparams;
			}

	



	public boolean isPreproEnable() {
		return preproEnable;
	}

	public String getPreproURL() {
		return preproURL;
	}
	
	public String getServerPath() {
		return serverPath;
	}
}
