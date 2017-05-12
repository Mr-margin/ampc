package ampc.com.gistone.util.checkExcel;

import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wrx on 2017/3/27.
 */
public class ExportUtil {

    /**
     * 工作薄对象
     */
    private XSSFWorkbook wb;


    /**
     * 下载模板,参数是为了给模板添加特有功能，根据自己的业务改变
     * @param prov
     * @param city
     * @param country
     * @param fileName
     * @param map
     * @return
     */
    /*
    调用
    * new ExportUtil().exportTemplate(focusMap.get("prov").get(0),focusMap.get("city").get(0),
              focusMap.get("county"),fileName,map).write(request, response, fileName);
     */
    public ExportUtil exportTemplate(String prov,String city,List<String> country,String fileName,Map<String,List<String>> map){
        String path = "";  // TODO 模板路径
        getWorkbook(path);
        for (int n = 0; n < wb.getNumberOfSheets(); n++) {
            XSSFSheet sheet = wb.getSheetAt(n);
            sheet.getRow(6).getCell(0).setCellValue("省市县已经填充完,请填写剩余部分");
            for (int j = 0; j < country.size(); j++){
                Row headerRow = sheet.createRow(j+7);
                headerRow.createCell(0).setCellValue(prov);
                headerRow.createCell(1).setCellValue(city);
                headerRow.createCell(2).setCellValue(country.get(j));
            }
            if (!map.isEmpty()) {
                makeExcelList(sheet,map.get(sheet.getSheetName()),7,country.size()+6,3);
            }
        }

        return this;
    }

    /**
     * 给模板生成下拉菜单
     * @param sheet
     * @param list
     * @param firstRow
     * @param lastRow
     * @param cellNum
     */
    public static void makeExcelList(XSSFSheet sheet,List<String> list,int firstRow,int lastRow,int cellNum){
        // list 转 String[]
        String[] strArr = list.toArray(new String[list.size()]);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,lastRow,cellNum,cellNum);
        //生成下拉框内容
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createExplicitListConstraint(strArr);
        //绑定下拉框和作用区域
        XSSFDataValidation data_validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);

        //对sheet页生效
        sheet.addValidationData(data_validation);
    }

    public void getWorkbook(String path){
        try {
            this.wb = new XSSFWorkbook(new FileInputStream(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 这是动态生成模板的方法
     * @param country
     * @param industryList
     * @param city
     * @param province
     * @return
     */
    public ExportUtil exportCompany(String country, List<String> industryList, String city, String province) {
        this.wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("企业用户批量创建");
        // 设置单元格宽度
        sheet.setDefaultColumnWidth(20);
        XSSFSheet hidden = wb.createSheet("hidden");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("序号");
        headerRow.createCell(1).setCellValue("企业名称（必填）");
        headerRow.createCell(2).setCellValue("企业状态\n（必填）");
        headerRow.createCell(3).setCellValue("行业类别");
        headerRow.createCell(4).setCellValue("所在省\n（必填）");
        headerRow.createCell(5).setCellValue("所在市\n（必填）");
        headerRow.createCell(6).setCellValue("所在区县\n（必填）");

        List<String[]> list = new ArrayList<>();
        // 企业状态
        String[] status = new String[]{"关停","未关停"};
        // 行业类别
        String[] industry = industryList.stream().collect(Collectors.joining(",")).split(",");
//        String[] industry = new String[]{"aa","bb"};
        // 所在省
        String[] pro = new String[]{province};
        // 所在市
        String[] ci = new String[]{city};
        // 所在县
        String[] cou = country.split(",");
        list.add(status);
        list.add(industry);
        list.add(pro);
        list.add(ci);
        list.add(cou);

        // 下面是生成下拉菜单
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        for (int i = 0; i < list.size(); i++){
            CellRangeAddressList regions = new CellRangeAddressList(1,1000,i+2,i+2);
            XSSFDataValidationConstraint dvConstraint = null;

            //生成下拉框内容
            if (i == 1){    // 因为excel下拉列表不允许超出255字节，所以下面代码是解决方法
                for (int y = 0, length= list.get(i).length; y < length; y++) {
                    String name = list.get(i)[y];
                    XSSFRow row = hidden.createRow(y);
                    XSSFCell cell = row.createCell(0);
                    cell.setCellValue(name);
                }
                Name namedCell = wb.createName();
                namedCell.setNameName("hidden");
                namedCell.setRefersToFormula("hidden!$A$1:$A$" + list.get(i).length);
                wb.setSheetHidden(1, true);
                dvConstraint = (XSSFDataValidationConstraint) dvHelper
                        .createFormulaListConstraint("hidden");
            }else {
                dvConstraint = (XSSFDataValidationConstraint) dvHelper
                        .createExplicitListConstraint(list.get(i));
            }
            //绑定下拉框和作用区域
            XSSFDataValidation data_validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint,regions);

            //对sheet页生效
            sheet.addValidationData(data_validation);
        }
// 下面注释的内容是2003版的excel下拉列表的写法。
        /*for (int i = 0; i < list.size(); i++){
            CellRangeAddressList regions = new CellRangeAddressList(1,1000,i+2,i+2);
            DVConstraint constraint = null;

            //生成下拉框内容
            if (i == 1){
                for (int y = 0, length= list.get(i).length; y < length; y++) {
                    String name = list.get(i)[y];
                    HSSFRow row = hidden.createRow(y);
                    HSSFCell cell = row.createCell(0);
                    cell.setCellValue(name);
                }
                Name namedCell = wb.createName();
                namedCell.setNameName("hidden");
                namedCell.setRefersToFormula("hidden!$A$1:$A$" + list.get(i).length);
                wb.setSheetHidden(1, true);
                constraint = DVConstraint.createFormulaListConstraint("hidden");
            }else {
                constraint = DVConstraint.createExplicitListConstraint(list.get(i));
            }
            //绑定下拉框和作用区域
            HSSFDataValidation data_validation = new HSSFDataValidation(regions,constraint);

            //对sheet页生效
            sheet.addValidationData(data_validation);
        }*/
        return this;
    }

    /**
     * 输出到客户端
     * @param fileName 输出文件名
     */
    public ExportUtil write(HttpServletRequest request,HttpServletResponse response, String fileName) throws IOException {
        //HttpServletRequest request = response.get;
        response.reset();
        //response.setContentType("application/octet-stream; charset=utf-8");
        //String fileName=null;
        if (request.getHeader("User-Agent").toLowerCase()
                .indexOf("firefox") > 0) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1"); // firefox浏览器
        } else if (request.getHeader("User-Agent").toUpperCase()
                .indexOf("MSIE") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
        }else if (request.getHeader("User-Agent").toUpperCase()
                .indexOf("CHROME") > 0) {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
        }
        //fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        response.setHeader("Content-disposition", "attachment; filename="+ fileName); //设定输出文件头
        response.setContentType("application/msexcel"); //定义输出类型
        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出数据流
     * @param os 输出数据流
     */
    public ExportUtil write(OutputStream os) throws IOException{
        wb.write(os);
        return this;
    }

}
