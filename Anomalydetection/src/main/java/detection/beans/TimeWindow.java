package detection.beans;

import java.util.HashMap;

public class TimeWindow
{
	private HashMap<String,StockBeanKNN> stockBeanMap;

	public HashMap<String, StockBeanKNN> getStockBeanMap()
	{
		return stockBeanMap;
	}

	public void setStockBeanMap(HashMap<String, StockBeanKNN> stockBeanMap)
	{
		this.stockBeanMap = stockBeanMap;
	}
	
}
