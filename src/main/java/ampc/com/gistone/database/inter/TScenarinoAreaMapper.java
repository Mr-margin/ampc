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
	 * @param map
	 * @return
	 */
	List<Map> selectByScenarinoId(Map map);
	
	/**
	 * 根据区域ID 查询区域信息
	 * @param map
	 * @return
	 */
	Map selectByAreaId(Map map);
	
	
    int deleteByPrimaryKey(Long scenarinoAreaId);

    int insert(TScenarinoAreaWithBLOBs record);

    int insertSelective(TScenarinoAreaWithBLOBs record);

    TScenarinoAreaWithBLOBs selectByPrimaryKey(Long scenarinoAreaId);

    int updateByPrimaryKeySelective(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TScenarinoAreaWithBLOBs record);

    int updateByPrimaryKey(TScenarinoArea record);
    
    List<TScenarinoArea> selectByEntity(TScenarinoArea record);
}