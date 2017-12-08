package org.whu.yves.main;

import org.junit.Test;
import org.whu.yves.Main.YamlReader;

public class TestYamlReader {
  @Test
  public void testYamlReader() {
    YamlReader.prepare("conf/seat.yaml");
    System.out.println(YamlReader.getUsers());
  }
}
