package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TPlanMeasure;
import ampc.com.gistone.database.model.TPlanMeasureWithBLOBs;

/**
* 预案措施映射
* @author WangShanxi
* @version v.0.1
* @date 2017年3月7日
*/
public interface TPlanMeasureMapper {
	/**
	 * 根据条件查询措施汇总的信息
	 * @param map
	 * @return
	 */
	List<Map> selectByQuery(Map map);
	
	/**
	 * 根据条件查询ID信息
	 * @param map
	 * @return
	 */
	List<Map> selectIdByQuery(Map map);
	
	/**
	 * 根据条件Map查询ID信息
	 * @param map
	 * @return
	 */
	List<Long> selectIdByMap(Map map);
	
	/**
    * 修改所有的减排为空
    * @param userId
    * @return
    */
    int updateRatio(List<Long> plist);
	
	/**
    * 修改预案措施是否有效
    * @param userId
    * @return
    */
   int updateIsEffeByIds(Long userId);
   
   /**
    * 根据id集合获取所有的预案措施
    * @param idss
    * @return
    */
   List<Map> getPmByIds(List<Long> idss);
   
   /**
    * 删除预案措施
    * @param userId
    * @return
    */
   int deleteMeasures(Long planMeasureId);
	
   /**
    * 根据对象中的条件查询数据
    * @param record
    * @return
    */
	List<TPlanMeasureWithBLOBs> selectByEntity(TPlanMeasure record);
   /**
    * 根据预案ID 删除对应的所有预案措施
    * @param planId
    * @return
    */
   int deleteByPlanId(Long planId);
    int deleteByPrimaryKey(Long planMeasureId);

    int insert(TPlanMeasureWithBLOBs record);

    int insertSelective(TPlanMeasureWithBLOBs record);

    TPlanMeasureWithBLOBs selectByPrimaryKey(Long planMeasureId);

    int updateByPrimaryKeySelective(TPlanMeasureWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TPlanMeasureWithBLOBs record);

    int updateByPrimaryKey(TPlanMeasure record);
}