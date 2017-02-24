package ampc.com.gistone.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;





import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.model.TMissionDetail;
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
			//任务名称
			String missionName=data.get("missionName").toString();
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
			//判断是否有任务名称,如果有则调用根据任务名模糊查询
			if(null!=missionName&&!missionName.equals("")){
				String name="%"+missionName+"%";
				map.put("missionName",name);
			}else{
				map.put("missionName",null);
			}
			//查询全部
			List<Map> list = this.tMissionDetailMapper.selectAllOrByMissionName(map);
			mapResult.put("total", this.tMissionDetailMapper.selectCountOrByMissionName(map));
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
			//创建类型 (1.只创建任务 2.创建任务并执行基准情景)
			Integer createType=Integer.valueOf(data.get("createType").toString());
			//执行添加操作
			int result=this.tMissionDetailMapper.insertSelective(mission);
			if(result>0){
				if(createType==2){
					/**
					 * To_Do 更改基准情景的执行状态
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
			//执行添加操作
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
	@Transactional
	@RequestMapping("mission/delete_mission")
	public AmpcResult delete_Mission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	    /**
	     * TODO 等待时段删除完成 直接添加
	     */
		//添加异常捕捉
		try {
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//要删除的任务集合
			//String missionIds=data.get("missionIds").toString();
			//用户的id  确定当前用户
			
			String missionIds="(24,25)";
			System.out.println(missionIds);
			
			
			Integer userId=Integer.valueOf(data.get("userId").toString());
			String[] idss=missionIds.split(",");
			int result=this.tMissionDetailMapper.updateIsEffeByIds("("+missionIds+")");
			
			
			//执行添加操作
			//int result=this.tMissionDetailMapper.updateByPrimaryKeySelective(mission);
			//return result>0?AmpcResult.ok(result):AmpcResult.build(1000, "任务修改失败",null);
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
}
