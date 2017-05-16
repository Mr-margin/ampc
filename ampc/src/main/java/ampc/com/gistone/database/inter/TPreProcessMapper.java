package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.preprocess.concn.CheckTableParams;
import ampc.com.gistone.preprocess.concn.ScenarinoEntity;

public interface TPreProcessMapper {

	void checkScenarinoResTable(CheckTableParams checkTableParams);

	void insert(ScenarinoEntity scenarinoEntity);

	void update(ScenarinoEntity scenarinoEntity);

	List<ScenarinoEntity> selectByUniqueKey(Map<String, Object> requestParams);

	void checkForcastFnlTable(CheckTableParams checkTableParams);

	List<ScenarinoEntity> selectByUniqueKeyForfnl(Map<String, Object> requestParams);

	void updateFnl(ScenarinoEntity fnlEntity);

	void insertFnl(ScenarinoEntity fnlEntity);

	List<ScenarinoEntity> selectBysome(ScenarinoEntity fnlEntity);

	List<ScenarinoEntity> selectBysome2(ScenarinoEntity fnlEntity);
	/**
	 * 空气质量预报-时间序列-查询污染物或气象要素的逐日或逐小时的数据
	 * @param fnlEntity
	 * @return
	 */
	ScenarinoEntity selectBysomes(ScenarinoEntity fnlEntity);
	/**
	 * 空气质量预报-时间序列-查询污染物或气象要素的逐日或逐小时的FNL数据
	 * @param fnlEntity
	 * @return
	 */
	ScenarinoEntity selectBysomesFnl(ScenarinoEntity fnlEntity);
	/**
	 */
	List<ScenarinoEntity> selectBysomeRE(ScenarinoEntity fnlEntity);
}
