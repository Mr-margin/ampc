package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TScenarinoDetail;

public interface TScenarinoDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TScenarinoDetail record);

    int insertSelective(TScenarinoDetail record);

    TScenarinoDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TScenarinoDetail record);

    int updateByPrimaryKey(TScenarinoDetail record);
}