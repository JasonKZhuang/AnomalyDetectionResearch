package algorithms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import detection.beans.DistanceBean;
import detection.beans.MyConstants;
import detection.beans.StockBeanKNN;
import detection.beans.StockRecord;

public class AlgorithmsHelper implements IAlgorithmsHelper
{

	public void kNN (StockBeanKNN bean, List<StockRecord> recordCollecton)
	{
		StockRecord self = bean.getSelf();
		List<DistanceBean> lsDistanceBean = new ArrayList<DistanceBean>(); 
		
		for (StockRecord r : recordCollecton)
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
			if (name1.equals(name2))
			{
				continue;
			}
			//the distance between self price value and any other close price
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
		
		Collections.sort(lsDistanceBean, new DistanceComparator()); 
		
		StockRecord[] nearestNeighbours = new StockRecord[MyConstants.K];
		for(int i=0;i<MyConstants.K; i++)
		{
			DistanceBean distBean = lsDistanceBean.get(i);
			nearestNeighbours[i] = distBean.getRecord();
		}
		
		bean.setNearestNeighbours(nearestNeighbours);
	}
	
	public double similarity(StockBeanKNN groupA, StockBeanKNN groupB)
	{
		int sameCount = 0;
		int unioCount = 0;
		double retValue = 0d;

		if (groupA == null)
		{
			throw new NullPointerException("g1 must not be null");
		}

		if (groupB == null)
		{
			throw new NullPointerException("g2 must not be null");
		}

		StockRecord[] g1 = groupA.getNearestNeighbours();
		StockRecord[] g2 = groupB.getNearestNeighbours();
		for(int i=0;i<g1.length;i++)
		{
			StockRecord r_g1 = g1[i];
			
			for(int j=0;j<g2.length;j++)
			{
				StockRecord r_g2 = g2[j];
				if (r_g1.getStockName().equals(r_g2.getStockName()))
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


	private class DistanceComparator implements Comparator<DistanceBean> 
	{
	    public int compare(DistanceBean a, DistanceBean b) 
	    {
	    	return a.getDistance() < b.getDistance() ? -1 : a.getDistance() == b.getDistance() ? 0 : 1;
	    }
	}
	
	
	
	
}
