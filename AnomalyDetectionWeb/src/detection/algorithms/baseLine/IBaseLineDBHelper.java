package detection.algorithms.baseLine;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import detection.beans.DividedStockWindow;
import detection.beans.SortValueBean;
import detection.beans.StockRecord;

public interface IBaseLineDBHelper
{
	/**
	 * empty the table
	 * @param tableName
	 */
	public void truncateTable(String tableName);
	
	public void updateStockNameFromStockRecords();
	
	public List<String> getStockNames();

	public List<String> getTransactionDates();
	
	public StockRecord getStockByNameDate(String stockName,Date tDate);
	
	public void divideStockData(List<String> stockNames,List<String> transactonDates);
	
	public void tranformToDivideWindow(List<String> stockNames,List<String> transactonDates);

	//public void updatePeerGroupMean(List<String> transactonDates);

	public List<StockRecord> getRecordByName(String aName);
	
	public void saveDivideWindow(DividedStockWindow dw);
	
	public List<DividedStockWindow> getDividedWindowsByStockname(String stockName);

	/**
	 * Creating Peer group for every stock, 
	 * which based on N dimension to calculate EUclidean Distance
	 * @param stockNames
	 * @return
	 */
	public HashMap<String, List<SortValueBean>> createPeerGroup(List<String> stockNames);
	
	public void savePeerGroup(HashMap<String, List<SortValueBean>> pgMap);

	public List<PeerGroupBean> getPeerGroups();
	
	public List<PeerGroupBean> getPeerGroupsByStockName(String stockName);
	
	public void updateProx();

	public void updateWeight(List<String> stockNameList);

	public void updatePeerGroupWeightMean(List<String> stockNameList,
			List<String> transactonDates);

	public void calculateExtremes(List<String> stockNameList);
	
	public List<PeerGroupRecordBean> getTopPeerGroupRecords();
	
}
