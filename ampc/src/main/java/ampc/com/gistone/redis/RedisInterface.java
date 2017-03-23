/**  
 * @Title: RedisInterface.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 下午7:33:41
 * @version 
 */
package ampc.com.gistone.redis;

import java.util.List;

/**  
 * @Title: RedisInterface.java
 * @Package ampc.com.gistone.redis
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月15日 下午7:33:41
 * @version 1.0
 */
public interface RedisInterface {
	public Long in(String key, String value);
	public String out(String key);
	public Long length(String key);
	public List<String> range(String key, int start, int end);
	public void remove(String key, long i, String value);
	public String rpoplpush(String string, String string2);

}
