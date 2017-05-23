package ampc.com.gistone.util.checkExcelUtil;

/**
 * 生成清单时使用的帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年5月20日
 */
public class CheckNativeUtil {
	//sheet页
	private String sheetNum;
	//列名
	private String title;
	//取值范围
	private String valueRange;
	//数据类型
	private String dataType;
	//是否非空
	private String isNotNull;
	public String getSheetNum() {
		return sheetNum;
	}
	public void setSheetNum(String sheetNum) {
		this.sheetNum = sheetNum;
	}
	public String getValueRange() {
		return valueRange;
	}
	public void setValueRange(String valueRange) {
		this.valueRange = valueRange;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getIsNotNull() {
		return isNotNull;
	}
	public void setIsNotNull(String isNotNull) {
		this.isNotNull = isNotNull;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
