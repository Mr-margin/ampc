package ampc.com.gistone.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.extract.ExtractDataService;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;
@RestController
@RequestMapping
public class TestController {
	
	
	@RequestMapping("log/log4jTest")
	public static void log4jTest(){
		/**
		 * 成功的实例
		 */
		try{
			//执行成功是用该方法记录
			LogUtil.getLogger().info("执行成功");
		}catch(Exception e){
			LogUtil.getLogger().error("异常了",e);
		}
		/**
		 * 警告的实例
		 * 在不影响程序的继续进行时使用该方法记录
		 */
		try{
			int i=1,j=2;
			if((i+j)==2)LogUtil.getLogger().info("执行成功");
			throw new Exception("计算错误");
		}catch(Exception e){
			//出现警告进行记录   在不影响程序的继续进行时使用该方法记录
			LogUtil.getLogger().warn("警告",e);
		}
		/**
		 * 异常的实例
		 * 在影响了结果的显示时使用该方法记录
		 */
		try{
			String[] a=null;
			System.out.println(a[1]);
			LogUtil.getLogger().info("执行成功");
		}catch(Exception e){
			//出现异常进行记录  在影响了结果的显示时使用该方法记录
			LogUtil.getLogger().error("异常了",e);
		}
	}
	
	@RequestMapping("ex/ex")
	public static void ReadMeasure(){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\07.行业划分和筛选条件\\123.xlsx";
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
                System.out.println(getCellValue(row.getCell(0)));
                System.out.println(getCellValue(row.getCell(1)));
                System.out.println(getCellValue(row.getCell(2)));
                System.out.println(getCellValue(row.getCell(3)));
                System.out.println(getCellValue(row.getCell(4)));
                System.out.println(getCellValue(row.getCell(5)));
            }  
        } catch (Exception ex) {  
            ex.printStackTrace();
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
	
	public static Object sssss() throws IOException{
		String path="C:\\Users\\Administrator\\Desktop\\result.json";
		ObjectMapper mapper=new ObjectMapper();
		JsonNode js=mapper.readTree(new File(path));
		
		return js;
	}
	
	
}
