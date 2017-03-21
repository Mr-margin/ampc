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
import java.util.Iterator;
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
import com.sun.tools.javac.code.Attribute.Array;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TAddressMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.inter.TUserMapper;
import ampc.com.gistone.database.model.TAddress;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TScenarinoArea;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.database.model.TUser;
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
	
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	
	@Autowired
	private TUserMapper tUserMapper;
	
	
	@Autowired
	private TAddressMapper tAddressMapper;
	
	/**
	 * 在原有基础上添加时段
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@RequestMapping("/time/save_time")
	public AmpcResult add_TIME(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws IOException, ParseException {
		ClientUtil.SetCharsetAndHeader(request, response);
		 Map<String,Object> data=(Map)requestDate.get("data");
		Long areaId = Long.parseLong(data.get("areaId").toString());//区域ID
		Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());//情景id
		Long missionId =Long.parseLong(data.get("missionId").toString());//任务id
		Long userId =Long.parseLong(data.get("userId").toString());//用户id
		Long selectTimeId = Long.parseLong(data.get("selectTimeId").toString());//添加时段处在的时段id
		String imeDate = data.get("addTimeDate").toString();//新增时段时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date timeDate=sdf.parse(imeDate);
		// 时间操作，结束时间与开始时间的数据有一位数间隔，需要时间计算
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeDate);
		cal.add(Calendar.SECOND, - 1);
		String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		
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
	@RequestMapping("/time/update_time")
	public AmpcResult update_TIME(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		ClientUtil.SetCharsetAndHeader(request, response);
		 Map<String,Object> data=(Map)requestDate.get("data");
		Long beforeTimeId=Long.parseLong(data.get("beforeTimeId").toString());//修改时段前一个的时段Id
		Long afterTimeId=Long.parseLong(data.get("afterTimeId").toString());//修改时段后一个的时段Id
		Long userId=Long.parseLong(data.get("userId").toString());//用户id
		String teDate=data.get("updateDate").toString();//时段的修改时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date updateDate=sdf.parse(teDate);
		//修改时间减一个小时作为前一个时段的结束时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(updateDate);
		cal.add(Calendar.SECOND, - 1);
		String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		
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
	public AmpcResult find_TIME(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long areaId=Long.parseLong(data.get("areaId").toString());//区域id
		Long userId=Long.parseLong(data.get("userId").toString());//用户的id
		String isEffective="1"; //Long.parseLong(data.get("isEffective").toString());//是否有效
		//查询时段信息
		TTime find_time = new TTime();
		find_time.setAreaId(areaId);
		find_time.setUserId(userId);
		find_time.setIsEffective(isEffective);
		
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
			System.out.println(obj);
			return AmpcResult.build(0, "find_TIME success",obj);
		}else{
			return AmpcResult.build(1, "find_TIME error");
		}
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	/**
	 * 删除当前用户选择的时段节点(暂时没用)
	 */
	@RequestMapping("/time/deleteno_time")
	public AmpcResult deleteno_TIME(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException{
		ClientUtil.SetCharsetAndHeader(request, response);
		 Map<String,Object> data=(Map)requestDate.get("data");
		Long beforeTimeId=Long.parseLong(data.get("beforeTimeId").toString());//上一个时段的时段Id
		Long afterTimeId=Long.parseLong(data.get("beforeTimeId").toString());//下一个时段的时段Id
		String endDate=data.get("beforeTimeId").toString();//删除时段的结束时间
		//Long userId=Long.parseLong(data.get("userId").toString());//用户的id
		Long planId=Long.parseLong(data.get("planId").toString());//预案id
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date timeEndDate=sdf.parse(endDate);
		//修改要删除时段的状态
		TTime delete_time = new TTime();
		delete_time.setTimeId(afterTimeId);
		delete_time.setIsEffective("0");
		int delete_timestatus=tTimeMapper.updateByPrimaryKeySelective(delete_time);
		TTime times=tTimeMapper.selectByPrimaryKey(afterTimeId);
		//判断要删除时段的状态是否修改成功,如果成功修改上一时段的结束时间以及预案
		if(delete_timestatus!=0){
			TTime update_time = new TTime();	
			update_time.setTimeEndDate(timeEndDate);
			update_time.setTimeId(beforeTimeId);
			update_time.setPlanId(planId);
			int update_timestatus=tTimeMapper.updateByPrimaryKeySelective(update_time);
			//判断上一个时段的结束时间是否修改成功
			if(update_timestatus!=0){
				TTime update_pland = new TTime();
				update_pland.setTimeId(afterTimeId);
				int update_status=tTimeMapper.updateByPrimaryKeySelective(update_pland);
				//判断修改是否成功
				if(update_status!=0){
				TPlan tPlan=tPlanMapper.selectByPrimaryKey(times.getPlanId());
				//查看预案是否为可复制预案
				if(tPlan.getCopyPlan().equals("0")){
					//为零则将预案设置为无效
					tPlan.setIsEffective("0");
					int up_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
					if(up_status!=0){
						//删除预案的措施
						int del_status=tPlanMeasureMapper.deleteByPlanId(times.getPlanId());	
						if(del_status!=0){
							return AmpcResult.build(0, "delete_time success");
						}
						return AmpcResult.build(1, "delete_time error");
					}
				}
				return AmpcResult.build(0, "delete_time success");
				}
				return AmpcResult.build(1, "delete_time error");
			}else{
				return AmpcResult.build(1, "delete_time error");	
			}
		}else{
		  return AmpcResult.build(1, "delete_time error");
		}
	}
	/**
	 * 删除当前用户选择的时段节点
	 * 
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/time/delete_time")
	public AmpcResult delete_TIME(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		 Map<String,Object> data=(Map)requestDate.get("data");
		Long deleteTimeId=Long.parseLong(data.get("deleteTimeId").toString());//删除时段的时段Id
		Long mergeTimeId=Long.parseLong(data.get("mergeTimeId").toString());//合并时段的时段Id
		String endDate=data.get("endDate").toString();//删除时段的结束时间
		String startDate=data.get("startDate").toString();//删除时段的开始时间
		Long userId=Long.parseLong(data.get("userId").toString());//用户的id
		String status=data.get("status").toString();//预案id
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date timeEndDate=sdf.parse(endDate);
		Date timeStartDate=sdf.parse(startDate);
		int updateStatus=0;//合并时段修改状态（0为修改失败）
		int upstatus=0;//删除时段的修改状态（0为修改失败）
		TTime tTime=new TTime();
		if(status.equals("up")){
			tTime.setTimeEndDate(timeEndDate);
			tTime.setTimeId(mergeTimeId);
		    updateStatus=tTimeMapper.updateByPrimaryKeySelective(tTime);
			
		}else{
			tTime.setTimeStartDate(timeStartDate);
			tTime.setTimeId(mergeTimeId);
			updateStatus=tTimeMapper.updateByPrimaryKeySelective(tTime);
		}
		TTime deltime=tTimeMapper.selectByPrimaryKey(deleteTimeId);
		//查看合并时间的开始或者结束时间是否修改，修改完成修改要删除时间的状态
		if(updateStatus!=0){
			TTime time=new TTime();
			time.setTimeId(deleteTimeId);
			time.setIsEffective("0");
			time.setPlanId(-1l);
			upstatus=tTimeMapper.updateByPrimaryKeySelective(time);
			if(upstatus!=0){
				TPlan tPlan=tPlanMapper.selectByPrimaryKey(deltime.getPlanId());
				TTime newtime=tTimeMapper.selectByPrimaryKey(mergeTimeId);
				TPlan newPlan=tPlanMapper.selectByPrimaryKey(newtime.getPlanId());
				if(tPlanMapper.selectByPrimaryKey(newtime.getPlanId())!=null){
				if(newPlan.getCopyPlan().equals("1")){
					newPlan.setPlanId(null);
					if(status.equals("up")){
						newPlan.setPlanEndTime(timeEndDate);	
					}else{
						
						newPlan.setPlanStartTime(timeStartDate);
					}
					
					int a=tPlanMapper.insertSelective(newPlan);
					if(a!=0){
						List<TPlan> list=tPlanMapper.selectByEnty(newPlan);
						TPlan tp=list.get(0);
						newtime.setPlanId(tp.getPlanId());
						tTimeMapper.updateByPrimaryKeySelective(newtime);
					}
				}else{
					if(status.equals("up")){
						newPlan.setPlanEndTime(timeEndDate);	
					}else{
						
						newPlan.setPlanStartTime(timeStartDate);
					}
					int a=tPlanMapper.updateByPrimaryKeySelective(newPlan);
				}
				//查看预案是否为可复制预案
				if(tPlan.getCopyPlan().equals("0")){
					//为零则将预案设置为无效
					tPlan.setIsEffective("0");
					int up_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
					if(up_status!=0){
						//删除预案的措施
						int del_status=tPlanMeasureMapper.deleteByPlanId(deltime.getPlanId());	
						if(del_status!=0){
							return AmpcResult.build(0, "delete_time success");
						}
						return AmpcResult.build(0, "delete_time success");
					}
				}
				}else{
					
					return AmpcResult.build(0, "delete_time success");
				}
				}
		}
		//查看修改时段状态是否成功
		
		return AmpcResult.build(1, "delete_time error");
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	/**
	 * 删除当前时段的预案 
	 */
	@RequestMapping("/time/delete_plan")
	public AmpcResult delete_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long timeId=Long.parseLong(data.get("timeId").toString());
		Long planId=Long.parseLong(data.get("planId").toString());
		Long userId=Long.parseLong(data.get("userId").toString());//用户的id
		//修改时段中的预案
		TTime update_pland = new TTime();
		update_pland.setTimeId(timeId);
		update_pland.setPlanId(-1l);
		int update_status=tTimeMapper.updateByPrimaryKeySelective(update_pland);
		//查看预案是否为可复制预案
		if(update_status!=0){
		TPlan tPlan=tPlanMapper.selectByPrimaryKey(planId);
		if(tPlan.getCopyPlan().equals("0")){
			//为零则将预案设置为无效
			tPlan.setIsEffective("0");
			int up_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
			if(up_status!=0){
				int del_status=tPlanMeasureMapper.deleteByPlanId(planId);
			}
		}
		return AmpcResult.build(0, "delete_plan success");
		}else{
		return AmpcResult.build(1, "delete_plan error");
		}
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
     /**
      * 删除时段（级联）
      */
	@RequestMapping("/delete_times")
	public Map delete_times(List<Long> timeIdss){
	try{	
		Map map=new HashMap();
			for(Long timeId:timeIdss){
			TTime t=new TTime();
			t.setTimeId(timeId);
			t.setIsEffective("0");
			Date date=new Date();
			t.setDeleteTime(date);
			//修改时段状态为无效
			
			TTime times=this.tTimeMapper.selectByPrimaryKey(timeId);
			int tstatus=this.tTimeMapper.updateByPrimaryKeySelective(t);
			if(tstatus!=0){
				if(times.getPlanId()!=null && times.getPlanId()!=-1){
				TPlan tPlan=tPlanMapper.selectByPrimaryKey(times.getPlanId());
				if(tPlan.getCopyPlan().equals("0")){
					//修改预案为无效状态
					tPlan.setIsEffective("0");
					int up_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
					if(up_status!=0){
						//删除预案的措施
						int del_status=tPlanMeasureMapper.deleteByPlanId(tPlan.getPlanId());						
					}
					
				}
				}
			}else{
				map.put("result", "-1");
				map.put("msg", "时段删除错误！");
				return map;
			}
			
	}
		map.put("result", "1");
		map.put("msg", "成功");
		return map;
	
	
	}catch(Exception e){
		e.printStackTrace();
		Map map=new HashMap();
		map.put("result", "-1");
		map.put("msg", "失败");
		return map;
	}
	}
	
	
	
	
	
	/**
	 * 区域查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * @author WangShanxi
	 */
	@RequestMapping("/area/get_areaAndTimeList")
	public AmpcResult get_AreaAndTimeList(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//情景Id
			Long scenarinoId=Long.valueOf(data.get("scenarinoId").toString());
			//用户的id  确定当前用户
			Long userId=Long.valueOf(data.get("userId").toString());
			//添加信息到参数中
			//新建返回结果的Map
			List mapResult=new ArrayList();
			//查询全部的区域ID和区域名称
			List<TScenarinoAreaWithBLOBs> areaAndName = this.tScenarinoAreaMapper.selectAllAreaByScenarinoId(scenarinoId);

			//循环结果  根据区域ID获取时段和预案信息
		    for (TScenarinoAreaWithBLOBs area : areaAndName) {
		    	AreaUtil areaUtil =new AreaUtil();
				if(areaAndName.size()!=3){
					areaUtil.setNew(true);			
				}
		    	areaUtil.setAreaId(area.getScenarinoAreaId());
		    	areaUtil.setAreaName(area.getAreaName());
		    	if(area.getCityCodes()!=null){
		    	JSONArray arr=JSONArray.fromObject(area.getCityCodes());
		    		areaUtil.setCityCodes(arr);
		    	}else{
		    		areaUtil.setCityCodes(new JSONArray());
		    		areaUtil.setNew(true);
		    	}
		    	
		    	if(area.getProvinceCodes()!=null){
		    		JSONArray arr1=JSONArray.fromObject(area.getProvinceCodes());
		    		areaUtil.setProvinceCodes(arr1);
			    	}else{
			    		areaUtil.setProvinceCodes(new JSONArray());
			    		areaUtil.setNew(true);
			    	}
		    	if(area.getCountyCodes()!=null){
		    		JSONArray arr2=JSONArray.fromObject(area.getCountyCodes());
		    		areaUtil.setCountyCodes(arr2);
			    	}else{
			    		areaUtil.setCountyCodes(new JSONArray());
			    		areaUtil.setNew(true);
			    	}
		    	List<Map> timeplan=this.tTimeMapper.selectByAreaId(area.getScenarinoAreaId());
		    	if(timeplan.size()!=3){
		    		areaUtil.setNew(true);
		    		
		    	}
		    	for(Map tp:timeplan){
		    		Long s=Long.valueOf(tp.get("planId").toString());
		    		if(tp.get("planId")==null||s==-1){
		    			tp.put("planId", -1);
		    			tp.put("planName", "无");
		    		}else{
		    			areaUtil.setNew(true);
		    		}
		    		
		    	}
		    	areaUtil.setTimeItems(timeplan);
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
			Long areaId=Long.valueOf(data.get("areaId").toString());
			//用户的id  确定当前用户
			Long userId=Long.valueOf(data.get("userId").toString());
			//添加信息到参数中
			//查询全部的区域ID和区域名称
			JSONObject obj=new JSONObject();
			JSONArray arr=new JSONArray();
			JSONArray arr1=new JSONArray();
			JSONArray arr2=new JSONArray();
			TScenarinoAreaWithBLOBs areaInfo = tScenarinoAreaMapper.selectByPrimaryKey(areaId);
			//进行Clob转换
			if(areaInfo.getProvinceCodes()!=null){
			 arr=JSONArray.fromObject(areaInfo.getProvinceCodes());
			}
			if(areaInfo.getCityCodes()!=null){
			arr1=JSONArray.fromObject(areaInfo.getCityCodes());
			}
			if(areaInfo.getCountyCodes()!=null){
			arr2=JSONArray.fromObject(areaInfo.getCountyCodes());
			}
			//重新写入返回结果集
			obj.put("areaId",areaId);
			obj.put("areaName", areaInfo.getAreaName());
			obj.put("provinceCodes", arr);
			obj.put("cityCodes", arr1);
			obj.put("countyCodes", arr2);
			//返回结果
			return AmpcResult.build(0,"get_areaList success",obj);
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
			if(null!=areaId && !areaId.equals("")){
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
					String scenarinoStartDate=data.get("scenarinoStartDate").toString();
					//情景结束时间
					String scenarinoEndDate=data.get("scenarinoEndDate").toString();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date endDate=sdf.parse(scenarinoEndDate);
					Date startDate=sdf.parse(scenarinoStartDate);
					//新建时段
					TTime add_tTime = new TTime();
					add_tTime.setAreaId(areId);
					add_tTime.setUserId(area.getUserId());
					add_tTime.setMissionId(Long.parseLong(data.get("missionId").toString()));
					add_tTime.setScenarinoId(area.getScenarinoDetailId());
					add_tTime.setTimeStartDate(startDate);
					add_tTime.setTimeEndDate(endDate);
				    result = tTimeMapper.insertSelective(add_tTime);
				    //判断添加时段是否成功
				    JSONObject obj=new JSONObject();
				   List<TTime> time =tTimeMapper.selectAllByAreaId(areId);
				   TTime t=time.get(0);
				  obj.put("timeId", t.getTimeId());
				  obj.put("areaId", areId);
				    return result>0?AmpcResult.ok(obj):AmpcResult.build(1000, "添加时段失败",null);
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
			List<Long> areaIdss=new ArrayList<Long>();
			if(areaIds.indexOf(",")!=-1){
			String[] idss=areaIds.split(",");
			for(int i=0;i<idss.length;i++){
				areaIdss.add(Long.parseLong(idss[i]));
			}
			}else{
				areaIdss.add(Long.parseLong(areaIds));
				}
			/**
			 * TODO 用事务更能保证一致性
			 */
			//统计时段的所有ID
			List<Long> timeIdss=new ArrayList<Long>();
			//记录时段的所有ID
			for (Long areaId: areaIdss) {
				List<TTime> times=tTimeMapper.selectEntityByAreaId(areaId);
				for (TTime time : times) {
					timeIdss.add(time.getTimeId());
				}
			}
			//执行时段级联删除方法
			Map res=this.delete_times(timeIdss);
			String str=res.get("result").toString();
			//判断时段级联方法是否执行成功  -1 不成功  1成功
			if(str.equals("-1")){
				return AmpcResult.build(1000, res.get("msg").toString(),null);
			}
			//执行区域删除方法
			int s=0;
			for(Long areaid:areaIdss){
				TScenarinoAreaWithBLOBs tScenarinoArea=new TScenarinoAreaWithBLOBs();
				tScenarinoArea.setScenarinoAreaId(areaid);
				tScenarinoArea.setIsEffective("0");
				int status=tScenarinoAreaMapper.updateByPrimaryKeySelective(tScenarinoArea);
				if(status!=0){
				s++;	
				}
			}
			//int result=tScenarinoAreaMapper.updateIsEffeByIds(areaIdss);
			//判断执行结果返回对应数据
			return s==areaIdss.size()?AmpcResult.ok(s):AmpcResult.build(1000, "区域删除失败",null);
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
	


@RequestMapping("area/find_areas")
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
public AmpcResult find_areas(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
	ClientUtil.SetCharsetAndHeader(request, response);
	Map<String,Object> data=(Map)requestDate.get("data");
	Long userId=Long.parseLong(data.get("userId").toString());//用户id
	TUser user=tUserMapper.selectByPrimaryKey(userId);
	
	//根据情景id查询所有的区域
	Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());//情景id
	TScenarinoAreaWithBLOBs tScenarinoArea=new TScenarinoAreaWithBLOBs();
	tScenarinoArea.setScenarinoDetailId(scenarinoId);
	List<TScenarinoAreaWithBLOBs> arealist=tScenarinoAreaMapper.selectByEntity(tScenarinoArea);
	
	//查询所有省市区
	TAddress tAddress=new TAddress();
	List<TAddress> prolist=tAddressMapper.selectBLevel(tAddress);
	JSONArray arr=new JSONArray();
	TScenarinoAreaWithBLOBs tsa=new TScenarinoAreaWithBLOBs();
	if(data.get("areaId")!=null && data.get("areaId")!=""){
		Long areaId =Long.parseLong(data.get("areaId").toString());//用户id
		TScenarinoAreaWithBLOBs areas=tScenarinoAreaMapper.selectByPrimaryKey(areaId);
		
		 Iterator<TScenarinoAreaWithBLOBs> areaIter = arealist.iterator();  
		while(areaIter.hasNext()){
			TScenarinoAreaWithBLOBs area=areaIter.next();
			if(area.getScenarinoAreaId().equals(areas.getScenarinoAreaId())){
				areaIter.remove();
			}
		}
		
		
		
	for(TAddress address:prolist){
		
		JSONObject obj=new JSONObject();
		
		//省
		if(address.getAddressLevel().equals("1")){
			obj.put("adcode", (address.getProvinceCode()+"0000"));
			obj.put("name",  address.getAddressName());
			obj.put("code", address.getAddressCode());
			for(TScenarinoAreaWithBLOBs area:arealist){
				if(area.getProvinceCodes()!=null&area.getProvinceCodes()!=""){
				JSONArray Province=JSONArray.fromObject(area.getProvinceCodes());
				for (int i = 0; i < Province.size(); i++) {
					JSONObject ob  = (JSONObject) Province.get(i);
					String obs=ob.toString();
					if(obs.indexOf(address.getProvinceCode().toString()+"0000")!=-1){
						String couname="("+(String) area.getAreaName()+")";
						obj.put("name",(address.getAddressName())+couname);
						obj.put("chkDisabled", true);
					}
					}
				}
			}
			obj.put("level", address.getAddressLevel());
			if(user.getProvinceCode().equals(address.getAddressCode())){
				obj.put("open",true);
			}
			if(areas.getProvinceCodes()!=null&areas.getProvinceCodes()!=""){
			JSONArray Province=JSONArray.fromObject(areas.getProvinceCodes());
			for (int i = 0; i < Province.size(); i++) {
				JSONObject ob  = (JSONObject) Province.get(i);
				String obs=ob.toString();
			if(obs.indexOf(address.getProvinceCode().toString()+"0000")!=-1){	
				obj.put("checked", true);
			}
			}
			}
		}//省
		
		//市
		if(address.getAddressLevel().equals("2")){
			obj.put("adcode", (address.getProvinceCode()+address.getCityCode()+"00"));
			obj.put("padcode", (address.getProvinceCode()+"0000"));
			obj.put("name",address.getAddressName());
			obj.put("code", address.getAddressCode());
			for(TScenarinoAreaWithBLOBs area:arealist){
				if(area.getCityCodes()!=null&&area.getCityCodes()!=""){
			JSONArray Province=JSONArray.fromObject(area.getCityCodes());
			for (int i = 0; i < Province.size(); i++) {
				JSONObject ob  = (JSONObject) Province.get(i);
				String obs=ob.toString();
				String code=address.getProvinceCode().toString()+address.getCityCode().toString();
				code+="00";
				if(obs.indexOf(code)!=-1){
					String couname="("+(String) area.getAreaName()+")";
					obj.put("name",(address.getAddressName())+couname);
					obj.put("chkDisabled", true);
				}
				}
				}
			}
			obj.put("level", address.getAddressLevel());
			if(user.getCityCode().equals(address.getAddressCode())){
				obj.put("open",true);
			}
			if(areas.getCityCodes()!=null&&areas.getCityCodes()!=""){
			JSONArray Province=JSONArray.fromObject(areas.getCityCodes());
			for (int i = 0; i < Province.size(); i++) {
				JSONObject ob  = (JSONObject) Province.get(i);
				String obs=ob.toString();
				String code=address.getProvinceCode().toString()+address.getCityCode().toString();
				code+="00";
			if(obs.indexOf(code)!=-1){	
				obj.put("checked", true);
			}
			}
			}
		}//市
		
		if(address.getAddressLevel().equals("3")){
			obj.put("adcode", address.getProvinceCode()+address.getCityCode()+address.getCountyCode());
			obj.put("padcode", (address.getProvinceCode()+address.getCityCode()+"00"));
			obj.put("name", address.getAddressName());
			obj.put("code", address.getAddressCode());
			for(TScenarinoAreaWithBLOBs area:arealist){
			if(area.getCountyCodes()!=null&&area.getCountyCodes()!=""){
				JSONArray Province=JSONArray.fromObject(area.getCountyCodes());
			for (int i = 0; i < Province.size(); i++) {
				JSONObject ob  = (JSONObject) Province.get(i);
				String obs=ob.toString();
				String code=address.getProvinceCode().toString()+address.getCityCode().toString()+address.getCountyCode().toString();
				if(obs.indexOf(code)!=-1){
					String couname="("+(String) area.getAreaName()+")";
					obj.put("name",(address.getAddressName())+couname);
					obj.put("chkDisabled", true);

				}
				}
			}
			}
			
			obj.put("level", address.getAddressLevel());
			if(areas.getCountyCodes()!=null&&areas.getCountyCodes()!=""){
			JSONArray Province=JSONArray.fromObject(areas.getCountyCodes());
			for (int i = 0; i < Province.size(); i++) {
				JSONObject ob  = (JSONObject) Province.get(i);
				String obs=ob.toString();
				String code=address.getProvinceCode().toString()+address.getCityCode().toString()+address.getCountyCode().toString();
			if(obs.indexOf(code)!=-1){	
				obj.put("checked", true);
			}
			}
			}
		}
		arr.add(obj);
	}
	
	//如果没有区域id
	}else{
		for(TAddress address:prolist){
			
			JSONObject obj=new JSONObject();
			
			//省
			if(address.getAddressLevel().equals("1")){
				obj.put("adcode", (address.getProvinceCode()+"0000"));
				obj.put("name",  address.getAddressName());
				obj.put("code", address.getAddressCode());
				for(TScenarinoAreaWithBLOBs area:arealist){
					if(area.getProvinceCodes()!=null&area.getProvinceCodes()!=""){
					JSONArray Province=JSONArray.fromObject(area.getProvinceCodes());
					for (int i = 0; i < Province.size(); i++) {
						JSONObject ob  = (JSONObject) Province.get(i);
						String obs=ob.toString();
						if(obs.indexOf(address.getProvinceCode().toString()+"0000")!=-1){
							String couname="("+(String) area.getAreaName()+")";
							obj.put("name",(address.getAddressName())+couname);
							obj.put("chkDisabled", true);
						}
						}
					}
				}
				obj.put("level", address.getAddressLevel());
				if(user.getProvinceCode().equals(address.getAddressCode())){
					obj.put("open",true);
				}
				
			
			}//省
			
			//市
			if(address.getAddressLevel().equals("2")){
				obj.put("adcode", (address.getProvinceCode()+address.getCityCode()+"00"));
				obj.put("padcode", (address.getProvinceCode()+"0000"));
				obj.put("name",address.getAddressName());
				obj.put("code", address.getAddressCode());
				for(TScenarinoAreaWithBLOBs area:arealist){
					if(area.getCityCodes()!=null&&area.getCityCodes()!=""){
				JSONArray Province=JSONArray.fromObject(area.getCityCodes());
				for (int i = 0; i < Province.size(); i++) {
					JSONObject ob  = (JSONObject) Province.get(i);
					String obs=ob.toString();
					String code=address.getProvinceCode().toString()+address.getCityCode().toString();
					code+="00";
					if(obs.indexOf(code)!=-1){
						String couname="("+(String) area.getAreaName()+")";
						obj.put("name",(address.getAddressName())+couname);
						obj.put("chkDisabled", true);
					}
					}
					}
				}
				obj.put("level", address.getAddressLevel());
				if(user.getCityCode().equals(address.getAddressCode())){
					obj.put("open",true);
				}
			}//市
			
			if(address.getAddressLevel().equals("3")){
				obj.put("adcode", address.getProvinceCode()+address.getCityCode()+address.getCountyCode());
				obj.put("padcode", (address.getProvinceCode()+address.getCityCode()+"00"));
				obj.put("name", address.getAddressName());
				obj.put("code", address.getAddressCode());
				for(TScenarinoAreaWithBLOBs area:arealist){
				if(area.getCountyCodes()!=null&&area.getCountyCodes()!=""){
					JSONArray Province=JSONArray.fromObject(area.getCountyCodes());
				for (int i = 0; i < Province.size(); i++) {
					JSONObject ob  = (JSONObject) Province.get(i);
					String obs=ob.toString();
					String code=address.getProvinceCode().toString()+address.getCityCode().toString()+address.getCountyCode().toString();
					if(obs.indexOf(code)!=-1){
						String couname="("+(String) area.getAreaName()+")";
						obj.put("name",(address.getAddressName())+couname);
						obj.put("chkDisabled", true);

					}
					}
				}
				}
				obj.put("level", address.getAddressLevel());
				
			}
			arr.add(obj);

			
		}
		
		
		
	}
	return AmpcResult.build(0, "find_areas success",arr);	
	}catch(Exception e){
		e.printStackTrace();
		return AmpcResult.build(1, "find_areas error");	
	}
}


/**
 * 查询所有省市区code
 * @param requestDate
 * @param request
 * @param response
 * @return
 */

@RequestMapping("area/find_areas_new")
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
public AmpcResult find_areas_new (@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
	ClientUtil.SetCharsetAndHeader(request, response);
	Map<String,Object> data=(Map)requestDate.get("data");
		//查询所有省市区code
	List<TAddress> prolist=tAddressMapper.selectAll();
	Long userId=Long.parseLong(data.get("userId").toString());//用户id
	//通过userId获取用户信息
	TUser user=tUserMapper.selectByPrimaryKey(userId);
	JSONArray arr=new JSONArray();
    for(TAddress address:prolist){
    	JSONObject obj=new JSONObject();
    	//省级
    	if(address.getAddressLevel().equals("1")){
    		obj.put("adcode", (address.getProvinceCode()+"0000"));
			obj.put("name",  address.getAddressName());
			obj.put("code", address.getAddressCode());
			obj.put("level", address.getAddressLevel());
			if(user.getProvinceCode().equals(address.getAddressCode())){
				obj.put("open",true);
			}	
    	}//省级
    	//市级
        if(address.getAddressLevel().equals("2")){
    				obj.put("adcode", (address.getProvinceCode()+address.getCityCode()+"00"));
    				obj.put("padcode", (address.getProvinceCode()+"0000"));
    				obj.put("name",address.getAddressName());
    				obj.put("code", address.getAddressCode());
    				obj.put("level", address.getAddressLevel());
    				if(user.getCityCode().equals(address.getAddressCode())){
    					obj.put("open",true);
    				}
    			}//市
	 
        //区县
        if(address.getAddressLevel().equals("3")){
			obj.put("adcode", address.getProvinceCode()+address.getCityCode()+address.getCountyCode());
			obj.put("padcode", (address.getProvinceCode()+address.getCityCode()+"00"));
			obj.put("name", address.getAddressName());
			obj.put("code", address.getAddressCode());
			obj.put("level", address.getAddressLevel());
        }
        arr.add(obj);
    }
    return AmpcResult.build(0, "find_areas success",arr);	
	}catch(Exception e){
		e.printStackTrace();
		return AmpcResult.build(1000, "find_areas erorr");	
	}
}
/**
 * 查询某个区域以外区域的省市区code
 * @param requestDate
 * @param request
 * @param response
 * @return
 */
@RequestMapping("area/find_areaAll")
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
public AmpcResult find_areaAll (@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
	ClientUtil.SetCharsetAndHeader(request, response);
	Map<String,Object> data=(Map)requestDate.get("data");
	Long userId=Long.parseLong(data.get("userId").toString());//用户id
	
	//根据情景id查询所有的区域
		Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());//情景id
		TScenarinoAreaWithBLOBs tScenarinoArea=new TScenarinoAreaWithBLOBs();
		tScenarinoArea.setScenarinoDetailId(scenarinoId);
		List<TScenarinoAreaWithBLOBs> arealist=tScenarinoAreaMapper.selectByEntity(tScenarinoArea);
		

		JSONArray arr=new JSONArray();
		TScenarinoAreaWithBLOBs tsa=new TScenarinoAreaWithBLOBs();
		if(data.get("areaId")!=null && data.get("areaId")!=""){
			Long areaId =Long.parseLong(data.get("areaId").toString());//区域id
			TScenarinoAreaWithBLOBs areas=tScenarinoAreaMapper.selectByPrimaryKey(areaId);
			
			 Iterator<TScenarinoAreaWithBLOBs> areaIter = arealist.iterator();  
			while(areaIter.hasNext()){
				TScenarinoAreaWithBLOBs area=areaIter.next();
				if(area.getScenarinoAreaId().equals(areas.getScenarinoAreaId())){
					areaIter.remove();
				}
			}
		}
		for(TScenarinoAreaWithBLOBs area:arealist){
			JSONObject obj=new JSONObject();
			obj.put("areaId", area.getScenarinoAreaId());
			obj.put("areaName", area.getAreaName());
			JSONArray provinceCodes=JSONArray.fromObject(area.getProvinceCodes());
			JSONArray countyCode=JSONArray.fromObject(area.getCountyCodes());
			JSONArray cityCode=JSONArray.fromObject(area.getCityCodes());
			if(area.getProvinceCodes()!=null){
			obj.put("provinceCodes", provinceCodes);
			}else{
			obj.put("provinceCodes", new JSONArray());
			}
			if(area.getCountyCodes()!=null){
			obj.put("cityCodes", cityCode);
			}else{
			obj.put("cityCodes", new JSONArray());
			}
			if(area.getCityCodes()!=null){
			obj.put("countyCodes", countyCode);
			}else{
			obj.put("countyCodes", new JSONArray());
			}
			arr.add(obj);
		}

	return AmpcResult.build(0, "find_areas success",arr);
	}catch(Exception e){
		e.printStackTrace();
		return AmpcResult.build(1000, "find_areas erorr");	
	}
}





}
