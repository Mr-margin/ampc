package ampc.com.gistone.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TMeasureMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TSectorMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMeasure;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TPlanMeasure;
import ampc.com.gistone.database.model.TSector;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.util.AmpcResult;

@RestController
@RequestMapping
public class PlanAndMeasureController {
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	
	@Autowired
	private TSectorMapper tSectorMapper;
	
	@Autowired
	private TMeasureMapper tMeasureMapper;
	
	@Autowired
	private TPlanMapper tPlanMapper;
	
	@Autowired
	private TTimeMapper tTimeMapper;
	
	/**
	 * 措施汇总查询
	 */
	@RequestMapping("/measure/list_measure")
	public  AmpcResult list_measure(HttpServletRequest request,
			HttpServletResponse response){
		try{
		Long planId=1l;//Long.parseLong(request.getParameter("planId"));//预案id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		JSONObject obj=new JSONObject();
		
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanId(planId);
		//查看行业id是否为空，不为空添加，为空不加
		if(request.getParameter("sectorId")!=null){	
			Long sectorId=1l;//Long.parseLong(request.getParameter("sectorId"));
			tPlanMeasure.setSectorId(sectorId);
		}
		//查询措施
		List<TPlanMeasure> Measurelist=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
		JSONArray arr=new JSONArray();
		//判断查询结果是否为空，返回对应的值
		if(!Measurelist.isEmpty()){
		for(TPlanMeasure TPlanMeasure:Measurelist){
			TSector Sector=tSectorMapper.selectByPrimaryKey(TPlanMeasure.getSectorId());
			TMeasure tMeasure=tMeasureMapper.selectByPrimaryKey(TPlanMeasure.getMeasureId());
			JSONObject objs=new JSONObject();
			objs.put("sectorName", Sector.getSectorName());
			objs.put("measureName", tMeasure.getMeasureName());
			objs.put("intensity", tMeasure.getIntensity());
			//objs.put("measureContent", TPlanMeasure.getMeasureContent());
			arr.add(objs);
		}
		obj.put("measurelist", arr);
		return AmpcResult.build(0, "list_measure success",obj);
		}else{
		return AmpcResult.build(1, "list_measure error");
		}
	}catch(NullPointerException n){
		System.out.println(n);
		return AmpcResult.build(1, "list_measure error");
	}
	}
	
	/**
	 * 预案添加措施
	 */
	@RequestMapping("/measure/add_measure")
	public  AmpcResult add_measure(HttpServletRequest request,
			HttpServletResponse response){
		try{	
		Long sectorId=Long.parseLong(request.getParameter("sectorId"));//行业id
		Long measureId=Long.parseLong(request.getParameter("measureId"));//措施id
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		Long planId=Long.parseLong(request.getParameter("planId"));//预案id
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setMeasureId(measureId);
		tPlanMeasure.setPlanId(planId);
		tPlanMeasure.setSectorId(sectorId);
		//预案添加措施
		int addstatus=tPlanMeasureMapper.insertSelective(tPlanMeasure);
		//判断是否成功
		if(addstatus!=0){
		return AmpcResult.build(0, "add_measure error");
		}
		return AmpcResult.build(1, "add_measure error");
	}catch(NullPointerException n){
		System.out.println(n);
		return AmpcResult.build(1, "add_measure error");
	}
		}
	
	/**
	 * 添加预案
	 */
	@RequestMapping("/plan/add_plan")
	public  AmpcResult add_plan(HttpServletRequest request,
			HttpServletResponse response){
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		String planName=request.getParameter("planName");//预案名称
	    Date addTime=new Date(request.getParameter("addTime"));//添加时间
	    Long usedBy=Long.parseLong(request.getParameter("usedBy"));//情景id
	    Long scenarioId=Long.parseLong(request.getParameter("scenarioId"));//行业id
	    Long missionId=Long.parseLong(request.getParameter("missionId"));//所属任务id
	    Date timeStartDate=new Date(request.getParameter("timeStartDate"));//时段开始时间
	    Date timeEndEate=new Date(request.getParameter("timeEndEate"));//时段结束时间
	    Long areaId=Long.parseLong(request.getParameter("areaId"));//区域id
	    
	    TPlan tPlan=new TPlan();
	    tPlan.setAddTime(addTime);
	    tPlan.setAreaId(areaId);
	    tPlan.setMissionId(missionId);
	    tPlan.setPlanEndTime(timeEndEate);
	    tPlan.setPlanStartTime(timeStartDate);
	    tPlan.setScenarioId(scenarioId);
	    tPlan.setUsedBy(usedBy);
	    tPlan.setUserId(userId);
	    tPlan.setPlanName(planName);
	    //添加预案
	    int addstatus=tPlanMapper.insertSelective(tPlan);
	   //判断是否添加成功，根据结果返回值
	    if(addstatus!=0){
	    	 return AmpcResult.build(0, "add_plan error");	
	    }
	    return AmpcResult.build(1, "add_plan error");
	}
	/**
	 * 措施详情修改
	 */
	@RequestMapping("/measure/update_measureContent")
	public  AmpcResult update_measureContent(HttpServletRequest request,
			HttpServletResponse response){
		try{
		String measureContent=request.getParameter("measureContent");//措施详情
		Long planMeasureId=Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanMeasureId(planMeasureId);
		tPlanMeasure.setMeasureContent(measureContent);
		//修改措施详情
		int update_status=tPlanMeasureMapper.updateByPrimaryKeySelective(tPlanMeasure);
		//判断是否修改成功
		if(update_status!=0){
			return AmpcResult.build(0, "update_measureContent error");
		}
			return AmpcResult.build(1, "update_measureContent error");
	}catch(NullPointerException n){
		System.out.println(n);
		return AmpcResult.build(1, "update_measureContent error");
	}
	}
	
