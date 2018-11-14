package detection.algorithms.kaizhi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public class KSTestListComparator implements Comparator<KSTestStatisticBean>
{
	
	private String sortField;
	
	private String sortTypes;//ASC,DESC
	
	/**
	 * @param fieldName sumPValue | rejectCount
	 * @param sortType ASC | DESC
	 */
	public KSTestListComparator(String fieldName,String sortType)
	{
		this.sortField = fieldName;
		this.sortTypes = sortType;
	}
	
	/**
     * a negative integer means first argument is less than the second
     * zero means equal to
     * a positive integer means first argument is greater than the second
     */
	public int compare(KSTestStatisticBean a, KSTestStatisticBean b) 
    {
		if (sortField.equals("sumPValue"))
		{
			if (sortTypes.equals("ASC"))
			{
				return a.getSumPValue() > b.getSumPValue() ? 1 
		    			: a.getSumPValue() == b.getSumPValue() ? 0 : -1;
	    	}else
			{
				return a.getSumPValue() < b.getSumPValue() ? 1 
		    			: a.getSumPValue() == b.getSumPValue() ? 0 : -1;
			}
		}else if (sortField.equals("rejectCount"))
		{
			if (sortTypes.equals("ASC"))
			{
				return a.getRejectCount() > b.getRejectCount() ? 1 
		    			: a.getRejectCount() == b.getRejectCount() ? 0 : -1;
	    	}else
			{
	    		return a.getRejectCount() < b.getRejectCount() ? 1 
		    			: a.getRejectCount() == b.getRejectCount() ? 0 : -1;
			}
		}
		
		return 0;

    }
	
	private int compare2(KSTestStatisticBean a, KSTestStatisticBean b) 
    {
		String getMethod = "";
		if (sortField!=null && !sortField.equals(""))
		{
			getMethod = "get" + sortField.substring(0,1).toUpperCase() + sortField.substring(1,sortField.length());
		}
		Method methodA;
		Method methodB;
		double retValueA = 0d;
		double retValueB = 0d;
		try
		{
			methodA = a.getClass().getMethod(getMethod);
			methodA.setAccessible(true);
			methodB = b.getClass().getMethod(getMethod);
			methodB.setAccessible(true);
			retValueA = (double) methodA.invoke(a.getClass());
			retValueB = (double) methodB.invoke(b.getClass());
			
			if (sortTypes.equals("ASC"))
	    	{
	    		
				return retValueA > retValueB ? 1 
		    			: a.getSumPValue() == b.getSumPValue() ? 0 : -1;
	    	}else
			{
				return retValueA < retValueB ? 1 
		    			: a.getSumPValue() == b.getSumPValue() ? 0 : -1;
			}
	    		
		} catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}catch (SecurityException e)
		{
			e.printStackTrace();
		}catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		
		return 0;

    }

}
