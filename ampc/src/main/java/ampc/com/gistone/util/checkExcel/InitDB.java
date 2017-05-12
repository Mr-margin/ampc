package ampc.com.gistone.util.checkExcel;

import java.io.File;

/**
 * 程序入口
 */
public class InitDB {
	//解析层对象
    private static InitService initService = new InitService();

    /**
     * 主函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
    	  //初始化excel校验规则
        initValidate();               
    }

    //初始化excel校验规则
    public static void initValidate()throws Exception{
        String path = "E:/xlsx1/";
        File file=new File(path);
        File[] tempList = file.listFiles();
        for (File p : tempList){
        	//调用验证方法
            initService.initValidate(p.getCanonicalPath());
        }
    }
}
