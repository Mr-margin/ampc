package ampc.com.gistone.util;

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
	    String regEx = "^[a-zA-Z]+[a-zA-Z0-9_]{6,15}$";
	    // 编译正则表达式
	    Pattern pattern = Pattern.compile(regEx);
	    // 忽略大小写的写法
	    // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(account);
	    // 字符串是否与正则表达式相匹配
	    boolean rs = matcher.matches();
	    return rs;
	}
	
}
