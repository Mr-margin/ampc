package ampc.com.gistone.util.excelUtil;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * 生成Excel填报模板和校验模板字段映射
 */
public class ExcelUtil {

  private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

  /**
   * Excel2003 后缀
   */
  public final static String EXCEL_2003_EXT = ".xls";

  /**
   * Excel2007 后缀
   */
  public final static String EXCEL_2007_EXT = ".xlsx";

  /**
   * 读取excel
   *
   * @param fileName 文件名
   * @param is       输入流
   * @return dto
   * @throws Exception
   */
  public static ExcelDto read(String fileName, InputStream is) throws Exception {
    ExcelDto dto = new ExcelDto();

    if (fileName.endsWith(EXCEL_2003_EXT)) {
      dto.setWorkbook(new HSSFWorkbook(is));
      dto.setExt(EXCEL_2003_EXT);
    } else if (fileName.endsWith(EXCEL_2007_EXT)) {
      dto.setWorkbook(new XSSFWorkbook(is));
      dto.setExt(EXCEL_2007_EXT);
    } else {
      throw new Exception("读取的不是Excel文件");
    }

    is.close();
    return dto;
  }

  /**
   * 生成模板并返回所有字段
   *
   * @param sourceFileName
   * @param inputStream
   * @param outputPath
   * @return
   * @throws Exception
   */
  public static GenerateTemplateReturnDto generateTemplate(String sourceFileName, InputStream inputStream, String outputPath) throws Exception {
    GenerateTemplateReturnDto ret = new GenerateTemplateReturnDto();
    List<InputField> ifs = new ArrayList<>();
    //key为调查表模板的File列的文件名称
    //value值为文件名称所包含每一行数据
    Map<String, List<Row>> files = new LinkedHashMap<>();

    //读取Excel
    ExcelDto dto = read(sourceFileName, inputStream);
    Workbook wb = dto.getWorkbook();

    Sheet sheet = wb.getSheetAt(0);
    Iterator<Row> rows = sheet.iterator();
    Row h = rows.next();//过掉表头
    String v = ExcelTool.singleton.getCellValue(h.getCell(15)); //获取表头第15列的字段名称
    boolean withRealTemplate = false; //需要抛弃掉某一条记录默认为false
    if ("RealTemplate".equals(v)) { //调查表需要抛弃某一条记录
      withRealTemplate = true;
    }

    //合并文件 构造key：文件名 value：记录列表的map
    while (rows.hasNext()) {
      Row row = rows.next();
      if (withRealTemplate) { //是否需要抛弃掉这一条记录
        String isReal = ExcelTool.singleton.getCellValue(row.getCell(15));
        if (!"1".equals(isReal)) {
          continue;
        }
      }
      Cell cell = row.getCell(0);
      if (null == cell) {
        break;
      }
      String fileName = row.getCell(0).getStringCellValue();
      if (files.containsKey(fileName)) {
        files.get(fileName).add(row);
      } else {
        List<Row> tempRows = new ArrayList<>();
        tempRows.add(row);
        files.put(fileName, tempRows);
      }
    }

    //合并sheet
    Set<Map.Entry<String, List<Row>>> entries = files.entrySet(); //遍历经过GROUP BY FILE后的文件数据
    for (Map.Entry<String, List<Row>> entry : entries) {
      //XXX:根据用户上传excel文件类型决定生成excel文件类型 有待商榷
      Workbook newWb = EXCEL_2007_EXT.equals(dto.getExt()) ? new XSSFWorkbook() : new HSSFWorkbook();
//            FileInputStream fis = new FileInputStream(new File(FilePath.data + "/template/template.xlsm"));
//            Workbook newWb = new XSSFWorkbook(fis);
//            fis.close();
      Workbook verifyWb = EXCEL_2007_EXT.equals(dto.getExt()) ? new XSSFWorkbook() : new HSSFWorkbook();

      List<Row> oneFileRows = entry.getValue();

     

      Map<String, List<Row>> sheetRowMap = new LinkedHashMap<>(); //key值为某一个File的Sheet名称 value值为所包含的记录
      //合并sheet 构造key：sheetName value：记录行的map 在经过GROUP BY FILE之后 在进行GROUP BY SHEET
      for (Row row : oneFileRows) {
        String sheetName = row.getCell(1).getStringCellValue();
        if (sheetRowMap.containsKey(sheetName)) {
          sheetRowMap.get(sheetName).add(row);
        } else {
          List<Row> oneSheetRows = new ArrayList<>();
          oneSheetRows.add(row);
          sheetRowMap.put(sheetName, oneSheetRows);
        }
      } //在经过这一步之后 就已经明确某一个文件的某一个sheet与它所包含的调查表模板数据列表

      //处理sheet
      for (Map.Entry<String, List<Row>> sheetEntry : sheetRowMap.entrySet()) {
        List<Row> oneSheetRows = sheetEntry.getValue();
        int sheetRowSize = oneSheetRows.size();
        Sheet newSheet = newWb.createSheet(sheetEntry.getKey()); //创建数据填报模板的sheet

        //获取用户输入字段
        List<InputField> sheetFields = getInputFields(oneSheetRows);

        //获取下拉列表
        if (!ret.getDropListMap().containsKey(sheetEntry.getKey())) {
          ret.getDropListMap().put(sheetEntry.getKey(), getDropList(oneSheetRows));
        }


        //将字段映射添加到返回字段映射列表
        ifs.addAll(sheetFields);

        //设置工作表单元格样式
        setColumnStyle(newWb, newSheet, sheetFields);

        for (int i = 0; i < sheetRowSize; i++) { //设置这一列单元格的所占宽度
          Row row = oneSheetRows.get(i);
          int sl = StringUtil.getWordCount(ExcelTool.singleton.getCellValue(row.getCell(3)));
          if (sl < 25) {
            sl = 25;
          }
          newSheet.setColumnWidth(i, sl * 256);
        }

        //转置单元格 参数2到参数7代表Etitle,CTitle1-CTitle5 参数1代表生成的模板从ETitle行下
        String[][] oo = ExcelTool.singleton.rowToCol(newWb, sheet, oneSheetRows, 2, 7, oneSheetRows.size(), newSheet, 0);
        for (int i = 0; i < oo.length; i++) {
          InputField ii = sheetFields.get(i);
          ii.setCtitle1(oo[i][1]);
          ii.setCtitle2(oo[i][2]);
          ii.setCtitle3(oo[i][3]);
          ii.setCtitle4(oo[i][4]);
          ii.setCtitle5(oo[i][5]);
        }

        //创建提示用户输入数据行 请从此行以下填写数据
        createRemindRow(newWb, newSheet, 6, oneSheetRows.size());

        //设置单元格类型
        for (int i = 0; i < sheetRowSize; i++) {
          String op = oneSheetRows.get(i).getCell(9).getStringCellValue();
          if ("list".equalsIgnoreCase(op)) {
            //TODO 设置下拉列表样式
          }
        }

        //创建校验工作表
        createVerifySheet(oneSheetRows, verifyWb, sheetEntry.getKey());
      }

      //XXX:根据用户上传文件类型决定文件生成类型
      String filePath = outputPath + "/" + entry.getKey() + dto.getExt();
      String verifyFilePath = outputPath + "/verify/" + entry.getKey() + "_verify" + dto.getExt();
      ExcelDto templateExcel = new ExcelDto();
      ExcelDto verifyExcel = new ExcelDto();
      templateExcel.setWorkbook(newWb);
      templateExcel.setKey(entry.getKey());
      templateExcel.setType(ExcelDto.TYPE_TEMPLATE);
      verifyExcel.setWorkbook(verifyWb);
      verifyExcel.setKey(entry.getKey());
      verifyExcel.setType(ExcelDto.TYPE_VERIFY);
      ret.getBookMap().put(filePath, templateExcel);
      ret.getBookMap().put(verifyFilePath, verifyExcel);

      logger.info(entry.getKey());

    }
    ret.setFields(ifs);
    return ret;
  }

