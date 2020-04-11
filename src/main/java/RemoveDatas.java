import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RemoveDatas {

  private static final Logger logger = LoggerFactory.getLogger(HouseKeeping.class);

  public static void main(String args[]) throws InterruptedException {

    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);
    for (int i = 0; i < Config.SEED_NUMBER; i++) {
      String path = Config.BASE + File.separator
              + Config.ORIGIN_DIR + i + File.separator + Config.DATA_DIR;
      pool.submit(() -> {
        File f = new File(path);
        if (f.exists()) {
          try {
            FileUtils.deleteDirectory(f);
          } catch (IOException e) {
            logger.error("Cannot delete file {}.", path, e);
          }
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish removing all data.");
  }

}
