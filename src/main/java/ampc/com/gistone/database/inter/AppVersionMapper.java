package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.AppVersion;

public interface AppVersionMapper {
    int deleteByPrimaryKey(Integer pkid);

    int insert(AppVersion record);

    int insertSelective(AppVersion record);

    AppVersion selectByPrimaryKey(Integer pkid);

    int updateByPrimaryKeySelective(AppVersion record);

    int updateByPrimaryKey(AppVersion record);
}