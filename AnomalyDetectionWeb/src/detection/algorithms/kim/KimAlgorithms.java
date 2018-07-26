package detection.algorithms.kim;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import detection.beans.DividedWindows;
import detection.beans.StockRecord;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.utility.MyConstants;

public class KimAlgorithms implements IKimAlgorithms
{
	protected static final Log myLog = LogFactory.getLog(KimAlgorithms.class);
	
	/**
	 * process divide original data into N time windows
	 * and each time window store the average price of this short period
	 */
	@Override
	public void divideStockData()
	{
		IDatabaseHelper dbHelper = new DatabaseHelperImpl();
		
		dbHelper.truncateTable(MyConstants.TABLE_DIVIDEWINDOWS);
		
		List<String> stockNames = dbHelper.getStockNames();
		
		List<String> transactonDates =  dbHelper.getTransactionDates();
		
		for (String stName : stockNames)
		{
			List<StockRecord> records = dbHelper.getRecordByName(stName);
			
			if (dbHelper.isStockForAvailableInAllDates(transactonDates, records) == false)
			{
				continue;
			}
			/////////////////////////////
			int idx = 1;
			double tempSum = 0;
			double tempAvg = 0;
			int window_no = 1;
			boolean flag = false;
			
			Date startDate = new Date();
			StockRecord record = null;
			for (int i=0;i<records.size();i++)
			{
				record = records.get(i);
				idx = i + 1;
				tempSum = tempSum + record.getClose();
				//if it is the first record then mark the start date
				if (idx == 1)
				{
					startDate = record.getTdate();
				}else
				{
					if (flag == true)//if this is a new divided window,record the start Date
					{
						startDate = record.getTdate();
						flag = false;
					}
				}
				
				if (idx%MyConstants.dividedFactor==0)
				{
					tempAvg = tempSum/MyConstants.dividedFactor;
					
					DividedWindows dw = new DividedWindows();
					dw.setStock_name(stName);
					dw.setWindow_no(window_no);
					dw.setStart_date(new java.sql.Date(startDate.getTime()));
					dw.setEnd_date(record.getTdate());
					dw.setAvg_price(tempAvg);
					
					dbHelper.saveDivideWindow(dw);
					
					window_no = window_no + 1;
					tempSum = 0;
					flag = true;
					
				}
			}
			
			if (flag == false)
			{
				tempAvg = tempSum/MyConstants.dividedFactor;
				DividedWindows dw = new DividedWindows();
				dw.setStock_name(stName);
				dw.setWindow_no(window_no);
				dw.setStart_date(new java.sql.Date(startDate.getTime()));
				dw.setEnd_date(record.getTdate());
				dw.setAvg_price(tempAvg);
				dbHelper.saveDivideWindow(dw);
				
			}
			
		}
		
		myLog.info("Dividing stock records has been finished.");
	}

	
	@Override
	public HashMap<String, List<String>> getPeerGroup()
	{
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public List<String> getPeerGroupForStock(String stockName, List<String> stockNames)
	{
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public double calculateEucldeanDistance(List<DividedWindows> y1, List<DividedWindows> y2)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
