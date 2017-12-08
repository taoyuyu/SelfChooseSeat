package org.whu.yves.https;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;

public class HttpsRequest {
  private static Logger LOG = Logger.getLogger(HttpsRequest.class);
  private int startTime = (int)(8.5 * 2 * 30);
  private int endTime = (int)(21.5 * 2*30);

  private static String sample = "toke=%s&startTime=%d&endTime=%d&seat=%s&date=%s";
  private int responseCode = -1;

  public String doPost(String hsUrl, HashMap<String, String> contentParms) {
    URL url;
    StringBuilder sb = new StringBuilder();

    try {
      url = new URL(hsUrl);
      HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
      if (contentParms != null) {
        con.setRequestProperty("token", contentParms.get("token"));
      }
      X509TrustManager xtm = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
          // TODO Auto-generated method stub

        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
          // TODO Auto-generated method stub

        }
      };

      TrustManager[] tm = {xtm};

      SSLContext ctx = SSLContext.getInstance("TLS");
      ctx.init(null, tm, null);

      con.setSSLSocketFactory(ctx.getSocketFactory());
      con.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
          return true;
        }
      });
      if (hsUrl.endsWith("freeBook")) {
        // 发送POST请求必须设置如下两行
        con.setDoOutput(true);
        con.setDoInput(true);
        String parms = String
            .format(sample, contentParms.get("token"),
                startTime, endTime, contentParms.get("seat"), contentParms.get("date"));
        LOG.info(parms);
        // 获取URLConnection对象对应的输出流
        PrintWriter out = new PrintWriter(con.getOutputStream());
        // 发送请求参数
        out.print(parms);
        // flush输出流的缓冲
        out.flush();
      } else if (contentParms != null) {
        Iterator iter = contentParms.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
          con.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }
      responseCode = con.getResponseCode();

      BufferedReader in = new BufferedReader(new InputStreamReader(
          con.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public int getResponseCode() {
    return this.responseCode;
  }

}
