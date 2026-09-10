/* Decompiler 32ms, total 599ms, lines 72 */
package com.ubagroup.hdpay;

import org.json.JSONObject;

public class JSONParser {
   public String parser(String result) throws Exception {
      JSONObject resp = new JSONObject();
      JSONObject response = new JSONObject();
      JSONObject jsonResponse = new JSONObject(result);
      String statusCode = "";
      String message = "";
      String var7 = "";

      try {
         statusCode = jsonResponse.getString("stscode");
         if (statusCode.equalsIgnoreCase("HP00")) {
            message = jsonResponse.getString("message");
            resp.put("status", "success");
            resp.put("message", message);
            ErrorHandler.WriteError("account lookup success response is " + resp.toString());
         } else {
            message = jsonResponse.getString("message");
            resp.put("status", "failed");
            resp.put("message", message);
            ErrorHandler.WriteError("account lookup failure response is " + response.toString());
         }
      } catch (Exception var9) {
         ErrorHandler.WriteError("exception caught is " + var9.getMessage());
         var9.fillInStackTrace();
      }

      return resp.toString();
   }

   public String accountLookupParser(String result) throws Exception {
      JSONObject resp = new JSONObject();
      JSONObject response = new JSONObject();
      JSONObject jsonResponse = new JSONObject(result);
      String statusCode = "";
      String message = "";
      String finalResponse = "";
      String acctName = "";
      String acctNo = "";
      String identifier = "";

      try {
         statusCode = jsonResponse.getString("stscode");
         if (statusCode.equalsIgnoreCase("HP00")) {
            message = jsonResponse.getString("message");
            acctNo = jsonResponse.getJSONObject("acinfo").getJSONObject("acdetails").getString("acno");
            acctName = jsonResponse.getJSONObject("acinfo").getJSONObject("acdetails").getJSONObject("prielements").getString("fullName");
            identifier = jsonResponse.getJSONObject("acinfo").getJSONObject("acdetails").getJSONObject("prielements").getString("identifierType");
            resp.put("status", "success");
            resp.put("accountName", acctName);
            resp.put("identifierType", identifier);
            resp.put("message", message);
            ErrorHandler.WriteError("account lookup success response is " + response.toString());
         } else {
            message = jsonResponse.getString("message");
            resp.put("status", "failed");
            resp.put("message", message);
            ErrorHandler.WriteError("account lookup failure response is " + response.toString());
         }
      } catch (Exception var12) {
         ErrorHandler.WriteError("exception caught is " + var12.getMessage());
         var12.fillInStackTrace();
      }

      return resp.toString();
   }
}