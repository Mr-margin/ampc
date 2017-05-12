package ampc.com.gistone.util.excelUtil;

/**
 * 行政区划帮助类
 * Created by TG on 2017/1/10.
 */
public class RegionUtil {

  public static String regionLike(String region){
    if("000000".equals(region)){
      return  null;
    }else if("0000".equals(region.substring(2,6))){
      return region.substring(0,2);
    }else if("00".equals(region.substring(4,6))){
      return region.substring(0,4);
    }else{
      return region;
    }
  }
  public static String getRegionLike(String regionId) {
    if(StringUtil.isEmpty(regionId))return null;
    if (regionId.endsWith("0000")) {
      return regionId.substring(0, 2) + "%";
    } else if (regionId.endsWith("00")) {
      return regionId.substring(0, 4) + "%";
    } else {
      return regionId+"%";
    }
  }
  /**
   * 行政区划代码匹配4位
   *
   * @param regionId
   * @return
   */
  public static String cityRegionLike(String regionId) {
    return regionId.substring(0, 4) + "%";
  }

  /**
   * 行政区划代码匹配2位
   *
   * @param regionId
   * @return
   */
  public static String proRegionLike(String regionId) {
    return regionId.substring(0, 2) + "%";
  }

  public static String getLocalRegionLike(String regionId) {
    if (regionId.endsWith("00")) {
      return regionId.substring(0, 4) + "%";
    } else if (regionId.endsWith("0000")) {
      return regionId.substring(0, 2) + "%";
    }else if(regionId.length()==4){
      return regionId + "%";
    }
    return null;
  }


  /**
   * 获取所在行政区划的上级代码
   *
   * @param regionId
   * @return
   */
  public static String getUpperRegion(String regionId) {
    if (regionId.endsWith("00")) {
      return regionId.substring(0, 2) + "%";
    } else if (regionId.endsWith("0000")) {
      return regionId.substring(0, 2) + "%";
    } else {
      return regionId.substring(0, 4) + "%";
    }
  }

  /**
   * 截取层级
   * @param regionId
   * @param level
   * @return
   */
  public static String subRegion(String regionId, Integer level) {
    if (1 == level) {
      return regionId.substring(0, 2) + "0000";
    } else if (2 == level) {
      return regionId.substring(0, 4) + "00";
    } else if (3 == level) {
      return regionId;
    }
    return null;
  }

  /**
   * 获取region 前四位
   * @param regionId
   * @return
   */
  public static String getFourChar(String regionId){
    return regionId.substring(0,4);
  }

}
