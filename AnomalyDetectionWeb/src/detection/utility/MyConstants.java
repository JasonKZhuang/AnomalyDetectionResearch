package detection.utility;

public class MyConstants
{
	public static final int K = 7;
	
	// Database credentials
	public static final String USER = "root";
	public static final String PASS = "p_3535252";

	// JDBC driver name and database URL
	//public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	
	public static final String DB_URL = "jdbc:mysql://localhost:3306/anomalyResearch?autoReconnect=true&useSSL=false&useServerPrepStmts=false&rewriteBatchedStatements=true"; 
	
	//Tables
	public static final String TABLE_STOCK_NAME = "kim_stocknames";
	
	public static final String TABLE_STOCK_RECORDS = "kim_stockrecords";
	
	public static final String TABLE_STOCK_RECORDS_PEER_GROUP = "kim_peergroups_records";
	
	public static final String TABLE_MEAN_VALUE     = "kim_mean_value";
	
	public static final String TABLE_STOCK_RETURN = "stockReturn";
	
	public static final String TABLE_JACCARD_INDEX = "jaccardIndex";
	
	public static final String TABLE_DIVIDEWINDOWS = "kim_dividedwindows";
	
	public static final String TABLE_PEER_GROUP = "kim_peergroup";
	
	public static final String TABLE_PEER_GROUPS = "kim_peergroups";
	
	public static final String TABLE_WEIGHTS = "kim_weights";
	
	public static final String TABLE_CELLS_DISTANCE = "kim_cellsdistance";
	
	//subdivide factor N
	public static final int dividedFactor = 1;
	
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
	
	//
	public static final int SIMILARITY_TOP_NUM = 11;//2,6,11;
	


	
	

}
