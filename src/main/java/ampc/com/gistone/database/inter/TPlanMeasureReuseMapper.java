package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlanMeasureReuse;
import ampc.com.gistone.database.model.TPlanMeasureReuseWithBLOBs;

public interface TPlanMeasureReuseMapper {
    int deleteByPrimaryKey(Long planMeasureReuseId);

    int insert(TPlanMeasureReuseWithBLOBs record);

    int insertSelective(TPlanMeasureReuseWithBLOBs record);

    TPlanMeasureReuseWithBLOBs selectByPrimaryKey(Long planMeasureReuseId);

    int updateByPrimaryKeySelective(TPlanMeasureReuseWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasureReuseWithBLOBs record);

    int updateByPrimaryKey(TPlanMeasureReuse record);
}