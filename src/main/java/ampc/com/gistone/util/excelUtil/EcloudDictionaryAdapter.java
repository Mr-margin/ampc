package ampc.com.gistone.util.excelUtil;



import java.util.*;

/**
 * 根据字典项 组织成java中的实体类表现形式 List<List<TemplateDictionary>>
 *
 * 共享Adapter中字典项数据集
 */
public class EcloudDictionaryAdapter extends Adapter {

  //适配Dictionary  切层级  省-市-县
  public static List<List<TemplateDictionary>> adapterRegion(List<Dictionary> region) {
    List l1 = new ArrayList<>();
    List l2 = new ArrayList<>();
    List l3 = new ArrayList<>();
    region.forEach(e -> {
      String[] level = e.getKeyParam().split("_");
      if (level.length == 1) {
        TemplateDictionary td = new TemplateDictionary();
        td.setKey(level[0]);
        td.setValue(e.getValueParam());
        td.setParentKey(null);
        l1.add(td);
      } else if (level.length == 2) {
        TemplateDictionary td = new TemplateDictionary();
        td.setKey(level[1]);
        td.setValue(e.getValueParam());
        td.setParentKey(RegionUtil.subRegion(e.getValueParam(), 1));
        l2.add(td);
      } else if (level.length == 3) {
        TemplateDictionary td = new TemplateDictionary();
        td.setKey(level[2]);
        td.setValue(e.getValueParam());
        td.setParentKey(RegionUtil.subRegion(e.getValueParam(), 2));
        l3.add(td);
      }
    });
    List l = new ArrayList<>();
    l.add(l1);
    l.add(l2);
    l.add(l3);
    return l;
  }


  /**
   * 下拉类型的规则
   */
  public static Set<String> root = new HashSet<String>() {{
    add("etaSO2name");
    add("etaPMname");
    add("etaNOxxname");
    add("etaNOxname");
    add("industrytype");
    add("judge");
    add("region");
  }};

  /**
   * 将字典项转换成java实体类表达形式
   */
  @Override
  public Map<String, List<TemplateDictionary>> build() {

    dicMap = new LinkedHashMap<>();

    Map<String, Object> map = (Map<String, Object>) this.dic;
    dicMap.put("region", (List<TemplateDictionary>) map.get("region"));

    List<EcloudDictionaryAll> otherList = (List<EcloudDictionaryAll>) map.get("other");
    Map<String, List<EcloudDictionaryAll>> tempMap = new LinkedHashMap<>();

    for (EcloudDictionaryAll d : otherList) {

      String listType = d.getSheetName();
      String k;

      if (root.contains(listType)) { //全局字典项以类型为key值
        k = listType;
      } else {
        k = d.getLevel1() + "-" + listType; //sheet字典项以sheet-listType为key值
      }

      List<EcloudDictionaryAll> l;
      if (tempMap.containsKey(k)) {
        l = tempMap.get(k);
      } else {
        l = new ArrayList<>();
        tempMap.put(k, l);
      }

      l.add(d);
    }

    for (String k : tempMap.keySet()) {

      List<EcloudDictionaryAll> dicList = tempMap.get(k);

      String[] level;
      if (root.contains(k)) {
        level = new String[]{"1", "2", "3", "4"};
      } else {
        level = new String[]{"2", "3", "4"};
      }

      List<TemplateDictionary> l = buildNestLevel(dicList, level);
      dicMap.put(k, l);
    }

    return dicMap;

  }

  
  /**
   * 切层级   l1  l2  l3  散列
   * @param dictionaryList
   * @param level
   * @return
   */
  List<TemplateDictionary> buildNestLevel(List<EcloudDictionaryAll> dictionaryList, String[] level) {

    List<List<TemplateDictionary>> list = new ArrayList<>();

    //进行切分
    for (int i = 0; i < level.length; i++) {

      List<TemplateDictionary> l = new ArrayList<>();
      //字典类型去重
      Set<String> uniqueSet = new HashSet<>();

      for (EcloudDictionaryAll dic : dictionaryList) {

        if ("1".equals(level[i]) && null == dic.getLevel1()
            || "2".equals(level[i]) && null == dic.getLevel2()
            || "3".equals(level[i]) && null == dic.getLevel3()
            || "4".equals(level[i]) && null == dic.getLevel4()) {
          continue;
        }

        String parentPlusKey = "";//父节点累加key
        String plusKey = "";//当前节点key

        for (int j = 0; j <= i; j++) {

          String key = "";

          if ("1".equals(level[j])) {
            key = dic.getLevel1();
          } else if ("2".equals(level[j])) {
            key = dic.getLevel2();
          } else if ("3".equals(level[j])) {
            key = dic.getLevel3();
          } else if ("4".equals(level[j])) {
            key = dic.getLevel4();
          }

          //累加各级键值
          plusKey = plusKey + "-" + key;

          if (j != i) {
            //父节点键值
            parentPlusKey = parentPlusKey + "-" + key;
          }
        }

        //字典列表构造
        if (!uniqueSet.contains(plusKey)) {

          uniqueSet.add(plusKey);
          TemplateDictionary d = new TemplateDictionary();
          d.setValue(plusKey);
          d.setParentKey(parentPlusKey);

          if ("1".equals(level[i])) {
            d.setKey(dic.getLevel1());
          } else if ("2".equals(level[i])) {
            d.setKey(dic.getLevel2());
          } else if ("3".equals(level[i])) {
            d.setKey(dic.getLevel3());
          } else if ("4".equals(level[i])) {
            d.setKey(dic.getLevel4());
          }

          l.add(d);
        }
      }

      if (l.size() > 0) {
        list.add(l);
      }
    }

    for (int i = list.size() - 1; i > 0; i--) {

      List<TemplateDictionary> p = list.get(i - 1);//父列表
      List<TemplateDictionary> c = list.get(i);//子列表

      for (TemplateDictionary oc : c) {

        for (TemplateDictionary op : p) {

          if (oc.getParentKey().equals(op.getValue())) { //符合父子关系

            if (null == op.getChild()) {
              op.setChild(new ArrayList<>());
            }

            op.getChild().add(oc);
          }
        }
      }
    }

    return list.get(0);
  }


  private static List<EcloudDictionaryAll> test() {
    List<EcloudDictionaryAll> list = new ArrayList<>();
    EcloudDictionaryAll d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("原煤");
    d.setLevel3("煤粉炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("洗精煤");
    d.setLevel3("煤粉炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("型煤");
    d.setLevel3("自动炉排层燃炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("其它洗煤");
    d.setLevel3("自动炉排层燃炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("洗精煤");
    d.setLevel3("流化床炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equipfueltype");
    d.setLevel1("表p1011_机组信息表");
    d.setLevel2("洗精煤");
    d.setLevel3("流化床炉");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equiptype");
    d.setLevel1("表s6041_土壤扬尘信息表");
    d.setLevel2("粘土");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equiptype");
    d.setLevel1("表s6041_土壤扬尘信息表");
    d.setLevel2("砂土");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("equiptype");
    d.setLevel1("表s6041_土壤扬尘信息表");
    d.setLevel2("壤土");
    list.add(d);
    d = new EcloudDictionaryAll();
    d.setSheetName("sindustrytype");
    d.setLevel1("表s6041_土壤扬尘信息表");
    d.setLevel2("荒地");
    list.add(d);
    return list;
  }

  public static void main(String[] args) {
    EcloudDictionaryAdapter ad = new EcloudDictionaryAdapter();
    Map<String, Object> map = new HashMap<>();
    map.put("other", test());
    ad.setDic(map);
    ad.build();
  }
}
