package detection.algorithms.kaizhi;

public class KSTestStatisticBean
{
	private String dateName;
	private double sumPValue;
	//private int statisticLargeCount;
	private double rejectCount;
	
	public String getDateName()
	{
		return dateName;
	}
	public void setDateName(String dateName)
	{
		this.dateName = dateName;
	}
	
	
	public double getSumPValue()
	{
		return sumPValue;
	}
	public void setSumPValue(double sumPValue)
	{
		this.sumPValue = sumPValue;
	}
	public double getRejectCount()
	{
		return rejectCount;
	}
	public void setRejectCount(double rejectCount)
	{
		this.rejectCount = rejectCount;
	}
	
}
