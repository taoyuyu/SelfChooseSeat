package org.whu.yves.Main;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.whu.yves.https.HttpsRequest;
import org.whu.yves.json.JsonParser;

public class SelfChooseSeat {

  private static Logger LOG = Logger.getLogger(SelfChooseSeat.class);
  private static String logInUrl = "https://seat.lib.whu.edu.cn:8443/rest/auth?username=%s&password=%s";
  private static String freeBook = "https://seat.lib.whu.edu.cn:8443/rest/v2/freeBook";

  public static void main(String[] args) {
    YamlReader.prepare("conf/seat.yaml");
    ArrayList<String> users = YamlReader.getUsers();
    for (String user : users) {
      String[] str = user.split(":");
      chooseSeat(str[0], str[1], str[2]);
    }
  }


  public static void chooseSeat(String username, String password, String seat) {
    int status = 0;
    boolean fail = true;
    JsonParser parser = null;
    while (fail) {
      switch (status) {
        case 0:
          LOG.info(username + " login");
          HttpsRequest logInRequest = new HttpsRequest();
          String url = String.format(logInUrl, username, password);
          String response = logInRequest.doPost(url, null);
          if (logInRequest.getResponseCode() != 200) {
            break;
          } else {
            LOG.error(response);
          }
          parser = new JsonParser(response);
          if (parser.getStatus().toLowerCase().equals("success")) {
            status = 1;
            LOG.info(username + " login succeed");
          } else {
            break;
          }
        case 1:
          LOG.info("book seat");
          HashMap<String, String> parms = new HashMap<>();
          parms.put("token", parser.getToken());
          parms.put("seat", seat);
          HttpsRequest book = new HttpsRequest();
          String result = book.doPost(freeBook, parms);
          if (book.getResponseCode() != 200) {
            LOG.error("Book seat error");
            break;
          }
          LOG.info(result);
          fail = false;
      }
    }
  }

}
