package detection.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import detection.beans.DividedStockWindow;
import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.beans.StockReturnRate;
import detection.beans.kim.CellsDistance;
import detection.beans.kim.PeerGroup;
import detection.beans.kim.PeerGroupBean;
import detection.beans.kim.Weight;
import detection.utility.DateUtil;
import detection.utility.MyConstants;

@Service
public class DatabaseHelperImpl implements IDatabaseHelper
{

	protected static final Log myLog = LogFactory.getLog(DatabaseHelperImpl.class);
	
	public void insertName()
	{
		String select_sql = " select distinct  stockname as name from " + MyConstants.TABLE_STOCK_RECORDS ;
		
		String sql = " insert into " + MyConstants.TABLE_STOCK_NAME +" (symbol)  values (?) ";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		Statement st = null;
		try
		{
			ps = conn.prepareStatement(sql);
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(select_sql);
			while (rs.next())
			{
				String stockName = rs.getString("name");
				ps.setString(1, stockName);
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
					st.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
			System.out.println("Inster finished.");
		}
	}
	
	public void insertName(List<String> nameList)
	{
		String sql = " insert into " + MyConstants.TABLE_STOCK_NAME +" (symbol)  values (?) ";
		Connection conn = ConnectionSingleton.getConnection();
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
		
		String sql = " insert into " + MyConstants.TABLE_STOCK_RECORDS +" (stockName, tdate, open,high,low,close,volume) "
				+    " values (?, ?, ?, ?, ?, ?, ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (StockRecord stock: records) 
			{
				ps.setString(1, stock.getStockName());
				ps.setDate(2, new java.sql.Date(stock.getTdate().getTime()));
				ps.setDouble(3, stock.getOpen());
				ps.setDouble(4, stock.getHigh());
				ps.setDouble(5, stock.getLow());
				ps.setDouble(6, stock.getClose());
				ps.setDouble(7, stock.getVolume());
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
	
	@Override
	public void insertRecords(List<StockRecord> records)
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
		
		String sql = " insert into " + MyConstants.TABLE_STOCK_RECORDS +" (stockName, tdate, open,high,low,close,volume) "
				+    " values (?, ?, ?, ?, ?, ?, ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for(int i=0;i<records.size();i++)
			{
				StockRecord stock = records.get(i);
				ps.setString(1, stock.getStockName());
				ps.setDate(2, new java.sql.Date(stock.getTdate().getTime()));
				ps.setDouble(3, stock.getOpen());
				ps.setDouble(4, stock.getHigh());
				ps.setDouble(5, stock.getLow());
				ps.setDouble(6, stock.getClose());
				ps.setDouble(7, stock.getVolume());
				ps.addBatch();
				
				if (i%1000==0)
				{
					ps.executeBatch();
					ps.clearParameters();
				}
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
	
	
	@Override
	public List<String> getStockNames()
	{
		myLog.info("get all stock names ...");
		
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT id, symbol FROM " + MyConstants.TABLE_STOCK_NAME 
				//+ " WHERE name like 'd%' "
				+ " ORDER BY symbol";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("symbol");
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
	
	@Override
	public List<StockName> getAllStocks()
	{
		myLog.info("get all stock Objects ...");
		List<StockName> returnList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = " SELECT id, symbol, realName, marketCap, sector, industryGroup"
				+ " FROM " + MyConstants.TABLE_STOCK_NAME ;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				int id = rs.getInt("id");
				String symbol = rs.getString("symbol");
				String realName = rs.getString("realName");
				double marketCap = rs.getDouble("marketCap");
				String sector = rs.getString("sector");
				String group = rs.getString("industryGroup");
				StockName sn = new StockName();
				sn.setId(id);
				sn.setSymbol(symbol);
				sn.setRealName(realName);
				sn.setMarketCap(marketCap);
				sn.setSector(sector);
				sn.setIndustryGroup(group);
				returnList.add(sn);
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
		
		return returnList;
	}
	
	@Override
	public List<StockName> getStocksBySector(String sector)
	{
		myLog.info("----->Get stock Objects by Sector..." + sector);
		
		List<StockName> returnList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = " SELECT id, symbol, realName, marketCap, sector, industryGroup"
				+ " FROM " + MyConstants.TABLE_STOCK_NAME
				+ " Where sector='" + sector +"'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				int id = rs.getInt("id");
				String symbol = rs.getString("symbol");
				String realName = rs.getString("realName");
				double marketCap = rs.getDouble("marketCap");
				//String sector = rs.getString("sector");
				String group = rs.getString("industryGroup");
				StockName sn = new StockName();
				sn.setId(id);
				sn.setSymbol(symbol);
				sn.setRealName(realName);
				sn.setMarketCap(marketCap);
				sn.setSector(sector);
				sn.setIndustryGroup(group);
				returnList.add(sn);
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
		
		return returnList;
	}
	
	@Override
	public void deleStockNameById(int id)
	{
		String sql = "DELETE FROM " + MyConstants.TABLE_STOCK_NAME
				+ " WHERE id =? ";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
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
	
	@Override
	public void deleStockNameAndRecordsBySymbol(String symbol)
	{
		String sqlName = "DELETE FROM " + MyConstants.TABLE_STOCK_NAME
				+ " WHERE symbol ='"+symbol+"'";
		String sqlRecord = "DELETE FROM " + MyConstants.TABLE_STOCK_RECORDS
				+ " WHERE stockName ='"+symbol+ "'";
		Connection conn = ConnectionSingleton.getConnection();
		Statement st = null;
		try
		{
			st = conn.createStatement();
			st.addBatch(sqlName);
			st.addBatch(sqlRecord);
			st.executeBatch();
			st.clearBatch();
		}catch(SQLException sqlExp)
		{
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}finally
		{
			if (st != null)
			{
				try
				{
					st.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void updateStockDetail(List<StockName> stocksDetailFromFile)
	{
//		private String symbol;
//		private String realName;
//		private double marketCap;//million
//		private String sector;
//		private String industryGroup;
		int i=0;
		for (StockName stockName : stocksDetailFromFile)
		{
			String sql = "UPDATE  " + MyConstants.TABLE_STOCK_NAME
					+ " SET realName=?,marketCap=?,sector=?,industryGroup=?"
					+ " WHERE symbol = ?";
			Connection conn = ConnectionSingleton.getConnection();
			
			PreparedStatement ps = null;
			try
			{
				ps = conn.prepareStatement(sql);
				ps.setString(1, stockName.getRealName());
				ps.setDouble(2, stockName.getMarketCap()/1000000);//million
				ps.setString(3, stockName.getSector());
				ps.setString(4, stockName.getIndustryGroup());
				ps.setString(5, stockName.getSymbol().toLowerCase());
				ps.executeUpdate();
				i++;
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
		System.out.println("update " + i + " companies.");
		
	}
	
	@Override
	public void trimDateDurationOfRecords(Date min, Date max) 
	{
		String sql = "Delete From  " + MyConstants.TABLE_STOCK_RECORDS
				+ " Where tdate<? or tdate >?";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setDate(1, new java.sql.Date(min.getTime()));
			ps.setDate(2, new java.sql.Date(max.getTime()));
			ps.executeUpdate();
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
	
	@Override
	public Map<String,Integer> getStockTotalDatesGroup()
	{
		//myLog.info("get all stock names ...");
		Map<String,Integer> retMap = new HashMap<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = " SELECT stockName, COUNT(stockName) as cs " 
				+ " FROM " + MyConstants.TABLE_STOCK_RECORDS  
				+ " GROUP BY stockName " 
				+ " ORDER BY cs desc " ;
			
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				String stockName = rs.getString("stockName");
				int cs = rs.getInt("cs");
				retMap.put(stockName, cs);
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
		
		return retMap;
	}
	
	@Override
	public List<StockRecord> getRecordByName(String aName)
	{
		//myLog.info("Getting records by stock name" + aName +" from " + MyConstants.TABLE_STOCK_RECORDS );
		
		List<StockRecord> retList = new ArrayList<StockRecord>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_STOCK_RECORDS 
				+ " t WHERE t.stockName = ? "
				+ " order by t.tdate asc";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("stockName");
				Date tdate = rs.getDate("tdate");
				float close = rs.getFloat("close");
				float open = rs.getFloat("open");
				
				StockRecord bean  = new StockRecord();
				bean.setStockName(stockName);
				bean.setTdate(tdate);
				bean.setClose(close);
				bean.setOpen(open);
				
				retList.add(bean);
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
		
		return retList;
	}
	
	@Override
	public List<StockRecord> getRecordsByDay(String oneDay)
	{
		List<StockRecord> retList = new ArrayList<>();
		
		Date lDate = new Date();
		try
		{
			lDate = DateUtil.convertStringToDate(oneDay);
		} catch (ParseException e1)
		{
			e1.printStackTrace();
		}
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement stmt = null;
		try
		{
			String sql = " SELECT t.* FROM " + MyConstants.TABLE_STOCK_RECORDS + " t " 
					   + " where t.tdate = '" + oneDay + "'";
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

	@Override
	public StockRecord getRecordByNameAndDate(String aName, String aDate)
	{
		StockRecord bean  = new StockRecord();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_STOCK_RECORDS 
				+" t WHERE t.stockName = ? and t.tdate = ? ";
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

	@Override
	public List<StockRecord> getRecordByNameAndDate(String aName, String sDate, String eDate)
	{
		List<StockRecord> retList = new ArrayList<StockRecord>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_STOCK_RECORDS 
				+" t WHERE t.stockName = ? and t.tdate between ? and ?";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aName);
			pstmt.setDate(2, java.sql.Date.valueOf(sDate));
			pstmt.setDate(3, java.sql.Date.valueOf(eDate));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				
				// Retrieve by column name
				//int id = rs.getInt("id");
				String stockName = rs.getString("stockName");
				Date tdate = rs.getDate("tdate");
				float close = rs.getFloat("close");
				float open = rs.getFloat("open");
				
				StockRecord bean  = new StockRecord();
				bean.setStockName(stockName);
				bean.setTdate(tdate);
				bean.setClose(close);
				bean.setOpen(open);
				
				retList.add(bean);
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
		
		return retList;
		
	}

	@Override
	public void saveBatchReturnRate(List<StockReturnRate> lstReturnRate)
	{
		String sql = " insert into " + MyConstants.TABLE_STOCK_RETURN 
				+" (symbol, tdate, returnRate) "
				+" values (?, ?, ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (StockReturnRate obj: lstReturnRate) 
			{
				ps.setString(1, obj.getSymbol());
				ps.setDate(2, new java.sql.Date(obj.getTdate().getTime()));
				ps.setDouble(3, obj.getReturnRate());
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
	
	@Override
	public List<JaccardBean> getJaccardData(String stockName)
	{
		List<JaccardBean> retList = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_JACCARD_INDEX 
				+" t WHERE t.name = ?";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				
				// Retrieve by column name
				long id = rs.getLong("id");
				Date tdate = rs.getDate("tdate");
				float jaccard = rs.getFloat("jaccard");
				
				JaccardBean bean  = new JaccardBean();
				bean.setStockName(stockName);
				bean.setTdate(tdate);
				bean.setId(id);
				bean.setJaccard(jaccard);
				
				retList.add(bean);
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
		
		return retList;
	}
	
	@Override
	public void insertJaccardRecord(String name, List<StockKNNBean> jaccardList)
	{
		String sql = " insert into " + MyConstants.TABLE_JACCARD_INDEX 
				+" (name, tdate, jaccard) "
				+" values (?, ?, ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (StockKNNBean s: jaccardList) 
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

	@Override
	public List<String> getTransactionDates()
	{
		myLog.info("Geting all transaction dates from table " + MyConstants.TABLE_STOCK_RECORDS + "...");
		
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT distinct t.tdate as myDate "
					+ "FROM " + MyConstants.TABLE_STOCK_RECORDS + " t "
					+ "ORDER BY myDate ASC ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
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

	@Override
	public List<Date> getTradingDates(String stockName)
	{
		myLog.info("----->Get Trading Dates List by stockName..." + stockName); 
		
		List<Date> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT t.tdate as myDate "
					+ " FROM " + MyConstants.TABLE_STOCK_RECORDS + " t "
					+ " WHERE t.stockName='"+stockName+"'"
					+ " GROUP BY t.tdate "
					+ " ORDER BY t.tdate ASC ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				Date myDate = rs.getDate("myDate");
				retList.add(myDate);
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			se.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
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
	
	@Override
	public void truncateTable(String tableName)
	{
		myLog.info("Truncating table " + tableName);
		
		String sql = " truncate table " + tableName;
		Connection conn = ConnectionSingleton.getConnection();
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
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean isStockForAvailableInAllDates(List<String> transactonDates,List<StockRecord> records)
	{
		String tempDate="";
		String tradeDate = "";
		StockRecord record = null;
		for(int i=0;i<transactonDates.size();i++)
		{
			tempDate = transactonDates.get(i);
			record   = records.get(i);
			tradeDate = DateUtil.convertDateToString(record.getTdate());
			
			if (tempDate.equals(tradeDate))
			{
				continue;
			}else
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public List<String> filterAllAvailableStocks(List<String> stockNames)
	{
		List<String> retList = new ArrayList<>();
		List<String> dateList =  getTransactionDates();
		
		for (String s : stockNames)
		{
			List<StockRecord> lstRecords = getRecordByName(s);
			if (isStockForAvailableInAllDates(dateList, lstRecords))
			{
				retList.add(s);
			}
		}
		
		return retList;
	}

	@Override
	public void saveDivideWindow(DividedStockWindow dw)
	{
		String sql = "INSERT INTO " + MyConstants.TABLE_DIVIDEWINDOWS
				+ " (stock_name,window_no,start_date,end_date,avg_price) "
				+ "values (?,?,?,?,?) ";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setString(1, dw.getStock_name());
			ps.setInt(2, dw.getWindow_no());
			ps.setDate(3, new java.sql.Date(dw.getStart_date().getTime()));
			ps.setDate(4, new java.sql.Date(dw.getEnd_date().getTime()));
			ps.setDouble(5, dw.getAvg_price());
			ps.executeUpdate();
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

	
	@Override
	public List<DividedStockWindow> getDividedWindowsByStockname(String stockName)
	{
		List<DividedStockWindow> retList = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_DIVIDEWINDOWS
				+ " t WHERE t.stock_name = ? "
				+ " order by t.window_no asc";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				// Retrieve by column name
				int dw_id = rs.getInt("dw_id");
				int window_no = rs.getInt("window_no");
				Date sdate = rs.getDate("start_date");
				Date edate = rs.getDate("end_date");
				double avg_price = rs.getDouble("avg_price");
				
				DividedStockWindow bean  = new DividedStockWindow();
				bean.setDw_id(dw_id);
				bean.setWindow_no(window_no);
				bean.setStock_name(stockName);
				bean.setStart_date(sdate);
				bean.setEnd_date(edate);
				bean.setAvg_price(avg_price);
				retList.add(bean);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException se)
		{
			se.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2)
			{
			} 
		} // end try
		
		return retList;
	}
	
	@Override
	public DividedStockWindow getDividedWindowByNameAndWindNo(String stockName, int windowNo)
	{
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " + MyConstants.TABLE_DIVIDEWINDOWS
				+ " t WHERE t.stock_name = ? AND t.window_no = ?";
				
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockName);
			pstmt.setInt(2, windowNo);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				// Retrieve by column name
				int dw_id = rs.getInt("dw_id");
				int window_no = rs.getInt("window_no");
				Date sdate = rs.getDate("start_date");
				Date edate = rs.getDate("end_date");
				double avg_price = rs.getDouble("avg_price");
				
				DividedStockWindow bean  = new DividedStockWindow();
				bean.setDw_id(dw_id);
				bean.setWindow_no(window_no);
				bean.setStock_name(stockName);
				bean.setStart_date(sdate);
				bean.setEnd_date(edate);
				bean.setAvg_price(avg_price);
				return bean;
			}
			rs.close();
			pstmt.close();
		} catch (SQLException se)
		{
			se.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se2)
			{
			} 
		} // end try
		
		return null;
	}
	

	@Override
	public List<String> getStockNamesFromDivideWindows()
	{
		myLog.info("get all stock names from "+ MyConstants.TABLE_DIVIDEWINDOWS + "...");
		
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = " SELECT distinct stock_name FROM " + MyConstants.TABLE_DIVIDEWINDOWS
				+ " ORDER BY stock_name";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				String stockName = rs.getString("stock_name");
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

	@Override
	public List<Integer> getDividedWindows()
	{
		myLog.info("Getting all Divided Windows No....");
		
		List<Integer> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT t.window_no as windowNo "
					+ "from " + MyConstants.TABLE_DIVIDEWINDOWS + " t "
					+ "GROUP BY t.window_no "
					+ "ORDER BY t.window_no ASC ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				//Retrieve by column name
				int wIdx = rs.getInt("windowNo");
				retList.add(wIdx);
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

	
	@Override
	public void savePeerGroups(PeerGroup value)
	{
		String sql = " insert into " + MyConstants.TABLE_PEER_GROUP
				+" (stock_name, peer_members, window_no, start_date, end_date, mean_value, euclDistance_to_members) "
				+" values (?, ?, ?, ?, ?, ?, ?) ";

		DividedStockWindow self = value.getSelf();
		List<DividedStockWindow> peers = value.getPeers();
		List<Double> peers_distance = value.getEucl_distance_peers();
		String str_peers ="";
		String str_peers_distance ="";
		//String str_proximity="";
		double meanValue = value.getGroupMeanValue();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setString(1, self.getStock_name());
			//
			for(int i=0;i<peers.size();i++)
			{
				DividedStockWindow p = peers.get(i);
				str_peers += p.getStock_name() + ",";
				//
				double eucl_distance = peers_distance.get(i).doubleValue();
				eucl_distance = Precision.round(eucl_distance,4);
				str_peers_distance += String.valueOf(eucl_distance)+",";
				//
				Exp exp = new Exp();
				double proxi = exp.value((-1 * MyConstants.gamma) *	eucl_distance);
				this.insertProxi(self.getStock_name(), p.getStock_name(), eucl_distance, proxi);
			}
			str_peers = str_peers.substring(0, str_peers.length() - 1);
			ps.setString(2,str_peers);
			//
			ps.setInt(3, self.getWindow_no());
			ps.setDate(4, new java.sql.Date(self.getStart_date().getTime()));
			ps.setDate(5, new java.sql.Date(self.getEnd_date().getTime()));
			ps.setDouble(6, Precision.round(meanValue,3));
			//
			str_peers_distance = str_peers_distance.substring(0, str_peers_distance.length() - 1);
			ps.setString(7,str_peers_distance);
			//
			ps.executeUpdate();
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

	@Override
	public List<String> getPeerNamesForGroup(String stockName)
	{
		myLog.info("Getting peer members for stock " + stockName + " ... ");
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			String strPeers="";
			sql = "SELECT distinct t.peer_members as peers "
					+ " FROM " + MyConstants.TABLE_PEER_GROUP + " t "
					+ " WHERE t.stock_name = '"+stockName+"'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				//Retrieve by column name
				strPeers = rs.getString("peers");
			}
			rs.close();
			stmt.close();
			
			String[] arrPeers = strPeers.split(",");
			for(int i=0;i<arrPeers.length;i++)
			{
				retList.add(arrPeers[i]);
			}
			
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

	/////
	@Override
	public List<PeerGroupBean> getPeergroupsForGroup(String stockName)
	{
		List<PeerGroupBean> retList = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = "SELECT t.* FROM " 
				+ MyConstants.TABLE_PEER_GROUP  + " t "
				+ " WHERE t.stock_name = ? "
				+ " ORDER BY t.window_no asc";
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				int rid = rs.getInt("rid");
				String peer_members = rs.getString("peer_members");
				int window_no = rs.getInt("window_no");
				Date start_date = rs.getDate("start_date");
				Date end_date = rs.getDate("end_date");
				double mean_value = rs.getDouble("mean_value");
				double mean_value_with_weights = rs.getDouble("mean_value_with_weights");
				
				PeerGroupBean bean  = new PeerGroupBean();
				bean.setRid(rid);
				bean.setPeer_members(peer_members);
				bean.setWindow_no(window_no);
				bean.setStart_date(start_date);
				bean.setEnd_date(end_date);
				bean.setMean_value(mean_value);
				bean.setMean_value_with_weights(mean_value_with_weights);
				
				retList.add(bean);
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
		
		return retList;
	}

	@Override
	public List<PeerGroupBean> getAllPeergroups()
	{
		List<PeerGroupBean> retList = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		Statement stmt = null;
		String sql = "SELECT t.* FROM " 
				+ MyConstants.TABLE_PEER_GROUP  + " t "
				+ " ORDER BY t.stock_name, t.window_no ";
		try
		{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				int rid = rs.getInt("rid");
				String stockName = rs.getString("stock_name");
				String peer_members = rs.getString("peer_members");
				int window_no = rs.getInt("window_no");
				Date start_date = rs.getDate("start_date");
				Date end_date = rs.getDate("end_date");
				double mean_value = rs.getDouble("mean_value");
				String euclDistance_to_members = rs.getString("euclDistance_to_members");
				double mean_value_with_weights = rs.getDouble("mean_value_with_weights");
				
				PeerGroupBean bean  = new PeerGroupBean();
				bean.setRid(rid);
				bean.setStock_name(stockName);
				bean.setPeer_members(peer_members);
				bean.setWindow_no(window_no);
				bean.setStart_date(start_date);
				bean.setEnd_date(end_date);
				bean.setMean_value(mean_value);
				bean.setEuclDistance_to_members(euclDistance_to_members);
				bean.setMean_value_with_weights(mean_value_with_weights);
				///
				retList.add(bean);
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
		
		return retList;
	}

	
	@Override
	public void updatePeerGroupNewMeanValue(int recId, double newMean)
	{
		String sql = "UPDATE  " + MyConstants.TABLE_PEER_GROUP
				+ " SET mean_value_with_weights = ? "
				+ " WHERE rid = ?";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, newMean);
			ps.setInt(2, recId);
			ps.executeUpdate();
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
	
	@Override
	public void insertProxi(String stockName, String peerName,double eduDistance, double proxi)
	{
		String sql = "INSERT INTO  " + MyConstants.TABLE_WEIGHTS
				+ " (stock_name,peer_name,eduDistance,proxi,weight) "
				+" values (?, ?, ?,?, ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setString(1, stockName);
			ps.setString(2, peerName);
			ps.setDouble(3, eduDistance);
			ps.setDouble(4, proxi);
			ps.setDouble(5, 0);
			ps.executeUpdate();
		}catch(SQLException sqlExp)
		{
			;
		}catch(Exception exp)
		{
			;
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
	
	@Override
	public List<Weight> getWeights(String stockName)
	{
		//myLog.info("get peer weight of one stock:"+stockName);
		
		List<Weight> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			
			int id;
			String stock_name;
			String peer_name;
			float eduDistance;
			float proxi;
			float weight;
			
			sql = " SELECT id, stock_name,peer_name,eduDistance, proxi, weight "
				+ " FROM " + MyConstants.TABLE_WEIGHTS 
				+ " WHERE stock_name='"+stockName+"'";
				
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				id = rs.getInt("id");
				stock_name = rs.getString("stock_name");
				peer_name = rs.getString("peer_name");
				eduDistance = rs.getFloat("eduDistance");
				proxi = rs.getFloat("proxi");
				weight = rs.getFloat("weight");
				Weight w = new Weight();
				w.setId(id);
				w.setStock_name(stock_name);
				w.setPeer_name(peer_name);
				w.setEduDistance(eduDistance);
				w.setProxi(proxi);
				w.setWeight(weight);
				//
				retList.add(w);
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

	@Override
	public Weight getWeights(String stockName, String peerName)
	{
		//myLog.info("get peer weight of one stock with a peer: "+stockName + " peer: "+ peerName);
		Weight retValue = new Weight();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			
			int id;
			String stock_name;
			String peer_name;
			float eduDistance;
			float proxi;
			float weight;
			
			sql = " SELECT id, stock_name,peer_name,eduDistance, proxi, weight "
				+ " FROM " + MyConstants.TABLE_WEIGHTS 
				+ " WHERE stock_name='"+stockName+"'"
				+ " AND peer_name='"+peerName+"'";
				
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				id = rs.getInt("id");
				stock_name = rs.getString("stock_name");
				peer_name = rs.getString("peer_name");
				eduDistance = rs.getFloat("eduDistance");
				proxi = rs.getFloat("proxi");
				weight = rs.getFloat("weight");
				
				retValue.setId(id);
				retValue.setStock_name(stock_name);
				retValue.setPeer_name(peer_name);
				retValue.setEduDistance(eduDistance);
				retValue.setProxi(proxi);
				retValue.setWeight(weight);
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			se.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2)
			{
			}
		} // end try
		
		return retValue;
	}
	
	@Override
	public List<String> getDistinctStockNameFromWeights()
	{
		myLog.info("get all Distinct stock names from weights ...");
		
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT Distinct  stock_name FROM " + MyConstants.TABLE_WEIGHTS 
				+ " ORDER BY stock_name";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String stockName = rs.getString("stock_name");
				retList.add(stockName);
			}
			rs.close();
			stmt.close();
		} catch (SQLException se)
		{
			se.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
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

	@Override
	public void updateWeights(int recId, float w)
	{
		String sql = "UPDATE  " + MyConstants.TABLE_WEIGHTS
				+ " SET weight= ? "
				+ " WHERE id = ?";
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setFloat(1, w);
			ps.setInt(2, recId);
			ps.executeUpdate();
		}catch(SQLException sqlExp)
		{
			System.out.println(sqlExp.getMessage());
			sqlExp.printStackTrace();
		}catch(Exception exp)
		{
			System.out.println(exp.getMessage());
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
	
	@Override
	public void insertCellsDistance(List<CellsDistance> saveList)
	{
		String sql = " insert into " + MyConstants.TABLE_CELLS_DISTANCE
				+" (stock_name, window_no, peer_name, peer_avg, peer_mean, difference) "
				+" values "
				+ "(?, ?, ?, ?, ? , ?) ";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
	
			for (CellsDistance s: saveList) 
			{
				ps.setString(1, s.getStock_name());
				ps.setInt(2, s.getWindow_no());
				ps.setString(3, s.getPeer_name());
				ps.setDouble(4, s.getPeer_avg());
				ps.setDouble(5, s.getPeer_mean());
				ps.setDouble(6, s.getDifference());
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
	
	@Override
	public List<StockReturnRate> getReturnRateByDateSector(StockReturnRate self, Date tempDate, String sector)
	{
		List<StockReturnRate> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		String sql;
		sql = " SELECT t1.rid as rid, t1.symbol as symbol, t1.returnRate as returnRate, t1.tdate as tdate"
			+ " FROM " + MyConstants.TABLE_STOCK_RETURN + " t1," + MyConstants.TABLE_STOCK_NAME +" t2"
			+ " WHERE t1.symbol = t2.symbol "
			+ " AND t2.sector='"+sector+"'"
			+ " AND t1.tdate='"+ DateUtil.getDate(tempDate)+"'";
		try
		{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				// Retrieve by column name
				int rid = rs.getInt("rid");
				String symbol = rs.getString("symbol");
				float returnRate  = rs.getFloat("returnRate");
				Date tdate = rs.getDate("tdate");
				StockReturnRate obj = new StockReturnRate();
				obj.setRid(rid);
				obj.setSymbol(symbol);
				obj.setTdate(tempDate);
				obj.setReturnRate(returnRate);
				if (self.getSymbol().equals(obj.getSymbol()))
				{
					self.setRid(obj.getRid());
					self.setSymbol(obj.getSymbol());
					self.setTdate(obj.getTdate());
					self.setReturnRate(obj.getReturnRate());
				}
				retList.add(obj);
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
}
