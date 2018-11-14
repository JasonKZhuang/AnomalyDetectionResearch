package detection.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * æ—¥æœŸè½¬æ�¢ç±» è½¬æ�¢ä¸€ä¸ª java.util.Date å¯¹è±¡åˆ°ä¸€ä¸ªå­—ç¬¦ä¸²ä»¥å�Š ä¸€ä¸ªå­—ç¬¦ä¸²åˆ°ä¸€ä¸ª java.util.Date å¯¹è±¡. åº„å¼€æ™ºæ·»åŠ ï¼š
 * åˆ¤æ–­æŒ‡å®šæ—¥æœŸçš„å¹´ä»½æ˜¯å�¦æ˜¯é—°å¹´ å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æ˜ŸæœŸçš„ç¬¬ä¸€å¤© å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æ˜ŸæœŸçš„æœ€å�Žä¸€å¤© å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„ç¬¬ä¸€å¤©
 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„æœ€å�Žä¸€å¤© ç»“æ�Ÿåº„å¼€æ™ºæ·»åŠ 
 * 
 */
public class DateUtil
{

	private static Log log = LogFactory.getLog(DateUtil.class);

	private static String datePattern = "yyyy-MM-dd";

	private static String timePattern = "HH:mm:ss";

	private static String datetimePattern = datePattern + " " + timePattern;

	
	public static String getDatePattern()
	{
		return datePattern;
	}

	public static final String getDate(Date aDate)
	{
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate != null)
		{
			df = new SimpleDateFormat(datePattern);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	public static final Date convertStringToDate(String aMask, String strDate)
			throws ParseException
	{
		SimpleDateFormat df = null;
		Date date = null;
		df = new SimpleDateFormat(aMask);

		if (log.isDebugEnabled())
		{
			log.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
		}

		try
		{
			date = df.parse(strDate);
		} catch (ParseException pe)
		{
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}

	
	public static String getTimeNow(Date theTime)
	{
		return getDateTime(timePattern, theTime);
	}

	/**
	 * This method returns the current date in the format: yyyy-MM-dd
	 * @return the current date
	 * @throws ParseException
	 */
	public static Calendar getToday() throws ParseException
	{
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat(datePattern);

		// This seems like quite a hack (date -> string -> date),
		// but it works ;-)
		String todayAsString = df.format(today);
		Calendar cal = new GregorianCalendar();
		cal.setTime(convertStringToDate(todayAsString));

		return cal;
	}

	public static final String getDateTime(String aMask, Date aDate)
	{
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null)
		{
			log.error("aDate is null!");
		} else
		{
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	public static final String getDateTime(Date aDate)
	{
		SimpleDateFormat df = new SimpleDateFormat(datetimePattern);
		String returnValue = "";

		if (aDate == null)
		{
			log.error("aDate is null!");
		} else
		{
			returnValue = df.format(aDate);
		}
		return (returnValue);
	}

	public static final String convertDateToString(Date aDate)
	{
		return getDateTime(datePattern, aDate);
	}

	public static Date convertStringToDate(String strDate)
			throws ParseException
	{
		Date aDate = null;

		try
		{
			if (log.isDebugEnabled())
			{
				log.debug("converting date with pattern: " + datePattern);
			}

			aDate = convertStringToDate(datePattern, strDate);
		} catch (ParseException pe)
		{
			log.error("Could not convert '" + strDate
					+ "' to a date, throwing exception");
			pe.printStackTrace();
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());

		}

		return aDate;
	}

	public static Date convertStringToDateTime(String strDate)
			throws ParseException
	{
		Date aDate = null;

		try
		{
			if (log.isDebugEnabled())
			{
				log.debug("converting date with pattern: " + datetimePattern);
			}

			aDate = convertStringToDate(datetimePattern, strDate);
		} catch (ParseException pe)
		{
			log.error("Could not convert '" + strDate
					+ "' to a date, throwing exception");
			pe.printStackTrace();
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());

		}

		return aDate;
	}

	public static boolean isLeapYear()
	{
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		return isLeapYear(year);
	}

	public static boolean isLeapYear(int year)
	{
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0)
		{
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	public static boolean isLeapYear(Date date)
	{

		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		int year = gc.get(Calendar.YEAR);
		return isLeapYear(year);
	}

	public static boolean isLeapYear(Calendar gc)
	{
		int year = gc.get(Calendar.YEAR);
		return isLeapYear(year);
	}

	public static Date getFirstDayOfWeek(Date date)
	{
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(Calendar.DAY_OF_WEEK))
		{
		case (Calendar.SUNDAY  ):
			gc.add(Calendar.DATE, 0);
			break;
		case (Calendar.MONDAY  ):
			gc.add(Calendar.DATE, -1);
			break;
		case (Calendar.TUESDAY  ):
			gc.add(Calendar.DATE, -2);
			break;
		case (Calendar.WEDNESDAY  ):
			gc.add(Calendar.DATE, -3);
			break;
		case (Calendar.THURSDAY  ):
			gc.add(Calendar.DATE, -4);
			break;
		case (Calendar.FRIDAY  ):
			gc.add(Calendar.DATE, -5);
			break;
		case (Calendar.SATURDAY  ):
			gc.add(Calendar.DATE, -6);
			break;
		}
		return gc.getTime();
	}

	public static Calendar getFirstDayOfWeek(Calendar gc)
	{
		switch (gc.get(Calendar.DAY_OF_WEEK))
		{
		case (Calendar.SUNDAY  ):
			gc.add(Calendar.DATE, 0);
			break;
		case (Calendar.MONDAY  ):
			gc.add(Calendar.DATE, -1);
			break;
		case (Calendar.TUESDAY  ):
			gc.add(Calendar.DATE, -2);
			break;
		case (Calendar.WEDNESDAY  ):
			gc.add(Calendar.DATE, -3);
			break;
		case (Calendar.THURSDAY  ):
			gc.add(Calendar.DATE, -4);
			break;
		case (Calendar.FRIDAY  ):
			gc.add(Calendar.DATE, -5);
			break;
		case (Calendar.SATURDAY  ):
			gc.add(Calendar.DATE, -6);
			break;
		}
		return gc;
	}

	public static Date getLastDayOfWeek(Date date)
	{
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(Calendar.DAY_OF_WEEK))
		{
		case (Calendar.SUNDAY  ):
			gc.add(Calendar.DATE, 6);
			break;
		case (Calendar.MONDAY  ):
			gc.add(Calendar.DATE, 5);
			break;
		case (Calendar.TUESDAY  ):
			gc.add(Calendar.DATE, 4);
			break;
		case (Calendar.WEDNESDAY  ):
			gc.add(Calendar.DATE, 3);
			break;
		case (Calendar.THURSDAY  ):
			gc.add(Calendar.DATE, 2);
			break;
		case (Calendar.FRIDAY  ):
			gc.add(Calendar.DATE, 1);
			break;
		case (Calendar.SATURDAY  ):
			gc.add(Calendar.DATE, 0);
			break;
		}
		return gc.getTime();
	}

