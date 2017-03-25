package ampc.com.gistone.database.inter;

import java.util.Date;

import ampc.com.gistone.database.model.TUngrib;

public interface TUngribMapper {
	
	/**
	 * @Description: TODO  
	 * @param pathdateDate   
	 * void  查询是否存在当前的pathdate的记录
	 * @throws 
	 * @author yanglei
	 * @date 2017年3月23日 下午3:36:33
	 */
	TUngrib selectUngrib(Date pathdateDate);
	
	/**
	 * @param i 
	 * @Description: TODO   
	 * void   查询中断的情况下的最新的fnl
	 * @throws
	 * @author yanglei
	 * @date 2017年3月22日 下午5:39:36
	 */
	Date getinterruptlastFnl(Integer fnlStatus);
	/**
	 * 
	 * @Description: TODO
	 * @return   
	 * String  获取最新的lastfnl
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 上午11:35:42
	 */
	TUngrib getlastungrib();
	
    int deleteByPrimaryKey(Long ungribId);

    int insert(TUngrib record);

    int insertSelective(TUngrib record);

    TUngrib selectByPrimaryKey(Long ungribId);

    int updateByPrimaryKeySelective(TUngrib record);

    int updateByPrimaryKey(TUngrib record);
}