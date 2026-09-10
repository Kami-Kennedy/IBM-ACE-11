/* Decompiler 80ms, total 440ms, lines 183 */
package com.ubagroup.hdpay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpCall {
   public static String httpForToken(String serviceURL) throws Exception {
      String param = "USERID=" + URLEncoder.encode(Constants.hd_username, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(Constants.hd_password, "UTF-8") + "&CHCODE=" + URLEncoder.encode(Constants.channel_code, "UTF-8");
      System.out.println("URL : " + serviceURL);
      String[] testArr = serviceURL.split("//");
      String URI = testArr[1];
      testArr = URI.split(":");
      final String uri = testArr[0];
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession sslSession) {
            return hostname.equals(uri);
         }
      });
      URL url = new URL(serviceURL);
      SSLContext sc = SSLContext.getInstance("TLSv1.2");
      TrustManager[] trust_mgr = get_trust_mgr();
      sc.init((KeyManager[])null, trust_mgr, new SecureRandom());
      HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      conn.setSSLSocketFactory(sc.getSocketFactory());
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("Authorization", "Basic " + Constants.secret_key);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setUseCaches(false);
      OutputStreamWriter wr = null;
      BufferedReader rd = null;
      StringBuilder res = new StringBuilder();

      label104: {
         String var14;
         try {
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(param);
            wr.flush();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while(true) {
               String line;
               if ((line = rd.readLine()) == null) {
                  break label104;
               }

               res.append(line);
            }
         } catch (Exception var17) {
            ErrorHandler.WriteError("exception detected and re-thrown! " + var17.fillInStackTrace());
            var17.printStackTrace();
            var14 = res.toString();
         } finally {
            if (wr != null) {
               wr.close();
            }

            if (rd != null) {
               rd.close();
            }

         }

         return var14;
      }

      if (wr != null) {
         wr.close();
      }

      if (rd != null) {
         rd.close();
      }

      return res.toString();
   }

   public static String processReq(String req, String serviceURL, String token) throws Exception {
      System.out.println("URL : " + serviceURL);
      String[] testArr = serviceURL.split("//");
      String URI = testArr[1];
      testArr = URI.split(":");
      final String uri = testArr[0];
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession sslSession) {
            return hostname.equals(uri);
         }
      });
      URL url = new URL(serviceURL);
      SSLContext sc = SSLContext.getInstance("TLSv1.2");
      TrustManager[] trust_mgr = get_trust_mgr();
      sc.init((KeyManager[])null, trust_mgr, new SecureRandom());
      HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      conn.setSSLSocketFactory(sc.getSocketFactory());
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("ChannelID", Constants.channel_code);
      conn.setRequestProperty("VERSION", "1.0");
      conn.setRequestProperty("Authorization", "Bearer " + token);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      OutputStreamWriter wr = null;
      BufferedReader rd = null;
      StringBuilder res = new StringBuilder();

      label104: {
         String var15;
         try {
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(req);
            wr.flush();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while(true) {
               String line;
               if ((line = rd.readLine()) == null) {
                  break label104;
               }

               res.append(line);
            }
         } catch (Exception var18) {
            ErrorHandler.WriteError("exception detected and re-thrown! " + var18.fillInStackTrace());
            var18.printStackTrace();
            var15 = res.toString();
         } finally {
            if (wr != null) {
               wr.close();
            }

            if (rd != null) {
               rd.close();
            }

         }

         return var15;
      }

      if (wr != null) {
         wr.close();
      }

      if (rd != null) {
         rd.close();
      }

      return res.toString();
   }

   private static TrustManager[] get_trust_mgr() {
      TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
         public X509Certificate[] getAcceptedIssuers() {
            return null;
         }

         public void checkClientTrusted(X509Certificate[] certs, String t) {
         }

         public void checkServerTrusted(X509Certificate[] certs, String t) {
         }
      }};
      return certs;
   }
}