package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlanMeasureReuse;
import ampc.com.gistone.database.model.TPlanMeasureReuseWithBLOBs;
/**
 * 可复用预案措施映射
 * @author WangShanxi
 */
public interface TPlanMeasureReuseMapper {
	/**
	 * 根据条件查询所有复用的预案措施
	 * @param map
	 * @return
	 */
	List<TPlanMeasureReuseWithBLOBs> selectByQuery(Map map);
	
    int deleteByPrimaryKey(Long planMeasureReuseId);

    int insert(TPlanMeasureReuseWithBLOBs record);

    int insertSelective(TPlanMeasureReuseWithBLOBs record);

    TPlanMeasureReuseWithBLOBs selectByPrimaryKey(Long planMeasureReuseId);

    int updateByPrimaryKeySelective(TPlanMeasureReuseWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasureReuseWithBLOBs record);

    int updateByPrimaryKey(TPlanMeasureReuse record);
}