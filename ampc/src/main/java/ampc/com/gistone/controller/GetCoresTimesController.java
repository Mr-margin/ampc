/**  
 * @Title: GetCoresTimesController.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月5日 下午3:13:45
 * @version 
 */
package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TCoresTimesMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

/**  
 * @Title: GetCoresTimesController.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO 获取每一种计算核数对应的情景的 耗时预期
 * @author yanglei
 * @date 2017年4月5日 下午3:13:45
 * @version 1.0
 */

@RestController
@RequestMapping
public class GetCoresTimesController {
	//加载corestimes映射
	@Autowired
	private TCoresTimesMapper tCoresTimesMapper;
	//加载
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@RequestMapping("/getCores/spentTimes")
	public AmpcResult getCoresTimes(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		String token = (String) requestDate.get("token");
		try {
			//获取数据包
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			//情景类型
			Integer scenarinoType = Integer.parseInt(data.get("scenarinoType").toString());
			//任务ID
			Long missionId = Long.parseLong(data.get("missionId").toString());
			//任务类型
			Integer missionType  = Integer.parseInt(data.get("missionType").toString());
			
			//通过任务ID获取domainid
			Long domainId = tMissionDetailMapper.selectDomainid(missionId);
			
			Map map = new HashMap();
			map.put("userId", userId);
			map.put("coresScenarinoType", scenarinoType);
			map.put("coresDomainId", domainId);
			map.put("coresMissionType", missionType);
			//获取对用用户对应domain下的计算核数的平均时间
			ArrayList<Map> list = tCoresTimesMapper.getCoreList(map);
			return AmpcResult.ok(list);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return AmpcResult.build(1000, "获取失败");
		}
	}

}
