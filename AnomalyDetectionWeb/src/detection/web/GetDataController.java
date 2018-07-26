package detection.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import detection.beans.JaccardBean;
import detection.beans.StockRecord;
import detection.db.IDatabaseHelper;

@Controller
public class GetDataController
{
	@Autowired
	private IDatabaseHelper dbHelper;
	
	private static List<String> stockNames = new ArrayList<>();
	private static int idx1 = 0;
	private static int idx2 = 0;
	protected final Log myLog = LogFactory.getLog(GetDataController.class);
	
	@ResponseBody
	@RequestMapping(value="/initStockData/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<StockRecord> initStockData(@PathVariable String stockName)
    {
		if (stockNames.size() == 0)
		{
			stockNames = dbHelper.getStockNames();
		}
		
		if (stockName.equals("0")||stockName.equals(""))
		{
			stockName = stockNames.get(0);
		}
		
		myLog.info("-- STOCK NAME:" + stockName);
		
		List<StockRecord> retList = null;
		try
		{
			retList = dbHelper.getRecordByName(stockName);
		}catch(Exception exp)
		{
			myLog.info(exp.getMessage());
		}
		return retList;
    }
	
	@ResponseBody
	@RequestMapping(value="/addStockData",method=RequestMethod.GET,produces="application/json")
    public List<StockRecord> addStockData()
    {
		idx1= idx1 + 1;
		
		if (idx1 == stockNames.size())
		{
			return null;
		}
		String stockName = stockNames.get(idx1);
//		if (stockName.equals("achv") || (stockName.equals("abio")))
//		{
//			return null;
//		}
		System.out.println("==:"+stockName);
		List<StockRecord> retList = new ArrayList<StockRecord>();
		retList = dbHelper.getRecordByName(stockName);
		
		return retList;
    }
	
	@ResponseBody
	@RequestMapping(value="/getStockData/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<StockRecord> getStockData(@PathVariable String stockName)
    {
		System.out.println("-- STOCK NAME:" + stockName);
		List<StockRecord> retList = new ArrayList<StockRecord>();
		retList = dbHelper.getRecordByName(stockName);
		return retList;
    }
	
	@ResponseBody
	@RequestMapping(value="/initJaccardData/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<JaccardBean> initJaccardData(@PathVariable String stockName)
    {
		if (stockNames.size() == 0)
		{
			stockNames = dbHelper.getStockNames();
		}
		
		if (stockName == null || stockName.equals("0"))
		{
			stockName = stockNames.get(0);
		}
		
		myLog.info("STOCK NAME Jaccard index:" + stockName);
		
		List<JaccardBean> retList = null;
		try
		{
			retList = dbHelper.getJaccardData(stockName);
		}catch(Exception exp)
		{
			myLog.info(exp.getMessage());
		}
		return retList;
    }
	
	@ResponseBody
	@RequestMapping(value="/addJaccardData",method=RequestMethod.GET,produces="application/json")
    public List<JaccardBean> addJaccardData()
    {
		idx2 = idx2 + 1;
		
		if (idx2 == stockNames.size())
		{
			return null;
		}
		
		String stockName = stockNames.get(idx2);

		List<JaccardBean> retList = null;
		try
		{
			retList = dbHelper.getJaccardData(stockName);
		}catch(Exception exp)
		{
			myLog.info(exp);
		}
		
		return retList;
    }
	
	
	
	
	
	
	
	
	@ResponseBody //SpringMVC return json data pattern
	@RequestMapping(value="/getStockDataTest/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<StockRecord> getStockDataTest(@PathVariable String stockName)
    {
		System.out.println("-----------");
		List<StockRecord> retList = new ArrayList<StockRecord>();
		Date curentDate = new Date();
		long dateTime = curentDate.getTime();
		for(int i=100;i>=1;i--)
		{
			StockRecord record = new StockRecord();
			record.setStockName("testStockName");
			Date tempDate = new Date();
			tempDate.setTime(dateTime - (i * 24 * 60 *60  *1000));
			record.setTdate(tempDate);
			record.setClose((float) (12.45 + i));
			retList.add(record);
		}
		
        
		return retList;
        
    }
	
	@ResponseBody //SpringMVC return json data pattern
	@RequestMapping(value="/getStockData2/{stockName}",method=RequestMethod.GET)
    public void getStockData2(@PathVariable String stockName)
    {
		
		System.out.println("-----------");
		
		List<StockRecord> retList = new ArrayList<StockRecord>();
		
		StockRecord record = new StockRecord();
		record.setStockName("testStockName");
		record.setTdate(new Date());
		record.setClose(123.45f);
		
		retList.add(record);
        
        
    }
	
}
