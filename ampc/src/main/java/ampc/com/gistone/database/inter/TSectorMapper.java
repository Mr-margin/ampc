package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSector;

public interface TSectorMapper {
    int deleteByPrimaryKey(Long sectorId);

    int insert(TSector record);

    int insertSelective(TSector record);

    TSector selectByPrimaryKey(Long sectorId);

    int updateByPrimaryKeySelective(TSector record);

    int updateByPrimaryKey(TSector record);
}