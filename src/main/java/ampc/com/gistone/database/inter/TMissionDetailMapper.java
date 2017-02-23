package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMissionDetail;

public interface TMissionDetailMapper {
    int deleteByPrimaryKey(Long missionId);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long missionId);

    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
}