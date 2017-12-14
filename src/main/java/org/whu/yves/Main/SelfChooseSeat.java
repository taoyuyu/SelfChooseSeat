package org.whu.yves.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.log4j.Logger;
import org.whu.yves.https.HttpsRequest;
import org.whu.yves.json.JsonParser;

public class SelfChooseSeat {

  private static Logger LOG = Logger.getLogger(SelfChooseSeat.class);
  private static String logInUrl = "https://seat.lib.whu.edu.cn:8443/rest/auth?username=%s&password=%s";
  private static String freeBook = "https://seat.lib.whu.edu.cn:8443/rest/v2/freeBook";
  private static BlockingQueue<String> queue = new LinkedBlockingDeque<>();

  public static void main(String[] args) throws InterruptedException {
    YamlReader.prepare(args[0]);
    LOG.info("date => " + YamlReader.getDate());
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          for (String temp : YamlReader.getPower()) {
            queue.put(temp.split(":")[1]);
          }

          for (String temp : YamlReader.getNoPower()) {
            queue.put(temp.split(":")[1]);
          }
        } catch (InterruptedException ie) {

        }
      }
    }).start();

    ArrayList<String> users = YamlReader.getUsers();
    for (String user : users) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          String[] str = user.split(":");
          String[] seats = str[2].split(",");
          String token = logIn(str[0], str[1]);
          for (String seatName : seats) {
            new Thread(new Runnable() {
              @Override
              public void run() {
                HashMap<String, String> parms = new HashMap<>();
                parms.put("token", token);
                parms.put("seat", YamlReader.getSeatIdByName(seatName));
                parms.put("date", YamlReader.getDate());
                chooseOneSeat(parms);
              }
            }).start();
          }
        }
      }).start();
    }
  }

  public static String logIn(String username, String password) {
    // return token;
    LOG.info(username + " login");
    HttpsRequest logInRequest = new HttpsRequest();
    String url = String.format(logInUrl, username, password);
    String response = logInRequest.doPost(url, null);
    if (logInRequest.getResponseCode() != 200) {
      LOG.error("System error");
      return logIn(username, password);
    }
    JsonParser parser = new JsonParser(response);
    if (parser.getStatus().toLowerCase().equals("success")) {
      LOG.info(username + " login succeed");
    } else {
      LOG.info(username + " login failed");
      return logIn(username, password);
    }
    return parser.getToken();
  }

  public static void chooseOneSeat(HashMap<String, String> parms) {
    HttpsRequest book = new HttpsRequest();
    String result = book.doPost(freeBook, parms);
    if (book.getResponseCode() != 200) {
      //重试
      LOG.info("retry seat: " + parms.get("seat"));
      chooseOneSeat(parms);
    } else {
      JsonParser parser = new JsonParser(result);
      LOG.info(result);
      if(parser.getStatus().equals("fail")) {
        String message = parser.getMessage();
        if (message.startsWith("系统可预约时间为")) {

        }
        if (message.startsWith("已有1个有效预约")) {
          LOG.info("thread exit");
          return;
        }
        try {
          //选择下一个座位 message :预约失败; 参数错误
          String seat = queue.take();
          LOG.info("pick next seat: " + seat);
          parms.put("seat", seat);
          chooseOneSeat(parms);

        } catch (InterruptedException ie) {
          LOG.error(ie.getMessage());
        }
      } else if (parser.getStatus().equals("success")) {
        LOG.info(result);
      }
    }
  }

}
