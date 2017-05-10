/**  
 * @Title: ToDataUngribUtil.java
 * @Package ampc.com.gistone.redisqueue
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月23日 下午2:47:03
 * @version 
 */
package ampc.com.gistone.redisqueue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ampc.com.gistone.database.inter.TGlobalSettingMapper;
import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.inter.TTasksStatusMapper;
import ampc.com.gistone.database.inter.TUngribMapper;
import ampc.com.gistone.database.model.TUngrib;
import ampc.com.gistone.redisqueue.result.Message;
import ampc.com.gistone.util.DateUtil;
import ampc.com.gistone.util.JsonUtil;
import ampc.com.gistone.util.LogUtil;

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
	//加载globalsetting映射
	@Autowired
	private TGlobalSettingMapper tGlobalSettingMapper;
	//tasks映射
	@Autowired
	private TTasksStatusMapper tTasksStatusMapper;
	//情景详情映射
	@Autowired
	private  TScenarinoDetailMapper tScenarinoDetailMapper;

	/**
	 * @Description: TODO
	 * @param rpop   
	 * void   持久化ungrib数据方法
	 * @throws
	 * @author yanglei
	 * @date 2017年3月23日 下午2:50:22
	 */
	public void updateDB(String rpop) {
		//创建ungrib对象
		TUngrib tUngrib = new TUngrib();
		JSONObject fromObject = JSONObject.fromObject(rpop);
		String id = (String) fromObject.get("id");//消息的id
		String time = (String) fromObject.get("time");//消息的时间
		String type = (String) fromObject.get("type");//消息的类型
		Map<String,Object> body =(Map<String,Object>) fromObject.getJSONObject("body");
		String pathdate = (String) body.get("pathdate");//起报时间
		try {
			if (null!=pathdate) {
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
					buffer.append(gfsobObjects[i].toString().trim());
				}
				//接受到的整个fnl到gfs的状态
				String status1 = buffer.toString();
				//需要更新的fnl到gfs的状态
				String status;
				//去正常运行的结果  当出现错误的时候  （0）则后面的不会跟新数据库  
				if (status1.contains("0")) {
					status =status1.substring(0, status1.indexOf("0"));
					status =status+"0";
				}else {
					status=status1;
				}
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
				//查询该条ungrib是否存在，存在则修改，否则添加
				tUngrib.setErrorFnlMsg(fnlerror);
				tUngrib.setErrorGfsMsg(gfserror);
				LogUtil.getLogger().info("我要给fnl和gfs赋值了");
				//调用方法给每个gfs的状态赋值
				tUngrib = ToDataUngribUtil.updateGFSstatus(tUngrib,status);
				//判断上一天的ungrib是否成功
				Date yestdate = DateUtil.ChangeDay(pathdateDate, -1);
				TUngrib yesTUngrib = tUngribMapper.selectUngrib(yestdate);
				if (null!=yesTUngrib) {
					//创建对象
					TUngrib tUngrib2 = tUngribMapper.selectUngrib(pathdateDate);
					//如果存在则执行更新操作，不存在则执行添加操作
					if (tUngrib2!=null) {
						//判断跟新还是不跟新ungrib
						int updateORNo = UpdateORNo(tUngrib2);
						if (updateORNo<10) {
							tUngrib.setAddTime(tUngrib2.getAddTime());
							tUngrib.setPathDate(pathdateDate);
							tUngrib.setUngribId(tUngrib2.getUngribId());
							//备注表示修改时间
							tUngrib.setUpdateTime(new Date());
							//更新到数据库
							int i = tUngribMapper.updateByPrimaryKey(tUngrib);
							if (i>0) {
								LogUtil.getLogger().info("跟新ungrib"+new Date());
								
							}else {
								LogUtil.getLogger().info("跟新ungrib失败！时间："+new Date());
							}
						}else {
							LogUtil.getLogger().info("ungrib已经全部更新完毕l！不需要更新了！");
						}
					}else {
						//执行添加操作
						tUngrib.setAddTime(new Date());
						tUngrib.setPathDate(pathdateDate);
						//添加到数据库
						int i = tUngribMapper.insert(tUngrib);
						if (i>0) {
							LogUtil.getLogger().info("添加了最新的ungrib数据！");
						}
					}
				}else {
					LogUtil.getLogger().info("上一天："+yestdate+"的ungrib没有更新！不允许跳跃跟新ungrib！");
				}
				
			}else {
				LogUtil.getLogger().info("ungrib的pathdate参数错误！");
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.getLogger().error("ungrib参数出错！",e);
		}
		
	}



	/**
	 * @Description: TODO
	 * @param tUngrib2   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月20日 上午11:49:30
	 */
	public int UpdateORNo(TUngrib tUngrib2) {
	//	boolean flag;
		StringBuffer buffer = new StringBuffer();
		Integer fnlStatus = tUngrib2.getFnlStatus();
		buffer.append(fnlStatus);
		Integer gfs1Status = tUngrib2.getGfs1Status();
		buffer.append(gfs1Status);
		Integer gfs2Status = tUngrib2.getGfs2Status();
		buffer.append(gfs2Status);
		Integer gfs3Status = tUngrib2.getGfs3Status();
		buffer.append(gfs3Status);
		Integer gfs4Status = tUngrib2.getGfs4Status();
		buffer.append(gfs4Status);
		Integer gfs5Status = tUngrib2.getGfs5Status();
		buffer.append(gfs5Status);
		Integer gfs6Status = tUngrib2.getGfs6Status();
		buffer.append(gfs6Status);
		Integer gfs7Status = tUngrib2.getGfs7Status();
		buffer.append(gfs7Status);
		Integer gfs8Status = tUngrib2.getGfs8Status();
		buffer.append(gfs8Status);
		Integer gfs9Status = tUngrib2.getGfs9Status();
		buffer.append(gfs9Status);
		String len = buffer.toString();
		//截取有用的ungrib数据
		int length;
		if (len.contains("0")) {
			 length = len.substring(0, len.indexOf("0")).length();
		}else if(len.contains("null")){
			length = len.substring(0, len.indexOf("null")).length();
		}else {
			 length = len.length();
		}
		LogUtil.getLogger().info("ungrib的长度："+length);
//		if (length<=10) {
//			flag = true;
//		}else {
//			flag = false;
//		}
		return length;
		
	}

	/**
	 * @Description: 给fnl和gfs赋值
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
	 /**
	  * 解析ungrib数据 并更新数据库
	  */
	@Transactional
	public void  updateUngrib(String rpop) {
		LogUtil.getLogger().info("开始解析ungrib数据");
		//创建ungrib对象
		TUngrib tUngrib = new TUngrib();
		try {
			Message message = JsonUtil.jsonToObj(rpop, Message.class);
			try {
				Date time = message.getTime();
				Object object = message.getBody();
				Map ungribmap = (Map) object;
				String pathdate = (String) ungribmap.get("pathdate");//起报时间
				if (null!=pathdate&&!StringUtils.isEmpty(pathdate)) {
					try {
						Date pathdateDate = DateUtil.StrtoDateYMD(pathdate,"yyyyMMdd");
						StringBuffer buffer = new StringBuffer();
						List fnlList  = (List) ungribmap.get("fnl");
						Integer fnl = Integer.parseInt(fnlList.get(0).toString());
						buffer.append(fnl.toString());
						List gfsList = (List) ungribmap.get("gfs");
						for (int i = 0; i < gfsList.size(); i++) {
							buffer.append(gfsList.get(i));
						}
						//接受到的整个fnl到gfs的状态
						String status1 = buffer.toString();
						//需要更新的fnl到gfs的状态
						String status;
						//去正常运行的结果  当出现错误的时候  （0）则后面的不会跟新数据库  
						if (status1.contains("0")) {
							status =status1.substring(0, status1.indexOf("0"));
							status =status+"0";
						}else {
							status=status1;
						}
						List fnlDescList = (List) ungribmap.get("fnlDesc");
						String fnlerror = fnlDescList.get(0).toString();  //fnl错误描述
						List gfsDescList = (List) ungribmap.get("gfsDesc");
						String gfserror = null;  
						for (int i = 0; i < gfsDescList.size(); i++) {
							String gfsString = gfsDescList.get(i).toString();
							if(!StringUtils.isEmpty(gfsString)) {
								gfserror = gfsString;//gfs错误的信息
							}
						}
						//查询该条ungrib是否存在，存在则修改，否则添加
						tUngrib.setErrorFnlMsg(fnlerror);
						tUngrib.setErrorGfsMsg(gfserror);
						LogUtil.getLogger().info("我要给fnl和gfs赋值了");
						//调用方法给每个gfs的状态赋值
						tUngrib = ToDataUngribUtil.updateGFSstatus(tUngrib,status);
						//判断上一天的ungrib是否成功
						Date yestdate = DateUtil.ChangeDay(pathdateDate, -1);
						try {
							TUngrib yesTUngrib = tUngribMapper.selectUngrib(yestdate);
							if (null!=yesTUngrib) {
								//创建对象
								try {
									TUngrib tUngrib2 = tUngribMapper.selectUngrib(pathdateDate);
									//如果存在则执行更新操作，不存在则执行添加操作
									if (tUngrib2!=null) {
										//判断跟新还是不跟新ungrib
										int updateORNo = UpdateORNo(tUngrib2);
										if (updateORNo<10) {
											tUngrib.setAddTime(tUngrib2.getAddTime());
											tUngrib.setPathDate(pathdateDate);
											tUngrib.setUngribId(tUngrib2.getUngribId());
											//备注表示修改时间
											tUngrib.setUpdateTime(new Date());
											//更新到数据库
											try {
												int i = tUngribMapper.updateByPrimaryKey(tUngrib);
												if (i>0) {
													LogUtil.getLogger().info("跟新ungrib"+new Date());
												}else {
													LogUtil.getLogger().info("跟新ungrib失败！时间："+new Date());
												}
											} catch (Exception e) {
												LogUtil.getLogger().error("跟新ungrib出异常！"+e.getMessage());
											}
										}else {
											LogUtil.getLogger().info("ungrib已经全部更新完毕l！不需要更新了！");
										}
									}else {
										//执行添加操作
										tUngrib.setAddTime(new Date());
										tUngrib.setPathDate(pathdateDate);
										//添加到数据库
										try {
											int i = tUngribMapper.insert(tUngrib);
											if (i>0) {
												LogUtil.getLogger().info("添加了最新的ungrib数据！");
											}else {
												LogUtil.getLogger().info("添加最新的ungrib数据失败！");
											}
										} catch (Exception e) {
											LogUtil.getLogger().error("添加了最新的ungrib数据出异常了"+e.getMessage());
										}
									}
								} catch (Exception e) {
									LogUtil.getLogger().error("查找当前pathdate的ungrib出错！"+e.getMessage());
								}
							}else {
								LogUtil.getLogger().info("上一天："+yestdate+"的ungrib没有更新！不允许跳跃跟新ungrib！");
							}
						} catch (Exception e) {
							LogUtil.getLogger().error("查找上一天的ungrib出错！"+e.getMessage());
						}
					} catch (Exception e) {
						LogUtil.getLogger().error("ungrib的pathdate格式不对！"+e.getMessage());
					}
				}else {
					LogUtil.getLogger().info("ungrib的pathdate参数为空！");
				}
			} catch (Exception e) {
				LogUtil.getLogger().error("ungrib的更新出错！"+e.getMessage());
			}
		} catch (IOException e1) {
			LogUtil.getLogger().error("ungrib转换异常！",e1);
		}
	}

}
