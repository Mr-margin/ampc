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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TObsMapper;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LevelUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	
	@Autowired
	private GetBySqlMapper getBySqlMapper;
//	@Autowired
//	private FindCode findCode;
	
	private int went=7;
	
	//定义公用的jackson帮助类
	private ObjectMapper mapper=new ObjectMapper();
	
	TScenarinoDetail tScenarinoDetail;
	
	/**
	 * 空气质量预报时间序列查询最小和最大可选时间
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@RequestMapping("/Air/get_time")
	public AmpcResult get_time(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ) throws NoSuchAlgorithmException, IOException {
		try{
			//设置跨域编码格式
			ClientUtil.SetCharsetAndHeader(request, response);
			//获取请求的参数
			Map<String, Object> data = (Map) requestDate.get("data");
			Date endDate;
			//获取用户id
			Object param=data.get("userId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("NativeAndNationController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			Long userId = Long.parseLong(param.toString());
			//返回给前台的数据
			JSONObject obj=new JSONObject();
			//获取一个Calendar对象并可以进行时间的计算，时区的指定
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    //获取当前时间的小时，判断是否大于早上8点，8点之后昨天的空气质量预报已经完成，8点之前只能取前天的空气质量预报
			//8点以后
			Date date=new Date();
		    if(calendar.get(Calendar.HOUR_OF_DAY) > 8){
		    	//获取昨天的日期
				calendar.add(Calendar.DATE, -1);
		    }else{
		    	//8点以前--获取前天的日期
		    	calendar.add(Calendar.DATE, -2);
		    }
		    //组织查询的参数
		    Map tsMap = new HashMap();
		    //用户id
		    tsMap.put("userId", userId);
		    //起报日期
		    tsMap.put("pathDate", sdf.format(calendar.getTime()));
		    //查询最近一次预报情景的结束时间
		    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
		    //结束时间
		    if(tScenarinoDetail==null){
		    	obj.put("msg", false);
		    	obj.put("msgContent", "数据库未查询到最近一次预报情景的结束日期");
		    	return AmpcResult.ok(obj);
		    }else{
		    	endDate=tScenarinoDetail.getScenarinoEndDate();
		    }
		    
			obj.put("maxtime", sdf.format(endDate));
		    //查询系统开始时间为最早可选时间
		    String sql = "select \"DAY\" from T_SCENARINO_FNL_DAILY_2017_"+userId+" where  ROWNUM = 1 group by \"DAY\" order by \"DAY\" ";
		    List<Map> list_FNL_DAILY = this.getBySqlMapper.findRecords(sql);
		    //获取查询到的最早系统时间并添加到json对象中
		    if(list_FNL_DAILY.size()>0){
		    	for(int i = 0;i<list_FNL_DAILY.size();i++){
					Map st_map = list_FNL_DAILY.get(i);
					obj.put("mintime", "".equals(st_map.get("DAY")) || st_map.get("DAY") == null ? "" : st_map.get("DAY").toString());//开始时间
		    	}
		    }else{
		    	//没有开始时间，返回错误信息：fnl缺少数据
		    	//或者使用预报的开始时间
		    	obj.put("mintime", tScenarinoDetail.getScenarinoStartDate());//开始时间
		    }
		    obj.put("nowtime", date.getTime());
		    obj.put("msg", true);
			LogUtil.getLogger().info("空气质量预报时间查询成功");
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
			int s=0;
			List<ScenarinoEntity> sclist=new ArrayList();
			Map<String,Object> scmap=new HashMap();
			TScenarinoDetail tScenarino=new TScenarinoDetail();
			tScenarino.setScenType("4");
			tScenarino.setPathDate(pathDate);
			//查询对应情景
			List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
				if(jztScenarinoDetail.isEmpty()){
					s=1;
					Date selectmaxpathdate = tScenarinoDetailMapper.selectmaxpathdate();
					Date selectminpathdate = tScenarinoDetailMapper.selectminpathdate();
					Long betweenDays = (long)((selectmaxpathdate.getTime() - selectminpathdate.getTime()) / (1000 * 60 * 60 *24) + 0.5);
					for(int a=0;a<betweenDays;a++){
						Date timess=sdf.parse(time);
						Calendar calc = Calendar.getInstance();
						calc.setTime(times);
						calc.add(Calendar.DATE, -(a+1));
						String addTimeDate1 =daysdf.format(calc.getTime());
						Date pathDate1=daysdf.parse(addTimeDate1);//对应情景起报日期
						TScenarinoDetail tScenarino1=new TScenarinoDetail();
						tScenarino1.setScenType("4");
						tScenarino1.setPathDate(pathDate1);
						List<TScenarinoDetail> jztScenarinoDetail1=tScenarinoDetailMapper.selectByEntity(tScenarino1);
						if(jztScenarinoDetail1.isEmpty()){
							continue;
						}
						TScenarinoDetail jztScenarino1=jztScenarinoDetail1.get(0);
						TMissionDetail tm1=new TMissionDetail();
						tm1.setMissionId(jztScenarino1.getMissionId());
						//通过情景中的任务id查询任务，再通过任务查询domainid
						TMissionDetail thetm1=tMissionDetailMapper.selectByPrimaryKey(jztScenarino1.getMissionId());
							
						int domainId1=Integer.valueOf(thetm1.getMissionDomainId().toString());
						
						if(datetype.equals("day")){//逐天
							String tables="T_SCENARINO_DAILY_";
							Date tims=jztScenarino1.getPathDate();
							DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(tims);
							tables+=nowTime+"_";
							tables+=userId;
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId((long)domainId1);
							
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.parseLong((jztScenarino1.getScenarinoId().toString())));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							String tables="T_SCENARINO_HOURLY_";
							Date tims=jztScenarino1.getPathDate();
							DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(tims);
							tables+=nowTime+"_";
							tables+=userId.toString();
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId(Long.valueOf(domainId1));
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(jztScenarino1.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}//时间分布判断
						if(!sclist.isEmpty()){
							break;
						}
					}
					
					
				}else{
			
		
		
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
				}
				if(sclist.isEmpty()){
					s=1;
					Date selectmaxpathdate = tScenarinoDetailMapper.selectmaxpathdate();
					Date selectminpathdate = tScenarinoDetailMapper.selectminpathdate();
					Long betweenDays = (long)((selectmaxpathdate.getTime() - selectminpathdate.getTime()) / (1000 * 60 * 60 *24) + 0.5);
					for(int a=0;a<betweenDays;a++){
						Date timess=sdf.parse(time);
						Calendar calc = Calendar.getInstance();
						calc.setTime(times);
						calc.add(Calendar.DATE, -(a+1));
						String addTimeDate1 =daysdf.format(calc.getTime());
						Date pathDate1=daysdf.parse(addTimeDate1);//对应情景起报日期
						TScenarinoDetail tScenarino1=new TScenarinoDetail();
						tScenarino1.setScenType("4");
						tScenarino1.setPathDate(pathDate1);
						List<TScenarinoDetail> jztScenarinoDetail1=tScenarinoDetailMapper.selectByEntity(tScenarino1);
						if(jztScenarinoDetail1.isEmpty()){
							continue;
						}
						TScenarinoDetail jztScenarino1=jztScenarinoDetail1.get(0);
						TMissionDetail tm1=new TMissionDetail();
						tm1.setMissionId(jztScenarino1.getMissionId());
						//通过情景中的任务id查询任务，再通过任务查询domainid
						List<TMissionDetail> tmlist1=tMissionDetailMapper.selectByEntity(tm1);
						TMissionDetail thetm1=tmlist1.get(0);	
						int domainId1=Integer.valueOf(thetm1.getMissionDomainId().toString());
						
						if(datetype.equals("day")){//逐天
							String tables="T_SCENARINO_DAILY_";
							Date tims=jztScenarino1.getPathDate();
							DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(tims);
							tables+=nowTime+"_";
							tables+=userId;
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId((long)domainId1);
							
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.parseLong((jztScenarino1.getScenarinoId().toString())));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							String tables="T_SCENARINO_HOURLY_";
							Date tims=jztScenarino1.getPathDate();
							DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(tims);
							tables+=nowTime+"_";
							tables+=userId.toString();
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId(Long.valueOf(domainId1));
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(jztScenarino1.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}//时间分布判断
						if(!sclist.isEmpty()){
							break;
						}
					}
					
					
				}

			//判断时均还是日均然后进行不同的解析
			Map<String,Object> spcmap=new HashMap();
			if(datetype.equals("hour")){
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					Map<String,Object> spmap;
					JSONObject obj=JSONObject.fromObject(content);
                    if(s==1){
                    	spmap=(Map)obj.get(daytime);
                    	if(spmap==null){
                    		LogUtil.getLogger().error("find_vertical 无当前时间数据！");
                			return	AmpcResult.build(1001, "无当前时间数据！",new HashMap());
                    	}
					}else{
						spmap=(Map)obj;
					}
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
					Map<String,Object> spmap;
					JSONObject obj = JSONObject.fromObject(content);
					 if(s==1){
	                    	spmap=(Map)obj.get(daytime);
	                    	if(spmap==null){
	                    		LogUtil.getLogger().error("find_vertical 无当前时间数据！");
	                			return	AmpcResult.build(1001, "无当前时间数据！",new HashMap());
	                    	}
						}else{
							spmap=(Map)obj;
						}

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
	 * @param requestDate
	 * @param request
	 * @param response
 	 * @return
	 */
	@RequestMapping("Air/findAllTimeSeries")
	public AmpcResult findAllTimeSeries(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			//接收参数
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			//获取用户ID
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AirController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			Long userId=Long.parseLong(param.toString());	//用户id
			//获取站点类型
			param=data.get("mode");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AirController  站点类型为空或出现非法字符!");
				return AmpcResult.build(1003, "站点类型为空或出现非法字符!");
			}
			String mode=param.toString();
			//开始日期
			param=data.get("startDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AirController  日期为空或出现非法字符!");
				return AmpcResult.build(1003, "日期为空或出现非法字符!");
			}
			String startDate = param.toString();
			//结束日期
			param=data.get("endDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AirController  日期为空或出现非法字符!");
				return AmpcResult.build(1003, "日期为空或出现非法字符!");
			}
			String endDate = param.toString();
			//选择站点值
			String cityStation=data.get("cityStation").toString();	
			//逐日或逐小时
			String datetype=data.get("datetype").toString();			
			//空间分辨率
			param=data.get("domain");
			if(!RegUtil.CheckParameter(param, "Integer", null, false)){
				LogUtil.getLogger().error("AirController  空间分辨率为空或出现非法字符!");
				return AmpcResult.build(1003, "空间分辨率为空或出现非法字符!");
			}
			int domain=Integer.valueOf(param.toString());
			//页面选择的页签值
			param=data.get("changeType");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AirController  页签值为空或出现非法字符!");
				return AmpcResult.build(1003, "页签值为空或出现非法字符!");
			}
			String tabType=param.toString();
			
			Long missionId;					//查询到的任务id
			TMissionDetail tMissionDetail;	//任务表的信息
			Long domainId = null;			//存放查询的模拟范围id
			String tables;					//存放拼接的表名
			SimpleDateFormat sdfYear;		//声明格式化时间对象
			Date dateYear;					//声明年份对象
			String strYear;					//声明String类型对象
			ScenarinoEntity scenarinoEntity = null;
			int differenceVal = 0;			//声明结束时间和开始时间差值
			Calendar calendar;				//声明对象
			String  contents;				//存放查询到的模拟数据
			Date calDate = null;
			String calDateStr;
			//污染物--逐日模拟数据
			Object[] simulationArr={"BC","PM25","CO","O3_1_MAX","NO2","NO3","PMFINE","SO2","PM10","SO4","AQI","O3_8_MAX","NH4","O3_AVG","OM"};
			//污染物--逐小时模拟数据
			Object[] simulationHour={"BC","O3","PM25","CO","NO2","NO3","OC","PMFINE","SO2","PM10","SO4","AQI","NH4","OM"};
			//污染物--观测数据
			Object[] speciesArr={"NO2","PM2_5","O3","SO2","PM10","AQI","O3_8h","CO"};
			//气象要素--模拟数据
