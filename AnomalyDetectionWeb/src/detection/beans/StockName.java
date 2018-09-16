package detection.beans;

public class StockName
{
	private int id;
	private String symbol;
	private String realName;
	private double marketCap;//million
	private String sector;
	private String industryGroup;
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getSymbol()
	{
		return symbol;
	}
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	public String getRealName()
	{
		return realName;
	}
	public void setRealName(String realName)
	{
		this.realName = realName;
	}
	public double getMarketCap()
	{
		return marketCap;
	}
	public void setMarketCap(double marketCap)
	{
		this.marketCap = marketCap;
	}
	public String getSector()
	{
		return sector;
	}
	public void setSector(String sector)
	{
		this.sector = sector;
	}
	public String getIndustryGroup()
	{
		return industryGroup;
	}
	public void setIndustryGroup(String industryGroup)
	{
		this.industryGroup = industryGroup;
	}
	
	
}
