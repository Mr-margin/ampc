package ampc.com.gistone.controller;

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
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

@RestController
@RequestMapping
public class EMissionController {

	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
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
	public AmpcResult save_emission(@RequestBody Map<String,Object> requestDate,Long JOBId,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String, Map> data = (Map) requestDate.get("data");
		String status=requestDate.get("status").toString();
		Long scenarionId=JOBId;
		
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
			if(!codelist.contains(code)){//去除重复省市重复存储
			TEmissionDetail temission=new TEmissionDetail();
			temission.setEmissionDate(emissiondate);
			if(code.equals("310101")||code.equals("310103")){//code转换
				code="310101";
				codelist.add("310101");
				codelist.add("310103");
			}else if(code.equals("310115")||code.equals("310119")){	
				code="310115";
				codelist.add("310115");
				codelist.add("310119");
			}else if(code.equals("522200")){
				code="520600";
				codelist.add("520600");
			}else if(code.equals("522400")){
				code="520500";
				codelist.add("520500");
			}else if(code.equals("632100")){
				code="630200";
				codelist.add("630200");
			}else if(code.equals("340100")||code.equals("341402")||code.equals("341421")){
				code="340100";
				codelist.add("340100");
				codelist.add("341402");
				codelist.add("341421");
			}else if(code.equals("340200")||code.equals("341422")){	
				code="340200";
				codelist.add("340200");
				codelist.add("341422");
			}else if(code.equals("340500")||code.equals("341423")||code.equals("341424")){
				code="340500";
				codelist.add("340500");
				codelist.add("341423");
				codelist.add("341424");
			}else{
				codelist.add(code);
			}
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
			Map somemap=(Map) map.get(code);//获取emission的值	
			JSONObject someobj = JSONObject.fromObject(somemap);
			Map<String,Object> lastMap= (Map)someobj;//将emission的值	转化为Map集合
			temission.setMeasureReduce(lastMap.get("op").toString());
			temission.setEmissionDetails(lastMap.get("category").toString());
			
			temission.setEmissionType("2");
			temission.setScenarunoId(scenarionId);
			a+=tEmissionDetailMapper.insertSelective(temission);//保存数据
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
			tScenarinoDetail.setExpand1("");
			tScenarinoDetail.setExpand2("");
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
			if(!codelist.contains(code)){//去除重复省市重复存储
			TEmissionDetail temission=new TEmissionDetail();
			temission.setEmissionDate(emissiondate);
			if(code.equals("310101")||code.equals("310103")){//code转换
				code="310101";
				codelist.add("310101");
				codelist.add("310103");
			}else if(code.equals("310115")||code.equals("310119")){	
				code="310115";
				codelist.add("310115");
				codelist.add("310119");
			}else if(code.equals("522200")){
				code="520600";
				codelist.add("520600");
			}else if(code.equals("522400")){
				code="520500";
				codelist.add("520500");
			}else if(code.equals("632100")){
				code="630200";
				codelist.add("630200");
			}else if(code.equals("340100")||code.equals("341402")||code.equals("341421")){
				code="340100";
				codelist.add("340100");
				codelist.add("341402");
				codelist.add("341421");
			}else if(code.equals("340200")||code.equals("341422")){	
				code="340200";
				codelist.add("340200");
				codelist.add("341422");
			}else if(code.equals("340500")||code.equals("341423")||code.equals("341424")){
				code="340500";
				codelist.add("340500");
				codelist.add("341423");
				codelist.add("341424");
			}else{
				codelist.add(code);
			}
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
	
	
	
	
	
	
	
	
	
	@RequestMapping("find_reduceEmission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_reduceEmission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TEmissionDetail tEmission=new TEmissionDetail();
			String pollutant=data.get("pollutant").toString();	
			Long scenarinoId=Long.valueOf(data.get("scenarinoId").toString());
			String level=data.get("codeLevel").toString();	
			tEmission.setScenarunoId(scenarinoId);
			tEmission.setEmissionType("2");
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
	
	
	
	
}
