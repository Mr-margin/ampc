package ampc.com.gistone.extract.vertical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TaskHelper {
	private static Logger logger = LoggerFactory.getLogger(TaskHelper.class);

	public synchronized static String execute(String... cmd) throws IOException {
		logger.info("tsdk helper start execute");
		Process process;
		if (cmd.length == 1) {
			process = Runtime.getRuntime().exec(cmd[0]);
		} else {
			process = Runtime.getRuntime().exec(cmd);
		}
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		if ((line = stdInput.readLine()) != null) {
			logger.info(line);
			return line;// 作业ID
		}

		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((line = stdError.readLine()) != null) {
			logger.error(line);
		}
		return null;
	}
}
