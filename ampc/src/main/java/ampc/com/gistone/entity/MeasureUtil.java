package ampc.com.gistone.entity;

/**
 * 进入预案措施中子措施的页面   要规定用户的取值范围 和要显示的中英文名称
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月17日
 */
public class MeasureUtil {
	//中文名称
	private String namech;
	//英文名称
	private String nameen;
	//值
	private Object value;
	//最小值
	private Object minValue;
	//最大值
	private Object maxValue;
	 
	public Object getMinValue() {
		return minValue;
	}
	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}
	public Object getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}
	 
	public String getNamech() {
		return namech;
	}
	public void setNamech(String namech) {
		this.namech = namech;
	}
	public String getNameen() {
		return nameen;
	}
	public void setNameen(String nameen) {
		this.nameen = nameen;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
