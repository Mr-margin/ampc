package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEsNation;

public interface TEsNationMapper {
    int deleteByPrimaryKey(Long esNationId);

    int insert(TEsNation record);

    int insertSelective(TEsNation record);

    TEsNation selectByPrimaryKey(Long esNationId);

    int updateByPrimaryKeySelective(TEsNation record);

    int updateByPrimaryKey(TEsNation record);
    
    //查询全国清单
    List<Map> selectAllNation(Map nationMap);
    //编辑更新清单信息
    int updateByIdSelective(TEsNation record);
    //查询总条数
	int selectTotalNation(Map nationMap);

	int verifyNationName(TEsNation tEsNation);
}