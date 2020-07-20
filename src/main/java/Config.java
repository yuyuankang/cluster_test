import java.util.Arrays;
import java.util.List;

class Config {

  private Config() {
  }

  static final String BASE = "C:\\workspace\\github\\cluster";

  static final int SEED_NUMBER = 3;
  static final int CLIENT_NUMBER = 1;
  static final int REPLICATION_NUM = 2;
  static final String ORIGIN_DIR = "incubator-iotdb";

  // The following configurations should be modified, cluster/iotdb-cluster.properties, cluster/iotdb-engine.properties, cluster/cluster-env.bat, server/iotdb-engine.properties

  static final String CLUSTER_BASE = "cluster\\src\\assembly\\resources\\conf";
  static final String SERVER_BASE = "server\\src\\assembly\\resources\\conf";

  static final String IOTDB_CLUSTER = "iotdb-cluster.properties";
  static final String IOTDB_ENGINE = "iotdb-engine.properties";
  static final String CLUSTER_ENV = "cluster-env.bat";

  static final String START_CLUSTER_BAT = "cluster\\target\\cluster-0.10.0-SNAPSHOT\\sbin\\start-node.bat";
  static final String START_CLIENT_BAT = "cli\\target\\iotdb-cli-0.11.0-SNAPSHOT\\sbin\\start-cli.bat";

  // The content to be modified, matcher
  static final List<String> replacedInClusterIotdbCluster
      = Arrays.asList("internal_meta_port=9003", "internal_data_port=40010", "cluster_rpc_port=55560");
  static final List<String> replacedInClusterClusterEnv = Arrays.asList("set JMX_PORT=31999");
  static final List<String> replacedInClusterIotdbEngine = Arrays.asList("rpc_port=6667");
  static final List<String> replacedInServerIotdbEngine = Arrays
      .asList("rpc_port=6667", "mqtt_port=1883");

  static final String COMPILE_CMD = "mvn package -pl cluster -am -DskipTests";
  static final int REMOVE_TEMP_BATCH_TIME = 5000; // unit: ms

  static final String START_COMPILATION_MESSAGE = "Start compilation in {} ";
  static final String FINISH_COMPILATION_MESSAGE = "Finish compilation in {} ";
  static final String START_MODIFYING_MESSAGE = "Start modifying configuration in file {}.";
  static final String FINISH_MODIFYING_MESSAGE = "Finish modifying configuration in file {}.";

  static final String DATA_DIR = "cluster\\target\\cluster-0.10.0-SNAPSHOT\\data";

  static final String SBIN = "cluster\\target\\cluster-0.10.0-SNAPSHOT\\sbin";

  static final String NODE_ID = "node_identifier";
  static final String PARTITIONS = "partitions";

  static final String QUERY_FILE = "queries.txt";

  static final String REPLICATION_MATCHER = "REPLICA_NUM=3";
  static final String NEW_REPLICATION_TEXT = String
      .join("=", REPLICATION_MATCHER.split("=")[0], String.valueOf(REPLICATION_NUM));
  static final int DATA_NUM = 3;
}
