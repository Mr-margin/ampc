package ampc.com.gistone.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TMeasureSectorExcelMapper;
import ampc.com.gistone.database.inter.TPlanMeasureMapper;
import ampc.com.gistone.database.inter.TQueryExcelMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectordocExcelMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TMeasureSectorExcel;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TSectordocExcel;
import ampc.com.gistone.entity.CheckUtil;
import ampc.com.gistone.entity.CheckUtil1;
import ampc.com.gistone.entity.ColorUtil;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;
import ampc.com.gistone.util.checkExcelUtil.ExcelToDate;

/**
 * 行业和措施的控制类
 * 
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
@RestController
@RequestMapping
public class ExcelToDateController {

	// 默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	//行业Excel映射
	@Autowired
	private TSectorExcelMapper tSectorExcelMapper;
	//措施Excel映射
	@Autowired
	private TMeasureExcelMapper tMeasureExcelMapper;
	//措施版本映射
	@Autowired
	public TMeasureSectorExcelMapper tMeasureSectorExcelMapper;
	//行业条件映射
	@Autowired
	public TQueryExcelMapper tQueryExcelMapper;
	//行业描述映射
	@Autowired
	public TSectordocExcelMapper tSectordocExcelMapper;
	//预案措施映射
	@Autowired
	public TPlanMeasureMapper tPlanMeasureMapper;
		
	/**
	 * 删除数据方法
	 * @param type
	 */
	public void deleteInfo(int type,Long userId){
		//只添加措施Excel 时删除对应的表中数据
		if(type==1){
			tMeasureExcelMapper.updateIsEffeByIds(userId);
			tPlanMeasureMapper.updateIsEffeByIds(userId);
		}else if(type==2){  //多个表一起添加时删除的 时删除对应的表中数据
			tSectorExcelMapper.updateIsEffeByIds(userId);
			tMeasureExcelMapper.updateIsEffeByIds(userId);
			tMeasureSectorExcelMapper.updateIsEffeByIds(userId);
			tQueryExcelMapper.updateIsEffeByIds(userId);
			tSectordocExcelMapper.updateIsEffeByIds(userId);
			tPlanMeasureMapper.updateIsEffeByIds(userId);
		}
	}
	
	/**
	 * 中间表保存
	 * 保存到措施模版表
	 */
	@RequestMapping("excel/save_ms")
	public AmpcResult save_MS(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response){
		// 添加异常捕捉
		try {
			// 获取到颜色集合
			//List<ColorUtil> colorUtil=ColorUtil.getColor();   暂时不添加颜色
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = null;
			if (data.get("userId") != null) {
				//获取用户ID
				Object param=data.get("userId");
				//进行参数判断
				if(!RegUtil.CheckParameter(param, "Long", null, false)){
					LogUtil.getLogger().error("ExcelToDateController 用户ID为空或出现非法字符!");
					return AmpcResult.build(1003, "ExcelToDateController 用户ID为空或出现非法字符!");
				}
				// 用户id
				userId = Long.parseLong(param.toString());
			}
			Long time=new Date().getTime();
			String versionId="中间表"+time;
			//获取到筛选后的数据
			LinkedHashSet<TMeasureSectorExcel> ms=checkInfo(versionId,userId);
			Iterator<TMeasureSectorExcel> iterator = ms.iterator();
			int i=0;
			//循环添加  并补充颜色
			while(iterator.hasNext()){
				TMeasureSectorExcel tmse =iterator.next();
//				if(i==colorUtil.size()){
//					i=0;
//				}
//				tmse.setColorcode(colorUtil.get(i).getColorCode());    暂时不添加颜色
//				tmse.setColorname(colorUtil.get(i).getColorName());
				int result=tMeasureSectorExcelMapper.insertSelective(tmse);
				if(result<1){
					throw new SQLException("ExcelToDateController 保存行业措施中间表失败");
				}
				i++;
			}
			LogUtil.getLogger().info("ExcelToDateController 保存行业措施中间表成功!");
			return AmpcResult.ok("更新成功");
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		}catch (Exception e) {
			LogUtil.getLogger().error("ExcelToDateController 保存行业措施中间表异常!",e);
			// 返回错误信息
			return AmpcResult.build(1001, "ExcelToDateController 保存行业措施中间表异常!");
		}
	}
	
	/**
	 * 行业描述Excel
	 * 根据Excel更改行业描述Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 * TODO 行业描述
	 */
	@RequestMapping("excel/update_sectorDocExcelData")
	public AmpcResult update_SectorDocExcelData(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = null;
			if (data.get("userId") != null) {
				//获取用户ID
				Object param=data.get("userId");
	   			//进行参数判断
				if(!RegUtil.CheckParameter(param, "Long", null, false)){
					LogUtil.getLogger().error("ExcelToDateController 用户ID为空或出现非法字符!");
					return AmpcResult.build(1003, "ExcelToDateController 用户ID为空或出现非法字符!");
				}
				// 用户id
				userId = Long.parseLong(param.toString());
			}
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tSectordocExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//错误信息的数据集合
			List<String> msg=null;
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TSectordocExcel> tse = ExcelToDate.ReadSectorDOC(fileName,versionId,userId,msg);
			for (TSectordocExcel tsd : tse) {
				int result=tSectordocExcelMapper.insertSelective(tsd);
				if(result<1){
					throw new SQLException("ExcelToDateController 保存行业描述信息失败,数据库添加失败。");
				}
			}
			LogUtil.getLogger().info("ExcelToDateController 保存行业描述信息成功!");
			return AmpcResult.ok("更新成功");
		}catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("ExcelToDateController 保存行业描述信息异常!",e);
			// 返回错误信息
			return AmpcResult.build(1001, "ExcelToDateController 保存行业描述信息异常!");
		}
	}
	
	/**
	 * 筛选条件Excel
	 * 根据Excel更改措施Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 * TODO 条件
	 */
	@RequestMapping("excel/update_queryExcelData")
	public AmpcResult update_QueryExcelData(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户 如果为空代表是系统默认
			Long userId = null;
			if (data.get("userId") != null) {
				//获取用户ID
				Object param=data.get("userId");
				//进行参数判断
				if(!RegUtil.CheckParameter(param, "Long", null, false)){
					LogUtil.getLogger().error("ExcelToDateController 用户ID为空或出现非法字符!");
					return AmpcResult.build(1003, "ExcelToDateController 用户ID为空或出现非法字符!");
				}
				// 用户id
				userId = Long.parseLong(param.toString());
			}
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tQueryExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TQueryExcel> tqe = ExcelToDate.ReadQuery(fileName,versionId,userId);
			for (TQueryExcel t : tqe) {
				int result=tQueryExcelMapper.insertSelective(t);
				if(result<1){
					throw new SQLException("ExcelToDateController 保存行业筛选条件失败,数据库添加失败。");
				}
			}
			LogUtil.getLogger().info("ExcelToDateController 保存行业筛选条件成功!");
			return AmpcResult.ok("更新成功");
		}catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("ExcelToDateController 保存行业筛选条件异常!",e);
			// 返回错误信息
			return AmpcResult.build(1001, "参数错误", null);
		}
	}
	
	/**
	 * 措施Excel
	 * 根据Excel更改措施Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_measureExcelData")
	public AmpcResult update_MeasureExcelData(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户 如果为空代表是系统默认
			Long userId = null;
			if (data.get("userId") != null) {
				//获取用户ID
				Object param=data.get("userId");
				//进行参数判断
				if(!RegUtil.CheckParameter(param, "Long", null, false)){
					LogUtil.getLogger().error("ExcelToDateController 用户ID为空或出现非法字符!");
					return AmpcResult.build(1003, "ExcelToDateController 用户ID为空或出现非法字符!");
				}
				// 用户id
				userId = Long.parseLong(param.toString());
			}
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long time=new Date().getTime();
			String versionId="措施文件"+time;
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TMeasureExcel> readTMeasure = ExcelToDate.ReadMeasure(fileName,versionId,userId);
			for (TMeasureExcel tMeasure : readTMeasure) {
				int result=tMeasureExcelMapper.insertSelective(tMeasure);
				if(result<1){
					throw new SQLException("ExcelToDateController 保存措施信息失败,数据库添加失败。");
				}
			}
			LogUtil.getLogger().info("ExcelToDateController 保存措施信息成功!");
			return AmpcResult.ok("更新成功");
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("ExcelToDateController 保存措施信息异常!",e);
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
	/**
	 * 行业Excel
	 * 根据Excel更改行业Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_sectorExcelData")
	public AmpcResult update_SectorData(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户 如果为空代表是系统默认
			Long userId = null;
			if (data.get("userId") != null) {
				//获取用户ID
				Object param=data.get("userId");
				//进行参数判断
				if(!RegUtil.CheckParameter(param, "Long", null, false)){
					LogUtil.getLogger().error("ExcelToDateController 用户ID为空或出现非法字符!");
					return AmpcResult.build(1003, "ExcelToDateController 用户ID为空或出现非法字符!");
				}
				// 用户id
				userId = Long.parseLong(param.toString());
			}
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long time=new Date().getTime();
			String versionId="行业文件"+time;
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TSectorExcel> readSector = ExcelToDate.ReadSector(fileName,versionId,userId);
			for (TSectorExcel tSector : readSector) {
				int result=tSectorExcelMapper.insertSelective(tSector);
				if(result<1){
					throw new SQLException("ExcelToDateController 保存行业信息失败,数据库添加失败。");
				}
			}
			LogUtil.getLogger().info("ExcelToDateController 保存行业信息成功!");
			return AmpcResult.ok("更新成功");
		} catch(SQLException e){
			LogUtil.getLogger().error(e.getMessage(),e);
			return AmpcResult.build(1000,e.getMessage());
		} catch (Exception e) {
			LogUtil.getLogger().error("ExcelToDateController 保存行业信息异常!",e);
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
	/**
	 * 用来进行类型装换
	 * @param tse
	 * @param tme
	 * @return
	 */
	public TMeasureSectorExcel castClass(TSectorExcel tse,TMeasureExcel tme,String vid,Long uid){
		TMeasureSectorExcel tmt=new TMeasureSectorExcel();
		tmt.setDebugModel(tme.getMeasureExcelDebug());
		tmt.setMsExcelA(tme.getMeasureExcelA());
		tmt.setMsExcelA1(tme.getMeasureExcelA1());
		tmt.setMsExcelAsh(tme.getMeasureExcelAsh());
		tmt.setMsExcelDisplay(tme.getMeasureExcelDisplay());
		tmt.setMsExcelIntensity(tme.getMeasureExcelIntensity());
		tmt.setMsExcelIntensity1(tme.getMeasureExcelIntensity1());
		tmt.setMsExcelLevel(tme.getMeasureExcelLevel());
		tmt.setMsExcelName(tme.getMeasureExcelName());
		tmt.setMsExcelOp(tme.getMeasureExcelOp());
		tmt.setMsExcelSulfer(tme.getMeasureExcelSulfer());
		tmt.setMsExcelSv(tme.getMeasureExcelSv());
		tmt.setMsExcelType(tme.getMeasureExcelType());
		tmt.setSectorsname(tse.getSectorExcelName());
		tmt.setMid(tme.getMeasureExcelId());
		tmt.setUserId(uid);
		tmt.setMsExcelVersionId(vid);
		tmt.setSid(tse.getSectorExcelId());
		return tmt;
	}
	
	/**
	 * TODO
	 * c1
	 */
	public String c1(LinkedHashSet<TMeasureSectorExcel> ms,CheckUtil1 cu11,TSectorExcel tse,int id1,int id2,int id3,int id4,String vid,Long uid){
		//获取第二个id[2]的条件集合
		List<CheckUtil> check2 = cu11.getCheck2();
		if(check2==null){
			//保存结果
			ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
			//返回证明 该条l4s符合这条措施
			return "ok";
		}
		//如果不为空 就循环id[2]的条件集合 
		for (CheckUtil cu2 : check2) {
			//判断符号的条件  
			if(cu2.getMethod().equals("or")){
				//如果为or 则满足一个就可以 如果为true 
				if(id2==cu2.getNum1()||id2==cu2.getNum2()){
					String result=c2(ms,cu11,tse,id3,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					continue;
				}
			}else if(cu2.getMethod().equals("between")){
				//如果为between;规定数字在两个数之间 
				if(cu2.getNum1()<=id2&&id2<=cu2.getNum2()){
					String result=c2(ms,cu11,tse,id3,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					continue;
				}
			}else if(cu2.getMethod().equals("and")){
				//and规定数字和第一个数字==为true
				if(cu2.getNum1()==id2){
					String result=c2(ms,cu11,tse,id3,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					continue;
				}
			}
		}
		return "no";
	}
	
	/**
	 * TODO
	 * c2
	 * @return
	 */
	public String c2(LinkedHashSet<TMeasureSectorExcel> ms,CheckUtil1 cu11,TSectorExcel tse,int id3,int id4,String vid,Long uid){
		//获取第三个id[3]的条件集合
		List<CheckUtil> check3 = cu11.getCheck3();
		if(check3==null){
			//保存结果
			ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
			//返回证明 该条l4s符合这条措施
			return "ok";
		}
		//如果不为空 就循环id[3]的条件集合 
		for (CheckUtil cu3 : check3) {
			//判断符号的条件  
			if(cu3.getMethod().equals("or")){
				//如果为or 则满足一个就可以 如果为true 
				if(id3==cu3.getNum1()||id3==cu3.getNum2()){
					String result=c3(ms,cu11,tse,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					//第三层的第一个条件不匹配 
					continue;
				}
			}else if(cu3.getMethod().equals("between")){
				//如果为between;规定数字在两个数之间 
				if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
					String result=c3(ms,cu11,tse,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					continue;
				}
			}else if(cu3.getMethod().equals("and")){
				//and规定数字和第一个数字==为true
				if(cu3.getNum1()==id3){
					String result=c3(ms,cu11,tse,id4,vid,uid);
					if(result.equals("ok")){
						return "ok";
					}else{
						return "no";
					}
				}else{
					continue;
				}
			}
		}
		return "no";
	}
	
	
	/**
	 * TODO
	 * c3
	 */
	public String c3(LinkedHashSet<TMeasureSectorExcel> ms,CheckUtil1 cu11,TSectorExcel tse,int id4,String vid,Long uid){
		//获取第四个id[4]的条件集合
		List<CheckUtil> check4 = cu11.getCheck4();
		if(check4==null){
			//保存结果
			ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
			//返回证明 该条l4s符合这条措施
			return "ok";
		}
		//如果不为空 就循环id[4]的条件集合 
		for (CheckUtil cu4 : check4) {
			//判断符号的条件  
			if(cu4.getMethod().equals("or")){
				//如果为or 则满足一个就可以 如果为true 
				if(id4==cu4.getNum1()||id4==cu4.getNum2()){
					//保存结果
					ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
					//返回证明 该条l4s符合这条措施
					return "ok";
				}else{
					continue;
				}
			}else if(cu4.getMethod().equals("between")){
				//如果为between;规定数字在两个数之间 
				if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
					//保存结果
					ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
					//返回证明 该条l4s符合这条措施
					return "ok";
				}else{
					continue;
				}
			}else if(cu4.getMethod().equals("and")){
				//and规定数字和第一个数字==为true
				if(cu4.getNum1()==id4){
					//保存结果
					ms.add(castClass(tse,cu11.gettMeasureExcel(),vid,uid));
					//返回证明 该条l4s符合这条措施
					return "ok";
				}else{
					continue;
				}
			}
		}
		//返回没有匹配结果 该条行业不匹配
		return "no";
	}
	
	/**
	 * TODO
	 * 验证L4s
	 */
	public LinkedHashSet<TMeasureSectorExcel> checkInfo(String vid,Long uid){
		try{
			
		//创建一个满足条件的
		LinkedHashSet<TMeasureSectorExcel> ms = new LinkedHashSet<TMeasureSectorExcel>();
		//获取所有的行业信息
		List<TSectorExcel> tseList=tSectorExcelMapper.selectAll(uid);
		if(tseList.size()==0){
			tseList=tSectorExcelMapper.selectAll(null);
		}
		//所有措施中的匹配数据
		List<CheckUtil1> checkUtil1=getMeasure(uid);
		//循环所有的行业信息
		for (TSectorExcel tse : tseList) {
			//先获取l4s的值
			String l4s=tse.getSectorExcelL4s().toString();
			//对l4s进行拆分
			String[] ids=l4s.split("-");
			//对每一段进行转换int用来比对
			int id1=Integer.valueOf(ids[0]);
			int id2=Integer.valueOf(ids[1]);
			int id3=Integer.valueOf(ids[2]);
			int id4=Integer.valueOf(ids[3]);
			//循环所有的措施条件集合帮助类
			for (CheckUtil1 cu11 : checkUtil1) {
				//获取第一个id[1]的条件集合
				List<CheckUtil> check1 = cu11.getCheck1();
				if(check1==null){
					break;
				}
				//循环第一个id[i]的条件集合
				CU1:for (CheckUtil cu1 : check1) {
					//判断符号的条件  
					if(cu1.getMethod().equals("or")){
						//如果为or 则满足一个就可以 如果为true 
						if(id1==cu1.getNum1()||id1==cu1.getNum2()){
							String result=c1(ms,cu11,tse,id1,id2,id3,id4,vid,uid);
							if(result.equals("ok")){
								 break CU1;
							}
						}else{
							continue;
						}
					}else if(cu1.getMethod().equals("between")){
						//如果为between;规定数字在两个数之间 
						if(cu1.getNum1()<=id1&&id1<=cu1.getNum2()){
							String result=c1(ms,cu11,tse,id1,id2,id3,id4,vid,uid);
							if(result.equals("ok")){
								 break CU1;
							}
						}else{
							continue;
						}
					}else if(cu1.getMethod().equals("and")){
						//and规定数字和第一个数字==为true
						if(cu1.getNum1()==id1){
							String result=c1(ms,cu11,tse,id1,id2,id3,id4,vid,uid);
							if(result.equals("ok")){
								 break CU1;
							}
						}else{
							continue;
						}
					}
				}
			}
		}
		//返回结果集
		LogUtil.getLogger().info("ExcelToDateController 验证L4sFilter成功!");
		return ms;
		}catch(Exception e){
			LogUtil.getLogger().error("ExcelToDateController 验证L4sFilter异常",e);
			return null;
		}
	}
	
	/**
	 * 将每一条措施中的L4s进行拆分 得到一个条件的结果集
	 * @return
	 */
	public List<CheckUtil1> getMeasure(Long userId){
		//创建l4s分段条件类集合
		List<CheckUtil1> reg=new ArrayList<CheckUtil1>();
		//获取到所有措施
		List<TMeasureExcel> tmeList=tMeasureExcelMapper.selectAll(userId);
		//如果没有获取默认的
		if(tmeList.size()==0){
			tmeList=tMeasureExcelMapper.selectAll(null);
		}
		//循环所有的措施
		for (TMeasureExcel tme : tmeList) {
			//获取到措施中的过滤L4sfilter
			String filterL4s=tme.getMeasureExcelL4s();
			//如果是加号就忽略后面的 取第一个
			if(filterL4s.indexOf("+")>0){
				filterL4s=filterL4s.split("\\+")[0];
			}
			//创建每一条 中每一个l4s分段的条件类
			CheckUtil1 checkUtil1=new CheckUtil1();
			//将l4sfilter根据,拆分 获取类l4s中的分组  每一个分组就是一条l4s
			String[] idss=filterL4s.split(",");
			//创建l4s[1]分段中的条件集合
			List<CheckUtil> list1=new ArrayList<CheckUtil>();
			//创建l4s[2]分段中的条件集合
			List<CheckUtil> list2=new ArrayList<CheckUtil>();
			//创建l4s[3]分段中的条件集合
			List<CheckUtil> list3=new ArrayList<CheckUtil>();
			//创建l4s[4]分段中的条件集合
			List<CheckUtil> list4=new ArrayList<CheckUtil>();
			//循环l4sfilter拆分后的每一条l4s
			for (String string : idss) {
				//把l4s按照-进行拆分
				String[] ids=string.split("-");
				//进行判断如果为4则代表为完整的l4s要满足每一个分段
				if(ids.length==4){
					String id1=ids[0];
					String id2=ids[1];
					String id3=ids[2];
					String id4=ids[3];
					checkChar(id1,list1);
					checkChar(id2,list2);
					checkChar(id3,list3);
					checkChar(id4,list4);
					checkUtil1.setCheck1(list1);
					checkUtil1.setCheck2(list2);
					checkUtil1.setCheck3(list3);
					checkUtil1.setCheck4(list4);
					reg.add(checkUtil1);
				}else if(ids.length==3){   //进行判断如果为3则代表要满足前3个分段
					String id1=ids[0];
					String id2=ids[1];
					String id3=ids[2];
					checkChar(id1,list1);
					checkChar(id2,list2);
					checkChar(id3,list3);
					checkUtil1.setCheck1(list1);
					checkUtil1.setCheck2(list2);
					checkUtil1.setCheck3(list3);
					reg.add(checkUtil1);
				}else if(ids.length==2){  //进行判断如果为2则代表要满足前2个分段
					String id1=ids[0];
					String id2=ids[1];
					checkChar(id1,list1);
					checkChar(id2,list2);
					checkUtil1.setCheck1(list1);
					checkUtil1.setCheck2(list2);
					reg.add(checkUtil1);
				}else{                   //进行判断如果为1则代表要满足前1个分段
					String id1=ids[0];
					checkChar(id1,list1);
					checkUtil1.setCheck1(list1);
					reg.add(checkUtil1);
				}
				//写入当前的措施
				checkUtil1.settMeasureExcel(tme);
			}
		}
		//返回结果集
		return reg;
	}
	
	/**
	 * 验证字符  返回判断集合
	 * @param str
	 * @param list
	 */
	public void checkChar(String str,List<CheckUtil> list){
		//判断要截取的字符位数  如果大于4代表字符串中包含符号
		if(str.length()>4){
			//截取符号
			String s = str.substring(4,5);
			//截取前九位进行比对
			String str1 = str.substring(0,9);
			//如果字符串的长度大于14则代表还要进行第二次递归判断
			String str2=null;
			if(str.length()>=14){
				str2 = str.substring(5);
			}
			//将第一个数字进行转换  符号左边的
			int num1 = Integer.valueOf(str1.substring(0,4));
			//将第二个数字进行转换  符号右边的
			int num2 = Integer.valueOf(str1.substring(5,9));
			//创建一个验证符号帮助类
			CheckUtil cu=new CheckUtil();
			cu.setNum1(num1);
			cu.setNum2(num2);
			//判断截取到的符号
			switch (s){ 
				case ";":  //或者关系   or:两个数有一个==就为true
					cu.setMethod("or");
					list.add(cu);
					//如果不为空就递归 判断第二段字符串的符号
					if(str2!=null){
						checkChar(str2,list);
					}
					break;
				case "~": 
					cu.setMethod("between");
					list.add(cu);
					//如果不为空就递归 判断第二段字符串的符号
					if(str2!=null){
						checkChar(str2,list);
					}
					break; 
			} 
		}else{
			//证明穿过来的字符串不包含字符   直接判断就可以了
			int num1=Integer.valueOf(str);
			CheckUtil cu=new CheckUtil();
			cu.setMethod("and");
			cu.setNum1(num1);
			list.add(cu);
		}
	

	}
	
}
