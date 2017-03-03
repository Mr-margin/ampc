package ampc.com.gistone.database.inter;

import java.util.Map;

import ampc.com.gistone.database.model.TSector;

/**
 * 行业映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
public interface TSectorMapper {
	/**
	 * 查询当前行业表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
	
	
    int deleteByPrimaryKey(Long sectorId);

    int insert(TSector record);

    int insertSelective(TSector record);

    TSector selectByPrimaryKey(Long sectorId);

    int updateByPrimaryKeySelective(TSector record);

    int updateByPrimaryKey(TSector record);
}