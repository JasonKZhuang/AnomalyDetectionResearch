package detection.beans;

import detection.utility.MyConstants;

/**
 * This bean is used to stock itself and its neighbours
 * @author jason
 */
public class StockKNNBean
{
	private StockRecord self;
	
	private StockRecord[] nearestNeighbours = new StockRecord[MyConstants.K];
	
	private double jaccardIndex;
	
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
	
}
