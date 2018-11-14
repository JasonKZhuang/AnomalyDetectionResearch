package detection.algorithms.kaizhi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import detection.utility.DateUtil;
import detection.utility.UtilsZ;

public class AlgorithmsKZ
{
	public List<SimilarityBean> calculateSimilarity(List<RecordBean> recordList)
	{
		List<SimilarityBean> retList = new ArrayList<>();

		// outer
		for (int i = 0; i < recordList.size(); i++)
		{
			RecordBean bean = recordList.get(i);
			double[] arrA = bean.getArrPrice();// used to compaire for multi dementions
			
			SimilarityBean sBean = new SimilarityBean();
			sBean.setBootName(bean.getMyDate());
			
			double[] arrSimValue = new double[recordList.size()];
			String[] arrSimDate  = new String[recordList.size()];
			List<SimDay> lstSimDays = new ArrayList<>();
			
			// inner
			for (int j = 0; j < recordList.size(); j++)
			{
				RecordBean item = recordList.get(j);
				
				double[] arrB = item.getArrPrice();
				String simDate = item.getMyDate();
				arrSimDate[j]  = simDate;
				//
				double f_sim = cosineSimilarity(arrA, arrB);
				arrSimValue[j] = f_sim;
				//
				SimDay  simDay = new SimDay();
				simDay.setSimDate(simDate);
				simDay.setSimValue(f_sim);
				lstSimDays.add(simDay);
			}
			//
			sBean.setSimilarity(arrSimValue);
			sBean.setSim_date(arrSimDate);
			sBean.setSimDays(lstSimDays);

			retList.add(sBean);
		}

		return retList;

	}

	public double cosineSimilarity(double[] vectorA, double[] vectorB)
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
		if (normA == 0 || normB == 0)
		{
			return 0;
		}

		double retValue = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
		try
		{
			retValue = UtilsZ.round(retValue, 6, BigDecimal.ROUND_HALF_UP);
		} catch (Exception exp)
		{
			System.out.println(exp.getMessage());
		}
		
