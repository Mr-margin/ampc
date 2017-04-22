package ampc.com.gistone.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import ampc.com.gistone.database.model.TEmissionDetailWithBLOBs;
import ampc.com.gistone.database.model.TEsNative;

public class BaseSaveUtil {

	@Bean
	public static List save_baseemission(Map mapses){
		try{
			List<TEmissionDetailWithBLOBs> list=new ArrayList<TEmissionDetailWithBLOBs>();
			Map<String,String> newcodemap=CodeUtil.codearray();
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
			int ax=0;
			Date ds=new Date();
			LogUtil.getLogger().error("开始进行解析"+ds+"------"+mapses.size());
			for(Object datess: mapses.keySet()){//日期
			
				LogUtil.getLogger().error("数据条数"+ax);
				ax++;
				String sdes=datess.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
			Date emissiondate=sdf.parse(sdes);
			Map noemission=(Map) mapses.get(datess);//获取emission的值	
			Map<String,Object> map=(Map<String,Object>) noemission.get("emission");
			
			
//			Map<String, Object> newmap = codeTransformUtil
//					.codeEmission(map, tEsNative);
			
			List<String> codelist=new ArrayList<String>();
			for(String code:map.keySet()){//根据key进行遍历emission
				if(!codelist.contains(code)){
					TEmissionDetailWithBLOBs temission=new TEmissionDetailWithBLOBs();
				temission.setEmissionDate(emissiondate);
					codelist.add(code);
				temission.setCode(code);
				 String pcode=code.substring(0, 2);
				 String ccode=code.substring(2,4);
				 Matcher matcher1 = pattern1.matcher(code);
				 Matcher matcher2 = pattern2.matcher(code);
				if(matcher1.find()){
					temission.setCodelevel("1");
				}else if(matcher2.find()){
					temission.setCodelevel("2");
				}else{
					temission.setCodelevel("3");
				}
				temission.setEmissionDetails(map.get(code).toString());//通过key获取需要的值
				list.add(temission);
				}else{
					continue;
				}
			}
			}
			
			
			
			Iterator<TEmissionDetailWithBLOBs> it = list.iterator();
			LogUtil.getLogger().error("数据数量1:"+list.size());
			LogUtil.getLogger().error("数据转换开始"+new Date());
			Map<String,TEmissionDetailWithBLOBs> zcode=new HashMap();
			 while(it.hasNext()){
				 TEmissionDetailWithBLOBs te= it.next();
				 String col=newcodemap.values().toString();
				String codes= te.getCode().toString();
				if(col.contains(codes)){
				Iterator<Entry<String, String>> iter=newcodemap.entrySet().iterator(); 
				while(iter.hasNext()){
					Entry<String, String> codeset=iter.next();
					String codehave=codeset.getValue();
					 if(codehave.contains(te.getCode())){
						 if(!zcode.keySet().contains(codeset.getKey())){//判断map中是否含有该code
						 te.setCode(codeset.getKey());
						 zcode.put(codeset.getKey(), te);
						 it.remove();
						 }else{
							 TEmissionDetailWithBLOBs tec= zcode.get(codeset.getKey());//获取已存在的改code的对象
							 JSONObject jso=JSONObject.fromObject(tec.getEmissionDetails());
							 Map<String,Object> lastMap= (Map<String, Object>)jso;
							 
							 //获取要合并数据的对象
							 JSONObject jsod=JSONObject.fromObject(te.getEmissionDetails());
							 Map<String,Map<String,Object>> addmap= (Map<String, Map<String,Object>>)jsod;
							 Map los =new HashMap();
							 Iterator<Entry<String, Object>> iters=lastMap.entrySet().iterator(); 
							 Map<String, Object> spcse=new HashMap();
							 while(iters.hasNext()){
								 Entry<String, Object> la= iters.next();
								 String industry=la.getKey();
								 
								 Map<String,Object>  speciesmap=new HashMap<String, Object>();
								 Map<String,Object> spemap= (Map<String,Object>)la.getValue();
								 Iterator<Entry<String, Object>> iterss=spemap.entrySet().iterator(); 
								 while(iterss.hasNext()){
									 
									 Entry<String, Object> spec=iterss.next();
									 String species=spec.getKey();
									 if(!spcse.keySet().contains(species)){
										 spcse.put(species, spec.getValue());	 
									 }else{
										 BigDecimal olds=new BigDecimal(spcse.get(species).toString()); 
										 BigDecimal newds=new BigDecimal(spec.getValue().toString());
										 BigDecimal yesds=newds.add(olds);
										 spcse.put(species, yesds);
									 }
									 
									 BigDecimal old=new BigDecimal(spec.getValue().toString());

									 BigDecimal news=new BigDecimal("0");
									 BigDecimal yes=new BigDecimal("0");
									 if(addmap.get(industry)!=null&&addmap.get(industry).get(species)!=null){
									 news=new BigDecimal(addmap.get(industry).get(species).toString());
										}
										yes=news.add(old);
										
										speciesmap.put(species, yes);
										los.put(industry, speciesmap);
										
								 }
							 }
							JSONObject ss=JSONObject.fromObject(los);
							
							//添加物种吧
							
							
							
							
							
							
							tec.setEmissionDetails(ss.toString());
							Matcher matcher3 = pattern1.matcher(codeset.getKey());
							Matcher matcher4 = pattern2.matcher(codeset.getKey());
							if(matcher3.find()){
								tec.setCodelevel("1");
							}else if(matcher4.find()){
								tec.setCodelevel("2");
							}else{
								tec.setCodelevel("3");
							}
							 zcode.put(codeset.getKey(), tec);
								it.remove();
						 }
					 }
					 
				 }
			 }
				
				  }
			 for(String sss:zcode.keySet()){
		 TEmissionDetailWithBLOBs t= zcode.get(sss);
		 	String ssss=t.getMeasureReduce();
				 list.add(zcode.get(sss));
			 }
			
			
			 
			 
			 List<TEmissionDetailWithBLOBs> lastlist=new ArrayList<TEmissionDetailWithBLOBs>();
			 Iterator<TEmissionDetailWithBLOBs> itl = list.iterator();
			 while(itl.hasNext()){
				 TEmissionDetailWithBLOBs tem= itl.next();
				 JSONObject jsod=JSONObject.fromObject(tem.getEmissionDetails());
				 Map<String,Map<String,Object>> addmaps= (Map<String, Map<String,Object>>)jsod;
				 Iterator<Entry<String,Map<String,Object>>> iters=addmaps.entrySet().iterator(); 
				 Map<String, Object> spcse=new HashMap();
				 Map<String,Map<String,Object>> Lastmaps= new HashMap();
				 BigDecimal BC=new BigDecimal(0);
				 BigDecimal CO=new BigDecimal(0);
				 BigDecimal NH3=new BigDecimal(0);
				 BigDecimal NOx=new BigDecimal(0);
				 BigDecimal SO2=new BigDecimal(0);
				 BigDecimal PM10=new BigDecimal(0);
				 BigDecimal VOC=new BigDecimal(0);
				 BigDecimal PMC=new BigDecimal(0);
				 BigDecimal PMFINE=new BigDecimal(0);
				 BigDecimal PM25=new BigDecimal(0);
				 BigDecimal OC=new BigDecimal(0);
 
				 while(iters.hasNext()){
					 Entry<String,Map<String,Object>> sp= iters.next();
					 Map<String,Object> spl= sp.getValue();
					 Map<String,Object> newmap=ADDSpc(spl);
					 Lastmaps.put(sp.getKey(), newmap);	
					    BC=BC.add(new BigDecimal(newmap.get("BC").toString()));
					    CO=CO.add(new BigDecimal(newmap.get("CO").toString()));
					    NH3=NH3.add(new BigDecimal(newmap.get("NH3").toString()));
					    NOx=NOx.add(new BigDecimal(newmap.get("NOx").toString()));
					    SO2=SO2.add(new BigDecimal(newmap.get("SO2").toString()));
					    PM10=PM10.add(new BigDecimal(newmap.get("PM10").toString()));
					    VOC=VOC.add(new BigDecimal(newmap.get("VOC").toString()));
					    PMC=PMC.add(new BigDecimal(newmap.get("PMC").toString()));
					    PMFINE=PMFINE.add(new BigDecimal(newmap.get("PMFINE").toString()));
					    PM25=PM25.add(new BigDecimal(newmap.get("PM25").toString()));
					    OC=OC.add(new BigDecimal(newmap.get("OC").toString()));
						
				 }
				    tem.setBc(BC);
					tem.setCo(CO);
					tem.setNh3(NH3);
					tem.setNox(NOx);
					tem.setSo2(SO2);
					tem.setPm10(PM10);
					tem.setVoc(VOC);
					tem.setPmc(PMC);
					tem.setPmfine(PMFINE);
					tem.setPm25(PM25);
					tem.setOc(OC);
				 JSONObject ssds=JSONObject.fromObject(Lastmaps);
					tem.setEmissionDetails(ssds.toString());
				 lastlist.add(tem);
			 }
			 LogUtil.getLogger().error("数据转换结束"+new Date());
			return lastlist;
			
		}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		
		public static Map<String,Object> ADDSpc(Map spcse){
			if(!spcse.keySet().contains("PM10")&&!spcse.keySet().contains("PMFINE")&&!spcse.keySet().contains("PMC")){
			Map<String,Object> admap=new HashMap<String, Object>();
			BigDecimal CO=new BigDecimal(spcse.get("CO").toString());
			BigDecimal NH3=new BigDecimal(spcse.get("NH3").toString());
			BigDecimal NOx=new BigDecimal(spcse.get("NOx").toString());
			BigDecimal SO2=new BigDecimal(spcse.get("SO2").toString());
			BigDecimal PM10more=new BigDecimal(spcse.get("PM10more").toString());
			BigDecimal VOC=new BigDecimal(spcse.get("VOC").toString());
			BigDecimal CO2=new BigDecimal(spcse.get("CO2").toString());
			BigDecimal PM25=new BigDecimal(spcse.get("PM25").toString()); 
			BigDecimal PMcoarse=new BigDecimal(spcse.get("PMcoarse").toString()); 
			BigDecimal BC=new BigDecimal(spcse.get("BC").toString()); 
			BigDecimal OC=new BigDecimal(spcse.get("OC").toString());
			admap.put("CO", CO);
			admap.put("NH3", NH3);
			admap.put("NOx", NOx);
			admap.put("SO2", SO2);
			admap.put("VOC", VOC);
			admap.put("PM25", PM25);
			admap.put("BC", BC);
			admap.put("OC", OC);
			admap.put("PM10", PM25.add(PMcoarse));		 
			admap.put("PMFINE", (PM25.subtract(BC)).subtract(OC));
			admap.put("PMC", PMcoarse);
			
			return admap;
			}
			return spcse;
		}
}
