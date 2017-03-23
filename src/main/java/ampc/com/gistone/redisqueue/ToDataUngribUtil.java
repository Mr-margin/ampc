/**  
 * @Title: ToDataUngribUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月23日 下午2:47:03
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.text.SimpleDateFormat;
import java.util.Date;

import oracle.net.aso.a;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.util.DateUtil;

/**  
 * @Title: ToDataUngribUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO 把每天接到的ungrib数据持久化到数据库工具类
 * @author yanglei
 * @date 2017年3月23日 下午2:47:03
 * @version 1.0
 */
@Component
public class ToDataUngribUtil {
	//加载ungribMapper映射
	@Autowired
	private TUngribMapper tUngribMapper;

	/**
	 * @Description: TODO
	 * @param rpop   
	 * void   持久化ungrib数据方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月23日 下午2:50:22
	 */
	public void updateDB(String rpop) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(rpop);
			String id = (String) jsonObject.get("id");//消息的id
			String time = (String) jsonObject.get("time");//消息的时间
			String type = (String) jsonObject.get("type");//消息的类型
			String pathdate = (String) jsonObject.get("pathdate");//起报时间
			Date pathdateDate = DateUtil.StrtoDateYMD(pathdate);
			int[] fnlArrray = (int[]) jsonObject.get("fnl");//fnl消息数组
			Integer fnl = fnlArrray[0];
			int[] gfsArrray = (int[]) jsonObject.get("gfs");//gfs消息数组
			int length = gfsArrray.length;
			//找到执行成功的之前的gfs并放入gfs数组中
			int index = 0;
			int[] gfs = null;
			if (fnl==0) {
				for (int i = 0; i < length; i++) {
					int a = gfsArrray[i];
					if (a==0) {
						index = i;
						gfs[i] = a;
					}
				}
			}
			String[] fnlDesc = (String[]) jsonObject.get("fnlDesc");
			String[] gfsDesc = (String[]) jsonObject.get("gfsDesc");
			TUngrib tUngrib = new TUngrib();
			tUngrib.setFnlStatus(fnl);
			Long UngribId = tUngribMapper.selectUngrib(pathdateDate);
			//如果存在则执行更新操作，不存在则执行添加操作
			if (UngribId!=null) {
				tUngrib.setBeizhu(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()).toString());
				for (int i = 0; i <index; i++) {
					
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
