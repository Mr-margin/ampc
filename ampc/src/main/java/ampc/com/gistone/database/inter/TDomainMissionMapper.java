package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TDomainMission;
import ampc.com.gistone.database.model.TDomainMissionWithBLOBs;

public interface TDomainMissionMapper {
    int deleteByPrimaryKey(Long domainId);

    int insert(TDomainMissionWithBLOBs record);

    int insertSelective(TDomainMissionWithBLOBs record);

    TDomainMissionWithBLOBs selectByPrimaryKey(Long domainId);

    int updateByPrimaryKeySelective(TDomainMissionWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TDomainMissionWithBLOBs record);

    int updateByPrimaryKey(TDomainMission record);
    List<TDomainMissionWithBLOBs> selectAll();
    int updateByUserId(Long userId);
    int deletebyid(Long domainId);
}