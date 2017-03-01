package ampc.com.gistone.controller;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TScenarinoArea;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.entity.AreaUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.JsonUtil;


@RestController
@RequestMapping
public class AreaAndTimeController {
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	@Autowired
	private TTimeMapper tTimeMapper;
	
	@Autowired
	private TPlanMapper tPlanMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@Autowired
	private TScenarinoAreaMapper tScenarinoAreaMapper;
	
	
	/**
	 * 在原有基础上添加时段
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@RequestMapping("/time/time_save")
	public AmpcResult add_TIME(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ParseException {

		Long areaId = 1l;// Long.parseLong(request.getParameter("areaId"));//区域ID
		Long scenarinoId = 1l;// Long.parseLong(request.getParameter("scenarinoId"));//情景id
		Long missionId = 1l;// Long.parseLong(request.getParameter("missionId"));//任务id
		Long userId = 1l; // Long.parseLong(request.getParameter("userId"));//用户id
		Long selectTimeId = 1l; // Long.parseLong(request.getParameter("selectTimeId"));//添加时段处在的时段id
		Date timeDate = new Date();// new
									// Date(request.getParameter("addTimeDate"));//新增时段时间

		// 时间操作，结束时间与开始时间的数据有一位数间隔，需要时间计算
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeDate);
		cal.add(Calendar.HOUR, - 1);
		String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date timeEndDate = sdf.parse(addTimeDate);

		TTime tTime = new TTime();
		// 查询添加时段处的结束时间
		tTime = tTimeMapper.selectByPrimaryKey(selectTimeId);
		// 查询时段表当前最大id，并且+1当做所添加时段的id
		String maxid = "select max(TIME_ID) from T_TIME";
		Long max = (long) this.getBySqlMapper.findrows(maxid);
		max += 1;
		// 添加一个新的时段
		TTime add_tTime = new TTime();
		add_tTime.setAreaId(areaId);
		add_tTime.setUserId(userId);
		add_tTime.setTimeId(max);
		add_tTime.setMissionId(missionId);
		add_tTime.setScenarinoId(scenarinoId);
		add_tTime.setTimeStartDate(timeDate);
		add_tTime.setTimeEndDate(tTime.getTimeEndDate());
		int insert_start = tTimeMapper.insertSelective(add_tTime);// insertSelective(add_tTime);
		System.out.println(insert_start);
		// 判断添加操作是否成功，成功后修改原有时段
		// 判断数据库操作是否成功，并添加对应数据
		JSONObject obj = new JSONObject();
		Integer start = 0;
		String msg = "";
		int update_start=0;
		if(insert_start!=0){
			//修改原有时段
		TTime update_tTime = new TTime();
		update_tTime.setTimeId(selectTimeId);
		update_tTime.setTimeEndDate(timeEndDate);
		update_start = tTimeMapper
				.updateByPrimaryKeySelective(update_tTime);
		// 判断所有数据库操作是否成功，并添加对应数据
				if (update_start != 0) {
					obj.put("timeId", max);
					start = 0;
					msg = "save_time success";
				} else {
					start = 1;
					msg = "save_time error";
				}

				// 返回json数据
				return AmpcResult.build(start, msg, obj);
		}else{
			//如果添加操作失败，直接返回失败数据
			start = 1;
			msg = "save_time error";
			return AmpcResult.build(start, msg, obj);
		}
		
	}
	
	/**
	 *  时段修改接口
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping("/time/time_update")
	public AmpcResult update_TIME(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		Long beforeTimeId=15l;//Long.parseLong(request.getParameter("beforeTimeId"));//修改时段前一个的时段Id
		Long afterTimeId=16l;//Long.parseLong(request.getParameter("afterTimeId"));//修改时段后一个的时段Id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		Date updateDate=new Date();//request.getParameter("addTimeDate"));//时段的修改时间
		//修改时间减一个小时作为前一个时段的结束时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(updateDate);
		cal.add(Calendar.HOUR, - 1);
		String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date timeEndDate = sdf.parse(addTimeDate);
		//修改前一个时段的结束时间
		TTime updatet_beforetTime = new TTime();
		updatet_beforetTime.setTimeId(beforeTimeId);
		updatet_beforetTime.setTimeEndDate(timeEndDate);
		int beforetTimestatus=tTimeMapper.updateByPrimaryKeySelective(updatet_beforetTime);
		//判断修改前一个时段的结束时间是否成功，成功执行修改后一个时段的开始时间，失败返回失败信息
		if(beforetTimestatus!=0){
			//开始修改后一个时段的开始时间
			TTime updatet_afterTimeId = new TTime();
			updatet_afterTimeId.setTimeId(afterTimeId);
			updatet_afterTimeId.setTimeStartDate(updateDate);
			int afterTimestatus=tTimeMapper.updateByPrimaryKeySelective(updatet_afterTimeId);
			//判断修改是否成功，成功返回成功信息，失败返回失败信息
			if(afterTimestatus!=0){
				return AmpcResult.build(0, "update_TIME success");
			}else{
				return AmpcResult.build(1, "update_TIME error");
			}
		}else{
		return AmpcResult.build(1, "update_TIME error");
		}
	}
	
	/**
	 * 获取时段信息
	 */
	@RequestMapping("/time/time_list")
	public AmpcResult find_TIME(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		Long areaId=1l;//Long.parseLong(request.getParameter("areaId"));//区域id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户的id
		String sort="timeStartDate";//request.getParameter("sort");//排序字段
		String isEffective="1"; //Long.parseLong(request.getParameter("isEffective"));//是否有效
		//查询时段信息
		TTime find_time = new TTime();
		find_time.setAreaId(areaId);
		find_time.setUserId(userId);
		find_time.setIsEffective(isEffective);
		find_time.setSort(sort);
		List<TTime> timelist=tTimeMapper.selectByPrimaryKeysort(find_time);
		JSONArray objlist=new JSONArray();
		//判断查询后是否有值，有值则查询预案并转成json格式返回，没有值则返回无值信息
		if(timelist.size()>0){
			for(TTime ttime:timelist){
				JSONObject objtime=new JSONObject();
				objtime.put("timeId", ttime.getTimeId());
				objtime.put("timeStartDate", ttime.getTimeStartDate());
				objtime.put("timeEndDate", ttime.getTimeEndDate());
				objtime.put("plandId", ttime.getPlanId());
				//查询预案信息
				TPlan tplan=tPlanMapper.selectByPrimaryKey(ttime.getPlanId());
				objtime.put("timeName", tplan.getPlanName());
				objlist.add(objtime);
			}
			JSONObject obj=new JSONObject();
			obj.put("timeItem", objlist);
			return AmpcResult.build(0, "find_TIME success",obj);
		}else{
			return AmpcResult.build(1, "find_TIME error");
		}
		
	}
	
