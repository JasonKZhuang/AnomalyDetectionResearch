package detection.beans.kim;

import java.util.ArrayList;
import java.util.List;

import detection.beans.DividedStockWindow;

public class PeerGroup
{
	private DividedStockWindow self = new DividedStockWindow();
	
	private List<DividedStockWindow> peers = new ArrayList<>();
	
	private List<Double> eucl_distance_peers = new ArrayList<>();
	
	private double groupMeanValue = 0;
	
	public DividedStockWindow getSelf()
	{
		return self;
	}
	public void setSelf(DividedStockWindow self)
	{
		this.self = self;
	}
	public List<DividedStockWindow> getPeers()
	{
		return peers;
	}
	public void setPeers(List<DividedStockWindow> peers)
	{
		this.peers = peers;
	}
	public double getGroupMeanValue()
	{
		return groupMeanValue;
	}
	public void setGroupMeanValue(double groupMeanValue)
	{
		this.groupMeanValue = groupMeanValue;
	}
	
	public List<Double> getEucl_distance_peers()
	{
		return eucl_distance_peers;
	}
	public void setEucl_distance_peers(List<Double> eucl_distance_peers)
	{
		this.eucl_distance_peers = eucl_distance_peers;
	}
	
	
	
	/*
	 * calculate peer group mean value
	 */
	public void setGroupMeanValue()
	{
		double sumValue = self.getAvg_price();
		
		int groupSize = peers.size() + 1;
		
		for(int i=0;i<peers.size();i++)
		{
			sumValue = sumValue + peers.get(i).getAvg_price();
		}
		
		this.groupMeanValue = sumValue/(double)groupSize;
	}
	
	
	
}
