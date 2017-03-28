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
public class QueueDataEmis {
	// "生成网格排放所需要的清单ID",
	private String sourceid;
	//计算方式 cache或者server
	private String calctype;
	//"PS排放系数路径",
	private String psal;
	//"SS排放系数路径",
	private String ssal;
	//"/work/b/lixin_meic/hebei/meic-city.conf"
	private String meiccityconfig;
	public String getSourceid() {
		return sourceid;
	}
	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}
	public String getCalctype() {
		return calctype;
	}
	public void setCalctype(String calctype) {
		this.calctype = calctype;
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
	
	
	
	
	

}
