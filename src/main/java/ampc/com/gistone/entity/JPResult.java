package ampc.com.gistone.entity;

import java.util.List;
/**
 * 减排计算最后结果类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月7日
 */
public class JPResult {
	private List<OPS> ops;
	private String smallIndex;
	private String type;
	public List<OPS> getOps() {
		return ops;
	}
	public void setOps(List<OPS> ops) {
		this.ops = ops;
	}
	public String getSmallIndex() {
		return smallIndex;
	}
	public void setSmallIndex(String smallIndex) {
		this.smallIndex = smallIndex;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
