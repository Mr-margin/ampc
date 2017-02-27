package ampc.com.gistone.database.model;
/**
 * 情景执行状态表
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public class TScenarinoStatus {
	//情景执行状态ID
    private Long scenarinoStatuId;
    //情景执行状态名称
    private Object scenarinoStatuName;

    public Long getScenarinoStatuId() {
        return scenarinoStatuId;
    }

    public void setScenarinoStatuId(Long scenarinoStatuId) {
        this.scenarinoStatuId = scenarinoStatuId;
    }

    public Object getScenarinoStatuName() {
        return scenarinoStatuName;
    }

    public void setScenarinoStatuName(Object scenarinoStatuName) {
        this.scenarinoStatuName = scenarinoStatuName;
    }
}