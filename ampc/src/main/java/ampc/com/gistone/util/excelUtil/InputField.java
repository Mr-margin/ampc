package ampc.com.gistone.util.excelUtil;

/**
 * 存放Excel实体类  
 */
@Deprecated
public class InputField {

    /** 文件 */
    private String bigIndex;
    /** sheet */
    private String smallIndex;
    /** 字段名称 */
    private String name;
    /** 字段类型 */
    private String type;
    /** 字段操作类型 如list-region_1 */
    private String op;
    /** CTitle1 */
    private String ctitle1;

    private String ctitle2;

    private String ctitle3;

    private String ctitle4;

    private String ctitle5;

    private String v1;

    public InputField() {
    }

    public InputField(String bigIndex, String smallIndex, String name, String ctitle1, String type, String op, String v1) {
        this.bigIndex = bigIndex;
        this.smallIndex = smallIndex;
        this.name = name;
        this.type = type;
        this.op = op;
        this.ctitle1 = ctitle1;
        this.v1 = v1;
    }


    public InputField(String bigIndex, String smallIndex, String name, String ctitle1, String type, String op, String c2, String c3, String c4, String c5, String v1) {
        this.bigIndex = bigIndex;
        this.smallIndex = smallIndex;
        this.name = name;
        this.type = type;
        this.op = op;
        this.ctitle1 = ctitle1;
        this.ctitle2 = c2;
        this.ctitle3 = c3;
        this.ctitle4 = c4;
        this.ctitle5 = c5;
        this.v1 = v1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSmallIndex() {
        return smallIndex;
    }

    public void setSmallIndex(String smallIndex) {
        this.smallIndex = smallIndex;
    }

    public String getBigIndex() {
        return bigIndex;
    }

    public void setBigIndex(String bigIndex) {
        this.bigIndex = bigIndex;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getCtitle1() {
        return ctitle1;
    }

    public void setCtitle1(String ctitle1) {
        this.ctitle1 = ctitle1;
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

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }
}
