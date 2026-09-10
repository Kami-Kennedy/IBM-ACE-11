/* Decompiler 5ms, total 320ms, lines 30 */
package HDPay.com.ubagroup.hdpay;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class Acct_val_JavaCompute extends MbJavaComputeNode {
   public void evaluate(MbMessageAssembly inAssembly) throws MbException {
      MbOutputTerminal out = this.getOutputTerminal("out");
      MbMessage inMessage = inAssembly.getMessage();
      MbMessageAssembly outAssembly = null;

      try {
         MbMessage outMessage = new MbMessage(inMessage);
         outAssembly = new MbMessageAssembly(inAssembly, outMessage);
      } catch (MbException var6) {
         throw var6;
      } catch (RuntimeException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new MbUserException(this, "evaluate()", "", "", var8.toString(), (Object[])null);
      }

      out.propagate(outAssembly);
   }
}