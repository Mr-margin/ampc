package ampc.com.gistone.util.checkExcel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class CreateVerifyFileUtil {

    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel

    public List<ValidateModel> createVerifyFile(String verifyExcelPath,String validateName) throws Exception{

        List<ValidateModel> validateList = new ArrayList<ValidateModel>();

        InputStream in = new FileInputStream(verifyExcelPath);
        //创建Excel工作薄
        Workbook work = this.getWorkbook(in,verifyExcelPath);
        if(null == work){
            throw new Exception("创建Excel工作薄为空;");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        Map<String,Map<String,List<String>>> validateMap = new LinkedHashMap<String,Map<String,List<String>>>();

        StringBuffer sb = new StringBuffer();

        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            Map<String,List<String>> headMap = new LinkedHashMap<String,List<String>>();
            String sheetName = work.getSheetName(i);
            if (!validateMap.containsKey(sheetName)){
                validateMap.put(sheetName,headMap);
            }
            sheet = work.getSheetAt(i);
            List<Object> headerName = new ArrayList<Object>();
            if(sheet==null){continue;}

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if(row==null){continue;}        //||row.getFirstCellNum()==j 在if里加上这句意思是不读表头（第一行）

                //遍历所有的列
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    String cellValue = String.valueOf(this.getCellValue(cell)).trim();

                    if(j == sheet.getFirstRowNum()){
                        if (!"".equals(cellValue)&&cellValue!=null){
                            headMap.put(cellValue,new ArrayList<String>());
                            headerName.add(cellValue);
                        }
                    }else{
                        if (cellValue != null){
                            if (cellValue.contains("~")){
                                String[] strArr = cellValue.split("~");
                                try {
                                    if (strArr.length==2){
                                        if (strArr[0]!=null&&!"".equals(strArr[0])){
                                            Double.parseDouble(strArr[0]);
                                        }
                                        if (strArr[1]!=null&&!"".equals(strArr[1])){
                                            Double.parseDouble(strArr[1]);
                                        }
                                    }else if (strArr.length == 1){
                                        if (strArr[0]!=null&&!"".equals(strArr[0])){
                                            Double.parseDouble(strArr[0]);
                                        }
                                    }

                                }catch (NumberFormatException e){
                                    sb.append(sheetName+"页的第"+(j+1)+"行第"+headerName.get(y)+"列有非数字出现;");
                                }
                            }

                            if (y<headerName.size()){
                                if (headMap.containsKey(headerName.get(y))){
                                    headMap.get(headerName.get(y)).add(cellValue.trim().toLowerCase());
                                }
                            }
                        }


                    }
                }
            }
        }
        if (sb.length()>0){
            throw new Exception(sb.toString());
        }
        Set sheetName = validateMap.keySet();
        for (Object sName : sheetName){
            Set headName = validateMap.get(sName).keySet();
            for (Object hName : headName){
                ValidateModel validateModel = new ValidateModel();
                validateModel.setSheetName(String.valueOf(sName));
                validateModel.setHeadName(String.valueOf(hName));

                validateModel.setValidateName(validateName);
                if (validateMap.get(sName).get(hName).size()>0){

                    List<String> strings = validateMap.get(sName).get(hName);
                    String validators = strings.stream().collect(Collectors.joining(","));
                    validateModel.setValidators(validators);
                }
                validateList.add(validateModel);
            }
        }
        return validateList;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    public  Object getCellValue(Cell cell){
        Object value = null;
        DecimalFormat df = new DecimalFormat("########.###########");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        int type = 0;
        try{
            type = cell.getCellType();
        }catch (Exception e){
            type = 6;       //没有6这个类型,即excel表格中数据时空的！
        }
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if("General".equals(cell.getCellStyle().getDataFormatString())){
                    value = df.format(cell.getNumericCellValue());
                }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                    value = sdf.format(cell.getDateCellValue());
                }else{
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = null;
                break;
            default:
                value = null;
                break;
        }
        return value;
    }


}
