package detection.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class UtilsZ
{

	public UtilsZ()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("java", 20);
		map.put("C++", 45);
		map.put("Java2Novice", 2);
		map.put("Unix", 67);
		map.put("MAC", 26);
		map.put("Why this kolavari", 93);

		Map<String, Integer> sortedMap = sortByValue(map,"ASC");

		for (Map.Entry<String, Integer> entry : sortedMap.entrySet())
		{
			System.out.println(entry.getKey() + " ==== " + entry.getValue());
		}
	}

	/**
	 * 
	 * @param value
	 *            v
	 * @param scale
	 *            3
	 * @param roundingMode
	 *            BigDecimal.ROUND_HALF_UP
	 * @return
	 */
	public static double round(double value, int scale, int roundingMode)
	{
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, String sortType)
	{
		Set<Entry<String, Integer>> set = unsortMap.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);

		if (sortType.equals("DESC"))
		{
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
			{
				public int compare(Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2)
				{
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
		} else
		{
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
			{
				public int compare(Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2)
				{
					return (o1.getValue()).compareTo(o2.getValue());
				}
			});

		}

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

}
