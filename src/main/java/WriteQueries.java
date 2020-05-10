import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteQueries {

  public static void main(String args[]) throws IOException {
    Path filePath = Paths.get(Config.QUERY_FILE);
    if (filePath.toFile().exists()) {
      Files.delete(filePath);
    }
    Files.createFile(filePath);
    FileWriter writer = new FileWriter(filePath.toFile(), true);
    int storageGroupNum = 100;
    String[] setLines = new String[storageGroupNum];
    String[] createLines = new String[storageGroupNum];
    String[] insertLines = new String[storageGroupNum * Config.DATA_NUM];
    for (int i = 0; i < storageGroupNum; i++) {
      String chars = "abcdefghijklmnopqrstuvwxyz";
      String storageGroupPath = "root."
              + chars.charAt((int) (Math.random() * 26))
              + chars.charAt((int) (Math.random() * 26))
              + i;
      setLines[i] = "SET STORAGE GROUP TO " + storageGroupPath;
      createLines[i] = "create timeseries " + storageGroupPath
              + ".wf01.wt01.status with datatype=BOOLEAN,encoding=PLAIN";
      for (int j = 0; j < Config.DATA_NUM; j++) {
        insertLines[i * Config.DATA_NUM + j] = "insert into " + storageGroupPath + ".wf01.wt01(timestamp,status) values(" + System.nanoTime() + ",true)";
      }
    }

    for (String s : setLines) {
      writer.write(s + ";\n");
    }
    for (String s : createLines) {
      writer.write(s + ";\n");
    }
    for (String s : insertLines) {
      writer.write(s + ";\n");
    }
    writer.close();
  }
}
