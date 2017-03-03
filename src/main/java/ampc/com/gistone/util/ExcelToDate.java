package ampc.com.gistone.util;

import java.io.File;
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
            		TSectorExcel sector=new TSectorExcel();
                    Row row = rows.next();  //获得行数据  
                    //System.out.println("Row #" + row.getRowNum());  //获得行号从0开始  
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
	* 读取excel数据   读取措施表
	* @param path  
	*/
	public static void ReadMeasure(String fileName){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\03.措施设计\\measure_sets_QYv1.xlsx";
        try {  
            Workbook wb  = null;  
            wb = WorkbookFactory.create(new File(path));  
            //获得第一个表单  
            int s= wb.getNumberOfSheets();
            System.out.println(s);
            for(int i=0;i<s;i++){
            	Sheet sheet = wb.getSheetAt(i);  
            	Iterator<Row> rows = sheet.rowIterator(); 
            	while (rows.hasNext()) {  
                    Row row = rows.next();  //获得行数据  
                    System.out.println("Row #" + row.getRowNum());  //获得行号从0开始  
                    Iterator<Cell> cells = row.cellIterator();    //获得第一行的迭代器  
                    while (cells.hasNext()) {  
                        Cell cell = cells.next();  
                        System.out.println("Cell #" + cell.getColumnIndex());  
                        
                    }  
                }  
            }
            
            System.out.println("执行成功");
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
	        	return String.valueOf((int)cell.getNumericCellValue()).trim();
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
