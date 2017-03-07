package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TAddress;

public interface TAddressMapper {
    int deleteByPrimaryKey(Integer addressId);

    int insert(TAddress record);

    int insertSelective(TAddress record);

    TAddress selectByPrimaryKey(Integer addressId);

    int updateByPrimaryKeySelective(TAddress record);

    int updateByPrimaryKey(TAddress record);
    
    List<TAddress> selectBLevel(TAddress record);
    
    List<TAddress> selectAll();
}