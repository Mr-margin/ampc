package ampc.com.gistone.database.inter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TScenarinoDetail;
import ampc.com.gistone.database.model.TTasksStatus;

/**
 * 情景映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月25日
 */
public interface TScenarinoDetailMapper {
	
	/**
	 * @Description: 获取情景的开始和结束时间
	 * @param scenarinoId
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月17日 下午2:08:15
	 */
	TScenarinoDetail selectStartAndEndDate(Long scenarinoId);
	
	/**
	 * 查询所有的错误情景总数
	 * @param userId
	 * @return
	 */
	Long selectErrorCount(Long userId);
	/**
	 * 查询所有的错误情景所在的任务ID
	 */
	List<Long> selectMissionIdByErrorScenarino(Long userId);
	
	/**
	 * @Description: 修改情景类型
	 * @param map
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月12日 下午7:28:47
	 */
	int updateScenType(Map map);
	/**
	 * @Description: 查询实时预报数据
	 * @param map
	 * @return   
	 * TTasksStatus  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月14日 下午4:27:24
	 */
	TScenarinoDetail selectScenarinoDetail(Map map);
	/**
	 * @param scenarinoId 
	 * @Description: 通过情景ID获取任务ID
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月14日 下午7:15:32
	 */
	Long selectMissionidByID(Long scenarinoId);
	
	/**
	 * @Description: 通过刚创建的实时预报情景的到该情景的ID
	 * @param tScenarinoDetail
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月11日 上午11:51:47
	 */
	Long selectforecastid(TScenarinoDetail tScenarinoDetail);
	/**
	 * @Description: 通过pathdate查询实时预报的ID
	 * @param basisTime
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月11日 上午11:21:39
	 */
	Long selectBasisId(Date basisTime);
	
	/**
	 * @Description:用户 通过pathdate获取情景ID和cores
	 * @param map
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 下午4:55:47
	 */
	TScenarinoDetail getidByuserIdAndpathdate(Map map);


	/**
	 * @Description:获取可执行所有的的预评估情景
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 下午5:47:07
	 */
	List<TScenarinoDetail> selectpreEvaluationSituation();

	/**
	 * @Description: 通过起报时间查询用户的实时预报情景的全部情景
	 * @param parmap
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 上午11:06:00
	 */
	TScenarinoDetail getForecastScenID(Map parmap);
	
	
	/**
	 * @Description: TODO
	 * @param tasksScenarinoId
	 * @return    SCENARINO_START_DATE, SCENARINO_END_DATE,SCEN_TYPE,PATH_DATE
	 * TScenarinoDetail  查找开始结束时间和情景类型
	 * @throws
	 * @author yanglei
	 * @date 2017年3月31日 上午11:54:53
	 */
	TScenarinoDetail selecttypetime(Long scenarinoId);

	
	/**
	 * @Description: TODO
	 * @param tasksScenarinoId
	 * @return   
	 * String  通过情景ID查找情景类型
	 * @throws
	 * @author yanglei
	 * @date 2017年3月30日 下午6:57:58
	 */
	String selectscentype(Long scenarinoId);
	
	/**
	 * @Description: 更新cores
	 * @param tScenarinoDetail   
	 * void  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月29日 下午6:22:06
	 */
	int updateCores(TScenarinoDetail tScenarinoDetail);

	/**
	 * @Description: 根据起报时间 用户id 情景类型  查找该条情景记录 用来确定实时预报消息里面的firsttime的值
	 * @param zoreDate
	 * @param userId
	 * @param scenarinoType
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年3月25日 下午4:53:34
	 */
	TScenarinoDetail selectFirstTime(Map map);
	
	 /**
     * 查询所有任务列表和情景列表 可根据条件名模糊查询
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectAllOrByQueryName(Map map);
    
    List<Map> selectAllOrByQueryName2(Map map);
    /**
     * 不分页查询所有有效的条数 可根据条件名模糊查询
     * @return 总条数
     */
    int selectCountOrByQueryName(Map map);
    int selectCountOrByQueryName2(Map map);
	 /**
     * 根据任务Id查询情景列表 并可根据条件名模糊查询
     * @param map 参数
     * @return 结果集
     */
    List<Map> selectByMissionIdAndQueryName(Map map);
    
    
    
