package detection.algorithms.baseLine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import detection.beans.DividedStockWindow;
import detection.beans.SortValueBean;
import detection.beans.StockRecord;
import detection.db.ConnectionSingleton;
import detection.utility.DateUtil;
import detection.utility.MyConstants;
import detection.utility.UtilsZ;

public class BaseLineDBHelper implements IBaseLineDBHelper
{
	protected static final Log myLog = LogFactory.getLog(BaseLineDBHelper.class);
	
	public BaseLineDBHelper()
	{
		// TODO Auto-generated constructor stub
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
	public void updateStockNameFromStockRecords()
	{
		truncateTable(MyConstants.TABLE_STOCK_NAME);
		
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
		}
	}

	@Override
	public List<String> getStockNames()
	{
		//myLog.info("Geting all stock name from table " + MyConstants.TABLE_STOCK_NAME + "...");
		List<String> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT symbol  FROM " + MyConstants.TABLE_STOCK_NAME ;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				String name = rs.getString("symbol");
				retList.add(name);
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
	public StockRecord getStockByNameDate(String stockName, Date tDate)
	{
		String sql;
		sql = "SELECT t.id, t.stockName, t.tdate, t.close "
			+ "FROM " + MyConstants.TABLE_STOCK_RECORDS + " t "
			+ "WHERE t.stockName = ? and t.tdate = ?";
		
		StockRecord retObject = new StockRecord();
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement pstmt = null;
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockName);
			pstmt.setDate(2, new java.sql.Date(tDate.getTime()));
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				retObject.setStockName(rs.getString("stockName"));
				retObject.setTdate(rs.getDate("tdate"));
				retObject.setClose(rs.getDouble("close"));
			}
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
		} 
		
		return retObject;
	}

	/**
	 * process divide original data into N time windows
	 * and each time window store the average price of this short period
	 */
	@Override
	public void divideStockData(List<String> stockNames,List<String> transactonDates)
	{
		truncateTable(MyConstants.TABLE_DIVIDEWINDOWS);
		
		//List<String> stockNames = getStockNames();
		
		//List<String> transactonDates =  getTransactionDates();
		myLog.info(">The system is dividing Stock date to N time windows....");
		for (String stName : stockNames)
		{
			List<StockRecord> records = getRecordByName(stName);
			
			/////////////////////////////
			int idx = 1;
			double tempSum = 0;
			double tempAvg = 0;
			int window_no = 1;
			boolean flag = false;
			
			Date startDate = new Date();
			StockRecord record = null;
			for (int i=0;i<records.size();i++)
			{
				record = records.get(i);
				idx = i + 1;
				tempSum = tempSum + record.getClose();
				//if it is the first record then mark the start date
				if (idx == 1)
				{
					startDate = record.getTdate();
				}else
				{
					if (flag == true)//if this is a new divided window,record the start Date
					{
						startDate = record.getTdate();
						flag = false;
					}
				}
				
				if (idx%MyConstants.dividedFactor==0)
				{
					tempAvg = tempSum/MyConstants.dividedFactor;
					
					DividedStockWindow dw = new DividedStockWindow();
					dw.setStock_name(stName);
					dw.setWindow_no(window_no);
					dw.setStart_date(new java.sql.Date(startDate.getTime()));
					dw.setEnd_date(record.getTdate());
					dw.setAvg_price(tempAvg);
					
					saveDivideWindow(dw);
					
					window_no = window_no + 1;
					tempSum = 0;
					flag = true;
					
				}
			}
			
			if (flag == false)
			{
				tempAvg = tempSum/MyConstants.dividedFactor;
				DividedStockWindow dw = new DividedStockWindow();
				dw.setStock_name(stName);
				dw.setWindow_no(window_no);
				dw.setStart_date(new java.sql.Date(startDate.getTime()));
				dw.setEnd_date(record.getTdate());
				dw.setAvg_price(tempAvg);
				saveDivideWindow(dw);
				
			}
			
		}
		
		myLog.info(">Dividing stock records has been finished.");
	}

	
	@Override
	public void tranformToDivideWindow(List<String> stockNames,
			List<String> transactonDates)
	{
		myLog.info(">>>Transform data into Divided Data table....");
		truncateTable(MyConstants.TABLE_DIVIDEWINDOWS);
		
		String sql = "INSERT INTO " + MyConstants.TABLE_DIVIDEWINDOWS
				+ " (stock_name,window_no,start_date,end_date,avg_price) "
				+ "values (?,?,?,?,?) ";
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement ps = null;
		
		try
		{
			for (String stName : stockNames)
			{
				List<StockRecord> records = getRecordByName(stName);
				int window_no = 1;
				StockRecord record = null;
				ps = conn.prepareStatement(sql);
				for (int i=0;i<records.size();i++)
				{
					record = records.get(i);
					ps.setString(1, stName);
					ps.setInt(2, window_no);
					ps.setDate(3, new java.sql.Date(record.getTdate().getTime()));
					ps.setDate(4, new java.sql.Date(record.getTdate().getTime()));
					ps.setDouble(5, record.getClose());
					ps.addBatch();
					window_no = window_no + 1;
				}
				ps.executeBatch();
			}
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
		
		myLog.info(">Dividing stock records has been finished.");
		
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
	public HashMap<String, List<SortValueBean>> createPeerGroup(List<String> stockNames)
	{
		myLog.info(">Creating Peer Groups for all stocks...");
		
		HashMap<String,List<SortValueBean>> retMap = new HashMap<>();
		
		//get dividend stocks for every stockname
		HashMap<String,List<DividedStockWindow>> hmWindows = new HashMap<>();
		for (String s : stockNames)
		{
			List<DividedStockWindow> wRecords = getDividedWindowsByStockname(s);
			hmWindows.put(s, wRecords);
		}
		
		///
		for (String selfKey : hmWindows.keySet()) 
		{
		    List<DividedStockWindow> selfList = hmWindows.get(selfKey);
		    ///
		    List<SortValueBean> distanceListAll = new ArrayList<>();
		    List<SortValueBean> distanceListK = new ArrayList<>();
		    
		    for (String otherKey : hmWindows.keySet()) 
			{
			    if (selfKey.equals(otherKey))
			    {
			    	continue;
			    }
			    List<DividedStockWindow> otherList = hmWindows.get(otherKey);
			    //
			    double edValue = calculateEuclideanDistance(selfList, otherList);
			    //
			    SortValueBean sortBean = new SortValueBean();
			    sortBean.setName(otherKey);
			    sortBean.setEcl_distance(edValue);
			    distanceListAll.add(sortBean);
			}
		    
		    //sort beans based on eucli distance
		    Collections.sort(distanceListAll, new SortComparator()); 
		    
		    //get the top k (in this situation k is all stock names except itself)
		    int topK = stockNames.size();
		    for(int i=0;i<(topK-1);i++)
		    {
		    	distanceListK.add( distanceListAll.get(i));
		    }
		    
		    retMap.put(selfKey, distanceListK);
		}
		
		return retMap;
	}
	
	@Override
	public void savePeerGroup(HashMap<String, List<SortValueBean>> pgMap)
	{
		truncateTable(MyConstants.TABLE_PEER_GROUPS);
		
		for(String key:pgMap.keySet())
		{
			List<SortValueBean> peerList = pgMap.get(key);
			for(SortValueBean peer : peerList)
			{
				insertOnePeerGroup(key, peer);
			}
		}
		
	}
	
	@Override
	public List<PeerGroupBean> getPeerGroups()
	{
		List<PeerGroupBean> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		String sql_select ="" ;
		try
		{
			stmt = conn.createStatement();
			sql_select = "SELECT rId,stock_name,peer_name, distance,prox, weight FROM " + MyConstants.TABLE_PEER_GROUPS ;
			ResultSet rs = stmt.executeQuery(sql_select);
			while (rs.next())
			{
				int 	rId 		= rs.getInt("rId");
				String  stock_name  = rs.getString("stock_name");
				String  peer_name  	= rs.getString("peer_name");
				double	distance 	= rs.getDouble("distance");
				double 	prox		=rs.getDouble("prox");
				double 	weight		=rs.getDouble("weight");
				PeerGroupBean pgBean = new PeerGroupBean();
				pgBean.setrId(rId);
				pgBean.setStock_name(stock_name);
				pgBean.setPeer_name(peer_name);
				pgBean.setDistance(distance);
				pgBean.setProx(prox);
				pgBean.setWeight(weight);
				
				retList.add(pgBean);
			}
			rs.close();
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
				{
					stmt.close();
				}
			} catch (SQLException se2)
			{
			}
		}
		return retList;
	}
	
	@Override
	public List<PeerGroupBean> getPeerGroupsByStockName(String stockName)
	{
		List<PeerGroupBean> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		String sql_select ="" ;
		try
		{
			stmt = conn.createStatement();
			sql_select = "SELECT rId, stock_name, peer_name, distance, prox, weight "
					+ " FROM " + MyConstants.TABLE_PEER_GROUPS 
					+ " WHERE stock_name= '"+stockName+"'";
			
			ResultSet rs = stmt.executeQuery(sql_select);
			while (rs.next())
			{
				int    rId 		= rs.getInt("rId");
				String stock_name  = rs.getString("stock_name");
				String peer_name  	= rs.getString("peer_name");
				double distance 	= rs.getDouble("distance");
				double prox			=rs.getDouble("prox");
				double weight		=rs.getDouble("weight");
				PeerGroupBean pgBean = new PeerGroupBean();
				pgBean.setrId(rId);
				pgBean.setStock_name(stock_name);
				pgBean.setPeer_name(peer_name);
				pgBean.setDistance(distance);
				pgBean.setProx(prox);
				pgBean.setWeight(weight);
				
				retList.add(pgBean);
			}
			rs.close();
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
				{
					stmt.close();
				}
			} catch (SQLException se2)
			{
			}
		}
		return retList;
	}

	@Override
	public void updateProx()
	{
		Exp exp = new Exp();
		myLog.info("Updating Proximity for " + MyConstants.TABLE_PEER_GROUPS + "...");
		//
		List<PeerGroupBean> pgList = getPeerGroups();
		//
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement pstmt = null;
		String sql_update = "UPDATE " + MyConstants.TABLE_PEER_GROUPS + " set prox= ? where rId = ?";
		try
		{
			for(PeerGroupBean pgBean: pgList)
			{
				pstmt = conn.prepareStatement(sql_update);
				int iId = pgBean.getrId();
				//divide 100 for distance because it is too big
				double distance 	= pgBean.getDistance()/100;
				//
				double proxi = exp.value((-1 * MyConstants.gamma) *	distance);
				double proxi_round = UtilsZ.round(proxi, 8, BigDecimal.ROUND_HALF_UP);
				pstmt.setDouble(1, proxi_round);
				pstmt.setInt(2, iId);
				pstmt.executeUpdate();
			}
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
				{
					pstmt.close();
				}
			} catch (SQLException se2)
			{
			}
		} 
		
	}
	
	@Override
	public void updateWeight(List<String> stockNameList)
	{
		myLog.info("Updating Weight for " + MyConstants.TABLE_PEER_GROUPS + "...");
		//
		for(String stockName: stockNameList)
		{
			List<PeerGroupBean> pgList = getPeerGroupsByStockName(stockName);
			//
			double sum_prox = 0d;
			for(PeerGroupBean pgBean : pgList)
			{
				sum_prox = sum_prox + pgBean.getProx();
			}
			//
			for(int i=0;i<pgList.size();i++)
			{
				PeerGroupBean pgObj = pgList.get(i);
				double weight = pgObj.getProx()/sum_prox;
				double weight_round = UtilsZ.round(weight, 8, BigDecimal.ROUND_HALF_UP);
				pgObj.setWeight(weight_round);
				updatePeerGroup(pgObj.getrId(), pgObj);
			}
		}
	}
	
	@Override
	public void updatePeerGroupWeightMean(List<String> stockNameList,List<String> transactonDates)
	{
		truncateTable(MyConstants.TABLE_STOCK_RECORDS_PEER_GROUP);
		
		double self_price = 0;
		//
		Date   tDate  = new Date();
		try
		{
			for(String stockName: stockNameList)
			{
				myLog.info(">>>Updating peer group records with stock:" + stockName +"...");
				for(int j=0;j<transactonDates.size();j++)
				{
					String strDate = transactonDates.get(j);
					tDate = DateUtil.convertStringToDate(strDate);
					self_price = getStockByNameDate(stockName, tDate).getClose();
					
					//
					double sum_peer_weight_mean = 0d;
					List<PeerGroupBean> pgList = getPeerGroupsByStockName(stockName);
					for(PeerGroupBean pgObject : pgList)
					{
						String peer_name = pgObject.getPeer_name();
						double peer_wegt = pgObject.getWeight();
						StockRecord sr = getStockByNameDate(peer_name, tDate);
						double peer_price = sr.getClose();
						sum_peer_weight_mean = sum_peer_weight_mean + (peer_price * peer_wegt);
					}
					
					//save object
					PeerGroupRecordBean pgrBean = new PeerGroupRecordBean();
					pgrBean.setTdate(tDate);
					pgrBean.setSelf_name(stockName);
					pgrBean.setSelf_price(self_price);
					pgrBean.setWeight_mean(sum_peer_weight_mean );
					insertPeerRecords(pgrBean);
				}
			}
				
		}catch(Exception exp)
		{
			exp.printStackTrace();
		}
		
	}
	
	@Override
	public void calculateExtremes(List<String> stockNameList)
	{
		List<PeerGroupRecordBean> tempList = new ArrayList<>();
		List<PeerGroupRecordBean> extremList = new ArrayList<>();
		for(String stockName:stockNameList)
		{
			myLog.info(">>>Extracting extrem values of "+stockName);
			tempList 	= new ArrayList<>();
			extremList 	= new ArrayList<>();
			tempList 	= getPeerGroupRecordsByName(stockName);
			extremList 	= extractExtremRecords(tempList);
			if (extremList!=null & extremList.size()>0)
			{
				updatePeerGroupRecords(extremList);
			}
		}
		
	}
	

	private void updatePeerGroupRecords(List<PeerGroupRecordBean> extremList)
	{
		String sql = " UPDATE `anomalyresearch`.`kim_peergroups_records` " 
		+ "	SET "
		+ "	`std` = ?,"
		+ "	`std_rank` = ?"
		+ " WHERE `rId` = ?";
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			for(PeerGroupRecordBean bb:extremList)
			{
				ps.setDouble(1, bb.getStd());
				ps.setDouble(2, bb.getStd_rank());
				ps.setInt(3, bb.getrId());
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

	private List<PeerGroupRecordBean> extractExtremRecords(
			List<PeerGroupRecordBean> tempList)
	{
		List<PeerGroupRecordBean> retList = new ArrayList<>();
		//
		double[] v = new double[tempList.size()];
		for(int i=0;i<tempList.size();i++)
		{
			v[i] = tempList.get(i).getDiffer();
		}
		double std = new StandardDeviation().evaluate(v);
		
		double m   = new Mean().evaluate(v);
		for(int i=0;i<tempList.size();i++)
		{
			PeerGroupRecordBean tempBean = tempList.get(i);
			double tempDiff = tempBean.getDiffer();
			tempBean.setStd_rank(Math.abs((tempDiff - m))/std);
			tempBean.setStd(std);
			retList.add(tempBean);
		}
		
		return retList;
		
	}

	@Override
	public List<PeerGroupRecordBean> getTopPeerGroupRecords()
	{
		List<PeerGroupRecordBean> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		String sql="SELECT `kim_peergroups_records`.`rId`,"
	    + " `kim_peergroups_records`.`tdate`, "
	    + " `kim_peergroups_records`.`self_name`, "
	    + " `kim_peergroups_records`.`self_price`, "
	    + " `kim_peergroups_records`.`weight_mean`, "
	    + " `kim_peergroups_records`.`diff`, "
	    + " `kim_peergroups_records`.`std`, "
	    + " `kim_peergroups_records`.`std_rank` "
	    + " FROM `anomalyresearch`.`kim_peergroups_records` "
	    + " order by `kim_peergroups_records`.`std_rank` desc "
	    + " limit 100 ";
		
		try
		{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				int rId = rs.getInt("rId");
				Date tDate = rs.getDate("tdate");
				String stockName = rs.getString("self_name");
				double self_price = rs.getDouble("self_price");
				double weight_mean = rs.getDouble("weight_mean");
				double differ = rs.getDouble("diff");
				double std = rs.getDouble("std");
				double std_rank = rs.getDouble("std_rank");
				
				PeerGroupRecordBean bb = new PeerGroupRecordBean();
				bb.setrId(rId);
				bb.setTdate(tDate);
				bb.setSelf_name(stockName);
				bb.setSelf_price(self_price);
				bb.setWeight_mean(weight_mean);
				bb.setDiffer(differ);
				bb.setStd(std);
				bb.setStd_rank(std_rank);
				
				retList.add(bb);
				
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
	
	private List<PeerGroupRecordBean> getPeerGroupRecordsByName(String stockName)
	{
		List<PeerGroupRecordBean> retList = new ArrayList<>();
		Connection conn = ConnectionSingleton.getConnection();
		Statement stmt = null;
		String sql="SELECT `kim_peergroups_records`.`rId`,"
	    + "`kim_peergroups_records`.`tdate`, "
	    + "`kim_peergroups_records`.`self_name`, "
	    + "`kim_peergroups_records`.`self_price`, "
	    + "`kim_peergroups_records`.`weight_mean`, "
	    + "`kim_peergroups_records`.`diff` "
	    + "FROM `anomalyresearch`.`kim_peergroups_records` "
	    + "WHERE `kim_peergroups_records`.`self_name`='"+stockName+"'";
		
		try
		{
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
			{
				int rId = rs.getInt("rId");
				Date tDate = rs.getDate("tdate");
				double self_price = rs.getDouble("self_price");
				double weight_mean = rs.getDouble("weight_mean");
				double differ = rs.getDouble("diff");
				PeerGroupRecordBean bb = new PeerGroupRecordBean();
				bb.setrId(rId);
				bb.setTdate(tDate);
				bb.setSelf_name(stockName);
				bb.setSelf_price(self_price);
				bb.setWeight_mean(weight_mean);
				bb.setDiffer(differ);
				retList.add(bb);
				
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
	
	private void insertPeerRecords(PeerGroupRecordBean pgrBean)
	{

		String sql = " insert into " + MyConstants.TABLE_STOCK_RECORDS_PEER_GROUP
				+" (tdate, self_name, self_price, weight_mean, diff, std, std_rank) "
				+" values "
				+ "(?, ?, ?, ?, ?, ?, ?) ";

		Connection conn = ConnectionSingleton.getConnection();
			
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setDate(1, new java.sql.Date(pgrBean.getTdate().getTime()));
			ps.setString(2, pgrBean.getSelf_name());
			ps.setDouble(3, pgrBean.getSelf_price());
			ps.setDouble(4, pgrBean.getWeight_mean());
			ps.setDouble(5, Math.abs(pgrBean.getSelf_price() - pgrBean.getWeight_mean()));
			ps.setDouble(6, pgrBean.getStd());
			ps.setDouble(7, pgrBean.getStd_rank());
			
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
	
	
	private void updatePeerGroup(int rId,PeerGroupBean pgBean)
	{
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement pstmt = null;
		String sql_update = "UPDATE " + MyConstants.TABLE_PEER_GROUPS 
				+ " SET prox= ? , weight= ?"
				+ " where rId = ?";
		try
		{
			pstmt = conn.prepareStatement(sql_update);
			pstmt.setDouble(1, pgBean.getProx());
			pstmt.setDouble(2, pgBean.getWeight());
			pstmt.setInt(3, rId);
			pstmt.executeUpdate();
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
				{
					pstmt.close();
				}
			} catch (SQLException se2)
			{
			}
		} 
		
	}
	private void insertOnePeerGroup(String selfName,SortValueBean peer)
	{
		
		String sql = " insert into " + MyConstants.TABLE_PEER_GROUPS
				+" (stock_name, peer_name, distance) "
				+" values (?, ?, ?) ";

		Connection conn = ConnectionSingleton.getConnection();
			
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(sql);
			ps.setString(1, selfName);
			ps.setString(2, peer.getName());
			ps.setDouble(3, peer.getEcl_distance());
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

	
	private double calculateEuclideanDistance(List<DividedStockWindow> y1, List<DividedStockWindow> y2)
	{
		double returnValue=0d;
		int myLength = y2.size();
		double[] d1 = new double[myLength]; 
		double[] d2 = new double[myLength]; 
		for(int i=0;i<myLength;i++)
		{
			d1[i] = y1.get(i).getAvg_price();
			d2[i] = y2.get(i).getAvg_price();
		}
		
		returnValue = euclideanDistance(d1, d2);
		return returnValue;
	}
	
	
	private double euclideanDistance(double[] a, double[] b)
	{
		double Sum = 0.0;
        for(int i=0;i<a.length;i++) 
        {
           Sum = Sum + Math.pow((a[i]-b[i]),2.0);
        }
        return Math.sqrt(Sum);
	}
	
	private class SortComparator implements Comparator<SortValueBean> 
	{
	    public int compare(SortValueBean a, SortValueBean b) 
	    {
	    	return a.getEcl_distance() < b.getEcl_distance() ? -1 : a.getEcl_distance() == b.getEcl_distance() ? 0 : 1;
	    }
	}

}
