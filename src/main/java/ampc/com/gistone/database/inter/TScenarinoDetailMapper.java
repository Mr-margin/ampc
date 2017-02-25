package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TScenarinoDetail;
/**
 * 情景映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月25日
 */
public interface TScenarinoDetailMapper {
	
	 /**
     * 根据任务Id查询情景列表 并可根据情景名模糊查询
     * @param map 参数
     * @return 结果集
     */
    List<Map> selectByMissionIdAndScenarinoName(Map map);
    
    
    /**
     * 根据任务Id查询出所有的情景Id 用来级联删除
     * @return 总条数
     */
    List<Integer> selectByMissionId(long missionId);
    
    
    /**
     * 修改任务是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Integer> list);
	
    int deleteByPrimaryKey(Long scenarinoId);

    int insert(TScenarinoDetail record);

    int insertSelective(TScenarinoDetail record);

    TScenarinoDetail selectByPrimaryKey(Long scenarinoId);

    int updateByPrimaryKeySelective(TScenarinoDetail record);

    int updateByPrimaryKey(TScenarinoDetail record);
}