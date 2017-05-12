package ampc.com.gistone.util.checkExcel;

/**
 * 验证Excel的实体类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年5月12日
 */
public class ValidateExcel {
    private Integer id;

    private String sheetname;

    private String headname;

    private String validators;

    private String validatename;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSheetname() {
        return sheetname;
    }

    public void setSheetname(String sheetname) {
        this.sheetname = sheetname == null ? null : sheetname.trim();
    }

    public String getHeadname() {
        return headname;
    }

    public void setHeadname(String headname) {
        this.headname = headname == null ? null : headname.trim();
    }

    public String getValidators() {
        return validators;
    }

    public void setValidators(String validators) {
        this.validators = validators == null ? null : validators.trim();
    }

    public String getValidatename() {
        return validatename;
    }

    public void setValidatename(String validatename) {
        this.validatename = validatename == null ? null : validatename.trim();
    }
}