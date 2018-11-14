package detection.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import detection.beans.DividedStockWindow;
import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.beans.StockReturnRate;
import detection.beans.StockWithNNBean;
import detection.beans.kim.CellsDistance;
import detection.beans.kim.PeerGroup;
import detection.beans.kim.PeerGroupBean;
import detection.beans.kim.Weight;

public interface IDatabaseHelper
{
	/**
	 *select distinct stockname from anomalyresearch.kim_stockrecords;
	 */
	public void insertName();
	
	/**
	 * insert all stocks name into stockname table
	 * @param nameList
	 */
	public void insertName(List<String> nameList);
	
	/**
	 * insert records
	 * @param stockName
	 * @param records
	 */
	public void insertRecords(String stockName, List<StockRecord> records);
	
	/**
	 * @param stockName
	 * @param records
	 */
	public void insertRecords(List<StockRecord> records);
	
	/**
	 * get all stock name
	 * @return
	 */
	public List<String> getStockNames();
	
	/**
	 * get all stock objects
	 * @return
	 */
	public List<StockName> getAllStocks();
	
	/**
	 * get stockname objects by sector
	 * @param sector
	 * @return
	 */
	public List<StockName> getStocksBySector(String sector);
	
	/**
	 * delete a stock name by record id
	 * @param id
	 */
	public void deleStockNameById(int id);
	
	/**
	 * delete a stock name by symbol
	 * @param id
	 */
	public void deleStockNameAndRecordsBySymbol(String symbol);
	
	/**
	 * update all stocks' detail including caplisation and sector
	 * @param stocksDetailFromFile
	 */
	public void updateStockDetail(List<StockName> stocksDetailFromFile);
	
	/**
	 * get rid of unavailable data
	 * @param stocksDetailFromFile
	 */
	public void trimDateDurationOfRecords(Date min, Date max);
	
	/**
	 * get statistic of group stock records
	 * in order to get rid of the stock which is less the specific number
	 * @return
	 */
	public Map<String,Integer> getStockTotalDatesGroup();
	
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
	 * save return rate to table batch
	 * @param lstReturnRate
	 */
	public void saveBatchReturnRate(List<StockReturnRate> lstReturnRate);
	
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
	 * Get all time windows new method
	 * @return
	 */
	public List<Date> getTradingDates(String stockName);
	
	/**
	 * empty the table
	 * @param tableName
	 */
	public void truncateTable(String tableName);

	///==================== kim ==================================///
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
	public void saveDivideWindow(DividedStockWindow dw);
	
	/**
	 * get all Divided Windows by a Stock Name
	 * @param stockName
	 * @return
	 */
	public List<DividedStockWindow> getDividedWindowsByStockname(String stockName);
	
	/**
	 * get a timewindow record by stock name and window no
	 * @param stockName
	 * @param windowNo
	 * @return
	 */
	public DividedStockWindow getDividedWindowByNameAndWindNo(String stockName,int windowNo);
	
	/**
	 * get all stock name from DividedWindows
	 * @return
	 */
	public List<String> getStockNamesFromDivideWindows();
	
	/**
	 * get all available time windows
	 * @return
	 */
	public List<Integer> getDividedWindows();

	/**
	 * Save peer group into data table
	 * @param value
	 */
	public void savePeerGroups(PeerGroup value);
	
	/**
	 * select peers from PeerGroup Table
	 * @param stockName
	 * @return
	 */
	public List<String> getPeerNamesForGroup(String stockName);
	
	/**
	 * select all peers from PeerGroup table for a stock, which includes mean value for each windows
	 * @param stockName
	 * @return
	 */
	public List<PeerGroupBean> getPeergroupsForGroup(String stockName);
	
	/**
	 * get all peer group records from PeerGroup Table
	 * sorted by stock name and window no
	 * @return
	 */
	public List<PeerGroupBean> getAllPeergroups();

	
	/**
	 * update weights and new Mean value for every record
	 * @param recId
	 * @param weights_to_members
	 * @param newMean
	 */
	public void updatePeerGroupNewMeanValue(int recId, double newMean);

	/**
	 * @param stockName
	 * @param peerName
	 * @param proxi
	 * @param weight
	 */
	public void insertProxi(String stockName,String peerName,double eduDistance, double proxi);
	
	/**
	 * get weights of a stock with peers
	 * @param stockName
	 * @return
	 */
	public List<Weight> getWeights(String stockName);
	
	/**
	 * 
	 * @param stockName
	 * @return
	 */
	public Weight getWeights(String stockName,String peerName);
	
	/**
	 * 
	 */
	public List<String> getDistinctStockNameFromWeights(); 
	
	/**
	 * 
	 */
	public void updateWeights(int recId, float w);
	/**
	 * insert Cell Distance
	 * @param saveList
	 */
	public void insertCellsDistance(List<CellsDistance> saveList);

	///======================================================///
	public List<StockReturnRate> getReturnRateByDateSector(StockReturnRate self,Date tempDate, String sector);

	

	
	
	
	
}
