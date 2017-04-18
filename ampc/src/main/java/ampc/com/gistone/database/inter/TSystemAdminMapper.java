package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TSystemAdmin;

public interface TSystemAdminMapper {
    int deleteByPrimaryKey(Long systemAdminId);

    int insert(TSystemAdmin record);

    int insertSelective(TSystemAdmin record);

    TSystemAdmin selectByPrimaryKey(Long systemAdminId);

    int updateByPrimaryKeySelective(TSystemAdmin record);

    int updateByPrimaryKey(TSystemAdmin record);
}