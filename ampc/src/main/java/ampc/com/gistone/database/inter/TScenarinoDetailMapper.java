package ampc.com.gistone.database.inter;

import java.util.Date;
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
     * 查询所有任务列表和情景列表 可根据条件名模糊查询
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectAllOrByQueryName(Map map);
    
    
    /**
     * 不分页查询所有有效的条数 可根据条件名模糊查询
     * @return 总条数
     */
    int selectCountOrByQueryName(Map map);
	
	 /**
     * 根据任务Id查询情景列表 并可根据条件名模糊查询
     * @param map 参数
     * @return 结果集
     */
    List<Map> selectByMissionIdAndQueryName(Map map);
    
    
    /**
     * 根据任务Id查询出所有的情景Id 用来级联删除
     * @return 总条数
     */
    List<Long> selectByMissionId(Map map);
    
   
    /**
     * 根据任务ID和情景名称 查询情景ID
     * @param map 参数
     * @return 情景ID
     */
    long selectByMidAndSName(Map map);
    
    
    /**
     * 添加情景对名称重复判断
     * @param map
     * @return
     */
    int check_ScenarinoName(Map map);
    
    /**
     * 修改任务是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Long> list);
	
    int deleteByPrimaryKey(Long scenarinoId);

    int insert(TScenarinoDetail record);

    int insertSelective(TScenarinoDetail record);

    TScenarinoDetail selectByPrimaryKey(Long scenarinoId);

    int updateByPrimaryKeySelective(TScenarinoDetail record);

    int updateByPrimaryKey(TScenarinoDetail record);
    
    List<TScenarinoDetail> selectAllByMissionId(Long missionId);
    
    Date selectMaxEndTime();
}