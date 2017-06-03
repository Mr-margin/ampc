package ampc.com.gistone.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TEsCouplingMapper;
import ampc.com.gistone.database.inter.TEsNationMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.database.inter.TEsNativeTpMapper;
import ampc.com.gistone.database.model.TEsCoupling;
import ampc.com.gistone.database.model.TEsNation;
import ampc.com.gistone.database.model.TEsNative;
import ampc.com.gistone.database.model.TEsNativeTp;
import ampc.com.gistone.extract.ExtractConfig;
import ampc.com.gistone.extract.ResultPathUtil;
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
	@Autowired
	public ExcelToDateController excelToDateController;
	
	private TEsNative tEsNative;
	
//	private TEsNativeTp tEsNativeTp;
	
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
			//分页开始条数
			nationMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			//分页结束条数
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
//			tEsNativeTp=new TEsNativeTp();
//			tEsNativeTp.setUserId(userId);
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
			
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的本地清单信息成功!");
			
			Map nativeTpMap=new HashMap();
			nativeTpMap.put("rows", listTp);
			nativeTpMap.put("total", total);
			nativeTpMap.put("page", pageNum);
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
			
			//读取配置文件路径
			String config = "/extract.properties";
			InputStream ins = getClass().getResourceAsStream(config);
			Properties pro = new Properties();
			try {
				pro.load(ins);
				extractConfig = new ExtractConfig();
				extractConfig.setLocalListingFilePath((String) pro.get("LocalListingFilePath"));
			} catch (FileNotFoundException e) {
				logger.error(config + " file does not exits!", e);
			} catch (IOException e) {
				logger.error("load " + config + " file error!", e);
			}
			//关闭输入流
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				logger.error("close " + config + " file error!", e);
			}
			//获取路径
			String nativefilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName;
			String nativesfilePath = pro.get("LocalListingFilePath")+""+userId+"/";
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
			
			
			//添加数据
			TEsNativeTp tEsNativeTp=new TEsNativeTp();
			tEsNativeTp.setUserId(userId);
			tEsNativeTp.setEsNativeTpName(nativeTpName);
			tEsNativeTp.setEsNativeTpYear(nativeTpYear);
			tEsNativeTp.setEsComment(nativeTpRemark);
			tEsNativeTp.setFilePath(nativefilePath);
			//插入数据
			int total=tEsNativeTpMapper.insertSelective(tEsNativeTp);
			Map msgMap=new HashMap();
			//清单模板数据成功入库
			if(total==1){
				Map nativeTpMap=new HashMap();
				nativeTpMap.put("userId", userId);
				nativeTpMap.put("nativeTpName", nativeTpName);
				nativeTpMap.put("nativeTpYear", nativeTpYear);
				nativeTpMap.put("nativeTpRemark", nativeTpRemark);
				
				TEsNativeTp nativeTp=tEsNativeTpMapper.selectByKey(nativeTpMap);
				
				msgMap.put("msg", true);
				msgMap.put("filePath", nativefilePath);
				msgMap.put("nativeTpId","mb_"+nativeTp.getEsNativeTpId());
			}else{
				//清单模板数据入库失败
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
			
			//读取配置文件路径
			String config = "/extract.properties";
			InputStream ins = getClass().getResourceAsStream(config);
			Properties pro = new Properties();
			try {
				pro.load(ins);
				extractConfig = new ExtractConfig();
				extractConfig.setLocalListingFilePath((String) pro.get("LocalListingFilePath"));
			} catch (FileNotFoundException e) {
				logger.error(config + " file does not exits!", e);
			} catch (IOException e) {
				logger.error("load " + config + " file error!", e);
			}
			//关闭输入流
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				logger.error("close " + config + " file error!", e);
			}
			//获取路径
			String nativefilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName+"/"+nativeName;
			String nativesfilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName+"/"+nativeName;
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
			
			//添加数据
			TEsNative tEsNative=new TEsNative();
			tEsNative.setUserId(userId);
			tEsNative.setEsNativeName(nativeName);
			tEsNative.setEsNativeYear(nativeYear);
			tEsNative.setEsComment(nativeRemark);
			tEsNative.setEsNativeTpId(nativeTpId);
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
				LogUtil.getLogger().error("NativeAndNationController 清单ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单ID为空或出现非法字符!");
			}
			Long nativeTpId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nativeTpName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			//读取配置文件路径
			String config = "/extract.properties";
			InputStream ins = getClass().getResourceAsStream(config);
			Properties pro = new Properties();
			try {
				pro.load(ins);
				extractConfig = new ExtractConfig();
				extractConfig.setLocalListingFilePath((String) pro.get("LocalListingFilePath"));
			} catch (FileNotFoundException e) {
				logger.error(config + " file does not exits!", e);
			} catch (IOException e) {
				logger.error("load " + config + " file error!", e);
			}
			//关闭输入流
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				logger.error("close " + config + " file error!", e);
			}
			//获取路径
			String nativefilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName;
			String nativesfilePath = pro.get("LocalListingFilePath")+""+userId+"/";