		return retValue;
	}

	public List<KSTestStatisticBean> ksTest(List<SimilarityBean> listBeans)
	{
		KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
		List<KSTestStatisticBean> retList = new ArrayList<>();
		for(int i=0;i<listBeans.size();i++)
		{
			SimilarityBean outerBean = listBeans.get(i);
			String outDate = outerBean.getBootName();
			double[] x = outerBean.getSimilarity();
			//You reject the null hypothesis that the two samples were drawn from the same distribution 
			//if the p-value is less than your significance level.
			//The two-sample KS test for the null hypothesis that the data sets
		    //are drawn from the same distribution. 
			//Small values of p-value show that 
			//the cumulative distribution function of x is significantly different from that of y. 
			double sum_p_value = 0.0d;
			int reject_count = 0;
			for(int j=0;j<listBeans.size();j++)
			{
				SimilarityBean innerBean = listBeans.get(j);
				String innerDate = innerBean.getBootName();
				if (outDate.equals(innerDate))
				{
					continue;
				}
				double[] y = innerBean.getSimilarity();
				/*
				int xn = x.length;
				int ym = y.length;
				double[] x1 = Arrays.copyOf(x, x.length);
				double[] y1 = Arrays.copyOf(y, y.length);
				double s = (double)(xn+ym)/(xn*ym);
				double D_alpha = KZConstants.KSTest_C_ALPHA * Math.pow(s, 0.5) ;
				double D_statistic = ksTest.kolmogorovSmirnovStatistic(x1, y1);
				if (D_statistic > D_alpha)
				{
					count_temp = count_temp + 1;//reject null hypothesis
				}else
				{
					;
				}
				*/
				
				/**
				 * http://blog.minitab.com/blog/understanding-statistics/what-can-you-say-when-your-p-value-is-greater-than-005
				 * In the majority of analyses, 
				 * an alpha of 0.05 is used as the cutoff for significance. 
				 * If the p-value is less than 0.05,we reject the null hypothesis that there's no difference between the means and conclude that a significant difference does exist. 
				 * If the p-value is larger than 0.05, we cannot conclude that a significant difference exists.
				 * That's pretty straightforward, right?  Below 0.05, significant(reject). Over 0.05, not significant.  
				 */
				double[] x2 = Arrays.copyOf(x, x.length);
				double[] y2 = Arrays.copyOf(y, y.length);
				double p_value = ksTest.kolmogorovSmirnovTest(x2, y2);
				if (p_value < KZConstants.KSTest_ALPHA)
				{
					sum_p_value = sum_p_value + p_value;
					reject_count = reject_count + 1;
				}
			}
			
			KSTestStatisticBean bean = new KSTestStatisticBean();
			bean.setDateName(outDate);
			bean.setSumPValue(sum_p_value);
			bean.setRejectCount(reject_count);
			retList.add(bean);
		}
		
		//sort the list bean
		Collections.sort(retList, new KSTestListComparator("rejectCount", "DESC"));//p-value
		return retList;
	}

	public String[] getAllDateFromList(List<SimilarityBean> simList )
	{
		String[] retValue = new String[simList.size()];
		for(int i=0;i<simList.size();i++)
		{
			SimilarityBean bean = simList.get(i);
			retValue[i] = bean.getBootName();
		}
		return retValue;
	}
	
	
	private void KSTestTest(List<SimilarityBean> sim_listBeans)
	{
		double [] x = new double[] {1,0.99969,0.999422,0.999274,0.999128,0.999071,0.998727,0.998548,0.997955,0.998164,0.997545,0.996919,0.996291,0.99607,0.99611,0.995201,0.994693,0.992104,0.991793,0.991107,0.990648,0.983217,0.981228,0.980933,0.983367,0.983024,0.985378,0.985529,0.984627,0.983715,0.984565,0.987128,0.986122,0.985376,0.984339,0.984005,0.984526,0.983694,0.986228,0.984518,0.984376,0.984658,0.982759,0.982389,0.984268,0.982756,0.980428,0.978824,0.977433,0.976924,0.974968,0.975898,0.976753,0.979914,0.981302,0.981434,0.977494,0.977234,0.97539,0.975694,0.977586,0.977352,0.977207,0.978311,0.976367,0.977057,0.978156,0.974984,0.97438,0.975557,0.975701,0.977767,0.977217,0.976656,0.975484,0.9748,0.974274,0.973543,0.97258,0.971695,0.971471,0.970128,0.969452,0.971388,0.971257,0.971512,0.971877,0.9708,0.97236,0.971593,0.969265,0.966959,0.965974,0.96263,0.964953,0.967461,0.96503,0.961925,0.960108,0.955865,0.956262,0.954337,0.950897,0.948162,0.949683,0.947641,0.947558,0.951116,0.952432,0.949866,0.94889,0.952406,0.954064,0.954445,0.953362,0.956636,0.953488,0.949464,0.950058,0.948601,0.949089,0.948421,0.948673,0.945095,0.942835,0.943787,0.946047,0.943644,0.946005,0.945056,0.948404,0.946646,0.947355,0.946869,0.946399,0.943714,0.943059,0.944833,0.942259,0.939851,0.939216,0.938091,0.9362,0.935068,0.937263,0.940272,0.943639,0.94049,0.940392,0.936643,0.93318,0.934456,0.9361,0.936016,0.935774,0.934331,0.933677,0.93647,0.935987,0.936913,0.936973,0.933826,0.929957,0.92509,0.921077,0.917739,0.920495,0.920692,0.916971,0.919124,0.920532,0.922232,0.919377,0.920356,0.916249,0.918098,0.917369,0.912784,0.915914,0.911864,0.906666,0.906122,0.906476,0.905659,0.907674,0.908577,0.903338,0.892492,0.895581,0.895759,0.894624,0.89072,0.894586,0.892466,0.89037,0.88478,0.886396,0.88438,0.883999,0.879777,0.879609,0.896483,0.896756,0.894006,0.893955,0.891851,0.886432,0.885979,0.887141,0.89123,0.890461,0.884928,0.883606,0.878938,0.878013,0.873514,0.872835,0.865874,0.872245,0.869295,0.877201,0.876694,0.879948,0.872867,0.872236,0.877414,0.874747};
		double [] y = new double[] {0.999422,0.999728,1,0.999814,0.999442,0.999331,0.998779,0.998646,0.997878,0.998068,0.99782,0.996906,0.996177,0.996189,0.995931,0.995497,0.995056,0.99183,0.991222,0.990249,0.989399,0.980979,0.978893,0.978773,0.981556,0.981332,0.984194,0.984371,0.983737,0.982772,0.983604,0.986308,0.985295,0.984493,0.983363,0.983087,0.984017,0.983259,0.985966,0.984055,0.983927,0.984293,0.982859,0.982412,0.98417,0.982577,0.980238,0.978785,0.977357,0.977086,0.974882,0.975576,0.976304,0.979509,0.981172,0.981243,0.977624,0.977406,0.975443,0.975906,0.977833,0.977512,0.977253,0.978251,0.9766,0.977289,0.978496,0.975338,0.974601,0.975018,0.975435,0.977631,0.977252,0.97664,0.975535,0.974818,0.974189,0.973645,0.97272,0.97185,0.971465,0.970348,0.969713,0.971396,0.971112,0.970991,0.97144,0.970274,0.971861,0.970883,0.968155,0.965471,0.964833,0.961139,0.963542,0.966049,0.963349,0.96021,0.958491,0.954037,0.954541,0.952554,0.949121,0.946266,0.947836,0.945968,0.945667,0.949562,0.951098,0.948271,0.947023,0.950491,0.952303,0.95251,0.951646,0.955009,0.951448,0.947418,0.948087,0.946877,0.947617,0.946639,0.946922,0.943229,0.940973,0.942051,0.944243,0.941793,0.944298,0.943017,0.946505,0.944723,0.945524,0.945383,0.944851,0.94211,0.941439,0.943176,0.940524,0.938129,0.937319,0.936141,0.934251,0.932825,0.935168,0.938356,0.941796,0.938456,0.938279,0.934592,0.931226,0.932564,0.934674,0.934528,0.934036,0.93255,0.931975,0.935022,0.934552,0.935696,0.935805,0.93242,0.928495,0.92365,0.919487,0.915941,0.91857,0.918556,0.914884,0.917064,0.918519,0.920234,0.917356,0.918145,0.914091,0.915868,0.915068,0.91037,0.913357,0.909352,0.90425,0.903844,0.90424,0.903521,0.905528,0.906546,0.901162,0.890321,0.893424,0.893631,0.892318,0.88826,0.892446,0.890317,0.888124,0.882618,0.884253,0.882141,0.882071,0.878144,0.877946,0.89534,0.895373,0.892385,0.892337,0.889959,0.884496,0.883943,0.885229,0.889262,0.888737,0.88329,0.881822,0.877065,0.876377,0.871649,0.871138,0.864186,0.870567,0.867468,0.875903,0.875442,0.87858,0.871477,0.870946,0.876,0.873411};
		
		for(int i=0;i<sim_listBeans.size();i++)
		{
			SimilarityBean outer_bean = sim_listBeans.get(i);
			x = outer_bean.getSimilarity();					
			for(int j=0;j<sim_listBeans.size();j++)
			{
				SimilarityBean inner_bean = sim_listBeans.get(j);
				if (outer_bean.getBootName().equals(inner_bean.getBootName()))
				{
					continue;
				}
				y = inner_bean.getSimilarity();
				KSTestTest(x, y);
			}
			System.out.println("====================================");
		}
		
		
		
		
	}

	/**
	 * the null hypothesis means that the data sets are drawn from the same distribution
	 * The null hypothesis is H0: both samples come from a population with the same distribution.
	 * The Kolmogorov–Smirnov test may also be used to test 
	 * whether two underlying one-dimensional probability distributions differ. 
	 * 
	 * if the statistic value great then critical value 
	 * 	then we reject the  null hypothesis 
	 * (two distributions are not same)
	 * 
	 * if the statistic value less then critical value 
	 * 	then we accept the  null hypothesis
	 * (two distributions are same)
	 * 
	 * 
	 * @param x
	 * @param y
	 */
	private void KSTestTest(double[] x,double[] y)
	{
		int xn = x.length;
		int ym = y.length;
		
		KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
		double statistic = ksTest.kolmogorovSmirnovStatistic(x, y);
		double s = (double)(xn+ym)/(xn*ym);//43/420
		double DAlpha = 1.36 * Math.pow(s, 0.5) ;
		System.out.print("D(a):\t"+DAlpha);
		System.out.print("\t");
		System.out.print("st:\t"+statistic);
		System.out.println();
	}

	public List<SimilarityRecord> getTopSimilarityDateWithValue(List<SimilarityBean> lst, int knn)
	{
		List<SimilarityRecord> retList = new ArrayList<>();
		List<SimilarityRecord> smRecords = new ArrayList<>();
		try
		{
			for(int i=0;i<lst.size();i++)
			{
				SimilarityBean sBean = lst.get(i);
				String selfDate  = sBean.getBootName();
				Date tDate = DateUtil.convertStringToDate(selfDate);
				List<SimDay> descList = sBean.getSimDays();
				//
				try
				{
					Collections.sort(descList, new Comparator<SimDay>() 
					{
						@Override
						public int compare(SimDay o1, SimDay o2) 
						{
							long v1 = (long) (o1.getSimValue().doubleValue() * 1000000);
							long v2 = (long) (o2.getSimValue().doubleValue() * 1000000);
							//System.out.println(v1 + "------------------" + v2);
							if (v1 < v2)
							{
								return 1;
							}else if (v1==v2)
							{
								return 0;
							}else
							{
								return -1;
							}
						}
					});
				}catch(Exception exp)
				{
					System.out.println(exp.getMessage());
					exp.printStackTrace();
				}
				
				int knnCount = 0;
				for(int j=0; j<descList.size(); j++)
				{
					SimDay simDay = descList.get(j);
					
					Date simDate = DateUtil.convertStringToDate(simDay.getSimDate());
					double simValue = simDay.getSimValue();
					
					if (simDay.getSimDate().equals(selfDate))
					{
						continue;
					}
					
					if (knnCount==(knn-1))
					{
						SimilarityRecord record = new SimilarityRecord();
						record.setTdate(tDate);
						record.setSim_date(simDate);
						record.setSim_value(simValue);
						smRecords.add(record);
						break;
					}
					knnCount = knnCount + 1;
				}
			}
		}catch(Exception exp)
		{
			System.out.println(exp.getMessage());
			exp.printStackTrace();
		}
		
		//===================//
		double[] myValue = new double[smRecords.size()];
		for(int i=0;i<smRecords.size();i++)
		{
			myValue[i] = smRecords.get(i).getSim_value();
		}
		
		double mean 	= new Mean().evaluate(myValue);
		double sd 		= new StandardDeviation().evaluate(myValue);
		
		if (sd != 0)
		{
			NormalDistribution normalDistribution = new NormalDistribution(mean, sd);
			
			double cumValue = 0;
			for(int i=0;i<smRecords.size();i++)
			{
				SimilarityRecord sr = smRecords.get(i);
				
				cumValue = normalDistribution.cumulativeProbability(sr.getSim_value());
				if (cumValue <= 0.01 && sr.getSim_value()<0.998)
				{
					//System.out.println(sr.getSim_value());
					//System.out.println("="+DateUtil.getDate(sr.getTdate())+":" + sr.getSim_value());
					//System.out.println("===============================");
					retList.add(sr);
				}
				
				/*
				if (sr.getSim_value()< (mean - (3 * sd)))
				{
					retList.add(sr);
				}
				*/
			}
		}
		return retList;
	}
	
	//desc
	class MySimDayComparator implements Comparator<SimDay>
	{
		@Override
		public int compare(SimDay o1, SimDay o2)
		{
			double d1 = 0;
			double d2 = 0;
			if (o1 == null || o1.getSimValue() == null)
			{
				d1 = 0;
			}else
			{
				d1 = o1.getSimValue().doubleValue();
			}

			//
			if (o2 == null || o2.getSimValue() == null)
			{
				d2 = 0;
			}else
			{
				d2 = o2.getSimValue().doubleValue();
			}
			//
			if (d1 < d2)
			{
				return 1;
			}else 
			{
				return -1;
			}
			
		}
		
	}
	  
}
