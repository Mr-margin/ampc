package ampc.com.gistone.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
	
	
	/**
	 * 执行Post请求 带参数
	 * @param url
	 * @param param
	 * @return
	 */
	public static String doPost(String url,String param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 执行http请求
			StringEntity entity = new StringEntity(param,"utf-8");//解决中文乱码问题    
            entity.setContentEncoding("UTF-8");    
            entity.setContentType("application/json");  
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	/**
	 * 执行Post请求 不带参数
	 * @param url
	 * @return
	 */
	public static String doPost(String url) {
		return doPost(url, null);
	}
	
}
