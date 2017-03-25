package ampc.com.gistone.controller;

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
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPlanMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.inter.TUserMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TPlan;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.database.model.TUser;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
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
			mapResult.put("total", this.tMissionDetailMapper.selectCountOrByQueryName(map));
			mapResult.put("rows",list);
			//返回结果
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//模拟范围id
			mission.setMissionDomainId(Long.parseLong(data.get("missionDomainId").toString()));
			//全国清单id
			mission.setEsCouplingId(Long.parseLong(data.get("esCouplingId").toString()));
			//任务的开始时间
			String startDate=data.get("missionStartDate").toString();
			String endDate=data.get("missionEndDate").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date missionStartDate=sdf.parse(startDate);
			Date missionEndDate=sdf.parse(endDate);
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
			return AmpcResult.build(1000, "添加失败",null);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			//任务ID 
			mission.setMissionId(Long.parseLong(data.get("missionId").toString()));
			//执行修改操作
			int result=this.tMissionDetailMapper.updateByPrimaryKeySelective(mission);
			//判断执行结果返回对应数据
			return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
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
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//添加信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionName", missionName);
			map.put("userId", userId);
			//执行判断操作
			int result=this.tMissionDetailMapper.check_MissioName(map);
			//返回true 表示可用  返回false 已存在
			return result==0?AmpcResult.ok(true):AmpcResult.build(1000, "名称已存在",false);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			long missionId=Long.parseLong(data.get("missionId").toString());
			//条件名称
			String queryName=data.get("queryName").toString();
			//列表排序  暂时内定按照任务ID逆序排序
			//String sort=data.get("sort").toString();
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
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
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	/**
	 * 情景复制查询方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
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
			List<Map> list = this.tScenarinoDetailMapper.selectAllOrByQueryName(map);
			mapResult.put("total", this.tScenarinoDetailMapper.selectCountOrByQueryName(map));
			mapResult.put("rows",list);
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			e.printStackTrace();
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
			//用户的id  确定当前用户
			scenarino.setUserId(Long.parseLong(data.get("userId").toString()));
			//情景ID 
			scenarino.setScenarinoId(Long.parseLong(data.get("scenarinoId").toString()));
			//执行状态 
			long state=Long.parseLong(data.get("state").toString());
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
				
				
				
				
				return AmpcResult.build(1000, "等待计算接口的实现",null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//将得到的数据拆分 放入集合中
			String[] idss=scenarinoIds.split(",");
			List<Long> scenarinoIdss=new ArrayList<Long>();
			for(int i=0;i<idss.length;i++){
				scenarinoIdss.add(Long.parseLong(idss[i]));
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
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
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
			//用户的id  确定当前用户
			Integer userId=Integer.valueOf(data.get("userId").toString());
			//任务Id
			long missionId=Long.parseLong(data.get("missionId").toString());
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
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	@RequestMapping("scenarino/find_scenarino_time")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_scenarino_time(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long missionId=Long.parseLong(data.get("missionId").toString());//任务id
			Long userId=Long.parseLong(data.get("userId").toString());//情景id
			//根据id查询任务
			TMissionDetail tMission=tMissionDetailMapper.selectByPrimaryKey(missionId);
			//根据任务id查询所有情景
			List<TScenarinoDetail> scenarlist=tScenarinoDetailMapper.selectAllByMissionId(missionId);
			//查询基准情景最大结束时间
			Date scenar=tScenarinoDetailMapper.selectMaxEndTime();
			//转换类型
			Date mission=tMission.getMissionStartDate();
			
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String times=formatter.format(scenar);
			String missiondate=formatter.format(mission);
			JSONArray arr=new JSONArray();
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
					obj.put("scenarinoEndDate", endtime);
					arr.add(obj);
				}	
			}
			//为基准情景保存数据
			JSONObject lastobj=new JSONObject();
			lastobj.put("scenarinoId", -1);
			lastobj.put("scenarinoName", "基准情景");
			lastobj.put("scenarinoStartDate", missiondate);
			lastobj.put("scenarinoEndDate", times);
			arr.add(lastobj);
			return AmpcResult.build(0, "find_scenarino_time success",arr);
		}catch(Exception e){
			e.printStackTrace();
		return AmpcResult.build(1000, "参数错误",null);
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
			Long userId=Long.parseLong(data.get("userId").toString());//情景id
			//查询用户信息
			TUser tUser=tUserMapper.selectByPrimaryKey(userId);
			Integer predictionTime=tUser.getPredictionTime();
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
			e.printStackTrace();
		return AmpcResult.build(1000, "参数错误",null);
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
			Long userId=Long.valueOf(data.get("userId").toString());//用户id
			String missionType=data.get("missionType").toString();//任务类型
			String scenarinoName=data.get("scenarinoName").toString();//情景名称
			String scenType=data.get("scenType").toString();//情景类型
			Date date=new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			String addTimeDate =sdf.format(cal.getTime());
			Date pathDate=sdf.parse(addTimeDate);
			Date enddate=null;
			Date startdate = null;
			
			Long sdid=0l;
			int d=0;
			int a=0;
			//预评估任务创建情景
			if(missionType.equals("1")){
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
				
				tsd.setBasisScenarinoId(basisScenarinoId);
				tsd.setBasisTime(basisTime);
				tsd.setScenarinoName(scenarinoName);
				tsd.setScenarinoStartDate(scenarinoStartDate);
				tsd.setScenarinoEndDate(scenarinoEndDate);
				tsd.setScenType(scenType);
				tsd.setMissionId(missionId);
				tsd.setPathDate(pathDate);
				tsd.setUserId(userId);
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
			if(missionType.equals("2")){
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
				tasks.setRangeDay(Long.valueOf(days+1));
				tasks.setScenarinoEndDate(enddate);
				tasks.setScenarinoStartDate(startdate);
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
				}
				}
				
				
			}
			if(d!=0){
				JSONObject obj=new JSONObject();
				obj.put("scenarinoId", sdid);
				return AmpcResult.build(0, "ok",obj);
			}

			 return AmpcResult.build(1000, "参数错误",null);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);				
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
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			JSONObject obj=new JSONObject();
			obj.put("scenarinoStatus", tScenarinoDetail.getScenarinoStatus());
		  return AmpcResult.build(0, "success",obj);	
		}catch(Exception e){
			e.printStackTrace();
			 return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	/**
	 * 
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
			Long scenarinoId=Long.parseLong(data.get("scenarinoId").toString());//情景id
			Long copyscenarinoId=Long.parseLong(data.get("copyscenarinoId").toString());//被复制情景id
			//被复制的情景
			TScenarinoDetail copytScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(copyscenarinoId);
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
			Date copyendtime=copytScenarinoDetail.getScenarinoEndDate();
			Date copystarttime=copytScenarinoDetail.getScenarinoStartDate();
			Long a=copyendtime.getTime()-copystarttime.getTime();
			float ha=a/1000f;//小时数
			//情景的开始结束时间
			Date endtime=tScenarinoDetail.getScenarinoEndDate();
			Date starttime=tScenarinoDetail.getScenarinoStartDate();
			Long b=endtime.getTime()-starttime.getTime();
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
			e.printStackTrace();
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
			TMissionDetail tMissionDetail=new TMissionDetail();
			tMissionDetail.setUserId(userId);
			List<TMissionDetail> missionlist=tMissionDetailMapper.selectByEntity(tMissionDetail);
			JSONArray arr=new JSONArray();
			if(!missionlist.isEmpty()){
			for(TMissionDetail mission:missionlist){
				JSONObject obj=new JSONObject();
				obj.put("missionId", mission.getMissionId());
				obj.put("missionName", mission.getMissionName());
				arr.add(obj);
			}
			}else{
				
				return AmpcResult.build(1000, "该用户没有创建任务",null);
			}
		return AmpcResult.build(0, "success",arr);	
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);	
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
			Long missionId=Long.parseLong(data.get("missionId").toString());
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setUserId(userId);;
			tScenarinoDetail.setMissionId(missionId);
			List<TScenarinoDetail> tScenarinoDetaillist=tScenarinoDetailMapper.selectByEntity(tScenarinoDetail);
			JSONArray arr=new JSONArray();
			if(!tScenarinoDetaillist.isEmpty()){
			for(TScenarinoDetail Scenarino:tScenarinoDetaillist){
				JSONObject obj=new JSONObject();
				obj.put("scenarinoId", Scenarino.getScenarinoId());
				obj.put("scenarinoName", Scenarino.getScenarinoName());
				obj.put("scenType", Scenarino.getScenType());
				obj.put("scenarinoStartDate", Scenarino.getScenarinoStartDate().getTime());
				obj.put("scenarinoEndDate", Scenarino.getScenarinoEndDate().getTime());
				arr.add(obj);
			}
			}else{
				
				return AmpcResult.build(1000, "该任务没有创建情景",null);
			}
		return AmpcResult.build(0, "success",arr);	
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	
}
