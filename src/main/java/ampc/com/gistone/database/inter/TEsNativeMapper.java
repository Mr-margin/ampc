package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TEsNative;

public interface TEsNativeMapper {
    int deleteByPrimaryKey(Long esNativeId);

    int insert(TEsNative record);

    int insertSelective(TEsNative record);

    TEsNative selectByPrimaryKey(Long esNativeId);

    int updateByPrimaryKeySelective(TEsNative record);

    int updateByPrimaryKey(TEsNative record);
}