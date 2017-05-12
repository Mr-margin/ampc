package ampc.com.gistone.util.excelUtil;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串帮助类
 * Created by chf on 2016/12/12.
 */
public class StringUtil {

  public static final char UNDERLINE = '_';

  /**
   * 统计字符串长度
   *
   * @param s
   * @return
   */
  public static int getWordCount(String s) {
    int length = 0;
    if (null == s || "".equals(s)) {
      return length;
    }
    for (int i = 0; i < s.length(); i++) {
      int ascii = Character.codePointAt(s, i);
      if (ascii >= 0 && ascii <= 255)
        length++;
      else
        length += 2;

    }
    return length;
  }

  /**
   * 驼峰转下划线
   *
   * @param param 驼峰字符串
   * @return 下划线
   */
  public static String camelToUnderline(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = param.charAt(i);
      if (Character.isUpperCase(c)) {
        sb.append(UNDERLINE);
        sb.append(Character.toLowerCase(c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 下划线转驼峰
   *
   * @param param
   * @return
   */
  public static String underlineToCamel(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    int len = param.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = param.charAt(i);
      if (c == UNDERLINE) {
        if (++i < len) {
          sb.append(Character.toUpperCase(param.charAt(i)));
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * 下划线转驼峰
   *
   * @param param
   * @return
   */
  public static String underlineToCamel2(String param) {
    if (param == null || "".equals(param.trim())) {
      return "";
    }
    StringBuilder sb = new StringBuilder(param);
    Matcher mc = Pattern.compile("_").matcher(param);
    int i = 0;
    while (mc.find()) {
      int position = mc.end() - (i++);
      //String.valueOf(Character.toUpperCase(sb.charAt(position)));
      sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
    }
    return sb.toString();
  }

  public static boolean isEmpty(String s) {
    if (null == s || "".equals(s.trim())) {
      return true;
    }
    return false;
  }
  /**
   * 手机号验证
   *
   * @param  str
   * @return 验证通过返回true
   */
  public static boolean isMobile(String str) {
    Pattern p = null;
    Matcher m = null;
    boolean b = false;
    p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
    m = p.matcher(str);
    b = m.matches();
    return b;
  }
  /**
   * 电话号码验证
   *
   * @param  str
   * @return 验证通过返回true
   */
  public static boolean isPhone(String str) {
    Pattern p1 = null,p2 = null;
    Matcher m = null;
    boolean b = false;
    p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
    p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
    if(str.length() >9)
    {   m = p1.matcher(str);
      b = m.matches();
    }else{
      m = p2.matcher(str);
      b = m.matches();
    }
    return b;
  }

  public static boolean isEmail(String email){
    boolean flag = false;
    try{
      String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
      Pattern regex = Pattern.compile(check);
      Matcher matcher = regex.matcher(email);
      flag = matcher.matches();
    }catch(Exception e){
      flag = false;
    }
    return flag;
  }

  public static boolean companynumber(String number) {
    boolean flag = false;
    String check = "^[A-Za-z0-9]{8}-[A-Za-z0-9]$";
    try{
      Pattern regex = Pattern.compile(check);
      Matcher matcher = regex.matcher(number);
      flag = matcher.matches();
    }catch(Exception e){
      flag = false;
    }
    return flag;
  }


  public static boolean isInArray(String key, String[] strs) {
    return Arrays.asList(strs).contains(key);
  }

  public static void main(String[] args) throws Exception {

    System.out.println(camelToUnderline("userId"));
    System.out.println(underlineToCamel("user_id"));
    System.out.println(underlineToCamel2("user_id"));

//        System.out.println(getWordCount("长城"));
//        FileWriter fw = new FileWriter("1.txt");
//        fw.write("中国");
//        fw.close();
//        FileInputStream fis = new FileInputStream("1.txt");
//        int i = fis.read();
//        System.out.println(i);
    File f = new File("1.txt");
    System.out.println(f.length());
    Character a = 'a';
    System.out.println(a.SIZE);
    System.out.println();
  }
}
