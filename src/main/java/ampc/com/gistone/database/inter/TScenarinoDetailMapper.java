package ampc.com.gistone.database.inter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TScenarinoDetail;

/**
 * 情景映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月25日
 */
public interface TScenarinoDetailMapper {
	
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
	 * @Description: 通过pathdate获取情景ID和cores
	 * @param today
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 下午4:55:47
	 */
	TScenarinoDetail getidAndcores(Date today);


	/**
	 * @Description:获取可执行所有的的预评估情景
	 * @return   
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 下午5:47:07
	 */
	List<TScenarinoDetail> getscenidAndcores();


	/**
	 * @Description: 获取最大的可执行预评估的时间
	 * @return   
	 * Date  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月8日 下午6:07:16
	 */
	Date getmaxtime();
	
	/**
	 * @Description: TODO
	 * @param lastpathdate
	 * @return   通过起报时间查询实时预报情景的全部情景
	 * TScenarinoDetail  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月6日 上午11:06:00
	 */
	TScenarinoDetail getbufaScenID(Date lastpathdate);
	
	
	/**
	 * @Description: TODO
	 * @param tasksScenarinoId
	 * @return   
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
	 * @Description: TODO
	 * @param tScenarinoDetail   
	 * void  更新cores
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
    
    
    /**
     * 不分页查询所有有效的条数 可根据条件名模糊查询
     * @return 总条数
     */
    int selectCountOrByQueryName(Map map);
	
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

	
    TScenarinoDetail selectMaxEndTime4();

	


	


	

	
	

	
}