package ampc.com.gistone.database.inter;

import java.util.Date;
import java.util.Map;

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

	/**
	 * @Description: TODO
	 * @param scenarinoId
	 * @return   
	 * TTasksStatus  查询时间段 开始时间 结束时间等
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午11:32:45
	 */
	TTasksStatus getRangeTime(Long tasksScenarinoId);

	/**
	 * @param pathdate 
	 * @Description: TODO
	 * @return   
	 * TTasksStatus   查询数据库中tasks的状态
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午7:31:13
	 */
	TTasksStatus selectStatus(Long tasksScenarinoId);

	/**
	 * @Description: TODO
	 * @param tasksStatus   
	 * void   跟新数据库的情景运行了之后的可用状态
	 * @throws
	 * @author yanglei
	 * @date 2017年4月5日 上午11:45:01
	 */
	void updateRunstatus(TTasksStatus tasksStatus);

	/**
	 * @Description: 获取运行最新的状态的实时预报情景
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月5日 下午8:50:21
	 */
	Date getlastrunstatus();
}