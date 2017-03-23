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
			List<String> dateResult=new ArrayList<String>();
			List<String> sjjpResult=new ArrayList<String>();
			List<String> jplResult=new ArrayList<String>();
			//判断发过来的code等级  并全部转换成3级编码
			if(addressLevle==1) code=code.substring(0,2)+"%";
			if(addressLevle==2) code=code.substring(0,4)+"%";
			//如果传过来的已经是3级则不许要转换
			if(addressLevle==3){
				List<Long> codes=new ArrayList<Long>();
				codes.add(Long.parseLong(code));
				//添加code条件
				mapQuery.put("codes", codes);
				//获取所有的基准情景减排结果
				List<TEmissionDetail> basisList=tEmissionDetailMapper.selectByMap(mapQuery);
				for (TEmissionDetail tEmissionDetail : basisList) {
					dateResult.add(DateUtil.DateToStr(tEmissionDetail.getEmissionDate()));
					BigDecimal sumResult=new BigDecimal(0);
					String edetail=tEmissionDetail.getEmissionDetails();
					Map edeMap=mapper.readValue(edetail, Map.class);
					for(Object obj:edeMap.keySet()){
						Map ede=(Map)edeMap.get(obj);
						Object result=ede.get(stainType);
						BigDecimal bigResult=new BigDecimal(result.toString());
						sumResult=sumResult.add(bigResult);
					}
					jplResult.add(sumResult.toString());
				}
				//查询情景的信息时为2
				mapQuery.put("emtype", 2);
				//查询情景的信息时给情景Id
				mapQuery.put("scenarinoId", scenarinoId);
				//获取所有的情景减排结果
				List<TEmissionDetail> tdList=tEmissionDetailMapper.selectByMap(mapQuery);
				
				for (TEmissionDetail tEmissionDetail : tdList) {
					dateResult.add(DateUtil.DateToStr(tEmissionDetail.getEmissionDate()));
					BigDecimal sumResult=new BigDecimal(0);
					String edetail=tEmissionDetail.getEmissionDetails();
					Map edeMap=mapper.readValue(edetail, Map.class);
					for(Object obj:edeMap.keySet()){
						Map ede=(Map)edeMap.get(obj);
						Object result=ede.get(stainType);
						BigDecimal bigResult=new BigDecimal(result.toString());
						sumResult=sumResult.add(bigResult);
					}
					sjjpResult.add(sumResult.toString());
				}
			}else{
				//转换行政区划code
				List<Long> codes=tAddressMapper.selectByCode(code);
				//添加code条件
				mapQuery.put("codes", codes);
			}
			
			Map map=new HashMap();
			map.put("dateResult", dateResult);
			map.put("sjjpResult", sjjpResult);
			map.put("jplResult", jplResult);
			return AmpcResult.ok(map);
		} catch (Exception e) {
			e.printStackTrace();
			return AmpcResult.build(1000, "参数错误");
		}
	}
	
}
