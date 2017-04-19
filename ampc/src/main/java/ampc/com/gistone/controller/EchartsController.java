package ampc.com.gistone.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TAddressMapper;
import ampc.com.gistone.database.inter.TEmissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.entity.PieUtil;
import ampc.com.gistone.entity.RadioListUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.CastNumUtil;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;

/**
 * Echarts控制类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月23日
 */
@RestController
@RequestMapping
public class EchartsController {
	//Jackson转换类
	ObjectMapper mapper=new ObjectMapper();
	//Echarts任务详情映射
	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	//情景的映射
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	//区域的映射
	@Autowired
	private TAddressMapper tAddressMapper;
	
	/**
	 * 查询情景的减排信息  柱状图
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/echarts/get_barInfo")
	public AmpcResult get_radioInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			// 行政区划代码
			String codes = data.get("code").toString();;
			// 行政区划等级
			Long addressLevle=Long.parseLong(data.get("addressLevle").toString());
			// 污染物类型
			String stainType = data.get("stainType").toString();
			//根据情景Id获取到情景对象
			TScenarinoDetail tsd=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			//情景的开始时间
			Date startDate=tsd.getScenarinoStartDate();
			//情景的结束时间
			Date endDate=tsd.getScenarinoEndDate();
			//定义条件Map
			Map mapQuery=new HashMap();
			//添加条件
			mapQuery.put("startDate", startDate);
			mapQuery.put("endDate", endDate);
			//查询基准的信息时为1
			mapQuery.put("emtype", 1);
			//查询基准的信息时默认给空
			mapQuery.put("scenarinoId", null);
			//创建三个结果集合
			List<String> dateResult=new ArrayList<String>();
			List<String> pflResult=new ArrayList<String>();
			List<String> jplResult=new ArrayList<String>();
			List<String> sjpflResult=new ArrayList<String>();
			String[] codesSplit=codes.split(",");
			if(codesSplit.length==1){
				String code=codesSplit[0];
				//判断发过来的code等级  并全部转换成3级编码
				if(addressLevle==1) code=code.substring(0,2)+"%";
				if(addressLevle==2) code=code.substring(0,4)+"%";
				//如果传过来的已经是3级则不许要转换
				if(addressLevle==3){
					//添加code条件
					mapQuery.put("code", code);
					//获取所有的基准情景减排结果
					List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//循环所有基准情景的减排结果
					for (TEmissionDetail tEmissionDetail : basisList) {
						//临时变量用来记录污染物在所有行业的总和
						BigDecimal sumResult=new BigDecimal(0);
						//获取所有的基准情景减排结果
						String edetail=tEmissionDetail.getEmissionDetails();
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							Object result=null;
							if(stainType.equals("PMC")){
								result=ede.get("PMcoarse");
							}else if(stainType.equals("PM10")){
								if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
									result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
								}
							}else if(stainType.equals("PMFINE")){
								if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
									double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
									if(d<0){
										throw new Exception("减排结果出现负数");
									}
									result=d;
								}
							}else{
								result=ede.get(stainType);
							}
							//如果为空则继续循环
							if(result==null) continue;
							//进行转换
							BigDecimal bigResult=new BigDecimal(result.toString());
							//累计增加
							sumResult=sumResult.add(bigResult);
						}
						//添加所有行业在该污染物的减排量总和
						pflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
					}
					//查询情景的信息时为2
					mapQuery.put("emtype", 2);
					//查询情景的信息时给情景Id
					mapQuery.put("scenarinoId", scenarinoId);
					//获取所有的情景减排结果
					List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//循环所有实际减排的减排结果
					for (TEmissionDetail tEmissionDetail : tdList) {
						//添加每一个的减排日期
						dateResult.add(DateUtil.DateToStr(tEmissionDetail.getEmissionDate()));
						//临时变量用来记录污染物在所有行业的总和
						BigDecimal sumResult=new BigDecimal(0);
						//获取所有实际减排结果
						String edetail=tEmissionDetail.getEmissionDetails();
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							Object result=null;
							if(stainType.equals("PMC")){
								result=ede.get("PMcoarse");
							}else if(stainType.equals("PM10")){
								if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
									result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
								}
							}else if(stainType.equals("PMFINE")){
								if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
									double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
									if(d<0){
										throw new Exception("减排结果出现负数");
									}
									result=d;
								}
							}else{
								result=ede.get(stainType);
							}
							//如果为空则继续循环
							if(result==null) continue;
							//进行转换
							BigDecimal bigResult=new BigDecimal(result.toString());
							//累计增加
							sumResult=sumResult.add(bigResult);
						}
						//添加所有行业在该污染物的减排量总和
						jplResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
					}
				}else{
					mapQuery.put("code", code);
					//获取所有的基准情景减排结果
					List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//内部循环初始值变量
					int j=0;
					//循环所有基准情景的减排结果
					for (int i=0;i<basisList.size();i++) {
						//临时变量用来记录污染物在所有行业的总和
						BigDecimal sumResult=new BigDecimal(0);
						//获取到基准的减排信息Json串
						String edetail=basisList.get(i).getEmissionDetails();
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							Object result=null;
							if(stainType.equals("PMC")){
								result=ede.get("PMcoarse");
							}else if(stainType.equals("PM10")){
								if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
									result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
								}
							}else if(stainType.equals("PMFINE")){
								if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
									double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
									if(d<0){
										throw new Exception("减排结果出现负数");
									}
									result=d;
								}
							}else{
								result=ede.get(stainType);
							}
							//如果为空则继续循环
							if(result==null) continue;
							//进行转换
							BigDecimal bigResult=new BigDecimal(result.toString());
							//累计增加
							sumResult=sumResult.add(bigResult);
						}
						//临时变量+1
						j++;
						for(;j<basisList.size();j++){
							if(!basisList.get(i).getCode().equals(basisList.get(j).getCode())){
								//获取到基准的减排信息Json串
								String edetail1=basisList.get(j).getEmissionDetails();
								//解析json获取到所有行业的减排信息
								Map edeMap1=mapper.readValue(edetail1, Map.class);
								//循环所有行业用来得到所有行业的污染物减排总和
								for(Object obj:edeMap1.keySet()){
									//获取行业
									Map ede=(Map)edeMap1.get(obj);
									//获取行业中的减排污染物信息
									Object result=null;
									if(stainType.equals("PMC")){
										result=ede.get("PMcoarse");
									}else if(stainType.equals("PM10")){
										if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
											result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
										}
									}else if(stainType.equals("PMFINE")){
										if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
											double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
											if(d<0){
												throw new Exception("减排结果出现负数");
											}
											result=d;
										}
									}else{
										result=ede.get(stainType);
									}
									//如果为空则继续循环
									if(result==null) continue;
									//进行转换
									BigDecimal bigResult=new BigDecimal(result.toString());
									//累计增加
									sumResult=sumResult.add(bigResult);
								}
							}else{
								//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
								i=j-1;
								break;
							}
							//判断是否还有数据了
							if((j+1)==basisList.size()){
								i=j;
							}
						}
						//添加所有行业在该污染物的减排量总和
						pflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
					}
					//查询情景的信息时为2
					mapQuery.put("emtype", 2);
					//查询情景的信息时给情景Id
					mapQuery.put("scenarinoId", scenarinoId);
					//获取所有的实际减排量的减排结果
					List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//内部循环初始值变量
					j=0;
					//循环所有实际减排量的减排结果
					for (int i=0;i<tdList.size();i++) {
						//添加每一个的减排日期
						dateResult.add(DateUtil.DateToStr(tdList.get(i).getEmissionDate()));
						BigDecimal sumResult=new BigDecimal(0);
						//获取到实际减排量的的减排信息Json串
						String edetail=tdList.get(i).getEmissionDetails();
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							Object result=null;
							if(stainType.equals("PMC")){
								result=ede.get("PMcoarse");
							}else if(stainType.equals("PM10")){
								if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
									result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
								}
							}else if(stainType.equals("PMFINE")){
								if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
									double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
									if(d<0){
										throw new Exception("减排结果出现负数");
									}
									result=d;
								}
							}else{
								result=ede.get(stainType);
							}
							//如果为空则继续循环
							if(result==null) continue;
							//进行转换
							BigDecimal bigResult=new BigDecimal(result.toString());
							//累计增加
							sumResult=sumResult.add(bigResult);
						}
						//临时变量+1
						j++;
						for(;j<tdList.size();j++){
							if(!tdList.get(i).getCode().equals(tdList.get(j).getCode())){
								//获取到实际减排量的减排信息Json串
								String edetail1=tdList.get(j).getEmissionDetails();
								//解析json获取到所有行业的减排信息
								Map edeMap1=mapper.readValue(edetail1, Map.class);
								//循环所有行业用来得到所有行业的污染物减排总和
								for(Object obj:edeMap1.keySet()){
									//获取行业
									Map ede=(Map)edeMap1.get(obj);
									//获取行业中的减排污染物信息
									Object result=null;
									if(stainType.equals("PMC")){
										result=ede.get("PMcoarse");
									}else if(stainType.equals("PM10")){
										if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
											result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
										}
									}else if(stainType.equals("PMFINE")){
										if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
											double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
											if(d<0){
												throw new Exception("减排结果出现负数");
											}
											result=d;
										}
									}else{
										result=ede.get(stainType);
									}
									//如果为空则继续循环
									if(result==null) continue;
									//进行转换
									BigDecimal bigResult=new BigDecimal(result.toString());
									//累计增加
									sumResult=sumResult.add(bigResult);
								}
							}else{
								//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
								i=j-1;
								break;
							}
							//判断是否还有数据了
							if((j+1)==tdList.size()){
								i=j;
							}
						}
						//添加所有行业在该污染物的减排量总和
						jplResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
					}
				}
				//如果没有基准的排放量信息  则默认给0
				if(pflResult==null||pflResult.size()==0){
					for(int i=0;i<jplResult.size();i++){
						pflResult.add("0");
					}
				}
				//创建返回的结果集合
				Map map=new HashMap();
				for(int i=0;i<pflResult.size();i++){
					sjpflResult.add(String.valueOf(Double.parseDouble(pflResult.get(i))-Double.parseDouble(jplResult.get(i))));
				}
				//写入日期结果集合
				map.put("dateResult", dateResult);
				//写入实际减排结果集合
				map.put("sjpflResult", sjpflResult);
				//写入减排量结果集合
				map.put("jplResult", jplResult);
				//返回结果
				LogUtil.getLogger().info("EchartsController 情景减排柱状图数据查询成功!");
				return AmpcResult.ok(map);
			}else{
				for(int q=0;q<codesSplit.length;q++){
					String code=codesSplit[q];
					//创建三个结果集合
					List<String> tempdateResult=new ArrayList<String>();
					List<String> temppflResult=new ArrayList<String>();
					List<String> tempjplResult=new ArrayList<String>();
					List<String> tempsjpflResult=new ArrayList<String>();
					//判断发过来的code等级  并全部转换成3级编码
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					//如果传过来的已经是3级则不许要转换
					if(addressLevle==3){
						//添加code条件
						mapQuery.put("code", code);
						//获取所有的基准情景减排结果
						List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
						//循环所有基准情景的减排结果
						for (TEmissionDetail tEmissionDetail : basisList) {
							//临时变量用来记录污染物在所有行业的总和
							BigDecimal sumResult=new BigDecimal(0);
							//获取所有的基准情景减排结果
							String edetail=tEmissionDetail.getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								Object result=null;
								if(stainType.equals("PMC")){
									result=ede.get("PMcoarse");
								}else if(stainType.equals("PM10")){
									if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
										result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
									}
								}else if(stainType.equals("PMFINE")){
									if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
										double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
										if(d<0){
											throw new Exception("减排结果出现负数");
										}
										result=d;
									}
								}else{
									result=ede.get(stainType);
								}
								//如果为空则继续循环
								if(result==null) continue;
								//进行转换
								BigDecimal bigResult=new BigDecimal(result.toString());
								//累计增加
								sumResult=sumResult.add(bigResult);
							}
							//添加所有行业在该污染物的减排量总和
							temppflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
						}
						//查询情景的信息时为2
						mapQuery.put("emtype", 2);
						//查询情景的信息时给情景Id
						mapQuery.put("scenarinoId", scenarinoId);
						//获取所有的情景减排结果
						List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
						//循环所有实际减排的减排结果
						for (TEmissionDetail tEmissionDetail : tdList) {
							//添加每一个的减排日期
							tempdateResult.add(DateUtil.DateToStr(tEmissionDetail.getEmissionDate()));
							//临时变量用来记录污染物在所有行业的总和
							BigDecimal sumResult=new BigDecimal(0);
							//获取所有实际减排结果
							String edetail=tEmissionDetail.getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								Object result=null;
								if(stainType.equals("PMC")){
									result=ede.get("PMcoarse");
								}else if(stainType.equals("PM10")){
									if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
										result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
									}
								}else if(stainType.equals("PMFINE")){
									if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
										double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
										if(d<0){
											throw new Exception("减排结果出现负数");
										}
										result=d;
									}
								}else{
									result=ede.get(stainType);
								}
								//如果为空则继续循环
								if(result==null) continue;
								//进行转换
								BigDecimal bigResult=new BigDecimal(result.toString());
								//累计增加
								sumResult=sumResult.add(bigResult);
							}
							//添加所有行业在该污染物的减排量总和
							tempjplResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
						}
					}else{
						mapQuery.put("code", code);
						//获取所有的基准情景减排结果
						List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
						//内部循环初始值变量
						int j=0;
						//循环所有基准情景的减排结果
						for (int i=0;i<basisList.size();i++) {
							//临时变量用来记录污染物在所有行业的总和
							BigDecimal sumResult=new BigDecimal(0);
							//获取到基准的减排信息Json串
							String edetail=basisList.get(i).getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								Object result=null;
								if(stainType.equals("PMC")){
									result=ede.get("PMcoarse");
								}else if(stainType.equals("PM10")){
									if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
										result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
									}
								}else if(stainType.equals("PMFINE")){
									if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
										double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
										if(d<0){
											throw new Exception("减排结果出现负数");
										}
										result=d;
									}
								}else{
									result=ede.get(stainType);
								}
								//如果为空则继续循环
								if(result==null) continue;
								//进行转换
								BigDecimal bigResult=new BigDecimal(result.toString());
								//累计增加
								sumResult=sumResult.add(bigResult);
							}
							//临时变量+1
							j++;
							for(;j<basisList.size();j++){
								if(!basisList.get(i).getCode().equals(basisList.get(j).getCode())){
									//获取到基准的减排信息Json串
									String edetail1=basisList.get(j).getEmissionDetails();
									//解析json获取到所有行业的减排信息
									Map edeMap1=mapper.readValue(edetail1, Map.class);
									//循环所有行业用来得到所有行业的污染物减排总和
									for(Object obj:edeMap1.keySet()){
										//获取行业
										Map ede=(Map)edeMap1.get(obj);
										//获取行业中的减排污染物信息
										Object result=null;
										if(stainType.equals("PMC")){
											result=ede.get("PMcoarse");
										}else if(stainType.equals("PM10")){
											if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
												result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
											}
										}else if(stainType.equals("PMFINE")){
											if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
												double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
												if(d<0){
													throw new Exception("减排结果出现负数");
												}
												result=d;
											}
										}else{
											result=ede.get(stainType);
										}
										//如果为空则继续循环
										if(result==null) continue;
										//进行转换
										BigDecimal bigResult=new BigDecimal(result.toString());
										//累计增加
										sumResult=sumResult.add(bigResult);
									}
								}else{
									//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
									i=j-1;
									break;
								}
								//判断是否还有数据了
								if((j+1)==basisList.size()){
									i=j;
								}
							}
							//添加所有行业在该污染物的减排量总和
							temppflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
						}
						//查询情景的信息时为2
						mapQuery.put("emtype", 2);
						//查询情景的信息时给情景Id
						mapQuery.put("scenarinoId", scenarinoId);
						//获取所有的实际减排量的减排结果
						List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
						//内部循环初始值变量
						j=0;
						//循环所有实际减排量的减排结果
						for (int i=0;i<tdList.size();i++) {
							//添加每一个的减排日期
							tempdateResult.add(DateUtil.DateToStr(tdList.get(i).getEmissionDate()));
							BigDecimal sumResult=new BigDecimal(0);
							//获取到实际减排量的的减排信息Json串
							String edetail=tdList.get(i).getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								Object result=null;
								if(stainType.equals("PMC")){
									result=ede.get("PMcoarse");
								}else if(stainType.equals("PM10")){
									if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
										result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
									}
								}else if(stainType.equals("PMFINE")){
									if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
										double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
										if(d<0){
											throw new Exception("减排结果出现负数");
										}
										result=d;
									}
								}else{
									result=ede.get(stainType);
								}
								//如果为空则继续循环
								if(result==null) continue;
								//进行转换
								BigDecimal bigResult=new BigDecimal(result.toString());
								//累计增加
								sumResult=sumResult.add(bigResult);
							}
							//临时变量+1
							j++;
							for(;j<tdList.size();j++){
								if(!tdList.get(i).getCode().equals(tdList.get(j).getCode())){
									//获取到实际减排量的减排信息Json串
									String edetail1=tdList.get(j).getEmissionDetails();
									//解析json获取到所有行业的减排信息
									Map edeMap1=mapper.readValue(edetail1, Map.class);
									//循环所有行业用来得到所有行业的污染物减排总和
									for(Object obj:edeMap1.keySet()){
										//获取行业
										Map ede=(Map)edeMap1.get(obj);
										//获取行业中的减排污染物信息
										Object result=null;
										if(stainType.equals("PMC")){
											result=ede.get("PMcoarse");
										}else if(stainType.equals("PM10")){
											if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
												result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
											}
										}else if(stainType.equals("PMFINE")){
											if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
												double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
												if(d<0){
													throw new Exception("减排结果出现负数");
												}
												result=d;
											}
										}else{
											result=ede.get(stainType);
										}
										//如果为空则继续循环
										if(result==null) continue;
										//进行转换
										BigDecimal bigResult=new BigDecimal(result.toString());
										//累计增加
										sumResult=sumResult.add(bigResult);
									}
								}else{
									//如果条件不满足更改第一层循环的值  因为到上面还要++所以-1
									i=j-1;
									break;
								}
								//判断是否还有数据了
								if((j+1)==tdList.size()){
									i=j;
								}
							}
							//添加所有行业在该污染物的减排量总和
							tempjplResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(sumResult.doubleValue(), 4), 2)));
						}
					}
					//如果没有基准的排放量信息  则默认给0
					if(temppflResult==null||temppflResult.size()==0){
						for(int i=0;i<tempjplResult.size();i++){
							temppflResult.add("0");
						}
					}
					//创建返回的结果集合
					Map map=new HashMap();
					for(int i=0;i<temppflResult.size();i++){
						tempsjpflResult.add(String.valueOf(Double.parseDouble(temppflResult.get(i))-Double.parseDouble(tempjplResult.get(i))));
						if(q==0){
							dateResult.add("1");
							dateResult.set(i,tempdateResult.get(i));
						}
						sjpflResult.add("1");
						jplResult.add("2");
						sjpflResult.set(i,String.valueOf(Double.parseDouble(sjpflResult.get(i))+Double.parseDouble(tempsjpflResult.get(i))));
						jplResult.set(i, String.valueOf(Double.parseDouble(jplResult.get(i))+Double.parseDouble(tempjplResult.get(i))));
					}
				}
				//创建返回的结果集合
				Map map=new HashMap();
				//写入日期结果集合
				map.put("dateResult", dateResult);
				//写入实际减排结果集合
				map.put("sjpflResult", sjpflResult);
				//写入减排量结果集合
				map.put("jplResult", jplResult);
				//返回结果
				LogUtil.getLogger().info("EchartsController 情景减排柱状图数据查询成功!");
				return AmpcResult.ok(map);
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排柱状图数据查询异常!",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	/**
	 * 查询情景的减排信息  饼状图
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/echarts/get_pieInfo")
	public AmpcResult get_pieInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			// 行政区划代码
			String codes = data.get("code").toString();;
			// 行政区划等级
			Long addressLevle=Long.parseLong(data.get("addressLevle").toString());
			// 污染物类型
			String stainType = data.get("stainType").toString();
			//查询类型
			Integer type=Integer.valueOf(data.get("type").toString());
			//情景的开始时间
			Date startDate=DateUtil.StrToDate1(data.get("startDate").toString());
			//情景的结束时间
			Date endDate=DateUtil.StrToDate1(data.get("endDate").toString());
			//定义条件Map
			Map mapQuery=new HashMap();
			//添加条件
			mapQuery.put("startDate", startDate);
			mapQuery.put("endDate", endDate);
			//查询情景的信息时为2
			mapQuery.put("emtype", 2);
			//查询情景的信息时给情景Id
			mapQuery.put("scenarinoId", scenarinoId);
			//创建结果集合
			List<PieUtil> puList=new ArrayList<PieUtil>();
			String[] codesSplit=codes.split(",");
			if(codesSplit.length==1){
				String code=codesSplit[0];
				//判断发过来的code等级  并全部转换成3级编码
				if(addressLevle==1) code=code.substring(0,2)+"%";
				if(addressLevle==2) code=code.substring(0,4)+"%";
				//添加code条件
				mapQuery.put("code", null);
				//获取所有的减排结果
				List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
				//循环所有减排结果
				for (int i=0;i<tdList.size();i++) {
					//判断是行业还是措施的  并赋值对应的Json串
					String edetail=null;
					if(type==1){
						//获取所有的基准情景减排结果
						edetail=tdList.get(i).getEmissionDetails();
					}else{
						edetail=tdList.get(i).getMeasureReduce();
					}
					//解析json获取到所有行业的减排信息
					Map edeMap=mapper.readValue(edetail, Map.class);
					//循环所有行业用来得到所有行业的污染物减排总和
					sector:for(Object obj:edeMap.keySet()){
						//获取行业
						Map ede=(Map)edeMap.get(obj);
						//获取行业中的减排污染物信息
						Object result=null;
						if(stainType.equals("PMC")){
							result=ede.get("PMcoarse");
						}else if(stainType.equals("PM10")){
							if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
								result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
							}
						}else if(stainType.equals("PMFINE")){
							if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
								double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
								if(d<0){
									throw new Exception("减排结果出现负数");
								}
								result=d;
							}
						}else{
							result=ede.get(stainType);
						}
						//如果为空则继续循环
						if(result==null) continue;
						//循环结果集合
						for(int j=0;j<puList.size();j++){
							PieUtil pu=puList.get(j);
							//判断结果集合中是否有该项记录 有则在基础上累计增加值
							if(pu.getName().equals(obj.toString())){
								double temp=Double.parseDouble(result.toString());
								double value=pu.getValue();
								pu.setValue(temp+value);
								//继续循环外层循环
								continue sector;
							}
						}
						//上面没有该条行业记录  新建行业记录 添加到结果集中
						PieUtil newPu=new PieUtil();
						newPu.setName(obj.toString());
						newPu.setValue(Double.parseDouble(result.toString()));
						puList.add(newPu);
					}
				}
				//讲数据保留4位有效数字
				for(int i=0;i<puList.size();i++){
					double value=puList.get(i).getValue();
					value=CastNumUtil.significand(value, 4);
					value=CastNumUtil.decimal(value, 2);
					puList.get(i).setValue(value);
				}
				//返回结果
				LogUtil.getLogger().info("EchartsController 情景减排饼状图数据查询成功!");
				return AmpcResult.ok(puList);
			}else{
				for(int q=0;q<codesSplit.length;q++){
					String code=codesSplit[q];
					//判断发过来的code等级  并全部转换成3级编码
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					//添加code条件
					mapQuery.put("code", code);
					//获取所有的减排结果
					List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//循环所有减排结果
					for (int i=0;i<tdList.size();i++) {
						//判断是行业还是措施的  并赋值对应的Json串
						String edetail=null;
						if(type==1){
							//获取所有的基准情景减排结果
							edetail=tdList.get(i).getEmissionDetails();
						}else{
							edetail=tdList.get(i).getMeasureReduce();
						}
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						sector:for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							Object result=null;
							if(stainType.equals("PMC")){
								result=ede.get("PMcoarse");
							}else if(stainType.equals("PM10")){
								if(ede.get("PMcoarse") != null&& ede.get("PM25") != null){
									result=Double.parseDouble(ede.get("PMcoarse").toString())+Double.parseDouble(ede.get("PM25").toString());
								}
							}else if(stainType.equals("PMFINE")){
								if(ede.get("PM25") != null&&ede.get("BC") != null&&ede.get("OC") != null){
									double d=Double.parseDouble(ede.get("PM25").toString())-Double.parseDouble(ede.get("BC").toString())-Double.parseDouble(ede.get("OC").toString());
									if(d<0){
										throw new Exception("减排结果出现负数");
									}
									result=d;
								}
							}else{
								result=ede.get(stainType);
							}
							//如果为空则继续循环
							if(result==null) continue;
							//循环结果集合
							for(int j=0;j<puList.size();j++){
								PieUtil pu=puList.get(j);
								//判断结果集合中是否有该项记录 有则在基础上累计增加值
								if(pu.getName().equals(obj.toString())){
									double temp=Double.parseDouble(result.toString());
									double value=pu.getValue();
									pu.setValue(temp+value);
									//继续循环外层循环
									continue sector;
								}
							}
							//上面没有该条行业记录  新建行业记录 添加到结果集中
							PieUtil newPu=new PieUtil();
							newPu.setName(obj.toString());
							newPu.setValue(Double.parseDouble(result.toString()));
							puList.add(newPu);
						}
					}
				}
				//讲数据保留4位有效数字
				for(int i=0;i<puList.size();i++){
					double value=puList.get(i).getValue();
					value=CastNumUtil.significand(value, 4);
					value=CastNumUtil.decimal(value, 2);
					puList.get(i).setValue(value);
				}
				//返回结果
				LogUtil.getLogger().info("EchartsController 情景减排饼状图数据查询成功!");
				return AmpcResult.ok(puList);
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排饼状图数据查询异常!",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	/**
	 * 查询情景的减排列表
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/echarts/get_radioList")
	public AmpcResult get_radioList(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println(DateUtil.DATEtoString(new Date(), "yyyy-MM-dd HH-mm:ss"));
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			//根据情景Id获取到情景对象
			TScenarinoDetail tsd=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			//情景的开始时间
			Date startDate=tsd.getScenarinoStartDate();
			//情景的结束时间
			Date endDate=tsd.getScenarinoEndDate();
			// 行政区划代码
			String codes = data.get("code").toString();;
			// 行政区划等级
			Long addressLevle=Long.parseLong(data.get("addressLevle").toString());
			//定义条件Map
			Map mapQuery=new HashMap();
			//添加条件
			mapQuery.put("startDate", startDate);
			mapQuery.put("endDate", endDate);
			//查询情景的基准类型为1
			mapQuery.put("emtype", 1);
			//定义返回结果集合
			List<RadioListUtil> resultList=new ArrayList<RadioListUtil>();
			String[] codesSplit=codes.split(",");
			if(codesSplit.length==1){
				String code=codesSplit[0];
				//判断发过来的code等级
				if(addressLevle==1) code=code.substring(0,2)+"%";
				if(addressLevle==2) code=code.substring(0,4)+"%";
				mapQuery.put("code", null);
				//获取到对应的减排信息
				List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
				//定义结果对象
				RadioListUtil basisrlu=null;
				for(TEmissionDetail ted:basisList){
					//判断是行业还是措施的  并赋值对应的Json串
					String edetail=ted.getEmissionDetails();
					//解析json获取到所有行业的减排信息
					Map edeMap=mapper.readValue(edetail, Map.class);
					//循环所有行业用来得到所有行业的污染物减排总和
					for(Object obj:edeMap.keySet()){
						//获取行业
						Map ede=(Map)edeMap.get(obj);
						//获取行业中的减排污染物信息
						tempUtil(ede,basisrlu);
					}
				}
				//如果基准为空则赋值为0
				if(basisrlu==null){
					basisrlu=new RadioListUtil();
					basisrlu.setPM25(0);
					basisrlu.setPM10(0);
					basisrlu.setSO2(0);
					basisrlu.setNOx(0);
					basisrlu.setVOC(0);
					basisrlu.setCO(0);
					basisrlu.setNH3(0);
					basisrlu.setBC(0);
					basisrlu.setOC(0);
					basisrlu.setPMFINE(0);
					basisrlu.setPMC(0);
				}
				//查询情景的减排信息类型为2
				mapQuery.put("emtype", 2);
				//查询减排信息类型的情景Id
				mapQuery.put("scenarinoId", scenarinoId);
				if(addressLevle==1){
					//List<String> cityCodes=tEmissionDetailMapper.selectCityCode(mapQuery);
					List<String> cityCodes=tAddressMapper.selectCityCode(code);
					//循环所有的省级编码集合
					for(String strcod:cityCodes){
						String str=strcod;
						str=str.substring(0,4);
						//添加条件 
						mapQuery.put("code", str+"%");
						//模糊查询当前这个省级下的所有减排记录
						List<TEmissionDetail> ttdd=tEmissionDetailMapper.selectByQuery(mapQuery);
						//如果没有数据代表没有该市的信息  直接判断下一条记录
						if(ttdd.size()==0) continue;
						//定义结果对象
						RadioListUtil rlu=new RadioListUtil();
						//查询区域名称
						String addressName=tAddressMapper.selectNameByCode(strcod);
						//添加区域名称
						rlu.setName(addressName);
						//添加区域Code
						rlu.setCode(strcod);
						//如果只有一条记录 
						if(ttdd.size()==1){
							//定义临时减排对象
							TEmissionDetail td=ttdd.get(0);
							//判断这条记录是否是市级  如果是咋返回结果类型为没有县级
							if(td.getCode().equals(strcod)){
								//添加类型没有县级
								rlu.setType(0);
							}else{
								//添加类型县市级
								rlu.setType(1);
							}
							//判断是行业还是措施的  并赋值对应的Json串
							String edetail=td.getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								tempUtil(ede,rlu);
							}
							resultUtil(basisrlu,rlu);
							resultList.add(rlu);
						}else{
							//添加类型有县级
							rlu.setType(1);
							//结果不只一条
							for(TEmissionDetail ted:ttdd){
								//判断是行业还是措施的  并赋值对应的Json串
								String edetail=ted.getEmissionDetails();
								//解析json获取到所有行业的减排信息
								Map edeMap=mapper.readValue(edetail, Map.class);
								//循环所有行业用来得到所有行业的污染物减排总和
								for(Object obj:edeMap.keySet()){
									//获取行业
									Map ede=(Map)edeMap.get(obj);
									//获取行业中的减排污染物信息
									tempUtil(ede,rlu);
								}
							}
							resultUtil(basisrlu,rlu);
							resultList.add(rlu);
						}
					} // end 循环省级code
				}else{
					List<String> cityCodes=tEmissionDetailMapper.selectCityCode(mapQuery);
					//循环所有的省级编码集合
					for(String strcod:cityCodes){
						//添加条件 
						mapQuery.put("code", strcod);
						//模糊查询当前这个省级下的所有减排记录
						List<TEmissionDetail> ttdd=tEmissionDetailMapper.selectByQuery(mapQuery);
						//如果没有数据代表没有该市的信息  直接判断下一条记录
						if(ttdd.size()==0) continue;
						//定义结果对象
						RadioListUtil rlu=new RadioListUtil();
						//查询区域名称
						String addressName=tAddressMapper.selectNameByCode(strcod);
						//添加区域名称
						rlu.setName(addressName);
						//添加区域Code
						rlu.setCode(strcod);
						//添加类型没有县级
						rlu.setType(0);
						//如果没有数据代表没有该市的信息  直接判断下一条记录
						for(TEmissionDetail td:ttdd){
							//判断是行业还是措施的  并赋值对应的Json串
							String edetail=td.getEmissionDetails();
							//解析json获取到所有行业的减排信息
							Map edeMap=mapper.readValue(edetail, Map.class);
							//循环所有行业用来得到所有行业的污染物减排总和
							for(Object obj:edeMap.keySet()){
								//获取行业
								Map ede=(Map)edeMap.get(obj);
								//获取行业中的减排污染物信息
								tempUtil(ede,rlu);
							}
						}
						resultUtil(basisrlu,rlu);
						resultList.add(rlu);
					}
					
				}
				LogUtil.getLogger().info("EchartsController 情景减排列表数据查询成功!");
				System.out.println(DateUtil.DATEtoString(new Date(), "yyyy-MM-dd HH-mm:ss"));
				return AmpcResult.ok(resultList);
			}else{
				for(int q=0;q<codesSplit.length;q++){
					String code=codesSplit[q];
					//判断发过来的code等级
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					mapQuery.put("code", null);
					//获取到对应的减排信息
					List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByQuery(mapQuery);
					//定义结果对象
					RadioListUtil basisrlu=null;
					for(TEmissionDetail ted:basisList){
						//判断是行业还是措施的  并赋值对应的Json串
						String edetail=ted.getEmissionDetails();
						//解析json获取到所有行业的减排信息
						Map edeMap=mapper.readValue(edetail, Map.class);
						//循环所有行业用来得到所有行业的污染物减排总和
						for(Object obj:edeMap.keySet()){
							//获取行业
							Map ede=(Map)edeMap.get(obj);
							//获取行业中的减排污染物信息
							tempUtil(ede,basisrlu);
						}
					}
					//如果基准为空则赋值为0
					if(basisrlu==null){
						basisrlu=new RadioListUtil();
						basisrlu.setPM25(0);
						basisrlu.setPM10(0);
						basisrlu.setSO2(0);
						basisrlu.setNOx(0);
						basisrlu.setVOC(0);
						basisrlu.setCO(0);
						basisrlu.setNH3(0);
						basisrlu.setBC(0);
						basisrlu.setOC(0);
						basisrlu.setPMFINE(0);
						basisrlu.setPMC(0);
					}
					//查询情景的减排信息类型为2
					mapQuery.put("emtype", 2);
					//查询减排信息类型的情景Id
					mapQuery.put("scenarinoId", scenarinoId);
					if(addressLevle==1){
						//List<String> cityCodes=tEmissionDetailMapper.selectCityCode(mapQuery);
						List<String> cityCodes=tAddressMapper.selectCityCode(code);
						//循环所有的省级编码集合
						for(String strcod:cityCodes){
							String str=strcod;
							str=str.substring(0,4);
							//添加条件 
							mapQuery.put("code", str+"%");
							//模糊查询当前这个省级下的所有减排记录
							List<TEmissionDetail> ttdd=tEmissionDetailMapper.selectByQuery(mapQuery);
							//如果没有数据代表没有该市的信息  直接判断下一条记录
							if(ttdd.size()==0) continue;
							//定义结果对象
							RadioListUtil rlu=new RadioListUtil();
							//查询区域名称
							String addressName=tAddressMapper.selectNameByCode(strcod);
							//添加区域名称
							rlu.setName(addressName);
							//添加区域Code
							rlu.setCode(strcod);
							//如果只有一条记录 
							if(ttdd.size()==1){
								//定义临时减排对象
								TEmissionDetail td=ttdd.get(0);
								//判断这条记录是否是市级  如果是咋返回结果类型为没有县级
								if(td.getCode().equals(strcod)){
									//添加类型没有县级
									rlu.setType(0);
								}else{
									//添加类型县市级
									rlu.setType(1);
								}
								//判断是行业还是措施的  并赋值对应的Json串
								String edetail=td.getEmissionDetails();
								//解析json获取到所有行业的减排信息
								Map edeMap=mapper.readValue(edetail, Map.class);
								//循环所有行业用来得到所有行业的污染物减排总和
								for(Object obj:edeMap.keySet()){
									//获取行业
									Map ede=(Map)edeMap.get(obj);
									//获取行业中的减排污染物信息
									tempUtil(ede,rlu);
								}
								resultUtil(basisrlu,rlu);
								resultList.add(rlu);
							}else{
								//添加类型有县级
								rlu.setType(1);
								//结果不只一条
								for(TEmissionDetail ted:ttdd){
									//判断是行业还是措施的  并赋值对应的Json串
									String edetail=ted.getEmissionDetails();
									//解析json获取到所有行业的减排信息
									Map edeMap=mapper.readValue(edetail, Map.class);
									//循环所有行业用来得到所有行业的污染物减排总和
									for(Object obj:edeMap.keySet()){
										//获取行业
										Map ede=(Map)edeMap.get(obj);
										//获取行业中的减排污染物信息
										tempUtil(ede,rlu);
									}
								}
								resultUtil(basisrlu,rlu);
								resultList.add(rlu);
							}
						} // end 循环省级code
					}else{
						List<String> cityCodes=tEmissionDetailMapper.selectCityCode(mapQuery);
						//循环所有的省级编码集合
						for(String strcod:cityCodes){
							//添加条件 
							mapQuery.put("code", strcod);
							//模糊查询当前这个省级下的所有减排记录
							List<TEmissionDetail> ttdd=tEmissionDetailMapper.selectByQuery(mapQuery);
							//如果没有数据代表没有该市的信息  直接判断下一条记录
							if(ttdd.size()==0) continue;
							//定义结果对象
							RadioListUtil rlu=new RadioListUtil();
							//查询区域名称
							String addressName=tAddressMapper.selectNameByCode(strcod);
							//添加区域名称
							rlu.setName(addressName);
							//添加区域Code
							rlu.setCode(strcod);
							//添加类型没有县级
							rlu.setType(0);
							//如果没有数据代表没有该市的信息  直接判断下一条记录
							for(TEmissionDetail td:ttdd){
								//判断是行业还是措施的  并赋值对应的Json串
								String edetail=td.getEmissionDetails();
								//解析json获取到所有行业的减排信息
								Map edeMap=mapper.readValue(edetail, Map.class);
								//循环所有行业用来得到所有行业的污染物减排总和
								for(Object obj:edeMap.keySet()){
									//获取行业
									Map ede=(Map)edeMap.get(obj);
									//获取行业中的减排污染物信息
									tempUtil(ede,rlu);
								}
							}
							resultUtil(basisrlu,rlu);
							resultList.add(rlu);
						}
						
					}
				}
				LogUtil.getLogger().info("EchartsController 情景减排列表数据查询成功!");
				return AmpcResult.ok(resultList);
			}
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排列表数据查询异常!",e);
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
	/**
	 * 减排分析列表结果格式化
	 * @param basisrlu
	 * @param rlu
	 * @throws Exception
	 */
	public void resultUtil(RadioListUtil basisrlu,RadioListUtil rlu) throws Exception{
		//如果基准的数字为0则设置-9999  负数抛异常 正数计算格式化
		if(basisrlu.getSO2()>0){
			double temp=CastNumUtil.significand(rlu.getSO2()/basisrlu.getSO2() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setSO2(temp);
		}else if(basisrlu.getSO2()==0){
			rlu.setSO2(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getNOx()>0){
			double temp=CastNumUtil.significand(rlu.getNOx()/basisrlu.getNOx() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setNOx(temp);
		}else if(basisrlu.getNOx()==0){
			rlu.setNOx(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getPM25()>0){
			double temp=CastNumUtil.significand(rlu.getPM25()/basisrlu.getPM25() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setPM25(temp);
		}else if(basisrlu.getPM25()==0){
			rlu.setPM25(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getVOC()>0){
			double temp=CastNumUtil.significand(rlu.getVOC()/basisrlu.getVOC() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setVOC(temp);
		}else if(basisrlu.getVOC()==0){
			rlu.setVOC(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getNH3()>0){
			double temp=CastNumUtil.significand(rlu.getNH3()/basisrlu.getNH3() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setNH3(temp);
		}else if(basisrlu.getNH3()==0){
			rlu.setNH3(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getPMC()>0){
			double temp=CastNumUtil.significand(rlu.getPMC()/basisrlu.getPMC() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setPMC(temp);
		}else if(basisrlu.getPMC()==0){
			rlu.setPMC(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getBC()>0){
			double temp=CastNumUtil.significand(rlu.getBC()/basisrlu.getBC() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setBC(temp);
		}else if(basisrlu.getBC()==0){
			rlu.setBC(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
	
		if(basisrlu.getOC()>0){
			double temp=CastNumUtil.significand(rlu.getOC()/basisrlu.getOC() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setOC(temp);
		}else if(basisrlu.getOC()==0){
			rlu.setOC(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getCO()>0){
			double temp=CastNumUtil.significand(rlu.getCO()/basisrlu.getCO() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setCO(temp);
		}else if(basisrlu.getCO()==0){
			rlu.setCO(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getPM10()>0){
			double temp=CastNumUtil.significand(rlu.getPM10()/basisrlu.getPM10() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setPM10(temp);
		}else if(basisrlu.getPM10()==0){
			rlu.setPM10(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
		if(basisrlu.getPMFINE()>0){
			double temp=CastNumUtil.significand(rlu.getPMFINE()/basisrlu.getPMFINE() , 3)*100;
			temp=CastNumUtil.decimal(temp, 1);
			rlu.setPMFINE(temp);
		}else if(basisrlu.getPMFINE()==0){
			rlu.setPMFINE(-9999);
		}else{
			throw new Exception("排放量为负数");
		}
		
	}
	
	/**
	 * 减排列表中间转换计算
	 * @param ede
	 * @param rlu
	 * @throws Exception
	 */
	public void tempUtil(Map ede,RadioListUtil rlu) throws Exception{
		//判断结果对象是非为空 如果为空则新建
		if(rlu==null){
			rlu=new RadioListUtil();
		}
		double so2=Double.parseDouble(ede.get("SO2").toString());
		//判断这个污染物是否出现在了集合中 如果存在则累加 否则只添加
		if(rlu.getSO2()>0){
			rlu.setSO2(rlu.getSO2()+so2);
		}else{
			rlu.setSO2(so2);
		}
		double nox=Double.parseDouble(ede.get("NOx").toString());
		if(rlu.getNOx()>0){
			rlu.setNOx(rlu.getNOx()+nox);
		}else{
			rlu.setNOx(nox);
		}
		double pm25=Double.parseDouble(ede.get("PM25").toString());
		if(rlu.getPM25()>0){
			rlu.setPM25(rlu.getPM25()+pm25);
		}else{
			rlu.setPM25(pm25);
		}
		double voc=Double.parseDouble(ede.get("VOC").toString());
		if(rlu.getVOC()>0){
			rlu.setVOC(rlu.getVOC()+voc);
		}else{
			rlu.setVOC(voc);
		}
		double nh3=Double.parseDouble(ede.get("NH3").toString());
		if(rlu.getNH3()>0){
			rlu.setNH3(rlu.getNH3()+nh3);
		}else{
			rlu.setNH3(nh3);
		}
		double pmc=Double.parseDouble(ede.get("PMcoarse").toString());
		if(rlu.getPMC()>0){
			rlu.setPMC(rlu.getPMC()+pmc);
		}else{
			rlu.setPMC(pmc);
		}
		double bc=Double.parseDouble(ede.get("BC").toString());
		if(rlu.getBC()>0){
			rlu.setBC(rlu.getBC()+bc);
		}else{
			rlu.setBC(bc);
		}
		double oc=Double.parseDouble(ede.get("OC").toString());
		if(rlu.getOC()>0){
			rlu.setOC(rlu.getOC()+oc);
		}else{
			rlu.setOC(oc);
		}
		double co=Double.parseDouble(ede.get("CO").toString());
		if(rlu.getCO()>0){
			rlu.setCO(rlu.getCO()+co);
		}else{
			rlu.setCO(co);
		}
		if(rlu.getPM10()>0){
			rlu.setPM10(rlu.getPM10()+pm25+pmc);
		}else{
			rlu.setPM10(pm25+pmc);
		}
		if((pm25-bc-oc)<0){
			throw new Exception("出现负值");
		}
		if(rlu.getPMFINE()>0){
			rlu.setPMFINE(rlu.getPM10()+pm25-bc-oc);
		}else{
			rlu.setPMFINE(pm25-bc-oc);
		}
	}
}
