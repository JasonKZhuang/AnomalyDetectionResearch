package detection.algorithms.kaizhi;

public class StockSeries
{
	private String stockName;
	private String[] tDate;
	private double[] prices;
	private double meanValue;
	private double std;
	private double r;
	
	public StockSeries(String stockName, int days)
	{
		this.stockName = stockName;
		this.prices = new double[days];
		this.tDate  = new String[days];
	}
	
	public String getStockName()
	{
		return stockName;
	}

	public void setStockName(String stockName)
	{
		this.stockName = stockName;
	}

	public double[] getPrices()
	{
		return prices;
	}

	public void setPrices(double[] prices)
	{
		this.prices = prices;
	}
	
	public void setPrices(int idx,double price)
	{
		this.prices[idx] = price;
	}


	public double getMeanValue()
	{
		return meanValue;
	}


	public void setMeanValue(double meanValue)
	{
		this.meanValue = meanValue;
	}


	public double getStd()
	{
		return std;
	}


	public void setStd(double std)
	{
		this.std = std;
	}


	public double getR()
	{
		return r;
	}


	public void setR(double r)
	{
		this.r = r;
	}

	public String[] gettDate()
	{
		return tDate;
	}

	public void settDate(String[] tDate)
	{
		this.tDate = tDate;
	}
	
	public void settDate(int idx,String d)
	{
		this.tDate[idx] = d;
	}

}
