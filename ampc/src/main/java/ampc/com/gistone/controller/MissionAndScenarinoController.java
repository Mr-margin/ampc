package ampc.com.gistone.controller;

import java.util.List;

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
		//任务名称
		//String missionName=request.getParameter("missionName");
		//当前页码
		//Integer pageNum=Integer.valueOf(request.getParameter("pageNum"));
		//每页展示的条数
		//Integer pageSize=Integer.valueOf(request.getParameter("pageSize"));
		//列表排序  暂时内定按照任务ID逆序排序
		//String sort=request.getParameter("sort");
		//用户的id  暂时没用
		//Integer userId=Integer.valueOf(request.getParameter("userId"));
		
		
//		TMissionDetail t=tMissionDetailMapper.selectByPrimaryKey(i);
		
		TMissionDetail tt = this.tMissionDetailMapper.selectByPrimaryKey(new Long(1));
		
		List<TMissionDetail> list = this.tMissionDetailMapper.selectByPrimaryAll();
		System.out.println(list.size());
		
		return AmpcResult.ok(tt);
	}
}
