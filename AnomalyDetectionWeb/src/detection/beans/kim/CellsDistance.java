package detection.beans.kim;

public class CellsDistance
{
	public long id;
	public String stock_name;
	public int window_no;
	public String peer_name;
	public double peer_avg;
	public double peer_mean;
	public double difference;
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getStock_name()
	{
		return stock_name;
	}
	public void setStock_name(String stock_name)
	{
		this.stock_name = stock_name;
	}
	public int getWindow_no()
	{
		return window_no;
	}
	public void setWindow_no(int window_no)
	{
		this.window_no = window_no;
	}
	
	public double getPeer_avg()
	{
		return peer_avg;
	}
	public void setPeer_avg(double peer_avg)
	{
		this.peer_avg = peer_avg;
	}
	public double getPeer_mean()
	{
		return peer_mean;
	}
	public void setPeer_mean(double peer_mean)
	{
		this.peer_mean = peer_mean;
	}
	public double getDifference()
	{
		return difference;
	}
	public void setDifference(double difference)
	{
		this.difference = difference;
	}
	public String getPeer_name()
	{
		return peer_name;
	}
	public void setPeer_name(String peer_name)
	{
		this.peer_name = peer_name;
	}
	
	

}
