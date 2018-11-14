package detection.algorithms.kaizhi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import detection.utility.DateUtil;
import detection.utility.UtilsZ;

public class MyAlgorithmMain
{

	private static String bootPath = "D:\\RMIT\\Master\\0Minor Thesis Project\\ExperimentData\\";

	private static IExcelHelper excelHelper;
	
	private static KZDBHelper kzDBHelper;
	
	private static AlgorithmsKZ kzAlgorithms;

	private static List<RecordBean> recordList = new ArrayList<>();
	
	private static List<StockBean> stockList = new ArrayList<>();
	
	private static Map<String, List<KSTestStatisticBean>> mapKSTest = new HashMap<>();
	
	public String fileName 		="";
	public String sheetNameOrig = "";
	public String sheetNameRank = "";
	
	public static int topNumk = 3;
	
	public MyAlgorithmMain()
	{
		excelHelper = new ExcelHelper();
		kzDBHelper  = new KZDBHelper();
		kzAlgorithms = new AlgorithmsKZ();
		
		this.fileName = "data_theory.xlsx";
		//this.fileName = "data_theory.xlsx";
		this.sheetNameOrig = "price1";
		this.sheetNameRank = "ourAlgorithm_top";
	}
	
	public static void main(String[] args)
	{
		MyAlgorithmMain main = new MyAlgorithmMain();
		
		main.processMain1(main.fileName);
		
		System.out.println("Finished");
	}
	
	//It is for thesis algorithm 2
	public void processMain1(String excelFileName)
	{
		File file = new File(bootPath + excelFileName);
		recordList = excelHelper.readExcelByPOI(file, sheetNameOrig);
		
		System.out.println("*************************************************************");
		boolean initFlag = true;
		
		//Algorithm 2
		List<CreditBean> creditList = cSU2D(recordList, topNumk, initFlag);
		for(int i=0;i<creditList.size();i++)
		{
			CreditBean cb = creditList.get(i);
			System.out.print("Date:"+cb.gettDate()+",Credit:"+cb.getCreditValue() +" | ");
		}
		System.out.println();
		
		System.out.println("*************************************************************");
		
		//Algorithm 3
		Map<CreditBean,List<String>> contribMap = uIS(recordList,creditList);
		
		//write to excel
		excelHelper.writeExtremeValuesToExcel(file, sheetNameRank, contribMap);
		
	}
	
