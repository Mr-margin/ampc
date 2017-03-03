package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasureExcel;

/**
 * 措施excel映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月3日
 */
public interface TMeasureExcelMapper {
	/**
	 * 查询当前行业措施Excel表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
    int deleteByPrimaryKey(Long measureExcelId);

    int insert(TMeasureExcel record);

    int insertSelective(TMeasureExcel record);

    TMeasureExcel selectByPrimaryKey(Long measureExcelId);

    int updateByPrimaryKeySelective(TMeasureExcel record);

    int updateByPrimaryKey(TMeasureExcel record);
}