package ampc.com.gistone.database.inter;

import java.util.List;

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
	
	
	/**
	 * 获取到所有的措施信息
	 * @param sectorExcelId
	 * @return
	 */
	List<TMeasureExcel> selectAll();
	
	
    int deleteByPrimaryKey(Long measureExcelId);

    int insert(TMeasureExcel record);

    int insertSelective(TMeasureExcel record);

    TMeasureExcel selectByPrimaryKey(Long measureExcelId);

    int updateByPrimaryKeySelective(TMeasureExcel record);

    int updateByPrimaryKey(TMeasureExcel record);
}