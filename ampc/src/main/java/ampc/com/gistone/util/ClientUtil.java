package ampc.com.gistone.util;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Client帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月24日
 */
public class ClientUtil {

	/**
	 * 设置编码和跨域
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	public static void SetCharsetAndHeader(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		//设置跨域
		response.setHeader("Access-Control-Allow-Origin", "*");
	}
}
