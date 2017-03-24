package ampc.com.gistone.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampc.com.gistone.database.inter.TScenarinoDetailMapper;
import ampc.com.gistone.database.model.TScenarinoDetail;
@RestController
@RequestMapping
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
