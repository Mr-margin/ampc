package ampc.com.gistone.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;

@RestController
@RequestMapping
public class AnalysisController {
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
//	
//	@Autowired
//	private TPlanMapper tPlanMapper;
	
	@RequestMapping("/Analysis_s/getLogin_Controller")
	public void getLogin_Controler(HttpServletRequest request, HttpServletResponse response ) throws NoSuchAlgorithmException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		
		String user_sql = "SELECT * FROM  POLIST_USER WHERE USER_NAME='1212' AND STATE ='1'";
		
		List<Map> list = this.getBySqlMapper.findRecords(user_sql);
		
		
		response.getWriter().write("");
		
	}
	
	
}
