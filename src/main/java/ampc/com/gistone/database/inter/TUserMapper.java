package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TUser;

/*
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
	
	/**
	 * 用户登录
	 * @param map
	 * @return
	 */
	Map login(Map map);
	
	/**
	 * 判断用户名是否存在
	 * @param userId
	 * @return
	 */
	Integer checkUserId(String account);
	
	
	/**
	 * 判断用户名是否可用
	 * @param userId
	 * @return
	 */
	Integer checkUserIsON(String account);
	/**
	 * @Description: 查找用户账户密码
	 * @param userAccount
	 * @return   
	 * TUser  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月9日 上午9:36:21
	 */
	TUser getUserAccount(String userAccount);
	/**
	 * @Description: 修改密码
	 * @param tUserAccount
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月9日 上午10:02:16
	 */
	int updatePassword(TUser tUserAccount);
	
    int deleteByPrimaryKey(Long userId);

    int insert(TUser record);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(TUser record);

    int updateByPrimaryKey(TUser record);

	


	
}