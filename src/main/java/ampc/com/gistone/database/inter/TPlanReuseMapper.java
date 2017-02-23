package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TPlanReuse;

public interface TPlanReuseMapper {
    int deleteByPrimaryKey(Long planReuseId);

    int insert(TPlanReuse record);

    int insertSelective(TPlanReuse record);

    TPlanReuse selectByPrimaryKey(Long planReuseId);

    int updateByPrimaryKeySelective(TPlanReuse record);

    int updateByPrimaryKey(TPlanReuse record);
}