  /**
   * 获取sheet对应的用户输入字段
   *
   * @param rows 单sheet对应记录行
   * @return 用户输入字段列表
   */
  private static List<InputField> getInputFields(List<Row> rows) {
    //TODO 闫柳给出第多少列是v1 如星的reset字段
    List<InputField> ifs = new ArrayList<>();
    for (Row row : rows) {
      InputField f = new InputField(ExcelTool.singleton.getCellValue(row.getCell(0)), ExcelTool.singleton.getCellValue(row.getCell(1)),
          ExcelTool.singleton.getCellValue(row.getCell(2)), null, ExcelTool.singleton.getCellValue(row.getCell(8)), ExcelTool.singleton.getCellValue(row.getCell(9)), ExcelTool.singleton.getCellValue(row.getCell(19)));
      ifs.add(f);
    }
    return ifs;
  }

  private static int listLevel(String op) {

    if (!op.contains("_")) {
      return 1;
    }

    return Integer.parseInt(op.split("_")[1]);
  }

  /**
   * 获取下拉规则   key 下拉类型   value 下拉的值
   * @param rows
   * @return
   */
  private static Map<String, List<String[]>> getDropList(List<Row> rows) {
    Map<String, List<String[]>> dropMap = new HashMap<>(); //key值为下拉类型 如list-judge

    for (Row row : rows) {

      String op = ExcelTool.singleton.getCellValue(row.getCell(9));
      String type;

      if (op.contains("list")) {
        type = op.split("-")[1];
        if (type.contains("_")) {
          type = type.split("_")[0];
        } else {
          //TODO 时间戳加3位随机数去重 引进级联组 防止sheet的这一种下拉类型包括多个字段 如list-judge
          type = type + "-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 900 + 100);
        }
      } else {
        continue;
      }

      List<String[]> l;
      if (dropMap.containsKey(type)) {
        l = dropMap.get(type);
      } else {
        l = new ArrayList<>();
        dropMap.put(type, l);
      }

      String[] a = new String[]{op, ExcelTool.singleton.getCellValue(row.getCell(2))}; //字符串数组{"op类型", "字段etitle"}
      l.add(a);
    }

    for (String k : dropMap.keySet()) {

      Map<Integer, String[]> sortMap = new TreeMap<>(); //下拉级联顺序排序 如list-region_1,list-region_2,list-region_3 key值为级别

      List<String[]> l = dropMap.get(k);
      for (String[] s : l) {
        sortMap.put(listLevel(s[0]), s);
      }

      dropMap.put(k, new ArrayList<>(sortMap.values()));
    }

    return dropMap;
  }

