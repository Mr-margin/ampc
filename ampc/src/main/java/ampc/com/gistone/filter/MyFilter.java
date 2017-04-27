package ampc.com.gistone.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 使用注解标注过滤器
 * @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器
 * 属性filterName声明过滤器的名称,可选
 * 属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性)
 */
@WebFilter(filterName="myFilter",urlPatterns="/*")
public class MyFilter implements Filter {
	
	@Override
	public void destroy() {
//        System.out.println("过滤器销毁");
    }
	
	

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest hrequest = (HttpServletRequest) request;
        HttpServletResponse hresponse = (HttpServletResponse) response;
        
        String url = hrequest.getServletPath();
     // 表明它允许"http://xxx"发起跨域请求
        hresponse.setHeader("Access-Control-Allow-Origin","*");
        // 表明在xxx秒内，不需要再发送预检验请求，可以缓存该结果
        hresponse.setHeader("Access-Control-Allow-Methods","POST, GET, DELETE, PUT");
        // 表明它允许xxx的外域请求
        hresponse.setHeader("Access-Control-Max-Age","3628800");
        // 表明它允许跨域请求包含xxx头
        hresponse.setHeader("Access-Control-Allow-Headers","x-requested-with");
        chain.doFilter(request, response);
//        Map special  = new HashMap();
//        special.put("getLogin_massage.do", "getLogin_massage.do");//session获取用户登陆信息
//        special.put("loginin.do", "loginin.do");//登录验证
//        
//        
//        if(!url.endsWith(OverallSituation.index)){//排除登录界面
//        	if(url.endsWith(OverallSituation.page)||url.endsWith(OverallSituation.servlet)){//只有页面和后台需要处理
//        		
//        		System.out.println(hrequest.getServletPath());
//        		
//        		for (Object key : special.keySet()) {//循环所有的特例
//        			if(!url.endsWith(special.get(key).toString())){//是否是特例需要处理
//        				
//        				HttpSession session = hrequest.getSession();
//        				if(session.getAttribute("Login_map")!=null){//验证session不为空
//        					System.out.println("登录，通过");
//        					chain.doFilter(request, response);
//        				}else{
//        					System.out.println("未登录，过滤");
//        					hresponse.sendRedirect("/"+OverallSituation.index);
//        				}
//        				
//        			}else{
//        				System.out.println("特例，通过");
//        				chain.doFilter(request, response);
//        			}
//        		}
//        		
//        	}else{
//        		chain.doFilter(request, response);
//        	}
//        }else{
//        	chain.doFilter(request, response);
//        }
        
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
//        System.out.println("过滤器初始化");
    }
    
}
