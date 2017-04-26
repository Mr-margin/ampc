package ampc.com.gistone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期转换帮助类
 * 
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月11日
 */
public class DateUtil {

	public final static String DATE_FORMAT = "yyyy-MM-dd";
	private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * 
	 * @Description: TODO
	 * @param str
	 * @return 字符串转化成日期 年月日形式 Date
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月21日 下午3:16:47
	 */
	public static Date StrtoDateYMD(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
	 * @Description: 字符串转化为时间
	 * @param str
	 * @param pattern
	 * @return Date
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月24日 下午3:57:35
	 */
	public static Date StrtoDateYMD(String str, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
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
	 * @return String 时间的转化 年月日
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月20日 下午7:51:09
	 */
	public static String DATEtoString(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String str = format.format(date);
		return str;
	}

	/**
	 * @Description: TODO
	 * @param oldlastfnlDate
	 * @param todayDate
	 * @return boolean 比较两个时间的大小，当第一个时间大于第二个时间的时候返回false 当第一个时间小于第二个时间的时候返回true
	 *         等于的时候返回true
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月20日 下午4:52:12
	 */
	public static boolean compare(Date oldlastfnlDate, Date todayDate) {
		boolean flag;
		int i = oldlastfnlDate.compareTo(todayDate);
		if (i < 0) {
			flag = true;
		} else {
			flag = false;

		}

		return flag;
	}

	/**
	 * 
	 * @Description: TODO
	 * @param date
	 * @return Date 当前日期的变化增减 返回时间
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月20日 下午4:35:27
	 */

	public static Date ChangeDay(Date date, int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}
	

	/**
	 * @Description: 时间	去掉当前的时分秒增减小时之后返回一个新的时间
	 * @param initDate
	 * @param pattern
	 * @param i
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月21日 上午10:20:19
	 */
	public static Date changedateByHour(Date initDate, int i) {
		/*SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String  newDatestr = new SimpleDateFormat("yyyyMMdd").format(initDate);
		String newString = newDatestr+" "+"00:00:00";
		Date newDate = null;
		//	Date handleinitDate = format.parse(newString);
			Calendar cal = Calendar.getInstance();
		//	cal.setTime(handleinitDate);
			cal.setTime(initDate);
			cal.add(Calendar.HOUR_OF_DAY, i);
			newDate = cal.getTime();*/
			//上面那个是针对传进来的参数没有格式化处理而写的
			Calendar cal = Calendar.getInstance();
			cal.setTime(initDate);
			cal.add(Calendar.HOUR_OF_DAY, i);
			Date newDate = cal.getTime();
		return newDate;
	}
	
	/**
	 * 
	 * @Description: TODO
	 * @param date
	 * @return String 时间在现有的时间上增减 返回字符串
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月20日 下午4:01:57
	 */

	public static String changeDate(Date date, String pattern, int i) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, i);
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

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate1(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * @Description: 将时间格式化并返回时间
	 * @param date
	 * @param string
	 * @return Date
	 * @throws @author
	 *             yanglei
	 * @date 2017年3月25日 下午12:02:37
	 */
	public static Date DateToDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String str = format.format(date);
		Date newdate = null;
		try {
			newdate = format.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newdate;
	}

	public static LocalDate convertStringToLocalDate(String value) {
		return LocalDate.parse(value);
	}

	public static LocalDateTime convertStringToLocalDatetime(String dateStr) {
		if (StringUtils.isNotEmpty(dateStr)) {
			Date d = phraseDate(dateStr);
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault());
		}
		return null;
	}

	public static Date phraseDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			logger.error("ParseException", e);
			return null;
		}
	}

	

	public static String timeToDays(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(time));
		return String.format("%s%03d", calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR));
	}
}
