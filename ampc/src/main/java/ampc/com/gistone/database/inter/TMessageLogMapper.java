package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TMessageLog;

public interface TMessageLogMapper {
    int deleteByPrimaryKey(Long messageLogId);

    int insert(TMessageLog record);

    int insertSelective(TMessageLog record);

    TMessageLog selectByPrimaryKey(Long messageLogId);

    int updateByPrimaryKeySelective(TMessageLog record);

    int updateByPrimaryKey(TMessageLog record);
}