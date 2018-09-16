package detection.beans;

import java.util.Date;

public class JaccardBean
{
	private long id;
	private String stockName;
	private Date tdate;
	private float jaccard;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getStockName()
	{
		return stockName;
	}
	public void setStockName(String stockName)
	{
		this.stockName = stockName;
	}
	public Date getTdate()
	{
		return tdate;
	}
	public void setTdate(Date tdate)
	{
		this.tdate = tdate;
	}
	public float getJaccard()
	{
		return jaccard;
	}
	public void setJaccard(float jaccard)
	{
		this.jaccard = jaccard;
	}
	
}
