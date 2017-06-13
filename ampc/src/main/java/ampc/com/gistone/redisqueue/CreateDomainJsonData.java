/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月22日 上午11:09:52
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TDomainMissionMapper;
import ampc.com.gistone.database.model.TDomainMission;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.redisqueue.entity.DomainBodyData;
import ampc.com.gistone.redisqueue.entity.DomainDataCmaq;
import ampc.com.gistone.redisqueue.entity.DomainDataCommon;
import ampc.com.gistone.redisqueue.entity.DomainDataMcip;
import ampc.com.gistone.redisqueue.entity.DomainDataMeic;
import ampc.com.gistone.redisqueue.entity.DomainDataWrf;
import ampc.com.gistone.redisqueue.entity.DomainParams;
import ampc.com.gistone.redisqueue.entity.QueueData;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**  
 * @Title: DomainData.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年5月22日 上午11:09:52
 * @version 1.0
 */
@Component
public class CreateDomainJsonData {
	@Autowired
	private ReadyData readyData;
	@Autowired
	private SendQueueData sendQueueData;
	//公用的Jackson解析对象
	private ObjectMapper mapper=new ObjectMapper();
	//domain信息表映射
	@Autowired
	private TDomainMissionMapper tDomainMissionMapper;
	
	/**
	 * 
	 * @Description: 准备domain参数
	 * @param userId
	 * @param domainId   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:33:46
	 */
	public boolean readyDomainData(Long userId,Long domainId){
		//消息头部的参数
		QueueData queueData = getHeadParameter("domain.create");
		//消息体参数
		DomainBodyData domainbodydata = new DomainBodyData();
		domainbodydata.setUserid(userId.toString());
		domainbodydata.setDomainid(domainId.toString());
		//domain数据参数
		DomainParams domainParams = getdomainParams(userId,domainId);
		domainbodydata.setDomain(domainParams);
		queueData.setBody(domainbodydata);
		boolean senddoamindata = sendQueueData.sendDomainDatajson(queueData);
		if (senddoamindata) {
			LogUtil.getLogger().info("");
		}else {
			LogUtil.getLogger().info("");
		}
		return senddoamindata;
	}
	
	
	
	
	/**
	 * @Description: domain参数的具体内容（common，cmaq，wrf，mcip，meic）
	 * @param userId
	 * @param domainId
	 * @return   
	 * DomainParams  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:32:28
	 */
	private DomainParams getdomainParams(Long userId, Long domainId) {
		/*//domain的common参数
		DomainDataCommon domainDatacommon = getDomainDataCommon();
		//domain的cmaq参数
		DomainDataCmaq domainDataCmaq = getDoaminDataCmaq();
		//domain的wrf参数
		DomainDataWrf domainDataWrf = getDomainDataWrf();
		//domain的meip参数
		DomainDataMcip domainDataMcip = getDomianDataMcip();
		//domain的meic参数
		DomainDataMeic domainDataMeic = getDomainDataMeic();*/
		DomainParams domainParams=null;
		TDomainMissionWithBLOBs selectByPrimaryKey=null;
		try {
			selectByPrimaryKey = tDomainMissionMapper.selectByPrimaryKey(domainId);
			String domainInfo = selectByPrimaryKey.getDomainInfo();
			domainParams = JsonUtil.jsonToObj(domainInfo, DomainParams.class);
		} catch (Exception e) {
			LogUtil.getLogger().error("CreateDomainJsonData--getdomainParams：查询domain数据出错！",e);
		}
		/*domainParams.setCommon(domainDatacommon);
		domainParams.setCmaq(domainDataCmaq);
		domainParams.setWrf(domainDataWrf);
		domainParams.setMcip(domainDataMcip);
		domainParams.setMeic(domainDataMeic);*/
		
		return domainParams;
	}




	/**
	 * @Description: 获取DomainDataMeic 参数
	 * @return   
	 * DomainDataMeic  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:15:26
	 */
	private DomainDataMeic getDomainDataMeic() {
		return null;
	}




	/**
	 * @Description: 获取DomianDataMcip参数
	 * @return   
	 * DomainDataMcip  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:13:55
	 */
	private DomainDataMcip getDomianDataMcip() {
		return null;
	}




