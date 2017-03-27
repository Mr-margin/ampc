package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TTasksStatus;

public interface TTasksStatusMapper {
    int deleteByPrimaryKey(Long tasksId);

    int insert(TTasksStatus record);

    int insertSelective(TTasksStatus record);

    TTasksStatus selectByPrimaryKey(Long tasksId);

    int updateByPrimaryKeySelective(TTasksStatus record);

    int updateByPrimaryKey(TTasksStatus record);
}