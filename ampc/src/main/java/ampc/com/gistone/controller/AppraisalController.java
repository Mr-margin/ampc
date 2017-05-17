package ampc.com.gistone.controller;

import java.math.BigDecimal;
import java.sql.Clob;
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
import oracle.net.aso.k;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TEmissionDetailMapper;
import ampc.com.gistone.database.inter.TMissionDetailMapper;
import ampc.com.gistone.database.inter.TObsMapper;
import ampc.com.gistone.database.inter.TPreProcessMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TEmissionDetailWithBLOBs;
import ampc.com.gistone.database.model.TMissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

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
	
	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	
	@Autowired
	private TObsMapper tObsMapper;
	
	//定义公用的jackson帮助类
	private ObjectMapper mapper=new ObjectMapper();
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
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			
			//获取任务ID
			param=data.get("missionId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController  任务id为空或出现非法字符!");
				return AmpcResult.build(1003, "任务id为空或出现非法字符!");
			}
			Long missionId=Long.valueOf(param.toString());
			
			//获取站点类型
			param=data.get("mode");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  站点类型为空或出现非法字符!");
				return AmpcResult.build(1003, "站点类型为空或出现非法字符!");
			}
			String mode=param.toString();
			
			//获取站点类型
			param=data.get("scenarinoId");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  情景id为空或出现非法字符!");
				return AmpcResult.build(1003, "情景id为空或出现非法字符!");
			}
			String scenarinoIds=param.toString();
			
			//获取日期类型
			param=data.get("datetype");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  日期为空或出现非法字符!");
				return AmpcResult.build(1003, "日期为空或出现非法字符!");
			}
			String datetype=param.toString();
			//空间分辨率
			param=data.get("domain");
			if(!RegUtil.CheckParameter(param, "Integer", null, false)){
				LogUtil.getLogger().error("AppraisalController  空间分辨率为空或出现非法字符!");
				return AmpcResult.build(1003, "空间分辨率为空或出现非法字符!");
			}
			int domain=Integer.valueOf(param.toString());
			//开始日期
			param=data.get("startDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  开始日期为空或出现非法字符!");
				return AmpcResult.build(1003, "开始日期为空或出现非法字符!");
			}
			String start_Date=param.toString();
