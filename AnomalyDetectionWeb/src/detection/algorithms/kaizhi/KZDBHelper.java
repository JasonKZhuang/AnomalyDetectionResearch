package detection.algorithms.kaizhi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import detection.algorithms.baseLine.BaseLineDBHelper;
import detection.db.ConnectionSingleton;
import detection.utility.DateUtil;
import detection.utility.MyConstants;

public class KZDBHelper
{
	protected static final Log myLog = LogFactory.getLog(BaseLineDBHelper.class);

	public KZDBHelper()
	{
		// TODO Auto-generated constructor stub
	}

	public void truncateTable(String tableName)
	{
		//myLog.info("Truncating table " + tableName);
		
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

	public void saveSimilarity(List<SimilarityBean> sim_listBeans, String[] dates)
	{
		truncateTable("kz_similarity");
		
		String sql = "INSERT INTO `anomalyresearch`.`kz_similarity` "
				+" (`tdate`, `sim_tdate`,	`sim_value`) "
				+" VALUES (?,?,?)";
				
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		try
		{
			for(int i=0;i<sim_listBeans.size();i++)
			{
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql);
				
				SimilarityBean simBean= sim_listBeans.get(i);
				Date tdate = DateUtil.convertStringToDate(simBean.getBootName());
				double[] arr_sim = simBean.getSimilarity();
				for(int j=0;j<dates.length;j++)
				{
					Date sim_date = DateUtil.convertStringToDate(dates[j]);
					ps.setDate(1, new java.sql.Date(tdate.getTime()));
					ps.setDate(2, new java.sql.Date(sim_date.getTime()));
					ps.setDouble(3, arr_sim[j]);
					ps.addBatch();
				}
				
				//System.out.println("Committing " + tdate.toString());
				ps.executeBatch();
				conn.commit();
				ps.clearBatch();
			}
			conn.setAutoCommit(true);
			
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

	public void updateSimilarity(List<SimilarityBean> sim_listBeans,
			String[] dates)
	{
		String sql = "UPDATE `anomalyresearch`.`kz_similarity` "
				+" SET `sim_value` = ? "
				+" WHERE `tdate` = ? AND `sim_tdate` =? ";
				
		Connection conn = ConnectionSingleton.getConnection();
		PreparedStatement ps = null;
		//int count =0;
		try
		{
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for(int i=0;i<sim_listBeans.size();i++)
			{
				SimilarityBean simBean= sim_listBeans.get(i);
				//the date
				Date tdate = DateUtil.convertStringToDate(simBean.getBootName());
				//the sim array
				double[] arr_sim = simBean.getSimilarity();
				
				for(int j=0;j<dates.length;j++)
				{
					Date sim_date = DateUtil.convertStringToDate(dates[j]);
					ps.setDouble(1, arr_sim[j]);
					ps.setDate(2, new java.sql.Date(tdate.getTime()));
					ps.setDate(3, new java.sql.Date(sim_date.getTime()));
					ps.addBatch();
				}
				ps.executeBatch();
				conn.commit();
				ps.clearBatch();
				//System.out.println("Committing " + tdate.toString());
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
		
	}
	
	public List<SimilarityRecord> getTopSimilarityDateWithValue(String[] dates)
	{
		List<SimilarityRecord> smRecords = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = " SELECT `kz_similarity`.`rId`,"  
				+ " `kz_similarity`.`tdate`,"  
				+ " `kz_similarity`.`sim_tdate`,"  
				+ " `kz_similarity`.`sim_value`"  
				+ " FROM `anomalyresearch`.`kz_similarity`"
				+ " WHERE `kz_similarity`.`tdate`=? "
				+ " ORDER BY `kz_similarity`.`sim_value` DESC";
		int count = 0;
		int    rId;
		double simValue =0d;
		Date   simDate = new Date();
		try
		{
			for(int i=0;i<dates.length;i++)
			{
				count = 0;
				Date tdate = DateUtil.convertStringToDate(dates[i]);
				pstmt = conn.prepareStatement(sql);
				pstmt.setDate(1, new  java.sql.Date(tdate.getTime()));
				ResultSet rs = pstmt.executeQuery();
				//get the more similar day
				while (rs.next())
				{
					if (count == (MyConstants.SIMILARITY_TOP_NUM -1))
					{
						rId = rs.getInt("rId");
						simDate  = rs.getDate("sim_tdate");
						simValue = rs.getDouble("sim_value");
						SimilarityRecord record = new SimilarityRecord();
						record.setrId(rId);
						record.setTdate(tdate);
						record.setSim_date(simDate);
						record.setSim_value(simValue);
						smRecords.add(record);
						break;
					}
					count = count + 1;
				}
				pstmt.close();
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
					pstmt.close();
			} catch (SQLException se2)
			{
			} 
		} // end try
		
		
		//sort the list bean
		Collections.sort(smRecords, new TopSimilarityComparator("ASC"));//1->9
		
		return smRecords;
		
	}
	
	public List<SimilarityRecord> getTopSimilarityDateWithValue(String[] dates,int knn ,int topCount)
	{
		List<SimilarityRecord> retList = new ArrayList<>();
		
		List<SimilarityRecord> smRecords = new ArrayList<>();
		
		Connection conn = ConnectionSingleton.getConnection();
		
		PreparedStatement pstmt = null;
		String sql = " SELECT `kz_similarity`.`rId`,"  
				+ " `kz_similarity`.`tdate`,"  
				+ " `kz_similarity`.`sim_tdate`,"  
				+ " `kz_similarity`.`sim_value`"  
				+ " FROM `anomalyresearch`.`kz_similarity`"
				+ " WHERE `kz_similarity`.`tdate` = ? AND `kz_similarity`.`sim_value` <> 1 "
				+ " ORDER BY `kz_similarity`.`sim_value` DESC";
		int count = 0;
		int    rId;
		double simValue =0d;
		Date   simDate = new Date();
		try
		{
			for(int i=0;i<dates.length;i++)
			{
				count = 0;
				Date tdate = DateUtil.convertStringToDate(dates[i]);
				pstmt = conn.prepareStatement(sql);
				pstmt.setDate(1, new  java.sql.Date(tdate.getTime()));
				ResultSet rs = pstmt.executeQuery();
				
				//get the more similar day
				while (rs.next())
				{
					if (count == (knn -1))
					{
						SimilarityRecord record = new SimilarityRecord();
						rId = rs.getInt("rId");
						simDate  = rs.getDate("sim_tdate");
						simValue = rs.getDouble("sim_value");
						
						record.setrId(rId);
						record.setTdate(tdate);
						record.setSim_date(simDate);
						record.setSim_value(simValue);
						smRecords.add(record);
						break;
					}
					count = count + 1;
					
				}
				pstmt.close();
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
					pstmt.close();
			} catch (SQLException se2)
			{
			} 
		} 
		
		double[] myValue = new double[smRecords.size()];
		for(int i=0;i<smRecords.size();i++)
		{
			myValue[i] = smRecords.get(i).getSim_value();
		}
		
		double mean 	= new Mean().evaluate(myValue);
		double sd 		= new StandardDeviation().evaluate(myValue);
		if (sd != 0)
		{
			NormalDistribution normalDistribution = new NormalDistribution(mean, sd);
			
			double cumValue = 0;
			for(int i=0;i<smRecords.size();i++)
			{
				SimilarityRecord sr = smRecords.get(i);
				cumValue = normalDistribution.cumulativeProbability(sr.getSim_value());
				if (cumValue <= 0.05)
				{
					retList.add(sr);
				}
			}
		}
				
		return retList;
		
	}
	
	
	private class TopSimilarityComparator implements Comparator<SimilarityRecord>
	{
		
		private String sortTypes;//ASC,DESC
		
		/**
		 * @param sortType ASC | DESC
		 */
		public TopSimilarityComparator(String sortType)
		{
			this.sortTypes = sortType;
		}
		
		/**
	     * a negative integer means first argument is less than the second
	     * zero means equal to
	     * a positive integer means first argument is greater than the second
	     */
		public int compare(SimilarityRecord a, SimilarityRecord b) 
	    {
			if (sortTypes.equals("ASC"))
			{
				return a.getSim_value() > b.getSim_value() ? 1 
		    			: a.getSim_value() == b.getSim_value() ? 0 : -1;
	    	}else
			{
				return a.getSim_value() < b.getSim_value() ? 1 
		    			: a.getSim_value() == b.getSim_value() ? 0 : -1;
			}
	    }
	}


	
}
