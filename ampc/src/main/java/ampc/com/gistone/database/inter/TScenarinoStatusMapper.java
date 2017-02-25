package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TScenarinoStatus;

public interface TScenarinoStatusMapper {
    int insert(TScenarinoStatus record);

    int insertSelective(TScenarinoStatus record);
}