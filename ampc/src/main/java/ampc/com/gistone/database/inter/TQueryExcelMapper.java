package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TQueryExcel;

public interface TQueryExcelMapper {
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
	List<TQueryExcel> selectAll();
	
	/**
	 * 获取到所有的行业信息
	 * @return
	 */
	List<TQueryExcel> selectByMap(Map map);
    int deleteByPrimaryKey(Long queryId);

    int insert(TQueryExcel record);

    int insertSelective(TQueryExcel record);

    TQueryExcel selectByPrimaryKey(Long queryId);

    int updateByPrimaryKeySelective(TQueryExcel record);

    int updateByPrimaryKey(TQueryExcel record);
}