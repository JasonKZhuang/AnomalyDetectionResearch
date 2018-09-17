package detection.algorithms2;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import detection.beans.StockName;
import detection.db.IDatabaseHelper;
import detection.fileOperation.IFileHelper;

public class MyAlgorithm2
{

	private String path = "D:\\GitHub\\Data";

	private static IFileHelper fileHelper;

	private static IDatabaseHelper dbHelper;

	private List<RecordBean> recordList = new ArrayList<>();

	public MyAlgorithm2()
	{
		String d = "d1";
		double[] price1 = new double[] { 47.47d, 45.88d, 52.16d };
		double[] price2 = new double[] { 48.11d, 47.33d, 53.07d };
		double[] price3 = new double[] { 48.85d, 47.69d, 53.38d };
		double[] price4 = new double[] { 48.79d, 49.76d, 53.53d };
		double[] price5 = new double[] { 49.48d, 48.79d, 53.36d };
		double[] price6 = new double[] { 50.58d, 48.38d, 54.69d };
		double[] price7 = new double[] { 51.35d, 48.34d, 53.98d };
		double[] price8 = new double[] { 50.95d, 48.52d, 54.00d };
		double[] price9 = new double[] { 51.76d, 47.86d, 52.88d };
		double[] price10 = new double[] { 50.59d, 48.76d, 52.29d };
		double[] price11 = new double[] { 50.50d, 52.71d, 47.17d };
		double[] price12 = new double[] { 49.48d, 51.80d, 46.53d };
		double[] price13 = new double[] { 49.77d, 46.82d, 51.97d };
		double[] price14 = new double[] { 49.98d, 46.87d, 52.30d };
		double[] price15 = new double[] { 49.48d, 46.09d, 52.06d };

		RecordBean bean1 = new RecordBean();
		RecordBean bean2 = new RecordBean();
		RecordBean bean3 = new RecordBean();
		RecordBean bean4 = new RecordBean();
		RecordBean bean5 = new RecordBean();
		RecordBean bean6 = new RecordBean();
		RecordBean bean7 = new RecordBean();
		RecordBean bean8 = new RecordBean();
		RecordBean bean9 = new RecordBean();
		RecordBean bean10 = new RecordBean();
		RecordBean bean11 = new RecordBean();
		RecordBean bean12 = new RecordBean();
		RecordBean bean13 = new RecordBean();
		RecordBean bean14 = new RecordBean();
		RecordBean bean15 = new RecordBean();
		bean1.setD("d1");
		bean2.setD("d2");
		bean3.setD("d3");
		bean4.setD("d4");
		bean5.setD("d5");
		bean6.setD("d6");
		bean7.setD("d7");
		bean8.setD("d8");
		bean9.setD("d9");
		bean10.setD("d10");
		bean11.setD("d11");
		bean12.setD("d12");
		bean13.setD("d13");
		bean14.setD("d14");
		bean15.setD("d15");

		bean1.setArrPrice(price1);
		bean2.setArrPrice(price2);
		bean3.setArrPrice(price3);
		bean4.setArrPrice(price4);
		bean5.setArrPrice(price5);
		bean6.setArrPrice(price6);
		bean7.setArrPrice(price7);
		bean8.setArrPrice(price8);
		bean9.setArrPrice(price9);
		bean10.setArrPrice(price10);
		bean11.setArrPrice(price11);
		bean12.setArrPrice(price12);
		bean13.setArrPrice(price13);
		bean14.setArrPrice(price14);
		bean15.setArrPrice(price15);

		recordList.add(bean1);
		recordList.add(bean2);
		recordList.add(bean3);
		recordList.add(bean4);
		recordList.add(bean5);
		recordList.add(bean6);
		recordList.add(bean7);
		recordList.add(bean8);
		recordList.add(bean9);
		recordList.add(bean10);
		recordList.add(bean11);
		recordList.add(bean12);
		recordList.add(bean13);
		recordList.add(bean14);
		recordList.add(bean15);

	}

	public static void main(String[] args)
	{
		MyAlgorithm2 app = new MyAlgorithm2();

		List<SimilarityBean> lstBeans = app.process();
		
		System.out.println(lstBeans.get(11));

	}

	private List<SimilarityBean> process()
	{
		List<SimilarityBean> retList = new ArrayList<>();

		// outer
		for (int i = 0; i < recordList.size(); i++)
		{
			RecordBean bean = recordList.get(i);
			double[] arrA = bean.getArrPrice();
			SimilarityBean sBean = new SimilarityBean();
			sBean.setBootName(bean.getD());
			double[] arrSim = new double[recordList.size()];
			// inner
			int t = 0;
			for (int j = 0; j < recordList.size(); j++)
			{
				RecordBean item = recordList.get(j);
				double[] arrB = item.getArrPrice();

				//
				double f_sim = 0;
				f_sim = cosineSimilarity(arrA, arrB);
				arrSim[t] = f_sim;
				//
				t = t + 1;
			}
			
			sBean.setSimilarity(arrSim);

			retList.add(sBean);
		}
		
		return retList;

	}

	private double cosineSimilarity(double[] vectorA, double[] vectorB) 
	{
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) 
	    {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }
	    
	   double retValue =  dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	   retValue = round(retValue, 6, BigDecimal.ROUND_HALF_UP);
	   return retValue;
	}
	
	public static double round(double value, int scale, int roundingMode) 
	{   
        BigDecimal bd = new BigDecimal(value);   
        bd = bd.setScale(scale, roundingMode);   
        double d = bd.doubleValue();   
        bd = null;   
        return d;   
    }   
}
