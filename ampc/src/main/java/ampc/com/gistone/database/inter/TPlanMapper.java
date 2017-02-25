package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlan;

public interface TPlanMapper {
    int deleteByPrimaryKey(Long planId);

    int insert(TPlan record);

    int insertSelective(TPlan record);

    TPlan selectByPrimaryKey(Long planId);

    int updateByPrimaryKeySelective(TPlan record);

    int updateByPrimaryKey(TPlan record);
    
    
}