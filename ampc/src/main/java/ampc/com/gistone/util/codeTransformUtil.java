package ampc.com.gistone.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONObject;
import ampc.com.gistone.database.model.TEsNative;

@RestController
@RequestMapping

public class codeTransformUtil {
	
	public static List<Long> codeTransform(List<Long> codelist,TEsNative tEsNative){
		List<Long> list=new ArrayList<Long>();
		List<Long> havelist=new ArrayList<Long>();
		String str=tEsNative.getEsCodeRange();
		String[] strArray=null;
		strArray=str.split(",");
		 List<String> strlist=Arrays.asList(strArray);
		for(Long code:codelist){
			String codestring=code.toString();
			String thestr=codestring.substring(0, 2);
			Map<String,String> codemap=CodeUtil.codearray();
			for(String codee:codemap.keySet()){
			String codestr=code.toString();
			if(!havelist.contains(code)){
			if(strlist.contains(thestr)){
				list.add(code);
				havelist.add(code);
				continue;
			}
			if(codestr.equals(codee)){
				String newcode=codemap.get(codee);
				String[] newcodeArray=null;
				strArray=newcode.split(",");
				for(String onecode:strArray){
					list.add(Long.valueOf(onecode.toString()));
					havelist.add(Long.valueOf(onecode.toString()));
					havelist.add(code);
				}
			}
			}else{
				continue;
			}
			}
		}	
		return list;
	}
	
	public static Map<String,Object> codeTransformEmission(Map<String,Object> map,TEsNative tEsNative){
		Map<String,Object> codemap=new HashMap<String, Object>();
		Map<String,String> newcodemap=CodeUtil.codearray();
		
		List<String> havelist=new ArrayList<String>();
		String str=tEsNative.getEsCodeRange();
		String[] strArray=null;
		strArray=str.split(",");
		 List<String> strlist=Arrays.asList(strArray);
		for(String code:map.keySet()){
			String codestring=code.toString();
			String thestr=codestring.substring(0, 2);
			String code4=codestring.substring(0, 4);
			Map<String, Object> somemap=(Map<String, Object>) map.get(code);	
			JSONObject someobj = JSONObject.fromObject(somemap);
			Map<String,Object> opMap= (Map<String, Object>)someobj;//将emission的值	转化为Map集
			Map<String,Object> typemap=new HashMap<String, Object>();
			for(String type:opMap.keySet()){
				
				Map<String, Object> indumap=(Map<String, Object>) opMap.get(type);	
				JSONObject indeobj = JSONObject.fromObject(indumap);
				Map<String,Object> lastMap= (Map<String, Object>)indeobj;
				Map<String,Object> industrymap=new HashMap<String, Object>();
				for(String industry:lastMap.keySet()){
				
				Map<String, Object> speciemap=(Map<String, Object>) lastMap.get(industry);//获取emission的值	
				JSONObject speciesobj = JSONObject.fromObject(speciemap);
				Map<String,Object> speciesMap= (Map<String, Object>)speciesobj;//将emission的值	转化为Map集合
				Map<String,Object>  speciesmap=new HashMap<String, Object>();
				for(String species:speciesMap.keySet()){
					
					if(strlist.contains(thestr)){
						if(speciesmap.get(species)!=null&&industrymap.get(industry)!=null){
							BigDecimal old=new BigDecimal(speciesmap.get(species).toString());
							BigDecimal news=new BigDecimal(speciesMap.get(species).toString());
							BigDecimal yes=news.add(old);
							speciesmap.put(species, yes);
							industrymap.put(industry, speciesmap);
							typemap.put(type, industrymap);
							codemap.put(code, typemap);
							continue;
						}else{
							speciesmap.put(species, new BigDecimal(speciesMap.get(species).toString()));
							industrymap.put(industry, speciesmap);
							typemap.put(type, industrymap);
							codemap.put(code, typemap);
							continue;
						}
					}
					for(Entry<String, String> codeset:newcodemap.entrySet()){
						String codehave=codeset.getValue();
					if(codehave.contains(code)){
						if(speciesmap.get(species)!=null&&industrymap.get(industry)!=null){
						BigDecimal old=new BigDecimal(speciesmap.get(species).toString());
						BigDecimal news=new BigDecimal(speciesMap.get(species).toString());
						BigDecimal yes=news.add(old);
						speciesmap.put(species, yes);
						industrymap.put(industry, speciesmap);
						typemap.put(type, industrymap);
						codemap.put(codeset.getKey(), typemap);
						continue;
						}else{
							speciesmap.put(species, new BigDecimal(speciesMap.get(species).toString()));
							industrymap.put(industry, speciesmap);
							typemap.put(type, industrymap);
							codemap.put(codeset.getKey(), typemap);
							continue;
						}
					}else{
						if(codemap.keySet().contains(code4)){
							Map<String, Object> oldmap=(Map<String, Object>) map.get(code4+"00");	
							JSONObject oldobj = JSONObject.fromObject(oldmap);
							Map<String,Object> oldopMap= (Map<String, Object>)oldobj;
							Map<String, Object> oldindumap=(Map<String, Object>) oldopMap.get(type);	
							JSONObject oldindeobj = JSONObject.fromObject(oldindumap);
							Map<String,Object> oldlastMap= (Map<String, Object>)oldindeobj;
							Map<String, Object> oldspeciemap=(Map<String, Object>) oldlastMap.get(industry);//获取emission的值	
							JSONObject oldspeciesobj = JSONObject.fromObject(oldspeciemap);
							Map<String,Object> oldspeciesMap= (Map<String, Object>)oldspeciesobj;
						if(speciesmap.get(species)!=null&&industrymap.get(industry)!=null){
							BigDecimal old=new BigDecimal(oldspeciesMap.get(species).toString());
							BigDecimal news=new BigDecimal(speciesMap.get(species).toString());
							BigDecimal yes=news.add(old);
							speciesmap.put(species, yes);
							industrymap.put(industry, speciesmap);
							typemap.put(type, industrymap);
							codemap.put(code4+"00", typemap);
							continue;
						}else{
							speciesmap.put(species, new BigDecimal(speciesMap.get(species).toString()));
							industrymap.put(industry, speciesmap);
							typemap.put(type, industrymap);
							codemap.put(code4+"00", typemap);
							continue;
						}
						}else{
							if(speciesmap.get(species)!=null&&industrymap.get(industry)!=null){
								BigDecimal old=new BigDecimal(speciesMap.get(species).toString());
								BigDecimal news=new BigDecimal(speciesMap.get(species).toString());
								BigDecimal yes=news.add(old);
								speciesmap.put(species, yes);
								industrymap.put(industry, speciesmap);
								typemap.put(type, industrymap);
								codemap.put(code4+"00", typemap);
								continue;
							}else{
								speciesmap.put(species, new BigDecimal(speciesMap.get(species).toString()));
								industrymap.put(industry, speciesmap);
								typemap.put(type, industrymap);
								codemap.put(code4+"00", typemap);
								continue;
							}
							
							
						}
						
					}
					}
				}
			}
			}
		}
		
		return codemap;
	}
	
}
