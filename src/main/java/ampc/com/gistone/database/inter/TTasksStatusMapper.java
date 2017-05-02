package ampc.com.gistone.database.inter;

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
	 * TTasksStatus  测试方法 通过情景ID查询情景的模式执行情况
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午10:11:36
	 */
	TTasksStatus selectendByscenarinoId(Long tasksScenarinoId);

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
	 * @Description: 跟新数据库的情景运行了之后的可用状态
	 * @param tasksStatus   
	 * void   
	 * @throws
	 * @author yanglei
	 * @date 2017年4月5日 上午11:45:01
	 */
	void updateRunstatus(TTasksStatus tasksStatus);



	

	/**
	 * @Description: 查找该条情景的执行情况的最后一天
	 * @param scenarinoId
	 * @return   
	 * TTasksStatus  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 下午6:42:50
	 */
	TTasksStatus gettaskEnddate(Long scenarinoId);

	/**
	 * @Description: 跟新已经发送的预评估时间
	 * @param tTasksStatus   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 下午7:46:26
	 */
	int updatemessageStatus(TTasksStatus tTasksStatus);

	/**
	 * @Description: 添加对应情景的减排系数
	 * @param tTasksStatus
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月12日 上午9:25:10
	 */
	int updateEmisData(TTasksStatus tTasksStatus);

	/**
	 * @Description: 通过情景ID获取该情景的减排系数
	 * @param scenarinoId
	 * @return   
	 * TTasksStatus  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月13日 下午7:44:27
	 */
	TTasksStatus selectentityByScenId(Long tasksScenarinoId);
	
	int updateinf(Long scenarinoId);

	/**
	 * @Description: 通过pathdate和情景类型获取该情景的模式执行状态
	 * @param map
	 * @return   
	 * TTasksStatus  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月15日 下午6:05:27
	 */
	TTasksStatus selectTasksstatusByPathdate(Map map);

	/**
	 * @Description: 修改模式停止的状态
	 * @param tTasksStatus
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月26日 下午5:59:37
	 */
	int updatestopstatus(TTasksStatus tTasksStatus);

	/**
	 * @Description: 修改模式终止返回的状态
	 * @param tTasksStatus
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月26日 下午9:28:59
	 */
	int updatestopModelresult(TTasksStatus tTasksStatus);

	/**
	 * @Description: 查询模式执行返回的结果
	 * @param tasksScenarinoId
	 * @return   
	 * String  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月27日 上午11:54:13
	 */
	String selectStartModelresult(Long tasksScenarinoId);

	/**
	 * @Description:修改模式暂停返回
	 * @param tTasksStatus
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月28日 上午10:27:15
	 */
	int updatepausestatus(TTasksStatus tTasksStatus);

}