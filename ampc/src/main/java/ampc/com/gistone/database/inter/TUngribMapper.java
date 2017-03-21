package ampc.com.gistone.database.inter;

import ampc.com.gistone.database.model.TUngrib;

public interface TUngribMapper {
	/**
	 * 
	 * @Description: TODO
	 * @return   
	 * String  获取最新的lastfnl
	 * @throws
	 * @author yanglei
	 * @date 2017年3月20日 上午11:35:42
	 */
	TUngrib getlastfnl();
	
    int deleteByPrimaryKey(Long ungribId);

    int insert(TUngrib record);

    int insertSelective(TUngrib record);

    TUngrib selectByPrimaryKey(Long ungribId);

    int updateByPrimaryKeySelective(TUngrib record);

    int updateByPrimaryKey(TUngrib record);
    
}