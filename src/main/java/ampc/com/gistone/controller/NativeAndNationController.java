package ampc.com.gistone.controller;

import java.text.SimpleDateFormat;
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
import ampc.com.gistone.database.model.TEsNation;
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
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			List<Map> list=tEsNationMapper.selectAllNation(userId);
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的全国清单信息成功!");
			return AmpcResult.ok(list);
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
	@RequestMapping("/NativeAndNation/find_natives")
	public AmpcResult find_natives(@RequestBody Map<String, Object> requestDate,
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
			List<Map> list=tEsNativeMapper.selectAllNative(userId);
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的本地清单信息成功!");
			return AmpcResult.ok(list);
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
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nationName = param.toString();
			
			//获取清单年份
			param=data.get("nationYear");
			if(!RegUtil.CheckParameter(param, "Short", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "清单年份为空或出现非法字符!");
			}
			Short nationYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nationRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "清单年份为空或出现非法字符!");
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
//			tEsNation.setAddTime();
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
	 * 编辑清单
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
			//获取ID
			param=data.get("nationId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单Id为空或出现非法字符!");
				return AmpcResult.build(1003, "清单Id为空或出现非法字符!");
			}
			Long nationId = Long.parseLong(param.toString());
			
			//获取清单名称
			param=data.get("nationName");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单名称为空或出现非法字符!");
				return AmpcResult.build(1003, "清单名称为空或出现非法字符!");
			}
			String nationName = param.toString();
			
			//获取清单年份
//			param=data.get("nationYear");
//			if(!RegUtil.CheckParameter(param, "Short", null, false)){
//				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
//				return AmpcResult.build(1003, "清单年份为空或出现非法字符!");
//			}
//			Short nationYear=Short.valueOf(param.toString());
			//获取清单备注
			param=data.get("nationRemark");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 清单年份为空或出现非法字符!");
				return AmpcResult.build(1003, "清单年份为空或出现非法字符!");
			}
			String nationRemark=param.toString();
			
			//日期格式
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=new Date();
			//添加数据
			TEsNation tEsNation=new TEsNation();
			tEsNation.setUserId(userId);
			tEsNation.setEsNationName(nationName);
//			tEsNation.setEsNationYear(nationYear);
			tEsNation.setNationRemark(nationRemark);
			tEsNation.setEsNationId(nationId);
			//插入数据
			int total=tEsNationMapper.updateByPrimaryKeySelective(tEsNation);
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
	
}
