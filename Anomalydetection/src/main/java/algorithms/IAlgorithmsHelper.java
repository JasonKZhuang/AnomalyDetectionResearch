package algorithms;

import java.util.List;

import detection.beans.StockBeanKNN;
import detection.beans.StockRecord;

public interface IAlgorithmsHelper
{
	public void kNN (StockBeanKNN bean, List<StockRecord> recordCollecton);
	
	public double similarity (StockBeanKNN groupA, StockBeanKNN groupB);
	
}
