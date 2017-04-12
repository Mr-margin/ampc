package ampc.com.gistone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.context.annotation.Bean;

import net.sf.json.JSONObject;

public class JSONreadUtil {
	@Bean
	public static JSONObject getCalculation_Template(){
		//String classFilePath = this.getClass().getResource("QuestTemplate.class").toString();
//		System.out.println(classFilePath);
		String classFilePath = ("C:\\Users\\Administrator\\Desktop\\result.json");
		System.out.println(classFilePath);
		String sets=ReadFile(classFilePath);//获得json文件的内容
		JSONObject JSONVal = JSONObject.fromObject(sets);//格式化成json对象
		return JSONVal;
	}
	

public static String ReadFile(String path){
		File file = new File(path);
		BufferedReader reader = null;
		String laststr = "";
		try {
			//System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			//一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				//显示行号
				System.out.println("line "+line+": "+tempString);
				laststr += tempString;
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					
				}
			}
		}
		return laststr;
	}

}
