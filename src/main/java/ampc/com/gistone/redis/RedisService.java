package ampc.com.gistone.redis;


import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * Created by wgy on 2017/2/20.
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    private ValueOperations<String,String> valueOperations;

    //private  HashOperations hashOperations;

    //private SetOperations setOperations;

    private ListOperations<String,String> listOperations;

    //private ZSetOperations zSetOperations;

    @PostConstruct
    public void postConstruct(){

        this.valueOperations = stringRedisTemplate.opsForValue();

        this.listOperations =  stringRedisTemplate.opsForList();

    }

    /**设置有超时时间的KV */
    public boolean set(String key, String value, long seconds) {

        boolean result = false;

        try{

            valueOperations.set(key,value,seconds);

            result = true;

        }catch (Exception e) {

            e.printStackTrace();
        }

        return result;

    }

    /**
     *存入不会超时的KV
     */
    public boolean set(String key, String value) {

        boolean result = false;

        try{

            valueOperations.set(key,value);

            result = true;

        }catch (Exception e) {

            e.printStackTrace();
        }

        return result;

    }

    public void del(String key)  {

        valueOperations.getOperations().delete(key);

    }

    /**
     * 获取相应的键值
     * @param key
     * @return
     */
    public String get(String key) {

        return valueOperations.get(key);
    }

    public boolean setIfAbsent(String key,String value){

        return valueOperations.setIfAbsent(key,value);

    }

    /**获得客户端列表 */
    public List<?> getClients(){

        return valueOperations.getOperations().getClientList();
    }
    /**
     * redis数据库条数
     */
    public Long dbSize() {

        return valueOperations.getOperations().execute((RedisCallback<Long>) c->(c.dbSize()));

    }

    /**判断redis数据库是否有对应的key*/
    public boolean exist(String key){

        return valueOperations.getOperations().hasKey(key);
    }
    /**获得redis数据库所有的key*/
    public Set<String> keys(String pattern){

        return valueOperations.getOperations().keys(pattern);
    }
    
    public long leftPush(String key,String value){


        return listOperations.leftPush(key,value);
    }

    public String rpop(String key){

        return listOperations.rightPop(key);
    }

    public String brpop(String key) {

      return brpop(0,key);
    }

    /**
     * 移除元素
     * @param key 移除的key
     * @param start  开始位置
     * @param end  结尾位置
     */
    public void ltrim(String key,int start,int end){

        listOperations.trim(key,start,end);
    }

    public String bRPopLPush(int timeOut,String key1,String key2) {

        return listOperations.getOperations().execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {

                StringRedisSerializer keyRedisSerializer = (StringRedisSerializer) stringRedisTemplate.getKeySerializer();

                StringRedisSerializer valueRedisSerializer= (StringRedisSerializer)stringRedisTemplate.getValueSerializer();

                byte [] key1Bytes = keyRedisSerializer.serialize(key1);

                byte [] key2Bytes = keyRedisSerializer.serialize(key1);

                byte[] listByte =  redisConnection.bRPopLPush(timeOut,key1Bytes,key2Bytes);;

                return valueRedisSerializer.deserialize(listByte);
            }
        });
    }

    public String bRPopLPush(String key1,String key2) {

        return bRPopLPush(0,key1,key2);
    }



    public String brpop(int timeout,String key) {

        return listOperations.getOperations().execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {

                StringRedisSerializer keyRedisSerializer = (StringRedisSerializer) stringRedisTemplate.getKeySerializer();

                StringRedisSerializer valueRedisSerializer= (StringRedisSerializer)stringRedisTemplate.getValueSerializer();

                byte [] keyBytes = keyRedisSerializer.serialize(key);

                List <byte[]> listByte = redisConnection.bRPop(timeout,keyBytes);

                if( null == listByte) {

                    return null;
                }

                return valueRedisSerializer.deserialize(listByte.get(1));
            }
        });
    }

	/**
	 * @Description: TODO
	 * @param key
	 * @return   
	 * long  队/栈  长
	 * @throws
	 * @author yanglei
	 * @date 2017年3月14日 下午5:14:14
	 */
	public long length(String key) {
		return stringRedisTemplate.opsForList().size(key);
	}



}
