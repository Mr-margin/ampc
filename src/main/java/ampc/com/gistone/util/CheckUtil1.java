package ampc.com.gistone.util;

import java.util.List;

import ampc.com.gistone.database.model.TMeasureExcel;

/**
 * 验证帮助类
 * l4s分段的条件类 主要来保存l4s每一个分段的 条件值
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月4日
 */
public class CheckUtil1 {
	//保存措施类
	private TMeasureExcel tMeasureExcel;
	//l4s[1]
	private List<CheckUtil> check1;
	//l4s[2]
	private List<CheckUtil> check2;
	//l4s[3]
	private List<CheckUtil> check3;
	//l4s[4]
	private List<CheckUtil> check4;
	public TMeasureExcel gettMeasureExcel() {
		return tMeasureExcel;
	}
	public void settMeasureExcel(TMeasureExcel tMeasureExcel) {
		this.tMeasureExcel = tMeasureExcel;
	}
	public List<CheckUtil> getCheck1() {
		return check1;
	}
	public void setCheck1(List<CheckUtil> check1) {
		this.check1 = check1;
	}
	public List<CheckUtil> getCheck2() {
		return check2;
	}
	public void setCheck2(List<CheckUtil> check2) {
		this.check2 = check2;
	}
	public List<CheckUtil> getCheck3() {
		return check3;
	}
	public void setCheck3(List<CheckUtil> check3) {
		this.check3 = check3;
	}
	public List<CheckUtil> getCheck4() {
		return check4;
	}
	public void setCheck4(List<CheckUtil> check4) {
		this.check4 = check4;
	}
	
}
