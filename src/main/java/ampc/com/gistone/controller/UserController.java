package ampc.com.gistone.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

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
			LogUtil.getLogger().error("UserController 查询用户列表信息异常!",e);
			return AmpcResult.build(1000,"查询用户列表信息异常!");
		}
	}
	
	/**
	 * 用户登录
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
			Integer isExist=tUserMapper.checkUserId(userId);
			if(isExist>0){
				Integer isOn=tUserMapper.checkUserIsON(userId);
				if(isOn>0){
					Map map=new HashMap();
					map.put("userId", userId);
					map.put("passWord", passWord);
					//查询所有的用户基本信息
					Map userMap=tUserMapper.login(map);
					if(userMap!=null){
						HttpSession session = request.getSession();
						session.setAttribute("user", userMap);
						LogUtil.getLogger().info("UserController  登录成功！");
						return AmpcResult.ok("用户登录成功!");
					}else{
						LogUtil.getLogger().error("UserController  用户和密码不匹配!");
						return AmpcResult.build(1000, "用户和密码不匹配!");
					}
				}else{
					LogUtil.getLogger().error("UserController 该用户已失效！");
					return AmpcResult.build(1000, "该用户已失效！");
				}
			}else{
				LogUtil.getLogger().error("UserController 用户名不存在！");
				return AmpcResult.build(1000, "用户名不存在！");
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("UserController 用户登录异常！",e);
			return AmpcResult.build(1000,"用户登录异常！");
		}
	}
	
	
	/**
	 * 取session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("user/get_sessionInfo")
	public AmpcResult get_sessionInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
		try{
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			HttpSession session = request.getSession();
			//验证session不为空
			if(session.getAttribute("userInfo")!=null){
				Map<String,String> userInfo = (Map)session.getAttribute("Login_map");//用户信息，包括角色
				LogUtil.getLogger().info("UserController  获取Session成功！");
				return AmpcResult.ok(userInfo);
			}else{
				LogUtil.getLogger().error("UserController  没有Session信息!");
				return AmpcResult.build(1000,"没有Session信息!");
			}
		}catch(Exception e){
			LogUtil.getLogger().error("UserController 获取Session异常！",e);
			return AmpcResult.build(1000,"获取Session异常!");
		}
		
	}
	
	
	/**
	 * 销毁session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("user/loginOut")
	public AmpcResult loginOut(HttpServletRequest request,HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		try{
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			session.invalidate();
			LogUtil.getLogger().info("UserController 用户退出成功！");
			return AmpcResult.ok(1);
		}catch (Exception e){
			LogUtil.getLogger().error("UserController 用户退出异常！",e);
			return AmpcResult.build(1000,"UserController 用户退出异常！");
		}
	}
	
	
	
	/**
	 * 获取验证码图片
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("user/yzmimg")
	@ResponseBody
	public void execute(HttpServletRequest request , HttpServletResponse response) throws Exception {  
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			LogUtil.getLogger().info("UserController  获取验证码成功！");
			CaptchaUtil.outputCaptcha(request, response);
		} catch (Exception e) {
			LogUtil.getLogger().error("UserController 获取验证码异常！",e);
		}
		
    }  
	
	/**
	 * 判断验证码的方法
	 * @param requestDate
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("user/checkyzm")
	public AmpcResult checkyzm(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request , HttpServletResponse response) throws Exception {  
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			String zhi= data.get("zhi").toString();
			HttpSession session = request.getSession();//取session
			String randomString=session.getAttribute("randomString").toString();
			zhi = zhi.toUpperCase();
			if(zhi.equals(randomString)){
				LogUtil.getLogger().info("UserController  验证码验证成功！");
				return AmpcResult.ok(1);
			}else{
				LogUtil.getLogger().error("UserController  验证码验证失败！");
				return AmpcResult.build(1000,"验证码验证失败！",0);
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("UserController 验证码验证异常！",e);
			return AmpcResult.build(1000,"验证码验证异常！");
		}
		
    }  
}
