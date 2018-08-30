package detection.algorithms.kim;

import java.util.HashMap;
import java.util.List;

import detection.beans.SortValueBean;
import detection.beans.StockRecord;
import detection.beans.kim.PeerGroup;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;

public class AppMain
{

	public static void main(String[] args)
	{
		
		AppMain appMain = new AppMain();
		appMain.test();
		//appMain.process();
	}
	
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
		
		IKimAlgorithms kim = new KimAlgorithms();
		System.out.println( "Distance of a and b:" + kim.euclideanDistance(a, b));
		System.out.println( "Distance of a and c:" + kim.euclideanDistance(a, c));
		System.out.println( "Distance of b and c:" + kim.euclideanDistance(b, c));
	
		
	}
	
	public void process()
	{
		IDatabaseHelper dbHelper = new DatabaseHelperImpl();
		
		IKimAlgorithms kim = new KimAlgorithms();
		/*
		kim.divideStockData();
		
		List<String> stockNames = dbHelper.getStockNamesFromDivideWindows();
		
		HashMap<String, List<SortValueBean>> pgMap = kim.createPeerGroup(stockNames);
		
		List<Integer> divdWindows = dbHelper.getDividedWindows();
		
		List<HashMap<String,PeerGroup>> newPGMap = kim.updatePeerGroup(divdWindows, pgMap);
		
		kim.savePeerGroup(newPGMap);
		
		kim.updateWeights();
		
		kim.updateMeanValueWithWeights();
		*/
		kim.processCellsDistance();
		
		System.out.println("==finished=============");
	}

}
