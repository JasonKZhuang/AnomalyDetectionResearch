package detection.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import detection.beans.DividedStockWindow;
import detection.beans.kim.PeerGroupBean;
import detection.db.IDatabaseHelper;

@Controller
public class GetDataKimController
{
	@Autowired
	private IDatabaseHelper dbHelper;
	
	protected static List<String> stockNames = new ArrayList<>();
	protected final Log myLog = LogFactory.getLog(GetDataController.class);
	
	@ResponseBody
	@RequestMapping(value="/kim/getStockNames",method=RequestMethod.GET,produces="application/json")
    public List<String> getStockNames()
    {
		myLog.info("-- Get All Stock NAMEs .");
		
		List<String> retList = null;
		try
		{
			retList = dbHelper.getStockNamesFromDivideWindows();
		}catch(Exception exp)
		{
			myLog.info(exp.getMessage());
		}
		return retList;
    }
	
	@ResponseBody
	@RequestMapping(value="/kim/getDividedStock/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<DividedStockWindow> getDividedStock(@PathVariable String stockName)
    {
		List<DividedStockWindow> retList = new ArrayList<>();
		retList = dbHelper.getDividedWindowsByStockname(stockName);
		return retList;
		/*
		for(int i=0;i<10;i++)
		{
			DividedStockWindow dsw = new DividedStockWindow();
			dsw.setStock_name(stockName);
			dsw.setWindow_no(i);
			dsw.setAvg_price(1.2*i);
			retList.add(dsw);
		}
		*/
		
    }
	///
	@ResponseBody
	@RequestMapping(value="/kim/getPeerNames/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<String> getPeerNames(@PathVariable String stockName)
    {
		List<String> retList = new ArrayList<>();
		retList = dbHelper.getPeerNamesForGroup(stockName);
		return retList;
    }
	
	///
	@ResponseBody
	@RequestMapping(value="/kim/getPeerGroups/{stockName}",method=RequestMethod.GET,produces="application/json")
    public List<PeerGroupBean> getPeerGroups(@PathVariable String stockName)
    {
		List<PeerGroupBean> retList = new ArrayList<>();
		retList = dbHelper.getPeergroupsForGroup(stockName);
		return retList;
    }
	
	
	
}
