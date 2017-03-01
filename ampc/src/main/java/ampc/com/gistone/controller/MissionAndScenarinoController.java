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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoAreaMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

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
	
	private AreaAndTimeController atc=new AreaAndTimeController();
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
			mission.setMissionStartDate(new Date(data.get("missionStartDate").toString()));
			//任务的结束时间
			mission.setMissionEndDate(new Date(data.get("missionEndDate").toString()));
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			//默认新建任务会赋值预评估
			mission.setMissionStatus("预评估");
			//创建类型 (1.只创建任务 2.创建任务并执行基准情景)
			Integer createType=Integer.valueOf(data.get("createType").toString());
			//执行添加操作
			int result=this.tMissionDetailMapper.insertSelective(mission);
			//判断添加结果
			if(result>0){
				//判断添加类型
				if(createType==2){
					/**
					 * TODO 更改基准情景的执行状态
					 */
					
					
					
					return AmpcResult.ok(result);
				}else{
					//只创建任务
					return AmpcResult.ok(result);
				}
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
			//执行区域删除方法
			int result=this.tScenarinoAreaMapper.updateIsEffeByIds(areaIdss);
			//判断是否执行成功
			if(result>0){
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
				return AmpcResult.build(1000, "区域修改失败",null);
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
	 * 情景创建方法
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@Transactional
	@RequestMapping("scenarino/save_scenarino")
	public AmpcResult save_Scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
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
			Integer scenarinoId=Integer.valueOf(data.get("scenarinoId").toString());
			//判断是否是复用情景 和创建类型
			if(null!=scenarinoId&&!scenarinoId.equals("")&&createType==1){
				/**
				 * TODO 复制情景
				 */
				
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
			//执行区域删除方法
			int result=this.tScenarinoAreaMapper.updateIsEffeByIds(areaIdss);
			//判断是否执行成功
			if(result>0){
				//执行情景删除方法
				result=this.tScenarinoDetailMapper.updateIsEffeByIds(scenarinoIdss);
				return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "情景修改失败",null);
			}else{
				return AmpcResult.build(1000, "区域修改失败",null);
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
}
