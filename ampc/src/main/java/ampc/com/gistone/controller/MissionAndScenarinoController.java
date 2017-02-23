package ampc.com.gistone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.util.AmpcResult;

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
	 * @param request
	 * @param response
	 * @return 返回响应结果对象
	 * @throws Exception
	 */
	@RequestMapping("mission/get_mission_list")
	public AmpcResult get_Mission_list(HttpServletRequest request,HttpServletResponse response) throws Exception{
	    //添加异常捕捉
		try {
			//任务名称
			String missionName=request.getParameter("missionName");
			//当前页码
			Integer pageNum=Integer.valueOf(request.getParameter("pageNum"));
			//每页展示的条数
			Integer pageSize=Integer.valueOf(request.getParameter("pageSize"));
			//列表排序  暂时内定按照任务ID逆序排序
			String sort=request.getParameter("sort");
			//用户的id  暂时没用
			Integer userId=Integer.valueOf(request.getParameter("userId"));
			
			/*Integer pageNum=1;
			Integer pageSize=2;
			String missionName="4";*/
			//添加分页信息到参数中
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("pageNum", (pageNum-1)*pageSize);
			map.put("pageSize", pageNum*pageSize);
			//新建返回结果的Map
			Map<String,Object> mapResult=new HashMap<String,Object>();
			mapResult.put("page", pageNum);
			//判断是否有任务名称,如果有则调用根据任务名模糊查询
			if(null!=missionName&&!missionName.equals("")){
				String name="%"+missionName+"%";
				map.put("missionName",name);
				List<Map> list = this.tMissionDetailMapper.selectByMissionName(map);
				mapResult.put("total", this.tMissionDetailMapper.selectByMissionNameCount(name));
				mapResult.put("rows",list);
			}else{
				//查询全部
				List<Map> list = this.tMissionDetailMapper.selectAll(map);
				mapResult.put("total", this.tMissionDetailMapper.selectCount());
			}
			return AmpcResult.ok(mapResult);
		} catch (Exception e) {
			e.printStackTrace();
			//返回错误信息
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
}
