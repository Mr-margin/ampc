package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TSectordocExcel;

public interface TSectordocExcelMapper {
	/**
	 * 查询当前行业Excel表中的最高版本号
	 * @param map
	 * @return
	 */
	Long selectMaxVersion(Long userId);
	
	/**
	 * 获取到所有的行业信息
	 * @return
	 */
	List<TSectordocExcel> selectAll();
	
	/**
	 * 获取ID
	 * @param map
	 * @return
	 */
	List<String> selectByUserId(Map map);
	
    int deleteByPrimaryKey(Long sectordocId);

    int insert(TSectordocExcel record);

    int insertSelective(TSectordocExcel record);

    TSectordocExcel selectByPrimaryKey(Long sectordocId);

    int updateByPrimaryKeySelective(TSectordocExcel record);

    int updateByPrimaryKey(TSectordocExcel record);
}