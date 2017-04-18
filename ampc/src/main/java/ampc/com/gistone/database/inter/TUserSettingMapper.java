package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TUserSetting;

public interface TUserSettingMapper {
    int deleteByPrimaryKey(Long userSettingId);

    int insert(TUserSetting record);

    int insertSelective(TUserSetting record);

    TUserSetting selectByPrimaryKey(Long userSettingId);

    int updateByPrimaryKeySelective(TUserSetting record);

    int updateByPrimaryKey(TUserSetting record);
}