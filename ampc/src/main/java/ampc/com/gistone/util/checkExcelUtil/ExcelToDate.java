package ampc.com.gistone.util.checkExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TSectordocExcel;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ConfigUtil;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;
import ampc.com.gistone.util.RegUtil;

/**
 * Excel解析帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月9日
 */
@RestController
@RequestMapping
public class ExcelToDate {
	//工作簿对象
	public Workbook wb  = null;  
	//得到一个POI的工具类
	public CreationHelper factory;
	public ClientAnchor anchor =null;
	//用来构建Common的map
	public LinkedHashMap drawMap;
	//定义输入流
	public InputStream is=null;
	//定义输出流
    public OutputStream os=null;
    //黄色背景
	public CellStyle yellowStyle;
    //存放验证信息的Map
    public LinkedHashMap checkMap=null;
    //配置文件帮助类
    @Autowired
    public ConfigUtil configUtil;
    /**
     * 用来存放错误信息的集合
     * 0.sheetName长度不匹配
     * 1.表头不匹配
     * 2.表头缺失
     * 3.单元格数据违反了非空验证
     * 4.单元格数据违反了数据类型要求验证
     * 5.单元格数据违反了取值范围要求验证
     * 6.单元格数据违反了长度验证
     */
    public static Map errorMap=new HashMap();
    
