package ampc.com.gistone.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TObsMapper;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LevelUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

@RestController
@RequestMapping
public class AirController {
	@Autowired
	private TMissionDetailMapper tMissionDetailMapper;

	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;

	@Autowired
	private TPreProcessMapper tPreProcessMapper;
	
	@Autowired
	private TObsMapper tObsMapper;

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
			return AmpcResult.ok(obj);
		}catch(Exception e){
			LogUtil.getLogger().error("get_time 查询时间失败",e);
			return AmpcResult.build(1001, "查询时间失败");
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
			//获取前端参数
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.valueOf(data.get("userId").toString());//用户id
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_vertical  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			String mode=data.get("mode").toString();//站点或者平均
			if(!RegUtil.CheckParameter(mode, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  站点类型为空!");
				return AmpcResult.build(1003, "站点类型为空!");
			}
			String time=data.get("time").toString();//时间
			if(!RegUtil.CheckParameter(time, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  时间为空!");
				return AmpcResult.build(1003, "时间为空!");
			}
			String cityStation=data.get("cityStation").toString();//站点code
			if(!RegUtil.CheckParameter(cityStation, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  站点code为空!");
				return AmpcResult.build(1003, "站点code为空!");
			}
			List<Integer> list=new ArrayList<Integer>();

			String datetype=data.get("datetype").toString();
			if(!RegUtil.CheckParameter(datetype, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  时间分辨率为空!");
				return AmpcResult.build(1003, "时间分辨率为空!");
			}
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			//获取对应的情景起报日期
			Date times=sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(times);
			cal.add(Calendar.DATE, 1);
			String addTimeDate =daysdf.format(cal.getTime());
			Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
			int hournum=times.getHours();
			Date daytimes=daysdf.parse(time);
			String daytime=daysdf.format(daytimes);

			List<ScenarinoEntity> sclist=new ArrayList();
			Map<String,Object> scmap=new HashMap();
			TScenarinoDetail tScenarino=new TScenarinoDetail();
			tScenarino.setScenType("4");
			tScenarino.setPathDate(pathDate);
			//查询对应情景
			List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
			TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
			TMissionDetail tm=new TMissionDetail();
			tm.setMissionId(jztScenarino.getMissionId());
			//通过情景中的任务id查询任务，再通过任务查询domainid
			List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
			TMissionDetail thetm=tmlist.get(0);	
			int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());
			//预评估任务
			//判断是时均还是日均，分别查不同的数据表
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
				scenarinoEntity.setDomainId((long)domainId);
				
				scenarinoEntity.setMode(mode);
				scenarinoEntity.setDay(daytimes);
				scenarinoEntity.setsId(Long.parseLong((jztScenarino.getScenarinoId().toString())));
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
				scenarinoEntity.setDomainId(Long.valueOf(domainId));
				scenarinoEntity.setMode(mode);
				scenarinoEntity.setDay(daytimes);
				scenarinoEntity.setsId(Long.valueOf(jztScenarino.getScenarinoId().toString()));
				scenarinoEntity.setTableName(tables);
				sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
			}//时间分布判断


			//判断时均还是日均然后进行不同的解析
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
											if(spr.equals("CO")){//co保留两位小数其他保留一位小数
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



			return	AmpcResult.ok(spcmap);
		}catch(Exception e){
			LogUtil.getLogger().error("find_vertical 垂直分布查询异常！",e);
			return	AmpcResult.build(1001, "垂直分布查询异常！");
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 空气质量预报--时间序列
	 * 根据任务id以及userid查询基准情
	 * @param requestDate
	 * @param request
	 * @param response
 	 * @return
	 */
	@RequestMapping("Air/findAllTimeSeries")
	public AmpcResult findAllTimeSeries(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			//接收参数
			Long missionId;			//查询到的任务id
			TMissionDetail tMissionDetail;	//任务表的信息
			Long domainId;			//存放查询的模拟范围id
			String tables;			//存放拼接的表名
			SimpleDateFormat sdfYear;	//声明格式化时间对象
			Date dateYear;			//声明年份对象
			String strYear;			//声明String类型对象
			ScenarinoEntity scenarinoEntity;
			int differenceVal;		//结束时间和开始时间差值
			Calendar calendar;		//声明对象
			String  contents;		//存放查询到的模拟数据
			Date calDate;
			String calDateStr;

			
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			Long userId=Long.parseLong(data.get("userId").toString());	//用户id
			String mode=data.get("mode").toString();					//检测站点
			String startDate=data.get("startDate").toString();			//开始日期
			String endDate=data.get("endDate").toString();				//结束日期
			String cityStation=data.get("cityStation").toString();		//选择站点值
			String datetype=data.get("datetype").toString();			//逐日或逐小时
			String domain=data.get("domain").toString();
			
			JSONObject objData=new JSONObject();	//存放模拟和观测数据
			
			if("day".equals(datetype)){		//逐日
				//查询当天的情景（未完成则查询前一天）
				SimpleDateFormat sdfNow=new SimpleDateFormat("yyyy-MM-dd");
				Date dateNow=new Date();
				String strDateNow=sdfNow.format(dateNow);
				HashMap<String, Object> mapNow=new HashMap<String, Object>();
				mapNow.put("USER_ID", userId);
				mapNow.put("PATH_DATE", strDateNow);	
//				mapNow.put("PATH_DATE", "2017-04-15");	//先查询当天的数据，需要时注释即可-------------------
				//进行当天的情景查询
				TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
				tScenarinoDetail=tScenarinoDetailMapper.selectScenarinoDetail_timeSeries(mapNow);
				
				//根据该情景所属任务的任务（missionId）查询其情景范围（dominID）
				missionId=tScenarinoDetail.getMissionId();
				tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);	
				domainId=tMissionDetail.getMissionDomainId();	//得到情景范围ID---作为查询参数
				
				tables="T_SCENARINO_DAILY_";	//表名+年份+userid
				sdfYear=new SimpleDateFormat("yyyy");	//当前年份
				dateYear=new Date();
				strYear=sdfYear.format(dateYear);
				tables+=strYear+"_";
				tables+=userId;
				scenarinoEntity=new ScenarinoEntity();
				scenarinoEntity.setCity_station(cityStation);
				scenarinoEntity.setDomain(3);					//空间分辨率--需要时注释即可,数据库中目前只有为3的数据-----------------------
//				scenarinoEntity.setDomain(Integer.valueOf(domain));
				scenarinoEntity.setMode(mode);
//				scenarinoEntity.setDomainId(Integer.parseInt(String.valueOf(domainId)));
				scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
				
//				scenarinoEntity.setsId(Integer.valueOf(tScenarinoDetail.getScenarinoId().toString()));
				scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
				scenarinoEntity.setTableName(tables);
				ScenarinoEntity Scenarino=tPreProcessMapper.selectBysomes(scenarinoEntity);
				
				Date start_date=sdfNow.parse(startDate);
				Date end_date=sdfNow.parse(endDate);
				differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
				calendar = Calendar.getInstance();
				
				//从查询的情景中获取到开始时间和结束时间
				if(Scenarino==null||"".equals(Scenarino)){ 
					
					//求出开始时间和结束时间相差的天数
//					Date start_date=sdfNow.parse(startDate);
//					Date end_date=sdfNow.parse(endDate);
//					int differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
					for(int i=0;i<=differenceVal;i++){	//循环判断查询所选最近一次有数据的日期
//						Calendar calendar = Calendar.getInstance();
						calendar.setTime(sdfNow.parse(endDate));
						calendar.add(Calendar.DAY_OF_MONTH, -(i+1));
						calDate = calendar.getTime();
						calDateStr=sdfNow.format(calDate);
						mapNow.put("PATH_DATE", calDateStr);
						tScenarinoDetail=tScenarinoDetailMapper.selectScenarinoDetail_timeSeries(mapNow);
						
						missionId=tScenarinoDetail.getMissionId();
						tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);	
						domainId=tMissionDetail.getMissionDomainId();	//得到情景范围ID---作为查询参数
						
						tables="T_SCENARINO_DAILY_";	//表名+年份+userid
						sdfYear=new SimpleDateFormat("yyyy");	//当前年份
						dateYear=new Date();
						strYear=sdfYear.format(dateYear);
						tables+=strYear+"_";
						tables+=userId;
						scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);					//空间分辨率--需要时注释即可,数据库中目前只有为3的数据-----------------------
//						scenarinoEntity.setDomain(Integer.valueOf(domain));
						scenarinoEntity.setMode(mode);
//						scenarinoEntity.setDomainId(Integer.parseInt(String.valueOf(domainId)));
						scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
						
//						scenarinoEntity.setsId(Integer.valueOf(tScenarinoDetail.getScenarinoId().toString()));
						scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
						scenarinoEntity.setTableName(tables);
						Scenarino=tPreProcessMapper.selectBysomes(scenarinoEntity);
						
						if(Scenarino!=null){	//查询到模拟数据跳出
							
							contents=Scenarino.getContent();		//获取模拟数据
							JSONObject contentobj= JSONObject.fromObject(contents);
							JSONArray dayArr=new JSONArray();
							Map<String,Object> contentmap= (Map)contentobj;	//模拟父数据
							
							JSONObject simulationData=new JSONObject();		//返给页面的模拟数据
							JSONObject speciesData=new JSONObject();		
							
//							for(String dayKey:contentmap.keySet()){			//对日期进行排序
//								dayArr.add(dayKey);
//							}
//							Collections.sort(dayArr);
//							dayArr.get(0);	//排序
							Map<String,Object> spmap=new HashMap<String, Object>();
							for(String dayKey:contentmap.keySet()){
								Object  species=contentmap.get(dayKey);		//值
								if(species!=null){
									spmap= (Map)species;
									break;
								}
							}
							for(String speciesKey:spmap.keySet()){			//循环所有物种名称---并给日期赋值
								for(String dayKey:contentmap.keySet() ){	//循环全部日期
//									for(String contentmapKey:contentmap.keySet()){
//										contentmap.get(dayKey)
//									}
									Object speciesobj=contentmap.get(dayKey);
									Map<String,Object> speciesMap= (Map)speciesobj;
//									for(String speciesMapKey:speciesMap.keySet()){
									Object speciesOne=speciesMap.get(speciesKey);
									Map<String,Object> speciesOneMap= (Map)speciesOne;
//									Object speciesOneStr=speciesOneMap.get("0");
									String speciesOneVal=speciesOneMap.get("0").toString();
									if("CO".equals(speciesKey)){
										if("".equals(speciesOneVal)||speciesOneVal==null){		//判断是否有值
											speciesData.put(dayKey,"-");
										}else{
											BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
											speciesData.put(dayKey,bd);
										}
									}else{
										if("".equals(speciesOneVal)||speciesOneVal==null){		//判断是否有值
											speciesData.put(dayKey,"-");
										}else{
											BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
											speciesData.put(dayKey,bd);
										}
									}
//									speciesData.put(dayKey,speciesOneMap.get("0"));
//									}
								}
								simulationData.put(speciesKey,speciesData);
							}
							/*
							 * 查询观测数据
							 */
							JSONObject observationData=new JSONObject();		//返给页面的模拟数据
							JSONObject obsSpeciesData=new JSONObject();
							HashMap<String, Object> obsMap=new HashMap<String, Object>();	//查询观测数据开始
							obsMap.put("city_station",cityStation);
							String modes;								//用于存放新的mode
							if("point".equals(mode)){
								 modes="station";
							}else{
								 modes=mode;
							}
							obsMap.put("mode",modes);
							String tables_obs="T_OBS_DAILY_";
							DateFormat df_obs = new SimpleDateFormat("yyyy");
							Date nowDate=new Date();
							String nowYear_obs= df_obs.format(nowDate);		//截取该任务的开始时间来改变查询的表格
							tables_obs+=nowYear_obs;
							obsMap.put("tableName",tables_obs);				//查询的表格
							
//							obsMap.put("date", sdfNow.parse(startDate));
//							ObsBean obsspecies=tObsMapper.queryUnionResult(obsMap);
//							Object obsContent_one=obsspecies.getContent();
//							Map<String,Object> obsContentMap_one= (Map)obsContent_one;
							Object[] speciesArr={"NO2","PM2_5","O3","SO2","PM10","AQI","O3_8h","CO"};
							for(int m=0;m<speciesArr.length;m++){	
							
								for(int j=0;j<=differenceVal;j++){		//循环页面开始年份和结束年份的差值--开始
				//					calendar.setTime(sdfNow.parse(startDate));
									calendar.setTime(sdfNow.parse("2017-04-15"));
									calendar.add(Calendar.DAY_OF_MONTH, (j+1));
									 calDate = calendar.getTime();
									 calDateStr=sdfNow.format(calDate);
									
									calendar.setTime(sdfNow.parse(calDateStr));
									calendar.add(Calendar.DAY_OF_MONTH, -1);
									Date calDates = calendar.getTime();
									String calDateStrs=sdfNow.format(calDates);
									Date calDate_now=sdfNow.parse(calDateStrs);
									obsMap.put("date", calDate_now);
									ObsBean obsBeans=tObsMapper.queryUnionResult(obsMap);	//查询观测数据
									
									if(!"".equals(obsBeans)&&obsBeans!=null&&!"{}".equals(obsBeans)){
										
										String obsContent=obsBeans.getContent().toString();
										JSONObject obsContentobj=JSONObject.fromObject(obsContent);
										Map<String,Object> obsContentMap= (Map)obsContentobj;
//										for(String obsContentKey:obsContentMap.keySet()){
//											if(speciesArr[m].equals(obsContentKey)){
//												obsSpeciesData.put(calDate_now, obsContentMap.get(obsContentKey));
//												break;
//											}
//										}
										for(int k=0;k<speciesArr.length;k++){		//查询的观测数据不为空时，进行赋值
											if(speciesArr[m].equals(speciesArr[k])){
												try {
													String speciesval=obsContentMap.get(speciesArr[k]).toString();	//键为空出现异常时，赋值"-"
													if("CO".equals(speciesArr[k])){									//值不为空，保留位数判断
														BigDecimal bd=(new BigDecimal(speciesval)).setScale(2, BigDecimal.ROUND_HALF_UP);
														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
													}else{
														BigDecimal bd=(new BigDecimal(speciesval)).setScale(1, BigDecimal.ROUND_HALF_UP);
														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
													}
													
												} catch (Exception e) {
													// TODO: handle exception
													obsSpeciesData.put(calDate_now,"-" );
												}
											}
										}
									}else{
										for(int k=0;k<speciesArr.length;k++){	//查询的观测数据为空时，进行赋值
											if(speciesArr[m].equals(speciesArr[k])){
												obsSpeciesData.put(calDate_now,"-" );
											}
										}
									}
									
								}	//循环页面开始年份和结束年份的差值--结束
								observationData.put(speciesArr[m],obsSpeciesData);
							}
//							Date scenarinoStartDate=tScenarinoDetail.getScenarinoStartDate();	//查询到的实时预报情景的开始时间
//							Date scenarinoEndDate=tScenarinoDetail.getScenarinoEndDate();		//查询到的实时预报情景的结束时间
							
							//查询的情景中结束时间如果没有从页面获取到的结束时间参数时，数据赋值为"-"进行表示
							//查询的情景中开始时间如果没有从页面获取到的开始时间参数时，则在FNL表中查询
							objData.put("模拟数据", simulationData);
							objData.put("观测数据", observationData);
														
							break;
						}else if(i==differenceVal-1){	//查询到最后一次
							if(Scenarino==null){	//开始时间没有查询到数据
								return AmpcResult.build(1000, "该情景任务中数据没有插入到数据库",null);
							}
						}
					}
				}else{	//说明已经查询到当天的到数据--第一次
					
					contents=Scenarino.getContent();		//获取模拟数据
					JSONObject contentobj= JSONObject.fromObject(contents);
					JSONArray dayArr=new JSONArray();
					Map<String,Object> contentmap= (Map)contentobj;	//模拟父数据
					
					JSONObject simulationData=new JSONObject();		//返给页面的模拟数据
					JSONObject speciesData=new JSONObject();		
					
//					for(String dayKey:contentmap.keySet()){			//对日期进行排序
//						dayArr.add(dayKey);
//					}
//					Collections.sort(dayArr);
//					dayArr.get(0);	//排序
					Map<String,Object> spmap=new HashMap<String, Object>();
					for(String dayKey:contentmap.keySet()){
						Object  species=contentmap.get(dayKey);		//值
						if(species!=null){
							spmap= (Map)species;
							break;
						}
					}
					for(String speciesKey:spmap.keySet()){			//循环所有物种名称---并给日期赋值
						for(String dayKey:contentmap.keySet() ){	//循环全部日期
//							for(String contentmapKey:contentmap.keySet()){
//								contentmap.get(dayKey)
//							}
							Object speciesobj=contentmap.get(dayKey);
							Map<String,Object> speciesMap= (Map)speciesobj;
//							for(String speciesMapKey:speciesMap.keySet()){
							Object speciesOne=speciesMap.get(speciesKey);
							Map<String,Object> speciesOneMap= (Map)speciesOne;
//							Object speciesOneStr=speciesOneMap.get("0");
							String speciesOneVal=speciesOneMap.get("0").toString();
							if("CO".equals(speciesKey)){
								if("".equals(speciesOneVal)||speciesOneVal==null){		//判断是否有值
									speciesData.put(dayKey,"-");
								}else{
									BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
									speciesData.put(dayKey,bd);
								}
							}else{
								if("".equals(speciesOneVal)||speciesOneVal==null){		//判断是否有值
									speciesData.put(dayKey,"-");
								}else{
									BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
									speciesData.put(dayKey,bd);
								}
							}
//							speciesData.put(dayKey,speciesOneMap.get("0"));
//							}
						}
						simulationData.put(speciesKey,speciesData);
					}
					/*
					 * 查询观测数据
					 */
					JSONObject observationData=new JSONObject();		//返给页面的模拟数据
					JSONObject obsSpeciesData=new JSONObject();
					HashMap<String, Object> obsMap=new HashMap<String, Object>();	//查询观测数据开始
					obsMap.put("city_station",cityStation);
					String modes;								//用于存放新的mode
					if("point".equals(mode)){
						 modes="station";
					}else{
						 modes=mode;
					}
					obsMap.put("mode",modes);
					String tables_obs="T_OBS_DAILY_";
					DateFormat df_obs = new SimpleDateFormat("yyyy");
					Date nowDate=new Date();
					String nowYear_obs= df_obs.format(nowDate);		//截取该任务的开始时间来改变查询的表格
					tables_obs+=nowYear_obs;
					obsMap.put("tableName",tables_obs);				//查询的表格
					
//					obsMap.put("date", sdfNow.parse(startDate));
//					ObsBean obsspecies=tObsMapper.queryUnionResult(obsMap);
//					Object obsContent_one=obsspecies.getContent();
//					Map<String,Object> obsContentMap_one= (Map)obsContent_one;
					Object[] speciesArr={"NO2","PM2_5","O3","SO2","PM10","AQI","O3_8h","CO"};
					for(int m=0;m<speciesArr.length;m++){	
					
						for(int j=0;j<=differenceVal;j++){		//循环页面开始年份和结束年份的差值--开始
		//					calendar.setTime(sdfNow.parse(startDate));
							calendar.setTime(sdfNow.parse("2017-04-15"));
							calendar.add(Calendar.DAY_OF_MONTH, (j+1));
							 calDate = calendar.getTime();
							 calDateStr=sdfNow.format(calDate);
							
							calendar.setTime(sdfNow.parse(calDateStr));
							calendar.add(Calendar.DAY_OF_MONTH, -1);
							Date calDates = calendar.getTime();
							String calDateStrs=sdfNow.format(calDates);
							Date calDate_now=sdfNow.parse(calDateStrs);
							obsMap.put("date", calDate_now);
							ObsBean obsBeans=tObsMapper.queryUnionResult(obsMap);	//查询观测数据
							
							if(!"".equals(obsBeans)&&obsBeans!=null&&!"{}".equals(obsBeans)){
								
								String obsContent=obsBeans.getContent().toString();
								JSONObject obsContentobj=JSONObject.fromObject(obsContent);
								Map<String,Object> obsContentMap= (Map)obsContentobj;
//								for(String obsContentKey:obsContentMap.keySet()){
//									if(speciesArr[m].equals(obsContentKey)){
//										obsSpeciesData.put(calDate_now, obsContentMap.get(obsContentKey));
//										break;
//									}
//								}
								for(int k=0;k<speciesArr.length;k++){		//查询的观测数据不为空时，进行赋值
									if(speciesArr[m].equals(speciesArr[k])){
										try {
											String speciesval=obsContentMap.get(speciesArr[k]).toString();	//键为空出现异常时，赋值"-"
											if("CO".equals(speciesArr[k])){									//值不为空，保留位数判断
												BigDecimal bd=(new BigDecimal(speciesval)).setScale(2, BigDecimal.ROUND_HALF_UP);
												obsSpeciesData.put(sdfNow.format(calDate_now), bd);
											}else{
												BigDecimal bd=(new BigDecimal(speciesval)).setScale(1, BigDecimal.ROUND_HALF_UP);
												obsSpeciesData.put(sdfNow.format(calDate_now), bd);
											}
											
										} catch (Exception e) {
											// TODO: handle exception
											obsSpeciesData.put(calDate_now,"-" );
										}
									}
								}
							}else{
								for(int k=0;k<speciesArr.length;k++){	//查询的观测数据为空时，进行赋值
									if(speciesArr[m].equals(speciesArr[k])){
										obsSpeciesData.put(calDate_now,"-" );
									}
								}
							}
							
						}	//循环页面开始年份和结束年份的差值--结束
						observationData.put(speciesArr[m],obsSpeciesData);
					}
//					Date scenarinoStartDate=tScenarinoDetail.getScenarinoStartDate();	//查询到的实时预报情景的开始时间
//					Date scenarinoEndDate=tScenarinoDetail.getScenarinoEndDate();		//查询到的实时预报情景的结束时间
					
					//查询的情景中结束时间如果没有从页面获取到的结束时间参数时，数据赋值为"-"进行表示
					//查询的情景中开始时间如果没有从页面获取到的开始时间参数时，则在FNL表中查询
					objData.put("模拟数据", simulationData);
					objData.put("观测数据", observationData);
				}
				
			}else{		//逐小时
				
			}
			return AmpcResult.build(0, "success",objData);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 空气质量预报--时间序列异常",e);
			return	AmpcResult.build(0, "error");
		}
		
	}

	
	@RequestMapping("Air/checkout")
	public AmpcResult find_checkout(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long userId=Long.valueOf(data.get("userId").toString());//用户id
		String mode=data.get("mode").toString();//站点或者平均
		String starttime=data.get("starttime").toString();//开始时间
		String endtime=data.get("endtime").toString();//开始时间
		String cityStation=data.get("cityStation").toString();//站点code
		String datetype=data.get("datetype").toString();

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
		SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
		Date start=daysdf.parse(starttime);
		Date end=daysdf.parse(endtime);
		Long beginTime = start.getTime();
		Long endTime2 = end.getTime();
		Long betweenDays = (long)((endTime2 - beginTime) / (1000 * 60 * 60 *24) + 0.5);
		Map<Integer,Map> validMap=new HashMap();//有效样本数Map
		Map<Integer,Map> exactMap=new HashMap();//准确样本数map
		List<Date> datelist=new ArrayList();
		Map<Integer,Map<Date,Map>> oAllspcMap=new HashMap();

		Map<Integer, Map<Date, Map<String, List>>> houroAllspcMap=new HashMap();
		Map<Integer,Map<Date,Map<String,List>>> hourpAllspcMap=new HashMap();
		Map<Integer,Map<Date,Map>> AllspcMap=new HashMap();//所有数据的Map集合
		List<Integer> howday=new ArrayList();//预报日期集合
		howday.add(-1);
		howday.add(1);
		howday.add(2);
		howday.add(3);
		Map<Integer,List<Date>> DatrMap=new HashMap();
		for(int a=0;a<=betweenDays;a++){
			//获取对应的情景起报日期
			Date times=daysdf.parse(starttime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(times);
			cal.add(Calendar.DATE, a);
			String addTimeDate =daysdf.format(cal.getTime());
			Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
			datelist.add(pathDate);
		}

		List<ScenarinoEntity> sclist=new ArrayList();
		List<ScenarinoEntity> dlist=new ArrayList();
		Map<Integer,Map> sumMapsum=new HashMap();
		Map<Integer,Map<String,BigDecimal>> psumMapsum=new HashMap();//所有日期所有预报日期的污染物数值和
		for(Integer how:howday){
			Map<String,BigDecimal> sumMap=new HashMap();//所有日期所预报日期的污染物数值和
			Map<Date,Map> psumMap=new HashMap();
			Map<Date,Map> dateMap=new HashMap();
			Map<Date,Map<String,List>> hourdateMap=new HashMap();
			Map<Date,Map> odateMap=new HashMap();
			Map<String,Integer> validspcMap=new HashMap();
			Map<String,Integer> spcexactMap=new HashMap();
			Map<Date, Map<String, List>> hourdatemap=new HashMap();
			List<Date> dalist=new ArrayList();
			for(Date thedate:datelist){//遍历时间集合，获取对应空气质量数据
				if(datetype.equals("day")){	
					String tables="T_OBS_DAILY_";
					DateFormat df = new SimpleDateFormat("yyyy");
					String nowTime= df.format(thedate);
					System.out.println(nowTime);
					tables+=nowTime;
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					scenarinoEntity.setMode(mode);
					Calendar cal = Calendar.getInstance();
					cal.setTime(thedate);
					cal.add(Calendar.DATE, how);
					String addTimeDate =daysdf.format(cal.getTime());
					Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
					scenarinoEntity.setDate(thedate);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);//查询对应的数据
				}else{
					String tables="T_OBS_HOURLY_";
					DateFormat df = new SimpleDateFormat("yyyy");
					String nowTime= df.format(thedate);
					System.out.println(nowTime);
					tables+=nowTime;
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					scenarinoEntity.setMode(mode);
					Calendar cal = Calendar.getInstance();
					cal.setTime(thedate);
					cal.add(Calendar.DATE, how);
					String addTimeDate =daysdf.format(cal.getTime());
					Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
					scenarinoEntity.setDate(thedate);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);//查询对应的数据
				}
				if(sclist.isEmpty()){
					LogUtil.getLogger().error("AppraisalController 未查询到观测数据！");
					return	AmpcResult.build(1001, "未查询到观测数据！");
					
				}
				Map spcMaps=new HashMap();
				Map<String, List> hourspcMaps=new HashMap();
				Map<String,Object> ospcMaps=new HashMap();
				Map<String,List> ohourspcMaps=new HashMap();
				Map<String,Object> odayspcMaps=new HashMap();
				ScenarinoEntity se=sclist.get(0);
				JSONObject obj=JSONObject.fromObject(se.getContent());
				if(datetype.equals("day")){
					Map<String,Object> observeMap=(Map)obj;
					if(!observeMap.isEmpty()){//判断是否为有效样本
						dalist.add(thedate);
						DatrMap.put(how, dalist);
						Iterator<Entry<String,Object>> iter=observeMap.entrySet().iterator();
						while(iter.hasNext()){
							Entry<String,Object> observe=iter.next();
							String spc=observe.getKey();
							String num=observe.getValue().toString();
							if(spc.equals("PM2_5")){
								spc="PM25";
							}
							if(!num.equals("-")){

								if(sumMap.get(spc)==null){
									sumMap.put(spc, new BigDecimal(num));	
								}else{
									sumMap.put(spc, sumMap.get(spc).add(new BigDecimal(num)));								
								}
							}else{
								if(sumMap.get(spc)==null){
									sumMap.put(spc, new BigDecimal(0));	
								}else{
									sumMap.put(spc, sumMap.get(spc).add(new BigDecimal(0)));									
								}
							}
							if(validspcMap.get(spc)==null){
								validspcMap.put(spc, 1);
							}else{
								validspcMap.put(spc, validspcMap.get(spc)+1);								
							}
							if(!num.equals("-")){
							odayspcMaps.put(spc, num);
							}else{
							odayspcMaps.put(spc, 0);
							}
						}
						
					}
				}else{
					Map<String,List<String>> observeMap=(Map)obj;
					if(!observeMap.isEmpty()){//判断是否为有效样本
						dalist.add(thedate);
						DatrMap.put(how, dalist);
						Iterator<Entry<String,List<String>>> iter=observeMap.entrySet().iterator();
						while(iter.hasNext()){
							Entry<String,List<String>> observe=iter.next();
							String spc=observe.getKey();
							List<String> numlist=observe.getValue();
							List<String> lists=new ArrayList();
							for(String num:numlist){
								if(spc.equals("PM2_5")){
									spc="PM25";
								}
								if(!num.equals("-")){

									if(sumMap.get(spc)==null){
										sumMap.put(spc, new BigDecimal(num));		
									}else{
										sumMap.put(spc, sumMap.get(spc).add(new BigDecimal(num)));									
									}
								}else{
									if(sumMap.get(spc)==null){
										sumMap.put(spc, new BigDecimal(0));	
									}else{
										sumMap.put(spc, sumMap.get(spc).add(new BigDecimal(0)));								
									}
								}
								if(validspcMap.get(spc)==null){
									validspcMap.put(spc, 1);
								}else{
									validspcMap.put(spc, validspcMap.get(spc)+1);						
								}
								
								if(!num.equals("-")){
									lists.add(num);
								}else{
									lists.add("0");
								}
							}//numList遍历
							ohourspcMaps.put(spc, lists);
						}
					}
				}
				hourdatemap.put(thedate,ohourspcMaps);
				odateMap.put(thedate, odayspcMaps);
			}//遍历时间集合
			validMap.put(how, validspcMap);//有效样本数
			oAllspcMap.put(how, odateMap);//所有观测数据day
			houroAllspcMap.put(how, hourdatemap);//所有观测数据小时
			sumMapsum.put(how, sumMap);//观测数据和
		}
		
		for(Integer how:howday){
			Map<String,BigDecimal> sumMap=new HashMap();//所有日期所预报日期的污染物数值和
			Map<String,BigDecimal> psumMap=new HashMap();
			Map<Date,Map> dateMap=new HashMap();
			Map<Date,Map> hourdateMap=new HashMap();
			Map<Date,Map> odateMap=new HashMap();
			Map<String,Integer> validspcMap=new HashMap();
			Map<String,Integer> spcexactMap=new HashMap();
			Map<Date,Map<String,List>> hourdatemap=new HashMap();
			for(Date thedate:datelist){//遍历时间集合，获取对应空气质量数据
				List<Date> dats=DatrMap.get(how);
				if(!dats.contains(thedate)){
					continue;
				}
				Map<String,List> ohourspcMaps=new HashMap();
				Map<String,Object> odayspcMaps=new HashMap();
				if(datetype.equals("day")){	
					Calendar cal = Calendar.getInstance();
					cal.setTime(thedate);
					if(how==-1){
						cal.add(Calendar.DATE, 1);
					}
					if(how==1){
						cal.add(Calendar.DATE, -1);
					}
					if(how==2){
						cal.add(Calendar.DATE, -2);
					}
					if(how==3){
						cal.add(Calendar.DATE, -3);
					}
					String addTimeDate =daysdf.format(cal.getTime());
					Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期


					List<ScenarinoEntity> ssslist=new ArrayList();
					Map<String,Object> scmap=new HashMap();
					TScenarinoDetail tScenarino=new TScenarinoDetail();
					tScenarino.setScenType("4");
					tScenarino.setPathDate(pathDate);
					//查询对应情景
					List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
					TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
					TMissionDetail tm=new TMissionDetail();
					tm.setMissionId(jztScenarino.getMissionId());
					//通过情景中的任务id查询任务，再通过任务查询domainid
					List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
					TMissionDetail thetm=tmlist.get(0);
					int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());
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
					scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setDay(jztScenarino.getScenarinoStartDate());
					scenarinoEntity.setsId(Long.valueOf(jztScenarino.getScenarinoId().toString()));
					scenarinoEntity.setTableName(tables);
					ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);
					
					
					ScenarinoEntity housr=ssslist.get(0);
					JSONObject objs=JSONObject.fromObject(housr.getContent());
					Map<String,Map<String,Object>> spcMap=(Map<String,Map<String,Object>>) objs;
					Iterator<Entry<String,Map<String,Object>>> iters=spcMap.entrySet().iterator();

					while(iters.hasNext()){
						Entry<String,Map<String,Object>> ite=iters.next();
						String spcs=ite.getKey();
						if(spcs.equals("O3_AVG")){
							spcs="O3";
						}
						BigDecimal p=new BigDecimal(ite.getValue().get("0").toString());
//						String ps=LevelUtil.Level(p);
//						String os=LevelUtil.Level(num);
						if(psumMap.get(spcs)==null){
							psumMap.put(spcs, p);
						}else{
							psumMap.put(spcs, psumMap.get(spcs).add(p));
							
						}
//						if(ps.equals(os)){
//							if(spcexactMap.get(spcs)==null){
//								spcexactMap.put(spcs, 1);
//							}else{
//								spcexactMap.put(spcs, spcexactMap.get(spcs)+1);							
//							}
//						}
						odayspcMaps.put(spcs, p);
					}
				}else{//逐日
					Calendar cal = Calendar.getInstance();
					cal.setTime(thedate);
					if(how==-1){
						cal.add(Calendar.DATE, 1);
					}
					if(how==1){
						cal.add(Calendar.DATE, -1);
					}
					if(how==2){
						cal.add(Calendar.DATE, -2);
					}
					if(how==3){
						cal.add(Calendar.DATE, -3);
					}
					String addTimeDate =daysdf.format(cal.getTime());
					Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期

					List<ScenarinoEntity> ssslist=new ArrayList();
					Map<String,Object> scmap=new HashMap();
					TScenarinoDetail tScenarino=new TScenarinoDetail();
					tScenarino.setScenType("4");
					tScenarino.setPathDate(pathDate);
					//查询对应情景
					List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
					TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
					TMissionDetail tm=new TMissionDetail();
					tm.setMissionId(jztScenarino.getMissionId());
					//通过情景中的任务id查询任务，再通过任务查询domainid
					List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
					TMissionDetail thetm=tmlist.get(0);
					int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());

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
					scenarinoEntity.setDomainId(Long.valueOf(domainId));
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setDay(jztScenarino.getScenarinoStartDate());
					scenarinoEntity.setsId(Long.valueOf(jztScenarino.getScenarinoId().toString()));
					scenarinoEntity.setTableName(tables);
					ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);	
					
					ScenarinoEntity housr=ssslist.get(0);
					JSONObject objs=JSONObject.fromObject(housr.getContent());
					Map<String,Map<String,List<Object>>> spcMap=(Map<String, Map<String, List<Object>>>) objs;
					
					Iterator<Entry<String,Map<String,List<Object>>>> iters=spcMap.entrySet().iterator();
					while(iters.hasNext()){
						Entry<String,Map<String,List<Object>>> ite=iters.next();
						String spce=ite.getKey();
						Map<String,List<Object>> spclmap=ite.getValue();
						List<Object> spclist=spclmap.get("0");
				
						for(int c=0;c<=23;c++){
							
							BigDecimal p=new BigDecimal(spclist.get(c).toString());
							BigDecimal o=null;
//							if(!numlists.get(c).equals("-")){
//								o=new BigDecimal(numlists.get(c));
//							}else{
//								o=new BigDecimal(0);
//							}
//							
//							String ps=LevelUtil.Level(p);
//							String os=LevelUtil.Level(o);
							
							if(psumMap.get(spce)==null){
								psumMap.put(spce, p);
							}else{
								psumMap.put(spce, psumMap.get(spce).add(p));
								
							}
//							if(ps.equals(os)){
//								if(spcexactMap.get(spce)==null){
//
//									spcexactMap.put(spce, 1);	
//								}else{
//									spcexactMap.put(spce, spcexactMap.get(spce)+1);							
//								}
//							}

						}
						ohourspcMaps.put(spce, spclist);
						
					}
				}//逐小时
				hourdatemap.put(thedate,ohourspcMaps);
				odateMap.put(thedate, odayspcMaps);
			}//遍历日期

			AllspcMap.put(how, odateMap);//所有预报数据
			hourpAllspcMap.put(how, hourdatemap);//所有预报数据hour
			psumMapsum.put(how, psumMap);//预测数据和
		}//遍历预测日期

		
		//开始计算数据
		
		
		
		
		
		Map<Integer,Map<String,Map<String,Double>>> aveMap=new HashMap();
