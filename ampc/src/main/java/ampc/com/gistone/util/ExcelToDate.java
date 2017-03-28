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
import ampc.com.gistone.database.model.TQueryExcel;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.database.model.TSectordocExcel;

/**
 * Excel解析帮助类
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月9日
 */
public class ExcelToDate {
	
	/**  
	 *行业描述Excel表
	* 读取excel描述数据   读取行业描述Excel表
	* @param path  
	*/
	public static List<TSectordocExcel> ReadSectorDOC(String fileName,Long versionId,Long userId){  
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\07.行业划分和筛选条件\\应急系统新_1描述文件.xlsx";
		List<TSectordocExcel> sectorDocList=new ArrayList<TSectordocExcel>();
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
		String path="E:\\项目检出\\curr\\docs\\02.应急系统设计文档\\03.措施设计\\measure_sets_ywjv5_YQ4.xlsx";
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
	
}
