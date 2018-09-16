import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.anomalyDetection.DatabaseHelperImpl;
import com.anomalyDetection.FileHelper;
import com.anomalyDetection.IDatabaseHelper;
import com.anomalyDetection.IFileHelper;

import algorithms.AlgorithmsHelper;
import algorithms.IAlgorithmsHelper;
import detection.beans.StockBeanKNN;
import detection.beans.StockRecord;
import detection.beans.TimeWindow;

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
	
	public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        //IDatabaseHelper dbHelper = new ConnectDB();
        //dbHelper.getRecords();
        //dbHelper.getRecords();
        fileHelper = new FileHelper();
        dbHelper   = new DatabaseHelperImpl();
        algHelper  = new AlgorithmsHelper();
        App app = new App();
        app.importStockName();
        app.importStockData();
        app.process();
        //app.processTest();
    }
	
	public void processTest()
	{
		StockRecord record = dbHelper.getRecordByNameAndDate("iba", "2006-01-03");
		System.out.println(record.getStockName() + " : " +  record.getClose());
	}
    
	public void process()
	{
		System.out.println("Get all stock name...");
		List<String> 		stockNames = dbHelper.getStockNames();//get all stock names
		System.out.println("Get " + stockNames.size() + " stocks.");
		
		System.out.println("Get all transaction date...");
		List<String> 		tDates = dbHelper.getTransactionDates();//get all transaction dates
		System.out.println("Get " + tDates.size() + " Transaction dates.");
		
		List<TimeWindow>    twList = new ArrayList<TimeWindow>();
		
		///to process all data for getting KNN of every stock in every time window
		twList = processAllTimeWidows(tDates, stockNames);
		
		////loop every stock for long period 
		////get every knn group to calculate Jaccard index
		HashMap<String, List<StockBeanKNN>> jaccardHM = processGetJaccardIndex(stockNames, twList);
		
		//to store all jaccord index of every time window of every stock into database table
		saveJaccardToDB(jaccardHM);

		System.out.println("Finished!");
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
		System.out.println("Processing all KNN object of all stocks in every time window.");
		List<TimeWindow>    twList = new ArrayList<TimeWindow>();
		List<StockRecord>   recordsInOneTimeWidow = new ArrayList<StockRecord>();
		
		for (int i = 0; i < tDates.size(); i++)
		{
			String myDate = tDates.get(i);
			System.out.println("Get all data of a date which is :" + myDate);
			//get all data of a specific day
			recordsInOneTimeWidow = dbHelper.getRecordsByDay(myDate); 
			
			//create each time window bean to store every stock which contain its KNN
			TimeWindow tw = new TimeWindow(); 
			HashMap<String, StockBeanKNN> hm = new HashMap<String, StockBeanKNN>();
			
			//iterate every stock, in order to find its Nearest Neighbour
			for (int j = 0; j < stockNames.size(); j++)
			{
				StockBeanKNN beanKNN = new StockBeanKNN();
				
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
	
	private HashMap<String, List<StockBeanKNN>> processGetJaccardIndex(List<String> stockNames,List<TimeWindow> twList)
	{
		System.out.println("Calculating Jaccard Index for all stock in each time window. ");
		
		IAlgorithmsHelper   algorithms = new AlgorithmsHelper();
		
		HashMap<String, List<StockBeanKNN>> jaccardHM = new HashMap<String, List<StockBeanKNN>>();
		
		for (int j = 0; j < stockNames.size(); j++)
		{
			System.out.println("Stock name: "+ stockNames.get(j) + "....");
			List<StockBeanKNN> lst = new ArrayList<StockBeanKNN>();
			
			//get all knn group of a specific stock during entire time windows
			for(int k=0;k<twList.size();k++)
			{
				TimeWindow tw = twList.get(k);
				HashMap<String, StockBeanKNN> hm = tw.getStockBeanMap();
				StockBeanKNN bean = hm.get(stockNames.get(j));
				if (bean==null)
				{
					continue;
				}
				lst.add(bean);
			}
			
			//for each timewindow, to calculate Jaccard Index
			List<StockBeanKNN> jaccardList = new ArrayList<StockBeanKNN>();
			for(int i=0;i<lst.size();i++)
			{
				StockBeanKNN outBean = lst.get(i);
				if (outBean == null)
				{
					System.out.println("----null");
					continue;
				}
				double jaccard_wi = 0;
				int count = 0;
				for(int b=0;b<lst.size();b++)
				{
					StockBeanKNN innerBean = lst.get(b);
					try
					{
						if ( innerBean == null 
							|| innerBean.getSelf() == null 
							|| outBean.isSame(innerBean) )
							continue;
						
						double s = algorithms.similarity(outBean, innerBean);
						jaccard_wi = jaccard_wi + s;
						count = count + 1;
					}catch(Exception exp)
					{
						exp.printStackTrace();
						continue;
					}
				}
				
				jaccard_wi = jaccard_wi/count;	
				
				outBean.setJaccardIndex(jaccard_wi);
				
				jaccardList.add(outBean);
			 }
			
			jaccardHM.put(stockNames.get(j), jaccardList);
			
		}
		
		return jaccardHM;
	
	}
	
	
	private void saveJaccardToDB(HashMap<String, List<StockBeanKNN>> hmJaccard)
	{
		dbHelper.truncateTable("jaccardIndex");
		System.out.println("Save data to database...");
		for (String key : hmJaccard.keySet()) 
		{
			List<StockBeanKNN> lst = hmJaccard.get(key);
			dbHelper.insertJaccardRecord(key, lst);
		}
	}
	
	
	public void importStockName()
	{
		List<File> fileList = fileHelper.getFileListFromPath(path);
		List<String> stockNameList = new ArrayList<String>();
		dbHelper.truncateTable("stockname");
		System.out.println("Total number of files is "+ fileList.size());
        for (File file : fileList)
		{
        		String fileName = file.getName();
        		fileName = fileName.substring(0, fileName.length() - 7);
        		stockNameList.add(fileName);
		}
        dbHelper.insertName(stockNameList);
	}
	
    public void importStockData()
    {
        List<File> fileList = fileHelper.getFileListFromPath(path);
        System.out.println("Total number of files is "+ fileList.size());
        dbHelper.truncateTable("stock");
        
        int i = 1;
        for (File file : fileList)
		{
			String fileName = file.getName();
			List<StockRecord> recordList = fileHelper.getRecordsFromFile(file);
			System.out.println(i+ " Insert table :" + fileName);
			dbHelper.insertRecords(fileName,recordList);
			i = i + 1;
		}
        System.out.println("finished");
    }
   
}
