package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TEsNativeTp;

public interface TEsNativeTpMapper {
    int deleteByPrimaryKey(Long esNativeTpId);

    int insert(TEsNativeTp record);

    int insertSelective(TEsNativeTp record);

    TEsNativeTp selectByPrimaryKey(Long esNativeTpId);

    int updateByPrimaryKeySelective(TEsNativeTp record);

    int updateByPrimaryKey(TEsNativeTp record);
}