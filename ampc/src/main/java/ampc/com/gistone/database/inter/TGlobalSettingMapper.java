package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TGlobalSetting;

public interface TGlobalSettingMapper {
    int deleteByPrimaryKey(Long globalSettingId);

    int insert(TGlobalSetting record);

    int insertSelective(TGlobalSetting record);

    TGlobalSetting selectByPrimaryKey(Long globalSettingId);

    int updateByPrimaryKeySelective(TGlobalSetting record);

    int updateByPrimaryKey(TGlobalSetting record);
}