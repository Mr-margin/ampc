package ampc.com.gistone.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TTimeMapper;
import ampc.com.gistone.database.model.TTime;
import ampc.com.gistone.util.AmpcResult;


@RestController
@RequestMapping
public class AreaAndTimeController {
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	@Autowired
	private TTimeMapper tTimeMapper;
	
	/**
	 * 在原有基础上添加时段
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/time/time_save")
	public void add_TIME(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		Long areaId =Long.parseLong(request.getParameter("areaId"));//区域ID
		Long scenarinoId =Long.parseLong(request.getParameter("scenarinoId"));//情景id
		Long missionId =Long.parseLong(request.getParameter("missionId"));//任务id
		Long userId =1l; //Long.parseLong(request.getParameter("userId"));//用户id
		Long selectTimeId =1l; //Long.parseLong(request.getParameter("selectTimeId"));//添加时段处在的时段id
		Date timeDate = new Date();//new Date(request.getParameter("addTimeDate"));//新增时段时间

		// 时间操作，结束时间与开始时间的数据有一位数间隔，需要时间计算
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeDate);
		cal.set(Calendar.HOUR, Calendar.HOUR - 1);
		String addTimeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		Date timeEndDate = new Date();
	
		TTime tTime=new TTime();
			//查询添加时段处的结束时间
			tTime=tTimeMapper.selectByPrimaryKey(selectTimeId);
			//查询时段表当前最大id，并且+1当做所添加时段的id
			String maxid="select max(TIME_ID) from T_TIME";
			Long max=(long) this.getBySqlMapper.findrows(maxid);
			max+=1;
			//添加一个新的时段
			TTime add_tTime=new TTime();
			add_tTime.setAreaId(areaId);
			add_tTime.setDeleteTime(tTime.getTimeEndDate());
			add_tTime.setUserId(userId);
			add_tTime.setTimeId(max);
			add_tTime.setMissionId(missionId);
			add_tTime.setScenarinoId(scenarinoId);
			add_tTime.setTimeStartDate(timeDate);
			add_tTime.setTimeEndDate(tTime.getTimeEndDate());
			int insert_start=tTimeMapper.insertSelective(add_tTime);//insertSelective(add_tTime);
			//修改原有时段
		    TTime update_tTime=new TTime();
			update_tTime.setTimeId(selectTimeId);
			update_tTime.setTimeEndDate(timeEndDate);
			int update_start=tTimeMapper.updateByPrimaryKeySelective(update_tTime);
			//判断数据库操作是否成功，并添加对应数据
			JSONObject obj = new JSONObject();
			Integer start=0;
			String msg="";
			if(insert_start!=0||update_start!=0){
				obj.put("timeId", max);
				start=0;
				msg="save_time success";
			}else{
				start=1;
				msg="save_time error";
			}
			//返回json数据
		response.getWriter().write(AmpcResult.build(start,msg, obj).toString());
	}
}
