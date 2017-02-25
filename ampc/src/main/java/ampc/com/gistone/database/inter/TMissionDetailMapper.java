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
     * 查询所有任务列表 可根据任务名模糊查询
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectAllOrByMissionName(Map map);
    
    
    /**
     * 不分页查询所有任务有效的条数 可根据任务名
     * @return 总条数
     */
    int selectCountOrByMissionName(Map map);
    
    
    /**
     * 修改任务是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Integer> list);
    
    /**
     * 添加任务对名称重复判断
     * @param map
     * @return
     */
    int check_MissioName(Map map);

    int deleteByPrimaryKey(Long missionId);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long missionId);

    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
}