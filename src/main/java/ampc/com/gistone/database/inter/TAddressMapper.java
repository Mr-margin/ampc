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
	 * 根据code 查询区域名称
	 * @param code
	 * @return
	 */
	String selectNameByCode(String code);
	
	/**
	 * 根据省级Code查询所有的市级Code
	 * @param code
	 * @return
	 */
	List<String> selectCityCode(String code);
	
    int deleteByPrimaryKey(Integer addressId);

    int insert(TAddress record);

    int insertSelective(TAddress record);

    TAddress selectByPrimaryKey(Integer addressId);

    int updateByPrimaryKeySelective(TAddress record);

    int updateByPrimaryKey(TAddress record);
    
    List<TAddress> selectBLevel(TAddress record);
    
    List<TAddress> selectAll();
}