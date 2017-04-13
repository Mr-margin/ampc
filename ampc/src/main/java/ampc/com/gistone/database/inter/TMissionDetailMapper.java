package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TMissionDetail;

/**
 * 任务映射接口
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
public interface TMissionDetailMapper {
    
    /**
     * 查询所有任务列表 可根据条件名模糊查询
     * @param map 分页参数
     * @return 结果集
     */
    List<Map> selectAllOrByQueryName(Map map);
    
    
    /**
     * 不分页查询所有任务有效的条数 可根据条件名
     * @return 总条数
     */
    int selectCountOrByQueryName(Map map);
    
    
    /**
     * 修改任务是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Integer> list);
    
    /**
     * 添加任务对名称重复判断
     * @param map
     * @return
     */
    int check_MissioName(Map map);

    int deleteByPrimaryKey(Long missionId);

    int insert(TMissionDetail record);

    int insertSelective(TMissionDetail record);

    TMissionDetail selectByPrimaryKey(Long missionId);

    int updateByPrimaryKeySelective(TMissionDetail record);

    int updateByPrimaryKey(TMissionDetail record);
    /**
     * 
     * @Description: TODO
     * @param missionId
     * @return   
     * int  查询domainid
     * @throws
     * @author yanglei
     * @date 2017年3月18日 下午5:55:44
     */
    Long selectDomainid(Long missionId);
    
    List<TMissionDetail> selectByEntity(TMissionDetail record);


	/**
	 * @Description: TODO
	 * @param missionId
	 * @return   
	 * Long   通过情景ID获取清单ID
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 下午6:51:51
	 */
	Long getsourceid(Long missionId);


	/**
	 * @Description: 通过用户ID获取missionid
	 * @param userId
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月10日 下午7:32:34
	 */
	Long getmissionid(Long userId);


	/**
	 * @Description: 通过用户ID确定实时预报任务
	 * @param userIds
	 * @return   
	 * List<TMissionDetail>  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月10日 下午8:24:03
	 */
	List<TMissionDetail> selectMissionDetail(Long userId);


	/**
	 * @Description: 修改实时预报任务
	 * @param updateOlDetail
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月11日 上午10:01:47
	 */
	int updateOldMissionDetail(TMissionDetail updateOlDetail);


	/**
	 * @Description: 根据新插入的任务 查找对应的ID
	 * @param missionDetail
	 * @return   
	 * Long  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月11日 上午10:12:33
	 */
	Long getmissionidbyMission(TMissionDetail missionDetail);
}