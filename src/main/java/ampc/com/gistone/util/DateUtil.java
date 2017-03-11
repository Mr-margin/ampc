package ampc.com.gistone.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换帮助类
 * 
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月11日
 */
public class DateUtil {

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

}
