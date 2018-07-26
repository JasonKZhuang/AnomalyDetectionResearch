package detection.db;

import java.util.List;

public class Test
{

	private static IDatabaseHelper dbHelper;
	
	public static void main(String[] args)
	{
		//dbHelper   = new DatabaseHelperImpl();
		//Test test = new Test();
		//test.testFilterAllAvailableStocks();
		
		System.out.println(2%5);
	}
	
	public void testFilterAllAvailableStocks()
	{
		List<String> stockNames = dbHelper.getStockNames();
		System.out.println(stockNames.size());
		List<String> newStockNames = dbHelper.filterAllAvailableStocks(stockNames);
		System.out.println("===============================");
		System.out.println(newStockNames.size());
	}

}
