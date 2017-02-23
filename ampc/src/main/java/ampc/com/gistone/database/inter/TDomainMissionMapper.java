package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TDomainMission;

public interface TDomainMissionMapper {
    int deleteByPrimaryKey(Long domainId);

    int insert(TDomainMission record);

    int insertSelective(TDomainMission record);

    TDomainMission selectByPrimaryKey(Long domainId);

    int updateByPrimaryKeySelective(TDomainMission record);

    int updateByPrimaryKey(TDomainMission record);
}