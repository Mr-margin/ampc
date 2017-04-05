package ampc.com.gistone.database.inter;

import java.util.ArrayList;
import java.util.Map;

import ampc.com.gistone.database.model.TCoresTimes;

public interface TCoresTimesMapper {
	
	/**
	 * @param map 
	 * @Description: TODO
	 * @return   
	 * ArrayList<Map>  获取每种情景的计算核数的耗时情况
	 * @throws
	 * @author yanglei
	 * @date 2017年4月5日 下午3:33:19
	 */
	ArrayList<Map> getCoreList(Map map);
	
    int deleteByPrimaryKey(Long coresTimeId);

    int insert(TCoresTimes record);

    int insertSelective(TCoresTimes record);

    TCoresTimes selectByPrimaryKey(Long coresTimeId);

    int updateByPrimaryKeySelective(TCoresTimes record);

    int updateByPrimaryKey(TCoresTimes record);
}