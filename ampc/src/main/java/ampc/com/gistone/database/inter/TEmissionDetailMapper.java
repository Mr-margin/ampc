package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEmissionDetail;
import ampc.com.gistone.database.model.TEmissionDetailWithBLOBs;

public interface TEmissionDetailMapper {
	

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