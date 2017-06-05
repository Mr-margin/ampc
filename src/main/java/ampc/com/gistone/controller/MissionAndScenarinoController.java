package ampc.com.gistone.controller;

import java.sql.SQLException;
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

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TDomainMissionMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.inter.TUserMapper;
import ampc.com.gistone.database.inter.TUserSettingMapper;
import ampc.com.gistone.database.model.TDomainMission;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.database.model.TUser;
import ampc.com.gistone.database.model.TUserSetting;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;
import ampc.com.gistone.util.ScenarinoStatusUtil;

/**
 * 任务和情景一级页面控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月22日
 */
@RestController
@RequestMapping
public class MissionAndScenarinoController {
    //默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	//任务映射
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	//情景映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	//区域映射
	@Autowired
	private TScenarinoAreaMapper tScenarinoAreaMapper;
	//时段映射
	@Autowired
	private TTimeMapper tTimeMapper;
	
	@Autowired
	private TUserMapper tUserMapper;

	@Autowired
	private AreaAndTimeController atc=new AreaAndTimeController();
	
	@Autowired
	private TPlanMapper tPlanMapper;
	
	@Autowired
	private TPlanMeasureMapper tPlanMeasureMapper;
	
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	@Autowired
	private ScenarinoStatusUtil scenarinoStatusUtil=new ScenarinoStatusUtil();
	
	@Autowired
	private TUserSettingMapper tUserSettingMapper;
	
	@Autowired
	private TDomainMissionMapper tDomainMissionMapper;
	/**
	 * 任务查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("mission/get_mission_list")
	public AmpcResult get_Mission_list(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			
			//条件名称
			String queryName=data.get("queryName").toString();
			//任务状态
			String missionStatus=data.get("missionStatus").toString();
			//当前页码
			Integer pageNum=Integer.valueOf(data.get("pageNum").toString());
			//每页展示的条数
			Integer pageSize=Integer.valueOf(data.get("pageSize").toString());
			//列表排序  暂时内定按照任务ID逆序排序
			//String sort=data.get("sort").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//添加分页信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("startNum", ((pageNum-1)*pageSize));
			map.put("endNum", (pageNum*pageSize));
			map.put("userId", userId);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			//返回的页码
			mapResult.put("page", pageNum);
			//判断是否有条件名称,如果有则调用根据条件名模糊查询
			if(null!=queryName&&!queryName.equals("")){
				String name="%"+queryName+"%";
				map.put("queryName",name);
			}else{
				map.put("queryName",null);
			}
			//判断任务状态
			if(null!=missionStatus&&!missionStatus.equals("")){
				map.put("missionStatus",missionStatus);
			}else{
				map.put("missionStatus",null);
			}
			//查询全部写入返回结果集
			List<Map> list = this.tMissionDetailMapper.selectAllOrByQueryName(map);
			for(Map ss:list){
				Long missionDomainId=Long.valueOf(ss.get("missionDomainId").toString());
				Long missionId=Long.valueOf(ss.get("missionId").toString());
				TMissionDetail tmd=new TMissionDetail();
				tmd.setIsEffective("1");
				tmd.setMissionId(missionId);
				List<TScenarinoDetail> tmlist=tScenarinoDetailMapper.selectAllByMissionId(missionId);
				int s=0;
				for(TScenarinoDetail tms:tmlist){
					if(tms.getScenarinoStatus()==6){
						s++;
					}
				}
				ss.put("scnum", tmlist.size());
				ss.put("zxscnum", s);
				TDomainMission tDomainMission=tDomainMissionMapper.selectByPrimaryKey(missionDomainId);
				ss.put("domainName", tDomainMission.getDomainName());
			}
			mapResult.put("total", this.tMissionDetailMapper.selectCountOrByQueryName(map));
			mapResult.put("rows",list);
			//返回结果
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 任务查询方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "系统异常",null);
		}
	}
	
	/**
	 * 任务创建方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("mission/save_mission")
	public AmpcResult save_Mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			TMissionDetail mission=new TMissionDetail();
			//任务名称
			mission.setMissionName(data.get("missionName").toString());
			if(!RegUtil.CheckParameter(data.get("missionName").toString(), "String", null, false)){
				LogUtil.getLogger().error("save_mission  任务名称为空!");
				return AmpcResult.build(1003, "任务名称为空!");
			}
			//模拟范围id
			mission.setMissionDomainId(Long.parseLong(data.get("missionDomainId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("missionDomainId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("save_mission  模拟范围id为空!");
				return AmpcResult.build(1003, "模拟范围id为空!");
			}
			//全国清单id
			mission.setEsCouplingId(Long.parseLong(data.get("esCouplingId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("esCouplingId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("save_mission  全国清单id为空!");
				return AmpcResult.build(1003, "全国清单id为空!");
			}
			//任务的开始时间
			String startDate=data.get("missionStartDate").toString();
			if(!RegUtil.CheckParameter(startDate, "String", null, false)){
				LogUtil.getLogger().error("save_mission  任务的开始时间为空!");
				return AmpcResult.build(1003, "任务的开始时间为空!");
			}
			String endDate=data.get("missionEndDate").toString();
			if(!RegUtil.CheckParameter(endDate, "String", null, false)){
				LogUtil.getLogger().error("save_mission  任务的结束时间为空!");
				return AmpcResult.build(1003, "任务的结束时间为空!");
			}
			Date adddate=new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date missionStartDate=sdf.parse(startDate);
			Date missionEndDate=sdf.parse(endDate);
			String adddates=sdf.format(adddate);
			Date add_date=sdf.parse(adddates);
			mission.setMissionAddTime(add_date);
			mission.setMissionStartDate(missionStartDate);
			//任务的结束时间
			mission.setMissionEndDate(missionEndDate);
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			//默认新建任务会赋值预评估
			mission.setMissionStatus(data.get("missionStauts").toString());
		
			//执行添加操作
			int result=this.tMissionDetailMapper.insertSelective(mission);
			//判断添加结果
			if(result>0){
				//判断添加类型
//				if(createType==2){
//					/**
//					 * TODO 更改基准情景的执行状态
//					 */
//					
//					
//					
//					return AmpcResult.ok(result);
//				}else{
					//只创建任务
					return AmpcResult.ok(result);
				//}
			}
			//添加失败
			LogUtil.getLogger().info("save_mission 任务创建成功");
			return AmpcResult.build(1000, "保存数据失败",null);
		} catch (Exception e) {
			LogUtil.getLogger().error("save_mission 任务创建方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "系统异常",null);
		}
	}
	
	/**
	 * 任务修改方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("mission/update_mission")
	public AmpcResult update_Mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//建立实体类
			TMissionDetail mission=new TMissionDetail();
			//任务名称
			mission.setMissionName(data.get("missionName").toString());
			if(!RegUtil.CheckParameter(data.get("missionName").toString(), "String", null, false)){
				LogUtil.getLogger().error("update_mission  任务名称为空!");
				return AmpcResult.build(1003, "任务名称为空!");
			}
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("userId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("update_mission  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//任务ID 
			mission.setMissionId(Long.parseLong(data.get("missionId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("missionId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("update_mission  任务ID为空!");
				return AmpcResult.build(1003, "任务ID为空!");
			}
			//执行修改操作
			int result=this.tMissionDetailMapper.updateByPrimaryKeySelective(mission);
			//判断执行结果返回对应数据
			return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 任务修改方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "系统异常",null);
		}
	}
	
	/**
	 * 任务删除方法 实际为修改数据的状态
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("mission/delete_mission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult delete_Mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response)throws Exception{
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要删除的任务集合
			String missionIds=data.get("missionIds").toString();
			if(!RegUtil.CheckParameter(missionIds, "String", null, false)){
				LogUtil.getLogger().error("delete_mission  要删除的任务集合为空!");
				return AmpcResult.build(1003, "要删除的任务集合为空!");
			}
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			if(!RegUtil.CheckParameter(userId, "Integer", null, false)){
				LogUtil.getLogger().error("delete_mission  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//将得到的数据拆分 放入集合中
			String[] idss=missionIds.split(",");
			List<Integer> list=new ArrayList<Integer>();
			for(int i=0;i<idss.length;i++){
				list.add(Integer.valueOf(idss[i]));
			}
			/**
			 * TODO 用事务更能保证一致性
			 */
			//统计情景的所有ID
			List<Long> scenarinoIdss=new ArrayList<Long>();
			
			//统计区域的所有ID
			List<Long> areaIdss=new ArrayList<Long>();
			//统计时段的所有ID
			List<Long> timeIdss=new ArrayList<Long>();
			//记录情景的所有ID
			for (Integer missionId : list) {
				Map map=new HashMap();
				map.put("userId", userId);
				map.put("missionId", missionId);
				List<Long> scenarinoIds=this.tScenarinoDetailMapper.selectByMissionId(map);
				for (Long scenarinoId : scenarinoIds) {
					scenarinoIdss.add(scenarinoId);
				}
			}
			//记录区域的所有ID
			for (Long scenarinoId: scenarinoIdss) {
				Map map1=new HashMap();
				map1.put("userId", userId);
				map1.put("scenarinoId", scenarinoId);
				List<Long> areaIds=this.tScenarinoAreaMapper.selectBySid(map1);
				for (Long areaId : areaIds) {
					areaIdss.add(areaId);
				}
			}
			//记录时段的所有ID
			for (Long areaId: areaIdss) {
				List<TTime> timeIds=this.tTimeMapper.selectEntityByAreaId(areaId);
				for (TTime timeId : timeIds) {
					timeIdss.add(timeId.getTimeId());
				}
			}
			//执行时段级联删除方法
