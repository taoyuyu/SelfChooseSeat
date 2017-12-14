package org.whu.yves.json;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.text.html.parser.Entity;
import org.apache.log4j.Logger;
import org.junit.Test;

public class JsonDataExtractor {
  private static Logger LOG = Logger.getLogger(JsonDataExtractor.class);
  @Test
  public void dataExtractor() {
    File file = new File("conf/data.json");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      JSONObject jsonObject = JSONObject.parseObject(sb.toString());
      Set<Entry<String, Object>> entrySet = ((JSONObject)((JSONObject) jsonObject.get("data")).get("layout")).entrySet();
      Iterator it = entrySet.iterator();
      while (it.hasNext()) {
        Entry<String, Object> entry =(Entry<String, Object>) it.next();
        JSONObject json = (JSONObject)entry.getValue();
        String id = json.getString("id");
        if (id != null) {
          if (json.getString("power").equals("true")) {
            System.out.println(String.format(" - '%s:%s'", json.getString("name"), id));
          }
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException ioe) {

      }

    }

  }
}
