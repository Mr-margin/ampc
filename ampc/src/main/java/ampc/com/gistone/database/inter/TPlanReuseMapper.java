package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlanMeasureReuseWithBLOBs;
import ampc.com.gistone.database.model.TPlanReuse;
import ampc.com.gistone.database.model.TPlanReuseWithBLOBs;


/**
 * 预案库映射
 * @author WangShanxi
 *
 */
public interface TPlanReuseMapper {
	/**
	 * 根据条件查询ID
	 * @param map
	 * @return
	 */
	Long getIdByQuery(Map map);
	/**
	 * 查询当前用户下的所有可复制预案 
	 * @param userId
	 * @return
	 */
	List<Map> selectCopyList(Long userId);
	
	
	
    int deleteByPrimaryKey(Long planReuseId);

    int insert(TPlanReuseWithBLOBs record);

    int insertSelective(TPlanReuseWithBLOBs record);

    TPlanReuseWithBLOBs selectByPrimaryKey(Long planReuseId);

    int updateByPrimaryKeySelective(TPlanReuseWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TPlanReuseWithBLOBs record);

    int updateByPrimaryKey(TPlanReuse record);
}