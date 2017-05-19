package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEsCoupling;
/**
 * 耦合过后的清单
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月28日
 */
public interface TEsCouplingMapper {
	/**
	 * 根据用户Id查询所有清单 
	 * @param userId
	 * @return
	 */
	List<Map> selectAll(Long userId);
    int deleteByPrimaryKey(Long esCouplingId);

    int insert(TEsCoupling record);

    int insertSelective(TEsCoupling record);

    TEsCoupling selectByPrimaryKey(Long esCouplingId);

    int updateByPrimaryKeySelective(TEsCoupling record);

    int updateByPrimaryKey(TEsCoupling record);
    
    //查询全国清单
    List<Map> selectAllNation(Long userId);
    //查询本地清单
    List<Map> selectAllNative(Long userId);
}