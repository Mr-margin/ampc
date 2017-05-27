package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TEsCouplingMapper;
import ampc.com.gistone.database.inter.TEsNationMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.database.inter.TEsNativeTpMapper;
import ampc.com.gistone.database.model.TEsNation;
import ampc.com.gistone.database.model.TEsNative;
import ampc.com.gistone.database.model.TEsNativeTp;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**
 * 清单控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月28日
 */
@RestController
@RequestMapping
public class NativeAndNationController {

	@Autowired
	public	TEsCouplingMapper tEsCouplingMapper;
	@Autowired
	public	TEsNationMapper	tEsNationMapper;
	@Autowired
	public	TEsNativeMapper	tEsNativeMapper;
	@Autowired
	public	TEsNativeTpMapper tEsNativeTpMapper;
	
	private TEsNative tEsNative;
	
	private TEsNativeTp tEsNativeTp;
	
	@RequestMapping("/NativeAndNation/doPost")
	public AmpcResult doPost(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
			AmpcResult listTps = null;
			try {
				// 设置跨域
				ClientUtil.SetCharsetAndHeader(request, response);
				//获取请求参数
				Map<String, Object> data = (Map) requestDate.get("data");
				//获取请求方法
				Object param=data.get("method");
				//根据参数值判断执行对应的处理方法
				if("find_natives".equals(param)){
					listTps = find_natives(requestDate,request,response);
				}else if("add_nativeTp".equals(param)){
					listTps = add_nationTp(requestDate,request,response);
				}else if("updata_nativeTp".equals(param)){
					listTps = updata_nativeTp(requestDate,request,response);
				}else if("delete_nativeTp".equals(param)){
					listTps = delete_nativeTp(requestDate,request,response);
				}else if("add_native".equals(param)){
					listTps = add_native(requestDate,request,response);
				}
				else if("".equals(param)){
					return AmpcResult.build(1001, "NativeAndNationController 请求方法参数异常!");
				}
				
				return AmpcResult.ok(listTps);
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				LogUtil.getLogger().error("NativeAndNationController doPost异常!",e);
				return AmpcResult.build(1001, "NativeAndNationController doPost异常!");
			}
			
	}
	