//			String nativefilePath = new String((pro.get("LocalListingFilePath")).toString().getBytes("iso-8859-1"),"utf-8");
//			String nativesfilePath = new String((pro.get("LocalListingFilePath")).toString().getBytes("iso-8859-1"),"utf-8");
			
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
			Map  sectorDocExcel =excelToDateController.update_SectorDocExcelData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_1描述文件.xlsx");
			if(sectorDocExcel==null){
				Map  queryExcel =excelToDateController.update_QueryExcelData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_2筛选逻辑.xlsx");
				if(queryExcel==null){
					Map  sector =excelToDateController.update_SectorData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_4行业匹配.xlsx");
					if(sector==null){
						//修改清单模板
						TEsNativeTp	tEsNativeTp=new TEsNativeTp();
						tEsNativeTp.setUserId(userId);
						tEsNativeTp.setEsNativeTpId(nativeTpId);
						tEsNativeTp.setIsEffective("1");
						int total=tEsNativeTpMapper.updateByIdSelective(tEsNativeTp);
						if(total==1){
							msgMap.put("msg", true);
							LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息成功!");
						}else{
							msgMap.put("msg", false);
						}
					}else{
						return AmpcResult.ok(sector);
					}
				}else{
					return AmpcResult.ok(queryExcel);
				}
			}else{
				return AmpcResult.ok(sectorDocExcel);
			}
			
			
//			Map  sectorDocExcel =excelToDateController.update_SectorDocExcelData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_1描述文件.xlsx");
//			if(sectorDocExcel==null){
//				Map  queryExcel =excelToDateController.update_QueryExcelData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_2筛选逻辑.xlsx");
//				if(queryExcel==null){
//					Map  sector =excelToDateController.update_SectorData(userId,nativeTpId,nativefilePath+ "/"+"应急系统新_4行业匹配.xlsx");
//					if(sector==null){
//						Map  nativeExcel =excelToDateController.check_nativeExcelData(userId, templateId, qdId, filePath)(nativefilePath+ "/"+"应急系统新_3清单数据_hb_ywjv11_QY3_CPH1.xlsx");
//						if(nativeExcel==null){
//							return AmpcResult.ok(nativeExcel);
//						}else{
//							return AmpcResult.ok(nativeExcel);
//						}
//					}else{
//						return AmpcResult.ok(sector);
//					}
//				}else{
//					return AmpcResult.ok(queryExcel);
//				}
//			}else{
//				return AmpcResult.ok(sectorDocExcel);
//			}
			
			
//			return AmpcResult.ok();
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
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nativeTpName = param.toString();
			
			//读取配置文件路径
			String config = "/extract.properties";
			InputStream ins = getClass().getResourceAsStream(config);
			Properties pro = new Properties();
			try {
				pro.load(ins);
				extractConfig = new ExtractConfig();
				extractConfig.setLocalListingFilePath((String) pro.get("LocalListingFilePath"));
			} catch (FileNotFoundException e) {
				logger.error(config + " file does not exits!", e);
			} catch (IOException e) {
				logger.error("load " + config + " file error!", e);
			}
			//关闭输入流
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				logger.error("close " + config + " file error!", e);
			}
			//获取路径
			String nativefilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName+"/"+nativeName;
			String nativesfilePath = pro.get("LocalListingFilePath")+""+userId+"/"+nativeTpName+"/"+nativeName;
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
			Map  nativeExcel =excelToDateController.check_nativeExcelData(userId, nativeTpId, nativeId, nativefilePath+ "/"+"应急系统新_3清单数据demo.xlsx");
				if(nativeExcel==null){
				//修改清单模板
				TEsNative tEsNative = new TEsNative();
				tEsNative.setEsNativeId(nativeId);
				tEsNative.setIsEffective("1");
				int total=tEsNativeMapper.updateByPrimaryKeySelective(tEsNative);
				if(total==1){
					msgMap.put("msg", true);
					LogUtil.getLogger().info("NativeAndNationController 校验本地清单模板信息成功!");
				}else{
					msgMap.put("msg", false);
				}
			}else{
				return AmpcResult.ok(nativeExcel);
			}
			return AmpcResult.ok(msgMap);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("NativeAndNationController 校验本地清单模板异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 校验本地清单模板异常!");
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
			//添加查询参数
			Map couplingMap=new HashMap();
			couplingMap.put("userId", userId);
			//分页开始条数
			couplingMap.put("startTotal", (pageNumber*pageSize)-pageSize+1);
			//分页结束条数
			couplingMap.put("endTotal",pageNumber*pageSize);
			//查询分页数据
			List<Map> list=tEsCouplingMapper.selectAllCoupling(couplingMap);
			//查询总条数
			int total=tEsCouplingMapper.selectTotalCoupling(userId);
			//返回页面的数据
			Map couplingsMap=new HashMap();
			couplingsMap.put("rows", list);
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
			if(total==1){
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 创建耦合清单信息成功!");
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
			if(total==1){
				msgMap.put("msg", true);
			}else{
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 编辑耦合清单信息成功!");
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
			if(total==1){
				//成功
				msgMap.put("msg", true);	
			}else{
				//失败
				msgMap.put("msg", false);
			}
			LogUtil.getLogger().info("NativeAndNationController 删除耦合清单信息成功!");
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
	
}
