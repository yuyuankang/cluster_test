import java.util.Arrays;
import java.util.List;

class Config {

  private Config() {
  }

  static final String BASE = "F:\\git_workspace\\cluster";

  static final int SEED_NUMBER = 3;
  static final int CLIENT_NUMBER = 1;
  static final String ORIGIN_DIR = "incubator-iotdb";

  // The following configurations should be modified, cluster/iotdb-cluster.properties, cluster/iotdb-engine.properties, cluster/cluster-env.bat, server/iotdb-engine.properties

  static final String CLUSTER_BASE = "cluster\\src\\assembly\\resources\\conf";
  static final String SERVER_BASE = "server\\src\\assembly\\resources\\conf";

  static final String IOTDB_CLUSTER = "iotdb-cluster.properties";
  static final String IOTDB_ENGINE = "iotdb-engine.properties";
  static final String CLUSTER_ENV = "cluster-env.bat";

  static final String START_CLUSTER_BAT = "cluster\\target\\cluster-0.10.0-SNAPSHOT\\sbin\\start-node.bat";
  static final String START_CLIENT_BAT = "client\\target\\iotdb-client-0.10.0-SNAPSHOT\\sbin\\start-client.bat";

  // The content to be modified, matcher
  static final List<String> replacedInClusterIotdbCluster
          = Arrays.asList("LOCAL_META_PORT=9003", "LOCAL_DATA_PORT=40010", "LOCAL_CLIENT_PORT=55560");
  static final List<String> replacedInClusterClusterEnv = Arrays.asList("set JMX_PORT=31999");
  static final List<String> replacedInClusterIotdbEngine = Arrays.asList("rpc_port=6667");
  static final List<String> replacedInServerIotdbEngine = Arrays.asList("rpc_port=6667", "mqtt_port=1883");

  static final String COMPILE_CMD = "mvn package -pl cluster,client -am -DskipTests";
  static final int REMOVE_TEMP_BATCH_TIME = 1000; // unit: ms

  static final String START_COMPILATION_MESSAGE = "Start compilation in {} ";
  static final String FINISH_COMPILATION_MESSAGE = "Finish compilation in {} ";
  static final String START_MODIFYING_MESSAGE = "Start modifying configuration in file {}.";
  static final String FINISH_MODIFYING_MESSAGE = "Finish modifying configuration in file {}.";
}
