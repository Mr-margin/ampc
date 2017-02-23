package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TEsNation;

public interface TEsNationMapper {
    int deleteByPrimaryKey(Long esNationId);

    int insert(TEsNation record);

    int insertSelective(TEsNation record);

    TEsNation selectByPrimaryKey(Long esNationId);

    int updateByPrimaryKeySelective(TEsNation record);

    int updateByPrimaryKey(TEsNation record);
}