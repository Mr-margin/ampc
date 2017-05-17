/*package ampc.com.gistone.util.checkExcelUtil;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*//**
 * Created by chf on 2016/12/5.
 *//*
@RestController
@RequestMapping("/file")
public class FileController {

  @Autowired
  private DataFileService dataFileService;

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  @Autowired
  private GtTemplateTableService templateService;

  @RequestMapping("/uploadTemplate")
  public IResponse uploadTemplate(String version, HttpServletRequest request) {
    logger.info("/file/uploadTemplate-version:" + version);
    try {
      MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
      MultipartFile file = req.getFile("file");
      String fileName = file.getOriginalFilename();
      if (null == fileName || "".equals(fileName)) {
        return new FailResponse(IResponse.ERR_CODE_PARAM_ERROR, "上传文件不能为空");
      }
      templateService.generate(file.getOriginalFilename(),
          file.getInputStream(), FilePath.data + "/template", version);
      return new SuccessResponse();
    } catch (Exception e) {
      logger.error("ERROR:/file/upload", e);
      return new FailResponse(e.getMessage());
    }
  }

  @RequestMapping(value = "export/template",method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(value="环保用户模板下载", notes="环保用户模板下载")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "region", value = "用户行政区划代码", required = true,
                  paramType = "query",dataType = "String",defaultValue = "130100"),
          @ApiImplicitParam(name = "sourceId", value = "文件的sourceId", required = true,
                  paramType = "query",dataType = "int",defaultValue = "1001")
  })
  public void exportFileTemplate(String region,Integer sourceId,HttpServletRequest request,HttpServletResponse response) {
    try {
      Map<String,List<String>> map = getExcelList(sourceId);
      Map<String, List<String>> focusMap = templateService.initFocus(region);
      String fileName = HbUserDataType.valueOf(sourceId).getBigIndex()+".xlsx";
      new ExportUtil().exportTemplate(focusMap.get("prov").get(0),focusMap.get("city").get(0),
              focusMap.get("county"),fileName,map).write(request, response, fileName);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  *//**
   * 数据填报->市填报/环保用户->上传文件
   *
   * @param taskId      子任务id
   * @param userId      用户id
   * @param version     版本号
   * @param description 描述（可无）
   * @param dataType    p:点源 s:面源
   * @param userType    用户的等级
   * @param region      行政区划代码
   * @param request
   * @return
   *//*
  @RequestMapping("/uploadData")
  public IResponse uploadData(Integer sourceId, String dataType, String taskId, String userId, String version,
                              String description, Integer userType,String region,HttpServletRequest request, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");

    logger.info("/file/uploadData sourceId:" + sourceId + ",dataType:" + dataType +
        ",taskId:" + taskId + ",version:" + version + ",description:" + description);
    try {
      MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
      MultipartFile file = req.getFile("file");
      if (null == file) {
        return new FailResponse(IResponse.ERR_CODE_PARAM_ERROR, "上传文件不能为空");
      }

      String fileName = file.getOriginalFilename();
      if (null == fileName || "".equals(fileName)) {
        return new FailResponse(IResponse.ERR_CODE_PARAM_ERROR, "上传文件不能为空");
      }
      if (!fileName.endsWith(".xlsx")) {
        return new FailResponse(IResponse.ERR_CODE_PARAM_ERROR, "请选择xlsx格式文件上传");
      }

      ServiceResult result;
      if (userType == UserType.ENVIRONMENT.getIndex()) {
        Map<String,List<String>> map = getExcelList(sourceId);
        result = templateService.readHb(file,region,sourceId, taskId, userId, version,description,map);
      }else {
        result = templateService.upload(sourceId, dataType, taskId, userId, version, description, file);
      }
      if (!result.isSuccess()) return new FailResponse(result.getCode(), result.getMessage(), result.getData());
      return new SuccessResponse();
    } catch (Exception e) {
      logger.error("ERROR:/file/uploadData", e);
      return new FailResponse(null, "请求异常");
    }
  }

  public Map<String,List<String>> getExcelList(Integer sourceId){
    Map<String,List<String>> map = new HashMap<>();
    if (sourceId == HbUserDataType.ETA.getIndex()) {
      // 根据bigIndex找excel中的下拉列表Map<smallIndex,List<下拉列表内容>>
      map = templateService.findHbExcelList(HbUserDataType.valueOf(sourceId).getBigIndex());
    }
    return map;
  }

  *//**
   * 数据填报->市填报用户->文件列表
   *
   * @param sourceId 排放源id
   * @param taskId   子任务id
   * @param version  版本号
   * @param userType 用户等级
   * @return
   *//*
  @RequestMapping("/list")
  public IResponse list(Integer sourceId, Integer taskId, String version,String userType) {
    try {
      SourceFileScanDto result = dataFileService.list(sourceId, taskId, version,userType);
      return new SuccessResponse(result);
    } catch (Exception e) {
      logger.error("ERROR:/file/list", e);
      return new FailResponse(null, "请求异常");
    }
  }

  @RequestMapping(value = "/testPost")
  public IResponse testPost(String smallIndex, String[] companyIds, Integer fileId, Integer pageNumber, Integer pageSize) {
    try {
      return new SuccessResponse(templateService.pageByCompanyIds("1.0", fileId, companyIds, smallIndex, pageNumber, pageSize));
    } catch (Exception e) {
      e.printStackTrace();
      return new FailResponse();
    }
  }

  @RequestMapping(value = "/download")
  public void download(String fileId, HttpServletRequest request, HttpServletResponse response) {
    try {
      response.setContentType("text/html;charset=UTF-8");
      BufferedInputStream in = null;
      BufferedOutputStream out = null;
      try {
        File f = new File(FilePath.data + "/" + fileId);
        if (!f.exists()) {
          PrintWriter writer = response.getWriter();
          writer.write("文件不存在");
          writer.flush();
          return;
        }
        String fileName = f.getName();

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
          response.setContentType("application/x-excel");
        } else if (fileName.endsWith(".rar")) {
          response.setContentType("application/x-rar-compressed");
        } else if (fileName.endsWith(".zip")) {
          response.setContentType("application/zip");
        }

        if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
          fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
          fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(f.length()));
        in = new BufferedInputStream(new FileInputStream(f));
        out = new BufferedOutputStream(response.getOutputStream());
        byte[] data = new byte[1024];
        int len = 0;
        while (-1 != (len = in.read(data, 0, data.length))) {
          out.write(data, 0, len);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (in != null) {
          in.close();
        }
        if (out != null) {
          out.close();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value = "/delete", method = RequestMethod.GET)
  public IResponse delete(String version, Integer fileId, String userId) {
    try {
      dataFileService.delByFileId(version, fileId, userId);
      return new SuccessResponse();
    } catch (Exception e) {
      logger.error("ERROR:/file/upload", e);
      return new FailResponse(null, "请求异常");
    }
  }

  @RequestMapping(value = "/data/list")
  public PageResultDto listData(Integer taskId, String version, Integer fileId,
                                String smallIndex, Integer pageNumber, Integer pageSize) {
    try {
      logger.info("file/data/list-taskId:" + taskId + "-version:" +
          version + "-fileId:" + fileId + "-smallIndex:" + smallIndex + "-pageNumber:" +
          pageNumber + "-pageSize:" + pageSize);
      PageResultDto dto = templateService.pageByFileIdAndSmallIndex(taskId,
          version, fileId, smallIndex, pageNumber, pageSize);
      return dto;
    } catch (Exception e) {
      logger.error("", e);
      return null;
    }
  }

  @RequestMapping(value = "/downloadTemplate")
  public IResponse downloadTemplate(Integer taskId, String version, String bigIndex,
                                    HttpServletRequest request, HttpServletResponse response) {
    logger.info("file/downloadTemplate-taskId:" +
        taskId + "-version:" + version + "-bigIndex:" + bigIndex);
    BufferedInputStream in = null;
    BufferedOutputStream out = null;
    try {
      String fileName = bigIndex + ".xlsx";
      String filePath = FilePath.data + "/template/" + version + "/" + fileName;
      logger.info(filePath);
      File f = new File(filePath);
      if (!f.exists()) {
        return new FailResponse(IResponse.ERR_CODE_PARAM_ERROR, "填报模板不存在");
      }

      if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
        fileName = URLEncoder.encode(fileName, "UTF-8");
      } else {
        fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
      }

      response.setCharacterEncoding("UTF-8");
      response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
      response.setHeader("Content-Length", String.valueOf(f.length()));
      in = new BufferedInputStream(new FileInputStream(f));
      out = new BufferedOutputStream(response.getOutputStream());
      byte[] data = new byte[1024];
      int len = 0;
      while (-1 != (len = in.read(data, 0, data.length))) {
        out.write(data, 0, len);
      }
      return new SuccessResponse();
    } catch (Exception e) {
      logger.error("ERROR:/file/downloadTemplate", e);
      return new FailResponse(null, "请求异常");
    } finally {
      try {
        if (null != in) {
          in.close();
        }
        if (null != out) {
          out.close();
        }
      } catch (IOException e) {
        logger.error("ERROR:/file/data/update", e);
        return new FailResponse(null, "文件操作异常");
      }
    }
  }

  public static void main(String[] args) {
    System.out.println(new File("C:\\Users\\1.txt").getName());
  }

}
*/