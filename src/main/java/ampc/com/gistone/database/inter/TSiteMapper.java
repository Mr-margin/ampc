package ampc.com.gistone.database.inter;

import java.util.List;
import java.util.Map;

import ampc.com.gistone.database.model.TSite;

public interface TSiteMapper {

	int insert(TSite record);

	int insertSelective(TSite record);

	List<TSite> selectSiteCode(String siteCode);

	List<TSite> selectAll();

	List<String> selectBySiteCodeList(Map map);
}