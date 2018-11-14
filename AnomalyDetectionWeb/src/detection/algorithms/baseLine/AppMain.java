package detection.algorithms.baseLine;

import java.util.HashMap;
import java.util.List;

import detection.beans.SortValueBean;
import detection.beans.StockRecord;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;

public class AppMain
{
	public String fileName = "data_real.xlsx";
	public String sheetNameRank = "baseLine_top";
	public String sheetNameOrig = "price";
	
	public static void main(String[] args)
	{
		AppMain appMain = new AppMain();
		appMain.sheetNameOrig="price";
		appMain.process_new();
		
	}

	public void process_new()
	{
		IBaseLineDBHelper blDBHelper = new BaseLineDBHelper();
		BaseLineExcelFileHelper blFile = new BaseLineExcelFileHelper();
	
		//first import excel stock records to stockrecords db table
		blFile.importStock(fileName,sheetNameOrig);
		
		//update the stockname table based on the stockrecords table
		blDBHelper.updateStockNameFromStockRecords();
		
		//get unique stockname
		List<String> stockNameList =  blDBHelper.getStockNames();
		
		//get all transaction dates
		List<String> transactonDates =  blDBHelper.getTransactionDates();
		
		//divide windows
		//blDBHelper.divideStockData(stockNameList, transactonDates);
		blDBHelper.tranformToDivideWindow(stockNameList, transactonDates);
		
		//identified peer group
		HashMap<String, List<SortValueBean>> pgMap = blDBHelper.createPeerGroup(stockNameList);

		//save peer group into table
		blDBHelper.savePeerGroup(pgMap);
		
		//the distance is divided by 100, because it is too big to calculate proxi
		blDBHelper.updateProx();
		
		//update table "bl_peergroups"."weight"
		blDBHelper.updateWeight(stockNameList);
		
		////update table "bl_peergroups_records"."weight_mean"
		blDBHelper.updatePeerGroupWeightMean(stockNameList,transactonDates);

		//
		blDBHelper.calculateExtremes(stockNameList);
	
		//get top 10 extreme values
		List<PeerGroupRecordBean> topRecord = blDBHelper.getTopPeerGroupRecords();
		
		//write to excel
		blFile.writeExtremeValuesToExcel(fileName, sheetNameRank, topRecord);
		
		System.out.println("finished!");
	}
	
	
	
	/*
	public void process()
	{
		IDatabaseHelper dbHelper = new DatabaseHelperImpl();
		
		IblAlgorithms bl = new blAlgorithms();
		
		blExcelFileHelper blFile = new blExcelFileHelper();

		//blFile.importStock("temp.xlsx");
		
		//blFile.importStockName();
		
		bl.divideStockData();
		
		List<String> stockNames = dbHelper.getStockNamesFromDivideWindows();
		
		HashMap<String, List<SortValueBean>> pgMap = bl.createPeerGroup(stockNames);
		
		List<Integer> divdWindows = dbHelper.getDividedWindows();
		
		List<HashMap<String,PeerGroup>> newPGMap = bl.updatePeerGroup(divdWindows, pgMap);
		
		bl.savePeerGroup(newPGMap);
		
		bl.updateWeights();
		
		bl.updateMeanValueWithWeights();
		
		bl.processCellsDistance();
		
		System.out.println("==finished=============");
	}
	*/
	
	public void test()
	{
		//acgl,abc,ahl,abg,agu,abt,ago,adc,ajg,abax
		//acc,abg,abc,ahl,agu,abt,adc,ago,abax,akr
		IDatabaseHelper dbHelper = new DatabaseHelperImpl();
		List<StockRecord> lstA = dbHelper.getRecordByName("acc");//acgl,abc
		List<StockRecord> lstB = dbHelper.getRecordByName("acgl");
		List<StockRecord> lstC = dbHelper.getRecordByName("abc");
		
		List<StockRecord> lstAx = dbHelper.getRecordByName("ahl");
		List<StockRecord> lstBx = dbHelper.getRecordByName("abg");
		List<StockRecord> lstCx = dbHelper.getRecordByName("agu");
		
		//100 records each list
		lstA.addAll(lstAx);
		lstB.addAll(lstBx);
		lstC.addAll(lstCx);
		
		System.out.println("the Dimentions are :"+lstA.size());
		double a[] = new double[lstA.size()];
		double b[] = new double[lstB.size()];
		double c[] = new double[lstC.size()];
		for(int i=0;i<lstA.size();i++)
		{
			a[i] = lstA.get(i).getClose();
			b[i] = lstB.get(i).getClose();
			c[i] = lstC.get(i).getClose();
			
		}
		
		IBaseLineAlgorithms bl = new BaseLineAlgorithms();
		System.out.println( "Distance of a and b:" + bl.euclideanDistance(a, b));
		System.out.println( "Distance of a and c:" + bl.euclideanDistance(a, c));
		System.out.println( "Distance of b and c:" + bl.euclideanDistance(b, c));
	
		
	}

}
