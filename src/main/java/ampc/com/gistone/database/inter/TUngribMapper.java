package ampc.com.gistone.database.inter;

import java.util.Date;

import ampc.com.gistone.database.model.TUngrib;

public interface TUngribMapper {
    int deleteByPrimaryKey(Long ungribId);

    int insert(TUngrib record);

    int insertSelective(TUngrib record);

    TUngrib selectByPrimaryKey(Long ungribId);

    int updateByPrimaryKeySelective(TUngrib record);

    int updateByPrimaryKey(TUngrib record);

	/**
	 * @Description: TODO
	 * @return   
	 * TUngrib  查询最新的ungrib
	 * @throws
	 * @author yanglei
	 * @date 2017年3月28日 上午11:32:15
	 */
	TUngrib getlastungrib();
	/**
	 * @Description: TODO  
	 * @param pathdateDate   
	 * void  查询是否存在当前的pathdate的记录
	 * @throws 
	 * @author yanglei
	 * @date 2017年3月23日 下午3:36:33
	 */
	TUngrib selectUngrib(Date pathdateDate);
}