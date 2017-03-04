package ampc.com.gistone.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectorMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TSector;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.CheckUtil;
import ampc.com.gistone.util.CheckUtil1;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ExcelToDate;

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
	
	
	/**
	 * 验证L4s
	 */
	public void checkInfo(){
		//获取所有的行业信息
		List<TSectorExcel> tseList=tSectorExcelMapper.selectAll();
		//获取到所有的行业名称
		List<String> nameList=tSectorExcelMapper.selectNameByGroupName();
		//所有措施中的匹配数据
		List<CheckUtil1> checkUtil1=getMeasure();
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
			CU1:for (CheckUtil1 cu11 : checkUtil1) {
				//获取第一个id[1]的条件集合
				List<CheckUtil> check1 = cu11.getCheck1();
				if(check1==null){
					System.out.println("没有第一个条件,退出,应该是出错了！");
					break;
				}
				//循环第一个id[i]的条件集合
				for (CheckUtil cu1 : check1) {
					//判断符号的条件  
					if(cu1.getMethod().equals("or")){
						//如果为or 则满足一个就可以 如果为true 
						if(id1==cu1.getNum1()||id1==cu1.getNum2()){
							//获取第二个id[2]的条件集合
							List<CheckUtil> check2 = cu11.getCheck2();
							if(check2==null){
								System.out.println("没有id[2]的条件,该条成立");
								//返回证明 该条l4s符合这条措施
								break CU1;
							}
							//如果不为空 就循环id[2]的条件集合 
							for (CheckUtil cu2 : check2) {
								//判断符号的条件  
								if(cu2.getMethod().equals("or")){
									//如果为or 则满足一个就可以 如果为true 
									if(id2==cu2.getNum1()||id2==cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("between")){
									//如果为between;规定数字在两个数之间 
									if(cu2.getNum1()<=id2&&id2<=cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("and")){
									//and规定数字和第一个数字==为true
									if(cu2.getNum1()==id2){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}
							}
							//返回没有匹配结果 该条行业不匹配
							System.out.println("id[2]没有匹配的");
						}else{
							continue;
						}
					}else if(cu1.getMethod().equals("between")){
						//如果为between;规定数字在两个数之间 
						if(cu1.getNum1()<=id1&&id1<=cu1.getNum2()){
							//获取第二个id[2]的条件集合
							List<CheckUtil> check2 = cu11.getCheck2();
							if(check2==null){
								System.out.println("没有id[2]的条件,该条成立");
								//返回证明 该条l4s符合这条措施
								break CU1;
							}
							//如果不为空 就循环id[2]的条件集合 
							for (CheckUtil cu2 : check2) {
								//判断符号的条件  
								if(cu2.getMethod().equals("or")){
									//如果为or 则满足一个就可以 如果为true 
									if(id2==cu2.getNum1()||id2==cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("between")){
									//如果为between;规定数字在两个数之间 
									if(cu2.getNum1()<=id2&&id2<=cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("and")){
									//and规定数字和第一个数字==为true
									if(cu2.getNum1()==id2){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}
							}
							//返回没有匹配结果 该条行业不匹配
							System.out.println("id[2]没有匹配的");
						}else{
							continue;
						}
					}else if(cu1.getMethod().equals("and")){
						//and规定数字和第一个数字==为true
						if(cu1.getNum1()==id1){
							//获取第二个id[2]的条件集合
							List<CheckUtil> check2 = cu11.getCheck2();
							if(check2==null){
								System.out.println("没有id[2]的条件,该条成立");
								//返回证明 该条l4s符合这条措施
								break CU1;
							}
							//如果不为空 就循环id[2]的条件集合 
							for (CheckUtil cu2 : check2) {
								//判断符号的条件  
								if(cu2.getMethod().equals("or")){
									//如果为or 则满足一个就可以 如果为true 
									if(id2==cu2.getNum1()||id2==cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("between")){
									//如果为between;规定数字在两个数之间 
									if(cu2.getNum1()<=id2&&id2<=cu2.getNum2()){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}else if(cu2.getMethod().equals("and")){
									//and规定数字和第一个数字==为true
									if(cu2.getNum1()==id2){
										//获取第三个id[3]的条件集合
										List<CheckUtil> check3 = cu11.getCheck3();
										if(check3==null){
											System.out.println("没有id[3]的条件,该条成立");
											//返回证明 该条l4s符合这条措施
											break;
										}
										//如果不为空 就循环id[3]的条件集合 
										for (CheckUtil cu3 : check3) {
											//判断符号的条件  
											if(cu3.getMethod().equals("or")){
												//如果为or 则满足一个就可以 如果为true 
												if(id3==cu3.getNum1()||id3==cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													//第三层的第一个条件不匹配 
													continue;
												}
											}else if(cu3.getMethod().equals("between")){
												//如果为between;规定数字在两个数之间 
												if(cu3.getNum1()<=id3&&id3<=cu3.getNum2()){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}else if(cu3.getMethod().equals("and")){
												//and规定数字和第一个数字==为true
												if(cu3.getNum1()==id3){
													//获取第四个id[4]的条件集合
													List<CheckUtil> check4 = cu11.getCheck4();
													if(check4==null){
														System.out.println("没有id[4]的条件,该条成立");
														//返回证明 该条l4s符合这条措施
														break;
													}
													//如果不为空 就循环id[4]的条件集合 
													for (CheckUtil cu4 : check4) {
														//判断符号的条件  
														if(cu4.getMethod().equals("or")){
															//如果为or 则满足一个就可以 如果为true 
															if(id4==cu4.getNum1()||id4==cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("between")){
															//如果为between;规定数字在两个数之间 
															if(cu4.getNum1()<=id4&&id4<=cu4.getNum2()){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}else if(cu4.getMethod().equals("and")){
															//and规定数字和第一个数字==为true
															if(cu4.getNum1()==id4){
																System.out.println("全部成立,返回结果");
																//返回证明 该条l4s符合这条措施
																break;
															}else{
																continue;
															}
														}
													}
													//返回没有匹配结果 该条行业不匹配
													System.out.println("id[4]没有匹配的");
												}else{
													continue;
												}
											}
										}
										//返回没有匹配结果 该条行业不匹配
										System.out.println("id[3]没有匹配的");
									}else{
										continue;
									}
								}
							}
							//返回没有匹配结果 该条行业不匹配
							System.out.println("id[2]没有匹配的");
						}else{
							continue;
						}
					}
				}
			}
			//返回没有匹配结果 该条行业不匹配
			System.out.println("id[1]没有匹配的");
		}
	}
	
	/**
	 * 将每一条措施中的L4s进行拆分 得到一个条件的结果集
	 * @return
	 */
	public List<CheckUtil1> getMeasure(){
		//创建l4s分段条件类集合
		List<CheckUtil1> reg=new ArrayList<CheckUtil1>();
		List<TMeasureExcel> tmeList=tMeasureExcelMapper.selectAll();
		for (TMeasureExcel tme : tmeList) {
			//获取到措施中的过滤L4sfilter
			String filterL4s=tme.getMeasureExcelL4s();
			//如果是加号就忽略后面的 取第一个
			if(filterL4s.indexOf("+")>0){
				filterL4s=filterL4s.split("+")[0];
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
				str2 = str.substring(5,14);
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
	
	
	
	/**
	 * 根据Excel更改行业Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_sectorExcelDate")
	public AmpcResult update_SectorDate(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = Long.parseLong(data.get("userId").toString());
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tSectorExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TSectorExcel> readSector = ExcelToDate.ReadSector(fileName,versionId,userId);
			for (TSectorExcel tSector : readSector) {
				int result=tSectorExcelMapper.insertSelective(tSector);
				if(result<1){
					return AmpcResult.build(1000, "添加失败!", null);
				}
			}
			return AmpcResult.ok("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
	/**
	 * 根据Excel更改措施Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_measureExcelDate")
	public AmpcResult update_MeasureExcelDate(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = Long.parseLong(data.get("userId").toString());
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tMeasureExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TMeasureExcel> readTMeasure = ExcelToDate.ReadMeasure(fileName,versionId,userId);
			for (TMeasureExcel tMeasure : readTMeasure) {
				int result=tMeasureExcelMapper.insertSelective(tMeasure);
				if(result<1){
					return AmpcResult.build(1000, "添加失败!", null);
				}
			}
			return AmpcResult.ok("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
}
