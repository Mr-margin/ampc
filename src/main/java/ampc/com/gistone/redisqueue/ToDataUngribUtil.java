/**  
 * @Title: ToDataUngribUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月23日 下午2:47:03
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
		TUngrib tUngrib = new TUngrib();
		JSONObject fromObject = JSONObject.fromObject(rpop);
		String id = (String) fromObject.get("id");//消息的id
		String time = (String) fromObject.get("time");//消息的时间
		String type = (String) fromObject.get("type");//消息的类型
		Map<String,Object> body =(Map<String,Object>) fromObject.getJSONObject("body");
		String pathdate = (String) body.get("pathdate");//起报时间
		Date pathdateDate = DateUtil.StrtoDateYMD(pathdate,"yyyyMMdd");
		JSONArray fnldata  = (JSONArray) body.get("fnl");//fnl消息数组
		Object[] fnlArrray = fnldata.toArray();
		Integer fnlstatus =Integer.parseInt(fnlArrray[0].toString());
		JSONArray gfsdata  = (JSONArray)body.get("gfs");//gfs消息数组
		StringBuffer buffer = new StringBuffer();
		//把fnl到gfs的全部状态拼接到一起
		buffer.append(fnlstatus.toString());
		Object[] gfsobObjects = gfsdata.toArray();
		for (int i = 0; i < gfsobObjects.length; i++) {
			buffer.append(gfsobObjects[i]);
		}
		String status1 = buffer.toString();
		String status;
		if (status1.contains("0")) {
			status =status1.substring(0, status1.indexOf("0"));
			status =status+"0";
		}else {
			status=status1;
		}
		System.out.println(status+"zhege shi status");
		JSONArray fnlDescdata  = (JSONArray) body.get("fnlDesc");//fnl错误描述
		Object[] fnlDescArrray = fnlDescdata.toArray();
		String fnlerror = fnlDescArrray[0].toString();
		JSONArray gfsDescdata  = (JSONArray) body.get("gfsDesc");//gfs错误描述
		Object[] gfsDescArrray = gfsDescdata.toArray();
		String gfserror = null;
		for (int i = 0; i < gfsDescArrray.length; i++) {
			String gfsString = gfsDescArrray[i].toString();
			if(!StringUtils.isEmpty(gfsString)) {
				 gfserror = gfsDescArrray[i].toString();//gfs错误的信息
			}
		}
		System.out.println("我到这里了");
		//String gfserror = gfsBuffer.toString().trim();
		//查询该条ungrib是否存在，存在则修改，否则添加
		tUngrib.setErrorFnlMsg(fnlerror);
		tUngrib.setErrorGfsMsg(gfserror);
		TUngrib tUngrib2 = new TUngrib();
		System.out.println("我要通过时间去查询是否存在ungrib");
		tUngrib2 = tUngribMapper.selectUngrib(pathdateDate);
		//System.out.println(UngribId+"查询出来的id");
		System.out.println("我要给fnl和gfs赋值了");
		//调用方法给每个gfs的状态赋值
		tUngrib = ToDataUngribUtil.updateGFSstatus(tUngrib,status);
		//如果存在则执行更新操作，不存在则执行添加操作
		if (tUngrib2!=null) {
			tUngrib.setAddTime(tUngrib2.getAddTime());
			tUngrib.setPathDate(pathdateDate);
			tUngrib.setUngribId(tUngrib2.getUngribId());
			//备注表示修改时间
			tUngrib.setUpdateTime(new Date());
			System.out.println("我马上要进行跟新数据库了");
			//添加到数据库
			tUngribMapper.updateByPrimaryKey(tUngrib);
			System.out.println("我跟新玩数据库了");
		}else {
			//执行添加操作
			tUngrib.setAddTime(new Date());
			tUngrib.setPathDate(pathdateDate);
			//添加到数据库
			int i = tUngribMapper.insert(tUngrib);
		}
		
	}

	/**
	 * @Description: TODO
	 * @param tUngrib
	 * @param index
	 * @return   
	 * TUngrib  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月23日 下午5:24:35
	 */
	private static TUngrib updateGFSstatus(TUngrib tUngrib, String status) {
		String[] fileds = new String[]{"FnlStatus","Gfs1Status","Gfs2Status","Gfs3Status","Gfs4Status","Gfs5Status","Gfs6Status","Gfs7Status","Gfs8Status","Gfs9Status"};
		for(int h = 0; h < status.length(); h++){
			if (status.charAt(h) == '1') {
				try {
					String methodName = "set"+ fileds[h];
					Method method = tUngrib.getClass().getMethod(methodName,new Class[]{Integer.class});
					method.invoke(tUngrib, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				try {
					if (status.charAt(h) == '0'&&h<10) {
						String methodName2 = "set"+ fileds[h];
						Method method = tUngrib.getClass().getMethod(methodName2,new Class[]{Integer.class});
						method.invoke(tUngrib, 0);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		return tUngrib;
	}
	
	

}
