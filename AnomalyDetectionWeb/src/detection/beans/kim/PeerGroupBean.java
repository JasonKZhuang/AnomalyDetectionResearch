package detection.beans.kim;

import java.util.Date;

public class PeerGroupBean
{
	private int rid;
	private String stock_name;			
	private String peer_members;			
	private int window_no;		
	private Date start_date;	
	private Date end_date;		
	private double mean_value;
	private String euclDistance_to_members;
	private double mean_value_with_weights;
	
	public int getRid()
	{
		return rid;
	}
	public void setRid(int rid)
	{
		this.rid = rid;
	}
	public String getStock_name()
	{
		return stock_name;
	}
	public void setStock_name(String stock_name)
	{
		this.stock_name = stock_name;
	}
	public String getPeer_members()
	{
		return peer_members;
	}
	public void setPeer_members(String peer_members)
	{
		this.peer_members = peer_members;
	}
	public int getWindow_no()
	{
		return window_no;
	}
	public void setWindow_no(int window_no)
	{
		this.window_no = window_no;
	}
	public Date getStart_date()
	{
		return start_date;
	}
	public void setStart_date(Date start_date)
	{
		this.start_date = start_date;
	}
	public Date getEnd_date()
	{
		return end_date;
	}
	public void setEnd_date(Date end_date)
	{
		this.end_date = end_date;
	}
	public double getMean_value()
	{
		return mean_value;
	}
	public void setMean_value(double mean_value)
	{
		this.mean_value = mean_value;
	}
	public String getEuclDistance_to_members()
	{
		return euclDistance_to_members;
	}
	public void setEuclDistance_to_members(String euclDistance_to_members)
	{
		this.euclDistance_to_members = euclDistance_to_members;
	}
	
	public double getMean_value_with_weights()
	{
		return mean_value_with_weights;
	}
	public void setMean_value_with_weights(double mean_value_with_weights)
	{
		this.mean_value_with_weights = mean_value_with_weights;
	}	
	

}
