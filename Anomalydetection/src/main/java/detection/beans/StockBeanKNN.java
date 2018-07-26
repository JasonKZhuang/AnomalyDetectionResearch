package detection.beans;

import java.util.Date;

/**
 * This bean is used to stock itself and its neighbours
 * @author jason
 */
public class StockBeanKNN
{
	private StockRecord self;
	
	private StockRecord[] nearestNeighbours = new StockRecord[MyConstants.K];
	
	private double jaccardIndex;

	public StockBeanKNN()
	{
		;
	}
	
	public StockRecord getSelf()
	{
		return self;
	}

	public void setSelf(StockRecord self)
	{
		this.self = self;
	}

	public StockRecord[] getNearestNeighbours()
	{
		return nearestNeighbours;
	}

	public void setNearestNeighbours(StockRecord[] nearestNeighbours)
	{
		this.nearestNeighbours = nearestNeighbours;
	}
	
	public double getJaccardIndex()
	{
		return jaccardIndex;
	}

	public void setJaccardIndex(double jaccardIndex)
	{
		this.jaccardIndex = jaccardIndex;
	}

	public void addNeighbour(StockRecord record)
	{
		for(int i=0;i<MyConstants.K;i++)
		{
			if (nearestNeighbours[i] == null)
			{
				nearestNeighbours[i] = record;
				break;
			}
		}
		
	}
	
	public boolean isSame(StockBeanKNN bean)
	{
		String stockName1 = self.getStockName();
		if (bean == null || bean.getSelf() == null)
		{
			return false;
		}
		
		String stockName2 = bean.getSelf().getStockName();
		
		Date d1 = self.getTdate();
		Date d2 = bean.getSelf().getTdate();
		
		if (stockName1.equals(stockName2) && d1.getTime() == d2.getTime())
		{
			return true;
		}
		return false;
	}
	
}
