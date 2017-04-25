package ampc.com.gistone.database.inter;

import java.util.Map;

import ampc.com.gistone.preprocess.obs.entity.ObsBean;

public interface TObsMapper {

	void checkObsTable(Map<String, Object> params);

	ObsBean queryUnionResult(Map<String, Object> params);

	void update(ObsBean newObsBean);

	void insert(ObsBean newObsBean);

}
