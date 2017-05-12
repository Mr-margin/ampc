package ampc.com.gistone.util.excelUtil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;

/**
 * 构造Excel下拉列表
 */
public class Core {

  /**
   * 递归构建字典列表
   * 根据下拉列表的值编写下拉列表规则
   * @param result			规则列表
   * @param plusKey        累加key值
   * @param dictionaryList 字典嵌套列表
   */
  public static void makeDictionary(List<List<Object>> result, String plusKey, List<TemplateDictionary> dictionaryList) {

    if (null != dictionaryList && !dictionaryList.isEmpty()) {

      List<Object> r = new ArrayList<>();
      r.add(plusKey);
      int l = dictionaryList.size();
      r.add(l);

      for (int i = 0; i < l; i++) {
        TemplateDictionary d = dictionaryList.get(i);
        r.add(d.getKey());
      }
      result.add(r);

      for (int i = 0; i < l; i++) {
        TemplateDictionary d = dictionaryList.get(i);
        String nPlusKey = plusKey + "-" + d.getKey();
        makeDictionary(result, nPlusKey, d.getChild());
      }
    }
  }

  

  /**
   * Excel下拉逻辑-5.构造级联公式
   * 级联
   *
   * @param sheet
   * @param dicName 字典页名称
   * @param header  表头
   * @param type    字典类型
   */
  public static void cascade(Sheet sheet, String dicName, String[] header, String type) {
    Row headerRow = sheet.getRow(0);
    int[] real = new int[header.length];//表头对应列数
    int l = headerRow.getLastCellNum();
    for (int i = 0; i < header.length; i++) {

      for (int j = 0; j < l; j++) {

        Cell cell = headerRow.getCell(j);
        String v = ExcelTool.singleton.getCellValue(cell);

        if (v.equals(header[i])) {
          real[i] = j;
        }
      }
    }

    //级联公式构造 构造Excel单元格的下拉公式  通过公式可以把定义的规则 把数据填充到Excel单元格
    String formula = "=IF(ISNUMBER(MATCH(KEY,DICSHEET!A:A,0)),OFFSET(DICSHEET!$A$1,MATCH(KEY,DICSHEET!A:A,0)-1,2,1,INDIRECT(\"DICSHEET!B\"&MATCH(KEY,DICSHEET!A:A,0))),\"\")";
    formula = formula.replaceAll("DICSHEET", dicName);
    String[] fA = new String[real.length];

    for (int i = 0; i < real.length; i++) {
      String key = "\"" + type + "\"";
      for (int j = 0; j < i; j++) {
        key = key + "&\"-\"&" + sheet.getSheetName() + "!$" + columnChar(real[j]) + "$ROWNUMBER";
      }
      String[] a = formula.split("KEY");
      StringBuilder b = new StringBuilder();

      for (int j = 0; j < a.length; j++) {

        b.append(a[j]);

        if (j != a.length - 1) {
          b.append(key);
        }
      }
      fA[i] = b.toString();
    }

    //TODO 默认构造1000列
    for (int i = 8; i < 1000; i++) {
      for (int j = 0; j < fA.length; j++) {
        String f = fA[j].replaceAll("ROWNUMBER", String.valueOf(i));
        //System.out.println(f);
        setDataValidation(
            (XSSFSheet) sheet, f, i - 1, i - 1, real[j], real[j]);
      }
    }
  }

