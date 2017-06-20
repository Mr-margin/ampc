package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TSectorExcel;

/**
 * 行业映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月6日
 */
public interface TSectorExcelMapper {
	
	
	/**
	 * 获取到所有的行业信息
	 * @return
	 */
	List<TSectorExcel> selectAll(Map map);
	
	 /**
     * 修改行业是否有效
     * @param userId
     * @return
     */
    int updateIsEffeByIds(Long userId);
    
    
    /**
     * 根据模板ID和用户ID删除对应的行业描述数据
     * @param map
     * @return
     */
    int deleteByQuery(Map map);
    int deleteByPrimaryKey(Long sectorExcelId);

    int insert(TSectorExcel record);

    int insertSelective(TSectorExcel record);

    TSectorExcel selectByPrimaryKey(Long sectorExcelId);

    int updateByPrimaryKeySelective(TSectorExcel record);

    int updateByPrimaryKey(TSectorExcel record);

	List<String> selectIndustryById(Long nationTpId);

	String selectVersionsExcelId(TSectorExcel tSectorExcel);
    
}