//			for(Long timeId:timeIdss){
//				int a=tTimeMapper.updateByisEffective(timeId);
//				TTime times=this.tTimeMapper.selectByPrimaryKey(152l);
//				if(a!=0){
//					if(times.getPlanId()!=null && times.getPlanId()!=-1){
//						TPlan tPlan=tPlanMapper.selectByPrimaryKey(times.getPlanId());
//						if(tPlan.getCopyPlan().equals("0")){
//							//修改预案为无效状态
//							tPlan.setIsEffective("0");
//							int up_status=tPlanMapper.updateByPrimaryKeySelective(tPlan);
//							if(up_status!=0){
//								//删除预案的措施
//								int del_status=tPlanMeasureMapper.deleteByPlanId(tPlan.getPlanId());						
//							}
//							
//						}
//						}
//				}
//			}
			Map res=atc.delete_times(timeIdss);
			String str=res.get("result").toString();
			//判断时段级联方法是否执行成功  -1 不成功  1成功
			if(str.equals("-1")){
				return AmpcResult.build(1000, res.get("msg").toString(),null);
			}
			int result;
			if(areaIdss.size()>0){
				//执行区域删除方法
				result=this.tScenarinoAreaMapper.updateIsEffeByIds(areaIdss);
				//判断是否执行成功
				if(result>0){
					if(scenarinoIdss.size()>0){
						//执行情景删除方法
						result=this.tScenarinoDetailMapper.updateIsEffeByIds(scenarinoIdss);
						if(result>0){
							//执行任务删除方法
							result=this.tMissionDetailMapper.updateIsEffeByIds(list);
							//判断执行结果返回对应数据
							return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
						}else{
							return AmpcResult.build(1000, "情景修改失败",null);
						}
					}else{
						//执行任务删除方法
						result=this.tMissionDetailMapper.updateIsEffeByIds(list);
						//判断执行结果返回对应数据
						return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
					}
				}else{
					return AmpcResult.build(1000, "区域修改失败",null);
				}
			}else{
				if(scenarinoIdss.size()>0){
					//执行情景删除方法
					result=this.tScenarinoDetailMapper.updateIsEffeByIds(scenarinoIdss);
					if(result>0){
						//执行任务删除方法
						result=this.tMissionDetailMapper.updateIsEffeByIds(list);
						//判断执行结果返回对应数据
						return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
					}else{
						return AmpcResult.build(1000, "情景修改失败",null);
					}
				}else{
					//执行任务删除方法
					result=this.tMissionDetailMapper.updateIsEffeByIds(list);
					//判断执行结果返回对应数据
					return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
				}
				
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 任务删除方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "任务删除异常",null);
		}
	}
	
	
	
	/**
	 * 添加任务对名称重复判断
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("mission/check_missioname")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult check_MissioName(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response)throws Exception{
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要添加的任务名称
			String missionName=data.get("missionName").toString();
			if(!RegUtil.CheckParameter(missionName, "String", null, false)){
				LogUtil.getLogger().error("check_missioname  要添加的任务名称为空!");
				return AmpcResult.build(1003, "要添加的任务名称为空!");
			}
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			if(!RegUtil.CheckParameter(userId, "Integer", null, false)){
				LogUtil.getLogger().error("check_missioname  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionName", missionName);
			map.put("userId", userId);
			//执行判断操作
			int result=this.tMissionDetailMapper.check_MissioName(map);
			//返回true 表示可用  返回false 已存在
			return result==0?AmpcResult.ok(true):AmpcResult.build(1003, "名称已存在",false);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 添加任务对名称重复判断异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "任务对名称重复判断错误",null);
		}
	}
	
	
	/**
	 * 情景列表查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("scenarino/get_scenarinoListBymissionId")
	public AmpcResult get_ScenarinoListBymissionId(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//任务Id
			Long missionId=Long.parseLong(data.get("missionId").toString());
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("get_scenarinoListBymissionId  任务Id为空!");
				return AmpcResult.build(1003, "任务Id为空!");
			}
			//条件名称
			String queryName=data.get("queryName").toString();
			
			//列表排序  暂时内定按照任务ID逆序排序
			//String sort=data.get("sort").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			if(!RegUtil.CheckParameter(userId, "Integer", null, false)){
				LogUtil.getLogger().error("get_scenarinoListBymissionId  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionId",missionId);
			map.put("userId", userId);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			//判断是否有条件名称,如果有则调用根据条件名模糊查询
			if(queryName!=null&&!queryName.equals("")){
				String name="%"+queryName+"%";
				map.put("queryName", name);
			}else{
				map.put("queryName", null);
			}
			//查询全部并写入结果集
			List<Map> list = this.tScenarinoDetailMapper.selectByMissionIdAndQueryName(map);
			mapResult.put("rows",list);
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 情景列表查询方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "情景列表查询方法异常",null);
		}
	}
	
	
	/**
	 * 情景复制查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 * 
	 */
	@RequestMapping("scenarino/get_CopyScenarinoList")
	public AmpcResult get_CopyScenarinoList(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//条件名称
			String queryName=data.get("queryName").toString();
			//任务状态
			String missionStatus=data.get("missionStatus").toString();
			
			//当前页码
			Integer pageNum=Integer.valueOf(data.get("pageNum").toString());
			//每页展示的条数
			Integer pageSize=Integer.valueOf(data.get("pageSize").toString());
			String scenarinoId=data.get("scenarinoId").toString();
			//列表排序  暂时内定按照任务ID逆序排序
			//String sort=data.get("sort").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//添加分页信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("startNum", ((pageNum-1)*pageSize));
			map.put("endNum", (pageNum*pageSize));
			map.put("userId", userId);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			//返回的页码
			mapResult.put("page", pageNum);
			//判断是否有条件名称,如果有则调用根据条件名模糊查询
			if(null!=queryName&&!queryName.equals("")){
				String name="%"+queryName+"%";
				map.put("queryName",name);
			}else{
				map.put("queryName",null);
			}
			//判断任务状态 预评估还是后评估
			if(null!=missionStatus&&!missionStatus.equals("")){
				map.put("missionStatus",missionStatus);
			}else{
				map.put("missionStatus",null);
			}
			//查询全部 写入结果集 返回
			List<Map> list = this.tScenarinoDetailMapper.selectAllOrByQueryName2(map);
			List<Map> newlist=new ArrayList<Map>();
			int sss=0;
			for(Map s:list){
				String id=s.get("scenarinoId").toString();
				if(!scenarinoId.equals(id)){
					newlist.add(s);		
					sss++;
				}
			}
			mapResult.put("total", sss);
			mapResult.put("rows",newlist);
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("get_CopyScenarinoList 情景复制查询方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "情景复制查询方法异常");
		}
	}
	
	
	
	/**
	 * 情景创建方法(作废)
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("scenarino/save_scenarino2")
	public AmpcResult save_Scenarino2(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			TScenarinoDetail scenarino=new TScenarinoDetail();
			//情景名称
			scenarino.setScenarinoName(data.get("scenarinoName").toString());
			//情景的开始时间
			scenarino.setScenarinoStartDate(new Date(data.get("scenarinoStartDate").toString()));
			//情景的结束时间
			scenarino.setScenarinoEndDate(new Date(data.get("scenarinoEndDate").toString()));
			//任务id  
			scenarino.setMissionId(Long.parseLong(data.get("missionId").toString()));
			//用户的id  确定当前用户
			scenarino.setUserId(Long.parseLong(data.get("userId").toString()));
			//创建类型 1.只创建情景 2.创建并编辑情景 
			Integer createType=Integer.valueOf(data.get("createType").toString());
			//情景的id  用来判断是否是复用情景
			Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());
			//判断是否是复用情景 和创建类型
			if(null!=scenarinoId&&!scenarinoId.equals("")&&createType==1){
				/**
				 * TODO 复制情景
				 */
				//情景开始时间
				Date startDate=new Date(data.get("scenarinoStartDate").toString());
				//情景结束时间
				Date endDate=new Date(data.get("scenarinoEndDate").toString());
				long start=startDate.getTime();
				long end=endDate.getTime();
				long differ=end-start;
				//获取到要复制的情景对象
				TScenarinoDetail scenarinoOld=this.tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
				//要获取复制情景下的所有区域
				Map map=new HashMap();
				map.put("userId", scenarino.getUserId());
				map.put("scenarinoId", scenarinoId);
				List<TScenarinoAreaWithBLOBs> areas=this.tScenarinoAreaMapper.selectBySids(map);
				//循环所有的区域对象
				for (TScenarinoAreaWithBLOBs tScenarinoAreaWithBLOBs : areas) {
					//根据区域ID 获取当前区域的所有时段
					List<TTime> timeList=this.tTimeMapper.selectAllByAreaId(tScenarinoAreaWithBLOBs.getScenarinoAreaId());
					if(timeList.size()>0&&null!=timeList){
						//循环所有的时段对象
						for (TTime tTime : timeList) {
							long d=tTime.getTimeEndDate().getTime()-tTime.getTimeStartDate().getTime();
							
							
						}
					}else{
						/**
						 * 当前区域没有时段
						 */
					}
				}
				
			}
			if(null!=scenarinoId&&!scenarinoId.equals("")&&createType==2){
				/**
				 * TODO 复制情景 并返回新建的情景ID
				 */
			}
			//执行添加操作
			int result=this.tScenarinoDetailMapper.insertSelective(scenarino);
			Map map=new HashMap();
			map.put("missionId", scenarino.getMissionId());
			map.put("scrnarinoName", scenarino.getScenarinoName());
			long sid=this.tScenarinoDetailMapper.selectByMidAndSName(map);
			if(result>0){
				for(int i=1;i<=3;i++){
					TScenarinoAreaWithBLOBs area=new TScenarinoAreaWithBLOBs();
					//区域名称
					area.setAreaName(i+"类区");
					//用户的id  确定当前用户
					area.setUserId(Long.parseLong(data.get("userId").toString()));
					//情景ID
					area.setScenarinoDetailId(sid);
					//执行添加操作
					int resu=this.tScenarinoAreaMapper.insertSelective(area);
					if(resu>0){
						Map<String,Object> map1=new HashMap<String,Object>();
						map1.put("scenarinoId",area.getScenarinoDetailId());
						map1.put("areaName",area.getAreaName());
						map1.put("userId", area.getUserId());
						long areId=this.tScenarinoAreaMapper.selectAreaIdByParam(map1);
						//情景开始时间
						String start_Date=data.get("scenarinoStartDate").toString();
						//情景结束时间
						String endDate=data.get("scenarinoEndDate").toString();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date timeEndDate = sdf.parse(endDate);
						Date startDate=sdf.parse(start_Date);
						//新建时段
						TTime add_tTime = new TTime();
						add_tTime.setAreaId(areId);
						add_tTime.setUserId(area.getUserId());
						add_tTime.setMissionId(Long.parseLong(data.get("missionId").toString()));
						add_tTime.setScenarinoId(area.getScenarinoDetailId());
						add_tTime.setTimeStartDate(startDate);
						add_tTime.setTimeEndDate(timeEndDate);
					    resu = tTimeMapper.insertSelective(add_tTime);
					    if(resu<1){
					    	//添加失败
							return AmpcResult.build(1000, "添加时段失败",null);
					    }
					}else{
						//添加失败
						return AmpcResult.build(1000, "添加区域失败",null);
					}
				}
			}else{
				//返回结果
				return AmpcResult.build(1000, "添加情景失败",null);
			}
			//判断非复用情景
			if(null==scenarinoId&&scenarinoId.equals("")&&createType==1&&result>0){
				return AmpcResult.ok(result);
			}else if(null==scenarinoId&&scenarinoId.equals("")&&createType==2&&result>0){
				//直接返回新建的情景ID
				return AmpcResult.ok(sid);
			}else{
				return AmpcResult.build(1000, "参数错误",null);
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController",e);
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	/**
	 * 情景修改方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("scenarino/updat_scenarino")
	public AmpcResult updat_Scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//创建情景实体类
			TScenarinoDetail scenarino=new TScenarinoDetail();
			//情景名称
			scenarino.setScenarinoName(data.get("scenarinoName").toString());
			if(!RegUtil.CheckParameter(data.get("scenarinoName").toString(), "String", null, false)){
				LogUtil.getLogger().error("updat_scenarino  情景名称为空!");
				return AmpcResult.build(1003, "情景名称为空!");
			}
			//用户的id  确定当前用户
			scenarino.setUserId(Long.parseLong(data.get("userId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("userId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("updat_scenarino  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//情景ID 
			scenarino.setScenarinoId(Long.parseLong(data.get("scenarinoId").toString()));
			if(!RegUtil.CheckParameter(Long.parseLong(data.get("scenarinoId").toString()), "Long", null, false)){
				LogUtil.getLogger().error("updat_scenarino  情景ID为空!");
				return AmpcResult.build(1003, "情景ID为空!");
			}
			//执行状态 
			Long state=Long.parseLong(data.get("state").toString());
			if(!RegUtil.CheckParameter(state, "Long", null, false)){
				LogUtil.getLogger().error("updat_scenarino  执行状态 为空!");
				return AmpcResult.build(1003, "执行状态 为空!");
			}
			//判断执行状态 -1为只修改名称
			if(state==-1){
				//执行修改操作
				int result=this.tScenarinoDetailMapper.updateByPrimaryKeySelective(scenarino);
				return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "情景修改失败",null);
			}else{
				/**
				 * TODO 
				 * 需要调用计算接口 并更改情景的执行状态  
				 * 等待计算接口的实现
				 */
				
				
				
				
				return AmpcResult.build(1004, "等待计算接口的实现",null);
			}
			
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 情景修改方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "情景修改方法异常",null);
		}
	}
	
	/**
	 * 情景删除方法 实际为修改数据的状态
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	//@Transactional
	@RequestMapping("scenarino/delete_scenarino")
	public AmpcResult delete_Scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要删除的任务集合
			String scenarinoIds=data.get("scenarinoIds").toString();
			if(!RegUtil.CheckParameter(scenarinoIds, "String", null, false)){
				LogUtil.getLogger().error("delete_scenarino  要删除的任务为空!");
				return AmpcResult.build(1003, "要删除的任务为空!");
			}
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			if(!RegUtil.CheckParameter(userId, "Integer", null, false)){
				LogUtil.getLogger().error("delete_scenarino  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//将得到的数据拆分 放入集合中
			String[] idss=scenarinoIds.split(",");
			List<Long> scenarinoIdss=new ArrayList<Long>();
			for(int i=0;i<idss.length;i++){
				scenarinoIdss.add(Long.parseLong(idss[i]));
			}
			int sure=0;
			for(String scenarinoId:idss){
			TScenarinoDetail tSDetail=tScenarinoDetailMapper.selectByPrimaryKey(Long.valueOf(scenarinoId));
			List<TScenarinoDetail> tslist=tScenarinoDetailMapper.selectAllByMissionId(tSDetail.getMissionId());
			for(TScenarinoDetail ts:tslist){
				String bsid="";
				if(null!=ts.getBasisScenarinoId()){
				bsid=ts.getBasisScenarinoId().toString();
				}
				if(scenarinoId.equals(bsid)){
					sure+=1;
				}
			}	
			}
			if(sure!=0){
				return AmpcResult.build(9999, "error");
			}
			
			
			/**
			 * TODO 用事务更能保证一致性
			 */
			//统计区域的所有ID
			List<Long> areaIdss=new ArrayList<Long>();
			//统计时段的所有ID
			List<Long> timeIdss=new ArrayList<Long>();
			//记录区域的所有ID
			for (Long scenarinoId: scenarinoIdss) {
				Map map1=new HashMap();
				map1.put("userId", userId);
				map1.put("scenarinoId", scenarinoId);
				List<Long> areaIds=this.tScenarinoAreaMapper.selectBySid(map1);
				for (Long areaId : areaIds) {
					areaIdss.add(areaId);
				}
			}
			//记录时段的所有ID
			for (Long areaId: areaIdss) {
				List<Long> timeIds=this.tTimeMapper.selectTimeIdByAreaId(areaId);
				for (Long timeId : timeIds) {
					timeIdss.add(timeId);
				}
			}
			//执行时段级联删除方法
			Map res=atc.delete_times(timeIdss);
			String str=res.get("result").toString();
			//判断时段级联方法是否执行成功  -1 不成功  1成功
			if(str.equals("-1")){
				return AmpcResult.build(1000, res.get("msg").toString(),null);
			}
			int result;
			if(areaIdss.size()>0){
				//执行区域删除方法
				result=this.tScenarinoAreaMapper.updateIsEffeByIds(areaIdss);
				//判断是否执行成功
				if(result>0){
					//执行情景删除方法
					result=this.tScenarinoDetailMapper.updateIsEffeByIds(scenarinoIdss);
					if(result>0){
						for(Long scenarinoId:scenarinoIdss){
						result=tTasksStatusMapper.updateinf(scenarinoId);
						if(result==0){
							return AmpcResult.build(1000, "情景状态删除失败",null);
						}
						}
					
					}
					return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "情景修改失败",null);
				}else{
					return AmpcResult.build(1000, "区域修改失败",null);
				}
			}else{
				//执行情景删除方法
				result=this.tScenarinoDetailMapper.updateIsEffeByIds(scenarinoIdss);
				return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "情景修改失败",null);
			}
			
		} catch (Exception e) {
			LogUtil.getLogger().error("delete_scenarino 情景删除方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "情景删除方法异常",null);
		}
	}
	
	/**
	 * 添加情景对名称重复判断
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("scenarino/check_scenarinoname")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult check_ScenarinoName(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response)throws Exception{
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要添加的任务名称
			String scenarinoName=data.get("scenarinoName").toString();
			if(!RegUtil.CheckParameter(scenarinoName, "String", null, false)){
				LogUtil.getLogger().error("check_scenarinoname  任务名称为空!");
				return AmpcResult.build(1003, "任务名称为空!");
			}
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			if(!RegUtil.CheckParameter(userId, "Integer", null, false)){
				LogUtil.getLogger().error("check_scenarinoname  用户的id为空!");
				return AmpcResult.build(1003, "用户的id为空!");
			}
			//任务Id
			Long missionId=Long.parseLong(data.get("missionId").toString());
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("check_scenarinoname  任务Id为空!");
				return AmpcResult.build(1003, "任务Id为空!");
			}
			//讲信息写入参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionId", missionId);
			map.put("scenarinoName", scenarinoName);
			map.put("userId", userId);
			//执行判断
			int result=this.tScenarinoDetailMapper.check_ScenarinoName(map);
			//返回true 表示可用  返回false 已存在
			return result==0?AmpcResult.ok(true):AmpcResult.build(1000, "名称已存在",false);
		} catch (Exception e) {
			LogUtil.getLogger().error("check_scenarinoname 添加情景对名称重复判断异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "添加情景对名称重复判断异常",null);
		}
	}
	/**
	 * 
	 * 查询情景与时间
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("scenarino/find_scenarino_time")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_scenarino_time(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long missionId=Long.parseLong(data.get("missionId").toString());//任务id
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("find_scenarino_time  任务id为空!");
				return AmpcResult.build(1003, "任务id为空!");
			}
			Long userId=Long.parseLong(data.get("userId").toString());//情景id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_scenarino_time  情景id为空!");
				return AmpcResult.build(1003, "情景id为空!");
			}
			//根据id查询任务
			TMissionDetail tMission=tMissionDetailMapper.selectByPrimaryKey(missionId);
			List<TScenarinoDetail> sslist=new ArrayList();
			
			//根据任务id查询所有情景
			List<TScenarinoDetail> scenarlist=tScenarinoDetailMapper.selectAllByMissionId(missionId);
			Map<String,Date> map=new HashMap();
			map.put("startDate", tMission.getMissionStartDate());
			map.put("endDate", tMission.getMissionEndDate());
			if(tMission.getMissionStatus().equals("3")){
			if(scenarlist.isEmpty()){
				if(false){
				
				sslist=tScenarinoDetailMapper.selectBytype4(map);
				}
				if(sslist.isEmpty()){
				System.out.println("任务下无情景");	
				return AmpcResult.build(1000, "任务下无情景",null);
				}
			}
			}
			
			//查询基准情景最大结束时间
			TScenarinoDetail tsdate=new TScenarinoDetail();
			TScenarinoDetail frtscen=new TScenarinoDetail();
			if(tMission.getMissionStatus().equals("3")){	
				if(null!=tScenarinoDetailMapper.selectMaxEndTime(missionId)){
					tsdate=tScenarinoDetailMapper.selectMaxEndTime(missionId);
				}else{
					if(sslist.isEmpty()){
					System.out.println("无基准情景与预报情景");	
					return AmpcResult.build(1000, "无基准情景与预报情景",null);
					}
					}
			
			}
			if(tMission.getMissionStatus().equals("2")){
				if(!tScenarinoDetailMapper.selectBytype4(map).isEmpty()){
					sslist=tScenarinoDetailMapper.selectBytype4(map);
				}else{
					System.out.println("无实时预报情景");	
					return AmpcResult.build(1000, "无实时预报情景",null);
					}
					
			}else if(tMission.getMissionStatus().equals("3")&&null!=tScenarinoDetailMapper.selectMaxEndTime4()){
				if(null!=tScenarinoDetailMapper.selectMaxEndTime4()){
					frtscen=tScenarinoDetailMapper.selectMaxEndTime4();
				}else{
					if(sslist.isEmpty()){
						System.out.println("无实时预报情景");	
						return AmpcResult.build(1000, "无实时预报情景",null);
						}
					
					}
			}
			Date scenar=null;
			Date frtdate=null;
			
			if(null!=tScenarinoDetailMapper.selectMaxEndTime(missionId)){
			scenar=tsdate.getScenarinoEndDate();
			}
//			if(null!=tScenarinoDetailMapper.selectMaxEndTime4()){
//			frtdate=frtscen.getScenarinoEndDate();
//			}
			//转换类型
			Date mission=tMission.getMissionStartDate();
			
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String times="";
			String frdate="";
			if(null!=tScenarinoDetailMapper.selectMaxEndTime(missionId)){
			times=formatter.format(scenar);
			}
			String missiondate=formatter.format(mission);
//			if(null!=tScenarinoDetailMapper.selectMaxEndTime4()){
//			frdate=formatter.format(frtdate);
//			}
			JSONArray arr=new JSONArray();
			
			if(tMission.getMissionStatus().equals("3")&&!sslist.isEmpty()){
				
				for(TScenarinoDetail tsc:sslist){
				JSONObject forobj=new JSONObject();
				forobj.put("scenarinoId",tsc.getScenarinoId());
				forobj.put("scenarinoName", tsc.getScenarinoName());
				forobj.put("scenarinoStartDate", formatter.format(tsc.getScenarinoStartDate()));
				forobj.put("ScenType", tsc.getScenType());
				forobj.put("scenarinoEndDate", formatter.format(tsc.getScenarinoEndDate()));
				arr.add(forobj);	
				}
			}else if(tMission.getMissionStatus().equals("3")){
				JSONObject lastobj=new JSONObject();
				lastobj.put("scenarinoId",tsdate.getScenarinoId());
				lastobj.put("scenarinoName", tsdate.getScenarinoName());
				lastobj.put("scenarinoStartDate", missiondate);
				lastobj.put("ScenType", tsdate.getScenType());
				lastobj.put("scenarinoEndDate", times);
				arr.add(lastobj);	
			}else{
				for(TScenarinoDetail tsc:sslist){
					JSONObject forobj=new JSONObject();
					forobj.put("scenarinoId",tsc.getScenarinoId());
					forobj.put("scenarinoName", tsc.getScenarinoName());
					forobj.put("scenarinoStartDate", formatter.format(tsc.getScenarinoStartDate()));
					forobj.put("ScenType", tsc.getScenType());
					forobj.put("scenarinoEndDate", formatter.format(tsc.getPathDate()));
					forobj.put("theDate", formatter.format(tsc.getScenarinoStartDate()));
					arr.add(forobj);	
					}
			}
			for(TScenarinoDetail tscenar:scenarlist){
				JSONObject obj=new JSONObject();
				//为预评估情景时保存数据
				if(tscenar.getScenType().equals("1")){
					obj.put("scenarinoId", tscenar.getScenarinoId());
					obj.put("scenarinoName", tscenar.getScenarinoName());
					String starttime=formatter.format(tscenar.getScenarinoStartDate());
					String endtime=formatter.format(tscenar.getScenarinoEndDate());
					String padate=formatter.format(tscenar.getPathDate());
					obj.put("scenarinoStartDate",starttime);
					obj.put("ScenType", tscenar.getScenType());
					obj.put("scenarinoEndDate", endtime);
					obj.put("pathDate", padate);
					arr.add(obj);
				}
				//为后处理情景时保存数据
				if(tscenar.getScenType().equals("2")){
					obj.put("scenarinoId", tscenar.getScenarinoId());
					obj.put("scenarinoName", tscenar.getScenarinoName());
					String starttime=formatter.format(tscenar.getScenarinoStartDate());  
					String endtime=formatter.format(tscenar.getScenarinoEndDate());
				 
					obj.put("scenarinoStartDate",starttime);
					obj.put("ScenType", tscenar.getScenType());
					obj.put("scenarinoEndDate", endtime);
					arr.add(obj);
				}	
			}
			
			
			return AmpcResult.build(0, "find_scenarino_time success",arr);
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 查询情景与时间异常",e);
		return AmpcResult.build(1001, "查询情景与时间异常",null);
		}
	}
	//查询结束日期
	@RequestMapping("scenarino/find_endTime")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_endTime(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
       try{
    	   ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			String scenType=data.get("scenType").toString();//任务id
			if(!RegUtil.CheckParameter(scenType, "String", null, false)){
				LogUtil.getLogger().error("find_endTime  情景类型为空!");
				return AmpcResult.build(1003, "情景类型为空!");
			}
			Long userId=Long.parseLong(data.get("userId").toString());//情景id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_endTime  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			//查询用户信息
			TUserSetting tUser=tUserSettingMapper.selectByUserId(userId);
			if(tUser==null||tUser.equals("")){
				return AmpcResult.build(1000, "未查询到用户");	
			}
			Integer predictionTime=Integer.valueOf(tUser.getPredictionTime().toString());
			//获取当前时间
			Date date=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject obj=new JSONObject();
			if(scenType.equals("1")){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, predictionTime);
			String addTimeDate =sdf.format(cal.getTime());
			obj.put("endTime", addTimeDate);
			}else{
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DATE, -1);
				String addTimeDate =sdf.format(cal.getTime());
				obj.put("endTime", addTimeDate);
			} 
    	   return AmpcResult.build(0, "find_endTime success",obj);
       }catch(Exception e){
    	   LogUtil.getLogger().error("MissionAndScenarinoController 查询结束日期异常",e);
		   return AmpcResult.build(1001, "查询结束日期异常",null);
		}
	}
	
	/**
	 * 情景创建方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("scenarino/save_scenarino")
	public AmpcResult save_Scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Long missionId=Long.valueOf(data.get("missionId").toString());//任务id
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("save_scenarino  任务id为空!");
				return AmpcResult.build(1003, "任务id为空!");
			}
			Long userId=Long.valueOf(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("save_scenarino  任务id为空!");
				return AmpcResult.build(1003, "任务id为空!");
			}
			String missionType=data.get("missionType").toString();//任务类型
			if(!RegUtil.CheckParameter(missionType, "String", null, false)){
				LogUtil.getLogger().error("save_scenarino  任务类型为空!");
				return AmpcResult.build(1003, "任务类型为空!");
			}
			String scenarinoName=data.get("scenarinoName").toString();//情景名称
			if(!RegUtil.CheckParameter(scenarinoName, "String", null, false)){
				LogUtil.getLogger().error("save_scenarino  情景名称为空!");
				return AmpcResult.build(1003, "情景名称为空!");
			}
			String scenType=data.get("scenType").toString();//情景类型
			if(!RegUtil.CheckParameter(scenType, "String", null, false)){
				LogUtil.getLogger().error("save_scenarino  情景类型为空!");
				return AmpcResult.build(1003, "情景类型为空!");
			}
			Date pathDate1=new Date();
			String pathdate2=sdf.format(pathDate1);
			Date pathDate=sdf.parse(pathdate2);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(date);
//			cal.add(Calendar.DATE, -1);
//			String addTimeDate =sdf.format(cal.getTime());
//			Date pathDate=sdf.parse(addTimeDate);
		    Date enddate=null;
			Date startdate = null;
			
			Long sdid=0l;
			int d=0;
			int a=0;
			//预评估任务创建情景
			if(missionType.equals("2")){
				//预评估任务创建预评估情景
				if(scenType.equals("1")){
					String startDate =data.get("scenarinoStartDate").toString();//开始时间
					String endDate= data.get("scenarinoEndDate").toString();//结束时间
					Date scenarinoStartDate=sdf.parse(startDate);
					Date scenarinoEndDate= sdf.parse(endDate);
					Long basisScenarinoId=Long.valueOf(data.get("basisScenarinoId").toString());//基础情景
					String baTime=data.get("basisTime").toString();//基础时间
					Date basisTime=sdf.parse(baTime);
				TScenarinoDetail tsd=new TScenarinoDetail();
				
//				TScenarinoDetail tsd=new TScenarinoDetail();
//				
//				tsd.setBasisScenarinoId(basisScenarinoId);
//				tsd.setBasisTime(basisTime);
//				tsd.setScenarinoName(scenarinoName);
//				tsd.setScenarinoStartDate(scenarinoStartDate);
//				tsd.setScenarinoEndDate(scenarinoEndDate);
//				tsd.setScenType(scenType);
//				tsd.setMissionId(missionId);
//				tsd.setUserId(userId);
//				tsd.setIsEffective("1");
				tsd.setBasisScenarinoId(basisScenarinoId);
				tsd.setBasisTime(basisTime);
				tsd.setScenarinoName(scenarinoName);
				tsd.setScenarinoStartDate(scenarinoStartDate);
				tsd.setScenarinoEndDate(scenarinoEndDate);
				tsd.setScenType(scenType);
				tsd.setMissionId(missionId);
				tsd.setPathDate(pathDate);
				tsd.setUserId(userId);
				tsd.setIsEffective("1");
				a=tScenarinoDetailMapper.insertSelective(tsd);	
				TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
				sdid=tScenarinoDetail.getScenarinoId();
				enddate=tScenarinoDetail.getScenarinoEndDate();
				startdate=tScenarinoDetail.getScenarinoStartDate();
				}
				//预评估任务创建后评估情景
			if(scenType.equals("2")){
				//基础情景
					if(data.get("controstScenarinoId")==null||data.get("controstScenarinoId")==""){
						
						String startDate =data.get("scenarinoStartDate").toString();//开始时间
						String endDate= data.get("scenarinoEndDate").toString();//结束时间
						Date scenarinoStartDate=sdf.parse(startDate);
						Date scenarinoEndDate= sdf.parse(endDate);
						Long basisScenarinoId=Long.valueOf(data.get("basisScenarinoId").toString());//基础情景
						String baTime=data.get("basisTime").toString();//基础时间
						Date basisTime=sdf.parse(baTime);
						TScenarinoDetail tsd=new TScenarinoDetail();
						
						tsd.setBasisScenarinoId(basisScenarinoId);
						tsd.setBasisTime(basisTime);
						tsd.setScenarinoName(scenarinoName);
						tsd.setScenarinoStartDate(scenarinoStartDate);
						tsd.setScenarinoEndDate(scenarinoEndDate);
						tsd.setScenType(scenType);
						tsd.setMissionId(missionId);
						tsd.setUserId(userId);
						tsd.setIsEffective("1");
						a=tScenarinoDetailMapper.insertSelective(tsd);
						TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
						sdid=tScenarinoDetail.getScenarinoId();
						enddate=tScenarinoDetail.getScenarinoEndDate();
						startdate=tScenarinoDetail.getScenarinoStartDate();
					}else{//对比情景
						Long controstScenarinoId=Long.valueOf(data.get("controstScenarinoId").toString());//对比情景id
						TScenarinoDetail tscent=tScenarinoDetailMapper.selectByPrimaryKey(controstScenarinoId);
						Date scenarinoStartDate =tscent.getScenarinoStartDate();
						Date scenarinoEndDate=tscent.getScenarinoEndDate();
						TScenarinoDetail tsd=new TScenarinoDetail();
						tsd.setScenarinoName(scenarinoName);
						tsd.setBasisScenarinoId(tscent.getBasisScenarinoId());
						tsd.setBasisTime(tscent.getBasisTime());
						tsd.setScenType(scenType);
						tsd.setMissionId(missionId);
						tsd.setScenarinoEndDate(scenarinoEndDate);
						tsd.setScenarinoStartDate(scenarinoStartDate);
						tsd.setUserId(userId);
						tsd.setIsEffective("1");
						tsd.setContrastScenarinoId(controstScenarinoId);
						a=tScenarinoDetailMapper.insertSelective(tsd);
						TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
						sdid=tScenarinoDetail.getScenarinoId();
						enddate=tScenarinoDetail.getScenarinoEndDate();
						startdate=tScenarinoDetail.getScenarinoStartDate();
					}
				}		
			}
			//后评估任务创建情景
			if(missionType.equals("3")){
				//创建后评估情景
				if(scenType.equals("2")){
					//基础情景
					if(data.get("controstScenarinoId")==null||data.get("controstScenarinoId")==""){
						
						String startDate =data.get("scenarinoStartDate").toString();//开始时间
						String endDate= data.get("scenarinoEndDate").toString();//结束时间
						Date scenarinoStartDate=sdf.parse(startDate);
						Date scenarinoEndDate= sdf.parse(endDate);
						Long basisScenarinoId=Long.valueOf(data.get("basisScenarinoId").toString());//基础情景
						String baTime=data.get("basisTime").toString();//基础时间
						Date basisTime=sdf.parse(baTime);
						TScenarinoDetail tsd=new TScenarinoDetail();
						
						tsd.setBasisScenarinoId(basisScenarinoId);
						tsd.setBasisTime(basisTime);
						tsd.setScenarinoName(scenarinoName);
						tsd.setScenarinoStartDate(scenarinoStartDate);
						tsd.setScenarinoEndDate(scenarinoEndDate);
						tsd.setScenType(scenType);
						tsd.setMissionId(missionId);
						tsd.setUserId(userId);
						tsd.setIsEffective("1");
						a=tScenarinoDetailMapper.insertSelective(tsd);

						TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
						sdid=tScenarinoDetail.getScenarinoId();
						enddate=tScenarinoDetail.getScenarinoEndDate();
						startdate=tScenarinoDetail.getScenarinoStartDate();
					}else{//对比情景
						Long controstScenarinoId=Long.valueOf(data.get("controstScenarinoId").toString());//对比情景id
						TScenarinoDetail tscent=tScenarinoDetailMapper.selectByPrimaryKey(controstScenarinoId);
						Date scenarinoStartDate =tscent.getScenarinoStartDate();
						Date scenarinoEndDate=tscent.getScenarinoEndDate();
						TScenarinoDetail tsd=new TScenarinoDetail(); 
						tsd.setScenarinoName(scenarinoName);
						tsd.setBasisScenarinoId(tscent.getBasisScenarinoId());
						tsd.setBasisTime(tscent.getBasisTime());
						tsd.setScenType(scenType);
						tsd.setMissionId(missionId);
						tsd.setScenarinoEndDate(scenarinoEndDate);
						tsd.setScenarinoStartDate(scenarinoStartDate);
						tsd.setUserId(userId);
						tsd.setIsEffective("1");
						tsd.setContrastScenarinoId(controstScenarinoId);
						a=tScenarinoDetailMapper.insertSelective(tsd);
						TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
						sdid=tScenarinoDetail.getScenarinoId();
						enddate=tScenarinoDetail.getScenarinoEndDate();
						startdate=tScenarinoDetail.getScenarinoStartDate();
					}
					
				}
				//创建基准情景
				if(scenType.equals("3")){
					Long spinUp=Long.valueOf(data.get("spinUp").toString());
					String startDate =data.get("scenarinoStartDate").toString();//开始时间
					String endDate= data.get("scenarinoEndDate").toString();//结束时间
					Date scenarinoStartDate=sdf.parse(startDate);
					Date scenarinoEndDate= sdf.parse(endDate);
					TScenarinoDetail tsd=new TScenarinoDetail();
					
					tsd.setScenarinoName(scenarinoName);
					tsd.setScenarinoStartDate(scenarinoStartDate);
					tsd.setScenarinoEndDate(scenarinoEndDate);
					tsd.setScenType(scenType);
					tsd.setMissionId(missionId);
					tsd.setUserId(userId);
					tsd.setSpinup(spinUp);
					tsd.setScenarinoStatus(5l);
					tsd.setIsEffective("1");
					a=tScenarinoDetailMapper.insertSelective(tsd);
					TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectid(tsd);
					sdid=tScenarinoDetail.getScenarinoId();
					enddate=tScenarinoDetail.getScenarinoEndDate();
					startdate=tScenarinoDetail.getScenarinoStartDate();
				}	
			}
			if(a!=0){//创建区域和
				TTasksStatus tasks=new TTasksStatus();
				tasks.setTasksScenarinoId(sdid);	
				  Calendar aCalendar = Calendar.getInstance();
			       aCalendar.setTime(enddate);
			       int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
			       aCalendar.setTime(startdate);
			       int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
			     int days= day1-day2;
				tasks.setTasksRangeDay(Long.valueOf(days+1));
				tasks.setTasksScenarinoEndDate(enddate);
				tasks.setTasksScenarinoStartDate(startdate);
				tTasksStatusMapper.insertSelective(tasks);

				List<String> areanamelist=new ArrayList();
				areanamelist.add("第一区域");
				areanamelist.add("第二区域");
				areanamelist.add("第三区域");
				for(String area:areanamelist){
				TScenarinoAreaWithBLOBs ScenarinoArea =new TScenarinoAreaWithBLOBs();
				ScenarinoArea.setAreaName(area);
				ScenarinoArea.setUserId(userId);
				ScenarinoArea.setScenarinoDetailId(sdid);
				int c=tScenarinoAreaMapper.insertSelective(ScenarinoArea);
				List<TScenarinoAreaWithBLOBs> list=tScenarinoAreaMapper.selectByEntity(ScenarinoArea);
				TScenarinoAreaWithBLOBs s=list.get(0);
				//创建时段
				if(c!=0){
					TTime times=new TTime();
					if(data.get("controstScenarinoId")==null||data.get("controstScenarinoId")==""){
					String startDate =data.get("scenarinoStartDate").toString();//开始时间
					String endDate= data.get("scenarinoEndDate").toString();//结束时间
					Date scenarinoStartDate=sdf.parse(startDate);
					Date scenarinoEndDate= sdf.parse(endDate);
					times.setTimeEndDate(scenarinoEndDate);
					times.setTimeStartDate(scenarinoStartDate);
					}else{
						Long controstScenarinoId=Long.valueOf(data.get("controstScenarinoId").toString());//对比情景id
						TScenarinoDetail tscent=tScenarinoDetailMapper.selectByPrimaryKey(controstScenarinoId);
						Date startDate =tscent.getScenarinoStartDate();
						Date endDate=tscent.getScenarinoEndDate();
						String end=sdf.format(endDate);
						String start=sdf.format(startDate);
						Date scenarinoEndDate=sdf.parse(end);
						Date scenarinoStartDate=sdf.parse(start);
						times.setTimeEndDate(scenarinoEndDate);
						times.setTimeStartDate(scenarinoStartDate);
					}
					
					times.setAreaId(s.getScenarinoAreaId());
					times.setMissionId(missionId);
					times.setUserId(userId);
					times.setScenarinoId(sdid);
					d=tTimeMapper.insertSelective(times);
				}else{
					LogUtil.getLogger().error("save_scenarino  区域创建异常!");
					return AmpcResult.build(1000, "区域创建异常",null);
				}
				}
				
				
			}else{
				LogUtil.getLogger().error("save_scenarino  情景创建异常!");
				return AmpcResult.build(1000, "情景创建异常",null);
			}
			if(d!=0){
				JSONObject obj=new JSONObject();
				obj.put("scenarinoId", sdid);
				return AmpcResult.ok(obj);
			}
			 LogUtil.getLogger().error("save_scenarino  时段创建异常!");
			 return AmpcResult.build(1000, "时段创建异常",null);
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 情景创建方法异常",e);
			return AmpcResult.build(1001, "情景创建方法异常",null);				
		}
	}
	/**
	 * 查询情景状态
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("scenarino/find_Scenarino_status")
	public AmpcResult find_Scenarino_status(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());
			if(!RegUtil.CheckParameter(scenarinoId, "Long", null, false)){
				LogUtil.getLogger().error("find_Scenarino_status  情景id为空!");
				return AmpcResult.build(1003, "情景id为空!");
			}
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			if(tScenarinoDetail==null||tScenarinoDetail.equals("")){
				LogUtil.getLogger().error("find_Scenarino_status  情景查询异常!");
				return AmpcResult.build(1000, "情景查询异常",null);
			}
			JSONObject obj=new JSONObject();
			obj.put("scenarinoStatus", tScenarinoDetail.getScenarinoStatus());
		  return AmpcResult.ok(obj);	
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 查询情景状态异常",e);
			 return AmpcResult.build(1001, "查询情景状态异常",null);	
		}
	}
	/**
	 * 情景复制
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("scenarino/copy_Scenarino")
	public AmpcResult copy_Scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("copy_Scenarino  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());//情景id
			if(!RegUtil.CheckParameter(scenarinoId, "Long", null, false)){
				LogUtil.getLogger().error("copy_Scenarino  情景id为空!");
				return AmpcResult.build(1003, "情景id为空!");
			}
			Long copyscenarinoId=Long.parseLong(data.get("copyscenarinoId").toString());//被复制情景id
			if(!RegUtil.CheckParameter(copyscenarinoId, "Long", null, false)){
				LogUtil.getLogger().error("copy_Scenarino  被复制情景id为空!");
				return AmpcResult.build(1003, "被复制情景id为空!");
			}
			//被复制的情景
			TScenarinoDetail copytScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(copyscenarinoId);
			if(copytScenarinoDetail==null||copytScenarinoDetail.equals("")){
				LogUtil.getLogger().error("copy_Scenarino  未查到被复制情景!");
				return AmpcResult.build(1003, "未查到被复制情景!");
			}
			//情景
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			
			//情景区域
			TScenarinoAreaWithBLOBs tScenarinoArea =new TScenarinoAreaWithBLOBs();
			tScenarinoArea.setScenarinoDetailId(scenarinoId);
			tScenarinoArea.setIsEffective("1");
			List<TScenarinoAreaWithBLOBs> arealist=tScenarinoAreaMapper.selectByEntity(tScenarinoArea);
			for(TScenarinoAreaWithBLOBs area:arealist){
				area.setIsEffective("0");
				tScenarinoAreaMapper.updateByPrimaryKeySelective(area);
			}
			//情景时段
			List<TTime> timelist=tTimeMapper.selectByScenarinoId(scenarinoId);
			for(TTime times:timelist){
				times.setIsEffective("0");
				tTimeMapper.updateByPrimaryKeySelective(times);	
			}
			//被复制情景的区域集合
			TScenarinoAreaWithBLOBs copytScenarinoArea =new TScenarinoAreaWithBLOBs();
			copytScenarinoArea.setScenarinoDetailId(copyscenarinoId);
			copytScenarinoArea.setIsEffective("1");
			List<TScenarinoAreaWithBLOBs> copyarealist=tScenarinoAreaMapper.selectByEntity(copytScenarinoArea);
			//被复制情景的开始结束时间
			Date copyendtime=copytScenarinoDetail.getScenarinoEndDate();//结束时间
			Date copystarttime=copytScenarinoDetail.getScenarinoStartDate();//开始时间
			Long a=copyendtime.getTime()-copystarttime.getTime();
			float ha=a/1000f;//小时数
			//情景的开始结束时间
			Date endtime=tScenarinoDetail.getScenarinoEndDate();
			Date starttime=tScenarinoDetail.getScenarinoStartDate();//开始时间
			Long b=endtime.getTime()-starttime.getTime();//结束时间
			float hb=b/1000f;//小时数
				
			float s=hb/ha;
			for(TScenarinoAreaWithBLOBs area:copyarealist){
				TScenarinoAreaWithBLOBs newtScenarinoArea =new TScenarinoAreaWithBLOBs();
				newtScenarinoArea.setScenarinoDetailId(scenarinoId);
				newtScenarinoArea.setAreaName(area.getAreaName());
				newtScenarinoArea.setCityCodes(area.getCityCodes().toString());
				newtScenarinoArea.setCountyCodes(area.getCountyCodes().toString());
				newtScenarinoArea.setProvinceCodes(area.getProvinceCodes().toString());
				newtScenarinoArea.setUserId(userId);	
				newtScenarinoArea.setAddTime(area.getAddTime());
				int yes=tScenarinoAreaMapper.insertSelective(newtScenarinoArea);
				List<TScenarinoAreaWithBLOBs> thetScenarinoArea=tScenarinoAreaMapper.selectByEntity(newtScenarinoArea);
				TScenarinoAreaWithBLOBs theis=thetScenarinoArea.get(0);
				if(yes!=0){
					TTime times=new TTime();
					times.setAreaId(area.getScenarinoAreaId());
					times.setScenarinoId(area.getScenarinoDetailId());
					List<TTime> timeslist=tTimeMapper.selectByEntity(times);
					for(int ss=0;ss<timeslist.size();ss++){
						TTime timee=timeslist.get(ss);
						Date start=timee.getTimeStartDate();
						Date end=timee.getTimeEndDate();
						Long chas=end.getTime()-start.getTime();//开始结束时间差
						Long qt=start.getTime()-copystarttime.getTime();//开始与开始时间差
						Long endchar=copyendtime.getTime()-end.getTime();
						float qthour=qt/1000/60/60f;//开始与开始时间差
						float qtcha=qthour*s;
						
						
						float ends=endchar/1000/60/60f;
						float endhour=ends*s;
						
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						cal.setTime(starttime);
						cal.add(Calendar.HOUR, (int)qtcha);
						String starDate =sdf.format(cal.getTime());//
						Date startDate=sdf.parse(starDate);
						
						Calendar qtcal = Calendar.getInstance();
						cal.setTime(endtime);
				
						cal.add(Calendar.HOUR, -(int)endhour);
						if(copyendtime.getTime()-end.getTime()!=0 && hb<ha){
							cal.add(Calendar.HOUR, -1);
						}
						
						String endTimeDate =sdf.format(cal.getTime());
						Date endtimes=sdf.parse(endTimeDate);
						TTime timeses=new TTime();
						timeses.setTimeEndDate(endtimes);
						timeses.setTimeStartDate(startDate);
						timeses.setMissionId(tScenarinoDetail.getMissionId());
						timeses.setAreaId(theis.getScenarinoAreaId());
						timeses.setPlanId(timee.getPlanId());
						timeses.setUserId(userId);
						timeses.setScenarinoId(scenarinoId);
						int whats=tTimeMapper.insertSelective(timeses);
						List<TTime> whotime=tTimeMapper.selectByEntity(timeses);
						TTime thstime=whotime.get(0);
						if(whats!=0){
							if(thstime.getPlanId()!=-1){
							TPlan tplan=tPlanMapper.selectByPrimaryKey(thstime.getPlanId());
							tplan.setCopyPlan("1");
							int as=tPlanMapper.updateByPrimaryKeySelective(tplan);
							if(as==0){
								return AmpcResult.build(1000, "预案修改失败",null);
							}
							}
						}else{
							return AmpcResult.build(1000, "时段复制失败",null);
						}
					}
					
				}else{
					
					return AmpcResult.build(1000, "区域复制失败",null);
				}					
			}
			int mis=scenarinoStatusUtil.updateScenarinoStatus(scenarinoId);
				if(mis!=0){ 
					return AmpcResult.ok("复制成功");
				}else{
					return AmpcResult.build(1000, "情景状态转换失败",null);
				}
	
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 情景复制异常",e);
			 return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	/**
	 * 根据userid查询任务
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("mission/find_All_mission")
	public AmpcResult find_All_mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_All_mission  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			TMissionDetail tMissionDetail=new TMissionDetail();
			tMissionDetail.setUserId(userId);
			List<TMissionDetail> missionlist=tMissionDetailMapper.selectByEntity2(tMissionDetail);
			JSONArray arr=new JSONArray();
			if(!missionlist.isEmpty()){
			for(TMissionDetail mission:missionlist){
				List<TScenarinoDetail> tScenarinoDetaillist=tScenarinoDetailMapper.selectAllByMissionId(mission.getMissionId());
				Long b=0l;
				for(TScenarinoDetail tScenarinoDetail:tScenarinoDetaillist){
				Long status=tScenarinoDetail.getScenarinoStatus();
				if(status==8&&!tScenarinoDetail.getScenType().equals("3")&&!tScenarinoDetail.getScenType().equals("4")){
					b+=1;
				}
				
			}
				if(b!=0){
					
				JSONObject obj=new JSONObject();
				for(TScenarinoDetail ts:tScenarinoDetaillist){
					if(ts.getScenType().equals("3")){
						obj.put("jzqjid",ts.getScenarinoId());	
					}
					
				}
				obj.put("missionId", mission.getMissionId());
				obj.put("missionName", mission.getMissionName());
				obj.put("missionStartDate", mission.getMissionStartDate().getTime());
				obj.put("missionEndDate", mission.getMissionEndDate().getTime());
				obj.put("domainId", mission.getMissionDomainId());
				arr.add(obj);
				}
			}
			}else{
				LogUtil.getLogger().error("find_All_mission 该用户没有创建任务");
				return AmpcResult.build(1004, "该用户没有创建任务",null);
			}
		return AmpcResult.ok(arr);	
		}catch(Exception e){
			LogUtil.getLogger().error("find_All_mission 根据userid查询任务有异常",e);
			return AmpcResult.build(1001, "根据userid查询任务有异常",null);	
		}
		
		
	}
	
	/**
	 * 根据任务id以及userid查询情景
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	
	@RequestMapping("scenarino/find_All_scenarino")
	public AmpcResult find_All_scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_All_scenarino  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			Long missionId=Long.parseLong(data.get("missionId").toString());
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("find_All_scenarino  任务id为空!");
				return AmpcResult.build(1003, "任务id为空!");
			}
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setUserId(userId);
			tScenarinoDetail.setMissionId(missionId);
			List<TScenarinoDetail> tScenarinoDetaillist=tScenarinoDetailMapper.selectByEntity2(tScenarinoDetail);
			JSONArray arr=new JSONArray();
			JSONObject objsed=new JSONObject();
			if(!tScenarinoDetaillist.isEmpty()){
			for(TScenarinoDetail Scenarino:tScenarinoDetaillist){
				if(Scenarino.getScenarinoStatus()==8 && !Scenarino.getScenType().equals("4")){
				JSONObject obj=new JSONObject();
				obj.put("scenarinoId", Scenarino.getScenarinoId());
				obj.put("scenarinoName", Scenarino.getScenarinoName());
				obj.put("scenType", Scenarino.getScenType());
				obj.put("scenarinoStartDate", Scenarino.getScenarinoStartDate().getTime());
				obj.put("scenarinoEndDate", Scenarino.getScenarinoEndDate().getTime());
				arr.add(obj);
				objsed.put("rows", arr);
				}
			}
			}else{
				LogUtil.getLogger().error("find_All_scenarino 该任务没有创建情景");
				return AmpcResult.build(1004, "该任务没有创建情景",null);
			}
		return AmpcResult.ok(objsed);
		}catch(Exception e){
			LogUtil.getLogger().error("find_All_scenarino 根据任务id以及userid查询情景有异常",e);
			return AmpcResult.build(1001, "根据任务id以及userid查询情景有异常",null);
		}
	}
	
	/**
	 * 根据userid查询有情景的任务
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("mission/find_haveScenarino_mission")
	public AmpcResult find_haveScenarino_mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_haveScenarino_mission  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			TMissionDetail tMissionDetail=new TMissionDetail();
			tMissionDetail.setUserId(userId);
			List<TMissionDetail> missionlist=tMissionDetailMapper.selectByEntity(tMissionDetail);
			
			JSONArray arr=new JSONArray();
			if(!missionlist.isEmpty()){
			for(TMissionDetail mission:missionlist){
				JSONObject obj=new JSONObject();
				TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
				tScenarinoDetail.setUserId(userId);
				tScenarinoDetail.setMissionId(mission.getMissionId());
				List<TScenarinoDetail> tScenarinoDetaillist=tScenarinoDetailMapper.selectByEntity(tScenarinoDetail);
				if(!tScenarinoDetaillist.isEmpty()){
				obj.put("missionId", mission.getMissionId());
				obj.put("missionName", mission.getMissionName());
				arr.add(obj);
				}
			}
			}else{
				LogUtil.getLogger().error("find_haveScenarino_mission 该用户没有创建任务");
				return AmpcResult.build(1004, "该用户没有创建任务",null);
			}
		return AmpcResult.ok(arr);
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 根据userid查询有情景的任务有异常",e);
			return AmpcResult.build(1001, "根据userid查询有情景的任务有异常",null);
		}
	}
	
	
	
	
	
	/**
	 * TODO 新任务情景
	 */
	/**
	 * 任务查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("new_mission/get_mission_list")
	public AmpcResult new_get_Mission_list(HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			//条件名称
			String queryName=request.getParameter("queryName");
			//任务状态
			String missionStatus=request.getParameter("missionStatus");
			//当前页码
			Integer pageNum=Integer.valueOf(request.getParameter("page"));
			//每页展示的条数
			Integer pageSize=Integer.valueOf(request.getParameter("rows"));
			//列表排序  暂时内定按照任务ID逆序排序
			String sort=request.getParameter("sort");
			//用户的id  确定当前用户
			String userId=request.getParameter("userId");
			//添加分页信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("startNum", ((pageNum-1)*pageSize));
			map.put("endNum", (pageNum*pageSize));
			map.put("userId", userId);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			//返回的页码
			mapResult.put("page", pageNum);
			//判断是否有条件名称,如果有则调用根据条件名模糊查询
			if(null!=queryName&&!queryName.equals("")){
				String name="%"+queryName+"%";
				map.put("queryName",name);
			}else{
				map.put("queryName",null);
			}
			//判断任务状态
			if(null!=missionStatus&&!missionStatus.equals("")){
				map.put("missionStatus",missionStatus);
			}else{
				map.put("missionStatus",null);
			}
			//查询当前用户下有多少错误的情景
			Long errorCount=tScenarinoDetailMapper.selectErrorCount(Long.parseLong(userId));
			mapResult.put("errorCount", errorCount);
			//查询全部写入返回结果集
			List<Map> list = this.tMissionDetailMapper.selectAllOrByQueryName(map);
			//循环结果集合
			for(Map ss:list){
				//获取到范围ID
				Long missionDomainId=Long.valueOf(ss.get("missionDomainId").toString());
				//获取任务ID
				Long missionId=Long.valueOf(ss.get("missionId").toString());
				//查询情景条件
				Map queryMap=new HashMap();
				queryMap.put("missionId", missionId);
				queryMap.put("userId", userId);
				//根据任务ID查询所有的情景
				List<Map> tmMap=tScenarinoDetailMapper.selectByMissionIdNew(queryMap);
				//记录当前有多少情景
				int s=0;
				//组装子集的数据
				List children=new ArrayList();
				Map qjTitle=new HashMap();
				qjTitle.put("id", missionId+"title");
				qjTitle.put("scenarinoNameTitle", "情景名称");
				qjTitle.put("scenarinoIdTitle","ID");
				qjTitle.put("operationTitle","操作");
				qjTitle.put("adminTitle","管理");
				qjTitle.put("scenarinoStatusTitle","情景状态");
				qjTitle.put("runTimeTitle","执行日期");
				qjTitle.put("EndDateTitle","结束日期");
				qjTitle.put("scenTypeTitle","类型");
				qjTitle.put("settingTitle","设置");
				int missionErrorCount=0;
				//加入数据
				children.add(qjTitle);
				for(int i=0;i<tmMap.size();i++){
					tmMap.get(i).put("id", "qj"+tmMap.get(i).get("scenarinoId"));
					//查询有多少个错误的情景  累计增加
					if(tmMap.get(i).get("expand4")==null||tmMap.get(i).get("expand4").equals("")){
						missionErrorCount++;
					}
					children.add(tmMap.get(i));
					//判断有多少个正在执行的情景
					if(tmMap.get(i).get("scenarinoStatus").equals(6)){
						s++;
					}
				}
				//记录当前任务下有多少个错误的情景
				ss.put("missionErrorCount",missionErrorCount);
				//写入情景信息
				ss.put("children", children);
				//写入ID
				ss.put("id", "rw"+ss.get("missionId"));
				//写入当前任务有多少情景
				ss.put("scnum", tmMap.size());
				//写入当前任务有多少正在执行的情景
				ss.put("zxscnum", s);
				//根据domain查询domain信息
				TDomainMission tDomainMission=tDomainMissionMapper.selectByPrimaryKey(missionDomainId);
				//写入domain名称
				ss.put("domainName", tDomainMission.getDomainName());
				ss.put("state", "closed");
			}
			mapResult.put("total", this.tMissionDetailMapper.selectCountOrByQueryName(map));
			mapResult.put("rows",list);
			
			LogUtil.getLogger().info("MissionAndScenarinoController 任务查询成功");
			//返回结果
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 任务查询方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "系统异常",null);
		}
	}
	/**
	 * 任务查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("new_mission/get_errormission_list")
	public AmpcResult new_get_ErrorMission_list(HttpServletRequest request, HttpServletResponse response){
	    //添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			//当前页码
			Integer pageNum=Integer.valueOf(request.getParameter("page"));
			//每页展示的条数
			Integer pageSize=Integer.valueOf(request.getParameter("rows"));
			//列表排序  暂时内定按照任务ID逆序排序
			String sort=request.getParameter("sort");
			//用户的id  确定当前用户
			String userId=request.getParameter("userId");
			//添加分页信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("startNum", ((pageNum-1)*pageSize));
			map.put("endNum", (pageNum*pageSize));
			map.put("userId", userId);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			//返回的页码
			mapResult.put("page", pageNum);
			//查询当前用户下有多少错误的情景
			Long errorCount=tScenarinoDetailMapper.selectErrorCount(Long.parseLong(userId));
			mapResult.put("errorCount", errorCount);
			//所有的错误任务ID
			List<Long> errorMissionId=tScenarinoDetailMapper.selectMissionIdByErrorScenarino(Long.parseLong(userId));
			map.put("missionlist", errorMissionId);
			//查询全部写入返回结果集
			List<Map> list = this.tMissionDetailMapper.selectErrorMission(map);
			//循环结果集合
			for(Map ss:list){
				//获取到范围ID
				Long missionDomainId=Long.valueOf(ss.get("missionDomainId").toString());
				//获取任务ID
				Long missionId=Long.valueOf(ss.get("missionId").toString());
				//查询情景条件
				Map queryMap=new HashMap();
				queryMap.put("missionId", missionId);
				queryMap.put("userId", userId);
				//根据任务ID查询所有的情景
				List<Map> tmMap=tScenarinoDetailMapper.selectByMissionIdNew(queryMap);
				//记录当前有多少情景
				int s=0;
				//组装子集的数据
				List children=new ArrayList();
				Map qjTitle=new HashMap();
				qjTitle.put("id", missionId+"title");
				qjTitle.put("scenarinoNameTitle", "情景名称");
				qjTitle.put("scenarinoIdTitle","ID");
				qjTitle.put("operationTitle","操作");
				qjTitle.put("adminTitle","管理");
				qjTitle.put("scenarinoStatusTitle","情景状态");
				qjTitle.put("runTimeTitle","执行日期");
				qjTitle.put("EndDateTitle","结束日期");
				qjTitle.put("scenTypeTitle","类型");
				qjTitle.put("settingTitle","设置");
				int missionErrorCount=0;
				//加入数据
				children.add(qjTitle);
				for(int i=0;i<tmMap.size();i++){
					tmMap.get(i).put("id", "qj"+tmMap.get(i).get("scenarinoId"));
					//查询有多少个错误的情景  累计增加
					if(tmMap.get(i).get("expand4")==null||tmMap.get(i).get("expand4").equals("")){
						missionErrorCount++;
					}
					children.add(tmMap.get(i));
					//判断有多少个正在执行的情景
					if(tmMap.get(i).get("scenarinoStatus").equals(6)){
						s++;
					}
				}
				//记录当前任务下有多少个错误的情景
				ss.put("missionErrorCount",missionErrorCount);
				//写入情景信息
				ss.put("children", children);
				//写入ID
				ss.put("id", "rw"+ss.get("missionId"));
				//写入当前任务有多少情景
				ss.put("scnum", tmMap.size());
				//写入当前任务有多少正在执行的情景
				ss.put("zxscnum", s);
				//根据domain查询domain信息
				TDomainMission tDomainMission=tDomainMissionMapper.selectByPrimaryKey(missionDomainId);
				//写入domain名称
				ss.put("domainName", tDomainMission.getDomainName());
				ss.put("state", "closed");
			}
			mapResult.put("total", errorMissionId.size());
			mapResult.put("rows",list);
			
			LogUtil.getLogger().info("MissionAndScenarinoController 任务查询成功");
			//返回结果
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			LogUtil.getLogger().error("MissionAndScenarinoController 任务查询方法异常",e);
			//返回错误信息
			return AmpcResult.build(1001, "系统异常",null);
		}
	}
	
}
