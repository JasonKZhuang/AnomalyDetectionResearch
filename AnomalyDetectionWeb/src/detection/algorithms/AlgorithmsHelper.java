package detection.algorithms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import detection.beans.DistanceBean;
import detection.beans.StockKNNBean;
import detection.beans.StockRecord;
import detection.utility.MyConstants;

public class AlgorithmsHelper implements IAlgorithmsHelper
{
	//calculate K nearest neighbours from the same time window records
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


	private class DistanceComparator implements Comparator<DistanceBean> 
	{
	    public int compare(DistanceBean a, DistanceBean b) 
	    {
	    	return a.getDistance() < b.getDistance() ? -1 : a.getDistance() == b.getDistance() ? 0 : 1;
	    }
	}
	
}
