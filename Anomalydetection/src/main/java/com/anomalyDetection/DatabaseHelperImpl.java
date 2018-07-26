package com.anomalyDetection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.beans.StockBeanKNN;
import detection.beans.StockRecord;

public class DatabaseHelperImpl implements IDatabaseHelper
{

	protected static final Log LOG = LogFactory.getLog(DatabaseHelperImpl.class);

	private static Connection conn = null;

	// Database credentials
	private static final String USER = "root";
	private static final String PASS = "p_3535252";

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/myProject?autoReconnect=true&useSSL=false";

	public Connection getConnection()
	{
		// JDBC
		try
		{
			System.out.println("Getting Connection...");
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e)
		{
			LOG.info(e.getMessage());
		} catch (ClassNotFoundException e)
		{
			LOG.info(e.getMessage());
		}
		return conn;
	}
	
	public List<String> getStockNames()
	{
		List<String> retList = new ArrayList<String>();
		
		if (conn == null)
		{
			conn = this.getConnection();
		}
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT id, name FROM stockname_test";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("name");
				retList.add(stockName);
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2)
			{
			} // nothing we can do
		} // end try
		return retList;
	}

	public List<String> getTransactionDates()
	{
		List<String> retList = new ArrayList<String>();
		if (conn == null)
		{
			conn = this.getConnection();
		}
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT t.tdate as myDate from stock_test t GROUP BY t.tdate ORDER BY t.tdate ASC ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				//Retrieve by column name
				Date myDate = rs.getDate("myDate");
				retList.add(DateUtil.convertDateToString(myDate));
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2)
			{
			} // nothing we can do
		} // end try
		return retList;
	}

	public List<StockRecord> getAllRecords()
	{
		List<StockRecord> retList = new ArrayList<StockRecord>();
		if (conn == null)
		{
			conn = this.getConnection();
		}
		Statement stmt = null;
		try
		{
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT id, stockName, tdate, close FROM stock_new";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				int id = rs.getInt("id");
				String stockName = rs.getString("stockName");
				Date tdate = rs.getDate("tdate");
				float close = rs.getFloat("close");

				StockRecord bean = new StockRecord();
				bean.setStockName(stockName);
				bean.setTdate(tdate);
				bean.setClose(close);
				retList.add(bean);
				
				// Display values
				System.out.print("ID: " + id);
				System.out.print(", stockName: " + stockName);
				System.out.print(", tdate: " + tdate.toString());
				System.out.println(", close: " + close);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2)
			{
			} // nothing we can do
		} // end try
		
		System.out.println("Goodbye!");
		
		return retList;
		
	}

	public StockRecord getRecordByNameAndDate(String aName, String aDate)
	{
		StockRecord bean  = new StockRecord();
		
		if (conn == null)
		{
			conn = this.getConnection();
		}
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM stock_test t WHERE t.stockName = ? and t.tdate = ? ";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aName);
			pstmt.setDate(2, java.sql.Date.valueOf(aDate));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("stockName");
				Date tdate = rs.getDate("tdate");
				float close = rs.getFloat("close");
				float open = rs.getFloat("open");
				
				bean.setStockName(stockName);
				bean.setTdate(tdate);
				bean.setClose(close);
				bean.setOpen(open);
				break;
			}
			// STEP 6: Clean-up environment
			rs.close();
			pstmt.close();
		} catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally
		{
			// finally block used to close resources
			try
			{
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2)
			{
			} // nothing we can do
		} // end try
		
		return bean;
	}

	public List<StockRecord> getRecordsByDay(String oneDay)
	{
		List<StockRecord> retList = new ArrayList<StockRecord>();
		
		Date lDate = new Date();
		try
		{
			lDate = DateUtil.convertStringToDate(oneDay);
		} catch (ParseException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if (conn == null)
		{
			conn = this.getConnection();
		}
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT t.* FROM myProject.stock_test t where t.tdate = '" + oneDay + "'";
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("stockName");
				float close = rs.getFloat("close");
				float open  = rs.getFloat("open");
				StockRecord bean = new StockRecord();
				bean.setStockName(stockName);
				bean.setTdate(lDate);
				bean.setOpen(open);
				bean.setClose(close);
				retList.add(bean);
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2)
			{
			} // nothing we can do
		} // end try
		return retList;
	}
	
	public void insertJaccardRecord(String name, List<StockBeanKNN> jaccardList)
	{
		String sql = " insert into jaccardIndex (name, tdate, jaccard) "
				+    " values (?, ?, ?) ";
		if (conn==null)
		{
			conn = this.getConnection();
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (StockBeanKNN s: jaccardList) 
			{
				ps.setString(1, s.getSelf().getStockName());
				ps.setDate(2, new java.sql.Date(s.getSelf().getTdate().getTime()));
				ps.setDouble(3, s.getJaccardIndex());
				ps.addBatch();
			}
			ps.executeBatch();
		}catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		
	}

	public void insertRecord(StockRecord record)
	{
		// TODO Auto-generated method stub
		
	}

	public void insertRecords(String stockName, List<StockRecord> records)
	{
		/*
		'id','int(11)','NO','PRI',NULL,'auto_increment'
		'stockName','varchar(45)','NO','',NULL,''
		'tdate','date','YES','',NULL,''
		'open','decimal(10,3)','YES','',NULL,''
		'high','decimal(10,3)','YES','',NULL,''
		'low','decimal(10,3)','YES','',NULL,''
		'close','decimal(10,3)','YES','',NULL,''
		'volume','bigint(20)','YES','',NULL,''
		*/
		
		String sql = " insert into stock (stockName, tdate, open,high,low,close,volume) "
				+    " values (?, ?, ?, ?, ?, ?, ?) ";
		if (conn==null)
		{
			conn = this.getConnection();
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (StockRecord stock: records) 
			{
				ps.setString(1, stock.getStockName());
				ps.setDate(2, new java.sql.Date(stock.getTdate().getTime()));
				ps.setFloat(3, stock.getOpen());
				ps.setFloat(4, stock.getHigh());
				ps.setFloat(5, stock.getLow());
				ps.setFloat(6, stock.getClose());
				ps.setFloat(7, stock.getVolume());
				ps.addBatch();
			}
			
			ps.executeBatch();
		}catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}

	}
	
	public void truncateTable(String tableName)
	{
		String sql = " truncate table " + tableName;
		if (conn==null)
		{
			conn = this.getConnection();
		}
		
		Statement st = null;
		
		try
		{
			st = conn.createStatement();
			st.execute(sql);
		}catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}finally
		{
			if (st !=null)
			{
				try
				{
					st.close();
				} catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}

	
	public void insertName(List<String> nameList)
	{
		String sql = " insert into stockname (name)  values (?) ";
		if (conn==null)
		{
			conn = this.getConnection();
		}
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (String strName: nameList) 
			{
				ps.setString(1, strName);
				ps.addBatch();
			}
			
			ps.executeBatch();
		}catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("Inster finished.");
		}
	}

	

	
	
}
