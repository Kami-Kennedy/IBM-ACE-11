/* Decompiler 12ms, total 622ms, lines 43 */
package com.ubagroup.hdpay;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Acct_val_JavaCompute extends BaseJavaComputeNode {
   public void evaluate(MbMessageAssembly inAssembly) throws MbException {
      MbOutputTerminal out = this.getOutputTerminal("out");
      MbMessage inMessage = inAssembly.getMessage();
      MbMessageAssembly outAssembly = null;

      try {
         MbMessage outMessage = new MbMessage();
         outAssembly = new MbMessageAssembly(inAssembly, outMessage);
         MbElement message = inAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("Variables");
         ErrorHandler.WriteError("HDPAY ACCOUNT LOOKUP");
         String debitAccount = message.getFirstElementByPath("acctNum").getValueAsString();
         String narration = message.getFirstElementByPath("narration").getValueAsString();
         String amount = message.getFirstElementByPath("amount").getValueAsString();
         String creditAccount = message.getFirstElementByPath("creditAccount").getValueAsString();
         String creditAccountBankCode = message.getFirstElementByPath("creditAccountBankCode").getValueAsString();
         String mobileNum = message.getFirstElementByPath("mobileNum").getValueAsString();
         ErrorHandler.WriteError("Parameters received are: mobileNo: " + mobileNum + ", acctNum: " + debitAccount + ", narration: " + narration + ", amount: " + amount + ", creditAccount: " + creditAccount + ", bic: " + creditAccountBankCode);
         String response = Processor.accountLookup(this.getConnection(), mobileNum, creditAccount, creditAccountBankCode);
         MbElement outRoot = outMessage.getRootElement();
         MbElement root = outRoot.createElementAsLastChild("NONE");
         root.createElementAsLastChild(50331648, "BLOB", response.getBytes());
      } catch (MbException var16) {
         throw var16;
      } catch (RuntimeException var17) {
         throw var17;
      } catch (Exception var18) {
         throw new MbUserException(this, "evaluate()", "", "", var18.toString(), (Object[])null);
      }

      out.propagate(outAssembly);
   }
}