//			Object[] meteormnArr={"TEMP","PRSFC","PT","RH","WSPD"};	
			Object[] meteormnArr={"温度","湿度","风速","气压","降水"};
//			Object[] meteorgcArr={"TEMP","PRSFC","PT","RH","WSPD"};	//气象要素--观测数据--暂无
			
			calendar = Calendar.getInstance();
			Map contentmap ;
			//逐小时 24小时存储值
			String hourOneVal;		
			//存放模拟和观测数据 
			Map objData=new HashMap();
			//返给页面的模拟数据
			Map simulationData=new HashMap();
			//存放查询到的模拟数据
			Map contentmapData=new HashMap();
			//日期格式化
			SimpleDateFormat sdfNow;
			//当前时间
			Date dateNow;
			//当前日期字符串
			String strDateNow;	
			//污染物页签开始
			if("wrw".equals(tabType)){		
				//逐日
				if("day".equals(datetype)){		
					//查询当天的情景（未完成则查询前一天）
					sdfNow=new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdfyear=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//当前时间
					dateNow=new Date();
					strDateNow=sdfNow.format(dateNow);
					//获取开始时间和结束时间差值
					differenceVal = (int) (( (sdfNow.parse(endDate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
					//当日日期大于结束日期的参数查询FNL
					if(dateNow.getTime()>(sdfNow.parse(endDate)).getTime()){
						//查询全部日期的物种数据
						for(int i=0;i<=differenceVal;i++){
							calendar.setTime(sdfNow.parse(endDate));
							calendar.add(Calendar.DAY_OF_MONTH, -i);
							calDate = calendar.getTime();
							calDateStr=sdfNow.format(calDate);
							
							tables="T_SCENARINO_FNL_DAILY_";		//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);		
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setDay(calDate);
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
							if(scenarinoEntity==null||"".equals(scenarinoEntity.toString())||"{}".equals(scenarinoEntity.toString())||"null".equals(scenarinoEntity.toString())){
								//循环全部物种
								contentmapData.put(sdfNow.format(calDate), "{}");	//数据--空
							}else{
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));	
							}
						}	//查询全部日期的物种数据结束
					}else{
						//8点以后
						if(calendar.get(Calendar.HOUR_OF_DAY) > 8){
						//查询模拟表
						//查询起报日期的sid
						calendar.add(Calendar.DATE, -1);//获取昨天的日期
					    Map tsMap = new HashMap();
					    tsMap.put("userId", userId);
					    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
					    //获取sid
					    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
						
					    tables="T_SCENARINO_DAILY_";	//表名+年份+userid
						sdfYear=new SimpleDateFormat("yyyy");	//当前年份
						dateYear=new Date();
						strYear=sdfYear.format(dateYear);
						tables+=strYear+"_";
						tables+=userId;
						scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(domain);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
						scenarinoEntity.setTableName(tables);
						scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);
						if(scenarinoEntity!=null){
							//获取模拟数据
							contents=scenarinoEntity.getContent();
							//模拟父数据
							contentmapData=mapper.readValue(contents, Map.class);
						}else{
							//获取模拟数据为空时赋值"{}"
							contents="{}";
							//模拟父数据
							contentmapData=mapper.readValue(contents, Map.class);
						}
						Calendar calendar1 = Calendar.getInstance();
						calendar1.add(Calendar.DATE, -2);
						Date nowdate=calendar1.getTime();
						String strdate=sdfNow.format(nowdate);
						//开始日期
						Date newDate=sdfNow.parse(strdate);
						//查询fnl表Today-2-开始时间
						int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_SCENARINO_FNL_DAILY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//未查询到FNL表的模拟数据
								if(scenarinoEntity==null){
									contentmapData.put(sdfNow.format(calDate),"{}");
								}else{
									//查询到FNL表的模拟数据
									//循环添加到map中
									contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
								}
								
							}
							
					    }else{//8点以前
					    	
					    	//查询模拟表
							//查询起报日期的sid
					    	//获取前天的日期
							calendar.add(Calendar.DATE, -2);
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							
						    tables="T_SCENARINO_DAILY_";	//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);
							//查询到的模拟数据不为空值
							if(scenarinoEntity!=null){
								//获取模拟数据
								contents=scenarinoEntity.getContent();
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}else{
								//获取模拟数据为空时赋值"{}"
								contents="{}";
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}
							Calendar calendar1 = Calendar.getInstance();
							calendar1.add(Calendar.DATE, -3);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_SCENARINO_FNL_DAILY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
							}
					    }
					}
					
						//出数据
						//循环所有物种名称---并给日期赋值
						for(int s=0;s<simulationArr.length;s++){
						//单个物种所有日期的数据
						Map speciesDayData=new HashMap();
						//循环全部日期
//							for(Object dayKey:contentmapData.keySet() ){
						for(int m=0;m<=differenceVal;m++){
							Calendar calendarwrw = Calendar.getInstance();
							calendarwrw.setTime(sdfNow.parse(startDate));
							calendarwrw.add(Calendar.DAY_OF_MONTH, +m);
							Date calDatewrw = calendarwrw.getTime();
							String dayKey=sdfNow.format(calDatewrw);

								//单个日期中的全部物种数据
							Object speciesobj=contentmapData.get(dayKey);	
							if(speciesobj==null||"{}".equals(speciesobj)||"".equals(speciesobj)||"null".equals(speciesobj)||"NULL".equals(speciesobj)){
								speciesDayData.put(dayKey,"-");
							}else{
								Map<String,Object> speciesMap= (Map)speciesobj;
								//单个物种数据
								Object speciesOne=speciesMap.get(simulationArr[s].toString());	
								Map speciesOneMap=(Map)speciesOne;
								String speciesOneVal=speciesOneMap.get("0").toString();
								if("CO".equals(simulationArr[s])){
									//判断值为空时赋值"-"
									if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		
										speciesDayData.put(dayKey,"-");
									}else{
										BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
										speciesDayData.put(dayKey,bd);
									}
								}else{
									//判断值为空时赋值"-"
									if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		
										speciesDayData.put(dayKey,"-");
									}else{
										BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
										speciesDayData.put(dayKey,bd);
									}
								}
							}
						}
						simulationData.put(simulationArr[s].toString(),speciesDayData);
					}
					
						/*
						 * 查询观测数据
						 */
						//返给页面的观测数据
						HashMap<Object, Object> observationData=new HashMap<Object, Object>();	
						//查询观测数据开始
						HashMap<String, Object> obsMap=new HashMap<String, Object>();	
						obsMap.put("city_station",cityStation);
						String modes;	//用于存放新的mode
						if("point".equals(mode)){
							 modes="station";
						}else{
							 modes=mode;
						}
						obsMap.put("mode",modes);
						String tables_obs="T_OBS_DAILY_";
						DateFormat df_obs = new SimpleDateFormat("yyyy");
						Date nowDate=new Date();
						//截取该任务的开始时间来改变查询的表格
						String nowYear_obs= df_obs.format(nowDate);		
						tables_obs+=nowYear_obs;
						//查询的表格
						obsMap.put("tableName",tables_obs);				
						
						for(int m=0;m<speciesArr.length;m++){	
							HashMap obsSpeciesData=new HashMap();
							//循环页面开始年份和结束年份的差值--开始
							for(int j=0;j<=differenceVal;j++){		
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, (j+1));
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								
								calendar.setTime(sdfNow.parse(calDateStr));
								calendar.add(Calendar.DAY_OF_MONTH, -1);
								Date calDates = calendar.getTime();
								String calDateStrs=sdfNow.format(calDates);
								Date calDate_now=sdfNow.parse(calDateStrs);
								obsMap.put("date", calDate_now);
								//查询观测数据
								ObsBean obsBeans=tObsMapper.queryUnionResult(obsMap);	
								if(!"".equals(obsBeans)&&obsBeans!=null&&!"{}".equals(obsBeans)&&!"null".equals(obsBeans)&&!"NULL".equals(obsBeans)){
									String obsContent=obsBeans.getContent().toString();
									Map obsContentobj=mapper.readValue(obsContent, Map.class);
									//查询的观测数据不为空时，进行赋值
									for(int k=0;k<speciesArr.length;k++){		
										if(speciesArr[m].equals(speciesArr[k])){
											if("".equals(obsContentobj.toString())||"null".equals(obsContentobj.toString())||"NULL".equals(obsContentobj.toString())||"{}".equals(obsContentobj.toString())||obsContentobj==null){
												obsSpeciesData.put(sdfNow.format(calDate_now), "-");
											}else{
												String speciesval=obsContentobj.get(speciesArr[k]).toString();
												//键为空出现异常时，赋值"-"
												if("-".equals(speciesval)||"".equals(speciesval)||"null".equals(speciesval)||"NULL".equals(speciesval)||"{}".equals(speciesval)||speciesval==null){
													obsSpeciesData.put(sdfNow.format(calDate_now), "-");
												}else{
													if("CO".equals(speciesArr[k])){
														//值不为空，保留位数判断
														BigDecimal bd=(new BigDecimal(speciesval)).setScale(2, BigDecimal.ROUND_HALF_UP);
														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
													}else{
														BigDecimal bd=(new BigDecimal(speciesval)).setScale(1, BigDecimal.ROUND_HALF_UP);
														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
													}
												}
											}	
										}
									}
								}else{
									//查询的观测数据为空时，进行赋值
									for(int k=0;k<speciesArr.length;k++){	
										if(speciesArr[m].equals(speciesArr[k])){
											obsSpeciesData.put(sdfNow.format(calDate_now),"-" );
										}
									}
								}
							}	//循环页面开始年份和结束年份的差值--结束
							if("PM2_5".equals(speciesArr[m])){
								observationData.put("PM25",obsSpeciesData);
							}else if("O3".equals(speciesArr[m])){
								observationData.put("O3_AVG",obsSpeciesData);
							}else if("O3_8h".equals(speciesArr[m])){
								observationData.put("O3_8_MAX",obsSpeciesData);
							}else{
								observationData.put(speciesArr[m],obsSpeciesData);
							}
						}
						//查询的情景中结束时间如果没有从页面获取到的结束时间参数时，数据赋值为"-"进行表示
						//查询的情景中开始时间如果没有从页面获取到的开始时间参数时，则在FNL表中查询
						objData.put("观测数据", observationData);
						objData.put("模拟数据", simulationData);
				}else{		
					//-----------------------------------------逐小时--开始------------------------------------------------//
					//查询当天的情景（未完成则查询前一天）
					sdfNow=new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdfyear=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//当前时间
					dateNow=new Date();
					strDateNow=sdfNow.format(dateNow);
					//获取开始时间和结束时间差值
					differenceVal = (int) (( (sdfNow.parse(endDate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
					//当日日期大于结束日期的参数查询FNL
					if(dateNow.getTime()>(sdfNow.parse(endDate)).getTime()){
						//查询全部日期的物种数据
						for(int i=0;i<=differenceVal;i++){
							calendar.setTime(sdfNow.parse(endDate));
							calendar.add(Calendar.DAY_OF_MONTH, -i);
							calDate = calendar.getTime();
							calDateStr=sdfNow.format(calDate);
							
							tables="T_SCENARINO_FNL_HOURLY_";		//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);		
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setDay(calDate);
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
							if(scenarinoEntity==null||"".equals(scenarinoEntity.toString())||"{}".equals(scenarinoEntity.toString())||"null".equals(scenarinoEntity.toString())){
								//循环全部物种
								
								contentmapData.put(sdfNow.format(calDate), "{}");	//数据--空
							}else{
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));	
							}
						}	//查询全部日期的物种数据结束
					}else{
						
						 if(calendar.get(Calendar.HOUR_OF_DAY) > 8){
							//8点以后
							//查询模拟表
							//查询起报日期的sid
							//获取昨天的日期
							calendar.add(Calendar.DATE, -1);
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //根据用户id和起报日期查询获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							//查询逐小时模拟数据
						    //表名
						    tables="T_SCENARINO_HOURLY_";	
							sdfYear=new SimpleDateFormat("yyyy");	
							dateYear=new Date();
							//当前年份
							strYear=sdfYear.format(dateYear);
							//+年份
							tables+=strYear+"_";
							//+userid
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);
							//查询到的模拟数据不为空值
							if(scenarinoEntity!=null){
								//获取模拟数据
								contents=scenarinoEntity.getContent();
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}else{
								//获取模拟数据为空时赋值"{}"
								contents="{}";
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}
							//该方法返回一个日历Calendar
							Calendar calendar1 = Calendar.getInstance();
							//当天日期减2
							calendar1.add(Calendar.DATE, -2);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_SCENARINO_FNL_HOURLY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
								if(scenarinoEntity==null){
									contentmapData.put(sdfNow.format(calDate), "{}");
								}else{
									contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
								}
								
							}
							
					    }else{
					    	//8点以前
					    	//查询模拟表
							//查询起报日期的sid
							calendar.add(Calendar.DATE, -2);//获取前天的日期
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							
						    tables="T_SCENARINO_HOURLY_";	//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);
							//查询到的模拟数据不为空值
							if(scenarinoEntity!=null){
								//获取模拟数据
								contents=scenarinoEntity.getContent();
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}else{
								//获取模拟数据为空时赋值"{}"
								contents="{}";
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}
							Calendar calendar1 = Calendar.getInstance();
							calendar1.add(Calendar.DATE, -3);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_SCENARINO_FNL_HOURLY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
							}
					    	
					    }
						
					}
					
					//出数据
					//循环全部物种名称
					for(int z=0;z<simulationHour.length;z++){
						//单个物种所有日期的数据
						Map speciesData=new HashMap();
						//循环全部数据，key为单个日期
//						for(Object dayKey:contentmapData.keySet() ){
						for(int m=0;m<=differenceVal;m++){
							Calendar calendarwrw = Calendar.getInstance();
							calendarwrw.setTime(sdfNow.parse(startDate));
							calendarwrw.add(Calendar.DAY_OF_MONTH, +m);
							Date calDatewrw = calendarwrw.getTime();
							String dayKey=sdfNow.format(calDatewrw);	
							
							
							//模拟单个日期24小时数据
							Map hourData=new HashMap();
							//获取单个日期中的全部物种数据
							Object speciesobj=contentmapData.get(dayKey);
							//该日期中没有查询到数据时
							if(speciesobj==null||"{}".equals(speciesobj)||"".equals(speciesobj)||"null".equals(speciesobj)||"NULL".equals(speciesobj)){
								for(int h=0;h<24;h++){
									hourData.put(h, "-");
								}
								speciesData.put(dayKey,hourData);
							}else{
								
								Map<String,Object> speciesMap= (Map)speciesobj;
								Object speciesOne=speciesMap.get(simulationHour[z].toString());
								
								if("null".equals(speciesOne)||"NULL".equals(speciesOne)||speciesOne==null||"".equals(speciesOne)){
									for(int h=0;h<24;h++){
										hourData.put(h, "-");
									}
									speciesData.put(dayKey,hourData);
								}else{
									Map speciesOneMap= (Map)speciesOne;
									List speciesOneVal=(List) speciesOneMap.get("0");
									if("CO".equals(simulationHour[z].toString())){
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
											for(int h=0;h<speciesOneVal.size();h++){
												hourData.put(h, "-");
											}
											speciesData.put(dayKey,hourData);
										}else{
											for(int h=0;h<speciesOneVal.size();h++){
												hourOneVal=speciesOneVal.get(h).toString();
												if("-".equals(hourOneVal)||"".equals(hourOneVal)||"null".equals(hourOneVal)||"NULL".equals(hourOneVal)||"{}".equals(hourOneVal)||hourOneVal==null){
													hourData.put(h, "-");
												}else{
													BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
													hourData.put(h, bd);
												}
												
											}
											speciesData.put(dayKey,hourData);
										}
									}else{
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)||"{}".equals(speciesOneVal)||"NULL".equals(speciesOneVal)||"null".equals(speciesOneVal)){		//判断是否有值
											for(int h=0;h<speciesOneVal.size();h++){
												hourData.put(h, "-");
											}
											speciesData.put(dayKey,hourData);
										}else{
											for(int h=0;h<speciesOneVal.size();h++){
												
												hourOneVal=speciesOneVal.get(h).toString();
												if("-".equals(hourOneVal)||"".equals(hourOneVal)||"null".equals(hourOneVal)||"NULL".equals(hourOneVal)||"{}".equals(hourOneVal)||hourOneVal==null){
													hourData.put(h, "-");
												}else{
													BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
													hourData.put(h, bd);
												}
												
											}
											speciesData.put(dayKey,hourData);
										}
									}
								}
							
							}
						}
						simulationData.put(simulationHour[z].toString(),speciesData);
					}	//循环物种结束
					
					
					/*
					 * 查询观测数据
					 */
					//返给页面的观测数据
					HashMap observationData=new HashMap();		
					//查询观测数据开始
					HashMap<String, Object> obsMap=new HashMap<String, Object>();	
					obsMap.put("city_station",cityStation);
					//用于存放新的mode
					String modes;	
					if("point".equals(mode)){
						 modes="station";
					}else{
						 modes=mode;
					}
					obsMap.put("mode",modes);
					String tables_obs="T_OBS_HOURLY_";
					DateFormat df_obs = new SimpleDateFormat("yyyy");
					Date nowDate=new Date();
					//截取该任务的开始时间来改变查询的表格
					String nowYear_obs= df_obs.format(nowDate);		
					tables_obs+=nowYear_obs;
					//查询的表格
					obsMap.put("tableName",tables_obs);				
					//循环全部逐小时物种
					for(int m=0;m<speciesArr.length;m++){
						//单个物种所有日期数据
						HashMap obsSpeciesData=new HashMap();
						//循环页面开始年份和结束年份的差值--开始
						for(int j=0;j<=differenceVal;j++){		
							//观测24小时数据
							Map obsHourData=new HashMap();	
							//查询单个日期观测数据
							calendar.setTime(sdfNow.parse(startDate));
							calendar.add(Calendar.DAY_OF_MONTH, +j);
							Date calDates = calendar.getTime();
							String calDateStrs=sdfNow.format(calDates);
							Date calDate_now=sdfNow.parse(calDateStrs);
							obsMap.put("date", calDate_now);
							ObsBean obsBeans=tObsMapper.queryUnionResult(obsMap);
							//查询到有数据
							if(!"".equals(obsBeans)&&obsBeans!=null&&!"{}".equals(obsBeans)){	
								String obsContent=obsBeans.getContent().toString();
								//单条观测数据的content属性无具体的数据，进行数据赋值
								if("".equals(obsContent)||obsContent==null||"null".equals(obsContent)||"NULL".equals(obsContent)||"{}".equals(obsContent)){	
									for(int k=0;k<speciesArr.length;k++){	//查询的观测数据为空时，进行赋值
										if(speciesArr[m].equals(speciesArr[k])){
											for(int d=0;d<24;d++){
												obsHourData.put(d, "-");
											}
											obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
										}
									}
								}else{		
									//单条观测数据的content属性有具体的数据
									Map obsContentobj=mapper.readValue(obsContent, Map.class);
									//循环定义好的逐小时物种
									for(int k=0;k<speciesArr.length;k++){		
										//判断是否和第一次循环的物种是否一致
										if(speciesArr[m].equals(speciesArr[k])){	
												List speciesval=(List) obsContentobj.get(speciesArr[k]);	
												if("CO".equals(speciesArr[k])){									
													for(int i=0;i<24;i++){
														String speciesVal=speciesval.get(i).toString();
														if("".equals(speciesVal)||"null".equals(speciesVal)||"NULL".equals(speciesVal)||"{}".equals(speciesVal)||speciesVal==null||"-".equals(speciesVal)){
															obsHourData.put(i, "-");
														}else{
															BigDecimal bd=(new BigDecimal(speciesVal)).setScale(2, BigDecimal.ROUND_HALF_UP);
															obsHourData.put(i, bd);
														}
													}
													obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
												}else{
													for(int i=0;i<24;i++){
														String speciesVal=speciesval.get(i).toString();
														if("".equals(speciesVal)||"null".equals(speciesVal)||"NULL".equals(speciesVal)||"{}".equals(speciesVal)||speciesVal==null||"-".equals(speciesVal)){
														obsHourData.put(i, "-");
														}else{
															BigDecimal bd=(new BigDecimal(speciesVal)).setScale(1, BigDecimal.ROUND_HALF_UP);
															obsHourData.put(i, bd);
														}
													}
													obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
												}
										}
									}
								}
							}else{
								//查询的观测数据为空时，进行"-"赋值
								//循环全部物种名称
								for(int k=0;k<speciesArr.length;k++){	
									if(speciesArr[m].equals(speciesArr[k])){
										//循环24小时
										for(int d=0;d<24;d++){
											//为每个小时赋值"-"
											obsHourData.put(d, "-");
										}
										obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
									}
								}
							}
							
						}	//循环页面开始年份和结束年份的差值--结束
						//修改返给页面的物种名称
						if("PM2_5".equals(speciesArr[m])){
							observationData.put("PM25",obsSpeciesData);
						}else if("O3_8h".equals(speciesArr[m])){
							observationData.put("O3_8_MAX",obsSpeciesData);
						}else{
							observationData.put(speciesArr[m],obsSpeciesData);
						}
					}
					objData.put("观测数据", observationData);
					objData.put("模拟数据", simulationData);
				}
			// ----------------------------------------查询空气质量预报--气象要素页签开始---------------------------------------//
			}else if("qxys".equals(tabType)){	
				//逐日
				if("day".equals(datetype)){
					
					sdfNow=new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdfyear=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//当前时间
					dateNow=new Date();
					strDateNow=sdfNow.format(dateNow);
					//获取开始时间和结束时间差值
					differenceVal = (int) (( (sdfNow.parse(endDate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
					//当日日期大于结束日期的参数查询FNL
					if(dateNow.getTime()>(sdfNow.parse(endDate)).getTime()){
						//查询全部日期的物种数据
						for(int i=0;i<=differenceVal;i++){
							calendar.setTime(sdfNow.parse(endDate));
							calendar.add(Calendar.DAY_OF_MONTH, -i);
							calDate = calendar.getTime();
							calDateStr=sdfNow.format(calDate);
							
							tables="T_METEOR_FNL_DAILY_";		//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);		
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setDay(calDate);
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
							if(scenarinoEntity==null||"".equals(scenarinoEntity.toString())||"{}".equals(scenarinoEntity.toString())||"null".equals(scenarinoEntity.toString())){
								//循环全部物种
								
								contentmapData.put(sdfNow.format(calDate), "{}");	//数据--空
							}else{
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));	
							}
						}	//查询全部日期的物种数据结束
					}else{
						//8点以后
						if(calendar.get(Calendar.HOUR_OF_DAY) > 8){
							 
						//查询模拟表
						//查询起报日期的sid
						calendar.add(Calendar.DATE, -1);//获取昨天的日期
					    Map tsMap = new HashMap();
					    tsMap.put("userId", userId);
					    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
					    //获取sid
					    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
						
					    tables="T_METEOR_DAILY_";	//表名+年份+userid
						sdfYear=new SimpleDateFormat("yyyy");	//当前年份
						dateYear=new Date();
						strYear=sdfYear.format(dateYear);
						tables+=strYear+"_";
						tables+=userId;
						scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(domain);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
						scenarinoEntity.setTableName(tables);
						scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);   
						if(scenarinoEntity==null){
							System.out.println("未查询到模拟GFS数据");
						}else{
							contents=scenarinoEntity.getContent();		//获取模拟数据
							contentmapData=mapper.readValue(contents, Map.class);	//模拟父数据
						}
						
						Calendar calendar1 = Calendar.getInstance();
						calendar1.add(Calendar.DATE, -2);
						Date nowdate=calendar1.getTime();
						String strdate=sdfNow.format(nowdate);
						//开始日期
						Date newDate=sdfNow.parse(strdate);
						//查询fnl表Today-2-开始时间
						int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_METEOR_FNL_DAILY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								if(scenarinoEntity==null){
									//循环添加到map中
									contentmapData.put(sdfNow.format(calDate), "{}");
								}else{
									//循环添加到map中
									contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
								}
								
							}
							
					    }else{//8点以前
					    	
					    	//查询模拟表
							//查询起报日期的sid
							calendar.add(Calendar.DATE, -2);//获取前天的日期
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							
						    tables="T_METEOR_DAILY_";	//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);   
							
							contents=scenarinoEntity.getContent();		//获取模拟数据
							contentmapData=mapper.readValue(contents, Map.class);	//模拟父数据
							
							Calendar calendar1 = Calendar.getInstance();
							calendar1.add(Calendar.DATE, -3);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_METEOR_FNL_DAILY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
							}
					    }
					}
					
						//出数据
						//循环所有物种名称---并给日期赋值
							for(int s=0;s<meteormnArr.length;s++){
							//单个物种所有日期的数据
							Map speciesDayData=new HashMap();
							//循环全部日期
//							for(Object dayKey:contentmapData.keySet() ){
							for(int m=0;m<=differenceVal;m++){
								Calendar calendarwrw = Calendar.getInstance();
								calendarwrw.setTime(sdfNow.parse(startDate));
								calendarwrw.add(Calendar.DAY_OF_MONTH, +m);
								Date calDatewrw = calendarwrw.getTime();
								String dayKey=sdfNow.format(calDatewrw);	
								
								//单个日期中的全部物种数据
								Object speciesobj=contentmapData.get(dayKey);	
								if(speciesobj==null||"{}".equals(speciesobj)||"".equals(speciesobj)||"null".equals(speciesobj)||"NULL".equals(speciesobj)){
									speciesDayData.put(dayKey,"-");
								}else{
								
									Map<String,Object> speciesMap= (Map)speciesobj;
									//单个物种数据
									Object speciesOne=speciesMap.get(meteormnArr[s].toString());	
									Map speciesOneMap=(Map)speciesOne;
//									String speciesOneVal=speciesOneMap.get("0").toString();
									String speciesOneVal;
									if(speciesOneMap==null||"".equals(speciesOneMap)){
										 speciesOneVal="-";
									}else{
										 speciesOneVal=speciesOneMap.get("0").toString();
									}
									if("CO".equals(meteormnArr[s])){
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
											speciesDayData.put(dayKey,"-");
										}else{
											BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
											speciesDayData.put(dayKey,bd);
										}
									}else{
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
											speciesDayData.put(dayKey,"-");
										}else{
											BigDecimal bd=(new BigDecimal(speciesOneMap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
											speciesDayData.put(dayKey,bd);
										}
									}
								
								}
								
							}
							simulationData.put(meteormnArr[s].toString(),speciesDayData);
					}
					
						objData.put("观测数据", "{}");	
						objData.put("模拟数据", simulationData);
				}else{		//---------------------------------气象要素逐小时--开始------------------------------------------------//
					
					//查询当天的情景（未完成则查询前一天）
					sdfNow=new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdfyear=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//当前时间
					dateNow=new Date();
					strDateNow=sdfNow.format(dateNow);
					//获取开始时间和结束时间差值
					differenceVal = (int) (( (sdfNow.parse(endDate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
					//当日日期大于结束日期的参数查询FNL
					if(dateNow.getTime()>(sdfNow.parse(endDate)).getTime()){
						//查询全部日期的物种数据
						for(int i=0;i<=differenceVal;i++){
							calendar.setTime(sdfNow.parse(endDate));
							calendar.add(Calendar.DAY_OF_MONTH, -i);
							calDate = calendar.getTime();
							calDateStr=sdfNow.format(calDate);
							
							tables="T_METEOR_FNL_HOURLY_";		//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);		
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setDay(calDate);
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
							if(scenarinoEntity==null||"".equals(scenarinoEntity.toString())||"{}".equals(scenarinoEntity.toString())||"null".equals(scenarinoEntity.toString())){
								//循环全部物种
								
								contentmapData.put(sdfNow.format(calDate), "{}");	//数据--空
							}else{
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));	
							}
						}	//查询全部日期的物种数据结束
					}else{
						
						 if(calendar.get(Calendar.HOUR_OF_DAY) > 8){
							//8点以后
							//查询模拟表
							//查询起报日期的sid
							//获取昨天的日期
							calendar.add(Calendar.DATE, -1);
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //根据用户id和起报日期查询获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							//查询逐小时模拟数据
						    //表名
						    tables="T_METEOR_HOURLY_";	
							sdfYear=new SimpleDateFormat("yyyy");	
							dateYear=new Date();
							//当前年份
							strYear=sdfYear.format(dateYear);
							//+年份
							tables+=strYear+"_";
							//+userid
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);   
							//获取模拟数据
//							contents=scenarinoEntity.getContent();
//							//模拟父数据
//							contentmapData=mapper.readValue(contents, Map.class);
							
							if(scenarinoEntity!=null){
								//获取模拟数据
								contents=scenarinoEntity.getContent();
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}else{
								//获取模拟数据为空时赋值"{}"
								contents="{}";
								//模拟父数据
								contentmapData=mapper.readValue(contents, Map.class);
							}
							
							//该方法返回一个日历Calendar
							Calendar calendar1 = Calendar.getInstance();
							//当天日期减2
							calendar1.add(Calendar.DATE, -2);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_METEOR_FNL_HOURLY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
//								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
								
								if(scenarinoEntity==null){
									contentmapData.put(sdfNow.format(calDate), "{}");
								}else{
									contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
								}
							}
							
					    }else{
					    	//8点以前
					    	//查询模拟表
							//查询起报日期的sid
							calendar.add(Calendar.DATE, -2);//获取前天的日期
						    Map tsMap = new HashMap();
						    tsMap.put("userId", userId);
						    tsMap.put("pathDate", sdfNow.format(calendar.getTime()));
						    //获取sid
						    TScenarinoDetail tScenarinoDetail = this.tScenarinoDetailMapper.selectendStart(tsMap);
							
						    tables="T_METEOR_HOURLY_";	//表名+年份+userid
							sdfYear=new SimpleDateFormat("yyyy");	//当前年份
							dateYear=new Date();
							strYear=sdfYear.format(dateYear);
							tables+=strYear+"_";
							tables+=userId;
							scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(domain);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
							scenarinoEntity.setTableName(tables);
							scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);   
							
							contents=scenarinoEntity.getContent();		//获取模拟数据
							contentmapData=mapper.readValue(contents, Map.class);	//模拟父数据
							
							Calendar calendar1 = Calendar.getInstance();
							calendar1.add(Calendar.DATE, -3);
							Date nowdate=calendar1.getTime();
							String strdate=sdfNow.format(nowdate);
							//开始日期
							Date newDate=sdfNow.parse(strdate);
							//查询fnl表Today-2-开始时间
							int differenceFnl = (int) (( (sdfNow.parse(strdate)).getTime() - (sdfNow.parse(startDate)).getTime() ) / (1000*3600*24));
							//查询fnl表
							for(int i=0;i<differenceFnl;i++){
								calendar.setTime(sdfNow.parse(startDate));
								calendar.add(Calendar.DAY_OF_MONTH, +i);
								calDate = calendar.getTime();
								calDateStr=sdfNow.format(calDate);
								//查询预报情景sid当做下一步的参数
								Map tscdetail=new HashMap();
								tscdetail.put("userId", userId);
								tscdetail.put("startDate", sdfNow.format(calendar.getTime()));
								TScenarinoDetail tscdetails=tScenarinoDetailMapper.selectbysid(tscdetail);
								//查询Fnl表数据
								tables="T_METEOR_FNL_HOURLY_";		//表名+年份+userid
								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
								dateYear=new Date();
								strYear=sdfYear.format(dateYear);
								tables+=strYear+"_";
								tables+=userId;
								scenarinoEntity=new ScenarinoEntity();
								scenarinoEntity.setCity_station(cityStation);
								scenarinoEntity.setDomain(domain);		
								scenarinoEntity.setMode(mode);
								scenarinoEntity.setDay(calDate);
								scenarinoEntity.setsId(tscdetails.getScenarinoId().longValue());
								scenarinoEntity.setTableName(tables);
								scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
								//循环添加到map中
								contentmapData.put(sdfNow.format(calDate), mapper.readValue(scenarinoEntity.getContent(), Map.class));
							}
					    }
					}
					
					
						//出数据
					//循环全部物种名称
					for(int z=0;z<meteormnArr.length;z++){
						//单个物种所有日期的数据
						Map speciesData=new HashMap();
						//循环全部数据，key为单个日期
//						for(Object dayKey:contentmapData.keySet() ){
						for(int m=0;m<=differenceVal;m++){
							Calendar calendarwrw = Calendar.getInstance();
							calendarwrw.setTime(sdfNow.parse(startDate));
							calendarwrw.add(Calendar.DAY_OF_MONTH, +m);
							Date calDatewrw = calendarwrw.getTime();
							String dayKey=sdfNow.format(calDatewrw);	
							
							//模拟单个日期24小时数据
							Map hourData=new HashMap();
							//获取单个日期中的全部物种数据
							Object speciesobj=contentmapData.get(dayKey);
							
							if(speciesobj==null||"{}".equals(speciesobj)||"".equals(speciesobj)||"null".equals(speciesobj)||"NULL".equals(speciesobj)){
								for(int h=0;h<24;h++){
									hourData.put(h, "-");
								}
								speciesData.put(dayKey,hourData);
							}else{
								
								Map<String,Object> speciesMap= (Map)speciesobj;
								Object speciesOne=speciesMap.get(meteormnArr[z].toString());
								
								if("null".equals(speciesOne)||"NULL".equals(speciesOne)||speciesOne==null||"".equals(speciesOne)){
									for(int h=0;h<24;h++){
										hourData.put(h, "-");
									}
									speciesData.put(dayKey,hourData);
								}else{
									Map speciesOneMap= (Map)speciesOne;
									List speciesOneVal=(List) speciesOneMap.get("0");
									if("CO".equals(meteormnArr[z].toString())){
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
											for(int h=0;h<speciesOneVal.size();h++){
												hourData.put(h, "-");
											}
											speciesData.put(dayKey,hourData);
										}else{
											for(int h=0;h<speciesOneVal.size();h++){
												hourOneVal=speciesOneVal.get(h).toString();
												if("-".equals(hourOneVal)){
													hourData.put(h, "-");
												}else{
													BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
													hourData.put(h, bd);
												}
												
											}
											speciesData.put(dayKey,hourData);
										}
									}else{
										if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
											for(int h=0;h<speciesOneVal.size();h++){
												hourData.put(h, "-");
											}
											speciesData.put(dayKey,hourData);
										}else{
											for(int h=0;h<speciesOneVal.size();h++){
												
												hourOneVal=speciesOneVal.get(h).toString();
												if("-".equals(hourOneVal)){
													hourData.put(h, "-");
												}else{
													BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
													hourData.put(h, bd);
												}
												
											}
											speciesData.put(dayKey,hourData);
										}
									}
								}
							
							}
						}
						simulationData.put(meteormnArr[z].toString(),speciesData);
					}	//循环物种结束
					
					
