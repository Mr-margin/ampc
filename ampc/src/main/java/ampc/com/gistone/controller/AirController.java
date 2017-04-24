package ampc.com.gistone.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;

@RestController
@RequestMapping
public class AirController {
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@RequestMapping("/Air/get_time")
	public AmpcResult get_time(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ) throws NoSuchAlgorithmException, IOException {
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TMissionDetail tm=tMissionDetailMapper.selectMaxMission();
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setMissionId(tm.getMissionId());
			TScenarinoDetail maxtm=tScenarinoDetailMapper.selectByrealmax(tm.getMissionId());
			TScenarinoDetail mintm=tScenarinoDetailMapper.selectByrealmin(tm.getMissionId());
			JSONObject obj=new JSONObject();
			obj.put("mintime", mintm.getPathDate().getTime());
			obj.put("maxtime", maxtm.getPathDate().getTime());
			LogUtil.getLogger().error("空气质量预报时间查询成功");
			return AmpcResult.build(0, "success",obj);
		}catch(Exception e){
			LogUtil.getLogger().error("异常了",e);
			return AmpcResult.build(0, "执行失败");
		}
	}
}
