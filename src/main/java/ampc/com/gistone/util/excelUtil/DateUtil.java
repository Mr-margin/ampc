package ampc.com.gistone.util.excelUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Excel中的日期工具类
 */
public class DateUtil {

    private static SimpleDateFormat YYYY_MM_DD_SLASH = new SimpleDateFormat("yyyy/MM/dd");

    private static SimpleDateFormat YYYY_MM_DD_LINE = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat YYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");

    private static SimpleDateFormat YYYY_MM_DD_HH_MM_SS_LINE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date getYYYYMMDDSlashDate(String str) throws ParseException {
        return YYYY_MM_DD_SLASH.parse(str);
    }

    public static String getYYYYMMDDSlashStr(Date date) {
        return YYYY_MM_DD_SLASH.format(date);
    }

    public static Date getYYYYMMDDLineDate(String str) throws ParseException {
        return YYYY_MM_DD_LINE.parse(str);
    }

    public static String getYYYYMMDDLineStr(Date date) {
        return YYYY_MM_DD_LINE.format(date);
    }

    public static String getYYYYMMDDHHMMSS(Date date) throws Exception {
        return YYYYMMDDHHMMSS.format(date);
    }

    public static String getYYYYMMDDHHMMSSLineStr(Date date) throws ParseException {
        return YYYY_MM_DD_HH_MM_SS_LINE.format(date);
    }

    public static Date getYYYYMMDDHHMMSSLineDate(String str) throws ParseException {
        return YYYY_MM_DD_HH_MM_SS_LINE.parse(str);
    }

    public static boolean lessThan(Date date1, Date date2) throws ParseException {
        Date day1 = YYYY_MM_DD_LINE.parse(YYYY_MM_DD_LINE.format(date1));
        Date day2 = YYYY_MM_DD_LINE.parse(YYYY_MM_DD_LINE.format(date1));
        if (day1.getTime() < day2.getTime()) {
            return true;
        }
        return false;
    }

    public static Date addDay(Date date, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, num);
        return c.getTime();
    }

    public static Date getYYYYMMDD235959SlashDate(String str) throws ParseException {
        Date d = getYYYYMMDDSlashDate(str);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static Date formatYYYYMMDD(Date date) throws Exception {
        return getYYYYMMDDLineDate(getYYYYMMDDLineStr(date));
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(new SimpleDateFormat("yyyy-MM-dd 23:59:59").parse("2016-03-03"));
    }

}
