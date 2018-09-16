package detection.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import detection.beans.JaccardBean;
import detection.beans.StockName;
import detection.beans.StockWithNNBean;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.fileOperation.FileHelper;
import detection.fileOperation.IFileHelper;
import detection.utility.MyConstants;

public class MyApp
{
	private static IFileHelper fileHelper;
	private static IDatabaseHelper dbHelper;
	private static IAlgorithmsHelper algHelper;
	
	public static void main(String[] args)
	{
		fileHelper = new FileHelper();
        dbHelper   = new DatabaseHelperImpl();
        algHelper  = new AlgorithmsHelper();
        
        MyApp myApp = new MyApp();
        myApp.process("Energy");
        
	}
	
	private void process(String sector)
	{
		//get all stock from stockName table
		List<StockName> stockNames = dbHelper.getStocksBySector(sector);
		
		//get all trading dates
		List<Date> tradingDates = dbHelper.getTradingDates(stockNames.get(0).getSymbol());
		
		//get sub testing dates
		List<Date> sub_tradingDates = new ArrayList<>();
		int d_start = MyConstants.TRADE_SUBDAYS_START_INDEX;
		int d_size  = MyConstants.TRADE_SUBDAYS_LENGTH;
		for(int loop=d_start; loop<d_size; loop++)
		{
			sub_tradingDates.add(tradingDates.get(loop));
		}
		
		//begin to calculate for each stock
		for (StockName snBean : stockNames)
		{
			//get a list which contains neighbors of a stock for all trading dates
			List<StockWithNNBean> lstNeighborsBeans = algHelper.getStock_NNForDuration(snBean, sub_tradingDates);
			
			//calculate jaccard coefficient of one stock for all trading dates
			List<JaccardBean> lstJaccard = algHelper.calculateJaccardForAStock(lstNeighborsBeans);
			for(int i=0;i<lstJaccard.size();i++)
			{
				JaccardBean jb = lstJaccard.get(i);
				System.out.print("["+jb.getTdate() + " : "+jb.getJaccard() +"] ");
			}
			
			System.out.println();
		}
		
	}
	
	
}
