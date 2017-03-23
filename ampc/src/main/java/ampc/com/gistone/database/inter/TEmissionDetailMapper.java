package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEmissionDetail;

public interface TEmissionDetailMapper {
	/**
	 * 根据条件查询对应的结果集
	 * @param map
	 * @return
	 */
	List<TEmissionDetail> selectByMap(Map map);
	
    int deleteByPrimaryKey(Long emissionId);

    int insert(TEmissionDetail record);

    int insertSelective(TEmissionDetail record);

    TEmissionDetail selectByPrimaryKey(Long emissionId);

    int updateByPrimaryKeySelective(TEmissionDetail record);

    int updateByPrimaryKeyWithBLOBs(TEmissionDetail record);

    int updateByPrimaryKey(TEmissionDetail record);
    
    List<TEmissionDetail> selectByEntity(TEmissionDetail record);
    TEmissionDetail selectBycodeAndDate(TEmissionDetail record);
}