//			//结束日期
			param=data.get("endDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  结束日期为空或出现非法字符!");
				return AmpcResult.build(1003, "结束日期为空或出现非法字符!");
			}
			String end_Date=param.toString();
			
			//获取站点
			String cityStation;
			if("city".equals(mode)){
				cityStation=data.get("cityStation").toString().substring(0, 4);	//检测站点具体值
			}else{
				cityStation=data.get("cityStation").toString();					//检测站点具体值
			}
			
			//格式化日期变量
			SimpleDateFormat sdf;
			//选择的结束和开始日期的差值
			int differenceVal;
			//声明局部变量
			Calendar calendar = Calendar.getInstance(); 
			Date calDate ;
			String calDateStr;
			
			sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date start_date=sdf.parse(start_Date);
			Date end_date=sdf.parse(end_Date);
			differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
			
			//定义结果Map
			Map scmap=new HashMap();
			//获取情景集合
			List<Long> list=new ArrayList<Long>();
			//将情景id保存到集合
			for(String scid : scenarinoIds.split(",")){
				list.add(Long.valueOf(scid));	
			}
			//该任务下的所有数据
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			//获取domainId
			Long domainId=Long.valueOf(tMissionDetail.getMissionDomainId().toString());
			//定义情景帮助集合类
			List<ScenarinoEntity> sclist=new ArrayList();
			//循环所有情景
			for(Long scenarinoId:list){
				//查询该任务中该情景下的所有数据
				TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
				//定义一个临时情景类
				TScenarinoDetail tScenarino=new TScenarinoDetail(); 
				//写入任务ID
				tScenarino.setMissionId(missionId);
				//写入情景状态
				tScenarino.setScenType("3");
				//预评估任务
				if(tMissionDetail.getMissionStatus().equals("2")){//任务类型
					/**
					 * TODO
					 * 任务开始时间与系统开始时间对比  暂时没有
					 */
					if(4<5){
						//逐天
						if(datetype.equals("day")){
							//表名title
							String tables="T_SCENARINO_DAILY_";
							//获取情景添加时间
							Date tims=tScenarinoDetail.getScenarinoAddTime();
							//定义格式化类
							DateFormat df = new SimpleDateFormat("yyyy");
							//进行格式化
							String nowTime= df.format(tims);
							//进行拼接
							tables+=nowTime+"_";
							tables+=userId;
							//定义查询类 并写入查询条件
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							//空间分辨率--需要时替换即可
							scenarinoEntity.setDomain(domain);		
							scenarinoEntity.setDomainId(domainId);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(scenarinoId);
							scenarinoEntity.setTableName(tables);
							//进行查询
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							//表名title
							String tables="T_SCENARINO_HOURLY_";
							//获取情景添加时间
							Date tims=tScenarinoDetail.getScenarinoAddTime();
							//定义格式化类
							DateFormat df = new SimpleDateFormat("yyyy");
							//进行格式化
							String nowTime= df.format(tims);
							//进行拼接
							tables+=nowTime+"_";
							tables+=userId;
							//定义查询类 并写入查询条件
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							//空间分辨率--需要时替换即可
							scenarinoEntity.setDomain(domain);	
							scenarinoEntity.setDomainId(domainId);
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(scenarinoId);
							scenarinoEntity.setTableName(tables);
							//进行查询
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}
					}
				//任务类型
				}else{
					if(datetype.equals("day")){		//逐天
						//表名title
						//*****
						String tables="T_SCENARINO_DAILY_";
						//获取情景添加时间
						Date tims=tScenarinoDetail.getScenarinoAddTime();
						//定义格式化类
						DateFormat df = new SimpleDateFormat("yyyy");
						//进行格式化
						String nowTime= df.format(tims);
						tables+=nowTime+"_";
						tables+=userId;
						//定义查询类 并写入查询条件
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(domain);
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(scenarinoId);
						scenarinoEntity.setTableName(tables);
						//进行查询
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					}else{//逐小时
						//表名title
						String tables="T_SCENARINO_HOURLY_";
						//获取情景添加时间
						Date tims=tScenarinoDetail.getScenarinoAddTime();
						//定义格式化类
						DateFormat df = new SimpleDateFormat("yyyy");
						//进行格式化
						String nowTime= df.format(tims);
						tables+=nowTime+"_";
						tables+=userId;
						//定义查询类 并写入查询条件
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(domain);
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(scenarinoId);
						scenarinoEntity.setTableName(tables);
						//进行查询
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					}//时间分布判断
				}
				//如果没有信息直接返回
				if(sclist.isEmpty()){
					return	AmpcResult.build(0, "success",scmap);
				}
				//如果是逐小时
				if(datetype.equals("hour")){
					//定义一个污染物名称
					Object[] speciesArr={"BC","O3","PM25","CO","NO2","NO3","OC","PMFINE","SO2","PM10","SO4","AQI","NH4","OM"};
					//对应污染物的结果
					Map speciesMap=new HashMap();
					//循环情景帮助类结果
					for(ScenarinoEntity sc:sclist){
						//获取情景ID
						Long scid=sc.getsId();
						//获取数据Json
						String content=sc.getContent();
						//进行Json解析
						Map detamap=mapper.readValue(content, Map.class);
						//循环污染物名称
						for(int i=0;i<speciesArr.length;i++){	
							//创建日期结果Map
							Map datemap=new HashMap();
							//content全部数据	key--日期
//							for(Object datetime:detamap.keySet()){
							for(int s=0; s<=differenceVal;s++){
//								calendar.setTime(sdf.parse(end_Date));
								calendar.setTime(sdf.parse(("2016-11-26").toString()));
								calendar.add(Calendar.DAY_OF_MONTH, -s);
								calDate = calendar.getTime();
								calDateStr=sdf.format(calDate);
								Object sp=detamap.get(calDateStr);
								//获取结果集中的对象
//								Object sp=detamap.get(datetime);
								//转换成Map
								Map spmap=(Map)sp;
								//存放数据
								Map spcmap=new HashMap();	
								//单个日期中全部物种数据
								for(Object spr:spmap.keySet()){	
									//如果当前的污染物和结果中的匹配
									if(speciesArr[i].equals(spr)){
										//判断是否是CO CO要进行特殊处理
										if("CO".equals(spr)){
											//获取当前污染物的信息
											Object height=spmap.get(spr);
											Map heightmap=(Map)height;
											Object hour=heightmap.get("0");
											List hourlist=(List)hour;
											for(int j=0;j<hourlist.size();j++){
												BigDecimal bd=(new BigDecimal(hourlist.get(j).toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
												spcmap.put(""+j+"",bd);
											}
										}else{
											Object height=spmap.get(spr);
											Map heightmap=(Map)height;
											Object hour=heightmap.get("0");
											List hourlist=(List)hour;
											for(int j=0;j<hourlist.size();j++){
												BigDecimal bd=(new BigDecimal(hourlist.get(j).toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
												spcmap.put(""+j+"",bd);
											}
										}
									}
								}	
								//写入日期结果
								datemap.put(calDateStr, spcmap);
							}
							//写入污染物结果
							speciesMap.put(""+speciesArr[i]+"", datemap);
						}
						//写入返回结果
						scmap.put(scid, speciesMap);
					}
				}else{
					for(ScenarinoEntity sc:sclist){
						String scid=String.valueOf(sc.getsId());
						Object content=sc.getContent();
						JSONObject obj=JSONObject.fromObject(content);//行业减排结果
						Map<String,Object> detamap=(Map)obj;
						Map<String,Object> datemap=new HashMap();
//						for(String datetime:detamap.keySet()){
						for(int s=0; s<=differenceVal;s++){
							calendar.setTime(sdf.parse(end_Date));
							calendar.add(Calendar.DAY_OF_MONTH, -s);
							calDate = calendar.getTime();
							calDateStr=sdf.format(calDate);
							Object sp=detamap.get(calDateStr);
							
//							Object sp=detamap.get(datetime);
							Map<String,Object> spmap=(Map)sp;
							for(String spr:spmap.keySet()){
								Map<String,Object> spcmap=new HashMap();
								Object height=spmap.get(spr);
								Map<String,Object> heightmap=(Map)height;
								Map<String,Object> hourcmap=new HashMap();
								if(spr.equals("CO")){
									BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
									spcmap.put(calDateStr, bd);
									}else{
									BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
									spcmap.put(calDateStr, bd);
									}
								if(datemap.get(spr)!=null){
									Object maps=datemap.get(spr);
									Map<String,Object> des=(Map)maps;
									if(spr.equals("CO")){
										BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
										des.put(calDateStr, bd);
										}else{
										BigDecimal bd=(new BigDecimal(heightmap.get("0").toString())).setScale(1, BigDecimal.ROUND_HALF_UP);
										des.put(calDateStr, bd);
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
			//返回结果
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
			if(!RegUtil.CheckParameter(userId, "Long", null, false)){
				LogUtil.getLogger().error("find_vertical  用户id为空!");
				return AmpcResult.build(1003, "用户id为空!");
			}
			Long missionId=Long.valueOf(data.get("missionId").toString());
			if(!RegUtil.CheckParameter(missionId, "Long", null, false)){
				LogUtil.getLogger().error("find_vertical  任务id为空!");
				return AmpcResult.build(1003, "任务id为空!");
			}
			String mode=data.get("mode").toString();
			if(!RegUtil.CheckParameter(mode, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  站点类型为空!");
				return AmpcResult.build(1003, "站点类型为空!");
			}
			String time=data.get("time").toString();
			if(!RegUtil.CheckParameter(time, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  时间为空!");
				return AmpcResult.build(1003, "时间为空!");
			}
			String cityStation=data.get("cityStation").toString();
			if(!RegUtil.CheckParameter(cityStation, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  站点code为空!");
				return AmpcResult.build(1003, "站点code为空!");
			}
			JSONArray lists = JSONArray.fromObject(data.get("scenarinoId"));
			List<Long> list=new ArrayList<Long>();
			for(Object scid:lists){
				list.add(Long.valueOf(scid.toString()));	
			}
			String datetype=data.get("datetype").toString();
			if(!RegUtil.CheckParameter(datetype, "String", null, false)){
				LogUtil.getLogger().error("find_vertical  时间分辨率为空!");
				return AmpcResult.build(1003, "时间分辨率为空!");
			}
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH");
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			Date times=sdf.parse(time);
			int hournum=times.getHours();
			Date daytimes=daysdf.parse(time);
			String daytime=daysdf.format(daytimes);
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			Long domainId=Long.valueOf(tMissionDetail.getMissionDomainId().toString());
			List<ScenarinoEntity> sclist=new ArrayList();
			Map<String,Object> scmap=new HashMap();
			for(Long scenarinoId:list){
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
			return	AmpcResult.ok(scmap);
		}catch(Exception e){
			LogUtil.getLogger().error("find_vertical 垂直分布查询异常！",e);
			return	AmpcResult.build(1001, "垂直分布查询异常！");
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
			Long domainId=Long.valueOf(tMissionDetail.getMissionDomainId().toString());
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
				if(ScenarinoDetail.getScenType().equals("3")){
					if(datetype.equals("day")){//逐天
						String tables="";
						if(tMissionDetail.getMissionStatus().equals("3")){
							tables="T_SCENARINO_DAILY_";
						}else{
						tables="T_SCENARINO_FNL_DAILY_";
						}
						 DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(adddate);
						tables+=nowTime+"_";
						tables+=userId;
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						if(!tables.contains("T_SCENARINO_DAILY_")){
						scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
						}
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Long.valueOf(ScenarinoDetail.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							String tables="";
							if(tMissionDetail.getMissionStatus().equals("3")){
								tables="T_SCENARINO_HOURLY_";
							}else{
							tables="T_SCENARINO_FNL_HOURLY_";
							}
							 DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(adddate);
							tables+=nowTime+"_";
							tables+=userId.toString();
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId(domainId);
							if(!tables.contains("T_SCENARINO_HOURLY_")){
								scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
								}
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(ScenarinoDetail.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}//时间分布判断
			}else{
				if(start.equals(daytime)){
					if(datetype.equals("day")){//逐天
						String tables="";
						if(tMissionDetail.getMissionStatus().equals("3")){
							tables="T_SCENARINO_DAILY_";
						}else{
						tables="T_SCENARINO_FNL_DAILY_";
						}
						 DateFormat df = new SimpleDateFormat("yyyy");
						String nowTime= df.format(adddate);
						tables+=nowTime+"_";
						tables+=userId;
						ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
						scenarinoEntity.setCity_station(cityStation);
						scenarinoEntity.setDomain(3);
						scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
						scenarinoEntity.setDomainId(domainId);
						scenarinoEntity.setMode(mode);
						scenarinoEntity.setsId(Long.valueOf(ScenarinoDetail.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}else{//逐小时
							String tables="";
							if(tMissionDetail.getMissionStatus().equals("3")){
								tables="T_SCENARINO_HOURLY_";
							}else{
							tables="T_SCENARINO_FNL_HOURLY_";
							}
							 DateFormat df = new SimpleDateFormat("yyyy");
							String nowTime= df.format(adddate);
							tables+=nowTime+"_";
							tables+=userId.toString();
							ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
							scenarinoEntity.setCity_station(cityStation);
							scenarinoEntity.setDomain(3);
							scenarinoEntity.setDomainId(domainId);
							scenarinoEntity.setDay(ScenarinoDetail.getScenarinoStartDate());
							scenarinoEntity.setMode(mode);
							scenarinoEntity.setsId(Long.valueOf(ScenarinoDetail.getScenarinoId().toString()));
							scenarinoEntity.setTableName(tables);
							sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						}//时间分布判断
				}else{
					continue;
				}	
			}
			}
			if(datetype.equals("hour")){
				for(ScenarinoEntity sc:sclist){
					String scid=String.valueOf(sc.getsId());
					String content=sc.getContent().toString();
					JSONObject obj=JSONObject.fromObject(content);
					Map<String,Object> detamap=new HashMap();
					if(tMissionDetail.getMissionStatus().equals("3")){
						Map<String,Object> demap=(Map)obj;
						detamap=(Map<String,Object>)demap.get(daytime);
					}else{
						detamap=(Map)obj;
					}
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
						Map<String,Object> detamap=new HashMap();
						if(tMissionDetail.getMissionStatus().equals("3")){
							Map<String,Object> demap=(Map)obj;
							detamap=(Map<String,Object>)demap.get(daytime);
						}else{
							detamap=(Map)obj;
						}
						
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
			
			Long domainId=Long.valueOf(tMissionDetail.getMissionDomainId().toString());
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
						scenarinoEntity.setsId(Long.valueOf(tsc.getScenarinoId().toString()));
						scenarinoEntity.setTableName(tables);
						scenarinoEntity.setDay(tsc.getScenarinoStartDate());
						sclist=tPreProcessMapper.selectBysome(scenarinoEntity);
						scelist.add(sclist.get(0));
						}else{//逐小时
							String tables="T_SCENARINO_FNL_HOURLY_";
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
							scenarinoEntity.setsId(Long.valueOf(tsc.getScenarinoId().toString()));
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
	
	/**
	 * 查询基准数据和观测数据
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Appraisal/find_standard")
	public AmpcResult find_All_scenarino(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			
			//获取任务ID
			param=data.get("missionId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController  任务id为空或出现非法字符!");
				return AmpcResult.build(1003, "任务id为空或出现非法字符!");
			}
			Long missionId=Long.valueOf(param.toString());
			
			//获取站点类型
			param=data.get("mode");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  站点类型为空或出现非法字符!");
				return AmpcResult.build(1003, "站点类型为空或出现非法字符!");
			}
			String mode=param.toString();
			
			//获取日期类型
			param=data.get("datetype");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  日期为空或出现非法字符!");
				return AmpcResult.build(1003, "日期为空或出现非法字符!");
			}
			String datetype=param.toString();
			//获取站点
			String cityStation;
			if("city".equals(mode)){
				//检测站点具体值
				 cityStation=data.get("cityStation").toString().substring(0, 4);	
			}else{
				 cityStation=data.get("cityStation").toString();					
			}
			//空间分辨率
			param=data.get("domain");
			if(!RegUtil.CheckParameter(param, "Integer", null, false)){
				LogUtil.getLogger().error("AppraisalController  空间分辨率为空或出现非法字符!");
				return AmpcResult.build(1003, "空间分辨率为空或出现非法字符!");
			}
			int domain=Integer.valueOf(param.toString());	
			//变化状态
			param=data.get("changeType");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  变化状态为空或出现非法字符!");
				return AmpcResult.build(1003, "变化状态为空或出现非法字符!");
			}
			String changeType=param.toString();
			
//			//开始日期
			param=data.get("startDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  开始日期为空或出现非法字符!");
				return AmpcResult.build(1003, "开始日期为空或出现非法字符!");
			}
			String start_Date=param.toString();
//			//结束日期
			param=data.get("endDate");
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  结束日期为空或出现非法字符!");
				return AmpcResult.build(1003, "结束日期为空或出现非法字符!");
			}
			String end_Date=param.toString();
			
			//定义情景查询对象
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setUserId(userId);
			tScenarinoDetail.setMissionId(missionId);
			//进行数据查询
			TScenarinoDetail tScenarinoDetaillist=tScenarinoDetailMapper.selectBystandard(tScenarinoDetail);
			//获取情景开始时间
			String startDate= DateUtil.DATEtoString(tScenarinoDetaillist.getScenarinoStartDate(), "yyyy-MM-dd");	
			//获取情景结束时间
			String endDate= DateUtil.DATEtoString(tScenarinoDetaillist.getScenarinoEndDate(), "yyyy-MM-dd");
			//该任务下的所有数据
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);	
			//获取domainId
			Long domainId=tMissionDetail.getMissionDomainId();
			//任务开始时间
			Date missionStartDate=tMissionDetail.getMissionStartDate();
			String missionStartDatestr=DateUtil.DATEtoString(missionStartDate, "yyyy-MM-dd HH:mm:ss");
			//任务结束时间
			Date missionEndDate=tMissionDetail.getMissionEndDate();		
			String missionEndDatestr=DateUtil.DATEtoString(missionEndDate, "yyyy-MM-dd HH:mm:ss");
			//定义结果集合
			Map objsed=new HashMap();
			//格式化日期变量
			SimpleDateFormat sdf;
			//选择的结束和开始日期的差值
			int differenceVal;
			//声明局部变量
			Calendar calendar = Calendar.getInstance(); 
			Date calDate ;
			String calDateStr;
			
			sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date start_date=sdf.parse(start_Date);
			Date end_date=sdf.parse(end_Date);
			differenceVal = (int) ((end_date.getTime() - start_date.getTime()) / (1000*3600*24));
			//如果情景不为空
			if(tScenarinoDetaillist!=null){
				//时间分辨率--逐日开始
				if(datetype.equals("day")){
					//所有物种集合
					Map spcmapobj=new HashMap();
					//查询基准数据开始
					String tables="T_SCENARINO_DAILY_";
					//获取情景添加时间
					Date tims=tScenarinoDetaillist.getScenarinoAddTime();
					//转换获取年份 拼接表名
					String nowTime= DateUtil.DATEtoString(tims,"yyyy");
					tables+=nowTime+"_";
					tables+=userId;
					//定义查询实体类
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					//空间分辨率--需要时替换即可,数据库中目前只有为3的数据
					scenarinoEntity.setDomain(domain);		
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setDomainId(domainId);
					scenarinoEntity.setsId(tScenarinoDetaillist.getScenarinoId());
					scenarinoEntity.setTableName(tables);
					//查询结果
					List<ScenarinoEntity> Lsclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					//如果为空
					if(!Lsclist.isEmpty()){
						//获取数据Json串
						String content=Lsclist.get(0).getContent();
						//总数据
						Map standard=mapper.readValue(content, Map.class);
						//循环数据集合       最外层的日期
//						for(Object key : standard.keySet()){
						for(int i=0; i<=differenceVal;i++){
							calendar.setTime(sdf.parse(end_Date));
							calendar.add(Calendar.DAY_OF_MONTH, -i);
							calDate = calendar.getTime();
							calDateStr=sdf.format(calDate);
							
							//值     根据日期获取当前日期下的所有值
//							Object species=standard.get(key);
							Object species=standard.get(calDateStr);
							Map spcmap= (Map)species;
							//循环当前时间的所有物种名称
							for(Object  spcmapkey : spcmap.keySet()){ 	
								//单个物种所有时间数据
								Map standardobj=new HashMap();
								//物种名称
								String spcmapkeyw=spcmapkey.toString();
								//为了拼接需要的数据格式  再次循环收到的数据集  键为日期
//								for(Object standard_Time:standard.keySet()){	
//									//获取对应年份下的所有信息
//									Object standard_Times=standard.get(standard_Time);
								for(int j=0; j<=differenceVal;j++){
									calendar.setTime(sdf.parse(end_Date));
									calendar.add(Calendar.DAY_OF_MONTH, -j);
									calDate = calendar.getTime();
									calDateStr=sdf.format(calDate);	
									//获取对应年份下的所有信息
									Object standard_Times=standard.get(calDateStr);
									//转化信息格式
									Map speciesmap= (Map)standard_Times;
									//再次循环当前时间的所有物种名称
									for(Object speciesmap_key:speciesmap.keySet()){
										//物种名称
										String speciesmap_keyn=speciesmap_key.toString();
										if(!speciesmap_keyn.equals(spcmapkeyw)) continue;
										//判断
										if(speciesmap_keyn.equals(spcmapkeyw)&&"CO".equals(spcmapkeyw)){
											//对应污染物结果
											Object speciesmap_keyval=speciesmap.get(speciesmap_key);				
											Map speciesmapval= (Map)speciesmap_keyval;
											String standardval=speciesmapval.get("0").toString();
											//判断结果  写入对应结果
											if("".equals(standardval)||"null".equals(standardval)||"NULL".equals(standardval)||null==standardval){
												standardobj.put((String)calDateStr, "-");
											}else{
												BigDecimal bd=(new BigDecimal(standardval)).setScale(2, BigDecimal.ROUND_HALF_UP);
												standardobj.put((String)calDateStr, bd);
											}
											
										}else{
											Object speciesmap_keyval=speciesmap.get(speciesmap_key);				
											Map<String,Object> speciesmapval= (Map)speciesmap_keyval;
											String standardval=speciesmapval.get("0").toString();
											if("".equals(standardval)||"null".equals(standardval)||"NULL".equals(standardval)||null==standardval){
												standardobj.put((String)calDateStr, "-");
											}else{
												BigDecimal bd=(new BigDecimal(standardval)).setScale(1, BigDecimal.ROUND_HALF_UP);
												standardobj.put((String)calDateStr, bd);
											}
										}
									}
								} 
								//写入结果集
								spcmapobj.put((String)spcmapkey, standardobj);
							}//物种名称结束
//							break;
						}
					}	/**查询基准数据结束*/
					//查询观测数据开始
					HashMap<String, Object> obsBeanobj=new HashMap<String, Object>();	
					//站点信息
					obsBeanobj.put("city_station",cityStation);
					//所选任务的开始时间
//					obsBeanobj.put("startDate", missionStartDatestr);	
//					//所选任务的结束时间
//					obsBeanobj.put("endDate", missionEndDatestr);
					obsBeanobj.put("startDate", start_Date);	
					//所选任务的结束时间
					obsBeanobj.put("endDate", end_Date);
					//用于存放新的mode
					String modes;	
					//判断mode类型
					if("point".equals(mode)){
						 modes="station";
					}else{
						 modes=mode;
					}
					//写入mode
					obsBeanobj.put("mode",modes);
					//表名title
					String tables_obs="T_OBS_DAILY_";
					//截取该任务的开始时间来改变查询的表格
					String nowTime_obs= DateUtil.DATEtoString(missionStartDate, "yyyy");	
					tables_obs+=nowTime_obs;
					//查询的表格
					obsBeanobj.put("tableName",tables_obs);
					//查询数据
					List<ObsBean> obsBeans=tObsMapper.queryObservationResult(obsBeanobj);	
					//获取json
					String contentstr=obsBeans.get(0).getContent();
					//读取数据
					Map contentmap=mapper.readValue(contentstr, Map.class);
					//存放所有的物种
					Map contentobj=new HashMap();
					//循环所有的物种key
					for(Object contentmapkey:contentmap.keySet()){	
						//记录数据
						Map contentobj_on=new HashMap();
						//循环结果集合
						for(int i=0;i<obsBeans.size();i++){
							//获取日期
							String contentobj_on_time=DateUtil.DATEtoString(obsBeans.get(i).getDate(), "yyyy-MM-dd");
							//获取json数据
							String contentobj_on_str=obsBeans.get(i).getContent();
							Map contentobj_on_map=mapper.readValue(contentobj_on_str, Map.class);
							//循环结果
							for(Object contentobj_on_key:contentobj_on_map.keySet()){
								//判断结果
								if(contentmapkey.equals(contentobj_on_key)){
									//判断结果写入对应结果
									if("CO".equals(contentmapkey)){
										String speciesval=contentobj_on_map.get(contentobj_on_key).toString();
										if(speciesval==null||"null".equals(speciesval)||"".equals(speciesval)){
											contentobj_on.put(contentobj_on_time,"-");
											break;
										}else{
											BigDecimal bd=(new BigDecimal(speciesval)).setScale(2, BigDecimal.ROUND_HALF_UP);
											contentobj_on.put(contentobj_on_time,bd);
											break;
										}
									}else{
										if("null".equals(contentobj_on_map.get(contentobj_on_key))||"NULL".equals(contentobj_on_map.get(contentobj_on_key))||"".equals(contentobj_on_map.get(contentobj_on_key))||contentobj_on_map.get(contentobj_on_key)==null){
											contentobj_on.put(contentobj_on_time,"-");
											break;
										}else{
											String speciesval=contentobj_on_map.get(contentobj_on_key).toString();
											BigDecimal bd=(new BigDecimal(speciesval)).setScale(1, BigDecimal.ROUND_HALF_UP);
											contentobj_on.put(contentobj_on_time,bd);
											break;
										}
										
									}
								}
								
							}
						}
						//修改污染物  写入结果
						String contentmapkey_new;
						if("PM2_5".equals(contentmapkey)){	//修改名称
							contentmapkey_new="PM25";
							contentobj.put(contentmapkey_new, contentobj_on);
						}else if("O3".equals(contentmapkey)){
							contentmapkey_new="O3_AVG";
							contentobj.put(contentmapkey_new, contentobj_on);
						}else if("O3_8h".equals(contentmapkey)){
							contentmapkey_new="O3_8_MAX";
							contentobj.put(contentmapkey_new, contentobj_on);
						}else{
							contentobj.put(contentmapkey, contentobj_on);
						}
					}
					//储存结果数据
					Map standardData=new HashMap();
					//观测数据
					standardData.put("观测数据", contentobj);	
					standardData.put("基准数据", spcmapobj);
					//写入数据
					objsed.put("data", standardData);
					objsed.put("scenarinoId","基准数据");
					objsed.put("scenarinoName","基准数据");
					objsed.put("observationId","观测数据");
					objsed.put("observationName","观测数据");
					//时间分辨率--逐日结束
				}else{	//---------------------------时间分辨率---逐小时开始-------------------------------//
					//定义结果集合
					Map spcmapobj=new HashMap();
					//表名Title
					String tables="T_SCENARINO_HOURLY_";
					//情景添加时间
					Date tims=tScenarinoDetaillist.getScenarinoAddTime();
					String nowTime=DateUtil.DATEtoString(tims, "yyyy");
					tables+=nowTime+"_";
					tables+=userId;
					//定义查询对象
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					//空间分辨率--需要时替换即可,数据库中目前只有为3的数据
					scenarinoEntity.setDomain(domain);		
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setDomainId(domainId);
					scenarinoEntity.setsId(tScenarinoDetaillist.getScenarinoId());
					scenarinoEntity.setTableName(tables);
					//执行查询
					List<ScenarinoEntity> Lsclist=tPreProcessMapper.selectBysome(scenarinoEntity);
					if(!Lsclist.isEmpty()){			//查询基准数据开始
						String content=Lsclist.get(0).getContent().toString();
						//总数据
						Map standard=mapper.readValue(content,Map.class);
						
						//循环数据-key=日期
//						for(Object  key : standard.keySet()){
						for(int s=0; s<=differenceVal;s++){
							calendar.setTime(sdf.parse(end_Date));
							calendar.add(Calendar.DAY_OF_MONTH, -s);
							calDate = calendar.getTime();
							calDateStr=sdf.format(calDate);	
							//值
//							Object species=standard.get(key);
							Object species=standard.get(calDateStr);
							Map spcmap= (Map)species;			
							//物种名称开始							
							for(Object  spcmapkey : spcmap.keySet()){ 
								Map standardobj=new HashMap();
								String spcmapkeyw=spcmapkey.toString();
								//键为年份	
//								for(Object standard_Time:standard.keySet()){
								for(int t=0; t<=differenceVal;t++){
									calendar.setTime(sdf.parse(end_Date));
									calendar.add(Calendar.DAY_OF_MONTH, -t);
									calDate = calendar.getTime();
									calDateStr=sdf.format(calDate);	
									
//									Object standard_Times=standard.get(standard_Time);
									Object standard_Times=standard.get(calDateStr);
									Map speciesmap= (Map)standard_Times;
									//循环年份数据
									for(Object speciesmap_key:speciesmap.keySet()){
										//存放结果
										Map standardobjdata=new HashMap();
										String speciesmap_keyn=speciesmap_key.toString();
										if(!speciesmap_keyn.equals(spcmapkeyw)) continue;
										//判断结果类型 存入对应结果
										if(speciesmap_keyn.equals(spcmapkeyw)&&"CO".equals(spcmapkeyw)){													
											Object speciesmap_keyval=speciesmap.get(speciesmap_key);				
											Map speciesmapval= (Map)speciesmap_keyval;
											List qq= (List) speciesmapval.get("0");
											for(int i=0;i<qq.size();i++){
												String standardval=qq.get(i).toString();
												if("".equals(standardval)||"null".equals(standardval)||"NULL".equals(standardval)||null==standardval){
													standardobjdata.put(i, "-");
												}else{
													BigDecimal bd=(new BigDecimal(standardval)).setScale(2, BigDecimal.ROUND_HALF_UP);
													standardobjdata.put(i,bd);
												}
											}					
//											standardobj.put((String)standard_Time, standardobjdata);
											standardobj.put(calDateStr, standardobjdata);
										}else {
											Object speciesmap_keyval=speciesmap.get(speciesmap_key);				
											Map speciesmapval= (Map)speciesmap_keyval;
											List qq= (List) speciesmapval.get("0");
											for(int i=0;i<qq.size();i++){
												String standardval=qq.get(i).toString();
												if("".equals(standardval)||"null".equals(standardval)||"NULL".equals(standardval)||null==standardval){
													standardobjdata.put(i, "-");
												}else{
													BigDecimal bd=(new BigDecimal(standardval)).setScale(2, BigDecimal.ROUND_HALF_UP);
													standardobjdata.put(i,bd);
												}
												
											}	
											//写入结果
//											standardobj.put(standard_Time.toString(), standardobjdata);
											standardobj.put(calDateStr, standardobjdata);
										}
									}
								}
								//写入结果
								spcmapobj.put(spcmapkey.toString(), standardobj);
							}	//物种名称结束
							break;
						}
					}	//基准数结束
					HashMap obsBeanobj=new HashMap();	//查询观测数据开始
					obsBeanobj.put("city_station",cityStation);
					//所选任务的开始时间
//					obsBeanobj.put("startDate", missionStartDatestr);
//					//所选任务的结束时间
//					obsBeanobj.put("endDate", missionEndDatestr);
					obsBeanobj.put("startDate", start_Date);	
					//所选任务的结束时间
					obsBeanobj.put("endDate", end_Date);
					//用于存放新的mode
					String modes;										
					if("point".equals(mode)){
						 modes="station";
					}else{
						 modes=mode;
					}
					obsBeanobj.put("mode",modes);
					//表名title
					String tables_obs="T_OBS_HOURLY_";
					//截取该任务的开始时间来改变查询的表格
					String nowTime_obs= DateUtil.DATEtoString(missionStartDate, "yyyy");	
					tables_obs+=nowTime_obs;
					//查询的表格
					obsBeanobj.put("tableName",tables_obs);				
					//查询观测数据
					List<ObsBean> obsBeans=tObsMapper.queryObservationResult(obsBeanobj);	
					String contentstr=obsBeans.get(0).getContent();
					Map contentmap=mapper.readValue(contentstr, Map.class);
					//存放所有的物种
					Map contentobj=new HashMap();			
					//循环所有的物种key开始
					for(Object contentmapkey:contentmap.keySet()){
						Map contentobj_on=new HashMap();
						//去除O3_8h的数据
						if(!"O3_8h".equals(contentmapkey.toString())){			
							//循环结果
							for(int i=0;i<obsBeans.size();i++){
								//获取日期
								String contentobj_on_time=DateUtil.DATEtoString(obsBeans.get(i).getDate(), "yyyy-MM-dd");
								//获取json
								String contentobj_on_str=obsBeans.get(i).getContent();
								Map contentobj_on_map=mapper.readValue(contentobj_on_str, Map.class);
								//循环结果
								for(Object contentobj_on_key:contentobj_on_map.keySet()){
									//判断是否是相同的
									if(contentmapkey.equals(contentobj_on_key.toString())){
										//判断污染物
										if("CO".equals(contentmapkey)){
											List contentobj_on_key_arrco=(List)contentobj_on_map.get(contentobj_on_key);
											Map contentobj_on_key_val=new HashMap();
											//循环结果集合
											for(int m=0;m<contentobj_on_key_arrco.size();m++){				//循环添加值
												//判断类型存放对英结果
												String observe_coval=contentobj_on_key_arrco.get(m).toString();
												if("-".equals(observe_coval)||"".equals(observe_coval)||"null".equals(observe_coval)||"NULL".equals(observe_coval)||null==observe_coval){	//判断某个值为-时，不进行保留位数操作
													contentobj_on_key_val.put(m, "-");
												}else{
													BigDecimal bd=(new BigDecimal(observe_coval)).setScale(2, BigDecimal.ROUND_HALF_UP);
													contentobj_on_key_val.put(m, bd);
												}
												
											}
											contentobj_on.put(contentobj_on_time,contentobj_on_key_val);
											break;
										}else{
											List contentobj_on_key_arr=(List)contentobj_on_map.get(contentobj_on_key);
											Map contentobj_on_key_val=new HashMap();
											for(int m=0;m<contentobj_on_key_arr.size();m++){
												
												String observeval=contentobj_on_key_arr.get(m).toString();
												if("-".equals(observeval)||"".equals(observeval)||"null".equals(observeval)||"NULL".equals(observeval)||null==observeval){
													contentobj_on_key_val.put(m, "-");
												}else{
													BigDecimal bd=(new BigDecimal(observeval)).setScale(1, BigDecimal.ROUND_HALF_UP);
													contentobj_on_key_val.put(m, bd);
												}
												
											}
											//写入结果
											contentobj_on.put(contentobj_on_time,contentobj_on_key_val);
											break;
										}
									}
								}
							}
							//新污染物
							String contentmapkey_new;
							//判断污染物存入对应结果
							if("PM2_5".equals(contentmapkey)){
								contentmapkey_new="PM25";
								contentobj.put(contentmapkey_new, contentobj_on);
							}else if("O3".equals(contentmapkey)){
								contentmapkey_new="O3_AVG";
								contentobj.put(contentmapkey_new, contentobj_on);
							}else if("O3_8h".equals(contentmapkey)){
								contentmapkey_new="O3_8_MAX";
								contentobj.put(contentmapkey_new, contentobj_on);
							}else{
								contentobj.put(contentmapkey, contentobj_on);
							}
						}	
					}	//循环所有物种的key结束
					Map standardData=new HashMap();
					//观测数据
					standardData.put("观测数据", contentobj);	
					standardData.put("基准数据", spcmapobj);		//基准数据
					objsed.put("data", standardData);
 				//写入结果数据
					objsed.put("scenarinoId","基准数据");
					objsed.put("scenarinoName","基准数据");
					objsed.put("observationId","观测数据");
					objsed.put("observationName","观测数据");
				}//时间分辨率---逐小时结束
			}else{
				//没有情景
				return AmpcResult.build(1000, "该任务没有创建情景",null);
			}
			//返回结果
			return AmpcResult.build(0, "success",objsed);
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 根据任务id以及userid查询情景有异常",e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	
	
	
	
	
	/**
	 * 评估报告数据查询接口
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	
	@RequestMapping("Appraisal/report")
	public AmpcResult report(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取用户ID
			Object param=data.get("userId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController 用户ID为空或出现非法字符!");
				return AmpcResult.build(1003, "用户ID为空或出现非法字符!");
			}
			// 用户id
			Long userId = Long.parseLong(param.toString());
			//情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//获取任务ID
			param=data.get("missionId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController  任务id为空或出现非法字符!");
				return AmpcResult.build(1003, "任务id为空或出现非法字符!");
			}
			Long missionId=Long.valueOf(param.toString());
			String start=data.get("startdate").toString();
			Date startdate=DateUtil.StrToDate(start);
			String end=data.get("enddate").toString();
			Date enddate=DateUtil.StrToDate(end);
			Long betweenDays = (long)((enddate.getTime() - startdate.getTime()) / (1000 * 60 * 60 *24) + 0.5);
			List<Date> datelist=new ArrayList();	
			SimpleDateFormat daysdf=new SimpleDateFormat("yyyy-MM-dd");
			for(int a=0;a<=betweenDays;a++){
				Calendar cal = Calendar.getInstance();
				cal.setTime(startdate);
				cal.add(Calendar.DATE, a);
				String addTimeDate =daysdf.format(cal.getTime());
				Date date=daysdf.parse(addTimeDate);//对应情景起报日期
				datelist.add(date);
			}
			param=data.get("mode");//获取站点类型
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("AppraisalController  站点类型为空或出现非法字符!");
				return AmpcResult.build(1003, "站点类型为空或出现非法字符!");
			}
			String mode=param.toString();
			
			//获取站点
			String cityStation;
			if("city".equals(mode)){
				//检测站点具体值
				 cityStation=data.get("cityStation").toString().substring(0, 4);	
			}else{
				 cityStation=data.get("cityStation").toString();					
			}
						
			//定义情景查询对象
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setUserId(userId);
			tScenarinoDetail.setMissionId(missionId);
			//进行数据查询
			TScenarinoDetail tScenarinoDetaillist=tScenarinoDetailMapper.selectBystandard(tScenarinoDetail);
			//获取情景开始时间
			String startDate= DateUtil.DATEtoString(tScenarinoDetaillist.getScenarinoStartDate(), "yyyy-MM-dd");	
			//获取情景结束时间
			String endDate= DateUtil.DATEtoString(tScenarinoDetaillist.getScenarinoEndDate(), "yyyy-MM-dd");
			//该任务下的所有数据
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);	
			//获取domainId
			Long domainId=tMissionDetail.getMissionDomainId();
			Map scmap=new HashMap();
			//任务开始时间
			Date missionStartDate=tMissionDetail.getMissionStartDate();
			String missionStartDatestr=DateUtil.DATEtoString(missionStartDate, "yyyy-MM-dd HH:mm:ss");
			//任务结束时间
			Date missionEndDate=tMissionDetail.getMissionEndDate();		
			String missionEndDatestr=DateUtil.DATEtoString(missionEndDate, "yyyy-MM-dd HH:mm:ss");
			//定义结果集合
			Map objsed=new HashMap();
			//如果情景不为空
			if(tScenarinoDetaillist!=null){
				//时间分辨率--逐日开始
					//所有物种集合
					Map spcmapobj=new HashMap();
					//查询基准数据开始
					String tables="T_SCENARINO_DAILY_";
					//获取情景添加时间
					Date tims=tScenarinoDetaillist.getScenarinoAddTime();
					//转换获取年份 拼接表名
					String nowTime= DateUtil.DATEtoString(tims,"yyyy");
					tables+=nowTime+"_";
					tables+=userId;
					//定义查询实体类
					ScenarinoEntity scenarinoEntity=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					//空间分辨率--需要时替换即可,数据库中目前只有为3的数据
					
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setDomainId(domainId);
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					//查询结果
					List<ScenarinoEntity> Lsclist=tPreProcessMapper.selectBysomeRE(scenarinoEntity);
					//如果为空
					if(!Lsclist.isEmpty()){
						//获取数据Json串
						String content=Lsclist.get(0).getContent();
						//总数据
						Map standard=mapper.readValue(content, Map.class);
						//循环数据集合       最外层的日期
						Map<String,Object> datemap=new HashMap();
						System.out.println(standard.keySet());
						for(Object key : standard.keySet()){
							Date s=daysdf.parse(key.toString());
							if(datelist.contains(s)){
							//值     根据日期获取当前日期下的所有值
							Object species=standard.get(key);				
							Map spcmap= (Map)species;
							//循环当前时间的所有物种名称
							for(Object  spcmapkey : spcmap.keySet()){ 	
								//单个物种所有时间数据
								Map standardobj=new HashMap();
								//物种名称
								String spcmapkeyw=spcmapkey.toString();
								Object num=spcmap.get(spcmapkeyw);
								Map numMap= (Map)num;
								BigDecimal bd=(new BigDecimal(numMap.get("0").toString()));
								if(datemap.get(spcmapkey)==null){
									datemap.put(spcmapkey.toString(), bd);
									}else{
									BigDecimal bc=new BigDecimal(datemap.get(spcmapkey).toString()).add(bd);
									datemap.put(spcmapkey.toString(), bc);
									}
									scmap.put("基准情景", datemap);
							}//物种名称结束
						}
						}
					}	/**查询基准数据结束*/
					
					String tabless="T_SCENARINO_DAILY_";
					//获取情景添加时间
					Date timss=tScenarinoDetail.getScenarinoAddTime();
					//定义格式化类
					DateFormat df = new SimpleDateFormat("yyyy");
					//进行格式化
					String nowTimes= df.format(tims);
					tabless+=nowTime+"_";
					tabless+=userId;
					//定义查询类 并写入查询条件
					ScenarinoEntity scenarinoEntitys=new ScenarinoEntity();
					scenarinoEntity.setCity_station(cityStation);
					scenarinoEntity.setDomainId(domainId);
					scenarinoEntity.setMode(mode);
					scenarinoEntity.setsId(scenarinoId);
					scenarinoEntity.setTableName(tables);
					//进行查询
					List<ScenarinoEntity> sclist=tPreProcessMapper.selectBysomeRE(scenarinoEntity);
					if(!sclist.isEmpty()){
					for(ScenarinoEntity sc:sclist){
						String scid=String.valueOf(sc.getsId());
						Object content=sc.getContent();
						JSONObject obj=JSONObject.fromObject(content);//行业减排结果
						Map<String,Object> detamap=(Map)obj;
						Map<String,Object> datemap=new HashMap();
						for(String datetime:detamap.keySet()){
							Date s=daysdf.parse(datetime);
							if(datelist.contains(s)){
							Object sp=detamap.get(datetime);
							Map<String,Object> spmap=(Map)sp;
							for(String spr:spmap.keySet()){
								Map<String,Object> spcmap=new HashMap();
								Object height=spmap.get(spr);
								Map<String,Object> heightmap=(Map)height;
								Map<String,Object> hourcmap=new HashMap();
								BigDecimal bd=(new BigDecimal(heightmap.get("0").toString()));
								if(datemap.get(spr)==null){
								datemap.put(spr, bd);
								}else{
								BigDecimal bc=new BigDecimal(datemap.get(spr).toString()).add(bd);
								datemap.put(spr, bc);
								}
								scmap.put("减排情景", datemap);
							}
							
						}
						}
					}
			}else{
				return AmpcResult.build(1000, "该任务没有创建情景",null);
			}
			
			}else{
				//没有情景
				return AmpcResult.build(1000, "该任务没有创建情景",null);
			}
			
			Map spr=(Map)scmap.get("减排情景");
			Map jzsp=(Map)scmap.get("基准情景");
			Map<String,String> concentration=new HashMap();
			for(Object sp:spr.keySet()){
				BigDecimal jp=new BigDecimal(spr.get(sp).toString());
				BigDecimal jz=new BigDecimal(jzsp.get(sp).toString());
				BigDecimal jpavg=jp.divide(new BigDecimal(datelist.size()), 6, BigDecimal.ROUND_HALF_UP);
				BigDecimal jzavg=jz.divide(new BigDecimal(datelist.size()), 6, BigDecimal.ROUND_HALF_UP);
				BigDecimal jg=(jzavg.subtract(jpavg)).divide(jzavg, 6, BigDecimal.ROUND_HALF_UP);
				Double con=jg.doubleValue();
				if(con<0){
				Double co=con*100;	
				BigDecimal bd=new BigDecimal(co).setScale(1, BigDecimal.ROUND_HALF_UP);
				String ser=bd.toString().substring(1);
				String updown="上升"+ser+"%";
				concentration.put(sp.toString(), updown);
				}else{
					Double co=con*100;	
					BigDecimal bd=new BigDecimal(co).setScale(1, BigDecimal.ROUND_HALF_UP);
					String updown="下降"+bd+"%";
					concentration.put(sp.toString(), updown);	
				}
			}
			Map<String,BigDecimal> jpmap=new HashMap();
			TEmissionDetailWithBLOBs tEmission=new TEmissionDetailWithBLOBs();
			tEmission.setScenarinoId(scenarinoId);
			tEmission.setEmissionType("2");
			//查询情景下的所有减排结果
			List<TEmissionDetailWithBLOBs> tEmissions=tEmissionDetailMapper.selectByEntity(tEmission);
			for(TEmissionDetailWithBLOBs emission:tEmissions){
			if(datelist.contains(emission.getEmissionDate())){
				Object detail=emission.getEmissionDetails();
				Map<String,Object> details=(Map)detail;
				for(String industry:details.keySet()){
					Map<String,Object> num=(Map) details.get(industry);
					for(String spe:num.keySet()){
						if(jpmap.keySet().contains(spe)){
							BigDecimal old=new BigDecimal(jpmap.get(spe).toString());
							BigDecimal news=new BigDecimal(num.get(spe).toString());
							BigDecimal yes=news.add(old);
							jpmap.put(spe, yes);
						}else{
							BigDecimal news=new BigDecimal(num.get(spe).toString());
							jpmap.put(spe, news);
						}
					}	
				}
			}
			}
			for(String sp:jpmap.keySet()){
				BigDecimal num=jpmap.get(sp);
				if(num.doubleValue()>0){
					BigDecimal yesd=num.setScale(0,BigDecimal.ROUND_HALF_UP);
					String ye="减少"+yesd+"吨";
					concentration.put(sp.toString()+"_jp", ye);
				}else{
					BigDecimal yesd=num.setScale(0,BigDecimal.ROUND_HALF_UP);
					String ser=yesd.toString().substring(1);
					String updown="增加"+ser+"%";
					concentration.put(sp.toString()+"_jp", updown);
				}
			}
			
			
			//返回结果
			return AmpcResult.ok(concentration);
		}catch(Exception e){
			LogUtil.getLogger().error("MissionAndScenarinoController 查询评估报告数据异常",e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	}
	/**
	 * 查询任务的开始时间和结束 
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("Appraisal/show_Times")
	public AmpcResult showTime(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			//设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String,Object> data=(Map)requestDate.get("data");
			//获取任务ID
			Object param=data.get("missionId");
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("AppraisalController  任务id为空或出现非法字符!");
				return AmpcResult.build(1003, "任务id为空或出现非法字符!");
			}
			Long missionId=Long.valueOf(param.toString());
			//查询该任务的数据
			TMissionDetail tMissionDetail=tMissionDetailMapper.selectByPrimaryKey(missionId);
			Map dates=new HashMap();
			dates.put("startTime", DateUtil.DATEtoString(tMissionDetail.getMissionStartDate(), "yyyy-MM-dd HH:mm:ss"));
			dates.put("endTime",  DateUtil.DATEtoString(tMissionDetail.getMissionEndDate(),"yyyy-MM-dd HH:mm:ss"));
			return AmpcResult.build(0, "success",dates);
		}catch(Exception e){
			LogUtil.getLogger().error("AppraisalController 查询任务的开始时间和结束有异常",e);
			return AmpcResult.build(1000, "参数错误",null);
		}
	} 
}
