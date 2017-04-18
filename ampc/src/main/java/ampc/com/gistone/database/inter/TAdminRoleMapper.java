package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TAdminRole;

public interface TAdminRoleMapper {
    int deleteByPrimaryKey(Long adminRoleId);

    int insert(TAdminRole record);

    int insertSelective(TAdminRole record);

    TAdminRole selectByPrimaryKey(Long adminRoleId);

    int updateByPrimaryKeySelective(TAdminRole record);

    int updateByPrimaryKey(TAdminRole record);
}