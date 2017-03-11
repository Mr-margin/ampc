package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TQueryExcelMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectordocExcelMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TPlanMeasure;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.entity.MeasureUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

@RestController
@RequestMapping
public class PlanAndMeasureController {
	//预案措施映射
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	//行业映射
	@Autowired
	private TSectorExcelMapper tSectorExcelMapper;
	//措施映射
	@Autowired
	private TMeasureExcelMapper tMeasureExcelMapper;
	//预案映射
	@Autowired
	private TPlanMapper tPlanMapper;
	//时段映射
	@Autowired
	private TTimeMapper tTimeMapper;
	//默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	//行业条件映射
	@Autowired
	public TQueryExcelMapper tQueryExcelMapper;
	//行业描述映射
	@Autowired
	public TSectordocExcelMapper tSectordocExcelMapper;
	
	/**
	 * 子措施条件查询
	 * @author WangShanxi
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/get_measureQuery")
	public  AmpcResult get_MeasureQuery(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		//添加异常捕捉
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//措施id
			Long measureId=Long.parseLong(data.get("measureId").toString());
			//行业名称
			String sectorName=data.get("sectorName").toString();
			//用户id
			Long userId=null;
			//预案措施id
			Long planMeasureId=null;
			if(!data.get("planMeasureId").toString().equals("null")){
				planMeasureId = Long.parseLong(data.get("planMeasureId").toString());
			}
			if(data.get("userId")!=null){
				userId = Long.parseLong(data.get("userId").toString());
			}
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("planId", planId);
			map.put("sectorName", sectorName);
			map.put("userId", userId);
			//查询条件
			List<TQueryExcel> tqeList=tQueryExcelMapper.selectByMap(map);
			//获取所有和当前用户相关的行业描述  右下角1
			List<Map> sdMap=tSectordocExcelMapper.selectByUserId(map);
			//如果没有就给默认的行业描述
			if(sdMap.size()==0){
				map.put("userId", null);
				sdMap=tSectordocExcelMapper.selectByUserId(map);
			}
			List<MeasureUtil> mlist=new ArrayList<MeasureUtil>();
			MeasureUtil mu=null;
			//查询措施的内容
			TMeasureExcel tme=tMeasureExcelMapper.selectByPrimaryKey(measureId);
			//这个里面主要是子措施的列 右下角2
			if(tme.getMeasureExcelA()!=null){
				mu=new MeasureUtil();
				mu.setName("a");
				mu.setValue(tme.getMeasureExcelA());
				mlist.add(mu);
			}
			if(tme.getMeasureExcelA1()!=null){
				mu=new MeasureUtil();
				mu.setName("a1");
				mu.setValue(tme.getMeasureExcelA1());
				mlist.add(mu);
			}
			//只显示中长期的
			if(tme.getMeasureExcelType().equals("中长期措施")){
				if(tme.getMeasureExcelIntensity()!=null){
					mu=new MeasureUtil();
					mu.setName("intensity");
					mu.setValue(tme.getMeasureExcelIntensity());
					mlist.add(mu);
				}
			}
			if(tme.getMeasureExcelIntensity1()!=null){
				mu=new MeasureUtil();
				mu.setName("intensity1");
				mu.setValue(tme.getMeasureExcelIntensity1());
				mlist.add(mu);
			}
			if(tme.getMeasureExcelAsh()!=null){
				mu=new MeasureUtil();
				mu.setName("ash");
				mu.setValue(tme.getMeasureExcelAsh());
				mlist.add(mu);
			}
			if(tme.getMeasureExcelSulfer()!=null){
				mu=new MeasureUtil();
				mu.setName("sulfer");
				mu.setValue(tme.getMeasureExcelSulfer());
				mlist.add(mu);
			}
			//将sv进行拆分
			if(tme.getMeasureExcelSv()!=null){
				String sv=tme.getMeasureExcelSv();
				String[] svs=sv.split(";");
				for(int i=0;i<svs.length;i++){
					String svss=svs[i];
					String[] svsss=svss.split("=");
					mu=new MeasureUtil();
					mu.setName(svsss[0]);
					mu.setValue(svsss[1]);
					mlist.add(mu);
				}
			}
			
			//创建结果集 并写入对应信息
			Map resultMap=new HashMap();
			resultMap.put("measureColumn", sdMap);
			resultMap.put("measureList", mlist);
			resultMap.put("query", tqeList);
			if(planMeasureId!=null){
				TPlanMeasure tPlanMeasure=tPlanMeasureMapper.selectByPrimaryKey(planMeasureId);
				resultMap.put("measureContent", tPlanMeasure.getMeasureContent());
			}
			//返回结果
			return AmpcResult.ok(resultMap);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	
	
	
	/**
	 * 措施汇总查询
	 * @author WangShanxi
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/get_measureList")
	public  AmpcResult get_MeasureList(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		//添加异常捕捉
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("planId", planId);
			map.put("userId", userId);
			//查看行业id是否为空，不为空添加，为空不加
			if(data.get("sectorName")!=null){	
				map.put("sectorName", data.get("sectorName"));
			}else{
				map.put("sectorName", null);
			}
			//查询措施
			List<Map> list=tPlanMeasureMapper.selectByQuery(map);
			return AmpcResult.ok(list);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	
	
	
	/**
	 * 创建预案
	 *  @author WangShanxi
	 */
	@RequestMapping("/plan/add_plan")
	public  AmpcResult add_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response) {
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//时段id
			Long timeId=Long.parseLong(data.get("timeId").toString());
			//预案名称
			String planName=data.get("planName").toString();
			//行业id
			Long scenarioId=Long.parseLong(data.get("scenarioId").toString());
			//所属任务id
		    Long missionId=Long.parseLong(data.get("missionId").toString());
		    //区域id
		    Long areaId=Long.parseLong(data.get("areaId").toString());
		    //预案开始时间
			Date startDate=DateUtil.StrToDate(data.get("timeStartTime").toString());
			//预案结束时间
			Date endDate=DateUtil.StrToDate(data.get("timeEndTime").toString());
			//创建预案对象
		    TPlan tPlan=new TPlan();
		    tPlan.setAreaId(areaId);
		    tPlan.setMissionId(missionId);
		    tPlan.setScenarioId(scenarioId);
		    tPlan.setUserId(userId);
		    tPlan.setPlanName(planName);
		    tPlan.setPlanStartTime(startDate);
		    tPlan.setPlanEndTime(endDate);
		    //添加预案
		    int addstatus=tPlanMapper.insertSelective(tPlan);
		   //判断是否添加成功，根据结果返回值
		    if(addstatus!=0){
		    	Map map=new HashMap();
		    	map.put("userId", userId);
		    	map.put("scenarioId", scenarioId);
		    	map.put("planName", planName);
		    	Integer id=tPlanMapper.getIdByQuery(map);
		    	TTime t=new TTime();
		    	t.setTimeId(timeId);
		    	t.setPlanId(Long.parseLong(id.toString()));
		    	tTimeMapper.updateByPrimaryKeySelective(t);
		    	return AmpcResult.ok(id);
		    }
		    return AmpcResult.build(1000, "添加失败");
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	
	/**
	 * 预案添加措施
	 * @author WangShanxi
	 */
	@RequestMapping("/measure/add_measure")
	public  AmpcResult add_measure(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		try{	
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long sectorName=Long.parseLong(data.get("sectorName").toString());//行业名称
			Long measureId=Long.parseLong(data.get("measureId").toString());//措施id
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			Long planId=Long.parseLong(data.get("planId").toString());//预案id
			TPlanMeasure tPlanMeasure=new TPlanMeasure();
			tPlanMeasure.setMeasureId(measureId);
			tPlanMeasure.setPlanId(planId);
			tPlanMeasure.setSectorName(sectorName);
			/**
			 * TODO 这里会调用计算接口 获取到范围和减排比 的数据
			 */
			//预案添加措施
			int addstatus=tPlanMeasureMapper.insertSelective(tPlanMeasure);
			//判断是否成功
			if(addstatus!=0){
			return AmpcResult.ok("添加成功");
			}
			return AmpcResult.build(1000,"添加失败");
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000,"参数错误");
		}
	}
	
	
	/**
	 * 措施详情修改
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/measure/update_measureContent")
	public  AmpcResult update_measureContent(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
			Map<String,Object> data=(Map)requestDate.get("data");
			String measureContent=data.get("measureContent").toString();//措施详情
			Long planMeasureId=Long.parseLong(data.get("planMeasureId").toString());//预案措施表id
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
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
	 */
	@RequestMapping("/measure/list_measureContent")
	public  AmpcResult list_measureContent(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
			Map<String,Object> data=(Map)requestDate.get("data");
			Long planMeasureId=Long.parseLong(data.get("planMeasureId").toString());//预案措施表id
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
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
	public  AmpcResult delete_measure(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
			Map<String,Object> data=(Map)requestDate.get("data");
			Long planMeasureId=Long.parseLong(data.get("planMeasureId").toString());//预案措施表id
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
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
	public  AmpcResult merge_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{	
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long userId=Long.parseLong(data.get("userId").toString());//用户id
		Long chiefPlanId=Long.parseLong(data.get("chiefPlanId").toString());//蓝本预案id
		Long planId=Long.parseLong(data.get("planId").toString());//被合并预案id
	    Date startTime=new Date(data.get("startTime").toString());//合并后的开始时间
	    Date endTime=new Date(data.get("endTime").toString());//合并后的结束时间
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
	}catch(Exception n){
		System.out.println(n);
		return AmpcResult.build(1, "merge_plan error");
	}
	}
	
	
	/**
	 * 复制预案
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/plan/copy_plan")
	public  AmpcResult copy_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		try{
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			Long planId=Long.parseLong(data.get("planId").toString());//预案id
			Long timeId=Long.parseLong(data.get("timeId").toString());//时段id
			
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
	public  AmpcResult demo_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		Long userId=1l;//用户id
		Long planId= 1l;//预案id
		String copyPlan="1";//是否是可复制预案"1为可复制，0为普通"
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
			newtPlanMeasure.setSectorName(t.getSectorName());
			newtPlanMeasure.setMeasureId(t.getMeasureId());
			int ssr=tPlanMeasureMapper.insertSelective(newtPlanMeasure);
			
			//TSectorExcel Sector=tSectorExcelMapper.selectByPrimaryKey(t.getSectorName());
			TMeasureExcel tMeasure=tMeasureExcelMapper.selectByPrimaryKey(t.getMeasureId());
			JSONObject objs=new JSONObject();
			objs.put("planMeasureId",maxs);
			objs.put("planId",max);
			//objs.put("sectorName", Sector.getSectorExcelName());
			objs.put("measureName", tMeasure.getMeasureExcelDisplay());
			objs.put("intensity", tMeasure.getMeasureExcelIntensity());
			objs.put("measureContent", t.getMeasureContent());
			arr.add(objs);
			}
		
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
		if(request.getParameter("sectorName")!=null){	
			Long sectorName=Long.parseLong(request.getParameter("sectorName"));
			tPlanMeasures.setSectorName(sectorName);
		}
		//查询措施
		List<TPlanMeasure> Measurelist=tPlanMeasureMapper.selectByEntity(tPlanMeasures);
		JSONArray arr=new JSONArray();
		//判断查询结果是否为空，返回对应的值
		if(!Measurelist.isEmpty()){
		for(TPlanMeasure tsPlanMeasure:Measurelist){
			//TSectorExcel Sector=tSectorExcelMapper.selectByPrimaryKey(tsPlanMeasure.getSectorName());
			TMeasureExcel tMeasure=tMeasureExcelMapper.selectByPrimaryKey(tsPlanMeasure.getMeasureId());
			JSONObject objs=new JSONObject();
			objs.put("planMeasureId", tsPlanMeasure.getPlanMeasureId());
			objs.put("planId",tsPlanMeasure.getPlanId());
			//objs.put("sectorName", Sector.getSectorExcelName());
			//objs.put("sectorId", Sector.getSectorExcelId());
			objs.put("measureName", tMeasure.getMeasureExcelDisplay());
			objs.put("intensity", tMeasure.getMeasureExcelIntensity());
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
	public  AmpcResult iscopy_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long userId=Long.parseLong(data.get("userId").toString());//用户id
		Long planId=Long.parseLong(data.get("planId").toString());//预案id
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
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 * 措施编辑完成操作
	 * @param request
	 * @param responsey
	 * @return
	 */
	@RequestMapping("/plan/finish_plan")
	public  AmpcResult finish_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response){
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long planId=Long.parseLong(data.get("planId").toString());//预案id
		Long userId=Long.parseLong(data.get("userId").toString());//用户id
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
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	
	
	/**
	 * 删除措施详情
	 * 
	 * 
	 */
	@RequestMapping("/measure/del_measureContent")
	public  AmpcResult del_measureContent(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response){
		try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long planMeasureId=Long.parseLong(data.get("planMeasureId").toString());
		Long userId=Long.parseLong(data.get("userId").toString());
		TPlanMeasure tPlanMeasure=new TPlanMeasure();
		tPlanMeasure.setMeasureContent("-1");
		int tstatus=tPlanMeasureMapper.updateByPrimaryKeyWithBLOBs(tPlanMeasure);
		if(tstatus!=0){
		return AmpcResult.build(0, "del_measureContent success");
		}
		return AmpcResult.build(1, "del_measureContent error");
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 *查询可复制预案
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/plan/copy_plan_list")
	public  AmpcResult copy_plan_list(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long userId=Long.parseLong(data.get("userId").toString());
		TPlan tPlan=new TPlan();
		tPlan.setCopyPlan("1");
		tPlan.setIsEffective("1");
		//根据是否为可复制预案和是否有效字段查询
		//List<TPlan> planlist=tPlanMapper.selectByEntity(tPlan);
		JSONObject obj=new JSONObject();
		JSONArray arr=new JSONArray();
//		for(TPlan plan:planlist){
//			JSONObject objs=new JSONObject();
//			objs.put("planName", plan.getPlanName());
//			objs.put("planId", plan.getPlanId());
//			arr.add(objs);
//		}
		obj.put("copyPlanlist", arr);
		return AmpcResult.build(0, "copy_plan_list success",obj);
	}
	
	
}
