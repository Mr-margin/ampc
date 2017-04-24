package ampc.com.gistone.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Clob;
import java.sql.SQLException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TMeasureSectorExcelMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TPlanMeasureReuseMapper;
import ampc.com.gistone.database.inter.TPlanReuseMapper;
import ampc.com.gistone.database.inter.TQueryExcelMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectordocExcelMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TPlanMeasure;
import ampc.com.gistone.database.model.TPlanMeasureReuseWithBLOBs;
import ampc.com.gistone.database.model.TPlanMeasureWithBLOBs;
import ampc.com.gistone.database.model.TPlanReuse;
import ampc.com.gistone.database.model.TPlanReuseWithBLOBs;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.entity.JPResult;
import ampc.com.gistone.entity.MeasureContentUtil;
import ampc.com.gistone.entity.MeasureUtil;
import ampc.com.gistone.entity.SMUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.CastNumUtil;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.ScenarinoStatusUtil;

/**
 * 预案措施控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月13日
 */
@RestController
@RequestMapping
public class PlanAndMeasureController {
	//公用的Jackson解析对象
	private ObjectMapper mapper=new ObjectMapper();
	//读取路径的帮助类
	@Autowired
	private ConfigUtil configUtil;
	// 情景映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	// 情景区域映射
	@Autowired
	private TScenarinoAreaMapper tScenarinoAreaMapper;
	// 预案库映射
	@Autowired
	private TPlanReuseMapper tPlanReuseMapper;
	// 预案措施映射
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	// 预案措施库映射
	@Autowired
	private TPlanMeasureReuseMapper tPlanMeasureReuseMapper;
	// 行业映射
	@Autowired
	private TSectorExcelMapper tSectorExcelMapper;
	// 措施映射
	@Autowired
	private TMeasureExcelMapper tMeasureExcelMapper;
	// 预案映射
	@Autowired
	private TPlanMapper tPlanMapper;
	// 时段映射
	@Autowired
	private TTimeMapper tTimeMapper;
	// 默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	// 行业条件映射
	@Autowired
	public TQueryExcelMapper tQueryExcelMapper;
	// 行业描述映射
	@Autowired
	public TSectordocExcelMapper tSectordocExcelMapper;
	// 行业措施中间表映射
	@Autowired
	public TMeasureSectorExcelMapper tMeasureSectorExcelMapper;
	//情景状态更新帮助类
	@Autowired
	private ScenarinoStatusUtil scenarinoStatusUtil=new ScenarinoStatusUtil();

	
	/**
	 * 重置情景状态
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/plan/update_Status")
	public AmpcResult update_Status(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			if(scenarinoStatusUtil.updateScenarinoStatus(scenarinoId)>0){
				LogUtil.getLogger().info("PlanAndMeasureController 重置情景状态成功!");
				return AmpcResult.ok("更改情景状态成功");
			}
			LogUtil.getLogger().error("PlanAndMeasureController 重置情景状态失败,数据库更改失败。");
			return AmpcResult.build(1000, "更改情景状态失败");
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController 重置情景异常!",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	/**
	 * 创建预案
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/plan/add_plan")
	public AmpcResult add_plan(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 时段id
			Long timeId = Long.parseLong(data.get("timeId").toString());
			//情景状态
			Long scenarinoStatus = Long.parseLong(data.get("scenarinoStatus").toString());
			// 预案名称
			String planName = data.get("planName").toString();
			//情景id
			Long scenarioId = Long.parseLong(data.get("scenarioId").toString());
			// 所属任务id
			Long missionId = Long.parseLong(data.get("missionId").toString());
			// 区域id
			Long areaId = Long.parseLong(data.get("areaId").toString());
			// 预案开始时间
			Date startDate = DateUtil.StrToDate(data.get("timeStartTime")
					.toString());
			// 预案结束时间
			Date endDate = DateUtil.StrToDate(data.get("timeEndTime")
					.toString());
			// 创建预案对象
			TPlan tPlan = new TPlan();
			tPlan.setAreaId(areaId);
			tPlan.setMissionId(missionId);
			tPlan.setScenarioId(scenarioId);
			tPlan.setUserId(userId);
			tPlan.setPlanName(planName);
			tPlan.setPlanStartTime(startDate);
			tPlan.setPlanEndTime(endDate);
			// 添加预案
			int addstatus = tPlanMapper.insertSelective(tPlan);
			// 判断是否添加成功，根据结果返回值
			if (addstatus != 0) {
				Map map = new HashMap();
				map.put("userId", userId);
				map.put("scenarioId", scenarioId);
				map.put("planName", planName);
				Long id = tPlanMapper.getIdByQuery(map);
				// 创建预案要更改时段的状态
				TTime t = new TTime();
				t.setTimeId(timeId);
				t.setPlanId(id);
				tTimeMapper.updateByPrimaryKeySelective(t);
				if(scenarinoStatus==1){
					int a=scenarinoStatusUtil.updateScenarinoStatus(scenarioId);
					if(a!=0){ 
						LogUtil.getLogger().info("PlanAndMeasureController 创建预案成功!");
						return AmpcResult.ok(id);
					}else{
						LogUtil.getLogger().error("PlanAndMeasureController 创建预案是,情景状态转换失败,数据库修改状态失败。");
						return AmpcResult.build(1000, "情景状态转换失败",null);
					}
				}else{
					LogUtil.getLogger().info("PlanAndMeasureController 创建预案成功!");
					return AmpcResult.ok(id);
				}	
			}
			LogUtil.getLogger().error("PlanAndMeasureController 创建预案失败,数据库添加失败。");
			return AmpcResult.build(1000, "添加失败");
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController 创建预案异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 复制预案
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/plan/copy_plan")
	public AmpcResult copy_plan(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 时段id
			Long timeId = Long.parseLong(data.get("timeId").toString());
			//情景状态
			Long scenarinoStatus = Long.parseLong(data.get("scenarinoStatus").toString());
			//情景id
			Long scenarioId = Long.parseLong(data.get("scenarioId").toString());
			// 所属任务id
			Long missionId = Long.parseLong(data.get("missionId").toString());
			// 区域id
			Long areaId = Long.parseLong(data.get("areaId").toString());
			// 预案开始时间
			Date startDate = DateUtil.StrToDate(data.get("timeStartTime").toString());
			// 预案结束时间
			Date endDate = DateUtil.StrToDate(data.get("timeEndTime").toString());
			// 要复制的预案id
			Long copyPlanId = Long.parseLong(data.get("copyPlanId").toString());
			//查询要复制预案的对象
			TPlanReuseWithBLOBs tb=tPlanReuseMapper.selectByPrimaryKey(copyPlanId);
			// 创建预案对象
			TPlan tPlan = new TPlan();
			tPlan.setAreaId(areaId);
			tPlan.setMissionId(missionId);
			tPlan.setScenarioId(scenarioId);
			tPlan.setUserId(userId);
			tPlan.setPlanName(tb.getPlanReuseName());
			tPlan.setPlanStartTime(startDate);
			tPlan.setPlanEndTime(endDate);
			// 添加预案
			int result = tPlanMapper.insertSelective(tPlan);
			// 判断是否添加成功，根据结果返回值
			if (result != 0) {
				Map map = new HashMap();
				map.put("userId", userId);
				map.put("scenarioId", scenarioId);
				map.put("planName", tb.getPlanReuseName());
				Long newPlanId = tPlanMapper.getIdByQuery(map);
				map.put("copyPlanId", copyPlanId);
				// 根据预案措施对象的条件查询所有的预案措施满足条件的信息
				List<TPlanMeasureReuseWithBLOBs> planMeasureReuseList = tPlanMeasureReuseMapper.selectByQuery(map);
				if (planMeasureReuseList.size() > 0) {
					// 循环结果 并复制信息 新建
					for (TPlanMeasureReuseWithBLOBs t : planMeasureReuseList) {
						TPlanMeasureWithBLOBs newtPlanMeasure = new TPlanMeasureWithBLOBs();
						newtPlanMeasure.setMeasureId(t.getMeasureId());
						newtPlanMeasure.setPlanId(newPlanId);
						newtPlanMeasure.setSectorName(t.getSectorName());
						newtPlanMeasure.setUserId(userId);
						newtPlanMeasure.setImplementationScope(t.getImplementationScope());
						newtPlanMeasure.setMeasureContent(t.getMeasureContent());
						newtPlanMeasure.setReductionRatio(t.getReductionRatio());
						newtPlanMeasure.setRatio(t.getRatio());
						newtPlanMeasure.setTableRatio(t.getTableRatio());
						newtPlanMeasure.setTableItem(t.getTableItem());
						newtPlanMeasure.setTablePool(t.getTablePool());
						result = tPlanMeasureMapper.insertSelective(newtPlanMeasure);
						if (result < 0) {
							LogUtil.getLogger().error("PlanAndMeasureController 复制预案失败,数据库添加预案措施时失败。");
							return AmpcResult.build(1000, "应用预案措施时出错");
						}
					}
				}
				// 将被复制的预案id存入要复制到的时段里
				TTime tTime = new TTime();
				tTime.setPlanId(newPlanId);
				tTime.setTimeId(timeId);
				tTime.setUserId(userId);
				int copy_status = tTimeMapper.updateByPrimaryKeySelective(tTime);
				// 判断是否成功
				if (copy_status != 0) {
					if(scenarinoStatus==1){
						int a=scenarinoStatusUtil.updateScenarinoStatus(scenarioId);
						if(a!=0){ 
							LogUtil.getLogger().info("PlanAndMeasureController 复用预案成功。");
							return AmpcResult.ok(newPlanId);
						}else{
							LogUtil.getLogger().error("PlanAndMeasureController 复用预案失败,数据库更改情景状态失败。");
							return AmpcResult.build(1000, "情景状态转换失败",-1);
						}
					}else{
						LogUtil.getLogger().info("PlanAndMeasureController 复用预案成功。");
						return AmpcResult.ok(newPlanId);
					}
				}
				LogUtil.getLogger().error("PlanAndMeasureController 复用预案失败,数据库更改时段中预案指向失败。");
				return AmpcResult.build(1000, "复用失败",-1);
				
			}
			LogUtil.getLogger().error("PlanAndMeasureController 复用预案失败,数据库添加新预案失败。");
			return AmpcResult.build(1000, "应用预案失败",-1);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController 复用预案异常。",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 预案存入预案库
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/plan/iscopy_plan")
	public AmpcResult iscopy_plan(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			Map mapQuery=new HashMap();
			mapQuery.put("userId", userId);
			mapQuery.put("planId", planId);
			//根据主键Id查询要存入预案库的预案
			Map resultMap = tPlanMapper.getInfoByQuery(mapQuery);
			// 新建预案库对象
			TPlanReuseWithBLOBs pr=new TPlanReuseWithBLOBs();
			pr.setUserId(userId);
			pr.setPlanReuseName(resultMap.get("planName"));
			pr.setAreaName(resultMap.get("areaName"));
			pr.setMissionName(resultMap.get("missionName"));
			pr.setScenarioName(resultMap.get("scenarinoName"));
			pr.setTimeStartTime(DateUtil.StrToDate1(resultMap.get("timeStartDate").toString()));
			pr.setTimeEndTime(DateUtil.StrToDate1(resultMap.get("timeEndDate").toString()));
			pr.setOldPlanId(planId);
			//因为存入的是CLOB对象 所以要进行转换
			Clob clob = null;
			String codes="";
			if(resultMap.get("provinceCodes")!=null){
				clob = (Clob) resultMap.get("provinceCodes");
				codes=clob.getSubString(1, (int) clob.length());
				pr.setProvinceCodes(codes);
			}
			if(resultMap.get("cityCodes")!=null){
				clob = (Clob) resultMap.get("cityCodes");
				codes=clob.getSubString(1, (int) clob.length());
				pr.setCityCodes(codes);
			}
			if(resultMap.get("countyCodes")!=null){
				clob = (Clob) resultMap.get("countyCodes");
				codes=clob.getSubString(1, (int) clob.length());
				pr.setCountyCodes(codes);
			}
			// 根据可复制预案信息新建预案数据
			int result = tPlanReuseMapper.insertSelective(pr);
			if (result > 0) {
				// 创建条件查询新添加的预案的Id
				Map map = new HashMap();
				map.put("userId", userId);
				map.put("oldPlanId", planId);
				map.put("planName", pr.getPlanReuseName());
				Long newPlanReuseId = tPlanReuseMapper.getIdByQuery(map);
				// 根据可复制预案id查询预案中的措施
				TPlanMeasureWithBLOBs tPlanMeasure = new TPlanMeasureWithBLOBs();
				tPlanMeasure.setPlanId(planId);
				tPlanMeasure.setUserId(userId);
				// 根据预案措施对象的条件查询所有的预案措施满足条件的信息
				List<TPlanMeasureWithBLOBs> planMeasureList = tPlanMeasureMapper
						.selectByEntity(tPlanMeasure);
				if (planMeasureList.size() > 0) {
					// 循环结果 并复制信息 新建
					for (TPlanMeasureWithBLOBs t : planMeasureList) {
						TPlanMeasureReuseWithBLOBs newtPlanMeasure = new TPlanMeasureReuseWithBLOBs();
						newtPlanMeasure.setMeasureId(t.getMeasureId());
						newtPlanMeasure.setPlanReuseId(newPlanReuseId);
						newtPlanMeasure.setSectorName(t.getSectorName());
						newtPlanMeasure.setUserId(userId);
						newtPlanMeasure.setImplementationScope(t
								.getImplementationScope());
						newtPlanMeasure.setMeasureContent(t
								.getMeasureContent());
						newtPlanMeasure.setReductionRatio(t
								.getReductionRatio());
						newtPlanMeasure.setRatio(t.getRatio());
						newtPlanMeasure.setTableRatio(t.getTableRatio());
						newtPlanMeasure.setTableItem(t.getTableItem());
						newtPlanMeasure.setTablePool(t.getTablePool());
						result = tPlanMeasureReuseMapper
								.insertSelective(newtPlanMeasure);
						if (result < 0) {
							LogUtil.getLogger().error("PlanAndMeasureController 预案存入预案库失败,数据库添加预案措施库数据失败。");
							return AmpcResult.build(1000, "复制预案措施时出错");
						}
					}
				}
			} else {
				LogUtil.getLogger().error("PlanAndMeasureController  预案存入预案库失败,数据库添加预案库数据失败。");
				return AmpcResult.build(1000, "复制预案信息时出错");
			}
			LogUtil.getLogger().info("PlanAndMeasureController  预案存入预案库成功！");
			return AmpcResult.ok("复制预案措施成功");
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController  预案存入预案库异常!",e);
			return AmpcResult.build(1000, "参数错误", null);
		}
	}

	/**
	 * 查询可复制预案
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/plan/copy_plan_list")
	public AmpcResult copy_plan_list(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户Id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 根据是否为可复制预案和是否有效字段查询
			List<Map> list = tPlanReuseMapper.selectCopyList(userId);
			Clob clob = null;
			String codes="";
			//因为存入的是CLOB对象 所以要进行转换
			for(Map map:list){
				if(map.get("provinceCodes")!=null){
					clob = (Clob) map.get("provinceCodes");
					codes=clob.getSubString(1, (int) clob.length());
					map.put("provinceCodes", codes);
				}
				if(map.get("cityCodes")!=null){
					clob = (Clob) map.get("cityCodes");
					codes=clob.getSubString(1, (int) clob.length());
					map.put("cityCodes", codes);				
				}
				if(map.get("countyCodes")!=null){
					clob = (Clob) map.get("countyCodes");
					codes=clob.getSubString(1, (int) clob.length());
					map.put("countyCodes", codes);
				}
			}
			LogUtil.getLogger().info("PlanAndMeasureController  查询可复用预案成功。");
			return AmpcResult.ok(list);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController  查询可复用预案异常。",e);
			return AmpcResult.build(1000, "参数错误");
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
	public AmpcResult get_planInfo(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			// 时段id
			Long timeId = Long.parseLong(data.get("timeId").toString());
			// 预案开始时间
			Date startDate = new Date(Long.parseLong(data.get("timeStartTime")
					.toString()));
			// 预案结束时间
			Date endDate = new Date(Long.parseLong(data.get("timeEndTime")
					.toString()));
			// 用户的id 确定当前用户
			Long userId = null;
			if (data.get("userId") != null) {
				userId = Long.parseLong(data.get("userId").toString());
			}
			// 根据UserId查询所有的行业名称
			List<String> nameList = this.tMeasureSectorExcelMapper.getSectorInfo(userId);
			if (nameList.size() == 0) {
				nameList = this.tMeasureSectorExcelMapper.getSectorInfo(null);
			}
			// 根据UserId查询所有的带有条件的行业名称 因为要优先显示
			List<String> queryList = this.tQueryExcelMapper.selectName(userId);
			//如果为空就查询系统默认的
			if (queryList.size() == 0) {
				queryList = this.tQueryExcelMapper.selectName(null);
			}
			//创建中间集合
			List<String> nameSet=new ArrayList<String>();
			//如果包含了条件的优先加入
			for(int i=0;i<queryList.size();i++){
				for(String str:nameList){
					if(str.equals(queryList.get(i))){
						nameSet.add(str);
					}
				}
			}
			//加入其他的行业名称
			sn:for(String str:nameList){
				for(int i=0;i<nameSet.size();i++){
					if(str.equals(nameSet.get(i))){
						continue sn;
					}
				}
				nameSet.add(str);
			}
			// 将结果统一放在一个帮助类中 用来返回结果
			List<SMUtil> result = new ArrayList<SMUtil>();
			for (String name : nameSet) {
				// 创建结果对象
				SMUtil sm = new SMUtil();
				// 创建条件
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("sectorsName", name);
				map.put("userId", userId);
				sm.setSectorsName(name);
				// 查询当前名称下有几个措施
				List<Map> list = this.tMeasureSectorExcelMapper
						.getMeasureInfo(map);
				if (list.size() == 0) {
					map.put("userId", null);
					list = this.tMeasureSectorExcelMapper.getMeasureInfo(map);
				}
				sm.setMeasureItems(list);
				// 创建条件
				map = new HashMap<String, Object>();
				map.put("planId", planId);
				map.put("userId", userId);
				map.put("sectorName", name);
				// 用来去查预案措施中的数据，用来确认当前有几个措施正在使用
				List<Map> measureList = tPlanMeasureMapper.selectIdByQuery(map);
				if (measureList.size() == 0) {
					map.put("userId", null);
					measureList = tPlanMeasureMapper.selectIdByQuery(map);
				}
				sm.setPlanMeasure(measureList);
				sm.setCount(measureList.size());
				// 将结果放入结果集中！
				result.add(sm);
			}
			LogUtil.getLogger().info("PlanAndMeasureController  预案编辑成功!");
			// 返回结果
			return AmpcResult.ok(result);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   预案编辑异常",e);
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}

	/**
	 * 措施汇总查询
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("measure/get_measureList")
	public AmpcResult get_MeasureList(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 污染物类型
			String stainType = data.get("stainType").toString();
			// 添加信息到参数中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("planId", planId);
			map.put("userId", userId);
			// 查看行业名称是否为空，不为空添加，为空不加
			if (data.get("sectorName") != null) {
				map.put("sectorName", data.get("sectorName"));
			} else {
				map.put("sectorName", null);
			}
			// 查询措施
			List<Map> list = tPlanMeasureMapper.selectByQuery(map);
			for (int i = 0; i < list.size(); i++) {
				// 直接给点源范围
				if (list.get(i).get("implementationScope") != null) {
					list.get(i).put("implementationScope",list.get(i).get("implementationScope"));
				}
				// 判断是否有汇总 和子措施的Json串
				if (list.get(i).get("tablePool") != null) {
					// 对Clob对象进行转换成对应Java对象
					Clob clob1 = (Clob) list.get(i).get("tablePool");
					Clob clob3 = (Clob) list.get(i).get("tableRatio");
					List<Object> poolList = null;
					if (clob1 != null) {
						poolList = mapper.readValue(clob1.getSubString(1, (int) clob1.length()),ArrayList.class);
					}
					Map ratioMap = null;
					if (clob3 != null) {
						ratioMap = mapper.readValue(clob3.getSubString(1, (int) clob3.length()),Map.class);
					}
					// 如果汇总有 则进入循环
					if (poolList != null) {
						for (Object object : poolList) {
							Map pool = (Map) object;
							String f1 = pool.get("f1").toString();
							// 判断如果是汇总则进入 其他继续循环
							if (f1.equals("汇总")) {
								if(pool.get(stainType)==null||pool.get(stainType).toString().equals("")) continue;
								double d1 = Double.parseDouble(pool.get(stainType).toString().split("/")[0]);
								double d2 = Double.parseDouble(pool.get(stainType).toString().split("/")[1]);
								double d3 = -9999.0;
								if(d2 == 0.0){
									
								}else{
									d3=d1/d2*100;
									d3=CastNumUtil.decimal(d3, 1);
								}
								list.get(i).put("reduct", String.valueOf(d3));
								// 如果子措施和汇总都有对应污染物 则计算涉及减排比
								if (ratioMap != null) {
									// 如果不是PM10
									if (stainType.equals("PM10")) {
										if (ratioMap.get("PMcoarse") != null&& ratioMap.get("PM25") != null&& pool.get(stainType) != null) {
											double pMcoarse = Double.parseDouble(ratioMap.get("PMcoarse").toString());
											double pM25 = Double.parseDouble(ratioMap.get("PM25").toString());
											double dpool = Double.parseDouble(pool.get(stainType).toString().split("/")[1]);
											double dr = -9999.0;
											if(dpool == 0.0){
												
											}else{
												 dr=(pMcoarse + pM25) / dpool*100;
												 dr=CastNumUtil.decimal(dr, 1);
											}
											list.get(i).put("ratio", String.valueOf(dr));
										}
									}else if(stainType.equals("PMC")){
										if (ratioMap.get("PMcoarse") != null&& pool.get(stainType) != null) {
											double pMcoarse = Double.parseDouble(ratioMap.get("PMcoarse").toString());
											double dpool = Double.parseDouble(pool.get(stainType).toString().split("/")[1]);
											double dr = -9999.0;
											if(dpool == 0.0){
												
											}else{
												 dr=pMcoarse/ dpool*100;
												 dr=CastNumUtil.decimal(dr, 1);
											}
											list.get(i).put("ratio", String.valueOf(dr));
										}
									}else if(stainType.equals("PMFINE")){
										if (ratioMap.get("PM25") != null&&ratioMap.get("BC") != null&&ratioMap.get("OC") != null&& pool.get(stainType) != null) {
											double pM25 = Double.parseDouble(ratioMap.get("PM25").toString());
											double bc = Double.parseDouble(ratioMap.get("BC").toString());
											double oc = Double.parseDouble(ratioMap.get("OC").toString());
											double dpool = Double.parseDouble(pool.get(stainType).toString().split("/")[1]);
											double dr = -9999.0;
											if(dpool == 0.0){
												
											}else{
												double d=pM25-bc-oc;
												if(d<0){
													throw new Exception("减排结果出现负数");
												}
												dr=(pM25-bc-oc)/ dpool*100;
												dr=CastNumUtil.decimal(dr, 1);
											}
											list.get(i).put("ratio", String.valueOf(dr));
										}
									}else{ 
										if (ratioMap.get(stainType) != null&& pool.get(stainType) != null) {
											double dratio = Double.parseDouble(ratioMap.get(stainType).toString());
											double dpool = Double.parseDouble(pool.get(stainType).toString().split("/")[1]);
											double dr = -9999.0;
											if(dpool == 0.0){
												
											}else{
												dr=dratio / dpool*100;
												dr=CastNumUtil.decimal(dr, 1);
											}
											list.get(i).put("ratio", String.valueOf(dr));
										}
									}
								}
							} else {
								continue;
							}
						}
					}
				}
			}
			// 移除多余的项
			for (int j = 0; j < list.size(); j++) {
				list.get(j).remove("tablePool");
				list.get(j).remove("tableItem");
				list.get(j).remove("tableRatio");
			}
			Map resultMap = new HashMap();
			resultMap.put("total", list.size());
			resultMap.put("rows", list);
			LogUtil.getLogger().info("PlanAndMeasureController   措施汇总查询成功！");
			// 返回结果
			return AmpcResult.ok(resultMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   措施汇总查询异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 子措施条件查询
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/measure/get_measureQuery")
	public AmpcResult get_MeasureQuery(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			// 措施id
			Long measureId = Long.parseLong(data.get("measureId").toString());
			// 行业名称
			String sectorName = data.get("sectorName").toString();
			// 用户id
			Long userId = null;
			// 预案措施id
			Long planMeasureId = null;
			if (!data.get("planMeasureId").toString().equals("null")) {
				planMeasureId = Long.parseLong(data.get("planMeasureId")
						.toString());
			}
			if (data.get("userId") != null) {
				userId = Long.parseLong(data.get("userId").toString());
			}
			// 添加信息到参数中
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("planId", planId);
			map.put("sectorName", sectorName);
			map.put("userId", userId);
			// 查询条件
			List<TQueryExcel> tqeList = tQueryExcelMapper.selectByMap(map);
			// 获取所有和当前用户相关的行业描述 右下角1 
			List<Map> sdMap = tSectordocExcelMapper.selectByUserId(map);
			// 如果没有就给默认的行业描述
			if (sdMap.size() == 0) {
				map.put("userId", null);
				sdMap = tSectordocExcelMapper.selectByUserId(map);
			}
			if(sdMap.size()==0){
				map.put("sectorName", "面源");
				sdMap = tSectordocExcelMapper.selectByUserId(map);
			}
			List<MeasureUtil> mlist = new ArrayList<MeasureUtil>();
			MeasureUtil mu = null;
			// 查询措施的内容
			TMeasureExcel tme = tMeasureExcelMapper
					.selectByPrimaryKey(measureId);
			// 这个里面主要是子措施的列 右下角2
			if (tme.getMeasureExcelA() != null) {
				mu = new MeasureUtil();
				mu.setNameen("a");
				mu.setNamech(tme.getMeasureExcelAname().toString());
				mu.setValue(tme.getMeasureExcelA());
				String[] a = CheckRange(tme.getMeasureExcelArange().toString());
				mu.setMinValue(a[0]);
				mu.setMaxValue(a[1]);
				mlist.add(mu);
			}
			if (tme.getMeasureExcelA1() != null) {
				mu = new MeasureUtil();
				mu.setNameen("a1");
				mu.setNamech(tme.getMeasureExcelA1name().toString());
				mu.setValue(tme.getMeasureExcelA1());
				String[] a1 = CheckRange(tme.getMeasureExcelA1range()
						.toString());
				mu.setMinValue(a1[0]);
				mu.setMaxValue(a1[1]);
				mlist.add(mu);
			}
			// 只显示中长期的
			if (tme.getMeasureExcelType().equals("中长期措施")) {
				if (tme.getMeasureExcelIntensity() != null) {
					mu = new MeasureUtil();
					mu.setNameen("intensity");
					mu.setNamech(tme.getMeasureExcelIntensityname().toString());
					mu.setValue(tme.getMeasureExcelIntensity());
					String[] intensity = CheckRange(tme
							.getMeasureExcelIntensityrange().toString());
					mu.setMinValue(intensity[0]);
					mu.setMaxValue(intensity[1]);
					mlist.add(mu);
				}
			}
			if (tme.getMeasureExcelIntensity1() != null) {
				mu = new MeasureUtil();
				mu.setNameen("intensity1");
				mu.setNamech(tme.getMeasureExcelIntensity1name().toString());
				mu.setValue(tme.getMeasureExcelIntensity1());
				String[] intensity1 = CheckRange(tme
						.getMeasureExcelIntensity1range().toString());
				mu.setMinValue(intensity1[0]);
				mu.setMaxValue(intensity1[1]);
				mlist.add(mu);
			}
			if (tme.getMeasureExcelAsh() != null) {
				mu = new MeasureUtil();
				mu.setNameen("ash");
				mu.setNamech(tme.getMeasureExcelAshname().toString());
				mu.setValue(tme.getMeasureExcelAsh());
				String[] ash = CheckRange(tme.getMeasureExcelAshrange()
						.toString());
				mu.setMinValue(ash[0]);
				mu.setMaxValue(ash[1]);
				mlist.add(mu);
			}
			if (tme.getMeasureExcelSulfer() != null) {
				mu = new MeasureUtil();
				mu.setNameen("sulfur");
				mu.setNamech(tme.getMeasureExcelSulfername().toString());
				mu.setValue(tme.getMeasureExcelSulfer());
				String[] sulfer = CheckRange(tme.getMeasureExcelSulferrange()
						.toString());
				mu.setMinValue(sulfer[0]);
				mu.setMaxValue(sulfer[1]);
				mlist.add(mu);
			}
			// 将sv进行拆分
			if (tme.getMeasureExcelSv() != null) {
				String sv = tme.getMeasureExcelSv();
				String[] svs = sv.split(";");
				for (int i = 0; i < svs.length; i++) {
					String svss = svs[i];
					String[] svsss = svss.split("=");
					mu = new MeasureUtil();
					mu.setNamech(tme.getMeasureExcelSvname().toString()+"("+svsss[0]+")");
					mu.setNameen(svsss[0]);
					mu.setValue(svsss[1]);
					String[] svsv = CheckRange(tme.getMeasureExcelSvrange()
							.toString());
					mu.setMinValue(svsv[0]);
					mu.setMaxValue(svsv[1]);
					mlist.add(mu);
				}
			}
			// 创建结果集 并写入对应信息
			Map resultMap = new HashMap();
			resultMap.put("measureColumn", sdMap);
			resultMap.put("measureList", mlist);
			resultMap.put("query", tqeList);
			if (planMeasureId != null) {
				TPlanMeasureWithBLOBs tPlanMeasure = tPlanMeasureMapper
						.selectByPrimaryKey(planMeasureId);
				String str = tPlanMeasure.getMeasureContent();
				resultMap.put("measureContent", str);
			}
			LogUtil.getLogger().info("PlanAndMeasureController   子措施条件查询成功!");
			// 返回结果
			return AmpcResult.ok(resultMap);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   子措施条件查询异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 预案添加措施 或者修改
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/measure/addOrUpdate_measure")
	public AmpcResult addOrUpdate_measure(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 添加跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 行业名称
			String sectorName = data.get("sectorName").toString();
			// 措施id
			Long measureId = Long.parseLong(data.get("measureId").toString());
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			// 预案措施id
			Long planMeasureId = null;
			if (!data.get("planMeasureId").equals("null")) {
				planMeasureId = Long.parseLong(data.get("planMeasureId")
						.toString());
			}
			//情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			// 预案措施中的子措施Json串、
			String measureContent = data.get("measureContent").toString();
			//修改情景的状态为可编辑
			if(!(scenarinoStatusUtil.updateScenarinoStatus(scenarinoId)>0)){
				LogUtil.getLogger().error("PlanAndMeasureController   预案措施的添加或修改失败,数据库修改情景状态失败！");
				return AmpcResult.build(1000, "更改情景状态失败");
			}
			TPlanMeasureWithBLOBs tPlanMeasure = new TPlanMeasureWithBLOBs();
			tPlanMeasure.setMeasureId(measureId);
			tPlanMeasure.setPlanId(planId);
			tPlanMeasure.setSectorName(sectorName);
			tPlanMeasure.setUserId(userId);
			// 写入子措施
			tPlanMeasure.setMeasureContent(measureContent);
			// 将子措施转换成JsonObject对象进行解析
			JSONObject jsonobject = JSONObject.fromObject(data.get("measureContent").toString());
			// 设置内部值的转换类型
			Map<String, Class> cmap = new HashMap<String, Class>();
			cmap.put("filters", HashMap.class);
			cmap.put("summary", HashMap.class);
			cmap.put("table", HashMap.class);
			cmap.put("table1", HashMap.class);
			cmap.put("oopp", HashMap.class);
			cmap.put("regionIds", ArrayList.class);
			// 将json对象转换成Java对象
			MeasureContentUtil mcu = (MeasureContentUtil) JSONObject.toBean(jsonobject, MeasureContentUtil.class, cmap);
			// 获取汇总的集合
			List<Map> poolMap = mcu.getTable();
			// 获取到总的点源范围
			String f2 = poolMap.get(0).get("f2").toString();
			// 获取子措施的集合
			List<Map> itemMap = mcu.getTable1();
			// 写入点源范围
			tPlanMeasure.setImplementationScope(f2);
			// 写入汇总的Json
			tPlanMeasure.setTablePool(JSONArray.fromObject(poolMap).toString());
			// 写入子措施的Json
			tPlanMeasure.setTableItem(JSONArray.fromObject(itemMap).toString());
			if (data.get("planMeasureId").equals("null")) {
				// 预案添加措施
				int addstatus = tPlanMeasureMapper.insertSelective(tPlanMeasure);
				// 判断是否成功
				if (addstatus != 0) {
					LogUtil.getLogger().info("PlanAndMeasureController   预案添加措施成功！");
					return AmpcResult.ok("添加成功");
				} else {
					LogUtil.getLogger().error("PlanAndMeasureController   预案添加措施失败，数据库添加措施失败！");
					return AmpcResult.build(1000, "添加失败");
				}
			} else {
				// 预案修改措施
				tPlanMeasure.setPlanMeasureId(planMeasureId);
				int updatestatus = tPlanMeasureMapper.updateByPrimaryKeySelective(tPlanMeasure);
				// 判断是否成功
				if (updatestatus > 0) {
					// 添加信息到参数中
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("planId", planId);
					map.put("userId", userId);
					map.put("sectorName", sectorName);
					//查询所有的预案措施
					List<Long> plist = tPlanMeasureMapper.selectIdByMap(map);
					//因为进行删除了 则要把所有的减排结果进行刷新
					int update_status = tPlanMeasureMapper.updateRatio(plist);
					if (update_status >= 0) {
						LogUtil.getLogger().info("PlanAndMeasureController   预案修改措施成功！");
						return AmpcResult.ok("修改成功");
					}
				} 
				LogUtil.getLogger().error("PlanAndMeasureController   预案修改措施失败！");
				return AmpcResult.build(1000, "修改失败");
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   预案添加或修改措施异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 删除预案中的措施
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/measure/delete_measure")
	public AmpcResult delete_measure(
			@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 添加跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 预案措施表集合
			Long planMeasureId = Long.parseLong(data.get("planMeasureId")
					.toString());
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 预案id
			Long planId = Long.parseLong(data.get("planId").toString());
			// 行业名称
			String sectorName = null;
			// 查看行业名称是否为空，不为空添加，为空不加
			if (data.get("sectorName") != null) {
				sectorName = data.get("sectorName").toString();
			}
			//情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//修改情景的状态为可编辑
			if(!(scenarinoStatusUtil.updateScenarinoStatus(scenarinoId)>0)){
				LogUtil.getLogger().error("PlanAndMeasureController   预案删除措施失败，数据库修改情景状态失败。");
				return AmpcResult.build(1000, "更改情景状态失败");
			}
			// 删除预案中的措施
			int delete_status = tPlanMeasureMapper
					.deleteMeasures(planMeasureId);
			if (delete_status >= 0) {
				// 添加信息到参数中
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("planId", planId);
				map.put("userId", userId);
				map.put("sectorName", sectorName);
				//查询所有的预案措施
				List<Long> plist = tPlanMeasureMapper.selectIdByMap(map);
				if(plist.size()>0){
					//因为进行删除了 则要把所有的减排结果进行刷新
					int update_status = tPlanMeasureMapper.updateRatio(plist);
					if (update_status >= 0) {
						LogUtil.getLogger().info("PlanAndMeasureController   预案删除措施成功!");
						return AmpcResult.ok("删除成功");
					}
				}
				LogUtil.getLogger().info("PlanAndMeasureController   预案删除措施成功!");
				return AmpcResult.ok("删除成功");
			}
			LogUtil.getLogger().error("PlanAndMeasureController   预案删除措施失败!");
			return AmpcResult.build(1000, "删除失败");
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   预案删除措施异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 措施汇总中的减排计算
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/jp/pmjp")
	public AmpcResult pmjp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 预案id
			String planMeasureIds = data.get("planMeasureIds").toString();
			// 将得到的数据拆分 放入集合中
			String[] idss = planMeasureIds.split(",");
			List<Long> planMeasureIdss = new ArrayList<Long>();
			for (int i = 0; i < idss.length; i++) {
				planMeasureIdss.add(Long.parseLong(idss[i]));
			}
			// 创建一个减排的结果集合
			List<JPResult> jpList = new ArrayList<JPResult>();
			// 根据拆分后得到的id查询所有的预案措施对象
			tempCalc(planMeasureIdss,jpList);
			// 将java对象转换为json对象
			JSONArray json = JSONArray.fromObject(jpList);
			String str = json.toString();
			// 调用减排计算接口 并获取结果Json
			String getResult = ClientUtil.doPost(configUtil.getCsjpURL(), str);
			// 并根据减排分析得到的结果进行JsonTree的解析
			Map mapResult=mapper.readValue(getResult, Map.class);
			if(!mapResult.get("status").toString().equals("success")){
				LogUtil.getLogger().error("PlanAndMeasureController   措施汇总中的减排计算接口出现异常。");
				return AmpcResult.build(1000, "减排计算接口出现异常",-1);
			}
			JSONObject jsonObject=JSONObject.fromObject(mapResult.get("data"));
			// 讲数据转换成Map
			Map dataMap = mapper.readValue(jsonObject.toString(), Map.class);
			// 每一个对象对应一个预案措施
			for (Object obj : dataMap.keySet()) {
				Map map=(Map) dataMap.get(obj);
				for (Object obj1 : map.keySet()) {
					Map map1=(Map) map.get(obj1);
					//定义临时对象
					TPlanMeasureWithBLOBs tPlanMeasure = new TPlanMeasureWithBLOBs();
					//获取预案措施Id
					Long id = Long.parseLong(obj1.toString().split("-")[1]);
					tPlanMeasure.setPlanMeasureId(id);
					JSONObject jsonStr = JSONObject.fromObject(map1.get("reduce"));
					tPlanMeasure.setTableRatio(jsonStr.toString());
					//根据Id修改预案措施 补全减排Json列的数据
					int updatestatus = tPlanMeasureMapper.updateByPrimaryKeySelective(tPlanMeasure);
					// 判断是否成功
					if (updatestatus < 0) {
						LogUtil.getLogger().error("PlanAndMeasureController   措施汇总中的减排计算失败,数据库中添加减排结果信息时失败。");
						return AmpcResult.build(1000, "修改失败");
					}
			    }
			}
			LogUtil.getLogger().info("PlanAndMeasureController   措施汇总中的减排计算成功");
			return AmpcResult.ok(getResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   措施汇总中的减排计算异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}

	}
	
	/**
	 * 区域数据的减排计算
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/jp/areajp")
	public AmpcResult areajp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//情景Id
			long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());
			//值键对
			Object object=data.get("areaAndPlanIds");
			Map apMap=(Map)object;
			//定义条件Map
			Map mapQuery=new HashMap();
			mapQuery.put("userId", userId);
			// 创建一个减排的结果集合
			List<JPResult> jpList = new ArrayList<JPResult>();
			//循环获取到的区域ID key   预案Id value
			for(Object apStr:apMap.keySet()){
				//区域ID 
				Object ap=apMap.get(apStr);
				//获取当前区域下的所有时段上有预案的预案Id
				List aplist=mapper.readValue(ap.toString(), ArrayList.class);
				//循环预案Id集合
				for (Object apo : aplist) {
					mapQuery.put("planId", apo);
					//根据条件查询 当前预案下的所有预案措施Id
					List<Long> pmIds=tPlanMeasureMapper.selectIdByMap(mapQuery);
					//如果该预案下包含措施 则需要调用减排计算的接口
					if(pmIds!=null&&pmIds.size()>0){
						tempCalc(pmIds,jpList);
					}
				}
			}
			// 将java对象转换为json对象
			JSONArray json = JSONArray.fromObject(jpList);
			String str = json.toString();
			// 调用减排计算接口 并获取结果Json
			String AreaJPURL=configUtil.getAreaURL()+scenarinoId;
			String getResult = ClientUtil.doPost(AreaJPURL, str);
			// 并根据减排分析得到的结果进行Json的解析
			Map map=mapper.readValue(getResult, Map.class);
			//判断执行状态是否成功  如果成功就要修改情景的执行状态
			if(map.get("status").toString().equals("success")){
				TScenarinoDetail tsd=new TScenarinoDetail();
				tsd.setScenarinoId(scenarinoId);
				tsd.setRatioStartDate(new Date());
				//3为计算减排中
				tsd.setScenarinoStatus(3l);
				//修改情景执行状态
				int update=tScenarinoDetailMapper.updateByPrimaryKeySelective(tsd);
				if(update>0){
					LogUtil.getLogger().info("PlanAndMeasureController   区域的减排计算成功");
					return AmpcResult.ok(1);
				}else{
					LogUtil.getLogger().error("PlanAndMeasureController   区域的减排计算失败，数据库在修改情景的状态时失败。");
					return AmpcResult.build(1000,"修改失败");
				}
			}
			LogUtil.getLogger().error("PlanAndMeasureController   区域的减排计算接口出现异常");
			//-1代表计算接口出现异常
			return AmpcResult.build(1000,"计算接口出现异常",-1);
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   区域的减排计算异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}

	/**
	 * 区域数据的减排状态查询
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/jp/areaStatusJp")
	public AmpcResult areaStatusJp(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//情景Id
			long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());
			// 调用减排计算状态接口 并获取结果Json
			//String strResult="
			String getResult = ClientUtil.doPost(configUtil.getAreaStatusURL()+"?jobId="+scenarinoId,"");
			// 并根据减排分析得到的结果进行JsonTree的解析
			Map mapResult=mapper.readValue(getResult, Map.class);
			Map resultMap=new HashMap();
			//如果执行成功则返回前台对应信息
			if(mapResult.get("status").toString().equals("success")){
				Map map1=(Map)mapResult.get("data");
				//如果符合则在排队中
				if(map1.get("status").toString().equals("Queue")){
					resultMap.put("type", 2);
					LogUtil.getLogger().info("PlanAndMeasureController   区域的减排状态排队中！");
					return AmpcResult.build(0,"计算排队中!",resultMap);
				}
				resultMap.put("type", 0);
				//用了多少秒
				resultMap.put("time", map1.get("time"));
				//计算的百分比
				resultMap.put("percent",map1.get("percent"));
				LogUtil.getLogger().info("PlanAndMeasureController   区域的减排状态查询成功！");
				return AmpcResult.ok(resultMap);
			}else{
				//重新进行计算
				// 用户id
				Long userId = Long.parseLong(data.get("userId").toString());
				//值键对
				Object object=data.get("areaAndPlanIds");
				// 创建一个减排的结果集合
				List<JPResult> jpList = new ArrayList<JPResult>();
				if(object.toString().equals("")){
					Map map=new HashMap();
					map.put("userId",userId);
					map.put("scenarinoId",scenarinoId);
					List<Long> areaList=tScenarinoAreaMapper.selectBySid(map);
					for(Long areaId:areaList){
						map.put("areaId", areaId);
						List<Long> planList=tPlanMapper.selectByAreaId(map);
						for(Long planId:planList){
							map.put("planId", planId);
							//根据条件查询 当前预案下的所有预案措施Id
							List<Long> pmIds=tPlanMeasureMapper.selectIdByMap(map);
							//如果该预案下包含措施 则需要调用减排计算的接口
							if(pmIds!=null&&pmIds.size()>0){
								tempCalc(pmIds,jpList);
							}
						}
					}
				}else{
					Map apMap=(Map)object;
					//定义条件Map
					Map mapQuery=new HashMap();
					mapQuery.put("userId", userId);
					//循环获取到的区域ID key   预案Id value
					for(Object apStr:apMap.keySet()){
						//区域ID 
						Object ap=apMap.get(apStr);
						//获取当前区域下的所有时段上有预案的预案Id
						List aplist=mapper.readValue(ap.toString(), ArrayList.class);
						//循环预案Id集合
						for (Object apo : aplist) {
							mapQuery.put("planId", apo);
							//根据条件查询 当前预案下的所有预案措施Id
							List<Long> pmIds=tPlanMeasureMapper.selectIdByMap(mapQuery);
							//如果该预案下包含措施 则需要调用减排计算的接口
							if(pmIds!=null&&pmIds.size()>0){
								tempCalc(pmIds,jpList);
							}
						}
					}
				}
				// 将java对象转换为json对象
				JSONArray json = JSONArray.fromObject(jpList);
				String str = json.toString();
				// 调用减排计算接口 并获取结果Json
				String AreaJPURL=configUtil.getAreaURL()+scenarinoId;
				getResult = ClientUtil.doPost(AreaJPURL, str);
				// 并根据减排分析得到的结果进行JsonTree的解析
				Map map=mapper.readValue(getResult, Map.class);
				//判断执行状态是否成功  如果成功就要修改情景的执行状态
				if(map.get("status").toString().equals("success")){
					TScenarinoDetail tsd=new TScenarinoDetail();
					tsd.setScenarinoId(scenarinoId);
					tsd.setRatioStartDate(new Date());
					tsd.setScenarinoStatus(3l);
					int update=tScenarinoDetailMapper.updateByPrimaryKeySelective(tsd);
					if(update>0){
						//正在进行重新计算减排中
						resultMap.put("type", 1);
						LogUtil.getLogger().warn("PlanAndMeasureController  警告: 区域的减排状态查询失败！正在重新计算！");
						return AmpcResult.ok(resultMap);
					}else{
						LogUtil.getLogger().error("PlanAndMeasureController   区域的减排状态查询失败,数据库在修改情景状态时失败。");
						return AmpcResult.build(1000,"修改失败");
					}
				}else{
					//代表计算接口出现异常
					resultMap.put("type", -1);
					LogUtil.getLogger().error("PlanAndMeasureController   区域的减排状态查询  外部接口异常！");
					return AmpcResult.build(1000,"外部接口查询异常",resultMap);
				}
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   区域的减排状态查询异常！",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	/**
	 * 数据的减排计算
	 * @author WangShanxi
	 * @param pmIds 预案措施Id集合
	 * @param jpList 结果集合
	 * @throws UnknownHostException 
	 */
	public void tempCalc(List<Long> pmIds ,List<JPResult> jpList) throws SQLException, UnknownHostException{
		// 根据拆分后得到的id查询所有的预案措施对象
		List<Map> pmList = tPlanMeasureMapper.getPmByIds(pmIds);
		// 循环每一个预案措施对象 拼接想要的数据放入JPResult帮助类集合中
		//定义临时变量K 用于内部循环
		int k=0;
		for (int i=0;i<pmList.size();i++) {
			// 创建JPResult帮助类;
			JPResult result = new JPResult();
			// 将子措施转换成JsonObject对象进行解析
			Clob clob = (Clob) pmList.get(i).get("measureContent");
			JSONObject jsonobject = JSONObject.fromObject(clob.getSubString(1, (int) clob.length()));
			// 设置内部值的转换类型
			Map<String, Class> cmap = new HashMap<String, Class>();
			cmap.put("filters", HashMap.class);
			cmap.put("summary", HashMap.class);
			cmap.put("table", HashMap.class);
			cmap.put("table1", HashMap.class);
			cmap.put("oopp", HashMap.class);
			cmap.put("regionIds", ArrayList.class);
			// 将json对象转换成Java对象
			MeasureContentUtil mcu = (MeasureContentUtil) JSONObject.toBean(jsonobject, MeasureContentUtil.class, cmap);
			//写入IP
			InetAddress ia=null;
			ia=ia.getLocalHost();
	        String localip=ia.getHostAddress();
			result.setServerPath(localip+":8082/ampc");
			// 写入BigIndex
			result.setBigIndex(mcu.getBigIndex());
			// 写入SmallIndex
			result.setSmallIndex(mcu.getSmallIndex());
			//写入行政区划代码
			result.setRegionIds(mcu.getRegionIds());
			// 写入行业名称
			result.setGroupName(pmList.get(i).get("sectorName").toString());
			// 写入预案开始时间
			result.setStart(pmList.get(i).get("planStartTime").toString().substring(0,pmList.get(i).get("planStartTime").toString().indexOf(".")));
			// 写入预案结束时间
			result.setEnd(pmList.get(i).get("planEndTime").toString().substring(0,pmList.get(i).get("planEndTime").toString().indexOf(".")));
			// 获取Json中的Filter
			List<Map> lms = mcu.getFilters();
			// 获取Json中的子措施集合
			List<Map> table1 = mcu.getTable1();
			// 创建要减排分析中的子项
			List<Object> opList = new ArrayList<Object>();
			// 循环Filter 因为Filter中的项 和 子措施汇总的是相同的 所以循环一个
			for (int j = 0; j < table1.size(); j++) {
				Map opresult = new HashMap();
				// 获取对应Filter中的子措施
				Map t1map = table1.get(j);
				// 获取用户修改的OP
				Map opmap = (Map) t1map.get("oopp");
				opresult.put("filter", lms.get(j));
				opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
				opresult.put("opLevel", pmList.get(i).get("level"));
				opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
				opresult.put("opType", pmList.get(i).get("op"));
				//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
				boolean isTrue=true;
				for (Object obj : opmap.keySet()) {
					//判断Op中是否有值
					if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
						//更改为false  不添加循环
						isTrue=false;
						//有一条没用则不要了这条记录
						break;
					} 
					opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
				}
				//判断是否要该条记录
				if(isTrue){
					opList.add(opresult);
				}
			}
			// 获取Json中的汇总集合
			List<Map> table = mcu.getTable();
			// 循环汇总中的所有数据 包括 会总管
			for (Map map : table) {
				String f1 = map.get("f1").toString();
				//如果是汇总就继续下一次循环 因为汇总不是用户可已更改的
				if (f1.equals("汇总")) continue;
				Map opresult = new HashMap();
				// 获取用户修改的OP
				Map opmap = (Map) map.get("oopp");
				//判断如果为空就不添加
				if(opmap==null) continue;
				// 用来定义Filter
				Map mf = new HashMap();
				// 判断类型
				if (f1.equals("剩余点源")) {
					mf.put("unitType", "P");
					opresult.put("filter", mf);
					opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
					opresult.put("opLevel", pmList.get(i).get("level"));
					opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
					opresult.put("opType", pmList.get(i).get("op"));
					//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
					boolean isTrue=true;
					for (Object obj : opmap.keySet()) {
						//判断Op中是否有值
						if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
							//更改为false  不添加循环
							isTrue=false;
							//有一条没用则不要了这条记录
							break;
						} 
						opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
					}
					//判断是否要该条记录
					if(isTrue){
						opList.add(opresult);
					}
				} 
				if (f1.equals("面源")) {
					mf.put("unitType", "S");
					opresult.put("filter", mf);
					opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
					opresult.put("opLevel", pmList.get(i).get("level"));
					opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
					opresult.put("opType", pmList.get(i).get("op"));
					//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
					boolean isTrue=true;
					for (Object obj : opmap.keySet()) {
						//判断Op中是否有值
						if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
							//更改为false  不添加循环
							isTrue=false;
							//有一条没用则不要了这条记录
							break;
						} 
						opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
					}
					//判断是否要该条记录
					if(isTrue){
						opList.add(opresult);
					}
				}
			}
			//k的记录加1
			k++;
			// 循环每一个预案措施对象 拼接想要的数据放入JPResult帮助类集合中
			for (;k<pmList.size();k++) {
				if(pmList.get(k).get("sectorName").toString().equals(pmList.get(i).get("sectorName").toString())){
					// 将子措施转换成JsonObject对象进行解析
					Clob clob1 = (Clob) pmList.get(k).get("measureContent");
					JSONObject jsonobject1 = JSONObject.fromObject(clob1.getSubString(1, (int) clob1.length()));
					// 将json对象转换成Java对象
					MeasureContentUtil mcu1 = (MeasureContentUtil) JSONObject.toBean(jsonobject1, MeasureContentUtil.class, cmap);
					// 获取Json中的Filter
					List<Map> lms1 = mcu1.getFilters();
					// 获取Json中的子措施集合
					List<Map> table11 = mcu1.getTable1();
					// 循环Filter 因为Filter中的项 和 子措施汇总的是相同的 所以循环一个
					for (int j = 0; j < table11.size(); j++) {
						Map opresult = new HashMap();
						// 获取对应Filter中的子措施
						Map t1map = table11.get(j);
						// 获取用户修改的OP
						Map opmap = (Map) t1map.get("oopp");
						opresult.put("filter", lms1.get(j));
						opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
						opresult.put("opLevel", pmList.get(k).get("level"));
						opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
						opresult.put("opType", pmList.get(k).get("op"));
						//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
						boolean isTrue=true;
						for (Object obj : opmap.keySet()) {
							//判断Op中是否有值
							if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
								//更改为false  不添加循环
								isTrue=false;
								//有一条没用则不要了这条记录
								break;
							} 
							opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
						}
						//判断是否要该条记录
						if(isTrue){
							opList.add(opresult);
						}
					}
					// 获取Json中的汇总集合
					List<Map> table22 = mcu1.getTable();
					// 判断汇总中是否有对点源和面源的计算
					// 循环汇总中的所有数据 包括 会总管
					for (Map map : table22) {
						String f1 = map.get("f1").toString();
						if (f1.equals("汇总")) continue;
						Map opresult = new HashMap();
						// 获取用户修改的OP
						Map opmap = (Map) map.get("oopp");
						//判断如果为空就不添加
						if(opmap==null) continue;
						// 用来定义Filter
						Map mf = new HashMap();
						// 判断类型
						if (f1.equals("剩余点源")) {
							mf.put("unitType", "P");
							opresult.put("filter", mf);
							opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
							opresult.put("opLevel", pmList.get(k).get("level"));
							opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
							opresult.put("opType", pmList.get(k).get("op"));
							//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
							boolean isTrue=true;
							for (Object obj : opmap.keySet()) {
								//判断Op中是否有值
								if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
									//更改为false  不添加循环
									isTrue=false;
									//有一条没用则不要了这条记录
									break;
								} 
								opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
							}
							//判断是否要该条记录
							if(isTrue){
								opList.add(opresult);
							}
						} 
						if (f1.equals("面源")) {
							mf.put("unitType", "S");
							opresult.put("filter", mf);
							opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
							opresult.put("opLevel", pmList.get(k).get("level"));
							opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
							opresult.put("opType", pmList.get(k).get("op"));
							//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
							boolean isTrue=true;
							for (Object obj : opmap.keySet()) {
								//判断Op中是否有值
								if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
									//更改为false  不添加循环
									isTrue=false;
									//有一条没用则不要了这条记录
									break;
								} 
								opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
							}
							//判断是否要该条记录
							if(isTrue){
								opList.add(opresult);
							}
						}
					}
				}else{
					//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
					i=k-1;
					break;
				}
				//判断是否还有数据了
				if((k+1)==pmList.size()){
					i=k;
				}
			}
			// 放入OPS
			result.setOps(opList);
			jpList.add(result);
		}
	}
	
	/**
	 * 取值范围验证方法
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	public static String[] CheckRange(String str) {
		String[] temp = str.split("~");
		if (temp[0].equals("inf")) {
			temp[0] = "null";
		}
		if (temp[1].equals("inf")) {
			temp[1] = "null";
		}
		return temp;
	}
	
	
	/**
	 * 减排对外接口
	 * @param scenarinoId   情景Id
	 * @param userId		用户ID
	 * @param startDate		开始时间
	 * @param endDate		结束时间
	 * @return	减排的Json串
	 */
	public String JPUtil(Long scenarinoId,Long userId,String startDate,String endDate){
		// 添加异常捕捉
		try {
			// 创建一个减排的结果集合
			List<JPResult> jpList = new ArrayList<JPResult>();
			Map mapQusery=new HashMap();
			mapQusery.put("userId",userId);
			mapQusery.put("scenarinoId",scenarinoId);
			List<Long> areaList=tScenarinoAreaMapper.selectBySid(mapQusery);
			for(Long areaId:areaList){
				mapQusery.put("areaId", areaId);
				List<Long> planList=tPlanMapper.selectByAreaId(mapQusery);
				for(Long planId:planList){
					mapQusery.put("planId", planId);
					//根据条件查询 当前预案下的所有预案措施Id
					List<Long> pmIds=tPlanMeasureMapper.selectIdByMap(mapQusery);
					//如果该预案下包含措施 则需要调用减排计算的接口
					if(pmIds!=null&&pmIds.size()>0){
						// 根据拆分后得到的id查询所有的预案措施对象
						List<Map> pmList = tPlanMeasureMapper.getPmByIds(pmIds);
						// 循环每一个预案措施对象 拼接想要的数据放入JPResult帮助类集合中
						//定义临时变量K 用于内部循环
						int k=0;
						for (int i=0;i<pmList.size();i++) {
							// 创建JPResult帮助类;
							JPResult result = new JPResult();
							// 将子措施转换成JsonObject对象进行解析
							Clob clob = (Clob) pmList.get(i).get("measureContent");
							JSONObject jsonobject = JSONObject.fromObject(clob.getSubString(1, (int) clob.length()));
							// 设置内部值的转换类型
							Map<String, Class> cmap = new HashMap<String, Class>();
							cmap.put("filters", HashMap.class);
							cmap.put("summary", HashMap.class);
							cmap.put("table", HashMap.class);
							cmap.put("table1", HashMap.class);
							cmap.put("oopp", HashMap.class);
							cmap.put("regionIds", ArrayList.class);
							// 将json对象转换成Java对象
							MeasureContentUtil mcu = (MeasureContentUtil) JSONObject.toBean(jsonobject, MeasureContentUtil.class, cmap);
							//写入IP
							InetAddress ia=null;
							ia=ia.getLocalHost();
					        String localip=ia.getHostAddress();
							result.setServerPath(localip+":8082/ampc");
							// 写入BigIndex
							result.setBigIndex(mcu.getBigIndex());
							// 写入SmallIndex
							result.setSmallIndex(mcu.getSmallIndex());
							//写入行政区划代码
							result.setRegionIds(mcu.getRegionIds());
							// 写入行业名称
							result.setGroupName(pmList.get(i).get("sectorName").toString());
							// 写入预案开始时间
							result.setStart(startDate);
							// 写入预案结束时间
							result.setEnd(endDate);
							// 获取Json中的Filter
							List<Map> lms = mcu.getFilters();
							// 获取Json中的子措施集合
							List<Map> table1 = mcu.getTable1();
							// 创建要减排分析中的子项
							List<Object> opList = new ArrayList<Object>();
							// 循环Filter 因为Filter中的项 和 子措施汇总的是相同的 所以循环一个
							for (int j = 0; j < table1.size(); j++) {
								Map opresult = new HashMap();
								// 获取对应Filter中的子措施
								Map t1map = table1.get(j);
								// 获取用户修改的OP
								Map opmap = (Map) t1map.get("oopp");
								opresult.put("filter", lms.get(j));
								opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
								opresult.put("opLevel", pmList.get(i).get("level"));
								opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
								opresult.put("opType", pmList.get(i).get("op"));
								//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
								boolean isTrue=true;
								for (Object obj : opmap.keySet()) {
									//判断Op中是否有值
									if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
										//更改为false  不添加循环
										isTrue=false;
										//有一条没用则不要了这条记录
										break;
									} 
									opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
								}
								//判断是否要该条记录
								if(isTrue){
									opList.add(opresult);
								}
							}
							// 获取Json中的汇总集合
							List<Map> table = mcu.getTable();
							// 循环汇总中的所有数据 包括 会总管
							for (Map map : table) {
								String f1 = map.get("f1").toString();
								//如果是汇总就继续下一次循环 因为汇总不是用户可已更改的
								if (f1.equals("汇总")) continue;
								Map opresult = new HashMap();
								// 获取用户修改的OP
								Map opmap = (Map) map.get("oopp");
								//判断如果为空就不添加
								if(opmap==null) continue;
								// 用来定义Filter
								Map mf = new HashMap();
								// 判断类型
								if (f1.equals("剩余点源")) {
									mf.put("unitType", "P");
									opresult.put("filter", mf);
									opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
									opresult.put("opLevel", pmList.get(i).get("level"));
									opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
									opresult.put("opType", pmList.get(i).get("op"));
									//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
									boolean isTrue=true;
									for (Object obj : opmap.keySet()) {
										//判断Op中是否有值
										if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
											//更改为false  不添加循环
											isTrue=false;
											//有一条没用则不要了这条记录
											break;
										} 
										opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
									}
									//判断是否要该条记录
									if(isTrue){
										opList.add(opresult);
									}
								} 
								if (f1.equals("面源")) {
									mf.put("unitType", "S");
									opresult.put("filter", mf);
									opresult.put("l4sFilter", pmList.get(i).get("l4sFilter"));
									opresult.put("opLevel", pmList.get(i).get("level"));
									opresult.put("opName", pmList.get(i).get("measureName")+"-"+pmList.get(i).get("planMeasureId"));
									opresult.put("opType", pmList.get(i).get("op"));
									//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
									boolean isTrue=true;
									for (Object obj : opmap.keySet()) {
										//判断Op中是否有值
										if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
											//更改为false  不添加循环
											isTrue=false;
											//有一条没用则不要了这条记录
											break;
										} 
										opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
									}
									//判断是否要该条记录
									if(isTrue){
										opList.add(opresult);
									}
								}
							}
							//k的记录加1
							k++;
							// 循环每一个预案措施对象 拼接想要的数据放入JPResult帮助类集合中
							for (;k<pmList.size();k++) {
								if(pmList.get(k).get("sectorName").toString().equals(pmList.get(i).get("sectorName").toString())){
									// 将子措施转换成JsonObject对象进行解析
									Clob clob1 = (Clob) pmList.get(k).get("measureContent");
									JSONObject jsonobject1 = JSONObject.fromObject(clob1.getSubString(1, (int) clob1.length()));
									// 将json对象转换成Java对象
									MeasureContentUtil mcu1 = (MeasureContentUtil) JSONObject.toBean(jsonobject1, MeasureContentUtil.class, cmap);
									// 获取Json中的Filter
									List<Map> lms1 = mcu1.getFilters();
									// 获取Json中的子措施集合
									List<Map> table11 = mcu1.getTable1();
									// 循环Filter 因为Filter中的项 和 子措施汇总的是相同的 所以循环一个
									for (int j = 0; j < table11.size(); j++) {
										Map opresult = new HashMap();
										// 获取对应Filter中的子措施
										Map t1map = table11.get(j);
										// 获取用户修改的OP
										Map opmap = (Map) t1map.get("oopp");
										opresult.put("filter", lms1.get(j));
										opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
										opresult.put("opLevel", pmList.get(k).get("level"));
										opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
										opresult.put("opType", pmList.get(k).get("op"));
										//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
										boolean isTrue=true;
										for (Object obj : opmap.keySet()) {
											//判断Op中是否有值
											if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
												//更改为false  不添加循环
												isTrue=false;
												//有一条没用则不要了这条记录
												break;
											} 
											opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
										}
										//判断是否要该条记录
										if(isTrue){
											opList.add(opresult);
										}
									}
									// 获取Json中的汇总集合
									List<Map> table22 = mcu1.getTable();
									// 判断汇总中是否有对点源和面源的计算
									// 循环汇总中的所有数据 包括 会总管
									for (Map map : table22) {
										String f1 = map.get("f1").toString();
										if (f1.equals("汇总")) continue;
										Map opresult = new HashMap();
										// 获取用户修改的OP
										Map opmap = (Map) map.get("oopp");
										//判断如果为空就不添加
										if(opmap==null) continue;
										// 用来定义Filter
										Map mf = new HashMap();
										// 判断类型
										if (f1.equals("剩余点源")) {
											mf.put("unitType", "P");
											opresult.put("filter", mf);
											opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
											opresult.put("opLevel", pmList.get(k).get("level"));
											opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
											opresult.put("opType", pmList.get(k).get("op"));
											//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
											boolean isTrue=true;
											for (Object obj : opmap.keySet()) {
												//判断Op中是否有值
												if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
													//更改为false  不添加循环
													isTrue=false;
													//有一条没用则不要了这条记录
													break;
												} 
												opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
											}
											//判断是否要该条记录
											if(isTrue){
												opList.add(opresult);
											}
										} 
										if (f1.equals("面源")) {
											mf.put("unitType", "S");
											opresult.put("filter", mf);
											opresult.put("l4sFilter", pmList.get(k).get("l4sFilter"));
											opresult.put("opLevel", pmList.get(k).get("level"));
											opresult.put("opName", pmList.get(k).get("measureName")+"-"+pmList.get(k).get("planMeasureId"));
											opresult.put("opType", pmList.get(k).get("op"));
											//定义临时变量 用来判断op中的项是否有值 如果没有就为false 则不需要添加该条记录
											boolean isTrue=true;
											for (Object obj : opmap.keySet()) {
												//判断Op中是否有值
												if(opmap.get(obj)==null||opmap.get(obj).toString().equals("")){
													//更改为false  不添加循环
													isTrue=false;
													//有一条没用则不要了这条记录
													break;
												} 
												opresult.put(obj,Double.parseDouble(opmap.get(obj).toString()));
											}
											//判断是否要该条记录
											if(isTrue){
												opList.add(opresult);
											}
										}
									}
								}else{
									//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
									i=k-1;
									break;
								}
								//判断是否还有数据了
								if((k+1)==pmList.size()){
									i=k;
								}
							}
							// 放入OPS
							result.setOps(opList);
							jpList.add(result);
						}
					}
				}
			}
			// 将java对象转换为json对象
			JSONArray json = JSONArray.fromObject(jpList);
			String str = json.toString();
			LogUtil.getLogger().info("PlanAndMeasureController   提供对外的计算接口成功！");
			return str;
		} catch (Exception e) {
			LogUtil.getLogger().error("PlanAndMeasureController   提供对外的计算接口异常！",e);
			return null;
		}
	}

}
