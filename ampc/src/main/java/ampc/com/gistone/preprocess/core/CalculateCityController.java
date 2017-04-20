package ampc.com.gistone.preprocess.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import ampc.com.gistone.util.AmpcResult;

@RestController
@RequestMapping(value = "/calc")
public class CalculateCityController {

private static final Logger logger  = LoggerFactory.getLogger(CalculateCityController.class);
	
	@Autowired
	private CalculateCityService calculateCityService;

	@RequestMapping(value = "/processData", method = RequestMethod.POST)
	public AmpcResult processData(@RequestBody Map<String, Object> requestData,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = (Map<String,Object>) requestData.get("data");
		double xorig = (Double)data.get("xorig");
		double yorig = (Double)data.get("yorig");
		double xcell =(Double)data.get("xcell"); 
		double ycell = (Double)data.get("ycell"); 
		int nrows = (int)data.get("nrows"); 
		int ncols = (int)data.get("ncols"); 
		double p_alp = (Double)data.get("p_alp");
		double p_bet = (Double)data.get("p_bet");
		double xcent = (Double)data.get("xcent");
		double ycent = (Double)data.get("ycent");
		try {
			String res = calculateCityService.getCityResult(xorig, yorig, xcell, ycell, nrows, ncols, p_alp, p_bet, xcent, ycent);
			return AmpcResult.ok(res);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return AmpcResult.build(-1, "Json转换错误");
		}
		
		
	}
}
