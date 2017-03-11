package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlanMeasure;




/**
 * 预案措施映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月7日
 */
public interface TPlanMeasureMapper {
	/**
	 * 根据条件查询措施汇总的信息
	 * @param map
	 * @return
	 */
	List<Map> selectByQuery(Map map);
	
	/**
	 * 根据条件查询ID信息
	 * @param map
	 * @return
	 */
	List<Map> selectIdByQuery(Map map);
	
	/**
     * 修改预案措施是否有效
     * @param userId
     * @return
     */
    int updateIsEffeByIds();
	
	List<TPlanMeasure> selectByEntity(TPlanMeasure record);
    
    int deleteByPlanId(Long planId);

    int deleteByPrimaryKey(Long planMeasureId);

    int insert(TPlanMeasure record);

    int insertSelective(TPlanMeasure record);

    TPlanMeasure selectByPrimaryKey(Long planMeasureId);

    int updateByPrimaryKeySelective(TPlanMeasure record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasure record);

    int updateByPrimaryKey(TPlanMeasure record);
}