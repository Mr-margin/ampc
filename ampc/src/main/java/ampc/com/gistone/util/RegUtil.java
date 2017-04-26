package ampc.com.gistone.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 正则验证帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年4月20日
 */
public class RegUtil {
	
	/**
	 * 账号验证
	 * @param account
	 * @return
	 */
	public static boolean CheckAccount(String account){
	    //账号验证 必须为数字和字母还有下划线
	    String regEx = "^[a-zA-Z]+[a-zA-Z0-9_]{5,14}$";
	    // 编译正则表达式
	    Pattern pattern = Pattern.compile(regEx);
	    // 忽略大小写的写法
	    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(account);
	    // 字符串是否与正则表达式相匹配
	    boolean rs = matcher.matches();
	    return rs;
	}
	
	/**
	 * 参数验证
	 * @param param	 要验证的参数
	 * @param clazz	 要转换的类型
	 * @param regEx  正则表达式
	 * @param isIgnore  正则是否忽略大小写
	 * @return
	 */
	public static boolean CheckParameter(Object param,String clazz,String regEx,boolean isIgnore){
		//判断是否为空
		if(param==null) return false;
		//是否是指定的目标类型
		if(clazz.equals("Integer")){
			if (!(param instanceof Integer)) {
			    return false;
			} 
		}
		if(clazz.equals("String")){
			if (!(param instanceof String)) {
			    return false;
			} 
		}
		if(clazz.equals("Double")){
			if (!(param instanceof Double)) {
			    return false;
			} 
		}
		if(clazz.equals("Float")){
			if (!(param instanceof Float)) {
			    return false;
			} 
		}
		if(clazz.equals("Long")){
			if (!(param instanceof Long)) {
			    return false;
			} 
		}
		if(clazz.equals("Date")){
			if (!(param instanceof Date)) {
			    return false;
			} 
		}
		if(clazz.equals("List")){
			if (!(param instanceof List)) {
			    return false;
			} 
		}
		if(clazz.equals("Map")){
			if (!(param instanceof Map)) {
			    return false;
			} 
		}
		if(clazz.equals("Set")){
			if (!(param instanceof Set)) {
			    return false;
			} 
		}
		//是否包含正则表达式
		if(regEx!=null){
			Pattern pattern=null;
			//是否忽略大小写
			if(isIgnore){
				pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			}else{
				pattern = Pattern.compile(regEx);
			}
			Matcher matcher = pattern.matcher(param.toString());
			if(!matcher.matches()) return false;
		}
		return true;
	}
	
}