	public static Date getFirstDayOfNextWeek(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextWeekè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getFirstDayOfWeek
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.setTime(DateUtil.getNextWeek(gc.getTime()));
		gc.setTime(DateUtil.getFirstDayOfWeek(gc.getTime()));
		return gc.getTime();
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸçš„ç¬¬ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return Calendarç±»åž‹ æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸçš„ç¬¬ä¸€å¤©
	 */
	public static Calendar getFirstDayOfNextWeek(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextWeekè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getFirstDayOfWeek
		 */
		gc.setTime(DateUtil.getNextWeek(gc.getTime()));
		gc.setTime(DateUtil.getFirstDayOfWeek(gc.getTime()));
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸçš„æœ€å�Žä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸçš„æœ€å�Žä¸€å¤©
	 */
	public static Date getLastDayOfNextWeek(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextWeekè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getLastDayOfWeek
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.setTime(DateUtil.getNextWeek(gc.getTime()));
		gc.setTime(DateUtil.getLastDayOfWeek(gc.getTime()));
		return gc.getTime();
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸ
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæ˜ŸæœŸ
	 */
	public static Date getNextWeek(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸåŠ 7å¤©
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, 7);
		return gc.getTime();
	}

	public static Calendar getNextWeek(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸåŠ 7å¤©
		 */
		gc.add(Calendar.DATE, 7);
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸Šä¸€ä¸ªæ˜ŸæœŸ
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸Šä¸€ä¸ªæ˜ŸæœŸ
	 */
	public static Date getPreviousWeek(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸå‡�7å¤©
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, -7);
		return gc.getTime();
	}

	public static Calendar getPreviousWeek(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸå‡�7å¤©
		 */
		gc.add(Calendar.DATE, -7);
		return gc;
	}

	/***************************************************************************
	 * èŽ·å¾—ç»™å®šæ—¥æœŸæ‰€åœ¨çš„æ˜ŸæœŸå‡ 
	 * 
	 * @param date
	 * @return æ•´æ•°ç±»åž‹
	 */
	public static int getWeekForDate(Date date)
	{
		Calendar a_gc = Calendar.getInstance();
		a_gc.setTime(date);
		return a_gc.get(Calendar.DAY_OF_WEEK) - 1;

	}

	/***************************************************************************
	 * èŽ·å¾—ç»™å®šæ—¥æœŸæ‰€åœ¨çš„æ˜ŸæœŸå‡ 
	 * 
	 * @param Calendar
	 * @return æ•´æ•°ç±»åž‹
	 */
	public static int getWeekForDate(Calendar gc)
	{
		return gc.get(Calendar.DAY_OF_WEEK) - 1;
	}

	// endï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ä¸Žæ˜ŸæœŸæœ‰å…³çš„æ–¹æ³•ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�//

	// ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ä¸Žæœˆä»½æœ‰å…³çš„æ–¹æ³•ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�//
	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„ç¬¬ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return Dateç±»åž‹æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„ç¬¬ä¸€å¤©
	 */
	public static Date getFirstDayOfMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è®¾ç½®ä¸º1å�·
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.set(Calendar.DAY_OF_MONTH, 1);
		return gc.getTime();
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„ç¬¬ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return Calendarç±»åž‹ æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„ç¬¬ä¸€å¤©
	 */
	public static Calendar getFirstDayOfMonth(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è®¾ç½®ä¸º1å�·
		 */
		gc.set(Calendar.DAY_OF_MONTH, 1);
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„æœ€å�Žä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸ
	 * @return æ—¥æœŸç±»åž‹ æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„æœ€å�Žä¸€å¤©
	 */
	public static Date getLastDayOfMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.å¦‚æžœdateåœ¨1æœˆï¼Œåˆ™ä¸º31æ—¥ 2.å¦‚æžœdateåœ¨2æœˆï¼Œåˆ™ä¸º28æ—¥ 3.å¦‚æžœdateåœ¨3æœˆï¼Œåˆ™ä¸º31æ—¥
		 * 4.å¦‚æžœdateåœ¨4æœˆï¼Œåˆ™ä¸º30æ—¥ 5.å¦‚æžœdateåœ¨5æœˆï¼Œåˆ™ä¸º31æ—¥ 6.å¦‚æžœdateåœ¨6æœˆï¼Œåˆ™ä¸º30æ—¥
		 * 7.å¦‚æžœdateåœ¨7æœˆï¼Œåˆ™ä¸º31æ—¥ 8.å¦‚æžœdateåœ¨8æœˆï¼Œåˆ™ä¸º31æ—¥ 9.å¦‚æžœdateåœ¨9æœˆï¼Œåˆ™ä¸º30æ—¥
		 * 10.å¦‚æžœdateåœ¨10æœˆï¼Œåˆ™ä¸º31æ—¥ 11.å¦‚æžœdateåœ¨11æœˆï¼Œåˆ™ä¸º30æ—¥ 12.å¦‚æžœdateåœ¨12æœˆï¼Œåˆ™ä¸º31æ—¥
		 * 1.å¦‚æžœdateåœ¨é—°å¹´çš„2æœˆï¼Œåˆ™ä¸º29æ—¥
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(Calendar.MONTH))
		{
		case 0:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 1:
			gc.set(Calendar.DAY_OF_MONTH, 28);
			break;
		case 2:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 3:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 4:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 5:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 6:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 7:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 8:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 9:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 10:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 11:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		}
		// æ£€æŸ¥é—°å¹´
		if ((gc.get(Calendar.MONTH) == Calendar.FEBRUARY)
				&& (isLeapYear(gc.get(Calendar.YEAR))))
		{
			gc.set(Calendar.DAY_OF_MONTH, 29);
		}
		return gc.getTime();
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„æœ€å�Žä¸€å¤©
	 * 
	 * @param Calendar
	 *            æŒ‡å®šæ—¥æœŸæ—¥åŽ†
	 * @return Calendarç±»åž‹æŒ‡å®šæ—¥æœŸçš„æ‰€å¤„æœˆä»½çš„æœ€å�Žä¸€å¤©
	 */
	public static Calendar getLastDayOfMonth(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.å¦‚æžœdateåœ¨1æœˆï¼Œåˆ™ä¸º31æ—¥ 2.å¦‚æžœdateåœ¨2æœˆï¼Œåˆ™ä¸º28æ—¥ 3.å¦‚æžœdateåœ¨3æœˆï¼Œåˆ™ä¸º31æ—¥
		 * 4.å¦‚æžœdateåœ¨4æœˆï¼Œåˆ™ä¸º30æ—¥ 5.å¦‚æžœdateåœ¨5æœˆï¼Œåˆ™ä¸º31æ—¥ 6.å¦‚æžœdateåœ¨6æœˆï¼Œåˆ™ä¸º30æ—¥
		 * 7.å¦‚æžœdateåœ¨7æœˆï¼Œåˆ™ä¸º31æ—¥ 8.å¦‚æžœdateåœ¨8æœˆï¼Œåˆ™ä¸º31æ—¥ 9.å¦‚æžœdateåœ¨9æœˆï¼Œåˆ™ä¸º30æ—¥
		 * 10.å¦‚æžœdateåœ¨10æœˆï¼Œåˆ™ä¸º31æ—¥ 11.å¦‚æžœdateåœ¨11æœˆï¼Œåˆ™ä¸º30æ—¥ 12.å¦‚æžœdateåœ¨12æœˆï¼Œåˆ™ä¸º31æ—¥
		 * 1.å¦‚æžœdateåœ¨é—°å¹´çš„2æœˆï¼Œåˆ™ä¸º29æ—¥
		 */
		switch (gc.get(Calendar.MONTH))
		{
		case 0:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 1:
			gc.set(Calendar.DAY_OF_MONTH, 28);
			break;
		case 2:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 3:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 4:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 5:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 6:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 7:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 8:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 9:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		case 10:
			gc.set(Calendar.DAY_OF_MONTH, 30);
			break;
		case 11:
			gc.set(Calendar.DAY_OF_MONTH, 31);
			break;
		}
		// æ£€æŸ¥é—°å¹´
		if ((gc.get(Calendar.MONTH) == Calendar.FEBRUARY)
				&& (isLeapYear(gc.get(Calendar.YEAR))))
		{
			gc.set(Calendar.DAY_OF_MONTH, 29);
		}
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆçš„ç¬¬ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆçš„ç¬¬ä¸€å¤©
	 */
	public static Date getFirstDayOfNextMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextMonthè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getFirstDayOfMonth
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.setTime(DateUtil.getNextMonth(gc.getTime()));
		gc.setTime(DateUtil.getFirstDayOfMonth(gc.getTime()));
		return gc.getTime();
	}

	public static Calendar getFirstDayOfNextMonth(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextMonthè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getFirstDayOfMonth
		 */
		gc.setTime(DateUtil.getNextMonth(gc.getTime()));
		gc.setTime(DateUtil.getFirstDayOfMonth(gc.getTime()));
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆçš„æœ€å�Žä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆçš„æœ€å�Žä¸€å¤©
	 */
	public static Date getLastDayOfNextMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.è°ƒç”¨getNextMonthè®¾ç½®å½“å‰�æ—¶é—´ 2.ä»¥1ä¸ºåŸºç¡€ï¼Œè°ƒç”¨getLastDayOfMonth
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.setTime(DateUtil.getNextMonth(gc.getTime()));
		gc.setTime(DateUtil.getLastDayOfMonth(gc.getTime()));
		return gc.getTime();
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆ
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€ä¸ªæœˆ
	 */
	public static Date getNextMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸçš„æœˆä»½åŠ 1
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.MONTH, 1);
		return gc.getTime();
	}

	public static Calendar getNextMonth(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸçš„æœˆä»½åŠ 1
		 */
		gc.add(Calendar.MONTH, 1);
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸Šä¸€ä¸ªæœˆ
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸Šä¸€ä¸ªæœˆ
	 */
	public static Date getPreviousMonth(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸçš„æœˆä»½å‡�1
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.MONTH, -1);
		return gc.getTime();
	}

	public static Calendar getPreviousMonth(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸçš„æœˆä»½å‡�1
		 */
		gc.add(Calendar.MONTH, -1);
		return gc;
	}

	// endï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ä¸Žæœˆä»½æœ‰å…³çš„æ–¹æ³•ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�//

	// ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ä¸Žæ—¥æœŸæœ‰å…³çš„æ–¹æ³•ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�//
	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„ä¸‹ä¸€å¤©
	 */
	public static Date getNextDay(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸåŠ 1å¤©
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, 1);
		return gc.getTime();
	}

