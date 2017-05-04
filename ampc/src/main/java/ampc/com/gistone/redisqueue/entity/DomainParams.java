/**  
 * @Title: DomainParams.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午7:01:26
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

/**  
 * @Title: DomainParams.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午7:01:26
 * @version 1.0
 */
public class DomainParams {
	
	private DomainDataCommon common; //公共参数
	private DomainDataCmaq cmaq; //cmaq 参数
	private DomainDataWrf wrf; // wrf 参数
	private DomainDataMcip mcip; // mcip 参数
	private DomainDataMeic meic;
	
	private MeganParams megan;
	

	public DomainDataCommon getCommon() {
		return common;
	}

	public void setCommon(DomainDataCommon common) {
		this.common = common;
	}

	public DomainDataCmaq getCmaq() {
		return cmaq;
	}

	public void setCmaq(DomainDataCmaq cmaq) {
		this.cmaq = cmaq;
	}

	public DomainDataWrf getWrf() {
		return wrf;
	}

	public void setWrf(DomainDataWrf wrf) {
		this.wrf = wrf;
	}

	public DomainDataMcip getMcip() {
		return mcip;
	}

	public void setMcip(DomainDataMcip mcip) {
		this.mcip = mcip;
	}

	public DomainDataMeic getMeic() {
		return meic;
	}

	public void setMeic(DomainDataMeic meic) {
		this.meic = meic;
	}

	public MeganParams getMegan() {
		return megan;
	}

	public void setMegan(MeganParams megan) {
		this.megan = megan;
	}
	
	
	

}
