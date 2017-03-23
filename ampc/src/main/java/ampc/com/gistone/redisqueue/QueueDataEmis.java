/**  
 * @Title: QueueDataEmis.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月17日 下午1:57:31
 * @version 
 */
package ampc.com.gistone.redisqueue;

import org.springframework.stereotype.Component;

/**  
 * @Title: QueueDataEmis.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO 
 * @author yanglei
 * @date 2017年3月17日 下午1:57:31
 * @version 1.0
 */
@Component
public class QueueDataEmis {
	// "生成网格排放所需要的清单ID",
	private String sourceId;
	//计算方式 cache或者server
	private String calcType;
	//"PS排放系数路径",
	private String psal;
	//"SS排放系数路径",
	private String ssal;
	//"/work/b/lixin_meic/hebei/meic-city.conf"
	private String meiccityconfig;
	
	
	
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getCalcType() {
		return calcType;
	}
	public void setCalcType(String calcType) {
		this.calcType = calcType;
	}
	public String getPsal() {
		return psal;
	}
	public void setPsal(String psal) {
		this.psal = psal;
	}
	public String getSsal() {
		return ssal;
	}
	public void setSsal(String ssal) {
		this.ssal = ssal;
	}
	public String getMeiccityconfig() {
		return meiccityconfig;
	}
	public void setMeiccityconfig(String meiccityconfig) {
		this.meiccityconfig = meiccityconfig;
	}
	public QueueDataEmis() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QueueDataEmis(String sourceId, String calcType, String psal,
			String ssal, String meiccityconfig) {
		super();
		this.sourceId = sourceId;
		this.calcType = calcType;
		this.psal = psal;
		this.ssal = ssal;
		this.meiccityconfig = meiccityconfig;
	}
	
	

}
