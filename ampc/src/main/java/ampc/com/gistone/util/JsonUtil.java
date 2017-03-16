package ampc.com.gistone.util;


import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月28日
 */
public class JsonUtil {

	private static ObjectMapper MAPPER = new ObjectMapper();
	/**
	 * 将Clob转换成Object对象
	 * @param clob
	 * @return
	 * @throws SQLException 
	 */
	public static Object clobToObject(Clob clob) throws Exception{
		String string=clob.getSubString(1, (int) clob.length());
		return MAPPER.readValue(string, Object.class);
	}
	/**
	 * 将String转换成指定对象
	 * @param clob
	 * @return
	 * @throws SQLException 
	 */
	public static Object strToClass(String str,Class clazz) throws Exception{
		return MAPPER.readValue(str, clazz);
	}
}
