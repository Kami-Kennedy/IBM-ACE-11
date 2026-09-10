/* Decompiler 76ms, total 380ms, lines 176 */
package com.ubagroup.hdpay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import org.json.JSONObject;

public class Utilities {
   public String tranGen() throws Exception {
      int max = 10000000;
      int min = 1;
      int random = (int)(Math.random() * (double)max + (double)min);
      String trx = String.valueOf(random);
      String tranRef = "ubatips" + trx;
      ErrorHandler.WriteError("reference is " + tranRef);
      return tranRef;
   }

   public String tranRef() throws Exception {
      int max = 10000000;
      int min = 1;
      int random = (int)(Math.random() * (double)max + (double)min);
      String trx = String.valueOf(random);
      String tranRef = "trf" + trx;
      ErrorHandler.WriteError("tran ref is " + tranRef);
      return tranRef;
   }

   public String date() throws Exception {
      Date date = new Date();
      DateFormat NewdateFormat = new SimpleDateFormat("dd-MMM-yyyy");
      String currDate = NewdateFormat.format(date);
      ErrorHandler.WriteError("date is " + currDate);
      return currDate;
   }

   public String[] generateToken(Connection conn) {
      String token = "";
      String response = "";
      String tokenRespStatus = "";
      String tokenRespExp = "";

      try {
         response = HttpCall.httpForToken(Constants.token_url);
         JSONObject obj = new JSONObject(response);
         tokenRespStatus = obj.getString("result");
         ErrorHandler.WriteError("token response is " + tokenRespStatus);
         if (tokenRespStatus.equalsIgnoreCase("success")) {
            token = obj.getString("token").trim();
            tokenRespExp = obj.getString("expires");
            long tokenExp = Instant.now().getEpochSecond() + Long.parseLong(tokenRespExp);
            insertToken(conn, token, String.valueOf(tokenExp));
            ErrorHandler.WriteError("token generated is " + token);
         } else {
            token = obj.getString("message").trim();
            tokenRespStatus = "failed";
         }
      } catch (Exception var9) {
         var9.getMessage();
      }

      return new String[]{tokenRespStatus, token};
   }

   public static void insertToken(Connection conn, String token, String acctNo) throws Exception {
      String query1 = "insert into ESBUSER.HDPAY(TOKEN, TOKENDATE, ACCTNO) values (?,?,?)";
      PreparedStatement ps = null;

      try {
         ps = conn.prepareStatement("insert into ESBUSER.HDPAY(TOKEN, TOKENDATE, ACCTNO) values (?,?,?)");
         ps.setString(1, token);
         ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
         ps.setString(3, acctNo);
         ps.executeUpdate();
         ErrorHandler.WriteError("New token successfully inserted into the Database");
      } catch (Exception var14) {
         ErrorHandler.WriteError("Exception caught while inserting New token for hdpay! " + var14.getMessage());
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (Exception var13) {
               var13.getMessage();
            }
         }

      }

   }

   public String[] checkAccessToken(Connection conn) throws Exception {
      String[] token = null;
      TokenParam tk = null;
      String tkn = "";
      String tknMessage = "";

      try {
         tk = getToken(conn);
         tkn = tk.getAccessToken();
         long accessTokenExpr = tk.getAccessTokenExp();
         tknMessage = "success";
         ErrorHandler.WriteError("token extracted from check token access is  " + tkn);
         ErrorHandler.WriteError("access token expr is  " + accessTokenExpr);
         long currentTime = Instant.now().getEpochSecond();
         ErrorHandler.WriteError("current time is  " + currentTime);
         if (tkn == null) {
            ErrorHandler.WriteError("access token is null");
            token = this.generateToken(conn);
            tkn = token[1];
            tknMessage = token[0];
         } else if (tkn != null && currentTime >= accessTokenExpr) {
            ErrorHandler.WriteError("access token expired");
            token = this.generateToken(conn);
            tkn = token[1];
            tknMessage = token[0];
         } else {
            ErrorHandler.WriteError("access token is still valid");
         }
      } catch (Exception var10) {
         var10.fillInStackTrace();
         ErrorHandler.WriteError("exception occured --->  " + var10.getMessage());
      }

      return new String[]{tknMessage, tkn};
   }

   public static TokenParam getToken(Connection conn) throws Exception {
      String token = "";
      String accessTokenExp = "";
      TokenParam tk = new TokenParam();
      String query = "select a.token, a.acctno from ESBUSER.HDPAY a where a.tokendate = (select max(b.tokendate) from ESBUSER.HDPAY b)";
      PreparedStatement ps = null;
      ResultSet rs = null;

      try {
         ps = conn.prepareStatement("select a.token, a.acctno from ESBUSER.HDPAY a where a.tokendate = (select max(b.tokendate) from ESBUSER.HDPAY b)");
         rs = ps.executeQuery();
         if (rs.next()) {
            token = rs.getString("token");
            ErrorHandler.WriteError("token from Database is  " + token);
            accessTokenExp = rs.getString("ACCTNO");
            ErrorHandler.WriteError("token exp from Database is  " + accessTokenExp);
            long accessTknExp = Long.parseLong(accessTokenExp);
            tk.setAccessToken(token);
            tk.setAccessTokenExp(accessTknExp);
         }
      } catch (Exception var21) {
         ErrorHandler.WriteError("Exception caught while selecting token for hdpay! " + var21.getMessage());
      } finally {
         if (rs != null) {
            try {
               rs.close();
            } catch (Exception var20) {
               var20.getMessage();
            }
         }

         if (ps != null) {
            try {
               ps.close();
            } catch (Exception var19) {
               var19.getMessage();
            }
         }

      }

      return tk;
   }
}