package ampc.com.gistone.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TSector;
import ampc.com.gistone.database.model.TSectorExcel;


public class ExcelToDate {
	/**  
	* 读取excel数据   读取行业Excel表
	* @param path  
	*/
	public static List<TSectorExcel> ReadSector(String fileName,Long versionId,Long userId){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\03.措施设计\\sector_mapping.xlsx";
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
	* 读取excel数据   读取措施Excel表
	* @param path  
	*/
	public static List<TMeasureExcel> ReadMeasure(String fileName,Long versionId,Long userId){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\03.措施设计\\measure_sets_QYv1.xlsx";
		List<TMeasureExcel> measureList=new ArrayList<TMeasureExcel>();
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
                    //System.out.println("Row #" + row.getRowNum());  //获得行号从0开始  
                    if(row.getRowNum()==0){
                    	continue;
                    }
                    TMeasureExcel measure=new TMeasureExcel();
                    //获取措施名称
                    measure.setMeasureExcelName(getCellValue(row.getCell(1)));
                    //获取措施检测
                    String debug=getCellValue(row.getCell(2));
                    measure.setMeasureExcelDebug(Long.parseLong(debug.substring(0,debug.indexOf('.'))));
                    //获取显示信息
                    measure.setMeasureExcelDisplay(getCellValue(row.getCell(3)));
                    //获取措施类型信息
                    measure.setMeasureExcelType(getCellValue(row.getCell(4)));
                    //获取措施等级信息
                    String level=getCellValue(row.getCell(5));
                    measure.setMeasureExcelLevel(Long.parseLong(level.substring(0,level.indexOf('.'))));
                    //获取措施L4s过滤信息
                    measure.setMeasureExcelL4s(getCellValue(row.getCell(6)));
                    //获取措施OP信息
                    measure.setMeasureExcelOp(getCellValue(row.getCell(7)));
                    //获取措施A信息
                    String a=getCellValue(row.getCell(8));
                    if(a!=null&&!a.equals("")){
                    	BigDecimal ba=new BigDecimal(a);
                    	measure.setMeasureExcelA(ba);
                    }
                    //获取措施A1信息
                    String a1=getCellValue(row.getCell(9));
                    if(a1!=null&&!a1.equals("")){
                    	BigDecimal ba1=new BigDecimal(a1);
                    	measure.setMeasureExcelA1(ba1);
                    }
                    //获取措施Intensity信息
                    String Intensity=getCellValue(row.getCell(10));
                    if(Intensity!=null&&!Intensity.equals("")){
                    	BigDecimal bIntensity=new BigDecimal(Intensity);
                    	measure.setMeasureExcelIntensity(bIntensity);
                    }
                    //获取措施Intensity1信息
                    String Intensity1=getCellValue(row.getCell(11));
                    if(Intensity1!=null&&!Intensity1.equals("")){
                    	BigDecimal bIntensity1=new BigDecimal(Intensity1);
                    	measure.setMeasureExcelIntensity(bIntensity1);
                    }
                    //获取措施ash信息
                    String ash=getCellValue(row.getCell(12));
                    if(ash!=null&&!ash.equals("")){
                    	measure.setMeasureExcelAsh(Long.parseLong(ash.substring(0,ash.indexOf('.'))));
                    }
                    //获取措施sulfer信息
                    String sulfer=getCellValue(row.getCell(13));
                    if(sulfer!=null&&!sulfer.equals("")){
                    	measure.setMeasureExcelSulfer(Long.parseLong(sulfer.substring(0,sulfer.indexOf('.'))));
                    }
                    //获取措施sv信息
                    String sv=getCellValue(row.getCell(14));
                    if(sv!=null&&!sv.equals("")){
                    	measure.setMeasureExcelSv(sv);
                    }
                    //写入版本等信息
                    measure.setMeasureExcelVersion(versionId);
                    measure.setUserId(userId);
                    measureList.add(measure);
                }  
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
}
