package ampc.com.gistone.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;

@RestController
@RequestMapping
public class AirController {
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	
	@RequestMapping("/Air/get_time")
	public AmpcResult get_time(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ) throws NoSuchAlgorithmException, IOException {
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TMissionDetail tm=tMissionDetailMapper.selectMaxMission();
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setMissionId(tm.getMissionId());
			TScenarinoDetail maxtm=tScenarinoDetailMapper.selectByrealmax(tm.getMissionId());
			TScenarinoDetail mintm=tScenarinoDetailMapper.selectByrealmin(tm.getMissionId());
			JSONObject obj=new JSONObject();
			obj.put("mintime", mintm.getPathDate().getTime());
			obj.put("maxtime", maxtm.getPathDate().getTime());
			LogUtil.getLogger().error("空气质量预报时间查询成功");
			return AmpcResult.build(0, "success",obj);
		}catch(Exception e){
			LogUtil.getLogger().error("异常了",e);
			return AmpcResult.build(0, "执行失败");
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 空气质量预报垂直分布查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Air/find_vertical")
	public AmpcResult find_vertical(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.valueOf(data.get("userId").toString());
			String mode=data.get("mode").toString();
			String time=data.get("time").toString();
			String cityStation=data.get("cityStation").toString();
			List<Integer> list=new ArrayList<Integer>();
			
			String datetype=data.get("datetype").toString();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			Date times=sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(times);
			cal.add(Calendar.DATE, 1);
			String addTimeDate =daysdf.format(cal.getTime());
			Date pathDate=daysdf.parse(addTimeDate);
			int hournum=times.getHours();
			Date daytimes=daysdf.parse(time);
			String daytime=daysdf.format(daytimes);

			List<ScenarinoEntity> sclist=new ArrayList();
			Map<String,Object> scmap=new HashMap();
			TScenarinoDetail tScenarino=new TScenarinoDetail();
			tScenarino.setPathDate(pathDate);
			List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
			TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
			TMissionDetail tm=new TMissionDetail();
			tm.setMissionId(jztScenarino.getMissionId());
			List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
			TMissionDetail thetm=tmlist.get(0);	
			int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());
			//预评估任务

				if(datetype.equals("day")){//逐天
				String tables="T_SCENARINO_FNL_DAILY_";
				Date tims=jztScenarino.getPathDate();
				 DateFormat df = new SimpleDateFormat("yyyy");
				String nowTime= df.format(tims);
				System.out.println(nowTime);
				tables+=nowTime+"_";
				tables+=userId;
				ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
				scenarinoEntity.setCity_station(cityStation);
				scenarinoEntity.setDomain(3);
				scenarinoEntity.setDomainId(domainId);
				scenarinoEntity.setMode(mode);
				scenarinoEntity.setsId(Integer.valueOf(jztScenarino.getScenarinoId().toString()));
				scenarinoEntity.setTableName(tables);
				sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}else{//逐小时
					String tables="T_SCENARINO_FNL_HOURLY_";
					Date tims=jztScenarino.getPathDate();
					 DateFormat df = new SimpleDateFormat("yyyy");
					String nowTime= df.format(tims);
					System.out.println(nowTime);
					tables+=nowTime+"_";
					tables+=userId.toString();
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					scenarinoEntity.setDomain(3);
					scenarinoEntity.setDomainId(domainId);
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setsId(Integer.valueOf(jztScenarino.getScenarinoId().toString()));
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}//时间分布判断
					
						
			
				Map<String,Object> spcmap=new HashMap();
			if(datetype.equals("hour")){
			for(ScenarinoEntity sc:sclist){
				String scid=String.valueOf(sc.getsId());
				String content=sc.getContent().toString();
				JSONObject obj=JSONObject.fromObject(content);
				Map<String,Object> spmap=(Map)obj;
				
					for(String spr:spmap.keySet()){
						Map<String,Object> heightmap=(Map) spmap.get(spr);
						JSONArray arr=new JSONArray();
						for(String heights:heightmap.keySet()){
							if(!heights.equals("12")){
							JSONArray litarr=new JSONArray();
							Object hour=heightmap.get(heights);
							JSONArray hourlist = JSONArray.fromObject(hour);
						Map<String,Object> hourcmap=new HashMap();
						if(hourlist.size()==24){
						for(int a=0;a<=23;a++){
							if(hournum==a){
								if(spr.equals("CO")){
									if(hourlist.get(hournum)!=null){
									litarr.add((new BigDecimal(hourlist.get(hournum).toString())).setScale(2, BigDecimal.ROUND_HALF_UP));
									}else{
										litarr.add("-");
									}
									}else{
										if(hourlist.get(hournum)!=null){
											litarr.add((new BigDecimal(hourlist.get(hournum).toString())).setScale(1, BigDecimal.ROUND_HALF_UP));											
											}else{
												litarr.add("-");
											}
									}
								if(heights.equals("0")){
									litarr.add(0);
								}else if(heights.equals("1")){
									litarr.add(50);
								}else if(heights.equals("2")){
									litarr.add(100);
								}else if(heights.equals("3")){
									litarr.add(200);
								}else if(heights.equals("4")){
									litarr.add(300);
								}else if(heights.equals("5")){
									litarr.add(400);
								}else if(heights.equals("6")){
									litarr.add(500);
								}else if(heights.equals("7")){
									litarr.add(700);
								}else if(heights.equals("8")){
									litarr.add(1000);
								}else if(heights.equals("9")){
									litarr.add(1500);
								}else if(heights.equals("10")){
									litarr.add(2000);
								}else if(heights.equals("11")){
									litarr.add(3000);
								}
								arr.add(litarr);
							}
							}
						}else{
							for(int a=0;a<=23;a++){
								if(spr.equals("CO")){
									if(hourlist.get(hournum)!=null){
									litarr.add((new BigDecimal(hourlist.get(hournum).toString())).setScale(2, BigDecimal.ROUND_HALF_UP));
									}else{
										litarr.add("-");
									}
									}else{
										if(hourlist.get(hournum)!=null){
											litarr.add((new BigDecimal(hourlist.get(hournum).toString())).setScale(1, BigDecimal.ROUND_HALF_UP));											
											}else{
												litarr.add("-");
											}
									}
								if(heights.equals("0")){
									litarr.add(0);
								}else if(heights.equals("1")){
									litarr.add(50);
								}else if(heights.equals("2")){
									litarr.add(100);
								}else if(heights.equals("3")){
									litarr.add(200);
								}else if(heights.equals("4")){
									litarr.add(300);
								}else if(heights.equals("5")){
									litarr.add(400);
								}else if(heights.equals("6")){
									litarr.add(500);
								}else if(heights.equals("7")){
									litarr.add(700);
								}else if(heights.equals("8")){
									litarr.add(1000);
								}else if(heights.equals("9")){
									litarr.add(1500);
								}else if(heights.equals("10")){
									litarr.add(2000);
								}else if(heights.equals("11")){
									litarr.add(3000);
								}
								arr.add(litarr);
							}
						}
						spcmap.put(spr, arr);
						scmap.put(scid, spcmap);
							}
					}
					}
				
				
			}
			}else{
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					JSONObject obj = JSONObject.fromObject(content);
					Map<String,Object> spmap=(Map)obj;
						
						for(String spr:spmap.keySet()){
							String height=spmap.get(spr).toString();
							JSONObject heightobj=JSONObject.fromObject(height);
							Map<String,Object> heightmap=(Map)heightobj;
							JSONArray arr=new JSONArray();
							for(String heights:heightmap.keySet()){
								if(!heights.equals("12")){
							Map<String,Object> hourcmap=new HashMap();
							JSONArray litarr=new JSONArray();
							if(spr.equals("CO")){
								if(heightmap.get(heights)!=null){
								litarr.add((new BigDecimal(heightmap.get(heights).toString())).setScale(2, BigDecimal.ROUND_HALF_UP));
								}else{
									litarr.add("-");
								}
								}else{
									if(heightmap.get(heights)!=null){
										litarr.add((new BigDecimal(heightmap.get(heights).toString())).setScale(1, BigDecimal.ROUND_HALF_UP));											
										}else{
											litarr.add("-");
										}
								}
							if(heights.equals("0")){
								litarr.add(0);
							}else if(heights.equals("1")){
								litarr.add(50);
							}else if(heights.equals("2")){
								litarr.add(100);
							}else if(heights.equals("3")){
								litarr.add(200);
							}else if(heights.equals("4")){
								litarr.add(300);
							}else if(heights.equals("5")){
								litarr.add(400);
							}else if(heights.equals("6")){
								litarr.add(500);
							}else if(heights.equals("7")){
								litarr.add(700);
							}else if(heights.equals("8")){
								litarr.add(1000);
							}else if(heights.equals("9")){
								litarr.add(1500);
							}else if(heights.equals("10")){
								litarr.add(2000);
							}else if(heights.equals("11")){
								litarr.add(3000);
							}
							
							
							arr.add(litarr);
							spcmap.put(spr, arr);
						
								}
						}
						}
						
					
					
				}
				
				
			}
			

			
			return	AmpcResult.build(0, "success",spcmap);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 垂直分布查询异常！",e);
			return	AmpcResult.build(0, "error");
		}
	}
	
	
	
	
	
}
