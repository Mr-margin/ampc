/**  
 * @Title: DomainDataCommon.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:11:48
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**  
 * @Title: DomainDataCommon.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:11:48
 * @version 1.0
 */
public class DomainDataCommon {
	
	//地图投影方式#不同投影下参数不同
	private String map_proj;
	//中心纬度
	private String ref_lat;
	//中心经度
	private String ref_lon;
	//第1标准纬度
	private String stand_lat1;
	//第2标准纬度
	private String stand_lat2;
	//标准经度
	private String stand_lon;
	//最大嵌套网格数
	private String max_dom;
	//x向分辨率（单位m）
	private String dx;
	//y向分辨率（单位m）
	private String dy;
	 @JsonProperty("Coord_Name")
	private String coord_Name;
	 
	 
	public String getMap_proj() {
		return map_proj;
	}
	public void setMap_proj(String map_proj) {
		this.map_proj = map_proj;
	}
	public String getRef_lat() {
		return ref_lat;
	}
	public void setRef_lat(String ref_lat) {
		this.ref_lat = ref_lat;
	}
	public String getRef_lon() {
		return ref_lon;
	}
	public void setRef_lon(String ref_lon) {
		this.ref_lon = ref_lon;
	}
	public String getStand_lat1() {
		return stand_lat1;
	}
	public void setStand_lat1(String stand_lat1) {
		this.stand_lat1 = stand_lat1;
	}
	public String getStand_lat2() {
		return stand_lat2;
	}
	public void setStand_lat2(String stand_lat2) {
		this.stand_lat2 = stand_lat2;
	}
	public String getStand_lon() {
		return stand_lon;
	}
	public void setStand_lon(String stand_lon) {
		this.stand_lon = stand_lon;
	}
	public String getMax_dom() {
		return max_dom;
	}
	public void setMax_dom(String max_dom) {
		this.max_dom = max_dom;
	}
	public String getDx() {
		return dx;
	}
	public void setDx(String dx) {
		this.dx = dx;
	}
	public String getDy() {
		return dy;
	}
	public void setDy(String dy) {
		this.dy = dy;
	}
	public String getCoord_Name() {
		return coord_Name;
	}
	public void setCoord_Name(String coord_Name) {
		this.coord_Name = coord_Name;
	}

	
	

}
