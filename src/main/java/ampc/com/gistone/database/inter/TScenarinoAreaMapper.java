package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TScenarinoArea;
import ampc.com.gistone.database.model.TScenarinoAreaWithBLOBs;
/**
 * 区域映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public interface TScenarinoAreaMapper {
	
	/**
	 * 根据情景ID 查询区域ID和区域名称
	 * @param map 参数
	 * @return
	 */
	List<Map> selectByScenarinoId(Map map);
	
	/**
	 * 根据情景ID 查询区域ID
	 * @param map 参数
	 * @return
	 */
	List<Long> selectBySid(Map map);
	
	/**
	 * 根据区域ID 查询区域信息
	 * @param map 参数
	 * @return
	 */
	Map selectByAreaId(Map map);
	
	/**
	 * 根据情景ID 区域名称 查询区域ID
	 * @param map 参数
	 * @return
	 */
	long selectAreaIdByParam(Map map);
	
	/**
     * 修改区域是否有效
     * @param missionId
     * @return
     */
    int updateIsEffeByIds(List<Long> list);
    
    /**
     * 添加区域对名称重复判断
     * @param map
     * @return
     */
    int check_AreaName(Map map);
    
    /**
     * 查询区域条数 根据情景ID
     * @return 总条数
     */
    int selectCountByScenarinoId(Map map);
	
	
    int deleteByPrimaryKey(Long scenarinoAreaId);

    int insert(TScenarinoAreaWithBLOBs record);

    int insertSelective(TScenarinoAreaWithBLOBs record);

    TScenarinoAreaWithBLOBs selectByPrimaryKey(Long scenarinoAreaId);

    int updateByPrimaryKeySelective(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKey(TScenarinoArea record);
    
    List<TScenarinoArea> selectByEntity(TScenarinoArea record);
}