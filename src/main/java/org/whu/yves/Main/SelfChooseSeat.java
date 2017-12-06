package org.whu.yves.Main;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.whu.yves.http.HttpRequest;

public class SelfChooseSeat {
  private static Logger LOG = Logger.getLogger(SelfChooseSeat.class);
  public static void main(String[] args) {
    // 预约;
    // http://system.lib.whu.edu.cn/libseat-wechat/freeBook?startTime=now&endTime=690&seat=31280&date=2017-12-06

    // http://system.lib.whu.edu.cn/libseat-wechat/getStartTimes?siteId=31280&date=2017-12-06

    //登录
    // http://system.lib.whu.edu.cn/libseat-wechat/index?code=08198HOn0LdIxr1WvWMn0fWGOn098HOJ&state=123

    // System.out.println(HttpRequest.sendGet("http://system.lib.whu.edu.cn/libseat-wechat/index", "code=08198HOn0LdIxr1WvWMn0fWGOn098HOq&state=123", null));

    String session = "JSESSIONID=A355A77F89A7F20D5D3ED6E4FDFEFF5E";
    if (freeBook(session)) {
      currentBoodking(session);
    }

  }

  private static boolean freeBook(String session) {
    String response = HttpRequest.sendGet("http://system.lib.whu.edu.cn/libseat-wechat/freeBook",
        "startTime=now&endTime=960&seat=35117&date=2017-12-06", session);
    if (response.contains("系统已经为你预约好座位")) {
      LOG.info("book succeed");
      return true;
    } else {
      LOG.info("book failed");
      return false;
    }
  }

  private static void currentBoodking(String session) {
    String html = HttpRequest
        .sendGet("http://system.lib.whu.edu.cn/libseat-wechat/currentBooking", "", session);
    System.out.println(html);
    Document document = Jsoup.parse(html);
    // [{"id":1311504,"receipt":"0224-504-3","onDate":"2017-12-06","seatId":35117,"status":"RESERVE","location":"总馆3层A3区A3，座位号014","begin":"15:21","end":"16:00","actualBegin":null,"awayBegin":null,"awayEnd":null,"userEnded":false,"message":"请在 12月06日15点06分 至 15点56分 之间前往场馆签到"}]
    String json = document.select("input[id=\"books\"]").attr("value");
    if (json.equals("null")) {
      LOG.info("无预约");
    } else {
      System.out.println(json);
    }
  }


}
