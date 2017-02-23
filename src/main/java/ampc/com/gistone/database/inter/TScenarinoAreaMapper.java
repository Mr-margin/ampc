package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TScenarinoArea;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;

public interface TScenarinoAreaMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TScenarinoAreaWithBLOBs record);

    int insertSelective(TScenarinoAreaWithBLOBs record);

    TScenarinoAreaWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKey(TScenarinoArea record);
}