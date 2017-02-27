package ampc.com.gistone.controller;

import java.util.ArrayList;
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
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
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

	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
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
			if(null!=missionStatus&&!missionStatus.equals("")){
				map.put("missionStatus",missionStatus);
			}else{
				map.put("missionStatus",null);
			}
			//查询全部
			List<Map> list = this.tMissionDetailMapper.selectAllOrByQueryName(map);
			mapResult.put("total", this.tMissionDetailMapper.selectCountOrByQueryName(map));
			mapResult.put("rows",list);
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
			System.out.println(data.get("missionStartDate").toString());
			//任务的开始时间
			mission.setMissionStartDate(new Date(data.get("missionStartDate").toString()));
			//任务的结束时间
			mission.setMissionEndDate(new Date(data.get("missionEndDate").toString()));
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			mission.setMissionStatus("预评估");
			//创建类型 (1.只创建任务 2.创建任务并执行基准情景)
			Integer createType=Integer.valueOf(data.get("createType").toString());
			//执行添加操作
			int result=this.tMissionDetailMapper.insertSelective(mission);
			System.out.println(mission.getMissionId());
			if(result>0){
				if(createType==2){
					/**
					 * TODO 更改基准情景的执行状态
					 */
					
					
					
					return AmpcResult.ok(result);
				}else{
					return AmpcResult.ok(result);
				}
			}
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
			TMissionDetail mission=new TMissionDetail();
			//任务名称
			mission.setMissionName(data.get("missionName").toString());
			//用户的id  确定当前用户
			mission.setUserId(Long.parseLong(data.get("userId").toString()));
			//任务ID 
			mission.setMissionId(Long.parseLong(data.get("missionId").toString()));
			//执行修改操作
			int result=this.tMissionDetailMapper.updateByPrimaryKeySelective(mission);
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
			//进行批量删除
			int result=this.tMissionDetailMapper.updateIsEffeByIds(list);
			 /**
		     * TODO 等待时段删除完成 直接添加
		     *      删除涉及到多次数据库操作，在事务的一致性有待提高
		     */
			
			
			
			return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
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
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionName", missionName);
			map.put("userId", userId);
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
			//查询全部
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
			if(null!=missionStatus&&!missionStatus.equals("")){
				map.put("missionStatus",missionStatus);
			}else{
				map.put("missionStatus",null);
			}
			//查询全部
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
			//创建类型 1.只创建情景 2.创建并编辑情景  3.复制情景
			Integer createType=Integer.valueOf(data.get("createType").toString());
			int result=this.tScenarinoDetailMapper.insertSelective(scenarino);
			if(createType==1&&result>0){
				return AmpcResult.ok(result);
			}else{
				Map map=new HashMap();
				map.put("missionId", scenarino.getMissionId());
				map.put("scrnarinoName", scenarino.getScenarinoName());
				Integer sid=this.tScenarinoDetailMapper.selectByMidAndSName(map);
				if(createType==2){
					//直接返回新建的情景ID
					return AmpcResult.ok(sid);
				}else{
					/**
					 * TODO 
					 * 需要复制信息 状态  以及更改时段时间信息等
					 */
				}
			}
			return AmpcResult.build(1000, "添加失败",null);
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
			TScenarinoDetail scenarino=new TScenarinoDetail();
			//情景名称
			scenarino.setScenarinoName(data.get("scenarinoName").toString());
			//用户的id  确定当前用户
			scenarino.setUserId(Long.parseLong(data.get("userId").toString()));
			//情景ID 
			scenarino.setScenarinoId(Long.parseLong(data.get("scenarinoId").toString()));
			//执行状态 
			long state=Long.parseLong(data.get("state").toString());
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
			List<Integer> list=new ArrayList<Integer>();
			for(int i=0;i<idss.length;i++){
				list.add(Integer.valueOf(idss[i]));
			}
			//进行批量删除
			int result=this.tScenarinoDetailMapper.updateIsEffeByIds(list);
			 /**
		     * TODO 等待时段删除完成 直接添加
		     *      删除涉及到多次数据库操作，在事务的一致性有待提高
		     */
			
			
			
			return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
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
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("missionId", missionId);
			map.put("scenarinoName", scenarinoName);
			map.put("userId", userId);
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
