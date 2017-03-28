package ampc.com.gistone.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

/**
 * 清单控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月28日
 */
@RestController
@RequestMapping
public class NativeAndNationController {

	@Autowired
	public TEsNativeMapper tEsNativeMapper;
	/**
	 * 查询当前用户下的清单
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/esnative/get_nativeInfo")
	public AmpcResult get_nativeInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户id
			Long userId = Long.parseLong(data.get("userId").toString());
			List<Map> list=tEsNativeMapper.selectAll(userId);
			return AmpcResult.ok(list);
		} catch (Exception e) {
			System.out.println(e);
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
}
