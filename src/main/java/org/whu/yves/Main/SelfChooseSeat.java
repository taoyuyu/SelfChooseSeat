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
    YamlReader.prepare(args[0]);
    LOG.info("date => " + YamlReader.getDate());

    ArrayList<String> users = YamlReader.getUsers();
    for (String user : users) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          String[] str = user.split(":");
          String[] seats = str[2].split(",");
          chooseSeat(str[0], str[1], seats);
        }
      }).start();
    }
  }


  public static void chooseSeat(String username, String password, String[] seats) {
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
            LOG.error("System error");
            break;
          }
          parser = new JsonParser(response);
          if (parser.getStatus().toLowerCase().equals("success")) {
            status = 1;
            LOG.info(username + " login succeed");
          } else {
            LOG.info(username + " login failed");
            break;
          }
        case 1:
          LOG.info("book seat");
          HashMap<String, String> parms = new HashMap<>();
          parms.put("token", parser.getToken());
          for (int i=0; i<seats.length; ++i) {
            String seat = seats[i];
            LOG.info("Booking seat: " + seat);
            parms.put("seat", seat);
            parms.put("date", YamlReader.getDate());

            HttpsRequest book = new HttpsRequest();
            String result = book.doPost(freeBook, parms);
            if (book.getResponseCode() != 200) {
              -- i;
              LOG.error("book seat error");
              continue;
            }
            JsonParser resultParser = new JsonParser(result);
            if (resultParser.getStatus().equals("fail")) {
              LOG.info(result);
              continue;
            } else if (resultParser.getStatus().equals("success")) {
              LOG.info(result);
              break;
            }
          }
          fail = false;
      }
    }
  }

}
