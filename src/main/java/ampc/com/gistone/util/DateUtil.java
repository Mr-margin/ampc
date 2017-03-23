package ampc.com.gistone.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	 * 
	 * @Description: TODO
	 * @param str
	 * @return   字符串转化成日期 年月日形式
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月21日 下午3:16:47
	 */
	public static Date StrtoDateYMD(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 
	 * @Description: TODO
	 * @param date
	 * @return   
	 * String  时间的转化 年月日
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 下午7:51:09
	 */
	public static String DATEtoString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}
	
	/**
	 * @Description: TODO
	 * @param oldlastfnlDate
	 * @param todayDate
	 * @return   
	 * boolean  比较两个时间的大小，当第一个时间大于第二个时间的时候返回false 当第一个时间小于第二个时间的时候返回true 等于的时候返回0
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 下午4:52:12
	 */
	public static boolean compare(Date oldlastfnlDate, Date todayDate) {
		boolean flag ;
		int i = oldlastfnlDate.compareTo(todayDate);
		if (i<0) {
			flag = false;
		}else if(i==0){
			
		}
		
		return false;
	}
	
	/**
	 * 
	 * @Description: TODO
	 * @param date
	 * @return   
	 * Date  当前日期减一天
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 下午4:35:27
	 */
	
	public static Date reduceOneDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	/**
	 * 
	 * @Description: TODO
	 * @param date
	 * @return   
	 * String  时间在现有的时间上减一天
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 下午4:01:57
	 */
	
	public static String reduceOnePathDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		String newdate = format.format(cal.getTime());
		return newdate;
	}

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
