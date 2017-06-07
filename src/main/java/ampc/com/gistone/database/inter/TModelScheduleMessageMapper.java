package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TModelScheduleMessage;

public interface TModelScheduleMessageMapper {
    int deleteByPrimaryKey(Long executeScheduleId);

    int insert(TModelScheduleMessage record);

    int insertSelective(TModelScheduleMessage record);

    TModelScheduleMessage selectByPrimaryKey(Long executeScheduleId);

    int updateByPrimaryKeySelective(TModelScheduleMessage record);

    int updateByPrimaryKey(TModelScheduleMessage record);
}