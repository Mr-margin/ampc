package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSector2;

public interface TSector2Mapper {
    int deleteByPrimaryKey(Long id);

    int insert(TSector2 record);

    int insertSelective(TSector2 record);

    TSector2 selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TSector2 record);

    int updateByPrimaryKey(TSector2 record);
}