//					//查询当天的情景（未完成则查询前一天）
//					sdfNow=new SimpleDateFormat("yyyy-MM-dd");
//					dateNow=new Date();
//					strDateNow=sdfNow.format(dateNow);
//					HashMap<String, Object> mapNow=new HashMap<String, Object>();
//					mapNow.put("USER_ID", userId);
//					mapNow.put("PATH_DATE", strDateNow);	
//					//进行当天的情景查询
//					TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
//					tScenarinoDetail=tScenarinoDetailMapper.selectScenarinoDetail_timeSeries(mapNow);	//查询的当日的情景数据
//					
//					if(tScenarinoDetail==null){		//当天的情景数据为空
//						Date start_date=sdfNow.parse(startDate);
//						Date end_date=sdfNow.parse(endDate);
//						differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
//						for(int m=0;m<=differenceVal;m++){	//循环查询开始时间和结束时间的差值
//							calendar.setTime(dateNow);
//							calendar.add(Calendar.DAY_OF_MONTH, -(m+1));
//							calDate = calendar.getTime();
//							calDateStr=sdfNow.format(calDate);
//							mapNow.put("PATH_DATE", calDateStr);
//							tScenarinoDetail=tScenarinoDetailMapper.selectScenarinoDetail_timeSeries(mapNow);
//							if(tScenarinoDetail!=null){
//								
//								//根据该情景所属任务的任务（missionId）查询其情景范围（dominID）
//								missionId=tScenarinoDetail.getMissionId();
//								tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);	
//								domainId=tMissionDetail.getMissionDomainId();	//得到情景范围ID---作为查询参数
//								
//								tables="T_METEOR_HOURLY_";	//表名+年份+userid
//								sdfYear=new SimpleDateFormat("yyyy");	//当前年份
//								dateYear=new Date();
//								strYear=sdfYear.format(dateYear);
//								tables+=strYear+"_";
//								tables+=userId;
//								scenarinoEntity=new ScenarinoEntity();
//								scenarinoEntity.setCity_station(cityStation);
//								//空间分辨率--需要时注释即可,数据库中目前只有为3的数据-----------------------
//								scenarinoEntity.setDomain(domain);
//								scenarinoEntity.setMode(mode);
//								scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
//								scenarinoEntity.setsId(Long.valueOf(tScenarinoDetail.getScenarinoId().toString()).longValue());
//								scenarinoEntity.setTableName(tables);
//								scenarinoEntity=tPreProcessMapper.selectBysomes(scenarinoEntity);
//								if(scenarinoEntity!=null){
//									//获取模拟数据
//									contents=scenarinoEntity.getContent();
//									//模拟父数据
//									contentmap=mapper.readValue(contents, Map.class);	
//									
//									JSONArray dayArr=new JSONArray();
//									
//									for(Object dayKey:contentmap.keySet()){			//日期
//										dayArr.add(dayKey);
//									}
//									Collections.sort(dayArr);	//排序
//									
//									//求出开始时间和结束时间相差的天数
//									int times = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));		//结束时间和开始时间的差值
//									Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(endDate);
//									Calendar cal = Calendar.getInstance();
//									cal.setTime(date);
//									cal.add(Calendar.DATE, 1);
//									String endDates=(new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
//									
//									for(int j=0;j<=times;j++){	//总日期天数
//										
//										calendar.setTime(sdfNow.parse(endDates));
//										calendar.add(Calendar.DAY_OF_MONTH, -(j+1));
//										calDate = calendar.getTime();
//										calDateStr=sdfNow.format(calDate);
//										if(Arrays.asList(dayArr).contains(calDateStr)){		//判断是否包含该日期
//											continue;
//										}else{
//											if(calDate.getTime()>sdfNow.parse(dayArr.get(dayArr.size()-1).toString()).getTime()){	//后期
//												Map speciesobj=new HashMap();
//												Map	numVal=new HashMap();
//												
//												for(int v=0;v<meteormnArr.length;v++){	//循环物种
//													JSONArray hourval=new JSONArray();
//													for(int k=0;k<24;k++){	//24个小时
//														hourval.add("-");
//													}
//													numVal.put("0", hourval);
//													speciesobj.put(meteormnArr[v], numVal);
//												}
//												contentmap.put(sdfNow.format(calDate),speciesobj);
//											}else if(calDate.getTime()<sdfNow.parse(dayArr.get(0).toString()).getTime()){	//前期
//												
//												tables="T_METEOR_FNL_HOURLY_";		//表名+年份+userid
//												sdfYear=new SimpleDateFormat("yyyy");	//当前年份
//												dateYear=new Date();
//												strYear=sdfYear.format(dateYear);
//												tables+=strYear+"_";
//												tables+=userId;
//												scenarinoEntity=new ScenarinoEntity();
//												scenarinoEntity.setCity_station(cityStation);
//												scenarinoEntity.setDomain(domain);
//												scenarinoEntity.setMode(mode);
//												scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
//												scenarinoEntity.setDay(calDate);
//												scenarinoEntity.setTableName(tables);
//												scenarinoEntity=tPreProcessMapper.selectBysomesFnl(scenarinoEntity);
//												
//												contentmap.put(sdfNow.format(calDate), scenarinoEntity.getContent().toString());
//											}
//										}
//									}
//									
//									differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));	//页面上选择的结束时间和开始时间的差值
//									
//									for(int z=0;z<meteormnArr.length;z++){
//										Map speciesData=new HashMap();			//模拟单个物种全部日期数据
//										for(Object dayKey:contentmap.keySet() ){	//循环全部日期
//											Object speciesobj=contentmap.get(dayKey);
//											Map<String,Object> speciesMap= (Map)speciesobj;
//											Object speciesOne=speciesMap.get(meteormnArr[z].toString());
//											Map hourData=new HashMap();			//模拟单个日期24小时数据
//											if("null".equals(speciesOne)||"NULL".equals(speciesOne)||speciesOne==null||"".equals(speciesOne)){
//												//--------------------------赋值
//												for(int h=0;h<24;h++){
//													hourData.put(h, "-");
//												}
//												speciesData.put(dayKey,hourData);
//											}else{
//												Map speciesOneMap= (Map)speciesOne;
//												List speciesOneVal=(List) speciesOneMap.get("0");
//												if("TEMP".equals(meteormnArr[z].toString())||"WSPD".equals(meteormnArr[z].toString())||"PT".equals(meteormnArr[z].toString())){
//													if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
//														for(int h=0;h<speciesOneVal.size();h++){
//															hourData.put(h, "-");
//														}
//														speciesData.put(dayKey,hourData);
//													}else{
//														for(int h=0;h<speciesOneVal.size();h++){
//															hourOneVal=speciesOneVal.get(h).toString();
//															if("-".equals(hourOneVal)){
//																hourData.put(h, "-");
//															}else{
//																BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
//																hourData.put(h, bd);
//															}
//															
//														}
//														speciesData.put(dayKey,hourData);
//													}
//												}else{
//													if("".equals(speciesOneVal)||speciesOneVal==null||"-".equals(speciesOneVal)){		//判断是否有值
//														for(int h=0;h<speciesOneVal.size();h++){
//															hourData.put(h, "-");
//														}
//														speciesData.put(dayKey,hourData);
//													}else{
//														for(int h=0;h<speciesOneVal.size();h++){
//															
//															hourOneVal=speciesOneVal.get(h).toString();
//															if("-".equals(hourOneVal)){
//																hourData.put(h, "-");
//															}else{
//																BigDecimal bd=(new BigDecimal(speciesOneVal.get(h).toString())).setScale(0, BigDecimal.ROUND_HALF_UP);
//																hourData.put(h, bd);
//															}
//															
//														}
//														speciesData.put(dayKey,hourData);
//													}
//												}
//											}
//											
//										}
//										simulationData.put(meteormnArr[z].toString(),speciesData);
//									}	//循环物种结束
//									break;
//								}
//							}
//							else if(m==differenceVal){	  //判断等于最后一次时
//								if(scenarinoEntity==null||"".equals(scenarinoEntity)||"{}".equals(scenarinoEntity)||"null".equals(scenarinoEntity)||"NULL".equals(scenarinoEntity)){	//为空时赋值"-"
//									//以页面的开始时间和结束时间为基准，循环所有的日期
//									Map<String,Object> contentNew = new HashMap<String, Object>();
//									int	selectDay = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
//									for(int k=0;k<meteormnArr.length;k++){	//循环物种开始
//										Map speciesData=new HashMap();	
//										for(int n=0;n<=selectDay;n++){		//循环日期开始
//											calendar.setTime(end_date);
//											calendar.add(Calendar.DAY_OF_MONTH, -n);
//											calDate = calendar.getTime();
//											calDateStr=sdfNow.format(calDate);	//日期
//											speciesData.put(calDateStr,"-");
//										}	//循环日期结束
//										simulationData.put(meteormnArr[k].toString(),speciesData);
//									}	//循环物种结束
//								}
//							}
//						}	//循环查询开始时间和结束时间的差值
//					}
					
						/*
						 * 查询观测数据
						 */
