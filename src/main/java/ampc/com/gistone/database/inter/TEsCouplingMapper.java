package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TEsCoupling;

public interface TEsCouplingMapper {
    int deleteByPrimaryKey(Long esCouplingId);

    int insert(TEsCoupling record);

    int insertSelective(TEsCoupling record);

    TEsCoupling selectByPrimaryKey(Long esCouplingId);

    int updateByPrimaryKeySelective(TEsCoupling record);

    int updateByPrimaryKey(TEsCoupling record);
}