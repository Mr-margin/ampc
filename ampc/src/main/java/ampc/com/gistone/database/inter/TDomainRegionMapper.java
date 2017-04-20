package ampc.com.gistone.database.inter;

import java.util.List;

import ampc.com.gistone.database.model.TDomainRegion;

public interface TDomainRegionMapper {
	
    int insert(TDomainRegion record);

    int insertSelective(TDomainRegion record);
    
    List<TDomainRegion> selectAll();
}