    /**
     * 根据任务Id查询出所有的情景Id 用来级联删除
     * @return 总条数
     */
    List<Long> selectByMissionId(Map map);
    
   
    /**
     * 根据任务ID和情景名称 查询情景ID
     * @param map 参数
     * @return 情景ID
     */
    long selectByMidAndSName(Map map);
    
    
    /**
     * 添加情景对名称重复判断
     * @param map
     * @return
     */
    int check_ScenarinoName(Map map);
    
    /**
     * 修改任务是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Long> list);
    
  List<TScenarinoDetail> selectAllByMissionId(Long missionId);
    
  TScenarinoDetail selectMaxEndTime(Long missionId);
    
    TScenarinoDetail selectid(TScenarinoDetail tScenarinoDetail);
    
    int deleteByPrimaryKey(Long scenarinoId);

    int insert(TScenarinoDetail record);

    int insertSelective(TScenarinoDetail record);

    TScenarinoDetail selectByPrimaryKey(Long scenarinoId);

    int updateByPrimaryKeySelective(TScenarinoDetail record);

    int updateByPrimaryKey(TScenarinoDetail record);


    List<TScenarinoDetail> selectByEntity(TScenarinoDetail record);

    List<TScenarinoDetail> selectByEntity2(TScenarinoDetail record);
    
    TScenarinoDetail selectMaxEndTime4();
    
    /**
	 * 查询基准情景 
	 * */
    TScenarinoDetail selectBystandard(TScenarinoDetail record);
	/**
	 * @Description: 获取运行最新的状态的实时预报情景
	 * @param userId
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月24日 下午5:42:52
	 */
	Date getlastrunstatus(Long userId);
	/**
	 * @Description: 通过情景类型和pathdate查询所有的情景
	 * @param map
	 * @return   
	 * List<TScenarinoDetail>  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月25日 上午10:23:55
	 */
	List<TScenarinoDetail> selectAllByPathdateAndtype(Map map);

    TScenarinoDetail selectByrealmin(Long missionId);
	
    TScenarinoDetail selectByrealmax(Long missionId);
    List<TScenarinoDetail> selectBytype4(Map map);
    /**
     * 查询空气质量预报--时间序列
     * @param map
     * @return
     * @author weitan
     */
    TScenarinoDetail selectScenarinoDetail_timeSeries(Map map);

	/**
	 * @Description: 通过情景ID获取情景的执行状态
	 * @param scenarinoId
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年5月5日 下午2:46:24
	 */
	Long selectScenExecStatus(Long scenarinoId);
	

	/**
	 * TODO  新页面的情景查询方法
	 * @param map
	 * @return
	 */
	List<Map> selectByMissionIdNew(Map map);
	
	/**
	 * 查询有多少个模式处理中的情景总数
	 * @return
	 */
	Long selectMiddleStatusCount(Map map);
	/**
	 * 查询结束时间
	 * @param userId
	 * @return
	 */
	TScenarinoDetail selectendStart(Map map);

	/**
	 * @Description: 查找用户某个情类型的全部情景
	 * @param hashmap --  userId  scenType 
	 * List  
	 * @return 
	 * @throws
	 * @author yanglei
	 * @date 2017年5月22日 下午5:27:19
	 */
	List<TScenarinoDetail> selectListFirstTime(Map hashmap);
	/**
	 * 根据开始时间查询sid
	 * @param tscdetail
	 * @return
	 */
	TScenarinoDetail selectbysid(Map tscdetail);
	Date selectmaxpathdate();
	Date selectminpathdate();

	/**
	 * @Description: 查找所用用户正在执行的实时预报
	 * @param map
	 * @return   
	 * List<TScenarinoDetail>  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月2日 下午7:33:20
	 */
	List<TScenarinoDetail> selectAllOnExecByPathdateAndtype(Map map);

	/**
	 * @Description: 修改情景执行的错误描述
	 * @param errormsgMap
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月8日 下午8:12:33
	 */
	int updateModelErrorMSG(Map errormsgMap);

	/**
	 * @Description: 查找对最大的实时预报情景
	 * @param userId
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月12日 下午7:19:41
	 */
	Date getmaxRealpreiction(Long userId);
	
	int selectAllcountOrByQueryName2(Map map);
}