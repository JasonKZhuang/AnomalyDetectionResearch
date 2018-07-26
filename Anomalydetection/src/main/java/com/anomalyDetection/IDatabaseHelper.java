package com.anomalyDetection;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import detection.beans.StockBeanKNN;
import detection.beans.StockRecord;

public interface IDatabaseHelper
{
	public Connection getConnection();
	
	public List<String> getStockNames();
	
	public List<String> getTransactionDates();
	
	public List<StockRecord> getAllRecords();
	
	/**
	 * get a record by stock name and transaction date
	 * @param aName
	 * @param aDate
	 * @return
	 */
	public StockRecord getRecordByNameAndDate(String aName, String aDate);
	
	/**
	 * get all stock records for one day
	 * @param strDate
	 * @return
	 */
	public List<StockRecord> getRecordsByDay(String oneDay);
	//==================================================================//
	public void insertJaccardRecord(String name, List<StockBeanKNN> jaccardList);
	
	
	
	
	
	//==================================================================//
	public void insertRecord(StockRecord record);
	
	public void insertRecords(String stockName, List<StockRecord> records);

	public void truncateTable(String tableName);
	
	public void insertName(List<String> nameList);
	
	
	
}
