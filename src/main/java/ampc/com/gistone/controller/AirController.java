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
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
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
				scenarinoEntity.setDomainId(domainId);
				scenarinoEntity.setMode(mode);
				scenarinoEntity.setDay(daytimes);
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
				scenarinoEntity.setDay(daytimes);
				scenarinoEntity.setsId(Integer.valueOf(jztScenarino.getScenarinoId().toString()));
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

	@SuppressWarnings("unchecked")
	@RequestMapping("Air/checkout")
	public AmpcResult find_checkout(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
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
			Map<Integer,Map<Date,Map<String,Object>>> oAllspcMap=new HashMap();
			
			Map<Integer,Map<Date,Map<String,List>>> houroAllspcMap=new HashMap();
			Map<Integer,Map> AllspcMap=new HashMap();//所有数据的Map集合
			List<Integer> howday=new ArrayList();//预报日期集合
			howday.add(-1);
			howday.add(1);
			howday.add(2);
			howday.add(3);

			for(int a=0;a<=betweenDays;a++){
				//获取对应的情景起报日期
				Date times=sdf.parse(starttime);
				Calendar cal = Calendar.getInstance();
				cal.setTime(times);
				cal.add(Calendar.DATE, a);
				String addTimeDate =daysdf.format(cal.getTime());
				Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
				datelist.add(pathDate);
			}

			List<ScenarinoEntity> sclist=new ArrayList();
			List<ScenarinoEntity> dlist=new ArrayList();
			Map<Integer,Map<String,BigDecimal>> sumMapsum=new HashMap();
			Map<Integer,Map<String,BigDecimal>> psumMapsum=new HashMap();//所有日期所有预报日期的污染物数值和
			for(Integer how:howday){
				Map<String,BigDecimal> sumMap=new HashMap();//所有日期所预报日期的污染物数值和
				Map<String,BigDecimal> psumMap=new HashMap();
				Map<Date,Map> dateMap=new HashMap();
				Map<Date,Map<String,List>> hourdateMap=new HashMap();
				Map<Date,Map<String,Object>> odateMap=new HashMap();
				Map<String,Integer> validspcMap=new HashMap();
				Map<String,Integer> spcexactMap=new HashMap();
				for(Date thedate:datelist){//遍历时间集合，获取对应空气质量数据
					if(datetype.equals("day")){	
						String tables="T_OBS_DAILY_";
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(starttime);
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
						scenarinoEntity.setDate(pathDate);
						sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);//查询对应的数据
					}else{
						String tables="T_OBS_HOURLY_";
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(starttime);
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
						scenarinoEntity.setDate(pathDate);
						sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);//查询对应的数据
					}
					Map spcMaps=new HashMap();
				
					Map<String, List> hourspcMaps=new HashMap();
					Map<String,Object> ospcMaps=new HashMap();
					ScenarinoEntity se=sclist.get(0);
					JSONObject obj=JSONObject.fromObject(se.getContent());
					if(datetype.equals("day")){
						Map<String,BigDecimal> observeMap=(Map)obj;
						if(!observeMap.isEmpty()){//判断是否为有效样本
							Iterator<Entry<String,BigDecimal>> iter=observeMap.entrySet().iterator();
							while(iter.hasNext()){
								Entry<String,BigDecimal> observe=iter.next();
								String spc=observe.getKey();
								BigDecimal num=observe.getValue();
								if(sumMap.get(spc)==null){
									sumMap.put(spc, num);		
								}else{
									sumMap.put(spc, sumMap.get(spc).add(num));									
								}
								if(validspcMap.get(spc)==null){
									validspcMap.put(spc, 1);		
								}else{
									validspcMap.put(spc, validspcMap.get(spc)+1);									
								}
								
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
								scenarinoEntity.setDomainId(domainId);
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(thedate);
								scenarinoEntity.setsId(Integer.valueOf(jztScenarino.getScenarinoId().toString()));
								scenarinoEntity.setTableName(tables);
								ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);
								ssslist.get(0);
								ScenarinoEntity housr=ssslist.get(0);
								JSONObject objs=JSONObject.fromObject(housr.getContent());
								Map<String,BigDecimal> spcMap=(Map<String,BigDecimal>) objs;
								Iterator<Entry<String,BigDecimal>> iters=spcMap.entrySet().iterator();

								while(iters.hasNext()){
									Entry<String,BigDecimal> ite=iters.next();
									String spcs=ite.getKey();
									BigDecimal p=ite.getValue();
									String ps=LevelUtil.Level(p);
									String os=LevelUtil.Level(num);
									if(psumMap.get(spcs)==null){
										psumMap.put(spcs, p);
									}else{
										psumMap.put(spcs, psumMap.get(spcs).add(p));
										
									}
									if(ps.equals(os)){
										if(spcexactMap.get(spcs)==null){
											spcexactMap.put(spcs, 1);		
										}else{
											spcexactMap.put(spcs, spcexactMap.get(spcs)+1);									
										}
									}
									spcMaps.put(spcs, p);
								}
								
								ospcMaps.put(spc, num);
							}	
						}
					}else{
						Map<String,List<BigDecimal>> observeMap=(Map)obj;
						if(!observeMap.isEmpty()){//判断是否为有效样本
					
							Iterator<Entry<String,List<BigDecimal>>> iter=observeMap.entrySet().iterator();
							while(iter.hasNext()){
								Entry<String,List<BigDecimal>> observe=iter.next();
								String spc=observe.getKey();
								List<BigDecimal> numlist=observe.getValue();
								for(BigDecimal num:numlist){
									if(sumMap.get(spc)==null){
										sumMap.put(spc, num);		
									}else{
										sumMap.put(spc, sumMap.get(spc).add(num));									
									}
									
									if(validspcMap.get(spc)==null){
										validspcMap.put(spc, 1);		
									}else{
										validspcMap.put(spc, validspcMap.get(spc)+1);									
									}
								}
								
								
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
								scenarinoEntity.setDomainId(domainId);
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(thedate);
								scenarinoEntity.setsId(Integer.valueOf(jztScenarino.getScenarinoId().toString()));
								scenarinoEntity.setTableName(tables);
								ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);

								ScenarinoEntity housr=ssslist.get(0);
								JSONObject objs=JSONObject.fromObject(housr.getContent());
								Map<String,List<BigDecimal>> spcMap=(Map<String,List<BigDecimal>>) objs;
								Iterator<Entry<String,List<BigDecimal>>> iters=spcMap.entrySet().iterator();
								while(iters.hasNext()){
									Entry<String,List<BigDecimal>> ite=iters.next();
									String spce=ite.getKey();
									List<BigDecimal> spclist=ite.getValue();
									for(int c=0;c<=23;c++){
										BigDecimal p=spclist.get(c);
										BigDecimal o=numlist.get(c);
										String ps=LevelUtil.Level(p);
										String os=LevelUtil.Level(o);
										
										if(psumMap.get(spce)==null){
											psumMap.put(spce, p);
										}else{
											psumMap.put(spce, psumMap.get(spce).add(p));
											
										}
										if(ps.equals(os)){
											if(spcexactMap.get(spce)==null){
				
												spcexactMap.put(spce, 1);		
											}else{
												spcexactMap.put(spce, spcexactMap.get(spc)+1);									
											}
										}

									}
									hourspcMaps.put(spce, spclist);
								}//时间分布判断
								
								ospcMaps.put(spc, numlist);
							}

						}

					}
					hourdateMap.put(thedate, hourspcMaps);
					odateMap.put(thedate, ospcMaps);
				}
				validMap.put(how, validspcMap);
				exactMap.put(how, spcexactMap);//准确样本数
				AllspcMap.put(how, dateMap);//所有预报数据
				houroAllspcMap.put(how, hourdateMap);
				oAllspcMap.put(how, odateMap);//所有观测数据
				sumMapsum.put(how, sumMap);//观测数据和
				psumMapsum.put(how, psumMap);//预测数据和
			}
			
			Map<Integer,Map<String,Map<String,Double>>> aveMap=new HashMap();
			

			if(datetype.equals("day")){
				Iterator<Entry<Integer,Map>> Alliter=AllspcMap.entrySet().iterator();
				while(Alliter.hasNext()){
					Map<String,Map<String,Double>> spcdeMap=new HashMap();
					Entry<Integer,Map> alls=Alliter.next();
					Integer day=alls.getKey();
					Map<Date,Map> alldateMap=alls.getValue();
					Iterator<Entry<Date,Map>> Alldateite=alldateMap.entrySet().iterator();
					while(Alldateite.hasNext()){
						Entry<Date,Map> dateite=Alldateite.next();
						Date date=dateite.getKey();
						Map<String,BigDecimal> spcMap=dateite.getValue();
						Iterator<Entry<String,BigDecimal>> Allspciter=spcMap.entrySet().iterator();
						while(Allspciter.hasNext()){
							Map<String,Double> suMap=new HashMap();
							Entry<String,BigDecimal> spciter=Allspciter.next();
							String spc=spciter.getKey();
							BigDecimal num=spciter.getValue();
							double ppresult=num.subtract(psumMapsum.get(day).get(spc).divide(new BigDecimal(validMap.get(day).get(spc).toString()))).doubleValue();
							double opresult=new BigDecimal(oAllspcMap.get(day).get(date).get(spc).toString()).subtract(sumMapsum.get(day).get(spc).divide(new BigDecimal(validMap.get(day).get(spc).toString()))).doubleValue();
							double store =opresult*ppresult;
							double p2=Math.pow(ppresult,2);
							double o2=Math.pow(opresult,2);
							double o_p= num.subtract(new BigDecimal(oAllspcMap.get(day).get(date).get(spc).toString())).doubleValue();
							if(aveMap.get(day)==null&&aveMap.get(day).get(spc)==null){
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
						}
						
					}
					
				}
			}else{
				Iterator<Entry<Integer,Map>> Alliter=AllspcMap.entrySet().iterator();
				while(Alliter.hasNext()){
					Map<String,Map<String,Double>> spcdeMap=new HashMap();
					Entry<Integer,Map> alls=Alliter.next();
					Integer day=alls.getKey();
					Map<Date,Map> alldateMap=alls.getValue();
					Iterator<Entry<Date,Map>> Alldateite=alldateMap.entrySet().iterator();
					while(Alldateite.hasNext()){
						Entry<Date,Map> dateite=Alldateite.next();
						Date date=dateite.getKey();
						Map<String,List<BigDecimal>> spcMap=dateite.getValue();
						Iterator<Entry<String,List<BigDecimal>>> Allspciter=spcMap.entrySet().iterator();
						while(Allspciter.hasNext()){
							Map<String,Double> suMap=new HashMap();
							Entry<String,List<BigDecimal>> spciter=Allspciter.next();
							String spc=spciter.getKey();
							List<BigDecimal> numl=spciter.getValue();
							for(int s=0;s<=23;s++){
								BigDecimal num=numl.get(s);
							double ppresult=num.subtract(psumMapsum.get(day).get(spc).divide(new BigDecimal(validMap.get(day).get(spc).toString()).multiply(new BigDecimal(24)))).doubleValue();
							double opresult=new BigDecimal(houroAllspcMap.get(day).get(date).get(spc).get(s).toString()).subtract(sumMapsum.get(day).get(spc).divide(new BigDecimal(validMap.get(day).get(spc).toString()).multiply(new BigDecimal(24)))).doubleValue();
							double store =opresult*ppresult;
							double p2=Math.pow(ppresult,2);
							double o2=Math.pow(opresult,2);
							double o_p= num.subtract(new BigDecimal(houroAllspcMap.get(day).get(date).get(spc).get(s).toString())).doubleValue();
							if(aveMap.get(day)==null&&aveMap.get(day).get(spc)==null){
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
						}
						}
						
					}
					
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
			double v=store/Math.sqrt(n);//计算相关系数系数
			BigDecimal vb=new BigDecimal(v).setScale(2, BigDecimal.ROUND_HALF_UP);
			double bs=o_p/(sumMapsum.get(day).get(sp).doubleValue());
			BigDecimal bias=new BigDecimal(bs).setScale(2, BigDecimal.ROUND_HALF_UP);
			double exact= Double.parseDouble(exactMap.get(day).get(sp).toString());
			double valid=Double.parseDouble(validMap.get(day).get(sp).toString());
			BigDecimal rate=new BigDecimal((exact/valid)*100).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			
			StrMap.put("coefficient", vb.toString());
			StrMap.put("bias", vb.toString());
			StrMap.put("rate", rate.toString()+"%");
			spaMap.put(sp, StrMap);
			}
			lastMap.put(day, spaMap);
			}
			
			
			return	AmpcResult.ok(lastMap);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 预报检验查询异常！",e);	
			return	AmpcResult.build(1000, "error");
		}
	}
}
