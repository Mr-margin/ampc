package ampc.com.gistone.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource(value = "classpath:/config.properties")
/**
 * 路径帮助类
 * 
 * @author WangShanxi
 */
public class ConfigUtil {
	// 措施的减排分析Url
	@Value("${jp.csjpURL}")
	private String csjpURL;
	
	// 措施的减排分析Url
	@Value("${jp.serverPath}")
	private String serverPath;
		
	// 区域的减排分析Url
	@Value("${jp.areaURL}")
	private String areaURL;
	// 区域状态的减排url
	@Value("${jp.areaStatusURL}")
	private String areaStatusURL;

	// 减排计算获取actionlist的url
	@Value("${jp.actionlistURL}")
	private String actionlistURL;

	// 是否调用外部的预处理接口
	@Value("${jp.preproEnable}")
	private boolean preproEnable;

	// 调用外部的预处理接口时使用的URL
	@Value("${jp.preproURL}")
	private String preproURL;

	
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

	public String getActionlistURL() {
		return actionlistURL;
	}

	public String getCsjpURL() {
		return csjpURL;
	}

	public String getAreaURL() {
		return areaURL;
	}

	public String getAreaStatusURL() {
		return areaStatusURL;
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
