package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasureSectorExcel;

/**
 * 行业措施中间表
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月6日
 */
public interface TMeasureSectorExcelMapper {
	/**
	 * 查询当前行业措施Excel表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
	
    int deleteByPrimaryKey(Long msExcelId);

    int insert(TMeasureSectorExcel record);

    int insertSelective(TMeasureSectorExcel record);

    TMeasureSectorExcel selectByPrimaryKey(Long msExcelId);

    int updateByPrimaryKeySelective(TMeasureSectorExcel record);

    int updateByPrimaryKey(TMeasureSectorExcel record);
}