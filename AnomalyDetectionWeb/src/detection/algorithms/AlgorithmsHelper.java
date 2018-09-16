package detection.algorithms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.beans.DistanceBean;
import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.beans.StockReturnRate;
import detection.beans.StockWithNNBean;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.utility.MyConstants;

public class AlgorithmsHelper implements IAlgorithmsHelper
{
	protected static final Log myLog = LogFactory.getLog(AlgorithmsHelper.class);
	
	protected static IDatabaseHelper dbHelper = new DatabaseHelperImpl();
	
	//calculate K nearest neighbours from the same time window records
	@Override
	public void kNN (StockKNNBean bean, List<StockRecord> reordsInTM)
	{
		StockRecord self = bean.getSelf();
		
		List<DistanceBean> lsDistanceBean = new ArrayList<DistanceBean>(); 
		
		for (StockRecord r : reordsInTM)
		{
			String name1="";
			String name2="";
			try
			{
				name1 = self.getStockName();
				name2 = r.getStockName();
			}catch (Exception exp)
			{
				continue;
			}
			
			//r is itself
			if (name1.equals(name2))
			{
				continue;
			}
			
			//the distance between self close price value and any other close price
			float diff = Math.abs(self.getClose() - r.getClose());
			
			//diff =  (float)(Math.round(diff * 10000))/10000;
		    BigDecimal bd = new BigDecimal(diff); 
		    bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP); 
		    diff = bd.floatValue();
			
		    DistanceBean  b = new DistanceBean();
			b.setRecord(r);
			b.setDistance(diff);
			lsDistanceBean.add(b);
		}
		//sort the distance
		Collections.sort(lsDistanceBean, new DistanceComparator()); 
		
		StockRecord[] nearestNeighbours = new StockRecord[MyConstants.K];
		for(int i=0;i<MyConstants.K; i++)
		{
			DistanceBean distBean = lsDistanceBean.get(i);
			nearestNeighbours[i] = distBean.getRecord();
		}
		
