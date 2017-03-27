package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TTasksStatus;

public interface TTasksStatusMapper {
    int deleteByPrimaryKey(Long tasksId);

    int insert(TTasksStatus record);

    int insertSelective(TTasksStatus record);

    TTasksStatus selectByPrimaryKey(Long tasksId);

    int updateByPrimaryKeySelective(TTasksStatus record);

    int updateByPrimaryKey(TTasksStatus record);

	/**
	 * @Description: TODO
	 * @param scenarinoId
	 * @return   
	 * Long  查询tasksid 通过情景id
	 * @throws
	 * @author yanglei
	 * @date 2017年3月22日 下午3:46:23
	 */
	Long selectTasksId(Long tasksScenarinoId);

	/**
	 * @Description: 获取该情景的时间段
	 * @param scenarinoId
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 上午10:57:29
	 */
	TTasksStatus getRangeTime(Long tasksScenarinoId);
}