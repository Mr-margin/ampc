package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEsCoupling;

public interface TEsCouplingMapper {
	/**
	 * 查询模板Id
	 * @param couplingMap
	 * @return
	 */
	Long selectTIdByCId(Map couplingMap);
	List<Map> selectAllCoupling(Map couplingMap);
	
	int selectTotalCoupling(Long userId);
	
	int updateByIdSelective(TEsCoupling tEsCoupling);
	
	/**
	 * 根据用户Id查询所有清单 
	 * @param userId
	 * @return
	 */
	List<Map> selectAll(Long userId);
    int deleteByPrimaryKey(Long esCouplingId);

    int insert(TEsCoupling record);

    int insertSelective(TEsCoupling record);

    TEsCoupling selectByPrimaryKey(Long esCouplingId);

    int updateByPrimaryKeySelective(TEsCoupling record);

    int updateByPrimaryKey(TEsCoupling record);
    
	int updateStatusByPrimaryKey(TEsCoupling tEsCoupling);
	
	int updateDataByPrimaryKey(TEsCoupling tEsCoupling);
}