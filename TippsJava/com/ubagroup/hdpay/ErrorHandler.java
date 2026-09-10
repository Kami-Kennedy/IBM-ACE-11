/* Decompiler 16ms, total 276ms, lines 47 */
package com.ubagroup.hdpay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorHandler {
   public static void WriteError(String message) throws Exception {
      try {
         DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
         Date date = new Date();
         String currDate = dateFormat.format(date);
         DateFormat dtStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
         Date dateStamp = new Date();
         String transDateStamp = dtStamp.format(dateStamp);
         String path = "/LOG/system/HDPAY/hdpay Log ~ " + currDate + ".txt";
         File newTextFile = new File(path);
         if (newTextFile.exists() && !newTextFile.isDirectory()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(newTextFile, true));
            writer.newLine();
            writer.write("\r\nLog Entry:");
            writer.write("\r\n" + transDateStamp);
            writer.write("\r\nMESSAGE:");
            writer.write("\r\n" + message);
            writer.write("\r\n_________________________________________________________________________________");
            writer.flush();
            writer.close();
         } else {
            FileWriter fw = new FileWriter(newTextFile);
            fw.write("\r\nLog Entry:");
            fw.write("\r\n" + transDateStamp);
            fw.write("\r\nMESSAGE:");
            fw.write("\r\n" + message);
            fw.write("\r\n____________________________________________________________________________________");
            fw.flush();
            fw.close();
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

   }
}