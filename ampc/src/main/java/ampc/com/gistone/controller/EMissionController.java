package ampc.com.gistone.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.sql.DATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TEmissionDetailMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TEsNative;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.RequestRegionData;
import ampc.com.gistone.util.codeTransformUtil;

@RestController
@RequestMapping
public class EMissionController {

	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	@Autowired
	private RequestRegionData rrd=new RequestRegionData();
	
	
	@Autowired
	private TEsNativeMapper tEsNativeMapper;
	/**
	 * 保存减排计算结果
	 * @param requestDate
	 * @param JOBId
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("save_emission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult save_emission(@RequestBody Map<String,Object> requestDate,Long jobId,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String, Map> data = (Map) requestDate.get("data");
		String status=requestDate.get("status").toString();
		
		Long scenarionId=jobId;
		System.out.println(scenarionId);
		TEsNative tEsNative =new TEsNative();
		tEsNative.setEsCodeRange("11,12,13");
		int a=0;
		int b=0;
		//编写正则表达式
	    String reg="0000$";
		String reg2="00$";
		//编译正则表达式
		Pattern pattern1 = Pattern.compile(reg);
		Pattern pattern2 = Pattern.compile(reg2);
		//获取当前时间
		Date date=new Date();
		SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
		String newdat=hms.format(date);
		Date thedate=hms.parse(newdat);
		//判断参数是否正常，正常继续执行程序，不正常返回error
		if(status.equals("success")){
		//循环data的value
		for(Map<String,Object> datas:data.values()){
		String emdate=datas.get("date").toString();//获取date值
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		Date emissiondate=sdf.parse(emdate);
		Map emission=(Map) datas.get("emission");//获取emission的值	
		JSONObject jasonObject = JSONObject.fromObject(emission);
		Map<String,Object> map= (Map)jasonObject;//将emission的值	转化为Map集合
		List<String> codelist=new ArrayList<String>();
		Map<String,Object> newmap=codeTransformUtil.codeTransformEmission(map, tEsNative);
		for(String code:newmap.keySet()){//根据key进行遍历
			if(!codelist.contains(code)){//去除重复省市重复存储
			TEmissionDetail temission=new TEmissionDetail();
			temission.setEmissionDate(emissiondate);
				codelist.add(code);
			temission.setCode(code);
			 String pcode=code.substring(0, 2);
			 String ccode=code.substring(2,4);
			 Matcher matcher1 = pattern1.matcher(code);
			 Matcher matcher2 = pattern2.matcher(code);
			if(matcher1.find()){
				temission.setCodeLevel("1");				
			}else if(matcher2.find()){
				temission.setCodeLevel("2");	
			}else{
				temission.setCodeLevel("3");
			}
			Map somemap=(Map) newmap.get(code);//获取emission的值	
			JSONObject someobj = JSONObject.fromObject(somemap);
			Map<String,Object> lastMap= (Map)someobj;//将emission的值	转化为Map集合
			
			
			temission.setEmissionType("2");
			temission.setScenarunoId(scenarionId);
			List<TEmissionDetail> telist=tEmissionDetailMapper.selectByEntity(temission);
			if(telist.isEmpty()){
			temission.setMeasureReduce(lastMap.get("op").toString());
			temission.setEmissionDetails(lastMap.get("category").toString());
			a+=tEmissionDetailMapper.insertSelective(temission);//保存数据
			}else{
				return AmpcResult.build(1000, "error","该情景减排结果已存在");
			}
			b++;//计数器
			}else{
				continue;
			}
		}
		}
		
		if(a==b){//查看计数器的数值是否与保存成功的数据相同，相同为成功，不同则失败
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setScenarinoId(scenarionId);
			tScenarinoDetail.setScenarinoStatus(8l);//如成功修改情景的状态为8，执行完毕
			tScenarinoDetail.setRatioEndDate(thedate);
			int s=tScenarinoDetailMapper.updateByPrimaryKeySelective(tScenarinoDetail);
			if(scenarionId==null){
				return AmpcResult.build(1000, "error","无情景id");
			}
			if(s!=0){
			return AmpcResult.build(0, "success");
			}
		}
		TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
		tScenarinoDetail.setScenarinoId(scenarionId);
		tScenarinoDetail.setScenarinoStatus(4l);//如失败修改情景的状态为4，执行失败
		tScenarinoDetailMapper.updateByPrimaryKeySelective(tScenarinoDetail);
		return AmpcResult.build(1000, "error","保存数量错误");
		}else{
			TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
			tScenarinoDetail.setScenarinoId(scenarionId);
			tScenarinoDetail.setScenarinoStatus(4l);//如参数状态为error修改情景的状态为4，执行失败
			tScenarinoDetailMapper.updateByPrimaryKeySelective(tScenarinoDetail);
			return AmpcResult.build(1000, "参数有误");	
		}
	}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "error");	
		}
	}
	
	
	
	
	
	
	
	
	
	
	@RequestMapping("save_baseemission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult save_baseemission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String, Map> data = (Map) requestDate.get("data");
		String status=requestDate.get("status").toString();
		int a=0;
		int b=0;
		//编写正则表达式
	    String reg="0000$";
		String reg2="00$";
		//编译正则表达式
		Pattern pattern1 = Pattern.compile(reg);
		Pattern pattern2 = Pattern.compile(reg2);
		//获取当前时间
		Date date=new Date();
		SimpleDateFormat hms = new SimpleDateFormat("yyyy-MM-dd");
		String newdat=hms.format(date);
		Date thedate=hms.parse(newdat);
		//判断参数是否正常，正常继续执行程序，不正常返回error
		if(status.equals("success")){
		//循环data的value
		for(Map<String,Object> datas:data.values()){
		String emdate=datas.get("date").toString();//获取date值
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		Date emissiondate=sdf.parse(emdate);
		Map emission=(Map) datas.get("emission");//获取emission的值	
		JSONObject jasonObject = JSONObject.fromObject(emission);
		Map<String,Object> map= (Map)jasonObject;//将emission的值	转化为Map集合
		List<String> codelist=new ArrayList<String>();
		for(String code:map.keySet()){//根据key进行遍历
			if(!codelist.contains(code)){
			TEmissionDetail temission=new TEmissionDetail();
			temission.setEmissionDate(emissiondate);
				codelist.add(code);
			temission.setCode(code);
			 String pcode=code.substring(0, 2);
			 String ccode=code.substring(2,4);
			 Matcher matcher1 = pattern1.matcher(code);
			 Matcher matcher2 = pattern2.matcher(code);
			if(matcher1.find()){
				temission.setCodeLevel("1");
			}else if(matcher2.find()){
				temission.setCodeLevel("2");
			}else{
				temission.setCodeLevel("3");
			}
			temission.setEmissionDetails(map.get(code).toString());//通过key获取需要的值
			
			a+=tEmissionDetailMapper.insertSelective(temission);//保存数据
			b++;//计数器
			}else{
				continue;
			}
		}
		}
		
		if(a==b){//查看计数器的数值是否与保存成功的数据相同，相同为成功，不同则失败
			return AmpcResult.build(0, "success","123");
		}
		return AmpcResult.build(1000, "error","345");
		}else{
			return AmpcResult.build(1000, "参数有误","567");	
		}
	}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "error");	
		}
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping("find_baseEmission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_baseEmission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TEmissionDetail tEmission=new TEmissionDetail();
			String pollutant=data.get("pollutant").toString();
			Long scenarinoId=Long.valueOf(data.get("scenarinoId").toString());
			String level=data.get("codeLevel").toString();
			tEmission.setEmissionType("1");
			List<TEmissionDetail> tEmissions=tEmissionDetailMapper.selectByEntity(tEmission);
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			//JSONArray industryarr=JSONArray.fromObject(tScenarinoDetail.getExpand1());
			
			List<Date> datelist=new ArrayList<Date>();
			for(TEmissionDetail em:tEmissions){
				if(!datelist.contains(em.getEmissionDate())){
					datelist.add(em.getEmissionDate());
				}else{
					continue;
				}
				
			}
			Map<String,Object> objss=new HashMap();
			for(Date dates:datelist){
			String datss=dates.toString();
			JSONArray arry=new JSONArray();
			for(TEmissionDetail emission:tEmissions){
				String emtime=emission.getEmissionDate().toString();
				Map<String,BigDecimal> map=new HashMap();
				
				if(emtime.equals(datss)){
			
				String detail=emission.getEmissionDetails();
				JSONObject obj=JSONObject.fromObject(detail);//行业减排结果
				Map<String,Object> details=(Map)obj;
				for(String industry:details.keySet()){
					//if(industryarr.contains(industry)){
					Map num=(Map) details.get(industry);
					JSONObject jasonObject = JSONObject.fromObject(num);
					Map<String,Object> nummap= (Map)jasonObject;
				if(level.equals("1")){
					String pcode=emission.getCode().substring(0, 2)+"0000";
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode, yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}
				}
				if(level.equals("2")){
					String pcode=emission.getCode().substring(0, 4)+"00";
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode,yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}
				}
				if(level.equals("3")){
					String pcode=emission.getCode();
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode, yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}
				}
					//}else{
					//	continue;
						
					//}
				}//行业循环
				arry.add(map);
				}else{//判断时间是否一样
					continue;
				}
			}	//集合循环
			objss.put(datss, arry);
			}//时间循环
			Map<String,BigDecimal> refmap=new HashMap();
			for(String datese:objss.keySet()){
				//获取相对时间的数组
				JSONArray num=JSONArray.fromObject(objss.get(datese).toString());

				for(Object noes:num){
					Map mapse=(Map)	noes;
					JSONObject jasonObject = JSONObject.fromObject(mapse);
					Map<String,Object> numma= (Map)jasonObject;
					for(String nums:numma.keySet()){
						if(refmap.keySet().contains(nums)){
							BigDecimal old=new BigDecimal(refmap.get(nums).toString());
							BigDecimal news=new BigDecimal(numma.get(nums).toString());
							BigDecimal yes=news.add(old);
							refmap.put(nums,yes);
						}else{
							refmap.put(nums,new BigDecimal(numma.get(nums).toString()));
						}
						}
				}
			}
			return AmpcResult.build(0, "success",refmap);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "error",null);
		}
		
		
	}
	
	/**
	 * 查询减排结果
	 * @param requestDate
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("find_reduceEmission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_reduceEmission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TEmissionDetail tEmission=new TEmissionDetail();
			String pollutant=data.get("pollutant").toString();	//污染物
			Long scenarinoId=Long.valueOf(data.get("scenarinoId").toString());//情景id
			String level=data.get("codeLevel").toString();	//code级别
			tEmission.setScenarunoId(scenarinoId);
			tEmission.setEmissionType("2");
			//查询情景下的所有减排结果
			List<TEmissionDetail> tEmissions=tEmissionDetailMapper.selectByEntity(tEmission);
			//查询情景主要为了查询共有措施以及行业
			TScenarinoDetail tScenarinoDetail=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			//JSONArray industryarr=JSONArray.fromObject(tScenarinoDetail.getExpand1());
			//将所有日期不重复加入集合
			List<Date> datelist=new ArrayList<Date>();
			for(TEmissionDetail em:tEmissions){
				if(!datelist.contains(em.getEmissionDate())){
					datelist.add(em.getEmissionDate());
				}else{
					continue;
				}	
			}
			Map<String,Object> objss=new HashMap();
			//遍历日期集合
			for(Date dates:datelist){
				//将当前日期转化为String
			String datss=dates.toString();
			JSONArray arry=new JSONArray();
			//遍历查询的减排结果
			for(TEmissionDetail emission:tEmissions){
				String emtime=emission.getEmissionDate().toString();
				Map<String,BigDecimal> map=new HashMap();
				//判断当前遍历的时间与所遍历的减排结果日期是否相同，相同的话继续执行不相同减排结果进行下一循环
				if(emtime.equals(datss)){
			
				String detail=emission.getEmissionDetails();
				JSONObject obj=JSONObject.fromObject(detail);//行业减排结果
				Map<String,Object> details=(Map)obj;
				//遍历减排结果所有的key也就是行业
				for(String industry:details.keySet()){
					//if(industryarr.contains(industry)){
					//根据key行业得出各物质减排结果的map集合
					Map num=(Map) details.get(industry);
					JSONObject jasonObject = JSONObject.fromObject(num);
					Map<String,Object> nummap= (Map)jasonObject;
					//查看需求是什么级别（省？市？区？）根据级别保存对应级别数据
				if(level.equals("1")){
					String pcode=emission.getCode().substring(0, 2)+"0000";
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode, yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}	
				}
				if(level.equals("2")){
					String pcode=emission.getCode().substring(0, 4)+"00";
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode,yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}	
				}
				if(level.equals("3")){
					String pcode=emission.getCode();
					if(map.keySet().contains(pcode)){
						BigDecimal old=new BigDecimal(map.get(pcode).toString());
						BigDecimal news=new BigDecimal(nummap.get(pollutant).toString());
						BigDecimal yes=news.add(old);
						map.put(pcode, yes);
					}else{
						map.put(pcode, new BigDecimal(nummap.get(pollutant).toString()));
					}	
				}
					//}else{
					//	continue;
						
					//}
				}//行业循环
				arry.add(map);	
				}else{//判断时间是否一样
					continue;
				}
			}	//集合循环
			objss.put(datss, arry);
			}//时间循环
			Map<String,BigDecimal> refmap=new HashMap();
			for(String datese:objss.keySet()){
				//获取相对时间的数组
				JSONArray num=JSONArray.fromObject(objss.get(datese).toString());
				//遍历数组将不同日期的相同污染物相加
				for(Object noes:num){
					//遍历每个日期的结果
					Map mapse=(Map)	noes;
					JSONObject jasonObject = JSONObject.fromObject(mapse);
					Map<String,Object> numma= (Map)jasonObject;
					//将每个日期的结果进行相加
					for(String nums:numma.keySet()){
						if(refmap.keySet().contains(nums)){
							BigDecimal old=new BigDecimal(refmap.get(nums).toString());
							BigDecimal news=new BigDecimal(numma.get(nums).toString());
							BigDecimal yes=news.add(old);
							refmap.put(nums,yes);
						}else{
							refmap.put(nums,new BigDecimal(numma.get(nums).toString()));
						}
						}
				}
			
			}
			return AmpcResult.build(0, "success",refmap);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "error",null);	
		}
		
		
	}
	
	/**
	 * 保存站点接口
	 * @return
	 */
	@RequestMapping("find_sion")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_sion(){
		try {
			 rrd.request();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AmpcResult.build(0, "success");
	}
	
	@RequestMapping("/codenew")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult codenew(){
		TEsNative tEsNative=new TEsNative();
		tEsNative.setEsCodeRange("11,12,13");
		List<Long> list=new ArrayList<Long>();
		list.add(410200l);
		list.add(130600l);
		List<Long> newlist=codeTransformUtil.codeTransform(list, tEsNative);
		return AmpcResult.build(0, "success",newlist);
	}
	
}
