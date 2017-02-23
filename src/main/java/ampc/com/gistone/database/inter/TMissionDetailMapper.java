package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMissionDetail;

public interface TMissionDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
}