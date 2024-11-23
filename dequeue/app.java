// MQ
import java.io.Console;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

// Db2
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

// 入力受け取り
import java.util.Scanner;

// メイン
public class app {

	// System exit status value (assume unset value to be 1)
	private static int status = 1;
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	private static final String APP_USER = "app"; // User name that application uses to connect to MQ
	private static final String APP_PASSWORD = "passw0rd"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from

	/**
	 * Main method
	 *
	 * @param args
	 */
    public static void main(String[] args) {
		while (true) { // 無限ループ
			String receivedMessage = MQGet();
			if (receivedMessage != null) {
				Db2Insert(receivedMessage);
			}
            try {
                Thread.sleep(5000); // 1秒間待機 (1000ミリ秒)
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted.");
                break; // 例外が発生した場合ループを終了
            }
        }
    }

    private static void MQPut()  {

		// Variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			//cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12ORHIGHER");

			// Create JMS objects
			context = cf.createContext();
			destination = context.createQueue("queue:///" + QUEUE_NAME);

			System.out.println("Please enter your message:");
			Scanner sc = new Scanner(System.in);
			long uniqueNumber = System.currentTimeMillis() % 1000;
        	String message = "Test" + uniqueNumber;

			producer = context.createProducer();
			producer.send(destination, message);
			System.out.println("\n【MQ】Sent message:\n" + message);

            context.close();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

		// System.exit(status);

	}

	private static String MQGet() {

		// Variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;
        String receivedMessage = null;

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			//cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12ORHIGHER");

			// Create JMS objects
			context = cf.createContext();
			destination = context.createQueue("queue:///" + QUEUE_NAME);

			consumer = context.createConsumer(destination); // autoclosable
			receivedMessage = consumer.receiveBody(String.class, 15000); // in ms or 15 seconds

			System.out.println("\n【MQ】Received message:\n" + receivedMessage);

			context.close();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

        return receivedMessage;
		//System.exit(status);

	} // end MQGet()

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}


    private static void Db2Insert(String input)  {
        // DB2データベースへの接続情報
        String jdbcUrl = "jdbc:db2://localhost:50000/testdb";
        String user = "db2inst1";
        String password = "Passw0rd";

        // JDBCコネクションの初期化
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // JDBCドライバのロード
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // DB2データベースへの接続
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            // SQLクエリの実行
            statement = connection.createStatement();
			String query = String.format("insert into test_table values('%s')", input);
			statement.executeUpdate(query);  // INSERT文には executeUpdate() を使用
			System.out.println("\n【Db2】Insert Message:\n" + input);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // リソースのクローズ
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	private static void Db2Select()  {
        // DB2データベースへの接続情報
        String jdbcUrl = "jdbc:db2://localhost:50000/testdb";
        String user = "db2inst1";
        String password = "Passw0rd";

        // JDBCコネクションの初期化
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // JDBCドライバのロード
            Class.forName("com.ibm.db2.jcc.DB2Driver");

            // DB2データベースへの接続
            connection = DriverManager.getConnection(jdbcUrl, user, password);

            // SQLクエリの実行
            statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM test_table");

            // 結果の表示
			System.out.println("【Db2】Table contents:");
            while (resultSet.next()) {
                System.out.println("Message: " + resultSet.getString("MESSAGE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // リソースのクローズ
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}