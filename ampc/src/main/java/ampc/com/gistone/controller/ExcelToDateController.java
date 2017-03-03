package ampc.com.gistone.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.config.GetBySqlMapper;
import ampc.com.gistone.database.inter.TMeasureExcelMapper;
import ampc.com.gistone.database.inter.TSectorExcelMapper;
import ampc.com.gistone.database.inter.TSectorMapper;
import ampc.com.gistone.database.model.TMeasureExcel;
import ampc.com.gistone.database.model.TSector;
import ampc.com.gistone.database.model.TSectorExcel;
import ampc.com.gistone.util.AmpcResult;
import ampc.com.gistone.util.ClientUtil;
import ampc.com.gistone.util.ExcelToDate;

/**
 * 行业和措施的控制类
 * 
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
@RestController
@RequestMapping
public class ExcelToDateController {

	// 默认映射
	@Autowired
	private GetBySqlMapper getBySqlMapper;
	
	//行业Excel映射
	@Autowired
	private TSectorExcelMapper tSectorExcelMapper;

	//措施Excel映射
	@Autowired
	private TMeasureExcelMapper tMeasureExcelMapper;
	
	
	/**
	 * 根据Excel更改行业Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_sectorExcelDate")
	public AmpcResult update_SectorDate(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = Long.parseLong(data.get("userId").toString());
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tSectorExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TSectorExcel> readSector = ExcelToDate.ReadSector(fileName,versionId,userId);
			for (TSectorExcel tSector : readSector) {
				int result=tSectorExcelMapper.insertSelective(tSector);
				if(result<1){
					return AmpcResult.build(1000, "添加失败!", null);
				}
			}
			return AmpcResult.ok("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
	/**
	 * 根据Excel更改措施Excel表中数据
	 * @param request     请求
	 * @param response    响应
	 * @return 返回响应结果对象
	 */
	@RequestMapping("excel/update_measureExcelDate")
	public AmpcResult update_MeasureExcelDate(@RequestBody Map<String, Object> requestDate,HttpServletRequest request, HttpServletResponse response) {
		// 添加异常捕捉
		try {
			// 设置跨域
			ClientUtil.SetCharsetAndHeader(request, response);
			Map<String, Object> data = (Map) requestDate.get("data");
			// 用户的id 确定当前用户
			Long userId = Long.parseLong(data.get("userId").toString());
			/**
			 * 根据request获取excel地址
			 */
			String fileName = request.getServletContext().getRealPath("/")+ "***.xlsx";
			Long versionId=tMeasureExcelMapper.selectMaxVersion(userId);
			if(versionId==null){
				versionId=1L;
			}else{
				versionId++;
			}
			//地址不确定  先写死了 获取到所有Excel中需要的数据
			List<TMeasureExcel> readTMeasure = ExcelToDate.ReadMeasure(fileName,versionId,userId);
			for (TMeasureExcel tMeasure : readTMeasure) {
				int result=tMeasureExcelMapper.insertSelective(tMeasure);
				if(result<1){
					return AmpcResult.build(1000, "添加失败!", null);
				}
			}
			return AmpcResult.ok("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			// 返回错误信息
			return AmpcResult.build(1000, "参数错误", null);
		}
	}
	
}
