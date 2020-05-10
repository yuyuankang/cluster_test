import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

  public static void main(String args[]) throws InterruptedException, IOException {

    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);

    // remove data in original
    String originalPath = Config.BASE + File.separator
        + Config.ORIGIN_DIR + File.separator + "data";
    pool.submit(() -> deleteDir(originalPath));
    String originalPartitionPath = Config.BASE + File.separator
            + Config.ORIGIN_DIR + File.separator + "partitions";
    String originalIdentifier = Config.BASE + File.separator
            + Config.ORIGIN_DIR + File.separator + "node_identifier";

    if (Paths.get(originalPartitionPath).toFile().exists()) {
      Files.delete(Paths.get(originalPartitionPath));
    }
    if (Paths.get(originalIdentifier).toFile().exists()) {
      Files.delete(Paths.get(originalIdentifier));
    }

    // remove data in this project
    Path nodeIdPath = Paths.get(Config.NODE_ID);
    if (nodeIdPath.toFile().exists()) {
      Files.delete(nodeIdPath);
      logger.info("File is removed, {}.", nodeIdPath);
    }else{
      logger.info("File does not exist, {}.", nodeIdPath );
    }
    Path partitionIdPath = Paths.get(Config.PARTITIONS);
    if (nodeIdPath.toFile().exists()) {
      Files.delete(partitionIdPath);
      logger.info("File is removed, {}.", nodeIdPath);
    }else{
      logger.info("File does not exist, {}.", nodeIdPath );
    }

    // remove files in duplications
    for (int i = Config.SEED_NUMBER - 1; i >= Config.CLIENT_NUMBER; i--) {
//    for (int i = 0; i < Config.SEED_NUMBER; i++) {
      String path = Config.BASE + File.separator
          + Config.ORIGIN_DIR + i + File.separator + Config.DATA_DIR;
      String partitionPath = Config.BASE + File.separator
          + Config.ORIGIN_DIR + i + File.separator + Config.SBIN + File.separator + "partitions";
      String identifier = Config.BASE + File.separator
          + Config.ORIGIN_DIR + i + File.separator + Config.SBIN + File.separator
          + "node_identifier";

      if (Paths.get(partitionPath).toFile().exists()) {
        Files.delete(Paths.get(partitionPath));
        logger.info("File is removed, {}.", Paths.get(partitionPath));
      }else{
        logger.info("File does not exist, {}.", Paths.get(partitionPath) );
      }
      if (Paths.get(identifier).toFile().exists()) {
        Files.delete(Paths.get(identifier));
        logger.info("File is removed, {}.", Paths.get(identifier));
      }else{
        logger.info("File does not exist, {}.", Paths.get(identifier) );
      }
      pool.submit(() -> deleteDir(path));
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish removing all data.");
  }


}
