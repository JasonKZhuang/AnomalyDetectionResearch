package detection.beans;

import java.util.Date;

public class StockRecord
{
	private String stockName;
	private Date tdate;
	private double open;
	private double high;
	private double low;
	private double close;
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

	public double getOpen()
	{
		return open;
	}

	public void setOpen(double open)
	{
		this.open = open;
	}

	public double getHigh()
	{
		return high;
	}

	public void setHigh(double high)
	{
		this.high = high;
	}

	public double getLow()
	{
		return low;
	}

	public void setLow(double low)
	{
		this.low = low;
	}

	public double getClose()
	{
		return close;
	}

	public void setClose(double close)
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
