package ampc.com.gistone.util.excelUtil;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel读取结果类
 * Created by chf on 2016/12/16.
 */
@Deprecated
public class ExcelResult {

    /** 对应字段type */
    public static final String TYPE_TEMPLATE = "template"; //末班文件
    public static final String TYPE_VERIFY = "verify"; //校验文件
    public static final String TYPE_DATA = "data"; //数据文件
    public static final String TYPE_RESEARCH = "research"; //调查表文件

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
