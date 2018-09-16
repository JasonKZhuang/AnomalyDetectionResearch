package detection.beans;

import java.util.Date;

public class StockReturnRate
{
	private int rid;
	private String symbol;
	private Date   tdate;
	private float returnRate;
	
	
	public int getRid()
	{
		return rid;
	}
	public void setRid(int rid)
	{
		this.rid = rid;
	}
	public String getSymbol()
	{
		return symbol;
	}
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	public Date getTdate()
	{
		return tdate;
	}
	public void setTdate(Date tdate)
	{
		this.tdate = tdate;
	}
	public float getReturnRate()
	{
		return returnRate;
	}
	public void setReturnRate(float returnRate)
	{
		this.returnRate = returnRate;
	}
	
}
