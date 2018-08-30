package detection.beans;

import java.util.Date;

public class DividedStockWindow
{
	private int dw_id;
	private String stock_name;
	private int window_no;
	private Date start_date;
	private Date end_date;
	private double avg_price;
	public int getDw_id()
	{
		return dw_id;
	}
	public void setDw_id(int dw_id)
	{
		this.dw_id = dw_id;
	}
	public String getStock_name()
	{
		return stock_name;
	}
	public void setStock_name(String stock_name)
	{
		this.stock_name = stock_name;
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
	public double getAvg_price()
	{
		return avg_price;
	}
	public void setAvg_price(double avg_price)
	{
		this.avg_price = avg_price;
	}
	
}
