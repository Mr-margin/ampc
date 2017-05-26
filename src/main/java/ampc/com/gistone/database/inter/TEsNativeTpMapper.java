package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TEsNativeTp;

public interface TEsNativeTpMapper {
    int deleteByPrimaryKey(Long esNativeTpId);

    int insert(TEsNativeTp record);

    int insertSelective(TEsNativeTp record);

    TEsNativeTp selectByPrimaryKey(Long esNativeTpId);

    int updateByPrimaryKeySelective(TEsNativeTp record);

    int updateByPrimaryKey(TEsNativeTp record);
    //根据id查询模板数据
	List<Map> selecttesNativeTp(TEsNativeTp record);
	//更新本地清单模板
	int updateByIdSelective(TEsNativeTp tEsNativeTp);
}