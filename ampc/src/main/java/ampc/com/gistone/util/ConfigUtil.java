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
 * 
 * @author WangShanxi
 */
public class ConfigUtil {
	// 措施的减排分析Url
	@Value("${jp.csjpURL}")
	private String csjpURL;
	
	// 措施的减排分析Url
	@Value("${jp.serverPath}")
	private String serverPath;
		
	// 区域的减排分析Url
	@Value("${jp.areaURL}")
	private String areaURL;
	// 区域状态的减排url
	@Value("${jp.areaStatusURL}")
	private String areaStatusURL;

	// 减排计算获取actionlist的url
	@Value("${jp.actionlistURL}")
	private String actionlistURL;

	// 是否调用外部的预处理接口
	@Value("${jp.preproEnable}")
	private boolean preproEnable;

	// 调用外部的预处理接口时使用的URL
	@Value("${jp.preproURL}")
	private String preproURL;

	public String getActionlistURL() {
		return actionlistURL;
	}

	public String getCsjpURL() {
		return csjpURL;
	}

	public String getAreaURL() {
		return areaURL;
	}

	public String getAreaStatusURL() {
		return areaStatusURL;
	}

	public boolean isPreproEnable() {
		return preproEnable;
	}

	public String getPreproURL() {
		return preproURL;
	}
	
	public String getServerPath() {
		return serverPath;
	}
}
