package ampc.com.gistone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureSectorExcelMapper;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

/**
 * 行业和措施中间表的Controller
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月6日
 */
@RestController
@RequestMapping
public class MeasureAndSectorController {

	//默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	@Autowired
	private TMeasureSectorExcelMapper tMeasureSectorExcelMapper;
	
	
	/**
	 * 中间表查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping(value = "ms/get_msInfo")
	public AmpcResult get_MsInfo(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户的id  确定当前用户
			Long userId=Long.parseLong(data.get("userId").toString());
			//查询全部写入返回结果集
			List<Map> list = this.tMeasureSectorExcelMapper.selectByUserId(userId);
			//返回结果
			return AmpcResult.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 * 获取所有的行业信息
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("ms/get_sectorInfo")
	public AmpcResult get_SectorInfo(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户的id  确定当前用户
			Long userId=Long.parseLong(data.get("userId").toString());
			//查询全部写入返回结果集
			List<String> list = this.tMeasureSectorExcelMapper.getSectorInfo(userId);
			//返回结果
			return AmpcResult.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 * 根据所有的行业名称获取所有的措施 和统计总数
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("ms/get_minfoBySname")
	public AmpcResult get_MInfoBySname(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户的id  确定当前用户
			Long userId=Long.parseLong(data.get("userId").toString());
			//获取到的行业名称
			String sectorsName=data.get("sectorsName").toString();
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("sectorsName", sectorsName);
			map.put("userId", userId);
			//查询全部写入返回结果集
			List<Map> list = this.tMeasureSectorExcelMapper.getMeasureInfo(map);
			//返回结果
			return AmpcResult.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
}
