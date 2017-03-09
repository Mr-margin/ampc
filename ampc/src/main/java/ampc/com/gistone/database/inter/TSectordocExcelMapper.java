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
	 * 获取ETITLE和中文
	 * @param map
	 * @return
	 */
	List<Map> selectByUserId(Map map);
	
	/**
     * 修改行业是否有效
     * @param userId
     * @return
     */
    int updateIsEffeByIds(Long userId);
    int deleteByPrimaryKey(Long sectordocId);

    int insert(TSectordocExcel record);

    int insertSelective(TSectordocExcel record);

    TSectordocExcel selectByPrimaryKey(Long sectordocId);

    int updateByPrimaryKeySelective(TSectordocExcel record);

    int updateByPrimaryKey(TSectordocExcel record);
}