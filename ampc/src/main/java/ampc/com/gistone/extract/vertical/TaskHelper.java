package ampc.com.gistone.extract.vertical;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class TaskHelper {
	private static Logger logger = LoggerFactory.getLogger(TaskHelper.class);

	public boolean process(String... commond) {
		try {
			Process process = new ProcessBuilder(commond).start();
			new PrintStream(process.getErrorStream()).start();
			new PrintStream(process.getInputStream()).start();
			process.waitFor();
			return true;
		} catch (Exception e) {
			logger.debug("[ConvertVideo | process]", e);
			return false;
		}
	}

	// 给对应文件赋权限
	public void chmod(String filePath) {
		File cshFile = new File(filePath);
		if (cshFile.exists() && cshFile.isFile()) {
			cshFile.setExecutable(true);
			cshFile.setReadable(true);
			cshFile.setWritable(true);
			logger.info("set " + filePath + " .setExecutable(true) setReadable(true) setWritable(true)");
		} else {
			logger.error(filePath + " is not exists");
			return;
		}
	}

	class PrintStream extends Thread {
		BufferedReader __is = null;

		public PrintStream(InputStream is) {
			__is = new BufferedReader(new InputStreamReader(is));
		}

		public void run() {
			try {
				while (this != null) {
					String _ch = __is.readLine();
					if (_ch != null) {
						logger.info(_ch);
					} else {
						return;
					}
				}
			} catch (Exception e) {
				logger.debug("printStream error", e);
			} finally {
				try {
					__is.close();
				} catch (IOException e) {
					logger.debug("", e);
				}
			}
		}
	}
}
