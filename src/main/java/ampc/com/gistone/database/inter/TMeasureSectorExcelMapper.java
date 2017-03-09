package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

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
	
	/**
	 * 根据用户ID 查询行业措施数据
	 * @param msExcelId
	 * @return
	 */
	List<Map> selectByUserId(Long userId);
	
	/**
	 * 根据用户ID 获取所有的行业名称
	 * @param msExcelId
	 * @return
	 */
	List<Map> getSectorInfo(Long userId);
	
	/**
	 * 根据条件查询对应的措施
	 * @param msExcelId
	 * @return
	 */
	List<Map> getMeasureInfo(Map map);
    
	int deleteByPrimaryKey(Long msExcelId);

    int insert(TMeasureSectorExcel record);

    int insertSelective(TMeasureSectorExcel record);

    TMeasureSectorExcel selectByPrimaryKey(Long msExcelId);

    int updateByPrimaryKeySelective(TMeasureSectorExcel record);

    int updateByPrimaryKey(TMeasureSectorExcel record);
}