	/**
	 * @Description: 获取DomainDataWrf参数
	 * "wrf": {
     *		"version": "WRF3.7",
     *		"parent_id": "1,   1,   2",//嵌套区域的母区域的标号
     *		"parent_grid_ratio": "1,   3,   3",//嵌套时，母网格相对于嵌套网格的水平网格比例
     *		"parent_time_step_ratio":"1,   3,   3",// 嵌套时，母网格相对于嵌套网格的时间步长比例
     *		"i_parent_start": "1,  64,  84",//嵌套网格的左下角（LLC）在上一级网格（母网格）中x方向的起始位置
     *		"j_parent_start": "1,  41,  107",//嵌套网格的左下角（LLC）在上一级网格（母网格）中y方向的起始位置
     *		"e_we": "167, 208,  217",//x方向(西-东方向)的终止格点值（网格边数）
     *		"e_sn": "146, 226,  277",//y方向(南-北方向)的终止格点值（网格边数）
     *		"e_vert":"24",// 嵌套时，母网格相对于嵌套网格的时间步长比例
     *		"eta_levels": "1.000,0.995,0.988,0.980,0.970,0.956,0.938,0.916,0.893,0.868,0.839,0.808,0.777,0.744,0.702,0.648,0.582,0.500,0.400,0.300,0.200,0.120,0.052,0.000",//wrf 垂直分层
     *		"mp_physics": "6",//  #云微物理过程方案
     *		"ra_lw_physics": "1",  //#长波辐射方案
     *		"ra_sw_physics": "5",  //#短波辐射方案
     *		"radt":"15",//  #调用辐散物理方案的时间间隔
     * 		"sf_sfclay_physics" : "7",  //#近地面层方案
     *		"sf_surface_physics":"7",//  #陆面过程方案
     *		"bl_pbl_physics":"7",//边界层方案   ****
     *		"bldt":"0", // #调用边界层物理方案的时间间隔
     *		"cu_physics":"1",  //#积云参数化方案
     *		"cudt":"0",//#调用积云参数化方案的时间间隔
     *		"pxlsm_smois_init": 0, //# Pleim_Xu 陆表模型土壤湿度初始化
     *		"grid_fdda":"1",  //# 格点逼近开关（1=开）
     *		"gfdda_interval_m":360,  // # 分析步长
     *		"grid_sfdda":"1", //# 地表格点逼近开关（1=开）
     *		"sgfdda_interval_m":360,//# 分析步长
     *		"obs_nudge_opt":1,//# obs_nudging 开关
     *		"diff_opt":1,//#扩散系数设定开关
     *		"km_opt":4,//#湍涡系数选项，4代表水平smagorinsky一阶闭合
     * 		"damp_opt":0,//#顶层抽吸作用
     *		"dampcoef":0.2,//#顶层抽吸系数
     *		"khdif":0.0,//#设定水平扩散系数
     *		"kvdif":0.0,//#设定垂直扩散系数
     *		"surface_input_source":1,//#土地利用类型和土壤类型数据的来源格式
     *		"sst_update": 1,//# 使用变化的sst,海洋，植被分数和反照率
     *		"gfdda_end_h":132,//# 预报前Xh停止调整
     *		"sgfdda_end_h":132,//# 预报前Xh停止地标调整
     *		"hypsometric_opt":2,//#real.exe高度、气压计算,默认
     *		"iswater":16,//# water point index (16 for USGS and 17 for MODIS)
     *		"islake":-1,//# 内陆水体的利用类别
     *		"isice":24,//# 冰的利用类别
     *		"isurban":1,//# 城市土地利用类别
     *		"isoilwater":14//# 特定水域的土壤类别
  	 *		},
	 * @return   
	 * DomainDataWrf  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午4:12:39
	 */
	private DomainDataWrf getDomainDataWrf() {
		DomainParams domainParams=null;
		Long domainId = null;
		TDomainMissionWithBLOBs selectByPrimaryKey=null;
		try {
			selectByPrimaryKey = tDomainMissionMapper.selectByPrimaryKey(domainId);
			String domainInfo = selectByPrimaryKey.getDomainInfo();
			domainParams = JsonUtil.jsonToObj(domainInfo, DomainParams.class);
		} catch (Exception e) {
			LogUtil.getLogger().error("CreateDomainJsonData--getDomainDataCommon：查询domain数据出错！",e);
		}
		return null;
	}




	/**
	 * @Description: 获取DoaminDataCmaq参数
	 *   
	 *  "cmaq":{
	 *		"version": "CMAQv5.1",
	 *		"nx": "158,199,208",//x向格数
	 *		"ny": "137,217,268",//y向格数
	 *		"xorig": "-2133000,-504000,219000",//网格区域左下角点坐标x
	 *		"yorig": "-1849500,-841500,88500",//网格区域左下角点坐标y
	 *		"mech_gas": "CB05", //气相化学机制
	 *		"mech_aero": "aero6" //气溶胶机制
  	 *	},
  	 * @return 
	 * DomainDataCmaq  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:56:52
	 */
	private DomainDataCmaq getDoaminDataCmaq() {
		DomainDataCmaq domainDataCmaq = null;
		Long domainId = null;
		TDomainMissionWithBLOBs selectByPrimaryKey=null;
		try {
			selectByPrimaryKey = tDomainMissionMapper.selectByPrimaryKey(domainId);
			String cmap = selectByPrimaryKey.getCmap();
			domainDataCmaq = JsonUtil.jsonToObj(cmap, DomainDataCommon.class);
		} catch (Exception e) {
			LogUtil.getLogger().error("CreateDomainJsonData--getDoaminDataCmaq：查询domain数据出错！",e);
		}
		return domainDataCmaq;
	}




