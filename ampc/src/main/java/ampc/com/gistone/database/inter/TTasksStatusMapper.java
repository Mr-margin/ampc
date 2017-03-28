package ampc.com.gistone.database.inter;

import java.util.Date;

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
	 * @param tasksStatus   
	 * void   得到模式执行的结果，并更新数据库
	 * @throws
	 * @author yanglei
	 * @date 2017年3月27日 下午7:14:46
	 */
	int updateStatus(TTasksStatus tasksStatus);

	/**
	 * @Description: TODO
	 * @param scenarinoId
	 * @return   
	 * TTasksStatus  测试方法 通过情景ID查询情景结束时间
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午10:11:36
	 */
	Date selectByscenarinoId(Long scenarinoId);
}