package detection.algorithms.baseLine;

public class PeerGroupBean
{
	private int rId;
	private String stock_name;
	private String peer_name;
	private double distance;
	private double prox;
	private double weight;
	
	public PeerGroupBean()
	{
		// TODO Auto-generated constructor stub
	}

	public int getrId()
	{
		return rId;
	}

	public void setrId(int rId)
	{
		this.rId = rId;
	}

	public String getStock_name()
	{
		return stock_name;
	}

	public void setStock_name(String stock_name)
	{
		this.stock_name = stock_name;
	}

	public String getPeer_name()
	{
		return peer_name;
	}

	public void setPeer_name(String peer_name)
	{
		this.peer_name = peer_name;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public double getProx()
	{
		return prox;
	}

	public void setProx(double prox)
	{
		this.prox = prox;
	}

	public double getWeight()
	{
		return weight;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}
	
	
	
}
