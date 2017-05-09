package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.preprocess.obs.entity.ObsBean;

public interface TObsMapper {

	void checkObsTable(Map<String, Object> params);

	ObsBean queryUnionResult(Map<String, Object> params);

	void update(ObsBean newObsBean);

	void insert(ObsBean newObsBean);
	/**
	 * 查询观测数据统一接口
	 * @param params
	 * @return
	 */
	List<ObsBean> queryObservationResult(Map<String, Object> params);
	
}
