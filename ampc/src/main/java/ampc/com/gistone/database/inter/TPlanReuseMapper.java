package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlanReuse;
import ampc.com.gistone.database.model.TPlanReuseWithBLOBs;

public interface TPlanReuseMapper {
    int deleteByPrimaryKey(Long planReuseId);

    int insert(TPlanReuseWithBLOBs record);

    int insertSelective(TPlanReuseWithBLOBs record);

    TPlanReuseWithBLOBs selectByPrimaryKey(Long planReuseId);

    int updateByPrimaryKeySelective(TPlanReuseWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TPlanReuseWithBLOBs record);

    int updateByPrimaryKey(TPlanReuse record);
}