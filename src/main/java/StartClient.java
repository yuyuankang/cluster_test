import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StartClient {

  private static final Logger logger = LoggerFactory.getLogger(StartClient.class);

  public static void main(String args[]) throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.CLIENT_NUMBER);
    for (int i = 0; i < Config.CLIENT_NUMBER; i++) {
      String path = Config.BASE + File.separator + Config.ORIGIN_DIR + File.separator + Config.START_CLIENT_BAT;
      pool.submit(() -> {
        ExecuteUtils.executeBatchFile(path, logger);
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish starting all clients.");

  }

}
