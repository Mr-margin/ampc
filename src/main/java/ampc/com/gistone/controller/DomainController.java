package ampc.com.gistone.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TDomainMissionMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.preprocess.core.CalculateCityService;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

@RestController
@RequestMapping
public class DomainController {
	@Autowired
	private TDomainMissionMapper tDomainMissionMapper;
	
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@Autowired
	private CalculateCityService calculateCityService;
	
	//定义公用的jackson帮助类
		private ObjectMapper mapper=new ObjectMapper();
	/**
	 * 查询所有domain
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Domain/findAll")	
	public AmpcResult findAll(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			//获取前端参数
			Map<String,Object> data=(Map)requestDate.get("data");
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
				return AmpcResult.build(1003, "userId为空!");
			}
			Long userId=Long.valueOf(data.get("userId").toString());
			//查询所有Domain信息
		List<TDomainMissionWithBLOBs> tdlist=tDomainMissionMapper.selectAll(userId);
		//创建返回的jsonArray
		JSONArray arr=new JSONArray();
		//遍历查询到的Domain集合，并添加到返回的json中
		for(TDomainMissionWithBLOBs td:tdlist){
			JSONObject obj=new JSONObject();
			//查看当前domain是否用来创建了任务
			List<TMissionDetail> Tmlist=tMissionDetailMapper.selectDomain(td.getDomainId());
			if(!Tmlist.isEmpty()){
				obj.put("haveMission", "已使用");	
			}else{
				obj.put("haveMission", "未使用");	
			}
			obj.put("domainId", td.getDomainId());
			obj.put("userId", td.getUserId());
			obj.put("addTime", td.getAddTime().getTime());//创建时间
			obj.put("createStatus", td.getCreateStatus().toString());//状态（用来区分当前还是历史）
			obj.put("domainName", td.getDomainName());//domain名称
			obj.put("domainDoc", td.getDomainDoc());//备注
			if(td.getDomainInfo()==null){
				obj.put("domainInfo",td.getDomainInfo());	
			}else{
			obj.put("domainInfo", td.getDomainInfo().replaceAll(" ", ""));
			}
			arr.add(obj);
			
		}
		return AmpcResult.ok(arr);
		}catch(Exception e){
			LogUtil.getLogger().error("findAll Domain查询异常！",e);
			return AmpcResult.build(1001, "Domain查询异常！");
		}
	}
	/**
	 * domain修改范围及省市
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Domain/updateRangeAndCode")
	public AmpcResult updateRangeAndCode(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			//获取前端参数
			Map<String,Object> data=(Map)requestDate.get("data");
			//domain范围
			if(!RegUtil.CheckParameter(data.get("domainRange"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainRange为空!");
				return AmpcResult.build(1003, "domainRange为空!");
			}
			String domainRange=data.get("domainRange").toString();

			//获取domainInfo
			if(!RegUtil.CheckParameter(data.get("domainInfo"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainInfo为空!");
				return AmpcResult.build(1003, "domainInfo为空!");
			}
			Map<String,Map> domainInfo=(Map<String,Map>)data.get("domainInfo");
			
			//用户id
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
				return AmpcResult.build(1003, "userId为空!");
			}
			Long userId=Long.valueOf(data.get("userId").toString());
			//domainId
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainId为空!");
				return AmpcResult.build(1003, "domainId为空!");
			}
			Long domainId=Long.valueOf(data.get("domainId").toString());
			
			
			Map<String,Object> common=domainInfo.get("common");//获取domainInfo中的common模块数据
			Map<String,Object> wrf=domainInfo.get("wrf");//获取domainInfo中的wrf模块数据
			Map<String,Object> mcip=domainInfo.get("mcip");//获取domainInfo中的mcip模块数据
			//因为domainInfo中没有meic模块，因此要创建Meic
			Map<String,Object> meic=new HashMap();
			//因为domainInfo中没有cmaq模块，因此要创建cmaq
			Map<String,Object> cmaq=new HashMap();
			//common数据填充
			common.put("map_proj", "lambert");//添加map_proj
			common.put("stand_lon", common.get("ref_lon"));//添加stand_lon
			//计算dx数据
			Long[] dxs=new Long[3];
			dxs[0]=Long.valueOf(common.get("dx").toString());
			dxs[1]=dxs[0]/3;
			dxs[2]=dxs[1]/3;
			//计算dy数据
			Long[] dys=new Long[3]; 
			dys[0]=Long.valueOf(common.get("dy").toString());
			dys[1]=dxs[0]/3;
			dys[2]=dxs[1]/3;
			 String db="";
			  for(int x=0;x<dxs.length;x++){
				  if(x==dxs.length){
					  db+=dxs[x];
				  }else{
					  db+=dys[x]; 
					  db+=",";
				  }
			  }
			 String ds="";
			  for(int x=0;x<dys.length;x++){
				  if(x==dys.length){
					  ds+=dys[x];
				  }else{
					  ds+=dys[x]; 
					  ds+=",";
				  }
			  }
		    common.put("dx", db);//添加dx
		    common.put("dy", ds);//添加dy
		  //添加Coord_Name
		    common.put("Coord_Name", "LAM_"+Math.round(Float.valueOf(common.get("stand_lat1").toString()))+"_"+Math.round(Float.valueOf(common.get("stand_lat2").toString()))+"_"+Math.round(Float.valueOf(common.get("ref_lat").toString()))+"_"+Math.round(Float.valueOf(common.get("ref_lon").toString())));
			//cmaq数据域填充
		   Long btrim=Long.valueOf(mcip.get("btrim").toString());
		   cmaq.put("version", "CMAQv5.1");//添加version
		   cmaq.put("mech_gas", "CB05");//添加mech_gas
		   cmaq.put("mech_aero", "aero6");//添加mech_aero
		  //获取e_we数据
		   String e_we=wrf.get("e_we").toString();
		  List<String> arr = Arrays.asList(e_we.split(","));
		   //计算nx数据
		   List<Long> nx=new ArrayList();
		   nx.add((Long.valueOf(arr.get(0))-((btrim+1))*2)-1);
		   if(arr.size()>=2){
			   nx.add((Long.valueOf(arr.get(1))-((btrim+1))*2)-1);
		   }
		   if(arr.size()>=3){
			   nx.add((Long.valueOf(arr.get(2))-((btrim+1))*2)-1);   
		   }
		   String n="";
		  for(int x=1;x<=nx.size();x++){
			  if(x==nx.size()){
			  n+=nx.get(x-1);
			  }else{
				  n+=nx.get(x-1); 
				  n+=",";
			  }
		  }
		   
		   cmaq.put("nx", n);
		   //获取e_sn数据
		   String e_sn=wrf.get("e_sn").toString();
		   List<String> arr2 = Arrays.asList(e_sn.split(","));
		   //获取i_parent_start数据
		   String i_parent_start=wrf.get("i_parent_start").toString();
		   List<String> i_parent_starts = Arrays.asList(i_parent_start.split(","));
		   //获取j_parent_start数据
		   String j_parent_start=wrf.get("j_parent_start").toString();
		   List<String> j_parent_starts =  Arrays.asList(j_parent_start.split(","));
		 //为wrf中的i_parent_start添加默认数据
		   List<Long> i=new ArrayList();
		   i.add(1l);
		   i.add(Long.valueOf(i_parent_starts.get(0)));
		   if(i_parent_starts.size()>=2){
			i.add(Long.valueOf(i_parent_starts.get(1)));      
		   }
		 //为wrf中的j_parent_start添加默认数据
		   List<Long> j=new ArrayList();
		   j.add(1l);
		   j.add(Long.valueOf(j_parent_starts.get(0)));
		   if(j_parent_starts.size()>=2){
		   j.add(Long.valueOf(j_parent_starts.get(1)));
		   }
		   String js="";
			  for(int x=1;x<=j.size();x++){
				  if(x==j.size()){
					  js+=j.get(x-1);
				  }else{
					  js+=j.get(x-1); 
					  js+=",";
				  }
			  }
			  String is="";
			  for(int x=1;x<=i.size();x++){
				  if(x==i.size()){
					  is+=i.get(x-1);
				  }else{
					  is+=i.get(x-1); 
					  is+=",";
				  }
			  }
		   wrf.put("j_parent_start", js);  
		   wrf.put("i_parent_start", is);  
		   
		   //计算ny数据
		   List<Long> ny=new ArrayList();
		   ny.add((Long.valueOf(arr2.get(0))-(btrim+1))*2-1);
		   if(arr2.size()>=2){
			   ny.add((Long.valueOf(arr2.get(1))-(btrim+1))*2-1);  
		   }
		 
		   if(arr2.size()>=3){
			   ny.add((Long.valueOf(arr2.get(2))-(btrim+1))*2-1);  
		   }
		   String nys="";
			  for(int x=1;x<=ny.size();x++){
				  if(x==ny.size()){
					  nys+=ny.get(x-1);
				  }else{
					  nys+=ny.get(x-1); 
					  nys+=",";
				  }
			  }
		   cmaq.put("ny", nys);
		   
		   //计算xorig数据
		   List<Long> xorig=new ArrayList();
		   xorig.add((-dxs[0]*(Long.valueOf(arr.get(0))-1)/2)+(dxs[0]*(btrim+1)));
		   if(arr.size()>=1&&i.size()>=2){
		   xorig.add((-dxs[0]*(Long.valueOf(arr.get(0))-1)/2)+(dxs[0]*(i.get(1)-1)) + (dxs[1]*(btrim+1)));  
		   }
		   if(arr.size()>=1&&i.size()==3){
			 xorig.add((-dxs[0]*(Long.valueOf(arr.get(0))-1)/2)+(dxs[0]*(i.get(1)-1))+(dxs[1]*(i.get(2))-1) +(dxs[2]*(btrim+1)));
		   }
		   
		   String xorigs="";
			  for(int x=1;x<=xorig.size();x++){
				  if(x==xorig.size()){
					  xorigs+=xorig.get(x-1);
				  }else{
					  xorigs+=xorig.get(x-1); 
					  xorigs+=",";
				  }
			  }
		   
		   cmaq.put("xorig", xorigs);
		   //计算yorig数据
		   List<Long> yorig=new ArrayList();
		   yorig.add((-dys[0]*(Long.valueOf(arr2.get(0))-1)/2)+(dys[0]*(btrim+1)));
		   
		   if(arr2.size()>=1&&j.size()>=2){
			   yorig.add((-dys[0]*(Long.valueOf(arr2.get(0))-1)/2)+(dys[0]*(j.get(1)-1)) + (dys[1]*(btrim+1))); 
			   }
		   if(arr2.size()>=1&&j.size()==3){
			   yorig.add((-dys[0]*(Long.valueOf(arr2.get(0))-1)/2)+(dys[0]*(j.get(1)-1))+(dys[1]*(j.get(2)-1)) +(dys[2]*(btrim+1)));
			   }
		   
		   String yorigs="";
			  for(int x=1;x<=yorig.size();x++){
				  if(x==yorig.size()){
					  yorigs+=yorig.get(x-1);
				  }else{
					  yorigs+=yorig.get(x-1); 
					  yorigs+=",";
				  }
			  }
		   cmaq.put("yorig", yorigs);
		   
		   //因为domainInfo中没有cmaq因此要添加进去
		   domainInfo.put("cmaq", cmaq); 
		   //wrf添加默认数据
		   wrf.put("version", "WRF3.7"); 
		   wrf.put("parent_id",  "1,1,2");
		   wrf.put("parent_grid_ratio", "1,3,3");
		   wrf.put("parent_time_step_ratio", "1,3,3");
		   wrf.put("e_vert", "24");
		   wrf.put("eta_levels", "1.000,0.995,0.988,0.980,0.970,0.956,0.938,0.916,0.893,0.868,0.839,0.808,0.777,0.744,0.702,0.648,0.582,0.500,0.400,0.300,0.200,0.120,0.052,0.000");
		   wrf.put("mp_physics", "6");
		   wrf.put("ra_lw_physics", "1");
		   wrf.put("ra_sw_physics", "5");
		   wrf.put("radt","15");
		   wrf.put("sf_sfclay_physics","7");
		   wrf.put("sf_surface_physics","7");
		   wrf.put("bl_pbl_physics","7");
		   wrf.put("bldt","0");
		   wrf.put("cu_physics","1");
		   wrf.put("cudt","0");
		   wrf.put("pxlsm_smois_init", 0);
		   wrf.put("grid_fdda","1");
		   wrf.put("gfdda_interval_m",360);
		   wrf.put("grid_sfdda","1");
		   wrf.put("sgfdda_interval_m",360);
		   wrf.put("obs_nudge_opt",1);
		   wrf.put("diff_opt",1);
		   wrf.put( "km_opt",4);
		   wrf.put("damp_opt",0);
		   wrf.put("dampcoef",0.2);
		   wrf.put("khdif",0.0);
		   wrf.put("kvdif",0.0);
		   wrf.put("surface_input_source",1);
		   wrf.put("sst_update", 1);
		   wrf.put("gfdda_end_h",132);
		   wrf.put("sgfdda_end_h",132);
		   wrf.put("hypsometric_opt",2);
		   wrf.put( "iswater",16);
		   wrf.put("islake",-1);
		   wrf.put("isice",24);
		   wrf.put("isurban",1);
		   wrf.put("isoilwater",14);
		   
		   //为mcip添加默认数据
		   mcip.put("ctm_vert",15);
		   mcip.put("CTMLAYS","1.000, 0.995, 0.988, 0.980, 0.970, 0.956, 0.938, 0.893, 0.839, 0.777, 0.702, 0.582, 0.400, 0.200, 0.000");
		   //meic添加数据
		   Map<String,Object> model=new HashMap();//创建mcip中的model模块
		   Map<String,Object> megan=new HashMap();//创建mcip中的megan模块
		   //model中添加默认数据
		   model.put("name","cmaq");
		   megan.put("version","MEGANv2.10");
		   megan.put("shutdown",false);
		   //通过判断version为model添加sunmodel数据
		   if(cmaq.get("version").equals("CMAQv5.0.1")||cmaq.get("version").equals("CMAQv5.1")){
			   model.put("submodel","cmaq-5.0");     
		   }else if(cmaq.get("version").equals("CMAQv4.7.1")){
			   model.put("submodel","cmaq-4.7");
		   }
		   //将model与megan插入meic
		   meic.put("model", model);
		   meic.put("megan", megan);
		   //将meic插入DOMAIN_INFO
		   domainInfo.put("meic", meic);
		   //DOMAIN_INFO转换为JSONObject
		   JSONObject obj=JSONObject.fromObject(domainInfo);
		   JSONObject obs=new JSONObject();
		   obs.put("common", JSONObject.fromObject(common));
		   JSONObject cmobj=new JSONObject();
		   cmobj.put("cmaq", JSONObject.fromObject(cmaq));
		   
		   String domainCode=calculateCityService.getCityResult((double) xorig.get(xorig.size()-1), (double) yorig.get(yorig.size()-1), (double)dxs[dxs.length-1], (double)dys[dys.length-1], Integer.valueOf(ny.get(ny.size()-1).toString()), Integer.valueOf(nx.get(nx.size()-1).toString()), Double.valueOf(common.get("stand_lat1").toString()), Double.valueOf(common.get("stand_lat2").toString()), Double.valueOf(common.get("ref_lon").toString()), Double.valueOf(common.get("ref_lat").toString()));
		   
		   
		   //将需要修改的数据添入对象
			TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
			td.setCmap(cmobj.toString());
			td.setDomainInfo(obj.toString());
			td.setCommon(obs.toString());
			td.setDomainRange(domainRange);
			td.setDomainId(domainId);
			td.setDomainCode(domainCode);
			int s=tDomainMissionMapper.updateByPrimaryKeySelective(td);
			//判断修改数据是否成功
			if(s>0){
				return AmpcResult.ok();
			}else{
				LogUtil.getLogger().error("updateRangeAndCode 数据库修改异常！");
				return AmpcResult.build(1000, "数据库修改异常！");
			}
			
		}catch(Exception e){
			LogUtil.getLogger().error("updateRangeAndCode Domain修改异常！",e);
			return AmpcResult.build(1001, "Domain修改异常！");	
			
		}
	}
	/**
	 * domain创建
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Domain/save_domain")	
	public AmpcResult save_domain(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		//获取前端参数
		Map<String,Object> data=(Map)requestDate.get("data");
		//获取userId
		if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
			LogUtil.getLogger().error("save_domain  userId为空!");
			return AmpcResult.build(1003, "userId为空!");
		}
		Long userId=Long.valueOf(data.get("userId").toString());
		//获取domainDoc(分层描述)
		if(!RegUtil.CheckParameter(data.get("domainDoc"), null, null, false)){
			LogUtil.getLogger().error("save_domain  domainDoc为空!");
			return AmpcResult.build(1003, "domainDoc为空!");
		}
		Object domainDoc=data.get("domainDoc");
		//获取domainName(domain名称)
		if(!RegUtil.CheckParameter(data.get("domainName"), null, null, false)){
			LogUtil.getLogger().error("save_domain  domainName为空!");
			return AmpcResult.build(1003, "domainName为空!");
		}
		String domainName=data.get("domainName").toString();
		//根据userId把前面的所有domain修改为历史
		int a=tDomainMissionMapper.updateByUserId(userId);
		if(a==0){
			LogUtil.getLogger().error("save_domain 数据库修改版本操作异常！");
			return AmpcResult.build(1000, "数据库修改版本操作异常！");
		}
		//将数据填入对象中
		TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
		td.setCreateStatus("2");
		td.setAddTime(new Date());
		td.setDomainDoc(domainDoc);
		td.setDomainName(domainName);
		td.setUserId(userId);
		//操作数据库添加数据
		int s=tDomainMissionMapper.insertSelective(td);
		TDomainMissionWithBLOBs TS=tDomainMissionMapper.selectbynameanddoc(td);
		Long DomainId=TS.getDomainId();
		JSONObject obj=new JSONObject();
		obj.put("DomainId", DomainId);
		if(s!=0){
			return AmpcResult.ok(obj);
		}
		
		LogUtil.getLogger().error("save_domain Domain创建数据库操作异常！");
		return AmpcResult.build(1000, "Domain创建数据库操作异常！");	
	}catch(Exception e){
		LogUtil.getLogger().error("save_domain Domain创建异常！",e);
		return AmpcResult.build(1001, "Domain创建异常！");	
	}
	}
	/**
	 * 修改domainName及Doc
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Domain/updateNameAndDoc")	
	public AmpcResult updateNameAndDoc(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		//获取前端参数
		Map<String,Object> data=(Map)requestDate.get("data");
		//获取userId
		if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
			return AmpcResult.build(1003, "userId为空!");
		}
		Long userId=Long.valueOf(data.get("userId").toString());
		//获取domainId
		if(!RegUtil.CheckParameter(data.get("domainId"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainId为空!");
			return AmpcResult.build(1003, "domainId为空!");
		}
		Long domainId=Long.valueOf(data.get("domainId").toString());
		//获取domainDoc(分层描述)
		if(!RegUtil.CheckParameter(data.get("domainDoc"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainDoc为空!");
			return AmpcResult.build(1003, "domainDoc为空!");
		}
		Object domainDoc=data.get("domainDoc");
		//获取domainName(domain名称)
		if(!RegUtil.CheckParameter(data.get("domainName"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainName为空!");
			return AmpcResult.build(1003, "domainName为空!");
		}
		String domainName=data.get("domainName").toString();
		//将数据添加到对象中
		TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
		td.setUpdateTime(new Date());
		td.setDomainDoc(domainDoc);
		td.setDomainName(domainName);
		td.setDomainId(domainId);
		//操作数据库保存数据
		int s=tDomainMissionMapper.updateByPrimaryKeySelective(td);
		if(s==0){
			LogUtil.getLogger().error("updateNameAndDoc 数据库修改Name及Doc操作异常！");
			return AmpcResult.build(1000, "数据库修改Name及Doc操作异常！");
		}
		return AmpcResult.ok();
	}catch(Exception e){
		LogUtil.getLogger().error("updateNameAndDoc 修改Name及Doc异常！",e);
		return AmpcResult.build(1001, "修改Name及Doc异常！");
	}
	}
	/**
	 * domain删除
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Domain/deleteDomain")	
	public AmpcResult deleteDomain(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			//获取前端参数
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取userId
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
				return AmpcResult.build(1003, "userId为空!");
			}
			Long userId=Long.valueOf(data.get("domainId").toString());
			//获取userId
			if(!RegUtil.CheckParameter(data.get("domainId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainId为空!");
				return AmpcResult.build(1003, "domainId为空!");
			}
			Long domainId=Long.valueOf(data.get("domainId").toString());
			//根据domainId逻辑删除数据
			int a=tDomainMissionMapper.deletebyid(domainId);
			if(a==0){
				LogUtil.getLogger().error("deleteDomain 数据库删除domain操作异常！");
				return AmpcResult.build(1000, "数据库删除domain操作异常！");	
			}
		return AmpcResult.ok();
		}catch(Exception e){
			LogUtil.getLogger().error("deleteDomain 删除domain异常！",e);
			return AmpcResult.build(1001, "删除domain异常！");	
		}
	}
}
