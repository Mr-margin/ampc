package ampc.com.gistone.redisqueue.test;

/**  
 * @Title: RedisTestService.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 下午6:36:52
 * @version 
 */

import java.util.List;

import oracle.net.aso.s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**  
 * @Title: RedisTestService.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 下午6:36:52
 * @version 1.0
 */
@Service
public class RedisTestService implements RedisInterface{
	@Autowired
	private StringRedisTemplate stringredistemplate;
	
	

	/* (非 Javadoc) 
	* <p>Title: in</p> 
	* <p>Description: </p> 
	* @param key
	* @param value
	* @return 
	* @see ampc.com.gistone.redis.RedisInterface#in(java.lang.String, java.lang.String) 
	*/ 
	
	@Override
	public Long in(String key, String value) {
		// TODO Auto-generated method stub
		return stringredistemplate.opsForList().leftPush(key, value);
	}

	/* (非 Javadoc) 
	* <p>Title: out</p> 
	* <p>Description: </p> 
	* @param key
	* @return 
	* @see ampc.com.gistone.redis.RedisInterface#out(java.lang.String) 
	*/ 
	
	@Override
	public String out(String key) {
		// TODO Auto-generated method stub
		return stringredistemplate.opsForList().rightPop(key);
	}

	/* (非 Javadoc) 
	* <p>Title: length</p> 
	* <p>Description: </p> 
	* @param key
	* @return 
	* @see ampc.com.gistone.redis.RedisInterface#length(java.lang.String) 
	*/ 
	
	@Override
	public Long length(String key) {
		// TODO Auto-generated method stub
		return stringredistemplate.opsForList().size(key);
	}

	/* (非 Javadoc) 
	* <p>Title: range</p> 
	* <p>Description: </p> 
	* @param key
	* @param start
	* @param end
	* @return 
	* @see ampc.com.gistone.redis.RedisInterface#range(java.lang.String, int, int) 
	*/ 
	
	@Override
	public List<String> range(String key, int start, int end) {
		// TODO Auto-generated method stub
		return stringredistemplate.opsForList().range(key, start, end);
	}

	/* (非 Javadoc) 
	* <p>Title: remove</p> 
	* <p>Description: </p> 
	* @param key
	* @param i
	* @param value 
	* @see ampc.com.gistone.redis.RedisInterface#remove(java.lang.String, long, java.lang.String) 
	*/ 
	
	@Override
	public void remove(String key, long i, String value) {
		// TODO Auto-generated method stub
		stringredistemplate.opsForList().remove(key, i, value); 
		
	}

	/* (非 Javadoc) 
	* <p>Title: rpoplpush</p> 
	* <p>Description: </p> 
	* @param string
	* @param string2
	* @return 
	* @see ampc.com.gistone.redis.RedisInterface#rpoplpush(java.lang.String, java.lang.String) 
	*/ 
	
	@Override
	public String rpoplpush(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

}
