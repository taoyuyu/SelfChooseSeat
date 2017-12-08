package org.whu.yves.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

public class YamlReader {
  private static Logger LOG = Logger.getLogger(YamlReader.class);
  private static HashMap configuration = null;
  private YamlReader() {
  }

  public static void prepare(String path) {
    if (configuration == null) {
      synchronized (YamlReader.class) {
        File file = new File(path);
        try {
          configuration = Yaml.loadType(new FileInputStream(file), HashMap.class);
        } catch (FileNotFoundException fne) {
          LOG.error(String.format("file %s not found", file.getAbsoluteFile()));
          throw new RuntimeException();
        }
      }
    }
  }


  public static ArrayList<String> getUsers() {
    return (ArrayList) configuration.get("users");
  }


}

