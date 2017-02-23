package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;




import org.apache.ibatis.annotations.Mapper;

import ampc.com.gistone.database.model.TMissionDetail;
/**
 * 任务映射接口
 * @author WangShanxi
 * @version v.0.1
 * @date 2017年2月23日
 */
@Mapper
public interface TMissionDetailMapper {
	/**
	 * 根据任务ID删除
	 * @param id  任务ID
	 * @return
	 */
    int deleteByPrimaryKey(Long id);

    /**
     * 添加任务
     * @param record 任务类
     * @return
     */
    int insert(TMissionDetail record);

    /**
     * 添加任务 判断非空
     * @param record 任务类
     * @return
     */
    int insertSelective(TMissionDetail record);

    /**
     * 根据任务Id查询
     * @param id 任务ID
     * @return 任务类
     */
    TMissionDetail selectByPrimaryKey(Long id);
    
    /**
     * 查询全部任务
     * @return 任务类
     */
    List<TMissionDetail> selectAll();

    /**
     * 修改任务 判断非空
     * @param record 任务类
     * @return
     */
    int updateByPrimaryKeySelective(TMissionDetail record);

    /**
     * 修改任务 判断非空
     * @param record 任务类
     * @return
     */
    int updateByPrimaryKey(TMissionDetail record);
}