//		AllspcMap.put(how, odateMap);//所有预报数据
//		hourpAllspcMap.put(how, hourdateMap);//所有预报数据hour
//		psumMapsum.put(how, psumMap);//预测数据和
//		validMap.put(how, validspcMap);//有效样本数
//		oAllspcMap.put(how, odateMap);//所有观测数据day
//		houroAllspcMap.put(how, hourdatemap);//所有观测数据小时
//		sumMapsum.put(how, sumMap);//观测数据和
		if(datetype.equals("day")){
			Iterator<Entry<Integer, Map<Date, Map>>> Alliter=oAllspcMap.entrySet().iterator();
			while(Alliter.hasNext()){
				Map<String,Integer> spcexactMap=new HashMap();
				Map<String,Map<String,Double>> spcdeMap=new HashMap();
				Entry<Integer, Map<Date, Map>> alls=Alliter.next();
				Integer day=alls.getKey();
				Map<Date,Map> alldateMap=alls.getValue();
				Iterator<Entry<Date,Map>> Alldateite=alldateMap.entrySet().iterator();
				while(Alldateite.hasNext()){
					Entry<Date,Map> dateite=Alldateite.next();
					Date date=dateite.getKey();
					Map<String,Object> spcMap=dateite.getValue();
					Iterator<Entry<String,Object>> Allspciter=spcMap.entrySet().iterator();
					while(Allspciter.hasNext()){
						Map<String,Double> suMap=new HashMap();
						Entry<String,Object> spciter=Allspciter.next();
						String spc=spciter.getKey();
						if(spc.equals("O3_8h")){
							continue;
						}
						BigDecimal num=new BigDecimal(spciter.getValue().toString());
						Integer vale=Integer.valueOf(validMap.get(day).get(spc).toString());
						
						String ps=LevelUtil.Level(new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()));
						String os=LevelUtil.Level(num);
						if(ps.equals(os)){
							if(spcexactMap.get(spc)==null){

								spcexactMap.put(spc, 1);
							}else{
								spcexactMap.put(spc, spcexactMap.get(spc)+1);				
							}
						}
					
						double opresult=num.subtract(new BigDecimal(sumMapsum.get(day).get(spc).toString()).divide(new BigDecimal(vale),2)).doubleValue();
						double ppresult=new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()).subtract(psumMapsum.get(day).get(spc).divide(new BigDecimal(vale),2)).doubleValue();
						double store =opresult*ppresult;
						double p2=Math.pow(ppresult,2);
						double o2=Math.pow(opresult,2);
						double o_p= new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()).subtract(num).doubleValue();
						Map amap=aveMap.get(day);
						if(amap!=null){
							if(amap.get(spc)==null){
								suMap.put("store", store);
								suMap.put("p2", p2);
								suMap.put("o2", o2);
								suMap.put("o_p", o_p);
								spcdeMap.put(spc, suMap);
								aveMap.put(day, spcdeMap);
							}else{
								suMap.put("store", spcdeMap.get(spc).get("store")+store);
								suMap.put("p2", spcdeMap.get(spc).get("p2")+p2);
								suMap.put("o2", spcdeMap.get(spc).get("o2")+o2);
								suMap.put("o_p", spcdeMap.get(spc).get("o_p")+o_p);
								spcdeMap.put(spc, suMap);
								aveMap.put(day, spcdeMap);
							}
							}else{
								suMap.put("store", store);
								suMap.put("p2", p2);
								suMap.put("o2", o2);
								suMap.put("o_p", o_p);
								spcdeMap.put(spc, suMap);
								aveMap.put(day, spcdeMap);
							}
					}
					
				}
				exactMap.put(day, spcexactMap);
			}
		}else{
			Iterator<Entry<Integer, Map<Date, Map<String, List>>>> Alliter=houroAllspcMap.entrySet().iterator();
			while(Alliter.hasNext()){
				Map<String,Integer> spcexactMap=new HashMap();
				Map<String,Map<String,Double>> spcdeMap=new HashMap();
				Entry<Integer, Map<Date, Map<String, List>>> alls=Alliter.next();
				Integer day=alls.getKey();
				Map<Date, Map<String, List>> alldateMap=alls.getValue();
				Iterator<Entry<Date, Map<String, List>>> Alldateite=alldateMap.entrySet().iterator();
				while(Alldateite.hasNext()){
					Entry<Date, Map<String, List>> dateite=Alldateite.next();
					Date date=dateite.getKey();
					Map<String, List> spcMap=dateite.getValue();
					Iterator<Entry<String, List>> Allspciter=spcMap.entrySet().iterator();
					while(Allspciter.hasNext()){
						Map<String,Double> suMap=new HashMap();
						Entry<String, List> spciter=Allspciter.next();
						String spc=spciter.getKey();
						if(spc.equals("O3_8h")){
							continue;
						}
						Map psm=psumMapsum.get(day);
						List<Object> numma=spciter.getValue();
						for(int s=0;s<=23;s++){
							BigDecimal num=new BigDecimal(numma.get(s).toString());
							
					
						
							String ps=LevelUtil.Level(new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()));
							String os=LevelUtil.Level(num);
							if(ps.equals(os)){
								if(spcexactMap.get(spc)==null){
									spcexactMap.put(spc, 1);
								}else{
									spcexactMap.put(spc, spcexactMap.get(spc)+1);			
								}
							}					
						Integer vale=Integer.valueOf(validMap.get(day).get(spc).toString());
						double opresult=num.subtract(new BigDecimal(sumMapsum.get(day).get(spc).toString()).divide(new BigDecimal(vale),2)).doubleValue();
						double ppresult=(new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()).subtract(new BigDecimal(sumMapsum.get(day).get(spc).toString()).divide(new BigDecimal(vale),2))).doubleValue();
						double store =opresult*ppresult;
						double p2=Math.pow(ppresult,2);
						double o2=Math.pow(opresult,2);
						double o_p= new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()).subtract(num).doubleValue();
						Map amap=aveMap.get(day);
						if(amap!=null){
						if(amap.get(spc)==null){
							suMap.put("store", store);
							suMap.put("p2", p2);
							suMap.put("o2", o2);
							suMap.put("o_p", o_p);
							spcdeMap.put(spc, suMap);
							aveMap.put(day, spcdeMap);
						}else{
							suMap.put("store", spcdeMap.get(spc).get("store")+store);
							suMap.put("p2", spcdeMap.get(spc).get("p2")+p2);
							suMap.put("o2", spcdeMap.get(spc).get("o2")+o2);
							suMap.put("o_p", spcdeMap.get(spc).get("o_p")+o_p);
							spcdeMap.put(spc, suMap);
							aveMap.put(day, spcdeMap);
						}
						}else{
							suMap.put("store", store);
							suMap.put("p2", p2);
							suMap.put("o2", o2);
							suMap.put("o_p", o_p);
							spcdeMap.put(spc, suMap);
							aveMap.put(day, spcdeMap);
						}
					}
					}
					
				}
				exactMap.put(day, spcexactMap);
			}
			
			
		}
		
		
		
		Map<Integer,Map<String,Map<String,String>>> lastMap=new HashMap();
		
		Iterator<Entry<Integer,Map<String,Map<String,Double>>>> aveite=aveMap.entrySet().iterator();
		while(aveite.hasNext()){
		Entry<Integer,Map<String,Map<String,Double>>> ave=aveite.next();
		Integer day=ave.getKey();
		Map<String,Map<String,Double>> spcmaps=ave.getValue();
		Iterator<Entry<String,Map<String,Double>>> spcs=spcmaps.entrySet().iterator();
		Map<String,Map<String,String>> spaMap=new HashMap();
		while(spcs.hasNext()){
		Entry<String,Map<String,Double>> spc=spcs.next();
		Map<String,String> StrMap=new HashMap();
		String sp=spc.getKey();
		Map<String,Double> arv=spc.getValue();
		double store=arv.get("store");
		double p2=arv.get("p2");
		double o2=arv.get("o2");
		double o_p=arv.get("o_p");
		double n=p2*o2;
		double v=0;
		BigDecimal vb=new BigDecimal(0);
		if(n!=0){
		v=store/Math.sqrt(n);//计算相关系数系数	
		vb=new BigDecimal(v).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		double bs=o_p/(Double.valueOf(sumMapsum.get(day).get(sp).toString()));
		BigDecimal bias=new BigDecimal(bs*100).setScale(1, BigDecimal.ROUND_HALF_UP);
		double exact=0;
		if(exactMap.get(day).get(sp)!=null){
			exact=Double.parseDouble(exactMap.get(day).get(sp).toString());
		}
		double valid=0;
		if(datetype.equals("day")){
			 valid=Double.parseDouble(validMap.get(day).get(sp).toString());
		}else{
			 valid=Double.parseDouble(validMap.get(day).get(sp).toString());
		}
	
		BigDecimal rate=new BigDecimal((exact/valid)*100).setScale(1, BigDecimal.ROUND_HALF_UP);
		
		
		StrMap.put("coefficient", vb.toString());
		StrMap.put("bias", bias.toString()+"%");
		StrMap.put("rate", rate.toString()+"%");
		spaMap.put(sp, StrMap);
		}
		lastMap.put(day, spaMap);
		}
		
		
		
		return	AmpcResult.ok(lastMap);
	}catch(Exception e){
		LogUtil.getLogger().error("AppraisalController 预报检验查询异常！",e);
		return	AmpcResult.build(1001, "预报检验查询异常！");
	}
	}
}
