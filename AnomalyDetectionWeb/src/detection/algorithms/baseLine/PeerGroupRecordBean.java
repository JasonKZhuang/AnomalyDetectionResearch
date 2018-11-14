package detection.algorithms.baseLine;

import java.util.Date;

public class PeerGroupRecordBean
{
	private int rId;
	private Date tdate;
	private String self_name;
	private double self_price;
	private double weight_mean;
	private double differ;
	private double std;
	private double std_rank;
	
	public PeerGroupRecordBean()
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

	public String getSelf_name()
	{
		return self_name;
	}

	public void setSelf_name(String self_name)
	{
		this.self_name = self_name;
	}

	public double getSelf_price()
	{
		return self_price;
	}

	public void setSelf_price(double self_price)
	{
		this.self_price = self_price;
	}
	
	public double getWeight_mean()
	{
		return weight_mean;
	}

	public void setWeight_mean(double weight_mean)
	{
		this.weight_mean = weight_mean;
	}

	public double getDiffer()
	{
		return differ;
	}

	public void setDiffer(double differ)
	{
		this.differ = differ;
	}

	public double getStd()
	{
		return std;
	}

	public void setStd(double std)
	{
		this.std = std;
	}

	public double getStd_rank()
	{
		return std_rank;
	}

	public void setStd_rank(double std_rank)
	{
		this.std_rank = std_rank;
	}
	
}
