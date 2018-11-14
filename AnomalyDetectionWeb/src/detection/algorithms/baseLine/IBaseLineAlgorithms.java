package detection.algorithms.baseLine;

import java.util.HashMap;
import java.util.List;

import detection.beans.DividedStockWindow;
import detection.beans.SortValueBean;
import detection.beans.kim.PeerGroup;

public interface IBaseLineAlgorithms
{
	/**
	 * process divide original data into N time windows
	 * and each time window store the average price of this short period
	 */
	public void divideStockData();
	
	/**
	 * Creating Peer group for every stock, 
	 * which based on N dimension to calculate EUclidean Distance
	 * @param stockNames
	 * @return
	 */
	public HashMap<String, List<SortValueBean>> createPeerGroup(List<String> stockNames);
	
	/**
	 * calculate Euclidean Distance
	 * @param y1
	 * @param y2
	 * @return
	 */
	public double calculateEuclideanDistance(List<DividedStockWindow> y1,List<DividedStockWindow> y2);
	
	/**
	 * calculate Euclidean Distance
	 * @param a
	 * @param b
	 * @return
	 */
	public double euclideanDistance(double[] a, double[] b);
	
	/**
	 * update peer group members for N time windows,including mean value of peer group in windows(i)
	 * @param stockNames
	 * @return contain each time window value,which includes all stocks with their peers
	 */
	public List<HashMap<String,PeerGroup>> updatePeerGroup(List<Integer> divdWindows
			,HashMap<String, List<SortValueBean>> pgMap);
	
	/**
	 * store updated peerGroup to Database
	 * @param divdWindows
	 * @param pgMap
	 * @return
	 */
	public void savePeerGroup(List<HashMap<String,PeerGroup>> peerGroups);
	
	/**
	 * update weights of table Weight
	 */
	public void updateWeights();
	
	/**
	 * updating new mean value of peerGroup with weight
	 */
	public void updateMeanValueWithWeights();

	/**
	 * process every cell distance in every group and time windows
	 */
	public void processCellsDistance();

	
	
}
