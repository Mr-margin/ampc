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
    List<TDomainMissionWithBLOBs> selectAll(Long userId);
    int updateByUserId(Long userId);
    int deletebyid(Long domainId);
    TDomainMissionWithBLOBs selectbynameanddoc(TDomainMissionWithBLOBs record);
    int updateByValid(Long domainId);
    int updateByValidtwo(Long userId);
    TDomainMissionWithBLOBs selectbyname(TDomainMissionWithBLOBs record);
    TDomainMissionWithBLOBs selectByuserIdandValtd(TDomainMissionWithBLOBs record);

	/**
	 * @Description: 获取domain-common的数据
	 * @param domainId
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月20日 下午7:29:25
	 */
	String selectCommonData(Long domainId);
}