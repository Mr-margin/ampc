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
public enum TasksEnum2 {
	
	meic("meic",1),cmaq("cmaq",2),dp_emis("dp_emis",3),dp_chem("dp_chem",4);

	private String name ;
	
	private int index ;

	/**
	
	 * <p>Description: </p>
	
	 * @param name
	 * @param index
	
	 */
	private TasksEnum2(String name, int index) {
		this.name = name;
		this.index = index;
	}
	public static String getname2(int index) {
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