		bean.setNearestNeighbours(nearestNeighbours);
	}
	
	//calculate similarity of two group,(cross value)/(union value)
	@Override
	public double similarity(StockKNNBean groupA, StockKNNBean groupB)
	{
		int sameCount = 0;
		int unioCount = 0;
		double retValue = 0d;

		if (groupA == null || groupB == null)
		{
			throw new NullPointerException("group must not be null");
		}

		StockRecord[] g1 = new  StockRecord[MyConstants.K + 1];
		StockRecord[] tempGroup1 = groupA.getNearestNeighbours();
		for(int i=0;i<tempGroup1.length;i++)
		{
			g1[i] = tempGroup1[i];
		}
		g1[tempGroup1.length] = groupA.getSelf();
		
		StockRecord[] g2 = new  StockRecord[MyConstants.K + 1];
		StockRecord[] tempGroup2 = groupB.getNearestNeighbours();
		for(int i=0;i<tempGroup2.length;i++)
		{
			g2[i] = tempGroup2[i];
		}
		g2[tempGroup2.length] = groupB.getSelf();
		
		for(int i=0;i<g1.length;i++)
		{
			StockRecord sr_g1 = g1[i];
			
			for(int j=0;j<g2.length;j++)
			{
				StockRecord sr_g2 = g2[j];
				if (sr_g1.getStockName().equals(sr_g2.getStockName()))
				{
					sameCount = sameCount + 1;
				}
			}
		}
		
		//the union count of two groups
		unioCount = g1.length + g2.length - sameCount;

		if (unioCount != 0)
		{
			retValue = (double)sameCount/(double)unioCount;
		} else
		{
			retValue = 0d;
		}
		
		return retValue;
		
	}
	
	@Override
	public List<StockWithNNBean> getStock_NNForDuration (StockName stockName, List<Date> tradingDates)
	{
		myLog.info("----->Get Nearest Neighbors of one stock for all trading Dates ..."); 
		
		List<StockWithNNBean> retList = new ArrayList<>();
		for(int i=0;i<tradingDates.size();i++)
		{
//			if (i==0)//ignor the first day
//			{
//				continue;
//			}
			
			Date tempDate = tradingDates.get(i);
			StockReturnRate self = new StockReturnRate();
			self.setSymbol(stockName.getSymbol());
			self.setTdate(tempDate);
			
			//get all return rate in the timewindow and sector
			List<StockReturnRate> returnRates = dbHelper.getReturnRateByDateSector(self, tempDate, stockName.getSector());
			
			StockWithNNBean bean = new StockWithNNBean();
			bean.setSelf(self);
			
			buildNN(bean, returnRates);
			
			retList.add(bean);
		}
		
		return retList;
		
	}
	
	@Override
	public List<JaccardBean> calculateJaccardForAStock(List<StockWithNNBean> stocks)
	{
		myLog.info("----->Get Jaccard coefficient of one stock for all trading Dates ..."); 
		
		int w_size = stocks.size();
		List<JaccardBean> retList = new ArrayList<>();
		
		for(int i=0;i<w_size;i++)
		{
			StockWithNNBean bean_group_Ti = stocks.get(i);
			StockReturnRate self = bean_group_Ti.getSelf();
			float sum = 0f;
			for(int j=0; j<w_size;j++)
			{
				StockWithNNBean bean_group_Tj = stocks.get(j);
				sum += similarity_new(bean_group_Ti, bean_group_Tj);
			}
			JaccardBean jaccard = new JaccardBean();
			jaccard.setStockName(self.getSymbol());
			jaccard.setTdate(self.getTdate());
			//remain 6 decimal
			BigDecimal   b   =   new   BigDecimal(sum/w_size);  
			float fJaccard   =   b.setScale(6, BigDecimal.ROUND_HALF_UP).floatValue();
			jaccard.setJaccard(fJaccard);
			///
			retList.add(jaccard);
		}
		
		return retList;
	}

	
	/**
	 * calculate similarity of two group,(cross value)/(union value)
	 * @param groupA
	 * @param groupB
	 * @return
	 */
	private float similarity_new (StockWithNNBean groupA, StockWithNNBean groupB)
	{
		int sameCount = 0;
		int unioCount = 0;
		float retValue = 0f;

		if (groupA == null || groupB == null)
		{
			throw new NullPointerException("group must not be null");
		}

		
		List<StockReturnRate> tempGroupA = groupA.getNeighbours();
		List<StockReturnRate> tempGroupB = groupB.getNeighbours();
		int sizeA = tempGroupA.size();
		int sizeB = tempGroupB.size();
		
		for(int i=0;i<sizeA;i++)
		{
			StockReturnRate return_of_GA = tempGroupA.get(i);
			
			for(int j=0;j<sizeB;j++)
			{
				StockReturnRate return_of_GB = tempGroupB.get(j);
				
				if (return_of_GA.getSymbol().equals(return_of_GB.getSymbol()))
				{
					sameCount = sameCount + 1;
					break;
				}
			}
		}
		
		//the union count of two groups
		unioCount = sizeA + sizeB - sameCount;

		if (unioCount != 0)
		{
			retValue = (float)sameCount/(float)unioCount;
		} else
		{
			retValue = 0f;
		}
		
		return retValue;
	}
	
	/**
	 * calculate the nearest neighbors for a stock based on return rate
	 * @param bean it contains its neighbors in corresponding window
	 * @param returnRates all records in this time window
	 */
	private void buildNN(StockWithNNBean bean, List<StockReturnRate> returnRates)
	{
		float selfReturn = bean.getSelf().getReturnRate();
		float minReturn = selfReturn - (Math.abs(selfReturn) * MyConstants.nearCoefficient);
		float maxReturn = selfReturn + (Math.abs(selfReturn) * MyConstants.nearCoefficient);
		float temReturn = 0;
		if (bean.getNeighbours() == null)
		{
			bean.setNeighbours(new ArrayList<StockReturnRate>());
		}
		
		for (StockReturnRate obj : returnRates)
		{
			/*
			if (obj.getSymbol().equals(bean.getSelf().getSymbol()))
			{
				continue;
			}
			*/
			temReturn = obj.getReturnRate();
			if (temReturn>=minReturn && temReturn<=maxReturn)
			{
				bean.getNeighbours().add(obj);
			}
		}
		
	}
	
	private class DistanceComparator implements Comparator<DistanceBean> 
	{
	    public int compare(DistanceBean a, DistanceBean b) 
	    {
	    	return a.getDistance() < b.getDistance() ? -1 : a.getDistance() == b.getDistance() ? 0 : 1;
	    }
	}

}