	/**
	 * 查询当前用户下的清单
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/escoupling/get_couplingInfo")
	public AmpcResult get_nativeInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			List<Map> list=tEsCouplingMapper.selectAll(userId);
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的耦合清单信息成功!");
			return AmpcResult.ok(list);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询当前用户下的耦合清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询当前用户下的耦合清单信息异常!");
		}
	}
	
	/**
	 * 查询当前用户的全国清单
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/NativeAndNation/find_nation")
	public AmpcResult find_nation(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			Long userId = Long.parseLong(param.toString());
			//获取页码
			param=data.get("pageNumber");
			if(!RegUtil.CheckParameter(param, null, null, false)){
				LogUtil.getLogger().error("NativeAndNationController 页码为空或出现非法字符!");
				return AmpcResult.build(1003, "页码为空或出现非法字符!");
			}
			int pageNumber = Integer.valueOf(param.toString());
			//获取每页显示条数
			param=data.get("pageSize");
			if(!RegUtil.CheckParameter(param, null, null, false)){
				LogUtil.getLogger().error("NativeAndNationController 每页条数为空或出现非法字符!");
				return AmpcResult.build(1003, "每页条数为空或出现非法字符!");
			}
			int pageSize = Integer.valueOf(param.toString());
			//添加查询参数
			Map nationMap=new HashMap();
			nationMap.put("userId", userId);
			nationMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			nationMap.put("endTotal",pageNumber*pageSize);
			//查询分页数据
			List<Map> list=tEsNationMapper.selectAllNation(nationMap);
			//查询总条数
			int total=tEsNationMapper.selectTotalNation(userId);
			//返回页面的数据
			Map nationsMap=new HashMap();
			nationsMap.put("rows", list);
			nationsMap.put("total", total);
			nationsMap.put("page", pageNumber);
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的全国清单信息成功!");
			return AmpcResult.ok(nationsMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询当前用户下的全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询当前用户下的全国清单信息异常!");
		}
	}
	
	/**
	 * 查询当前用户下的本地清单
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	public AmpcResult find_natives(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//查询本地清单模板前添加参数
			tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			//查询本地清单模板
			List<Map> listTp=tEsNativeTpMapper.selecttesNativeTp(tEsNativeTp);
			//循环全部模板
			for(int k=0;k<listTp.size();k++){
				//循环获取每个模板
				Map tEsNativeTp=listTp.get(k);
				//获取每个模板ID
				Long es_native_Id= Long.valueOf(tEsNativeTp.get("esNativeTpId").toString());
				tEsNative=new TEsNative();
				tEsNative.setUserId(userId);
				//和模板清单数据关联的ID
				tEsNative.setEsNativeTpId(es_native_Id);
				//循环查询得到单个id的本地清单数据结果集
				List<TEsNative>listNative=tEsNativeMapper.selectAllNative(tEsNative);
				//存放每个清单模板下的清单数据集合
				List tpDataList=new ArrayList();
				//循环清单模板
				for(int i=0;i<listNative.size();i++){
					//对每个清单模板下的清单数据的格式重新设置为符合easyui中tree树形结构的格式
					Map tesNative=new HashMap();
					TEsNative tEsNative=listNative.get(i);
					tesNative.put("id", i);
					tesNative.put("esNativeId", tEsNative.getEsNativeId());
					tesNative.put("esNativeTpName", tEsNative.getEsNativeName());
					tesNative.put("esNativeRelationId", tEsNative.getEsNativeRelationId());
					tesNative.put("esNativeTpId", tEsNative.getEsNativeTpId());
					tesNative.put("esNativeYear", tEsNative.getEsNativeYear());
					tesNative.put("esUploadTpTime", tEsNative.getEsUploadTime());
					tesNative.put("esVersion", tEsNative.getEsVersion());
					tesNative.put("isEffective", tEsNative.getIsEffective());
					tesNative.put("updateTime", tEsNative.getUpdateTime());
					tesNative.put("userId", tEsNative.getUserId());
					tesNative.put("addTime", tEsNative.getAddTime());
					tesNative.put("deleteTime", tEsNative.getDeleteTime());
					tesNative.put("esCodeRange", tEsNative.getEsCodeRange());
					tesNative.put("esComment", tEsNative.getEsComment());
					//把map对象添加到集合中
					tpDataList.add(tesNative);
				}
				
				//将查询到清单模板对应所拥有的清单数据集合添加到map中
				tEsNativeTp.put("id", k);
				tEsNativeTp.put("children", tpDataList);
				//添加配置页面收缩配置
				tEsNativeTp.put("state", "closed");
			}
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的本地清单信息成功!");
			return AmpcResult.ok(listTp);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询当前用户下的本地清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询当前用户下的本地清单信息异常!");
		}
	}
	
	/**
	 * 创建全国清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/NativeAndNation/add_nation")
	public AmpcResult add_nation(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//获取清单名称
			param=data.get("nationName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单名称为空或出现非法字符!");
			}
			String nationName = param.toString();
			
			//获取清单年份
			param=data.get("nationYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单年份为空或出现非法字符!");
			}
			Short nationYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nationRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单备注为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单备注为空或出现非法字符!");
			}
			String nationRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNation tEsNation=new TEsNation();
			tEsNation.setUserId(userId);
			tEsNation.setEsNationName(nationName);
			tEsNation.setEsNationYear(nationYear);
			tEsNation.setNationRemark(nationRemark);
			//插入数据
			int total=tEsNationMapper.insertSelective(tEsNation);
			Map msgMap=new HashMap();
			if(total==1){
				
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 创建全国清单信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 创建全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 创建全国清单信息异常!");
		}
	}
	/**
	 * 编辑全国清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/NativeAndNation/update_nation")
	public AmpcResult update_nation(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//获取清单ID
			param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单Id为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单Id为空或出现非法字符!");
			}
			Long esNationId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nationName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单名称为空或出现非法字符!");
			}
			String esNationName = param.toString();
			
			//获取清单年份
			param=data.get("nationYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单年份为空或出现非法字符!");
			}
			Short nationYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nationRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单备注为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单备注为空或出现非法字符!");
			}
			String nationRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNation tEsNation=new TEsNation();
			tEsNation.setUserId(userId);
			tEsNation.setEsNationName(esNationName);
			tEsNation.setEsNationYear(nationYear);
			tEsNation.setNationRemark(nationRemark);
			tEsNation.setEsNationId(esNationId);
			//更新数据
			int total=tEsNationMapper.updateByIdSelective(tEsNation);
			Map msgMap=new HashMap();
			if(total==1){
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 编辑全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 编辑全国清单信息异常!");
		}
	}
	/**
	 * 删除全国清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/NativeAndNation/delete_nation")
	public AmpcResult delete_nation(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			
			Object param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单ID为空或出现非法字符!");
			}
			// 清单id
			Long nationId = Long.parseLong(param.toString());
			int total=tEsNationMapper.deleteByPrimaryKey(nationId);
			Map msgMap=new HashMap();
			if(total==1){
				//成功
				msgMap.put("msg", true);	
			}else{
				//失败
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 删除全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 删除全国清单信息异常!");
		}
	}
	
	/**
	 * 创建本地清单模板
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult add_nationTp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//获取清单名称
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板名称为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			//获取清单年份
			param=data.get("nativeTpYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板年份为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板年份为空或出现非法字符!");
			}
			Short nativeTpYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nativeTpRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板备注为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板备注为空或出现非法字符!");
			}
			String nativeTpRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNativeTp tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			tEsNativeTp.setEsNativeTpName(nativeTpName);
			tEsNativeTp.setEsNativeTpYear(nativeTpYear);
			tEsNativeTp.setEsComment(nativeTpRemark);
			//插入数据
			int total=tEsNativeTpMapper.insertSelective(tEsNativeTp);
			Map msgMap=new HashMap();
			if(total==1){
				
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 创建本地清单模板信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 创建本地清单模板异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 创建本地清单模板异常!");
		}
	}
	/**
	 * 编辑本地清单模板
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult updata_nativeTp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//获取清单ID
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单Id为空或出现非法字符!");
				return AmpcResult.build(1003, "清单Id为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			//获取清单年份
			param=data.get("nativeTpYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单年份为空或出现非法字符!");
			}
			Short nativeTpYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nativeTpRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "清单备注为空或出现非法字符!");
			}
			String nativeTpRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNativeTp tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			tEsNativeTp.setEsNativeTpName(nativeTpName);
			tEsNativeTp.setEsNativeTpYear(nativeTpYear);
			tEsNativeTp.setEsComment(nativeTpRemark);
			tEsNativeTp.setEsNativeTpId(nativeTpId);
			//更新数据
			int total=tEsNativeTpMapper.updateByIdSelective(tEsNativeTp);
			Map msgMap=new HashMap();
			if(total==1){
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 编辑全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 编辑全国清单信息异常!");
		}
	}
	/**
	 * 删除本地清单模板
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult delete_nativeTp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			Object param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单ID为空或出现非法字符!");
			}
			// 清单id
			Long nativeTpId = Long.parseLong(param.toString());
			int total=tEsNativeTpMapper.deleteByPrimaryKey(nativeTpId);
			Map msgMap=new HashMap();
			if(total==1){
				//成功
				msgMap.put("msg", true);	
			}else{
				//失败
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 删除全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 删除全国清单信息异常!");
		}
	}
	
	/**
	 * 创建本地清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult add_native(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//获取清单名称
			param=data.get("nativeName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单名称为空或出现非法字符!");
			}
			String nativeName = param.toString();
			
			//获取清单年份
			param=data.get("nativeYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单年份为空或出现非法字符!");
			}
			Short nativeYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nativeRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单备注为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单备注为空或出现非法字符!");
			}
			String nativeRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNative tEsNative=new TEsNative();
			tEsNative.setUserId(userId);
			tEsNative.setEsNativeName(nativeName);
			tEsNative.setEsNativeYear(nativeYear);
			tEsNative.setEsComment(nativeRemark);
			//插入数据
			int total=tEsNativeMapper.insertSelective(tEsNative);
			Map msgMap=new HashMap();
			if(total==1){
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 创建本地清单模板信息成功!");
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 创建本地清单模板异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 创建本地清单模板异常!");
		}
	}
	
}
