package ampc.com.gistone.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONObject;
import ampc.com.gistone.database.inter.TEmissionDetailMapper;
import ampc.com.gistone.database.inter.TEsNativeMapper;
import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TEsNative;

public class BaseSaveUtil {

	@Bean
	public static List save_baseemission(Map mapses){
		try{
			List<TEmissionDetail> list=new ArrayList<TEmissionDetail>();
			TEsNative tEsNative = new TEsNative();
			tEsNative.setEsCodeRange("-1,-2");
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
			//循环data的value
			for(Object datess: mapses.keySet()){
				String sdes=datess.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
			Date emissiondate=sdf.parse(sdes);
			Map noemission=(Map) mapses.get(datess);//获取emission的值	
			Map emission=(Map) noemission.get("emission");
			JSONObject jasonObject = JSONObject.fromObject(emission);
			Map<String,Object> map= (Map)jasonObject;//将emission的值	转化为Map集合
			
			Map<String, Object> newmap = codeTransformUtil
					.codeEmission(map, tEsNative);
			
			List<String> codelist=new ArrayList<String>();
			for(String code:newmap.keySet()){//根据key进行遍历
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
				list.add(temission);
				}else{
					continue;
				}
			}
			}
			
			return list;
			
		}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
}
