package org.whu.yves.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.whu.yves.https.HttpsRequest;
import org.whu.yves.json.JsonParser;

public class SelfChooseSeat {

  private static Logger LOG = Logger.getLogger(SelfChooseSeat.class);
  private static String logInUrl = "https://seat.lib.whu.edu.cn:8443/rest/auth?username=%s&password=%s";
  private static String freeBook = "https://seat.lib.whu.edu.cn:8443/rest/v2/freeBook";
  private static String dateString = null;
  public static void main(String[] args) {
    Date date=new Date();//取时间
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
    date=calendar.getTime(); //这个时间就是日期往后推一天的结果
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    dateString = formatter.format(date);

    LOG.info(dateString);
    YamlReader.prepare(args[0]);
    ArrayList<String> users = YamlReader.getUsers();
    for (String user : users) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          String[] str = user.split(":");
          chooseSeat(str[0], str[1], str[2]);
        }
      }).start();
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
            LOG.error(response);
            break;
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
          parms.put("date", dateString);

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