  /**
   * 设置单元格样式
   *
   * @param wb     工作簿
   * @param sheet  sheet页
   * @param fields sheet对应输入字段列表
   */
  private static void setColumnStyle(Workbook wb, Sheet sheet, List<InputField> fields) {

    //创建文本格式样式
    CellStyle textStyle = wb.createCellStyle();
    DataFormat df = wb.createDataFormat();
    textStyle.setDataFormat(df.getFormat("@"));
    textStyle.setAlignment(CellStyle.ALIGN_CENTER);

    CellStyle centerStyle = wb.createCellStyle();
    centerStyle.setAlignment(CellStyle.ALIGN_CENTER);
    centerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    int size = fields.size();
    for (int i = 0; i < size; i++) {
      InputField f = fields.get(i);
      switch (f.getType()) {
        case "int":
          sheet.setDefaultColumnStyle(i, centerStyle);
          break;
        case "string":
          sheet.setDefaultColumnStyle(i, textStyle);
          break;
        case "double":
          sheet.setDefaultColumnStyle(i, centerStyle);
          break;
        case "date":
          sheet.setDefaultColumnStyle(i, centerStyle);
          break;
      }
    }
  }

  /**
   * 创建校验工作表
   *
   * @param oneSheetRows 单sheet数据行
   * @param verifyWb     校验工作簿
   * @param sheetName    sheet名称
   */
  private static void createVerifySheet(List<Row> oneSheetRows, Workbook verifyWb, String sheetName) {
    //验证工作表
    Sheet verifySheet = verifyWb.createSheet(sheetName);
    Map<Integer, List<String>> regexes = new HashMap<>(); //key值为cell的列数 List为校验表达式列表 {isinter,notnull}
    int max = 0;
    //存放头部
    Row r = verifySheet.createRow(0);
    int l = oneSheetRows.size();
    for (int i = 0; i < l; i++) {
      String head = ExcelTool.singleton.getCellValue(oneSheetRows.get(i).getCell(2)); //获取etitle
      Cell cell = r.createCell(i);
      cell.setCellValue(head); //写入表头
      regexes.put(i, new ArrayList<>());
    }

    //获取校验规则
    for (int i = 0; i < l; i++) {
      Row row = oneSheetRows.get(i);
      List<String> list = regexes.get(i);
      String type = ExcelTool.singleton.getCellValue(row.getCell(8)); //字段类型

      //区间校验规则 下限0~ 上限~100 上下限0~100
      Cell minCell = row.getCell(10);//Range_min
      Cell maxCell = row.getCell(11);//Range_max
      String regex = "~";
      if (null != minCell) {
        String v = ExcelTool.singleton.getCellValue(minCell);
        if (null != v && !"".equals(v)) {
          if ("int".equals(type)) {
            regex = (int) Double.parseDouble(ExcelTool.singleton.getCellValue(minCell)) + regex;
          } else if ("double".equals(type)) {
            regex = ExcelTool.singleton.getCellValue(minCell) + regex;
          }
        }
      }
      if (null != maxCell) {
        String v = ExcelTool.singleton.getCellValue(maxCell);
        if (null != v && !"".equals(v)) {
          if ("int".equals(type)) {
            regex = regex + (int) Double.parseDouble(ExcelTool.singleton.getCellValue(maxCell));
          } else if ("double".equals(type)) {
            regex = regex + ExcelTool.singleton.getCellValue(maxCell);
          }
        }
      }
      if (!"~".equals(regex)) {
        list.add(regex);
        if (list.size() > max) {
          max = list.size();
        }
      }

      regex = null;
      //类型校验规则 isinteger isfloat
      switch (type) {
        case "int":
          regex = "isinteger";
          break;
        case "double":
          regex = "isfloat";
          break;
        case "string":
          break;
        case "date":
          regex = "isdate";
          break;
        default:
          break;
      }
      if (null != regex) {
        list.add(regex);
        if (list.size() > max) {
          max = list.size();
        }
      }

      //是否必填校验规则 notNull
      String str = ExcelTool.singleton.getCellValue(row.getCell(12));
      if ("1".equals(str)) {
        list.add("notNull");
        if (list.size() > max) {
          max = list.size();
        }
      }
    }

    //输出校验规则排在最上面
    int startRow = 1;
    for (int i = 0; i < max; i++) {
      r = verifySheet.createRow(startRow + i);
      for (int j = 0; j < l; j++) {
        List<String> list = regexes.get(j);
        if (list.size() > i) {
          Cell c = r.createCell(j);
          c.setCellValue(list.get(i));
        }
      }
    }
  }

  /**
   * 创建提示用户输入数据行
   *
   * @param wb     工作簿
   * @param sheet  sheet页
   * @param rowNum 起始行
   * @param colNum 合并列数
   */
  private static void createRemindRow(Workbook wb, Sheet sheet, int rowNum, int colNum) {
    //数据录入提示头部
    Row remindRow = sheet.createRow(rowNum);
    //添加合并行
    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, colNum - 1));
    Cell remindCell = remindRow.createCell(0);
    CellStyle remindStyle = wb.createCellStyle();
    remindStyle.setFillForegroundColor(HSSFColor.RED.index);
    remindStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    remindStyle.setAlignment(CellStyle.ALIGN_CENTER);
    Font font = wb.createFont();
    font.setFontName("黑体");
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//粗体显示
    font.setFontHeightInPoints((short) 12);
    remindStyle.setFont(font);
    remindCell.setCellStyle(remindStyle);
    remindCell.setCellValue("请从此行以下填写数据");
  }

 

 

 

}
