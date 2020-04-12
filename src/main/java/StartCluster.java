import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StartCluster {

  private static final Logger logger = LoggerFactory.getLogger(StartCluster.class);

  public static void main(String args[]) throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);
    int number = 1;
//    for (int i = 0; i < Config.SEED_NUMBER; i++) {
    for (int i = Config.SEED_NUMBER - 1; i >= number; i--) {
      String path = Config.BASE + File.separator + Config.ORIGIN_DIR + i + File.separator + Config.START_CLUSTER_BAT;
      pool.submit(() -> ExecuteUtils.executeBatchFile(path, logger));
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish starting the servers.");
  }
}
