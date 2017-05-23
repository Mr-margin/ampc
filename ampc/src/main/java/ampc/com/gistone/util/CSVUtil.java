package ampc.com.gistone.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampc.com.gistone.extract.Constants;
import ampc.com.gistone.extract.vertical.VerticalService;

public class CSVUtil {
	private static Logger logger = LoggerFactory.getLogger(VerticalService.class);

	// 返回污染物下标csv文件
	public static File createIndexCSVFile(String speciespecieIndex, String outPutPath) {
		String fileName = speciespecieIndex.split("=")[0] + Constants.CSV_SUFFIX;
		File csvFile = null;
		BufferedWriter csvFileOutputStream = null;

		File file = new File(outPutPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		csvFile = new File(outPutPath, fileName);
		if (csvFile.exists() && csvFile.isFile()) {
			return csvFile;
		}
		try {
			csvFile.createNewFile();
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"),
					1024);
			// 写入文件内容
			csvFileOutputStream.write(speciespecieIndex.split("=")[1]);
			csvFileOutputStream.write("\n");
			csvFileOutputStream.flush();
		} catch (Exception e) {
			logger.error("VerticalService | variables() : createIndexCSVFile Exception");
			return null;
		} finally {
			try {
				csvFileOutputStream.close();
			} catch (IOException e) {
				logger.error("VerticalService | variables() : createIndexCSVFile IOException");
				return null;
			}
		}
		return csvFile;
	}

	// 返回矩阵的csv文件
	public static File createCSVFile(double[][] resTemp, String outPutPath, String fileName) {
		File csvFile = null;
		BufferedWriter csvFileOutputStream = null;
		try {
			File file = new File(outPutPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			// 定义文件名格式并创建
			csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"),
					1024);
			// 写入文件内容
			for (int i = 0; i < resTemp.length; i++) {
				for (int j = 0; j < resTemp[i].length; j++) {
					csvFileOutputStream.write(resTemp[i][j] + "");
					if (j < resTemp[i].length - 1) {
						csvFileOutputStream.write(",");
					}
				}
				csvFileOutputStream.write("\n");
			}
			csvFileOutputStream.flush();
		} catch (Exception e) {
			logger.error("VerticalService | variables() : createCSVFile Exception");
			return null;
		} finally {
			try {
				csvFileOutputStream.close();
			} catch (IOException e) {
				logger.error("VerticalService | variables() : createCSVFile IOException");
				return null;
			}
		}
		return csvFile;
	}
}
