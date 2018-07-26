package detection.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.utility.MyConstants;

public class ConnectionSingleton
{
	protected static final Log myLog = LogFactory.getLog(DatabaseHelperImpl.class);
	
	private static ConnectionSingleton instance = null;
	
	private static Connection  conn = null;
	
	private ConnectionSingleton()
	{
		;
	}
	
	synchronized public static ConnectionSingleton getInstance()
	{
		if (instance == null)
		{
			instance = new ConnectionSingleton();
		}
		return instance;
	}
	
	synchronized public static Connection getConnection()
	{
		if (conn==null)
		{
			// JDBC
			try
			{
				myLog.info("Getting Connection...");
				Class.forName(MyConstants.JDBC_DRIVER);
				conn = DriverManager.getConnection(MyConstants.DB_URL, MyConstants.USER, MyConstants.PASS);
			} catch (SQLException e)
			{
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		return conn;
	}
}
