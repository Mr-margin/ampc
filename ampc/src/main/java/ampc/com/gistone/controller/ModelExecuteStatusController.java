/**  
 * @Title: ModelExecuteStutas.java
 * @Package ampc.com.gistone.controller
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月28日 上午9:46:23
 * @version 
 */
package ampc.com.gistone.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;

/**  
 * @Title: ModelExecuteStutas.java
 * @Package ampc.com.gistone.controller
 * @Description: 模式执行的状态的controller  返回给前端的tasks任务运行状态的接口
 * @author yanglei
 * @date 2017年3月28日 上午9:46:23
 * @version 1.0
 */

@RestController
@RequestMapping
public class ModelExecuteStatusController {
	
	@Autowired
	private TTasksStatusMapper tasksStatusMapper;
	
	/**
	 * 
	 * @Description: TODO
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return   
	 * AmpcResult  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午9:49:03
	 */
	@RequestMapping("/ModelExecuteStatus")
	public AmpcResult getModelexecuteStatus(@RequestBody Map<String,Object> requestDate,HttpServletRequest request,HttpServletResponse response){
		
		String token = (String) requestDate.get("token");
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			
			//用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			System.out.println(userId);
			//情景ID
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			System.out.println(scenarinoId);
			Date endDate  = tasksStatusMapper.selectByscenarinoId(scenarinoId);
			String endtime = DateUtil.DATEtoString(endDate, "yyyyMMdd");
			System.out.println(endtime+"..........");
			//确定是逐日执行还是逐模块执行
			//查找stepindex和taskendtime
			//
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}

}
