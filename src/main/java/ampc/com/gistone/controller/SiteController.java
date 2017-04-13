package ampc.com.gistone.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TSite;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

@RestController
@RequestMapping
public class SiteController {
	
	@Autowired
	private TSiteMapper tSiteMapper;
	
	@RequestMapping("/Site/find_Site")
	public AmpcResult find_Site(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			String siteCode =data.get("siteCode").toString();
			List<TSite> siteList=tSiteMapper.selectSiteCode(siteCode);
			JSONArray arr=new JSONArray();
			for(TSite site:siteList){
				if(site.getCityName()!=null&&site.getStationName()!=null&&site.getLat()!=null&&site.getLon()!=null&&site.getStationId()!=null){
				JSONObject obj=new JSONObject();
				obj.put("cityName", site.getCityName());
				obj.put("stationName", site.getStationName());
				obj.put("Lat", site.getLat());
				obj.put("Lon", site.getLon());
				obj.put("stationId", site.getStationId());
				arr.add(obj);
				}
			}
			return AmpcResult.build(0, "success",arr);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "执行失败");
		}
	}
}
