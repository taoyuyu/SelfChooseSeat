package org.whu.yves.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

public class YamlReader {

  private static Logger LOG = Logger.getLogger(YamlReader.class);
  private static HashMap configuration = null;
  private static String tomorrow = null;
  private static Map<String, String> map = new HashMap<>();

  private YamlReader() {
  }

  private static void initMap() {
    putSeatToMap(getPower());
    putSeatToMap(getNoPower());
  }

  private static void putSeatToMap(ArrayList<String> seats) {
    for(String seat: seats) {
      String[] str = seat.split(":");
      map.put(str[0], str[1]);
    }
  }

  public static void prepare(String path) {
    setTomorrowDate();
    if (configuration == null) {
      synchronized (YamlReader.class) {
        File file = new File(path);
        try {
          configuration = Yaml.loadType(new FileInputStream(file), HashMap.class);
          initMap();
          double start = (Double) configuration.get("start");
          configuration.replace("start", (int) (start * 2 * 30));
          double end = (Double) configuration.get("end");
          configuration.replace("end", (int) (end * 2 * 30));
        } catch (FileNotFoundException fne) {
          LOG.error(String.format("file %s not found", file.getAbsoluteFile()));
          throw new RuntimeException();
        }
      }
    }
  }

  private static void setTomorrowDate() {
    Date date=new Date();//取时间
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
    date=calendar.getTime(); //这个时间就是日期往后推一天的结果
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    tomorrow = formatter.format(date);
  }


  public static ArrayList<String> getUsers() {
    return (ArrayList) configuration.get("users");
  }

  public static String getDate() {
    return (String) configuration.getOrDefault("date", tomorrow);
  }

  public static int getStart() {
    return (Integer) configuration.get("start");
  }

  public static int getEnd() {
    return (Integer) configuration.get("end");
  }

  public static ArrayList<String> getPower() {
    return (ArrayList<String>) configuration.get("power");
  }

  public static ArrayList<String> getNoPower() {
    return (ArrayList<String>) configuration.get("noPower");
  }

  public static String getSeatIdByName(String name) {
    return map.get(name);
  }

}

