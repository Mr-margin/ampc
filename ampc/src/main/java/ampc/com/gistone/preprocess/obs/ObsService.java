package ampc.com.gistone.preprocess.obs;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ampc.com.gistone.database.inter.TObsMapper;
import ampc.com.gistone.preprocess.obs.entity.ObsBean;

@Service
@Transactional
public class ObsService {

	public final DateTimeFormatter dayTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public TObsMapper tObsMapper;

	public String checkAndAddTable(ObsBean newObsBean, String timePoint) {
		int year = newObsBean.getYears();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("years", year);
		params.put("time_point", timePoint);
		tObsMapper.checkObsTable(params);
		return (String) params.get("tName");
	}

}
