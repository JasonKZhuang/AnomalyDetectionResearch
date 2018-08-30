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
	public static final String TABLE_STOCK_NAME = "stockname_test";
	
	public static final String TABLE_STOCK_RECORDS = "stock_test";
	
	public static final String TABLE_JACCARD_INDEX = "jaccardIndex";
	
	public static final String TABLE_DIVIDEWINDOWS = "DividedWindows";
	
	public static final String TABLE_PEER_GROUP = "PeerGroup";
	
	public static final String TABLE_WEIGHTS = "weights";
	
	public static final String TABLE_CELLS_DISTANCE = "CellsDistance";
	
	//subdivide factor N
	public static final int dividedFactor = 5;
	
	//gamma coefficient,could be 0.5,1.0,1.5,2.0
	public static final double gamma = 0.5;

	
	

}
