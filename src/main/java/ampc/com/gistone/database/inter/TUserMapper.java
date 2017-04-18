package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TUser;

/**
 * 用户映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年4月13日
 */
public interface TUserMapper {
	
	/**
	 * 查询所有的用户信息
	 * @return 返回用户信息集合
	 */
	List<Map> selectUserList();
	
	Integer login(Map map);
    int deleteByPrimaryKey(Long userId);

	int insert(TUser record);

	int insertSelective(TUser record);

	TUser selectByPrimaryKey(Long userId);

	int updateByPrimaryKeySelective(TUser record);

	int updateByPrimaryKey(TUser record);

	
}