    //SheetName错误信息
    public static final String SHEETNAME_ERROR="SheetName名称超过规定长度(要求在10个字符以内)!";
    
   
    /**
     * 初始化Excel信息方法
     * @param path 要解析的路径
     */
	public boolean init(String path,StringBuilder msg){
		try{
			String p=path.substring(path.indexOf(".")+1);
			if(!p.equals("xlsx")||p.equals("xls")){
				msg.append("上传文件不是Excel类型文件。");
				return false;
			}
			File file =new File(path);
			if(!file.exists()){
				msg.append("上传文件不存在,没有找到对应文件。");
				return false;
			}
			
	        is=new FileInputStream(file);    
	        wb = WorkbookFactory.create(is);  
	        factory = wb.getCreationHelper();
	        anchor = new XSSFClientAnchor(0, 0, 0, 0, (short)1, 2, (short)3, 10);
	        drawMap = new LinkedHashMap();
	        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
	          Sheet sheet = wb.getSheetAt(i);
	          drawMap.put(sheet.getSheetName(), sheet.createDrawingPatriarch());
	        }
	        yellowStyle = wb.createCellStyle();
	        yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
          	yellowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
          	yellowStyle.setAlignment(CellStyle.ALIGN_CENTER);
          	yellowStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        LogUtil.getLogger().info("ExcelToDateController 初始化Excel信息方法!");
	        return true;
		}catch(Exception e){
			LogUtil.getLogger().error("ExcelToDateController 初始化Excel信息方法异常!",e);
			msg.append("初始化Excel出现异常!");
			return false;
		}
	}
	/**
	 * 保存清单的验证文件
	 * @param fileName
	 * @return
	 */
	public boolean saveCheckFile(String info,File txt){
       try {
    	   if(!txt.exists()){
    	    txt.createNewFile();
    	   }
           FileWriter writer;
           writer = new FileWriter(txt);
           writer.write(info);
           writer.flush();
           writer.close();
           LogUtil.getLogger().info("ExcelToDateController 保存清单的验证文件成功!");
           return true;
       } catch (IOException e) {
    	   LogUtil.getLogger().error("ExcelToDateController 保存清单的验证文件异常!",e);
           return false;
       }
   }
	
	
	 /**
     * 删除文件，可以是文件或文件夹
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return true;
        } else {
        	 if (file.delete()) {
                 System.out.println("删除单个文件" + fileName + "成功！");
                 return true;
             } else {
                 System.out.println("删除单个文件" + fileName + "失败！");
                 return false;
             }
        }
    }
	/**
	 * 根据jsonName解析对应的校验规则
	 * @param jsonName 校验规则文件
	 * @return 校验规则Map
	 */
	public LinkedHashMap readCheckJson(String jsonName){  
		try {
			/**
  			 * TODO 本地配置
  			 */
			//获取校验文件
			//File directory = new File("");
			//String path= directory.getCanonicalPath()+"\\src\\main\\resources\\checkFile\\"+jsonName;
			/**
			 * TODO外网配置
			 */
			String path="/home/xulili/apache-tomcat-8.5.13/webapps/ampc/WEB-INF/classes/checkFile/"+jsonName;
			//解析文件获取解析信息
			LinkedHashMap map=JsonUtil.readObjFromJsonFile(path, LinkedHashMap.class);
			//返回信息
			LogUtil.getLogger().info("ExcelToDateController 获取Excel校验信息成功!");
			return map;
		} catch (IOException e) {
			LogUtil.getLogger().error("ExcelToDateController 获取Excel校验信息异常!",e);
			return null;
		}
    }
	
   /**
    * 读取表头
    * @param sheet
    * @return
    */
	public Map readHeader(Sheet sheet) {
	    //设置表头
	    String[] headers = obtainRow(sheet, 0);
	    //存放表头顺序
	    Map m = new LinkedHashMap<>();
	    for (int j = 0; j < headers.length; j++)
	      m.put(j, headers[j]);
	    return m;
	}
	
	/**
    * 获取行数据
    * 获取字符串数组类型的行数据
    * @param sheet  sheet页
    * @param rowNum 行数
    * @return 字符串数据数组
    */
	public String[] obtainRow(Sheet sheet, int rowNum) {
	    Row row = sheet.getRow(rowNum);
	    if (null == row) {
	      return null;
	    }
	    int num = row.getLastCellNum();
	    //获取数据库字段名称数组
	    String[] values = new String[num];
	    for (int j = 0; j < num; j++) {
	      String s = getCellValue(row.getCell(j));
	      values[j] = s;
	    }
	    return values;
	}
	
    /**
	  * 生成标注
	  */
	public Comment getComment(Sheet sheet, String message) {
	  Drawing drawing = (Drawing)drawMap.get(sheet.getSheetName());
	  ClientAnchor an = drawing.createAnchor(0, 0, 0, 0, (short) 1, 2, (short) 3, 10);
	  Comment comment0 = drawing.createCellComment(an);
	  RichTextString str0 = factory.createRichTextString(message);
	  comment0.setString(str0);
	  comment0.setAuthor("Apache POI");
	  return comment0;
	}
	
   /**
	*将列传换成英文字母   相当于Excel中的列名 如:1-A 2-B
	*/
    public String columnChar(int num) {
		int big = num / 26;
		String sBig = "";
		if (big != 0) {
		  sBig = String.valueOf(((char) (big + 64)));
		}
		int small = num % 26;
		String sSmall = String.valueOf(((char) (small + 65)));
		return sBig + sSmall;
    }
	
    /**
	  * 保存Excel
	  * @param path
	  * @throws Exception
	  */
	public void save(String path) throws Exception {
	    FileOutputStream out = new FileOutputStream(path);
	    wb.write(out);
	    out.close();
	}
	
	/**
	 * 单元格的验证方法
	 * @param file  文件名称
	 * @param sheetName  sheet页名称
	 * @param rowNum     行数
	 * @param columnNum  列数
	 * @param tempMap    验证Map
	 * @param cellError  单元格错误集合
	 * @param cellValue  单元格值
	 */
	public void checkCell(String file,String sheetName,int rowNum,int columnNum,Map tempMap,List<String> cellError,String cellValue){
		//进行非空验证
        if(tempMap.get("isNotNull").toString().equals("y")){
        	if(cellValue.isEmpty()){
        		//写入错误信息
        		cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格为空,验证文件要求必须填写!");
				//写入错误信息
				errorMap.put(3,file+":单元格数据违反了非空验证!");
            }
    	}
        //获取要求单元格的数据类型
        String dataType=tempMap.get("dataType").toString();
        //进行数据类型验证
        if(!dataType.isEmpty()){
        	//将数据转换进行判断
        	if(!RegUtil.CheckParameter(cellValue, dataType, null, false)){
        		//写入错误信息
        		cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据类型不匹配,验证文件要求必须为"+dataType+"类型!");
				//写入错误信息
				errorMap.put(4,file+":单元格数据违反了数据类型要求验证!");
			}else{
				//获取要求单元格的取值范围
                String valueRange=tempMap.get("valueRange").toString();
                //进行取值范围验证
                if(!valueRange.equals("none")){
                	//判断取值范围
                	if(dataType.equals("Integer")){
                		String[] valuesRanges=valueRange.split("~");
                		int value=Integer.valueOf(cellValue);
                		if(valuesRanges[0].equals("inf")&&!(valuesRanges[1].equals("inf"))){
                			if(value>Integer.valueOf(valuesRanges[1])){
                				//写入错误信息
                        		cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
            					errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}else if(valuesRanges[1].equals("inf")&&!(valuesRanges[0].equals("inf"))){
                			if(value<Integer.valueOf(valuesRanges[0])){
                				//写入错误信息
                				cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
                				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}else if(!(valuesRanges[0].equals("inf"))&&!(valuesRanges[0].equals("inf"))){
                			if(value<Integer.valueOf(valuesRanges[0])||value>Integer.valueOf(valuesRanges[1])){
                				//写入错误信息
                				cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
                				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}
                	}else if(dataType.equals("Double")){
                		String[] valuesRanges=valueRange.split("~");
                		double value=Double.valueOf(cellValue);
                		if(valuesRanges[0].equals("inf")&&!(valuesRanges[1].equals("inf"))){
                			if(value>Double.valueOf(valuesRanges[1])){
                				//写入错误信息
                        		cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
                        		errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}else if(valuesRanges[1].equals("inf")&&!(valuesRanges[0].equals("inf"))){
                			if(value<Double.valueOf(valuesRanges[0])){
                				//写入错误信息
                				cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
                				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}else if(!(valuesRanges[0].equals("inf"))&&!(valuesRanges[0].equals("inf"))){
                			if(value<Integer.valueOf(valuesRanges[0])||value>Integer.valueOf(valuesRanges[1])){
                				//写入错误信息
                				cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+valuesRanges[0]+"~"+valuesRanges[1]+"之间!");
            					//写入错误信息
                				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                			}
                		}
                	}else{
                		//记录是否存在
                		boolean isExist=false;
                		//信息拼接
                		String joinInfo="";
                		//如果验证为sheetName则要求单元格的值必须和sheetName名称相同
                		if(valueRange.equals("sheetName")){
                			//如果存在
                			if(cellValue.equals(sheetName)){
                				isExist=true;
                			}else{ //不存在拼接信息
                				joinInfo="sheetName";
                			}
                		}else{
                			//多个值需要拆分判断
                			String[] valuesRanges=valueRange.split(",");
                    		for(int q=0;q<valuesRanges.length;q++){
                    			if((q+1)==valuesRanges.length){
                    				joinInfo+=valuesRanges[q];
                    			}else{
                    				joinInfo+=valuesRanges[q]+"、";
                    			}
                    			if(cellValue.equals(valuesRanges[q])){
                    				isExist=true;
                    				break;
                    			}
                    		}
                		}
                		if(!isExist){
                			//写入错误信息
            				cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格数据取值范围不匹配,验证文件要求必须为"+joinInfo+"!");
        					//写入错误信息
            				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                		}
                	}
                }
			}
        }
        //获取要求单元格的长度验证
        String maxLenths=tempMap.get("maxLenth").toString();
        //进行数据长度验证
        if(!maxLenths.isEmpty()){
        	int maxLenth=Integer.valueOf(maxLenths);
        	if(cellValue.length()>maxLenth){
        		//写入错误信息
        		cellError.add(file+":"+sheetName+"工作簿下的第"+rowNum+"行"+columnChar(columnNum)+"列的单元格违反了长度验证,验证文件要求长度在"+maxLenth+"之内!");
				//写入错误信息
				errorMap.put(6,file+":单元格数据违反了长度验证!");
            }
    	}
	}
   /**  
    * TODO
	* 行业描述Excel表
	* 读取excel描述数据   读取行业描述Excel表
	* @param path  
	*/
	public List<TSectordocExcel> ReadSectorDOC(String fileName,Long versionId,Long userId,List<String> msg,String outPath,Long templateId){ 
		//定义结果  默认false 
	    boolean isError=false;
		//获取文件名称
		String file=fileName.substring(fileName.lastIndexOf("/")+1);
		//返回结果的集合
		List<TSectordocExcel> sectorDocList=new ArrayList<TSectordocExcel>();
		//生成校验清单文件的数据集合
		List<CheckNativeUtil> nativeList=new ArrayList<CheckNativeUtil>();
		//初始化错误类型Map
		errorMap=new HashMap();
        try {  
        	String message=null;
        	StringBuilder mssg=new StringBuilder();
        	//执行Excel初始化
        	if(!init(fileName,mssg)){
        		msg.add(file+":"+mssg.toString());
        		return null;
        	}
        	//获取验证集合
        	checkMap=readCheckJson("应急系统新_1描述文件.json");
        	if(checkMap==null){
        		msg.add(file+":读取系统内置校验文件出现异常!");
        		return null;
        	}
        	//获取字段验证Map
        	Map checkHeader=(Map)((Map)checkMap.get("应急系统新_1描述文件")).get("sheet0");
        	//获取sheet名称的长度验证
        	int sheetNameMaxLength=Integer.valueOf(((Map)checkHeader.get("sheetName")).get("maxLenth").toString());
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //sheet页验证
            if(sheetCount<1){
            	msg.add(file+":缺失sheet页!");
        		return null;
            }
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	//判断sheet名称的信息是否合格
            	if(sheetName.length()>sheetNameMaxLength){
            		//写入错误信息
					errorMap.put(0,file+":"+SHEETNAME_ERROR);
            	}
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {
            		//获得行数据 
                    Row row = rows.next(); 
                    //第一行是表头行
                    if(row.getRowNum()==0){
                    	//获取表头信息
                    	Map headerMap=readHeader(sheet);
                    	//定义临时变量
                    	int j=0;
                    	//循环所有的验证表头
                    	for(Object ch:checkHeader.keySet()){
                    		if(ch.toString().equals("sheetName")) continue;
                    		try{
                    			if(!headerMap.get(j).equals(ch)){
                    				//写入错误信息
                    				message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j)+"列的表头"+headerMap.get(j)+"和验证文件表头名称不匹配,验证文件表头名称为:"+ch+"!";
                            		Cell cell=row.getCell(j);
                            		cell=row.createCell((short) j);
                            		cell.setCellValue(getCellValue(cell));
                					cell.setCellStyle(yellowStyle);
                					cell.setCellComment(getComment(sheet, message));
                					//写入错误信息
                					errorMap.put(1,file+":表头和验证文件表头名称不匹配!");
                    			}
                    		}catch(Exception e){
                    			//写入错误信息
                    			message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j+1)+"列的表头缺失,验证文件表头名称为:"+ch+"!";
                    			Cell cell=row.getCell(j);
                    			cell=row.createCell((short) j);
                        		cell.setCellValue(getCellValue(cell));
            					cell.setCellStyle(yellowStyle);
            					cell.setCellComment(getComment(sheet, message));
            					//写入错误信息
            					errorMap.put(2,file+":表头缺失!");
                    		}
                    		//临时变量迭代 获取下一个表头信息
                    		j++;
                    	}
                    	continue;
                    }
                    //定义中间验证Map
                    Map tempMap=null;
                    //定义中间单元格对象
                    Cell cell=null;
                    //单元格的验证错误信息
                    List<String> cellError=new ArrayList();
                    //记录单元格信息
                    String cellValue="";
                    //获取单元格信息
                    cell=row.getCell(0);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("Sheet");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1,0, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 0);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                    //获取单元格信息
                    cell=row.getCell(1);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("Etitle");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1,1, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 1);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                  
                    //获取单元格信息
                    cell=row.getCell(2);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("CTitle1");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1,2, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 2);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                  
                    //用来判断类型验证是否出错  方便下面对取值范围进行判断
                    boolean typeError=false;
                    //获取单元格信息
                    cell=row.getCell(3);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("Type");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1,3, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	//出错就复制true
                    	typeError=true;
                    	cell=row.createCell((short) 3);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                    //获取单元格信息
                    cell=row.getCell(4);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("属性类型");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //记录类型
                    String type=cellValue;
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1, 4, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 4);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                    //获取单元格信息
                    cell=row.getCell(5);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("用户填写说明");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1, 5, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 5);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                 
                    //获取单元格信息
                    cell=row.getCell(6);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("是否必填");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    //执行单元格验证方法
                    checkCell(file, sheetName, row.getRowNum()+1, 6, tempMap, cellError, cellValue);
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 6);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                  
                    //获取单元格信息
                    cell=row.getCell(7);
                    //获取验证条件
                    tempMap=(Map)checkHeader.get("取值范围");
                    //先获取Excel中属性
                    cellValue=getCellValue(cell);
                    //单元格的验证错误信息
                    cellError=new ArrayList();
                    /**
                     * TODO 因为这个列是要对清单数据的匹配条件 所以要特别验证
                     */
                    if(cellValue.isEmpty()){
                		//写入错误信息
                		cellError.add(file+":"+sheetName+"工作簿下的第"+(row.getRowNum()+1)+"行"+columnChar(7)+"列的单元格为空,验证文件要求必须填写!");
        				//写入错误信息
        				errorMap.put(3,file+":单元格数据违反了非空验证!");
                    }
                    //将数据转换进行判断
                	if(!RegUtil.CheckParameter(cellValue, "String", null, false)){
                		//写入错误信息
                		cellError.add(file+":"+sheetName+"工作簿下的第"+(row.getRowNum()+1)+"行"+columnChar(7)+"列的单元格数据类型不匹配,验证文件要求必须为String类型!");
        				//写入错误信息
        				errorMap.put(4,file+":单元格数据违反了数据类型要求验证!");
        			}
                    //进行取值范围验证
                    if(!cellValue.equals("none")){
                    	if(typeError){
                    		//写入错误信息
            				cellError.add(file+":"+sheetName+"工作簿下的第"+(row.getRowNum()+1)+"行"+columnChar(7)+"列的单元格数据无法进行校验,因为Type列有错误!");
        					//写入错误信息
            				errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                    	}else{
                    		boolean rangeError=false;
                    		//获取类型
                    		String typeValue=getCellValue(row.getCell(3));
                    		//判断取值范围
                        	if(typeValue.equals("int")){
                        		String[] valuesRanges=cellValue.split("~");
                        		if(valuesRanges.length!=2){
                        			rangeError=true;
                        		}else{
                        			if(!valuesRanges[0].equals("inf")||!RegUtil.CheckParameter(valuesRanges[0], "Integer", null, false)) rangeError=true;
                        			if(!valuesRanges[1].equals("inf")||!RegUtil.CheckParameter(valuesRanges[1], "Integer", null, false)) rangeError=true;
                        		} 
                        	}else if(typeValue.equals("double")){
                        		String[] valuesRanges=cellValue.split("~");
                        		if(valuesRanges.length!=2){
                        			rangeError=true;
                        		}else{
                        			if(!valuesRanges[0].equals("inf")||!RegUtil.CheckParameter(valuesRanges[0], "Double", null, false)) rangeError=true;
                        			if(!valuesRanges[1].equals("inf")||!RegUtil.CheckParameter(valuesRanges[1], "Double", null, false)) rangeError=true;
                        		}
                        	}
                        	if(rangeError){
                        		//写入错误信息
                        		cellError.add(file+":"+sheetName+"工作簿下的第"+(row.getRowNum()+1)+"行"+columnChar(7)+"列的单元格数据取值范围不正确,详见右侧说明!");
            					//写入错误信息
            					errorMap.put(5,file+":单元格数据违反了数据取值范围验证!");
                        	}
                    	}
                    }
                    //判断是否出现错误
                    if(cellError.size()>0){
                    	cell=row.createCell((short) 7);
                		cell.setCellValue(cellValue);
    					cell.setCellStyle(yellowStyle);
    					message="";
    					for(int u=0;u<cellError.size();u++){
    						message+=(u+1)+":"+cellError.get(u)+"\n";
    					}
    					cell.setCellComment(getComment(sheet, message));
                    }
                    //循环错误信息的Map,记录错误信息
                	if(errorMap.size()>0){
                		isError=true;
                		continue;
                	}
                	//创建验证清单的数据对象并赋值
                	CheckNativeUtil cn=new CheckNativeUtil();
                	cn.setSheetNum(String.valueOf(i));
                	cn.setDataType(getCellValue(row.getCell(3)));
                	cn.setTitle(getCellValue(row.getCell(1))+","+getCellValue(row.getCell(2)));
                	cn.setIsNotNull(getCellValue(row.getCell(6)));
                	cn.setValueRange(getCellValue(row.getCell(7)));
                	nativeList.add(cn);
                    //只保留属性为 统计 和 筛选和统计 的信息
                    if(type.equals("统计")||type.equals("筛选和统计")){
                    	//如果没有出现错误则记录数据 
                    	if(!isError){
                    		TSectordocExcel sectorDoc=new TSectordocExcel();
                            //写入行业表中名称
                            sectorDoc.setSectordocName(getCellValue(row.getCell(0)));
                            //写入行业Etitle
                            sectorDoc.setSectordocEtitle(getCellValue(row.getCell(1)));
                            //写入行业名头
                            sectorDoc.setSectordocCtitle(getCellValue(row.getCell(2)));
                            //写入属性
                            sectorDoc.setSectordocType(getCellValue(row.getCell(3)));
                            //写入属性类型
                            sectorDoc.setSectordocStype(getCellValue(row.getCell(4)));
                            //写入用户填写说明
                            sectorDoc.setSectordocDoc(getCellValue(row.getCell(5)));
                            //写入行业显示的名称
                            sectorDoc.setSectordocDisname(sheetName);
                            //写入版本等信息
                            sectorDoc.setSectordocVersion(versionId);
                            sectorDoc.setDetailedListId(templateId);
                            sectorDoc.setDetailedListType("本地清单");
                            sectorDoc.setUserId(userId);
                            sectorDocList.add(sectorDoc);
                    	}
                    }
                }
            	
            }
            //如果出现错误就写入错误信息 并创建输出流用来存放验证
            if(isError){
            	for(Object key:errorMap.keySet()){
        			msg.add(errorMap.get(key).toString());
        		}
            	File filePath =new File(outPath);
            	os = new FileOutputStream(filePath);
                LogUtil.getLogger().error("ExcelToDateController 校验行业描述Excel表异常!");
                return null;
            }else{
            	 //删除验证Excel的结果文件
                if(!delete(outPath)){
            		LogUtil.getLogger().error("ExcelToDateController 删除验证Excel异常!");
                    return null;
            	}
            }
            //定义结果数据对象
            LinkedHashMap<String,Map> resultMap=new LinkedHashMap<String, Map>();
            //循环当前的有多少Sheet页
            for(int o=0;o<sheetCount;o++){
            	//中间转换
            	String num=String.valueOf(o);
            	//得到对应sheet页的信息
            	List<CheckNativeUtil> list=nativeList.stream().filter(n->n.getSheetNum().equals(num)).collect(Collectors.toList());
            	//记录页的数据
            	LinkedHashMap<String,Map> dataMap=new LinkedHashMap<String,Map>();
            	LinkedHashMap<String,String> tempMap=new LinkedHashMap<String,String>();
            	//循环页数据  记录页数据
            	for(CheckNativeUtil cnu : list){
            		tempMap=new LinkedHashMap<String,String>();
            		tempMap.put("valueRange", cnu.getValueRange());
            		tempMap.put("dataType", cnu.getDataType());
            		tempMap.put("isNotNull", cnu.getIsNotNull());
            		tempMap.put("maxLenth", "");
            		dataMap.put(cnu.getTitle(), tempMap);
            	}
            	tempMap=new LinkedHashMap<String,String>();
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(o); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	tempMap.put("valueRange", sheetName);
            	tempMap.put("maxLenth", "10");
            	dataMap.put("sheetName", tempMap);
            	//填充到结果数据
            	resultMap.put("sheet"+o, dataMap);
            }
            //对数据进行转换
  		    ObjectMapper mapper=new ObjectMapper();
  			String info= mapper.writeValueAsString(resultMap);
  		   //获取校验文件
  			/**
  			 * TODO 本地配置
  			 */
     	  // File directory = new File("");
     	  // String qdPath= directory.getCanonicalPath()+"\\src\\main\\resources\\checkFile\\应急系统新_3清单数据.json";
     	  /**
     	   * TODO 外网配置
     	   */
  			String qdPath="/home/xulili/apache-tomcat-8.5.13/webapps/ampc/WEB-INF/classes/checkFile/应急系统新_3清单数据.json";
  			File txt=new File(qdPath);
            //保存清单校验文件
            if(!saveCheckFile(info,txt)){
            	msg.add("创建清单校验文件失败!");
            	//删除清单验证的文件
            	if(!delete(qdPath)){
            		LogUtil.getLogger().error("ExcelToDateController 删除清单验证文件异常!");
                    return null;
            	}
            }
            //返回信息
			LogUtil.getLogger().info("ExcelToDateController 获取行业描述Excel表成功!");
            return sectorDocList;
        } catch (Exception e) {  
        	LogUtil.getLogger().error("ExcelToDateController 获取行业描述Excel表异常!",e);
            return null;
        }finally{
        	try{
        		if(is!=null) is.close();
        		if(os!=null){
        			wb.write(os);
        			os.close();
        		} 
        		if(wb!=null) wb.close();
        	}catch(Exception e){
        		LogUtil.getLogger().error("ExcelToDateController 关闭流异常!",e);
        	}
        }  
    }  
	
	
	/**  
	 * 筛选条件 Excel 
	* 读取excel筛选逻辑数据   读取筛选逻辑Excel表
	* @param path  
	*/
	public List<TQueryExcel> ReadQuery(String fileName,Long versionId,Long userId,List<String> msg,String outPath,Long templateId){  
		//定义结果  默认false 
	    boolean isError=false;
		List<TQueryExcel> queryList=new ArrayList<TQueryExcel>();
		//获取文件名称
		String file=fileName.substring(fileName.lastIndexOf("/")+1);
		//初始化错误类型Map
		errorMap=new HashMap();
		try {  
			String message=null;
			StringBuilder mssg=new StringBuilder();
        	//执行Excel初始化
        	if(!init(fileName,mssg)){
        		msg.add(file+":"+mssg.toString());
        		return null;
        	}
        	//获取验证集合
        	checkMap=readCheckJson("应急系统新_2筛选文件.json");
        	if(checkMap==null){
        		msg.add(file+":读取系统内置校验文件出现异常!");
        		return null;
        	}
        	//获取字段验证Map
        	Map checkHeader=(Map)((Map)checkMap.get("应急系统新_2筛选文件")).get("sheet0");
        	//记录sheet页的Key 用来获取验证条件
        	List<String> keyList=new ArrayList<String>();
        	for(Object ch1:checkHeader.keySet()){
        		keyList.add(ch1.toString());
        	}
        	//获取sheet名称的长度验证
        	int sheetNameMaxLength=Integer.valueOf(((Map)checkHeader.get("sheetName")).get("maxLenth").toString());
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //sheet页验证
            if(sheetCount<1){
            	msg.add(file+":缺失sheet页!");
        		return null;
            }
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	//判断sheet名称的信息是否合格
            	if(sheetName.length()>sheetNameMaxLength){
            		//写入错误信息
					errorMap.put(0,file+":"+SHEETNAME_ERROR);
            	}
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
                    	//获取表头信息
                    	Map headerMap=readHeader(sheet);
                    	//定义临时变量
                    	int j=0;
                    	//循环所有的验证表头
                    	for(Object ch:checkHeader.keySet()){
                    		if(ch.toString().equals("sheetName")) continue;
                    		try{
                    			if(!headerMap.get(j).equals(ch)){
                    				//写入错误信息
                    				message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j)+"列的表头"+headerMap.get(j)+"和验证文件表头名称不匹配,验证文件表头名称为:"+ch+"!";
                            		Cell cell=row.getCell(j);
                            		cell=row.createCell((short) j);
                            		cell.setCellValue(getCellValue(cell));
                					cell.setCellStyle(yellowStyle);
                					cell.setCellComment(getComment(sheet, message));
                					//写入错误信息
                					errorMap.put(1,file+":表头和验证文件表头名称不匹配!");
                    			}
                    		}catch(Exception e){
                    			//写入错误信息
                    			message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j+1)+"列的表头缺失,验证文件表头名称为:"+ch+"!";
                    			Cell cell=row.getCell(j);
                    			cell=row.createCell((short) j);
                        		cell.setCellValue(getCellValue(cell));
            					cell.setCellStyle(yellowStyle);
            					cell.setCellComment(getComment(sheet, message));
            					//写入错误信息
            					errorMap.put(2,file+":表头缺失!");
                    		}
                    		//临时变量迭代 获取下一个表头信息
                    		j++;
                    	}
                    	continue;
                    }
                    //定义中间验证Map
                    Map tempMap=null;
                    //定义中间单元格对象
                    Cell cell=null;
                    //单元格的验证错误信息
                    List<String> cellError=new ArrayList();
                    //记录单元格信息
                    String cellValue="";
                    for(int k=0;k<keyList.size()-1;k++){
                    	//获取单元格信息
                        cell=row.getCell(k);
                        //获取验证条件
                        tempMap=(Map)checkHeader.get(keyList.get(k));
                        //先获取Excel中属性
                        cellValue=getCellValue(cell);
                        //单元格的验证错误信息
                        cellError=new ArrayList();
                        //执行单元格验证方法
                        checkCell(file, sheetName, row.getRowNum()+1,k, tempMap, cellError, cellValue);
                        //判断是否出现错误
                        if(cellError.size()>0){
                        	cell=row.createCell((short) k);
                    		cell.setCellValue(cellValue);
        					cell.setCellStyle(yellowStyle);
        					message="";
        					for(int u=0;u<cellError.size();u++){
        						message+=(u+1)+":"+cellError.get(u)+"\n";
        					}
        					cell.setCellComment(getComment(sheet, message));
                        }
                    }
                    //循环错误信息的Map,记录错误信息
                	if(errorMap.size()>0){
                		isError=true;
                		continue;
                	}
                    
                    TQueryExcel query=new TQueryExcel();
                    //写入行业Etitle
                    query.setQueryEtitle(getCellValue(row.getCell(0)));
                    //写入条件名称
                    query.setQueryName(getCellValue(row.getCell(1)));
                    //写入出现条件英文
                    query.setQueryShowqueryen(getCellValue(row.getCell(2)));
                    //写入出现条件
                    query.setQueryShowquery(getCellValue(row.getCell(3)));
                    //写入条件值
                    query.setQueryValue(getCellValue(row.getCell(4)));
                    //写入选项类型
                    query.setQueryOptiontype(getCellValue(row.getCell(5)));
                    //写入选项1
                    query.setQueryOption1(getCellValue(row.getCell(6)));
                    //写入选项2
                    query.setQueryOption2(getCellValue(row.getCell(7)));
                    //写入选项3
                    query.setQueryOption3(getCellValue(row.getCell(8)));
                    //写入选项4
                    query.setQueryOption4(getCellValue(row.getCell(9)));
                    //写入选项5
                    query.setQueryOption5(getCellValue(row.getCell(10)));
                    //写入行业显示名称
                    query.setSectorName(sheetName);
                    //写入版本等信息
                    query.setQueryVersion(versionId);
                    query.setDetailedListId(templateId);
                    query.setDetailedListType("本地清单");
                    query.setUserId(userId);
                    queryList.add(query);
                }  
            }
            //如果出现错误就写入错误信息 并创建输出流用来存放验证
            if(isError){
            	for(Object key:errorMap.keySet()){
        			msg.add(errorMap.get(key).toString());
        		}
            	File filePath =new File(outPath);
            	os = new FileOutputStream(filePath);
                LogUtil.getLogger().error("ExcelToDateController 校验行业筛选Excel表异常!");
                return null;
            }else{
            	//删除验证Excel的结果文件
                if(!delete(outPath)){
            		LogUtil.getLogger().error("ExcelToDateController 删除验证行业筛选Excel异常!");
                    return null;
            	}
            }
            LogUtil.getLogger().info("ExcelToDateController 获取行业筛选Excel成功!");
            return queryList;
        } catch (Exception e) {  
        	LogUtil.getLogger().error("ExcelToDateController 获取行业筛选Excel异常!",e);
            return null;
        }finally{
        	try{
        		if(is!=null) is.close();
        		if(os!=null){
        			wb.write(os);
        			os.close();
        		} 
        		if(wb!=null) wb.close();
        	}catch(Exception e){
        		LogUtil.getLogger().error("ExcelToDateController 关闭流异常!",e);
        	}
        }    
    }  
	
	
	/**  TODO
	 *行业Excel表
	* 读取excel数据   读取行业Excel表
	* @param path  
	*/
	public List<TSectorExcel> ReadSector(String fileName,String versionId,Long userId,List<String> msg,String outPath,Long templateId){  
		List<TSectorExcel> sectorList=new ArrayList<TSectorExcel>();
		//定义结果  默认false 
	    boolean isError=false;
		//获取文件名称
		String file=fileName.substring(fileName.lastIndexOf("/")+1);
		//初始化错误类型Map
		errorMap=new HashMap();
        try {  
        	String message=null;
        	StringBuilder mssg=new StringBuilder();
        	//执行Excel初始化
        	if(!init(fileName,mssg)){
        		msg.add(file+":"+mssg.toString());
        		return null;
        	}
        	//获取验证集合
        	checkMap=readCheckJson("应急系统新_4行业匹配.json");
        	if(checkMap==null){
        		msg.add(file+":读取系统内置校验文件出现异常!");
        		return null;
        	}
        	//获取字段验证Map
        	Map checkHeader=(Map)((Map)checkMap.get("应急系统新_4行业匹配")).get("sheet0");
        	//记录sheet页的Key 用来获取验证条件
        	List<String> keyList=new ArrayList<String>();
        	for(Object ch1:checkHeader.keySet()){
        		keyList.add(ch1.toString());
        	}
        	//获取sheet名称的长度验证
        	int sheetNameMaxLength=Integer.valueOf(((Map)checkHeader.get("sheetName")).get("maxLenth").toString());
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //sheet页验证
            if(sheetCount<1){
            	msg.add(file+":缺失sheet页!");
        		return null;
            }
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	//判断sheet名称的信息是否合格
            	if(sheetName.length()>sheetNameMaxLength){
            		//写入错误信息
					errorMap.put(0,file+":"+SHEETNAME_ERROR);
            	}
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
                    	//获取表头信息
                    	Map headerMap=readHeader(sheet);
                    	//定义临时变量
                    	int j=0;
                    	//循环所有的验证表头
                    	for(Object ch:checkHeader.keySet()){
                    		if(ch.toString().equals("sheetName")) continue;
                    		try{
                    			if(!headerMap.get(j).equals(ch)){
                    				//写入错误信息
                    				message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j)+"列的表头"+headerMap.get(j)+"和验证文件表头名称不匹配,验证文件表头名称为:"+ch+"!";
                            		Cell cell=row.getCell(j);
                            		cell=row.createCell((short) j);
                            		cell.setCellValue(getCellValue(cell));
                					cell.setCellStyle(yellowStyle);
                					cell.setCellComment(getComment(sheet, message));
                					//写入错误信息
                					errorMap.put(1,file+":表头和验证文件表头名称不匹配!");
                    			}
                    		}catch(Exception e){
                    			//写入错误信息
                    			message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j+1)+"列的表头缺失,验证文件表头名称为:"+ch+"!";
                    			Cell cell=row.getCell(j);
                    			cell=row.createCell((short) j);
                        		cell.setCellValue(getCellValue(cell));
            					cell.setCellStyle(yellowStyle);
            					cell.setCellComment(getComment(sheet, message));
            					//写入错误信息
            					errorMap.put(2,file+":表头缺失!");
                    		}
                    		//临时变量迭代 获取下一个表头信息
                    		j++;
                    	}
                    	continue;
                    }
                    
                    //定义中间验证Map
                    Map tempMap=null;
                    //定义中间单元格对象
                    Cell cell=null;
                    //单元格的验证错误信息
                    List<String> cellError=new ArrayList();
                    //记录单元格信息
                    String cellValue="";
                    for(int k=0;k<keyList.size()-1;k++){
                    	//获取单元格信息
                        cell=row.getCell(k);
                        //获取验证条件
                        tempMap=(Map)checkHeader.get(keyList.get(k));
                        //先获取Excel中属性
                        cellValue=getCellValue(cell);
                        //单元格的验证错误信息
                        cellError=new ArrayList();
                        //执行单元格验证方法
                        checkCell(file, sheetName, row.getRowNum()+1,k, tempMap, cellError, cellValue);
                        //判断是否出现错误
                        if(cellError.size()>0){
                        	cell=row.createCell((short) k);
                    		cell.setCellValue(cellValue);
        					cell.setCellStyle(yellowStyle);
        					message="";
        					for(int u=0;u<cellError.size();u++){
        						message+=(u+1)+":"+cellError.get(u)+"\n";
        					}
        					cell.setCellComment(getComment(sheet, message));
                        }
                    }
                    //循环错误信息的Map,记录错误信息
                	if(errorMap.size()>0){
                		isError=true;
                		continue;
                	}
                	//保存行业对象
                    TSectorExcel sector=new TSectorExcel();
                    //获取L4s的值
                    String nameL4s=getCellValue(row.getCell(1))+"-"+getCellValue(row.getCell(3))+"-"+getCellValue(row.getCell(5))+"-"+getCellValue(row.getCell(7)); 
                    sector.setSectorExcelL4s(nameL4s);
                    //获取行业名称
                    Cell cellName=row.getCell(9);
                    String sectorName= getCellValue(cellName);
                    sector.setSectorExcelName(sectorName);
                    //写入公式
                    sector.setId(getCellValue(row.getCell(0)));
                    //写入id0
                    sector.setId0(getCellValue(row.getCell(1)));
                    //写入id0name
                    sector.setId0name(getCellValue(row.getCell(2)));
                    //写入id1
                    sector.setId1(getCellValue(row.getCell(3)));
                    //写入id1name
                    sector.setId1name(getCellValue(row.getCell(4)));
                    //写入id2
                    sector.setId2(getCellValue(row.getCell(5)));
                    //写入id2name
                    sector.setId2name(getCellValue(row.getCell(6)));
                    //写入id3
                    sector.setId3(getCellValue(row.getCell(7)));
                    //写入id3name
                    sector.setId3name(getCellValue(row.getCell(8)));
                    //写入Group1
                    sector.setGroup1(getCellValue(row.getCell(10)));
                    //写入Group2
                    sector.setGroup2(getCellValue(row.getCell(11)));
                    //写入Group3
                    sector.setGroup3(getCellValue(row.getCell(12)));
                    //写入Tag
                    sector.setTag(getCellValue(row.getCell(13)));
                    //写入版本等信息
                    sector.setVersionExcelId(versionId);
                    sector.setUserId(userId);
                    sector.setDetailedListId(templateId);
                    sector.setDetailedListType("本地清单");
                    sectorList.add(sector);
                }  
            }
            //如果出现错误就写入错误信息 并创建输出流用来存放验证
            if(isError){
            	for(Object key:errorMap.keySet()){
        			msg.add(errorMap.get(key).toString());
        		}
            	File filePath =new File(outPath);
            	os = new FileOutputStream(filePath);
                LogUtil.getLogger().error("ExcelToDateController 校验行业匹配Excel表异常!");
                return null;
            }else{
            	//删除验证Excel的结果文件
                if(!delete(outPath)){
            		LogUtil.getLogger().error("ExcelToDateController 删除验证行业匹配Excel异常!");
                    return null;
            	}
            }
            
            LogUtil.getLogger().info("ExcelToDateController 获取行业匹配Excel成功!");
            return sectorList;
        } catch (Exception e) {  
        	LogUtil.getLogger().error("ExcelToDateController 获取行业匹配Excel异常!",e);
            return null;
        } finally{
        	try{
        		if(is!=null) is.close();
        		if(os!=null){
        			wb.write(os);
        			os.close();
        		} 
        		if(wb!=null) wb.close();
        	}catch(Exception e){
        		LogUtil.getLogger().error("ExcelToDateController 关闭流异常!",e);
        	}
        }  
    }  
	
	
	
   /**  TODO
	* 清单数据验证
	* 读取excel数据   
	* @param path  
	*/
	public boolean CheckNative(String fileName,List<String> msg,String outPath){  
		//定义结果  默认false 
	    boolean isError=false;
		//获取文件名称
		String file=fileName.substring(fileName.lastIndexOf("/")+1);
		//初始化错误类型Map
		errorMap=new HashMap();
        try {  
        	String message=null;
        	StringBuilder mssg=new StringBuilder();
        	//执行Excel初始化
        	if(!init(fileName,mssg)){
        		msg.add(file+":"+mssg.toString());
        		return false;
        	}
        	//获取验证集合
        	checkMap=readCheckJson("应急系统新_3清单数据.json");
        	if(checkMap==null){
        		msg.add(file+":读取系统内置校验文件出现异常!");
        		return false;
        	}
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //sheet页验证
            if(sheetCount<1){
            	msg.add(file+":缺失sheet页!");
        		return false;
            }
            if(checkMap.size()!=sheetCount){
            	msg.add(file+":sheet页和行业描述的sheet页不匹配!");
        		return false;
            }
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获取字段验证Map
            	Map checkHeader=(Map)checkMap.get("sheet"+i);
            	//记录sheet页的Key 用来获取验证条件
            	List<String> keyList=new ArrayList<String>();
            	for(Object ch1:checkHeader.keySet()){
            		keyList.add(ch1.toString());
            	}
            	//获取sheet名称的长度验证
            	int sheetNameMaxLength=Integer.valueOf(((Map)checkHeader.get("sheetName")).get("maxLenth").toString());
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	//判断sheet名称的信息是否合格
            	if(sheetName.length()>sheetNameMaxLength){
            		//写入错误信息
					errorMap.put(0,file+":"+SHEETNAME_ERROR);
            	}
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
                    	//获取表头信息
                    	Map headerMap=readHeader(sheet);
                    	//定义临时变量
                    	int j=0;
                    	//循环所有的验证英文表头
                    	for(Object ch1:checkHeader.keySet()){
                    		String ch=ch1.toString().split(",")[0];
                    		if(ch.toString().equals("sheetName")) continue;
                    		try{
                    			if(!headerMap.get(j).equals(ch)){
                    				//写入错误信息
                    				message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j)+"列的表头"+headerMap.get(j)+"和验证文件表头名称不匹配,验证文件表头名称为:"+ch+"!";
                            		Cell cell=row.getCell(j);
                            		cell=row.createCell((short) j);
                            		cell.setCellValue(getCellValue(cell));
                					cell.setCellStyle(yellowStyle);
                					cell.setCellComment(getComment(sheet, message));
                					//写入错误信息
                					errorMap.put(1,file+":表头和验证文件表头名称不匹配!");
                    			}
                    		}catch(Exception e){
                    			//写入错误信息
                    			message=file+":"+sheetName+"工作簿下的第1行"+columnChar(j+1)+"列的表头缺失,验证文件表头名称为:"+ch+"!";
                    			Cell cell=row.getCell(j);
                    			cell=row.createCell((short) j);
                        		cell.setCellValue(getCellValue(cell));
            					cell.setCellStyle(yellowStyle);
            					cell.setCellComment(getComment(sheet, message));
            					//写入错误信息
            					errorMap.put(2,file+":表头缺失!");
                    		}
                    		//临时变量迭代 获取下一个表头信息
                    		j++;
                    	}
                    	continue;
                    }
                    //如果是第二行 则是中文表头
                    if(row.getRowNum()==1){
                    	//获取表头信息
                    	Map headerMap=new HashMap();
                    	String[] headers=obtainRow(sheet,1);
                    	for (int j = 0; j < headers.length; j++)
                    		headerMap.put(j, headers[j]);
                    	//定义临时变量
                    	int j=0;
                    	//循环所有的验证表头
                    	for(Object ch1:checkHeader.keySet()){
                    		if(ch1.toString().equals("sheetName")) continue;
                    		String ch=ch1.toString().split(",")[1];
                    		try{
                    			if(!headerMap.get(j).equals(ch)){
                    				//写入错误信息
                    				message=file+":"+sheetName+"工作簿下的第2行"+columnChar(j)+"列的中文表头"+headerMap.get(j)+"和验证文件表头名称不匹配,验证文件表头名称为:"+ch+"!";
                            		Cell cell=row.getCell(j);
                            		cell=row.createCell((short) j);
                            		cell.setCellValue(getCellValue(cell));
                					cell.setCellStyle(yellowStyle);
                					cell.setCellComment(getComment(sheet, message));
                					//写入错误信息
                					errorMap.put(1,file+":表头和验证文件表头名称不匹配!");
                    			}
                    		}catch(Exception e){
                    			//写入错误信息
                    			message=file+":"+sheetName+"工作簿下的第2行"+columnChar(j+1)+"列的中文表头缺失,验证文件表头名称为:"+ch+"!";
                    			Cell cell=row.getCell(j);
                    			cell=row.createCell((short) j);
                        		cell.setCellValue(getCellValue(cell));
            					cell.setCellStyle(yellowStyle);
            					cell.setCellComment(getComment(sheet, message));
            					//写入错误信息
            					errorMap.put(2,file+":表头缺失!");
                    		}
                    		//临时变量迭代 获取下一个表头信息
                    		j++;
                    	}
                    	continue;
                    }
                    //定义中间验证Map
                    Map tempMap=null;
                    //定义中间单元格对象
                    Cell cell=null;
                    //单元格的验证错误信息
                    List<String> cellError=new ArrayList();
                    //记录单元格信息
                    String cellValue="";
                    
                    for(int k=0;k<keyList.size()-1;k++){
                    	//获取单元格信息
                        cell=row.getCell(k);
                        //获取验证条件
                        tempMap=(Map)checkHeader.get(keyList.get(k));
                        //先获取Excel中属性
                        cellValue=getCellValue(cell);
                        //单元格的验证错误信息
                        cellError=new ArrayList();
                        //执行单元格验证方法
                        checkCell(file, sheetName, row.getRowNum()+1,k, tempMap, cellError, cellValue);
                        //判断是否出现错误
                        if(cellError.size()>0){
                        	cell=row.createCell((short) k);
                    		cell.setCellValue(cellValue);
        					cell.setCellStyle(yellowStyle);
        					message="";
        					for(int u=0;u<cellError.size();u++){
        						message+=(u+1)+":"+cellError.get(u)+"\n";
        					}
        					cell.setCellComment(getComment(sheet, message));
                        }
                    }
                    //循环错误信息的Map,记录错误信息
                	if(errorMap.size()>0){
                		isError=true;
                		continue;
                	}
                	
                }  
            }
            //如果出现错误就写入错误信息 并创建输出流用来存放验证
            if(isError){
            	for(Object key:errorMap.keySet()){
        			msg.add(errorMap.get(key).toString());
        		}
            	File filePath =new File(outPath);
            	os = new FileOutputStream(filePath);
                LogUtil.getLogger().error("ExcelToDateController 校验清单数据Excel表异常!");
                return false;
            }else{
            	//删除验证Excel的结果文件
                if(!delete(outPath)){
            		LogUtil.getLogger().error("ExcelToDateController 删除校验清单数据Excel异常!");
                    return false;
            	}
            }
            LogUtil.getLogger().info("ExcelToDateController 校验清单数据Excel成功!");
            return true;
        } catch (Exception e) {  
        	LogUtil.getLogger().error("ExcelToDateController 校验清单数据Excel异常!",e);
            return false;
        } finally{
        	try{
        		if(is!=null) is.close();
        		if(os!=null){
        			wb.write(os);
        			os.close();
        		} 
        		if(wb!=null) wb.close();
        	}catch(Exception e){
        		LogUtil.getLogger().error("ExcelToDateController 关闭流异常!",e);
        	}
        }  
    }  
	
	
	
	
	/**  
	 * 措施Excel表
	* 读取excel数据   读取措施Excel表
	* @param path  
	*/
	public List<TMeasureExcel> ReadMeasure(String fileName,Object versionId,Long userId){  
		String path="D:\\svn_ampc\\docs\\02.应急系统设计文档\\03.措施设计\\measure_sets_ywjv5_YQ9 _CH1_XL2.xlsx";
		List<TMeasureExcel> measureList=new ArrayList<TMeasureExcel>();
        try {  
            Workbook wb  = null;  
            //自动根据Excel版本创建对应的Workbook
            wb = WorkbookFactory.create(new File(path));  
        	//获得当前页
        	Sheet sheet = wb.getSheetAt(0);  
        	//获得所有行
        	Iterator<Row> rows = sheet.rowIterator(); 
        	//循环所有行
        	while (rows.hasNext()) {  
                Row row = rows.next();  //获得行数据  
                //System.out.println("Row #" + row.getRowNum());  //获得行号从0开始  
                if(row.getRowNum()==0){
                	continue;
                }
                TMeasureExcel measure=new TMeasureExcel();
                //获取措施名称
                measure.setMeasureExcelName(getCellValue(row.getCell(0)));
                //获取措施检测
                String debug=getCellValue(row.getCell(1));
                measure.setMeasureExcelDebug(Long.parseLong(debug.substring(0,debug.indexOf('.'))));
                //获取显示信息
                measure.setMeasureExcelDisplay(getCellValue(row.getCell(2)));
                //获取措施类型信息
                measure.setMeasureExcelType(getCellValue(row.getCell(3)));
                //获取措施等级信息
                String level=getCellValue(row.getCell(4));
                measure.setMeasureExcelLevel(level);
                //获取措施L4s过滤信息
                measure.setMeasureExcelL4s(getCellValue(row.getCell(5)));
                //获取措施OP信息
                measure.setMeasureExcelOp(getCellValue(row.getCell(6)));
                //获取措施A信息
                String a=getCellValue(row.getCell(7));
                if(a!=null&&!a.equals("")){
                	measure.setMeasureExcelA(a);
                	//获取措施A中文信息
                	measure.setMeasureExcelAname(getCellValue(row.getCell(14)));
                	//获取措施A的取值范围
                	measure.setMeasureExcelArange(getCellValue(row.getCell(21)));
                }
                //获取措施A1信息
                String a1=getCellValue(row.getCell(8));
                if(a1!=null&&!a1.equals("")){
                	measure.setMeasureExcelA1(a1);
                	//获取措施A1中文信息
                	measure.setMeasureExcelA1name(getCellValue(row.getCell(15)));
                	//获取措施A1的取值范围
                	measure.setMeasureExcelA1range(getCellValue(row.getCell(22)));
                }
                //获取措施Intensity信息
                String Intensity=getCellValue(row.getCell(9));
                if(Intensity!=null&&!Intensity.equals("")){
                	measure.setMeasureExcelIntensity(Intensity);
                	//获取措施Intensity中文信息
                	measure.setMeasureExcelIntensityname(getCellValue(row.getCell(16)));
                	//获取措施Intensity的取值范围
                	measure.setMeasureExcelIntensityrange(getCellValue(row.getCell(23)));
                }
                //获取措施Intensity1信息
                String Intensity1=getCellValue(row.getCell(10));
                if(Intensity1!=null&&!Intensity1.equals("")){
                	measure.setMeasureExcelIntensity(Intensity1);
                	//获取措施Intensity1中文信息
                	measure.setMeasureExcelIntensity1name(getCellValue(row.getCell(17)));
                	//获取措施Intensity1的取值范围
                	measure.setMeasureExcelIntensity1range(getCellValue(row.getCell(24)));
                }
                //获取措施ash信息
                String ash=getCellValue(row.getCell(11));
                if(ash!=null&&!ash.equals("")){
                	measure.setMeasureExcelAsh(ash);
                	//获取措施ash中文信息
                	measure.setMeasureExcelAshname(getCellValue(row.getCell(18)));
                	//获取措施ash取值范围
                	measure.setMeasureExcelAshrange(getCellValue(row.getCell(25)));
                }
                //获取措施sulfer信息
                String sulfer=getCellValue(row.getCell(12));
                if(sulfer!=null&&!sulfer.equals("")){
                	measure.setMeasureExcelSulfer(sulfer);
                	//获取措施sulfer中文信息
                	measure.setMeasureExcelSulfername(getCellValue(row.getCell(19)));
                	//获取措施sulfer取值范围
                	measure.setMeasureExcelSulferrange(getCellValue(row.getCell(26)));
                }
                //获取措施sv信息
                String sv=getCellValue(row.getCell(13));
                if(sv!=null&&!sv.equals("")){
                	measure.setMeasureExcelSv(sv);
                	//获取措施sv中文信息
                	measure.setMeasureExcelSvname(getCellValue(row.getCell(20)));
                	//获取措施sv取值范围
                	measure.setMeasureExcelSvrange(getCellValue(row.getCell(27)));
                }
                //写入版本等信息
                measure.setMeasureExcelVersion(versionId);
                measure.setUserId(userId);
                measure.setDetailedListId(1L);
                measure.setDetailedListType("本地清单");
                measureList.add(measure);
            }  
        	LogUtil.getLogger().info("ExcelToDateController 读取措施数据Excel成功!");
            return measureList;
        } catch (Exception e) {  
        	LogUtil.getLogger().error("ExcelToDateController 读取措施数据Excel异常!",e);
            return null;
        }  
    }  
	/**   
	* 获取单元格的值   
	* @param cell   
	* @return   
	*/    
	public static String getCellValue(Cell cell){
		if(cell == null) return "";
		switch (cell.getCellType()) {   //根据cell中的类型来输出数据  
	        case HSSFCell.CELL_TYPE_NUMERIC:
	        	return String.valueOf(cell.getNumericCellValue()).trim();
	        case HSSFCell.CELL_TYPE_STRING:
	        	return cell.getStringCellValue().trim();
	        case HSSFCell.CELL_TYPE_BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue()).trim();
	        case HSSFCell.CELL_TYPE_FORMULA:
	            return cell.getCellFormula().trim();
	        default:
	            return "";
		}
	}
	

}
