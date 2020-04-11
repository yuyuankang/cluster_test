import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

class ExecuteUtils {

  private ExecuteUtils() {
  }

  static void executeBatchFile(String path, Logger logger) {
    logger.info("Start executing batch file: {}.", path);
    if (new File(path).exists()) {
      try {
        Runtime.getRuntime().exec("cmd /c start \"\" " + path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.error("Batch file is not found: {}", path);
    }
  }
}
