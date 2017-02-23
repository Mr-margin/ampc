package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlanMeasureReuse;

public interface TPlanMeasureReuseMapper {
    int deleteByPrimaryKey(Long planMeasureReuseId);

    int insert(TPlanMeasureReuse record);

    int insertSelective(TPlanMeasureReuse record);

    TPlanMeasureReuse selectByPrimaryKey(Long planMeasureReuseId);

    int updateByPrimaryKeySelective(TPlanMeasureReuse record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasureReuse record);

    int updateByPrimaryKey(TPlanMeasureReuse record);
}