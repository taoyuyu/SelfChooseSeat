package org.whu.yves.json;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestJsonParser {
  private static Logger LOG = Logger.getLogger(TestJsonParser.class);
  @Test
  public void TestParser() {
    String jsonString = "{\"status\":\"success\",\"data\":{\"token\":\"D65PHTKCFE12081306\"},\"code\":\"0\",\"message\":\"\"}";
    JsonParser jsonParser = new JsonParser(jsonString);
    LOG.info(jsonParser.getToken());
    LOG.info(jsonParser.getStatus());
  }

}
