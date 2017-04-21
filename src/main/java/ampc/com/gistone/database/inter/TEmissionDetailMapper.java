package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TEmissionDetailWithBLOBs;

public interface TEmissionDetailMapper {
	/**
	 * 根据条件查询对应市级Code
	 * @param map
	 * @return
	 */
	List<String> selectCityCode(Map map);
	
	/**
	 * 根据条件查询对应的结果集  用于减排列表的显示
	 * @param map
	 * @return
	 */
	List<TEmissionDetailWithBLOBs> selectByQuery(Map map);

 List<TEmissionDetailWithBLOBs> selectByEntity(TEmissionDetail record);
    TEmissionDetail selectBycodeAndDate(TEmissionDetail record);
    
    int deleteByScenarunoId(Long scenarunoId);
    int deleteByPrimaryKey(Long emissionId);

    int insert(TEmissionDetailWithBLOBs record);

    int insertSelective(TEmissionDetailWithBLOBs record);

    TEmissionDetailWithBLOBs selectByPrimaryKey(Long emissionId);

    int updateByPrimaryKeySelective(TEmissionDetailWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TEmissionDetailWithBLOBs record);

    int updateByPrimaryKey(TEmissionDetail record);
}