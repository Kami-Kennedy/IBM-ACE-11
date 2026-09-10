/* Decompiler 59ms, total 367ms, lines 189 */
package com.ubagroup.hdpay;

import java.sql.Connection;
import org.json.JSONObject;

public class Processor {
   public static String accountLookup(Connection conn, String mobileNum, String creditAccount, String creditAccountBankCode) throws Exception {
      JSONObject resp = new JSONObject();
      new JSONObject();
      JSONParser parser = new JSONParser();
      Utilities util = new Utilities();
      String jsonRequest = "";
      String response = "";
      String token = "";
      String result = "";
      String idenType = "";
      String date = "";
      String[] tknResp = null;
      int newCreditBankCode = Integer.valueOf(creditAccountBankCode);
      if (newCreditBankCode < 500) {
         idenType = "BANK";
      }

      if (newCreditBankCode >= 500 && newCreditBankCode <= 510) {
         idenType = "MSISDN";
      }

      if (newCreditBankCode > 510) {
         idenType = "WALLET";
      }

      try {
         ErrorHandler.WriteError("i am here");
         tknResp = util.checkAccessToken(conn);
         ErrorHandler.WriteError("token extracted is  " + tknResp[1]);
         ErrorHandler.WriteError("token message is  " + tknResp[0]);
         if (tknResp[0].equalsIgnoreCase("failed")) {
            resp.put("status", "failed");
            resp.put("message", "error occured generating access token");
            response = resp.toString();
         } else {
            date = util.date();
            JSONObject primaryArgsReq = new JSONObject();
            primaryArgsReq.put("acno", creditAccount);
            primaryArgsReq.put("custno", mobileNum);
            primaryArgsReq.put("identifier", creditAccount);
            primaryArgsReq.put("identifiertype", idenType);
            primaryArgsReq.put("msisdn", mobileNum);
            primaryArgsReq.put("nin", "");
            primaryArgsReq.put("firstname", "");
            primaryArgsReq.put("middlename", "");
            primaryArgsReq.put("lastname", "");
            primaryArgsReq.put("fullname", "");
            primaryArgsReq.put("bankcode", creditAccountBankCode);
            JSONObject nestedObj = new JSONObject();
            nestedObj.put("paytype", "TIPS");
            nestedObj.put("date", date);
            nestedObj.put("reqrefid", util.tranGen());
            nestedObj.put("initiatedby", "IB");
            nestedObj.put("primaryarg", primaryArgsReq);
            JSONObject mainRequest = new JSONObject();
            mainRequest.put("aclookup", nestedObj);
            jsonRequest = mainRequest.toString();
            ErrorHandler.WriteError("account look up request is " + jsonRequest);
            result = HttpCall.processReq(jsonRequest, Constants.acct_lookup_url, tknResp[1]);
            ErrorHandler.WriteError("account lookup response is " + result);
            if (result != null && result != "") {
               response = parser.accountLookupParser(result);
            } else {
               resp.put("status", "failed");
               resp.put("message", "Unable to process account lookup request from HDPay");
               response = resp.toString();
            }
         }
      } catch (Exception var18) {
         ErrorHandler.WriteError(var18.getMessage());
         resp.put("status", "failed");
         resp.put("message", "error while processing request. try later");
         response = resp.toString();
      }

      return response;
   }

