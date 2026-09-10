/* Decompiler 17ms, total 905ms, lines 24 */
package com.ubagroup.hdpay;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;
import java.sql.Connection;
import java.util.ResourceBundle;

public abstract class BaseJavaComputeNode extends MbJavaComputeNode {
   public static final String APP_SERVICE_NAME = ResourceBundle.getBundle("com/ubagroup/hdpay/conf").getString("app.service.name");
   public static final String JDBC_PROVIDER_NAME = ResourceBundle.getBundle("com/ubagroup/hdpay/conf").getString("app.jdbc.provider.name");
   public static final String FINACLE_PROVIDER_NAME = ResourceBundle.getBundle("com/ubagroup/hdpay/conf").getString("app.jdbc.finacle.provider.name");

   public Connection getConnection() throws MbException {
      Connection conn = this.getJDBCType4Connection(JDBC_PROVIDER_NAME, JDBC_TransactionType.MB_TRANSACTION_AUTO);
      return conn;
   }

   public Connection getFinacleConnection() throws MbException {
      Connection conn = this.getJDBCType4Connection(FINACLE_PROVIDER_NAME, JDBC_TransactionType.MB_TRANSACTION_AUTO);
      return conn;
   }
}