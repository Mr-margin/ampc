package ampc.com.gistone.util.excelUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 读取Excel工具类 写入错误信息
 */
public class ExcelTool {
  //单例
  public static ExcelTool singleton = new ExcelTool();
  //Excel 2003
  public final static String EXCEL_2003_EXT = ".xls";
  //Excel 2007
  public final static String EXCEL_2007_EXT = ".xlsx";
  //工作簿
  private Workbook workbook;
  //黄色背景
  private CellStyle yellowStyle;
  //得到一个POI的工具类
  CreationHelper factory;
  //ClientAnchor是附属在WorkSheet上的一个对象，  其固定在一个单元格的左上角和右下角.
  ClientAnchor anchor;
  //涂抹错误记录
  public boolean clearError = false;
  //指定读取sheet名称
  public List sheetNames;
  //开始记录行数
  public int startRow;
  //数据map
  private Map dataMap;
  //表头map
  private Map headerMap;
  //drawing
  private Map drawMap;

  public ExcelTool(Workbook workbook) {
    this.workbook = workbook;
    this.factory = workbook.getCreationHelper();
    this.anchor = factory.createClientAnchor();
  }

  public void buildHeader() {
    this.headerMap = new LinkedHashMap<>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet sheet = workbook.getSheetAt(i);
      if (sheetNames.contains(sheet.getSheetName())) {
        headerMap.put(sheet.getSheetName(), readHeader(sheet));
      }
    }
  }

  public ExcelTool(String extend, InputStream stream) throws Exception {

    this.workbook = obtainBook(extend, stream).getWorkbook();

    //设置背景色为黄色
    this.yellowStyle = workbook.createCellStyle();
    this.yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    this.yellowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    this.yellowStyle.setAlignment(CellStyle.ALIGN_CENTER);
    this.yellowStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    this.factory = workbook.getCreationHelper();
    //this.anchor = factory.createClientAnchor();
    this.anchor = new XSSFClientAnchor(0, 0, 0, 0, (short)1, 2, (short)3, 10);

    this.drawMap = new LinkedHashMap<>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet sheet = workbook.getSheetAt(i);
      this.drawMap.put(sheet.getSheetName(), sheet.createDrawingPatriarch());
    }
  }

  public ExcelTool() {

  }

  /**
   * 读取excel
   *
   * @param extend 文件后缀
   * @param is     输入流
   * @return dto
   * @throws Exception
   */
  public ExcelResult obtainBook(String extend, InputStream is) throws Exception {
    ExcelResult result = new ExcelResult();

    if (extend.equals(EXCEL_2003_EXT)) {
      result.setWorkbook(new HSSFWorkbook(is));
      result.setExt(EXCEL_2003_EXT);
    } else if (extend.equals(EXCEL_2007_EXT)) {
      result.setWorkbook(new XSSFWorkbook(is));
      result.setExt(EXCEL_2007_EXT);
    } else {
      throw new Exception("读取的不是Excel文件");
    }

    is.close();
    return result;
  }

  /**
   * 清空错误信息的方法
   */
  private void clearErrorInfo() {
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet sheet = workbook.getSheetAt(i);
      for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
        Row row = sheet.getRow(j);
        if (null == row) continue;
        for (int k = 0; k < row.getLastCellNum(); k++) {
          Cell cell = row.getCell(k);
          if (null != cell) {
            if (null != cell.getCellComment()) {
              cell.removeCellComment();
              cell.setCellStyle(copyStyle(cell.getCellStyle()));
            }
          }
        }
      }
    }
  }

  
  //复制当前单元格的样式
  public CellStyle copyStyle(CellStyle style) {
    CellStyle s = workbook.createCellStyle();
    if (null == style) {
      s.setAlignment(CellStyle.ALIGN_CENTER);
      s.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    } else {
      s.setAlignment(style.getAlignment());
      s.setVerticalAlignment(style.getVerticalAlignment());
      s.setDataFormat(style.getDataFormat());
    }
    return s;
  }

  /**
   * 获取单元格内容
   * 不用判断单元格类型获取单元格内容
   *
   * @param cell 单元格
   * @return 单元格内容
   */
  public String getCellValue(Cell cell) {

    if (null == cell) return null;

    String cellValue = "";
    int type = cell.getCellType();
    String format = cell.getCellStyle().getDataFormatString();
    int i = cell.getCellStyle().getDataFormat();

    switch (type) {
      case Cell.CELL_TYPE_STRING:
        cellValue = cell.getRichStringCellValue().getString().trim();
        break;
      case Cell.CELL_TYPE_NUMERIC:
        if (org.apache.poi.ss.usermodel.DateUtil.isADateFormat(i, format)) {
          cellValue = ampc.com.gistone.util.excelUtil.DateUtil.getYYYYMMDDLineStr(cell.getDateCellValue());
        } else {
          if ("General".equals(format) || "@".equals(format)) {
            double doubleVal = cell.getNumericCellValue();
            int intVal = (int) doubleVal;
            if (doubleVal == intVal) {
              cellValue = String.valueOf(intVal);
            } else {
              cellValue = String.valueOf(doubleVal);
            }
          } else if ("m/d/yy".equals(format)) {
            cellValue = DateUtil.getYYYYMMDDSlashStr(cell.getDateCellValue());
          } else {
            double doubleVal = cell.getNumericCellValue();
            int intVal = (int) doubleVal;
            if (doubleVal == intVal) {
              cellValue = String.valueOf(intVal);
            } else {
              cellValue = String.valueOf(doubleVal);
            }
          }
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

  /**
   * 根据header数据获取一行数据的map
   * key header 值是对应的值
   * @param sheet
   * @param rowNum
   * @param headers
   * @return
   */
  public Map<String, String> obtainRow(Sheet sheet, int rowNum, String[] headers) {
    Row row = sheet.getRow(rowNum);
    if (null == row) {
      return null;
    }

    //获取数据库字段名称数组
    Map<String, String> map = new HashMap<>();
    for (int j = 0; j < headers.length; j++) {
      String s = getCellValue(row.getCell(j));
      map.put(headers[j], s);
    }

    return map;
  }


  /**
   * 获取行数据
   * 获取字符串数组类型的行数据
   *
   * @param sheet  sheet页
   * @param rowNum 行数
   * @return 字符串数据数组
   */
  public String[] obtainRow(Sheet sheet, int rowNum) {
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
   * 统计字符串长度
   *
   * @param s
   * @return
   */
  public int calcWordCount(String s) {
    int length = 0;
    if (null == s || "".equals(s)) {
      return length;
    }
    for (int i = 0; i < s.length(); i++) {
      int ascii = Character.codePointAt(s, i);
      if (ascii >= 0 && ascii <= 255)
        length++;
      else
        length += 2;

    }
    return length;
  }

  /**
   * 读取表头
   * @param sheet
   * @return
   */
  public Map readHeader(Sheet sheet) {
    //设置表头
    String[] headers = obtainRow(sheet, 0);
    //存放表头顺序
    Map m = new LinkedHashMap<>();
    for (int j = 0; j < headers.length; j++)
      m.put(headers[j], j + 1);

    return m;
  }

  /**
   * 给单元格写入值
   * @param valueMap
   */
  public void writeValue(Map valueMap) {
    Set s = valueMap.keySet();
    for (Object k : s) {
      Map hMap = (Map) this.headerMap.get(k);
      Sheet sheet = workbook.getSheet(k.toString());
      List<Map> l = (List) valueMap.get(k);
      for (int i = 0; i < l.size(); i++) {
        Map m = l.get(i);
        Row row = sheet.createRow(startRow + i);
        Set ss = m.keySet();
        for (Object kk : ss) {
          if (hMap.containsKey(kk)) {
            int n = (int) hMap.get(kk) - 1;
            if (null != m.get(kk) && !"".equals(m.get(kk))) {
              Cell cell = row.createCell(n);
              cell.setCellValue(m.get(kk).toString());
            }
          }
        }
      }
    }
  }

  /**
   * 读一个Excel所有数据
   * @return
   */
  public Map[] readValue() {

    if (clearError) this.clearErrorInfo();

    Map<String, Map<String, Integer>> headerMap = new LinkedHashMap<>();
    Map map = new HashMap<>();

    int sheetCount = workbook.getNumberOfSheets();
    for (int i = 0; i < sheetCount; i++) {
      Sheet sheet = workbook.getSheetAt(i);
      if (null != sheetNames && !sheetNames.contains(sheet.getSheetName())) continue;

      Map<String, Integer> header = readHeader(sheet);
      headerMap.put(sheet.getSheetName(), header);
      String[] headers = header.keySet().toArray(new String[0]);

      //遍历数据
      int lastRowNum = sheet.getLastRowNum();
      List<Map<String, String>> list = new ArrayList<>();

      for (int j = startRow; j <= lastRowNum; j++) {
        Map<String, String> rowData = obtainRow(sheet, j, headers);
        list.add(rowData);
      }

      map.put(sheet.getSheetName(), list);
    }

    this.headerMap = headerMap;
    this.dataMap = map;

    return new Map[]{headerMap, map};
  }

  //读取excel数据
  public Map[] readValue(Workbook wb, int startRow, List sheetNames) {

    Map<String, Map<String, Integer>> headerMap = new LinkedHashMap<>();
    Map map = new HashMap<>();

    int sheetCount = wb.getNumberOfSheets();
    for (int i = 0; i < sheetCount; i++) {
      Sheet sheet = wb.getSheetAt(i);
      if (null != sheetNames && !sheetNames.contains(sheet.getSheetName())) continue;

      Map<String, Integer> header = readHeader(sheet);
      headerMap.put(sheet.getSheetName(), header);
      String[] headers = header.keySet().toArray(new String[0]);

      //遍历数据
      int lastRowNum = sheet.getLastRowNum();
      List<Map<String, String>> list = new ArrayList<>();

      for (int j = startRow; j <= lastRowNum; j++) {
        Map<String, String> rowData = obtainRow(sheet, j, headers);
        if (null == rowData) {
          continue;
        }
        list.add(rowData);
      }

      map.put(sheet.getSheetName(), list);
    }

    return new Map[]{headerMap, map};
  }

  /**
   * 读取数据
   * 根据上传excel文件读取填报数据
   *
   * @param stream   文件输入流
   * @param extend   文件后缀
   * @param startRow 从第几行读取
   * @return 虚拟实体类
   * @throws Exception
   */
  public Map[] readValue(InputStream stream, String extend, int startRow, List sheetNames) throws Exception {
    try {
      ExcelResult excelResult = obtainBook(extend, stream);
      return readValue(excelResult.getWorkbook(), startRow, sheetNames);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (null != stream) {
        stream.close();
      }
    }
  }

  /**
   * 读取数据
   * @param stream
   * @param extend
   * @param startRow
   * @return
   * @throws IOException
   */
  public Map[] readValue(InputStream stream, String extend, int startRow) throws IOException {
    try {
      ExcelResult excelResult = obtainBook(extend, stream);
      return readValue(excelResult.getWorkbook(), startRow, null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (null != stream) {
        stream.close();
      }
    }
  }

  /**
   * 单元格行向转置为列向
   *
   * @param sheet    原sheet
   * @param row1     起始行
   * @param row2     终止行
   * @param newSheet 新建sheet
   * @param row3     转置后sheet起始行
   */
  public String[][] rowToCol(Workbook wb, Sheet sheet, List<Row> oneSheetRows,
                              int row1, int row2, int colNum,
                              Sheet newSheet, int row3) {

    String[][] oo = new String[oneSheetRows.size()][row2 -row1 + 1];

    //获取sheet合并区域 获取该sheet合并单元格的所有区间范围
    int mergeCount = sheet.getNumMergedRegions();
    List<CellRangeAddress> mergeList = new ArrayList<>(); //poi合并单元格的类
    for (int i = 0; i < mergeCount; i++) {
      mergeList.add(sheet.getMergedRegion(i));
    }

    Map regionMergeMap = new HashMap<>();

    //转置单元格 横向转纵向 CTitlt1-CTitle5转置 row2-row1+1代表ctitle1到ctitle5在加上etitle的数量
    CustomCell[][] cells = new CustomCell[row2 - row1 + 1][colNum];
    for (int i = 0; i < colNum; i++) {
      Row row = oneSheetRows.get(i);
      //遍历CTitlt1-CTitle5
      for (int j = row1; j <= row2; j++) {
        Cell cell = row.getCell(j);
        if (null != cell) {
          //获取合并区间
          int[] rect = getMergeBoundary(mergeList, cell);
          CustomCell cc = new CustomCell();
          CellStyle style = cell.getCellStyle();
          if (CellStyle.VERTICAL_CENTER == style.getVerticalAlignment()) { //单元格纵向对齐转成横向单元格对齐
            cc.setAlignment(CellStyle.ALIGN_CENTER);
          }
          if (CellStyle.ALIGN_CENTER == style.getAlignment()) { //单元格横向对齐转纵向对齐
            cc.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
          }

          //获取单元格颜色
          Object color = getColorValue(style); //获取单元格背景颜色
          cc.setColor(color);

          String s;
          //判断空值
          if (null != (s = getCellValue(cell))
              && !"".equals(s)
              && !"NAN".equalsIgnoreCase(s)) {
            cc.setV(s);
          }
          if (null != rect) {//合并单元格 判断依据为上一步获取到的该单元格的合并区间
            cc.setW(rect[1] - rect[0] + 1);
            cc.setH(rect[3] - rect[2] + 1);
            //设置合并区域key值
            String regionKey = rect[0] + "-" + rect[1] + "-" + rect[2] + "-" + rect[3];
            if (!regionMergeMap.containsKey(regionKey)) { //没有被设置合并规则的二维数组位置则设置
              Object[] array = new Object[]{j - row1, s}; //1:列数 2:数值
              regionMergeMap.put(regionKey, array);
              oo[i][j-row1] = array[1].toString();
            } else {
              Object[] array = (Object[]) regionMergeMap.get(regionKey);
              int con = (int) array[0];
              oo[i][con] = array[1].toString();
            }
            cc.setRegionKey(regionKey);
          } else {//非合并单元格
            cc.setW(1);
            cc.setH(1);
            oo[i][j-row1] = s;
          }
          cells[j - row1][i] = cc;
        } else {
          cells[j - row1][i] = null;
          oo[i][j-row1] = null;
        }
      }
    }

    Set<String> flagMap = new HashSet<>();//已合并区域set 去重标志 将转置好的二维数组准备写入Excel中
    for (int i = 0; i < cells.length; i++) {
      int ri = i + row3;//累加ETitle行
      Row row = newSheet.createRow(ri);
      if (0 == i) {
        row.setZeroHeight(true);
      }
      for (int j = 0; j < cells[i].length; j++) {
        if (null != cells[i][j]) {
          CellStyle style = wb.createCellStyle();
          style.setAlignment(cells[i][j].getAlignment());
          style.setVerticalAlignment(cells[i][j].getVerticalAlignment());

          //设置颜色
          Object color = cells[i][j].getColor();
          if (null != color) {
            XSSFColor xssfColor = new XSSFColor();
            xssfColor.setRGB((byte[]) color);
            style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            ((XSSFCellStyle) style).setFillForegroundColor(xssfColor);
          }

          String key = cells[i][j].getRegionKey();
          if (null != key) {//合并单元格
            if (!flagMap.contains(key)) {//合并区间没有合并过
              flagMap.add(key);//设置合并标志
              //合并单元格
              CellRangeAddress range = new CellRangeAddress(ri, ri + cells[i][j].getH() - 1,
                  j, j + cells[i][j].getW() - 1);
              newSheet.addMergedRegion(range); //poi设置合并合并区间
              Cell cell = row.createCell(j);
              //设置单元格内容
              if (null != cells[i][j].getV()) {
                cell.setCellValue(cells[i][j].getV());
              }
              cell.setCellStyle(style);
            }
          } else {//非合并单元格
            Cell cell = row.createCell(j);
            if (null != cells[i][j].getV()) {
              cell.setCellValue(cells[i][j].getV());
            }
            cell.setCellStyle(style);
          }
        }
      }
    }

    return oo;
  }


  /**
   * 判断该单元格是否属于合并单元格
   *
   * @param ranges sheet所有合并区间
   * @param cell   单元格
   * @return 合并区间 [起始行,结束行,起始列,结束列]
   * 如果为合并单元格则返回合并区间 不是合并单元格则返回null
   */
  private static int[] getMergeBoundary(List<CellRangeAddress> ranges, Cell cell) {
    if (null == cell) {
      return null;
    }

    //遍历所有合并区间
    int count = ranges.size();
    for (int i = 0; i < count; i++) {
      CellRangeAddress range = ranges.get(i);
      int firstColumn = range.getFirstColumn();
      int lastColumn = range.getLastColumn();
      int firstRow = range.getFirstRow();
      int lastRow = range.getLastRow();

      //判断单元格是否在合并区间范围内
      if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow
          && cell.getColumnIndex() >= firstColumn && cell.getColumnIndex() <= lastColumn) {
        return new int[]{firstRow, lastRow, firstColumn, lastColumn};
      }
    }

    return null;
  }

  /**
   * 获取单元格颜色取值 分为2003和2007两种 2003为string类型 2007位字节数组类型
   *
   * @param style
   * @return
   */
  private static Object getColorValue(CellStyle style) {
    Color color = style.getFillForegroundColorColor();
    if (null != color) {
      if (color instanceof XSSFColor) {
        XSSFColor xssfColor = ((XSSFColor) color);
        byte[] b = xssfColor.getRGB();
        return b;
      } else {
        String hex = ((HSSFColor) color).getHexString();
        return hex;
      }
    }
    return null;
  }

  //生成标注
  public Comment getComment(Sheet sheet, String message) {
    Drawing drawing = (Drawing) this.drawMap.get(sheet.getSheetName());
    ClientAnchor an = drawing.createAnchor(0, 0, 0, 0, (short) 1, 2, (short) 3, 10);
    Comment comment0 = drawing.createCellComment(an);
    RichTextString str0 = factory.createRichTextString(message);
    comment0.setString(str0);
    comment0.setAuthor("Apache POI");
    return comment0;
  }

  //获取yellow style
  public CellStyle createErrorStyle(CellStyle style) {
    //设置背景色为黄色
    CellStyle errorStyle = workbook.createCellStyle();
    errorStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    errorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    errorStyle.setAlignment(CellStyle.ALIGN_CENTER);
    errorStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    if (null != style) errorStyle.setDataFormat(style.getDataFormat());
    return errorStyle;
  }

  /**
   * 写入错误信息
   * @param errors
   * @throws Exception
   */
  public void writeError(List<ItemError> errors) throws Exception {
    for (int i = 0; i < errors.size(); i++) {
      ItemError error = errors.get(i);
      Sheet errorSheet = workbook.getSheet(error.sheetName);
      Row errorRow = errorSheet.getRow(startRow + error.rowNumber - 1);
      if (null == errorRow) errorRow = errorSheet.createRow(startRow + error.rowNumber - 1);
      Map hMap = (Map) headerMap.get(error.sheetName);
      Cell errorCell = errorRow.getCell((int) hMap.get(error.columnName) - 1);
      if (null == errorCell) errorCell = errorRow.createCell((int) hMap.get(error.columnName) - 1);
      CellStyle errorStyle = this.createErrorStyle(errorCell.getCellStyle());
      errorCell.setCellStyle(errorStyle);
      if (null == errorCell.getCellComment()) {
        errorCell.setCellComment(getComment(errorSheet, error.message));
      }
    }
  }

  /**
   * 保存Excel
   * @param path
   * @throws Exception
   */
  public void save(String path) throws Exception {
    FileOutputStream out = new FileOutputStream(path);
    workbook.write(out);
    out.close();
  }

  //缺失sheet
  public List loseSheet() {
    List lose = new ArrayList<>(this.sheetNames);
    List have = new ArrayList<>(this.dataMap.keySet());
    lose.removeAll(have);
    return lose;
  }

  public Map getHeaderMap() {
    return this.headerMap;
  }

  //对缺失的表头进行标注
  public void headerRemark(Map loseHeader) {
    Set s = loseHeader.keySet();
    for (Object k : s) {
      Sheet sheet = workbook.getSheet(k.toString());
      Map hm = (Map) headerMap.get(k.toString());
      List hs = (List) loseHeader.get(k);
      for (int i = 0; i < startRow - 1; i++) {
        Row row = sheet.getRow(i);
        for (Object h : hs) {
          int n = (int) hm.get(h) - 1;
          for (int j = n; j >= 0; j--) {
            Cell cell = row.getCell(j);
            if (null != cell) {
              cell.setCellStyle(yellowStyle);
              cell.setCellComment(getComment(sheet, "补充缺失的表头"));
              break;
            }
          }
        }
      }
    }
  }
}
