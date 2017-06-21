package ampc.com.gistone.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TAddressMapper;
import ampc.com.gistone.database.inter.TEsCouplingMapper;
import ampc.com.gistone.database.inter.TEsNationMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.database.inter.TEsNativeTpMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.model.TAddress;
import ampc.com.gistone.database.model.TEsCoupling;
import ampc.com.gistone.database.model.TEsNation;
import ampc.com.gistone.database.model.TEsNative;
import ampc.com.gistone.database.model.TEsNativeTp;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.extract.ExtractConfig;
import ampc.com.gistone.extract.ResultPathUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
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
@JsonIgnoreProperties
public class NativeAndNationController {

	@Autowired
	public	TEsCouplingMapper tEsCouplingMapper;
	@Autowired
	public	TEsNationMapper	tEsNationMapper;
	@Autowired
	public	TEsNativeMapper	tEsNativeMapper;
	@Autowired
	public	TEsNativeTpMapper	tEsNativeTpMapper;
	@Autowired
	public ExcelToDateController	excelToDateController;
	@Autowired
	public TSectorExcelMapper	tSectorExcelMapper;
	@Autowired
	public TAddressMapper	tAddressMapper;
	@Autowired
	public TMissionDetailMapper	tMissionDetailMapper;
	
	//读取路径的帮助类
	@Autowired
	private ConfigUtil configUtil;
	
	private TEsNative tEsNative;
	
	//定义公用的jackson帮助类
	private ObjectMapper mapper=new ObjectMapper();
	
	private final static Logger logger = LoggerFactory.getLogger(ResultPathUtil.class);
	
	private ExtractConfig extractConfig;
	
