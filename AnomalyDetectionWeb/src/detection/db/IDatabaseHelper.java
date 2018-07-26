package detection.db;

import java.util.List;

import detection.beans.DividedWindows;
import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockRecord;

public interface IDatabaseHelper
{
	/**
	 * get all stock name
	 * @return
	 */
	public List<String> getStockNames();
	
	/**
	 * get Stock list by stock name
	 * @param aName
	 * @return
	 */
	public List<StockRecord> getRecordByName(String aName);
	
	/**
	 * get all stock records for one day
	 * @param strDate
	 * @return
	 */
	public List<StockRecord> getRecordsByDay(String oneDay);
	
	/**
	 * get a record by stock name and transaction date
	 * @param aName
	 * @param aDate
	 * @return
	 */
	public StockRecord getRecordByNameAndDate(String aName, String aDate);
	
	/**
	 * get record List by a stock name and transaction period
	 * @param aName
	 * @param aDate
	 * @return
	 */
	public List<StockRecord> getRecordByNameAndDate(String aName, String sDate, String eDate);

	/**
	 * get Jaccard  index datalist by a stock name
	 * @param stockName
	 * @return
	 */
	public List<JaccardBean> getJaccardData(String stockName);
	
	/**
	 * insert data to Jaccard index table
	 * @param name
	 * @param jaccardList
	 */
	public void insertJaccardRecord(String name, List<StockKNNBean> jaccardList);
	
	/**
	 * Get all time windows
	 * @return
	 */
	public List<String> getTransactionDates();
	
	/**
	 * empty the table
	 * @param tableName
	 */
	public void truncateTable(String tableName);

	/**
	 * to verify if the stock is available for all transaction dates
	 * @param stockName
	 * @param transactonDates
	 * @param records
	 * @return
	 */
	public boolean isStockForAvailableInAllDates(List<String> transactonDates,List<StockRecord> records);
	
	/**
	 * get all available stocks which have full records in all dates
	 * @param stockNames
	 * @return List of stockNames
	 */
	public List<String> filterAllAvailableStocks(List<String> stockNames);
	
	/**
	 * save DividedWindows object to database table
	 * @param dw
	 */
	public void saveDivideWindow(DividedWindows dw);
	
	
}
