package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TScenarinoArea;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;

public interface TScenarinoAreaMapper {
    int deleteByPrimaryKey(Long scenarinoAreaId);

    int insert(TScenarinoAreaWithBLOBs record);

    int insertSelective(TScenarinoAreaWithBLOBs record);

    TScenarinoAreaWithBLOBs selectByPrimaryKey(Long scenarinoAreaId);

    int updateByPrimaryKeySelective(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKey(TScenarinoArea record);
    
    List<TScenarinoArea> selectByEntity(TScenarinoArea record);
}