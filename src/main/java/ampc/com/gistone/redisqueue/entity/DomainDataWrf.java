/**  
 * @Title: DomainDataWrf.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:19:05
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;

/**  
 * @Title: DomainDataWrf.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午5:19:05
 * @version 1.0
 */
public class DomainDataWrf {
	
	private String version;
	//嵌套区域的母区域的标号 "1,   1,   2",
	private String parent_id;
	//嵌套时，母网格相对于嵌套网格的水平网格比例    "1,   3,   3",
	private String parent_grid_ratio;
	// 嵌套时，母网格相对于嵌套网格的时间步长比例  "1,   3,   3",
	private String parent_time_step_ratio;
	//嵌套网格的左下角（LLC）在上一级网格（母网格）中x方向的起始位置    "1,  64,  84",  
	private String i_parent_start;
	//嵌套网格的左下角（LLC）在上一级网格（母网格）中y方向的起始位置     "j_parent_start": "1,  41,  107",
	private String j_parent_start;
	//x方向(西-东方向)的终止格点值（网格边数） "167, 208,  217"
	private String e_we;
	//y方向(南-北方向)的终止格点值（网格边数） "146, 226,  277",
	private String e_sn;
	// 嵌套时，母网格相对于嵌套网格的时间步长比例  "24",
	private String e_vert;
	//wrf 垂直分层 "1.000,0.995,0.988,0.980,0.970,0.956,0.938,0.916,0.893,0.868,0.839,0.808,0.777,0.744,0.702,0.648,0.582,0.500,0.400,0.300,0.200,0.120,0.052,0.000"
	private String eta_levels;
	//  #云微物理过程方案  "6",
	private String mp_physics;
	//#长波辐射方案 "1",
	private String ra_lw_physics;
	//#短波辐射方案  "5", 
	private String ra_sw_physics;
	//  #调用辐散物理方案的时间间隔    "15",
	private String radt;
	//#近地面层方案  "7",
	private String sf_sfclay_physics;
	//  #陆面过程方案 "7",
	private String sf_surface_physics;
	//边界层方案  "7"
	private String bl_pbl_physics;
	// #调用边界层物理方案的时间间隔  "0", 
	private String bldt;
	//#积云参数化方案  "1",
	private String cu_physics;
	//#调用积云参数化方案的时间间隔 "0" 
	private String cudt;
	//# Pleim_Xu 陆表模型土壤湿度初始化   0,
	private String pxlsm_smois_init;
	//# 格点逼近开关（1=开） "1",
	private String grid_fdda;
	// # 分析步长   360,
	private String gfdda_interval_m;
	//# 地表格点逼近开关（1=开） "1",
	private String grid_sfdda;
	//# 分析步长  360,
	private String sgfdda_interval_m;
	//# obs_nudging 开关   1,
	private String obs_nudge_opt;
	//#扩散系数设定开关 1
	private String diff_opt;
	//#湍涡系数选项，4代表水平smagorinsky一阶闭合   4
	private String km_opt;
	//#顶层抽吸作用 0 
	private String damp_opt;
	//#顶层抽吸系数 0.2,
	private String dampcoef;
	//#设定水平扩散系数  0.0,
	private String khdif;
	//#设定垂直扩散系数  0.0,
	private String kvdif;
	//#土地利用类型和土壤类型数据的来源格式   1,
	private String surface_input_source;
	//# 使用变化的sst,海洋，植被分数和反照率  1
	private String sst_update;
	//# 预报前Xh停止调整   132,
	private String gfdda_end_h;
	//# 预报前Xh停止地标调整   132
	private String sgfdda_end_h;
	//#real.exe高度、气压计算,默认  2
	private String hypsometric_opt;
	//# water point index (16 for USGS and 17 for MODIS) 16,
	private String iswater;
	//# 内陆水体的利用类别  -1
	private String islake;
	//# 冰的利用类别  24
	private String isice;
	//# 城市土地利用类别  1,
	private String isurban;
	//# 特定水域的土壤类别   14
	private String isoilwater;
	
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getParent_grid_ratio() {
		return parent_grid_ratio;
	}
	public void setParent_grid_ratio(String parent_grid_ratio) {
		this.parent_grid_ratio = parent_grid_ratio;
	}
	public String getParent_time_step_ratio() {
		return parent_time_step_ratio;
	}
	public void setParent_time_step_ratio(String parent_time_step_ratio) {
		this.parent_time_step_ratio = parent_time_step_ratio;
	}
	public String getI_parent_start() {
		return i_parent_start;
	}
	public void setI_parent_start(String i_parent_start) {
		this.i_parent_start = i_parent_start;
	}
	public String getJ_parent_start() {
		return j_parent_start;
	}
	public void setJ_parent_start(String j_parent_start) {
		this.j_parent_start = j_parent_start;
	}
	public String getE_we() {
		return e_we;
	}
	public void setE_we(String e_we) {
		this.e_we = e_we;
	}
	public String getE_sn() {
		return e_sn;
	}
	public void setE_sn(String e_sn) {
		this.e_sn = e_sn;
	}
	public String getE_vert() {
		return e_vert;
	}
	public void setE_vert(String e_vert) {
		this.e_vert = e_vert;
	}
	public String getEta_levels() {
		return eta_levels;
	}
	public void setEta_levels(String eta_levels) {
		this.eta_levels = eta_levels;
	}
	public String getMp_physics() {
		return mp_physics;
	}
	public void setMp_physics(String mp_physics) {
		this.mp_physics = mp_physics;
	}
	public String getRa_lw_physics() {
		return ra_lw_physics;
	}
	public void setRa_lw_physics(String ra_lw_physics) {
		this.ra_lw_physics = ra_lw_physics;
	}
	public String getRa_sw_physics() {
		return ra_sw_physics;
	}
	public void setRa_sw_physics(String ra_sw_physics) {
		this.ra_sw_physics = ra_sw_physics;
	}
	public String getRadt() {
		return radt;
	}
	public void setRadt(String radt) {
		this.radt = radt;
	}
	public String getSf_sfclay_physics() {
		return sf_sfclay_physics;
	}
	public void setSf_sfclay_physics(String sf_sfclay_physics) {
		this.sf_sfclay_physics = sf_sfclay_physics;
	}
	public String getSf_surface_physics() {
		return sf_surface_physics;
	}
	public void setSf_surface_physics(String sf_surface_physics) {
		this.sf_surface_physics = sf_surface_physics;
	}
	public String getBl_pbl_physics() {
		return bl_pbl_physics;
	}
	public void setBl_pbl_physics(String bl_pbl_physics) {
		this.bl_pbl_physics = bl_pbl_physics;
	}
	public String getBldt() {
		return bldt;
	}
	public void setBldt(String bldt) {
		this.bldt = bldt;
	}
	public String getCu_physics() {
		return cu_physics;
	}
	public void setCu_physics(String cu_physics) {
		this.cu_physics = cu_physics;
	}
	public String getCudt() {
		return cudt;
	}
	public void setCudt(String cudt) {
		this.cudt = cudt;
	}
	public String getPxlsm_smois_init() {
		return pxlsm_smois_init;
	}
	public void setPxlsm_smois_init(String pxlsm_smois_init) {
		this.pxlsm_smois_init = pxlsm_smois_init;
	}
	public String getGrid_fdda() {
		return grid_fdda;
	}
	public void setGrid_fdda(String grid_fdda) {
		this.grid_fdda = grid_fdda;
	}
	public String getGfdda_interval_m() {
		return gfdda_interval_m;
	}
	public void setGfdda_interval_m(String gfdda_interval_m) {
		this.gfdda_interval_m = gfdda_interval_m;
	}
	public String getGrid_sfdda() {
		return grid_sfdda;
	}
	public void setGrid_sfdda(String grid_sfdda) {
		this.grid_sfdda = grid_sfdda;
	}
	public String getSgfdda_interval_m() {
		return sgfdda_interval_m;
	}
	public void setSgfdda_interval_m(String sgfdda_interval_m) {
		this.sgfdda_interval_m = sgfdda_interval_m;
	}
	public String getObs_nudge_opt() {
		return obs_nudge_opt;
	}
	public void setObs_nudge_opt(String obs_nudge_opt) {
		this.obs_nudge_opt = obs_nudge_opt;
	}
	public String getDiff_opt() {
		return diff_opt;
	}
	public void setDiff_opt(String diff_opt) {
		this.diff_opt = diff_opt;
	}
	public String getKm_opt() {
		return km_opt;
	}
	public void setKm_opt(String km_opt) {
		this.km_opt = km_opt;
	}
	public String getDamp_opt() {
		return damp_opt;
	}
	public void setDamp_opt(String damp_opt) {
		this.damp_opt = damp_opt;
	}
	public String getDampcoef() {
		return dampcoef;
	}
	public void setDampcoef(String dampcoef) {
		this.dampcoef = dampcoef;
	}
	public String getKhdif() {
		return khdif;
	}
	public void setKhdif(String khdif) {
		this.khdif = khdif;
	}
	public String getKvdif() {
		return kvdif;
	}
	public void setKvdif(String kvdif) {
		this.kvdif = kvdif;
	}
	public String getSurface_input_source() {
		return surface_input_source;
	}
	public void setSurface_input_source(String surface_input_source) {
		this.surface_input_source = surface_input_source;
	}
	public String getSst_update() {
		return sst_update;
	}
	public void setSst_update(String sst_update) {
		this.sst_update = sst_update;
	}
	public String getGfdda_end_h() {
		return gfdda_end_h;
	}
	public void setGfdda_end_h(String gfdda_end_h) {
		this.gfdda_end_h = gfdda_end_h;
	}
	public String getSgfdda_end_h() {
		return sgfdda_end_h;
	}
	public void setSgfdda_end_h(String sgfdda_end_h) {
		this.sgfdda_end_h = sgfdda_end_h;
	}
	public String getHypsometric_opt() {
		return hypsometric_opt;
	}
	public void setHypsometric_opt(String hypsometric_opt) {
		this.hypsometric_opt = hypsometric_opt;
	}
	public String getIswater() {
		return iswater;
	}
	public void setIswater(String iswater) {
		this.iswater = iswater;
	}
	public String getIslake() {
		return islake;
	}
	public void setIslake(String islake) {
		this.islake = islake;
	}
	public String getIsice() {
		return isice;
	}
	public void setIsice(String isice) {
		this.isice = isice;
	}
	public String getIsurban() {
		return isurban;
	}
	public void setIsurban(String isurban) {
		this.isurban = isurban;
	}
	public String getIsoilwater() {
		return isoilwater;
	}
	public void setIsoilwater(String isoilwater) {
		this.isoilwater = isoilwater;
	}
	
	
	
	
	
	

}
