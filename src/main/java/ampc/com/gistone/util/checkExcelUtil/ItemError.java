package ampc.com.gistone.util.checkExcelUtil;
/**
 * Created by chf on 2017/3/30.
 * Item出错信息
 */
public class ItemError {

  public String fileName; //文件名

  public String sheetName; //sheet名

  public String columnName; //表头名

  public Object id; //转换成Excel中的列名 方便用户对应错误列

  public String message; //出错信息

  public Object columnValue; //校验导致出错的值

  public int rowNumber; //行数 方便对用户进行出错信息的提示

  /**
   * 1.上传的Excel文件不合法
   * 2.上传的Excel缺少sheet页
   * 3.sheet页工作簿的名称长度不合法  默认要求不可超过10个字符
   * 4.表头不匹配
   * 5.单元格数据违反了非空验证
   * 6.单元格数据违反了数据类型要求验证
   * 7.单元格数据违反了取值范围要求验证
   */
  public String errorType;//错误类型
  

  public ItemError() {
  }

  public ItemError(String sheetName, String columnName, String message, int rowNumber, String errorType) {
    this.sheetName = sheetName;
    this.columnName = columnName;
    this.message = message;
    this.rowNumber = rowNumber;
    this.errorType = errorType;
  }

  public ItemError(String fileName, String sheetName, String columnName, Object id, String message, Object columnValue, int rowNumber, String errorType) {
    this.fileName = fileName;
    this.sheetName = sheetName;
    this.columnName = columnName;
    this.id = id;
    this.message = message;
    this.columnValue = columnValue;
    this.rowNumber = rowNumber;
    this.errorType = errorType;
  }
}
