package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TGlobalSetting;

public interface TGlobalSettingMapper {
    int deleteByPrimaryKey(Long globalSettingId);

	int insert(TGlobalSetting record);

	int insertSelective(TGlobalSetting record);

	TGlobalSetting selectByPrimaryKey(Long globalSettingId);

	int updateByPrimaryKeySelective(TGlobalSetting record);

	int updateByPrimaryKey(TGlobalSetting record);

	/**
	 * @Description: 查找所有用户的全局设置
	 * @return   
	 * List<TGlobalSetting>  
	 * @throws
	 * @author yanglei
	 * @date 2017年4月12日 上午11:29:36
	 */
	List<TGlobalSetting> selectAll();
	

}