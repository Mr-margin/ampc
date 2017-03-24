package ampc.com.gistone.util;

import org.springframework.beans.factory.annotation.Autowired;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;

public class ScenarinoStatusUtil {
	@Autowired
	private TScenarinoDetailMapper tScenarinoDetailMapper;
	
	public int updateScenarinoStatus(Long scenarinoId){
		TScenarinoDetail tScenarinoDetail=new TScenarinoDetail();
		tScenarinoDetail.setScenarinoStatus(2l);
		tScenarinoDetail.setScenarinoId(scenarinoId);
		int status=tScenarinoDetailMapper.updateByPrimaryKeySelective(tScenarinoDetail);
		return status;
	}
}
