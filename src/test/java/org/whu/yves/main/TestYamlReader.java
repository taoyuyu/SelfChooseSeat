package org.whu.yves.main;

import java.nio.charset.Charset;
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

    LOG.info(YamlReader.getNoPower());
    LOG.info(YamlReader.getNoPower());

    LOG.info(YamlReader.getSeatIdByName("145"));
  }

  @Test
  public void testString() {
    String str = "homeworks=null;s2.id=1003058075;s2.lessons=s13;s2.name=\"\u4E13\u9898\u3007  \u521D\u8BC6MATLAB\"";
    System.out.println(new String(str.getBytes(), Charset.forName("utf-8")));
  }
}
