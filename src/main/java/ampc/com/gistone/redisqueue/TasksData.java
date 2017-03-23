package ampc.com.gistone.redisqueue;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  
 * @Title: TasksMap.java
 * @Package ampc.com.gistone.redisqueuedata
 * @Description: TODO
 * @author yanglei
 * @date 2017年3月16日 下午6:29:16
 * @version 1.0
 */

public class TasksData {
	
	/*
	 * tasks的类型
	 * 目前就两种情况
	 * 
	 */
	public Map<String, List<String>> tasks(Integer scenarinoType) {
		ArrayList<String> list = new ArrayList<String>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		switch (scenarinoType) {
		case 1:
		case 4:
			list.add("wrf");
			list.add("mcip");
			list.add("megan");
			list.add("meic");
			list.add("cmaq");
			list.add("dp");
			map.put("tasks", list);
			break;
		case 2:
		case 3:
		case 5:
			list.add("meic");
			list.add("cmaq");
			list.add("dp");
			map.put("tasks", list);
			break;
		default:
			String string = "";
			list.add(string);
			map.put("tasks", list);
			break;
		}
		/*
		if (scenarinoType==1) {//实时预报情景
			list.add("wrf");
			list.add("mcip");
			list.add("megan");
			list.add("meic");
			list.add("cmaq");
			map.put("tasks", list);
		}else if(scenarinoType==2){//预评估情景
			list.add("meic");
			list.add("cmaq");
			map.put("tasks", list);
		}else if (scenarinoType==3) {//后评估情景
			list.add("meic");
			list.add("cmaq");
			map.put("tasks", list);
		}else if (scenarinoType==4) {//基准情景
			list.add("wrf");
			list.add("mcip");
			list.add("megan");
			list.add("meic");
			list.add("cmaq");
			map.put("tasks", list);
			
		}else if (scenarinoType==5) {//后评估情景
			list.add("meic");
			list.add("cmaq");
			map.put("tasks", list);
		}*/
			
		return map;
	}
	

}
