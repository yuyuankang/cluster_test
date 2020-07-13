import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HouseKeeping {

  private static final Logger logger = LoggerFactory.getLogger(HouseKeeping.class);


  private static void duplicate() throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);
//    for (int i = Config.SEED_NUMBER - 1; i >= Config.CLIENT_NUMBER; i--) {
    for (int i = 0; i < Config.SEED_NUMBER; i++) {
      String path = Config.BASE + File.separator + Config.ORIGIN_DIR + i;
      File file = new File(path);
      pool.submit(() -> {
        try {
          if (file.exists()) {
            logger.info("Directory {} exists, deleting.", file.getAbsoluteFile());
            FileUtils.deleteDirectory(file);
            logger.info("Directory {} is removed", file.getAbsoluteFile());
          }
          logger.info("Copying directory {}.", file.getAbsoluteFile());
          FileUtils.copyDirectory(new File(Config.BASE + File.separator + Config.ORIGIN_DIR), file);
          logger.info("Finish copying {}.", file.getAbsoluteFile());
        } catch (IOException e) {
          logger.error("IOException", e);
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish duplicating.");
  }

  private static String generateNewText(int i, String text) {
    String[] afterSplit = text.split("=");
    afterSplit[1] = String.valueOf((Integer.parseInt(afterSplit[1]) + i));
    return String.join("=", afterSplit);
  }


  private static void modifyFile(Path path, String oldText, String newText) throws IOException {
    // TODO this can be improved to scan a file once while replace multiple lines
    if (!path.toFile().exists()) {
      throw new IOException("File is not found " + path.toAbsolutePath());
    }
    Stream<String> lines = Files.lines(path);
    List<String> replaced = lines.map(line -> line.replaceAll(oldText, newText))
        .collect(Collectors.toList());
    Files.write(path, replaced);
    lines.close();
  }


  private static void modifyConfig() throws IOException {

    String firstPath = Config.BASE + File.separator + Config.ORIGIN_DIR + File.separator
        + Config.CLUSTER_BASE + File.separator + Config.IOTDB_CLUSTER;
    modifyFile(Paths.get(firstPath), Config.REPLICATION_MATCHER, Config.NEW_REPLICATION_TEXT);

//    for (int i = Config.SEED_NUMBER - 1; i >= Config.CLIENT_NUMBER; i--) {
    for (int i = 1; i < Config.SEED_NUMBER; i++) {
      String path;
      // modify cluster/iotdb-cluster.properties
      path = Config.BASE + File.separator + Config.ORIGIN_DIR + i + File.separator
          + Config.CLUSTER_BASE + File.separator + Config.IOTDB_CLUSTER;
      logger.info(Config.START_MODIFYING_MESSAGE, path);
      for (String text : Config.replacedInClusterIotdbCluster) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      modifyFile(Paths.get(path), Config.REPLICATION_MATCHER, Config.NEW_REPLICATION_TEXT);
      logger.info(Config.FINISH_MODIFYING_MESSAGE, path);

      // modify cluster/iotdb-engine.properties
      path = Config.BASE + File.separator + Config.ORIGIN_DIR + i + File.separator
          + Config.CLUSTER_BASE + File.separator + Config.IOTDB_ENGINE;
      logger.info(Config.START_MODIFYING_MESSAGE, path);
      for (String text : Config.replacedInClusterIotdbEngine) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(Config.FINISH_MODIFYING_MESSAGE, path);

      // modify cluster/cluster-env.bat
      path = Config.BASE + File.separator + Config.ORIGIN_DIR + i + File.separator
          + Config.CLUSTER_BASE + File.separator + Config.CLUSTER_ENV;
      logger.info(Config.START_MODIFYING_MESSAGE, path);
      for (String text : Config.replacedInClusterClusterEnv) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(Config.FINISH_MODIFYING_MESSAGE, path);

      // modify server/iotdb-engine.properties
      path = Config.BASE + File.separator + Config.ORIGIN_DIR + i + File.separator
          + Config.SERVER_BASE + File.separator + Config.IOTDB_ENGINE;
      logger.info(Config.START_MODIFYING_MESSAGE, path);
      for (String text : Config.replacedInServerIotdbEngine) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(Config.FINISH_MODIFYING_MESSAGE, path);
    }
    logger.info("Finish modifying configurations.");
  }

  private static void compile() throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(Config.SEED_NUMBER);
//    for (int i = Config.SEED_NUMBER - 1; i >= Config.CLIENT_NUMBER; i--) {
    for (int i = 0; i < Config.SEED_NUMBER; i++) {
      int finalI = i;
      pool.submit(() -> {
        try {
          String compileLocation = Config.BASE + File.separator + Config.ORIGIN_DIR + finalI;
          logger.info(Config.START_COMPILATION_MESSAGE, compileLocation);
          String tempBatchFilePath = "compile" + finalI + ".bat";
          generateBatchFile(compileLocation, tempBatchFilePath);
          ExecuteUtils.executeBatchFile(tempBatchFilePath, logger);
          Thread.sleep(Config.REMOVE_TEMP_BATCH_TIME);
          cleanTempFiles(tempBatchFilePath);
          logger.info(Config.FINISH_COMPILATION_MESSAGE, compileLocation);
        } catch (IOException e) {
          logger.error("TOException", e);
        } catch (InterruptedException e) {
          logger.error("Interrupted", e);
          Thread.currentThread().interrupt();
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(30, TimeUnit.MINUTES);
    logger.info("Finish compiling.");
  }

  private static void cleanTempFiles(String filePath) throws IOException {
    Files.delete(Paths.get(filePath));
  }

  private static void generateBatchFile(String compileLocation, String fileName)
      throws IOException {
    Path tempBatchPath = Paths.get(fileName);
    if (tempBatchPath.toFile().exists()) {
      Files.delete(tempBatchPath);
    }
    Files.createFile(tempBatchPath);
    FileWriter writer = new FileWriter(tempBatchPath.toFile(), true);
    String[] commands = new String[]{
        "cd " + compileLocation + "\n",
        Config.COMPILE_CMD + "\n"
    };
    for (String cmd : commands) {
      writer.write(cmd);
    }
    writer.flush();
    writer.close();
  }

  public static void main(String args[]) throws IOException, InterruptedException {
    duplicate();
    modifyConfig();
    compile();
  }
}
