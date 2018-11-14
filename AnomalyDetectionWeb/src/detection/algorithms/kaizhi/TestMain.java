package detection.algorithms.kaizhi;

import java.math.BigDecimal;
import java.util.Arrays;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.text.similarity.CosineSimilarity;

import detection.utility.UtilsZ;

public class TestMain
{

	public TestMain()
	{

	}

	public static void main(String[] args)
	{
		double[] array = { 45.6, 12.3, 85.9987, 32.9822, 89.11, 39, 69, 44, 42, 1, 6, 8 };
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++)
		{
			System.out.println(array[i]);
		}
		
		org.apache.commons.text.similarity.CosineSimilarity cs = new CosineSimilarity();
		//cs.cosineSimilarity(arg0, arg1)
		
		BinomialDistribution bd_v1 = new BinomialDistribution(100,0.3);
		int[] v1 = bd_v1.sample(100);//an array representing the random sample
		double[] v1_d = new double[100];
		for(int i=0;i<v1.length;i++)
		{
			v1_d[i] = (double)v1[i];
			System.out.print(v1_d[i]+",");
		}
		System.out.println();
		BinomialDistribution bd_v2 = new BinomialDistribution(100,0.34);
		int[] v2 = bd_v2.sample(100);//an array representing the random sample
		double[] v2_d = new double[100];
		for(int i=0;i<v2.length;i++)
		{
			v2_d[i] = (double)v2[i];
			System.out.print(v2_d[i]+",");
		}
		System.out.println();
		KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
		double p_value  = ksTest.kolmogorovSmirnovTest(v1_d, v2_d);
		System.out.println(UtilsZ.round(p_value, 6, BigDecimal.ROUND_HALF_UP) );
		
	}

}
