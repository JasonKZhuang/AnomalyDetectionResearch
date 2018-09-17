package detection.algorithms2;

public class RecordBean
{
	private String	d;
	private double[] arrPrice;
	
	public RecordBean()
	{
		;
	}
	
	public RecordBean(String day, int size)
	{
		this.d = day;
		this.arrPrice = new double[size];
	}
	
	public String getD()
	{
		return d;
	}
	public void setD(String d)
	{
		this.d = d;
	}

	public double[] getArrPrice()
	{
		return arrPrice;
	}

	public void setArrPrice(double[] arrPrice)
	{
		this.arrPrice = arrPrice;
	}

}
