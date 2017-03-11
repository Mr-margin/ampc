package ampc.com.gistone.database.inter;

import java.util.Map;

import ampc.com.gistone.database.model.TPlan;

public interface TPlanMapper {
	/**
	 * 根据条件查询ID
	 * @param map
	 * @return
	 */
	int getIdByQuery(Map map);

    int deleteByPrimaryKey(Long planId);

    int insert(TPlan record);

    int insertSelective(TPlan record);

    TPlan selectByPrimaryKey(Long planId);

    int updateByPrimaryKeySelective(TPlan record);

    int updateByPrimaryKey(TPlan record);
}