package ampc.com.gistone.entity;

/**
 * 验证帮助类
 * 验证L4s符号帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
public class CheckUtil {
	//判断方法  主要分为  or:两个数有一个==就为true;between;规定数字在两个数之间;and规定数字和第一个数字==为true
	private String method; 
	//第一个值
	private int num1;
	//第二个值
	private int num2;
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getNum1() {
		return num1;
	}
	public void setNum1(int num1) {
		this.num1 = num1;
	}
	public int getNum2() {
		return num2;
	}
	public void setNum2(int num2) {
		this.num2 = num2;
	}
	
}
