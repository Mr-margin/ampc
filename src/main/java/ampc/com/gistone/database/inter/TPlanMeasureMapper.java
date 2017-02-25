package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TPlanMeasure;

public interface TPlanMeasureMapper {
    int deleteByPrimaryKey(Long planMeasureId);

    int insert(TPlanMeasure record);

    int insertSelective(TPlanMeasure record);

    TPlanMeasure selectByPrimaryKey(Long planMeasureId);

    int updateByPrimaryKeySelective(TPlanMeasure record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasure record);

    int updateByPrimaryKey(TPlanMeasure record);
    
    List<TPlanMeasure> selectByEntity(TPlanMeasure record);
}