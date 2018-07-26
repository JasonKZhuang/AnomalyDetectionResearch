package detection.algorithms;

import java.util.List;

import detection.beans.StockKNNBean;
import detection.beans.StockRecord;

public interface IAlgorithmsHelper
{
	/**
	 * calculate K nearest neighbours from the same time window records
	 * @param bean
	 * @param recordCollecton
	 */
	public void kNN (StockKNNBean bean, List<StockRecord> recordCollecton);
	
	/**
	 * calculate similarity of two group,(cross value)/(union value)
	 * @param groupA
	 * @param groupB
	 * @return
	 */
	public double similarity (StockKNNBean groupA, StockKNNBean groupB);
	
}
