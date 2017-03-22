package ampc.com.gistone.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;

@RestController
@RequestMapping
public class EMissionController {

	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	
	
	
	@RequestMapping("save_emission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult save_emission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
	try{
		ClientUtil.SetCharsetAndHeader(request, response);
		Map<String, Map> data = (Map) requestDate.get("data");
		int a=0;
		int b=0;
		//编写正则表达式
	    String reg="0000$";
		String reg2="00$";
		//变异正则表达式
		Pattern pattern1 = Pattern.compile(reg);
		Pattern pattern2 = Pattern.compile(reg2);
		for(Map<String,Object> datas:data.values()){
		String emdate=datas.get("date").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date emissiondate=sdf.parse(emdate);
		Map emission=(Map) datas.get("emission");	
		JSONObject jasonObject = JSONObject.fromObject(emission);
		Map<String,Object> map= (Map)jasonObject;
		for(String code:map.keySet()){
			TEmissionDetail temission=new TEmissionDetail();
			temission.setEmissionDate(emissiondate);
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
			temission.setEmissionDetails(map.get(code).toString());
			a+=tEmissionDetailMapper.insertSelective(temission);
			b++;
		}
		}
		
		if(a==b){
			return AmpcResult.build(0, "success",null);
		}
		return AmpcResult.build(1000, "error",null);
		
	}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误",null);	
		}
	}
	@RequestMapping("find_emission")
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED) 
	public AmpcResult find_emission(@RequestBody Map<String,Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		try{
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			TEmissionDetail tEmission=new TEmissionDetail();
			String pollutant=data.get("pollutant").toString();	
			String emDate=data.get("emissionDate").toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date emissiondate=sdf.parse(emDate);
			tEmission.setCodeLevel(data.get("codeLevel").toString());
			tEmission.setEmissionDate(emissiondate);
			List<TEmissionDetail> tEmissions=tEmissionDetailMapper.selectByEntity(tEmission);
			JSONArray arr=new JSONArray();
			List plist=new ArrayList();
			List ctlist=new ArrayList();
			List coulist=new ArrayList();
			for(TEmissionDetail emission:tEmissions){
				JSONObject reobj=new JSONObject();
				String detail=emission.getEmissionDetails();
				JSONObject obj=JSONObject.fromObject(detail);
				Map<String,Object> details=(Map)obj;
				if(emission.getCodeLevel().equals("2")){
					//查看当前返回值是否包含此次遍历的code
					if(!ctlist.contains(emission.getCode())){
				if(emission.getCode().equals("522200")){
					reobj.put("code", "520600");
					ctlist.add("520600");
					ctlist.add("522200");
				}else if(emission.getCode().equals("522400")){
					reobj.put("code", "520500");
					ctlist.add("520500");
					ctlist.add("522400");
				}else if(emission.getCode().equals("632100")){
					reobj.put("code", "630200");	
					ctlist.add("630200");
					ctlist.add("632100");
				}else{
					reobj.put("code", emission.getCode());
					ctlist.add(emission.getCode());
				}
					}else{
						continue;
					}
				}else if(emission.getCodeLevel().equals("3")){
					if(!coulist.contains(emission.getCode())){
					if(emission.getCode().equals("310101")||emission.getCode().equals("310103")){
						reobj.put("code", "310101");
						coulist.add("310101");
						coulist.add("310103");
					}else if(emission.getCode().equals("310115")||emission.getCode().equals("310119")){
						reobj.put("code", "310115");
						coulist.add("310115");
						coulist.add("310119");
					}else if(emission.getCode().equals("341402")||emission.getCode().equals("341421")||
							emission.getCode().equals("341422")||emission.getCode().equals("341423")||
							emission.getCode().equals("341424")){
						continue;
					}else{
						reobj.put("code", emission.getCode());
						coulist.add(emission.getCode());	
					}
					}else{
						continue;
					}
				}else{
					if(!plist.contains(emission.getCode())){
					reobj.put("code", emission.getCode());
					plist.add(emission.getCode());
					}else{
						continue;	
					}
				}
				reobj.put("emissionDate", emissiondate.getTime());
				JSONArray irarr=new JSONArray();
				for(String industry:details.keySet()){
					JSONObject irobj=new JSONObject();
					Map<String,Object> em=(Map) details.get(industry);
					BigDecimal reduction=new BigDecimal(em.get(pollutant).toString());
					irobj.put("industry", industry);
					irobj.put(pollutant, reduction);
					irarr.add(irobj);
				}
				reobj.put("reduction", irarr);
				arr.add(reobj);
			}	
			return AmpcResult.build(0, "success",arr);
		}catch(Exception e){
			e.printStackTrace();
			return AmpcResult.build(1000, "error",null);	
		}
		
		
	}
	
	
	
	
}