	public static Calendar getNextDay(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸåŠ 1å¤©
		 */
		gc.add(Calendar.DATE, 1);
		return gc;
	}

	/**
	 * å�–å¾—æŒ‡å®šæ—¥æœŸçš„å‰�ä¸€å¤©
	 * 
	 * @param date
	 *            æŒ‡å®šæ—¥æœŸã€‚
	 * @return æŒ‡å®šæ—¥æœŸçš„å‰�ä¸€å¤©
	 */
	public static Date getPreviousDay(Date date)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸå‡�1å¤©
		 */
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, -1);
		return gc.getTime();
	}

	public static Calendar getPreviousDay(Calendar gc)
	{
		/**
		 * è¯¦ç»†è®¾è®¡ï¼š 1.æŒ‡å®šæ—¥æœŸå‡�1å¤©
		 */
		gc.add(Calendar.DATE, -1);
		return gc;
	}

	/**
	 * ç»™å®šæ—¥æœŸç±»åž‹èŽ·å¾—æ—¥æœŸæ‰€åœ¨å¹´
	 */
	public static String getYearForDateTime(Date date)
	{
		// --yyyy-MM-dd hh:mm
		String str_date = getDateTime(datetimePattern, date).trim();
		return str_date.substring(0, 4);
	}

	/***************************************************************************
	 * ç»™å®šæ—¥æœŸç±»åž‹èŽ·å¾—æ—¥æœŸæ‰€åœ¨æœˆä»½
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonthForDateTime(Date date)
	{
		// --yyyy-MM-dd hh:mm
		String str_date = getDateTime(datetimePattern, date).trim();
		return str_date.substring(5, 7);
	}

	/***************************************************************************
	 * ç»™å®šæ—¥æœŸç±»åž‹èŽ·å¾—æ—¥æœŸæ‰€åœ¨æ—¥
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateForDateTime(Date date)
	{
		// --yyyy-MM-dd hh:mm
		String str_date = getDateTime(datetimePattern, date).trim();
		return str_date.substring(8, 10);
	}

	/***************************************************************************
	 * ç»™å®šæ—¥æœŸç±»åž‹èŽ·å¾—æ—¥æœŸæ‰€åœ¨æ—¶
	 * 
	 * @param date
	 * @return
	 */
	public static String getHourForDateTime(Date date)
	{
		// --yyyy-MM-dd hh:mm
		String str_date = getDateTime(datetimePattern, date).trim();
		return str_date.substring(11, 13);
	}

	public static String getMinuteForDateTime(Date date)
	{
		// --yyyy-MM-dd hh:mm
		String str_date = getDateTime(datetimePattern, date).trim();
		return str_date.substring(14, 16);
	}

	// endï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ä¸Žæ—¥æœŸæœ‰å…³çš„æ–¹æ³•ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�ï¼�//

	/**
	 * ç»™å®šä¸€ä¸ªæ—¥æœŸï¼Œè¿”å›žåŠ å‡�nå¤©å�Žçš„æ—¥æœŸï¼Œè¿”å›žDate nDaysAfterOneDate
	 * 
	 * @param basicDate
	 *            Date
	 * @param n
	 *            int
	 * @return Date
	 */
	public static Date nDaysAfterOneDate(Date basicDate, int n)
	{
		long nDay = (basicDate.getTime() / (24 * 60 * 60 * 1000) + n)
				* (24 * 60 * 60 * 1000);
		Date newDate = new Date();
		newDate.setTime(nDay);
		return newDate;
	}

	/**
	 * å°†StringæŒ‰ç…§æ ‡å‡†æ ¼å¼�åŒ–æˆ�Date
	 * 
	 * @param value
	 * @return
	 */
	public static Date convert(Object value)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (value == null)
			return null;
		if (((String) value).trim().length() == 0)
			return null;

		if (value instanceof String)
		{
			try
			{
				return df.parse((String) value);
			} catch (Exception ex)
			{
				log.error("è¾“å…¥çš„æ—¥æœŸç±»åž‹ä¸�å�ˆä¹Žyyyy-MM-dd", ex);
				return null;
			}
		} else
		{
			log.warn("è¾“å…¥çš„ä¸�æ˜¯å­—ç¬¦ç±»åž‹");
			return null;
		}
	}

	/**
	 * å½“å‰�æœˆæ˜¯å¤šå°‘å¤©
	 * 
	 * @param year
	 *            å¹´
	 * @param month
	 *            æœˆ
	 * @param format
	 *            æ—¥æœŸæ ¼å¼�åŒ–æ ¼å¼�
	 * @return String
	 */

	public static String beforeMonthDays(String year, String month)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), (Integer.parseInt(month)), 0);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String day = formatter.format(calendar.getTime());
		day = day.substring((day.length() - 2), day.length());
		return day;
	}

	/** **************************************************************************************************** */

	public int getIntervalDays(Calendar startday, Calendar endday)
	{
		if (startday.after(endday))
		{
			Calendar cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTimeInMillis();
		long el = endday.getTimeInMillis();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}

	public static int getIntervalDays(String startDate, String endDate)
	{
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			java.util.Date date = myFormatter.parse(startDate);
			java.util.Date mydate = myFormatter.parse(endDate);
			return (int) (mydate.getTime() - date.getTime())
					/ (24 * 60 * 60 * 1000);
		} catch (Exception e)
		{
			return 0;
		}
	}

	/**
	 * è®¡ç®—ä¸¤ä¸ªæ—¥æœŸæ—¶é—´ç›¸å·®å‡ å°�æ—¶
	 * 
	 * @param startDate
	 *            å¼€å§‹æ—¥æœŸæ—¶é—´
	 * @param endDate
	 *            ç»“æ�Ÿæ—¥æœŸæ—¶é—´
	 * @return int å°�æ—¶
	 */

	public static int getIntervalHours(String startDate, String endDate)
	{
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd hh");
		try
		{
			java.util.Date date = myFormatter.parse(startDate);
			java.util.Date mydate = myFormatter.parse(endDate);
			return (int) (mydate.getTime() - date.getTime()) / (60 * 60 * 1000);
		} catch (Exception e)
		{
			return 0;
		}
	}

	/**
	 * è®¡ç®—ä¸¤ä¸ªæ—¶é—´ç›¸å·®å‡ å°�æ—¶
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */

	public static int getIntervalHours(Date startday, Date endday)
	{
		if (startday.after(endday))
		{
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		// return (int) (ei / (1000 * 60 * 60 * 24));
		return (int) (ei / (1000 * 60 * 60));
	}

	public static int getIntervalDays(Date startday, Date endday)
	{
		if (startday.after(endday))
		{
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}

	public static int dateMinus(Date firstDate, Date secondDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fir = sdf.format(firstDate);
		String sec = sdf.format(secondDate);
		int minus = dateMinus(fir, sec);
		return minus;
	}

	public static int dateMinus(String frontdate, String afterdate)
	{
		String front[] = frontdate.split("-");
		for (int i = 0; i < front.length; i++)
			;
		String after[] = afterdate.split("-");
		for (int i = 0; i < after.length; i++)
			;
		String frontYear = null;
		String frontMon = null;
		String frontDay = null;
		String afterYear = null;
		String aftertMon = null;
		String afterDay = null;
		frontYear = front[0];
		frontMon = front[1];

		if (Integer.parseInt(frontMon) < 10)
			frontMon = front[1].replaceAll("0", "");
		else
			frontMon = front[1];
		frontDay = front[2];
		if (Integer.parseInt(frontDay) < 10)
			frontDay = front[2].replaceAll("0", "");
		else
			frontDay = front[2];
		afterYear = after[0];
		aftertMon = after[1];
		if (Integer.parseInt(aftertMon) < 10)
			aftertMon = after[1].replaceAll("0", "");
		else
			aftertMon = after[1];
		afterDay = after[2];
		if (Integer.parseInt(afterDay) < 10)
			afterDay = after[2].replaceAll("0", "");

		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(frontYear), (Integer.parseInt(frontMon) - 1),
				Integer.parseInt(frontDay));
		Date tempd1 = cal.getTime();
		cal.set(Integer.parseInt(afterYear), (Integer.parseInt(aftertMon) - 1),
				Integer.parseInt(afterDay));
		Date tempd2 = cal.getTime();
		long minus = tempd2.getTime() - tempd1.getTime();
		return (int) (minus / 60L / 60L / 24L / 1000L);
	}

}
