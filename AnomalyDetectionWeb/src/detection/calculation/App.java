package detection.calculation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.algorithms.AlgorithmsHelper;
import detection.algorithms.IAlgorithmsHelper;
import detection.beans.StockKNNBean;
import detection.beans.StockRecord;
import detection.beans.TimeWindow;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.fileOperation.IFileHelper;
import detection.web.GetDataController;

/**
 * Hello world!
 *
 */
public class App 
{
	private String path ="/Users/jason/Documents/JasonProjects/J2EE/anomalydetection/Data/Stocks";
	private static IFileHelper fileHelper;
	private static IDatabaseHelper dbHelper;
	private static IAlgorithmsHelper algHelper;
	
	protected final Log myLog = LogFactory.getLog(GetDataController.class);
	
	public static void main( String[] args )
    {
        //IDatabaseHelper dbHelper = new ConnectDB();
        //dbHelper.getRecords();
        //dbHelper.getRecords();
        //fileHelper = new FileHelper();
        dbHelper   = new DatabaseHelperImpl();
        algHelper  = new AlgorithmsHelper();
        App app = new App();
        //app.importStockName();
        //app.importStockData();
        app.process();
        //app.processTest();
    }
    
	public void process()
	{
		myLog.info("Get all stock name...");
		List<String> stockNames = dbHelper.getStockNames();
		
		myLog.info("Get Time Windows Length or all transaction date...");
		List<String> tDates = dbHelper.getTransactionDates();
		
		List<TimeWindow> twList = new ArrayList<>();
		
		///to process all data for getting KNN of every stock in every time window
		twList = processAllTimeWidows(tDates, stockNames);
		
		////loop every stock for long period 
		////get every knn group to calculate Jaccard index
		HashMap<String, List<StockKNNBean>> jaccardHM = processGetJaccardIndex(stockNames, twList);
		
		//to store all jaccord index of every time window of every stock into database table
		saveJaccardToDB(jaccardHM);

		myLog.info("Finished!");
		//2018 July 16
		
	}
	
	/**
	 * this method to process all data 
	 * in order to get every Time Windows which contain  all KNN of all stocks 
	 * assuming we hava 100 trading days, so, List<TimeWindow> contains 100 records
	 * @return
	 */
	private List<TimeWindow> processAllTimeWidows(List<String> tDates, List<String> stockNames)
	{
		myLog.info("Processing all KNN object of all stocks in every time window.");
		List<TimeWindow>    twList = new ArrayList<>();
		List<StockRecord>   recordsInOneTimeWidow = new ArrayList<>();
		
		for (int i = 0; i < tDates.size(); i++)
		{
			String myDate = tDates.get(i);
			myLog.info("Get all data of a date which is :" + myDate);
			//get all data of a specific day
			recordsInOneTimeWidow = dbHelper.getRecordsByDay(myDate); 
			
			//create each time window bean to store every stock which contain its KNN
			TimeWindow tw = new TimeWindow(); 
			HashMap<String, StockKNNBean> hm = new HashMap<>();
			
			//iterate every stock, in order to find its Nearest Neighbour
			for (int j = 0; j < stockNames.size(); j++)
			{
				StockKNNBean beanKNN = new StockKNNBean();
				
				String stockName = stockNames.get(j);
				StockRecord record = dbHelper.getRecordByNameAndDate(stockName, myDate);
				if (record == null || record.getStockName() == null )
				{
					continue;
				}
				beanKNN.setSelf(record);
				
				algHelper.kNN(beanKNN, recordsInOneTimeWidow);
				
				hm.put(stockName, beanKNN);
				
			}
			
			tw.setStockBeanMap(hm);
			
			twList.add(tw);
			
		}
		
		return twList;
	}
	
	/**
	 * the return value is a hm with StockName as the key and 
	 * the vale is the list of JaccardIndex
	 * @param stockNames
	 * @param twList
	 * @return
	 */
	private HashMap<String, List<StockKNNBean>> processGetJaccardIndex(List<String> stockNames,List<TimeWindow> twList)
	{
		myLog.info("Calculating Jaccard Index for all stock in each time window. ");
		
		IAlgorithmsHelper   algorithms = new AlgorithmsHelper();
		
		HashMap<String, List<StockKNNBean>> jaccardHM = new HashMap<>();
		
		String stockName ="";
		for (int i = 0; i < stockNames.size(); i++)
		{
			stockName = stockNames.get(i) ;
			
			myLog.info("Stock name: "+ stockName + "....");
			
			List<StockKNNBean> lst = new ArrayList<>();
			//get all knn group of a specific stock during entire time windows
			for(int j=0;j < twList.size(); j++)
			{
				TimeWindow tw = twList.get(j);
				
				HashMap<String, StockKNNBean> hm = tw.getStockBeanMap();
				
				StockKNNBean bean = hm.get(stockName);
				
				if (bean==null)
				{
					continue;
				}
				lst.add(bean);
			}
			
			//for each timewindow, to calculate Jaccard Index
			List<StockKNNBean> jaccardList = new ArrayList<>();
			for(int j=0;j<lst.size();j++)
			{
				StockKNNBean outBean = lst.get(j);
				if (outBean == null)
				{
					continue;
				}
				
				double jaccard_coefficient = 0;
				double jaccard_sum = 0;
				int count = 0;
				for(int k=0;k<lst.size();k++)
				{
					StockKNNBean innerBean = lst.get(k);
					try
					{
						double s = algorithms.similarity(outBean, innerBean);
						jaccard_sum = jaccard_sum + s;
						count = count + 1;
					}catch(Exception exp)
					{
						exp.printStackTrace();
					}
				}
				if (count==0)
				{
					jaccard_coefficient = 0;
				}else
				{
					jaccard_coefficient = jaccard_sum/count;
				}
				
				outBean.setJaccardIndex(jaccard_coefficient);
				
				jaccardList.add(outBean);
			 }
			
			jaccardHM.put(stockName, jaccardList);
			
		}
		
		return jaccardHM;
	
	}
	
	private void saveJaccardToDB(HashMap<String, List<StockKNNBean>> hmJaccard)
	{
		dbHelper.truncateTable("jaccardIndex");
		myLog.info("Save data to database...");
		for (String key : hmJaccard.keySet()) 
		{
			List<StockKNNBean> lst = hmJaccard.get(key);
			dbHelper.insertJaccardRecord(key, lst);
		}
	}
	
   
}
