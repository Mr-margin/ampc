package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
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
import ampc.com.gistone.util.ClientUtil;

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
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	/**
	 * 措施汇总查询
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/list_measure")
	public  AmpcResult list_measure(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/add_measure")
	public  AmpcResult add_measure(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{	
		Long sectorId=1l;//Long.parseLong(request.getParameter("sectorId"));//行业id
		Long measureId=1l;//Long.parseLong(request.getParameter("measureId"));//措施id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		Long planId=1l;//Long.parseLong(request.getParameter("planId"));//预案id
		String maxid = "select max(PLAN_MEASURE_ID) from T_PLAN_MEASURE";
		Long max = (long) this.getBySqlMapper.findrows(maxid);
		max += 1;
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanMeasureId(max);
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
	 * 创建预案
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/plan/add_plan")
	public  AmpcResult add_plan(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		String planName="各种减排";//request.getParameter("planName");//预案名称
	    Date addTime=new Date();//(request.getParameter("addTime"));//添加时间
	    Long usedBy=1l;//Long.parseLong(request.getParameter("usedBy"));//情景id
	    Long scenarioId=1l;//Long.parseLong(request.getParameter("scenarioId"));//行业id
	    Long missionId=1l;//Long.parseLong(request.getParameter("missionId"));//所属任务id
	    Date timeStartDate=new Date();//(request.getParameter("timeStartDate"));//时段开始时间
	    Date timeEndEate=new Date();//(request.getParameter("timeEndEate"));//时段结束时间
	    Long areaId=1l;//Long.parseLong(request.getParameter("areaId"));//区域id
	    
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/update_measureContent")
	public  AmpcResult update_measureContent(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
		String measureContent="一个大仓库";//request.getParameter("measureContent");//措施详情
		Long planMeasureId=1l;//Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanMeasureId(planMeasureId);
		tPlanMeasure.setMeasureContent(measureContent);
		//修改措施详情
		int update_status=tPlanMeasureMapper.updateByPrimaryKeyWithBLOBs(tPlanMeasure);
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/list_measureContent")
	public  AmpcResult list_measureContent(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
		Long planMeasureId=1l;//Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		//查询预案措施表
		TPlanMeasure tPlanMeasure=tPlanMeasureMapper.selectByPrimaryKey(planMeasureId);
		JSONObject obj=new JSONObject();
		if(tPlanMeasure.getMeasureContent()!=null){
		obj.put("measureContent", tPlanMeasure.getMeasureContent());
		 return AmpcResult.build(0, "list_measureContent success",obj);
		}
		 return AmpcResult.build(1, "list_measureContent error");
		}catch(NullPointerException n){
			System.out.println(n);
			return AmpcResult.build(1, "list_measureContent error");
		}
	}
	
	/**
	 * 删除预案中的措施
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/delete_measure")
	public  AmpcResult delete_measure(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
		Long planMeasureId=5l;//Long.parseLong(request.getParameter("planMeasureId"));//预案措施表id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/plan/merge_plan")
	public  AmpcResult merge_plan(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
	try{
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		Long chiefPlanId=3l;//Long.parseLong(request.getParameter("chiefPlanId"));//蓝本预案id
		Long planId=4l;//Long.parseLong(request.getParameter("planId"));//被合并预案id
	    Date startTime=new Date();//(request.getParameter("startTime"));//合并后的开始时间
	    Date endTime=new Date();//(request.getParameter("endTime"));//合并后的结束时间
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
	    	 return AmpcResult.build(0, "merge_plan success");
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
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/plan/copy_plan")
	public  AmpcResult copy_plan(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		Long planId=1l;//Long.parseLong(request.getParameter("planId"));//预案id
		Long timeId=5l;//Long.parseLong(request.getParameter("timeId"));//时段id
		
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
	/**
	 * 预案编辑功能（预案中措施修改，包含可复用预案）
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Transactional
	@RequestMapping("/plan/copy_new")
	public  AmpcResult demo_plan(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		Long userId=1l;//用户id
		Long planId= 1l;//预案id
		String copyPlan="1";//是否是可复制预案
		JSONObject obj=new JSONObject();
		//判断是否是可复制预案
		if(copyPlan.equals("1")){
			//查询可复制预案数据
		TPlan tplan=tPlanMapper.selectByPrimaryKey(planId);
		  String maxid = "select max(PLAN_ID) from T_PLAN";
			Long max = (long) this.getBySqlMapper.findrows(maxid);
			max += 1;
		TPlan newtplan=new TPlan();
		newtplan.setPlanId(max);
		newtplan.setUserId(userId);
		newtplan.setAddTime(new Date());
		newtplan.setPlanName(tplan.getPlanName());
		newtplan.setAreaId(tplan.getAreaId());
		newtplan.setIsEffective(tplan.getIsEffective());
		newtplan.setMissionId(tplan.getMissionId());
		newtplan.setPlanEndTime(tplan.getPlanEndTime());
		newtplan.setPlanStartTime(tplan.getPlanStartTime());
		newtplan.setUsedBy(tplan.getUsedBy());
		newtplan.setScenarioId(tplan.getScenarioId());
		//根据可复制预案信息新建预案数据
		int whats=tPlanMapper.insertSelective(newtplan);
		if(whats!=0){
			//根据可复制预案id查询预案中的措施
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanId(planId);
		List<TPlanMeasure> list=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
		List lis=new ArrayList();
		if(!list.isEmpty()){
			//根据可复制预案中的措施创建新建预案的措施
			JSONArray arr=new JSONArray();
		for(TPlanMeasure t:list){
			TPlanMeasure newtPlanMeasure=new TPlanMeasure();
			String maxids = "select max(PLAN_MEASURE_ID) from T_PLAN_MEASURE";
			Long maxs = (long) this.getBySqlMapper.findrows(maxids);
			maxs += 1;
			newtPlanMeasure.setAddTime(new Date());
			newtPlanMeasure.setMeasureContent(t.getMeasureContent());
			newtPlanMeasure.setPlanId(max);
			newtPlanMeasure.setPlanMeasureId(maxs);
			newtPlanMeasure.setSectorId(t.getSectorId());
			newtPlanMeasure.setMeasureId(t.getMeasureId());
			int ssr=tPlanMeasureMapper.insertSelective(newtPlanMeasure);
			
			TSector Sector=tSectorMapper.selectByPrimaryKey(t.getSectorId());
			TMeasure tMeasure=tMeasureMapper.selectByPrimaryKey(t.getMeasureId());
			JSONObject objs=new JSONObject();
			objs.put("planMeasureId",maxs);
			objs.put("planId",max);
			objs.put("sectorName", Sector.getSectorName());
			objs.put("measureName", tMeasure.getMeasureName());
			objs.put("intensity", tMeasure.getIntensity());
			objs.put("measureContent", t.getMeasureContent());
			arr.add(objs);
			}
		//查询新建预案的措施信息
	
//		TPlanMeasure tPlanMeasures=new TPlanMeasure();
//		tPlanMeasures.setPlanId(max);
//		//查看行业id是否为空，不为空添加，为空不加
//		if(request.getParameter("sectorId")!=null){	
//			Long sectorId=1l;//Long.parseLong(request.getParameter("sectorId"));
//			tPlanMeasures.setSectorId(sectorId);
//		}
//		//查询措施
//		List<TPlanMeasure> Measurelist=tPlanMeasureMapper.selectByEntity(tPlanMeasures);
//		JSONArray arr=new JSONArray();
//		//判断查询结果是否为空，返回对应的值
//		if(!Measurelist.isEmpty()){
//		for(TPlanMeasure tsPlanMeasure:Measurelist){
//			TSector Sector=tSectorMapper.selectByPrimaryKey(tsPlanMeasure.getSectorId());
//			TMeasure tMeasure=tMeasureMapper.selectByPrimaryKey(tsPlanMeasure.getMeasureId());
//			JSONObject objs=new JSONObject();
//			objs.put("planId",tsPlanMeasure.getPlanId());
//			objs.put("sectorName", Sector.getSectorName());
//			objs.put("measureName", tMeasure.getMeasureName());
//			objs.put("intensity", tMeasure.getIntensity());
//			//objs.put("measureContent", tsPlanMeasure.getMeasureContent());
//			arr.add(objs);
	//	}
		obj.put("measurelist", arr);
		return AmpcResult.build(0, "copy_new success",obj);
		}
		return AmpcResult.build(0,"copy_new success",obj);
		}
		return AmpcResult.build(1,"copy_new error");
		}else{
			//如果不是可复制预案，直接查询
		TPlanMeasure tPlanMeasures=new TPlanMeasure();
		tPlanMeasures.setPlanId(planId);
		//查看行业id是否为空，不为空添加，为空不加
		if(request.getParameter("sectorId")!=null){	
			Long sectorId=1l;//Long.parseLong(request.getParameter("sectorId"));
			tPlanMeasures.setSectorId(sectorId);
		}
		//查询措施
		List<TPlanMeasure> Measurelist=tPlanMeasureMapper.selectByEntity(tPlanMeasures);
		JSONArray arr=new JSONArray();
		//判断查询结果是否为空，返回对应的值
		if(!Measurelist.isEmpty()){
		for(TPlanMeasure tsPlanMeasure:Measurelist){
			TSector Sector=tSectorMapper.selectByPrimaryKey(tsPlanMeasure.getSectorId());
			TMeasure tMeasure=tMeasureMapper.selectByPrimaryKey(tsPlanMeasure.getMeasureId());
			JSONObject objs=new JSONObject();
			objs.put("planMeasureId", tsPlanMeasure.getPlanMeasureId());
			objs.put("planId",tsPlanMeasure.getPlanId());
			objs.put("sectorName", Sector.getSectorName());
			objs.put("sectorId", Sector.getSectorId());
			objs.put("measureName", tMeasure.getMeasureName());
			objs.put("intensity", tMeasure.getIntensity());
			objs.put("measureContent", tsPlanMeasure.getMeasureContent());
			arr.add(objs);
		}
		obj.put("measurelist", arr);
		return AmpcResult.build(0, "copy_new success",obj);
		}else{
		return AmpcResult.build(1, "copy_new error");
		}
		}
	}
	
	
	/**
	 * 预案设置成可复制
	 * @throws UnsupportedEncodingException 
	 */
	@Transactional
	@RequestMapping("/plan/iscopy_plan")
	public  AmpcResult iscopy_plan(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		Long planId=8l;//Long.parseLong(request.getParameter("planId"));//预案id
		//将预案的是否可以复制状态改为可复制
		TPlan tPlan=new TPlan();
		tPlan.setPlanId(planId);
		tPlan.setCopyPlan("1");
		int status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
		//查看修改是否成功
		if(status!=0){
			return AmpcResult.build(0, "iscopy_plan success");
		}
		return AmpcResult.build(1, "iscopy_plan error");
	}
	
	
	/**
	 * 措施编辑完成操作
	 * @param request
	 * @param responsey
	 * @return
	 */
	@RequestMapping("/plan/finish_plan")
	public  AmpcResult finish_plan(HttpServletRequest request,
			HttpServletResponse response){
		Long planId=8l;//Long.parseLong(request.getParameter("planId"));//预案id
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));//用户id
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setPlanId(planId);
		List<TPlanMeasure> tlist=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
		int i=0;
		int y=0;
		for(TPlanMeasure t:tlist){
			if(t.getMeasureContent().equals("-1")&&t.getMeasureContent()==null){
				i++;
				int status=tPlanMeasureMapper.deleteByPrimaryKey(t.getPlanMeasureId());
				if(status!=0){
					y++;
				}
			}
			if(i==y){
				return AmpcResult.build(0, "finish_plan success");
			}
		}
		return AmpcResult.build(1, "finish_plan error");
	}
	
	
	/**
	 * 删除措施详情
	 * 
	 * 
	 */
	@RequestMapping("/measure/del_measureContent")
	public  AmpcResult del_measureContent(HttpServletRequest request,
			HttpServletResponse response){
		Long planMeasureId=10l;//Long.parseLong(request.getParameter("planMeasureId"));
		Long userId=1l;//Long.parseLong(request.getParameter("userId"));
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setMeasureContent("-1");
		int tstatus=tPlanMeasureMapper.updateByPrimaryKeyWithBLOBs(tPlanMeasure);
		if(tstatus!=0){
		return AmpcResult.build(0, "del_measureContent success");
		}
		return AmpcResult.build(1, "del_measureContent error");
	}
	
}
