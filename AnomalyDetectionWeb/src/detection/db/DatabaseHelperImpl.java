package detection.db;

import java.sql.Connection;
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
import org.springframework.stereotype.Service;

import detection.beans.DividedWindows;
import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockRecord;
import detection.utility.DateUtil;
import detection.utility.MyConstants;

@Service
public class DatabaseHelperImpl implements IDatabaseHelper
{

	protected static final Log myLog = LogFactory.getLog(DatabaseHelperImpl.class);
	
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
			sql = "SELECT id, name FROM " + MyConstants.TABLE_STOCK_NAME 
				//+ " WHERE name like 'd%' "
				+ " ORDER BY name";
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
	
	@Override
	public List<StockRecord> getRecordByName(String aName)
	{
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
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT t.tdate as myDate "
					+ "from " + MyConstants.TABLE_STOCK_RECORDS + " t "
					+ "GROUP BY t.tdate "
					+ "ORDER BY t.tdate ASC ";
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
	public void saveDivideWindow(DividedWindows dw)
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
	
}
