package ampc.com.gistone.util.excelUtil;


import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel数据存库 或生成文件的实体类
 */
@Deprecated
public class GenerateTemplateReturnDto {

    /** 生成字段列表 */
    private List<InputField> fields;

    /** 下拉字典map key：sheet value：下拉参数 */
    //外层key值是Excel中的sheet名称 内层key值是下拉菜单类型（如region行政区划下拉）
    //value值为级联菜单或者单级菜单所应用到的Etitle列表
    private Map<String, Map<String, List<String[]>>> dropListMap = new HashMap<>();

  

    /** 文件工作簿对应map key:文件地址 workbook工作簿 暂时保存模板excel和校验excel */
    //
    Map<String, ExcelDto> bookMap = new HashMap<>();

    public Workbook investBook;

    public List<InputField> getFields() {
        return fields;
    }

    public void setFields(List<InputField> fields) {
        this.fields = fields;
    }

    public Map<String, ExcelDto> getBookMap() {
        return bookMap;
    }

    public void setBookMap(Map<String, ExcelDto> bookMap) {
        this.bookMap = bookMap;
    }

   
    public Map<String, Map<String, List<String[]>>> getDropListMap() {
        return dropListMap;
    }

    public void setDropListMap(Map<String, Map<String, List<String[]>>> dropListMap) {
        this.dropListMap = dropListMap;
    }
}
