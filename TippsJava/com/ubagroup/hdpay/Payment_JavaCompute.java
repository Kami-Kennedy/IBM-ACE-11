/* Decompiler 21ms, total 325ms, lines 45 */
package com.ubagroup.hdpay;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Payment_JavaCompute extends BaseJavaComputeNode {
   public void evaluate(MbMessageAssembly inAssembly) throws MbException {
      MbOutputTerminal out = this.getOutputTerminal("out");
      MbMessageAssembly outAssembly = null;

      try {
         MbMessage outMessage = new MbMessage();
         outAssembly = new MbMessageAssembly(inAssembly, outMessage);
         MbElement message = inAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("Variables");
         ErrorHandler.WriteError("HDPAY PAYMENT");
         String debitAccount = message.getFirstElementByPath("acctNum").getValueAsString();
         String narration = message.getFirstElementByPath("narration").getValueAsString();
         String amount = message.getFirstElementByPath("amount").getValueAsString();
         String acctType = message.getFirstElementByPath("acctType").getValueAsString();
         String creditAccount = message.getFirstElementByPath("creditAccount").getValueAsString();
         String creditAccountBankCode = message.getFirstElementByPath("creditAccountBankCode").getValueAsString();
         String mobileNum = message.getFirstElementByPath("mobileNum").getValueAsString();
         String acctName = message.getFirstElementByPath("acctName").getValueAsString();
         String identifierType = message.getFirstElementByPath("identifierType").getValueAsString();
         ErrorHandler.WriteError("Parameters received are: mobileNo: " + mobileNum + ", acctNum: " + debitAccount + ", narration: " + narration + ", amount: " + amount + ", acctType: " + acctType + ", creditAccount: " + creditAccount + ", bic: " + creditAccountBankCode + ", acctName " + acctName + ", identifier " + identifierType);
         String response = Processor.payment(this.getConnection(), mobileNum, debitAccount, narration, amount, acctType, creditAccount, creditAccountBankCode, acctName, identifierType);
         MbElement outRoot = outMessage.getRootElement();
         MbElement root = outRoot.createElementAsLastChild("NONE");
         root.createElementAsLastChild(50331648, "BLOB", response.getBytes());
      } catch (MbException var18) {
         throw var18;
      } catch (RuntimeException var19) {
         throw var19;
      } catch (Exception var20) {
         throw new MbUserException(this, "evaluate()", "", "", var20.toString(), (Object[])null);
      }

      out.propagate(outAssembly);
   }
}