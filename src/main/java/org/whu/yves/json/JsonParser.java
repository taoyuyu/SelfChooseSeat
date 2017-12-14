package org.whu.yves.json;

import com.alibaba.fastjson.JSONObject;

public class JsonParser {
  private JSONObject jsonObject ;
  public JsonParser(String jsonString) {
    jsonObject = JSONObject.parseObject(jsonString);
  }

  public String getToken() {
    JSONObject object = (JSONObject) jsonObject.get("data");
    return object.getString("token");
  }
  public String getStatus() {
    return jsonObject.getString("status");
  }
  public String getMessage() {
    return jsonObject.getString("message");
  }

}
