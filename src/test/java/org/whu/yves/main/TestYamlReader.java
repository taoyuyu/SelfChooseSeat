package org.whu.yves.main;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.whu.yves.Main.YamlReader;

public class TestYamlReader {
  private static Logger LOG = Logger.getLogger(TestYamlReader.class);
  @Test
  public void testYamlReader() {
    YamlReader.prepare("conf/seat.yaml");
    LOG.info(YamlReader.getUsers());
    LOG.info(YamlReader.getDate());
    LOG.info(YamlReader.getStart());
    LOG.info(YamlReader.getEnd());
  }
}
