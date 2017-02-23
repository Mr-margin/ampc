package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TScenarinoDetail;

public interface TScenarinoDetailMapper {
    int deleteByPrimaryKey(Long scenarinoId);

    int insert(TScenarinoDetail record);

    int insertSelective(TScenarinoDetail record);

    TScenarinoDetail selectByPrimaryKey(Long scenarinoId);

    int updateByPrimaryKeySelective(TScenarinoDetail record);

    int updateByPrimaryKey(TScenarinoDetail record);
}