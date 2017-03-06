package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TSectorExcel;

/**
 * 行业映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月6日
 */
public interface TSectorExcelMapper {
	/**
	 * 查询当前行业Excel表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
	
	/**
	 * 获取到所有的行业信息
	 * @param sectorExcelId
	 * @return
	 */
	List<TSectorExcel> selectAll();
	
    int deleteByPrimaryKey(Long sectorExcelId);

    int insert(TSectorExcel record);

    int insertSelective(TSectorExcel record);

    TSectorExcel selectByPrimaryKey(Long sectorExcelId);

    int updateByPrimaryKeySelective(TSectorExcel record);

    int updateByPrimaryKey(TSectorExcel record);
}