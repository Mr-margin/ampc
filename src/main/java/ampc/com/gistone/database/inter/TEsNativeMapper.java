package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEsNative;
/**
 * 清单映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月28日
 */
public interface TEsNativeMapper {
	/**
	 * 根据用户Id查询所有清单 
	 * @param userId
	 * @return
	 */
	List<Map> selectAll(Long userId);
	
    int deleteByPrimaryKey(Long esNativeId);

    int insert(TEsNative record);

    int insertSelective(TEsNative record);

    TEsNative selectByPrimaryKey(Long esNativeId);

    int updateByPrimaryKeySelective(TEsNative record);

    int updateByPrimaryKey(TEsNative record);
}