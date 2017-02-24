package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TTime;

public interface TTimeMapper {
    int deleteByPrimaryKey(Long timeId);

    int insert(TTime record);

    int insertSelective(TTime record);

    TTime selectByPrimaryKey(Long timeId);

    int updateByPrimaryKeySelective(TTime record);
    
    int updateByPrimaryKey(TTime record);
    
    List<TTime> selectByPrimaryKeysort(TTime record);
}