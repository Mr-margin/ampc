package ampc.com.gistone.util.checkExcel;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ampc.com.gistone.util.excelUtil.ValidateExcel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wrx on 2016/12/31.
 */
public class ValidateExcelUtil {
    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel
    private static CellStyle cs;
    private static CellStyle cs2;
    private static CreationHelper factory;
    private static ClientAnchor anchor;
    private static Drawing drawing;
    private boolean result;

    /**
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象
     * @param in,fileName
     * @return
     * @throws IOException
     */

    public Map<String,List<Map<String, String>>> read(InputStream in,String fileName,List<ValidateExcel> verify
    ,String errorPath,int startRow) throws Exception{
        // 得到工作簿
        Workbook wb = getWorkbook(in,fileName);
        this.result = true;
        try {
            // 最终返回数据格式 Map<sheetName,List<Map<表头,单元格内的值>>>
            Map<String,List<Map<String, String>>> map = new HashMap<>();
            // 存储sheet与表头数据 Map<sheetName,Map<表头单元格的index,表头>>
            Map<String,Map<Integer,String>> mapHead = new HashMap<>();
            // 检验文件与模板的sheet、表头是否一致
            List<String> missSheet = checkExcel(wb,verify,mapHead);
            if (!missSheet.isEmpty()) {
                throw new Exception("-1" + missSheet.stream().collect(Collectors.joining(",")));      // -1表示模板有损坏，请重新下载正确的模板
            }
            // 将所有正确的sheetName取出来
            Set<String> sheetNameSet = verify.stream().map(ValidateExcel :: getSheetname).collect(Collectors.toSet());
            // 遍历所有正确的sheet,用户填写的其他sheet系统是不读取的
            for (String sheetName : sheetNameSet) {
                // 取出sheet
                Sheet sheet = wb.getSheet(sheetName);
                //得到一个换图的对象
                this.drawing = sheet.createDrawingPatriarch();
                // 得到表头 Map<表头单元格的index,表头>
                Map<Integer, String> headers = mapHead.get(sheetName);
                //遍历数据
                int lastRowNum = sheet.getLastRowNum();
                // List<Map<映射字段,单元格内的值>>
                List<Map<String, String>> list = new ArrayList<>();
                // 查找校验规则
                List<ValidateExcel> v = verify.stream().filter(e -> e.getSheetname().equals(sheetName))
                        .collect(Collectors.toList());

                for (int j = startRow; j <= lastRowNum; j++) {
                    Map<String, String> rowData = obtainRow(sheet, j, headers,v);
                    if (null == rowData) {
                        continue;
                    }
                    list.add(rowData);
                }
                map.put(sheet.getSheetName(), list);
            }

            if (result == false){
                throw new Exception("-2");  // -2：填写内容有错，请下载错误报告
            }
            return map;

        } catch (Exception e) {
            if (e.getMessage().equals("-2")) {
                FileOutputStream out = new FileOutputStream(errorPath);
                wb.write(out);
                out.close();
            }
            throw new Exception(e.getMessage());
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    /**
     * 检查上传文件与模板的结构(sheet,表头等信息)是否一致
     * @param wb
     * @param verify
     * @param map
     * @return
     */
    private List<String> checkExcel(Workbook wb, List<ValidateExcel> verify,Map<String,Map<Integer,String>> map) {
        List<String> missSheet = new ArrayList<>();
        // 取出所有正确的sheetName
        Set<String> sheetNameSet = verify.stream().map(ValidateExcel :: getSheetname).collect(Collectors.toSet());
        // 检验上传数据与模板结构是否一致
        for (String sheetName : sheetNameSet) {
            //设置表头
            Sheet sheet = wb.getSheet(sheetName);

            if (null == sheet) {
                missSheet.add(sheetName);
                continue;
            }
            map.put(sheetName,new HashMap<>());
            Set<String> headerSet = verify.stream().filter(e -> e.getSheetname().equals(sheetName))
                    .map(ValidateExcel::getHeadname).collect(Collectors.toSet());
            List<String> headers = obtainRow(sheet, 0);

            // TODO 下面验证表头是否完整可以再拓展 比如把缺失的表头补全，这个还是要根据自己的逻辑写
            boolean headFlag = false;
            for (String s : headerSet) {
                if (!headers.contains(s)) {
                    // TODO 如果需要补全表头可以在这里写
                    headFlag = true;
                    break;
                }else {
                    map.get(sheetName).put(headers.indexOf(s), s);
                }
            }
            if (headFlag) missSheet.add(sheetName+"中缺失部分表头");
        }
        return missSheet;
    }

    /**
     * 根据header数据获取一行数据的map
     *
     * @param sheet
     * @param rowNum
     * @param headers
     * @return
     */
    public Map<String, String> obtainRow(Sheet sheet, int rowNum, Map<Integer,String> headers,
                                         List<ValidateExcel> verify) {
        Row row = sheet.getRow(rowNum);
        if (null == row) {
            row = sheet.createRow(rowNum);
        }

        Map<String,String> validator = verify.stream().
                collect(Collectors.toMap(ValidateExcel :: getHeadname,ValidateExcel :: getValidators));
        Set<Integer> headIndex = headers.keySet();
        //存储表头,单元格内的值
        Map<String, String> map = new HashMap<>();

        for (Integer integer : headIndex) {
            Cell cell = row.getCell(integer);
            if (null == cell) {
                cell = row.createCell(integer);
            }
            String head = headers.get(integer);
            String s = getCellValue(cell);
            String validate = validator.get(head);
            validate(s, validate, cell,head);
            if (result){
                map.put(head, s);
            }
        }

        return map;
    }

    /**
     * 对单元格内容进行校验
     * @param value
     * @param validate
     * @param cell
     * @param head
     */
    private void validate(String value, String validate,Cell cell,String head) {
        // 取消颜色以及备注
        if (null != cell.getCellComment()) {
            cell.removeCellComment();
            cell.setCellStyle(cs2);
        }
        List<String> errorList = new ArrayList<>();
        String[] split = validate.split(",");
        for (String s : split) {
            if (s.equalsIgnoreCase("focus")){
                // TODO focus的规则需要根据项目业务自己写
                break;
            }else if (s.contains("~")) {
                try {
                    double doubleValue = Double.parseDouble(value);
                    // TODO 重写
                    boolean success = range(doubleValue,Double.parseDouble(s.split("~")[0])
                            ,Double.parseDouble(s.split("~")[1]));
                    if (success == false){
                        errorList.add("必须在"+s+"范围内");
                    }
                }catch (Exception e){
                    if (validate.contains("isfloat")) {
                        errorList.add("必须是浮点型数字且在"+s+"范围内");
                    }else if (validate.contains("isinteger")){
                        errorList.add("必须是整型数字且在"+s+"范围内");
                    }
                }
                break;
            }else if (s.equalsIgnoreCase("isfloat") || s.equalsIgnoreCase("isinteger")){
                setError(value,errorList,s);
                break;
            }else if (s.equalsIgnoreCase("notnull")){
                if (null == value || "".equals(value)){
                    errorList.add("不能为空");
                }
            }else if (s.equalsIgnoreCase("list")){  // TODO 这是验证下拉菜单的规则，需要根据自己的逻辑写

            }
        }
        if (!errorList.isEmpty()){
            writeErrorMessage(cell,errorList);
            this.result = false;
        }
    }

    private void writeErrorMessage(Cell cell,List<String> errorList) {
        cell.setCellStyle(cs);
        // 对这个单元格加上注解
        Comment comment0 = this.drawing.createCellComment(this.anchor);
        comment0.setAuthor("Admin");
        RichTextString str0 = this.factory.createRichTextString(errorList.stream().collect(Collectors.joining(",")));
        comment0.setString(str0);
        cell.setCellComment(comment0);
    }

    /**
     * 校验isfloat,isinteger的正确性
     * @param value
     * @param list
     * @param flag
     */
    public void setError(String value,List<String> list,String flag){
        try {
            if (flag.equalsIgnoreCase("isfloat")) {
                Double.parseDouble(value);
            }else {
                Integer.parseInt(value);
            }
        }catch (Exception e){
            if (flag.equalsIgnoreCase("isfloat")) {
                list.add("必须是浮点型数字");
            }else {
                list.add("必须是整型数字");
            }
        }
    }

    /**
     * 校验范围的正确性 TODO 需要重新加入逻辑，现在只支持既有上限又有下限的情况
     * @param current
     * @param min
     * @param max
     * @return
     */
    public boolean range(double current,double min,double max){
        return Math.max(min,current) == Math.min(current,max);
    }
    /**
     * 获取行数据
     * 获取字符串数组类型的行数据
     *
     * @param sheet  sheet页
     * @param rowNum 行数
     * @return 字符串数据数组
     */
    public static List<String> obtainRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (null == row) {
            return null;
        }

        int num = row.getLastCellNum();
        //获取数据库字段名称集合
        List<String> values = new ArrayList<>();
        for (int j = 0; j < num; j++) {
            String s = getCellValue(row.getCell(j));
            values.add(s);
        }

        return values;
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
            //ClientAnchor是附属在WorkSheet上的一个对象，  其固定在一个单元格的左上角和右下角.
            this.anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 5, (short) 5, 10);
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
            //ClientAnchor是附属在WorkSheet上的一个对象，  其固定在一个单元格的左上角和右下角.
            this.anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 5, (short) 5, 10);
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        //得到一个POI的工具类
        this.factory = wb.getCreationHelper();
        //设置背景色为黄色
        this.cs = wb.createCellStyle();
        cs.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);

        //设置背景色为白色
        this.cs2 = wb.createCellStyle();
        cs2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cs2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cs2.setBorderBottom(CellStyle.BORDER_THIN);
        cs2.setBorderLeft(CellStyle.BORDER_THIN);
        cs2.setBorderRight(CellStyle.BORDER_THIN);
        cs2.setBorderTop(CellStyle.BORDER_THIN);
        return wb;
    }

    /**
     * 获取单元格内容
     * 不用判断单元格类型获取单元格内容
     *
     * @param cell 单元格
     * @return 单元格内容
     */
    public static String getCellValue(Cell cell) {
        if (null == cell) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化
        String cellValue = "";
        int type = cell.getCellType();

        switch (type) {
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getRichStringCellValue().getString().trim();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                String format = cell.getCellStyle().getDataFormatString();
                if ("General".equals(format) || "@".equals(format)) {
                    double doubleVal = cell.getNumericCellValue();
                    int intVal = (int) doubleVal;
                    if (doubleVal == intVal) {
                        cellValue = String.valueOf(intVal);
                    } else {
                        cellValue = String.valueOf(doubleVal);
                    }
                } else if ("m/d/yy".equals(format)) {
                    cellValue = sdf.format(cell.getDateCellValue());
                } else {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case Cell.CELL_TYPE_FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        double doubleVal = cell.getNumericCellValue();
                        int intVal = (int) doubleVal;
                        if (doubleVal == intVal) {
                            cellValue = String.valueOf(intVal);
                        } else {
                            cellValue = String.valueOf(doubleVal);
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        cellValue = cell.getRichStringCellValue().getString();
                        break;
                }
                break;
            default:
                cellValue = "";
        }
        return cellValue;
    }

}
