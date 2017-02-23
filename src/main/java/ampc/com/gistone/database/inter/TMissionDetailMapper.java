package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TMissionDetail;

public interface TMissionDetailMapper {
    int deleteByPrimaryKey(Long missionId);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long missionId);
    
    //增加方法返回所有值
    List<TMissionDetail> selectByPrimaryAll();

    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
}