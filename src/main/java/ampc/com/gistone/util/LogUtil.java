package ampc.com.gistone.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampc.com.gistone.extract.ExtractDataService;

public class LogUtil {
	//定义日志类
	private final static Logger logger = LoggerFactory.getLogger(ExtractDataService.class);
	public static Logger getLogger(){
		return logger;
	}
}
