package ampc.com.gistone.extract.timer;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ampc.com.gistone.util.excelUtil.DateUtil;
import ampc.com.gistone.util.excelUtil.StringUtil;

//删除目标目录下符合条件的文件，定时器
@Component
public class DeledeScheduled {
	private static final Logger logger = LoggerFactory.getLogger(DeledeScheduled.class);

	@Autowired
	private TimerParameter timerParameter;

	// @Scheduled(cron = "0 0/1 * * * ?")
	@Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行,删除本天前的
	public void dayDeleteTempFileTask() {
		String deleteFilePath = timerParameter.getDayDeleteFilePath();
		String date = DateUtil.getYYYYMMDDLineStr(new Date());
		deleteFile(deleteFilePath, date);
	}

//	 @Scheduled(cron = "0 0/1 * * * ?")
	@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行，保留近期的
	public void weeklyDeleteTempFileTask() {
		String deleteFilePath = timerParameter.getManyDaysDeleteFilePath();
		String date = DateUtil.getYYYYMMDDLineStr(new Date(System.currentTimeMillis() - timerParameter.getManyDays()));
		deleteFile(deleteFilePath, date);
	}
//匹配需要删除的文件路径
	private void deleteFile(String deleteFilePath, String date) {
		if (StringUtil.isEmpty(deleteFilePath)) {
			return;
		}
		for (String filePath : deleteFilePath.split(",")) {
			File file = new File(filePath);
			if (!file.exists()) {
				return;
			}
			for (File fileChilFile : file.listFiles()) {
				if (!fileChilFile.exists()) {
					continue;
				}
				String regex = "\\d{4}-\\d{2}-\\d{2}";
				String fileChilFileName = fileChilFile.getName();
				if (fileChilFile.isDirectory() && fileChilFileName.matches(regex)
						&& fileChilFileName.compareTo(date) < 0) {
					deleteAll(fileChilFile);
				}
				continue;
			}
		}
	}
//递归删除程序
	private void deleteAll(File path) {
		if (!path.exists()) // 路径存在
			return;
		if (path.isFile()) { // 是文件
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAll(files[i]);
		}
		path.delete();
		logger.info("delete " + path.toString() + " file");
	}
}
