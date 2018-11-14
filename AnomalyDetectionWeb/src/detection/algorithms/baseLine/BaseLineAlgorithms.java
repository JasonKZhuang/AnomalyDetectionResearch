package detection.algorithms.baseLine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.beans.DividedStockWindow;
import detection.beans.SortValueBean;
import detection.beans.StockRecord;
import detection.beans.kim.CellsDistance;
import detection.beans.kim.PeerGroup;
import detection.beans.kim.PeerGroupBean;
import detection.beans.kim.Weight;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.utility.MyConstants;

public class BaseLineAlgorithms implements IBaseLineAlgorithms
{
	protected static final Log myLog = LogFactory.getLog(BaseLineAlgorithms.class);
	
	protected static IDatabaseHelper dbHelper = new DatabaseHelperImpl();
	
	protected static DecimalFormat decimalFormat = new DecimalFormat("0.0000000000000000000000");
	
	/**
	 * process divide original data into N time windows
	 * and each time window store the average price of this short period
	 */
	@Override
	public void divideStockData()
	{
		myLog.info(">The system is dividing Stock date to N time windows....");
		
		dbHelper.truncateTable(MyConstants.TABLE_DIVIDEWINDOWS);
		
		List<String> stockNames = dbHelper.getStockNames();
		
		List<String> transactonDates =  dbHelper.getTransactionDates();
		
		for (String stName : stockNames)
		{
			List<StockRecord> records = dbHelper.getRecordByName(stName);
			
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
					
					dbHelper.saveDivideWindow(dw);
					
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
				dbHelper.saveDivideWindow(dw);
				
			}
			
		}
		