	/**
	 * 源清单请求过滤
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
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
				}else if("checkNativeTp".equals(param)){
					listTps = checkNativeTp(requestDate,request,response);
				}else if("checkNative".equals(param)){
					listTps = checkNative(requestDate,request,response);
				}else if("find_coupling".equals(param)){
					listTps = find_coupling(requestDate,request,response);
				}else if("add_coupling".equals(param)){
					listTps = add_coupling(requestDate,request,response);
				}else if("update_coupling".equals(param)){
					listTps = update_coupling(requestDate,request,response);
				}else if("delete_coupling".equals(param)){
					listTps = delete_coupling(requestDate,request,response);
				}else if("find_couplingNativeTp".equals(param)){
					listTps = find_couplingNativeTp(requestDate,request,response);
				}else if("find_couplingNatives".equals(param)){
					listTps = find_couplingNatives(requestDate,request,response);
				}else if("findCityAndIndustryById".equals(param)){
					listTps = findCityAndIndustryById(requestDate,request,response);
				}else if("saveCoupling".equals(param)){
					listTps = saveCoupling(requestDate,request,response);
				}else if("resultCouplingMessage".equals(param)){
					listTps = resultCouplingMessage(requestDate,request,response);
				}else if("lookByCouplingId".equals(param)){
					listTps = lookByCouplingId(requestDate,request,response);
				}else if("verifyByNationName".equals(param)){
					listTps = verifyByNationName(requestDate,request,response);
				}else if("verifyByNativeTpName".equals(param)){
					listTps = verifyByNativeTpName(requestDate,request,response);
				}else if("verifyByNativeName".equals(param)){
					listTps = verifyByNativeName(requestDate,request,response);
				}else if("verifyByCouplingName".equals(param)){
					listTps = verifyByCouplingName(requestDate,request,response);
				}
				else if("".equals(param)){
					return AmpcResult.build(1003, "NativeAndNationController 请求方法参数异常!");
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
			
			String queryNames = data.get("queryName").toString();
			String queryName;
			if(queryNames==null){
				queryName="";
			}else{
				queryName=queryNames;
			}
			//添加查询参数
			Map nationMap=new HashMap();
			nationMap.put("userId", userId);
			//分页开始条数
			nationMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			//分页结束条数
			nationMap.put("endTotal",pageNumber*pageSize);
			nationMap.put("queryName",queryName);
			//查询分页数据
			List<Map> list=tEsNationMapper.selectAllNation(nationMap);
			//查询总条数
			int total=tEsNationMapper.selectTotalNation(nationMap);
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
			
			param=data.get("pageNum");
				//进行参数判断
			if(!RegUtil.CheckParameter(param, null, null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			int pageNum = Integer.valueOf(param.toString());
			
			param=data.get("pageSize");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, null, null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			int pageSize = Integer.valueOf(param.toString());
			
			//查询本地清单模板前添加参数
			Map tEsNativeTpMap = new HashMap(); 
			tEsNativeTpMap.put("userId", userId);
			tEsNativeTpMap.put("startTotal", (pageNum*pageSize)-pageSize+1);
			tEsNativeTpMap.put("endTotal", pageNum*pageSize);
			//查询本地清单模板
			List<Map> listTp=tEsNativeTpMapper.selectAllNativeTp(tEsNativeTpMap);
			//循环全部模板
			for(int k=0;k<listTp.size();k++){
				//循环获取每个模板
				Map tEsNativeTpsMap=listTp.get(k);
				//获取每个模板ID
				Long es_native_Id= Long.valueOf(tEsNativeTpsMap.get("esNativeTpId").toString());
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
					tesNative.put("id", "qd_"+k+""+i);
					tesNative.put("esNativeId", tEsNative.getEsNativeId());
					tesNative.put("esNativeTpName", tEsNative.getEsNativeName());
					tesNative.put("esNativeRelationId", tEsNative.getEsNativeRelationId());
					tesNative.put("esNativeTpId", tEsNative.getEsNativeTpId());
					tesNative.put("esNativeTpYear", tEsNative.getEsNativeYear());
					tesNative.put("esUploadTpTime", tEsNative.getEsUploadTime());
					tesNative.put("esVersion", tEsNative.getEsVersion());
					tesNative.put("isEffective", tEsNative.getIsEffective());
					tesNative.put("updateTime", tEsNative.getUpdateTime());
					tesNative.put("userId", tEsNative.getUserId());
					tesNative.put("addTime", tEsNative.getAddTime());
					tesNative.put("deleteTime", tEsNative.getDeleteTime());
					tesNative.put("esCodeRange", tEsNative.getEsCodeRange());
					tesNative.put("esComment", tEsNative.getEsComment());
					tesNative.put("filePath", tEsNative.getFilePath());
					tesNative.put("isVerify", tEsNative.getIsVerify());
					//把map对象添加到集合中
					tpDataList.add(tesNative);
				}
				
				//将查询到清单模板对应所拥有的清单数据集合添加到map中
				tEsNativeTpsMap.put("id", "mb_"+k);
				tEsNativeTpsMap.put("children", tpDataList);
				//添加配置页面收缩配置
				tEsNativeTpsMap.put("state", "closed");
			}
			int total=tEsNativeTpMapper.selectTotalNativeTp(userId);
			
			Map nativeTpMap=new HashMap();
			nativeTpMap.put("rows", listTp);
			nativeTpMap.put("total", total);
			nativeTpMap.put("page", pageNum);
			
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的本地清单信息成功!");
			return AmpcResult.ok(nativeTpMap);
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
			
			//添加数据
			TEsNation tEsNation=new TEsNation();
			tEsNation.setUserId(userId);
			tEsNation.setEsNationName(nationName);
			tEsNation.setEsNationYear(nationYear);
			tEsNation.setNationRemark(nationRemark);
			//插入数据
			int total=tEsNationMapper.insertSelective(tEsNation);
			Map msgMap=new HashMap();
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 创建全国清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 创建全国清单信息失败!");
			}
			
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
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息失败!");
			}
			
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
			if(total>0){
				//成功
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息成功!");
			}else{
				//失败
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息失败!");
			}
			
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
			
			//读取配置文件路径
			String nativefilePath = new String((configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName).toString().getBytes("iso-8859-1"),"utf-8");
			String nativesfilePath = new String(( configUtil.getFtpURL()+"/"+userId+"/").toString().getBytes("iso-8859-1"),"utf-8");
			//用于展示给用户的路径
			String native_filePath = "/"+userId+"/"+nativeTpName;
			
			//获取file对象
			File files =new File(nativesfilePath);
			File file =new File(nativefilePath);
			//目录已经存在
			if(files.exists()){
				LogUtil.getLogger().info(files+"该模板目录已经存在");
				//判断是否包含该文件模板
				if(file.exists()){
					LogUtil.getLogger().info(file+"该模板父级目录已经存在");
				}else{
					//模板文件夹不存在,进行创建
					file.mkdir();
				}
			}else{
				//不存在进行创建目录
				files.mkdirs();
				if(file.exists()){
					LogUtil.getLogger().info(files+"文件已存在!");
				}else{
					//不存在创建文件
					//模板文件夹创建完成
					file.mkdir();
					LogUtil.getLogger().info(files+"模板文件夹创建完成!");
				}
			}
			
			//文件输出路径
			String nativeOutPath = configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName+"/outPath";
			//用于展示给用户的路径
			String nativesOutPath = "/"+userId+"/"+nativeTpName+"/outPath";
			
			//获取file对象
			File outPath =new File(nativeOutPath);
			File outPaths =new File(nativesfilePath);
			//目录已经存在
			if(outPath.exists()){
				LogUtil.getLogger().info(outPath+"输出目录已经存在!");
				//判断是否包含该文件模板
			}else{
				//不存在进行创建目录
				outPath.mkdirs();
			}
			
			//添加数据
			TEsNativeTp tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			tEsNativeTp.setEsNativeTpName(nativeTpName);
			tEsNativeTp.setEsNativeTpYear(nativeTpYear);
			tEsNativeTp.setEsComment(nativeTpRemark);
			
			tEsNativeTp.setFilePath(native_filePath);
			tEsNativeTp.setEsNativeTpOutPath(nativesOutPath);
			//插入数据
			int total=tEsNativeTpMapper.insertSelective(tEsNativeTp);
			Map msgMap=new HashMap();
			//清单模板数据成功入库
			if(total>0){
				Map nativeTpMap=new HashMap();
				nativeTpMap.put("userId", userId);
				nativeTpMap.put("nativeTpName", nativeTpName);
				nativeTpMap.put("nativeTpYear", nativeTpYear);
				nativeTpMap.put("nativeTpRemark", nativeTpRemark);
				
				TEsNativeTp nativeTp=tEsNativeTpMapper.selectByKey(nativeTpMap);
				
				msgMap.put("msg", true);
				msgMap.put("filePath", nativefilePath);
				msgMap.put("nativeTpId","mb_"+nativeTp.getEsNativeTpId());
				LogUtil.getLogger().info("NativeAndNationController 创建本地清单模板信息成功!");
			}else{
				//清单模板数据入库失败
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 创建本地清单模板信息失败!");
			}
			
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
				LogUtil.getLogger().error("NativeAndNationController 清单模板d为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板Id为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			//获取清单年份
			param=data.get("nativeTpYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板年份为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板年份为空或出现非法字符!");
			}
			Short nativeTpYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nativeTpRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板备注为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板备注为空或出现非法字符!");
			}
			String nativeTpRemark=param.toString();
			
			param=data.get("filePath");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板上传路径为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板上传路径为空或出现非法字符!");
			}
			String filePath=param.toString();
			
			param=data.get("nativeTpoutPath");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板错误输出路径路径为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板错误输出路径为空或出现非法字符!");
			}
			String nativeTpoutPath=param.toString();
			
			param=data.get("newNativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板错误输出路径路径为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板错误输出路径为空或出现非法字符!");
			}
			String newNativeTpName=param.toString();
			
			//修改FTP中对应文件夹的名称
//			File file=new File(configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName);
			File file=new File(new String((configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName).toString().getBytes("iso-8859-1"),"utf-8"));
			//判断之前创建的文件夹是否存在
			if(file.exists()){
				//如果存在修改文件夹名称为修改后的新名称
//				File newfile=new File(configUtil.getFtpURL()+"/"+userId+"/"+newNativeTpName);
				File newfile=new File(new String((configUtil.getFtpURL()+"/"+userId+"/"+newNativeTpName).toString().getBytes("iso-8859-1"),"utf-8"));
				boolean bool=file.renameTo(newfile);
				LogUtil.getLogger().info(String.valueOf(bool)+"true为清单模板文件夹名称修改成功");
			}
			
			//添加数据
			TEsNativeTp tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			tEsNativeTp.setEsNativeTpName(newNativeTpName);
			tEsNativeTp.setEsNativeTpYear(nativeTpYear);
			tEsNativeTp.setEsComment(nativeTpRemark);
			tEsNativeTp.setEsNativeTpId(nativeTpId);
			//修改文件上传路径
			String filePathStr = "/"+userId+"/"+newNativeTpName;
			//修改文件输出路径
			String outPathStr = "/"+userId+"/"+newNativeTpName+"/outPath";
			tEsNativeTp.setFilePath(filePathStr);
			tEsNativeTp.setEsNativeTpOutPath(outPathStr);
			//更新数据
			int total=tEsNativeTpMapper.updateByIdSelective(tEsNativeTp);
			Map msgMap=new HashMap();
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 编辑全国清单信息失败!");
			}
			
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
			if(total>0){
				//成功
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息成功!");
			}else{
				//失败
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 删除全国清单信息失败!");
			}
			
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
			//获取清单备注
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板id为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板id为空或出现非法字符!");
			}
			Long nativeTpId=Long.parseLong(param.toString());
			
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板id为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板id为空或出现非法字符!");
			}
			String nativeTpName=param.toString();
			
			//服务器配置路径
//			String nativefilePath = new String((configUtil.getFtpURL()+""+userId+"/"+nativeTpName+"/"+nativeName).toString().getBytes("iso-8859-1"),"utf-8");
//			String nativesfilePath = new String((configUtil.getFtpURL()+""+userId+"/"+nativeTpName+"/"+nativeName).toString().getBytes("iso-8859-1"),"utf-8");
//			String natives_filePath = "/"+userId+"/"+nativeTpName+"/"+nativeName;
//			
//			//获取file对象
//			File files =new File(nativesfilePath);
//			File file =new File(nativefilePath);
//			//目录已经存在
//			if(files.exists()){
//				LogUtil.getLogger().info("NativeAndNationController 本地清单目录已存在!");
//				//判断是否包含该文件模板
////				if(file.exists()){
////					System.out.println("该模板已经存在");
////				}else{
////					//模板文件夹不存在,进行创建
////					file.mkdir();
////				}
//			}else{
//				//不存在进行创建目录
//				files.mkdir();
//				LogUtil.getLogger().info("NativeAndNationController 本地清单目录已创建!");
////				if(file.exists()){
////					System.out.println("文件已存在");
////				}else{
////					//不存在创建文件
////					//模板文件夹创建完成
////					file.mkdir();
////					System.out.println("模板文件夹创建完成");
////				}
//			}
			
			String nativesfilePath = new String((configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName+"/"+nativeName).toString().getBytes("iso-8859-1"),"utf-8");
			LogUtil.getLogger().info(nativesfilePath);
			//调用接口所需参数
			String natives_filePath = "/"+userId+"/"+nativeTpName+"/"+nativeName;
			
			//获取file对象
			File files =new File(nativesfilePath);
			//目录已经存在
			if(files.exists()){
				LogUtil.getLogger().info(files+"本地清单文件夹已经存在!");
			}else{
				//不存在进行创建目录
				files.mkdir();
				LogUtil.getLogger().info(files+"本地清单文件夹创建成功");
			}
			
			//添加数据
			TEsNative tEsNative=new TEsNative();
			tEsNative.setUserId(userId);
			tEsNative.setEsNativeName(nativeName);
			tEsNative.setEsNativeYear(nativeYear);
			tEsNative.setEsComment(nativeRemark);
			tEsNative.setEsNativeTpId(nativeTpId);
			tEsNative.setFilePath(natives_filePath);
			//插入数据
			int total=tEsNativeMapper.insertSelective(tEsNative);
			Map msgMap=new HashMap();
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 创建本地清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 创建本地清单信息失败!");
			}
			
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 创建本地清单异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 创建本地清单异常!");
		}
	}
	
	/**
	 * 校验清单模板数据
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult checkNativeTp(@RequestBody Map<String, Object> requestDate,
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
			
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			param=data.get("esNativeTpOutPath");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 模板文件校验出错时输出路径为空或出现非法字符!");
				return AmpcResult.build(1003, "模板文件校验出错时输出路径为空或出现非法字符!");
			}
			String esNativeTpOutPath = param.toString();
			
			//读取配置文件路径
			String nativefilePath = new String((configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName).toString().getBytes("iso-8859-1"),"utf-8");
			String nativesfilePath = new String((configUtil.getFtpURL()+"/"+userId+"/").toString().getBytes("iso-8859-1"),"utf-8");
			//调用接口所需参数
			String native_filePath = "/"+userId+"/"+nativeTpName;
			
			//获取file对象
			File files =new File(nativesfilePath);
			File file =new File(nativefilePath);
			//目录已经存在
			if(files.exists()){
				System.out.println("目录已经存在!");
				//判断是否包含该文件模板
				if(file.exists()){
					System.out.println("该模板已经存在");
				}else{
					//模板文件夹不存在,进行创建
					file.mkdir();
				}
			}else{
				//不存在进行创建目录
				files.mkdirs();
				if(file.exists()){
					System.out.println("文件已存在");
				}else{
					//不存在创建文件
					//模板文件夹创建完成
					file.mkdir();
					System.out.println("模板文件夹创建完成");
				}
			}
			
			Map msgMap = new HashMap();
			//调用校验数据函数
			boolean bool=excelToDateController.deleteExcelData(nativeTpId, userId);
			if(bool==true){
				Map  sector =excelToDateController.update_SectorData(userId,nativeTpId,native_filePath+ "/"+"应急系统新_4行业匹配.xlsx",esNativeTpOutPath);
				if(sector==null){
					Map  queryExcel =excelToDateController.update_QueryExcelData(userId,nativeTpId,native_filePath+ "/"+"应急系统新_2筛选逻辑.xlsx",esNativeTpOutPath);
					if(queryExcel==null){
						Map  sectorDocExcel =excelToDateController.update_SectorDocExcelData(userId,nativeTpId,native_filePath+ "/"+"应急系统新_1描述文件.xlsx",esNativeTpOutPath);
						if(sectorDocExcel==null){
							
							//用户id,模板id,措施id
							Map save_MS=excelToDateController.save_MS(userId,nativeTpId,"系统内置");
							if(save_MS==null){
								//成功
								//修改清单模板
								TEsNativeTp	tEsNativeTp=new TEsNativeTp();
								tEsNativeTp.setUserId(userId);
								tEsNativeTp.setEsNativeTpId(nativeTpId);
								//修改为1表示已校验
								tEsNativeTp.setIsVerify("1");
								int total=tEsNativeTpMapper.updateByIdSelective(tEsNativeTp);
								if(total==1){
									msgMap.put("msg", true);
									LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息数据更新成功!");
								}else{
									msgMap.put("msg", false);
									LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息数据更新失败!");
								}
							}else{
								//失败
								msgMap.put("msg", "保存到措施模版表失败!");
								LogUtil.getLogger().info("NativeAndNationController 保存到措施模版表失败!");
							}
						}else{
							LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息失败!");
							return AmpcResult.ok(sectorDocExcel);
						}
					}else{
						LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息失败!");
						return AmpcResult.ok(queryExcel);
					}
				}else{
					LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息失败!");
					return AmpcResult.ok(sector);
				}
			}else{
				LogUtil.getLogger().info("NativeAndNationController 删除Excel信息失败!");
				return AmpcResult.build(1001, "删除Excel信息失败!");
			}
			
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 校验本地清单模板异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验本地清单模板异常!");
		}
	}
	
	/**
	 * 校验清单数据
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult checkNative(@RequestBody Map<String, Object> requestDate,
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
			
			param=data.get("nativeId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单ID为空或出现非法字符!");
			}
			Long nativeId = Long.parseLong(param.toString());
			
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nativeName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nativeName = param.toString();
			
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			param=data.get("esNativeTpOutPath");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 模板文件校验出错时输出路径为空或出现非法字符!");
				return AmpcResult.build(1003, "模板文件校验出错时输出路径为空或出现非法字符!");
			}
			String esNativeTpOutPath = param.toString();
			
			//读取配置文件路径
			String nativefilePath = configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName+"/"+nativeName;
			String nativesfilePath = configUtil.getFtpURL()+"/"+userId+"/"+nativeTpName+"/"+nativeName;
			//调用接口所需参数
			String native_filePath = "/"+userId+"/"+nativeTpName+"/"+nativeName;
			
			//获取file对象
			File files =new File(nativesfilePath);
			File file =new File(nativefilePath);
			//目录已经存在
			if(files.exists()){
				System.out.println("目录已经存在!");
				//判断是否包含该文件模板
				if(file.exists()){
					System.out.println("该模板已经存在");
				}else{
					//模板文件夹不存在,进行创建
					file.mkdir();
				}
			}else{
				//不存在进行创建目录
				files.mkdirs();
				if(file.exists()){
					System.out.println("文件已存在");
				}else{
					//不存在创建文件
					//模板文件夹创建完成
					file.mkdir();
					System.out.println("模板文件夹创建完成");
				}
			}
			
			Map msgMap = new HashMap();
//			String esNativeTpOutPathre = esNativeTpOutPath.replace("/", "\\");
			String esNativeTpOutPathre = esNativeTpOutPath;
			//调用校验数据函数
			Map  nativeExcel =excelToDateController.check_nativeExcelData(userId, nativeTpId, nativeId, native_filePath+ "/"+"应急系统新_3清单数据.xlsx",esNativeTpOutPathre);
				if(nativeExcel==null){
				//修改清单模板
				TEsNative tEsNative = new TEsNative();
				tEsNative.setEsNativeId(nativeId);
				tEsNative.setIsVerify("1");
				int total=tEsNativeMapper.updateByPrimaryKeySelective(tEsNative);
				if(total==1){
					msgMap.put("msg", true);
					LogUtil.getLogger().info("NativeAndNationController 校验本地清单信息数据更新成功!");
				}else{
					msgMap.put("msg", false);
					LogUtil.getLogger().info("NativeAndNationController 校验本地清单信息数据更新失败!");
				}
			}else{
				LogUtil.getLogger().info("NativeAndNationController 校验本地清单信息失败!");
				return AmpcResult.ok(nativeExcel);
			}
				
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 校验本地清单异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验本地清单异常!");
		}
	}
	
	/**
	 * 查询耦合清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult find_coupling(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
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
			
			String queryName = data.get("queryName").toString();
			
			//添加查询参数
			Map couplingMap=new HashMap();
			couplingMap.put("userId", userId);
			//分页开始条数
			couplingMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			//分页结束条数
			couplingMap.put("endTotal",pageNumber*pageSize);
			couplingMap.put("queryName",queryName);
			//查询分页数据
			List<Map> list=tEsCouplingMapper.selectAllCoupling(couplingMap);
			//查询总条数
			int total=tEsCouplingMapper.selectTotalCoupling(couplingMap);
			//存放数据集合
			List couplingList = new ArrayList();
			List MeiccityconfigList = new ArrayList();
			//循环修改每个数据中的值
			for(int i=0;i<list.size();i++){
				Map tEsCouplingMap = list.get(i);
				//查询每条清单耦合数据是否被任务使用
				int result = tMissionDetailMapper.selectTtotalByCouplingId(Long.valueOf(tEsCouplingMap.get("esCouplingId").toString()));
				if(result>0){
					//已经被使用
					tEsCouplingMap.put("employ", 1);
				}else{
					//未使用
					tEsCouplingMap.put("employ", 0);
				}
				//clob类型字段的处理
				Clob clob = (Clob) tEsCouplingMap.get("esCouplingMeiccityconfig");
				String detailinfo = "";
				//判断是否为空
			    if(clob != null){
			    	//进行格式转换
			    	detailinfo = clob.getSubString((long)1,(int)clob.length());
			    	
			    	//覆盖键值重新添加数据
				    //存放新的清单数据
				    String strs="["+detailinfo+"]";
				    List<Map> listConfig = mapper.readValue(strs, List.class);
				    
				    for(int j=0;j<listConfig.size();j++){
				    	Map	mapConfig = (Map)listConfig.get(j);
				    	String nativeName= mapConfig.get("meicCityId").toString();
				    	String meicCityName = tEsNativeMapper.selectNameByNativeId(Long.valueOf(nativeName));
				    	String _parameter = mapConfig.get("regionId").toString()+"00";
				    	String regionName = tAddressMapper.selectNameByCode(_parameter);
				    	//清单id
				    	mapConfig.put("meicCityId", meicCityName);
				    	//城市id
				    	mapConfig.put("regionId", regionName);
				    	MeiccityconfigList.add(mapConfig);
				    }
			    }
			    
			    String tEsNationName;
			    String tEsNativeTpName;
			    if(tEsCouplingMap.get("esCouplingNationId")!=null){
			    	//查询全国清单名称
					TEsNation tEsNation = tEsNationMapper.selectByPrimaryKey(Long.valueOf(tEsCouplingMap.get("esCouplingNationId").toString()));
					if(tEsNation==null){
						tEsNationName = "-";
					}else{
						tEsNationName = tEsNation.getEsNationName().toString();
					}
					
			    }else{
			    	tEsNationName = "-";
			    }
			    
			    if(tEsCouplingMap.get("esCouplingNativetpId")!=null){
			    	//查询本地清单名称
					TEsNativeTp tEsNativeTp = tEsNativeTpMapper.selectByPrimaryKey(Long.valueOf(tEsCouplingMap.get("esCouplingNativetpId").toString()));
					if(tEsNativeTp==null){
						tEsNativeTpName = "-";
					}else{
						tEsNativeTpName = tEsNativeTp.getEsNativeTpName();
					}
			    }else{
			    	tEsNativeTpName = "-";
			    }
				
			    tEsCouplingMap.put("esCouplingNationId", tEsNationName);
			    tEsCouplingMap.put("esCouplingNativeTpId", tEsNativeTpName);
				tEsCouplingMap.put("esCouplingMeiccityconfig", MeiccityconfigList);
				//添加到集合中
				couplingList.add(tEsCouplingMap);
			}
			
			//返回页面的数据
			Map couplingsMap=new HashMap();
			couplingsMap.put("rows", couplingList);
			couplingsMap.put("total", total);
			couplingsMap.put("page", pageNumber);
			
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的全国清单信息成功!");
			return AmpcResult.ok(couplingsMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询当前用户下的全国清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询当前用户下的全国清单信息异常!");
		}
	}
	
	/**
	 * 创建耦合清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult add_coupling(@RequestBody Map<String, Object> requestDate,
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
			param=data.get("couplingName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单名称为空或出现非法字符!");
			}
			String couplingName = param.toString();
			
			//获取清单年份
			param=data.get("couplingYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单年份为空或出现非法字符!");
			}
			Short couplingYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("couplingDesc");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单备注为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单备注为空或出现非法字符!");
			}
			String couplingDesc=param.toString();
			
			//添加数据
			TEsCoupling tEsCoupling=new TEsCoupling();
			tEsCoupling.setUserId(userId);
			tEsCoupling.setEsCouplingName(couplingName);
			tEsCoupling.setEsCouplingYear(couplingYear);
			tEsCoupling.setEsCouplingDesc(couplingDesc);
			//插入数据
			int total=tEsCouplingMapper.insertSelective(tEsCoupling);
			Map msgMap=new HashMap();
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 创建耦合清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 创建耦合清单信息失败!");
			}
			
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 创建耦合清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 创建耦合清单信息异常!");
		}
	}
	
	/**
	 * 编辑耦合清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult update_coupling(@RequestBody Map<String, Object> requestDate,
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
			param=data.get("couplingId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单Id为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单Id为空或出现非法字符!");
			}
			Long couplingId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("couplingName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单名称为空或出现非法字符!");
			}
			String couplingName = param.toString();
			
			//获取清单年份
			param=data.get("couplingYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单年份为空或出现非法字符!");
			}
			Short couplingYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("couplingDesc");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单备注为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单备注为空或出现非法字符!");
			}
			String couplingDesc=param.toString();
			
			//添加数据
			TEsCoupling tEsCoupling=new TEsCoupling();
			tEsCoupling.setUserId(userId);
			tEsCoupling.setEsCouplingName(couplingName);
			tEsCoupling.setEsCouplingYear(couplingYear);
			tEsCoupling.setEsCouplingDesc(couplingDesc);
			tEsCoupling.setEsCouplingId(couplingId);
			//更新数据
			int total=tEsCouplingMapper.updateByIdSelective(tEsCoupling);
			Map msgMap=new HashMap();
			if(total>0){
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 编辑耦合清单信息成功!");
			}else{
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 编辑耦合清单信息失败!");
			}
			
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 编辑耦合清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 编辑耦合清单信息异常!");
		}
	}
	
	/**
	 * 删除耦合清单
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult delete_coupling(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			
			Object param=data.get("couplingId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单ID为空或出现非法字符!");
			}
			//耦合清单id
			Long couplingId = Long.parseLong(param.toString());
			int total=tEsCouplingMapper.deleteByPrimaryKey(couplingId);
			Map msgMap=new HashMap();
			if(total>0){
				//成功
				msgMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 删除耦合清单信息成功!");
			}else{
				//失败
				msgMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 删除耦合清单信息失败!");
			}
			
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 删除耦合清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 删除耦合清单信息异常!");
		}
	}
	
	/**
	 * 查询耦合清单模板数据
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult find_couplingNativeTp(@RequestBody Map<String, Object> requestDate,
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
			TEsNativeTp tEsNativeTp = new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			//查询本地清单模板
			List<Map> listTp=tEsNativeTpMapper.selecttesNativeTp(tEsNativeTp);
			
			for(int i=0;i<listTp.size();i++){
				Map NativeTpMap= listTp.get(i);
				
				TEsNative tEsNative = new TEsNative();
				tEsNative.setEsNativeTpId(Long.valueOf(NativeTpMap.get("esNativeTpId").toString()));
				tEsNative.setUserId(userId);
				//查询总条数
				int total=tEsNativeMapper.selectTotalNative(tEsNative);
				//有效
				if(total>0){
					
				}else{
					//无效
					listTp.remove(i);
				}
			}
			
			Map nativeTpMap=new HashMap();
			nativeTpMap.put("rows", listTp);
			
			LogUtil.getLogger().info("NativeAndNationController 查询清单信息成功!");
			return AmpcResult.ok(nativeTpMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询清单模板信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询清单模板信息异常!");
		}
	}
	
	/**
	 * 根据模板ID查询清单数据
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult find_couplingNatives(@RequestBody Map<String, Object> requestDate,
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
			
			param=data.get("nativeTpId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "模板ID为空或出现非法字符!");
			}
			// 模板id
			Long nativeTpId = Long.parseLong(param.toString());
			
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
			Map nativeMap=new HashMap();
			nativeMap.put("userId", userId);
			nativeMap.put("nativeTpId", nativeTpId);
			//分页开始条数
			nativeMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			//分页结束条数
			nativeMap.put("endTotal",pageNumber*pageSize);
			//查询分页数据
			List<Map> list=tEsNativeMapper.selectByNativeTpAllNative(nativeMap);
			
			TEsNative tEsNative = new TEsNative();
			tEsNative.setEsNativeTpId(nativeTpId);
			tEsNative.setUserId(userId);
			//查询总条数
			int total=tEsNativeMapper.selectTotalNative(tEsNative);
			//返回页面的数据
			Map nativesMap=new HashMap();
			nativesMap.put("rows", list);
			nativesMap.put("total", total);
			nativesMap.put("page", pageNumber);
			
			LogUtil.getLogger().info("NativeAndNationController 查询清单模板下的清单数据信息成功!");
			return AmpcResult.ok(nativesMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询清单模板下的清单数据信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询清单模板下的清单数据信息异常!");
		}
	}
	
	/**
	 * 根据id查询涉及到的城市和行业
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult findCityAndIndustryById(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单ID为空或出现非法字符!");
			}
			Long nationId = Long.parseLong(param.toString());
			
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			param=data.get("nativesId");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单ID为空或出现非法字符!");
			}
			String  nativesIdStr = param.toString();
			String nativeIds=nativesIdStr.substring(1, nativesIdStr.length() -1);
			String[] nativeIdsArray=nativeIds.split(",");
			JSONArray json = JSONArray.fromObject(nativeIdsArray);
			String str = json.toString();
			
			//此处会有调用晓东的接口，根据参数为清单id查询涉及到的城市id
			String yunURL=configUtil.getYunURL()+"/summary/regions";
			String getResult=ClientUtil.doPost(yunURL,str);
			Map contentMap=mapper.readValue(getResult, Map.class);
			String cityStr = contentMap.get("data").toString();
			JSONObject obj = new JSONObject();
			JSONObject objs= obj.fromObject(cityStr);
			//得到城市集合
			Map cityMap=mapper.readValue(objs.toString(), Map.class);
			
			Set<String> cityIdArray = new HashSet<String>();
			//遍历每个清单id下的城市
			for(Object cityKey:cityMap.keySet()){
				List cityStrs = (List)cityMap.get(cityKey);
				for (Object object : cityStrs) {
					String code=object.toString();
					code=code.substring(0,4);
					cityIdArray.add(code);
					
				}
			}
			
			//查询到的城市添加到Map集合中
			Map cityNameMap = new HashMap<String, Object>();
			for (String string : cityIdArray) {
				String tAddressName=tAddressMapper.selectCityNameById(string+"00");
				cityNameMap.put(string, tAddressName);
			}
			//循环全部城市ID
			//根据本地清单模板id查询涉及到的行业
			List<String> listTSectorExcel=tSectorExcelMapper.selectIndustryById(nativeTpId);
			
			Map cityAndIndustryMap= new HashMap<String, Object>();
			cityAndIndustryMap.put("cityNames", cityNameMap);
			cityAndIndustryMap.put("industryNames", listTSectorExcel);
			
			LogUtil.getLogger().info("NativeAndNationController 根据id查询涉及到的城市和行业信息成功!");
			return AmpcResult.ok(cityAndIndustryMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 根据id查询涉及到的城市和行业信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 根据id查询涉及到的城市和行业信息异常!");
		}
	}
	
	/**
	 * 保存耦合配置信息
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult saveCoupling(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单ID为空或出现非法字符!");
			}
			Long nationId = Long.parseLong(param.toString());
			
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			param=data.get("nativesId");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单ID为空或出现非法字符!");
			}
			String nativesId = param.toString();
			
			param=data.get("couplingId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单ID为空或出现非法字符!");
			}
			Long couplingId = Long.parseLong(param.toString());
			
			param=data.get("CouplingCity");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合后涉及的城市ID为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合后涉及的城市ID为空或出现非法字符!");
			}
			String  CouplingCity = param.toString();
			
			param=data.get("meicCityConfig");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单数据、行政区划、行业名称参数信息为空或出现非法字符!");
				return AmpcResult.build(1003, "清单数据、行政区划、行业名称参数信息为空或出现非法字符!");
			}
			String  meicCityConfig = param.toString();
			
			//调用晓东接口所需的参数
			Map couplingMap = new HashMap<String, Object>();
			//用户id
			couplingMap.put("userId", userId);
			//全国清单ID
//			couplingMap.put("meicId", nationId);
			//先写死
			couplingMap.put("meicId", "MEIC-2013");
			//固定前缀
			couplingMap.put("prefix", "MC");
			//耦合后的清单ID
			couplingMap.put("destId", couplingId);
			//模板ID
			couplingMap.put("templateId", nativeTpId);
			//返回结果的路径
			//读取config.properties配置文件的url
			String serverPath=configUtil.getServerPath()+"/NativeAndNation/doPost";
			String  resultUrl = serverPath;
			couplingMap.put("serverPath", resultUrl);
			//设置参数查询行业版本
			TSectorExcel tSectorExcel = new TSectorExcel();
			tSectorExcel.setUserId(userId);
			//模板ID参数
			tSectorExcel.setDetailedListId(nativeTpId);
			//版本号参数
			//查询得到版本号
			String versionNumber = tSectorExcelMapper.selectVersionsExcelId(tSectorExcel);
			//如果查询结果为空去除用户id参数再进行查询
			if(versionNumber.equals("")||versionNumber==null){
				 tSectorExcel=new TSectorExcel();
				 tSectorExcel.setDetailedListId(nativeTpId);
				 versionNumber=tSectorExcelMapper.selectVersionsExcelId(tSectorExcel);
			}
			//行业版本
			couplingMap.put("versionExcelId", versionNumber);
			JSONArray meicCityConfigArr = JSONArray.fromObject(meicCityConfig); 
			couplingMap.put("meicCityConfig", meicCityConfigArr);
			//转化为json对象
			JSONObject json = JSONObject.fromObject(couplingMap);
			//调用晓东的接口
			String yunURL=configUtil.getYunURL()+"/coupling/submit";
			String getResult=ClientUtil.doPost(yunURL,json.toString());
			Map detamap=mapper.readValue(getResult, Map.class);
			Map messageMap = new HashMap<String, Object>();
			//如果状态执行成功
			if("success".equals(detamap.get("status"))){
				//添加所需参数更新耦合配置信息
				TEsCoupling tEsCoupling = new TEsCoupling();
				tEsCoupling.setEsCouplingId(couplingId);
				tEsCoupling.setEsCouplingCity(CouplingCity);
				tEsCoupling.setEsCouplingNativetpId(nativeTpId);
				tEsCoupling.setEsCouplingNationId(nationId);
				//该字段类型需修改为String类型
				tEsCoupling.setEsCouplingNativeId(nativesId.substring(1, nativesId.length()-1));
				tEsCoupling.setEsCouplingMeiccityconfig(meicCityConfig.substring(1, meicCityConfig.length()-1));
				//更新耦合清单数据
				int result= tEsCouplingMapper.updateDataByPrimaryKey(tEsCoupling);
				if(result>0){
					//更新成功
					messageMap.put("msg", true);
					LogUtil.getLogger().info("NativeAndNationController 保存耦合配置信息数据更新成功!");
				}else{
					//更新失败
					messageMap.put("msg", false);
					LogUtil.getLogger().info("NativeAndNationController 保存耦合配置信息数据更新失败!");
				}
			}else{
				//调用晓东接口失败
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 保存耦合配置信息失败!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 保存耦合配置信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 保存耦合配置信息异常!");
		}
	}
	
	/**
	 * 返回的耦合消息
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult resultCouplingMessage(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> data = (Map) requestDate.get("data");
			//判断状态是否成功
//			Object param=data.get("status");
//			//进行参数判断
//			if(!RegUtil.CheckParameter(param, "Long", null, false)){
//				LogUtil.getLogger().error("NativeAndNationController 耦合清单ID为空或出现非法字符!");
//				return AmpcResult.build(1003, "耦合清单ID为空或出现非法字符!");
//			}
//			String status = param.toString();
//			if("success".equals(status)){
//				//请求成功
//			}else{
//				//请求失败
//			}
			
			//获取耦合清单D
			Object param=data.get("destId");
//			param=data.get("destId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单ID为空或出现非法字符!");
			}
			Long tEsCouplingId = Long.parseLong(param.toString());
			
			//添加更新所需耦合清单id参数
			TEsCoupling tEsCoupling = new TEsCoupling();
			tEsCoupling.setEsCouplingId(tEsCouplingId);
			//更新耦合清单状态为3成功
			int result = tEsCouplingMapper.updateStatusByPrimaryKey(tEsCoupling);
			//返回调用者的信息
			Map messageMap = new HashMap<String, Object>();
			if(result>0){
				//更新成功
				messageMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 更新耦合配置状态数据更新成功!");
			}else{
				//更新失败
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 更新耦合配置状态数据更新失败!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 更新耦合配置状态异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 更新耦合配置状态异常!");
		}
	}
	
	/**
	 * 查看耦合清单详细信息
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult lookByCouplingId(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("couplingId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单ID为空或出现非法字符!");
			}
			Long couplingId = Long.parseLong(param.toString());
			
			param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单ID为空或出现非法字符!");
			}
			Long nationId = Long.parseLong(param.toString());
			
			param=data.get("nativeTpId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单模板ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单模板ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			param=data.get("nativesId");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单ID为空或出现非法字符!");
			}
			String nativesId = param.toString();
			
			param=data.get("meicCityConfig");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单数据、行政区划、行业名称参数信息为空或出现非法字符!");
				return AmpcResult.build(1003, "清单数据、行政区划、行业名称参数信息为空或出现非法字符!");
			}
			String  meicCityConfig = param.toString();
			//根据耦合ID查询信息
			Map tEsCouplingMap = tEsCouplingMapper.selectCouplingByPrimaryKey(couplingId);
			//查询全国清单名称
			TEsNation tEsNation = tEsNationMapper.selectByPrimaryKey(nationId);
			//查询本地清单名称
			TEsNativeTp tEsNativeTp  =tEsNativeTpMapper.selectByPrimaryKey(nativeTpId);
			//添加全国清单名称
			tEsCouplingMap.put("nationName", tEsNation.getEsNationName().toString());
			//添加本地清单名称
			tEsCouplingMap.put("nativeTpName", tEsNativeTp.getEsNativeTpName());
			
			
			LogUtil.getLogger().info("NativeAndNationController 查询耦合清单详细信息成功!");
			return AmpcResult.ok(tEsCouplingMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询耦合清单详细信息状态异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询耦合清单详细信息状态异常!");
		}
	}
	
	/**
	 * 校验全国清单名称
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult verifyByNationName(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("nationName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 全国清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "全国清单名称为空或出现非法字符!");
			}
			String nationName = param.toString();
			
			TEsNation tEsNation = new TEsNation();
			tEsNation.setEsNationName(nationName);
			int result = tEsNationMapper.verifyNationName(tEsNation);
			//返回调用者的信息
			Map messageMap = new HashMap<String, Object>();
			if(result>0){
				//名称已被使用
				messageMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 全国清单名称已被使用!");
			}else{
				//未查询到该名称的数据
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 全国清单名称未被使用!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 校验全国清单名称异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验全国清单名称异常!");
		}
	}
	
	/**
	 * 校验本地清单模板名称
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult verifyByNativeTpName(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单模板名称为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单模板名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			TEsNativeTp tEsNativeTp = new TEsNativeTp();
			tEsNativeTp.setEsNativeTpName(nativeTpName);
			int result = tEsNativeTpMapper.verifyNativeTpName(tEsNativeTp);
			//返回调用者的信息
			Map messageMap = new HashMap<String, Object>();
			if(result>0){
				//名称已被使用
				messageMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 本地清单模板名称已被使用!");
			}else{
				//未查询到该名称的数据
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 本地清单模板名称未被使用!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 校验本地清单模板名称异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验本地清单模板名称异常!");
		}
	}
	
	/**
	 * 校验本地清单名称
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult verifyByNativeName(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("nativeName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 本地清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "本地清单名称为空或出现非法字符!");
			}
			String nativeName = param.toString();
			
			TEsNative tEsNative = new TEsNative();
			tEsNative.setEsNativeName(nativeName);
			int result = tEsNativeMapper.verifyNativeName(tEsNative);
			//返回调用者的信息
			Map messageMap = new HashMap<String, Object>();
			if(result>0){
				//名称已被使用
				messageMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 本地清单名称已被使用!");
			}else{
				//未查询到该名称的数据
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 本地清单名称未被使用!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 校验本地清单名称异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验本地清单名称异常!");
		}
	}
	
	/**
	 * 校验耦合清单名称
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	public AmpcResult verifyByCouplingName(@RequestBody Map<String, Object> requestDate,
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
			Long userId = Long.parseLong(param.toString());
			
			param=data.get("couplingName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 耦合清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "耦合清单名称为空或出现非法字符!");
			}
			String couplingName = param.toString();
			
			TEsCoupling tEsCoupling = new TEsCoupling();
			tEsCoupling.setEsCouplingName(couplingName);
			int result = tEsCouplingMapper.verifyCouplingName(tEsCoupling);
			//返回调用者的信息
			Map messageMap = new HashMap<String, Object>();
			if(result>0){
				//名称已被使用
				messageMap.put("msg", true);
				LogUtil.getLogger().info("NativeAndNationController 耦合清单名称已被使用!");
			}else{
				//未查询到该名称的数据
				messageMap.put("msg", false);
				LogUtil.getLogger().info("NativeAndNationController 耦合清单名称未被使用!");
			}
			
			return AmpcResult.ok(messageMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 校验耦合清单名称异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验耦合清单名称异常!");
		}
	}
	
}
