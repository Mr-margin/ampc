package ampc.com.gistone.util.checkExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestMapping;

import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TSectordocExcel;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;

/**
 * Excel解析帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月9日
 */
public class ExcelToDate {
	
	//工作簿对象
	public static Workbook wb  = null;  
	//得到一个POI的工具类
	public static CreationHelper factory;
	public static ClientAnchor anchor =null;
	//用来构建Common的map
	public static LinkedHashMap drawMap;
	//错误信息
	public static ItemError itemError=null;
	//定义输入流
	public static InputStream is=null;
	//定义输出流
    public static OutputStream os=null;
    //黄色背景
	public static CellStyle yellowStyle;
    //定义结果  默认false 
    public static boolean isError=false;
    //存放验证信息的Map
    public static LinkedHashMap checkMap=null;	
	/**
	 * 根据jsonName解析对应的校验规则
	 * @param jsonName 校验规则文件
	 * @return 校验规则Map
	 */
	public static LinkedHashMap readCheckJson(String jsonName){  
		try {
			//获取校验文件
			File directory = new File("");
			String path= directory.getCanonicalPath()+"\\src\\main\\resources\\"+jsonName;
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
	  * 生成标注
	  */
	public static Comment getComment(Sheet sheet, String message) {
	  Drawing drawing = (Drawing)drawMap.get(sheet.getSheetName());
	  ClientAnchor an = drawing.createAnchor(0, 0, 0, 0, (short) 1, 2, (short) 3, 10);
	  Comment comment0 = drawing.createCellComment(an);
	  RichTextString str0 = factory.createRichTextString(message);
	  comment0.setString(str0);
	  comment0.setAuthor("Apache POI");
	  return comment0;
	}
	
	
	
    /**
     * 初始化Excel信息方法
     * @param path 要解析的路径
     */
	public static boolean init(String path){
		try{
			File file =new File(path);
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
			return false;
		}
	}
	
	
	/**
	  * 保存Excel
	  * @param path
	  * @throws Exception
	  */
	public static void save(String path) throws Exception {
	    FileOutputStream out = new FileOutputStream(path);
	    wb.write(out);
	    out.close();
	}
	/**
    * 读取表头
    * @param sheet
    * @return
    */
	public static Map readHeader(Sheet sheet) {
	    //设置表头
	    String[] headers = obtainRow(sheet, 0);
	    //存放表头顺序
	    Map m = new LinkedHashMap<>();
	    for (int j = 0; j < headers.length; j++)
	      m.put(headers[j], j + 1);
	    return m;
	}
	
	 /**
	  *将列传换成英文字母   相当于Excel中的列名 如:1-A 2-B
	  */
    public static String columnChar(int num) {
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
    * 获取行数据
    * 获取字符串数组类型的行数据
    * @param sheet  sheet页
    * @param rowNum 行数
    * @return 字符串数据数组
    */
	public static String[] obtainRow(Sheet sheet, int rowNum) {
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
	*行业描述Excel表
	* 读取excel描述数据   读取行业描述Excel表
	* @param path  
	*/
	public static List<TSectordocExcel> ReadSectorDOC(String fileName,Long versionId,Long userId,String msg){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\07.行业划分和筛选条件\\应急系统新_1描述文件.xlsx";
		//返回结果的集合
		List<TSectordocExcel> sectorDocList=new ArrayList<TSectordocExcel>();
        try {  
        	//执行Excel初始化
        	if(!init(path)){
        		msg="初始化Excel失败!";
        		return null;
        	}
        	//获取验证集合
        	checkMap=readCheckJson("应急系统新_1描述文件.json");
        	if(checkMap==null){
        		msg="获取Excel验证失败!";
        		return null;
        	}
        	//获取sheet名称的长度验证
        	int sheetNameMaxLength=Integer.valueOf(((Map)((Map)checkMap.get("应急系统新_1描述文件")).get("sheetName")).get("maxLenth").toString());
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i); 
            	//获取sheet名称
            	String sheetName=sheet.getSheetName();
            	//判断sheet名称的信息是否合格
            	if(sheetName.length()>sheetNameMaxLength){
            		
            	}
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
                    	Map headerMap=readHeader(sheet);
                    	
                    	
                    	continue;
                    }
                    String t=getCellValue(row.getCell(4));
                    //只保留属性为 统计 和 筛选和统计 的信息
                    if(t.equals("统计")||t.equals("筛选和统计")){
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
                        sectorDoc.setUserId(userId);
                        sectorDocList.add(sectorDoc);
                    }
                }  
            }
            return sectorDocList;
        } catch (Exception ex) {  
            ex.printStackTrace();
            return null;
        }  
    }  
	
	
	/**  
	 * 筛选条件 Excel
	* 读取excel筛选逻辑数据   读取筛选逻辑Excel表
	* @param path  
	*/
	public static List<TQueryExcel> ReadQuery(String fileName,Long versionId,Long userId){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\07.行业划分和筛选条件\\应急系统新_2筛选逻辑.xlsx";
		List<TQueryExcel> queryList=new ArrayList<TQueryExcel>();
        try {  
            Workbook wb  = null;  
            //自动根据Excel版本创建对应的Workbook
            wb = WorkbookFactory.create(new File(path));  
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i);  
            	String sheetName=sheet.getSheetName();
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
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
                    query.setUserId(userId);
                    queryList.add(query);
                }  
            }
            return queryList;
        } catch (Exception ex) {  
            ex.printStackTrace();
            return null;
        }  
    }  
	
	
	
	
	
	
	
	/**  
	 *行业Excel表
	* 读取excel数据   读取行业Excel表
	* @param path  
	*/
	public static List<TSectorExcel> ReadSector(String fileName,String versionId,Long userId){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\07.行业划分和筛选条件\\应急系统新_4行业匹配.xlsx";
		List<TSectorExcel> sectorList=new ArrayList<TSectorExcel>();
        try {  
            Workbook wb  = null;  
            //自动根据Excel版本创建对应的Workbook
            wb = WorkbookFactory.create(new File(path));  
            //获得所有页数
            int sheetCount=wb.getNumberOfSheets();
            //循环每一页
            for(int i=0;i<sheetCount;i++){
            	//获得当前页
            	Sheet sheet = wb.getSheetAt(i);  
            	//获得所有行
            	Iterator<Row> rows = sheet.rowIterator(); 
            	//循环所有行
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    if(row.getRowNum()==0){
                    	continue;
                    }
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
                    //写入版本等信息
                    sector.setVersionExcelId(versionId);
                    sector.setUserId(userId);
                    sectorList.add(sector);
                }  
            }
            return sectorList;
        } catch (Exception ex) {  
            ex.printStackTrace();
            return null;
        }  
    }  
	/**  
	 * 措施Excel表
	* 读取excel数据   读取措施Excel表
	* @param path  
	*/
	public static List<TMeasureExcel> ReadMeasure(String fileName,Object versionId,Long userId){  
		String path="D:\\AMPC_SVN\\docs\\02.应急系统设计文档\\03.措施设计\\measure_sets_ywjv5_YQ8.xlsx";
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
                measureList.add(measure);
            }  
            return measureList;
        } catch (Exception ex) {  
            ex.printStackTrace();
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
	
//	/**
//	   * 清空错误信息的方法
//	   */
//	  private void clearErrorInfo() {
//	    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
//	      Sheet sheet = wb.getSheetAt(i);
//	      for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
//	        Row row = sheet.getRow(j);
//	        if (null == row) continue;
//	        for (int k = 0; k < row.getLastCellNum(); k++) {
//	          Cell cell = row.getCell(k);
//	          if (null != cell) {
//	            if (null != cell.getCellComment()) {
//	              cell.removeCellComment();
//	              cell.setCellStyle(copyStyle(cell.getCellStyle()));
//	            }
//	          }
//	        }
//	      }
//	    }
//	  }
//	/**
//	   * 对缺失的表头进行标注
//	   * @param loseHeader
//	   */
//	  public void headerRemark(Map loseHeader) {
//	    Set s = loseHeader.keySet();
//	    for (Object k : s) {
//	      Sheet sheet = workbook.getSheet(k.toString());
//	      Map hm = (Map) headerMap.get(k.toString());
//	      List hs = (List) loseHeader.get(k);
//	      for (int i = 0; i < startRow - 1; i++) {
//	        Row row = sheet.getRow(i);
//	        for (Object h : hs) {
//	          int n = (int) hm.get(h) - 1;
//	          for (int j = n; j >= 0; j--) {
//	            Cell cell = row.getCell(j);
//	            if (null != cell) {
//	              cell.setCellStyle(yellowStyle);
//	              cell.setCellComment(getComment(sheet, "补充缺失的表头"));
//	              break;
//	            }
//	          }
//	        }
//	      }
//	    }
//	  }
}