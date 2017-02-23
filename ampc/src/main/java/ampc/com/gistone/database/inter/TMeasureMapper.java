package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMeasure;

public interface TMeasureMapper {
    int deleteByPrimaryKey(Long measureId);

    int insert(TMeasure record);

    int insertSelective(TMeasure record);

    TMeasure selectByPrimaryKey(Long measureId);

    int updateByPrimaryKeySelective(TMeasure record);

    int updateByPrimaryKey(TMeasure record);
}