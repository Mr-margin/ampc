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
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.util.JsonUtil;

public class TestController {

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
