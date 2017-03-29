package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSite;

public interface TSiteMapper {
    int insert(TSite record);

    int insertSelective(TSite record);
}