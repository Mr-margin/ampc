/**  
 * @Title: DomainDataMcip.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午6:46:59
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**  
 * @Title: DomainDataMcip.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午6:46:59
 * @version 1.0
 */
public class DomainDataMcip {
	//x,y向裁剪网格数  3,
	private int btrim;
	//CTM-z方向的垂直层数 15,
	private int ctm_vert;
	// 同CTM一致的垂直分层    "1.000, 0.995, 0.988, 0.980, 0.970, 0.956, 0.938, 0.893, 0.839, 0.777, 0.702, 0.582, 0.400, 0.200, 0.000"
	@JsonProperty("CTMLAYS")
	private String ctmlays;
	
	
	public int getBtrim() {
		return btrim;
	}
	public void setBtrim(int btrim) {
		this.btrim = btrim;
	}
	public int getCtm_vert() {
		return ctm_vert;
	}
	public void setCtm_vert(int ctm_vert) {
		this.ctm_vert = ctm_vert;
	}
	public String getCtmlays() {
		return ctmlays;
	}
	public void setCtmlays(String ctmlays) {
		this.ctmlays = ctmlays;
	}
	
	
	 
	
	
	
	

}
