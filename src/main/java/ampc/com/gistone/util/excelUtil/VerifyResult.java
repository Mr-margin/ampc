package ampc.com.gistone.util.excelUtil;


import java.util.List;
import java.util.Map;

/**
 * 验证规则的结果类
 */
public class VerifyResult {
  private static final String SUCCESS = "SUCCESS";
  private static final String ERROR = "ERROR";
  private String result;
  private List<String> errorList;
  public List<ItemError> errorInfoList; //errorList
  private boolean success;
    private List<Map<Object,Object>> importDicData;

    public List<Map<Object, Object>> getImportDicData() {
        return importDicData;
    }

    public void setImportDicData(List<Map<Object, Object>> importDicData) {
        this.importDicData = importDicData;
    }


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public List<String> getErrorList() {
    return errorList;
  }

  public void setErrorList(List<String> errorList) {
    this.errorList = errorList;
  }

  public static String getSUCCESS() {
    return SUCCESS;
  }

  public static String getERROR() {
    return ERROR;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<ItemError> getErrorInfoList() {
    return errorInfoList;
  }

  public void setErrorInfoList(List<ItemError> errorInfoList) {
    this.errorInfoList = errorInfoList;
  }
}
