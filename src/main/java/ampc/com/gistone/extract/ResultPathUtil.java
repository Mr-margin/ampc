package ampc.com.gistone.extract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResultPathUtil {

	private final static Logger logger = LoggerFactory.getLogger(ResultPathUtil.class);
	private ExtractConfig extractConfig;

	@PostConstruct
	public void init() {
		getExtractConfig("/extract.properties");
	}

	private void getExtractConfig(String config) {

		InputStream ins = getClass().getResourceAsStream(config);
		Properties pro = new Properties();
		try {
			pro.load(ins);
			extractConfig = new ExtractConfig();
			extractConfig.setConcnFilePath((String) pro.get("concnFilePath"));
			extractConfig.setEmisFilePath((String) pro.get("emisFilePath"));
			extractConfig.setWindFilePath((String) pro.get("windFilePath"));
			extractConfig.setConcnDailyPrefix((String) pro.get("concnDailyPrefix"));
			extractConfig.setConcnHourlyPrefix((String) pro.get("concnHourlyPrefix"));
			extractConfig.setMeteorDailyPrefix((String) pro.get("meteorDailyPrefix"));
			extractConfig.setMeteorHourlyPrefix((String) pro.get("meteorHourlyPrefix"));
			extractConfig.setMeteorDailyWindPrefix((String) pro.get("meteorDailyWindPrefix"));
			extractConfig.setMeteorHourlyWindPrefix((String) pro.get("meteorHourlyWindPrefix"));
			extractConfig.setEmisDailyPrefix((String) pro.get("emisDailyPrefix"));
			extractConfig.setEmisHourlyPrefix((String) pro.get("emisHourlyPrefix"));
		} catch (FileNotFoundException e) {
			logger.error(config + " file does not exits!", e);
		} catch (IOException e) {
			logger.error("load " + config + " file error!", e);
		}

		try {
			if (ins != null)
				ins.close();
		} catch (IOException e) {
			logger.error("close " + config + " file error!", e);
		}
	}

	public ExtractConfig getExtractConfig() {
		return extractConfig;
	}

	public void setExtractConfig(ExtractConfig extractConfig) {
		this.extractConfig = extractConfig;
	}

}
