package ampc.com.gistone.controller;

import java.math.BigDecimal;
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

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;

@RestController
@RequestMapping
public class AppraisalController {
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	/**
	 * 时间序列查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Appraisal/find_appraisal")
	public AmpcResult find_appraisal(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){ 
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Integer userId=Integer.valueOf(data.get("userId").toString());
			Long missionId=Long.valueOf(data.get("missionId").toString());
			String mode=data.get("mode").toString();
			String cityStation=data.get("cityStation").toString();
			JSONArray lists = JSONArray.fromObject(data.get("scenarinoId"));
			List<Integer> list=new ArrayList<Integer>();
			Map<String,Object> scmap=new HashMap();
			for(Object scid:lists){
				list.add(Integer.valueOf(scid.toString()));	
			}
			String datetype=data.get("datetype").toString();

			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);//该任务下的所有数据
			Integer domainId=Integer.valueOf(tMissionDetail.getMissionDomainId().toString());
			List<ScenarinoEntity> sclist=new ArrayList();
			for(Integer scenarinoId:list){
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(Long.valueOf(scenarinoId.toString()));//查询该任务中该情景下的所有数据
			TScenarinoDetail tScenarino=new TScenarinoDetail();
			tScenarino.setMissionId(missionId);
			tScenarino.setScenType("3");
			List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
			TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
			//预评估任务
			if(tMissionDetail.getMissionStatus().equals("2")){//任务类型
				if(4<5){//任务开始时间与系统开始时间对比
					if(datetype.equals("day")){//逐天
				String tables="T_SCENARINO_DAILY_";
				Date tims=tScenarinoDetail.getScenarinoAddTime();
				 DateFormat df = new SimpleDateFormat("yyyy");
				String nowTime= df.format(tims);
				tables+=nowTime+"_";
				tables+=userId;
				ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
				scenarinoEntity.setCity_station(cityStation);
				scenarinoEntity.setDomain(3);
				scenarinoEntity.setDomainId(domainId);
				scenarinoEntity.setMode(mode);
				scenarinoEntity.setsId(scenarinoId);
				scenarinoEntity.setTableName(tables);
				sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}else{//逐小时
					String tables="T_SCENARINO_HOURLY_";
					Date tims=tScenarinoDetail.getScenarinoAddTime();
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
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}//时间分布判断
					
				}//任务开始时间与系统开始时间对比
				//任务类型
			}else{
				if(datetype.equals("day")){//逐天
					String tables="T_SCENARINO_DAILY_";
					Date tims=tScenarinoDetail.getScenarinoAddTime();
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
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					}else{//逐小时
						String tables="T_SCENARINO_HOURLY_";
						Date tims=tScenarinoDetail.getScenarinoAddTime();
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
						scenarinoEntity.setsId(scenarinoId);
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);

					}//时间分布判断
			}//任务类型
			if(sclist.isEmpty()){
				return	AmpcResult.build(0, "success",scmap);
			}
			if(datetype.equals("hour")){
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
				JSONObject obj=JSONObject.fromObject(content);//行业减排结果
				Map<String,Object> detamap=(Map)obj;
				Map<String,Object> datemap=new HashMap();
				for(String datetime:detamap.keySet()){
					String sp=detamap.get(datetime).toString();
					JSONObject spobj=JSONObject.fromObject(sp);//行业减排结果
					Map<String,Object> spmap=(Map)spobj;
					Map<String,Object> spcmap=new HashMap();
					for(String spr:spmap.keySet()){
						String height=spmap.get(spr).toString();
						JSONObject heightobj=JSONObject.fromObject(height);//行业减排结果
						Map<String,Object> heightmap=(Map)heightobj;
						String hour=heightmap.get("0").toString();
						JSONArray hourlist= JSONArray.fromObject(hour);
						
						Map<String,Object> hourcmap=new HashMap();
						if(hourlist.size()==24){
						for(int a=0;a<=23;a++){
							if(spr.equals("CO")){
								BigDecimal bd=(new BigDecimal(heightmap.get(a).toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
								hourcmap.put(String.valueOf(a),bd);
								}else{
								BigDecimal bd=(new BigDecimal(heightmap.get(a).toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
								hourcmap.put(String.valueOf(a),bd);
								}	
						}
						}else{
							for(int a=0;a<=23;a++){
								hourcmap.put(String.valueOf(a),"-");
							}
						}
						spcmap.put(datetime, hourcmap);
						if(datemap.get(spr)!=null){
							Object maps=datemap.get(spr);
							JSONObject mapsbj=JSONObject.fromObject(maps);//行业减排结果
							Map<String,Object> des=(Map)mapsbj;
							des.put(datetime, hourcmap);
							datemap.put(spr, des);
							}else{
							datemap.put(spr, spcmap);
							}
						scmap.put(scid, datemap);
					}
					
				}
			}
			}else{
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					JSONObject obj=JSONObject.fromObject(content);//行业减排结果
					Map<String,Object> detamap=(Map)obj;
					Map<String,Object> datemap=new HashMap();
					for(String datetime:detamap.keySet()){
						String sp=detamap.get(datetime).toString();
						JSONObject spobj=JSONObject.fromObject(sp);//行业减排结果
						Map<String,Object> spmap=(Map)spobj;
						
						for(String spr:spmap.keySet()){
							Map<String,Object> spcmap=new HashMap();
							String height=spmap.get(spr).toString();
							JSONObject heightobj=JSONObject.fromObject(height);//行业减排结果
							Map<String,Object> heightmap=(Map)heightobj;
							Map<String,Object> hourcmap=new HashMap();
							if(spr.equals("CO")){
								BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
								spcmap.put(datetime, bd);
								}else{
								BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
								spcmap.put(datetime, bd);
								}
							if(datemap.get(spr)!=null){
							Object maps=datemap.get(spr);
							JSONObject mapsbj=JSONObject.fromObject(maps);//行业减排结果
							Map<String,Object> des=(Map)mapsbj;
							if(spr.equals("CO")){
								BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
								des.put(datetime, bd);
								}else{
								BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
								des.put(datetime, bd);
								}
							
							datemap.put(spr, des);
							}else{
							datemap.put(spr, spcmap);
							}
							scmap.put(scid, datemap);
						}
						
					}
				}
				
				
			}
			

			}
			return	AmpcResult.build(0, "success",scmap);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 时间序列查询异常！",e);
			return AmpcResult.build(0, "error");
		}
	}
	
	/**
	 * 垂直分布查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Appraisal/find_vertical")
	public AmpcResult find_vertical(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.valueOf(data.get("userId").toString());
			Long missionId=Long.valueOf(data.get("missionId").toString());
			String mode=data.get("mode").toString();
			String time=data.get("time").toString();
			String cityStation=data.get("cityStation").toString();
			JSONArray lists = JSONArray.fromObject(data.get("scenarinoId"));
			List<Integer> list=new ArrayList<Integer>();
			for(Object scid:lists){
				list.add(Integer.valueOf(scid.toString()));	
			}
			String datetype=data.get("datetype").toString();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			Date times=sdf.parse(time);
			int hournum=times.getHours();
			Date daytimes=daysdf.parse(time);
			String daytime=daysdf.format(daytimes);
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			Integer domainId=Integer.valueOf(tMissionDetail.getMissionDomainId().toString());
			List<ScenarinoEntity> sclist=new ArrayList();
			Map<String,Object> scmap=new HashMap();
			for(Integer scenarinoId:list){
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(Long.valueOf(scenarinoId.toString()));
			TScenarinoDetail tScenarino=new TScenarinoDetail();
			tScenarino.setMissionId(missionId);
			tScenarino.setScenType("3");
			List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
			TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
			//预评估任务
			if(tMissionDetail.getMissionStatus().equals("2")){//任务类型
				if(4<5){//任务开始时间与系统开始时间对比
					if(datetype.equals("day")){//逐天
				String tables="T_SCENARINO_DAILY_";
				Date tims=tScenarinoDetail.getScenarinoAddTime();
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
				scenarinoEntity.setsId(scenarinoId);
				scenarinoEntity.setTableName(tables);
				sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}else{//逐小时
					String tables="T_SCENARINO_HOURLY_";
					Date tims=tScenarinoDetail.getScenarinoAddTime();
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
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
				}//时间分布判断
					
				}//任务开始时间与系统开始时间对比	
				//任务类型		
			}else{
				if(datetype.equals("day")){//逐天
					String tables="T_SCENARINO_DAILY_";
					Date tims=tScenarinoDetail.getScenarinoAddTime();
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
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					}else{//逐小时
						String tables="T_SCENARINO_HOURLY_";
						Date tims=tScenarinoDetail.getScenarinoAddTime();
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
						scenarinoEntity.setsId(scenarinoId);
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					}//时间分布判断
			}//任务类型
			
			if(datetype.equals("hour")){
			for(ScenarinoEntity sc:sclist){
				String scid=String.valueOf(sc.getsId());
				String content=sc.getContent().toString();
				JSONObject obj=JSONObject.fromObject(content);
				Map<String,Object> detamap=(Map)obj;
				Map<String,Object> datemap=new HashMap();
				for(String datetime:detamap.keySet()){
					if(datetime.equals(daytime)){
					Map<String,Object> spmap=(Map)detamap.get(datetime);
					Map<String,Object> spcmap=new HashMap();
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
							if(hournum-1==a){
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
							}
							}
						}else{
							for(int a=0;a<=23;a++){
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
							}
						}
						spcmap.put(spr, arr);
						scmap.put(scid, spcmap);
							}
					}
					}
				}
				}
			}
			}else{
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					JSONObject obj = JSONObject.fromObject(content);
					Map<String,Object> detamap=(Map)obj;
					Map<String,Object> datemap=new HashMap();
					for(String datetime:detamap.keySet()){
						if(datetime.equals(daytime)){
						String sp=detamap.get(datetime).toString();
						JSONObject spobj=JSONObject.fromObject(sp);
						Map<String,Object> spmap=(Map)spobj;
						Map<String,Object> spcmap=new HashMap();
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
							scmap.put(scid, spcmap);
								}
						}
						}
						
					}
					}
				}
				
				
			}
			

			}
			return	AmpcResult.build(0, "success",scmap);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 垂直分布查询异常！",e);
			return	AmpcResult.build(0, "error");
		}
	}
	
	
	
	
	/**
	 * 垂直分布基准查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	
	@RequestMapping("Appraisal/find_basevertical")
	public AmpcResult find_basevertical(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.valueOf(data.get("userId").toString());
			Long missionId=Long.valueOf(data.get("missionId").toString());
			String mode=data.get("mode").toString();
			String time=data.get("time").toString();
			String cityStation=data.get("cityStation").toString();

			String datetype=data.get("datetype").toString();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			List<ScenarinoEntity> sclist=new ArrayList();
			Date times=sdf.parse(time);
			int hournum=times.getHours();
			Date daytimes=daysdf.parse(time);
			String daytime=daysdf.format(daytimes);
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			Integer domainId=Integer.valueOf(tMissionDetail.getMissionDomainId().toString());
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setMissionId(missionId);
			List<TScenarinoDetail> tslist=new ArrayList<TScenarinoDetail>();
			
			tScenarinoDetail.setScenType("3");
			if(tScenarinoDetailMapper.selectByEntity(tScenarinoDetail).isEmpty()){
				tScenarinoDetail.setScenType("4");
			tslist=tScenarinoDetailMapper.selectByEntity(tScenarinoDetail);
			}else{
				tslist=tScenarinoDetailMapper.selectByEntity(tScenarinoDetail);
			}
			Map<String,Object> scmap=new HashMap();
			for(TScenarinoDetail ScenarinoDetail:tslist){
				Date startdate=ScenarinoDetail.getScenarinoStartDate();
				String start=daysdf.format(startdate);
				Date adddate=ScenarinoDetail.getScenarinoAddTime();
				if(start.equals(daytime)){
					if(datetype.equals("day")){//逐天
						String tables="T_SCENARINO_FNL_DAILY_";
						 DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(adddate);
						System.out.println(nowTime);
						tables+=nowTime+"_";
						tables+=userId;
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Integer.valueOf(ScenarinoDetail.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							String tables="T_SCENARINO_FNL_HOURLY_";
							 DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(adddate);
							System.out.println(nowTime);
							tables+=nowTime+"_";
							tables+=userId.toString();
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId(domainId);
							scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Integer.valueOf(ScenarinoDetail.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}//时间分布判断
				}else{
					continue;
				}	
			}
			if(datetype.equals("hour")){
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					JSONObject obj=JSONObject.fromObject(content);
					Map<String,Object> detamap=(Map)obj;
					Map<String,Object> datemap=new HashMap();

						Map<String,Object> spcmap=new HashMap();
						for(String spr:detamap.keySet()){
							String height=detamap.get(spr).toString();
							JSONObject heightobj=JSONObject.fromObject(height);
							Map<String,Object> heightmap=(Map)heightobj;
							JSONArray arr=new JSONArray();
							for(String heights:heightmap.keySet()){
								if(!heights.equals("12")){
								JSONArray litarr=new JSONArray();
								Object hour=heightmap.get(heights);
								JSONArray hourlist = JSONArray.fromObject(hour);
							Map<String,Object> hourcmap=new HashMap();
							if(hourlist.size()==24){
							for(int a=0;a<=23;a++){
								if(hournum-1==a){
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
								}
								}
							}else{
								for(int a=0;a<=23;a++){
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
								}
							}
							spcmap.put(spr, arr);
							scmap.put("-1", spcmap);
								}
						}
						}

				}
				}else{
					for(ScenarinoEntity sc:sclist){
						String scid=String.valueOf(sc.getsId());
						String content=sc.getContent().toString();
						JSONObject obj = JSONObject.fromObject(content);
						Map<String,Object> detamap=(Map)obj;
						Map<String,Object> datemap=new HashMap();
							Map<String,Object> spcmap=new HashMap();
							for(String spr:detamap.keySet()){
								String height=detamap.get(spr).toString();
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
								scmap.put("-1", spcmap);
									}
							}
							}
					}
				}
			
			return	AmpcResult.build(0, "success",scmap);
		}catch(Exception e){
		LogUtil.getLogger().error("AppraisalController 垂直分布基准查询异常！",e);
		
		return	AmpcResult.build(0, "error","0000");	
		}
	}
	
	
	
	
	
	@RequestMapping("Appraisal/find_baseappraisal")
	public AmpcResult find_find_baseappraisal(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){ 
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Integer userId=Integer.valueOf(data.get("userId").toString());
			Long missionId=Long.valueOf(data.get("missionId").toString());
			String mode=data.get("mode").toString();
			String cityStation=data.get("cityStation").toString();
			Date today=new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			String todays=sdf.format(today);
			Date todayd=sdf.parse(todays);
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			String status=tMissionDetail.getMissionStatus().toString();
			String datetype=data.get("datetype").toString();
			//计算任务开始时间结束时间差
			Date strdate=tMissionDetail.getMissionStartDate();
			Date endate=tMissionDetail.getMissionEndDate();
			SimpleDateFormat sdft=new SimpleDateFormat("yyyy-MM-dd");
			String strdates=sdft.format(endate);
			String endates=sdft.format(endate);
			String todaystr=sdft.format(todayd);
			Date todayda=sdft.parse(todaystr);
			Date startdate=sdft.parse(strdates);
			Date enddate=sdft.parse(endates);
			Long start=startdate.getTime();
			Long end=enddate.getTime();
			Long todayl=todayda.getTime();
			Long day=(start-end)/(24*60*60*1000);
			Long tday=(todayl-end)/(24*60*60*1000)-1;//任务开始结束时间差
			Long chaday=day-tday;
			
			Integer domainId=Integer.valueOf(tMissionDetail.getMissionDomainId().toString());
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setMissionId(missionId);
			tScenarinoDetail.setScenType("4");
			List<TScenarinoDetail> tslist=tScenarinoDetailMapper.selectByEntity(tScenarinoDetail);
			List<Map<String,TScenarinoDetail>> datelist=new ArrayList();
			List<ScenarinoEntity> scelist=new ArrayList();
			if(status.equals("2")){
				for(int a=1;a<=tday;a++){
					Calendar cal = Calendar.getInstance();
					cal.setTime(startdate);
					cal.add(Calendar.DATE, a);
					String addTimeDate =sdf.format(cal.getTime());
					for(TScenarinoDetail tsd:tslist){
						Date sstart=tsd.getScenarinoStartDate();
						String sstrats=sdft.format(sstart);
						if(addTimeDate.equals(sstrats)){
							Map<String,TScenarinoDetail> map=new HashMap();
							map.put(addTimeDate, tsd);
							datelist.add(map);
						}else{
							continue;
						}
					}
				}
			}else{
				for(int a=1;a<=day;a++){
					Calendar cal = Calendar.getInstance();
					cal.setTime(startdate);
					cal.add(Calendar.DATE, a);
					String addTimeDate =sdf.format(cal.getTime());
					for(TScenarinoDetail tsd:tslist){
						Date sstart=tsd.getScenarinoStartDate();
						String sstrats=sdft.format(sstart);
						if(addTimeDate.equals(sstrats)){
							Map<String,TScenarinoDetail> map=new HashMap();
							map.put(addTimeDate, tsd);
							datelist.add(map);
						}else{
							continue;
						}
					}
				}
			}
			if(status.equals("2")){
				for(Map<String,TScenarinoDetail> dates:datelist){
					String date="";
					for(String datethe:dates.keySet()){
						date=datethe;
					}
					TScenarinoDetail tsc=dates.get(date);
					List<ScenarinoEntity> sclist=new ArrayList();
					if(datetype.equals("day")){//逐天
						String tables="T_SCENARINO_FNL_DAILY_";
						Date tims=tScenarinoDetail.getScenarinoAddTime();
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(tims);
						tables+=nowTime+"_";
						tables+=userId;
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Integer.valueOf(tsc.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						scenarinoEntity.setDay(tsc.getScenarinoStartDate());
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						scelist.add(sclist.get(0));
						}else{//逐小时
							String tables="T_SCENARINO_FNL_HOURLY_";
							Date tims=tScenarinoDetail.getScenarinoAddTime();
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
							scenarinoEntity.setsId(Integer.valueOf(tsc.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							scenarinoEntity.setDay(tsc.getScenarinoStartDate());
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
							scelist.add(sclist.get(0));
						}//时间分布判断	
					
					
						for(int a=1;a<=chaday;a++){
							Map<String,TScenarinoDetail> damap=datelist.get(datelist.size()-1);
							String dated="";
							for(String datethe:damap.keySet()){
								date=datethe;
							}
							
							
						}
					
					
					
				}
				
				
				
			}else{
				
				
				
				
				
			}
			return	AmpcResult.build(0, "success");
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 创建预案异常！",e);
			return	AmpcResult.build(0, "error");
		}
		}

}
