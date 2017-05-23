package ampc.com.gistone.extract.vertical;

import org.apache.commons.lang.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilePathUtil {

//  private static final String DELIMITER = "/";

  public static String combinePath(String... args) {
    if (ArrayUtils.isEmpty(args)) return "";
    return String.join(File.separator, args);
  }
  public static boolean writeLocalFile(File file, String content) throws IOException {
	    FileWriter fw = new FileWriter(file);
	    try (BufferedWriter bw = new BufferedWriter(fw)) {
	      bw.write(content);
	    }
	    return true;
	  }
}
