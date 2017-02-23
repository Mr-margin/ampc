package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TMissionDetail;

/**
 * 任务映射接口
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
public interface TMissionDetailMapper {
    
    /**
     * 查询所有任务列表
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectAll(Map map);
    
    /**
     * 查询任务根据名称
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectByMissionName(Map map);
    
    /**
     * 不分页查询所有任务有效的条数
     * @return 总条数
     */
    int selectCount();
    
    /**
     * 根据名称不分页查询所有任务有效的条数
     * @return 总条数
     */
    int selectByMissionNameCount(String name);
    
    int deleteByPrimaryKey(Long missionId);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long missionId);
    
    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
}