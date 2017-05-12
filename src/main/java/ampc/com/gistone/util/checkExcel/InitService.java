package ampc.com.gistone.util.checkExcel;

import java.util.List;

/**
 * 验证层
 */
public class InitService {

	//验证方法
    public void initValidate(String canonicalPath) {
        String validateName = null;
        if (canonicalPath.lastIndexOf("\\") != -1){
            validateName = canonicalPath.substring((canonicalPath.lastIndexOf("\\")+1),(canonicalPath.lastIndexOf("_")));
        }else {
            validateName = canonicalPath.substring((canonicalPath.lastIndexOf("/")+1),(canonicalPath.lastIndexOf("_")));
        }
        try {
            List<ValidateModel> validateModelList = new CreateVerifyFileUtil().createVerifyFile(canonicalPath,validateName);
            // TODO 把数据插入到数据库
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
