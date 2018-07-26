package detection.algorithms.kim;

import java.util.HashMap;
import java.util.List;

import detection.beans.DividedWindows;

public interface IKimAlgorithms
{
	/**
	 * process divide original data into N time windows
	 * and each time window store the average price of this short period
	 */
	public void divideStockData();
	
	11111, continue do this
	public HashMap<String, List<String>> getPeerGroup();
	
	public List<String> getPeerGroupForStock(String stockName,List<String> stockNames);
	
	public double calculateEucldeanDistance(List<DividedWindows> y1,List<DividedWindows> y2);
}
