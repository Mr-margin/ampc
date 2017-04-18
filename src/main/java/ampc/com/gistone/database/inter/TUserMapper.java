package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TUser;

public interface TUserMapper {
    int deleteByPrimaryKey(Long userId);

	int insert(TUser record);

	int insertSelective(TUser record);

	TUser selectByPrimaryKey(Long userId);

	int updateByPrimaryKeySelective(TUser record);

	int updateByPrimaryKey(TUser record);

	
}