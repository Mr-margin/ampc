package ampc.com.gistone.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource(value = "classpath:/config.properties")
/**
 * 路径帮助类
 * @author WangShanxi
 */
public class ConfigUtil {
	//措施的减排分析Url
	@Value("${jp.csjpURL}")
    private String csjpURL;
	//区域的减排分析Url
	@Value("${jp.areaURL}")
    private String areaURL;
	//区域状态的减排url
	@Value("${jp.areaStatusURL}")
    private String areaStatusURL;

	public String getCsjpURL() {
		return csjpURL;
	}

	public String getAreaURL() {
		return areaURL;
	}

	public String getAreaStatusURL() {
		return areaStatusURL;
	}

	
    
}
