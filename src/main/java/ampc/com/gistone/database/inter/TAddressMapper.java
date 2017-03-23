package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TAddress;

/**
 * 行政区划代码
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年3月23日
 */
public interface TAddressMapper {
	/**
	 * 根据code 查询对应的信息
	 * @param code
	 * @return
	 */
	List<Long> selectByCode(String code);
	
    int deleteByPrimaryKey(Integer addressId);

    int insert(TAddress record);

    int insertSelective(TAddress record);

    TAddress selectByPrimaryKey(Integer addressId);

    int updateByPrimaryKeySelective(TAddress record);

    int updateByPrimaryKey(TAddress record);
    
    List<TAddress> selectBLevel(TAddress record);
    
    List<TAddress> selectAll();
}