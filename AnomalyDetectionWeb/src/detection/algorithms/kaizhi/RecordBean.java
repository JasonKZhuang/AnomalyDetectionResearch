package detection.algorithms.kaizhi;

public class RecordBean
{
	private String	 myDate;
	private double[] arrPrice;
	private String[] arrStocks;
	
	public RecordBean()
	{
		;
	}
	
	public RecordBean(String day, int size)
	{
		this.myDate = day;
		this.arrPrice = new double[size];
	}

	public String getMyDate()
	{
		return myDate;
	}

	public void setMyDate(String myDate)
	{
		this.myDate = myDate;
	}

	public double[] getArrPrice()
	{
		return arrPrice;
	}

	public void setArrPrice(double[] arrPrice)
	{
		this.arrPrice = arrPrice;
	}

	public String[] getArrStocks()
	{
		return arrStocks;
	}

	public void setArrStocks(String[] arrStocks)
	{
		this.arrStocks = arrStocks;
	}
	
}
