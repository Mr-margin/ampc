/**  
 * @Title: DomainDataCmaq.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:16:40
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

/**  
 * @Title: DomainDataCmaq.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:16:40
 * @version 1.0
 */
public class DomainDataCmaq {
	//cmaq的版本号
	private  String version;
	//x向格数
	private  String nx;
	//y向格数
	private  String ny;
	//网格区域左下角点坐标x
	private  String xorig;
	//网格区域左下角点坐标y
	private  String yorig;
	//气溶胶机制
	private String mech_aero;
	//气相化学机制
	private String mech_gas;
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the nx
	 */
	public String getNx() {
		return nx;
	}
	/**
	 * @param nx the nx to set
	 */
	public void setNx(String nx) {
		this.nx = nx;
	}
	/**
	 * @return the ny
	 */
	public String getNy() {
		return ny;
	}
	/**
	 * @param ny the ny to set
	 */
	public void setNy(String ny) {
		this.ny = ny;
	}
	/**
	 * @return the xorig
	 */
	public String getXorig() {
		return xorig;
	}
	/**
	 * @param xorig the xorig to set
	 */
	public void setXorig(String xorig) {
		this.xorig = xorig;
	}
	/**
	 * @return the yorig
	 */
	public String getYorig() {
		return yorig;
	}
	/**
	 * @param yorig the yorig to set
	 */
	public void setYorig(String yorig) {
		this.yorig = yorig;
	}
	/**
	 * @return the mech_aero
	 */
	public String getMech_aero() {
		return mech_aero;
	}
	/**
	 * @param mech_aero the mech_aero to set
	 */
	public void setMech_aero(String mech_aero) {
		this.mech_aero = mech_aero;
	}
	/**
	 * @return the mech_gas
	 */
	public String getMech_gas() {
		return mech_gas;
	}
	/**
	 * @param mech_gas the mech_gas to set
	 */
	public void setMech_gas(String mech_gas) {
		this.mech_gas = mech_gas;
	}
	
	
	
	

}
