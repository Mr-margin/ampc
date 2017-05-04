/**  
 * @Title: DomainDataMeic.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午6:51:38
 * @version 
 */
package ampc.com.gistone.redisqueue.entity;


/**  
 * @Title: DomainDataMeic.java
 * @Package ampc.com.gistone.redisqueue.entity
 * @Description: TODO
 * @author yanglei
 * @date 2017年4月19日 下午6:51:38
 * @version 1.0
 */
public class DomainDataMeic {
	
	private Meagen megan;
	private ForModel model;

	  public Meagen getMegan() {
	    return megan;
	  }

	  public void setMegan(Meagen megan) {
	    this.megan = megan;
	  }

	  public ForModel getModel() {
	    return model;
	  }

	  public void setModel(ForModel model) {
	    this.model = model;
	  }

	  public static class Meagen {
	    private String shutdown;
	    private String version;

	    public String getVersion() {
	      return version;
	    }

	    public void setVersion(String version) {
	      this.version = version;
	    }

	    public String getShutdown() {
	      return shutdown;
	    }

	    public void setShutdown(String shutdown) {
	      this.shutdown = shutdown;
	    }
	  }

	  public static class ForModel {
	    private String name;
	    private String submodel;

	    public String getName() {
	      return name;
	    }

	    public void setName(String name) {
	      this.name = name;
	    }

	    public String getSubmodel() {
	      return submodel;
	    }

	    public void setSubmodel(String submodel) {
	      this.submodel = submodel;
	    }
	  }

}