	/**
	 *措施详情查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/measure/list_measureContent")
	public  AmpcResult list_measureContent(HttpServletRequest request,
			HttpServletResponse response){
		try{
		Long planMeasureId=Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		//查询预案措施表
		TPlanMeasure tPlanMeasure=tPlanMeasureMapper.selectByPrimaryKey(planMeasureId);
		JSONObject obj=new JSONObject();
		if(tPlanMeasure.getMeasureContent()!=null){
		obj.put("measureContent", tPlanMeasure.getMeasureContent());
		 return AmpcResult.build(0, "list_measureContent error");
		}
		 return AmpcResult.build(1, "list_measureContent error");
		}catch(NullPointerException n){
			System.out.println(n);
			return AmpcResult.build(1, "list_measureContent error");
		}
	}
	/**
	 * 删除预案中的措施
	 */
	@RequestMapping("/measure/delete_measure")
	public  AmpcResult delete_measure(HttpServletRequest request,
			HttpServletResponse response){
		try{
		Long planMeasureId=Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		//删除预案中的措施
		int delete_status=tPlanMeasureMapper.deleteByPrimaryKey(planMeasureId);
		if(delete_status!=0){
			return AmpcResult.build(0, "delete_measure success");
		}
		return AmpcResult.build(1, "delete_measure error");
	}catch(NullPointerException n){
		System.out.println(n);
		return AmpcResult.build(1, "delete_measure error");
	}
		}
	
	/**
	 * 预案合并
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/plan/merge_plan")
	public  AmpcResult merge_plan(HttpServletRequest request,
			HttpServletResponse response){
	try{
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		Long chiefPlanId=Long.parseLong(request.getParameter("chiefPlanId"));//蓝本预案id
		Long planId=Long.parseLong(request.getParameter("planId"));//被合并预案id
	    Date startTime=new Date(request.getParameter("startTime"));//合并后的开始时间
	    Date endTime=new Date(request.getParameter("endTime"));//合并后的结束时间
	    //修改蓝本预案信息
	    TPlan tPlan=new TPlan();
	    tPlan.setPlanId(chiefPlanId);
	    tPlan.setPlanEndTime(endTime);
	    tPlan.setPlanStartTime(startTime);
	    int update_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
	    //判断是否修改成功，成功后删除被合并预案以及其措施
	    if(update_status!=0){
	    TPlan del_tPlan=new TPlan();
	    del_tPlan.setPlanId(planId);
	    del_tPlan.setIsEffective("0");
	    //修改预案中的状态
	    int del_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
	    if(del_status!=0){
	    	 TPlanMeasure  tPlanMeasure=new TPlanMeasure();
	    	 tPlanMeasure.setPlanId(planId);
	    	 //查看预案是否有措施
	    	List<TPlanMeasure> list=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
	    	if(!list.isEmpty()){
	    		//删除预案中的措施
	         int status=tPlanMeasureMapper.deleteByPlanId(planId);
	         if(status!=0){
	        	 return AmpcResult.build(0, "merge_plan success");
	         }
	         return AmpcResult.build(1, "merge_plan error");
	    	}
	    	 return AmpcResult.build(1, "merge_plan error");
	    }
	    return AmpcResult.build(1, "merge_plan error");
	    }
	    return AmpcResult.build(1, "merge_plan error");
	}catch(NullPointerException n){
		System.out.println(n);
		 return AmpcResult.build(1, "merge_plan error");
	}
	}
	
	
	/**
	 * 复制预案
	 */
	@RequestMapping("/plan/copy_plan")
	public  AmpcResult copy_plan(HttpServletRequest request,
			HttpServletResponse response){
		try{
		Long userId=Long.parseLong(request.getParameter("userId"));//用户id
		Long planId=Long.parseLong(request.getParameter("planId"));//预案id
		Long timeId=Long.parseLong(request.getParameter("timeId"));//时段id
		
		//将被复制的预案id存入要复制到的时段里
		TTime tTime=new TTime();
		tTime.setPlanId(planId);
		tTime.setTimeId(timeId);
		int copy_status=tTimeMapper.updateByPrimaryKeySelective(tTime);
		//判断是否成功
		if(copy_status!=0){
			return AmpcResult.build(0,"copy_plan success");
		}
		return AmpcResult.build(1,"copy_plan error");
	}catch(NullPointerException n){
		System.out.println(n);
		return AmpcResult.build(1,"copy_plan error");
	}
		}
}
