/**  
 * @Title: TasksEnum1.java
 * @Package ampc.com.gistone.redisqueue.tasksenum
 * @Description: TODO
 * @author yanglei
 * @date 2017年6月5日 下午1:04:47
 * @version 
 */
package ampc.com.gistone.redisqueue.tasksenum;

/**  
 * @Title: TasksEnum1.java
 * @Package ampc.com.gistone.redisqueue.tasksenum
 * @Description: TODO
 * @author yanglei
 * @date 2017年6月5日 下午1:04:47
 * @version 1.0
 */
public enum TasksEnum1 {
	/*WRF("wrf",1),mcip("mcip",2),dp_met("dp_met",3),megan("megan",4),
	meic("meic",5),cmaq("cmaq",6),dp_emis("dp_emis",7),dp_chem("dp_chem",8);*/

	WRF("气象模拟",1),mcip("气象格式转换",2),dp_met("气象后处理",3),megan("天然源排放",4),
	meic("人为源排放",5),cmaq("CMAQ",6),dp_emis("排放后处理",7),dp_chem("化学后处理",8);
	private String name ;
	
	private int index ;

	/**
	
	 * <p>Description: </p>
	
	 * @param name
	 * @param index
	
	 */
	private TasksEnum1(String name, int index) {
		this.name = name;
		this.index = index;
	}
	public static String getname1(int index) {
		for (TasksEnum1 c : TasksEnum1.values()) {
			if (c.getIndex()==index) {
				return c.getName();
			}
		}
		return null;
	}
	 
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}  
	
	
}
