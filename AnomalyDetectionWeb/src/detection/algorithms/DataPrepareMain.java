package detection.algorithms;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.beans.StockReturnRate;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.fileOperation.FileHelper;
import detection.fileOperation.IFileHelper;
import detection.utility.DateUtil;
import detection.utility.MyConstants;

public class DataPrepareMain
{
	//private String path ="/Users/jason/Documents/JasonProjects/GitHubProject/AnomalyDetectionResearch/AnomalyDetectionWeb/Data/";
	private String path ="D:\\GitHub\\Data";
	private static IFileHelper fileHelper;
	private static IDatabaseHelper dbHelper;
	
	public static void main(String[] args)
	{
		DataPrepareMain myApp = new DataPrepareMain();
	    fileHelper = new FileHelper();
        dbHelper   = new DatabaseHelperImpl();
        //======//
        myApp.importStockName();
		myApp.updateStockDetail();
		Map<String, StockName> stockMap = myApp.cleanStocksName();
		//======//
		myApp.importStockData(stockMap);
		myApp.cleanStockRecords();
        //======//
        myApp.calculateReturnRate();
        //========///
        
        
	}
	
	/**
	 * import all stock name first
	 */
	private void importStockName()
	{
		List<File> fileList = fileHelper.getFileListFromPath(path+"Stocks");
		List<String> stockNameList = new ArrayList<String>();
		dbHelper.truncateTable(MyConstants.TABLE_STOCK_NAME);
		System.out.println("Total number of files is "+ fileList.size());
        for (File file : fileList)
		{
        		String fileName = file.getName();
        		fileName = fileName.substring(0, fileName.length() - 7);
        		stockNameList.add(fileName);
		}
        dbHelper.insertName(stockNameList);
	}
	
	/**
	 * update detail including full name and market cap and sector
	 */
	private void updateStockDetail()
	{
		dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"BasicIndustriesCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"CapitalGoodsCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"ConsumerNon-DurablesCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"ConsumerServicesCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"EnergyCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"FinanceCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"HealthCareCompanies.csv")));
        dbHelper.updateStockDetail(fileHelper.getStocksDetailFromFile(new File(path+"MiscellaneousCompanies.csv")));
		System.out.println("Finished!");
	}
	
	/**
	 * remove the stocks which has no sector or no marketcap
	 * @return
	 */
	private Map<String,StockName> cleanStocksName()
	{
		Map<String,StockName> retMap = new HashMap<>();
		List<StockName> lstStockObjects = dbHelper.getAllStocks();
		String sector = "";
		double marketCap = 0d;
		for (StockName s : lstStockObjects)
		{
			sector = s.getSector();
			marketCap = s.getMarketCap();
			if (sector == null || sector.equals("") || marketCap==0)
			{
				dbHelper.deleStockNameById(s.getId());
			}else
			{
				retMap.put(s.getSymbol(), s);
			}
		}
		
		return retMap;
	}
	
	/**
	 * import stock records for all available stocks
	 * @param stockMap
	 */
	private void importStockData(Map<String, StockName> stockMap )
    {
        List<File> fileList = fileHelper.getFileListFromPath(path+"Stocks");
        System.out.println("Total number of files is "+ fileList.size());
        dbHelper.truncateTable(MyConstants.TABLE_STOCK_RECORDS);
        int i = 1;
        for (File file : fileList)
		{
			String fileName = file.getName();
			String key = fileName.substring(0, fileName.length() - 7);
			if (stockMap.get(key) ==null)
			{
				continue;
			}
			List<StockRecord> recordList = fileHelper.getRecordsFromFile(file);
			dbHelper.insertRecords(key,recordList);
			System.out.println(i+ " Insert table :" + fileName);
			i = i + 1;
		}
        System.out.println("finished");
    }
	
	/**
	 * remove all stock records which are not suitable for our experiment
	 */
	private void cleanStockRecords()
	{
		Date d_min = new Date();
		Date d_max = new Date();
		try
		{
			d_min = DateUtil.convertStringToDate(MyConstants.DATE_MIN_STR);
			d_max = DateUtil.convertStringToDate(MyConstants.DATE_MAX_STR);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		dbHelper.trimDateDurationOfRecords(d_min, d_max);
		System.out.println("Getting rid of records which are ont in the duration is finished.");
		
		Map<String,Integer> mp = dbHelper.getStockTotalDatesGroup();
		for (Map.Entry<String, Integer> entry : mp.entrySet()) 
		{
		   String key = entry.getKey();
		   Integer value = entry.getValue();
		   if (value < MyConstants.TRADE_DAYS_MIN)
		   {
			   System.out.println("Delete Stock name " + key +" and records " + value);
			   dbHelper.deleStockNameAndRecordsBySymbol(key);
		   }
		}
		System.out.println("Delete cascately StockName and records By Symbol finished.");
	}
	
	/**
	 * (close of today - close of yesterday)/close of Yesterday * 100
	 */
	private void calculateReturnRate()
	{
		List<StockName> lstStockNames = dbHelper.getAllStocks();
		dbHelper.truncateTable(MyConstants.TABLE_STOCK_RETURN);
		for (StockName sn : lstStockNames)
		{
			List<StockRecord> records = dbHelper.getRecordByName(sn.getSymbol());
			float preValue = 0f;
			float curValue = 0f;
			float returnRate  = 0f;
			List<StockReturnRate> lstReturnRate = new ArrayList<>();
			
			for(int i=0;i<records.size();i++)
			{
				StockRecord sr = records.get(i);
				curValue = sr.getClose();
				if (i==0)
				{
					preValue = curValue;
					continue;
				}
				
				returnRate = ((curValue - preValue)/preValue) * 100;
				StockReturnRate srr = new StockReturnRate();
				srr.setSymbol(sn.getSymbol());
				srr.setTdate(sr.getTdate());
				srr.setReturnRate(returnRate);
				lstReturnRate.add(srr);
				preValue = curValue;
			}
			
			dbHelper.saveBatchReturnRate(lstReturnRate);
			System.out.println("Calculation of Daily Return of ["+ sn.getSymbol() +"] has been finished.");
			
		}
	}

}
