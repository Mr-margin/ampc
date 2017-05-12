package ampc.com.gistone.util.excelUtil;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel实体类 
 */
@Deprecated
public class ExcelDto {

    /** 对应字段type */
    public static final String TYPE_TEMPLATE = "template"; //模板文件
    public static final String TYPE_VERIFY = "verify"; //校验规则文件
    public static final String TYPE_DATA = "data"; //上传数据文件
    public static final String TYPE_RESEARCH = "research"; //调查表模板文件

    /** 工作簿 */
    private Workbook workbook;
    /** 文件后缀 */
    private String ext;
    /** 文件用途 */
    private String type;
    /** 文件key值 */
    private String key;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
