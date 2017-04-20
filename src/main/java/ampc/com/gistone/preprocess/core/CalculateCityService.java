package ampc.com.gistone.preprocess.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import ampc.com.gistone.database.inter.TDomainRegionMapper;
import ampc.com.gistone.database.inter.TSiteMapper;
import ampc.com.gistone.database.model.TDomainRegion;
import ampc.com.gistone.database.model.TSite;

@Service
public class CalculateCityService {

	private final static Logger logger = LoggerFactory.getLogger(CalculateCityService.class);
	
	@Autowired
	private TSiteMapper tSiteMapper;
	@Autowired
	private TDomainRegionMapper tDomainRegionMapper;
	private Map<String, List<TSite>> map;
	private Projection projection;
	
	public String getCityResult(double xorig, double yorig, double xcell, double ycell, int nrows, int ncols, 
			double p_alp, double p_bet, double xcent, double ycent) throws JsonProcessingException {
		
		buildCityStation();
		if(map == null || map.size() <= 0) return "";
		
		String[] proj = new String[]{"+proj=lcc", "+lat_1= "+p_alp+"",
                "+lat_2="+p_bet+"",
                "+lat_0="+ycent+"",
                "+lon_0="+xcent+"",
                "+x_0=0", "+y_0=0", "+ellps=WGS84", "+units=m +no_defs"};
		
		projection = ProjectionFactory.fromPROJ4Specification(proj);
		
		Map<String, Object> res = buildResult(xorig, yorig, xcell, ycell, nrows, ncols);
		
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(res);
	}

	private TDomainRegion getRegionEntity(String code) {
		
		List<TDomainRegion> list = tDomainRegionMapper.selectAll();
		for(TDomainRegion tDomainRegion : list) {
			String regionId = tDomainRegion.getRegionid();
			if(code.equals(regionId)) {
				return tDomainRegion;
			}
		}
		return null;
	}

	private Map<String, Object> buildResult(double xorig, double yorig, double xcell, double ycell, int nrows, int ncols) {
		Map<String, Object> res = new HashMap<String, Object>();
		
		for(Map.Entry<String, List<TSite>> entry : map.entrySet()) {
			String cityCode = (String) entry.getKey();
			List<TSite> list = entry.getValue();
			boolean flag = false;
			for(TSite tSite : list) {
				Point2D p = transform(Double.valueOf(tSite.getLon()), Double.valueOf(tSite.getLat()), projection);
				boolean bool = buildRange(xorig, yorig, xcell, ycell, nrows, ncols, p.getX(), p.getY());
				if(!bool) {
					flag = true;
					break;
				}
			}
			if(flag) continue;
			String provinceCode = cityCode.substring(0, 2);
			TDomainRegion provinceEntity = getRegionEntity(provinceCode);
			TDomainRegion cityEntity = getRegionEntity(cityCode);
			if(res.containsKey(provinceCode)) {
				Map<String, Object> provinceMap = (Map<String, Object>) res.get(provinceCode);
				Map<String, Object> cityMap = (Map<String, Object>) provinceMap.get("city");
				if(cityMap == null) cityMap = new HashMap<String, Object>();
				cityMap.put(cityCode, cityEntity.getRegionname());
			} else {
				Map<String, Object> provinceMap = new HashMap<String, Object>();
				provinceMap.put("name", provinceEntity.getRegionname());
				Map<String, Object> cityMap = new HashMap<String, Object>();
				cityMap.put(cityCode, cityEntity.getRegionname());
				provinceMap.put("city", cityMap);
				res.put(provinceCode, provinceMap);
			}
		}
		
		return res;
	}
	
	private boolean buildRange(double xorig, double yorig, double xcell, double ycell, int nrows, int ncols, double x,
			double y) {

		int xRange = (int) ((x - xorig) / xcell);
		int yRange = (int) ((y - yorig) / ycell);
		if(yRange > nrows - 1 || xRange > ncols - 1 || yRange < 0 || xRange < 0) return false;
		return true;
	}

	public Point2D.Double transform(double lon, double lat, Projection projection) {
	    Point2D.Double src = new Point2D.Double(lon, lat);
	    Point2D.Double dst = new Point2D.Double();
	    projection.transform(src, dst);
	    return dst;
	}

	private void buildCityStation() {
		
		List<TSite> tSiteList = tSiteMapper.selectAll();
		if(tSiteList == null || tSiteList.size() <= 0) return;
		
		map = new HashMap<String, List<TSite>>();
		for(int i = 0; i < tSiteList.size(); i++) {
			TSite tSite = tSiteList.get(i);
			String siteCode = tSite.getSiteCode();
			String stationId = tSite.getStationId();
			if(!stationId.endsWith("A") ||stationId.equals("1028A")){
		        continue;
		    }
			if(siteCode.length() >= 4) {
				String cityCode = siteCode.substring(0, 4);
				if(map.containsKey(cityCode)) {
					List<TSite> list = map.get(cityCode);
					list.add(tSite);
				} else {
					List<TSite> list = new ArrayList<TSite>();
					list.add(tSite);
					map.put(cityCode, list);
				}
			}
		}
	}
	
}
