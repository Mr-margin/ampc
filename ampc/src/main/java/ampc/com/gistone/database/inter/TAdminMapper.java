package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TAdmin;

public interface TAdminMapper {
    int deleteByPrimaryKey(Long adminId);

    int insert(TAdmin record);

    int insertSelective(TAdmin record);

    TAdmin selectByPrimaryKey(Long adminId);

    int updateByPrimaryKeySelective(TAdmin record);

    int updateByPrimaryKey(TAdmin record);
}