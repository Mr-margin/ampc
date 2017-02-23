package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlan;

public interface TPlanMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPlan record);

    int insertSelective(TPlan record);

    TPlan selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPlan record);

    int updateByPrimaryKey(TPlan record);
}