package ampc.com.gistone.preprocess.obs.entity;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import ampc.com.gistone.util.JsonUtil;

public class ObsDailyBean extends ObsBean {

    //请求内容
    private Map<String,Double> contentMap = new LinkedHashMap<String,Double>();

    public Map<String, Double> getContentMap() {
        return contentMap;
    }

    public void setContentMap(String content) {

        try {
            this.contentMap = JsonUtil.jsonToObj(content,LinkedHashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
