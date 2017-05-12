package ampc.com.gistone.util.excelUtil;


/**
 * 字段映射实体类
 */
public class FieldMapping {

  private static final String TITLE_SEPERATOR = "-";

  /**
   * 主键id
   */
  private Integer id;
  /**
   * 文件
   */
  private String bigIndex;
  /**
   * sheet
   */
  private String smallIndex;
  /**
   * 真实字段
   */
  private String realField;
  /**
   * 展现字段
   */
  private String showField;
  /**
   * 字段类型
   */
  private String fieldType;
  /**
   * 字段操作
   */
  private String op;
  /**
   * 版本号
   */
  private String version;
  /**
   * cittle1
   */
  private String ctitle1;

  private String ctitle2;

  private String ctitle3;

  private String ctitle4;

  private String ctitle5;

  private String v1; //预留字段1

  /**
   * 数据库字段名称
   */
  private String columnName;

  public FieldMapping() {
  }

  public FieldMapping(String version, String bigIndex, String smallIndex, String showField, String realField, String columnName, String ctitle1, String fieldType, String op) {
    this.columnName = columnName;
    this.version = version;
    this.op = op;
    this.fieldType = fieldType;
    this.showField = showField;
    this.realField = realField;
    this.smallIndex = smallIndex;
    this.bigIndex = bigIndex;
    this.ctitle1 = ctitle1;
  }

  public FieldMapping(String version, String bigIndex, String smallIndex, String showField, String realField, String columnName, String ctitle1, String fieldType, String op, String ctitle2, String ctitle3, String ctitle4, String ctitle5) {
    this.columnName = columnName;
    this.version = version;
    this.op = op;
    this.fieldType = fieldType;
    this.showField = showField;
    this.realField = realField;
    this.smallIndex = smallIndex;
    this.bigIndex = bigIndex;
    this.ctitle1 = ctitle1;
    this.ctitle2 = ctitle2;
    this.ctitle3 = ctitle3;
    this.ctitle4 = ctitle4;
    this.ctitle5 = ctitle5;
  }

  public FieldMapping(String version, String bigIndex, String smallIndex, String showField, String realField, String columnName, String ctitle1, String fieldType, String op, String ctitle2, String ctitle3, String ctitle4, String ctitle5, String v1) {
    this.columnName = columnName;
    this.version = version;
    this.op = op;
    this.fieldType = fieldType;
    this.showField = showField;
    this.realField = realField;
    this.smallIndex = smallIndex;
    this.bigIndex = bigIndex;
    this.ctitle1 = ctitle1;
    this.ctitle2 = ctitle2;
    this.ctitle3 = ctitle3;
    this.ctitle4 = ctitle4;
    this.ctitle5 = ctitle5;
    this.v1 = v1;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getBigIndex() {
    return bigIndex;
  }

  public void setBigIndex(String bigIndex) {
    this.bigIndex = bigIndex;
  }

  public String getSmallIndex() {
    return smallIndex;
  }

  public void setSmallIndex(String smallIndex) {
    this.smallIndex = smallIndex;
  }

  public String getRealField() {
    return realField;
  }

  public void setRealField(String realField) {
    this.realField = realField;
  }

  public String getShowField() {
    return showField;
  }

  public void setShowField(String showField) {
    this.showField = showField;
  }

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }

  public String getOp() {
    return op;
  }

  public void setOp(String op) {
    this.op = op;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCtitle1() {
    return ctitle1;
  }

  public void setCtitle1(String ctitle1) {
    this.ctitle1 = ctitle1;
  }

  public String getColumnName() {
    if (null != this.columnName) {
      return this.columnName;
    } else {
      return StringUtil.camelToUnderline(this.realField);
    }
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getCtitle2() {
    return ctitle2;
  }

  public void setCtitle2(String ctitle2) {
    this.ctitle2 = ctitle2;
  }

  public String getCtitle3() {
    return ctitle3;
  }

  public void setCtitle3(String ctitle3) {
    this.ctitle3 = ctitle3;
  }

  public String getCtitle4() {
    return ctitle4;
  }

  public void setCtitle4(String ctitle4) {
    this.ctitle4 = ctitle4;
  }

  public String getCtitle5() {
    return ctitle5;
  }

  public void setCtitle5(String ctitle5) {
    this.ctitle5 = ctitle5;
  }

  public String getTitle() {
    String title = ctitle1;
    if (null != ctitle2 && !"".equals(ctitle2.trim())) title = title + TITLE_SEPERATOR + ctitle2;
    if (null != ctitle3 && !"".equals(ctitle3.trim())) title = title + TITLE_SEPERATOR + ctitle3;
    if (null != ctitle4 && !"".equals(ctitle4.trim())) title = title + TITLE_SEPERATOR + ctitle4;
    //if (null != ctitle5 && !"".equals(ctitle5.trim())) title = title + TITLE_SEPERATOR + ctitle5;
    return title;
  }

  public String getV1() {
    return v1;
  }

  public void setV1(String v1) {
    this.v1 = v1;
  }
}
