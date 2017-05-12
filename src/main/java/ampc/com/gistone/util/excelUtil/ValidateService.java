package ampc.com.gistone.util.excelUtil;


import java.util.List;
import java.util.Map;

/**
 * 验证规则的接口
 */
public interface ValidateService {
  /**
   * 生成校验规则
   *
   * @param verifyExcelPath
   * @param verifyExcelName
   * @return
   */
  VerifyResult createVerifyFile(String verifyExcelPath, String verifyExcelName);

  /**
   * 根据校验规则进行校验
   *
   * @param verifyFilePath
   * @param verifyRuleName
   * @param errorPath
   * @return
   */
  VerifyResult validateFile(String verifyFilePath, String verifyRuleName, String errorPath, int startNum);

  /**
   * 校验一列数据
   *
   * @param bigIndex
   * @param smallIndex
   * @param type
   * @param value
   * @return
   */
  String validateColumn(String bigIndex, String smallIndex, String type, String value, boolean oneError);

  /**
   * 校验一行数据
   *
   * @param row
   * @return
   */
  List<ItemError> validateRow(String smallIndex, List<ValidateExcel> list, Map row, Map sepcify, boolean oneError);

  /**
   * 校验一个列表数据
   *
   * @param list
   * @return
   */
  List<ItemError> validateRows(String smallIndex, List<ValidateExcel> ve, List<Map> list, Map specify, boolean oneError);

  /**
   * 校验一个文件的数据
   *
   * @param bigIndex
   * @param file
   * @return
   */
  List<ItemError> validateFile(String bigIndex, Map file, Map sepcify, boolean oneError);

  /**
   * 逻辑校验
   *
   * @param bigIndex
   * @param file
   * @param oneError
   * @return
   */
  List<ItemError> validateLogic(String bigIndex, Map file, boolean oneError);

  /**
   * 校验表头
   *
   * @param bigIndex
   * @param headerMap
   * @return
   */
  Map validateHeader(String bigIndex, Map headerMap);

  VerifyResult validateImportDic(String saveFilePath, String bigIndex, String errorFilePath, String taskId);
}