//						JSONObject observationData=new JSONObject();		//返给页面的观测数据
//						JSONObject obsSpeciesData=new JSONObject();
//						HashMap<String, Object> obsMap=new HashMap<String, Object>();	//查询观测数据开始
//						obsMap.put("city_station",cityStation);
//						String modes;	//用于存放新的mode
//						if("point".equals(mode)){
//							 modes="station";
//						}else{
//							 modes=mode;
//						}
//						obsMap.put("mode",modes);
//						String tables_obs="T_OBS_HOURLY_";
//						DateFormat df_obs = new SimpleDateFormat("yyyy");
//						Date nowDate=new Date();
//						String nowYear_obs= df_obs.format(nowDate);		//截取该任务的开始时间来改变查询的表格
//						tables_obs+=nowYear_obs;
//						obsMap.put("tableName",tables_obs);				//查询的表格
//						
//						for(int m=0;m<speciesArr.length;m++){	
//						
//							for(int j=0;j<=differenceVal;j++){		//循环页面开始年份和结束年份的差值--开始
//								calendar.setTime(sdfNow.parse(startDate));
//								calendar.add(Calendar.DAY_OF_MONTH, (j+1));
//								 calDate = calendar.getTime();
//								 calDateStr=sdfNow.format(calDate);
//								
//								calendar.setTime(sdfNow.parse(calDateStr));
//								calendar.add(Calendar.DAY_OF_MONTH, -1);
//								Date calDates = calendar.getTime();
//								String calDateStrs=sdfNow.format(calDates);
//								Date calDate_now=sdfNow.parse(calDateStrs);
//								obsMap.put("date", calDate_now);
//								ObsBean obsBeans=tObsMapper.queryUnionResult(obsMap);	//查询观测数据
//								if(!"".equals(obsBeans)&&obsBeans!=null&&!"{}".equals(obsBeans)){	//查询到数据
//									String obsContent=obsBeans.getContent().toString();
//									//单条观测数据的content属性无具体的数据，进行数据赋值
//									if("".equals(obsContent)||obsContent==null||"null".equals(obsContent)||"NULL".equals(obsContent)||"{}".equals(obsContent)){	
//										for(int d=0;d<24;d++){
//											obsHourData.put(d, "-");
//										}
//										obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
//									}else{		//单条观测数据的content属性有具体的数据
//										JSONObject obsContentobj=JSONObject.fromObject(obsContent);
//										Map<String,Object> obsContentMap= (Map)obsContentobj;
//										for(int k=0;k<speciesArr.length;k++){		//查询的观测数据不为空时，进行赋值
//											if(speciesArr[m].equals(speciesArr[k])){
//												try {
//													String speciesval=obsContentMap.get(speciesArr[k]).toString();	//键为空出现异常时，赋值"-"
//													if("CO".equals(speciesArr[k])){									//值不为空，保留位数判断
//														BigDecimal bd=(new BigDecimal(speciesval)).setScale(2, BigDecimal.ROUND_HALF_UP);
//														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
//													}else{
//														BigDecimal bd=(new BigDecimal(speciesval)).setScale(1, BigDecimal.ROUND_HALF_UP);
//														obsSpeciesData.put(sdfNow.format(calDate_now), bd);
//													}
//												} catch (Exception e) {
//													// TODO: handle exception
//													for(int d=0;d<24;d++){
//														obsHourData.put(d, "-");
//													}
//													obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
//												}
//											}
//										}
//									}
//								}else{
//									for(int k=0;k<speciesArr.length;k++){	//查询的观测数据为空时，进行赋值
//										if(speciesArr[m].equals(speciesArr[k])){
//											for(int d=0;d<24;d++){
//												obsHourData.put(d, "-");
//											}
//											obsSpeciesData.put(sdfNow.format(calDate_now),obsHourData);
//										}
//									}
//								}
//								
//							}	//循环页面开始年份和结束年份的差值--结束
//							if("PM2_5".equals(speciesArr[m])){
//								observationData.put("PM25",obsSpeciesData);
//							}else if("O3".equals(speciesArr[m])){
//								observationData.put("O3_AVG",obsSpeciesData);
//							}else if("O3_8h".equals(speciesArr[m])){
//								observationData.put("O3_8_MAX",obsSpeciesData);
//							}else{
//								observationData.put(speciesArr[m],obsSpeciesData);
//							}
//						}
						//查询的情景中结束时间如果没有从页面获取到的结束时间参数时，数据赋值为"-"进行表示
						//查询的情景中开始时间如果没有从页面获取到的开始时间参数时，则在FNL表中查询
