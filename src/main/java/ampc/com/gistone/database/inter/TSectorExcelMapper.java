package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSectorExcel;

/**
 * Excel映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
public interface TSectorExcelMapper {
	/**
	 * 查询当前行业Excel表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
	
    int deleteByPrimaryKey(Long sectorExcelId);

    int insert(TSectorExcel record);

    int insertSelective(TSectorExcel record);

    TSectorExcel selectByPrimaryKey(Long sectorExcelId);

    int updateByPrimaryKeySelective(TSectorExcel record);

    int updateByPrimaryKey(TSectorExcel record);
}