	/**
	 * @Description: 获取domain参数的common的参数
	 *  "common": {
     *		"map_proj": "lambert",//地图投影方式#不同投影下参数不同
     *		"ref_lat": "35.00",//中心纬度
     *		"ref_lon": "110.00",//中心经度
     *		"stand_lat1": "20.0",//第1标准纬度
     *		"stand_lat2": "50.0",//第2标准纬度
     *		"stand_lon": "110.0",//标准经度
     *		"max_dom": "3",//最大嵌套网格数
     *		"dx": "27000,9000,3000",//x向分辨率（单位m）
     *		"dy": "27000,9000,3000",//y向分辨率（单位m）
     *		"Coord_Name":"LAM_20_50_35_110"
     *		},
	 * @return   
	 * DomainDataCommon  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午3:53:52
	 */
	private DomainDataCommon getDomainDataCommon() {
		DomainDataCommon domainDataCommon=null ;
		Long domainId = null;
		TDomainMissionWithBLOBs selectByPrimaryKey=null;
		try {
			selectByPrimaryKey = tDomainMissionMapper.selectByPrimaryKey(domainId);
			String common = selectByPrimaryKey.getCommon();
			domainDataCommon = JsonUtil.jsonToObj(common, DomainDataCommon.class);
		} catch (Exception e) {
			LogUtil.getLogger().error("CreateDomainJsonData--getDomainDataCommon：查询domain数据出错！",e);
		}
		return domainDataCommon;
	}




	/**
	 * 
	 * @Description: 设置队列头部的消息内容
	 * @param type
	 * @return   
	 * QueueData  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 上午11:46:19
	 */
	private QueueData getHeadParameter(String type) {
		//创建消息体对象
		QueueData queueData = new QueueData();
		//消息时间为当前的系统时间（北京时间 ）
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		queueData.setId(UUID.randomUUID().toString());//设置消息id
		queueData.setTime(time);//设置消息时间
		queueData.setType(type);
		return queueData;
	}




	/**
	 * @Description: 更新domain信息返回的结果
	 * @param rpop   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月12日 下午5:15:07
	 */
	public void updateDomainResult(String rpop) {
		Message Domainmessage = null;
		String disposeStatus = null;
		try {
			Domainmessage  = JsonUtil.jsonToObj(rpop, Message.class);
			Date time = Domainmessage.getTime();
			String type = Domainmessage.getType();
			Map body = (Map) Domainmessage.getBody();
			Object userIdObject = body.get("userid");
			//验证userId
			if (RegUtil.CheckParameter(userIdObject, "Long", null, false)) {
				Long userId = Long.parseLong(userIdObject.toString());
				//验证domainID
				Object domainIdObject = body.get("domainid");
				if (RegUtil.CheckParameter(domainIdObject, "Integer", null, false)) {
					Long domainId = Long.parseLong(domainIdObject.toString());
					//验证code
					Object codeobject = body.get("code");
					if (RegUtil.CheckParameter(codeobject, "Integer", null, false)) {
						Integer code = Integer.parseInt(codeobject.toString());
						//获取描述信息
						String desc = body.get("desc").toString();
						//domain信息对象
						TDomainMissionWithBLOBs tDomainMission = new TDomainMissionWithBLOBs();
						if (0!=code) {
							disposeStatus = "4";
						}else {
							disposeStatus = "3";
						}
						tDomainMission.setDomainResultType(type);
						tDomainMission.setDomainResultTime(time);
						tDomainMission.setDomainId(domainId);
						tDomainMission.setUserId(userId);
						tDomainMission.setDomainResultDesc(desc);
						//domain的处理状态
						tDomainMission.setDisposeStatus(disposeStatus);
						int updateByPrimaryKeySelective = tDomainMissionMapper.updateByPrimaryKeySelective(tDomainMission);
						if (updateByPrimaryKeySelective>0) {
							LogUtil.getLogger().info("updateDomainResult：更新domain-result成功！domainid:"+domainId);
						}else {
							LogUtil.getLogger().info("updateDomainResult：更新domain-result失败！domainid:"+domainId);
						}
					}else {
						LogUtil.getLogger().info("updateDomainResult：code参数错误！code:"+codeobject);
					}
				}else {
					LogUtil.getLogger().info("updateDomainResult：domainid参数错误！domainid： "+domainIdObject);
				}
			}else {
				LogUtil.getLogger().info("updateDomainResult：userid参数错误。userId："+userIdObject);
			}
		} catch (IOException e) {
			LogUtil.getLogger().error("updateDomainResult:domain-result 转化失败！",e.getMessage());
		}
		
		
	}

}
