package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TTime;
/**
 * 时段映射
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月27日
 */
public interface TTimeMapper {
	/**
	 * 根据区域ID 获取时段和预案
	 * @param id 区域ID
	 * @return
	 */
	List<Map> selectByAreaId(Object id);
    
	
	
	
	int deleteByPrimaryKey(Long timeId);

    int insert(TTime record);

    int insertSelective(TTime record);

    TTime selectByPrimaryKey(Long timeId);

    int updateByPrimaryKeySelective(TTime record);
    
    int updateByPrimaryKey(TTime record);
    
    List<TTime> selectByPrimaryKeysort(TTime record);
    List<TTime> selectListByAreaId(Long areaId);
}