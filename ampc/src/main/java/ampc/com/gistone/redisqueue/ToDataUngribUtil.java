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
		TUngrib tUngrib = new TUngrib();
		try {
			jsonObject = new JSONObject(rpop);
			String id = (String) jsonObject.get("id");//消息的id
			String time = (String) jsonObject.get("time");//消息的时间
			String type = (String) jsonObject.get("type");//消息的类型
			String pathdate = (String) jsonObject.get("pathdate");//起报时间
			Date pathdateDate = DateUtil.StrtoDateYMD(pathdate);
			String[] fnlDesc = (String[]) jsonObject.get("fnlDesc");//fnl错误描述
			String[] gfsDesc = (String[]) jsonObject.get("gfsDesc");//gfs错误描述
			int[] fnlArrray = (int[]) jsonObject.get("fnl");//fnl消息数组
			Integer fnl = fnlArrray[0];
			int[] gfsArrray = (int[]) jsonObject.get("gfs");//gfs消息数组
			int length = gfsArrray.length;
			//找到执行成功的之前的gfs并放入gfs数组中
			int index = 0;//gfs成功的天数
			int[] gfs = new int[length];//gfs实际的记录
			//fnl等于1表示成功
			if (fnl==1) {
				for (int i = 0; i < length; i++) {
					int a = gfsArrray[i];
					if (a==0) {
						index = i;
						gfs[i] = a;
					}
				}
			}else {
				tUngrib.setErrorFnlMsg(fnlDesc[0]);//添加fnl的错误信息描述
			}
			tUngrib.setFnlStatus(fnl);
			Long UngribId = tUngribMapper.selectUngrib(pathdateDate);
			//调用方法给每个gfs的状态赋值
			tUngrib = ToDataUngribUtil.updateGFSstatus(tUngrib,index);
			//如果存在则执行更新操作，不存在则执行添加操作
			if (UngribId!=null) {
				tUngrib.setUngribId(UngribId);
				//备注表示修改时间
				tUngrib.setBeizhu(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()).toString());
				//添加到数据库
				tUngribMapper.updateByPrimaryKey(tUngrib);
			}else {
				//执行添加操作
				tUngrib.setAddTime(new Date());
				tUngrib.setPathDate(pathdateDate);
				//添加到数据库
				int i = tUngribMapper.insert(tUngrib);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	private static TUngrib updateGFSstatus(TUngrib tUngrib, int index) {
		
		switch (index) {
		case 1:
			tUngrib.setGfs1Status(1);
			break;
		case 2:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			break;
		case 3:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			break;
		case 4:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			break;
		case 5:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			tUngrib.setGfs5Status(1);
			break;
		case 6:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			tUngrib.setGfs5Status(1);
			tUngrib.setGfs6Status(1);
			break;
		case 7:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			tUngrib.setGfs5Status(1);
			tUngrib.setGfs6Status(1);
			tUngrib.setGfs7Status(1);
			break;
		case 8:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			tUngrib.setGfs5Status(1);
			tUngrib.setGfs6Status(1);
			tUngrib.setGfs7Status(1);
			tUngrib.setGfs8Status(1);
			break;
		case 9:
			tUngrib.setGfs1Status(1);
			tUngrib.setGfs2Status(1);
			tUngrib.setGfs3Status(1);
			tUngrib.setGfs4Status(1);
			tUngrib.setGfs5Status(1);
			tUngrib.setGfs6Status(1);
			tUngrib.setGfs7Status(1);
			tUngrib.setGfs8Status(1);
			tUngrib.setGfs9Status(1);
			break;

		default:
			break;
		}
		return tUngrib;
	}
	

}