	//it is for Algorithm 2: 
	public List<CreditBean> cSU2D(List<RecordBean> recordList, int topNum, boolean initFlag)
	{
		System.out.println("A2 is calculating similarity................");
		
		List<CreditBean> retList = new ArrayList<>();
		
		List<SimilarityBean> sim_listBeans = kzAlgorithms.calculateSimilarity(recordList);
			
		Map<String, Integer> creditHM = new HashMap<>();
		
		for (int knn=1; knn<=topNum; knn++ )
		{
			//System.out.println("Calculating Algorithm2 for topNum:" + knn);
			
			//List<SimilarityRecord> topSimilarityList = kzDBHelper.getTopSimilarityDateWithValue(dates,knn,topNum);
			List<SimilarityRecord> topSimilarityList = kzAlgorithms.getTopSimilarityDateWithValue(sim_listBeans,knn);
			
			for(SimilarityRecord simRecord :topSimilarityList)
			{
				String  d = DateUtil.getDate(simRecord.getTdate());
				Integer v = creditHM.get(d);
				if (v == null || v == 0)
				{
					creditHM.put(d, 1);
				}else
				{
					creditHM.put(d, v + 1);
				}
			}
		}
		//
		Map<String, Integer> sortedMap = UtilsZ.sortByValue(creditHM, "DESC");
		//
		//int tempValue = 0;
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet())
		{
			String k = entry.getKey();
			int    v = entry.getValue();
			/*
			if (tempValue !=0 && tempValue != v)
			{
				break;
			}
			tempValue = v;
			*/
			CreditBean cb = new CreditBean();
			cb.settDate(k);
			cb.setCreditValue(v);
			retList.add(cb);
		}
		return retList;
		///print for debug
		/*
		if (retList.size()>0)
		{
			for(int i=0;i<retList.size();i++)
			{
				CreditBean cb = retList.get(i);
				System.out.print("Date:"+cb.gettDate()+",Credit:"+cb.getCreditValue() +" | ");
			}
			System.out.println();
			//
			CreditBean cb0 = retList.get(0);
			for(int i=0;i<sim_listBeans.size();i++)
			{
				SimilarityBean simBean = sim_listBeans.get(i);
				if (cb0.gettDate().equals(simBean.getBootName()))
				{
					System.out.print("The similarity of the highest credit date are:\n"+simBean.getBootName()+"=>");
					double[] dd = simBean.getSimilarity();
					for(int j=0;j<dd.length;j++)
					{
						System.out.print(dd[j]+",");
					}
					System.out.println();
				}
			}
		}
		*/
	}
	
	//it is for Algorithm 3: Unusual Individual Stock (UIS)
	public Map<CreditBean,List<String>> uIS(List<RecordBean> argRecordList,List<CreditBean> creditList)
	{
		Map<CreditBean,List<String>> retMap = new HashMap<>();
		boolean initFlag = false;
		
		List<String> allDates = constructDateList(argRecordList);
		List<StockSeries> sortedStockSeries = constructSeriewList(argRecordList);
		
		//every day in the credit list
		for(int i=0; i< creditList.size(); i++ )
		{
			List<String> retList = new ArrayList<>();
			
			CreditBean oldCB = creditList.get(i);
			String oldDate = oldCB.gettDate();
			int    oldCred = oldCB.getCreditValue();
			System.out.println(">>>Calculating Algorithm3 for Date:" + oldDate);
			
			//DESC sort stocks based on (price-mean)/std
			//System.out.println("DESC sorting stocks based on (price-mean)/std ...");
			sortSeries(sortedStockSeries , allDates, oldCB);
			
			//
			boolean removeFlag = false;
			for(int removeNum=1; removeNum < sortedStockSeries.size(); removeNum++)
			{
				removeFlag = false;
				System.out.println("Remove top " + removeNum + " stocks , and reConstruct Record List...");
				/*
				for(int tt =0;tt<removeNum;tt++)
				{
					System.out.println(">>>>>> the " + tt + " is removed :" + sortedStockSeries.get(tt).getStockName());
				}
				*/
				List<RecordBean> newRecordList = reConstructRecordList(allDates, sortedStockSeries, removeNum);
				List<CreditBean> newCreditList = cSU2D(newRecordList, topNumk, initFlag);
				
				for(int t=0;t<newCreditList.size();t++)
				{
					CreditBean newCB = newCreditList.get(t);
					String newDate = newCB.gettDate();
					int    newCred = newCB.getCreditValue();
					if (oldDate.equals(newDate) && oldCred == newCred)
					{
						removeFlag = true;
						break;
					}
				}
				
				if (removeFlag == false)
				{
					for(int rmIdx=0; rmIdx<removeNum; rmIdx++)
					{
						StockSeries ss = sortedStockSeries.get(rmIdx);
						retList.add(ss.getStockName());
					}
					break;
				}
				
			}
			
			System.out.println("The removement for Date " + oldDate + " finished.");
			retMap.put(oldCB, retList);
		}
		
		return retMap;
		
	}
		
	private List<RecordBean> reConstructRecordList(List<String> allDates
			, List<StockSeries> sortedStockSeries, int popNum)
	{
		int arrSize = allDates.size();
		//copy deeply
		List<StockSeries> newSeriesList = new ArrayList<>();
		for(int i=0; i<sortedStockSeries.size(); i++)
		{
			StockSeries oldBean = sortedStockSeries.get(i);
			StockSeries newBean = new StockSeries(oldBean.getStockName(), arrSize);
			newBean.settDate(oldBean.gettDate());
			newBean.setPrices(oldBean.getPrices());
			newBean.setStd(oldBean.getStd());
			newBean.setMeanValue(oldBean.getMeanValue());
			newBean.setR(oldBean.getR());
			newSeriesList.add(newBean);
		}
		
		//newSeriesList.remove(popNum - 1);
		//remove top num stocks
		for(int i=0;i<popNum;i++)
		{
			newSeriesList.remove(0);
		}
		
		List<RecordBean> returnRecordList = new ArrayList<>();
		int numOfSeries = newSeriesList.size();
		for(int i=0;i<allDates.size();i++)
		{
			String tDate = allDates.get(i);
			RecordBean newBean = new RecordBean(tDate,numOfSeries);
			double[] arrPrice = new double[numOfSeries];
			String[] arrStocks = new String[numOfSeries];
			for(int seriesJ=0; seriesJ<newSeriesList.size(); seriesJ++)
			{
				StockSeries ss = newSeriesList.get(seriesJ);
				arrStocks[seriesJ] = ss.getStockName();
				arrPrice[seriesJ]  = ss.getPrices()[i];
			}
			newBean.setArrStocks(arrStocks);
			newBean.setArrPrice(arrPrice);
			returnRecordList.add(newBean);
		}
		
		return returnRecordList;
	}

	private void sortSeries(List<StockSeries> stockSeries, List<String> allDates, CreditBean cb)
	{
		//seek for the day index of credit date
		String tempDay = "";
		int dayIndex = 0 ;
		for(int i=0;i<allDates.size();i++)
		{
			tempDay = allDates.get(i);
			if (cb.gettDate().equals(tempDay))
			{
				dayIndex = i;
				break;
			}
		}
		
		//calculate meanValue and Standard Deviation
		for(int i=0;i<stockSeries.size();i++)
		{
			StockSeries tempObj = stockSeries.get(i);
			double[] priceValue	= tempObj.getPrices();
			double std 			= new StandardDeviation().evaluate(priceValue);
			double meanValue 	= new Mean().evaluate(priceValue);
			tempObj.setStd(std);
			tempObj.setMeanValue(meanValue);
		}
		
		//calculate rank value 
		for(int i=0; i<stockSeries.size(); i++)
		{
			StockSeries tempObj = stockSeries.get(i);
			double price     = tempObj.getPrices()[dayIndex];
			double std 		 = tempObj.getStd();
			double mVal 	 = tempObj.getMeanValue();
			double r 		 = 0d;
			if (std !=0)
			{
				r = Math.abs((price - mVal))/std;
			}
			tempObj.setR(r);
		}
		
		//sort the list bean
		Collections.sort(stockSeries, new StockSeriesComparator("DESC"));
		
	}

	private List<String> constructDateList(List<RecordBean> recordList)
	{
		List<String> retList = new ArrayList<>();
		for(int i=0;i<recordList.size();i++)
		{
			retList.add(recordList.get(i).getMyDate());
		}
		return retList;
	}
	
	private List<StockSeries> constructSeriewList(List<RecordBean> recordList)
	{
		List<StockSeries> retValue = new ArrayList<>();
		int days = recordList.size();
		
		String[] stocks = recordList.get(0).getArrStocks();
		for(int i=0;i<stocks.length;i++)
		{
			StockSeries s = new StockSeries(stocks[i], days);
			for(int j=0; j<recordList.size(); j++)
			{
				RecordBean tempRB = recordList.get(j);
				s.setPrices(j, tempRB.getArrPrice()[i]);
				s.settDate(j , tempRB.getMyDate());
			}
			retValue.add(s);
		}
		return retValue;
	}
	
	private class StockSeriesComparator implements Comparator<StockSeries> 
	{
		private String sortType="";
		
		public StockSeriesComparator(String argSortType)
		{
			this.sortType = argSortType;
		}
		
		public int compare(StockSeries a, StockSeries b) 
	    {
	    	if (sortType.equals("ASC") || sortType.equals(""))
	    	{
	    		return a.getR() < b.getR() ? -1 : a.getR() == b.getR() ? 0 : 1;
	    	}else 
	    	{
	    		return b.getR() < a.getR() ? -1 : b.getR() == a.getR() ? 0 : 1;
	    	}
	    }
	}
	
	public void processMain2(String excelFileName)
	{
		AlgorithmsKZ alKZ = new AlgorithmsKZ();
		
		File file = new File(bootPath + excelFileName);
		
		recordList = excelHelper.readExcelByPOI(file,sheetNameOrig);
		
		List<SimilarityBean> sim_listBeans = alKZ.calculateSimilarity(recordList);
		
		String[] dates = alKZ.getAllDateFromList(sim_listBeans);
		
		kzDBHelper.saveSimilarity(sim_listBeans,dates);
		
		List<SimilarityRecord> similarityList = kzDBHelper.getTopSimilarityDateWithValue(dates);
		
		excelHelper.writSimilarityRecord(file,sheetNameRank,similarityList);
		
	}
	
	
	
	
	private List<StockBean> getStockList(String fileName)
	{
		File file = new File(bootPath+fileName);
		stockList = excelHelper.readStocksFromExcel(file);
		return stockList;
	}
	
	private void testCompare()
	{
		List<KSTestStatisticBean> ksList = new ArrayList<>();
		
		KSTestStatisticBean a = new KSTestStatisticBean();
		a.setDateName("2012-01-01");
		a.setSumPValue(5.0);
		a.setRejectCount(1);
		ksList.add(a);
		
		KSTestStatisticBean b = new KSTestStatisticBean();
		b.setDateName("2013-03-03");
		b.setRejectCount(2);
		a.setSumPValue(3.0);
		ksList.add(b);
		
		//sort the list bean
		Collections.sort(ksList, new KSTestListComparator("rejectCount", "ASC"));//p-value
		
		for(int i=0;i<ksList.size();i++)
		{
			System.out.print(ksList.get(i).getDateName() + ":");
			System.out.print(ksList.get(i).getSumPValue() + ":");
			System.out.print(ksList.get(i).getRejectCount() + ":");
			System.out.println();
		}
	}
	
	private void processMainOld(String excelFileName)
	{
		File file = new File(bootPath+excelFileName);
		
		recordList = excelHelper.readExcelByPOI(file,"price");
		
		AlgorithmsKZ al = new AlgorithmsKZ();
		
		List<SimilarityBean> sim_listBeans = al.calculateSimilarity(recordList);
		
		String[] dates = al.getAllDateFromList(sim_listBeans);
		
		excelHelper.writeSimilarityToExcelSheet(file, "similarity", sim_listBeans);
		
		List<KSTestStatisticBean> ksListBeans = al.ksTest(sim_listBeans);
		
		mapKSTest.put(excelFileName, ksListBeans);
		
		excelHelper.writeKSTestToExcelSheet(file, ksListBeans);
		
		int lstMiddleIdx = ksListBeans.size()/2;
		
		for(int i=0;i<ksListBeans.size();i++)
		{
//			if (   (i>=0 && i<4) 
//				|| ( i>=(lstMiddleIdx-2) && i<=(lstMiddleIdx+2) )
//				|| ( i>= ksListBeans.size()- KZConstants.KSTest_BTTOM)
//				)
//			
//			KSTestStatisticBean bb = ksListBeans.get(i);
//			String dName = bb.getDateName();
//			if (dName.equals("2018-01-01") 
//				||dName.equals("2018-01-09")
//				||dName.equals("2018-01-18")
//				||dName.equals("2018-01-26")
//					)
//			
//			{
			if (i<10)
			{
				KSTestStatisticBean ksBean = ksListBeans.get(i);
				String ksDateName = ksBean.getDateName();
				for(int j=0;j<sim_listBeans.size();j++)
				{
					SimilarityBean simBean  = sim_listBeans.get(j);
					String simDateName = simBean.getBootName();
					
					if (simDateName.equals(ksDateName))
					{
						excelHelper.writeSingleDayToExcelSheet(file, simBean, dates);
						break;
					}
				}
			}
			
		}
		
	}
	private void divideSheet()
	{
		/*
		String newFileName="";
		String newFileNameWithPath="";
		main.getStockList(fileName);
		for(int i=0;i<stockList.size();i++)
		{
			newFileName =  stockList.get(i).getStockName() + ".xlsx";
			
			newFileNameWithPath = bootPath + newFileName;
			File file = new File(newFileNameWithPath);
			if (file.exists())
			{
				file.delete();
			}
			
			excelHelper.copyNewExcelWithLessOne(bootPath+fileName, newFileNameWithPath,i+2);
			
			main.processMain(newFileName);
		}
		
		List<KSTestStatisticBean> ksTestList = mapKSTest.get(fileName);
		KSTestStatisticBean topBeanMain = ksTestList.get(0);
		for (Map.Entry<String, List<KSTestStatisticBean>> entry : mapKSTest.entrySet()) 
		{
			String stockName = entry.getKey();
			if (stockName.equals(fileName))
			{
				continue;
			}
			List<KSTestStatisticBean> ksTestListStock = entry.getValue(); 
			KSTestStatisticBean topBeanStock = ksTestListStock.get(0);
			if (topBeanMain.getDateName().equals(topBeanStock.getDateName()))
			{
				;
			}else
			{
				anomalyList.add(topBeanStock.getDateName());
				System.out.println("Anomaly stock is " + stockName +" at " + topBeanMain.getDateName());
			}
			
			
		}
		*/
	}
	
}
