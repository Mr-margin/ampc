package ampc.com.gistone.util.checkExcel;

/**
 * 验证规则实体类
 */
public class ValidateModel {
    private Integer id;
    private String sheetName;
    private String headName;
    private String validators;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValidateName() {
        return validateName;
    }

    public void setValidateName(String validateName) {
        this.validateName = validateName;
    }

    private String validateName;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getValidators() {
        return validators;
    }

    public void setValidators(String validators) {
        this.validators = validators;
    }
}
