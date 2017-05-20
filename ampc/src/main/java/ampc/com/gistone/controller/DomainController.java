package ampc.com.gistone.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		List<TDomainMissionWithBLOBs> tdlist=tDomainMissionMapper.selectAll();
		//创建返回的jsonArray
		JSONArray arr=new JSONArray();
		//遍历查询到的Domain集合，并添加到返回的json中
		for(TDomainMissionWithBLOBs td:tdlist){
			JSONObject obj=new JSONObject();
			//查看当前domain是否用来创建了任务
			List<TMissionDetail> Tmlist=tMissionDetailMapper.selectDomain(td.getDomainId());
			if(Tmlist.isEmpty()){
				LogUtil.getLogger().error("findAll 未查询到Domain信息");
				return AmpcResult.build(1000, "未查询到Domain信息");
			}
			if(!Tmlist.isEmpty()){
				obj.put("haveMission", "已使用");	
			}else{
				obj.put("haveMission", "未使用");		
			}
			obj.put("userId", td.getUserId());
			obj.put("addTime", td.getAddTime().getTime());//创建时间
			obj.put("version", td.getVersion());//版本（用来区分当前还是历史）
			obj.put("domainName", td.getDomainName());//domain名称
			obj.put("domainDoc", td.getDomainDoc());//备注
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

			//domain省市
			if(!RegUtil.CheckParameter(data.get("domainCode"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainCode为空!");
				return AmpcResult.build(1003, "domainCode为空!");
			}
			Map domainCode=(Map)data.get("domainCode");
			JSONObject OBJ=JSONObject.fromObject(domainCode);
			
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
			//将需要修改的数据添入对象
			TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
			td.setDomainCode(OBJ.toString());
			td.setDomainRange(domainRange);
			td.setDomainId(domainId);
			int s=tDomainMissionMapper.updateByPrimaryKeySelective(td);
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
		if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
			LogUtil.getLogger().error("save_domain  userId为空!");
			return AmpcResult.build(1003, "userId为空!");
		}
		Long userId=Long.valueOf(data.get("userId").toString());
		
		if(!RegUtil.CheckParameter(data.get("domainDoc"), null, null, false)){
			LogUtil.getLogger().error("save_domain  domainDoc为空!");
			return AmpcResult.build(1003, "domainDoc为空!");
		}
		Object domainDoc=data.get("domainDoc");
		if(!RegUtil.CheckParameter(data.get("domainName"), null, null, false)){
			LogUtil.getLogger().error("save_domain  domainName为空!");
			return AmpcResult.build(1003, "domainName为空!");
		}
		String domainName=data.get("domainName").toString();
		int a=tDomainMissionMapper.updateByUserId(userId);
		if(a==0){
			LogUtil.getLogger().error("save_domain 数据库修改版本操作异常！");
			return AmpcResult.build(1000, "数据库修改版本操作异常！");	
		}
		TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
		td.setVersion("2");
		td.setAddTime(new Date());
		td.setDomainDoc(domainDoc);
		td.setDomainName(domainName);
		td.setUserId(userId);
		int s=tDomainMissionMapper.insertSelective(td);
		if(s!=0){
			return AmpcResult.ok();
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
		if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
			return AmpcResult.build(1003, "userId为空!");
		}
		Long userId=Long.valueOf(data.get("domainId").toString());
		if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainId为空!");
			return AmpcResult.build(1003, "domainId为空!");
		}
		Long domainId=Long.valueOf(data.get("domainId").toString());
		if(!RegUtil.CheckParameter(data.get("domainDoc"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainDoc为空!");
			return AmpcResult.build(1003, "domainDoc为空!");
		}
		Object domainDoc=data.get("domainDoc");
		if(!RegUtil.CheckParameter(data.get("domainName"), null, null, false)){
			LogUtil.getLogger().error("updateRangeAndCode  domainName为空!");
			return AmpcResult.build(1003, "domainName为空!");
		}
		String domainName=data.get("domainName").toString();
		TDomainMissionWithBLOBs td=new TDomainMissionWithBLOBs();
		td.setUpdateTime(new Date());
		td.setDomainDoc(domainDoc);
		td.setDomainName(domainName);
		td.setDomainId(domainId);
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
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  userId为空!");
				return AmpcResult.build(1003, "userId为空!");
			}
			Long userId=Long.valueOf(data.get("domainId").toString());
			if(!RegUtil.CheckParameter(data.get("userId"), null, null, false)){
				LogUtil.getLogger().error("updateRangeAndCode  domainId为空!");
				return AmpcResult.build(1003, "domainId为空!");
			}
			Long domainId=Long.valueOf(data.get("domainId").toString());
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
