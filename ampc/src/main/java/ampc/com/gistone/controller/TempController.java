package ampc.com.gistone.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;


/**
 * 中间数据
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月23日
 */
@RestController
@RequestMapping
public class TempController {
	ObjectMapper mapper=new ObjectMapper();
	@Autowired
	private TEmissionDetailMapper tEmissionDetailMapper;
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	@Autowired
	private TAddressMapper tAddressMapper;
	/**
	 * 查询情景的减排信息
	 * @author WangShanxi
	 * @param request 请求
	 * @param response 响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("/scenarino/get_radioInfo")
	public AmpcResult get_radioInfo(@RequestBody Map<String, Object> requestDate,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 情景id
			Long scenarinoId = Long.parseLong(data.get("scenarinoId").toString());
			// 行政区划代码
			String code = data.get("code").toString();;
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
			List<String> sjjpResult=new ArrayList<String>();
			List<String> jplResult=new ArrayList<String>();
			//判断发过来的code等级  并全部转换成3级编码
			if(addressLevle==1) code=code.substring(0,2)+"%";
			if(addressLevle==2) code=code.substring(0,4)+"%";
			//如果传过来的已经是3级则不许要转换
			if(addressLevle==3){
				//直接查询就可以了
				List<Long> codes=new ArrayList<Long>();
				codes.add(Long.parseLong(code));
				//添加code条件
				mapQuery.put("codes", codes);
				//获取所有的基准情景减排结果
				List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByMap(mapQuery);
				//循环所有基准情景的减排结果
				for (TEmissionDetail tEmissionDetail : basisList) {
					//添加每一个的减排日期
					dateResult.add(DateUtil.DateToStr(tEmissionDetail.getEmissionDate()));
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
						Object result=ede.get(stainType);
						//如果为空则继续循环
						if(result==null) continue;
						//进行转换
						BigDecimal bigResult=new BigDecimal(result.toString());
						//累计增加
						sumResult=sumResult.add(bigResult);
					}
					//添加所有行业在该污染物的减排量总和
					jplResult.add(sumResult.toString());
				}
				//查询情景的信息时为2
				mapQuery.put("emtype", 2);
				//查询情景的信息时给情景Id
				mapQuery.put("scenarinoId", scenarinoId);
				//获取所有的情景减排结果
				List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByMap(mapQuery);
				//循环所有实际减排的减排结果
				for (TEmissionDetail tEmissionDetail : tdList) {
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
						Object result=ede.get(stainType);
						//如果为空则继续循环
						if(result==null) continue;
						//进行转换
						BigDecimal bigResult=new BigDecimal(result.toString());
						//累计增加
						sumResult=sumResult.add(bigResult);
					}
					//添加所有行业在该污染物的减排量总和
					sjjpResult.add(sumResult.toString());
				}
			}else{
				//转换行政区划code
				//List<Long> codes=tAddressMapper.selectByCode(code);
				
				List<Long> codes=new ArrayList<Long>();
				codes.add(130123l);
				codes.add(131082l);
				
				//添加code条件
				mapQuery.put("codes", codes);
				//获取所有的基准情景减排结果
				List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByMap(mapQuery);
				//内部循环初始值变量
				int j=0;
				//循环所有基准情景的减排结果
				for (int i=0;i<basisList.size();i++) {
					if(i-1+codes.size()>=basisList.size()) break;
					//添加每一个的减排日期
					dateResult.add(DateUtil.DateToStr(basisList.get(i).getEmissionDate()));
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
						Object result=ede.get(stainType);
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
								Object result=ede.get(stainType);
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
					jplResult.add(sumResult.toString());
				}
				//查询情景的信息时为2
				mapQuery.put("emtype", 2);
				//查询情景的信息时给情景Id
				mapQuery.put("scenarinoId", scenarinoId);
				//获取所有的实际减排量的减排结果
				List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByMap(mapQuery);
				//内部循环初始值变量
				j=0;
				//循环所有实际减排量的减排结果
				for (int i=0;i<tdList.size();i++) {
					if(i-1+codes.size()>=tdList.size()) break;
					//临时变量用来记录污染物在所有行业的总和
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
						Object result=ede.get(stainType);
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
								Object result=ede.get(stainType);
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
					sjjpResult.add(sumResult.toString());
				}
				
			}
			//创建返回的结果集合
			Map map=new HashMap();
			//写入日期结果集合
			map.put("dateResult", dateResult);
			//写入实际减排结果集合
			map.put("sjjpResult", sjjpResult);
			//写入减排量结果集合
			map.put("jplResult", jplResult);
			//返回结果
			return AmpcResult.ok(map);
		} catch (Exception e) {
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
}
