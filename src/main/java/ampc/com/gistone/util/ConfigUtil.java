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
	// 获取emis的参数（不需要减排系数）
	@Value("${emis.paramesURL}")
	private String emisParamesURL;
	//模式运行的bodyflag参数
	@Value("${model.bodyparames.flag}")
	private Integer modelbodyflagparams;

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
			//redis 连接池中的最大空闲连接
//			@Value("${spring.redis.pool.max-idle}")
//			private int maxIdle;
//			//redis 连接池中的最小空闲连接
//			@Value("${spring.redis.pool.min-idle}")
//			private int minidle;
//			//redis 连接池最大连接数（使用负值表示没有限制
//			@Value("${spring.redis.pool.max-active}")
//			private String maxActive;
//			//redis 连接池最大阻塞等待时间（使用负值表示没有限制)
//			@Value("${spring.redis.pool.max-wait}")
//			private Long maxWait;
//		    //连接超时时间（毫秒）
//			@Value("${spring.redis.timeout}")
//			private int timeout;
//	
//		    @Bean
//		    public JedisPool redisPoolFactory() {
//		        LogUtil.getLogger().info("JedisPool注入成功！！");
//		        LogUtil.getLogger().info("redis地址：" + redisHost + ":" + redisPort);
//		        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//		        jedisPoolConfig.setMaxIdle(maxIdle);
//		        jedisPoolConfig.setMaxWaitMillis(maxWait);
//		        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, redisPassWord);
//		        return jedisPool;
//		    }
//	
//	    
//			@Bean
//	        public RedisConnectionFactory redisConnectionFactory() {
//	            JedisConnectionFactory cf = new JedisConnectionFactory();  
//	            cf.setHostName(redisHost);  
//	            cf.setPort(redisPort); 
//	            cf.setPassword(redisPassWord);
//	            cf.afterPropertiesSet();  
//	            return cf;  
//	        }
//			
//
//
//			public int getTimeout() {
//				return timeout;
//			}
//
//
//			public void setMaxIdle(int maxIdle) {
//				this.maxIdle = maxIdle;
//			}
//
//
//
//
//			public int getMinidle() {
//				return minidle;
//			}
//
//
//
//			public String getMaxActive() {
//				return maxActive;
//			}
//
//
//			public Long getMaxWait() {
//				return maxWait;
//			}



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

	
	

	public Integer getModelbodyflagparams() {
				return modelbodyflagparams;
			}

	public String getEmisParamesURL() {
				return emisParamesURL;
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
