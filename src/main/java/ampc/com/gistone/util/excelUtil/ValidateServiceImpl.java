package ampc.com.gistone.util.excelUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 验证规则实现
 */
@Service
@Transactional
public class ValidateServiceImpl {

 

 /**
  * 校验单元格
  * @param name  字段名	
  * @param rule   校验规则
  * @param value 字段值
  * @param oneError  是否一个错误就返回
  * @return
  */
  private String validate(String name, String rule, String value, boolean oneError) {

    String error = null;

    if (rule.contains("notnull")) {
      if (null == value || "".equals(value.trim())) {
        error = (null == error) ? "不能为空" : error + ",并且不能为空";
        return error;
      }
    } else {
      if (null == value || "".equals(value.trim())) {
        return error;
      }
    }

    if (rule.contains("isfloat")) {
      try {
        Double.parseDouble(value);
      } catch (Exception e) {
        error = (null == error) ? "必须是浮点型" : error + ",并且必须是浮点型";
        if (oneError) return error;
      }
    } else if (rule.contains("isinteger")) {
      try {
        Integer.parseInt(value);
      } catch (Exception e) {
        error = (null == error) ? "必须是整型" : error + ",并且必须是整型";
        if (oneError) return error;
      }
    } else if (rule.contains("isdate")) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        sdf.parse(value);
      } catch (Exception e) {
        error = (null == error) ? "必须是日期格式且以'-'分割" : error + ",并且必须是日期格式且以'-'分割";
        if (oneError) return error;
      }
    }

    if (rule.contains("~")) {
      String[] split = rule.split(",");
      for (String s : split) {
        if (s.contains("~")) {
          rule = s;
          break;
        }
      }
      String[] arr = rule.split("~");
             /*
                *   1~5：这种情况 arr.length=2   arr[0]=1,arr[1]=5
                *   ~5:  这种情况 arr.length=2   arr[0]="",arr[1]=5
                *   1~:  这种情况 arr.length=1   arr[0]=1
                * */
      if (arr.length > 1) {
        if ("".equals(arr[0]) || arr[0] == null) {
          try {
            double num = Double.parseDouble(value);
            if (num > Double.parseDouble(arr[1])) {
              error = (null == error) ? "必须小于" + arr[1] : error + ",并且必须小于" + arr[1];
              if (oneError) return error;
            }
          } catch (NumberFormatException e) {
            error = (null == error) ? "必须小于" + arr[1] : error + ",并且必须小于" + arr[1];
            if (oneError) return error;
          }
        } else {
          try {
            double num = Double.parseDouble(value);
            if (num < Double.parseDouble(arr[0]) || num > Double.parseDouble(arr[1])) {
              error = (null == error) ? "必须在" + rule + "之间" : error + ",并且必须在" + rule + "之间";
              if (oneError) return error;
            }
          } catch (NumberFormatException e) {
            error = (null == error) ? "必须在" + rule + "之间" : error + ",并且必须在" + rule + "之间";
            if (oneError) return error;
          }
        }
      } else {
        try {
          double num = Double.parseDouble(value.trim());
          if (num < Double.parseDouble(arr[0])) {
            error = (null == error) ? "必须大于" + arr[0] : error + ",并且必须大于" + arr[0];
            if (oneError) return error;
          }
        } catch (NumberFormatException e) {
          error = (null == error) ? "必须大于" + arr[0] : error + ",并且必须大于" + arr[0];
          if (oneError) return error;
        }
      }
    }

    //TODO validators
    if ("tel".equals(name)) {
      if (!StringUtil.isPhone(value) && !StringUtil.isMobile(value)) {
        error = (null == error) ? "要符合电话号码或手机号码格式" : error + ",并且要符合电话号码或手机号码格式";
        if (oneError) return error;
      }
    } else if ("mail".equals(name)) {
      if (!StringUtil.isEmail(value)) {
        error = (null == error) ? "要符合邮箱格式" : error + ",并且要符合邮箱格式";
        if (oneError) return error;
      }
    }

    return error;
  }

  /**
   * 校验一个单元格
   */
  public String validateColumn(String bigIndex, String smallIndex, String type, String value, boolean oneError) {
    return this.validate(type, "校验规则", value, oneError);
  }

  /**
   * 校验一行
   */
  public List<ItemError> validateRow(String smallIndex, List<ValidateExcel> validateExcelList, Map row, Map specify, boolean oneError) {

    List<ItemError> errorInfoList = new ArrayList<>();

    //基础类型校验
    for (ValidateExcel e : validateExcelList) { //遍历该sheet的每一个字段的校验规则
      String v = (null != row && row.containsKey(e.getHeadname()) && null != row.get(e.getHeadname()) && !"".equals(row.get(e.getHeadname()))) ? row.get(e.getHeadname()).toString() : null; //获取单元格的值 map不存在字段的key值和空串""和null都记录为null
      String error = this.validate(e.getHeadname(), e.getValidators(), v, oneError); //校验一个单元格
      if (null != error) {
        ItemError info = new ItemError();
        info.fileName = e.getValidatename();
        info.sheetName = e.getSheetname();
        info.columnName = e.getHeadname();


        info.columnValue = v;
        info.id = (null != row && row.containsKey("id")) ? row.get("id") : null;
        errorInfoList.add(info);
        if (oneError) return errorInfoList;
      }
    }

    //指定值校验（如省、市）
    if (null != row && null != specify && !specify.isEmpty()) {
      Set k = specify.keySet();
      for (Object e : k) {
        if (row.containsKey(e) && null != row.get(e) && !row.get(e).equals(specify.get(e))) {
          String error = "只能为" + specify.get(e);
          ItemError info = new ItemError();
          info.sheetName = smallIndex;
          info.columnName = e.toString();

          //填充中文名称 表头是英文 转换成中文显示
          //Map<String, FieldMapping> fieldMap = MappingHolder.selectMapBySmall(MappingHolder.TEMPLATE, "1.0", smallIndex);
          //info.message = (null != fieldMap && fieldMap.containsKey(e.toString()) && null != fieldMap.get(e.toString()).getTitle()) ? fieldMap.get(e.toString()).getTitle() + error : error;

          info.columnValue = row.get(e).toString();
          info.id = row.get("id");
          errorInfoList.add(info);
          if (oneError) return errorInfoList;
        }
      }
    }

    //字典下拉校验 假设没有此类下拉校验的需求 这个地方一下的内容可以删除掉 （注意）
   // Map<String, Map> opMap = MappingHolder.selectOp(MappingHolder.TEMPLATE, "1.0", smallIndex);
    //Map<String, FieldMapping> fieldMap = MappingHolder.selectMapBySmall(MappingHolder.TEMPLATE, "1.0", smallIndex);

//    if (null != row && null != opMap) {
//      Set<String> opKey = opMap.keySet();
//      for (String e : opKey) {
//        Object o = opMap.get(e);
//        if (o instanceof Map) {
//          Map levelMap = (Map) o;
//          Object[] a = levelMap.values().toArray();
//          String[] values = new String[a.length];
//          String[] types = new String[a.length];
//          for (int i = 0; i < a.length; i++) {
//            values[i] = (row.containsKey(a[i].toString()) && null != row.get(a[i].toString())) ? row.get(a[i].toString()).toString() : null;
//            types[i] = fieldMap.get(a[i]).getFieldType();
//          }
//          String key = DictionaryHolder.root.contains(e) ? e : smallIndex + "-" + e;
//          int i = DictionaryHolder.verify("1.0", key, values, types); //字典校验
//          if (i != values.length - 1) {
//            ItemError error = new ItemError();
//            error.columnName = a[i + 1].toString();
//            error.columnValue = values[i + 1];
//
//            if (i != -1) { //二级或者二级之后错误
//              String parent = fieldMap.get(a[i]).getTitle();
//              String child = fieldMap.get(a[i + 1]).getTitle();
//              error.message = parent + "为" + values[i] + "的" + child + "没有" + values[i + 1] + "选项";
//            } else { //第一级菜单就错误了
//              String s = fieldMap.get(a[i + 1]).getTitle();
//              error.message = s + "没有" + values[i + 1] + "选项";
//            }
//
//            error.sheetName = smallIndex;
//            error.id = row.get("id");
//            errorInfoList.add(error);
//            if (oneError) return errorInfoList;
//          }
//        } else if (o instanceof List) { //没有级别的字典项
//          List<String> l = (List) o;
//          for (String s : l) {
//            if (row.containsKey(s)) {
//              Object val = row.get(s);
//              if (null != val) {
//                String[] values = new String[]{val.toString()};
//                String[] types = new String[]{fieldMap.get(s).getFieldType()};
//                int i = DictionaryHolder.verify("1.0", DictionaryHolder.root.contains(e) ? e : smallIndex + "-" + e, values, types);
//                if (i != values.length - 1) {
//                  ItemError error = new ItemError();
//                  error.columnName = s;
//                  error.columnValue = values[i + 1];
//                  error.message = fieldMap.get(s).getTitle() + "没有" + val.toString() + "选项";
//                  error.sheetName = smallIndex;
//                  error.id = row.get("id");
//                  errorInfoList.add(error);
//                  if (oneError) return errorInfoList;
//                }
//              }
//            }
//          }
//        }
//      }
   // }
    return errorInfoList;
  }

  
  /**
   * 校验文件是否合法
   */
  public List<ItemError> validateFile(String bigIndex, Map file, Map specify, boolean oneError) {
    //ValidateExcelExample example = new ValidateExcelExample();
    //example.or().andValidatenameEqualTo(bigIndex);
    //List<ValidateExcel> validateExcelList = validateExcelMapper.selectByExample(example);
	//验证数据集合
	List<ValidateExcel> validateExcelList = null;
    List<ItemError> infoList = new ArrayList<>();
    Set k = file.keySet();
    for (Object e : k) { //遍历每一个sheet数据 e是sheet名称
      List sl = (List) file.get(e); //获取sheet列表
      List<ValidateExcel> v = validateExcelList.stream().filter(t -> t.getSheetname().equals(e)).collect(Collectors.toList());//获取这个sheet的校验规则
      List<ItemError> es = this.validateRows(e.toString(), v, sl, specify, oneError);
      infoList.addAll(es);
      if (!infoList.isEmpty() && oneError) return infoList;
    }
    return infoList;
  }

  
  /**
   * 校验多行
   */
  public List<ItemError> validateRows(String smallIndex, List<ValidateExcel> validateExcelList, List<Map> list, Map sepcify, boolean oneError) {
    List<ItemError> infoList = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      Map e = list.get(i); //取出一条记录
      List<ValidateExcel> l = validateExcelList.stream().filter(e1 -> e1.getSheetname().equals(smallIndex)).collect(Collectors.toList()); //获取这个sheet的校验规则
      List<ItemError> info = this.validateRow(smallIndex, l, e, sepcify, oneError); //校验一行记录
      for (ItemError er : info)
        er.rowNumber = i + 1;
      infoList.addAll(info);
      if (!infoList.isEmpty() && oneError) return infoList;
    }
    return infoList;
  }

 
  /**
   * 校验表头缺失
   */
  public Map validateHeader(String bigIndex, Map headerMap) {
    //查询出validate对应所有的校验规则
//  ValidateExcelExample example = new ValidateExcelExample();
//  example.or().andValidatenameEqualTo(bigIndex);
//  List<ValidateExcel> vl = validateExcelMapper.selectByExample(example);
	List<ValidateExcel> vl=null;
    Map sMap = new LinkedHashMap<>();
    vl.forEach(e -> {
      List sl = new ArrayList<>();
      if (sMap.containsKey(e.getSheetname())) {
        sl = (List) sMap.get(e.getSheetname());
      } else {
        sl = new ArrayList();
        sMap.put(e.getSheetname(), sl);
      }
      sl.add(e);
    });
    //构建缺失表头的map
    Map loseMap = new LinkedHashMap<>();
    Set s = headerMap.keySet();
    for (Object k : s) {
      Map h = (Map) headerMap.get(k);
      Set hs = h.keySet();
      List<ValidateExcel> sl = (List) sMap.get(k);
      List loseList = new ArrayList<>();
      sl.forEach(e -> {
        if (!hs.contains(e.getHeadname())) loseList.add(e.getHeadname());
      });
      if (!loseList.isEmpty()) loseMap.put(k, loseList);
    }
    return loseMap;
  }
}
