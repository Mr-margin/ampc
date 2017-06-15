package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TModelScheduleMessage;

public interface TModelScheduleMessageMapper {
    int deleteByPrimaryKey(Long executeScheduleId);

    int insert(TModelScheduleMessage record);

    int insertSelective(TModelScheduleMessage record);

    TModelScheduleMessage selectByPrimaryKey(Long executeScheduleId);

    int updateByPrimaryKeySelective(TModelScheduleMessage record);

    int updateByPrimaryKey(TModelScheduleMessage record);
    /**
	 * @Description: 查询旧的index消息
	 * @param pmap
	 * @return   
	 * TModelScheduleMessage  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月8日 下午3:37:49
	 */
	TModelScheduleMessage selectByscenIdAndIndex(Map pmap);

	/**
	 * @Description: 修改记录
	 * @param tModelScheduleMessage
	 * @return   
	 * int  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月8日 下午3:51:01
	 */
	int updateByscenIdAndIndex(TModelScheduleMessage tModelScheduleMessage);

	/**
	 * @Description: 获取情景执行的进度的列表
	 * @param scenarinoId
	 * @return   
	 * List<TModelScheduleMessage>  
	 * @throws
	 * @author yanglei
	 * @date 2017年6月15日 下午12:48:52
	 */
	List<TModelScheduleMessage> selectListByscenarinoId(Long scenarinoId);
}