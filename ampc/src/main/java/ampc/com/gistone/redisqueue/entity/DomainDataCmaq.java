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
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNx() {
		return nx;
	}
	public void setNx(String nx) {
		this.nx = nx;
	}
	public String getNy() {
		return ny;
	}
	public void setNy(String ny) {
		this.ny = ny;
	}
	public String getXorig() {
		return xorig;
	}
	public void setXorig(String xorig) {
		this.xorig = xorig;
	}
	public String getYorig() {
		return yorig;
	}
	public void setYorig(String yorig) {
		this.yorig = yorig;
	}
	
	

}
