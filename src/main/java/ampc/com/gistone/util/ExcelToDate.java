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

import ampc.com.gistone.database.model.TMeasureExcel;
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
                    	BigDecimal bash=new BigDecimal(ash);
                    	measure.setMeasureExcelAsh(bash);
                    }
                    //获取措施sulfer信息
                    String sulfer=getCellValue(row.getCell(13));
                    if(sulfer!=null&&!sulfer.equals("")){
                    	BigDecimal bsulfer=new BigDecimal(sulfer);
                    	measure.setMeasureExcelSulfer(bsulfer);
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
	/**
	 * 定义默认颜色帮助类
	 */
	public static List<ColorUtil> getColor(){
		List<ColorUtil> list=new ArrayList<ColorUtil>();
		ColorUtil color=null;
		color=new ColorUtil("f0f8ff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("00ced1","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("556b2f","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("8fbc8f","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("8b0000","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("bdb76b","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("483d8b","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("a9a9a9","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("a9a9a9","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("696969","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("b8860b","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ff8c00","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("00008b","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("6400","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("008b8b","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("e9967a","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("dc143c","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("2f4f4f","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("8b008b","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("9400d3","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("9932cc","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ffebcd","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f5fffa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("7fffd4","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("afeeee","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("98fb98","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("eee8aa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("db7093","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("7cfc00","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("d2b48c","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("32cd32","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffa500","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("00ff7f","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("dcdcdc","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fff0f5","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("e6e6fa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("da70d6","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("4b0082","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ffefd5","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffc0cb","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("b0e0e6","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("808000","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("4682b4","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("faebd7","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fff5ee","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("80","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("2e8b57","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("bc8f8f","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("a52a2a","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ff4500","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ff0000","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("ff00ff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fffaf0","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("4169e1","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("f0e68c","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("7fff00","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("adff2f","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("9acd32","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffff00","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("808080","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("708090","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("b22222","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("d8bfd8","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("daa520","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffd700","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffe4c4","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("6495ed","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("5f9ea0","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("0000ff","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("fdf5e6","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffb6c1","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("b0c4de","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("20b2aa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("d3d3d3","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("778899","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("add8e6","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("90ee90","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("e0ffff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffa07a","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f08080","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("87cefa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffe4b5","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("8000","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("fff8dc","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f5f5dc","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("cd853f","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f0fff0","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffdead","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fffacd","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f5deb3","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("00ffff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffe4e1","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("d2691e","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("40e0d0","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("00ffff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ff69b4","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("228b22","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("f4a460","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ff7f50","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("1e90ff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ff1493","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("6b8e23","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("00bfff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("6a5acd","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("deb887","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("8080","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("800000","btn-cuoshi-shen");
		list.add(color);
		color=new ColorUtil("00ff00","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ffdab9","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("f0ffff","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("87ceeb","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("ff6347","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fa8072","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("fffafa","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		color=new ColorUtil("","btn-cuoshi-qian");
		list.add(color);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return list;
	}
}
