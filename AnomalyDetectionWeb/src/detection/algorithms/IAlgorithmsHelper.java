package detection.algorithms;

import java.util.Date;
import java.util.List;

import detection.beans.JaccardBean;
import detection.beans.StockKNNBean;
import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.beans.StockWithNNBean;

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
	
	///==============new method ========================================//
	/**
	 * for every timeWindow build nearest neighbors
	 * @param stockName
	 * @param tradingDays
	 * @return
	 */
	public List<StockWithNNBean> getStock_NNForDuration (StockName stockName, List<Date> tradingDates);
	
	/**
	 * calculate Jaccards in all timewindows for one stock
	 * Based on its neighbors in each timewindow
	 * @return
	 */
	public List<JaccardBean> calculateJaccardForAStock(List<StockWithNNBean> stocks);
	
}
