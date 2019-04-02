
package com.easyticket.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtil {

	// /**
	// * yyyy-MM-dd HH:mm:ss
	// */
	// public static final String FOMAT_1 = "yyyy-MM-dd HH:mm:ss";
	// /**
	// * yyyyMMddHHmmss
	// */
	// public static final String FOMAT_2 = "yyyyMMddHHmmss";

	private final static Logger LOG = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * yyyy
	 */
	public static final String YEAR = "yyyy";
	/**
	 * MM
	 */
	public static final String MONTH = "MM";
	/**
	 * dd
	 */
	public static final String DAY = "dd";
	/**
	 * yyyy-MM
	 */
	public static final String TO_MONTH = "yyyy-MM";
	/**
	 * yyyy-MM-dd
	 */
	public static final String TO_DATE = "yyyy-MM-dd";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final String TO_MINUTE = "yyyy-MM-dd HH:mm";
	/**
	 * MM-dd HH:mm
	 */
	public static final String TO_MONTHDAY = "MM-dd HH:mm";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String TO_SECOND = "yyyy-MM-dd HH:mm:ss";
	/**
	 * HH:mm
	 */
	public static final String TO_HOUR_MINUTE = "HH:mm";
	/**
	 * yyyy/MM/dd HH:mm:ss
	 */
	public static final String TO_SPRIT_SECOND = "yyyy/MM/dd HH:mm:ss";
	/**
	 * MM/dd HH:mm
	 */
	public static final String TO_SPRIT_MINUTE = "MM/dd HH:mm";
	/**
	 * yyyy/MM/dd HH:mm
	 */
	public static final String TO_SPRIT_YEAR = "yyyy/MM/dd HH:mm";
	/**
	 * yyyy年MM月dd日 HH:mm
	 */
	public static final String TO_CHINESE_MINUTE = "yyyy年MM月dd日 HH:mm";
	/**
	 * MM月dd日 HH:mm
	 */
	public static final String TO_CHINESE_MINUTE_NOT_YEAR = "MM月dd日 HH:mm";
	/**
	 * MM月dd日 E HH:mm
	 */
	public static final String TO_CHINESE_DAY_MINUTE = "MM月dd日 E HH:mm";
	/**
	 * yyyy年MM月dd日
	 */
	public static final String TO_CHINESE_DAY = "yyyy年MM月dd日";
	/**
	 * yyyy-MM-dd E
	 */
	public static final String TO_WEEK = "yyyy-MM-dd E";
	/**
	 * MM-dd E
	 */
	public static final String TO_WEEK_NO_YEAR = "MM-dd E";
	/**
	 * MM月dd日 HH:mm
	 */
	public static final String TO_CHINESE_DAYS_MINUTE = "MM月dd日  HH:mm";
	/**
	 * yyyy-MM-dd HH:mm:ss SSSS
	 */
	public static final String TO_MILLISECOND = "yyyy-MM-dd HH:mm:ss SSSS";

	public static final String[] FORMATS = new String[] { TO_DATE, TO_MONTH, TO_MINUTE, TO_SECOND, TO_MILLISECOND };
	/**
	 * yyyyMM
	 */
	public static final String TO_MONTH_N = "yyyyMM";
	/**
	 * yyyyMMdd
	 */
	public static final String TO_DATE_N = "yyyyMMdd";
	/**
	 * yyyyMMddHHmm
	 */
	public static final String TO_MINUTE_N = "yyyyMMddHHmm";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String TO_SECOND_N = "yyyyMMddHHmmss";
	/**
	 * yyyyMMddHHmmssSSSS
	 */
	public static final String TO_MILLISECOND_N = "yyyyMMddHHmmssSSSS";
	/**
	 * yyyy.MM.dd
	 */
	public static final String TO_SPECIAL = "yyyy.MM.dd";
	/**
	 * yyyy/MM/dd
	 */
	public static final String TO_DATE_SLASH = "yyyy/MM/dd";
	/**
	 * HH:mm
	 */
	public static final String TO_HOUR_AND_MINUTE = "HH:mm";

	// 一天中最后的时间23:59:59
	public static final String LAST_TIME = "235959";

	/*
	 * 返回指定格式的当前日期和时间
	 */
	public static String getDate(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date());
	}

	/**
	 * 将字符串解析成Date对象。该方法尝试用[{@link #TO_MONTH}/{@link #TO_DATE}/
	 * {@link #TO_MINUTE}/{@link #TO_SECOND}/{@link #TO_MILLISECOND}
	 * ]格式进行解析，如果无法解析将抛出异常。<br/>
	 * 该方法遍历五种日期格式，效率较低。如果你明确知道字符串的日期格式应调用 {@link #parse(String, String)}方法。
	 * 
	 * @param str
	 *            字符串
	 * @return 返回对应的Date对象。
	 */
	public static Date parse(String str) {
		String pattern = getDateFormat(str);
		return parse(str, pattern);
	}

	/**
	 * 将指定格式的字符串解析成Date对象。
	 * 
	 * @param str
	 *            字符串
	 * @param format
	 *            格式
	 * @return 返回对应的Date对象。
	 */
	public static Date parse(String str, String format) {
		SimpleDateFormat parser = new SimpleDateFormat(format);
		try {
			return parser.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException("将字符串解析为Date对象时发生异常", e);
		}
	}

	/**
	 * 将Date对象解析成yyyy-MM-dd格式的字符串。
	 * 
	 * @param date
	 *            Date对象
	 * @return 返回yyyy-MM-dd格式的字符串。
	 */
	public static String format(Date date) {
		return format(date, TO_DATE);
	}

	/**
	 * 将Date对象解析成指定格式的字符串。
	 * 
	 * @param date
	 *            Date对象
	 * @param pattern
	 *            格式
	 * @return 返回指定格式的字符串。
	 */
	public static String format(Date date, String pattern) {
		SimpleDateFormat formater = new SimpleDateFormat(pattern);
		return formater.format(date);
	}

	/**
	 * 获取字符串的日期格式。如果字符串不在[{@link #TO_MONTH}/{@link #TO_DATE}/ {@link #TO_MINUTE}
	 * /{@link #TO_SECOND}/{@link #TO_MILLISECOND} ]格式范围内将抛出异常。
	 * 
	 * @param str
	 *            字符串
	 * @return 返回字符串的日期格式。
	 */
	public static String getDateFormat(String str) {
		for (String format : FORMATS) {
			if (isDate(str, format)) {
				return format;
			}
		}
		throw new IllegalArgumentException("不支持的日期格式：" + str);
	}

	/**
	 * 判断字符串是否为日期格式的字符串。
	 * 
	 * @param str
	 *            字符串
	 * @return 如果是日期格式的字符串返回true，否则返回false。
	 */
	public static Boolean isDate(String str) {
		for (String format : FORMATS) {
			if (isDate(str, format)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否为指定日期格式的字符串。
	 * 
	 * @param str
	 *            字符串
	 * @param format
	 *            日期格式
	 * @return 如果是指定日期格式的字符串返回true，否则返回false。
	 */
	public static Boolean isDate(String str, String format) {
		try {
			parse(str, format);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	/**
	 * 获取当前日期（只取到日期，时间部分都为0）。
	 * 
	 * @return 返回当前日期。
	 */
	public static Date getToday() {
		return parse(format(new Date()), TO_DATE);
	}

	/**
	 * 获取指定日期的下一天日期。
	 * 
	 * @param date
	 *            指定日期
	 * @return 返回指定日期的下一天日期。
	 */
	public static Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 获取指定日期的前一天日期。
	 * 
	 * @param date
	 *            指定日期
	 * @return 返回指定日期的下一天日期。
	 */
	public static Date getPrevDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 获取指定日期的下一周日期。
	 *
	 * @param date
	 *            指定日期
	 * @return 返回指定日期的下一天日期。
	 */
	public static Date getNextWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 获取一天的开始时间。
	 * 
	 * @param dateStr
	 *            指定日期
	 * @return 返回指定日期的开始时间，格式为yyyyMMddHHmmss。
	 */
	public static String getStartTime(String dateStr) {
		return DateUtil.getDateSFToTF(dateStr, getDateFormat(dateStr), TO_SECOND_N);
	}

	/**
	 * 获取一天的结束时间。
	 * 
	 * @param dateStr
	 *            指定日期
	 * @return 返回指定日期的结束时间，格式为yyyyMMddHHmmss。
	 */
	public static String getEndTime(String dateStr) {
		return DateUtil.getDateSFToTF(dateStr, getDateFormat(dateStr), TO_DATE_N) + LAST_TIME;
	}

	/**
	 * 
	 * getDateSFToTF某种格式的时间串转成指定格式的字符串.
	 * 
	 * @param dateStr
	 * @param targetFormat
	 * @return
	 * @throws @author
	 *             caoz
	 * @date 2015年6月2日 下午3:18:32
	 */
	public static String getDateSFToTF(String dateStr, String sourceFormat, String targetFormat) {
		SimpleDateFormat sformater = new SimpleDateFormat(sourceFormat, Locale.CHINA);
		SimpleDateFormat tformater = new SimpleDateFormat(targetFormat, Locale.CHINA);
		Date date = null;

		if (null != dateStr && !"".equals(dateStr)) {
			try {
				date = sformater.parse(dateStr);
			} catch (ParseException e) {
				throw new RuntimeException("将字符串解析为Date对象时发生异常", e);
			}
			return tformater.format(date);
		} else {
			return "";
		}

	}

	/**
	 * 获取与当前日期相距几天的日期。
	 *
	 * @param day
	 *            指定日期
	 * @return 返回指定日期相距几天的日期。
	 */
	public static Date getDate(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getToday());
		calendar.add(Calendar.DAY_OF_MONTH, day);
		Date date = calendar.getTime();
		return date;
	}

	/**
	 * 获取指定日期与当前日期差几天
	 *
	 * @param day
	 *            指定日期
	 * @return 获取指定日期与当前日期差几天。
	 */
	public static int compareDate(String date1) {
		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(parse(date1));

		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

		aCalendar.setTime(getToday());

		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

		return day2 - day1;
	}

	/**
	 * @title getTimeByHour()
	 * @desc 获取当前时间前后N个小时的时间
	 * @param hour
	 * @return
	 * @author chenwy
	 * @date 2017年6月30日 下午5:58:55
	 */
	public static String getTimeByHour(int hour) {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

		return new SimpleDateFormat(DateUtil.TO_SECOND_N).format(calendar.getTime());
	}

	/**
	 * @title getDayCompare()
	 * @desc 比较两个时间差距多少
	 * @param day1
	 *            格式为yyyyMMddHHmmss
	 * @param day2
	 *            格式为yyyyMMddHHmmss
	 * @return
	 * @author chenwy
	 * @date 2017年7月6日 上午11:43:51
	 */
	public static long getDayCompare(String day1, String day2, String format) {
		SimpleDateFormat parser = new SimpleDateFormat(format);
		try {
			Date one = parser.parse(day1);
			Date two = parser.parse(day2);
			long time1 = one.getTime() / 1000;
			long time2 = two.getTime() / 1000;
			return time2 - time1;
		} catch (ParseException e) {
			throw new RuntimeException("将字符串解析为Date对象时发生异常", e);
		}
	}

	/**
	 * @title addDate()
	 * @desc 获取指定日期增加N天的日期
	 * @param date
	 * @param day
	 * @return
	 * @author chenwy
	 * @date 2017年11月10日 下午5:45:19
	 */
	public static Date addDate(String date, int day) {
		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(parse(date, TO_SECOND_N));

		aCalendar.add(Calendar.DAY_OF_MONTH, day);

		Date date1 = aCalendar.getTime();

		return date1;
	}

	/**
	 * @title getNextNonth()
	 * @desc 获取当前日期的下N个月日期
	 * @param date
	 * @param Num
	 * @return
	 * @author chenwy
	 * @date 2017年11月28日 上午11:09:49
	 */
	public static Date getNextNonth(String date, int nMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(date, TO_SECOND_N));
		calendar.add(Calendar.MONTH, nMonth);
		Date date1 = calendar.getTime();
		return date1;
	}

	/**
	 * 给时间加上几个小时
	 * 
	 * @param day
	 *            当前时间 格式：yyyy-MM-dd HH:mm:ss
	 * @param hour
	 *            需要加的时间
	 * @return
	 */
	public static String addDateMinut(String day, int hour) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(day);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (date == null)
			return "";
		System.out.println("front:" + format.format(date)); // 显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hour);// 24小时制
		date = cal.getTime();
		System.out.println("after:" + format.format(date)); // 显示更新后的日期
		cal = null;
		return format.format(date);

	}

	public static void main(String arg[]) {

		// System.out.println(getDayCompare("201707061244", "201707061245" ,
		// DateUtil.TO_MINUTE_N));

		System.out.println(DateUtil.format(getNextNonth("20170706124444", 12), DateUtil.TO_SECOND));
		// System.out.println(DateUtil.getDateSFToTF("20170706124444",
		// DateUtil.TO_SECOND_N, DateUtil.TO_CHINESE_DAY));
		/*
		 * String decryptToken = "1231231201711231813"; String accountId =
		 * decryptToken.substring(0,decryptToken.length()-12); String timestamp =
		 * decryptToken.substring(decryptToken.length()-12);
		 * System.out.println(accountId); System.out.println(timestamp);
		 * System.out.println(DateUtil.getDate(DateUtil.TO_MINUTE_N));
		 * System.out.println(getDayCompare(timestamp,DateUtil.getDate(DateUtil.
		 * TO_MINUTE_N),DateUtil.TO_MINUTE_N));
		 */

		/*
		 * String dateStr = "20141212101223"; String tf =
		 * DateUtil.getDateSFToTF(dateStr, DateUtil.TO_SECOND_N, DateUtil.TO_SECOND);
		 * LOG.debug("=getDateSFToTF=" + tf);
		 * 
		 * System.out.println(getDate("EEE, dd MMM yyyy HH:mm:ss zzz"));
		 * 
		 * System.out.println(DateUtil.format(DateUtil.getPrevDay(new Date()),
		 * DateUtil.TO_SECOND_N));
		 * 
		 * System.out.println(DateUtil.format(DateUtil.getNextWeek(new
		 * Date()),DateUtil.TO_SECOND_N));
		 */
	}
	/**
	 * 查看是否是正常的订票时间
	 * @return true 正常订票时间 false：维护时间
	 */
    public static boolean isNormalTime(){
    	boolean runFlag = false;
    	String startTimeStr = "06:58:40";
    	String endTimeStr ="23:00:00";
		String format = "HH:mm:ss";
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
		String now = sf.format(new Date());
//		now = "05:58:00";
		Date nowTime;
		try {
			nowTime = new SimpleDateFormat(format).parse(now);
			Date startTime = new SimpleDateFormat(format).parse(startTimeStr);
			Date endTime = new SimpleDateFormat(format).parse(endTimeStr);
			if (isEffectiveDate(nowTime, startTime, endTime)) {
				runFlag = true;
				System.out.println("系统时间在"+startTimeStr+"点到"+endTimeStr+"点之间.");
			} else {
				runFlag = false;
				System.out.println("系统时间不在"+startTimeStr+"点到"+endTimeStr+"点之间.");
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return runFlag;
	}
    
    /**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
	 *
	 * @param nowTime   当前时间
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return
	 * @author jqlin
	 */
	public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime()
				|| nowTime.getTime() == endTime.getTime()) {
			return true;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}
}
