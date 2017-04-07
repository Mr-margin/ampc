package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlanReuse;
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

    int insert(TPlanReuse record);

    int insertSelective(TPlanReuse record);

    TPlanReuse selectByPrimaryKey(Long planReuseId);

    int updateByPrimaryKeySelective(TPlanReuse record);

    int updateByPrimaryKey(TPlanReuse record);
}