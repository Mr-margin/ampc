package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TMeasureSectorExcelMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TQueryExcelMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectordocExcelMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TPlanMeasure;
import ampc.com.gistone.database.model.TPlanMeasureWithBLOBs;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.entity.JPResult;
import ampc.com.gistone.entity.MeasureContentUtil;
import ampc.com.gistone.entity.MeasureUtil;
import ampc.com.gistone.entity.SMUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;

/**
 * 预案措施控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月13日
 */
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
	//行业措施中间表映射
	@Autowired
	public TMeasureSectorExcelMapper tMeasureSectorExcelMapper;
	
	
	
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
		    	Long id=tPlanMapper.getIdByQuery(map);
		    	//创建预案要更改时段的状态
		    	TTime t=new TTime();
		    	t.setTimeId(timeId);
		    	t.setPlanId(id);
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
	 * 复制预案
	 * @author WangShanxi
	 */
	@RequestMapping("/plan/copy_plan")
	public  AmpcResult copy_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//时段id
			Long timeId=Long.parseLong(data.get("timeId").toString());
			//将被复制的预案id存入要复制到的时段里
			TTime tTime=new TTime();
			tTime.setPlanId(planId);
			tTime.setTimeId(timeId);
			tTime.setUserId(userId);
			int copy_status=tTimeMapper.updateByPrimaryKeySelective(tTime);
			//判断是否成功
			if(copy_status!=0){
				return AmpcResult.ok("复用成功");
			}
			return AmpcResult.build(1000,"复用失败");
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000,"参数错误");
		}
	}
	
	/**
	 * 预案设置成可复制
	 * @author WangShanxi 
	 */
	@RequestMapping("/plan/iscopy_plan")
	public  AmpcResult iscopy_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) {
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//将预案的是否可以复制状态改为可复制
			TPlan tPlan=new TPlan();
			tPlan.setPlanId(planId);
			tPlan.setCopyPlan("1");
			tPlan.setUserId(userId);
			int status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
			//查看修改是否成功
			if(status!=0){
				return AmpcResult.ok("修改成功");
			}
			return AmpcResult.build(1000, "修改失败");
		}catch(Exception e){
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	/**
	 * 预案编辑完成操作
	 * @author WangShanxi
	 */
	@RequestMapping("/plan/finish_plan")
	public  AmpcResult finish_plan(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//创建条件进行查询所有预案措施
			TPlanMeasure tPlanMeasure=new TPlanMeasure();
			tPlanMeasure.setPlanId(planId);
			tPlanMeasure.setUserId(userId);
			List<TPlanMeasureWithBLOBs> tlist=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
			for(TPlanMeasureWithBLOBs t:tlist){
				//如果预案措施中的子措施为空 则删除掉该条预案措施
				if(t.getMeasureContent().equals("-1")||t.getMeasureContent()==null){
					int status=tPlanMeasureMapper.deleteByPrimaryKey(t.getPlanMeasureId());
					//如果删除后的返回值小于0 则删除失败
					if(status<0){
						return AmpcResult.build(1000, "删除失败",null);	
					}
				}
			}
			return AmpcResult.ok("删除成功");	
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	
	/**
	 * 查询可复制预案
	 * @author WangShanxi
	 */
	@RequestMapping("/plan/copy_plan_list")
	public  AmpcResult copy_plan_list(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户Id
			Long userId=Long.parseLong(data.get("userId").toString());
			//根据是否为可复制预案和是否有效字段查询
			List<Map> list=tPlanMapper.selectCopyList(userId);
			return AmpcResult.ok(list);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000,"参数错误");
		}
	}
	
	/**
	 * 预案编辑方法
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("plan/get_planInfo")
	public AmpcResult get_planInfo(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案id
			Long planId=Long.parseLong(data.get("planId").toString());
			//时段id
			Long timeId=Long.parseLong(data.get("timeId").toString());
			//预案开始时间
			Date startDate=new Date(Long.parseLong(data.get("timeStartTime").toString()));
			//预案结束时间
			Date endDate=new Date(Long.parseLong(data.get("timeEndTime").toString()));
			//用户的id  确定当前用户
			Long userId=null;
			if(data.get("userId")!=null){
				userId = Long.parseLong(data.get("userId").toString());
			}
			
			TPlan tplan=tPlanMapper.selectByPrimaryKey(planId);
			//判断是否是可复制预案
			if(tplan.getCopyPlan().equals("1")){
				//新建预案
				TPlan newtplan=new TPlan();
				newtplan.setUserId(userId);
				newtplan.setPlanName(tplan.getPlanName());
				newtplan.setAreaId(tplan.getAreaId());
				newtplan.setMissionId(tplan.getMissionId());
				newtplan.setScenarioId(tplan.getScenarioId());
				newtplan.setPlanStartTime(startDate);
				newtplan.setPlanEndTime(endDate);
				//根据可复制预案信息新建预案数据
				int result=tPlanMapper.insertSelective(newtplan);
				if(result>0){
					//创建条件查询新添加的预案的Id
					Map map=new HashMap();
					map.put("userId", userId);
					map.put("scenarioId", newtplan.getScenarioId());
					map.put("planName", newtplan.getPlanName());
					Long newPlanId=tPlanMapper.getIdByQuery(map);
					//根据可复制预案id查询预案中的措施
					TPlanMeasure tPlanMeasure=new TPlanMeasure();
					tPlanMeasure.setPlanId(planId);
					tPlanMeasure.setUserId(userId);
					//根据预案措施对象的条件查询所有的预案措施满足条件的信息
					List<TPlanMeasureWithBLOBs> planMeasureList=tPlanMeasureMapper.selectByEntity(tPlanMeasure);
					if(planMeasureList.size()>0){
						//循环结果 并复制信息 新建
						for(TPlanMeasureWithBLOBs t:planMeasureList){
							TPlanMeasureWithBLOBs newtPlanMeasure=new TPlanMeasureWithBLOBs();
							newtPlanMeasure.setMeasureId(t.getMeasureId());
							newtPlanMeasure.setPlanId(newPlanId);
							newtPlanMeasure.setSectorName(t.getSectorName());
							newtPlanMeasure.setUserId(userId);
							newtPlanMeasure.setImplementationScope(t.getImplementationScope());
							newtPlanMeasure.setMeasureContent(t.getMeasureContent());
							newtPlanMeasure.setReductionRatio(t.getReductionRatio());
							result=tPlanMeasureMapper.insertSelective(newtPlanMeasure);
							if(result<0){
								return AmpcResult.build(1000, "复制预案措施时出错");
							}
						}
					}
					//将时段中的预案ID修改成已经新建的预案Id
					TTime tTime=new TTime();
					tTime.setPlanId(newPlanId);
					tTime.setTimeId(timeId);
					tTime.setUserId(userId);
					result =tTimeMapper.updateByPrimaryKeySelective(tTime);
					//判断是否成功
					if(result <0){
						return AmpcResult.build(1000, "修改时段信息时出错");
					}
					//将复制后的情景Id 保存
					planId=newPlanId;
				}else{
					return AmpcResult.build(1000, "复制预案信息时出错");
				}
			}
			//根据UserId查询所有的行业名称
			List<Map> nameList = this.tMeasureSectorExcelMapper.getSectorInfo(userId);
			if(nameList.size()==0){
				nameList = this.tMeasureSectorExcelMapper.getSectorInfo(null);
			}
			//过滤掉一些重复的名称
			LinkedHashSet<String> nameSet=new LinkedHashSet<String>();
			for (Map map : nameList) {
				nameSet.add(map.get("sectorsname").toString());
			}
			//将结果同意放在一个帮助类中 用来返回结果
			List<SMUtil> result=new ArrayList<SMUtil>();
			for (String name : nameSet) {
				//创建结果对象
				SMUtil sm=new SMUtil();
				//创建条件 
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("sectorsName", name);
				map.put("userId", userId);
				sm.setSectorsName(name);
				//查询当前名称下有几个措施
				List<Map> list = this.tMeasureSectorExcelMapper.getMeasureInfo(map);
				if(list.size()==0){
					map.put("userId", null);
					list = this.tMeasureSectorExcelMapper.getMeasureInfo(map);
				}
				sm.setMeasureItems(list);
				//创建条件 
				map=new HashMap<String,Object>();
				map.put("planId", planId);
				map.put("userId", userId);
				map.put("sectorName", name);
				//用来去查预案措施中的数据，用来确认当前有几个措施正在使用
				List<Map> measureList=tPlanMeasureMapper.selectIdByQuery(map);
				if(measureList.size()==0){
					map.put("userId", null);
					measureList=tPlanMeasureMapper.selectIdByQuery(map);
				}
				sm.setPlanMeasure(measureList);
				sm.setCount(measureList.size());
				//将结果放入结果集中！
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
				mu.setNameen("a");
				mu.setNamech(tme.getMeasureExcelAname().toString());
				mu.setValue(tme.getMeasureExcelA());
				String[] a=CheckRange(tme.getMeasureExcelArange().toString());
				mu.setMinValue(a[0]);
				mu.setMaxValue(a[1]);
				mlist.add(mu);
			}
			if(tme.getMeasureExcelA1()!=null){
				mu=new MeasureUtil();
				mu.setNameen("a1");
				mu.setNamech(tme.getMeasureExcelA1name().toString());
				mu.setValue(tme.getMeasureExcelA1());
				String[] a1=CheckRange(tme.getMeasureExcelA1range().toString());
				mu.setMinValue(a1[0]);
				mu.setMaxValue(a1[1]);
				mlist.add(mu);
			}
			//只显示中长期的
			if(tme.getMeasureExcelType().equals("中长期措施")){
				if(tme.getMeasureExcelIntensity()!=null){
					mu=new MeasureUtil();
					mu.setNameen("intensity");
					mu.setNamech(tme.getMeasureExcelIntensityname().toString());
					mu.setValue(tme.getMeasureExcelIntensity());
					String[] intensity=CheckRange(tme.getMeasureExcelIntensityrange().toString());
					mu.setMinValue(intensity[0]);
					mu.setMaxValue(intensity[1]);
					mlist.add(mu);
				}
			}
			if(tme.getMeasureExcelIntensity1()!=null){
				mu=new MeasureUtil();
				mu.setNameen("intensity1");
				mu.setNamech(tme.getMeasureExcelIntensity1name().toString());
				mu.setValue(tme.getMeasureExcelIntensity1());
				String[] intensity1=CheckRange(tme.getMeasureExcelIntensity1range().toString());
				mu.setMinValue(intensity1[0]);
				mu.setMaxValue(intensity1[1]);
				mlist.add(mu);
			}
			if(tme.getMeasureExcelAsh()!=null){
				mu=new MeasureUtil();
				mu.setNameen("ash");
				mu.setNamech(tme.getMeasureExcelAshname().toString());
				mu.setValue(tme.getMeasureExcelAsh());
				String[] ash=CheckRange(tme.getMeasureExcelAshrange().toString());
				mu.setMinValue(ash[0]);
				mu.setMaxValue(ash[1]);
				mlist.add(mu);
			}
			if(tme.getMeasureExcelSulfer()!=null){
				mu=new MeasureUtil();
				mu.setNameen("sulfer");
				mu.setNamech(tme.getMeasureExcelSulfername().toString());
				mu.setValue(tme.getMeasureExcelSulfer());
				String[] sulfer=CheckRange(tme.getMeasureExcelSulferrange().toString());
				mu.setMinValue(sulfer[0]);
				mu.setMaxValue(sulfer[1]);
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
					mu.setNamech(tme.getMeasureExcelSvname().toString());
					mu.setNameen(svsss[0]);
					mu.setValue(svsss[1]);
					String[] svsv=CheckRange(tme.getMeasureExcelSvrange().toString());
					mu.setMinValue(svsv[0]);
					mu.setMaxValue(svsv[1]);
					mlist.add(mu);
				}
			}
			//创建结果集 并写入对应信息
			Map resultMap=new HashMap();
			resultMap.put("measureColumn", sdMap);
			resultMap.put("measureList", mlist);
			resultMap.put("query", tqeList);
			if(planMeasureId!=null){
				TPlanMeasureWithBLOBs tPlanMeasure=tPlanMeasureMapper.selectByPrimaryKey(planMeasureId);
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
	 * 预案添加措施  或者修改
	 * @author WangShanxi
	 */
	@RequestMapping("/measure/addOrUpdate_measure")
	public  AmpcResult addOrUpdate_measure(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		try{	
			//添加跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//行业名称
			String sectorName=data.get("sectorName").toString();
			//措施id
			Long measureId=Long.parseLong(data.get("measureId").toString());
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//预案id 
			Long planId=Long.parseLong(data.get("planId").toString());
			//预案措施id
			Long planMeasureId=null;
			if(!data.get("planMeasureId").equals("null")){
				planMeasureId = Long.parseLong(data.get("planMeasureId").toString());
			}
			//预案措施中的子措施Json串、
			System.out.println(data.get("measureContent").toString());
			String measureContent=data.get("measureContent").toString();
			TPlanMeasureWithBLOBs tPlanMeasure=new TPlanMeasureWithBLOBs();
			tPlanMeasure.setMeasureId(measureId);
			tPlanMeasure.setPlanId(planId);
			tPlanMeasure.setSectorName(sectorName);
			tPlanMeasure.setUserId(userId);
			tPlanMeasure.setMeasureContent(measureContent);
			
			
			
			
			
			
			
			
			
			
			
			
			
			/**
			 * TODO  需要自己解析得到减排比例
			 */
			//tPlanMeasure.setImplementationScope(implementAtionScope);
			//tPlanMeasure.setReductionRatio(reductionRatio);
			if(data.get("planMeasureId").equals("null")){
				//预案添加措施
				int addstatus=tPlanMeasureMapper.insertSelective(tPlanMeasure);
				//判断是否成功
				if(addstatus!=0){
					return AmpcResult.ok("添加成功");
				}else{
					return AmpcResult.build(1000,"添加失败");
				}
			}else{
				//预案修改措施
				tPlanMeasure.setPlanMeasureId(planMeasureId);
				int updatestatus=tPlanMeasureMapper.updateByPrimaryKeyWithBLOBs(tPlanMeasure);
				//判断是否成功
				if(updatestatus!=0){
					return AmpcResult.ok("修改成功");
				}else{
					return AmpcResult.build(1000,"修改失败");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000,"参数错误");
		}
	}
	
	/**
	 * 删除预案中的措施
	 * @author WangShanxi 
	 */
	@RequestMapping("/measure/delete_measure")
	public  AmpcResult delete_measure(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,
			HttpServletResponse response) {
		try{
			//添加跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案措施表集合
			String planMeasureIds=data.get("planMeasureIds").toString();
			//用户id
			Long userId=Long.parseLong(data.get("userId").toString());
			//拆分要删除的id
			List<Long> idss=new ArrayList<Long>();
			String[] ids=planMeasureIds.split(",");
			for(int i=0;i<ids.length;i++){
				idss.add(Long.parseLong(ids[i]));
			}
			//放入条件
			Map map =new HashMap();
			map.put("userId", userId);
			map.put("idss", idss);
			//删除预案中的措施
			int delete_status=tPlanMeasureMapper.deleteMeasures(map);
			if(delete_status!=0){
				return AmpcResult.ok("删除成功！");
			}
			return AmpcResult.build(1000, "删除失败");
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	
	
	/**
	 * 
	 * 措施汇总中数据的减排计算
	 * @author WangShanxi
	 */
	@RequestMapping("/jp/pmjp")
	public AmpcResult pmjp(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		//添加异常捕捉
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//预案id
			String planMeasureIds=data.get("planMeasureIds").toString();
			//将得到的数据拆分 放入集合中
			String[] idss=planMeasureIds.split(",");
			List<Long> planMeasureIdss=new ArrayList<Long>();
			for(int i=0;i<idss.length;i++){
				planMeasureIdss.add(Long.parseLong(idss[i]));
			}
			//根据拆分后得到的id查询所有的预案措施对象
			List<Map> pmList=tPlanMeasureMapper.getPmByIds(planMeasureIdss);
			//创建一个减排的结果集合
			List<JPResult> jpList=new ArrayList<JPResult>();
			//循环每一个预案措施对象 拼接想要的数据放入JPResult帮助类集合中
			for (Map pm : pmList) {
				//创建JPResult帮助类;
				JPResult result=new JPResult();
				//将子措施转换成JsonObject对象进行解析
				Clob clob=(Clob)pm.get("measureContent");
				JSONObject jsonobject = JSONObject.fromObject(clob.getSubString(1, (int) clob.length()));
				//设置内部值的转换类型
				Map<String, Class> cmap = new HashMap<String, Class>(); 
				cmap.put("filters", HashMap.class);
				cmap.put("summary", HashMap.class); 
				cmap.put("table", HashMap.class);
				cmap.put("table1", HashMap.class);
				cmap.put("oopp", HashMap.class);
				//将json对象转换成Java对象
				MeasureContentUtil mcu= (MeasureContentUtil)JSONObject.toBean(jsonobject,MeasureContentUtil.class,cmap);
				//写入BigIndex
				result.setBigIndex(mcu.getBigIndex());
				//写入SmallIndex
				result.setSmallIndex(mcu.getSmallIndex());
				//写入唯一Id 是预案措施Id + 预案名称  
				result.setGroupName(pm.get("planMeasureId").toString()+pm.get("planName"));
				//写入预案开始时间
				result.setStart(pm.get("planStartTime").toString().substring(0,pm.get("planStartTime").toString().indexOf(".")));
				//写入预案结束时间
				result.setEnd(pm.get("planEndTime").toString().substring(0,pm.get("planEndTime").toString().indexOf(".")));
				//获取Json中的Filter
				List<Map> lms=mcu.getFilters();
				//获取Json中的子措施汇总
				List<Map> table1=mcu.getTable1();
				//创建要减排分析中的子项
				List<Object> opList=new ArrayList<Object>();
				//循环Filter 因为Filter中的项 和 子措施汇总的是相同的 所以循环一个
				for(int i=0;i<lms.size();i++){
					Map opresult=new HashMap();
					//获取对应Filter中的子措施
					Map t1map=table1.get(i);
					//获取用户修改的OP
					Map opmap=(Map)t1map.get("oopp");
					opresult.put("filter", lms.get(i));
					opresult.put("l4sFilter", pm.get("l4sFilter"));
					opresult.put("opLevel",pm.get("level"));
					opresult.put("opName", pm.get("measureName"));
					opresult.put("opType", pm.get("op"));
					for(Object obj : opmap.keySet()){
						opresult.put(obj, Double.parseDouble(opmap.get(obj).toString()));
					}
					opList.add(opresult);
				}
				result.setOps(opList);
				jpList.add(result);
			}
			//将java对象转换为json对象
			JSONArray json = JSONArray.fromObject(jpList);
			String str = json.toString();
			System.out.println(str);
			//调用减排计算接口 并获取结果Json
			String getResult=ClientUtil.doPost("http://192.168.1.53:8089/calc/submit/subSector",str);
			System.out.println(getResult);
			
			/**
			 * TODO  要保存减排结果
			 */
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			return AmpcResult.ok(getResult);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 取值范围验证方法
	 * @param str
	 * @return
	 */
	public static String[] CheckRange(String str){
		String[] temp=str.split("~");
		if(temp[0].equals("inf")){
			temp[0]="null";
		}
		if(temp[1].equals("inf")){
			temp[1]="null";
		}
		return temp;
	}
	
}
