package detection.beans;

import java.util.Date;

public class StockRecord
{
	private String stockName;
	private Date tdate;
	private float open;
	private float high;
	private float low;
	private float close;
	private long volume;
	
	public StockRecord()
	{
		// TODO Auto-generated constructor stub
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

	public float getOpen()
	{
		return open;
	}

	public void setOpen(float open)
	{
		this.open = open;
	}

	public float getHigh()
	{
		return high;
	}

	public void setHigh(float high)
	{
		this.high = high;
	}

	public float getLow()
	{
		return low;
	}

	public void setLow(float low)
	{
		this.low = low;
	}

	public float getClose()
	{
		return close;
	}

	public void setClose(float close)
	{
		this.close = close;
	}

	public long getVolume()
	{
		return volume;
	}

	public void setVolume(long volume)
	{
		this.volume = volume;
	}
	
}