//						objData.put("观测数据", observationData);
						objData.put("观测数据", "{}");
						objData.put("模拟数据", simulationData);
				}
				
			}
			return AmpcResult.build(0, "success",objData);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 空气质量预报--时间序列异常",e);
			return	AmpcResult.build(0, "error");
		}
		
	}
	
	/**
	 * 空气质量预报--预报检验--污染物和气象要素
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	
	@RequestMapping("Air/checkout")
	public AmpcResult find_checkout(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
		//设置跨域
		ClientUtil.SetCharsetAndHeader(request, response);
		//接收请求参数
		Map<String,Object> data=(Map)requestDate.get("data");
		//获取用户ID
		Object param=data.get("userId");
		//进行参数判断
		if(!RegUtil.CheckParameter(param, "Long", null, false)){
			LogUtil.getLogger().error("AirController 用户ID为空或出现非法字符!");
			return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
		}
		// 用户id
		Long userId = Long.parseLong(param.toString());
		//获取站点类型
		param=data.get("mode");
		if(!RegUtil.CheckParameter(param, "String", null, false)){
			LogUtil.getLogger().error("AirController  站点类型为空或出现非法字符!");
			return AmpcResult.build(1003, "站点类型为空或出现非法字符!");
		}
		String mode=param.toString();
		//开始时间
		param=data.get("starttime");
		if(!RegUtil.CheckParameter(param, "String", null, false)){
			LogUtil.getLogger().error("AirController  日期为空或出现非法字符!");
			return AmpcResult.build(1003, "日期为空或出现非法字符!");
		}
		String starttime=param.toString()+" 00";
		//结束时间
		param=data.get("endtime");
		if(!RegUtil.CheckParameter(param, "String", null, false)){
			LogUtil.getLogger().error("AirController  日期为空或出现非法字符!");
			return AmpcResult.build(1003, "日期为空或出现非法字符!");
		}
		String endtime=param.toString()+" 00";
		//获取站点
		String cityStation;
		if("city".equals(mode)){
			//检测站点具体值
			 cityStation=data.get("cityStation").toString().substring(0, 4);	
		}else{
			 cityStation=data.get("cityStation").toString();					
		}
		//获取日期类型
		param=data.get("datetype");
		if(!RegUtil.CheckParameter(param, "String", null, false)){
			LogUtil.getLogger().error("AirController  日期为空或出现非法字符!");
			return AmpcResult.build(1003, "日期为空或出现非法字符!");
		}
		String datetype=param.toString();
		//选择页签
		param=data.get("tabType");
		if(!RegUtil.CheckParameter(param, "String", null, false)){
			LogUtil.getLogger().error("AirController  页签参数为空或出现非法字符!");
			return AmpcResult.build(1003, "页签参数为空或出现非法字符!");
		}
		String tabType=param.toString();	

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
		List<Date> daslist=new ArrayList();
		List<Date> pdaslist=new ArrayList();
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
		Map<Integer,Map<String,Map<String,String>>> lastMap=new HashMap();
		lastMap.put(-1, new HashMap());
		lastMap.put(1, new HashMap());
		lastMap.put(2, new HashMap());
		lastMap.put(3, new HashMap());
			Map<Integer,Map<Date,Object>> odMap=new HashMap();
			
			List<String> erlist=new ArrayList();
			for(Integer how:howday){
				Map<Date,Object> oddMap=new HashMap();
				for(Date thedate:datelist){
					if(daslist.contains(thedate)){
						continue;
					}
					if(datetype.equals("day")){	
					String tables="T_OBS_DAILY_";
					DateFormat df = new SimpleDateFormat("yyyy");
					String nowTime= df.format(thedate);
					tables+=nowTime;
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					if(mode.equals("point")){
						scenarinoEntity.setMode("station");	
					}else{
					scenarinoEntity.setMode(mode);
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(thedate);
					cal.add(Calendar.DATE, how);
					String addTimeDate =daysdf.format(cal.getTime());
					Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
					scenarinoEntity.setDate(thedate);
					scenarinoEntity.setTableName(tables);
					sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);
					}else{
						String tables="T_OBS_HOURLY_";
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(thedate);
						tables+=nowTime;
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						if(mode.equals("point")){
							scenarinoEntity.setMode("station");	
						}else{
						scenarinoEntity.setMode(mode);
						}
						Calendar cal = Calendar.getInstance();
						cal.setTime(thedate);
						cal.add(Calendar.DATE, how);
						String addTimeDate =daysdf.format(cal.getTime());
						Date pathDate=daysdf.parse(addTimeDate);//对应情景起报日期
						scenarinoEntity.setDate(thedate);
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome2(scenarinoEntity);
						
					}
					if(!sclist.isEmpty()){
						oddMap.put(thedate, sclist.get(0));
					}else{
						daslist.add(thedate);
					}
				}
					odMap.put(how, oddMap);	
			}
			
			
			
			Map<Integer,Map<Date,Object>> pdMap=new HashMap();
			
			List<String> perlist=new ArrayList();
			for(Integer how:howday){
				Map<Date,Object> pddMap=new HashMap();
				List<ScenarinoEntity> ssslist=new ArrayList();
				for(Date thedate:datelist){
					if(pdaslist.contains(thedate)){
						continue;
					}
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


						
						Map<String,Object> scmap=new HashMap();
						TScenarinoDetail tScenarino=new TScenarinoDetail();
						tScenarino.setScenType("4");
						tScenarino.setPathDate(pathDate);
						//查询对应情景
						List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
						if(jztScenarinoDetail.isEmpty()){
							continue;
						}
						TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
						TMissionDetail tm=new TMissionDetail();
						tm.setMissionId(jztScenarino.getMissionId());
						//通过情景中的任务id查询任务，再通过任务查询domainid
						List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
						TMissionDetail thetm=tmlist.get(0);
						int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						String tables="";
						if(how==-1){
							tables+="T_SCENARINO_FNL_DAILY_";
							scenarinoEntity.setDay(jztScenarino.getScenarinoStartDate());
						}else{
							tables+="T_SCENARINO_DAILY_";	
						}
						Date tims=jztScenarino.getPathDate();
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(tims);
						tables+=nowTime+"_";
						tables+=userId;
						
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						scenarinoEntity.setDomainId(Long.valueOf(domainId).longValue());
						scenarinoEntity.setMode(mode);
						
						scenarinoEntity.setsId(Long.valueOf(jztScenarino.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						if(how==-1){
						ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{
							ScenarinoEntity	sss=tPreProcessMapper.selectBysomes(scenarinoEntity);
							ssslist.add(sss);
						}
					}else{
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

				
						Map<String,Object> scmap=new HashMap();
						TScenarinoDetail tScenarino=new TScenarinoDetail();
						tScenarino.setScenType("4");
						tScenarino.setPathDate(pathDate);
						//查询对应情景
						List<TScenarinoDetail> jztScenarinoDetail=tScenarinoDetailMapper.selectByEntity(tScenarino);
						if(jztScenarinoDetail.isEmpty()){
							continue;
						}
						TScenarinoDetail jztScenarino=jztScenarinoDetail.get(0);
						TMissionDetail tm=new TMissionDetail();
						tm.setMissionId(jztScenarino.getMissionId());
						//通过情景中的任务id查询任务，再通过任务查询domainid
						List<TMissionDetail> tmlist=tMissionDetailMapper.selectByEntity(tm);
						TMissionDetail thetm=tmlist.get(0);
						int domainId=Integer.valueOf(thetm.getMissionDomainId().toString());
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						String tables="";
						if(how==-1){
							tables+="T_SCENARINO_FNL_HOURLY_";
							scenarinoEntity.setDay(jztScenarino.getScenarinoStartDate());
						}else{
							tables+="T_SCENARINO_HOURLY_";	
							scenarinoEntity.setsId(Long.valueOf(jztScenarino.getScenarinoId().toString()));
						}
						Date tims=jztScenarino.getPathDate();
						DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(tims);
						tables+=nowTime+"_";
						tables+=userId.toString();
						
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						scenarinoEntity.setDomainId(Long.valueOf(domainId));
						scenarinoEntity.setMode(mode);
						
						
						scenarinoEntity.setTableName(tables);
						if(how==-1){
						ssslist=tPreProcessMapper.selectBysome(scenarinoEntity);	
						}else{
							ScenarinoEntity	sss=tPreProcessMapper.selectBysomes(scenarinoEntity);
							ssslist.add(sss);
						}
					}
					if(!ssslist.isEmpty()){
						pddMap.put(thedate, ssslist.get(0));
					}else{
						pdaslist.add(thedate);
					
					}
				}
					pdMap.put(how, pddMap);	
			}
			Map<Integer,Map<Date,Object>> lastoMap=new HashMap();
			Map<Integer,Map<Date,Object>> lastpMap=new HashMap();
			Iterator<Entry<Integer,Map<Date,Object>>> oiter=odMap.entrySet().iterator();
			while(oiter.hasNext()){
				Entry<Integer,Map<Date,Object>> obs=oiter.next();
				Integer num=obs.getKey();
				Map<Date,Object> datemap= obs.getValue();
				Map<Date,Object> om=new HashMap();
				Map<Date,Object> pm=new HashMap();
				Iterator<Entry<Date,Object>> dateiter=datemap.entrySet().iterator();
				while(dateiter.hasNext()){
					Entry<Date,Object> dates=dateiter.next();
					Date date=dates.getKey();
					ScenarinoEntity se=(ScenarinoEntity)odMap.get(num).get(date);
					JSONObject boj=JSONObject.fromObject(se.getContent());
					Map<String,Object> observeMap=(Map) boj;
					if(!observeMap.isEmpty()){
					if(null!=pdMap.get(num)){
						if(null!=pdMap.get(num).get(date)){
							ScenarinoEntity ses=(ScenarinoEntity)pdMap.get(num).get(date);
							JSONObject bj=JSONObject.fromObject(ses.getContent());
							Map<String,Object> obsMap=(Map) bj;
							if(!obsMap.isEmpty()){
							om.put(date, dates.getValue());
							pm.put(date, pdMap.get(num).get(date));
							}
						}else{
							pdaslist.add(date);
							daslist.add(date);
						}
						
					}else{
						pdaslist.add(date);
						daslist.add(date);
					}
				}else{
						pdaslist.add(date);
						daslist.add(date);
					}
					
				}
				lastoMap.put(num, om);
				lastpMap.put(num, pm);
				}
			
			
			
			
			
			
			
			
			
			
		Set<Integer> days=lastoMap.keySet();
			
		for(Integer how:days){
			Map<Date,Object> dat=lastoMap.get(how);
			Set<Date> das=dat.keySet();
			
			Map<String,BigDecimal> sumMap=new HashMap();//所有日期所预报日期的污染物数值和
			Map<Date,Map> psumMap=new HashMap();
			Map<Date,Map> dateMap=new HashMap();
			Map<Date,Map<String,List>> hourdateMap=new HashMap();
			Map<Date,Map> odateMap=new HashMap();
			Map<String,Integer> validspcMap=new HashMap();
			Map<String,Integer> spcexactMap=new HashMap();
			Map<Date, Map<String, List>> hourdatemap=new HashMap();
			List<Date> dalist=new ArrayList();
			for(Date thedate:das){//遍历时间集合，获取对应空气质量数据
				
				Map spcMaps=new HashMap();
				Map<String, List> hourspcMaps=new HashMap();
				Map<String,Object> ospcMaps=new HashMap();
				Map<String,List> ohourspcMaps=new HashMap();
				Map<String,Object> odayspcMaps=new HashMap();
				ScenarinoEntity se=(ScenarinoEntity) dat.get(thedate);
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
							if(spc.equals("O3_8h")){
								spc="O3_8_MAX";
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
								if(spc.equals("O3_8h")){
									spc="O3_8_MAX";
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
		Set<Integer> pdays=lastpMap.keySet();
		for(Integer how:pdays){
			Map<Date,Object> dat=lastpMap.get(how);
			Set<Date> das=dat.keySet();
			Map<String,BigDecimal> sumMap=new HashMap();//所有日期所预报日期的污染物数值和
			Map<String,BigDecimal> psumMap=new HashMap();
			Map<Date,Map> dateMap=new HashMap();
			Map<Date,Map> hourdateMap=new HashMap();
			Map<Date,Map> odateMap=new HashMap();
			Map<String,Integer> validspcMap=new HashMap();
			Map<String,Integer> spcexactMap=new HashMap();
			Map<Date,Map<String,List>> hourdatemap=new HashMap();
			for(Date thedate:das){//遍历时间集合，获取对应空气质量数据
				
				Map<String,List> ohourspcMaps=new HashMap();
				Map<String,Object> odayspcMaps=new HashMap();
				if(datetype.equals("day")){	
					
					ScenarinoEntity housr=(ScenarinoEntity) dat.get(thedate);
					JSONObject objs=null;
					if(how==-1){
					objs=JSONObject.fromObject(housr.getContent());
					}else{
					
						String da=daysdf.format(thedate);
						Map ss =mapper.readValue(housr.getContent().toString(), Map.class);
						objs= JSONObject.fromObject(ss.get(da));
						
					}
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
				
					ScenarinoEntity housr=(ScenarinoEntity) dat.get(thedate);
					JSONObject objs=null;
					if(how==-1){
					objs=JSONObject.fromObject(housr.getContent());
					}else{
						String da=daysdf.format(thedate);
						Map ss =mapper.readValue(housr.getContent().toString(), Map.class);
						objs= JSONObject.fromObject(ss.get(da));
			
					}
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

							
							if(psumMap.get(spce)==null){
								psumMap.put(spce, p);
							}else{
								psumMap.put(spce, psumMap.get(spce).add(p));
								
							}

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
					
						BigDecimal num=new BigDecimal(spciter.getValue().toString());
						Integer vale=Integer.valueOf(validMap.get(day).get(spc).toString());
						
						String ps=null;
						if(hourpAllspcMap.get(day)==null){
							
							continue;
						}
						if(hourpAllspcMap.get(day).get(date)==null){
						
							continue;
						}
						if(AllspcMap.get(day).get(date).get(spc)==null){
						
							ps="99999999";
						}else{
							 ps=LevelUtil.Level(new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()));
						}
						String os=LevelUtil.Level(num);
						if(ps.equals(os)){
							if(spcexactMap.get(spc)==null){

								spcexactMap.put(spc, 1);
							}else{
								spcexactMap.put(spc, spcexactMap.get(spc)+1);				
							}
						}
					
						double opresult=num.subtract(new BigDecimal(sumMapsum.get(day).get(spc).toString()).divide(new BigDecimal(vale),2)).doubleValue();
						
						double ppresult=0;
						if(AllspcMap.get(day).get(date).get(spc)!=null){
							ppresult=new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()).subtract(psumMapsum.get(day).get(spc).divide(new BigDecimal(vale),2)).doubleValue();
						}else{
							
							continue;
						}
						double store =opresult*ppresult;
						double p2=Math.pow(ppresult,2);
						double o2=Math.pow(opresult,2);
						
						double o_p= 0;
						if(AllspcMap.get(day).get(date).get(spc)!=null){
							o_p=new BigDecimal(AllspcMap.get(day).get(date).get(spc).toString()).subtract(num).doubleValue();
						}else{
							
							continue;
						}
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
                       
						if(spc.equals("CO")||spc.equals("O3_8h")||spc.equals("AQI")||spc.equals("PM10")||spc.equals("SO2")||spc.equals("O3")||spc.equals("NO2")||spc.equals("PM25")){
							
							
						}else{
							continue;
						}
                    
						Map psm=psumMapsum.get(day);
						List<Object> numma=spciter.getValue();
						for(int s=0;s<=23;s++){
							BigDecimal num=new BigDecimal(numma.get(s).toString());
							if(hourpAllspcMap.get(day)==null){
					
								continue;
							}
							if(hourpAllspcMap.get(day).get(date)==null){

								continue;
							}
							List ms=hourpAllspcMap.get(day).get(date).get(spc);
						
							String ps=null;
							if(ms==null){
								ps="99999999";
							}else{
								 ps=LevelUtil.Level(new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()));
							}
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
						double ppresult=0;
						if(ms!=null){
							ppresult=(new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()).subtract(new BigDecimal(psumMapsum.get(day).get(spc).toString()).divide(new BigDecimal(vale),2))).doubleValue();
						}
						double store =opresult*ppresult;
						double p2=Math.pow(ppresult,2);
						double o2=Math.pow(opresult,2);
						double o_p= 0;
						if(ms!=null){
							o_p= new BigDecimal(hourpAllspcMap.get(day).get(date).get(spc).get(s).toString()).subtract(num).doubleValue();
						
						}else{
							
							continue;
						}
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
		
		
//		Map<Integer,Map<String,Map<String,String>>> lastMap=new HashMap();
		
		
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
		 List<Date> plistTemp= new ArrayList<Date>();  
		 Iterator<Date> pit=pdaslist.iterator();  
		 while(pit.hasNext()){  
		  Date a=pit.next();  
		  if(plistTemp.contains(a)){  
		   pit.remove();  
		  }  
		  else{  
		   plistTemp.add(a);  
		  }  
		 }  
		 
		 List<Date> listTemp= new ArrayList<Date>();  
		 Iterator<Date> it=daslist.iterator();  
		 while(it.hasNext()){  
		  Date a=it.next();  
		  if(listTemp.contains(a)){  
		   it.remove();  
		  }  
		  else{  
		   listTemp.add(a);  
		  }  
		 }  
		 
		 
//		Map<String,Object> obj=new HashMap();
//		obj.put("lastMap", lastMap);
//		obj.put("obs", listTemp);
//		obj.put("pbs", plistTemp);
//		return	AmpcResult.ok(obj);


		return	AmpcResult.ok(lastMap);

	}catch(Exception e){
		LogUtil.getLogger().error("AirController 预报检验查询异常！",e);
		return	AmpcResult.build(1001, "预报检验查询异常！");
	}
	}
	
	
	/**
	 * 实时预报含有数据的时间查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Air/times")
	public AmpcResult find_times(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String,Object> data=(Map)requestDate.get("data");
		Long userId=Long.valueOf(data.get("userId").toString());//用户id
		String date=data.get("date").toString();
		SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
		Date pathdate=hms.parse(date);
		TScenarinoDetail ts=new TScenarinoDetail();
		ts.setPathDate(pathdate);
		ts.setScenType("4");
		List<TScenarinoDetail> tsList=tScenarinoDetailMapper.selectByEntity(ts);
		if(tsList.isEmpty()){
			LogUtil.getLogger().error("没有当前时间的实时预报情景！");
			return	AmpcResult.build(1001, "没有当前时间的实时预报情景！");
		}
		TScenarinoDetail thets=tsList.get(0);
		Date start=thets.getScenarinoStartDate();
		String stra=hms.format(start);
		Date starttime=hms.parse(stra);
		TMissionDetail tm=tMissionDetailMapper.selectByPrimaryKey(thets.getMissionId());
		TTasksStatus tTasksStatus=tTasksStatusMapper.selectStatus(thets.getScenarinoId());
		Long status=tTasksStatus.getStepindex();
		Date endtime=null;
		if(status==8){
			Date sdd=tTasksStatus.getTasksEndDate();
			String addTimeDate =hms.format(sdd);
			endtime=hms.parse(addTimeDate);//对应情景起报日期
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(tTasksStatus.getTasksEndDate());
			cal.add(Calendar.DATE, -1);
			String addTimeDate =hms.format(cal.getTime());
			endtime=hms.parse(addTimeDate);//对应情景起报日期
		}
		
		
		Long startTime = starttime.getTime();
		Long endTime2 = endtime.getTime();
		Long betweenDays = (long)((endTime2 - startTime) / (1000 * 60 * 60 *24) + 0.5);
	    JSONObject obj=new JSONObject();
		JSONArray arr=new JSONArray();
		for(int a=0;a<=betweenDays;a++){
			Calendar cal = Calendar.getInstance();
			cal.setTime(thets.getScenarinoStartDate());
			cal.add(Calendar.DATE, a);
			String addTimeDate =hms.format(cal.getTime());
			Date tim=hms.parse(addTimeDate);//对应情景起报日期
			arr.add(tim.getTime());
			
		}
		obj.put("missionId", thets.getMissionId());
		obj.put("domainId", tm.getMissionDomainId());
		obj.put("scenarioId",thets.getScenarinoId());
		obj.put("timearr",arr);
		return	AmpcResult.ok(obj);
	}catch(Exception e){
		LogUtil.getLogger().error(" 实时预报含有数据的时间查询！",e);
		return	AmpcResult.build(1001, " 实时预报含有数据的时间查询！");
		
	}
	}
	/**
	 * 预报检验的开始结束时间查询
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Air/checkout_time")	
	public AmpcResult checkout_time(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			//获取前端参数
			SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
			Date date=new Date();
			int hour=date.getHours();
			String sdate="2017-04-27";
			Date startdate=hms.parse(sdate);
			JSONObject obj=new JSONObject();
			
			obj.put("starttime", startdate.getTime());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DATE, -1);
				String addTimeDate =hms.format(cal.getTime());
				Date endtime=hms.parse(addTimeDate);
				obj.put("endDate", endtime.getTime());
			
		return AmpcResult.ok(obj);
		}catch(Exception e){
			LogUtil.getLogger().error("checkout_time 查询预报检验时间异常",e);
			return AmpcResult.build(1001, "查询预报检验时间异常！");	
		}
	}
	/**
	 * 查询最大和最小起报日期
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/Air/findpathdate")	
	public AmpcResult findpathdate(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
//			Long userId=Long.valueOf(data.get("userId").toString());//用户id
//			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
//				LogUtil.getLogger().error("find_vertical  用户id为空!");
//				return AmpcResult.build(1003, "用户id为空!");
//			}

			Date selectmaxpathdate = tScenarinoDetailMapper.selectmaxpathdate();
			Date selectminpathdate = tScenarinoDetailMapper.selectminpathdate();
			JSONObject obj=new JSONObject();
			SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
			obj.put("mintime", selectminpathdate.getTime());
			Long betweenDays = (long)((selectmaxpathdate.getTime() - selectminpathdate.getTime()) / (1000 * 60 * 60 *24) + 0.5);
			for(int a=0;a<betweenDays;a++){
				Calendar cal = Calendar.getInstance();
				cal.setTime(selectmaxpathdate);
				cal.add(Calendar.DATE, -a);
				String addTimeDate =hms.format(cal.getTime());
				Date endtime=hms.parse(addTimeDate);
		
				
				TScenarinoDetail ts=new TScenarinoDetail();
				ts.setPathDate(endtime);
				ts.setScenType("4");
				List<TScenarinoDetail> tsList=tScenarinoDetailMapper.selectByEntity(ts);
				if(tsList.isEmpty()){
					
					continue;
				}
				TScenarinoDetail thets=tsList.get(0);
				Date start=thets.getScenarinoStartDate();
				String stra=hms.format(start);
				Date starttime=hms.parse(stra);
				TMissionDetail tm=tMissionDetailMapper.selectByPrimaryKey(thets.getMissionId());
				TTasksStatus tTasksStatus=tTasksStatusMapper.selectStatus(thets.getScenarinoId());
				Long status=tTasksStatus.getStepindex();
				if(tTasksStatus.getTasksEndDate()!=null){
					obj.put("maxtime", endtime.getTime());
					break;
				}
			}
			
			return AmpcResult.ok(obj);
		}catch(Exception e){
			LogUtil.getLogger().error("findpathdate 查询最大和最小起报日期异常",e);
			return AmpcResult.build(1001, "查询最大和最小起报日期！");	
		}
	}
	 
//	@RequestMapping("/code/finds")	
//	public AmpcResult findcodes(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response ){
//		List<Long> idlist =new ArrayList<Long>();
//		idlist.add(1l);
//		idlist.add(2l);
//		idlist.add(22l);
//		Map map=findCode.Findcode(idlist);
//		return AmpcResult.ok(map);
//	 }
}
