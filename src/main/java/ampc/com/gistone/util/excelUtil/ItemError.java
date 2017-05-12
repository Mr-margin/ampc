package ampc.com.gistone.util.excelUtil;
/**
 * Created by chf on 2017/3/30.
 * Item出错信息
 */
public class ItemError {

  public String fileName; //文件名

  public String sheetName; //sheet名

  public String columnName; //字段名

  public Object id; //对应记录的索引id

  public String message; //出错信息

  public Object columnValue; //校验导致出错的值

  public int rowNumber; //行数 方便对用户进行出错信息的提示

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
