import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HouseKeeping {

  private static final Logger logger = LoggerFactory.getLogger(HouseKeeping.class);


  private static final String BASE = "F:\\git_workspace\\cluster";

  private static final int SEED_NUMBER = 3;
  private static final String ORIGIN_DIR = "incubator-iotdb";

  // The following configurations should be modified, cluster/iotdb-cluster.properties, cluster/iotdb-engine.properties, cluster/cluster-env.bat, server/iotdb-engine.properties

  private static final String CLUSTER_BASE = "cluster\\src\\assembly\\resources\\conf";
  private static final String SERVER_BASE = "server\\src\\assembly\\resources\\conf";

  private static final String IOTDB_CLUSTER = "iotdb-cluster.properties";
  private static final String IOTDB_ENGINE = "iotdb-engine.properties";
  private static final String CLUSTER_ENV = "cluster-env.bat";

  private static final String START_NODE_BAT = "cluster\\target\\cluster-0.10.0-SNAPSHOT\\sbin\\start-node.bat";

  // The content to be modified, matcher
  private static final List<String> replacedInClusterIotdbCluster
          = Arrays.asList("LOCAL_META_PORT=9003", "LOCAL_DATA_PORT=40010", "LOCAL_CLIENT_PORT=55560");
  private static final List<String> replacedInClusterClusterEnv = Arrays.asList("set JMX_PORT=31999");
  private static final List<String> replacedInClusterIotdbEngine = Arrays.asList("rpc_port=6667");
  private static final List<String> replacedInServerIotdbEngine = Arrays.asList("rpc_port=6667", "mqtt_port=1883");

  private static final String COMPILE_CMD = "mvn package -pl cluster,client -am -DskipTests";
  private static final int removeTempBatchTime = 1000; // unit: ms

  private static final String START_COMPILATION_MESSAGE = "Start compilation in {} ";
  private static final String FINISH_COMPILATION_MESSAGE = "Finish compilation in {} ";
  private static final String START_MODIFYING_MESSAGE = "Start modifying configuration in file {}.";
  private static final String FINISH_MODIFYING_MESSAGE = "Finish modifying configuration in file {}.";


  private static void duplicate() throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(SEED_NUMBER);
    for (int i = 0; i < SEED_NUMBER; i++) {
      String path = BASE + File.separator + ORIGIN_DIR + i;
      File file = new File(path);
      pool.submit(() -> {
        try {
          if (file.exists()) {
            logger.info("Directory {} exists, deleting.", file.getAbsoluteFile());
            FileUtils.deleteDirectory(file);
            logger.info("Directory {} is removed", file.getAbsoluteFile());
          }
          logger.info("Copying directory {}.", file.getAbsoluteFile());
          FileUtils.copyDirectory(new File(BASE + File.separator + ORIGIN_DIR), file);
          logger.info("Finish copying {}.", file.getAbsoluteFile());
        } catch (IOException e) {
          e.printStackTrace();
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
    List<String> replaced = lines.map(line -> line.replaceAll(oldText, newText)).collect(Collectors.toList());
    Files.write(path, replaced);
    lines.close();
  }

  private static void modifyConfig() throws IOException {
    for (int i = 1; i < SEED_NUMBER; i++) {
      String path;
      // modify cluster/iotdb-cluster.properties
      path = BASE + File.separator + ORIGIN_DIR + i + File.separator + CLUSTER_BASE + File.separator + IOTDB_CLUSTER;
      logger.info(START_MODIFYING_MESSAGE, path);
      for (String text : replacedInClusterIotdbCluster) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(FINISH_MODIFYING_MESSAGE, path);
      // modify cluster/iotdb-engine.properties
      path = BASE + File.separator + ORIGIN_DIR + i + File.separator + CLUSTER_BASE + File.separator + IOTDB_ENGINE;
      logger.info(START_MODIFYING_MESSAGE, path);
      for (String text : replacedInClusterIotdbEngine) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(FINISH_MODIFYING_MESSAGE, path);
      // modify cluster/cluster-env.bat
      path = BASE + File.separator + ORIGIN_DIR + i + File.separator + CLUSTER_BASE + File.separator + CLUSTER_ENV;
      logger.info(START_MODIFYING_MESSAGE, path);
      for (String text : replacedInClusterClusterEnv) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(FINISH_MODIFYING_MESSAGE, path);
      // modify server/iotdb-engine.properties
      path = BASE + File.separator + ORIGIN_DIR + i + File.separator + SERVER_BASE + File.separator + IOTDB_ENGINE;
      logger.info(START_MODIFYING_MESSAGE, path);
      for (String text : replacedInServerIotdbEngine) {
        modifyFile(Paths.get(path), text, generateNewText(i, text));
      }
      logger.info(FINISH_MODIFYING_MESSAGE, path);
    }
    logger.info("Finish modifying configurations.");
  }


  private static void startServer() throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(SEED_NUMBER);
    for (int i = 0; i < SEED_NUMBER; i++) {
      int finalI = i;
      pool.submit(() -> {
        String path = BASE + File.separator + ORIGIN_DIR + finalI + File.separator + START_NODE_BAT;
        logger.info("start executing the batch file: {}", path);
        if (new File(path).exists()) {
          try {
            Runtime.getRuntime().exec("cmd /c start \"\" " + path);
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          logger.error("Batch file is not found: {}", path);
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.MINUTES);
    logger.info("Finish starting the servers.");
  }

  private static void execute(String batchFileUrl, int i, String compileDes)
          throws IOException, InterruptedException {
    File tempBatch = new File("compile" + i + ".bat");
    if (tempBatch.exists()) {
      tempBatch.delete();
    }
    tempBatch.createNewFile();
    FileWriter writer = new FileWriter(tempBatch, true);
    String[] commands = new String[]{
            "F:\n",
            "cd " + compileDes + "\n",
            COMPILE_CMD + "\n"
    };
    for (String cmd : commands) {
      writer.write(cmd);
    }
    writer.flush();
    writer.close();
    Runtime.getRuntime().exec(batchFileUrl);
    Thread.sleep(removeTempBatchTime);
    tempBatch.delete();
  }

  private static void compile() throws InterruptedException {
    ExecutorService pool = new ScheduledThreadPoolExecutor(SEED_NUMBER);
    for (int i = 0; i < SEED_NUMBER; i++) {
      int finalI = i;
      pool.submit(() -> {
        try {
          String path = BASE + File.separator + ORIGIN_DIR + finalI;
          logger.info(START_COMPILATION_MESSAGE, path);
          File tempBatch = new File("compile" + finalI + ".bat");
          execute("cmd /c start \"\" " + "compile" + finalI + ".bat", finalI, path);
          logger.info(FINISH_COMPILATION_MESSAGE, path);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(30, TimeUnit.MINUTES);
    logger.info("Finish compiling.");
  }

  public static void main(String args[]) throws IOException, InterruptedException {
    duplicate();
    modifyConfig();
    compile();
//    startServer();
  }


}
