import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RemoveData {

  private static final Logger logger = LoggerFactory.getLogger(HouseKeeping.class);

  private static void deleteDir(String path) {
    File f = new File(path);
    if (f.exists()) {
      try {
        FileUtils.deleteDirectory(f);
        logger.info("Dir is removed: {}.", path);
      } catch (IOException e) {
        logger.error("Cannot delete file {}.", path, e);
      }
    } else {
      logger.info("Dir does not exist: {}.", path);
    }
  }

  public static void main(String args[]) throws InterruptedException {

    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);

    String originalPath = Config.BASE + File.separator
            + Config.ORIGIN_DIR + File.separator + Config.DATA_DIR;
    pool.submit(() -> deleteDir(originalPath));

    for (int i = 0; i < Config.SEED_NUMBER; i++) {
      String path = Config.BASE + File.separator
              + Config.ORIGIN_DIR + i + File.separator + Config.DATA_DIR;
      pool.submit(() -> deleteDir(path));
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish removing all data.");
  }


}
