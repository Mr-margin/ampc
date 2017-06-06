package ampc.com.gistone.database.inter;


import java.util.List;

import ampc.com.gistone.database.model.TMessageLog;

public interface TMessageLogMapper {
    int deleteByPrimaryKey(Long messageLogId);

    int insert(TMessageLog record);

    int insertSelective(TMessageLog record);

    TMessageLog selectByPrimaryKey(Long messageLogId);

    int updateByPrimaryKeySelective(TMessageLog record);

    int updateByPrimaryKey(TMessageLog record);

	/**
	 * @Description: 查找情景的运行日志
	 * @param scenarinoId
	 * @return   
	 * List<TMessageLog>  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月5日 上午11:59:03
	 */
	List<TMessageLog> selectListByscenarinoId(Long scenarinoId);
}