   public static String payment(Connection conn, String mobileNum, String debitAccount, String narration, String amount, String acctType, String creditAccount, String creditAccountBankCode, String acctName, String identifierType) throws Exception {
      JSONObject resp = new JSONObject();
      JSONObject Response = new JSONObject();
      JSONParser parser = new JSONParser();
      Utilities util = new Utilities();
      String jsonRequest = "";
      String response = "";
      String token = "";
      String result = "";
      String date = "";
      String[] tknResp = null;

      try {
         ErrorHandler.WriteError("i am here");
         tknResp = util.checkAccessToken(conn);
         ErrorHandler.WriteError("token extracted is  " + tknResp[1]);
         ErrorHandler.WriteError("token message is  " + tknResp[0]);
         if (tknResp[0].equalsIgnoreCase("failed")) {
            resp.put("status", "failed");
            resp.put("message", "error occured generating access token");
            response = resp.toString();
         } else {
            date = util.date();
            JSONObject transferDetails = new JSONObject();
            transferDetails.put("tranrefid", util.tranRef());
            transferDetails.put("tranamt", amount);
            transferDetails.put("trancurr", "TZS");
            JSONObject s_identify = new JSONObject();
            s_identify.put("type", "");
            s_identify.put("value", "");
            JSONObject feeObj = new JSONObject();
            feeObj.put("feeamt", "0");
            feeObj.put("feecurr", "TZS");
            JSONObject sourcedtl = new JSONObject();
            if (debitAccount.trim().startsWith("56")) {
               sourcedtl.put("s_accoutno", debitAccount);
               sourcedtl.put("s_identifiertype", "BANK");
               sourcedtl.put("s_mobile", mobileNum);
               sourcedtl.put("s_bic", Constants.bank_code);
               sourcedtl.put("s_actype", acctType);
               sourcedtl.put("s_identify", s_identify);
               sourcedtl.put("fee", feeObj);
            } else {
               String lastFour = debitAccount.substring(debitAccount.length() - 4);
               String remainingDigitsStr = debitAccount.substring(0, debitAccount.length() - 4);
               sourcedtl.put("s_pin", lastFour);
               sourcedtl.put("s_accoutno", remainingDigitsStr);
               sourcedtl.put("s_identifiertype", "BANK");
               sourcedtl.put("s_mobile", mobileNum);
               sourcedtl.put("s_bic", Constants.bank_code);
               sourcedtl.put("s_actype", acctType);
               sourcedtl.put("s_identify", s_identify);
               sourcedtl.put("fee", feeObj);
            }

            JSONObject d_identify = new JSONObject();
            d_identify.put("type", "");
            d_identify.put("value", "");
            JSONObject destinationdtl = new JSONObject();
            destinationdtl.put("d_accoutno", creditAccount);
            destinationdtl.put("d_identifier", creditAccount);
            destinationdtl.put("d_identifiertype", identifierType);
            destinationdtl.put("d_accountcategory", "PERSON");
            destinationdtl.put("d_actype", "WALLET");
            destinationdtl.put("d_bic", creditAccountBankCode);
            destinationdtl.put("d_mobile", creditAccountBankCode);
            destinationdtl.put("d_identify", d_identify);
            destinationdtl.put("d_accoutname", acctName);
            JSONObject nestedObj = new JSONObject();
            nestedObj.put("paytype", "TIPS");
            nestedObj.put("date", date);
            nestedObj.put("reqrefid", util.tranGen());
            nestedObj.put("paydate", util.date());
            nestedObj.put("initiatedby", "HDPAY");
            nestedObj.put("trantype", "TRANSFER");
            nestedObj.put("description", narration);
            nestedObj.put("trandetail", transferDetails);
            nestedObj.put("sourcedtl", sourcedtl);
            nestedObj.put("destinationdtl", destinationdtl);
            JSONObject mainRequest = new JSONObject();
            mainRequest.put("transfers", nestedObj);
            jsonRequest = mainRequest.toString();
            ErrorHandler.WriteError("payment request is " + jsonRequest);
            result = HttpCall.processReq(jsonRequest, Constants.payment_url, tknResp[1]);
            ErrorHandler.WriteError("payment response is " + result);
            if (result != null && result != "") {
               response = parser.parser(result);
            } else {
               resp.put("status", "failed");
               resp.put("message", "Unable to process payment request from HDPay");
               Response.put("Response", resp);
               response = Response.toString();
            }
         }
      } catch (Exception var28) {
         ErrorHandler.WriteError(var28.getMessage());
         resp.put("status", "failed");
         resp.put("message", "error while processing request. try later");
         response = resp.toString();
      }

      return response;
   }
}