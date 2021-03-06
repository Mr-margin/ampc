package ampc.com.gistone.controller;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TAddressMapper;
import ampc.com.gistone.database.inter.TEmissionDetailMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.entity.PieUtil;
import ampc.com.gistone.entity.RadioListUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.CastNumUtil;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

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
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
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
			//获取情景id
			Object param=data.get("scenarinoId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 情景id为空或出现非法字符!");
				return AmpcResult.build(1003, "情景id为空或出现非法字符!");
			}
			//情景id
			Long scenarinoId = Long.parseLong(param.toString());
		
			//获取行政区划代码
			param=data.get("code");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划代码为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划代码为空或出现非法字符!");
			}
			// 行政区划代码
			String codes = param.toString();
			
			// 行政区划等级
			param=data.get("addressLevle");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划等级为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划等级为空或出现非法字符!");
			}
			// 行政区划等级
			Long addressLevle=Long.parseLong(param.toString());
			
			
			//污染物类型
			param=data.get("stainType");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 污染物类型为空或出现非法字符!");
				return AmpcResult.build(1003, "污染物类型为空或出现非法字符!");
			}
			// 污染物类型
			String stainType = param.toString();
			//根据情景Id获取到情景对象
			TScenarinoDetail tsd=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			if(tsd==null){
				throw new SQLException("EchartsController  查询情景的减排信息柱状图失败，数据库没有该条情景。");
			}
			//情景的开始时间
			Date sDate=tsd.getScenarinoStartDate();
			//情景的结束时间
			Date eDate=tsd.getScenarinoEndDate();
			//进行日期的转换
			String startDate=DateUtil.DATEtoString(sDate, "yyyy-MM-dd");
			//进行日期的转换
			String endDate=DateUtil.DATEtoString(eDate, "yyyy-MM-dd");
			//创建日期结果集合
			List<String> dateResult=new ArrayList<String>();
			//创建基准结果集合
			List<String> pflResult=new ArrayList<String>();
			//创建减排量结果集合
			List<String> jplResult=new ArrayList<String>();
			//创建实际减排量结果集合
			List<String> sjpflResult=new ArrayList<String>();
			//对传入的code进行拆分 
			String[] codesSplit=codes.split(",");
			//查询基准结果SQL
			String basisSql="";
			//查询减排量SQL
			String jplSql="";
			//判断是查询多个行政区划，还是单个行政区划
			if(codesSplit.length==1){
				//如果查询单个获取对应Code
				String code=codesSplit[0];
				//根据Code级别进行条件调整
				if(addressLevle==1) code=code.substring(0,2)+"%";
				if(addressLevle==2) code=code.substring(0,4)+"%";
				//查询基准结果SQL
				basisSql="SELECT EMISSION_DATE,SUM("+stainType+") STAINTYPESUM"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=1"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND CODE LIKE '"+code+"'"
						+ " GROUP BY EMISSION_DATE"
						+ " ORDER BY EMISSION_DATE";
				
				//查询减排量SQL
				jplSql="SELECT EMISSION_DATE,SUM("+stainType+") STAINTYPESUM"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND CODE LIKE '"+code+"'"
						+ " AND SCENARINO_ID="+scenarinoId+""
						+ " GROUP BY EMISSION_DATE"
						+ " ORDER BY EMISSION_DATE";
			}else{ //如果是多个行政区划查询
				//基准排放量StringBuffer
				StringBuffer basisSb=new StringBuffer("SELECT EMISSION_DATE,SUM("+stainType+") STAINTYPESUM"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=1"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND (");
				//循环所有的行政区划 进行sql拼接
				for(int i=0;i<codesSplit.length;i++){
					String code=codesSplit[i];
					//根据Code级别进行条件调整
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					if(i==0){
						basisSb.append(" CODE LIKE '"+code+"'");
					}
					basisSb.append(" OR CODE LIKE '"+code+"'");
				}
				basisSb.append(") GROUP BY EMISSION_DATE ORDER BY EMISSION_DATE");
				//查询基准结果SQL
				basisSql=basisSb.toString();
				//减排量StringBuffer
				StringBuffer jplSb=new StringBuffer("SELECT EMISSION_DATE,SUM("+stainType+") STAINTYPESUM"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND (");
				//循环所有的行政区划 进行sql拼接
				for(int i=0;i<codesSplit.length;i++){
					String code=codesSplit[i];
					//根据Code级别进行条件调整
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					if(i==0){
						jplSb.append(" CODE LIKE '"+code+"'");
					}
					jplSb.append(" OR CODE LIKE '"+code+"'");
				}
				jplSb.append(") AND SCENARINO_ID="+scenarinoId+"");
				jplSb.append(" GROUP BY EMISSION_DATE ORDER BY EMISSION_DATE");
				//查询减排量SQL
				jplSql=jplSb.toString();
			}
			//执行SQL返回基准的结果集
			List<Map> selectBisisMap = this.getBySqlMapper.findRecords(basisSql);
			//执行SQL返回减排量的结果集
			List<Map> selectjplMap = this.getBySqlMapper.findRecords(jplSql);
			//定义中间时间变量
			Date tempDate;
			//定义日历类用来时间增加
			Calendar cal = Calendar.getInstance();
			//将情景的开始时间设置为默认时间
			cal.setTime(sDate);
			//给中间时间变量复制
			tempDate = cal.getTime();
			//开始循环  如果中间变量的日期大于了情景的结束时间就跳出循环
			while(!(tempDate.getTime()>eDate.getTime())){
				//定义临时变量用来接收数值
				BigDecimal result=null;
				//定义临时变量用来确定是否结果中包含这个时间
				boolean isFind=false;
				//写入日期结果信息
				dateResult.add(DateUtil.DATEtoString(tempDate, "yyyy-MM-dd"));
				//循环当前区域下和基准情景时间的排放量总和结果
				for (Map tempMap : selectBisisMap) {
					//判断如果当前结果就是中间变量的时间则代表找到了结果
					if(DateUtil.StrToDate1(tempMap.get("EMISSION_DATE").toString()).getTime()==tempDate.getTime()){
						//获取当前污染物在所有行业当天当前CODE的排放量总和
						result=(BigDecimal)tempMap.get("STAINTYPESUM");
						//如果为空赋值0
						if(result==null){
							result=new BigDecimal(0);
						} 
						//代表找到了 跳出
						isFind=true;
						break;
					}
				}
				//判断是否找到 
				if(!isFind){
					//没有找到个结果集中赋值0
					pflResult.add("0");
				}else{
					//添加所有行业在该污染物的排放量总和
					pflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(result.doubleValue(), 4), 2)));
				}
				//初始化中间变量
				result=null;
				//初始化中间变量
				isFind=false;
				//循环减排量结果集
				for (Map tempMap : selectjplMap) {
					//判断如果当前结果就是中间变量的时间则代表找到了结果
					if(DateUtil.StrToDate1(tempMap.get("EMISSION_DATE").toString()).getTime()==tempDate.getTime()){
						//获取当前污染物在所有行业当天当前CODE的减排量总和
						result=(BigDecimal)tempMap.get("STAINTYPESUM");
						//如果为空赋值0
						if(result==null){
							result=new BigDecimal(0);
						} 
						//代表找到了 跳出
						isFind=true;
						break;
					}
				}
				//判断是否找到 
				if(!isFind){
					//没有找到个结果集中复制0
					jplResult.add("0");
				}else{
					//添加所有行业在该污染物的排放量总和
					jplResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand(result.doubleValue(), 4), 2)));
				}
				//日期增加一天
				cal.add(Calendar.DATE, 1);
				//更改中间时间的值
				tempDate = cal.getTime();
			}
			//创建返回的结果集合
			Map map=new HashMap();
			for(int i=0;i<pflResult.size();i++){
				sjpflResult.add(String.valueOf(CastNumUtil.decimal(CastNumUtil.significand((Double.parseDouble(pflResult.get(i))-Double.parseDouble(jplResult.get(i))), 4), 2)));
			}
			//写入日期结果集合
			map.put("dateResult", dateResult);
			//写入实际减排结果集合
			map.put("sjpflResult", sjpflResult);
			//写入减排量结果集合
			map.put("jplResult", jplResult);
			//返回结果 添加日志
			LogUtil.getLogger().info("EchartsController 情景减排柱状图数据查询成功!");
			return AmpcResult.ok(map);
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排柱状图数据查询异常!",e);
			return AmpcResult.build(1001, "EchartsController 情景减排柱状图数据查询异常!");
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
			//获取情景id
			Object param=data.get("scenarinoId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 情景id为空或出现非法字符!");
				return AmpcResult.build(1003, "情景id为空或出现非法字符!");
			}
			//情景id
			Long scenarinoId = Long.parseLong(param.toString());
		
			//获取行政区划代码
			param=data.get("code");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划代码为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划代码为空或出现非法字符!");
			}
			// 行政区划代码
			String codes = param.toString();
			
			// 行政区划等级
			param=data.get("addressLevle");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划等级为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划等级为空或出现非法字符!");
			}
			// 行政区划等级
			Long addressLevle=Long.parseLong(param.toString());
			
			
			//污染物类型
			param=data.get("stainType");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 污染物类型为空或出现非法字符!");
				return AmpcResult.build(1003, "污染物类型为空或出现非法字符!");
			}
			// 污染物类型
			String stainType = param.toString();
			if(stainType.equals("NOX")){
				stainType="NOx";
			}
			
			//查询类型
			param=data.get("type");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Integer", null, false)){
				LogUtil.getLogger().error("EchartsController 查询类型为空或出现非法字符!");
				return AmpcResult.build(1003, "查询类型为空或出现非法字符!");
			}
			//查询类型
			Integer type=Integer.valueOf(param.toString());
			
			//情景的开始时间
			param=data.get("startDate");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 情景的开始时间为空或出现非法字符!");
				return AmpcResult.build(1003, "情景的开始时间为空或出现非法字符!");
			}
			//情景的开始时间
			String startDate=param.toString();
		
			//获取情景结束时间
			param=data.get("endDate");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 情景的结束时间为空或出现非法字符!");
				return AmpcResult.build(1003, "情景的结束时间为空或出现非法字符!");
			}
			//情景的结束时间
			String endDate=param.toString();
			//创建结果集合
			List<PieUtil> puList=new ArrayList<PieUtil>();
			//对传入的code进行拆分 
			String[] codesSplit=codes.split(",");
			//查询减排量SQL
			String jplSql="";
			//判断是单个还是多个行政区划
			if(codesSplit.length==1){
				//如果查询单个获取对应Code
				String code=codesSplit[0];
				//根据Code级别进行条件调整
				if(addressLevle==1) code=code.substring(0,2)+"%";
				if(addressLevle==2) code=code.substring(0,4)+"%";
				//查询减排量SQL
				jplSql="SELECT EMISSION_DETAILS,MEASURE_REDUCE"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND CODE LIKE '"+code+"'"
						+ " AND SCENARINO_ID="+scenarinoId+""
						+ " ORDER BY EMISSION_DATE,CODE";
			}else{
				//减排量StringBuffer
				StringBuffer jplSb=new StringBuffer("SELECT EMISSION_DETAILS,MEASURE_REDUCE"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND (");
				//循环所有的行政区划 进行sql拼接
				for(int i=0;i<codesSplit.length;i++){
					String code=codesSplit[i];
					//根据Code级别进行条件调整
					if(addressLevle==1) code=code.substring(0,2)+"%";
					if(addressLevle==2) code=code.substring(0,4)+"%";
					if(i==0){
						jplSb.append(" CODE LIKE '"+code+"'");
					}
					jplSb.append(" OR CODE LIKE '"+code+"'");
				}
				jplSb.append(") AND SCENARINO_ID="+scenarinoId+"");
				jplSb.append(" ORDER BY EMISSION_DATE,CODE");
				//查询减排量SQL
				jplSql=jplSb.toString();
			}
			//执行SQL返回减排量的结果集
			List<Map> selectjplMap = this.getBySqlMapper.findRecords(jplSql);
			//循环所有减排结果
			for (int i=0;i<selectjplMap.size();i++) {
				//判断是行业还是措施的  并赋值对应的Json串
				String edetail=null;
				if(type==1){
					Clob clob = (Clob) selectjplMap.get(i).get("EMISSION_DETAILS");
					if (clob != null) {
						edetail = clob.getSubString(1, (int) clob.length());
					}
				}else{
					Clob clob = (Clob) selectjplMap.get(i).get("MEASURE_REDUCE");
					if (clob != null) {
						edetail = clob.getSubString(1, (int) clob.length());
					}
				}
				//解析json获取到所有行业的减排信息
				Map edeMap=mapper.readValue(edetail, Map.class);
				//循环所有行业用来得到所有行业的污染物减排总和
				sector:for(Object obj:edeMap.keySet()){
					//获取行业
					Map ede=(Map)edeMap.get(obj);
					//获取行业中的减排污染物信息
					Object result=null;
					result=ede.get(stainType);
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
				value=CastNumUtil.decimal(value, 4);
				puList.get(i).setValue(value);
			}
			//返回结果
			LogUtil.getLogger().info("EchartsController 情景减排饼状图数据查询成功!");
			return AmpcResult.ok(puList);
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排饼状图数据查询异常!",e);
			return AmpcResult.build(1001, "EchartsController 情景减排饼状图数据查询异常!");
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
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			//获取情景id
			Object param=data.get("scenarinoId");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 情景id为空或出现非法字符!");
				return AmpcResult.build(1003, "情景id为空或出现非法字符!");
			}
			//情景id
			Long scenarinoId = Long.parseLong(param.toString());
			//根据情景Id获取到情景对象
			TScenarinoDetail tsd=tScenarinoDetailMapper.selectByPrimaryKey(scenarinoId);
			if(tsd==null){
				throw new SQLException("EchartsController  查询情景的减排信息列表失败，数据库没有该条情景。");
			}
			//情景的开始时间
			Date sDate=tsd.getScenarinoStartDate();
			//情景的结束时间
			Date eDate=tsd.getScenarinoEndDate();
			//进行日期的转换
			String startDate=DateUtil.DATEtoString(sDate, "yyyy-MM-dd");
			//进行日期的转换
			String endDate=DateUtil.DATEtoString(eDate, "yyyy-MM-dd");
			//获取行政区划代码
			param=data.get("code");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "String", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划代码为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划代码为空或出现非法字符!");
			}
			// 行政区划代码
			String codes = param.toString();
			
			// 行政区划等级
			param=data.get("addressLevle");
			//进行参数判断
			if(!RegUtil.CheckParameter(param, "Long", null, false)){
				LogUtil.getLogger().error("EchartsController 行政区划等级为空或出现非法字符!");
				return AmpcResult.build(1003, "行政区划等级为空或出现非法字符!");
			}
			// 行政区划等级
			Long addressLevle=Long.parseLong(param.toString());
			//定义条件Map
			Map mapQuery=new HashMap();
			//添加条件
			mapQuery.put("startDate", startDate);
			mapQuery.put("endDate", endDate);
			//查询情景的基准类型为1
			mapQuery.put("emtype", 1);
			//定义返回结果集合
			List<RadioListUtil> resultList=new ArrayList<RadioListUtil>();
			//查询基准结果SQL
			String basisSql="";
			//查询减排量SQL
			String jplSql="";
			//查询对应的CodeSQL
			String codeSql="";
			//对Code进行拆分
			String[] codesSplit=codes.split(",");
			//判断是查询单个还是多个
			if(codesSplit.length==1){
				//如果查询单个获取对应Code
				String code=codesSplit[0];
				//根据Code级别进行条件调整
				if(addressLevle==1){
					code=code.substring(0,2)+"%";
				} 
				if(addressLevle==2){
					code=code.substring(0,2)+"%";
					codeSql="SELECT CODE"
							+ " FROM T_EMISSION_DETAIL"
							+ " WHERE EMISSION_TYPE=2"
							+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
							+ " AND CODE LIKE '"+code+"'"
							+ " AND SCENARINO_ID="+scenarinoId+""
							+ " GROUP BY CODE"
							+ " ORDER BY CODE";
				} 
				if(addressLevle==3){
					code=code.substring(0,4)+"%";
					codeSql="SELECT CODE"
							+ " FROM T_EMISSION_DETAIL"
							+ " WHERE EMISSION_TYPE=2"
							+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
							+ " AND CODE LIKE '"+code+"'"
							+ " AND SCENARINO_ID="+scenarinoId+""
							+ " AND CODELEVEL=3"
							+ " GROUP BY CODE"
							+ " ORDER BY CODE";
				} 
				//查询基准结果SQL
				basisSql="SELECT CODE,SUM(PM25) PM25,SUM(PM10) PM10,SUM(SO2) SO2,SUM(NOX) NOX,SUM(VOC)"
						+ " VOC,SUM(CO) CO,SUM(NH3) NH3,SUM(BC) BC,SUM(OC) OC,SUM(PMFINE) PMFINE,SUM(PMC) PMC"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=1"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND CODE LIKE '"+code+"'"
						+ " GROUP BY CODE"
						+ " ORDER BY CODE";
				
				//查询减排量SQL
				jplSql="SELECT CODE,SUM(PM25) PM25,SUM(PM10) PM10,SUM(SO2) SO2,SUM(NOX) NOX,SUM(VOC)"
						+ " VOC,SUM(CO) CO,SUM(NH3) NH3,SUM(BC) BC,SUM(OC) OC,SUM(PMFINE) PMFINE,SUM(PMC) PMC"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND CODE LIKE '"+code+"'"
						+ " AND SCENARINO_ID="+scenarinoId+""
						+ " GROUP BY CODE"
						+ " ORDER BY CODE";
			}else{ //如果是多个行政区划查询
				//基准排放量StringBuffer
				StringBuffer basisSb=new StringBuffer("SELECT CODE,SUM(PM25) PM25,SUM(PM10) PM10,SUM(SO2) SO2,SUM(NOX) NOX,SUM(VOC)"
						+ " VOC,SUM(CO) CO,SUM(NH3) NH3,SUM(BC) BC,SUM(OC) OC,SUM(PMFINE) PMFINE,SUM(PMC) PMC"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=1"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND (");
				//循环所有的行政区划 进行sql拼接
				for(int i=0;i<codesSplit.length;i++){
					String code=codesSplit[i];
					//根据Code级别进行条件调整
					if(addressLevle==1||addressLevle==2) code=code.substring(0,2)+"%";
					if(addressLevle==3) code=code.substring(0,4)+"%";
					if(i==0){
						basisSb.append(" CODE LIKE '"+code+"'");
					}
					basisSb.append(" OR CODE LIKE '"+code+"'");
				}
				basisSb.append(") GROUP BY CODE ORDER BY CODE");
				//查询基准结果SQL
				basisSql=basisSb.toString();
				//减排量StringBuffer
				StringBuffer jplSb=new StringBuffer("SELECT CODE,SUM(PM25) PM25,SUM(PM10) PM10,SUM(SO2) SO2,SUM(NOX) NOX,SUM(VOC)"
						+ " VOC,SUM(CO) CO,SUM(NH3) NH3,SUM(BC) BC,SUM(OC) OC,SUM(PMFINE) PMFINE,SUM(PMC) PMC"
						+ " FROM T_EMISSION_DETAIL"
						+ " WHERE EMISSION_TYPE=2"
						+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
						+ " AND (");
				//循环所有的行政区划 进行sql拼接
				for(int i=0;i<codesSplit.length;i++){
					String code=codesSplit[i];
					//根据Code级别进行条件调整
					if(addressLevle==1||addressLevle==2) code=code.substring(0,2)+"%";
					if(addressLevle==3) code=code.substring(0,4)+"%";
					if(i==0){
						jplSb.append(" CODE LIKE '"+code+"'");
					}
					jplSb.append(" OR CODE LIKE '"+code+"'");
				}
				jplSb.append(") AND SCENARINO_ID="+scenarinoId+"");
				jplSb.append(" GROUP BY CODE ORDER BY CODE");
				//查询减排量SQL
				jplSql=jplSb.toString();
				
				//查询对应Code
				if(addressLevle==2){
					//CodeStringBuffer
					StringBuffer codeSb=new StringBuffer("SELECT CODE"
							+ " FROM T_EMISSION_DETAIL"
							+ " WHERE EMISSION_TYPE=2"
							+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
							+ " AND (");
					//循环所有的行政区划 进行sql拼接
					for(int i=0;i<codesSplit.length;i++){
						String code=codesSplit[i];
						//根据Code级别进行条件调整
						if(addressLevle==1||addressLevle==2) code=code.substring(0,2)+"%";
						if(addressLevle==3) code=code.substring(0,4)+"%";
						if(i==0){
							codeSb.append(" CODE LIKE '"+code+"'");
						}
						codeSb.append(" OR CODE LIKE '"+code+"'");
					}
					codeSb.append(") AND SCENARINO_ID="+scenarinoId+"");
					codeSb.append(" GROUP BY CODE ORDER BY CODE");
					//查询减排量SQL
					codeSql=codeSb.toString();
				} 
				if(addressLevle==3){
					//CodeStringBuffer
					StringBuffer codeSb=new StringBuffer("SELECT CODE"
							+ " FROM T_EMISSION_DETAIL"
							+ " WHERE EMISSION_TYPE=2"
							+ " AND EMISSION_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd') AND TO_DATE('"+endDate+"','yyyy-MM-dd')"
							+ " AND (");
					//循环所有的行政区划 进行sql拼接
					for(int i=0;i<codesSplit.length;i++){
						String code=codesSplit[i];
						//根据Code级别进行条件调整
						if(addressLevle==1||addressLevle==2) code=code.substring(0,2)+"%";
						if(addressLevle==3) code=code.substring(0,4)+"%";
						if(i==0){
							codeSb.append(" CODE LIKE '"+code+"'");
						}
						codeSb.append(" OR CODE LIKE '"+code+"'");
					}
					codeSb.append(") AND SCENARINO_ID="+scenarinoId+"");
					codeSb.append(" AND CODELEVEL=3");
					codeSb.append(" GROUP BY CODE ORDER BY CODE");
					//查询减排量SQL
					codeSql=codeSb.toString();
				} 
			}
			//执行SQL返回基准的结果集
			List<Map> selectBisisMap = this.getBySqlMapper.findRecords(basisSql);
			//执行SQL返回减排量的结果集
			List<Map> selectjplMap = this.getBySqlMapper.findRecords(jplSql);
			//根据Code级别进行条件调整
			if(addressLevle==1){
				//循环所有的Code
				for(int k=0;k<codesSplit.length;k++){
					//取Code
					String code=codesSplit[k];
					//根据Code级别进行条件调整
					String checkCode=code.substring(0,2);
					//定义基准结果对象
					RadioListUtil basisrlu=null;
					//循环所有基准情景的减排结果
					for (int i=0;i<selectBisisMap.size();) {
						//获取基准Map
						Map basisMap=selectBisisMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(basisMap.get("CODE").toString().startsWith(checkCode)){
							//获取行业中的减排污染物信息
							basisrlu=tempUtil(basisMap,basisrlu);
							//定义判断是否包含子集 默认没有子集
							boolean isTrue=false;
							selectBisisMap.remove(i);
							//进行内部嵌套循环
							for(int j=0;j<selectBisisMap.size();){
								basisMap=selectBisisMap.get(j);
								//如果是同一个区域下的进行累积增加
								if(basisMap.get("CODE").toString().startsWith(checkCode)){
									//获取行业中的减排污染物信息
									basisrlu=tempUtil(basisMap,basisrlu);
									//包含子集
									isTrue=true;
									selectBisisMap.remove(j);
								}else{
									break;
								}
								
							}
							//判断是否包含子集 如果包含则赋值
							if(isTrue){
								basisrlu.setType(1);
							}else{
								basisrlu.setType(0);
							}
						}else{
							break;
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
					//定义基准结果对象
					RadioListUtil jplrlu=null;
					//根据Code查询对应的减排
					//循环所有基准情景的减排结果
					for (int i=0;i<selectjplMap.size();) {
						//获取基准Map
						Map jplMap=selectjplMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(jplMap.get("CODE").toString().startsWith(checkCode)){
							//获取行业中的减排污染物信息
							jplrlu=tempUtil(jplMap,jplrlu);
							//添加Code
							jplrlu.setCode(code);
							//根据Code查询对应的中文名称
							String name=tAddressMapper.selectNameByCode(code);
							if(name==null){
								throw new SQLException("EchartsController 减排列表，根据Code没有查找到对应的行政信息。");
							}
							jplrlu.setName(name);
							//定义判断是否包含子集 默认没有子集
							boolean isTrue=false;
							selectjplMap.remove(i);
							for(int j=0;j<selectjplMap.size();){
								jplMap=selectjplMap.get(j);
								if(jplMap.get("CODE").toString().startsWith(checkCode)){
									//获取行业中的减排污染物信息
									jplrlu=tempUtil(jplMap,jplrlu);
									//包含子集
									isTrue=true;
									selectjplMap.remove(j);
								}else{
									break;
								}
							}
							//判断是否包含子集 如果包含则赋值
							if(isTrue){
								jplrlu.setType(1);
							}else{
								jplrlu.setType(0);
							}
						}else{
							break;
						}
					}
					//判断如果都不为空证明有计算结果
					if(basisrlu!=null&&jplrlu!=null){
						resultUtil(basisrlu,jplrlu);
						resultList.add(jplrlu);
					}
				}
			}else if(addressLevle==2){
				//执行SQL返回Code的结果集
				List<Map> selectcodeMap = this.getBySqlMapper.findRecords(codeSql);
				for(int o=0;o<selectcodeMap.size();){
					//取Code
					String code=selectcodeMap.get(o).get("CODE").toString();
					//根据Code级别进行条件调整
					String checkCode=code.substring(0,4);
					//定义基准结果对象
					RadioListUtil basisrlu=null;
					//循环所有基准情景的减排结果  如果基准和Code的减排结果集不匹配 则删掉这条基准记录 直到有匹配的
					for(int i=0;i<selectBisisMap.size();){
						//获取基准Map
						Map basisMap=selectBisisMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(!basisMap.get("CODE").toString().startsWith(checkCode)){
							selectBisisMap.remove(i);
						}else{
							break;
						}
					}
					//循环所有基准情景的减排结果
					for (int i=0;i<selectBisisMap.size();) {
						//获取基准Map
						Map basisMap=selectBisisMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(basisMap.get("CODE").toString().startsWith(checkCode)){
							//获取行业中的减排污染物信息
							basisrlu=tempUtil(basisMap,basisrlu);
							//定义判断是否包含子集 默认没有子集
							boolean isTrue=false;
							selectBisisMap.remove(i);
							for(int j=0;j<selectBisisMap.size();){
								basisMap=selectBisisMap.get(j);
								if(basisMap.get("CODE").toString().startsWith(checkCode)){
									//获取行业中的减排污染物信息
									basisrlu=tempUtil(basisMap,basisrlu);
									//包含子集
									isTrue=true;
									selectBisisMap.remove(j);
								}else{
									break;
								}
							}
							//判断是否包含子集 如果包含则赋值
							if(isTrue){
								basisrlu.setType(1);
							}else{
								basisrlu.setType(0);
							}
						}else{
							break;
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
					
					//定义基准结果对象
					RadioListUtil jplrlu=null;
					//根据Code查询对应的减排
					//循环所有基准情景的减排结果
					for (int i=0;i<selectjplMap.size();) {
						//获取基准Map
						Map jplMap=selectjplMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(jplMap.get("CODE").toString().startsWith(checkCode)){
							//获取行业中的减排污染物信息
							jplrlu=tempUtil(jplMap,jplrlu);
							//添加Code
							jplrlu.setCode(checkCode+"00");
							//根据Code查询对应的中文名称
							String name=tAddressMapper.selectNameByCode(checkCode+"00");
							if(name==null){
								throw new SQLException("EchartsController 减排列表，根据Code没有查找到对应的行政信息。");
							}
							jplrlu.setName(name);
							//定义判断是否包含子集 默认没有子集
							boolean isTrue=false;
							selectjplMap.remove(i);
							for(int j=0;j<selectjplMap.size();){
								jplMap=selectjplMap.get(j);
								if(jplMap.get("CODE").toString().startsWith(checkCode)){
									//获取行业中的减排污染物信息
									jplrlu=tempUtil(jplMap,jplrlu);
									//包含子集
									isTrue=true;
									selectjplMap.remove(j);
								}else{
									break;
								}
							}
							//判断是否包含子集 如果包含则赋值
							if(isTrue){
								jplrlu.setType(1);
							}else{
								jplrlu.setType(0);
							}
						}else{
							break;
						}
					}
					//判断如果都不为空证明有计算结果
					if(basisrlu!=null&&jplrlu!=null){
						resultUtil(basisrlu,jplrlu);
						resultList.add(jplrlu);
					}
					//该类型的编码已经匹配完毕 在集合中删除和匹配code相符的Code
					for(int i=0;i<selectcodeMap.size();){
						if(selectcodeMap.get(o).get("CODE").toString().startsWith(checkCode)){
							selectcodeMap.remove(o);
						}else{
							break;
						}
					}
				}
			}else{
				//执行SQL返回Code的结果集
				List<Map> selectcodeMap = this.getBySqlMapper.findRecords(codeSql);
				for(int o=0;o<selectcodeMap.size();){
					//取Code
					String code=selectcodeMap.get(o).get("CODE").toString();
					//定义基准结果对象
					RadioListUtil basisrlu=null;
					//循环所有基准情景的减排结果  如果基准和Code的减排结果集不匹配 则删掉这条基准记录 直到有匹配的
					for (int i=0;i<selectBisisMap.size();) {
						//获取基准Map
						Map basisMap=selectBisisMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(!basisMap.get("CODE").toString().equals(code)){
							selectBisisMap.remove(i);
						}else{
							break;
						}
					}
					//循环所有基准情景的减排结果
					for (int i=0;i<selectBisisMap.size();) {
						//获取基准Map
						Map basisMap=selectBisisMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(basisMap.get("CODE").toString().equals(code)){
							//获取行业中的减排污染物信息
							basisrlu=tempUtil(basisMap,basisrlu);
							selectBisisMap.remove(i);
						}else{
							break;
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
					
					//定义基准结果对象
					RadioListUtil jplrlu=null;
					//根据Code查询对应的减排
					//循环所有基准情景的减排结果
					for (int i=0;i<selectjplMap.size();) {
						//获取基准Map
						Map jplMap=selectjplMap.get(i);
						//判断如果其中包含给的Code则包含该区域下的信息进行添加信息
						if(jplMap.get("CODE").toString().equals(code)){
							//获取行业中的减排污染物信息
							jplrlu=tempUtil(jplMap,jplrlu);
							//添加Code
							jplrlu.setCode(code);
							//根据Code查询对应的中文名称
							String name=tAddressMapper.selectNameByCode(code);
							if(name==null){
								throw new SQLException("EchartsController 减排列表，根据Code没有查找到对应的行政信息。");
							}
							jplrlu.setName(name);
							jplrlu.setType(0);
							selectjplMap.remove(i);
						}else{
							break;
						}
					}
					//判断如果都不为空证明有计算结果
					if(basisrlu!=null&&jplrlu!=null){
						resultUtil(basisrlu,jplrlu);
						resultList.add(jplrlu);
					}
					//该编码已经匹配完毕 在集合中删除该编码
					selectcodeMap.remove(o);
				}
			}
			LogUtil.getLogger().info("EchartsController 情景减排列表数据查询成功!");
			return AmpcResult.ok(resultList);
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("EchartsController 情景减排列表数据查询异常!",e);
			return AmpcResult.build(1001, "EchartsController 情景减排列表数据查询异常!");
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
	public RadioListUtil tempUtil(Map ede,RadioListUtil rlu) throws Exception{
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
		double nox=Double.parseDouble(ede.get("NOX").toString());
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
		double pmc=Double.parseDouble(ede.get("PMC").toString());
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
		double pm10=Double.parseDouble(ede.get("PM10").toString());
		if(rlu.getPM10()>0){
			rlu.setPM10(rlu.getPM10()+pm10);
		}else{
			rlu.setPM10(pm10);
		}
		double pmfine=Double.parseDouble(ede.get("PMFINE").toString());
		if(rlu.getPMFINE()>0){
			rlu.setPMFINE(rlu.getPM10()+pmfine);
		}else{
			rlu.setPMFINE(pmfine);
		}
		return rlu;
	}
}
