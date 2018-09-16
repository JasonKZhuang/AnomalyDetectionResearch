package detection.beans;

import java.util.List;

public class StockWithNNBean
{
	private StockReturnRate self;
	
	private List<StockReturnRate> neighbours;
	
	//private double jaccardIndex;
	
	public StockReturnRate getSelf()
	{
		return self;
	}
	public void setSelf(StockReturnRate self)
	{
		this.self = self;
	}
	public List<StockReturnRate> getNeighbours()
	{
		return neighbours;
	}
	public void setNeighbours(List<StockReturnRate> neighbours)
	{
		this.neighbours = neighbours;
	}
	
	
	
}