  /**
   * Excel下拉逻辑-2.切分字典层级  
   * 将ecloud_dictionary_all切分成不同的层级
   * 将级联下拉列表转换成java中的实体类表达形式 
   * @param sheetName      excel的sheet名称
   * @param rule           切分规则排列
   * @param dictionaryList 字典列表
   * @return
   */
  public static List<List<TemplateDictionary>> splitLevel(String sheetName, String[] rule, List<EcloudDictionaryAll> dictionaryList) {
    List<List<TemplateDictionary>> list = new ArrayList<>();
    String type = rule[0].split("_")[0].split("-")[1];
    String[] level = new String[rule.length];

    //将规则转换层级 1,2,3,4...
    for (int i = 0; i < rule.length; i++) {
      String r = rule[i];
      if (r.contains("_")) {
        level[i] = r.split("_")[1];
      } else {
        level[i] = "1";
        if (!EcloudDictionaryAdapter.root.contains(type)) { //非全局
          level[i] = "2";
        }
      }
    }

    //进行切分
    for (int i = 0; i < level.length; i++) {

      List<TemplateDictionary> l = new ArrayList<>();
      //字典类型去重
      Set<String> uniqueSet = new HashSet<>();

      for (EcloudDictionaryAll dic : dictionaryList) {

        //第一级不需要进行level1匹配 第二三级需要进行level1匹配
        //TODO 进行三级过滤
        if (dic.getSheetName().equals(type)
            && ("region".equals(type) || "1".equals(level[i]) || (!"1".equals(level[i]) && dic.getLevel1().equals(sheetName)))) {

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
              key = dic.getLevel3();
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
      }

      list.add(l);
    }
    return list;
  }

  /**
   * 构造字典
   * @param wb
   * @param sheetMap 字典规则
   * @param dic
   * @throws Exception
   */
  public static void buildBook(Workbook wb, Map<String, Map<String, List<String[]>>> sheetMap, Dic dic) throws Exception {

    int l = wb.getNumberOfSheets();

    List<List<Object>> dicList = new ArrayList<>();
    for (int i = 0; i < l; i++) {
      Sheet sheet = wb.getSheetAt(i);

      Map<String, List<String[]>> dropMap = sheetMap.get(sheet.getSheetName());
      if (null == dropMap) {
        continue;
      }

      for (String s : dropMap.keySet()) {
        List<String[]> list = dropMap.get(s);
        String[] levels = new String[list.size()];
        String[] headers = new String[list.size()];

        for (int j = 0; j < list.size(); j++) {
          levels[j] = list.get(j)[0];
          headers[j] = list.get(j)[1];
        }

        List<TemplateDictionary> dictionaryList;

        if ("region".equals(s)) {
          List<List<TemplateDictionary>> temp = splitLevel(sheet.getSheetName(), levels, dic.getList());
          dictionaryList = coverLevel(temp);
        } else {
          List<List<TemplateDictionary>> temp = splitLevel(sheet.getSheetName(), levels, dic.getList());
          dictionaryList = coverLevel(temp);
        }

        makeDictionary(dicList, sheet.getSheetName() + "-" + s, dictionaryList);
       
        Core.cascade(sheet, "dictionary", headers, sheet.getSheetName() + "-" + s);//级联公式
      }
    }

    //创建字典sheet页
    dictionarySheet(wb, "dictionary", dicList);
    //创建级联sheet页
    //dictionarySheet(wb, "cascade", casList);
  }

  /**
   * 将字典项写入到sheet页
   * @param wb
   * @param sheetName
   * @param result
   */
  public static void dictionarySheet(Workbook wb, String sheetName, List<List<Object>> result) {
    Sheet sheet = wb.getSheet(sheetName);
    if (null == sheet) {
      sheet = wb.createSheet(sheetName);
    }

    for (List<Object> l : result) {
      int size = l.size();
      int rowNum = sheet.getPhysicalNumberOfRows();
      Row row = sheet.createRow(rowNum);
      for (int i = 0; i < size; i++) {
        Cell cell = row.createCell(i);
        cell.setCellValue(String.valueOf(l.get(i)));
      }
    }

    //设置sheet隐藏
  }

  /**
   * Excel下拉逻辑-3.合并字典
   * 逐级覆盖生成嵌套字典
   * 生成下拉列表的java实现表达方式
   * @param list
   * @return
   */
  public static List<TemplateDictionary> coverLevel(List<List<TemplateDictionary>> list) {

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

  public static void main(String[] args) throws Exception {
//        List<List<TDictionary>> levelList = splitLevel("表p1011_机组信息表", new String[]{"list-equipfueltype_3", "list-equipfueltype_2"}, Dic.build());
//        List<TDictionary> list = coverLevel(levelList);
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("dictionary");
//        List<List<TDictionary>> regionList = new TDictionarySheet().getDitionary1("region");
//        List<TDictionary> regions = coverLevel(regionList);
//        List<List<Object>> result = new ArrayList<>();
//        dictionary(result, "region", regions);
//        dictionary(result, "equipfueltype", list);
//        for (List<Object> l : result) {
//            int size = l.size();
//            int rowNum = sheet.getPhysicalNumberOfRows();
//            Row row = sheet.createRow(rowNum);
//            for (int i = 0; i < size; i++) {
//                Cell cell = row.createCell(i);
//                cell.setCellValue(String.valueOf(l.get(i)));
//            }
//        }
//        //cascade(mainSheet, "dictionary", new String[]{"c", "b", "a"}, "region");
//        OutputStream os = new FileOutputStream("E:/test.xlsx");
//        workbook.write(os);
    System.out.println("1SHEET1".split("SHEET").length);
  }

  //将列传换成英文字母   相当于Excel中的列名 如:1-A 2-B
  public static String columnChar(int num) {
    int big = num / 26;
    String sBig = "";
    if (big != 0) {
      sBig = String.valueOf(((char) (big + 64)));
    }
    int small = num % 26;
    String sSmall = String.valueOf(((char) (small + 65)));
    return sBig + sSmall;
  }

  /**
   * 给指定Excel区间范围设置下拉公式
   * 将公式写到单元格
   * @param sheet sheet页
   * @param name   公式
   * @param firstRow  设置的区间
   * @param endRow
   * @param firstCol
   * @param endCol
   */
  public static void setDataValidation(XSSFSheet sheet, String name, int firstRow, int endRow, int firstCol, int endCol) {
    XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
    DataValidationConstraint constraint = helper.createFormulaListConstraint(name);
    CellRangeAddressList regions = new CellRangeAddressList(firstRow,
        endRow, firstCol, endCol);
    sheet.addValidationData(helper.createValidation(constraint, regions));
  }
}
