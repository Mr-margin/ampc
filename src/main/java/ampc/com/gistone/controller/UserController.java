package ampc.com.gistone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TUserMapper;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.CaptchaUtil;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;

/**
 * 用户控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年4月13日
 */
@RestController
@RequestMapping
public class UserController {
	// 用户表映射
	@Autowired
	public TUserMapper tUserMapper;
	
	/**
	 * 用户列表查询
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/user/get_userList")
	public AmpcResult get_userList(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			
			//查询所有的用户基本信息
			List<Map> list=tUserMapper.selectUserList();
			LogUtil.getLogger().info("UserController  查询用户列表信息成功!");
			return AmpcResult.ok(list);
		} catch (Exception e) {
			LogUtil.getLogger().info("UserController 查询用户列表信息异常!",e);
			return AmpcResult.build(1000,"查询用户列表信息异常!");
		}
	}
	
	/**
	 * 用户列表查询
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/user/login")
	public AmpcResult login(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//用户Id
			long userId=Long.parseLong(data.get("userId").toString());
			//密码
			String passWord=data.get("passWord").toString();
			Map map=new HashMap();
			map.put("userId", userId);
			map.put("passWord", passWord);
			//查询所有的用户基本信息
			Integer count=tUserMapper.login(map);
			if(count>0){
				return AmpcResult.ok();
			}else{
				return AmpcResult.build(1000, "用户不存在");
			}
		} catch (Exception e) {
			LogUtil.getLogger().info("UserController 用户登录异常！",e);
			return AmpcResult.build(1000,"用户登录异常！");
		}
	}
	
	
	//获取验证码的方法
	@RequestMapping("img.do")
	@ResponseBody
	public void execute(HttpServletRequest request , HttpServletResponse response) throws Exception {  
		CaptchaUtil.outputCaptcha(request, response);
    }  
	
	@RequestMapping("yzm.do")
	public void yzm(HttpServletRequest request , HttpServletResponse response) throws Exception {  
		String zhi= request.getParameter("zhi");
		HttpSession session = request.getSession();//取session
		String randomString=session.getAttribute("randomString").toString();
		zhi = zhi.toUpperCase();
//			System.out.println(zhi+"----"+randomString);
		if(zhi.equals(randomString)){
			response.getWriter().write("1");
		}else{
			response.getWriter().write("0");
		}
    }  
}
