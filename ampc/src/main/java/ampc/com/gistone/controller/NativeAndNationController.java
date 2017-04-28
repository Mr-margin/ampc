package ampc.com.gistone.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TEsCouplingMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

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
	public TEsCouplingMapper tEsCouplingMapper;
	/**
	 * 查询当前用户下的清单
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/escoupling/get_couplingInfo")
	public AmpcResult get_nativeInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			List<Map> list=tEsCouplingMapper.selectAll(userId);
			LogUtil.getLogger().info("NativeAndNationController 查询当前用户下的耦合清单信息成功!");
			return AmpcResult.ok(list);
		} catch (Exception e) {
			LogUtil.getLogger().error("NativeAndNationController 查询当前用户下的耦合清单信息异常!",e);
			return AmpcResult.build(1001, "NativeAndNationController 查询当前用户下的耦合清单信息异常!");
		}
	}
}
