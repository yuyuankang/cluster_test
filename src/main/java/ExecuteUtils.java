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
        String command = "cmd /c start \"\" " + path;
        Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.error("Batch file is not found: {}", path);
    }
  }

  static void executeBatchFile(String path, Logger logger, String... args) {
    logger.info("Start executing batch file: {}.", path);
    if (new File(path).exists()) {
      try {
        String command = "cmd /c start \"\" " + path + " " + String.join(" ", args);
        Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.error("Batch file is not found: {}", path);
    }
  }
}
