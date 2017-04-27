package ampc.com.gistone.controller;

import java.util.ArrayList;
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

import ampc.com.gistone.database.inter.TAddressMapper;
import ampc.com.gistone.database.inter.TDomainMissionMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TAddress;
import ampc.com.gistone.database.model.TDomainMission;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TSite;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

@RestController
@RequestMapping
public class SiteController {
	
	@Autowired
	private TSiteMapper tSiteMapper;
	
	
	@Autowired
	private TAddressMapper tAddressMapper;
	
	
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	
	@Autowired
	private TDomainMissionMapper tDomainMissionMapper;
	/**
	 * 站点查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Site/find_Site")
	public AmpcResult find_Site(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			String siteCode =data.get("siteCode").toString();
			if(!RegUtil.CheckParameter(siteCode, "String", null, false)){
				LogUtil.getLogger().error("find_Site  省市code为空!");
				return AmpcResult.build(1003, "省市code为空!");
			}
			List<TSite> siteList=tSiteMapper.selectSiteCode(siteCode+"00");
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
			return AmpcResult.ok(arr);
		}catch(Exception e){
			LogUtil.getLogger().error("站点查询失败",e);
			return AmpcResult.build(1001, "站点查询失败");
		}
	}
	
	@RequestMapping("/Site/find_code")
	public AmpcResult find_code(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			String[] strarr={"110100","120100","130100","130200","130300","130400","130500","130600","130700","130800","130900","131000","131100"};
			JSONObject arr=new JSONObject();
			JSONObject sarr=new JSONObject();
			List<String> list=new ArrayList();
			for(String code:strarr){
				String twocode=code.substring(0, 2);
				String addcode=twocode+"0000";
				if(!list.contains(addcode)){
				list.add(addcode);
				}
			}
			
			for(String code:list){
				
				TAddress tAddres=new TAddress();
				tAddres.setAddressCode(Integer.valueOf(code));
				List<TAddress> tAddr=tAddressMapper.selectBLevel(tAddres);
				TAddress ta=tAddr.get(0);
				JSONObject objs=new JSONObject();
				JSONObject obs=new JSONObject();
				objs.put("code", ta.getAddressCode());
				objs.put("name", ta.getAddressName());
				
				for(String codes:strarr){
					String twocode=codes.substring(0, 2);
					String addcode=twocode+"0000";
					JSONObject tses=new JSONObject();
					if(code.equals(addcode)){
						TAddress tAddress=new TAddress();
						tAddress.setAddressCode(Integer.valueOf(codes));
						List<TAddress> tAddre=tAddressMapper.selectBLevel(tAddress);
						TAddress tAdd =tAddre.get(0);
						tses.put("code", tAdd.getAddressCode());
						tses.put("name", tAdd.getAddressName());
						obs.put(tAdd.getAddressCode(), tses);
						objs.put("station", obs);
					}
					
				}
				sarr.put(code, objs);
			}
			
			return AmpcResult.build(0, "success",sarr);
		}catch(Exception e){
			LogUtil.getLogger().error("异常了",e);
			return AmpcResult.build(1001, "执行失败");
		}
	}
	
	
	/**
	 * code查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Site/find_codes")
	public AmpcResult find_codes(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			if(null!=data.get("MissionId")&&data.get("MissionId")!=""){
				Long MissionId=Long.valueOf(data.get("MissionId").toString());
				TMissionDetail tMissionDetail=new TMissionDetail();
				tMissionDetail.setMissionId(MissionId);
				List<TMissionDetail>  tmlist=tMissionDetailMapper.selectByEntity(tMissionDetail);
				TMissionDetail tm=tmlist.get(0);
				
				TDomainMissionWithBLOBs tDomainMission=tDomainMissionMapper.selectByPrimaryKey(tm.getMissionDomainId());
				String str=tDomainMission.getDomainCode();
				JSONObject obj=JSONObject.fromObject(str);
				LogUtil.getLogger().info("站点查询成功");
				return AmpcResult.ok(obj);
			}else{
				if(null==tMissionDetailMapper.selectMaxMission()){
					LogUtil.getLogger().error("站点查询失败");
					return AmpcResult.build(1004, "无可用实时预报任务");
					
				}
				TMissionDetail tm=tMissionDetailMapper.selectMaxMission();
				TDomainMissionWithBLOBs tDomainMission=tDomainMissionMapper.selectByPrimaryKey(tm.getMissionDomainId());
				if(tDomainMission==null||tDomainMission.equals("")){
					LogUtil.getLogger().error("find_codes  domain未查询到!");
					return AmpcResult.build(1003, "domain未查询到!");
					
				}
				String str=tDomainMission.getDomainCode();
				JSONObject obj=JSONObject.fromObject(str);
				LogUtil.getLogger().info("站点查询成功");
				return AmpcResult.ok(obj);
			}
		}catch(Exception e){
			LogUtil.getLogger().error("省市code查询失败",e);
			return AmpcResult.build(1001, "省市code查询失败");
		}
	}

	
	
	
}