		myLog.info(">Dividing stock records has been finished.");
	}

	
	@Override
	public HashMap<String, List<SortValueBean>> createPeerGroup(List<String> stockNames)
	{
		myLog.info(">Creating Peer Groups for all stocks...");
		HashMap<String,List<SortValueBean>> retMap = new HashMap<>();
		
		///
		HashMap<String,List<DividedStockWindow>> hmWindows = new HashMap<>();
		for (String s : stockNames)
		{
			List<DividedStockWindow> wRecords = dbHelper.getDividedWindowsByStockname(s);
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
		    
		    //get the top k 
		    for(int i=0;i<MyConstants.K;i++)
		    {
		    	distanceListK.add( distanceListAll.get(i));
		    }
		    
		    retMap.put(selfKey, distanceListK);
		}
		
		return retMap;
	}

	@Override
	public double calculateEuclideanDistance(List<DividedStockWindow> y1, List<DividedStockWindow> y2)
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
		
		//EuclideanDistance ed = new EuclideanDistance();
		//returnValue = ed.compute(d1, d2);
		returnValue = euclideanDistance(d1, d2);
		return returnValue;
	}
	
	@Override
	public double euclideanDistance(double[] a, double[] b)
	{
		double Sum = 0.0;
        for(int i=0;i<a.length;i++) 
        {
           Sum = Sum + Math.pow((a[i]-b[i]),2.0);
        }
        return Math.sqrt(Sum);
	}


	@Override
	public List<HashMap<String,PeerGroup>> updatePeerGroup(List<Integer> divdWindows
			,HashMap<String, List<SortValueBean>> pgMap)//every stock with its peer members
	{
		myLog.info(">Updating every peer group in each window, including window_no, start date, end date and mean value...");
		List<HashMap<String,PeerGroup>> retValue = new ArrayList<>();
		/////out loop is time windows list
		for(int i=0;i<divdWindows.size();i++)
		{
			//get time window index
			int wIdx = divdWindows.get(i);
			myLog.info(">Dealing with window : " + wIdx+ ".....");
			HashMap<String,PeerGroup> tempHM = new HashMap<>();
			
			//iterate every stock with its peer members
			//get the stockname,window_no, avg_price from database,
			//calculate the meanvalue of peerGroup including itself
			//put the result into the tempHM
			for (Map.Entry<String, List<SortValueBean>> entry : pgMap.entrySet()) 
			{
				//key is stockName,value is the peer group members
				//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				String stockName = entry.getKey();
				List<SortValueBean> peerList = entry.getValue();
				
				PeerGroup pg = new PeerGroup();
			   
				DividedStockWindow dsw = dbHelper.getDividedWindowByNameAndWindNo(stockName, wIdx);
			    pg.setSelf(dsw);
			    
			    for(int j=0;j<peerList.size();j++)
			    {
			    	SortValueBean svb = peerList.get(j);
			    	DividedStockWindow peer_dsw = dbHelper.getDividedWindowByNameAndWindNo(svb.getName(), wIdx);
			    	pg.getPeers().add(peer_dsw);
			    	pg.getEucl_distance_peers().add(svb.getEcl_distance());
			    }
			    pg.setGroupMeanValue();
			   
			    tempHM.put(stockName, pg);

			}
			
			retValue.add(tempHM);
			
		}
		
		return retValue;
	}

	@Override
	public void savePeerGroup(List<HashMap<String, PeerGroup>> peerGroups)
	{
		dbHelper.truncateTable(MyConstants.TABLE_PEER_GROUP);
		dbHelper.truncateTable(MyConstants.TABLE_WEIGHTS);
		
		myLog.info(">Saving peer Groups infromation to database....");
		
		for(int i=0;i<peerGroups.size(); i++)
		{
			HashMap<String, PeerGroup> hmPG = peerGroups.get(i);
			for (PeerGroup value : hmPG.values()) 
			{
			    dbHelper.savePeerGroups(value);
			}
		}
		
	}
	
	@Override
	public void updateWeights()
	{
		List<String> lstStockName = dbHelper.getDistinctStockNameFromWeights();
		for(int i=0;i<lstStockName.size();i++)
		{
			String stockName = lstStockName.get(i);
			List<Weight> lstWeight = dbHelper.getWeights(stockName);
			//get the sum value of Proximity
			float sumProxi = 0f;
			for (Weight w : lstWeight)
			{
				sumProxi += w.getProxi();
			}
			//
			for (int j=0;j<lstWeight.size();j++)
			{
				Weight w = lstWeight.get(j);
				float d_weight = 0f;
				if (sumProxi==0)
				{
					d_weight = 0f;
				}else
				{
					d_weight = w.getProxi()/sumProxi;
				}
				
				dbHelper.updateWeights(w.getId(), d_weight);
			}
		}
	}


	@Override
	public void updateMeanValueWithWeights()
	{
		myLog.info(">Updating the New Mean value of peer group with wights....");
		List<PeerGroupBean> pgBeanList = dbHelper.getAllPeergroups();
		for(int i=0;i<pgBeanList.size();i++)
		{
			PeerGroupBean tempBean = pgBeanList.get(i);
			int 	recId = tempBean.getRid();
			String 	stockName = tempBean.getStock_name();
			int		windowNo = tempBean.getWindow_no();
			String[]  arr_peers_name = tempBean.getPeer_members().split(",");
			double newMeanValue = 0f;
			
			//calculate the sum value of (weight * peer's value) 
			//for current stock and current window no.
			for(int k=0; k<arr_peers_name.length; k++)
			{
				String peerName = arr_peers_name[k];
				//stock_name
				Weight tempWeight = dbHelper.getWeights(stockName,peerName);
				DividedStockWindow  dsw = dbHelper.getDividedWindowByNameAndWindNo(peerName, windowNo);
				newMeanValue += tempWeight.getWeight() * dsw.getAvg_price();
			}
			//
			dbHelper.updatePeerGroupNewMeanValue(recId, newMeanValue);
			
		}
	}

	@Override
	public void processCellsDistance()
	{
		dbHelper.truncateTable(MyConstants.TABLE_CELLS_DISTANCE);
		myLog.info(">>>>process every cell distance in every group and time windows....");
		String self_name = "";
		int window_no = 0;
		String peer_members = "";
		double stock_avg=0.0d;
		double peer_mean=0.0d;
		double difference=0.0d;
		String peer_name="";
		
		List<PeerGroupBean> pgBeanList = dbHelper.getAllPeergroups();
		List<CellsDistance> saveList = null;
		
		for(int i=0;i<pgBeanList.size();i++)
		{
			PeerGroupBean tempBean = pgBeanList.get(i);
			self_name = tempBean.getStock_name();
			window_no  = tempBean.getWindow_no();
			peer_mean  = tempBean.getMean_value_with_weights();
			if (peer_mean==0)
			{
				continue;
			}
			
			//
			saveList = new ArrayList<>();
			//the difference between self and weight_mean_value
			DividedStockWindow dswSelf = dbHelper.getDividedWindowByNameAndWindNo(self_name, window_no);
			stock_avg = dswSelf.getAvg_price();
			difference = Math.abs(stock_avg - peer_mean);
			CellsDistance beanSelf = new CellsDistance();
			beanSelf.setStock_name(self_name);
			beanSelf.setWindow_no(window_no);
			beanSelf.setPeer_name(self_name);
			beanSelf.setPeer_avg(stock_avg);
			beanSelf.setPeer_mean(peer_mean);
			beanSelf.setDifference(difference);
			saveList.add(beanSelf);

			//
			peer_members = tempBean.getPeer_members();
			String[] peers = peer_members.split(",");
			for(int j=0;j<peers.length;j++)
			{
				peer_name = peers[j];
				DividedStockWindow dsw = dbHelper.getDividedWindowByNameAndWindNo(peer_name, window_no);
				double peer_stock_avg = dsw.getAvg_price();
				double peer_difference = Math.abs(peer_stock_avg - peer_mean);
				
				CellsDistance peer_bean = new CellsDistance();
				peer_bean.setStock_name(self_name);
				peer_bean.setWindow_no(window_no);
				peer_bean.setPeer_name(peer_name);
				peer_bean.setPeer_avg(peer_stock_avg);
				peer_bean.setPeer_mean(peer_mean);
				peer_bean.setDifference(peer_difference);
				saveList.add(peer_bean);
			}
			
			if (saveList.size()>0)
			{
				dbHelper.insertCellsDistance(saveList);
			}
			
		}
		
	}


	/**
	 * the sum value of  (the weight * old mean value) = the new mean value
	 * @param tempBean
	 * @param weights_to_members
	 * @return
	 */
	private double calculateMeanValueWithWeights(PeerGroupBean tempBean, String weights_to_members)
	{
		myLog.info(">>>>>>"+weights_to_members);
		double retValue = 0.0d;
		String[] arr_stockName = tempBean.getPeer_members().split(","); 
		String[] weights = weights_to_members.split(",");
		try
		{
			int windowNo = tempBean.getWindow_no();
			for(int i=0;i<arr_stockName.length;i++)
			{
				DividedStockWindow dsw = dbHelper.getDividedWindowByNameAndWindNo(arr_stockName[i], windowNo);
				double d_weight = Double.parseDouble(weights[i]);
				retValue = retValue + (d_weight * dsw.getAvg_price());
			}
		}catch(Exception exp)
		{
			System.out.println("111111111111111");
			exp.printStackTrace();
		}
		
		return retValue;
	}

	private String processProxiToWeight(String proxis)
	{
		String retValue ="";
		String[] arr_proxi_input = proxis.split(",");
		double[] arr_proxi_input_d  = new double[arr_proxi_input.length];
		double[] arr_proxi_output_d = new double[arr_proxi_input.length];
		double sumValue = 0.0d;
		
		//calculate sum value
		for(int i=0;i<arr_proxi_input.length;i++)
		{
			arr_proxi_input_d[i] = Double.parseDouble(arr_proxi_input[i]);
			sumValue = sumValue + arr_proxi_input_d[i];
		}
		
		//calculate percentage of every proxi, which is weight
		for(int i=0;i<arr_proxi_input_d.length;i++)
		{
			if (sumValue ==0)
			{
				arr_proxi_output_d[i] = 0;
				retValue +=  "0,";
			}else
			{
				arr_proxi_output_d[i] = arr_proxi_input_d[i]/sumValue;
				retValue += decimalFormat.format(arr_proxi_output_d[i]) + ","; 
			}
		}

		return retValue.substring(0,retValue.length() - 1);
		
	}

	private class SortComparator implements Comparator<SortValueBean> 
	{
	    public int compare(SortValueBean a, SortValueBean b) 
	    {
	    	return a.getEcl_distance() < b.getEcl_distance() ? -1 : a.getEcl_distance() == b.getEcl_distance() ? 0 : 1;
	    }
	}
	
}
