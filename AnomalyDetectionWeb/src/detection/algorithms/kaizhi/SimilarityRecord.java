package detection.algorithms.kaizhi;

import java.util.Date;

public class SimilarityRecord
{
	private int rId;
	private Date tdate;
	private Date sim_date;
	private double sim_value;

	public SimilarityRecord()
	{
		// TODO Auto-generated constructor stub
	}

	public int getrId()
	{
		return rId;
	}

	public void setrId(int rId)
	{
		this.rId = rId;
	}

	public Date getTdate()
	{
		return tdate;
	}

	public void setTdate(Date tdate)
	{
		this.tdate = tdate;
	}

	public Date getSim_date()
	{
		return sim_date;
	}

	public void setSim_date(Date sim_date)
	{
		this.sim_date = sim_date;
	}

	public double getSim_value()
	{
		return sim_value;
	}

	public void setSim_value(double sim_value)
	{
		this.sim_value = sim_value;
	}
	
	

}
