package ampc.com.gistone.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureSectorExcelMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.entity.SMUtil;
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

	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	
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
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//用户的id  确定当前用户
			Long userId=null;
			if(data.get("userId")!=null){
				userId = Long.parseLong(data.get("userId").toString());
			}
			//根据UserId查询所有的行业名称
			List<Map> nameList = this.tMeasureSectorExcelMapper.getSectorInfo(userId);
			if(nameList.size()==0){
				userId=null;
				nameList = this.tMeasureSectorExcelMapper.getSectorInfo(userId);
			}
			//过滤掉一些重复的名称
			LinkedHashSet<String> nameSet=new LinkedHashSet<String>();
			for (Map map : nameList) {
				nameSet.add(map.get("sectorsname").toString());
			}
			//将结果同意放在一个帮助类中 用来返回结果
			List<SMUtil> result=new ArrayList<SMUtil>();
			for (String name : nameSet) {
				SMUtil sm=new SMUtil();
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("sectorsName", name);
				map.put("userId", userId);
				sm.setSectorsName(name);
				//查询全部写入返回结果集
				List<Map> list = this.tMeasureSectorExcelMapper.getMeasureInfo(map);
				sm.setMeasureItems(list);
				map=new HashMap<String,Object>();
				map.put("planId", planId);
				map.put("userId", userId);
				map.put("sectorName", name);
				List<Map> measureList=tPlanMeasureMapper.selectIdByQuery(map);
				sm.setPlanMeasure(measureList);
				sm.setCount(measureList.size());
				result.add(sm);
			}
			
			//返回结果
			return AmpcResult.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
}
