package ampc.com.gistone.util;

public class ColorUtil {
	private String colorCode;
	private String colorName;
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	
	public ColorUtil(String colorCode,String colorName){
		this.colorCode=colorCode;
		this.colorName=colorName;
	}
}
