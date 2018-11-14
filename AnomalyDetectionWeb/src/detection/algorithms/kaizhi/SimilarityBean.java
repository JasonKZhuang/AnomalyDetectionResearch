package detection.algorithms.kaizhi;

import java.util.List;

public class SimilarityBean
{
	private String bootName;
	private String[] sim_date;
	private double[] similarity;
	private List<SimDay> simDays;
	
	public SimilarityBean()
	{
		
	}

	public String getBootName()
	{
		return bootName;
	}


	public void setBootName(String bootName)
	{
		this.bootName = bootName;
	}


	public String[] getSim_date()
	{
		return sim_date;
	}


	public void setSim_date(String[] sim_date)
	{
		this.sim_date = sim_date;
	}


	public double[] getSimilarity()
	{
		return similarity;
	}


	public void setSimilarity(double[] similarity)
	{
		this.similarity = similarity;
	}

	public List<SimDay> getSimDays()
	{
		return simDays;
	}

	public void setSimDays(List<SimDay> simDays)
	{
		this.simDays = simDays;
	}
	
}
