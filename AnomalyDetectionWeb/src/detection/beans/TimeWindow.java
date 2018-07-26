package detection.beans;

import java.util.HashMap;

public class TimeWindow
{
	private HashMap<String,StockKNNBean> stockBeanMap;

	public HashMap<String, StockKNNBean> getStockBeanMap()
	{
		return stockBeanMap;
	}

	public void setStockBeanMap(HashMap<String, StockKNNBean> stockBeanMap)
	{
		this.stockBeanMap = stockBeanMap;
	}
	
}