	/**
	 * 删除当前用户选择的时段节点
	 */
	@RequestMapping("/time/delete_time")
	public AmpcResult delete_TIME(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		Long beforeTimeId=1l;//Long.parseLong(request.getParameter("beforeTimeId"));//上一个时段的时段Id
		Long afterTimeId=18l;//Long.parseLong(request.getParameter("beforeTimeId"));//下一个时段的时段Id
		Date timeEndDate=new Date();//request.getParameter("beforeTimeId"));//删除时段的结束时间
		//Long userId=Long.parseLong(request.getParameter("userId"));//用户的id
		//Long planId=Long.parseLong(request.getParameter("userId"));//预案id
		
		//修改要删除时段的状态
		TTime delete_time = new TTime();
		delete_time.setTimeId(afterTimeId);
		delete_time.setIsEffective("0");
		int delete_timestatus=tTimeMapper.updateByPrimaryKeySelective(delete_time);
		//判断要删除时段的状态是否修改成功,如果成功修改上一时段的结束时间
		if(delete_timestatus!=0){
			TTime update_time = new TTime();	
			update_time.setTimeEndDate(timeEndDate);
			update_time.setTimeId(beforeTimeId);
			int update_timestatus=tTimeMapper.updateByPrimaryKeySelective(update_time);
			//判断上一个时段的结束时间是否修改成功
			if(update_timestatus!=0){
				return AmpcResult.build(0, "delete_time success");
			}else{
				return AmpcResult.build(1, "delete_time error");	
			}
		}else{
		  return AmpcResult.build(1, "delete_time error");
		}
	}
	
	
	
