package ampc.com.gistone.preprocess.obs.entity;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ampc.com.gistone.util.JsonUtil;

public class ObsHourlyBean extends ObsBean {

    //请求内容
    private Map contentMap = new LinkedHashMap<String,List<String>>();

    public Map<String, List<String>> getContentMap() {
		return contentMap;
	}

	public void setContentMap(String content) {

        try {
            this.contentMap = JsonUtil.jsonToObj(content,Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
