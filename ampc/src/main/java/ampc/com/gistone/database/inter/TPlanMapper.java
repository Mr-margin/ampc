package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlan;

/**
 * 预案映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月13日
 */
public interface TPlanMapper {
	/**
	 * 根据条件查询ID
	 * @param map
	 * @return
	 */
	Long getIdByQuery(Map map);
	
	/**
	 * 根据条件查询ID
	 * @param map
	 * @return
	 */
	Map getInfoByQuery(Map map);
	
	/**
	 * 根据条件查询ID
	 * @param map
	 * @return
	 */
	List<Long> selectByAreaId(Map map);
	

    int deleteByPrimaryKey(Long planId);

    int insert(TPlan record);

    int insertSelective(TPlan record);

    TPlan selectByPrimaryKey(Long planId);

    int updateByPrimaryKeySelective(TPlan record);

    int updateByPrimaryKey(TPlan record);
    List<TPlan> selectByEnty(TPlan record);
}