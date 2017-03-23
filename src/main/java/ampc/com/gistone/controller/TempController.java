package ampc.com.gistone.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;


/**
 * 中间数据
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月23日
 */
@RestController
@RequestMapping
public class TempController {

	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	/**
	 * 查询情景的减排信息
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/scenarino/get_radioInfo")
	public AmpcResult get_radioInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			// 情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			// 行政区划代码
			String code = data.get("code").toString();;
			// 行政区划等级
			Long addressLevle=Long.parseLong(data.get("addressLevle").toString());
			if(addressLevle==1) code=code.substring(0,2)+"%";
			if(addressLevle==2) code=code.substring(0,4)+"%";
			
			TScenarinoDetail tsd=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);		
			
			return AmpcResult.build(1000, "添加失败");
		} catch (Exception e) {
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
}
