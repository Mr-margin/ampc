package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TRole;

public interface TRoleMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(TRole record);

    int insertSelective(TRole record);

    TRole selectByPrimaryKey(Long roleId);

    int updateByPrimaryKeySelective(TRole record);

    int updateByPrimaryKey(TRole record);
}