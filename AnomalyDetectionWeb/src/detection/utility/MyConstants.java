package detection.utility;

public class MyConstants
{
	public static final int K = 10;
	
	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "p_3535252";

	// JDBC driver name and database URL
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost:3306/myProject?autoReconnect=true&useSSL=false"; 
	
	//Tables
	public static final String TABLE_STOCK_NAME = "stockNames";
	
	public static final String TABLE_STOCK_RECORDS = "stockRecords";
	
	public static final String TABLE_STOCK_RETURN = "stockReturn";
	
	public static final String TABLE_JACCARD_INDEX = "jaccardIndex";
	
	public static final String TABLE_DIVIDEWINDOWS = "DividedWindows";
	
	public static final String TABLE_PEER_GROUP = "PeerGroup";
	
	public static final String TABLE_WEIGHTS = "weights";
	
	public static final String TABLE_CELLS_DISTANCE = "CellsDistance";
	
	//subdivide factor N
	public static final int dividedFactor = 5;
	
	//gamma coefficient,could be 0.5,1.0,1.5,2.0
	public static final double gamma = 0.5;
	
	//min date of data duration
	public static final String DATE_MIN_STR="2012-02-27";
	//max date of data duration
	public static final String DATE_MAX_STR="2017-11-10";
	//max number of trading day 
	public static final int TRADE_DAYS_MIN = 1439;
	//sub trading dates start index
	public static final int TRADE_SUBDAYS_START_INDEX = 1;
	//sub trading dates length
	public static final int TRADE_SUBDAYS_LENGTH = 100;
	
	
	//the coefficient of how others are my neighbors
	public static final float nearCoefficient = 0.1f;
	


	
	

}
