package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlanMeasure;

public interface TPlanMeasureMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPlanMeasure record);

    int insertSelective(TPlanMeasure record);

    TPlanMeasure selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPlanMeasure record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasure record);

    int updateByPrimaryKey(TPlanMeasure record);
}