	/**
	 * 区域查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	@RequestMapping("area/get_areaAndTimeList")
	public AmpcResult get_AreaAndTimeList(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//情景Id
			Integer scenarinoId=Integer.valueOf(data.get("scenarinoId").toString());
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("scenarinoId",scenarinoId);
			map.put("userId", userId);
			//新建返回结果的Map
			List mapResult=new ArrayList();
			//查询全部的区域ID和区域名称
			List<Map> areaAndName = this.tScenarinoAreaMapper.selectByScenarinoId(map);
			//循环结果  根据区域ID获取时段和预案信息
		    for (Map area : areaAndName) {
		    	AreaUtil areaUtil =new AreaUtil();
		    	Object id=area.get("areaId");
		    	areaUtil.setAreaId(id);
		    	areaUtil.setAreaName(area.get("areaName"));
		    	areaUtil.setTimeItems(this.tTimeMapper.selectByAreaId(id));
		    	mapResult.add(areaUtil);
			}
		    //返回结果
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 * 区域查询接口(只查询省市县区域不查询时段)
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	@RequestMapping("area/get_areaList")
	public AmpcResult get_AreaList(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//区域Id
			Integer areaId=Integer.valueOf(data.get("areaId").toString());
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("areaId",areaId);
			map.put("userId", userId);
			//查询全部的区域ID和区域名称
			Map areaInfo = this.tScenarinoAreaMapper.selectByAreaId(map);
			//进行Clob转换
			Object obj=JsonUtil.clobToObject((Clob)areaInfo.get("provinceCodes"));
			Object obj1=JsonUtil.clobToObject((Clob)areaInfo.get("cityCodes"));
			Object obj2=JsonUtil.clobToObject((Clob)areaInfo.get("countyCodes"));
			//重新写入返回结果集
			areaInfo.put("provinceCodes", obj);
			areaInfo.put("cityCodes", obj1);
			areaInfo.put("countyCodes", obj2);
			//返回结果
			return AmpcResult.ok(areaInfo);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	
	/**
	 * 区域创建或修改方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	@Transactional
	@RequestMapping("area/saveorupdate_area")
	public AmpcResult saveorupdate_Area(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			TScenarinoAreaWithBLOBs area=new TScenarinoAreaWithBLOBs();
			//区域ID
			String areaId=data.get("areaId").toString();
			//区域名称
			area.setAreaName(data.get("areaName").toString());
			//用户的id  确定当前用户
			area.setUserId(Long.parseLong(data.get("userId").toString()));
			//省级区域代码数组
			area.setProvinceCodes(data.get("provinceCodes").toString());
			//市级区域代码数组
			area.setCityCodes(data.get("cityCodes").toString());
			//县级区域代码数组
			area.setCountyCodes(data.get("countyCodes").toString());
			//判断区域Id 用来确定是添加还是修改
			if(null!=areaId&&!areaId.equals("")){
				//修改
				area.setScenarinoAreaId((Long.parseLong(areaId)));
				int result=this.tScenarinoAreaMapper.updateByPrimaryKeySelective(area);
				//判断修改是否成功
			    return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "添加时段失败",null);
			}else{
				//情景ID
				area.setScenarinoDetailId(Long.parseLong(data.get("scenarinoId").toString()));
				//执行添加操作
				int result=this.tScenarinoAreaMapper.insertSelective(area);
				if(result>0){
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("scenarinoId",area.getScenarinoDetailId());
					map.put("areaName",area.getAreaName());
					map.put("userId", area.getUserId());
					long areId=this.tScenarinoAreaMapper.selectAreaIdByParam(map);
					//情景开始时间
					Date startDate=new Date(data.get("scenarinoStartDate").toString());
					//情景结束时间
					Date endDate=new Date(data.get("scenarinoEndDate").toString());
					// 时间操作，将情景结束时间减少一个小时
					Calendar cal = Calendar.getInstance();
					cal.setTime(endDate);
					cal.add(Calendar.HOUR, - 1);
					String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(cal.getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date timeEndDate = sdf.parse(addTimeDate);
					//新建时段
					TTime add_tTime = new TTime();
					add_tTime.setAreaId(areId);
					add_tTime.setUserId(area.getUserId());
					add_tTime.setMissionId(Long.parseLong(data.get("missionId").toString()));
					add_tTime.setScenarinoId(area.getScenarinoDetailId());
					add_tTime.setTimeStartDate(startDate);
					add_tTime.setTimeEndDate(timeEndDate);
				    result = tTimeMapper.insertSelective(add_tTime);
				    //判断添加时段是否成功
				    return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "添加时段失败",null);
				}else{
					//添加失败
					return AmpcResult.build(1000, "添加区域失败",null);
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	
	/**
	 * 区域删除方法 实际为修改数据的状态
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	//@Transactional
	@RequestMapping("area/delete_area")
	public AmpcResult delete_Area(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要删除的任务集合
			String areaIds=data.get("areaIds").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//将得到的数据拆分 放入集合中
			String[] idss=areaIds.split(",");
			List<Integer> list=new ArrayList<Integer>();
			for(int i=0;i<idss.length;i++){
				list.add(Integer.valueOf(idss[i]));
			}
			//进行批量删除
			int result=this.tScenarinoAreaMapper.updateIsEffeByIds(list);
			 /**
		     * TODO 等待时段删除完成 直接添加
		     *      删除涉及到多次数据库操作，在事务的一致性有待提高
		     */
			//判断执行结果返回对应数据
			return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "区域删除失败",null);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	
	
	
	
	/**
	 * 添加区域对名称重复判断
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	@RequestMapping("area/check_areaname")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult check_AreaName(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response)throws Exception{
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要添加的区域名称
			String areaName=data.get("areaName").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//情景Id
			long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("scenarinoId", scenarinoId);
			map.put("areaName", areaName);
			map.put("userId", userId);
			int result=this.tScenarinoAreaMapper.check_AreaName(map);
			//返回true 表示可用  返回false 已存在
			return result==0?AmpcResult.ok(true):AmpcResult.build(1000, "名称已存在",false);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
}
