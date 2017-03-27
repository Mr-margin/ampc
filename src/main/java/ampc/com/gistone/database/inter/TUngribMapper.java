package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TUngrib;

public interface TUngribMapper {
    int deleteByPrimaryKey(Long ungribId);

    int insert(TUngrib record);

    int insertSelective(TUngrib record);

    TUngrib selectByPrimaryKey(Long ungribId);

    int updateByPrimaryKeySelective(TUngrib record);

    int updateByPrimaryKey(TUngrib record);
}