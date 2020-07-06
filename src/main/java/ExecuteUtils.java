import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

class ExecuteUtils {

  private ExecuteUtils() {
  }

  static void executeBatchFile(String path, Logger logger) {
    logger.info("Start executing batch file: {}.", path);
    if (new File(path).exists()) {
      String command = "sh " + path;
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        logger.error("Error occurs when executing the command {}. ", command, e);
      }
    } else {
      logger.error("Batch file is not found: {}", path);
    }
  }

  static void executeBatchFile(String path, Logger logger, String... args) {
    logger.info("Start executing batch file: {}.", path);
    if (new File(path).exists()) {
      String command = "sh "+ path + " " + String.join(" ", args);
      try {
        Runtime.getRuntime().exec(command);
      } catch (IOException e) {
        logger.error("Error occurs when executing the command {}. ", command, e);
      }
    } else {
      logger.error("Batch file is not found: {}", path);
    }
  }
}
